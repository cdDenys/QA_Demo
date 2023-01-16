package carshare.service;

import carshare.controller.dto.UserDetailsDTO;
import carshare.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading users from database
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method return userdata by login
     *
     * @param login                             Login from authentication request
     * @return                                  User data
     * @throws UsernameNotFoundException        if user not found
     */
    @Override
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        return UserDetailsDTO.build(userRepository
                .getByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login)));
    }
}
