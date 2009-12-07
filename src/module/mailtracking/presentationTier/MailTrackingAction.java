package module.mailtracking.presentationTier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.Document;
import module.mailtracking.domain.DocumentType;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.domain.exception.PermissionDeniedException;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.UserGroup;
import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/mailtracking")
public class MailTrackingAction extends ContextBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	readCorrespondenceTypeView(request);
	readMailTracking(request);

	return super.execute(mapping, form, request, response);
    }

    private MailTracking readMailTracking(HttpServletRequest request) {
	MailTracking mailTracking = (MailTracking) request.getAttribute("mailTracking");

	if (mailTracking == null) {
	    String mailTrackingId = request.getParameter("mailTrackingId");
	    mailTracking = MailTracking.fromExternalId(mailTrackingId);
	}

	request.setAttribute("mailTracking", mailTracking);
	return mailTracking;
    }

    private CorrespondenceType readCorrespondenceTypeView(HttpServletRequest request) {
	String typeValue = request.getParameter("correspondenceType");
	CorrespondenceType type = StringUtils.isEmpty(typeValue) ? null : CorrespondenceType.valueOf(typeValue);

	if (type == null)
	    type = CorrespondenceType.SENT;

	request.setAttribute("correspondenceType", type.name());
	return type;
    }

    @CreateNodeAction(bundle = "MAIL_TRACKING_RESOURCES", key = "mail.tracking.interface", groupKey = "label.module.mailtracking")
    public final ActionForward prepareCreateNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");

	final Node mainNode = ActionNode.createActionNode(virtualHost, node, "/mailtracking", "prepare",
		"resources.MailTrackingResources", "link.sideBar.mailtracking.manageMailing", UserGroup.getInstance());

	return forwardToMuneConfiguration(request, virtualHost, node);
    }

    public final ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	User currentUser = UserView.getCurrentUser();
	MailTracking mailTracking = readMailTracking(request);

	if (mailTracking != null) {
	    if (!mailTracking.isCurrentUserWithSomeRoleOnThisMailTracking())
		return forward(request, "/mailtracking/permissionDenied.jsp");

	    getEntries(request);

	    request.setAttribute("yearBean", readYearBean(request));

	    return forward(request, "/mailtracking/management.jsp");
	}

	java.util.List<MailTracking> mailTrackings = MailTracking.getMailTrackingsWhereUserHasSomeRole(currentUser);

	if (mailTrackings.size() == 1 && mailTrackings.get(0).hasCurrentUserOnlyViewOrEditionOperations()) {
	    request.setAttribute("mailTracking", mailTrackings.get(0));
	    return prepare(mapping, form, request, response);
	}

	request.setAttribute("mailTrackings", mailTrackings);

	return forward(request, "/mailtracking/chooseMailTracking.jsp");
    }

    private YearBean readYearBean(HttpServletRequest request) {
	YearBean bean = (YearBean) request.getAttribute("yearBean");

	if (bean == null) {
	    bean = this.getRenderedObject("year.bean");
	}

	if (bean == null) {
	    if (!StringUtils.isEmpty(request.getParameter("yearId"))) {
		Year chosenYear = this.getDomainObject(request, "yearId");
		bean = new YearBean(chosenYear.getMailTracking(), chosenYear);
	    }
	}

	if (bean == null) {
	    bean = new YearBean(readMailTracking(request));
	}

	return bean;
    }

    private java.util.List<CorrespondenceEntry> getEntries(HttpServletRequest request) {
	SearchParametersBean searchBean = getSearchParametersBean(request);

	java.util.List<CorrespondenceEntry> searchedEntries;
	if (searchBean.isExtendedSearchActive()) {
	    searchedEntries = readMailTracking(request).find(readCorrespondenceTypeView(request), searchBean.getSender(),
		    searchBean.getRecipient(), searchBean.getSubject(), searchBean.getWhenReceivedBegin(),
		    searchBean.getWhenReceivedEnd());
	} else if (searchBean.isSimpleSearchActive()) {
	    searchedEntries = readMailTracking(request).simpleSearch(readCorrespondenceTypeView(request),
		    searchBean.getAllStringFieldsFilter());
	} else {
	    searchedEntries = CorrespondenceEntry.getActiveEntries();
	}

	request.setAttribute("searchEntries", searchedEntries);

	return searchedEntries;
    }

    private CorrespondenceEntryBean readCorrespondenceEntryBean(HttpServletRequest request) {
	CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("correspondenceEntryBean");

	if (entryBean == null)
	    entryBean = this.getRenderedObject("correspondence.entry.bean");

	if (entryBean == null)
	    entryBean = new CorrespondenceEntryBean(readMailTracking(request));

	request.setAttribute("correspondenceEntryBean", entryBean);
	return entryBean;
    }

    private SearchParametersBean getSearchParametersBean(HttpServletRequest request) {
	SearchParametersBean searchBean = (SearchParametersBean) request.getAttribute("searchParametersBean");

	if (searchBean == null)
	    searchBean = this.getRenderedObject("search.parameters.simple.bean");

	if (searchBean == null)
	    searchBean = this.getRenderedObject("search.parameters.extended.bean");

	if (searchBean == null)
	    searchBean = new SearchParametersBean();

	request.setAttribute("searchParametersBean", searchBean);

	return searchBean;
    }

    public final ActionForward addNewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTracking mailTracking = readMailTracking(request);

	if (!mailTracking.isCurrentUserAbleToCreateEntries())
	    throw new PermissionDeniedException();

	if (!preValidate(readCorrespondenceEntryBean(request), request)) {
	    RenderUtils.invalidateViewState("associate.document.bean");
	    setAssociateDocumentBean(request, null);
	    return prepareCreateNewEntry(mapping, form, request, response);
	}

	Document document = null;
	try {
	    AssociateDocumentBean documentBean = getAssociateDocumentBean(request);

	    document = createDocument(request, documentBean.getStream(), documentBean.getFilesize(), documentBean
		    .getDescription(), documentBean.getFilename(), DocumentType.MAIN_DOCUMENT);
	} catch (DocumentUploadException e) {
	    if (!DOCUMENT_NOT_SPECIFIED_MESSAGE.equals(e.getMessage())) {
		addMessage(request, e.getMessage());

		RenderUtils.invalidateViewState("associate.document.bean");
		setAssociateDocumentBean(request, null);

		return prepareCreateNewEntry(mapping, form, request, response);
	    }
	}

	mailTracking.createNewEntry(readCorrespondenceEntryBean(request), readCorrespondenceTypeView(request), document);
	request.setAttribute("correspondenceEntryBean", new CorrespondenceEntryBean(readMailTracking(request)));

	return prepare(mapping, form, request, response);
    }

    public final ActionForward addNewEntryInvalid(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	readCorrespondenceEntryBean(request);

	RenderUtils.invalidateViewState("associate.document.bean");
	return prepareCreateNewEntry(mapping, form, request, response);
    }

    private static final Integer MAX_SENDER_SIZE = 50;
    private static final Integer MAX_RECIPIENT_SIZE = 50;

    private boolean preValidate(CorrespondenceEntryBean correspondenceEntryBean, HttpServletRequest request) {
	if (StringUtils.isEmpty(correspondenceEntryBean.getSender())) {
	    addMessage(request, "error.mail.tracking.sender.is.required");
	    return false;
	}

	if (correspondenceEntryBean.getSender().length() > MAX_SENDER_SIZE) {
	    addMessage(request, "error.mail.tracking.sender.length.must.be.less.than",
		    new String[] { MAX_SENDER_SIZE.toString() });
	}

	if (StringUtils.isEmpty(correspondenceEntryBean.getRecipient())) {
	    addMessage(request, "error.mail.tracking.recipient.is.required");
	    return false;
	}

	if (correspondenceEntryBean.getRecipient().length() > MAX_RECIPIENT_SIZE) {
	    addMessage(request, "error.mail.tracking.recipient.length.must.be.less.than", new String[] { MAX_RECIPIENT_SIZE
		    .toString() });
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

	return forward(request, "/mailtracking/editCorrespondenceEntry.jsp");
    }

    public final ActionForward editEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTracking mailTracking = readMailTracking(request);

	if (!preValidate(readCorrespondenceEntryBean(request), request)) {
	    return prepareEditEntry(mapping, form, request, response);
	}

	if (!readCorrespondenceEntryBean(request).getEntry().isUserAbleToEdit())
	    throw new PermissionDeniedException();

	CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request);

	mailTracking.editEntry(bean);
	return prepare(mapping, form, request, response);
    }

    public final ActionForward prepareDeleteEntry(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {
	CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

	CorrespondenceEntryBean bean = entry.createBean();
	request.setAttribute("correspondenceEntryBean", bean);

	return forward(request, "/mailtracking/deleteCorrespondenceEntry.jsp");
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
	    Document document = createDocument(request, bean.getStream(), bean.getFilesize(), bean.getDescription(), bean
		    .getFilename(), bean.getType());
	    bean.getEntry().associateDocument(document);
	} catch (DocumentUploadException e) {
	    addMessage(request, e.getMessage());
	}

	RenderUtils.invalidateViewState("associate.document.bean");
	setAssociateDocumentBean(request, bean.getEntry());

	return prepareEditEntry(mapping, form, request, response);
    }

    public final ActionForward viewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

	request.setAttribute("correspondenceEntryBean", entry.createBean());

	return forward(request, "/mailtracking/viewCorrespondenceEntry.jsp");
    }

    private static final String DOCUMENT_NOT_SPECIFIED_MESSAGE = "error.correspondence.entry.document.not.specified";
    private static final String DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE = "error.correspondence.entry.document.description.mandatory";
    private static final String MAX_FILE_EXCEEDED_MESSAGE = "error.correspondence.entry.document.file.size.exceeded";

    private Document createDocument(HttpServletRequest request, InputStream stream, Long fileSize, String description,
	    String fileName, DocumentType type) throws IOException, DocumentUploadException {

	if (stream == null || fileSize == 0)
	    throw new DocumentUploadException(DOCUMENT_NOT_SPECIFIED_MESSAGE);

	if (stream != null && StringUtils.isEmpty(description))
	    throw new DocumentUploadException(DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE);

	if (fileSize > Document.MAX_DOCUMENT_FILE_SIZE)
	    throw new DocumentUploadException(MAX_FILE_EXCEEDED_MESSAGE);

	byte[] content = consumeStream(fileSize, stream);
	return Document.saveDocument(description, fileName, content, description, type);
    }

    public AssociateDocumentBean getAssociateDocumentBean(final HttpServletRequest request) {
	AssociateDocumentBean bean = (AssociateDocumentBean) request.getAttribute("associateDocumentBean");

	if (bean == null)
	    bean = this.getRenderedObject("associate.document.bean");

	return bean;
    }

    public AssociateDocumentBean setAssociateDocumentBean(final HttpServletRequest request, CorrespondenceEntry entry) {
	AssociateDocumentBean bean = new AssociateDocumentBean(entry);
	request.setAttribute("associateDocumentBean", bean);

	return bean;
    }

    private CorrespondenceEntry getCorrespondenceEntryWithExternalId(final HttpServletRequest request) {
	String entryId = this.getAttribute(request, "entryId");
	return CorrespondenceEntry.fromExternalId(entryId);
    }

    private static final java.util.Map<String, Object> SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP = new java.util.HashMap<String, Object>();
    static {
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("0", CorrespondenceEntry.SORT_BY_REFERENCE_COMPARATOR);
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("1", new BeanComparator("whenSent"));
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("2", new BeanComparator("recipient"));
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("3", new BeanComparator("subject"));
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("4", new BeanComparator("sender"));
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("asc", 1);
	SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("desc", -1);
    }

    private static final java.util.Map<String, Object> RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP = new java.util.HashMap<String, Object>();
    static {
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("0", CorrespondenceEntry.SORT_BY_REFERENCE_COMPARATOR);
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("1", new BeanComparator("whenReceived"));
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("2", new BeanComparator("sender"));
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("4", new BeanComparator("senderLetterNumber"));
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("5", new BeanComparator("subject"));
	RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP.put("6", new BeanComparator("recipient"));
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

	Comparator[] propertiesToCompare = getPropertiesToCompare(request, iSortingCols, readCorrespondenceTypeView(request));
	Integer[] orderToUse = getOrdering(request, iSortingCols, readCorrespondenceTypeView(request));

	if (propertiesToCompare.length == 0) {
	    if (CorrespondenceType.SENT.equals(readCorrespondenceTypeView(request))) {
		propertiesToCompare = new Comparator[] { new BeanComparator("whenSent") };
		orderToUse = new Integer[] { -1 };
	    } else {
		propertiesToCompare = new Comparator[] { new BeanComparator("whenReceived") };
		orderToUse = new Integer[] { -1 };
	    }
	}

	java.util.List<CorrespondenceEntry> entries = null;
	YearBean yearBean = readYearBean(request);

	if (StringUtils.isEmpty(sSearch)) {
	    if (yearBean.getChosenYear() != null) {
		entries = yearBean.getChosenYear().getAbleToViewActiveEntries(readCorrespondenceTypeView(request));
	    } else {
		entries = readMailTracking(request).getAbleToViewActiveEntries(readCorrespondenceTypeView(request));
	    }
	} else {
	    if (yearBean.getChosenYear() != null) {
		entries = yearBean.getChosenYear().simpleSearch(readCorrespondenceTypeView(request), sSearch);
	    } else {
		entries = readMailTracking(request).simpleSearch(readCorrespondenceTypeView(request), sSearch);
	    }
	}

	Integer numberOfRecordsMatched = entries.size();
	java.util.List<CorrespondenceEntry> limitedEntries = limitAndOrderSearchedEntries(entries, propertiesToCompare,
		orderToUse, iDisplayStart, iDisplayLength);

	String jsonResponseString = null;
	if (CorrespondenceType.SENT.equals(readCorrespondenceTypeView(request))) {
	    jsonResponseString = serializeAjaxFilterResponseForSentMail(sEcho, readMailTracking(request).getActiveEntries(
		    readCorrespondenceTypeView(request)).size(), numberOfRecordsMatched, limitedEntries, request);
	} else if (CorrespondenceType.RECEIVED.equals(readCorrespondenceTypeView(request))) {
	    jsonResponseString = serializeAjaxFilterResponseForReceivedMail(sEcho, readMailTracking(request).getActiveEntries(
		    readCorrespondenceTypeView(request)).size(), numberOfRecordsMatched, limitedEntries, request);
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

	return forward(request, "/mailtracking/createNewEntry.jsp");
    }

    public ActionForward deleteDocument(ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	readDocument(request).deleteDocument();

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

	    stringBuilder.append("\"").append(
		    entry.isUserAbleToView(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryView(request, entry)
			    : "permission_not_granted").append(",");

	    stringBuilder.append(
		    entry.isUserAbleToEdit(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryEdition(request, entry)
			    : "permission_not_granted").append(",");

	    stringBuilder.append(
		    entry.isUserAbleToDelete(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryRemoval(request,
			    entry) : "permission_not_granted").append(",");

	    stringBuilder
		    .append(
			    entry.isUserAbleToViewMainDocument(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryMainDocument(
				    request, entry)
				    : "permission_not_granted").append("\" ], ");
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
	String realLink = contextPath
		+ String.format(
			"/mailtracking.do?entryId=%s&amp;method=viewEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s", entry
				.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
				.getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

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

	    stringBuilder.append("\"").append(
		    entry.isUserAbleToView(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryView(request, entry)
			    : "permission_not_granted").append(",");

	    stringBuilder.append(
		    entry.isUserAbleToEdit(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryEdition(request, entry)
			    : "permission_not_granted").append(",");

	    stringBuilder.append(
		    entry.isUserAbleToDelete(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryRemoval(request,
			    entry) : "permission_not_granted").append(",");

	    stringBuilder
		    .append(
			    entry.isUserAbleToViewMainDocument(UserView.getCurrentUser()) ? generateLinkForCorrespondenceEntryMainDocument(
				    request, entry)
				    : "permission_not_granted").append("\" ], ");
	}

	stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

	stringBuilder.append(" ]\n }");

	return stringBuilder.toString();

    }

    private String generateLinkForCorrespondenceEntryMainDocument(HttpServletRequest request, CorrespondenceEntry entry) {
	String contextPath = request.getContextPath();
	String realLink = contextPath
		+ String.format("/mailtracking.do?entryId=%s&amp;method=downloadFile&amp;fileId=%s", entry.getExternalId(), entry
			.getMainDocument().getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

	return realLink;
    }

    private String generateLinkForCorrespondenceEntryRemoval(HttpServletRequest request, CorrespondenceEntry entry) {
	String contextPath = request.getContextPath();
	String realLink = contextPath
		+ String
			.format(
				"/mailtracking.do?entryId=%s&amp;method=prepareDeleteEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s",
				entry.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
					.getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

	return realLink;
    }

    private String generateLinkForCorrespondenceEntryEdition(HttpServletRequest request, CorrespondenceEntry entry) {
	String contextPath = request.getContextPath();
	String realLink = contextPath
		+ String
			.format(
				"/mailtracking.do?entryId=%s&amp;method=prepareEditEntry&amp;correspondenceType=%s&amp;mailTrackingId=%s",
				entry.getExternalId(), readCorrespondenceTypeView(request).name(), readMailTracking(request)
					.getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

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

	java.util.Map<String, Object> mapToUse = CorrespondenceType.SENT.equals(type) ? SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP
		: RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP;

	for (int i = 0; i < iSortingCols; i++) {
	    String iSortingColDir = request.getParameter("iSortDir_" + i);
	    order.add((Integer) mapToUse.get(iSortingColDir));
	}

	return order.toArray(new Integer[] {});
    }

    private Comparator[] getPropertiesToCompare(HttpServletRequest request, Integer iSortingCols, CorrespondenceType type) {
	java.util.List<Comparator> properties = new java.util.ArrayList<Comparator>();

	java.util.Map<String, Object> mapToUse = CorrespondenceType.SENT.equals(type) ? SENT_CORRESPONDENCE_TABLE_COLUMNS_MAP
		: RECEIVED_CORRESPONDENCE_TABLE_COLUMNS_MAP;

	for (int i = 0; i < iSortingCols; i++) {
	    String iSortingColIdx = request.getParameter("iSortCol_" + i);
	    properties.add((Comparator) mapToUse.get(iSortingColIdx));
	}

	return properties.toArray(new Comparator[] {});
    }

    public ActionForward downloadFile(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws IOException {
	if (!getCorrespondenceEntryWithExternalId(request).isUserAbleToViewMainDocument(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	String documentId = request.getParameter("fileId");
	final Document document = CorrespondenceEntry.fromExternalId(documentId);

	return download(response, document.getFilename(), document.getContent(), document.getContentType());
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
	    return !StringUtils.isEmpty(this.getAllStringFieldsFilter());
	}

	public Boolean isExtendedSearchActive() {
	    return !StringUtils.isEmpty(this.getSender()) || !StringUtils.isEmpty(this.getRecipient())
		    || !StringUtils.isEmpty(this.getSubject()) || this.getWhenReceivedBegin() != null
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

	    if (entry != null)
		this.setType(entry.hasMainDocument() ? DocumentType.OTHER_DOCUMENT : DocumentType.MAIN_DOCUMENT);
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

    @Override
    public Context createContext(String contextPathString, HttpServletRequest request) {
	LayoutContext context = (LayoutContext) super.createContext(contextPathString, request);
	context.addHead("/mailtracking/layoutHead.jsp");
	return context;
    }

}
