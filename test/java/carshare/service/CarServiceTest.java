package carshare.service;

import carshare.advice.exception.CarCreationException;
import carshare.advice.exception.CarNotFoundException;
import carshare.advice.exception.UserNotFoundException;
import carshare.database.entity.Car;
import carshare.database.entity.Image;
import carshare.database.entity.User;
import carshare.database.repository.CarRepository;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class CarServiceTest {

    @MockBean
    private UserService userService;
    @MockBean
    private CarRepository carRepository;

    private final CarService carService;

    @Autowired
    public CarServiceTest(final CarService carService) {
        this.carService = carService;
    }

    static UUID carId;
    static Car car;
    static User user;
    static Image image;

    @BeforeEach
    void setUp() {
        carId = UUID.randomUUID();
        user = new User(UUID.randomUUID(), "TEST", "TEST", "TEST", "test_login",
                "test_password", false, LocalDateTime.now(), "test@gmail.com", "380672229999",
                Sex.MALE, LocalDateTime.now(), "url", null, null, null);
        car = new Car(UUID.randomUUID(), "TEST", "TEST", "TEST", "TEST", LocalDateTime.now(),
                "TEST", "TEST", "TEST", null, null);
        image = new Image(UUID.randomUUID(), "TEST",  car);
        car.setUser(user);
        car.setImages(Collections.singleton(image));
        user.setCars(Collections.singleton(car));
    }

    @Test
    void createTest() throws UserNotFoundException, CarCreationException {
        //Init
        when(userService.isVerified(car.getUser().getId())).thenReturn(true);
        when(carRepository.save(car)).thenReturn(car);

        //When
        Car createdCar = carService.create(car);

        //Then
        assertNotNull(createdCar, "Car is not created");
        assertEquals(car.getVin(), createdCar.getVin());
        assertEquals(car.getNumber(), createdCar.getNumber());
        assertEquals(car.getBrand(), createdCar.getBrand());
        assertEquals(car.getModel(), createdCar.getModel());
        assertEquals(car.getUser().getLogin(), createdCar.getUser().getLogin());
        assertEquals(car.getUser().getEmail(), createdCar.getUser().getEmail());
        assertEquals(car.getUser().getMobilePhone(), createdCar.getUser().getMobilePhone());
        verify(carRepository, times(1)).save(car);
        verify(userService, times(1)).isVerified(car.getUser().getId());
    }

    @Test
    void createIfCarIsNullTest() {
        //Init
        Throwable exception = assertThrows(CarCreationException.class, () -> carService.create(null));

        //Then
        assertEquals("User not verified or car data is invalid.", exception.getMessage());
        verify(carRepository, times(0)).save(car);
        verify(userService, times(0)).isVerified(car.getUser().getId());
    }

    @Test
    void createIfCarsUserIsNullTest() throws UserNotFoundException {
        //Init
        car.setUser(null);
        when(userService.isVerified(any(UUID.class))).thenReturn(true);
        when(carRepository.save(car)).thenReturn(car);

        //Then
        assertThrows(CarCreationException.class, () -> carService.create(car));
        verify(carRepository, times(0)).save(car);
    }

    @Test
    void createNotVerifiedUserTest() throws UserNotFoundException {
        //Init
        when(userService.isVerified(any(UUID.class))).thenReturn(false);
        when(carRepository.save(car)).thenReturn(car);

        //Then
        assertThrows(CarCreationException.class, () -> carService.create(car));
        verify(carRepository, times(0)).save(car);
        verify(userService, times(1)).isVerified(car.getUser().getId());
    }

    @Test
    void getByIdTest() throws CarNotFoundException {
        //Init
        when(carRepository.existsById(car.getId())).thenReturn(true);
        when(carRepository.findById(car.getId())).thenReturn(Optional.ofNullable(car));

        //When
        Car carById = carService.getById(car.getId());

        //Then
        assertNotNull(carById, "Car not found.");
        assertEquals(car.getVin(), carById.getVin());
        assertEquals(car.getNumber(), carById.getNumber());
        assertEquals(car.getBrand(), carById.getBrand());
        assertEquals(car.getModel(), carById.getModel());
        assertEquals(car.getUser().getLogin(), carById.getUser().getLogin());
        assertEquals(car.getUser().getEmail(), carById.getUser().getEmail());
        assertEquals(car.getUser().getMobilePhone(), carById.getUser().getMobilePhone());
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(1)).findById(car.getId());
    }

    @Test
    void getByIdIfCarNotExistTest() {
        //Init
        when(carRepository.existsById(car.getId())).thenReturn(false);

        //Then
        assertThrows(CarNotFoundException.class, () -> carService.getById(car.getId()));
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(0)).findById(car.getId());
    }

    @Test
    void updateTest() throws UserNotFoundException, CarNotFoundException {
        //Init
        car.setBrand("NEW TEST");
        car.setNumber("NEW TEST");
        car.setVin("NEW TEST");
        car.setModel("NEW TEST");
        when(carRepository.existsById(car.getId())).thenReturn(true);
        when(userService.isVerified(car.getUser().getId())).thenReturn(true);
        when(carRepository.save(car)).thenReturn(car);

        //When
        Car updatedCar = carService.update(car);

        //Then
        assertNotNull(updatedCar, "Car is not updated.");
        assertEquals(car.getVin(), updatedCar.getVin());
        assertEquals(car.getNumber(), updatedCar.getNumber());
        assertEquals(car.getBrand(), updatedCar.getBrand());
        assertEquals(car.getModel(), updatedCar.getModel());
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void updateIfCarNotExistTest() {
        //Init
        when(carRepository.existsById(car.getId())).thenReturn(false);

        //Then
        assertThrows(CarNotFoundException.class, () -> carService.update(car));
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(0)).save(car);
    }

    @Test
    void deleteTest() throws CarNotFoundException {
        //Init
        when(carRepository.existsById(car.getId())).thenReturn(true);
        doNothing().when(carRepository).deleteById(car.getId());

        //When
        UUID deletedCarId = carService.delete(car.getId());

        //Then
        assertNotNull(deletedCarId, "Car is not deleted.");
        assertEquals(car.getId(), deletedCarId);
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(1)).deleteById(car.getId());
    }

    @Test
    void deleteIfCarNotExist() {
        //Init
        when(carRepository.existsById(car.getId())).thenReturn(false);

        //Then
        assertThrows(CarNotFoundException.class, () -> carService.delete(car.getId()));
        verify(carRepository, times(1)).existsById(car.getId());
        verify(carRepository, times(0)).deleteById(car.getId());
    }
}