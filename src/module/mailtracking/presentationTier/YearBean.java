/**
 * 
 */
package module.mailtracking.presentationTier;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;

public class YearBean implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer year;

    private MailTracking mailTracking;

    private Year chosenYear;

    public YearBean() {

    }

    public YearBean(final MailTracking mailTracking) {
	setMailTracking(mailTracking);
    }

    public YearBean(MailTracking mailTracking, Year chosenYear) {
	setMailTracking(mailTracking);
	setChosenYear(chosenYear);
    }

    public Integer getYear() {
	return this.year;
    }

    public void setYear(final Integer year) {
	this.year = year;
    }

    public MailTracking getMailTracking() {
	return this.mailTracking;
    }

    public void setMailTracking(final MailTracking mailTracking) {
	this.mailTracking = mailTracking;
    }

    public Year getChosenYear() {
	return this.chosenYear;
    }

    public void setChosenYear(final Year chosenYear) {
	this.chosenYear = chosenYear;
    }
}