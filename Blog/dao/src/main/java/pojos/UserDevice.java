package pojos;

import enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pojos.audit.DateAudit;
import pojos.tokens.RefreshToken;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="USER_DEVICE")
@EqualsAndHashCode(callSuper = true)
public class UserDevice extends DateAudit {

    @Id
    @Column(name = "USER_DEVICE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "DEVICE_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "NOTIFICATION_TOKEN")
    private String notificationToken;

    @Column(name = "DEVICE_ID", unique = true, nullable = false)
    private String deviceId;

    @OneToOne(optional = false, mappedBy = "userDevice")
    private RefreshToken refreshToken;

    @Column(name = "IS_REFRESH_ACTIVE")
    private Boolean isRefreshActive;

}
