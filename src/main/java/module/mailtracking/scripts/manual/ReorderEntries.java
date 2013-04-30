/*
 * @(#)ReorderEntries.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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

import java.util.List;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import pt.ist.bennu.core.domain.scheduler.WriteCustomTask;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class ReorderEntries extends WriteCustomTask {

    @Override
    public void doService() {
        MailTracking tracking = MailTracking.readMailTrackingByName("Conselho de Gestão");

        for (int i = 482, j = 477; i < 550; i++) {
            List<CorrespondenceEntry> entries = tracking.simpleSearch(CorrespondenceType.RECEIVED, "2010/" + i, false);

            if (entries.isEmpty()) {
                break;
            }

            String oldReference = entries.get(0).getReference();
            entries.get(0).setReference("2010/" + j++);
            out.println(String.format("Old value: %s, New value: %s", oldReference, entries.get(0).getReference()));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
