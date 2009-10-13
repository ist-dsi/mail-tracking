package module.mailtracking.presentationTier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.organization.domain.Unit;
import module.organization.presentationTier.renderers.OrganizationViewConfiguration;
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
	Unit unit = readOrganizationalUnit(request);
	MailTracking.createMailTracking(unit);

	return manageMailTracking(mapping, form, request, response);
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, HttpServletResponse response) {
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

}
