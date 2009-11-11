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
import module.organization.domain.Person;
import myorg.domain.User;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/manageMailTracking")
public class ManageMailTrackingAction extends ContextBaseAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	return super.execute(mapping, form, request, response);
    }

    public ActionForward prepareUsersManagement(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {

	request.setAttribute("searchUserBean", new SearchUserBean());
	request.setAttribute("mailTrackingBean", readMailTracking(request).createBean());

	return forward(request, "/mailtracking/management/manageUsers.jsp");
    }

    public ActionForward removeOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	MailTrackingBean bean = readMailTrackingBean(request);

	if (!bean.getMailTracking().isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeOperator(readUser(request));

	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	User user = readUser(request);
	if (!readMailTracking(request).isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	readMailTracking(request).addOperator(user);

	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	if (!readMailTracking(request).isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	readMailTracking(request).removeViewer(readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	User user = readUser(request);

	if (!readMailTracking(request).isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	readMailTracking(request).addViewer(user);

	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	User user = readUser(request);

	if (!readMailTracking(request).isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	readMailTracking(request).addManager(user);
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	User user = readUser(request);

	if (!readMailTracking(request).isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	readMailTracking(request).removeManager(user);
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward prepareMailTrackingAttributesManagement(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("mailTrackingBean", readMailTracking(request).createBean());

	return forward(request, "/mailtracking//management/manageAttributes.jsp");
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, HttpServletResponse response) throws Exception {
	MailTrackingBean bean = readMailTrackingBean(request);

	if (!bean.getMailTracking().isCurrentUserAbleToEditMailTrackingAttributes())
	    throw new PermissionDeniedException();

	bean.getMailTracking().edit(bean);

	addMessage(request, "mailtracking.operations", "message.mail.tracking.attributes.updated", new String[0]);

	return prepareMailTrackingAttributesManagement(mapping, form, request, response);
    }

    public ActionForward searchUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	SearchUserBean searchBean = readSearchUserBean(request);

	java.util.List<User> usersResult = new java.util.ArrayList<User>();
	if (SearchUserBean.SearchUserMode.USERNAME.equals(searchBean.getMode())) {
	    User user = User.findByUsername(searchBean.getValue());
	    usersResult.add(user);
	} else if (SearchUserBean.SearchUserMode.NAME.equals(searchBean.getMode())) {
	    java.util.List<Person> matchPersons = Person.searchPersons(searchBean.getValue());
	    for (Person person : matchPersons) {
		if (person.getUser() != null) {
		    usersResult.add(person.getUser());
		}
	    }
	}

	if (usersResult.isEmpty()) {
	    this.addMessage(request, "coolThing");
	}

	request.setAttribute("searchResults", usersResult);

	return prepareUsersManagement(mapping, form, request, response);
    }

    private MailTrackingBean readMailTrackingBean(final HttpServletRequest request) {
	return this.getRenderedObject("mail.tracking.bean");
    }

    private User readUser(HttpServletRequest request) {
	return User.fromExternalId(request.getParameter("userId"));
    }

    public ActionForward prepareMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {
	request.setAttribute("importationFileBean", new ImportationFileBean());

	return forward(request, "/mailtracking/manager/importMailTracking.jsp");
    }

    public ActionForward importMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {

	MailTracking mailTracking = readMailTrackingBean(request).getMailTracking();

	ImportationFileBean bean = readImportationFileBean(request);

	java.util.List<String> importationContents = consumeCsvImportationContent(bean);

	java.util.List<ImportationReportEntry> importationResults = new java.util.ArrayList<ImportationReportEntry>();

	boolean errorOccurred;
	try {
	    if (bean.getType().equals(CorrespondenceType.SENT)) {
		MailTrackingImportationHelper
			.importSentMailTrackingFromCsv(mailTracking, importationContents, importationResults);
		errorOccurred = true;
	    } else {
		MailTrackingImportationHelper.importReceivedMailTrackingFromCsv(mailTracking, importationContents,
			importationResults);
		errorOccurred = true;
	    }
	} catch (MailTrackingImportationHelper.ImportationErrorException e) {
	    errorOccurred = false;
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

    private MailTracking readMailTracking(final HttpServletRequest request) {
	return this.getDomainObject(request, "mailTrackingId");
    }

    private SearchUserBean readSearchUserBean(HttpServletRequest request) {
	return this.getRenderedObject("search.user.bean");
    }

}
