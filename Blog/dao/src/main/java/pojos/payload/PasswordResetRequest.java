package pojos.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import validation.annotation.MatchPassword;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MatchPassword
public class PasswordResetRequest {

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Confirm Password cannot be blank")
    private String confirmPassword;

    @NotBlank(message = "Token has to be supplied along with a password reset request")
    private String token;

}
