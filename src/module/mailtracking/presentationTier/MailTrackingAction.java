package module.mailtracking.presentationTier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.Document;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.UserGroup;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.commons.beanutils.PropertyUtils;
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
	return super.execute(mapping, form, request, response);
    }

    @CreateNodeAction(bundle = "MAIL_TRACKING_RESOURCES", key = "option.create.new.mail.tracking.page", groupKey = "label.module.contents")
    public final ActionForward prepareCreateNewPage(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");

	ActionNode.createActionNode(virtualHost, node, "/mailtracking", "prepare", "resources.MailTrackingResources",
		"link.sideBar.mailtracking.manageMailing", UserGroup.getInstance());

	return forwardToMuneConfiguration(request, virtualHost, node);
    }

    public final ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	getEntries(request);

	getCorrespondenceEntryBean(request);

	return forward(request, "/mailtracking/management.jsp");
    }

    private java.util.List<CorrespondenceEntry> getEntries(HttpServletRequest request) {
	SearchParametersBean searchBean = getSearchParametersBean(request);

	java.util.List<CorrespondenceEntry> searchedEntries;
	if (searchBean.isExtendedSearchActive()) {
	    searchedEntries = CorrespondenceEntry.find(searchBean.getSender(), searchBean.getRecipient(),
		    searchBean.getSubject(), searchBean.getWhenReceivedBegin(), searchBean.getWhenReceivedEnd());
	} else if (searchBean.isSimpleSearchActive()) {
	    searchedEntries = CorrespondenceEntry.simpleSearch(searchBean.getAllStringFieldsFilter());
	} else {
	    searchedEntries = CorrespondenceEntry.getActiveEntries();
	}

	request.setAttribute("searchEntries", searchedEntries);

	return searchedEntries;
    }

    private CorrespondenceEntryBean getCorrespondenceEntryBean(HttpServletRequest request) {
	CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("correspondenceEntryBean");

	if (entryBean == null)
	    entryBean = this.getRenderedObject("correspondence.entry.bean");

	if (entryBean == null)
	    entryBean = new CorrespondenceEntryBean();

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

    private static final Integer NUMBER_LAST_ENTRIES = 10;

    private java.util.List<CorrespondenceEntry> getLastEntries(HttpServletRequest request) {
	java.util.List<CorrespondenceEntry> lastEntries = CorrespondenceEntry
		.getLastActiveEntriesSortedByDate(NUMBER_LAST_ENTRIES);
	request.setAttribute("lastEntries", lastEntries);

	return lastEntries;
    }

    public final ActionForward addNewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	CorrespondenceEntry.createNewEntry(getCorrespondenceEntryBean(request));

	request.setAttribute("correspondenceEntryBean", new CorrespondenceEntryBean());

	return prepare(mapping, form, request, response);
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
	CorrespondenceEntryBean bean = getCorrespondenceEntryBean(request);

	bean.getEntry().edit(bean);
	return prepare(mapping, form, request, response);
    }

    public final ActionForward deleteEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

	entry.delete();

	return prepare(mapping, form, request, response);
    }

    public final ActionForward associateDocument(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	AssociateDocumentBean bean = getAssociateDocumentBean(request);
	request.setAttribute("entryId", bean.getEntry().getExternalId());

	if (bean.getStream() == null || bean.getFilesize() == 0) {
	    addMessage(request, "error.correspondence.entry.document.not.specified");
	    return prepareEditEntry(mapping, form, request, response);
	}

	if (bean.getFilesize() > Document.MAX_DOCUMENT_FILE_SIZE) {
	    addMessage(request, "error.correspondence.entry.document.file.size.exceeded");
	    return prepareEditEntry(mapping, form, request, response);
	}

	byte[] content = bean.consumeStream();
	Document document = Document.saveDocument(bean.getDescription(), bean.getFilename(), content, bean.getDescription());
	bean.getEntry().associateDocument(document);

	setAssociateDocumentBean(request, bean.getEntry());

	RenderUtils.invalidateViewState("associate.document.bean");

	return prepareEditEntry(mapping, form, request, response);
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

    private static final java.util.Map<String, Object> CORRESPONDENCE_TABLE_COLUMNS_MAP = new java.util.HashMap<String, Object>();

    static {
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("0", "sender");
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("1", "recipient");
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("2", "subject");
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("3", "whenReceived");
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("asc", 1);
	CORRESPONDENCE_TABLE_COLUMNS_MAP.put("desc", -1);
    }

    public ActionForward ajaxFilterCorrespondence(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws IOException {
	String sEcho = request.getParameter("sEcho");
	Integer iSortingCols = Integer.valueOf(request.getParameter("iSortingCols"));
	String sSearch = request.getParameter("sSearch");
	Integer iDisplayStart = Integer.valueOf(request.getParameter("iDisplayStart"));
	Integer iDisplayLength = Integer.valueOf(request.getParameter("iDisplayLength"));

	String[] propertiesToCompare = getPropertiesToCompare(request, iSortingCols);
	Integer[] orderToUse = getOrdering(request, iSortingCols);

	java.util.List<CorrespondenceEntry> entries = null;
	if (StringUtils.isEmpty(sSearch)) {
	    entries = CorrespondenceEntry.getActiveEntries();
	} else {
	    entries = CorrespondenceEntry.simpleSearch(sSearch);
	}

	Integer numberOfRecordsMatched = entries.size();
	java.util.List<CorrespondenceEntry> limitedEntries = limitAndOrderSearchedEntries(entries, propertiesToCompare,
		orderToUse, iDisplayStart, iDisplayLength);

	String jsonResponseString = serializeAjaxFilterResponse(sEcho, CorrespondenceEntry.getActiveEntries().size(),
		numberOfRecordsMatched, limitedEntries, request);

	final byte[] jsonResponsePayload = jsonResponseString.getBytes();

	response.setContentType("application/json");
	response.setContentLength(jsonResponsePayload.length);
	response.getOutputStream().write(jsonResponsePayload);
	response.getOutputStream().flush();
	response.getOutputStream().close();

	return null;
    }

    private String serializeAjaxFilterResponse(String sEcho, Integer iTotalRecords, Integer iTotalDisplayRecords,
	    java.util.List<CorrespondenceEntry> limitedEntries, HttpServletRequest request) {
	StringBuilder stringBuilder = new StringBuilder("{");
	stringBuilder.append("\"sEcho\": ").append(sEcho).append(", \n");
	stringBuilder.append("\"iTotalRecords\": ").append(iTotalRecords).append(", \n");
	stringBuilder.append("\"iTotalDisplayRecords\": ").append(iTotalDisplayRecords).append(", \n");
	stringBuilder.append("\"aaData\": ").append("[ \n");

	for (CorrespondenceEntry entry : limitedEntries) {
	    stringBuilder.append("[ \"").append(entry.getSender()).append("\", ");
	    stringBuilder.append("\"").append(entry.getRecipient()).append("\", ");
	    stringBuilder.append("\"").append(entry.getSubject()).append("\", ");
	    stringBuilder.append("\"").append(entry.getWhenReceived().toString("dd/MM/yyyy")).append("\", ");
	    stringBuilder.append("\"").append(generateLinkForCorrespondenceEntryEdition(request, entry)).append(",").append(
		    generateLinkForCorrespondenceEntryRemoval(request, entry)).append("\" ], ");

	}

	stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

	stringBuilder.append(" ]\n }");

	return stringBuilder.toString();
    }

    private String generateLinkForCorrespondenceEntryRemoval(HttpServletRequest request, CorrespondenceEntry entry) {
	String contextPath = request.getContextPath();
	String realLink = contextPath + String.format("/mailtracking.do?entryId=%s&method=deleteEntry", entry.getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

	return realLink;
    }

    private String generateLinkForCorrespondenceEntryEdition(HttpServletRequest request, CorrespondenceEntry entry) {
	String contextPath = request.getContextPath();
	String realLink = contextPath
		+ String.format("/mailtracking.do?entryId=%s&method=prepareEditEntry", entry.getExternalId());
	realLink += String.format("&%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME, GenericChecksumRewriter
		.calculateChecksum(realLink));

	return realLink;
    }

    private java.util.List<CorrespondenceEntry> limitAndOrderSearchedEntries(java.util.List searchedEntries,
	    final String[] propertiesToCompare, final Integer[] orderToUse, Integer iDisplayStart, Integer iDisplayLength) {
	Collections.sort(searchedEntries, new Comparator<CorrespondenceEntry>() {

	    @Override
	    public int compare(CorrespondenceEntry oLeft, CorrespondenceEntry oRight) {
		for (int i = 0; i < propertiesToCompare.length; i++) {
		    try {
			Comparable propLeft = (Comparable) PropertyUtils.getProperty(oLeft, propertiesToCompare[i]);
			Comparable propRight = (Comparable) PropertyUtils.getProperty(oRight, propertiesToCompare[i]);

			if (propLeft.compareTo(propRight) != 0) {
			    return orderToUse[i] * propLeft.compareTo(propRight);
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

    private Integer[] getOrdering(HttpServletRequest request, Integer iSortingCols) {
	java.util.List<Integer> order = new java.util.ArrayList<Integer>();

	for (int i = 0; i < iSortingCols; i++) {
	    String iSortingColDir = request.getParameter("iSortDir_" + i);
	    order.add((Integer) CORRESPONDENCE_TABLE_COLUMNS_MAP.get(iSortingColDir));
	}

	return order.toArray(new Integer[] {});
    }

    private String[] getPropertiesToCompare(HttpServletRequest request, Integer iSortingCols) {
	java.util.List<String> properties = new java.util.ArrayList<String>();
	for (int i = 0; i < iSortingCols; i++) {
	    String iSortingColIdx = request.getParameter("iSortCol_" + i);
	    properties.add((String) CORRESPONDENCE_TABLE_COLUMNS_MAP.get(iSortingColIdx));
	}

	return properties.toArray(new String[] {});
    }

    public ActionForward downloadFile(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws IOException {
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

	private CorrespondenceEntry entry;

	public AssociateDocumentBean() {

	}

	public AssociateDocumentBean(CorrespondenceEntry entry) {
	    this.setEntry(entry);
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

	public byte[] consumeStream() throws IOException {
	    byte[] content = new byte[this.getFilesize().intValue()];
	    this.getStream().read(content);

	    return content;
	}
    }

}
