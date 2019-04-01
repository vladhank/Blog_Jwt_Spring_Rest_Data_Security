package repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pojos.tokens.EmailVerificationToken;
import java.util.List;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailVerificationToken,Long> {

    List<EmailVerificationToken> findByUserEmail(String email);

    List<EmailVerificationToken> findByToken(String token);

}
