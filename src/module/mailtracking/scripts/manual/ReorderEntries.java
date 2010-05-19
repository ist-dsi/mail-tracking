package module.mailtracking.scripts.manual;

import java.util.List;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class ReorderEntries extends CustomTask implements TransactionalCommand {

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

    @Override
    public void doIt() {
	MailTracking tracking = MailTracking.readMailTrackingByName("Conselho de Gestão");

	for (int i = 482, j = 477; i < 550; i++) {
	    List<CorrespondenceEntry> entries = tracking.simpleSearch(CorrespondenceType.RECEIVED, "2010/" + i, false);

	    if (entries.isEmpty()) {
		break;
	    }

	    String oldReference = entries.get(0).getReference();
	    entries.get(0).setReference("2010/" + j++);
	    out.println(String.format("Old value: %s, New value: %s", oldReference, entries.get(0).getReference()));
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
