package module.mailtracking.domain;

import myorg.domain.exceptions.DomainException;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;

public class Document extends Document_Base {

    public static final Integer MAX_DOCUMENT_FILE_SIZE = 3145728;

    public Document() {
	super();
    }

    public Document(String displayName, String filename, byte[] content, String description) {
	super();
	init(displayName, filename, content, description);
	setDescription(description);
    }

    private void init(String displayName, String filename, byte[] content, String description) {
	checkParameters(displayName, filename, content, description);
	init(displayName, filename, content);
	setDescription(description);
    }

    private void checkParameters(String displayName, String filename, byte[] content, String description) {
	if (StringUtils.isEmpty(filename)) {
	    throw new DomainException("error.mail.tracking.document.filename.cannot.be.empty");
	}

	if (StringUtils.isEmpty(description)) {
	    throw new DomainException("error.mail.tracking.document.description.cannot.be.empty");
	}

	if (content == null) {
	    throw new DomainException("error.mail.tracking.document.content.cannot.be.null");
	}

    }

    @Service
    public static Document saveDocument(String displayName, String filename, byte[] content, String description) {
	return new Document(displayName, filename, content, description);
    }
}
