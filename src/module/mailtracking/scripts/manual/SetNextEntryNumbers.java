package module.mailtracking.scripts.manual;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;
import myorg.domain.scheduler.WriteCustomTask;

public class SetNextEntryNumbers extends WriteCustomTask {

    @Override
    public void doIt() {
	setCountersOnYear();
    }

    public void setCountersOnYear() {
	final MailTracking mailtracking = MailTracking.readMailTrackingByName("Executive Board");

	Year year = mailtracking.getYearFor(2010);
	year.setNextReceivedEntryNumber(203);
	year.setNextSentEntryNumber(63);
    }
}
