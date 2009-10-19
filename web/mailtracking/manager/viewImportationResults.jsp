<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.importation.results" /></h3>

<bean:define id="partyId" name="unit" property="externalId" />

<html:link href="<%= request.getContextPath() + "/manageMailTracking.do?method=prepare&amp;partyOid=" + partyId %>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<p>
<logic:equals name="errorOccurred" value="true" >
	<bean:message key="message.importation.operation.done.sucessfully" bundle="MAIL_TRACKING_RESOURCES" />
</logic:equals>


<logic:equals name="errorOccurred" value="false">
	<bean:message key="message.some.error.occurred.on.importation.operation" bundle="MAIL_TRACKING_RESOURCES" />
</logic:equals>
</p>

<fr:view name="importationFileResults" schema="module.mail.tracking.correspondence.importation.results.view" >
	<fr:layout name="">
		<fr:property name="classes" value="" />
		<fr:property name="" value="" />
	</fr:layout>
</fr:view>

