package carshare.service;

import carshare.database.entity.DriverLicense;
import carshare.database.repository.DriverLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Service for driver license management
 */
@Service
public class DriverLicenseService {

    private final DriverLicenseRepository driverLicenseRepository;

    @Autowired
    public DriverLicenseService(final DriverLicenseRepository driverLicenseRepository) {
        this.driverLicenseRepository = driverLicenseRepository;
    }

    /**
     * Method accepts driver license data and save it to database
     *
     * @param driverLicense                         Driver license data
     */
    @Transactional
    public DriverLicense create(final DriverLicense driverLicense) {
        if (driverLicense == null) {
            return null;
        }
        return driverLicenseRepository.save(driverLicense);
    }

    /**
     * Method accepts UUID of driver license and return driver license by UUID
     *
     * @param driverLicenseId                       UUID of driver license data
     * @return                                      Driver license data
     */
    public DriverLicense getById(final UUID driverLicenseId) {
        if (!driverLicenseRepository.existsById(driverLicenseId)){
            return null;
        }
        return driverLicenseRepository.findById(driverLicenseId).orElse(new DriverLicense());
    }

    /**
     * Method return list of all driver licenses from database
     *
     * @return                                      List of all driver licenses
     */
    public List<DriverLicense> getAll(){
        return new ArrayList<>((Collection<? extends DriverLicense>) driverLicenseRepository.findAll());
    }

    /**
     * Method accepts driver license data change fields and rewrite it to database
     *
     * @param driverLicense                         Driver license data
     */
    @Transactional
    public DriverLicense update(final DriverLicense driverLicense) {
        if (!driverLicenseRepository.existsById(driverLicense.getId())){
            return null;
        }
        return driverLicenseRepository.save(driverLicense);
    }

    /**
     * Method accepts UUID of driver license and delete it from database
     *
     * @param driverLicenseId                       UUID of driver license
     */
    @Transactional
    public UUID delete(final UUID driverLicenseId) {
        if (!driverLicenseRepository.existsById(driverLicenseId)){
            return null;
        }
        driverLicenseRepository.deleteById(driverLicenseId);
        return driverLicenseId;
    }
}
