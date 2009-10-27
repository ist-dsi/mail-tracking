<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.import.entries" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<bean:define id="partyId" name="unit" property="externalId" />

<html:link href="<%= request.getContextPath() + "/manageMailTracking.do?method=prepare&amp;partyOid=" + partyId %>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<fr:form id="import.form" action="<%= "/manageMailTracking.do?method=importCGMailTracking&amp;partyOid=" + partyId %>" encoding="multipart/form-data">
	<fr:edit id="importation.file.bean" name="importationFileBean" visible="false" />
	
	<fr:edit id="importation.file.bean.set" name="importationFileBean" schema="module.mail.tracking.correspondence.importation.edit">
		<fr:layout name="tabular" />
	</fr:edit>
	
	<html:submit><bean:message key="label.import" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>

