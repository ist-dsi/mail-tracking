package module.mailtracking.domain;

import org.joda.time.DateTime;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.util.BundleUtil;

public class CorrespondenceEntryLog extends CorrespondenceEntryLog_Base implements Comparable<CorrespondenceEntryLog> {
    
    public CorrespondenceEntryLog(final CorrespondenceEntry entry, String key, String... args) {
	setCorrespondenceEntry(entry);
	setOperationDescription(BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources", key, args));
	final User user = UserView.getCurrentUser();
	setUsername(user == null ? "--" : user.getUsername());
	setWhenOperation(new DateTime());
    }

    @Override
    public int compareTo(final CorrespondenceEntryLog log) {
	if (getWhenOperation() != null && log.getWhenOperation() != null) {
	    int i = getWhenOperation().compareTo(log.getWhenOperation());
	    if (i != 0) {
		return i;
	    }
	}
	return getExternalId().compareTo(log.getExternalId());
    }
    
}
