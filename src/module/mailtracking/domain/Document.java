package module.mailtracking.domain;

import java.util.Comparator;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;

public class Document extends Document_Base {

    public static final Integer MAX_DOCUMENT_FILE_SIZE = 8388608;

    public static Comparator<Document> SORT_DOCUMENTS_BY_DATE = new BeanComparator("creationDate");

    public Document() {
	super();
	setMyOrg(MyOrg.getInstance());
	setCreationDate(new DateTime());
    }

    public Document(String displayName, String filename, byte[] content, String description, DocumentType type) {
	super();
	init(displayName, filename, content, description, type);
	setDescription(description);
	setState(DocumentState.ACTIVE);
    }

    private void init(String displayName, String filename, byte[] content, String description, DocumentType type) {
	checkParameters(displayName, filename, content, description, type);
	init(displayName, filename, content);
	setDescription(description);
	setType(type);
    }

    private void checkParameters(String displayName, String filename, byte[] content, String description, DocumentType type) {
	if (StringUtils.isEmpty(filename)) {
	    throw new DomainException("error.mail.tracking.document.filename.cannot.be.empty");
	}

	if (StringUtils.isEmpty(description)) {
	    throw new DomainException("error.mail.tracking.document.description.cannot.be.empty");
	}

	if (content == null) {
	    throw new DomainException("error.mail.tracking.document.content.cannot.be.null");
	}

	if (type == null) {
	    throw new DomainException("error.mail.tracking.document.type.cannot.be.null");
	}
    }

    @Service
    public void deleteDocument() {
	this.setState(DocumentState.DELETED);
    }

    @Service
    public static Document saveDocument(String displayName, String filename, byte[] content, String description, DocumentType type) {
	return new Document(displayName, filename, content, description, type);
    }

    public Boolean isMainDocument() {
	return DocumentType.MAIN_DOCUMENT.equals(getType());
    }

    public Boolean isOtherDocument() {
	return DocumentType.OTHER_DOCUMENT.equals(getType());
    }

    public void deleteDomainObject() {
	removeCorrespondenceEntry();
	// removeMyOrg();
	removeStorage();
	removeFileSupport();

	super.deleteDomainObject();
    }

    public boolean isDocumentDeleted() {
	return DocumentState.DELETED.equals(getState());
    }
}
