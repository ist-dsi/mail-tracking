package module.mailtracking.scripts.manual;

import module.mailtracking.domain.Document;
import module.mailtracking.domain.MailTracking;
import myorg.domain.MyOrg;
import myorg.domain.scheduler.WriteCustomTask;

public class SetMyOrgInstanceOnDocuments extends WriteCustomTask {

    @Override
    protected void doService() {
	MailTracking mailTracking = MailTracking.readMailTrackingByName("Executive Board");

	for (Document document : mailTracking.getTotalDocuments()) {
	    if (!document.hasMyOrg()) {
		document.setMyOrg(MyOrg.getInstance());
	    }
	}

    }
}
