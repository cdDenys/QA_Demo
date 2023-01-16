package carshare.service;

import carshare.advice.exception.RoleNotFoundException;
import carshare.database.entity.Role;
import carshare.database.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class RoleServiceTest {

    @MockBean
    private RoleRepository roleRepository;

    private final RoleService roleService;

    @Autowired
    public RoleServiceTest(RoleService roleService) {
        this.roleService = roleService;
    }

    static Role role;

    @BeforeEach
    void setUp() {
        role = new Role(UUID.randomUUID(), "TEST", "TEST");
    }

    @Test
    void createTest() {
        //Init
        when(roleRepository.save(role)).thenReturn(role);

        //When
        Role createdRole = roleService.create(role);

        //Then
        assertNotNull(createdRole, "Role is not created");
        assertEquals(role.getName(), createdRole.getName());
        assertEquals(role.getDescription(), createdRole.getDescription());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void createIfRoleIsNullTest() {
        assertNull(roleService.create(null));
        verify(roleRepository, times(0)).save(role);
    }

    @Test
    void getByIdTest() throws RoleNotFoundException {
        //Init
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.ofNullable(role));

        //When
        Role foundRoleById = roleService.getById(role.getId());

        //Then
        assertNotNull(foundRoleById);
        assertEquals(role.getName(), foundRoleById.getName());
        assertEquals(role.getDescription(), foundRoleById.getDescription());
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(1)).findById(role.getId());
    }

    @Test
    void getByIdIfRoleNotExistTest() {
        //Init
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        //Then
        assertThrows(RoleNotFoundException.class, () -> roleService.getById(role.getId()));
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(0)).findById(role.getId());
    }

    @Test
    void updateTest() throws RoleNotFoundException {
        //Init
        role.setName("TEST_TEST");
        role.setDescription("TEST_TEST");
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        when(roleRepository.save(role)).thenReturn(role);

        //When
        Role updatedRole = roleService.update(role);

        //Then
        assertNotNull(updatedRole);
        assertEquals(role.getName(), updatedRole.getName());
        assertEquals(role.getDescription(), updatedRole.getDescription());
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void updateIfRoleNotExistTest() {
        //Init
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        //Then
        assertThrows(RoleNotFoundException.class, () -> roleService.update(role));
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(0)).save(role);
    }

    @Test
    void deleteTest() throws RoleNotFoundException {
        //Init
        when(roleRepository.existsById(role.getId())).thenReturn(true);
        doNothing().when(roleRepository).deleteById(role.getId());

        //When
        UUID deletedRoleId = roleService.delete(role.getId());

        //Then
        assertNotNull(deletedRoleId, "Car is not deleted.");
        assertEquals(role.getId(), deletedRoleId);
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(1)).deleteById(role.getId());
    }

    @Test
    void deleteIfRoleNotExistTest() {
        //Init
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        //Then
        assertThrows(RoleNotFoundException.class, () -> roleService.delete(role.getId()));
        verify(roleRepository, times(1)).existsById(role.getId());
        verify(roleRepository, times(0)).deleteById(role.getId());

    }
}