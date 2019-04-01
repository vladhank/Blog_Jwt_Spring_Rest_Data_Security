package repositories;

import pojos.PasswordResetToken;
import pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	Optional<Instant> findExpiryDateByToken(String token);

	Boolean existsByToken(String token);

	Optional<User> findUserByToken(String token);

	Optional<PasswordResetToken> findByToken(String token);
}