package module.mailtracking.domain.exception;

public class PermissionDeniedException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException() {
	super("error.mail.tracking.lack.permissions");
    }
}
