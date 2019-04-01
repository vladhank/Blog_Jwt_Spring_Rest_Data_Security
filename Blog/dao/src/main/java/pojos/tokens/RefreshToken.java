package pojos.tokens;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import pojos.UserDevice;
import pojos.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "REFRESH_TOKEN")
public class RefreshToken extends DateAudit {

	@Id
	@Column(name = "TOKEN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TOKEN", nullable = false, unique = true)
	@NaturalId(mutable = true)
	private String token;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_DEVICE_ID", unique = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private UserDevice userDevice;

	@Column(name = "REFRESH_COUNT")
	private Long refreshCount;

	@Column(name = "EXPIRY_DT", nullable = false)
	private Instant expiryDate;

	public void incrementRefreshCount() {
		refreshCount = refreshCount + 1;
	}

}
