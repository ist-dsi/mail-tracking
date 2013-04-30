/*
 * @(#)MailTracking.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Anil Kassamali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Correspondence Registry Module.
 *
 *   The Correspondence Registry Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Correspondence Registry Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Correspondence Registry Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.mailtracking.domain;

import java.util.Collections;
import java.util.Comparator;

import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.organization.domain.Person;
import module.organization.domain.Unit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.domain.groups.NamedGroup;
import pt.ist.bennu.core.domain.groups.People;
import pt.ist.fenixframework.Atomic;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author Anil Kassamali
 * 
 */
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
        this.setVirtualHost(VirtualHost.getVirtualHostForThread());
    }

    private void checkParameters(Unit unit) {
        if (unit == null) {
            throw new DomainException("error.mail.tracking.unit.cannot.be.empty");
        }
    }

    @Atomic
    public static MailTracking createMailTracking(Unit unit) {
        if (unit.getMailTracking() != null) {
            throw new DomainException("error.mail.tracking.exists.for.unit");
        }

        MailTracking mailTracking = new MailTracking(unit);

        People operators = new NamedGroup("operators");
        operators.addUsers(UserView.getCurrentUser());
        mailTracking.setOperatorsGroup(operators);

        People managers = new NamedGroup("managers");
        managers.addUsers(UserView.getCurrentUser());
        mailTracking.setManagersGroup(managers);

        People viewers = new NamedGroup("viewers");
        for (Person person : unit.getChildPersons()) {
            if (person.getUser() != null) {
                viewers.addUsers(person.getUser());
            }
        }
        viewers.addUsers(UserView.getCurrentUser());
        mailTracking.setViewersGroup(viewers);

        return mailTracking;
    }

    @Atomic
    public void removeOperator(final User user) {
        ((People) this.getOperatorsGroup()).removeUsers(user);
    }

    @Atomic
    public void addOperator(final User user) {
        ((People) this.getOperatorsGroup()).addUsers(user);
    }

    @Atomic
    public void addViewer(final User user) {
        ((People) this.getViewersGroup()).addUsers(user);
    }

    @Atomic
    public void removeViewer(final User user) {
        ((People) this.getViewersGroup()).removeUsers(user);
    }

    @Atomic
    public void addManager(final User user) {
        ((People) this.getManagersGroup()).addUsers(user);
    }

    @Atomic
    public void removeManager(final User user) {
        ((People) this.getManagersGroup()).removeUsers(user);
    }

    @Atomic
    public void edit(MailTrackingBean bean) {

        this.setName(bean.getName());
        this.setActive(bean.getActive());
    }

    public java.util.List<CorrespondenceEntry> getActiveEntries(final CorrespondenceType type) {
        return this.getEntries(CorrespondenceEntryState.ACTIVE, type);
    }

    public java.util.List<CorrespondenceEntry> getDeletedEntries(final CorrespondenceType type) {
        return this.getEntries(CorrespondenceEntryState.DELETED, type);
    }

    public java.util.List<CorrespondenceEntry> getAbleToViewEntries(final CorrespondenceType type, boolean onlyActiveEntries) {
        java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

        java.util.List<CorrespondenceEntry> baseEntries = onlyActiveEntries ? getActiveEntries(type) : getAnyStateEntries(type);

        CollectionUtils.select(baseEntries, new Predicate() {

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

    java.util.List<CorrespondenceEntry> getEntries(final CorrespondenceEntryState state, final CorrespondenceType type) {
        return filterEntriesByTypeAndState(this.getEntriesSet(), state, type);
    }

    static java.util.List<CorrespondenceEntry> filterEntriesByTypeAndState(
            final java.util.Collection<CorrespondenceEntry> entries, final CorrespondenceEntryState state,
            final CorrespondenceType type) {
        java.util.List<CorrespondenceEntry> activeEntries = new java.util.ArrayList<CorrespondenceEntry>();

        CollectionUtils.select(entries, new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                CorrespondenceEntry entry = (CorrespondenceEntry) arg0;
                return (state == null || state.equals(entry.getState())) && (type == null || type.equals(entry.getType()));
            }

        }, activeEntries);

        return activeEntries;
    }

    public java.util.List<CorrespondenceEntry> simpleSearch(CorrespondenceType type, final String key, boolean onlyActiveEntries) {
        java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

        if (StringUtils.isEmpty(key)) {
            return entries;
        }

        CollectionUtils.select(this.getAbleToViewEntries(type, onlyActiveEntries), new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                return Helper.matchGivenSearchToken((CorrespondenceEntry) arg0, key);
            }

        }, entries);

        return entries;
    }

    @Atomic
    public CorrespondenceEntry createNewEntry(CorrespondenceEntryBean bean, CorrespondenceType type, Document mainDocument)
            throws Exception {

        CorrespondenceEntry entry = new CorrespondenceEntry(this, bean, type, this.getNextEntryNumber(type));

        if (mainDocument != null) {
            entry.addDocuments(mainDocument);
        }

        return entry;
    }

    @Atomic
    public CorrespondenceEntry editEntry(CorrespondenceEntryBean bean) {
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

        if (entries.isEmpty()) {
            return 1L;
        }

        Collections.sort(entries, SORT_BY_ENTRY_NUMBER);

        return entries.get(entries.size() - 1).getEntryNumber() + 1;
    }

    public static java.util.List<MailTracking> getMailTrackingsWhereUserHasSomeRole(final User user) {

        if (user.getPerson() == null && user.hasRoleType(RoleType.MANAGER)) {
            return MailTrackingsOnVirtualHost.retrieve();
        }

        java.util.List<MailTracking> unitsWithMailTrackings = new java.util.ArrayList<MailTracking>();
        CollectionUtils.select(MailTrackingsOnVirtualHost.retrieve(), new Predicate() {

            @Override
            public boolean evaluate(Object arg0) {
                return ((MailTracking) arg0).isUserWithSomeRoleOnThisMailTracking(user);
            }

        }, unitsWithMailTrackings);

        return unitsWithMailTrackings;
    }

    protected static boolean isUserViewerOfMailTracking(Unit unit, User user) {
        return unit.getMailTracking() != null && unit.getMailTracking().isUserViewer(user);
    }

    protected static Boolean isUserOperatorOfMailTracking(Unit unit, User user) {
        return unit.getMailTracking() != null && unit.getMailTracking().isUserOperator(user);
    }

    protected static Boolean isUserManagerOfMailTracking(Unit unit, User user) {
        return unit.getMailTracking() != null && unit.getMailTracking().isUserManager(user);
    }

    public boolean isUserManager(User user) {
        return this.getManagersGroup().isMember(user);
    }

    public boolean isUserOperator(User user) {
        return this.getOperatorsGroup().isMember(user);
    }

    public boolean isUserViewer(User user) {
        return this.getViewersGroup().isMember(user);
    }

    public static boolean isMyOrgManager(final User user) {
        return user.hasRoleType(RoleType.MANAGER);
    }

    public boolean isCurrentUserOperator() {
        return this.isUserOperator(UserView.getCurrentUser());
    }

    public boolean isCurrentUserViewer() {
        return this.isUserViewer(UserView.getCurrentUser());
    }

    public boolean isCurrentUserManager() {
        return this.isUserManager(UserView.getCurrentUser());
    }

    public boolean isUserAbleToCreateEntries(final User user) {
        return this.isUserOperator(user) || this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToCreateEntries() {
        return this.isUserAbleToCreateEntries(UserView.getCurrentUser());
    }

    public boolean isUserAbleToImportEntries(final User user) {
        return this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToImportEntries() {
        return this.isUserAbleToImportEntries(UserView.getCurrentUser());
    }

    public boolean isUserAbleToManageViewers(final User user) {
        return this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToManageViewers() {
        return isUserAbleToManageViewers(UserView.getCurrentUser());
    }

    public boolean isUserAbleToManageOperators(final User user) {
        return this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToManageOperators() {
        return this.isUserAbleToManageOperators(UserView.getCurrentUser());
    }

    public boolean isUserAbleToManageManagers(final User user) {
        return isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToManageManagers() {
        return isUserAbleToManageManagers(UserView.getCurrentUser());
    }

    public static boolean isUserAbleToCreateMailTrackingModule(final User user) {
        return isMyOrgManager(user);
    }

    public static boolean isCurrentUserAbleToCreateMailTrackingModule() {
        return isUserAbleToCreateMailTrackingModule(UserView.getCurrentUser());
    }

    public boolean isUserAbleToEditMailTrackingAttributes(final User user) {
        return isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToEditMailTrackingAttributes() {
        return this.isUserAbleToEditMailTrackingAttributes(UserView.getCurrentUser());
    }

    public boolean isUserAbleToViewMailTracking(final User user) {
        return this.isUserViewer(user) || this.isUserOperator(user) || this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToViewMailTracking() {
        return this.isUserAbleToViewMailTracking(UserView.getCurrentUser());
    }

    public boolean isUserAbleToManageUsers(final User user) {
        return this.isUserAbleToManageViewers(user) || this.isUserAbleToManageOperators(user)
                || this.isUserAbleToManageManagers(user);
    }

    public boolean isCurrentUserAbleToManageUsers() {
        return this.isUserAbleToManageUsers(UserView.getCurrentUser());
    }

    public boolean isUserWithSomeRoleOnThisMailTracking(final User user) {
        return this.isUserViewer(user) || this.isUserOperator(user) || this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserWithSomeRoleOnThisMailTracking() {
        return isUserWithSomeRoleOnThisMailTracking(UserView.getCurrentUser());
    }

    public boolean isUserAbleToRearrangeEntries(final User user) {
        return isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToRearrangeEntries() {
        return isUserAbleToRearrangeEntries(UserView.getCurrentUser());
    }

    public boolean hasUserOnlyViewOrEditionOperations(final User user) {
        return (this.isUserAbleToCreateEntries(user) || this.isUserAbleToViewMailTracking(user))
                && !this.isUserAbleToCreateEntries(user) && !this.isUserAbleToImportEntries(user)
                && !this.isUserAbleToManageUsers(user);
    }

    public boolean isCurrentUserAbleToManageYears() {
        return isUserAbleToManageYears(UserView.getCurrentUser());
    }

    public boolean isUserAbleToManageYears(final User user) {
        return this.isUserManager(user) || isMyOrgManager(user);
    }

    public boolean hasCurrentUserOnlyViewOrEditionOperations() {
        return hasUserOnlyViewOrEditionOperations(UserView.getCurrentUser());
    }

    public boolean isUserAbleToSetReferenceCounters(final User user) {
        return isUserOperator(user) || isUserManager(user) || isMyOrgManager(user);
    }

    public boolean isCurrentUserAbleToSetReferenceCounters() {
        return isUserAbleToSetReferenceCounters(UserView.getCurrentUser());
    }

    public Integer getTotalNumberOfSentEntries() {
        return this.getAnyStateEntries(CorrespondenceType.SENT).size();
    }

    public Integer getTotalNumberOfActiveSentEntries() {
        return this.getActiveEntries(CorrespondenceType.SENT).size();
    }

    public Integer getTotalNumberOfDeletedSentEntries() {
        return this.getDeletedEntries(CorrespondenceType.SENT).size();
    }

    public Integer getTotalNumberOfReceivedEntries() {
        return this.getAnyStateEntries(CorrespondenceType.RECEIVED).size();
    }

    public Integer getTotalNumberOfActiveReceivedEntries() {
        return this.getActiveEntries(CorrespondenceType.RECEIVED).size();
    }

    public Integer getTotalNumberOfDeletedReceivedEntries() {
        return this.getDeletedEntries(CorrespondenceType.RECEIVED).size();
    }

    public Integer getTotalNumberOfActiveDocuments() {
        return this.getTotalDocuments().size();
    }

    public java.util.List<Document> getTotalDocuments() {
        java.util.Collection<CorrespondenceEntry> entries = this.getEntriesSet();

        java.util.List<Document> allDocuments = new java.util.ArrayList<Document>();

        for (CorrespondenceEntry entry : entries) {
            allDocuments.addAll(entry.getDocumentsSet());
        }

        return allDocuments;
    }

    public java.util.Set<User> getTotalUsers() {
        java.util.Set<User> allUsers = new java.util.HashSet<User>();

        allUsers.addAll(this.getViewersGroup().getMembers());
        allUsers.addAll(this.getOperatorsGroup().getMembers());
        allUsers.addAll(this.getManagersGroup().getMembers());

        return allUsers;
    }

    public Year getYearFor(final Integer forYear) {
        return Year.getYearFor(this, forYear);
    }

    Year getYearFor(final DateTime date) {
        return Year.getYearFor(this, date);
    }

    @Atomic
    public Year createYearFor(Integer year) {
        return Year.createYearFor(this, year);
    }

    @Atomic
    public void reIndexEntriesByYear() {
        for (CorrespondenceEntry entry : this.getEntriesSet()) {
            entry.reIndexByYear();
        }
    }

    public Year getCurrentYear() {
        for (Year year : getYearsSet()) {
            if (year.isBetween(new DateTime())) {
                return year;
            }
        }

        return null;
    }

    public static MailTracking readMailTrackingByName(String name) {
        for (MailTracking mailTracking : MailTrackingsOnVirtualHost.retrieve()) {
            if (name.equals(mailTracking.getName().getContent(Language.pt))
                    || name.equals(mailTracking.getName().getContent(Language.en))) {
                return mailTracking;
            }
        }

        return null;
    }

    @Override
    public boolean isConnectedToCurrentHost() {
        VirtualHost virtualHostForThread = VirtualHost.getVirtualHostForThread();

        return getVirtualHost() == virtualHostForThread;
    }
}
