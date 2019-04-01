package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pojos.User;
import pojos.tokens.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	Optional<EmailVerificationToken> findByToken(String token);

	EmailVerificationToken findByUser(User user);
}
