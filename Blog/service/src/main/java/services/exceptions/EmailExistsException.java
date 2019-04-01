package services.exceptions;

public class EmailExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailExistsException() {
        super();
    }

    public EmailExistsException(final String errorMessage) {
        super(errorMessage);
    }
}
