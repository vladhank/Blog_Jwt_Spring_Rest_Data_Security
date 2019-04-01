package controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pojos.CustomUserDetails;
import pojos.User;
import pojos.payload.ApiResponse;
import pojos.payload.LogOutRequest;
import pojos.payload.UpdatePasswordRequest;
import services.UserService;
import services.event.OnUserAccountChangeEvent;
import services.exceptions.UpdatePasswordException;
import services.impl.AuthService;
import validation.annotation.CurrentUser;

import javax.validation.Valid;

@RestController
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserProfile(@CurrentUser CustomUserDetails currentUser) {
        logger.info("Inside secured resource with user");
        logger.info(currentUser.getEmail() + " has role: " + currentUser.getRoles());
        return ResponseEntity.ok("Hello. This is about me");
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAdmins() {
        logger.info("Inside secured resource with admin");
        return ResponseEntity.ok("Hello. This is about admins");
    }

    @PostMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserPassword(@CurrentUser CustomUserDetails customUserDetails,
                                                @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        User updatedUser = authService.updatePassword(customUserDetails.getUser(), updatePasswordRequest)
                .orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));

        OnUserAccountChangeEvent onUserPasswordChangeEvent =
                new OnUserAccountChangeEvent(updatedUser, "Update Password", "Change successful");
        applicationEventPublisher.publishEvent(onUserPasswordChangeEvent);

        return ResponseEntity.ok(new ApiResponse("Password changed successfully", true));
    }

    @PostMapping("/logout")
    public ResponseEntity logoutUser(@RequestBody LogOutRequest logOutRequest,
                                     @Valid @CurrentUser CustomUserDetails customUserDetails) {
        userService.logoutUser(customUserDetails, logOutRequest);
        return ResponseEntity.ok(new ApiResponse("Log out successful", true));
    }
}
