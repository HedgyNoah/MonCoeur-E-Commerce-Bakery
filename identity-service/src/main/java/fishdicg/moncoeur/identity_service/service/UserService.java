package fishdicg.moncoeur.identity_service.service;

import fishdicg.moncoeur.identity_service.constant.PredefinedRole;
import fishdicg.moncoeur.identity_service.dto.PageResponse;
import fishdicg.moncoeur.identity_service.dto.request.SendVerificationRequest;
import fishdicg.moncoeur.identity_service.dto.request.UserCreationRequest;
import fishdicg.moncoeur.identity_service.dto.request.UserUpdateRequest;
import fishdicg.moncoeur.identity_service.dto.request.VerificationRequest;
import fishdicg.moncoeur.identity_service.dto.response.UserResponse;
import fishdicg.moncoeur.identity_service.entity.Role;
import fishdicg.moncoeur.identity_service.entity.User;
import fishdicg.moncoeur.identity_service.exception.AppException;
import fishdicg.moncoeur.identity_service.exception.ErrorCode;
import fishdicg.moncoeur.identity_service.mapper.UserMapper;
import fishdicg.moncoeur.identity_service.repository.RoleRepository;
import fishdicg.moncoeur.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    SecureRandom random;
    KafkaProducerService kafkaProducerService;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            String code = generateCode();

            user.setVerificationCode(code);
            user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            kafkaProducerService.sendEmailEvent(user.getEmail(), code);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    public void verifyUser(VerificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info("verification code: {}", request.getVerificationCode());
        if(user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(request.getVerificationCode())) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        if(user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_VERIFICATION_CODEE);
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
    }

    public void sendCode(SendVerificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String code = generateCode();

        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        kafkaProducerService.sendEmailEvent(user.getEmail(), code);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        log.info("context get name: {}", id);

        User user = userRepository.findById(id).orElseThrow(() ->
            new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateMyInfo(UserUpdateRequest request) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsersWithPagination(int page, int size, String sortBy,
                                                                String order, String search) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        Page<User> pageData;
        if(search != null && !search.isEmpty()) {
            pageData = userRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else {
            pageData = userRepository.findAll(pageable);
        }
        List<UserResponse> userResponseList = pageData.getContent().stream()
                .map(userMapper::toUserResponse).toList();

        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .sortBy(sortBy)
                .order(order)
                .search(search)
                .data(userResponseList)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    private String generateCode() {
        int code = random.nextInt(1000000);
        return String.format("%06d", code);
    }
}
