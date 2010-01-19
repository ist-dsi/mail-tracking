package module.mailtracking.scripts.manual;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.Helper;
import module.mailtracking.domain.MailTracking;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class DeleteAllEntriesFromDatabase extends CustomTask implements TransactionalCommand {

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

    @Override
    public void doIt() {
	final MailTracking mailtracking = MailTracking.readMailTrackingByName("Executive Board");

	new Helper().removeEntriesFromConcelhoGestaoAndResetCounters(mailtracking);

    }

}
