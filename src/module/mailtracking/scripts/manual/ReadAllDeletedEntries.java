package module.mailtracking.scripts.manual;

import java.util.List;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import myorg.domain.scheduler.ReadCustomTask;

public class ReadAllDeletedEntries extends ReadCustomTask {

    @Override
    public void doIt() {
	final MailTracking mailtracking = MailTracking.readMailTrackingByName("Executive Board");

	List<CorrespondenceEntry> sentDeletedEntryList = mailtracking.getDeletedEntries(CorrespondenceType.SENT);
	List<CorrespondenceEntry> receivedDeletedEntryList = mailtracking.getDeletedEntries(CorrespondenceType.RECEIVED);

	out.println("Sent entries");
	for (CorrespondenceEntry sentEntry : sentDeletedEntryList) {
	    out.println("Entry nº: " + sentEntry.getReference());
	}

	out.println("Received entries");
	for (CorrespondenceEntry receivedEntry : receivedDeletedEntryList) {
	    out.println("Entry nº: " + receivedEntry.getReference());
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
