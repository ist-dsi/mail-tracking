package module.mailtracking.scripts.manual;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.MailTracking;
import module.organization.domain.Unit;
import myorg.domain.User;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class AddUsersToDRHMailTracking extends CustomTask implements TransactionalCommand {

    private static final String[] IST_USERNAMES = { "ist12444", "ist12889", "ist20831", "ist20940", "ist21303", "ist21767",
	    "ist21768", "ist21769", "ist21846", "ist22137", "ist22329", "ist22674", "ist22686", "ist22751", "ist22752",
	    "ist22758", "ist23202", "ist23470", "ist23487", "ist23647", "ist23889", "ist23930", "ist23932", "ist24064",
	    "ist24252", "ist24385", "ist24688", "ist24726", "ist24769", "ist24875", "ist24877", "ist24888", "ist24954",
	    "ist25096", "ist25136", "ist32949", "ist90385" };

    @Override
    public void doIt() {
	Unit unit = Unit.fromExternalId("450971566500");

	MailTracking mailTracking = unit.getMailTracking();

	for (String user : IST_USERNAMES) {
	    mailTracking.addViewer(User.findByUsername(user));
	}
    }

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

}
