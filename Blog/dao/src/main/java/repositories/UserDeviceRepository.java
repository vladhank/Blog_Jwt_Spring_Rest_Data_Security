package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pojos.UserDevice;
import pojos.tokens.RefreshToken;

import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

	Optional<UserDevice> findById(Long id);

	Optional<RefreshToken> findRefreshTokenById(Long id);

	Optional<UserDevice> findByRefreshToken(RefreshToken refreshToken);

	Optional<UserDevice> findByDeviceId(String deviceId);
}
