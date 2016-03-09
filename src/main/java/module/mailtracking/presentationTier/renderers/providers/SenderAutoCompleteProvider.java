/*
 * @(#)SenderAutoCompleteProvider.java
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
package module.mailtracking.presentationTier.renderers.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import org.fenixedu.commons.StringNormalizer;

import module.mailtracking.domain.MailTracking;
import module.organization.domain.Party;
import module.organization.domain.Person;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class SenderAutoCompleteProvider implements AutoCompleteProvider<Person> {

    @Override
    public Collection<Person> getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
        final List<Person> persons = new ArrayList<Person>();
        MailTracking mailTracking = readMailTracking(argsMap);

        final String trimmedValue = value.trim();
        final String[] input = trimmedValue.split(" ");
        for (int i = 0; i < input.length; i++) {
            input[i] = StringNormalizer.normalize(input[i]);
        }

        for (final User user : mailTracking.getTotalUsers()) {
            if (user != null) {
                final String unitName = StringNormalizer.normalize(user.getDisplayName() + "(" + user.getUsername() + ")");
                if (hasMatch(input, unitName)) {
                    persons.add(user.getPerson());
                }
            }
        }

        Collections.sort(persons, Party.COMPARATOR_BY_NAME);

        return persons;

    }

    private boolean hasMatch(final String[] input, final String unitNameParts) {
        for (final String namePart : input) {
            if (unitNameParts.indexOf(namePart) == -1) {
                return false;
            }
        }
        return true;
    }

    private MailTracking readMailTracking(Map<String, String> argsMap) {
        String mailTrackingId = argsMap.get("mailTrackingId");

        return FenixFramework.getDomainObject(mailTrackingId);
    }

}
