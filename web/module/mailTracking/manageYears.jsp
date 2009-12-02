<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.years.management" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page="<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>">
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<logic:equal name="existsMailTrackingForUnit" value="false">
	<p><em><bean:message key="message.mail.tracking.for.unit.is.not.created" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:equal>

<logic:equal name="existsMailTrackingForUnit" value="true">
	<fr:view name="mailTrackingBean" property="mailTracking.years" schema="module.mail.tracking.years.view"/>
</logic:equal>

<fr:form action="<%= String.format("/mailTrackingOrganizationModel.do?method=createYearFor&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>">
	<fr:edit id="" name="module.mail.tracking.years.create" schema="module.mail.tracking.years.create" />
	
	<html:submit><bean:message label="label.mail.tracking.years.create" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
