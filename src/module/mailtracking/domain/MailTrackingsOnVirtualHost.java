/*
 * @(#)MailTrackingsOnVirtualHost.java
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
package module.mailtracking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import myorg.domain.MyOrg;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class MailTrackingsOnVirtualHost {

    public static final List<MailTracking> retrieve() {
	Set<MailTracking> mailTrackings = MyOrg.getInstance().getMailTrackingsSet();

	List<MailTracking> result = new ArrayList<MailTracking>();

	for (MailTracking tracking : mailTrackings) {
	    if (tracking.isConnectedToCurrentHost()) {
		result.add(tracking);
	    }
	}
	
	return result;
    }

}
