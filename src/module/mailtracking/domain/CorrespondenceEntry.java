package module.mailtracking.domain;

import java.util.Collections;
import java.util.Comparator;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    CorrespondenceEntry(MailTracking mailTracking, CorrespondenceEntryBean bean) {
	this();
	init(mailTracking, bean.getSender(), bean.getRecipient(), bean.getSubject(), bean.getWhenReceivedAsDateTime(), bean
		.getWhenSentAsDateTime(), bean.getSenderLetterNumber(), bean.getDispathToWhom(), this.getType());
    }

    protected void init(MailTracking mailTracking, String sender, String recipient, String subject, DateTime whenReceived,
	    DateTime whenSent, String senderLetterNumber, String dispatchedToWhom, CorrespondenceType type) {
	setState(CorrespondenceEntryState.ACTIVE);
	setSender(sender);
	setRecipient(recipient);
	setSubject(subject);
	setWhenReceived(whenReceived);
	setWhenSent(whenSent);
	setSenderLetterNumber(senderLetterNumber);
	setDispatchedToWhom(dispatchedToWhom);
	setType(type);
	setMailTracking(mailTracking);
	setEntryNumber(mailTracking.getNextEntryNumber(type));

	this.checkParameters();

    }

    private void checkParameters() {
	if (StringUtils.isEmpty(this.getSender()))
	    throw new DomainException("error.correspondence.entry.sender.cannot.be.empty");

	if (StringUtils.isEmpty(this.getRecipient()))
	    throw new DomainException("error.correspondence.entry.recipient.cannot.be.empty");

	if (StringUtils.isEmpty(this.getSubject()))
	    throw new DomainException("error.correspondence.entry.subject.cannot.be.empty");

	if (this.getType() == null)
	    throw new DomainException("error.correspondence.entry.type.cannot.be.empty");

	if (this.getMailTracking() == null)
	    throw new DomainException("error.correspondence.entry.mail.tracking.cannot.be.empty");

	if (CorrespondenceType.SENT.equals(this.getType())) {
	    if (this.getWhenSent() == null)
		throw new DomainException("error.correspondence.entry.when.sent.cannot.be.empty");
	}

	if (CorrespondenceType.RECEIVED.equals(this.getType())) {
	    if (this.getWhenReceived() == null)
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
	private LocalDate whenReceived;
	private LocalDate whenSent;
	private String senderLetterNumber;
	private String dispathToWhom;

	private CorrespondenceEntry entry;

	public CorrespondenceEntryBean() {

	}

	public CorrespondenceEntryBean(CorrespondenceEntry entry) {
	    this.setSender(entry.getSender());
	    this.setRecipient(entry.getRecipient());
	    this.setSubject(entry.getSubject());
	    this.setWhenReceived(new LocalDate(entry.getWhenReceived().getYear(), entry.getWhenReceived().getMonthOfYear(), entry
		    .getWhenReceived().getDayOfMonth()));
	    this.setWhenSent(new LocalDate(entry.getWhenReceived().getYear(), entry.getWhenReceived().getMonthOfYear(), entry
		    .getWhenReceived().getDayOfMonth()));
	    this.setDispathToWhom(entry.getDispatchedToWhom());
	    this.setSenderLetterNumber(this.getSenderLetterNumber());
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

	public LocalDate getWhenReceived() {
	    return whenReceived;
	}

	public void setWhenReceived(LocalDate whenReceived) {
	    this.whenReceived = whenReceived;
	}

	public CorrespondenceEntry getEntry() {
	    return entry;
	}

	public void setEntry(CorrespondenceEntry entry) {
	    this.entry = entry;
	}

	public LocalDate getWhenSent() {
	    return whenSent;
	}

	public void setWhenSent(LocalDate whenSent) {
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

	public DateTime getWhenReceivedAsDateTime() {
	    return this.getWhenReceived() != null ? new DateTime(this.getWhenReceived().getYear(), this.getWhenReceived()
		    .getMonthOfYear(), this.getWhenReceived().getDayOfMonth(), 0, 0, 0, 0) : null;
	}

	public DateTime getWhenSentAsDateTime() {
	    return this.getWhenSent() != null ? new DateTime(this.getWhenSent().getYear(), this.getWhenSent().getMonthOfYear(),
		    this.getWhenReceived().getDayOfMonth(), 0, 0, 0, 0) : null;
	}

    }

    @Service
    public void edit(CorrespondenceEntryBean bean) {
	if (CorrespondenceType.SENT.equals(this.getType())) {
	    this.setSender(bean.getSender());
	    this.setRecipient(bean.getRecipient());
	    this.setSubject(bean.getSubject());
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	} else if (CorrespondenceType.RECEIVED.equals(this.getType())) {
	    this.setWhenReceived(bean.getWhenReceivedAsDateTime());
	    this.setSender(bean.getSender());
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	    this.setSenderLetterNumber(bean.getSenderLetterNumber());
	    this.setSubject(bean.getSubject());
	    this.setRecipient(bean.getRecipient());
	    this.setDispatchedToWhom(bean.getDispathToWhom());
	}

	checkParameters();
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
