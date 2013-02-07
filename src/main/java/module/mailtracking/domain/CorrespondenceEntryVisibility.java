/*
 * @(#)CorrespondenceEntryVisibility.java
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

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public enum CorrespondenceEntryVisibility {
    TO_PUBLIC {
        @Override
        public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
            return true;
        }

        @Override
        public boolean isUserAbleToEdit(CorrespondenceEntry entry, User user) {
            return entry.getMailTracking().isUserOperator(user) || entry.getMailTracking().isUserManager(user)
                    || MailTracking.isMyOrgManager(user);
        }

        @Override
        public boolean isUserAbleToDelete(CorrespondenceEntry entry, User user) {
            return entry.getMailTracking().isUserOperator(user) || entry.getMailTracking().isUserManager(user)
                    || MailTracking.isMyOrgManager(user);
        }
    },
    ONLY_OWNER_AND_OPERATOR {
        @Override
        public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
            return (entry.hasOwner() && entry.getOwner().equals(user.getPerson()))
                    || (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }

        @Override
        public boolean isUserAbleToEdit(CorrespondenceEntry entry, User user) {
            return (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }

        @Override
        public boolean isUserAbleToDelete(CorrespondenceEntry entry, User user) {
            return (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }
    },
    ONLY_OPERATOR {
        @Override
        public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
            return (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }

        @Override
        public boolean isUserAbleToEdit(CorrespondenceEntry entry, User user) {
            return (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }

        @Override
        public boolean isUserAbleToDelete(CorrespondenceEntry entry, User user) {
            return (entry.getMailTracking().isUserOperator(user) && entry.getLastEditor().equals(user))
                    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
        }
    };

    public String getVisibilityDescriptionForSentEntry() {
        return BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
                "module.mailtracking.domain.CorrespondenceEntryVisibility." + name() + ".sent.entry.description");
    }

    public String getVisibilityDescriptionForReceivedEntry() {
        return BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
                "module.mailtracking.domain.CorrespondenceEntryVisibility." + name() + ".received.entry.description");
    }

    public abstract boolean isUserAbleToView(CorrespondenceEntry entry, User user);

    public abstract boolean isUserAbleToEdit(CorrespondenceEntry entry, User user);

    public abstract boolean isUserAbleToDelete(CorrespondenceEntry entry, User user);

    public static class CustomEnum implements java.io.Serializable {
        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        CorrespondenceEntryVisibility customEnum;

        public CustomEnum() {

        }

        public CustomEnum(CorrespondenceEntryVisibility value) {
            setCustomEnum(value);
        }

        public String getVisibilityDescriptionForSentEntry() {
            return customEnum.getVisibilityDescriptionForSentEntry();
        }

        public String getVisibilityDescriptionForReceivedEntry() {
            return customEnum.getVisibilityDescriptionForReceivedEntry();
        }

        public void setCustomEnum(CorrespondenceEntryVisibility value) {
            this.customEnum = value;
        }

        public CorrespondenceEntryVisibility getCustomEnum() {
            return this.customEnum;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((customEnum == null) ? 0 : customEnum.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CustomEnum other = (CustomEnum) obj;
            if (customEnum == null) {
                if (other.customEnum != null) {
                    return false;
                }
            } else if (!customEnum.equals(other.customEnum)) {
                return false;
            }
            return true;
        }

    }

}
