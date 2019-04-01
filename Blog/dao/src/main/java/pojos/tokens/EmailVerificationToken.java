package pojos.tokens;

import enums.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pojos.User;
import pojos.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class EmailVerificationToken extends DateAudit {

	@Id
	@Column(name = "TOKEN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TOKEN", nullable = false, unique = true)
	private String token;

	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "USER_ID")
	private User user;

	@Column(name = "TOKEN_STATUS")
	@Enumerated(EnumType.STRING)
	private TokenStatus tokenStatus;

	@Column(name = "EXPIRY_DT", nullable = false)
	private Instant expiryDate;

	public void confirmStatus() {
		setTokenStatus(TokenStatus.STATUS_CONFIRMED);
	}

}
