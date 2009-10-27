package module.mailtracking.presentationTier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTrackingImportationHelper;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.MailTrackingImportationHelper.ImportationReportEntry;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.organization.domain.Unit;
import module.organization.presentationTier.renderers.OrganizationViewConfiguration;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/manageMailTracking")
public class ManageMailTrackingAction extends ContextBaseAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	readOrganizationalUnit(request);
	readSearchUserBean(request);

	return super.execute(mapping, form, request, response);
    }

    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {

	request.setAttribute("myorg", getMyOrg());
	request.setAttribute("config", OrganizationViewConfiguration.defaultConfiguration());

	request.setAttribute("mailTrackings", getMyOrg().getMailTrackings());
	return forward(request, "/mailtracking/showOrgStructure.jsp");
    }

    public ActionForward manageMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	Unit unit = readOrganizationalUnit(request);
	MailTracking mailTracking = unit.getMailTracking();

	request.setAttribute("existsMailTrackingForUnit", mailTracking != null);

	if (mailTracking != null)
	    request.setAttribute("mailTrackingBean", readMailTrackingBean(request));

	return forward(request, "/mailtracking/manageMailTracking.jsp");
    }

    public ActionForward createMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	if (!MailTracking.isManager(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	Unit unit = readOrganizationalUnit(request);
	MailTracking.createMailTracking(unit);

	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, HttpServletResponse response) {
	if (!MailTracking.isManager(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	MailTrackingBean bean = readMailTrackingBean(request);
	bean.getMailTracking().edit(bean);

	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward removeOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	MailTrackingBean bean = readMailTrackingBean(request);

	bean.getMailTracking().removeOperator(readUser(request));
	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	mailTrackingBean.getMailTracking().addOperator(user);
	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	mailTrackingBean.getMailTracking().addViewer(user);
	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward searchUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	SearchUserBean searchBean = readSearchUserBean(request);

	java.util.List<User> usersResult = new java.util.ArrayList<User>();
	if (!StringUtils.isEmpty(searchBean.getIstUsername())) {
	    User user = User.findByUsername(searchBean.getIstUsername());
	    usersResult.add(user);
	} else if (!StringUtils.isEmpty(searchBean.getName())) {
	}

	if (usersResult.isEmpty()) {
	    this.addMessage(request, "coolThing");
	}

	request.setAttribute("searchResults", usersResult);
	request.setAttribute("searchUserBean", new SearchUserBean());

	return manageMailTracking(mapping, form, request, response);
    }

    private Unit readOrganizationalUnit(final HttpServletRequest request) {
	Unit unit = Unit.fromExternalId(request.getParameter("partyOid"));

	request.setAttribute("unit", unit);
	return unit;
    }

    private MailTrackingBean readMailTrackingBean(final HttpServletRequest request) {
	MailTrackingBean bean = (MailTrackingBean) request.getAttribute("mailTrackingBean");

	if (bean == null) {
	    bean = this.getRenderedObject("mail.tracking.bean");
	}

	if (bean == null) {
	    Unit unit = readOrganizationalUnit(request);
	    bean = unit.getMailTracking().createBean();
	}

	request.setAttribute("mailTrackingBean", bean);

	return bean;
    }

    private User readUser(HttpServletRequest request) {
	return User.fromExternalId(request.getParameter("userId"));
    }

    private SearchUserBean readSearchUserBean(HttpServletRequest request) {
	SearchUserBean searchBean = (SearchUserBean) request.getAttribute("searchUserBean");

	if (searchBean == null) {
	    searchBean = this.getRenderedObject("search.user.bean");
	}

	if (searchBean == null) {
	    searchBean = new SearchUserBean();
	}

	request.setAttribute("searchUserBean", searchBean);

	return searchBean;
    }

    public ActionForward prepareCGMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {

	request.setAttribute("importationFileBean", new ImportationFileBean());

	return forward(request, "/mailtracking/manager/importMailTracking.jsp");
    }

    public ActionForward importCGMailTracking(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {

	MailTracking mailTracking = readMailTrackingBean(request).getMailTracking();

	ImportationFileBean bean = readImportationFileBean(request);

	java.util.List<String> importationContents = consumeCsvImportationContent(bean);

	java.util.List<ImportationReportEntry> importationResults = new java.util.ArrayList<ImportationReportEntry>();

	boolean errorOccurred;

	if (bean.getType().equals(CorrespondenceType.SENT)) {
	    errorOccurred = MailTrackingImportationHelper.importSentMailTrackingFromCsv(mailTracking, importationContents,
		    importationResults);
	} else {
	    errorOccurred = MailTrackingImportationHelper.importReceivedMailTrackingFromCsv(mailTracking, importationContents,
		    importationResults);
	}

	request.setAttribute("errorOccurred", errorOccurred);
	request.setAttribute("importationFileResults", importationResults);

	return forward(request, "/mailtracking/manager/viewImportationResults.jsp");
    }

    private ImportationFileBean readImportationFileBean(final HttpServletRequest request) {
	ImportationFileBean importFileBean = (ImportationFileBean) request.getAttribute("importationFileBean");

	if (importFileBean == null)
	    importFileBean = this.getRenderedObject("importation.file.bean");

	return importFileBean;
    }

    public static class SearchUserBean implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String istUsername;
	private String name;

	public SearchUserBean() {

	}

	public String getIstUsername() {
	    return istUsername;
	}

	public void setIstUsername(String istUsername) {
	    this.istUsername = istUsername;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

    }

    public static class ImportationFileBean implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String filename;
	private String mimetype;
	private Long filesize;
	private InputStream stream;

	private CorrespondenceType type;

	public ImportationFileBean() {

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

	public CorrespondenceType getType() {
	    return type;
	}

	public void setType(CorrespondenceType type) {
	    this.type = type;
	}
    }

    private byte[] consumeStream(Long fileSize, InputStream stream) throws IOException {
	byte[] content = new byte[fileSize.intValue()];
	stream.read(content);

	return content;
    }

    private java.util.List<String> consumeCsvImportationContent(ImportationFileBean bean) throws IOException {
	InputStreamReader inputStreamReader = new InputStreamReader(bean.getStream(), "UTF8");
	BufferedReader reader = new BufferedReader(inputStreamReader);

	java.util.List<String> stringContents = new java.util.ArrayList<String>();
	String line = null;
	while ((line = reader.readLine()) != null) {
	    stringContents.add(line);
	}

	return stringContents;
    }

}
