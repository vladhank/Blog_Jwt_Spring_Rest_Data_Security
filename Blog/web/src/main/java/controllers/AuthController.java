package controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import pojos.CustomUserDetails;
import pojos.PasswordResetToken;
import pojos.User;
import pojos.payload.*;
import pojos.tokens.EmailVerificationToken;
import pojos.tokens.RefreshToken;
import security.JwtTokenProvider;
import services.UserService;
import services.event.OnGenerateResetLinkEvent;
import services.event.OnRegenerateEmailVerificationEvent;
import services.event.OnUserAccountChangeEvent;
import services.event.OnUserRegistrationCompleteEvent;
import services.exceptions.*;
import services.impl.AuthService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private static final Logger logger = Logger.getLogger(AuthController.class);


    @GetMapping("/checkEmailInUse")
    public ResponseEntity<?> checkEmailInUse(@RequestParam("email") String email) {
        Boolean emailExists = authService.emailAlreadyExists(email);
        return ResponseEntity.ok(new ApiResponse(emailExists.toString(), true));
    }

    @GetMapping("/checkUsernameInUse")
    public ResponseEntity<?> checkUsernameInUse(@RequestParam(
            "username") String username) {
        Boolean usernameExists = authService.usernameAlreadyExists(username);
        return ResponseEntity.ok(new ApiResponse(usernameExists.toString(), true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Authentication> authenticationOpt = authService.authenticateUser(loginRequest);
        authenticationOpt.orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));
        Authentication authentication = authenticationOpt.get();
        CustomUserDetails customUserDetails = ( CustomUserDetails ) authentication.getPrincipal();

        logger.info("Logged in User returned : " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<RefreshToken> refreshTokenOpt = authService
                .createAndPersistRefreshTokenForDevice(authentication, loginRequest, customUserDetails.getUser());
        refreshTokenOpt.orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequest + "]"));
        String refreshToken = refreshTokenOpt.map(RefreshToken::getToken).get();
        String jwtToken = authService.generateToken(customUserDetails);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwtToken, refreshToken,
                tokenProvider.getExpiryDuration()));
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest,
                                          WebRequest request) {
        Optional<User> registeredUserOpt = authService.registerUser(registrationRequest);
        registeredUserOpt.orElseThrow(() -> new UserRegistrationException(registrationRequest.getEmail(),
                "Missing user in database"));
        UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/confirm");

        OnUserRegistrationCompleteEvent onUserRegistrationCompleteEvent =
                new OnUserRegistrationCompleteEvent(registeredUserOpt.get(), urlBuilder);
        applicationEventPublisher.publishEvent(onUserRegistrationCompleteEvent);

        registeredUserOpt.ifPresent(user -> logger.info("Registered User returned [API[: " + user));
        return ResponseEntity.ok(new ApiResponse("User registered successfully. Check your email" +
                " for verification ", true));
    }


    @PostMapping("/forgot_password")
    public ResponseEntity<?> resetLink(@Valid @RequestBody PasswordResetLinkRequest passwordResetLinkRequest) {
        Optional<PasswordResetToken> passwordResetTokenOpt = authService
                .generatePasswordResetToken(passwordResetLinkRequest);
        passwordResetTokenOpt.orElseThrow(() -> new PasswordResetLinkException(passwordResetLinkRequest.getEmail(),
                "Couldn't create a valid token"));
        PasswordResetToken passwordResetToken = passwordResetTokenOpt.get();
        UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/reset");
        OnGenerateResetLinkEvent generateResetLinkMailEvent = new OnGenerateResetLinkEvent(passwordResetToken,
                urlBuilder);
        applicationEventPublisher.publishEvent(generateResetLinkMailEvent);
        return ResponseEntity.ok(new ApiResponse("Password reset link sent successfully", true));
    }


    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        Optional<User> userOpt = authService.resetPassword(passwordResetRequest);
        userOpt.orElseThrow(() -> new PasswordResetException(passwordResetRequest.getToken(), "Error in resetting " +
                "password"));
        User user = userOpt.get();
        OnUserAccountChangeEvent onPasswordChangeEvent = new OnUserAccountChangeEvent(user, "Reset Password",
                "Changed Successfully");
        applicationEventPublisher.publishEvent(onPasswordChangeEvent);
        return ResponseEntity.ok(new ApiResponse("Password changed successfully", true));
    }


    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        Optional<User> verifiedUser = authService.confirmEmailRegistration(token);
        verifiedUser.orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", token,
                "Failed to confirm. Please generate a new email verification request!" +
                        "Request failed! "));
        return ResponseEntity.ok(new ApiResponse("User verified successfully", true));
    }


    @GetMapping("/resendRegistrationToken")
    public ResponseEntity<?> resendRegistrationToken(@RequestParam("token") String existingToken) {
        Optional<EmailVerificationToken> newEmailTokenOpt = authService.recreateRegistrationToken(existingToken);
        newEmailTokenOpt.orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken,
                "User is already registered. No need to re-generate token"));

        User registeredUser = newEmailTokenOpt.map(EmailVerificationToken::getUser)
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken,
                        "No user associated with this request. Re-verification denied"));

        UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/confirm");
        OnRegenerateEmailVerificationEvent regenerateEmailVerificationEvent = new OnRegenerateEmailVerificationEvent(
                registeredUser, urlBuilder, newEmailTokenOpt.get());
        applicationEventPublisher.publishEvent(regenerateEmailVerificationEvent);

        return ResponseEntity.ok(new ApiResponse("Email verification resent successfully", true));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJwtToken(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        Optional<String> updatedJwtToken = authService.refreshJwtToken(tokenRefreshRequest);
        updatedJwtToken.orElseThrow(() -> new TokenRefreshException(tokenRefreshRequest.getRefreshToken(),
                "Unexpected error during token refresh. Please logout and login again."));
        String refreshToken = tokenRefreshRequest.getRefreshToken();
        logger.info("Created new Jwt Auth token: " + updatedJwtToken);
        return ResponseEntity.ok(new JwtAuthenticationResponse(updatedJwtToken.get(), refreshToken,
                tokenProvider.getExpiryDuration()));
    }
}
