package services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pojos.UserDevice;
import pojos.tokens.RefreshToken;
import repositories.RefreshTokenRepository;
import services.exceptions.TokenRefreshException;
import services.util.Util;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Value("900000")
	private Long refreshTokenDurationMs;

	/**
	 * Find a refresh token based on the id
	 */
	public Optional<RefreshToken> findById(Long id) {
		return refreshTokenRepository.findById(id);
	}

	/**
	 * Find the refresh token string based on the id
	 */
	public Optional<String> findTokenById(Long id) {
		return refreshTokenRepository.findTokenById(id);
	}

	/**
	 * Find a refresh token based on the natural id i.e the token itself
	 */
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	/**
	 * Finds the user device the refreshed token is attached to using the id
	 */
	public Optional<UserDevice> findUserDeviceById(Long id) {
		return refreshTokenRepository.findUserDeviceById(id);
	}

	/**
	 * Finds the user device the refreshed token is attached to with the natural id
	 */
	public Optional<UserDevice> findUserDeviceByToken(String token) {
		return refreshTokenRepository.findUserDeviceByToken(token);
	}

	/**
	 * Persist the updated refreshToken instance to database
	 */
	public RefreshToken save(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}


	/**
	 * Creates and returns a new refresh token
	 */
	public RefreshToken createRefreshToken() {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setToken(Util.generateRandomUuid());
		refreshToken.setRefreshCount(0L);
		return refreshToken;
	}

	/**
	 * Verify whether the token provided has expired or not on the basis of the current
	 * server time and/or throw error otherwise
	 */
	public void verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			throw new TokenRefreshException(token.getToken(), "Expired token. Please issue a new request");
		}
	}

	/**
	 * Delete the refresh token associated with the user device
	 */
	public void deleteById(Long id) {
		refreshTokenRepository.deleteById(id);
	}

	/**
	 * Increase the count of the token usage in the database. Useful for
	 * audit purposes
	 */
	public void increaseCount(RefreshToken refreshToken) {
		refreshToken.incrementRefreshCount();
		save(refreshToken);
	}
}
