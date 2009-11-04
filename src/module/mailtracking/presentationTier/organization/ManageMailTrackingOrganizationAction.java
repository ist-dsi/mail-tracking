package module.mailtracking.presentationTier.organization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.mailtracking.presentationTier.ImportationFileBean;
import module.mailtracking.presentationTier.MailTrackingView;
import module.mailtracking.presentationTier.SearchUserBean;
import module.organization.domain.OrganizationalModel;
import module.organization.domain.Person;
import module.organization.domain.Unit;
import module.organization.presentationTier.actions.OrganizationModelAction;
import myorg.domain.User;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/mailTrackingOrganizationModel")
public class ManageMailTrackingOrganizationAction extends OrganizationModelAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	request.setAttribute("party", readOrganizationalUnit(request));
	request.setAttribute("organizationalModel", readOrganizationalModel(request));
	request.setAttribute("existsMailTrackingForUnit", readOrganizationalUnit(request).getMailTracking() != null);
	request.setAttribute("viewName", MailTrackingView.VIEW_NAME);

	return super.execute(mapping, form, request, response);
    }

    public ActionForward back(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	return viewModel(mapping, form, request, response);
    }

    public ActionForward createMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	if (!MailTracking.isCurrentUserAbleToCreateMailTrackingModule())
	    throw new PermissionDeniedException();

	Unit unit = readOrganizationalUnit(request);
	MailTracking.createMailTracking(unit);

	return viewModel(mapping, form, request, response);
    }

    private Unit readOrganizationalUnit(final HttpServletRequest request) {
	return this.getDomainObject(request, "partyOid");
    }

    private OrganizationalModel readOrganizationalModel(final HttpServletRequest request) {
	return this.getDomainObject(request, "organizationalModelOid");
    }

    public ActionForward prepareMailTrackingAttributesManagement(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("mailTrackingBean", readOrganizationalUnit(request).getMailTracking().createBean());
	return forward(request, "/module/mailTracking/manageAttributes.jsp");
    }

    public ActionForward prepareUsersManagement(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("searchUserBean", readSearchUserBean(request));

	request.setAttribute("mailTrackingBean", readOrganizationalUnit(request).getMailTracking().createBean());
	return forward(request, "/module/mailTracking/manageUsers.jsp");
    }

    public ActionForward removeOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	MailTrackingBean bean = readMailTrackingBean(request);

	if (!bean.getMailTracking().isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeOperator(readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	if (!mailTrackingBean.getMailTracking().isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	mailTrackingBean.getMailTracking().addOperator(user);
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	if (!mailTrackingBean.getMailTracking().isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	mailTrackingBean.getMailTracking().addViewer(user);
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTrackingBean bean = readMailTrackingBean(request);

	if (!bean.getMailTracking().isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeViewer(readUser(request));
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	if (!mailTrackingBean.getMailTracking().isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	mailTrackingBean.getMailTracking().addManager(user);
	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	User user = readUser(request);
	MailTrackingBean mailTrackingBean = readMailTrackingBean(request);

	if (!mailTrackingBean.getMailTracking().isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	mailTrackingBean.getMailTracking().removeManager(user);
	return prepareUsersManagement(mapping, form, request, response);
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

	return searchBean;
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
	request.setAttribute("searchUserBean", new SearchUserBean());

	return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, HttpServletResponse response) throws Exception {

	MailTrackingBean bean = readMailTrackingBean(request);

	if (!bean.getMailTracking().isCurrentUserAbleToEditMailTrackingAttributes())
	    throw new PermissionDeniedException();

	bean.getMailTracking().edit(bean);

	return viewModel(mapping, form, request, response);
    }

    public ActionForward prepareMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {
	request.setAttribute("importationFileBean", new ImportationFileBean());

	return forward(request, "/module/mailTracking/importEntries.jsp");
    }

}
