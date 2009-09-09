package module.mailtracking.presentationTier;

import java.io.IOException;
import java.io.InputStream;

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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
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

	request.setAttribute("correspondenceEntryBean", new CorrespondenceEntry());

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
