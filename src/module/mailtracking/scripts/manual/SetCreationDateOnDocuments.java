package module.mailtracking.scripts.manual;

import module.mailtracking.domain.Document;
import module.mailtracking.domain.MailTracking;
import myorg.domain.scheduler.WriteCustomTask;

public class SetCreationDateOnDocuments extends WriteCustomTask {

    @Override
    public void doIt() {
	MailTracking mailTracking = MailTracking.readMailTrackingByName("Executive Board");

	for (Document document : mailTracking.getTotalDocuments()) {
	    document.setCreationDate(document.getCorrespondenceEntry().getCreationDate());
	}
    }

}
