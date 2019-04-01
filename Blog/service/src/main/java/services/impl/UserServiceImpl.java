package services.impl;

import enums.RoleName;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pojos.CustomUserDetails;
import pojos.Role;
import pojos.User;
import pojos.UserDevice;
import pojos.payload.LogOutRequest;
import pojos.payload.RegistrationRequest;
import pojos.tokens.RefreshToken;
import repositories.UserRepository;
import services.RoleService;
import services.exceptions.NotFoundException;
import services.exceptions.ServiceException;
import services.UserService;
import services.exceptions.UserLogoutException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service("userService")
@Transactional
@NoArgsConstructor
public class UserServiceImpl extends BaseService<User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private static final Logger logger = Logger.getLogger(UserService.class);


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public User getLoggedInUser(String email) {

        return findByEmail(email).get();
    }

    public Optional<User> findById(Long Id) {

        return userRepository.findById(Id);
    }

    public User save(User user) {

        return userRepository.save(user);
    }

    public Boolean existsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    public Boolean existsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public User createUser(RegistrationRequest registerRequest) {

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setCreated(LocalDateTime.now());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.getRoles().add(roleService.findRoleByName(RoleName.ROLE_USER));
        newUser.setActive(true);
        newUser.setIsEmailVerified(false);
        return newUser;

    }

    public void logoutUser(CustomUserDetails customUserDetails, LogOutRequest logOutRequest) {
        String deviceId = logOutRequest.getDeviceInfo().getDeviceId();
        Optional<UserDevice> userDeviceOpt = userDeviceService.findByDeviceId(deviceId);
        userDeviceOpt.orElseThrow(() -> new UserLogoutException(logOutRequest.getDeviceInfo().getDeviceId(), "" +
                "Invalid device Id supplied. No matching user device found"));
        logger.info("Removing refresh token associated with device [" + userDeviceOpt + "]");
        userDeviceOpt.map(UserDevice::getRefreshToken)
                .map(RefreshToken::getId)
                .ifPresent(refreshTokenService::deleteById);
    }

    @Override
    public User makeAdmin(long id) {

        User user = Optional.ofNullable(userRepository.getOne(id))
                .orElseThrow(() -> new NotFoundException("User with id " + id + " doesn't exist"));
        user.getRoles().add(new Role(RoleName.ROLE_ADMIN));
        return userRepository.save(user);
    }

}
