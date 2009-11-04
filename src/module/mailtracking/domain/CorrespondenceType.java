package module.mailtracking.domain;

import myorg.util.BundleUtil;

public enum CorrespondenceType {
    SENT, RECEIVED;

    public String getSimpleName() {
	return this.name();
    }

    public String getQualifiedName() {
	return this.getClass().getName() + "." + this.getSimpleName();
    }

    public String getDescription() {
	return BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources", this.getQualifiedName());
    }

}
