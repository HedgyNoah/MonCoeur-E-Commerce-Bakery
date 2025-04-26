package fishdicg.moncoeur.identity_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fishdicg.moncoeur.identity_service.constant.Navigation;
import fishdicg.moncoeur.identity_service.dto.request.authentication.AuthenticationRequest;
import fishdicg.moncoeur.identity_service.dto.request.authentication.IntrospectRequest;
import fishdicg.moncoeur.identity_service.dto.request.authentication.LogoutRequest;
import fishdicg.moncoeur.identity_service.dto.response.AuthenticationResponse;
import fishdicg.moncoeur.identity_service.dto.response.IntrospectResponse;
import fishdicg.moncoeur.identity_service.dto.response.NavigationResponse;
import fishdicg.moncoeur.identity_service.entity.InvalidatedToken;
import fishdicg.moncoeur.identity_service.entity.User;
import fishdicg.moncoeur.identity_service.exception.AppException;
import fishdicg.moncoeur.identity_service.exception.ErrorCode;
import fishdicg.moncoeur.identity_service.repository.InvalidatedTokenRepository;
import fishdicg.moncoeur.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(Boolean.FALSE.equals(user.getVerified())) throw new AppException(ErrorCode.UNAUTHENTICATED);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        NavigationResponse route = isAdmin ? Navigation.PRODUCTS_ROUTE : Navigation.HOME_ROUTE;

        var accessToken = generateToken(user, VALID_DURATION);
        var refreshToken = generateToken(user, REFRESHABLE_DURATION);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .route(route)
                .build();
    }

    public void logout(LogoutRequest request) {
        try {
            //Access Token
            var accessToken = verifyToken(request.getAccessToken(), false);
            String atId = accessToken.getJWTClaimsSet().getJWTID();
            Date atExpiryTime = accessToken.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(InvalidatedToken.builder()
                    .id(atId)
                    .expiryTime(atExpiryTime)
                    .build());

            //Refresh Token
            var refreshToken = verifyToken(request.getRefreshToken(), true);
            String rtId = refreshToken.getJWTClaimsSet().getJWTID();
            Date rtExpiryTime = refreshToken.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(InvalidatedToken.builder()
                    .id(rtId)
                    .expiryTime(rtExpiryTime)
                    .build());

        } catch (JOSEException | ParseException | AppException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String generateToken(User user, long duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("http://localhost:8888")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public AuthenticationResponse refreshAccessToken(String refreshToken) {
        try {
            SignedJWT parsedToken = verifyToken(refreshToken, true);

            String username = parsedToken.getJWTClaimsSet().getSubject();
            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            String newAccessToken = generateToken(user, VALID_DURATION);

            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();

        } catch (JOSEException | ParseException e) {
            log.info("Error when refresh token: ", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }


    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role ->
                stringJoiner.add("ROLE_" + role.getName()));
        return stringJoiner.toString();
    }
}
