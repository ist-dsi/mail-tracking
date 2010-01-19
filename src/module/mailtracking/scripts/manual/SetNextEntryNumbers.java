package module.mailtracking.scripts.manual;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.Helper;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class SetNextEntryNumbers extends CustomTask implements TransactionalCommand {

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

    @Override
    public void doIt() {
	Helper helper = new Helper();

	helper.setCountersOnYear();
    }
}
