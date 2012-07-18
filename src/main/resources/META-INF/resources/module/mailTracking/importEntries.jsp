<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.import.entries" bundle="MAIL_TRACKING_RESOURCES" /></h3>
<p>
<html:link page="<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>">
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<fr:form id="import.form" action="<%= String.format("/mailTrackingOrganizationModel.do?method=importMailTracking&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>" encoding="multipart/form-data">
	<fr:edit id="importation.file.bean" name="importationFileBean" visible="false" />
	
	<fr:edit id="importation.file.bean.set" name="importationFileBean" schema="module.mail.tracking.correspondence.importation.edit">
		<fr:layout name="tabular" />
	</fr:edit>
	
	<html:submit><bean:message key="label.import" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
