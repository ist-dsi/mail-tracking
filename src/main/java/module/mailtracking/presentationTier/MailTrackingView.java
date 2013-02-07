/*
 * @(#)MailTrackingView.java
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

import javax.servlet.http.HttpServletRequest;

import module.organization.domain.OrganizationalModel;
import module.organization.domain.Party;
import module.organization.domain.Unit;
import module.organization.presentationTier.actions.PartyViewHook;
import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.util.BundleUtil;

/**
 * 
 * @author Anil Kassamali
 * 
 */
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

        if (unit.getMailTracking() != null) {
            request.setAttribute("mailTrackingBean", unit.getMailTracking().createBean());
        }

        return "/module/mailTracking/mailTrackingView.jsp";
    }

    @Override
    public boolean isAvailableFor(final Party party) {
        return (party instanceof Unit) && UserView.getCurrentUser().hasRoleType(RoleType.MANAGER);
    }

}
