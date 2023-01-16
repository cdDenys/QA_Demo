package carshare.service;

import carshare.advice.exception.UserCreationException;
import carshare.advice.exception.UserNotFoundException;
import carshare.database.entity.User;
import carshare.database.repository.UserRepository;
import carshare.enums.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleService roleService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private final UserService userService;

    @Autowired
    public UserServiceTest(final UserService userService) {
        this.userService = userService;
    }

    static User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "TEST", "TEST", "TEST", "test_login",
                "test_password", false, LocalDateTime.now(), "test@gmail.com", "380672229999",
                Sex.MALE, LocalDateTime.now(), "url", null, null, null);
    }

    @Test
    void createTest() throws UserCreationException {
        //Init
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        //When
        User createdUser = userService.create(user);

        //Then
        assertNotNull(createdUser, "User is not created");
        assertEquals(false, createdUser.getVerified());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getMobilePhone(), createdUser.getMobilePhone());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getSex(), createdUser.getSex());
        verify(roleService, times(1)).getRole("USER");
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createIfUserIsNullTest() {
        //Init
        Throwable exception = assertThrows(UserCreationException.class, () -> userService.create(null));

        //Then
        assertEquals("Check your data and try again.", exception.getMessage());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void getByIdTest() throws UserNotFoundException {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        //When
        User userById = userService.getById(user.getId());

        //Then
        assertNotNull(userById, "User is not find.");
        assertEquals(user.getId(), userById.getId());
        assertEquals(user.getLogin(), userById.getLogin());
        assertEquals(user.getMobilePhone(), userById.getMobilePhone());
        assertEquals(user.getEmail(), userById.getEmail());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getByIdIfUserNotExistTest() {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(false);

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.getById(user.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).findById(user.getId());
    }

    @Test
    void updateTest() throws UserNotFoundException {
        //Init
        user.setLogin("NEW TEST");
        user.setMobilePhone("NEW TEST");
        user.setEmail("NEW TEST");
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        //When
        User updatedUser = userService.update(user);

        //Then
        assertNotNull(updatedUser, "User is not updated.");
        assertEquals(user.getLogin(), updatedUser.getLogin());
        assertEquals(user.getMobilePhone(), updatedUser.getMobilePhone());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateIfUserNotExistTest() {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.update(user));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteTest() throws UserNotFoundException {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        //When
        UUID deletedUserId = userService.delete(user.getId());

        //Then
        assertNotNull(deletedUserId, "User is not deleted.");
        assertEquals(user.getId(), deletedUserId);
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void deleteIfUserNotExist() {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(false);

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.delete(user.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).deleteById(user.getId());
    }

    @Test
    void isVerifiedTest() throws UserNotFoundException {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        //When
        boolean isVerified = userService.isVerified(user.getId());

        //Then
        assertFalse(isVerified);
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void isVerifiedIfUserNotExistTest() {
        //Init
        when(userRepository.existsById(user.getId())).thenReturn(false);

        //Then
        assertThrows(UserNotFoundException.class, () -> userService.isVerified(user.getId()));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).findById(user.getId());
    }
}