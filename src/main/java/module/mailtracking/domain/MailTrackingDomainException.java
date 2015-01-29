package module.mailtracking.domain;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class MailTrackingDomainException extends DomainException {

    private static final long serialVersionUID = 5516719852788275297L;

    public MailTrackingDomainException(String key, String... args) {
        super(Status.PRECONDITION_FAILED, "resources.MailTrackingResources", key, args);
    }

}
