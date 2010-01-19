package module.mailtracking.domain;

import java.util.Collections;
import java.util.List;

import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

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

    @Service
    public void removeEntriesFromConcelhoGestaoAndResetCounters(final MailTracking mailtracking) {
	for (CorrespondenceEntry entry : mailtracking.getEntries()) {
	    entry.deleteDomainObject();
	}

	for (Year year : mailtracking.getYears()) {
	    year.resetCounters();
	}
    }

    static boolean matchGivenSearchToken(final CorrespondenceEntry entry, final String searchToken) {
	final String normalizedKey = StringNormalizer.normalize(searchToken);

	String normalizedReference = StringNormalizer.normalize(entry.getReference() != null ? entry.getReference() : "");
	String normalizedReceivedDate = StringNormalizer.normalize(entry.getWhenReceived() != null ? entry.getWhenReceived()
		.toString("dd/MM/yyyy") : "");
	String normalizedSentDate = StringNormalizer.normalize(entry.getWhenSent() != null ? entry.getWhenSent().toString(
		"dd/MM/yyyy") : "");
	String normalizedEntrySender = StringNormalizer.normalize(entry.getSender() != null ? entry.getSender() : "");
	String normalizedEntryRecipient = StringNormalizer.normalize(entry.getRecipient() != null ? entry.getRecipient() : "");
	String normalizedSubject = StringNormalizer.normalize(entry.getSubject() != null ? entry.getSubject() : "");
	String normalizedSenderLetterNumber = StringNormalizer.normalize(entry.getSenderLetterNumber() != null ? entry
		.getSenderLetterNumber() : "");

	if (CorrespondenceType.SENT.equals(entry.getType())) {
	    return normalizedReference.contains(normalizedKey) || normalizedSentDate.contains(normalizedKey)
		    || normalizedEntryRecipient.contains(normalizedKey) || normalizedSubject.contains(normalizedKey)
		    || normalizedEntrySender.contains(normalizedKey);
	} else if (CorrespondenceType.RECEIVED.equals(entry.getType())) {
	    return normalizedReference.contains(normalizedKey) || normalizedReceivedDate.contains(normalizedKey)
		    || normalizedEntrySender.contains(normalizedKey) || normalizedEntryRecipient.contains(normalizedKey)
		    || normalizedSubject.contains(normalizedKey) || normalizedSenderLetterNumber.contains(normalizedKey);
	}

	return false;
    }

    public void setCountersOnYear(Year year, int nextReceivedEntryNumber, int nextSentEntryNumber) {
	year.setNextReceivedEntryNumber(nextReceivedEntryNumber);
	year.setNextSentEntryNumber(nextSentEntryNumber);
    }

    @Service
    public void setCountersOnYear() {
	final MailTracking mailtracking = MailTracking.readMailTrackingByName("Executive Board");

	setCountersOnYear(mailtracking.getYearFor(2007), 1, 1);
	setCountersOnYear(mailtracking.getYearFor(2008), 1600, 234);
	setCountersOnYear(mailtracking.getYearFor(2009), 1381, 346);
	setCountersOnYear(mailtracking.getYearFor(2010), 1, 1);
    }
}
