package module.mailtracking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import myorg.domain.MyOrg;

public class MailTrackingsOnVirtualHost {

    public static final List<MailTracking> retrieve() {
	Set<MailTracking> mailTrackings = MyOrg.getInstance().getMailTrackingsSet();

	List<MailTracking> result = new ArrayList<MailTracking>();

	for (MailTracking tracking : mailTrackings) {
	    if (tracking.isConnectedToCurrentHost()) {
		result.add(tracking);
	    }
	}
	
	return result;
    }

}
