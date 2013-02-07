/*
 * @(#)YearBean.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Anil Kassamali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Correspondence Registry Module.
 *
 *   The Correspondence Registry Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Correspondence Registry Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Correspondence Registry Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.mailtracking.presentationTier;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.Year;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class YearBean implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer year;

    private MailTracking mailTracking;

    private Year chosenYear;

    private Integer nextSentEntryNumber;

    private Integer nextReceivedEntryNumber;

    public YearBean() {

    }

    public YearBean(final MailTracking mailTracking) {
        setMailTracking(mailTracking);
    }

    public YearBean(MailTracking mailTracking, Year chosenYear) {
        setMailTracking(mailTracking);
        setChosenYear(chosenYear);
        setNextSentEntryNumber(chosenYear.getNextSentEntryNumber());
        setNextReceivedEntryNumber(chosenYear.getNextReceivedEntryNumber());
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

    public Integer getNextSentEntryNumber() {
        return nextSentEntryNumber;
    }

    public void setNextSentEntryNumber(Integer nextSentEntryNumber) {
        this.nextSentEntryNumber = nextSentEntryNumber;
    }

    public Integer getNextReceivedEntryNumber() {
        return nextReceivedEntryNumber;
    }

    public void setNextReceivedEntryNumber(Integer nextReceivedEntryNumber) {
        this.nextReceivedEntryNumber = nextReceivedEntryNumber;
    }

}
