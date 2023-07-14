package de.hsel.tetris.usermanagement;

import de.hsel.tetris.exception.ResourceNotFoundException;
import de.hsel.tetris.usermanagement.dto.UserRegistrationDto;
import de.hsel.tetris.usermanagement.dto.UserUpdateDto;
import de.hsel.tetris.usermanagement.exception.UserValidationException;
import de.hsel.tetris.usermanagement.repositories.UserRepository;
import de.hsel.tetris.usermanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserService(mockUserRepository, mockPasswordEncoder);
    }

    @Test
    void canRegisterUser() throws UserValidationException {
        // Set up stubs
        when(mockPasswordEncoder.encode(any())).thenReturn("encoded");
        doAnswer(invocation -> invocation.getArguments()[0]).when(mockUserRepository).save(any());

        // run code under test
        final var email = "user@unit.test";
        final var username = "testuser";
        final var password = "testpassword";

        var userRegistrationDto = new UserRegistrationDto(email, username, password);

        var user = userService.register(userRegistrationDto);

        // run assertions
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
    }

    @Test
    void cannotRegisterUserWithDuplicateEmail() {
        // set up stubs
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        // run code under test and assertions
        var exception = catchThrowableOfType(() -> userService.register(new UserRegistrationDto(null, null, null)), UserValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors().get("email")).isEqualTo("taken");
    }

    @Test
    void cannotRegisterUserWithDuplicateUsername() {
        // set up stubs
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // run code under test and assertions
        var exception = catchThrowableOfType(() -> userService.register(new UserRegistrationDto(null, null, null)), UserValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors().get("username")).isEqualTo("taken");
    }

    @Test
    void cannotRegisterUserWithDuplicateEmailAndUsername() {
        // set up stubs
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(new User()));
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        // run code under test and assertions
        var exception = catchThrowableOfType(() -> userService.register(new UserRegistrationDto(null, null, null)), UserValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors().get("username")).isEqualTo("taken");
        assertThat(exception.getErrors().get("email")).isEqualTo("taken");
    }

    @Test
    void cannotUpdateUserIfNotExists() {
        // Set up stubs
        when(mockUserRepository.findById(any())).thenReturn(Optional.empty());

        // run test & assertions
        assertThatThrownBy(() -> userService.updateProfilePicture(0, new UserUpdateDto())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void canUpdateWhenAllFieldsPresent() throws ResourceNotFoundException, UserValidationException {
        // Set up stubs
        when(mockPasswordEncoder.encode(anyString())).thenReturn("encoded");
        when(mockUserRepository.findById(0)).thenReturn(Optional.of(new User("old@e.mail", "oldname", "oldpassword", "ROLE_USER")));
        doAnswer(invocation -> invocation.getArguments()[0]).when(mockUserRepository).save(any());

        // run code under test
        String newEmail = "new@e.mail";
        String newName = "newname";
        var user = userService.updateProfilePicture(0, new UserUpdateDto(newEmail, newName, "newpassword"));

        // run assertions
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getUsername()).isEqualTo(newName);
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
    }

    @Test
    void canUpdateWhenOnlyEmailPresent() throws ResourceNotFoundException, UserValidationException {
        // Set up stubs
        String oldName = "oldname";
        String oldPassword = "oldpassword";
        String roles = "ROLE_USER";

        when(mockUserRepository.findById(0)).thenReturn(Optional.of(new User("old@e.mail", oldName, oldPassword, roles)));
        doAnswer(invocation -> invocation.getArguments()[0]).when(mockUserRepository).save(any());

        // run code under test
        var newEmail = "new@e.mail";
        var updateDto = new UserUpdateDto();
        updateDto.setEmail(newEmail);

        var user = userService.updateProfilePicture(0, updateDto);

        // run assertions
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getUsername()).isEqualTo(oldName);
        assertThat(user.getPassword()).isEqualTo(oldPassword);
        assertThat(user.getRoles()).isEqualTo(roles);
    }

    @Test
    void canDeleteWhenUserPresent() {
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());
        assertThat(userService.deleteUserById(0)).isTrue();
    }

    @Test
    void canUpdateWhenOnlyUsernamePresent() throws ResourceNotFoundException, UserValidationException {
        // Set up stubs
        String oldEmail = "old@e.mail";
        String oldPassword = "oldpassword";
        String roles = "ROLE_USER";

        when(mockUserRepository.findById(0)).thenReturn(Optional.of(new User(oldEmail, "oldName", oldPassword, roles)));
        doAnswer(invocation -> invocation.getArguments()[0]).when(mockUserRepository).save(any());

        // run code under test
        var newName = "newName";
        var updateDto = new UserUpdateDto();
        updateDto.setUsername(newName);

        var user = userService.updateProfilePicture(0, updateDto);

        // run assertions
        assertThat(user.getEmail()).isEqualTo(oldEmail);
        assertThat(user.getUsername()).isEqualTo(newName);
        assertThat(user.getPassword()).isEqualTo(oldPassword);
        assertThat(user.getRoles()).isEqualTo(roles);
    }

    @Test
    void canUpdateWhenOnlyPasswordPresent() throws ResourceNotFoundException, UserValidationException {
        // Set up stubs
        String oldEmail = "old@e.mail";
        String oldName = "oldname";
        String roles = "ROLE_USER";

        when(mockPasswordEncoder.encode(anyString())).thenReturn("encoded");
        when(mockUserRepository.findById(0)).thenReturn(Optional.of(new User(oldEmail, oldName, "oldpassword", roles)));
        doAnswer(invocation -> invocation.getArguments()[0]).when(mockUserRepository).save(any());

        // run code under test
        var updateDto = new UserUpdateDto();
        updateDto.setPassword("newpassword");

        var user = userService.updateProfilePicture(0, updateDto);

        // run assertions
        assertThat(user.getEmail()).isEqualTo(oldEmail);
        assertThat(user.getUsername()).isEqualTo(oldName);
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles()).isEqualTo(roles);
    }

    @Test
    void cannotUpdateWhenEmailIsAlreadyTaken() {
        // set up stubs
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        // run code under test and assertions
        var updateDto = new UserUpdateDto();
        updateDto.setEmail("email@email.com");
        var exception = catchThrowableOfType(() -> userService.updateProfilePicture(0, updateDto), UserValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors().get("email")).isEqualTo("taken");
    }

    @Test
    void cannotUpdateWhenUsernameIsAlreadyTaken() {
        // set up stubs
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // run code under test and assertions
        var updateDto = new UserUpdateDto();
        updateDto.setUsername("username");
        var exception = catchThrowableOfType(() -> userService.updateProfilePicture(0, updateDto), UserValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors().get("username")).isEqualTo("taken");
    }

    @Test
    void canGetUser() {
        // Set up stubs
        var user = new User();
        var username = "testuser";
        user.setUsername(username);
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(user));

        // run code under test
        var retrievedUser = userService.getUser(0);

        // run assertions
        assertThat(retrievedUser).isNotEmpty();
        assertThat(retrievedUser.get().getUsername()).isEqualTo(username);

    }

    @Test
    void canGetAllUsers() {
        // Set up stubs
        var users = new User[]{new User(), new User()};
        when(mockUserRepository.findAll()).thenReturn(Arrays.asList(users));

        // run code under test
        var retrievedUsers = userService.getAllUsers();

        // run assertions
        assertThat(retrievedUsers).hasSize(2);
    }
}