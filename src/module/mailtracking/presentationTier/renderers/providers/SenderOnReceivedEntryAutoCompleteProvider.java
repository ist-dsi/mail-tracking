package module.mailtracking.presentationTier.renderers.providers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import myorg.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class SenderOnReceivedEntryAutoCompleteProvider implements AutoCompleteProvider {

    @Override
    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	Set<AutoCompleteValueWrapper> matchedRecipients = new HashSet<AutoCompleteValueWrapper>();

	MailTracking mailTracking = readMailTracking(argsMap);

	String normalizedValue = StringNormalizer.normalize(value);

	for (CorrespondenceEntry entry : mailTracking.getActiveEntries(CorrespondenceType.RECEIVED)) {
	    if (StringNormalizer.normalize(entry.getSender()).contains(normalizedValue)) {
		matchedRecipients.add(new AutoCompleteValueWrapper(entry.getSender()));
	    }
	}

	return matchedRecipients;
    }

    private MailTracking readMailTracking(Map<String, String> argsMap) {
	String mailTrackingId = argsMap.get("mailTrackingId");

	return MailTracking.fromExternalId(mailTrackingId);
    }

}
