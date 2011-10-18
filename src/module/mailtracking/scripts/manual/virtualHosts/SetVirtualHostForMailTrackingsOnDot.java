package module.mailtracking.scripts.manual.virtualHosts;

import java.util.Set;

import jvstm.TransactionalCommand;
import module.mailtracking.domain.MailTracking;
import myorg.domain.MyOrg;
import myorg.domain.VirtualHost;
import myorg.domain.scheduler.CustomTask;
import pt.ist.fenixframework.pstm.Transaction;

public class SetVirtualHostForMailTrackingsOnDot extends CustomTask implements TransactionalCommand {

    @Override
    public void doIt() {
	Set<MailTracking> mailTrackings = MyOrg.getInstance().getMailTrackingsSet();
	VirtualHost virtualHostForDot = getVirtualHostForDot();

	for (MailTracking mailTracking : mailTrackings) {
	    if (mailTracking.hasVirtualHost()) {
		continue;
	    }

	    mailTracking.setVirtualHost(virtualHostForDot);
	}
    }

    private VirtualHost getVirtualHostForDot() {
	Set<VirtualHost> virtualHostsSet = MyOrg.getInstance().getVirtualHostsSet();
	
	for (VirtualHost virtualHost : virtualHostsSet) {
	    if ("dot.ist.utl.pt".equals(virtualHost.getHostname())) {
		return virtualHost;
	    }

	    out.println(virtualHost.getHostname() + " is not equal to 'dot.local'");
	}
	
	throw new RuntimeException("could not find virtual host for dot");
    }

    @Override
    public void run() {
	Transaction.withTransaction(false, this);
	out.println("Done.");
    }

}
