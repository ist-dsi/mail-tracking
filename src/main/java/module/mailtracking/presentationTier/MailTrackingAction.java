/*
 * @(#)MailTrackingAction.java
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
package module.mailtracking.presentationTier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryFieldComparator;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.Document;
import module.mailtracking.domain.DocumentType;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;
import module.mailtracking.domain.exception.PermissionDeniedException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.base.BaseAction;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Anil Kassamali
 * 
 */
@StrutsApplication(bundle = "MailTrackingResources", path = "mailTracking", titleKey = "link.sideBar.mailtracking.manageMailing",
        accessGroup = "logged", hint = "Mail Tracking")
@StrutsFunctionality(app = MailTrackingAction.class, path = "mailTracking", titleKey = "link.sideBar.mailtracking.manageMailing")
@Mapping(path = "/mailtracking")
public class MailTrackingAction extends BaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        readCorrespondenceTypeView(request);
        readMailTracking(request);

        return super.execute(mapping, form, request, response);
    }

    protected MailTracking readMailTracking(HttpServletRequest request) {
        MailTracking mailTracking = (MailTracking) request.getAttribute("mailTracking");

        if (mailTracking == null) {
            String mailTrackingId = request.getParameter("mailTrackingId");
            mailTracking = FenixFramework.getDomainObject(mailTrackingId);
        }

        request.setAttribute("mailTracking", mailTracking);
        return mailTracking;
    }

    protected CorrespondenceType readCorrespondenceTypeView(HttpServletRequest request) {
        String typeValue = request.getParameter("correspondenceType");
        CorrespondenceType type = Strings.isNullOrEmpty(typeValue) ? null : CorrespondenceType.valueOf(typeValue);

        if (type == null) {
            type = CorrespondenceType.SENT;
        }

        request.setAttribute("correspondenceType", type.name());
        return type;
    }

    @EntryPoint
    public final ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        User currentUser = Authenticate.getUser();
        MailTracking mailTracking = readMailTracking(request);

        if (mailTracking != null) {
            if (!mailTracking.isCurrentUserWithSomeRoleOnThisMailTracking()) {
                return forward("/mailtracking/permissionDenied.jsp");
            }

            request.setAttribute("searchEntries", mailTracking.getAbleToViewEntries(readCorrespondenceTypeView(request),
                    readFilterDeletedEntriesBean(request).getValue()));
            request.setAttribute("yearBean", readYearBean(request));

            return forward("/mailtracking/management.jsp");
        }

        java.util.Set<MailTracking> mailTrackings = MailTracking.getMailTrackingsWhereUserHasSomeRole(currentUser);

        if (mailTrackings.size() == 1 && mailTrackings.iterator().next().hasCurrentUserOnlyViewOrEditionOperations()) {
            request.setAttribute("mailTracking", mailTrackings.iterator().next());
            return prepare(mapping, form, request, response);
        }

        request.setAttribute("mailTrackings", mailTrackings);

        return forward("/mailtracking/chooseMailTracking.jsp");
    }

    private FilterDeletedEntriesBean readFilterDeletedEntriesBean(HttpServletRequest request) {
        FilterDeletedEntriesBean bean = (FilterDeletedEntriesBean) request.getAttribute("filterDeletedEntriesBean");

        if (bean == null) {
            bean = this.getRenderedObject("filter.deleted.entries.bean");
        }

        if (bean == null) {
            if (!Strings.isNullOrEmpty(request.getParameter("filterDeletedEntries"))) {
                Boolean value = Boolean.parseBoolean(request.getParameter("filterDeletedEntries"));
                bean = new FilterDeletedEntriesBean(value);
            }
        }

        if (bean == null) {
            bean = new FilterDeletedEntriesBean();
        }

        request.setAttribute("filterDeletedEntriesBean", bean);

        return bean;
    }

    private YearBean readYearBean(HttpServletRequest request) {
        YearBean bean = (YearBean) request.getAttribute("yearBean");

        if (bean == null) {
            bean = this.getRenderedObject("year.bean");
        }

        if (bean == null) {
            if (!Strings.isNullOrEmpty(request.getParameter("yearId"))) {
                Year chosenYear = this.getDomainObject(request, "yearId");
                bean = new YearBean(chosenYear.getMailTracking(), chosenYear);
            }
        }

        if (bean == null) {
            bean = new YearBean(readMailTracking(request));
            bean.setChosenYear(readMailTracking(request).getCurrentYear());
        }

        return bean;
    }

    // private java.util.List<CorrespondenceEntry> getEntries(HttpServletRequest
    // request) {
    // SearchParametersBean searchBean = getSearchParametersBean(request);
    //
    // java.util.List<CorrespondenceEntry> searchedEntries;
    // if (searchBean.isSimpleSearchActive()) {
    // searchedEntries =
    // readMailTracking(request).simpleSearch(readCorrespondenceTypeView(request),
    // searchBean.getAllStringFieldsFilter());
    // } else {
    // searchedEntries = CorrespondenceEntry.getActiveEntries();
    // }
    //
    // request.setAttribute("searchEntries", searchedEntries);
    //
    // return searchedEntries;
    // }

    protected CorrespondenceEntryBean readCorrespondenceEntryBean(HttpServletRequest request) {
        CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("correspondenceEntryBean");

        if (entryBean == null) {
            entryBean = this.getRenderedObject("correspondence.entry.bean");
        }

        if (entryBean == null) {
            entryBean = new CorrespondenceEntryBean(readMailTracking(request));
        }

        request.setAttribute("correspondenceEntryBean", entryBean);
        return entryBean;
    }

    private SearchParametersBean getSearchParametersBean(HttpServletRequest request) {
        SearchParametersBean searchBean = (SearchParametersBean) request.getAttribute("searchParametersBean");

        if (searchBean == null) {
            searchBean = this.getRenderedObject("search.parameters.simple.bean");
        }

        if (searchBean == null) {
            searchBean = this.getRenderedObject("search.parameters.extended.bean");
        }

        if (searchBean == null) {
            searchBean = new SearchParametersBean();
        }

        request.setAttribute("searchParametersBean", searchBean);

        return searchBean;
    }

    public ActionForward addNewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTracking mailTracking = readMailTracking(request);

        if (!mailTracking.isCurrentUserAbleToCreateEntries()) {
            throw new PermissionDeniedException();
        }

        if (!preValidate(readCorrespondenceEntryBean(request), request, readCorrespondenceTypeView(request))) {
            RenderUtils.invalidateViewState("associate.document.bean");
            setAssociateDocumentBean(request, null);
            return prepareCreateNewEntry(mapping, form, request, response);
        }

        Document document = null;
        try {
            AssociateDocumentBean documentBean = getAssociateDocumentBean(request);

            document =
                    createDocument(request, documentBean.getStream(), documentBean.getFilesize(), documentBean.getDescription(),
                            documentBean.getFilename(), DocumentType.MAIN_DOCUMENT);
        } catch (DocumentUploadException e) {
            if (!DOCUMENT_NOT_SPECIFIED_MESSAGE.equals(e.getMessage())) {
                addMessage(request, e.getMessage());

                RenderUtils.invalidateViewState("associate.document.bean");
                setAssociateDocumentBean(request, null);

                return prepareCreateNewEntry(mapping, form, request, response);
            }
        }

        CorrespondenceEntry entry =
                mailTracking.createNewEntry(readCorrespondenceEntryBean(request), readCorrespondenceTypeView(request), document);
        addMessage(request, "message.mail.tracking.add.entry.successfully");
        request.setAttribute("entryId", entry.getExternalId());

        return viewEntry(mapping, form, request, response);
    }

    public ActionForward addNewEntryInvalid(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        readCorrespondenceEntryBean(request);

        RenderUtils.invalidateViewState("associate.document.bean");
        return prepareCreateNewEntry(mapping, form, request, response);
    }

    private static final Integer MAX_SENDER_SIZE = 50;
    private static final Integer MAX_RECIPIENT_SIZE = 50;

    protected boolean preValidate(CorrespondenceEntryBean correspondenceEntryBean, HttpServletRequest request,
            CorrespondenceType correspondenceType) {
        if (Strings.isNullOrEmpty(correspondenceEntryBean.getSender())) {
            addMessage(request, "error.mail.tracking.sender.is.required");
            return false;
        }

        if (correspondenceEntryBean.getSender().length() > MAX_SENDER_SIZE) {
            addMessage(request, "error.mail.tracking.sender.length.must.be.less.than",
                    new String[] { MAX_SENDER_SIZE.toString() });
        }

        if (Strings.isNullOrEmpty(correspondenceEntryBean.getRecipient())) {
            addMessage(request, "error.mail.tracking.recipient.is.required");
            return false;
        }

        if (correspondenceEntryBean.getRecipient().length() > MAX_RECIPIENT_SIZE) {
            addMessage(request, "error.mail.tracking.recipient.length.must.be.less.than",
                    new String[] { MAX_RECIPIENT_SIZE.toString() });
            return false;
        }

        final String reference = correspondenceEntryBean.getReference();
        final CorrespondenceEntry entry = correspondenceEntryBean.getEntry();

        if (CollectionUtils.select(correspondenceEntryBean.getMailTracking().getActiveEntries(correspondenceType),
                new Predicate() {

                    @Override
                    public boolean evaluate(Object arg0) {
                        return ((CorrespondenceEntry) arg0).getReference().equals(reference)
                                && ((CorrespondenceEntry) arg0) != entry; // if they are the same
                    }
                }).size() >= 1) {
            addMessage(request, "error.mail.tracking.reference.duplicated");
            return false;
        }

        return true;
    }

    public final ActionForward prepareEditEntry(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

        CorrespondenceEntryBean bean = entry.createBean();

        request.setAttribute("correspondenceEntryBean", bean);

        setAssociateDocumentBean(request, entry);

        return forward("/mailtracking/editCorrespondenceEntry.jsp");
    }

    public final ActionForward editEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTracking mailTracking = readMailTracking(request);

        if (!preValidate(readCorrespondenceEntryBean(request), request, readCorrespondenceTypeView(request))) {
            return prepareEditEntry(mapping, form, request, response);
        }

        if (!readCorrespondenceEntryBean(request).getEntry().isUserAbleToEdit()) {
            throw new PermissionDeniedException();
        }

        CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request);

        mailTracking.editEntry(bean);
        return prepare(mapping, form, request, response);
    }

    public final ActionForward prepareDeleteEntry(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

        CorrespondenceEntryBean bean = entry.createBean();
        request.setAttribute("correspondenceEntryBean", bean);

        return forward("/mailtracking/deleteCorrespondenceEntry.jsp");
    }

    public final ActionForward deleteEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request);
        bean.getEntry().delete(bean.getDeletionReason());

        if (!readCorrespondenceEntryBean(request).getEntry().isUserAbleToDelete()) {
            throw new PermissionDeniedException();
        }

        return prepare(mapping, form, request, response);
    }

    public final ActionForward associateDocument(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        AssociateDocumentBean bean = getAssociateDocumentBean(request);
        request.setAttribute("entryId", bean.getEntry().getExternalId());

        try {
            Document document =
                    createDocument(request, bean.getStream(), bean.getFilesize(), bean.getDescription(), bean.getFilename(),
                            bean.getType());
            bean.getEntry().associateDocument(document);
        } catch (DocumentUploadException e) {
            addMessage(request, e.getMessage());
        }

        RenderUtils.invalidateViewState("associate.document.bean");
        setAssociateDocumentBean(request, bean.getEntry());

        return prepareEditEntry(mapping, form, request, response);
    }

    public ActionForward viewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);
        entry.logView();

        request.setAttribute("correspondenceEntryBean", entry.createBean());

        return forward("/mailtracking/viewCorrespondenceEntry.jsp");
    }

    private static final String DOCUMENT_NOT_SPECIFIED_MESSAGE = "error.correspondence.entry.document.not.specified";
    private static final String DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE =
            "error.correspondence.entry.document.description.mandatory";
    private static final String MAX_FILE_EXCEEDED_MESSAGE = "error.correspondence.entry.document.file.size.exceeded";

    private Document createDocument(HttpServletRequest request, InputStream stream, Long fileSize, String description,
            String fileName, DocumentType type) throws IOException, DocumentUploadException {

        if (stream == null || fileSize == 0) {
            throw new DocumentUploadException(DOCUMENT_NOT_SPECIFIED_MESSAGE);
        }

        if (stream != null && Strings.isNullOrEmpty(description)) {
            throw new DocumentUploadException(DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE);
        }

        if (fileSize > Document.MAX_DOCUMENT_FILE_SIZE) {
            throw new DocumentUploadException(MAX_FILE_EXCEEDED_MESSAGE);
        }

        byte[] content = consumeStream(fileSize, stream);
        return Document.saveDocument(description, fileName, content, description, type);
    }

    public AssociateDocumentBean getAssociateDocumentBean(final HttpServletRequest request) {
        AssociateDocumentBean bean = (AssociateDocumentBean) request.getAttribute("associateDocumentBean");

        if (bean == null) {
            bean = this.getRenderedObject("associate.document.bean");
        }

        return bean;
    }

    public AssociateDocumentBean setAssociateDocumentBean(final HttpServletRequest request, CorrespondenceEntry entry) {
        AssociateDocumentBean bean = new AssociateDocumentBean(entry);
        request.setAttribute("associateDocumentBean", bean);

        return bean;
    }

    protected CorrespondenceEntry getCorrespondenceEntryWithExternalId(final HttpServletRequest request) {
        String entryId = this.getAttribute(request, "entryId");
        return FenixFramework.getDomainObject(entryId);
    }

    private static final java.util.Map<String, Object> SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP =
            new java.util.HashMap<String, Object>();
    static {
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("0", CorrespondenceEntry.SORT_BY_REFERENCE_COMPARATOR);
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("1", new CorrespondenceEntryFieldComparator("whenSent"));
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("2", new CorrespondenceEntryFieldComparator("recipient"));
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("3", new CorrespondenceEntryFieldComparator("subject"));
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("4", new CorrespondenceEntryFieldComparator("sender"));
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("asc", 1);
        SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("desc", -1);
    }

    private static final java.util.Map<String, Object> RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP =
            new java.util.HashMap<String, Object>();
    static {
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("0", CorrespondenceEntry.SORT_BY_REFERENCE_COMPARATOR);
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("1", new CorrespondenceEntryFieldComparator("whenReceived"));
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("2", new CorrespondenceEntryFieldComparator("sender"));
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("3", new CorrespondenceEntryFieldComparator("senderLetterNumber"));
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("4", new CorrespondenceEntryFieldComparator("subject"));
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("5", new CorrespondenceEntryFieldComparator("recipient"));
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("asc", 1);
        RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("desc", -1);
    }

    public ActionForward ajaxFilterCorrespondence(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String sEcho = request.getParameter("sEcho");
        Integer iSortingCols = Integer.valueOf(request.getParameter("iSortingCols"));
        String sSearch = request.getParameter("sSearch");
        Integer iDisplayStart = Integer.valueOf(request.getParameter("iDisplayStart"));
        Integer iDisplayLength = Integer.valueOf(request.getParameter("iDisplayLength"));

        final CorrespondenceType correspondenceType = readCorrespondenceTypeView(request);
        Comparator[] propertiesToCompare = getPropertiesToCompare(request, iSortingCols, correspondenceType);
        Integer[] orderToUse = getOrdering(request, iSortingCols, correspondenceType);

        if (propertiesToCompare.length == 0) {
            if (CorrespondenceType.SENT.equals(correspondenceType)) {
                propertiesToCompare = new Comparator[] { new BeanComparator("whenSent") };
                orderToUse = new Integer[] { -1 };
            } else {
                propertiesToCompare = new Comparator[] { new BeanComparator("whenReceived") };
                orderToUse = new Integer[] { -1 };
            }
        }

        java.util.List<CorrespondenceEntry> entries = null;
        YearBean yearBean = readYearBean(request);
        FilterDeletedEntriesBean filterDeletedBean = readFilterDeletedEntriesBean(request);

        final MailTracking mailTracking = readMailTracking(request);
        if (Strings.isNullOrEmpty(sSearch)) {
            if (yearBean.getChosenYear() != null) {
                entries = yearBean.getChosenYear().getAbleToViewEntries(correspondenceType, filterDeletedBean.getValue());
            } else {
                entries = mailTracking.getAbleToViewEntries(correspondenceType, filterDeletedBean.getValue());
            }
        } else {
            if (yearBean.getChosenYear() != null) {
                entries = yearBean.getChosenYear().simpleSearch(correspondenceType, sSearch, filterDeletedBean.getValue());
            } else {
                entries = mailTracking.simpleSearch(correspondenceType, sSearch, filterDeletedBean.getValue());
            }
        }

        Integer numberOfRecordsMatched = entries.size();
        java.util.List<CorrespondenceEntry> limitedEntries =
                limitAndOrderSearchedEntries(entries, propertiesToCompare, orderToUse, iDisplayStart, iDisplayLength);

        String jsonResponseString = null;
        if (CorrespondenceType.SENT.equals(correspondenceType)) {
            jsonResponseString =
                    serializeAjaxFilterResponseForSentMail(sEcho, mailTracking.countActiveEntries(correspondenceType),
                            numberOfRecordsMatched, limitedEntries, request);
        } else if (CorrespondenceType.RECEIVED.equals(correspondenceType)) {
            jsonResponseString =
                    serializeAjaxFilterResponseForReceivedMail(sEcho, mailTracking.countActiveEntries(correspondenceType),
                            numberOfRecordsMatched, limitedEntries, request);
        }

        final byte[] jsonResponsePayload = jsonResponseString.getBytes("iso-8859-15");

        response.setContentType("application/json; charset=iso-8859-15");
        response.setContentLength(jsonResponsePayload.length);
        response.getOutputStream().write(jsonResponsePayload);
        response.getOutputStream().flush();
        response.getOutputStream().close();

        return null;
    }

    public ActionForward prepareCreateNewEntry(ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        readCorrespondenceEntryBean(request);
        setAssociateDocumentBean(request, null);

        return forward("/mailtracking/createNewEntry.jsp");
    }

    public ActionForward deleteDocument(ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        readDocument(request).getCorrespondenceEntry().deleteDocument(readDocument(request));

        return prepareEditEntry(mapping, form, request, response);
    }

    private Document readDocument(final HttpServletRequest request) {
        return this.getDomainObject(request, "fileId");
    }

    private String serializeAjaxFilterResponseForSentMail(String sEcho, Integer iTotalRecords, Integer iTotalDisplayRecords,
            java.util.List<CorrespondenceEntry> limitedEntries, HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"sEcho\": ").append(sEcho).append(", \n");
        stringBuilder.append("\"iTotalRecords\": ").append(iTotalRecords).append(", \n");
        stringBuilder.append("\"iTotalDisplayRecords\": ").append(iTotalDisplayRecords).append(", \n");
        stringBuilder.append("\"aaData\": ").append("[ \n");

        for (CorrespondenceEntry entry : limitedEntries) {
            stringBuilder.append("[ \"").append(entry.getReference()).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getWhenSent().toString("dd/MM/yyyy"))).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getRecipient())).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getSubject())).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getSender())).append("\", ");

            stringBuilder
                    .append("\"")
                    .append(entry.isUserAbleToView(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryView(request,
                            entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToEdit(Authenticate.getUser()) && entry.isActive() ? generateLinkForCorrespondenceEntryEdition(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToDelete(Authenticate.getUser()) && entry.isActive() ? generateLinkForCorrespondenceEntryRemoval(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder.append(
                    entry.isUserAbleToViewMainDocument(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryMainDocument(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToCopyEntry(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryCopy(request,
                            entry) : "permission_not_granted").append("\",");

            stringBuilder.append("\"").append(entry.isActive()).append("\" ], ");

        }

        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        stringBuilder.append(" ]\n }");

        return stringBuilder.toString();
    }

    private Object escapeQuotes(String value) {
        return value.replaceAll("\\\"", "\\\\\"");
    }

    private String generateLinkForCorrespondenceEntryView(HttpServletRequest request, CorrespondenceEntry entry) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format(
                                "/mailtracking.do?entryId=%s&amp;method=viewEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s",
                                entry.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
                                        .getExternalId());
        realLink +=
                String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME,
                        GenericChecksumRewriter.calculateChecksum(realLink, request.getSession()));

        return realLink;
    }

    private String serializeAjaxFilterResponseForReceivedMail(String sEcho, Integer iTotalRecords, Integer iTotalDisplayRecords,
            List<CorrespondenceEntry> limitedEntries, HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"sEcho\": ").append(sEcho).append(", \n");
        stringBuilder.append("\"iTotalRecords\": ").append(iTotalRecords).append(", \n");
        stringBuilder.append("\"iTotalDisplayRecords\": ").append(iTotalDisplayRecords).append(", \n");
        stringBuilder.append("\"aaData\": ").append("[ \n");

        for (CorrespondenceEntry entry : limitedEntries) {
            stringBuilder.append("[ \"").append(entry.getReference()).append("\", ");
            stringBuilder.append("\"").append(entry.getWhenReceived().toString("dd/MM/yyyy")).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getSender())).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getSenderLetterNumber())).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getSubject())).append("\", ");
            stringBuilder.append("\"").append(escapeQuotes(entry.getRecipient())).append("\", ");

            stringBuilder
                    .append("\"")
                    .append(entry.isUserAbleToView(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryView(request,
                            entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToEdit(Authenticate.getUser()) && entry.isActive() ? generateLinkForCorrespondenceEntryEdition(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToDelete(Authenticate.getUser()) && entry.isActive() ? generateLinkForCorrespondenceEntryRemoval(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder.append(
                    entry.isUserAbleToViewMainDocument(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryMainDocument(
                            request, entry) : "permission_not_granted").append(",");

            stringBuilder
                    .append(entry.isUserAbleToCopyEntry(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryCopy(request,
                            entry) : "permission_not_granted").append("\",");

            stringBuilder.append("\"").append(entry.isActive()).append("\" ], ");

        }

        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        stringBuilder.append(" ]\n }");

        return stringBuilder.toString();

    }

    private String generateLinkForCorrespondenceEntryCopy(HttpServletRequest request, CorrespondenceEntry entry) {
        return String.format("javascript: loadFastCopyEntryPage(%s);", entry.getExternalId());
    }

    private String generateLinkForCorrespondenceEntryMainDocument(HttpServletRequest request, CorrespondenceEntry entry) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format("/mailtracking.do?entryId=%s&amp;method=downloadFile&amp;fileId=%s",
                                entry.getExternalId(), entry.getMainDocument().getExternalId());
        realLink +=
                String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME,
                        GenericChecksumRewriter.calculateChecksum(realLink, request.getSession()));

        return realLink;
    }

    private String generateLinkForCorrespondenceEntryRemoval(HttpServletRequest request, CorrespondenceEntry entry) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format(
                                "/mailtracking.do?entryId=%s&amp;method=prepareDeleteEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s",
                                entry.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
                                        .getExternalId());
        realLink +=
                String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME,
                        GenericChecksumRewriter.calculateChecksum(realLink, request.getSession()));

        return realLink;
    }

    private String generateLinkForCorrespondenceEntryEdition(HttpServletRequest request, CorrespondenceEntry entry) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format(
                                "/mailtracking.do?entryId=%s&amp;method=prepareEditEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s",
                                entry.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
                                        .getExternalId());
        realLink +=
                String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME,
                        GenericChecksumRewriter.calculateChecksum(realLink, request.getSession()));

        return realLink;
    }

    private java.util.List<CorrespondenceEntry> limitAndOrderSearchedEntries(java.util.List searchedEntries,
            final Comparator[] propertiesToCompare, final Integer[] orderToUse, Integer iDisplayStart, Integer iDisplayLength) {

        Collections.sort(searchedEntries, new Comparator<CorrespondenceEntry>() {

            @Override
            public int compare(CorrespondenceEntry oLeft, CorrespondenceEntry oRight) {
                for (int i = 0; i < propertiesToCompare.length; i++) {
                    try {
                        Comparator comparator = propertiesToCompare[i];

                        if (comparator.compare(oLeft, oRight) != 0) {
                            return orderToUse[i] * comparator.compare(oLeft, oRight);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                return 0;
            }
        });

        return searchedEntries.subList(iDisplayStart, Math.min(iDisplayStart + iDisplayLength, searchedEntries.size()));
    }

    private Integer[] getOrdering(HttpServletRequest request, Integer iSortingCols, CorrespondenceType type) {
        java.util.List<Integer> order = new java.util.ArrayList<Integer>();

        java.util.Map<String, Object> mapToUse =
                CorrespondenceType.SENT.equals(type) ? SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP : RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP;

        for (int i = 0; i < iSortingCols; i++) {
            String iSortingColDir = request.getParameter("iSortDir_" + i);
            order.add((Integer) mapToUse.get(iSortingColDir));
        }

        return order.toArray(new Integer[] {});
    }

    private Comparator[] getPropertiesToCompare(HttpServletRequest request, Integer iSortingCols, CorrespondenceType type) {
        java.util.List<Comparator> properties = new java.util.ArrayList<Comparator>();

        java.util.Map<String, Object> mapToUse =
                CorrespondenceType.SENT.equals(type) ? SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP : RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP;

        for (int i = 0; i < iSortingCols; i++) {
            String iSortingColIdx = request.getParameter("iSortCol_" + i);
            properties.add((Comparator) mapToUse.get(iSortingColIdx));
        }

        return properties.toArray(new Comparator[] {});
    }

    public ActionForward downloadFile(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        final CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);
        if (!entry.isUserAbleToViewMainDocument(Authenticate.getUser())) {
            throw new PermissionDeniedException();
        }

        String documentId = request.getParameter("fileId");
        final Document document = FenixFramework.getDomainObject(documentId);

        entry.logDownload(document);

        return download(response, document.getFilename(), document.getContent(), document.getContentType());
    }

    public ActionForward prepareSetReferenceCounters(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        YearBean yearBean = new YearBean(readMailTracking(request));
        request.setAttribute("yearBean", yearBean);

        return forward("/mailtracking/setReferenceCounters.jsp");
    }

    public ActionForward setReferenceCounters(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        YearBean yearBean = readYearBean(request);

        MailTrackingActionOperations.setReferenceCounters(yearBean);
        RenderUtils.invalidateViewState();
        request.setAttribute("yearBean", null);

        return prepare(mapping, form, request, response);
    }

    public ActionForward chooseYearForReferenceCountSet(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        YearBean yearBean = readYearBean(request);

        RenderUtils.invalidateViewState();

        request.setAttribute("yearBean", new YearBean(readMailTracking(request), yearBean.getChosenYear()));

        return forward("/mailtracking/setReferenceCounters.jsp");
    }

    public static class FilterDeletedEntriesBean implements java.io.Serializable {
        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        private Boolean value;

        public FilterDeletedEntriesBean() {
            value = Boolean.TRUE;
        }

        public FilterDeletedEntriesBean(Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(final Boolean value) {
            this.value = value;
        }
    }

    public static class SearchParametersBean implements java.io.Serializable {
        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        private String sender;
        private String recipient;
        private String subject;
        private DateTime whenReceivedBegin;
        private DateTime whenReceivedEnd;

        private String allStringFieldsFilter;

        public SearchParametersBean() {

        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public DateTime getWhenReceivedBegin() {
            return whenReceivedBegin;
        }

        public void setWhenReceivedBegin(DateTime whenReceivedBegin) {
            this.whenReceivedBegin = whenReceivedBegin;
        }

        public DateTime getWhenReceivedEnd() {
            return whenReceivedEnd;
        }

        public void setWhenReceivedEnd(DateTime whenReceivedEnd) {
            this.whenReceivedEnd = whenReceivedEnd;
        }

        public String getAllStringFieldsFilter() {
            return allStringFieldsFilter;
        }

        public void setAllStringFieldsFilter(String allStringFieldsFilter) {
            this.allStringFieldsFilter = allStringFieldsFilter;
        }

        public Boolean isSimpleSearchActive() {
            return !Strings.isNullOrEmpty(this.getAllStringFieldsFilter());
        }

        public Boolean isExtendedSearchActive() {
            return !Strings.isNullOrEmpty(this.getSender()) || !Strings.isNullOrEmpty(this.getRecipient())
                    || !Strings.isNullOrEmpty(this.getSubject()) || this.getWhenReceivedBegin() != null
                    || this.getWhenReceivedEnd() != null;
        }
    }

    public static class AssociateDocumentBean implements java.io.Serializable {
        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        private String filename;
        private String mimetype;
        private Long filesize;
        private InputStream stream;
        private String description;
        private DocumentType type;

        private CorrespondenceEntry entry;

        public AssociateDocumentBean() {

        }

        public AssociateDocumentBean(CorrespondenceEntry entry) {
            this.setEntry(entry);

            if (entry != null) {
                this.setType(entry.hasMainDocument() ? DocumentType.OTHER_DOCUMENT : DocumentType.MAIN_DOCUMENT);
            }
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getMimetype() {
            return mimetype;
        }

        public void setMimetype(String mimetype) {
            this.mimetype = mimetype;
        }

        public Long getFilesize() {
            return filesize;
        }

        public void setFilesize(Long filesize) {
            this.filesize = filesize;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        public CorrespondenceEntry getEntry() {
            return entry;
        }

        public void setEntry(CorrespondenceEntry entry) {
            this.entry = entry;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public DocumentType getType() {
            return type;
        }

        public void setType(DocumentType type) {
            this.type = type;
        }

    }

    private static class DocumentUploadException extends java.lang.Exception {
        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        public DocumentUploadException(String message) {
            super(message);
        }
    }

    private byte[] consumeStream(Long fileSize, InputStream stream) throws IOException {
        byte[] content = new byte[fileSize.intValue()];
        stream.read(content);

        return content;
    }

//    @Override
//    public Context createContext(String contextPathString, HttpServletRequest request) {
//        LayoutContext context = (LayoutContext) super.createContext(contextPathString, request);
//        context.addHead("/mailtracking/layoutHead.jsp");
//        return context;
//    }

    public static class YearProvider implements DataProvider {

        @Override
        public Converter getConverter() {
            return new DomainObjectKeyConverter();
        }

        @Override
        public Object provide(Object source, Object currentValue) {
            YearBean bean = (YearBean) source;
            return bean.getMailTracking().getYearsSet();
        }
    }

}
