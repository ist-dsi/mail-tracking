<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.importation.results" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<bean:define id="partyId" name="unit" property="externalId" />

<html:link href="<%= request.getContextPath() + "/manageMailTracking.do?method=prepare&amp;partyOid=" + partyId %>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<p>
<logic:equal name="errorOccurred" value="false" >
	<bean:message key="message.importation.operation.done.sucessfully" bundle="MAIL_TRACKING_RESOURCES" />
</logic:equal>


<logic:equal name="errorOccurred" value="true">
	<bean:message key="message.some.error.occurred.on.importation.operation" bundle="MAIL_TRACKING_RESOURCES" />
</logic:equal>
</p>


<fr:view name="importationFileResults" schema="module.mail.tracking.correspondence.importation.results.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2"/>
		<fr:property name="columnClasses" value="smalltxt aleft, smalltxt aleft"/>
	</fr:layout>
</fr:view>

