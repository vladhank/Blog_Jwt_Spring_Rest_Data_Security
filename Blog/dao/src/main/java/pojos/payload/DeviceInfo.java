package pojos.payload;

import enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import validation.annotation.NullOrNotBlank;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {

//	@NotBlank(message = "Device id cannot be blank")
	private String deviceId;

	@NotNull(message = "Device type cannot be null")
	private DeviceType deviceType;

	@NullOrNotBlank(message = "Device notification token can be null but not blank")
	private String notificationToken;

	public DeviceInfo(String deviceId, DeviceType deviceType, String notificationToken) {
		this.deviceId = deviceId;
		deviceType = deviceType;
		notificationToken = notificationToken;
	}

}
