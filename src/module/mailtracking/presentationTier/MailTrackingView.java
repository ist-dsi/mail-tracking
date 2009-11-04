package module.mailtracking.presentationTier;

import javax.servlet.http.HttpServletRequest;

import module.organization.domain.OrganizationalModel;
import module.organization.domain.Party;
import module.organization.domain.Unit;
import module.organization.presentationTier.actions.PartyViewHook;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.RoleType;
import myorg.util.BundleUtil;

public class MailTrackingView extends PartyViewHook {

    @Override
    public String getPresentationName() {
	return BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
		"title.mail.tracking.for.organization.management");
    }

    public static final String VIEW_NAME = "04_mailTrackingView";

    @Override
    public String getViewName() {
	return VIEW_NAME;
    }

    @Override
    public String hook(HttpServletRequest request, OrganizationalModel organizationalModel, Party party) {
	Unit unit = (Unit) party;

	request.setAttribute("existsMailTrackingForUnit", unit.getMailTracking() != null);

	if (unit.getMailTracking() != null)
	    request.setAttribute("mailTrackingBean", unit.getMailTracking().createBean());

	return "/module/mailTracking/mailTrackingView.jsp";
    }

    @Override
    public boolean isAvailableFor(final Party party) {
	return (party instanceof Unit) && UserView.getCurrentUser().hasRoleType(RoleType.MANAGER);
    }

}
