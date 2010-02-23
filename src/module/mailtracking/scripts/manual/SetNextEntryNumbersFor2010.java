package module.mailtracking.scripts.manual;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class SetNextEntryNumbersFor2010 extends CustomTask implements TransactionalCommand {

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

    @Override
    public void doIt() {
	setCountersOnYear();
    }

    @Service
    public void setCountersOnYear() {
	final MailTracking mailtracking = MailTracking.readMailTrackingByName("Executive Board");

	Year year = mailtracking.getYearFor(2010);
	year.setNextReceivedEntryNumber(203);
	year.setNextSentEntryNumber(63);
    }
}
