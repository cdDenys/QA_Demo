package carshare.service;

import carshare.database.entity.Category;
import carshare.database.entity.DriverLicense;
import carshare.database.entity.User;
import carshare.database.repository.DriverLicenseRepository;
import carshare.enums.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class DriverLicenseServiceTest {

    @MockBean
    private DriverLicenseRepository driverLicenseRepository;

    private final DriverLicenseService driverLicenseService;

    @Autowired
    public DriverLicenseServiceTest(DriverLicenseService driverLicenseService) {
        this.driverLicenseService = driverLicenseService;
    }

    static DriverLicense driverLicense;
    static User user;
    static Category category;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "TEST", "TEST", "TEST", "test_login",
                "test_password", false, LocalDateTime.now(), "test@gmail.com", "380672229999",
                Sex.MALE, LocalDateTime.now(), "url", null, null, null);

        category = new Category(UUID.randomUUID(), "B2", "Regular");

        driverLicense = new DriverLicense(UUID.randomUUID(), "TEST", LocalDateTime.now(),
                LocalDateTime.now(), Collections.singleton(category), user);
    }

    @Test
    void createTest() {
        //Init
        when(driverLicenseRepository.save(driverLicense)).thenReturn(driverLicense);

        //When
        DriverLicense createdDriverLicense = driverLicenseService.create(driverLicense);

        //Then
        assertNotNull(createdDriverLicense, "Driver license is not created.");
        assertEquals(driverLicense.getNumber(), createdDriverLicense.getNumber());
        assertEquals(driverLicense.getExpireDate(), createdDriverLicense.getExpireDate());
        assertEquals(driverLicense.getIssueDate(), createdDriverLicense.getIssueDate());
        assertEquals(driverLicense.getUser().getMobilePhone(), createdDriverLicense.getUser().getMobilePhone());
        assertEquals(driverLicense.getUser().getLogin(), createdDriverLicense.getUser().getLogin());
        assertEquals(driverLicense.getUser().getEmail(), createdDriverLicense.getUser().getEmail());
        verify(driverLicenseRepository, times(1)).save(driverLicense);
    }

    @Test
    void createIfDriverLicenseIsNullTest() {
        assertNull(driverLicenseService.create(null));
        verify(driverLicenseRepository, times(0)).save(driverLicense);
    }

    @Test
    void getByIdTest() {
        //Init
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(true);
        when(driverLicenseRepository.findById(driverLicense.getId())).thenReturn(Optional.ofNullable(driverLicense));

        //When
        DriverLicense foundDriverLicense = driverLicenseService.getById(driverLicense.getId());

        //Then
        assertNotNull(foundDriverLicense, "Driver license not found.");
        assertEquals(driverLicense.getNumber(), foundDriverLicense.getNumber());
        assertEquals(driverLicense.getExpireDate(), foundDriverLicense.getExpireDate());
        assertEquals(driverLicense.getIssueDate(), foundDriverLicense.getIssueDate());
        assertEquals(driverLicense.getUser().getMobilePhone(), foundDriverLicense.getUser().getMobilePhone());
        assertEquals(driverLicense.getUser().getLogin(), foundDriverLicense.getUser().getLogin());
        assertEquals(driverLicense.getUser().getEmail(), foundDriverLicense.getUser().getEmail());
        verify(driverLicenseRepository, times(1)).existsById(driverLicense.getId());
        verify(driverLicenseRepository, times(1)).findById(driverLicense.getId());
    }

    @Test
    void getByIdIfDriverLicenseNotExistTest() {
        //Init
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(false);

        //Then
        assertNull(driverLicenseService.getById(driverLicense.getId()));
    }

    @Test
    void updateTest(){
        //Init
        driverLicense.setNumber("TEST_TEST");
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(true);
        when(driverLicenseRepository.save(driverLicense)).thenReturn(driverLicense);

        //When
        DriverLicense updatedDriverLicense = driverLicenseService.update(driverLicense);

        //Then
        assertNotNull(updatedDriverLicense, "Driver license is not updated.");
        assertEquals(driverLicense.getNumber(), updatedDriverLicense.getNumber());
        verify(driverLicenseRepository, times(1)).save(driverLicense);
    }

    @Test
    void updateIfDriverLicenseNotExistTest(){
        //Init
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(false);

        //Then
        assertNull(driverLicenseService.update(driverLicense));
        verify(driverLicenseRepository, times(0)).save(driverLicense);
    }

    @Test
    void deleteTest() {
        //Init
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(true);
        doNothing().when(driverLicenseRepository).deleteById(driverLicense.getId());

        //When
        UUID deletedDriverLicenseId = driverLicenseService.delete(driverLicense.getId());

        //Then
        assertNotNull(deletedDriverLicenseId, "Driver license is not updated.");
        assertEquals(driverLicense.getId(), deletedDriverLicenseId);
        verify(driverLicenseRepository, times(1)).existsById(driverLicense.getId());
        verify(driverLicenseRepository, times(1)).deleteById(driverLicense.getId());
    }

    @Test
    void deleteIfDriverLicenseNotExistTest() {
        //Init
        when(driverLicenseRepository.existsById(driverLicense.getId())).thenReturn(false);

        //Then
        assertNull(driverLicenseService.delete(driverLicense.getId()));
        verify(driverLicenseRepository, times(1)).existsById(driverLicense.getId());
        verify(driverLicenseRepository, times(0)).deleteById(driverLicense.getId());
    }
}