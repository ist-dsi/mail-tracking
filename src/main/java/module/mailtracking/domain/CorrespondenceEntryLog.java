package module.mailtracking.domain;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

public class CorrespondenceEntryLog extends CorrespondenceEntryLog_Base implements Comparable<CorrespondenceEntryLog> {

    public CorrespondenceEntryLog(final CorrespondenceEntry entry, String key, String... args) {
        setCorrespondenceEntry(entry);
        setOperationDescription(BundleUtil.getString("resources/MailTrackingResources", key, args));
        final User user = Authenticate.getUser();
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
