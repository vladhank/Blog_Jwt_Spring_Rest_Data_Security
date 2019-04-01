package pojos.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import validation.annotation.NullOrNotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NullOrNotBlank(message = "Login Username can be null but not blank")
    private String username;

    @NullOrNotBlank(message = "Login Email can be null but not blank")
    private String email;

    @NotNull(message = "Login password cannot be blank")
    private String password;

    @Valid
    @NotNull(message = "Device info cannot be null")
    private DeviceInfo deviceInfo;

}
