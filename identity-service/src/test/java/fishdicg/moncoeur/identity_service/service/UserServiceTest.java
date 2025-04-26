package fishdicg.moncoeur.identity_service.service;

import fishdicg.moncoeur.identity_service.constant.PredefinedRole;
import fishdicg.moncoeur.identity_service.dto.request.SendVerificationRequest;
import fishdicg.moncoeur.identity_service.dto.request.UserCreationRequest;
import fishdicg.moncoeur.identity_service.dto.request.UserUpdateRequest;
import fishdicg.moncoeur.identity_service.dto.request.VerificationRequest;
import fishdicg.moncoeur.identity_service.dto.response.UserResponse;
import fishdicg.moncoeur.identity_service.entity.Role;
import fishdicg.moncoeur.identity_service.entity.User;
import fishdicg.moncoeur.identity_service.exception.AppException;
import fishdicg.moncoeur.identity_service.mapper.UserMapper;
import fishdicg.moncoeur.identity_service.repository.RoleRepository;
import fishdicg.moncoeur.identity_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private SecureRandom mockRandom;
    @Mock
    private KafkaProducerService mockKafkaProducerService;

    private UserService userServiceUnderTest;

    @BeforeEach
    void setUp() {
        userServiceUnderTest = new UserService(mockUserRepository, mockUserMapper, mockPasswordEncoder,
                mockRoleRepository, mockRandom, mockKafkaProducerService);
    }

    @Test
    void testCreateUser() {
        // Setup
        final UserCreationRequest request = UserCreationRequest.builder()
                .password("password")
                .build();
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserMapper.toUser(...).
        final User user = User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2026, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build();
        when(mockUserMapper.toUser(UserCreationRequest.builder()
                .password("password")
                .build())).thenReturn(user);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");
        when(mockRoleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(Role.builder().build()));
        when(mockRandom.nextInt(1000000)).thenReturn(0);
        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.createUser(request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserRepository).save(any(User.class));
        verify(mockKafkaProducerService).sendEmailEvent(eq("email"), anyString());
    }

    @Test
    void testCreateUser_RoleRepositoryReturnsAbsent() {
        // Setup
        final UserCreationRequest request = UserCreationRequest.builder()
                .password("password")
                .build();
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserMapper.toUser(...).
        final User user = User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build();
        when(mockUserMapper.toUser(UserCreationRequest.builder()
                .password("password")
                .build())).thenReturn(user);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");
        when(mockRoleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.empty());
        when(mockRandom.nextInt(1000000)).thenReturn(0);
        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.createUser(request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserRepository).save(any(User.class));
        verify(mockKafkaProducerService).sendEmailEvent("email", "verificationCode");
    }

    @Test
    void testVerifyUser() {
        // Setup
        final VerificationRequest request = VerificationRequest.builder()
                .userId("userId")
                .verificationCode("verificationCode")
                .build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2026, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("userId")).thenReturn(user);

        // Run the test
        userServiceUnderTest.verifyUser(request);

        // Verify the results
        verify(mockUserRepository).save(any(User.class));
    }

    @Test
    void testVerifyUser_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        final VerificationRequest request = VerificationRequest.builder()
                .userId("userId")
                .verificationCode("verificationCode")
                .build();
        when(mockUserRepository.findById("userId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.verifyUser(request)).isInstanceOf(AppException.class);
    }

    @Test
    void testSendCode() {
        // Setup
        final SendVerificationRequest request = SendVerificationRequest.builder()
                .userId("userId")
                .build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("userId")).thenReturn(user);

        when(mockRandom.nextInt(1000000)).thenReturn(0);

        // Run the test
        userServiceUnderTest.sendCode(request);

        // Verify the results
        verify(mockUserRepository).save(any(User.class));
        verify(mockKafkaProducerService).sendEmailEvent("email", "verificationCode");
    }

    @Test
    void testSendCode_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        final SendVerificationRequest request = SendVerificationRequest.builder()
                .userId("userId")
                .build();
        when(mockUserRepository.findById("userId")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.sendCode(request)).isInstanceOf(AppException.class);
    }

    @Test
    void testGetMyInfo() {
        // Setup
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserRepository.findByUsername(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findByUsername("username")).thenReturn(user);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.getMyInfo();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMyInfo_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findByUsername("username")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.getMyInfo()).isInstanceOf(AppException.class);
    }

    @Test
    void testUpdateMyInfo() {
        // Setup
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build();
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("id")).thenReturn(user);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");

        // Configure UserRepository.save(...).
        final User user1 = User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build();
        when(mockUserRepository.save(any(User.class))).thenReturn(user1);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.updateMyInfo(request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserMapper).updateUser(any(User.class), eq(UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build()));
    }

    @Test
    void testUpdateMyInfo_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build();
        when(mockUserRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.updateMyInfo(request)).isInstanceOf(AppException.class);
    }

    @Test
    void testUpdateUser() {
        // Setup
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build();
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("id")).thenReturn(user);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");
        when(mockRoleRepository.findAllById(List.of("value"))).thenReturn(List.of(Role.builder().build()));

        // Configure UserRepository.save(...).
        final User user1 = User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build();
        when(mockUserRepository.save(any(User.class))).thenReturn(user1);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.updateUser("id", request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserMapper).updateUser(any(User.class), eq(UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build()));
    }

    @Test
    void testUpdateUser_UserRepositoryFindByIdReturnsAbsent() {
        // Setup
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build();
        when(mockUserRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.updateUser("id", request)).isInstanceOf(AppException.class);
    }

    @Test
    void testUpdateUser_RoleRepositoryReturnsNoItems() {
        // Setup
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build();
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("id")).thenReturn(user);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");
        when(mockRoleRepository.findAllById(List.of("value"))).thenReturn(Collections.emptyList());

        // Configure UserRepository.save(...).
        final User user1 = User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build();
        when(mockUserRepository.save(any(User.class))).thenReturn(user1);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.updateUser("id", request);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockUserMapper).updateUser(any(User.class), eq(UserUpdateRequest.builder()
                .password("password")
                .roles(List.of("value"))
                .build()));
    }

    @Test
    void testDeleteUser() {
        // Setup
        // Run the test
        userServiceUnderTest.deleteUser("id");

        // Verify the results
        verify(mockUserRepository).deleteById("id");
    }

    @Test
    void testGetAllUsers() {
        // Setup
        final List<UserResponse> expectedResult = List.of(UserResponse.builder().build());

        // Configure UserRepository.findAll(...).
        final List<User> users = List.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findAll()).thenReturn(users);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final List<UserResponse> result = userServiceUnderTest.getAllUsers();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetAllUsers_UserRepositoryReturnsNoItems() {
        // Setup
        when(mockUserRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<UserResponse> result = userServiceUnderTest.getAllUsers();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetUser() {
        // Setup
        final UserResponse expectedResult = UserResponse.builder().build();

        // Configure UserRepository.findById(...).
        final Optional<User> user = Optional.of(User.builder()
                .password("password")
                .email("email")
                .verified(false)
                .verificationCode("verificationCode")
                .verificationExpiry(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .roles(Set.of(Role.builder().build()))
                .build());
        when(mockUserRepository.findById("id")).thenReturn(user);

        when(mockUserMapper.toUserResponse(any(User.class))).thenReturn(UserResponse.builder().build());

        // Run the test
        final UserResponse result = userServiceUnderTest.getUser("id");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetUser_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> userServiceUnderTest.getUser("id")).isInstanceOf(AppException.class);
    }
}
