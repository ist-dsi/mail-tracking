package module.mailtracking.domain;

import java.util.Collections;
import java.util.Comparator;

import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.organization.domain.Person;
import module.organization.domain.Unit;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.MyOrg;
import myorg.domain.RoleType;
import myorg.domain.User;
import myorg.domain.exceptions.DomainException;
import myorg.domain.groups.NamedGroup;
import myorg.domain.groups.People;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.security.accessControl.Checked;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.StringNormalizer;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class MailTracking extends MailTracking_Base {

    private MailTracking() {
	super();
	setMyOrg(MyOrg.getInstance());

    }

    private MailTracking(Unit unit) {
	this();
	init(unit);
    }

    private void init(Unit unit) {
	checkParameters(unit);

	this.setUnit(unit);
	this.setName(unit.getPartyName());
	this.setActive(Boolean.TRUE);
    }

    private void checkParameters(Unit unit) {
	if (unit == null)
	    throw new DomainException("error.mail.tracking.unit.cannot.be.empty");
    }

    @Service
    @Checked("xpto.ALTO")
    public static MailTracking createMailTracking(Unit unit) {
	if (!isManager(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	if (unit.hasMailTracking())
	    throw new DomainException("error.mail.tracking.exists.for.unit");

	MailTracking mailTracking = new MailTracking(unit);

	People operators = new NamedGroup("operators");
	operators.addUsers(UserView.getCurrentUser());
	mailTracking.setOperatorsGroup(operators);

	People viewers = new NamedGroup("viewers");
	for (Person person : unit.getChildPersons()) {
	    if (person.hasUser())
		viewers.addUsers(person.getUser());
	}
	viewers.addUsers(UserView.getCurrentUser());

	mailTracking.setViewersGroup(viewers);

	return mailTracking;
    }

    @Service
    public void removeOperator(User user) {
	((People) this.getOperatorsGroup()).removeUsers(user);
    }

    @Service
    public void addOperator(User user) {
	if (!user.getPerson().getParentUnits().contains(this.getUnit()))
	    throw new DomainException("error.mail.tracking.person.not.in.associated.unit");

	((People) this.getOperatorsGroup()).addUsers(user);
    }

    public boolean isUserOperator(User user) {
	return this.getOperatorsGroup().isMember(user);
    }

    @Service
    public void addViewer(User user) {
	((People) this.getViewersGroup()).addUsers(user);
    }

    @Service
    public void removeViewer(User user) {
	((People) this.getViewersGroup()).removeUsers(user);
    }

    public boolean isUserViewer(User user) {
	return this.getViewersGroup().isMember(user);
    }

    @Service
    public void edit(MailTrackingBean bean) {
	if (!isManager(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	this.setName(bean.getName());
	this.setActive(bean.getActive());
    }

    public java.util.List<CorrespondenceEntry> getActiveEntries(final CorrespondenceType type) {
	return this.getEntries(CorrespondenceEntryState.ACTIVE, type);
    }

    public java.util.List<CorrespondenceEntry> getDeletedEntries(final CorrespondenceType type) {
	return this.getEntries(CorrespondenceEntryState.DELETED, type);
    }

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

    public java.util.List<CorrespondenceEntry> getAnyStateEntries(final CorrespondenceType type) {
	return this.getEntries(null, type);
    }

    public java.util.List<CorrespondenceEntry> getEntries(final CorrespondenceEntryState state, final CorrespondenceType type) {
	java.util.List<CorrespondenceEntry> activeEntries = new java.util.ArrayList<CorrespondenceEntry>();

	CollectionUtils.select(this.getEntries(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
		return (state == null || state.equals(entry.getState())) && (type == null || type.equals(entry.getType()));
	    }

	}, activeEntries);

	return activeEntries;
    }

    public java.util.List<CorrespondenceEntry> find(CorrespondenceType type, final String sender, final String recipient,
	    final String subject, final DateTime whenReceivedBegin, final DateTime whenReceivedEnd) {
	java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

	final String normalizedSender = StringNormalizer.normalize(sender);
	final String normalizedRecipient = StringNormalizer.normalize(recipient);
	final String normalizedSubject = StringNormalizer.normalize(subject);

	if (StringUtils.isEmpty(sender) && StringUtils.isEmpty(recipient) && whenReceivedBegin == null && whenReceivedEnd == null)
	    return entries;

	CollectionUtils.select(this.getActiveEntries(type), new Predicate() {

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

    @Service
    public CorrespondenceEntry createNewEntry(CorrespondenceEntryBean bean, CorrespondenceType type, Document mainDocument)
	    throws Exception {
	if (!isUserOperator(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();

	CorrespondenceEntry entry = new CorrespondenceEntry(this, bean, type, this.getNextEntryNumber(type));

	if (mainDocument != null)
	    entry.addDocuments(mainDocument);

	return entry;
    }

    @Service
    public CorrespondenceEntry editEntry(CorrespondenceEntryBean bean) {
	if (!isUserOperator(UserView.getCurrentUser()))
	    throw new PermissionDeniedException();
	bean.getEntry().edit(bean);
	return bean.getEntry();
    }

    public static class MailTrackingBean implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MultiLanguageString name;
	private Boolean active;
	private MailTracking mailTracking;

	public MailTrackingBean(MailTracking mailTracking) {
	    this.mailTracking = mailTracking;
	    this.name = mailTracking.getName();
	    this.active = mailTracking.getActive();
	}

	public MultiLanguageString getName() {
	    return name;
	}

	public void setName(MultiLanguageString name) {
	    this.name = name;
	}

	public Boolean getActive() {
	    return active;
	}

	public void setActive(Boolean active) {
	    this.active = active;
	}

	public MailTracking getMailTracking() {
	    return mailTracking;
	}

	public void setMailTracking(MailTracking mailTracking) {
	    this.mailTracking = mailTracking;
	}

    }

    public MailTrackingBean createBean() {
	return new MailTrackingBean(this);
    }

    public static final Comparator<CorrespondenceEntry> SORT_BY_ENTRY_NUMBER = new Comparator<CorrespondenceEntry>() {

	@Override
	public int compare(CorrespondenceEntry entryLeft, CorrespondenceEntry entryRight) {
	    return entryLeft.getEntryNumber().compareTo(entryRight.getEntryNumber());
	}

    };

    public Long getNextEntryNumber(CorrespondenceType type) {
	java.util.List<CorrespondenceEntry> entries = this.getAnyStateEntries(type);

	if (entries.isEmpty())
	    return 1L;

	Collections.sort(entries, SORT_BY_ENTRY_NUMBER);

	return entries.get(entries.size() - 1).getEntryNumber() + 1;
    }

    public static java.util.List<MailTracking> getMailTrackingsWhereUserIsOperatorOrViewer(final User user) {

	if (user.getPerson() == null && user.hasRoleType(RoleType.MANAGER))
	    return MyOrg.getInstance().getMailTrackings();

	java.util.List<Unit> unitsWithMailTrackings = new java.util.ArrayList<Unit>();
	CollectionUtils.select(user.getPerson().getParentUnits(), new Predicate() {

	    @Override
	    public boolean evaluate(Object arg0) {
		return MailTracking.isUserOperatorOfMailTracking((Unit) arg0, user)
			|| MailTracking.isUserViewerOfMailTracking((Unit) arg0, user);
	    }

	}, unitsWithMailTrackings);

	java.util.List<MailTracking> mailTrackingList = new java.util.ArrayList<MailTracking>();

	for (Unit unit : unitsWithMailTrackings) {
	    mailTrackingList.add(unit.getMailTracking());
	}

	return mailTrackingList;
    }

    protected static boolean isUserViewerOfMailTracking(Unit unit, User user) {
	return unit.getMailTracking() != null && unit.getMailTracking().isUserViewer(user);
    }

    protected static Boolean isUserOperatorOfMailTracking(Unit unit, User user) {
	return unit.getMailTracking() != null && unit.getMailTracking().isUserOperator(user);
    }

    public static boolean isManager(final User user) {
	return user.hasRoleType(RoleType.MANAGER);
    }

    public static boolean isOperator(MailTracking mailtracking, final User user) {
	return mailtracking.isUserOperator(user);
    }

}
