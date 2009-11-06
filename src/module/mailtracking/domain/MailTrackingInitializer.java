package module.mailtracking.domain;

import module.mailtracking.presentationTier.MailTrackingView;
import module.organization.presentationTier.actions.OrganizationModelAction;
import myorg.domain.ModuleInitializer;
import myorg.domain.MyOrg;
import pt.ist.fenixWebFramework.services.Service;

public class MailTrackingInitializer extends MailTrackingInitializer_Base implements ModuleInitializer {
    private static boolean isInitialized = false;

    private static ThreadLocal<MailTrackingInitializer> init = null;

    public static MailTrackingInitializer getInstance() {
	if (init != null) {
	    return init.get();
	}

	if (!isInitialized) {
	    initialize();
	}
	final MyOrg myOrg = MyOrg.getInstance();
	return myOrg.getMailTrackingInitializer();
    }

    @Service
    public synchronized static void initialize() {
	if (!isInitialized) {
	    try {
		final MyOrg myOrg = MyOrg.getInstance();
		final MailTrackingInitializer initializer = myOrg.getMailTrackingInitializer();
		if (initializer == null) {
		    new MailTrackingInitializer();
		}
		init = new ThreadLocal<MailTrackingInitializer>();
		init.set(myOrg.getMailTrackingInitializer());

		isInitialized = true;
	    } finally {
		init = null;
	    }
	}
    }

    public MailTrackingInitializer() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    @Override
    public void init(MyOrg root) {
	OrganizationModelAction.partyViewHookManager.register(new MailTrackingView());
    }

}
