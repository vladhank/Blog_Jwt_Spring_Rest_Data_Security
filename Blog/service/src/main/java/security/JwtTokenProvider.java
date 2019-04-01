package security;

import pojos.CustomUserDetails;
import io.jsonwebtoken.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pojos.User;
import services.exceptions.InvalidTokenRequestException;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private static final Logger logger = Logger.getLogger(JwtTokenProvider.class);

	@Value("mySecret")
	private String jwtSecret;

	@Value("900000")
	private Long jwtExpirationInMs;

	@Value("Refresh")
	private String jwtClaimRefreshName;


	public String generateToken(CustomUserDetails customUserDetails) {
		Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
		return Jwts.builder()
				.setSubject(Long.toString(customUserDetails.getId()))
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(expiryDate))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	public String generateTokenFromUserId(Long userId) {
		Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
		return Jwts.builder()
				.setSubject(Long.toString(userId))
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(expiryDate))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody();

		return Long.parseLong(claims.getSubject());
	}


	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
			throw new InvalidTokenRequestException("JWT", authToken, "Incorrect signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
			throw new InvalidTokenRequestException("JWT", authToken, "Malformed jwt token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
			throw new InvalidTokenRequestException("JWT", authToken, "Token expired. Refresh required.");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
			throw new InvalidTokenRequestException("JWT", authToken, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
			throw new InvalidTokenRequestException("JWT", authToken, "Illegal argument token");
		}
	}

	public Long getExpiryDuration() {
		return jwtExpirationInMs;
	}
}