package pojos.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import validation.annotation.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NullOrNotBlank(message = "Registration username can be null but not blank")
    private String username;

    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$|^[а-яА-я]+(([',. -][а-яА-Я ])?[а-яА-Я]*)*$", message = "First name can only contain letters")
    @NullOrNotBlank(message = "Registration username can be null but not blank")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$|^[а-яА-я]+(([',. -][а-яА-Я ])?[а-яА-Я]*)*$", message = "Last name can only contain letters")
    private String lastName;

    @NullOrNotBlank(message = "Registration email can be null but not blank")
    private String email;

    @NotNull(message = "Registration password cannot be null")
    private String password;

}
