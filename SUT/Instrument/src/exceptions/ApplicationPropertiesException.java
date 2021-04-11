package exceptions;

public class ApplicationPropertiesException extends Exception {
    public ApplicationPropertiesException(Exception e) {
        this.initCause(e);
    }
    public ApplicationPropertiesException() {
    }
}
