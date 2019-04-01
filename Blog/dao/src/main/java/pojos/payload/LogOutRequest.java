package pojos.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogOutRequest {

    @Valid
    @NotNull(message = "Device info cannot be null")
    private DeviceInfo deviceInfo;

}
