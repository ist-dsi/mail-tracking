package module.mailtracking.scripts.manual;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceEntryState;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class DeactivateEntry extends CustomTask implements TransactionalCommand {

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

    @Override
    public void doIt() {
	CorrespondenceEntry entry = CorrespondenceEntry.fromExternalId("730144443922");
	entry.setState(CorrespondenceEntryState.DELETED);

    }

}
