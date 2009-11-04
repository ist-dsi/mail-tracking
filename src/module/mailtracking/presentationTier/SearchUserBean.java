package module.mailtracking.presentationTier;

import myorg.util.BundleUtil;

public class SearchUserBean implements java.io.Serializable {

    public static enum SearchUserMode {
	NAME, USERNAME;

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

    private static final long serialVersionUID = 1L;

    private String value;
    private SearchUserMode mode;

    public SearchUserBean() {

    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public SearchUserMode getMode() {
	return mode;
    }

    public void setMode(SearchUserMode mode) {
	this.mode = mode;
    }

}
