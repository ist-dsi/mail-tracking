/*
 * @(#)SetVirtualHostForMailTrackingsOnDot.java
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
package module.mailtracking.scripts.manual.virtualHosts;

import java.util.Set;

import module.mailtracking.domain.MailTracking;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.scheduler.WriteCustomTask;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class SetVirtualHostForMailTrackingsOnDot extends WriteCustomTask {

    @Override
    public void doService() {
        Set<MailTracking> mailTrackings = MyOrg.getInstance().getMailTrackingsSet();
        VirtualHost virtualHostForDot = getVirtualHostForDot();

        for (MailTracking mailTracking : mailTrackings) {
            if (mailTracking.getVirtualHost() != null) {
                continue;
            }

            mailTracking.setVirtualHost(virtualHostForDot);
        }
    }

    private VirtualHost getVirtualHostForDot() {
        Set<VirtualHost> virtualHostsSet = MyOrg.getInstance().getVirtualHostsSet();

        for (VirtualHost virtualHost : virtualHostsSet) {
            if ("dot.ist.utl.pt".equals(virtualHost.getHostname())) {
                return virtualHost;
            }

            out.println(virtualHost.getHostname() + " is not equal to 'dot.ist.utl.pt'");
        }

        throw new RuntimeException("could not find virtual host for dot");
    }

}
