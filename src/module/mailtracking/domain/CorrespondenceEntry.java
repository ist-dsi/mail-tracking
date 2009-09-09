package module.mailtracking.domain;

import java.util.Collections;
import java.util.Comparator;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class CorrespondenceEntry extends CorrespondenceEntry_Base {

    public static final Comparator<CorrespondenceEntry> SORT_BY_WHEN_RECEIVED_COMPARATOR = new Comparator<CorrespondenceEntry>() {

	@Override
	public int compare(CorrespondenceEntry entryLeft, CorrespondenceEntry entryRight) {
	    return entryLeft.getWhenReceived().compareTo(entryRight.getWhenReceived());
	}

    };

    public CorrespondenceEntry() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public CorrespondenceEntry(String sender, String recipient, String subject, DateTime whenReceived) {
	this();
	init(sender, recipient, subject, whenReceived);
    }

    protected void init(String sender, String recipient, String subject, DateTime whenReceived) {
	checkParameters(sender, recipient, subject, whenReceived);

	setState(CorrespondenceEntryState.ACTIVE);
	setSender(sender);
	setRecipient(recipient);
	setSubject(subject);
	setWhenReceived(whenReceived);
    }

    private void checkParameters(String sender, String recipient, String subject, DateTime whenReceived) {
	if (StringUtils.isEmpty(sender)) {
	    throw new DomainException("error.correspondence.entry.sender.cannot.be.empty");
	}

	if (StringUtils.isEmpty(recipient)) {
	    throw new DomainException("error.correspondence.entry.recipient.cannot.be.empty");
	}

	if (StringUtils.isEmpty(subject)) {
	    throw new DomainException("error.correspondence.entry.subject.cannot.be.empty");
	}

	if (whenReceived == null) {
	    throw new DomainException("error.correspondence.entry.when.received.cannot.be.empty");
	}
    }

    public static java.util.List<CorrespondenceEntry> getLastActiveEntriesSortedByDate(Integer numberOfEntries) {
	java.util.List<CorrespondenceEntry> entries = getActiveEntries();

	Collections.sort(entries, SORT_BY_WHEN_RECEIVED_COMPARATOR);

	return entries.subList(0, Math.min(entries.size(), numberOfEntries));
    }

    public static java.util.List<CorrespondenceEntry> getActiveEntries() {
	java.util.List<CorrespondenceEntry> allEntries = MyOrg.getInstance().getCorrespondenceEntries();
	java.util.List<CorrespondenceEntry> activeEntries = new java.util.ArrayList<CorrespondenceEntry>();

	CollectionUtils.select(allEntries, new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		return CorrespondenceEntryState.ACTIVE.equals(((CorrespondenceEntry) arg0).getState());
	    }

	}, activeEntries);

	return activeEntries;
    }

    private static CorrespondenceEntry createNewEntry(String sender, String recipient, String subject, DateTime whenReceived) {
	return new CorrespondenceEntry(sender, recipient, subject, whenReceived);
    }

    @Service
    public static CorrespondenceEntry createNewEntry(final CorrespondenceEntryBean bean) {
	return createNewEntry(bean.getSender(), bean.getRecipient(), bean.getSubject(), bean.getWhenReceived());
    }

    @Service
    public void markEntryAsDeleted() {
	this.setState(CorrespondenceEntryState.DELETED);
    }

    public static java.util.List<CorrespondenceEntry> find(final String sender, final String recipient, final String subject,
	    final DateTime whenReceivedBegin, final DateTime whenReceivedEnd) {
	java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

	final String normalizedSender = StringNormalizer.normalize(sender);
	final String normalizedRecipient = StringNormalizer.normalize(recipient);
	final String normalizedSubject = StringNormalizer.normalize(subject);

	if (StringUtils.isEmpty(sender) && StringUtils.isEmpty(recipient) && whenReceivedBegin == null && whenReceivedEnd == null)
	    return entries;

	CollectionUtils.select(getActiveEntries(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
		String normalizedEntrySender = StringNormalizer.normalize(entry.getSender());
		String normalizedEntryRecipient = StringNormalizer.normalize(entry.getRecipient());
		String normalizedEntrySubject = StringNormalizer.normalize(entry.getSubject());

		DateTime whenReceivedEntry = entry.getWhenReceived();

		return (StringUtils.isEmpty(sender) || normalizedEntrySender.indexOf(normalizedSender) > -1)
			&& (StringUtils.isEmpty(normalizedRecipient) || normalizedEntryRecipient.indexOf(normalizedRecipient) > -1)
			&& (StringUtils.isEmpty(normalizedSubject) || normalizedEntrySubject.indexOf(normalizedSubject) > -1)
			&& (whenReceivedBegin == null || !whenReceivedEntry.isBefore(whenReceivedBegin))
			&& (whenReceivedEnd == null || !whenReceivedEntry.isAfter(whenReceivedEnd));
	    }

	}, entries);

	return entries;
    }

    public static java.util.List<CorrespondenceEntry> simpleSearch(final String key) {
	java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

	if (StringUtils.isEmpty(key)) {
	    return entries;
	}

	final String normalizedKey = StringNormalizer.normalize(key);

	CollectionUtils.select(getActiveEntries(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
		String normalizedEntrySender = StringNormalizer.normalize(entry.getSender());
		String normalizedEntryRecipient = StringNormalizer.normalize(entry.getRecipient());
		String normalizedSubject = StringNormalizer.normalize(entry.getSubject());

		return normalizedEntrySender.indexOf(normalizedKey) > -1 || normalizedEntryRecipient.indexOf(normalizedKey) > -1
			|| normalizedSubject.indexOf(normalizedKey) > -1;
	    }

	}, entries);

	return entries;
    }

    public static class CorrespondenceEntryBean implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sender;
	private String recipient;
	private String subject;
	private DateTime whenReceived;

	private CorrespondenceEntry entry;

	public CorrespondenceEntryBean() {

	}

	public CorrespondenceEntryBean(CorrespondenceEntry entry) {
	    this.setSender(entry.getSender());
	    this.setRecipient(entry.getRecipient());
	    this.setSubject(entry.getSubject());
	    this.setWhenReceived(entry.getWhenReceived());
	    this.setEntry(entry);
	}

	public String getSender() {
	    return sender;
	}

	public void setSender(String sender) {
	    this.sender = sender;
	}

	public String getRecipient() {
	    return recipient;
	}

	public void setRecipient(String recipient) {
	    this.recipient = recipient;
	}

	public String getSubject() {
	    return subject;
	}

	public void setSubject(String subject) {
	    this.subject = subject;
	}

	public DateTime getWhenReceived() {
	    return whenReceived;
	}

	public void setWhenReceived(DateTime whenReceived) {
	    this.whenReceived = whenReceived;
	}

	public CorrespondenceEntry getEntry() {
	    return entry;
	}

	public void setEntry(CorrespondenceEntry entry) {
	    this.entry = entry;
	}

    }

    @Service
    public void edit(CorrespondenceEntryBean bean) {
	checkParameters(bean.getSender(), bean.getRecipient(), bean.getSubject(), bean.getWhenReceived());

	this.setSender(bean.getSender());
	this.setRecipient(bean.getRecipient());
	this.setSubject(bean.getSubject());
	this.setWhenReceived(bean.getWhenReceived());
    }

    public CorrespondenceEntryBean createBean() {
	return new CorrespondenceEntryBean(this);
    }

    @Service
    public void delete() {
	this.setState(CorrespondenceEntryState.DELETED);

    }

    @Service
    public void associateDocument(Document document) {
	if (document == null)
	    throw new DomainException("error.mailtracking.associate.document.is.null");

	this.addDocuments(document);
    }
}
