package module.mailtracking.domain;

import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.presentationTier.renderers.converters.LocalDateConverter;
import myorg.domain.exceptions.DomainException;
import myorg.util.BundleUtil;

import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.services.Service;

public class MailTrackingImportationHelper {

    private static final Integer SENT_ID_IDX = 0;
    private static final Integer SENT_DATE_IDX = 1;
    private static final Integer SENT_RECIPIENT_IDX = 2;
    private static final Integer SENT_SUBJECT_IDX = 3;
    private static final Integer SENT_SENDER_IDX = 4;
    private static final Integer SENT_OBSERVATIONS_IDX = 5;

    private static final String MESSAGE_LINE_IMPORTATION_OK = "message.mail.tracking.importation.ok";
    private static final String MESSAGE_LINE_IMPORTATION_ERROR = "message.mail.tracking.importation.error";

    @Service
    public static void importSentMailTrackingFromCsv(MailTracking mailTracking, java.util.List<String> importationContents,
	    java.util.List<ImportationReportEntry> results) {

	boolean errorOcurred = false;

	for (String line : importationContents) {
	    ImportationReportEntry resultEntry = new ImportationReportEntry();

	    resultEntry.setLine(line);

	    try {
		String[] fields = line.split(";", -1);

		CorrespondenceEntryBean bean = new CorrespondenceEntryBean(mailTracking);
		bean.setWhenSent(convertToLocalDate(fields[SENT_DATE_IDX]));
		bean.setRecipient(fields[SENT_RECIPIENT_IDX]);
		bean.setSubject(fields[SENT_SUBJECT_IDX]);
		bean.setSender(fields[SENT_SENDER_IDX]);
		bean.setObservations(fields[SENT_OBSERVATIONS_IDX]);

		CorrespondenceEntry entry = mailTracking.createNewEntry(bean, CorrespondenceType.SENT, null);
		entry.setReference(String.format("%s/%s", entry.getYear().getName(), fields[SENT_ID_IDX]));

		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_OK));
	    } catch (pt.ist.fenixframework.pstm.IllegalWriteException e) {
		throw e;
	    } catch (Exception e) {
		errorOcurred = true;
		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_ERROR));
		resultEntry.setReason(e.getMessage());
	    } catch (DomainException e) {
		errorOcurred = true;
		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_ERROR));
		resultEntry.setReason(e.getMessage());
	    }

	    results.add(resultEntry);
	}

	if (errorOcurred) {
	    throw new ImportationErrorException();
	}
    }

    private static final Integer RECEIVED_ID_IDX = 0;
    private static final Integer RECEIVED_RECEIVED_DATE_IDX = 1;
    private static final Integer RECEIVED_SENDER_IDX = 2;
    private static final Integer RECEIVED_SENT_DATE_IDX = 3;
    private static final Integer RECEIVED_SENDER_LETTER_NUMBER_IDX = 4;
    private static final Integer RECEIVED_SUBJECT_IDX = 5;
    private static final Integer RECEIVED_RECIPIENT_IDX = 6;
    private static final Integer DISPATCHED_TO_WHOM_IDX = 7;

    @Service
    public static void importReceivedMailTrackingFromCsv(MailTracking mailTracking, java.util.List<String> importationContents,
	    java.util.List<ImportationReportEntry> results) {

	boolean errorOcurred = false;

	for (String line : importationContents) {

	    ImportationReportEntry resultEntry = new ImportationReportEntry();

	    resultEntry.setLine(line);

	    if (line.startsWith("#"))
		break;

	    try {
		String[] fields = line.split(";", -1);

		CorrespondenceEntryBean bean = new CorrespondenceEntryBean(mailTracking);
		bean.setWhenReceived(convertToLocalDate(fields[RECEIVED_RECEIVED_DATE_IDX]));
		bean.setSender(fields[RECEIVED_SENDER_IDX]);
		bean.setWhenSent(convertToLocalDate(fields[RECEIVED_SENT_DATE_IDX]));
		bean.setSenderLetterNumber(fields[RECEIVED_SENDER_LETTER_NUMBER_IDX]);
		bean.setSubject(fields[RECEIVED_SUBJECT_IDX]);
		bean.setRecipient(fields[RECEIVED_RECIPIENT_IDX]);
		bean.setDispatchedToWhom(fields[DISPATCHED_TO_WHOM_IDX]);

		CorrespondenceEntry entry = mailTracking.createNewEntry(bean, CorrespondenceType.RECEIVED, null);
		entry.setReference(String.format("%s/%s", entry.getYear().getName(), fields[RECEIVED_ID_IDX]));

		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_OK));
	    } catch (pt.ist.fenixframework.pstm.IllegalWriteException e) {
		throw e;
	    } catch (Exception e) {
		errorOcurred = true;
		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_ERROR));
		resultEntry.setReason(e.getMessage());
	    } catch (DomainException e) {
		errorOcurred = true;
		resultEntry.setState(BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources",
			MESSAGE_LINE_IMPORTATION_ERROR));
		resultEntry.setReason(e.getMessage());
	    }

	    results.add(resultEntry);
	}

	if (errorOcurred) {
	    throw new ImportationErrorException();
	}
    }

    private static LocalDate convertToLocalDate(String value) throws ConversionException {
	try {
	    return (LocalDate) new LocalDateConverter("dd.MM.yyyy").convert(LocalDate.class, value);
	} catch (ConversionException e) {
	    try {
		return (LocalDate) new LocalDateConverter("dd/MM/yyyy").convert(LocalDate.class, value);
	    } catch (ConversionException ex) {
		return (LocalDate) new LocalDateConverter("dd-MM-yyyy").convert(LocalDate.class, value);
	    }
	}
    }

    public static class ImportationReportEntry implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String line;
	private String state;
	private String reason;

	public ImportationReportEntry() {
	}

	public String getLine() {
	    return line;
	}

	public void setLine(String line) {
	    this.line = line;
	}

	public String getReason() {
	    return reason;
	}

	public void setReason(String reason) {
	    this.reason = reason;
	}

	public String getState() {
	    return state;
	}

	public void setState(String state) {
	    this.state = state;
	}

    }

    public static class ImportationErrorException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImportationErrorException() {
	    super();
	}

	public ImportationErrorException(String key, String... args) {
	    super(key, args);
	}
    }
}
