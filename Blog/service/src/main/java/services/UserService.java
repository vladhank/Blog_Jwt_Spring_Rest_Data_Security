package services;

import pojos.CustomUserDetails;
import pojos.User;
import pojos.payload.LogOutRequest;
import pojos.payload.RegistrationRequest;

import java.util.Optional;

public interface UserService extends IService<User> {

    User createUser(RegistrationRequest registerRequest);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User getLoggedInUser(String email);

    User makeAdmin(long userID);

    Optional<User> findById(Long Id);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    void logoutUser(CustomUserDetails customUserDetails, LogOutRequest logOutRequest);

}
