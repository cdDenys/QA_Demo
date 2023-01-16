package carshare.service;

import carshare.advice.exception.RoleNotFoundException;
import carshare.database.entity.Role;
import carshare.database.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for role management
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Method accepts role data and save it to database
     *
     * @param role                                  Role data
     */
    @Transactional
    public Role create(final Role role) {
        if (role == null) {
            return null;
        }
        return roleRepository.save(role);
    }

    /**
     * Method accepts UUID of role and return role by UUID
     *
     * @param roleId                                UUID of role data
     * @return                                      Role data
     * @throws RoleNotFoundException                if role not found
     */
    public Role getById(final UUID roleId) throws RoleNotFoundException {
        if (!roleRepository.existsById(roleId)){
            throw new RoleNotFoundException("Role not exists.");
        }
        return roleRepository.findById(roleId).orElse(new Role());
    }

    /**
     * Method return list of all roles from database
     *
     * @return                                      List of all roles
     */
    public List<Role> getAll(){
        return new ArrayList<>((Collection<? extends Role>) roleRepository.findAll());
    }

    /**
     * Method accepts role name and return role with data
     *
     * @param name                                  Name of role
     */
    public Set<Role> getRole(String name){
        return roleRepository.getByName(name);
    }

    /**
     * Method accepts role data change fields and rewrite it to database
     *
     * @param role                                  Role data
     */
    @Transactional
    public Role update(final Role role) throws RoleNotFoundException {
        if (!roleRepository.existsById(role.getId())){
            throw new RoleNotFoundException("Role not exists.");
        }
       return roleRepository.save(role);
    }

    /**
     * Method accepts UUID of role and delete it from database
     *
     * @param roleId                                UUID of role data
     * @throws RoleNotFoundException                if role not found
     */
    @Transactional
    public UUID delete(final UUID roleId) throws RoleNotFoundException {
        if (!roleRepository.existsById(roleId)){
            throw new RoleNotFoundException("Role not exists.");
        }
        roleRepository.deleteById(roleId);
        return roleId;
    }
}
