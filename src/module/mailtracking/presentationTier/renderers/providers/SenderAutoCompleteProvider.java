package module.mailtracking.presentationTier.renderers.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import module.mailtracking.domain.MailTracking;
import module.organization.domain.Party;
import module.organization.domain.Person;
import myorg.domain.User;
import myorg.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class SenderAutoCompleteProvider implements AutoCompleteProvider {

    @Override
    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	final List<Person> persons = new ArrayList<Person>();
	MailTracking mailTracking = readMailTracking(argsMap);

	final String trimmedValue = value.trim();
	final String[] input = trimmedValue.split(" ");
	StringNormalizer.normalize(input);

	for (final User user : mailTracking.getTotalUsers()) {
	    if (user.hasPerson()) {
		final String unitName = StringNormalizer.normalize(user.getPerson().getPartyName().getContent());
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

	return MailTracking.fromExternalId(mailTrackingId);
    }

}
