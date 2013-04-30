/*
 * @(#)AddUsersToDRHMailTracking.java
 *
 * Copyright 2011 Instituto Superior Tecnico
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
package module.mailtracking.scripts.manual;

import module.mailtracking.domain.MailTracking;
import module.organization.domain.Unit;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.scheduler.WriteCustomTask;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class AddUsersToDRHMailTracking extends WriteCustomTask {

    private static final String[] IST_USERNAMES = { "ist12444", "ist12889", "ist20831", "ist20940", "ist21303", "ist21767",
            "ist21768", "ist21769", "ist21846", "ist22137", "ist22329", "ist22674", "ist22686", "ist22751", "ist22752",
            "ist22758", "ist23202", "ist23470", "ist23487", "ist23647", "ist23889", "ist23930", "ist23932", "ist24064",
            "ist24252", "ist24385", "ist24688", "ist24726", "ist24769", "ist24875", "ist24877", "ist24888", "ist24954",
            "ist25096", "ist25136", "ist32949", "ist90385" };

    @Override
    public void doService() {
        Unit unit = FenixFramework.getDomainObject("450971566500");

        MailTracking mailTracking = unit.getMailTracking();

        for (String user : IST_USERNAMES) {
            mailTracking.addViewer(User.findByUsername(user));
        }
    }

}
