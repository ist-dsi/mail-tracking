package module.mailtracking.domain;

import java.util.Collections;
import java.util.Comparator;

import module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum;
import module.organization.domain.Person;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.User;
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

    public static final Comparator<CorrespondenceEntry> SORT_BY_WHEN_SENT_COMPARATOR = new Comparator<CorrespondenceEntry>() {

	@Override
	public int compare(CorrespondenceEntry entryLeft, CorrespondenceEntry entryRight) {
	    return entryLeft.getWhenSent().compareTo(entryRight.getWhenSent());
	}
    };

    public static final Comparator<CorrespondenceEntry> SORT_BY_REFERENCE_COMPARATOR = new Comparator<CorrespondenceEntry>() {

	@Override
	public int compare(CorrespondenceEntry left, CorrespondenceEntry right) {
	    String yearLeft = left.getReference().substring(0, 4);
	    String yearRight = right.getReference().substring(0, 4);

	    if (yearLeft.compareTo(yearRight) == 0) {
		String codeLeft = left.getReference().substring(5);
		String codeRight = right.getReference().substring(5);

		return new Integer(codeLeft).compareTo(new Integer(codeRight));
	    }

	    return yearLeft.compareTo(yearRight);
	}
    };

    CorrespondenceEntry() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    CorrespondenceEntry(final MailTracking mailTracking, final CorrespondenceEntryBean bean, final CorrespondenceType type,
	    final Long entryNumber) {
	this();
	setCreationDate(new DateTime());
	setEditionDate(new DateTime());
	setCreator(UserView.getCurrentUser());
	setLastEditor(UserView.getCurrentUser());
	init(mailTracking, bean, type, entryNumber);
    }

    protected void init(final MailTracking mailTracking, final CorrespondenceEntryBean bean, final CorrespondenceType type,
	    final Long entryNumber) {
	setState(CorrespondenceEntryState.ACTIVE);
	setType(type);
	setMailTracking(mailTracking);
	setEntryNumber(entryNumber);
	setOwner(bean.getOwner());
	setVisibility(bean.getVisibility().getCustomEnum());
	setObservations(bean.getObservations());

	if (CorrespondenceType.SENT.equals(type)) {
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	    this.setRecipient(bean.getRecipient());
	    this.setSubject(bean.getSubject());
	    this.setSender(bean.getSender());
	    this.setYear(mailTracking.getYearFor(bean.getWhenSentAsDateTime()));
	    this.setReference(String.format("%s/%s", this.getYear().getName(), this.getYear().nextSentEntryNumber()));
	} else if (CorrespondenceType.RECEIVED.equals(type)) {
	    this.setWhenReceived(bean.getWhenReceivedAsDateTime());
	    this.setSender(bean.getSender());
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	    this.setSenderLetterNumber(bean.getSenderLetterNumber());
	    this.setSubject(bean.getSubject());
	    this.setRecipient(bean.getRecipient());
	    this.setDispatchedToWhom(bean.getDispatchedToWhom());
	    this.setYear(mailTracking.getYearFor(bean.getWhenReceivedAsDateTime()));
	    this.setReference(String.format("%s/%s", this.getYear().getName(), this.getYear().nextRecievedEntryNumber()));
	}

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

	if (this.getState() == null)
	    throw new DomainException("error.correspondence.entry.state.cannot.be.empty");

	if (this.getEntryNumber() == null)
	    throw new DomainException("error.correspondence.entry.entry.number.cannot.be.empty");

	if (CorrespondenceType.SENT.equals(this.getType())) {
	    if (this.getWhenSent() == null)
		throw new DomainException("error.correspondence.entry.when.sent.cannot.be.empty");
	}

	if (CorrespondenceType.RECEIVED.equals(this.getType())) {
	    if (this.getWhenReceived() == null)
		throw new DomainException("error.correspondence.entry.when.received.cannot.be.empty");
	}

	if (this.getCreationDate() == null)
	    throw new DomainException("error.correspondence.entry.creation.date.cannot.be.empty");

	if (this.getEditionDate() == null)
	    throw new DomainException("error.correspondence.entry.edtion.date.cannot.be.empty");

	if (this.getCreator() == null)
	    throw new DomainException("error.correspondence.entry.creator.cannot.be.empty");

	if (this.getLastEditor() == null)
	    throw new DomainException("error.correspondence.entry.last.editor.cannot.be.empty");

	if (this.getVisibility() == null)
	    throw new DomainException("error.correspondence.entry.visibility.cannot.be.empty");

	if (StringUtils.isEmpty(this.getReference())) {
	    throw new DomainException("error.correspondence.entry.reference.cannot.be.empty");
	}

	if ("\\d{4}/\\d+".matches(this.getReference())) {
	    throw new DomainException("error.correspondence.entry.reference.invalid");
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
	private String dispatchedToWhom;
	private Boolean privateEntry;
	private Person owner;

	private CorrespondenceEntry entry;

	private MailTracking mailTracking;

	private CustomEnum visibility;

	private String deletionReason;

	private String observations;

	public CorrespondenceEntryBean(MailTracking mailTracking) {
	    this.setMailTracking(mailTracking);
	    this.setVisibility(new CustomEnum(CorrespondenceEntryVisibility.TO_PUBLIC));
	}

	public CorrespondenceEntryBean(CorrespondenceEntry entry) {
	    this.setOwner(entry.getOwner());
	    this.setVisibility(new CustomEnum(entry.getVisibility()));
	    this.setMailTracking(entry.getMailTracking());
	    this.setEntry(entry);
	    this.setObservations(entry.getObservations());

	    if (CorrespondenceType.SENT.equals(entry.getType())) {
		if (entry.getWhenSent() != null)
		    this.setWhenSent(new LocalDate(entry.getWhenSent().getYear(), entry.getWhenSent().getMonthOfYear(), entry
			    .getWhenSent().getDayOfMonth()));
		this.setRecipient(entry.getRecipient());
		this.setSubject(entry.getSubject());
		this.setSender(entry.getSender());
	    } else if (CorrespondenceType.RECEIVED.equals(entry.getType())) {
		if (entry.getWhenReceived() != null)
		    this.setWhenReceived(new LocalDate(entry.getWhenReceived().getYear(), entry.getWhenReceived()
			    .getMonthOfYear(), entry.getWhenReceived().getDayOfMonth()));
		this.setSender(entry.getSender());
		if (entry.getWhenSent() != null)
		    this.setWhenSent(new LocalDate(entry.getWhenSent().getYear(), entry.getWhenSent().getMonthOfYear(), entry
			    .getWhenSent().getDayOfMonth()));
		this.setSenderLetterNumber(entry.getSenderLetterNumber());
		this.setSubject(entry.getSubject());
		this.setRecipient(entry.getRecipient());
		this.setDispatchedToWhom(entry.getDispatchedToWhom());
	    }

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

	public String getDispatchedToWhom() {
	    return dispatchedToWhom;
	}

	public void setDispatchedToWhom(String dispathToWhom) {
	    this.dispatchedToWhom = dispathToWhom;
	}

	public DateTime getWhenReceivedAsDateTime() {
	    return this.getWhenReceived() != null ? new DateTime(this.getWhenReceived().getYear(), this.getWhenReceived()
		    .getMonthOfYear(), this.getWhenReceived().getDayOfMonth(), 0, 0, 0, 0) : null;
	}

	public DateTime getWhenSentAsDateTime() {
	    return this.getWhenSent() != null ? new DateTime(this.getWhenSent().getYear(), this.getWhenSent().getMonthOfYear(),
		    this.getWhenSent().getDayOfMonth(), 0, 0, 0, 0) : null;
	}

	public Boolean getPrivateEntry() {
	    return privateEntry;
	}

	public void setPrivateEntry(Boolean privateEntry) {
	    this.privateEntry = privateEntry;
	}

	public Person getOwner() {
	    return this.owner;
	}

	public void setOwner(Person owner) {
	    this.owner = owner;
	}

	public MailTracking getMailTracking() {
	    return this.mailTracking;
	}

	public void setMailTracking(MailTracking mailTracking) {
	    this.mailTracking = mailTracking;
	}

	public CustomEnum getVisibility() {
	    return this.visibility;
	}

	public void setVisibility(CustomEnum visibility) {
	    this.visibility = visibility;
	}

	public String getDeletionReason() {
	    return this.deletionReason;
	}

	public void setDeletionReason(String value) {
	    this.deletionReason = value;
	}

	public String getObservations() {
	    return this.observations;
	}

	public void setObservations(String value) {
	    this.observations = value;
	}
    }

    void edit(CorrespondenceEntryBean bean) {
	this.setEditionDate(new DateTime());
	this.setLastEditor(UserView.getCurrentUser());
	this.setVisibility(bean.getVisibility().getCustomEnum());
	setOwner(bean.getOwner());
	setObservations(bean.getObservations());

	if (CorrespondenceType.SENT.equals(this.getType())) {
	    this.setSender(bean.getSender());
	    this.setRecipient(bean.getRecipient());
	    this.setSubject(bean.getSubject());
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	    this.setYear(this.getMailTracking().getYearFor(bean.getWhenSentAsDateTime()));
	} else if (CorrespondenceType.RECEIVED.equals(this.getType())) {
	    this.setWhenReceived(bean.getWhenReceivedAsDateTime());
	    this.setSender(bean.getSender());
	    this.setWhenSent(bean.getWhenSentAsDateTime());
	    this.setSenderLetterNumber(bean.getSenderLetterNumber());
	    this.setSubject(bean.getSubject());
	    this.setRecipient(bean.getRecipient());
	    this.setDispatchedToWhom(bean.getDispatchedToWhom());
	    this.setYear(this.getMailTracking().getYearFor(bean.getWhenReceivedAsDateTime()));
	}

	checkParameters();
    }

    public CorrespondenceEntryBean createBean() {
	return new CorrespondenceEntryBean(this);
    }

    @Service
    public void delete(String reason) {
	if (StringUtils.isEmpty(reason))
	    throw new DomainException("error.mailtracking.deletion.reason.cannot.be.null");

	this.setState(CorrespondenceEntryState.DELETED);
	this.setDeletionReason(reason);
	this.setDeletionResponsible(UserView.getCurrentUser());
	this.setDeletionDate(new DateTime());
    }

    @Service
    public void associateDocument(Document document) {
	if (document == null)
	    throw new DomainException("error.mailtracking.associate.document.is.null");

	this.addDocuments(document);
    }

    public java.util.List<Document> getActiveDocuments() {
	java.util.List<Document> associatedDocuments = new java.util.ArrayList<Document>();

	CollectionUtils.select(this.getDocuments(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		return DocumentState.ACTIVE.equals(((Document) arg0).getState());
	    }

	}, associatedDocuments);

	return associatedDocuments;
    }

    public boolean isUserAbleToView(User user) {
	return this.getVisibility().isUserAbleToView(this, user);
    }

    public boolean isUserAbleToView() {
	return isUserAbleToView(UserView.getCurrentUser());
    }

    public boolean isUserAbleToEdit(final User user) {
	return this.getVisibility().isUserAbleToEdit(this, user);
    }

    public boolean isUserAbleToEdit() {
	return this.isUserAbleToEdit(UserView.getCurrentUser());
    }

    public boolean isUserAbleToDelete() {
	return this.isUserAbleToDelete(UserView.getCurrentUser());
    }

    public boolean isUserAbleToDelete(final User user) {
	return this.getVisibility().isUserAbleToDelete(this, user);
    }

    public boolean isUserAbleToViewMainDocument(final User user) {
	return this.isUserAbleToView() && this.hasMainDocument();
    }

    public Document getMainDocument() {
	return (Document) CollectionUtils.find(this.getDocuments(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		return DocumentType.MAIN_DOCUMENT.equals(((Document) arg0).getType());
	    }

	});
    }

    public boolean hasMainDocument() {
	return this.getMainDocument() != null;
    }

    public void reIndexByYear() {
	if (CorrespondenceType.SENT.equals(this.getType())) {
	    this.setYear(this.getMailTracking().getYearFor(getWhenSent()));
	} else if (CorrespondenceType.RECEIVED.equals(this.getType())) {
	    this.setYear(this.getMailTracking().getYearFor(getWhenReceived()));
	} else {
	    throw new DomainException("error.mail.tracking.correspondence.type.invalid");
	}
    }
}
