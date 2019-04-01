package services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pojos.PasswordResetToken;
import repositories.PasswordResetTokenRepository;
import services.UserService;
import services.exceptions.InvalidTokenRequestException;
import services.util.Util;

import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordResetTokenService {

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Value("3600000")
	private Long expiration;

	public PasswordResetToken save(PasswordResetToken passwordResetToken) {
		return passwordResetTokenRepository.save(passwordResetToken);
	}

	public Optional<PasswordResetToken> findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	public PasswordResetToken createToken() {
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		String token = Util.generateRandomUuid();
		passwordResetToken.setToken(token);
		passwordResetToken.setExpiryDate(Instant.now().plusMillis(expiration));
		return passwordResetToken;
	}

	public void verifyExpiration(PasswordResetToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			throw new InvalidTokenRequestException("Password Reset Token", token.getToken(),
					"Expired token. Please issue a new request");
		}
	}
}
