package carshare.service;

import carshare.advice.exception.UserCreationException;
import carshare.advice.exception.UserNotFoundException;
import carshare.config.jwt.JwtConfig;
import carshare.controller.dto.UserDetailsDTO;
import carshare.controller.dto.JwtDTO;
import carshare.database.entity.User;
import carshare.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for user management
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            final UserRepository userRepository,
            final RoleService roleService,
            final AuthenticationManager authenticationManager,
            final JwtConfig jwtConfig,
            final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method accepts userdata and if not null save new user to database
     *
     * @param user                          User data
     * @throws UserCreationException        if user is not created
     */
    @Transactional
    public User create(final User user) throws UserCreationException {
        if (user == null) {
            throw new UserCreationException("Check your data and try again.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roleService.getRole("USER"));
        return userRepository.save(user);
    }

    /**
     * Method accepts UUID of user and return user by UUID
     *
     * @param userId                        UUID of user data
     * @return                              User data
     * @throws UserNotFoundException        if user not found
     */
    public User getById(final UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)){
            throw new UserNotFoundException("User not exists.");
        }
        return userRepository.findById(userId).orElse(new User());
    }

    /**
     * Method return list of all users from database
     *
     * @return                              List of users
     */
    public List<User> getAll() {
        return new ArrayList<>((Collection<? extends User>) userRepository.findAll());
    }


    /**
     * Method accepts user data change fields and rewrite it to database
     *
     * @param user                          User data
     * @throws UserNotFoundException        if user not found
     */
    @Transactional
    public User update(final User user) throws UserNotFoundException {
        if (!userRepository.existsById(user.getId())){
            throw new UserNotFoundException("User not exists.");
        }
        return userRepository.save(user);
    }

    /**
     * Method accepts UUID of user and delete it from database
     *
     * @param userId                        UUID of user data
     * @throws UserNotFoundException        if user not found
     */
    @Transactional
    public UUID delete(final UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)){
            throw new UserNotFoundException("User not exists.");
        }
        userRepository.deleteById(userId);
        return userId;
    }

    /**
     * Method authenticate user by login and password
     *
     * @param login                         user login from SingIn request
     * @param password                      user password from SingIn request
     * @return                              JwtDTO with userdata, jwt token and roles
     */
    public JwtDTO login(final String login, final String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtConfig.generateJwtToken(authentication);

        UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtDTO(jwt,
                userDetails.getId(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getMiddleName(),
                userDetails.getEmail(),
                userDetails.getVerified(),
                userDetails.getBirthDate(),
                userDetails.getCreatTs(),
                userDetails.getSex(),
                userDetails.getAvatarPath(),
                roles);
    }

    /**
     * Method check if user verified
     *
     * @param userId                         User id for verification check
     * @return                               Result of verification
     */
    public boolean isVerified(final UUID userId) throws UserNotFoundException {
        return getById(userId).getVerified();
    }
}