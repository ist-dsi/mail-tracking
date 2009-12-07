package module.mailtracking.domain;

import module.mailtracking.presentationTier.YearBean;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class Year extends Year_Base {

    protected Year() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    protected Year(final MailTracking mailTracking, final String name, final DateTime startDate, final DateTime endDate) {
	super();

	setMailTracking(mailTracking);
	setName(name);
	setStartDate(startDate);
	setEndDate(endDate);

	checkParameters();
    }

    private void checkParameters() {
	if (getMailTracking() == null) {
	    throw new DomainException("error.mail.tracking.year.mail.tracking.invalid");
	}

	if (StringUtils.isEmpty(getName())) {
	    throw new DomainException("error.mail.tracking.year.name.invalid");
	}

	if (getStartDate() == null) {
	    throw new DomainException("error.mail.tracking.year.start.date.invalid");
	}

	if (getEndDate() == null) {
	    throw new DomainException("error.mail.tracking.year.end.date.invalid");
	}

	if (!getEndDate().isAfter(getStartDate())) {
	    throw new DomainException("error.mail.tracking.year.end.before.start.date");
	}

	for (Year year : getMailTracking().getYears()) {
	    if (this == year) {
		continue;
	    }

	    if (year.getName().equalsIgnoreCase(getName())) {
		throw new DomainException("error.mail.tracking.year.name.exists");
	    }

	    if (intersect(year.getStartDate(), year.getEndDate(), getStartDate(), getEndDate())) {
		throw new DomainException("error.mail.tracking.year.intersect");
	    }
	}
    }

    static Year getYearFor(MailTracking mailTracking, Integer forYear) {
	DateTime time = new DateTime(forYear, 6, 01, 0, 0, 0, 0);

	return null;
    }

    @Service
    public static Year createYearFor(MailTracking mailTracking, Integer forYear) {

	if (forYear < 2007 || forYear > 2010) {
	    throw new DomainException("error.mail.tracking.year.invalid");
	}

	if (getYearFor(mailTracking, forYear) != null) {
	    throw new DomainException("error.mail.tracking.year.already.created");
	}

	DateTime startDate = new DateTime(forYear, 01, 01, 0, 0, 0, 0);
	DateTime endDate = new DateTime(forYear, 12, 31, 23, 59, 59, 0);

	return new Year(mailTracking, forYear.toString(), startDate, endDate);
    }

    static Year getYearFor(final MailTracking mailTracking, final DateTime date) {
	for (Year year : mailTracking.getYears()) {
	    if (year.isBetween(date)) {
		return year;
	    }
	}

	return null;
    }

    public java.util.List<CorrespondenceEntry> getActiveEntries(final CorrespondenceType type) {
	return this.getEntries(CorrespondenceEntryState.ACTIVE, type);
    }

    public java.util.List<CorrespondenceEntry> getDeletedEntries(final CorrespondenceType type) {
	return this.getEntries(CorrespondenceEntryState.DELETED, type);
    }

    public java.util.List<CorrespondenceEntry> getAnyStateEntries(final CorrespondenceType type) {
	return this.getEntries(null, type);
    }

    private java.util.List<CorrespondenceEntry> getEntries(final CorrespondenceEntryState state, final CorrespondenceType type) {
	return MailTracking.filterEntriesByTypeAndState(this.getEntries(), state, type);
    }

    // TODO REFACTOR
    public java.util.List<CorrespondenceEntry> getAbleToViewActiveEntries(final CorrespondenceType type) {
	java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

	CollectionUtils.select(getActiveEntries(type), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		return ((CorrespondenceEntry) arg0).isUserAbleToView(UserView.getCurrentUser());
	    }

	}, entries);

	return entries;
    }

    // TODO REFACTOR
    public java.util.List<CorrespondenceEntry> simpleSearch(CorrespondenceType type, final String key) {
	java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

	if (StringUtils.isEmpty(key)) {
	    return entries;
	}

	final String normalizedKey = StringNormalizer.normalize(key);

	CollectionUtils.select(this.getAbleToViewActiveEntries(type), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
		String normalizedEntrySender = StringNormalizer.normalize(entry.getSender());
		String normalizedEntryRecipient = StringNormalizer.normalize(entry.getRecipient());
		String normalizedSubject = StringNormalizer.normalize(entry.getSubject());

		return (normalizedEntrySender.indexOf(normalizedKey) > -1 || normalizedEntryRecipient.indexOf(normalizedKey) > -1 || normalizedSubject
			.indexOf(normalizedKey) > -1);
	    }

	}, entries);

	return entries;
    }

    private boolean isBetween(final DateTime toCompare) {
	return isDateTimeBetween(this.getStartDate(), this.getEndDate(), toCompare);
    }

    private static boolean isDateTimeBetween(final DateTime start, final DateTime end, final DateTime toCompare) {
	return !start.isAfter(toCompare) && !end.isBefore(toCompare);
    }

    private static boolean intersect(final DateTime startLeft, final DateTime endLeft, final DateTime startRight,
	    final DateTime endRight) {
	if (!endLeft.isAfter(startLeft)) {
	    throw new DomainException("error.mail.tracking.end.date.before.start.date");
	}

	if (!endRight.isAfter(startRight)) {
	    throw new DomainException("error.mail.tracking.end.date.before.start.date");
	}

	return isDateTimeBetween(startLeft, endLeft, startRight) || isDateTimeBetween(startLeft, endLeft, endRight)
		|| isDateTimeBetween(startRight, endRight, startLeft) || isDateTimeBetween(startRight, endRight, endLeft);
    }

    public static YearBean createEmptyBean() {
	return new YearBean();
    }

    @Override
    public Integer getNextReceivedEntryNumber() {
	throw new DomainException("error.mail.tracking.method.cannot.be.called.outside");
    }

    @Override
    public Integer getNextSentEntryNumber() {
	throw new DomainException("error.mail.tracking.method.cannot.be.called.outside");
    }

    Integer nextSentEntryNumber() {
	if (super.getNextSentEntryNumber() == null) {
	    super.setNextSentEntryNumber(1);
	    return 1;
	}

	Integer num = super.getNextSentEntryNumber();
	super.setNextSentEntryNumber(num + 1);
	return num;
    }

    Integer nextRecievedEntryNumber() {
	if (super.getNextReceivedEntryNumber() == null) {
	    super.setNextReceivedEntryNumber(1);
	    return 1;
	}

	Integer num = super.getNextReceivedEntryNumber();
	super.setNextReceivedEntryNumber(num + 1);
	return num;
    }

    void resetCounters() {
	super.setNextSentEntryNumber(1);
	super.setNextReceivedEntryNumber(1);
    }
}
