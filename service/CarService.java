package carshare.service;

import carshare.advice.exception.CarNotFoundException;
import carshare.advice.exception.UserNotFoundException;
import carshare.database.entity.Car;
import carshare.database.repository.CarRepository;
import carshare.advice.exception.CarCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Service for car management
 */
@Service
public class CarService {

    private final CarRepository carRepository;
    private final UserService userService;

    @Autowired
    public CarService(final CarRepository carRepository, final UserService userService) {
        this.carRepository = carRepository;
        this.userService = userService;
    }

    /**
     * Method accepts car data and save it to database
     *
     * @param car                               Car data
     * @throws CarCreationException             if car is not created
     * @throws UserNotFoundException            if user not found
     */
    @Transactional
    public Car create(final Car car) throws CarCreationException, UserNotFoundException {
        if (car != null && car.getUser() != null &&
                userService.isVerified(car.getUser().getId())
        ) {
            car.getImages().forEach(carImage ->
                    carImage.setCar(car)
            );
            return carRepository.save(car);
        }
        throw new CarCreationException("User not verified or car data is invalid.");
    }

    /**
     * Method accepts UUID of car and return car by UUID
     *
     * @param carId                             UUID of car data
     * @return                                  Car with data
     * @throws CarNotFoundException             if car not exist
     */
    public Car getById(final UUID carId) throws CarNotFoundException {
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException("Car not exists.");
        }
        return carRepository.findById(carId).orElse(new Car());
    }

    /**
     * Method return list of all existed entities from database
     *
     * @return                                  List of all cars
     */
    public List<Car> getAll() {
        return new ArrayList<>((Collection<? extends Car>) carRepository.findAll());
    }

    /**
     * Method accepts car data change fields and rewrite it to database
     *
     * @param car                               Car data
     * @throws CarNotFoundException             if car not exist
     */
    @Transactional
    public Car update(final Car car) throws CarNotFoundException {
        if (!carRepository.existsById(car.getId())) {
            throw new CarNotFoundException("Car not exists.");
        }
        return carRepository.save(car);
    }

    /**
     * Method accepts UUID of car and delete it from database
     *
     * @param carId                             UUID of car data
     * @throws CarNotFoundException             if car not exist
     */
    @Transactional
    public UUID delete(final UUID carId) throws CarNotFoundException {
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException("Car not exists.");
        }
        carRepository.deleteById(carId);
        return carId;
    }
}