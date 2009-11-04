<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>


<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.attributes.management" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page="<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>">
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<fr:form action='<%= String.format("/mailTrackingOrganizationModel.do?method=editMailTrackingAttributes&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<fr:edit id="mail.tracking.bean" name="mailTrackingBean" schema="module.mailtracking.manage.edit">
		<fr:destination name="cancel" path="<%= String.format("/manageMailTracking.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>" />
	</fr:edit>
	
	<p><html:submit><bean:message key="label.edit" bundle="MAIL_TRACKING_RESOURCES" /></html:submit></p>
	<p><html:cancel><bean:message key="label.cancel" bundle="MAIL_TRACKING_RESOURCES" /></html:cancel></p>
</fr:form>
