<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.importation.results" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

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

<p>
<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>
