package module.mailtracking.domain;

import java.util.Collections;
import java.util.List;

import pt.ist.fenixWebFramework.services.Service;

public class Helper {

    @Service
    public void reNumberEntries(final MailTracking mailTracking) {
	for (Year year : mailTracking.getYears()) {
	    year.resetCounters();
	}

	List<CorrespondenceEntry> entries = mailTracking.getActiveEntries(CorrespondenceType.SENT);
	Collections.sort(entries, CorrespondenceEntry.SORT_BY_WHEN_SENT_COMPARATOR);
	for (CorrespondenceEntry entry : entries) {
	    entry.setOriginalReference(entry.getEntryNumber().toString());
	    entry.reIndexByYear();

	    entry.setReference(String.format("%s/%s", entry.getYear().getName(), entry.getYear().nextSentEntryNumber()));
	}

	entries = mailTracking.getActiveEntries(CorrespondenceType.RECEIVED);
	Collections.sort(entries, CorrespondenceEntry.SORT_BY_WHEN_RECEIVED_COMPARATOR);
	for (CorrespondenceEntry entry : entries) {
	    entry.setOriginalReference(entry.getEntryNumber().toString());
	    entry.reIndexByYear();

	    entry.setReference(String.format("%s/%s", entry.getYear().getName(), entry.getYear().nextRecievedEntryNumber()));
	}

    }

}
