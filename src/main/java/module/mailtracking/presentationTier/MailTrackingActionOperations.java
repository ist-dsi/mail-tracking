/*
 * @(#)MailTrackingActionOperations.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.Helper;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.MailTrackingImportationHelper;
import module.mailtracking.domain.MailTrackingImportationHelper.ImportationReportEntry;
import module.mailtracking.domain.exception.PermissionDeniedException;
import pt.ist.bennu.core.domain.User;

import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.IntegerNumberConverter;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class MailTrackingActionOperations {
    public static void removeOperator(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeOperator(user);
    }

    public static void addOperator(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageOperators()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().addOperator(user);
    }

    public static void removeViewer(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeViewer(user);
    }

    public static void addViewer(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageViewers()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().addViewer(user);
    }

    public static void addManager(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().addManager(user);
    }

    public static void removeManager(final MailTrackingBean bean, final User user) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageManagers()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().removeManager(user);
    }

    public static boolean importMailTracking(final MailTrackingBean mailTrackingBean, final ImportationFileBean importationBean,
	    java.util.List<ImportationReportEntry> importationResults) throws Exception {
	java.util.List<String> importationContents = consumeCsvImportationContent(importationBean);

	boolean errorOccurred;
	try {
	    if (importationBean.getType().equals(CorrespondenceType.SENT)) {
		MailTrackingImportationHelper.importSentMailTrackingFromCsv(mailTrackingBean.getMailTracking(),
			importationContents, importationResults);
		errorOccurred = true;
	    } else if (importationBean.getType().equals(CorrespondenceType.RECEIVED)) {
		MailTrackingImportationHelper.importReceivedMailTrackingFromCsv(mailTrackingBean.getMailTracking(),
			importationContents, importationResults);
		errorOccurred = true;
	    } else {
		throw new RuntimeException("wrong.type");
	    }
	} catch (MailTrackingImportationHelper.ImportationErrorException e) {
	    errorOccurred = false;
	}

	return errorOccurred;
    }

    private static java.util.List<String> consumeCsvImportationContent(ImportationFileBean bean) throws IOException {
	InputStreamReader inputStreamReader = new InputStreamReader(bean.getStream(), "UTF8");
	BufferedReader reader = new BufferedReader(inputStreamReader);

	java.util.List<String> stringContents = new java.util.ArrayList<String>();
	String line = null;
	while ((line = reader.readLine()) != null) {
	    stringContents.add(line);
	}

	return stringContents;
    }

    public static void createYearFor(YearBean bean) {
	if (!bean.getMailTracking().isCurrentUserAbleToManageYears()) {
	    throw new PermissionDeniedException();
	}

	bean.getMailTracking().createYearFor(bean.getYear());
    }

    public static void rearrangeEntries(final MailTrackingBean mailTrackingBean) {
	if (!mailTrackingBean.getMailTracking().isCurrentUserAbleToRearrangeEntries()) {
	    throw new PermissionDeniedException();
	}

	(new Helper()).reNumberEntries(mailTrackingBean.getMailTracking());
    }

    public static void setReferenceCounters(final YearBean bean) {
	if (!bean.getMailTracking().isCurrentUserAbleToSetReferenceCounters()) {
	    throw new PermissionDeniedException();
	}

	bean.getChosenYear().setCounters(bean.getNextSentEntryNumber(), bean.getNextReceivedEntryNumber());
    }

    public static class CreateYearProvider implements DataProvider {

	@Override
	public Converter getConverter() {
	    return new IntegerNumberConverter();
	}

	@Override
	public Object provide(Object source, Object currentValue) {
	    YearBean bean = (YearBean) source;

	    java.util.List<Integer> yearsWithout = new java.util.ArrayList<Integer>();

	    Integer currentYear = (new DateTime()).getYear();

	    for (int i = currentYear - 2; i < (currentYear + 2); i++) {
		if (bean.getMailTracking().getYearFor(i) == null) {
		    yearsWithout.add(i);
		}
	    }

	    return yearsWithout;
	}
    }

}
