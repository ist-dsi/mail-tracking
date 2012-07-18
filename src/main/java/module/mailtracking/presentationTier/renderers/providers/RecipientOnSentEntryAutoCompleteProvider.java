/*
 * @(#)RecipientOnSentEntryAutoCompleteProvider.java
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
package module.mailtracking.presentationTier.renderers.providers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import pt.ist.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class RecipientOnSentEntryAutoCompleteProvider implements AutoCompleteProvider {

    @Override
    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	Set<AutoCompleteValueWrapper> matchedRecipients = new HashSet<AutoCompleteValueWrapper>();

	MailTracking mailTracking = readMailTracking(argsMap);

	String normalizedValue = StringNormalizer.normalize(value);

	for (CorrespondenceEntry entry : mailTracking.getActiveEntries(CorrespondenceType.SENT)) {
	    if (StringNormalizer.normalize(entry.getRecipient()).contains(normalizedValue)) {
		matchedRecipients.add(new AutoCompleteValueWrapper(entry.getRecipient()));
	    }
	}

	return matchedRecipients;
    }

    private MailTracking readMailTracking(Map<String, String> argsMap) {
	String mailTrackingId = argsMap.get("mailTrackingId");

	return MailTracking.fromExternalId(mailTrackingId);
    }

}
