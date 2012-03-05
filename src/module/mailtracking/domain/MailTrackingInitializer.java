/*
 * @(#)MailTrackingInitializer.java
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
package module.mailtracking.domain;

import module.mailtracking.presentationTier.MailTrackingView;
import module.organization.presentationTier.actions.OrganizationModelAction;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class MailTrackingInitializer extends MailTrackingInitializer_Base implements ModuleInitializer {
    private static boolean isInitialized = false;

    private static ThreadLocal<MailTrackingInitializer> init = null;

    public static MailTrackingInitializer getInstance() {
	if (init != null) {
	    return init.get();
	}

	if (!isInitialized) {
	    initialize();
	}
	final MyOrg myOrg = MyOrg.getInstance();
	return myOrg.getMailTrackingInitializer();
    }

    @Service
    public synchronized static void initialize() {
	if (!isInitialized) {
	    try {
		final MyOrg myOrg = MyOrg.getInstance();
		final MailTrackingInitializer initializer = myOrg.getMailTrackingInitializer();
		if (initializer == null) {
		    new MailTrackingInitializer();
		}
		init = new ThreadLocal<MailTrackingInitializer>();
		init.set(myOrg.getMailTrackingInitializer());

		isInitialized = true;
	    } finally {
		init = null;
	    }
	}
    }

    public MailTrackingInitializer() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    @Override
    public void init(MyOrg root) {
	OrganizationModelAction.partyViewHookManager.register(new MailTrackingView());
    }

}
