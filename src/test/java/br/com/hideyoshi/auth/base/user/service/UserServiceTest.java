package br.com.hideyoshi.auth.base.user.service;

import br.com.hideyoshi.auth.base.service.UserService;
import br.com.hideyoshi.auth.base.entity.Provider;
import br.com.hideyoshi.auth.base.entity.Role;
import br.com.hideyoshi.auth.base.entity.User;
import br.com.hideyoshi.auth.base.model.UserDTO;
import br.com.hideyoshi.auth.base.repository.UserRepository;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    private UserService underTest;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.underTest = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void canSaveUser() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));

        BDDMockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(createUser().toEntity());

        // Given
        UserDTO user = this.createUser();

        // When
        UserDTO userSaved = this.underTest.saveUser(user);

        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user.toEntity());
        assertThat(userSaved).isInstanceOf(UserDTO.class);
    }

    @Test
    void canSaveOAuthUser() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));

        BDDMockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(createOAuthUser().toEntity());

        // Given
        UserDTO user = this.createOAuthUser();

        // When
        UserDTO userSaved = this.underTest.saveUser(user);

        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user.toEntity());
        assertThat(userArgumentCaptor.getValue().getPassword()).isEmpty();
        assertThat(userSaved).isInstanceOf(UserDTO.class);
    }

    @Test
    void cannotSaveUser() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));


        // Given
        UserDTO user = this.createUser();
        //Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.saveUser(user);
                },
                "Excepts a BadRequestException to be thrown."
        );
    }

    @Test
    void cannotSaveUserWithEmptyPassword() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));


        // Given
        UserDTO user = this.createUser();

        // When
        user.setPassword(null);

        //Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.saveUser(user);
                },
                "Password cannot be empty."
        );
    }

    @Test
    void canAlterUser() {
        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO user = this.createUser();
        // When
        this.underTest.alterUser(1L, user);
        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user.toEntity());
    }

    @Test
    void cannotAlterUserDoesntExist() {
        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        // Given
        UserDTO user = this.createUser();
        // When
        //Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.alterUser(1L, user);
                },
                "User doesn't exist."
        );
    }

    @Test
    void canAddRoleToUser() {
        UserDTO user = this.createUser();
        user.setRoles(List.of());

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO userSaved = this.underTest.getUser(user.getUsername());
        if (!Objects.nonNull(userSaved)) {
            userSaved = this.underTest.saveUser(user);
        }
        // When
        this.underTest.addRoleToUser(userSaved.getId(), Role.USER.getDescription());
        // Then
        userSaved = this.underTest.getUser(userSaved.getUsername());
        assertTrue(userSaved.getRoles().stream().anyMatch(e -> Role.USER.equals(e)));
    }

    @Test
    void cannotAddRoleToUserDoesntExist() {

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        // Given
        UserDTO user = this.createUser();
        // When
        // Then
        UserDTO finalUserSaved = user;
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.addRoleToUser(finalUserSaved.getId(), Role.USER.getDescription());
                },
                "User not found. Error while adding role."
        );
    }

    @Test
    void cannotAddRoleToUserRoleDoesntExist() {
        UserDTO user = this.createUser();

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO userSaved = this.underTest.getUser(user.getUsername());
        if (!Objects.nonNull(userSaved)) {
            userSaved = this.underTest.saveUser(user);
        }
        // When
        // Then
        UserDTO finalUserSaved = userSaved;
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    this.underTest.addRoleToUser(finalUserSaved.getId(), "BANANA");
                },
                "Argument not valid."
        );
    }

    @Test
    void canRemoveRoleFromUser() {
        UserDTO user = this.createUser();

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        BDDMockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(createUser().toEntity());

        // Given
        UserDTO userSaved = this.underTest.getUser(user.getUsername());
        if (!Objects.nonNull(userSaved)) {
            userSaved = this.underTest.saveUser(user);
        }
        this.underTest.addRoleToUser(userSaved.getId(), Role.USER.getDescription());
        // When
        this.underTest.removeRoleFromUser(userSaved.getId(), Role.USER.getDescription());
        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).hasSameClassAs(user.toEntity());
        assertFalse(userArgumentCaptor.getValue().getRoles().stream().anyMatch(e -> Role.USER.equals(e)));
    }

    @Test
    void cannotRemoveRoleFromUserDoesntExist() {
        UserDTO user = this.createUser();

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO userSaved = this.underTest.getUser(user.getUsername());
        if (!Objects.nonNull(userSaved)) {
            userSaved = this.underTest.saveUser(user);
        }
        this.underTest.addRoleToUser(userSaved.getId(), Role.USER.getDescription());
        // When
        // Then
        UserDTO finalUserSaved = userSaved;
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    this.underTest.removeRoleFromUser(finalUserSaved.getId(), "BANANA");
                },
                "Argument not valid."
        );
    }

    @Test
    void cannotRemoveRoleFromUserRoleDoesntExist() {
        // Given
        UserDTO user = this.createUser();
        // When
        // Then
        UserDTO finalUserSaved = user;
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.removeRoleFromUser(finalUserSaved.getId(), Role.USER.getDescription());
                },
                "User not found. Error while adding role."
        );
    }

    @Test
    void canGetUser() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO user = this.createUser();
        // When
        UserDTO userOnDB = this.underTest.getUser(user.getUsername());
        // Then
        ArgumentCaptor<String> usernameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(usernameArgumentCaptor.capture());

        assertThat(userOnDB).isNotNull();
        assertThat(userOnDB).isInstanceOf(UserDTO.class);
        assertThat(user.getUsername()).isEqualTo(usernameArgumentCaptor.getValue());
    }

    @Test
    void cannotGetUser() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));

        // Given
        UserDTO user = this.createUser();
        // When
        //Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.getUser(user.getUsername());
                },
                "Excepts a BadRequestException to be thrown."
        );
    }

    @Test
    void canGetUsers() {
        List<UserDTO> users = this.underTest.getUsers();
        assertThat(users).isNotNull();
    }

    @Test
    void canLoadUserByUsername() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(createUser().toEntity()));

        // Given
        UserDTO user = this.createUser();
        // When
        UserDTO userOnDB = (UserDTO) this.underTest.loadUserByUsername(user.getUsername());
        // Then
        ArgumentCaptor<String> usernameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(usernameArgumentCaptor.capture());

        assertThat(userOnDB).isNotNull();
        assertThat(userOnDB).isInstanceOf(UserDetails.class);
        assertThat(user.getUsername()).isEqualTo(usernameArgumentCaptor.getValue());
    }

    @Test
    void cannotLoadUserByUsername() {

        BDDMockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));

        // Given
        UserDTO user = this.createUser();
        // When
        //Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.loadUserByUsername(user.getUsername());
                },
                "User Not Found. Please create an Account."
        );
    }

    @Test
    void canDeleteUser() {
        UserDTO user = this.createUser();

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(user.toEntity()));

        // When
        this.underTest.deleteUser(user.getId());
        // Then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(user.getId());
    }

    @Test
    void cannotDeleteUser() {
        UserDTO user = this.createUser();

        BDDMockito.when(userRepository.findById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        // When
        // Then
        assertThrows(
                BadRequestException.class,
                () -> {
                    this.underTest.deleteUser(user.getId());
                },
                "User doesn't exist."
        );
    }

    private UserDTO createUser() {
        return UserDTO.builder()
                .id(1L)
                .name("Clark Kent")
                .email("superman@gmail.com")
                .username("Superman")
                .password("password")
                .provider(Provider.LOCAL)
                .build();
    }

    private UserDTO createOAuthUser() {
        return UserDTO.builder()
                .id(1L)
                .name("Clark Kent")
                .email("superman@gmail.com")
                .username("Superman")
                .provider(Provider.GOOGLE)
                .build();
    }
}