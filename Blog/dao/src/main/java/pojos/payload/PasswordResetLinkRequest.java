package pojos.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetLinkRequest {

    @NotBlank(message = "Email cannot be blank")
    private String email;

}

