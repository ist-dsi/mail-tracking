/*
 * @(#)Document.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Anil Kassamali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Correspondence Registry Module.
 *
 *   The Correspondence Registry Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Correspondence Registry Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Correspondence Registry Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.mailtracking.domain;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Anil Kassamali
 * @author Luis Cruz
 * 
 */
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

    @Atomic
    public void deleteDocument() {
        this.setState(DocumentState.DELETED);
    }

    @Atomic
    public static Document saveDocument(String displayName, String filename, byte[] content, String description, DocumentType type) {
        return new Document(displayName, filename, content, description, type);
    }

    public Boolean isMainDocument() {
        return DocumentType.MAIN_DOCUMENT.equals(getType());
    }

    public Boolean isOtherDocument() {
        return DocumentType.OTHER_DOCUMENT.equals(getType());
    }

    @Override
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
