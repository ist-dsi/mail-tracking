package module.mailtracking.presentationTier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.MailTrackingImportationHelper.ImportationReportEntry;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.organization.domain.Person;
import myorg.domain.User;
import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;
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
	MailTrackingActionOperations.removeOperator(readMailTrackingBean(request), readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	MailTrackingActionOperations.addOperator(readMailTrackingBean(request), readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    private static User readUser(HttpServletRequest request) {
	return User.fromExternalId(request.getParameter("userId"));
    }

    public ActionForward removeViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTrackingActionOperations.removeViewer(readMailTrackingBean(request), readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) {
	MailTrackingActionOperations.addViewer(readMailTrackingBean(request), readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTrackingActionOperations.addManager(readMailTrackingBean(request), readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTrackingActionOperations.removeManager(readMailTrackingBean(request), readUser(request));
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

    public ActionForward prepareMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {
	request.setAttribute("importationFileBean", new ImportationFileBean());

	return forward(request, "/mailtracking/manager/importMailTracking.jsp");
    }

    public ActionForward importMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	java.util.List<ImportationReportEntry> importationResults = new java.util.ArrayList<ImportationReportEntry>();

	request.setAttribute("errorOccurred", MailTrackingActionOperations.importMailTracking(readMailTrackingBean(request),
		readImportationFileBean(request), importationResults));
	request.setAttribute("importationFileResults", importationResults);

	return forward(request, "/mailtracking/manager/viewImportationResults.jsp");
    }

    private ImportationFileBean readImportationFileBean(final HttpServletRequest request) {
	ImportationFileBean importFileBean = (ImportationFileBean) request.getAttribute("importationFileBean");

	if (importFileBean == null)
	    importFileBean = this.getRenderedObject("importation.file.bean");

	return importFileBean;
    }

    private MailTracking readMailTracking(final HttpServletRequest request) {
	return this.getDomainObject(request, "mailTrackingId");
    }

    private SearchUserBean readSearchUserBean(HttpServletRequest request) {
	return this.getRenderedObject("search.user.bean");
    }

    @Override
    public Context createContext(String contextPathString, HttpServletRequest request) {
	LayoutContext context = (LayoutContext) super.createContext(contextPathString, request);
	context.addHead("/mailtracking/layoutHead.jsp");
	return context;
    }
}
