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

public class CorrespondenceEntry extends CorrespondenceEntry_Base {

    public static final Comparator<CorrespondenceEntry> SORT_BY_WHEN_RECEIVED_COMPARATOR = new Comparator<CorrespondenceEntry>() {

	@Override
	public int compare(CorrespondenceEntry entryLeft, CorrespondenceEntry entryRight) {
	    return entryLeft.getWhenReceived().compareTo(entryRight.getWhenReceived());
	}

    };

    CorrespondenceEntry() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    CorrespondenceEntry(MailTracking mailTracking, String sender, String recipient, String subject, DateTime whenReceived,
	    CorrespondenceType type) {
	this();
	init(mailTracking, sender, recipient, subject, whenReceived, type);
    }

    protected void init(MailTracking mailTracking, String sender, String recipient, String subject, DateTime whenReceived,
	    CorrespondenceType type) {
	checkParameters(mailTracking, sender, recipient, subject, whenReceived, type);

	setState(CorrespondenceEntryState.ACTIVE);
	setSender(sender);
	setRecipient(recipient);
	setSubject(subject);
	setWhenReceived(whenReceived);
	setType(type);
	setMailTracking(mailTracking);
	setEntryNumber(mailTracking.getNextEntryNumber(type));
    }

    private void checkParameters(MailTracking mailTracking, String sender, String recipient, String subject,
	    DateTime whenReceived, CorrespondenceType type) {
	if (StringUtils.isEmpty(sender))
	    throw new DomainException("error.correspondence.entry.sender.cannot.be.empty");

	if (StringUtils.isEmpty(recipient))
	    throw new DomainException("error.correspondence.entry.recipient.cannot.be.empty");

	if (StringUtils.isEmpty(subject))
	    throw new DomainException("error.correspondence.entry.subject.cannot.be.empty");

	if (whenReceived == null)
	    throw new DomainException("error.correspondence.entry.when.received.cannot.be.empty");

	if (type == null)
	    throw new DomainException("error.correspondence.entry.type.cannot.be.empty");

	if (mailTracking == null)
	    throw new DomainException("error.correspondence.entry.mail.tracking.cannot.be.empty");
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

    public static java.util.List<CorrespondenceEntry> getActiveEntries(final CorrespondenceType type) {
	java.util.List<CorrespondenceEntry> allEntries = MyOrg.getInstance().getCorrespondenceEntries();
	java.util.List<CorrespondenceEntry> activeEntries = new java.util.ArrayList<CorrespondenceEntry>();

	CollectionUtils.select(allEntries, new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
		return CorrespondenceEntryState.ACTIVE.equals(entry.getState()) && (type == null || type.equals(entry.getType()));
	    }

	}, activeEntries);

	return activeEntries;
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
	private DateTime whenSent;
	private String senderLetterNumber;
	private String dispathToWhom;

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

	public DateTime getWhenSent() {
	    return whenSent;
	}

	public void setWhenSent(DateTime whenSent) {
	    this.whenSent = whenSent;
	}

	public String getSenderLetterNumber() {
	    return senderLetterNumber;
	}

	public void setSenderLetterNumber(String senderLetterNumber) {
	    this.senderLetterNumber = senderLetterNumber;
	}

	public String getDispathToWhom() {
	    return dispathToWhom;
	}

	public void setDispathToWhom(String dispathToWhom) {
	    this.dispathToWhom = dispathToWhom;
	}

    }

    @Service
    public void edit(CorrespondenceEntryBean bean) {
	checkParameters(this.getMailTracking(), bean.getSender(), bean.getRecipient(), bean.getSubject(), bean.getWhenReceived(),
		this.getType());

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
