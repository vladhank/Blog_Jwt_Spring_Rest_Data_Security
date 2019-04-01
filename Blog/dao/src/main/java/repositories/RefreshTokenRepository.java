package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pojos.UserDevice;
import pojos.tokens.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findById(Long id);

	Optional<String> findTokenById(Long id);

	Optional<RefreshToken> findByToken(String token);

	Optional<UserDevice> findUserDeviceById(Long id);

	Optional<UserDevice> findUserDeviceByToken(String token);
}
