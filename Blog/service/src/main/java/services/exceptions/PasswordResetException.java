package services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class PasswordResetException extends RuntimeException {

	private String user;
	private String message;

	public PasswordResetException(String user, String message) {
		super(String.format("Couldn't reset password for [%s]: [%s])", user, message));
		this.user = user;
		this.message = message;
	}
}