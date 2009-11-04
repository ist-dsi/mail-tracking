package module.mailtracking.domain;

import myorg.domain.User;
import myorg.util.BundleUtil;

public enum CorrespondenceEntryVisibility {
    TO_PUBLIC {
	@Override
	public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
	    return true;
	}
    },
    ONLY_OWNER_AND_OPERATOR {
	@Override
	public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
	    return (entry.hasOwner() && entry.getOwner().equals(user.getPerson()))
		    || (entry.getMailTracking().isUserOperator(user) && entry.getCreator().equals(user))
		    || entry.getMailTracking().isUserManager(user) || MailTracking.isMyOrgManager(user);
	}
    },
    ONLY_OPERATOR {
	@Override
	public boolean isUserAbleToView(CorrespondenceEntry entry, User user) {
	    return (entry.getMailTracking().isUserOperator(user) && entry.getCreator().equals(user))
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
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    CustomEnum other = (CustomEnum) obj;
	    if (customEnum == null) {
		if (other.customEnum != null)
		    return false;
	    } else if (!customEnum.equals(other.customEnum))
		return false;
	    return true;
	}

    }

}
