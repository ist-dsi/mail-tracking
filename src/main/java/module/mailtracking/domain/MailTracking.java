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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.organization.domain.Person;
import module.organization.domain.Unit;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class MailTracking extends MailTracking_Base {

    private MailTracking() {
        super();
        setBennu(Bennu.getInstance());
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
        if (unit == null) {
            throw new MailTrackingDomainException("error.mail.tracking.unit.cannot.be.empty");
        }
    }

    public void setOperatorsGroup(Group operatorsGroup) {
        setOperatorsAccessGroup(operatorsGroup.toPersistentGroup());
    }

    public Group getOperatorsGroup() {
        return getOperatorsAccessGroup() == null ? null : getOperatorsAccessGroup().toGroup();
    }

    public void setViewersGroup(Group viewersGroup) {
        setViewersAccessGroup(viewersGroup.toPersistentGroup());
    }

    public Group getViewersGroup() {
        return getViewersAccessGroup() == null ? null : getViewersAccessGroup().toGroup();
    }

    public void setManagersGroup(Group viewersGroup) {
        setManagersAccessGroup(viewersGroup.toPersistentGroup());
    }

    public Group getManagersGroup() {
        return getManagersAccessGroup() == null ? null : getManagersAccessGroup().toGroup();
    }

    @Atomic
    public static MailTracking createMailTracking(Unit unit) {
        if (unit.getMailTracking() != null) {
            throw new MailTrackingDomainException("error.mail.tracking.exists.for.unit");
        }

        User authenticatedUser = Authenticate.getUser();
        MailTracking mailTracking = new MailTracking(unit);

        mailTracking.setOperatorsGroup(Group.nobody());
        mailTracking.addOperator(authenticatedUser);

        mailTracking.setManagersGroup(Group.nobody());
        mailTracking.addManager(authenticatedUser);

        mailTracking.setViewersGroup(Group.nobody());
        mailTracking.addViewer(authenticatedUser);

        unit.getChildAccountabilityStream().map(a -> a.getChild()).filter(p -> p.isPerson()).map(p -> (Person) p)
                .map(p -> p.getUser()).filter(u -> u != null).forEach(u -> mailTracking.addViewer(u));;

        return mailTracking;
    }

    @Atomic
    public void removeOperator(final User user) {
        setOperatorsGroup(getOperatorsGroup().revoke(user));
    }

    @Atomic
    public void addOperator(final User user) {
        setOperatorsGroup(getOperatorsGroup().grant(user));
    }

    @Atomic
    public void addViewer(final User user) {
        setViewersGroup(getViewersGroup().grant(user));
    }

    @Atomic
    public void removeViewer(final User user) {
        setViewersGroup(getViewersGroup().revoke(user));
    }

    @Atomic
    public void addManager(final User user) {
        setManagersGroup(getManagersGroup().grant(user));
    }

    @Atomic
    public void removeManager(final User user) {
        setManagersGroup(getManagersGroup().revoke(user));
    }

    @Atomic
    public void edit(MailTrackingBean bean) {

        this.setName(bean.getName());
        this.setActive(bean.getActive());
    }

    public int countActiveEntries(final CorrespondenceType type) {
        return this.countEntries(CorrespondenceEntryState.ACTIVE, type);
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
                return ((CorrespondenceEntry) arg0).isUserAbleToView(Authenticate.getUser());
            }

        }, entries);

        return entries;
    }

    public java.util.List<CorrespondenceEntry> getAnyStateEntries(final CorrespondenceType type) {
        return this.getEntries(null, type);
    }

    int countEntries(final CorrespondenceEntryState state, final CorrespondenceType type) {
        return countEntriesByTypeAndState(this.getEntriesSet(), state, type);
    }

    java.util.List<CorrespondenceEntry> getEntries(final CorrespondenceEntryState state, final CorrespondenceType type) {
        return filterEntriesByTypeAndState(this.getEntriesSet(), state, type);
    }

    static int countEntriesByTypeAndState(final java.util.Collection<CorrespondenceEntry> entries,
            final CorrespondenceEntryState state, final CorrespondenceType type) {
        int result = 0;
        for (final CorrespondenceEntry entry : entries) {
            if ((state == null || state.equals(entry.getState())) && (type == null || type.equals(entry.getType()))) {
                result++;
            }
        }
        return result;
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

    public java.util.List<CorrespondenceEntry> simpleSearch(CorrespondenceType type, final String key,
            boolean onlyActiveEntries) {
        java.util.List<CorrespondenceEntry> entries = new java.util.ArrayList<CorrespondenceEntry>();

        if (Strings.isNullOrEmpty(key)) {
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

        private LocalizedString name;
        private Boolean active;
        private MailTracking mailTracking;

        public MailTrackingBean(MailTracking mailTracking) {
            this.mailTracking = mailTracking;
            this.name = mailTracking.getName();
            this.active = mailTracking.getActive();
        }

        public LocalizedString getName() {
            return name;
        }

        public void setName(LocalizedString name) {
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

    public static Set<MailTracking> getMailTrackingsWhereUserHasSomeRole(final User user) {

        if (user.getPerson() == null && DynamicGroup.get("managers").isMember(Authenticate.getUser())) {
            return Bennu.getInstance().getMailTrackingsSet();
        }

        java.util.Set<MailTracking> unitsWithMailTrackings = new java.util.HashSet<MailTracking>();
        CollectionUtils.select(Bennu.getInstance().getMailTrackingsSet(), new Predicate() {

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
        final Group group = getManagersGroup();
        return group != null && group.isMember(user);
    }

    public boolean isUserOperator(User user) {
        final Group group = getOperatorsGroup();
        return group != null && group.isMember(user);
    }

    public boolean isUserViewer(User user) {
        final Group group = getViewersGroup();
        return group != null && group.isMember(user);
    }

    public static boolean isBennuManager(final User user) {
        return DynamicGroup.get("managers").isMember(Authenticate.getUser());
    }

    public boolean isCurrentUserOperator() {
        return this.isUserOperator(Authenticate.getUser());
    }

    public boolean isCurrentUserViewer() {
        return this.isUserViewer(Authenticate.getUser());
    }

    public boolean isCurrentUserManager() {
        return this.isUserManager(Authenticate.getUser());
    }

    public boolean isUserAbleToCreateEntries(final User user) {
        return this.isUserOperator(user) || this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToCreateEntries() {
        return this.isUserAbleToCreateEntries(Authenticate.getUser());
    }

    public boolean isUserAbleToImportEntries(final User user) {
        return this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToImportEntries() {
        return this.isUserAbleToImportEntries(Authenticate.getUser());
    }

    public boolean isUserAbleToManageViewers(final User user) {
        return this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToManageViewers() {
        return isUserAbleToManageViewers(Authenticate.getUser());
    }

    public boolean isUserAbleToManageOperators(final User user) {
        return this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToManageOperators() {
        return this.isUserAbleToManageOperators(Authenticate.getUser());
    }

    public boolean isUserAbleToManageManagers(final User user) {
        return isBennuManager(user);
    }

    public boolean isCurrentUserAbleToManageManagers() {
        return isUserAbleToManageManagers(Authenticate.getUser());
    }

    public static boolean isUserAbleToCreateMailTrackingModule(final User user) {
        return isBennuManager(user);
    }

    public static boolean isCurrentUserAbleToCreateMailTrackingModule() {
        return isUserAbleToCreateMailTrackingModule(Authenticate.getUser());
    }

    public boolean isUserAbleToEditMailTrackingAttributes(final User user) {
        return isBennuManager(user);
    }

    public boolean isCurrentUserAbleToEditMailTrackingAttributes() {
        return this.isUserAbleToEditMailTrackingAttributes(Authenticate.getUser());
    }

    public boolean isUserAbleToViewMailTracking(final User user) {
        return this.isUserViewer(user) || this.isUserOperator(user) || this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToViewMailTracking() {
        return this.isUserAbleToViewMailTracking(Authenticate.getUser());
    }

    public boolean isUserAbleToManageUsers(final User user) {
        return this.isUserAbleToManageViewers(user) || this.isUserAbleToManageOperators(user)
                || this.isUserAbleToManageManagers(user);
    }

    public boolean isCurrentUserAbleToManageUsers() {
        return this.isUserAbleToManageUsers(Authenticate.getUser());
    }

    public boolean isUserWithSomeRoleOnThisMailTracking(final User user) {
        return this.isUserViewer(user) || this.isUserOperator(user) || this.isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserWithSomeRoleOnThisMailTracking() {
        return isUserWithSomeRoleOnThisMailTracking(Authenticate.getUser());
    }

    public boolean isUserAbleToRearrangeEntries(final User user) {
        return isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToRearrangeEntries() {
        return isUserAbleToRearrangeEntries(Authenticate.getUser());
    }

    public boolean hasUserOnlyViewOrEditionOperations(final User user) {
        return (this.isUserAbleToCreateEntries(user) || this.isUserAbleToViewMailTracking(user))
                && !this.isUserAbleToCreateEntries(user) && !this.isUserAbleToImportEntries(user)
                && !this.isUserAbleToManageUsers(user);
    }

    public boolean isCurrentUserAbleToManageYears() {
        return isUserAbleToManageYears(Authenticate.getUser());
    }

    public boolean isUserAbleToManageYears(final User user) {
        return this.isUserManager(user) || isBennuManager(user);
    }

    public boolean hasCurrentUserOnlyViewOrEditionOperations() {
        return hasUserOnlyViewOrEditionOperations(Authenticate.getUser());
    }

    public boolean isUserAbleToSetReferenceCounters(final User user) {
        return isUserOperator(user) || isUserManager(user) || isBennuManager(user);
    }

    public boolean isCurrentUserAbleToSetReferenceCounters() {
        return isUserAbleToSetReferenceCounters(Authenticate.getUser());
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
        final Stream<User> viewers = getViewersGroup().getMembers();
        final Stream<User> operators = getOperatorsGroup().getMembers();
        final Stream<User> managers = getManagersGroup().getMembers();

        final Stream<User> stream = Stream.concat(Stream.concat(viewers, operators), managers);
        return stream.collect(Collectors.toSet());
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
        for (MailTracking mailTracking : Bennu.getInstance().getMailTrackingsSet()) {
            if (name.equals(mailTracking.getName().getContent(new Locale("pt")))
                    || name.equals(mailTracking.getName().getContent(Locale.ENGLISH))) {
                return mailTracking;
            }
        }
        return null;
    }
}
