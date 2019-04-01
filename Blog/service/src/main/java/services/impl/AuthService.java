package services.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import pojos.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import pojos.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pojos.payload.*;
import pojos.tokens.EmailVerificationToken;
import pojos.tokens.RefreshToken;
import security.JwtTokenProvider;
import services.RoleService;
import services.UserService;
import services.exceptions.*;
import services.util.Util;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailVerificationTokenService emailVerificationTokenService;

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    private static final Logger logger = Logger.getLogger(AuthService.class);


    public Optional<User> registerUser(RegistrationRequest newRegistrationRequest) {
        String newRegistrationRequestEmail = newRegistrationRequest.getEmail();
        if (emailAlreadyExists(newRegistrationRequestEmail)) {
            logger.error("Email already exists: " + newRegistrationRequestEmail);
            throw new ResourceAlreadyInUseException("Email", "Address", newRegistrationRequestEmail);
        }
        logger.info("Trying to register new user [" + newRegistrationRequestEmail + "]");
        User newUser = userService.createUser(newRegistrationRequest);
        User registeredNewUser = userService.save(newUser);
        return Optional.ofNullable(registeredNewUser);
    }


    public Boolean emailAlreadyExists(String email) {
        return userService.existsByEmail(email);
    }


    public Boolean usernameAlreadyExists(String username) {
        return userService.existsByUsername(username);
    }


    public Optional<Authentication> authenticateUser(LoginRequest loginRequest) {
        return Optional.ofNullable(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword())));
    }


    public Optional<User> confirmEmailRegistration(String emailToken) {
        Optional<EmailVerificationToken> emailVerificationTokenOpt =
                emailVerificationTokenService.findByToken(emailToken);
        emailVerificationTokenOpt.orElseThrow(() ->
                new ResourceNotFoundException("Token", "Email verification", emailToken));

        Optional<User> registeredUserOpt = emailVerificationTokenOpt.map(EmailVerificationToken::getUser);

        Boolean isUserAlreadyVerified = emailVerificationTokenOpt.map(EmailVerificationToken::getUser)
                .map(User::getIsEmailVerified).filter(Util::isTrue).orElse(false);

        if (isUserAlreadyVerified) {
            logger.info("User [" + emailToken + "] already registered.");
            return registeredUserOpt;
        }
        emailVerificationTokenOpt.ifPresent(emailVerificationTokenService::verifyExpiration);
        emailVerificationTokenOpt.ifPresent(EmailVerificationToken::confirmStatus);
        emailVerificationTokenOpt.ifPresent(emailVerificationTokenService::save);
        registeredUserOpt.ifPresent(User::confirmVerification);
        registeredUserOpt.ifPresent(userService::save);
        return registeredUserOpt;
    }


    public Optional<EmailVerificationToken> recreateRegistrationToken(String existingToken) {
        Optional<EmailVerificationToken> emailVerificationTokenOpt =
                emailVerificationTokenService.findByToken(existingToken);
        emailVerificationTokenOpt.orElseThrow(() ->
                new ResourceNotFoundException("Token", "Existing email verification", existingToken));
        Boolean userAlreadyVerified =
                emailVerificationTokenOpt.map(EmailVerificationToken::getUser)
                        .map(User::getIsEmailVerified).filter(Util::isTrue).orElse(false);
        if (userAlreadyVerified) {
            return Optional.empty();
        }
        return emailVerificationTokenOpt.map(emailVerificationTokenService::updateExistingTokenWithNameAndExpiry);
    }


    public Boolean currentPasswordMatches(User currentUser, String password) {
        return passwordEncoder.matches(password, currentUser.getPassword());
    }

    public Optional<User> updatePassword(User user,
                                         UpdatePasswordRequest updatePasswordRequest) {
        User currentUser = userService.getLoggedInUser(user.getEmail());

        if (!currentPasswordMatches(currentUser, updatePasswordRequest.getOldPassword())) {
            logger.info("Current password is invalid for [" + currentUser.getPassword() + "]");
            throw new UpdatePasswordException(currentUser.getEmail(), "Invalid current password");
        }
        String newPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        currentUser.setPassword(newPassword);
        userService.save(currentUser);
        return Optional.ofNullable(currentUser);
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        return tokenProvider.generateToken(customUserDetails);
    }

    public String generateTokenFromUserId(Long userId) {
        return tokenProvider.generateTokenFromUserId(userId);
    }

    public Optional<RefreshToken> createAndPersistRefreshTokenForDevice(Authentication authentication,
                                                                        LoginRequest loginRequest, User currentUser) {
//        CustomUserDetails currentUser = ( CustomUserDetails ) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        UserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
        userDevice.setUser(currentUser);
        userDevice.setRefreshToken(refreshToken);
        refreshToken.setUserDevice(userDevice);
        refreshToken = refreshTokenService.save(refreshToken);
        return Optional.ofNullable(refreshToken);
    }

    public Optional<String> refreshJwtToken(TokenRefreshRequest tokenRefreshRequest) {
        //tokenFromDb's device info should match this one
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();
        Optional<RefreshToken> refreshTokenOpt =
                refreshTokenService.findByToken(requestRefreshToken);
        refreshTokenOpt.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Missing refresh token in " +
                "database. Please login again"));

        refreshTokenOpt.ifPresent(refreshTokenService::verifyExpiration);
        refreshTokenOpt.ifPresent(userDeviceService::verifyRefreshAvailability);
        refreshTokenOpt.ifPresent(refreshTokenService::increaseCount);
        return refreshTokenOpt.map(RefreshToken::getUserDevice)
                .map(UserDevice::getUser)
                .map(User::getId).map(this::generateTokenFromUserId);
    }

    public Optional<PasswordResetToken> generatePasswordResetToken(PasswordResetLinkRequest passwordResetLinkRequest) {
        String email = passwordResetLinkRequest.getEmail();
        Optional<User> userOpt = userService.findByEmail(email);
        userOpt.orElseThrow(() -> new PasswordResetLinkException(email, "No matching user found for the given " +
                "request"));
        PasswordResetToken passwordResetToken = passwordResetTokenService.createToken();
        userOpt.ifPresent(passwordResetToken::setUser);
        passwordResetTokenService.save(passwordResetToken);
        return Optional.ofNullable(passwordResetToken);
    }

    public Optional<User> resetPassword(PasswordResetRequest passwordResetRequest) {
        String token = passwordResetRequest.getToken();
        Optional<PasswordResetToken> passwordResetTokenOpt = passwordResetTokenService.findByToken(token);
        passwordResetTokenOpt.orElseThrow(() -> new ResourceNotFoundException("Password Reset Token", "Token Id",
                token));

        passwordResetTokenOpt.ifPresent(passwordResetTokenService::verifyExpiration);
        final String encodedPassword = passwordEncoder.encode(passwordResetRequest.getPassword());

        Optional<User> userOpt = passwordResetTokenOpt.map(PasswordResetToken::getUser);
        userOpt.ifPresent(user -> user.setPassword(encodedPassword));
        userOpt.ifPresent(userService::save);
        return userOpt;
    }
}
