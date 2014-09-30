<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.years.management" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<logic:equal name="existsMailTrackingForUnit" value="false">
	<p><em><bean:message key="message.mail.tracking.for.unit.is.not.created" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:equal>

 
<logic:equal name="existsMailTrackingForUnit" value="true">
	
	<logic:empty name="mailTrackingBean" property="mailTracking.years">
		<bean:message key="message.mail.tracking.years.not.defined" bundle="MAIL_TRACKING_RESOURCES" />
	</logic:empty>
	
	<logic:notEmpty name="mailTrackingBean" property="mailTracking.years">
	<fr:view name="mailTrackingBean" property="mailTracking.years" schema="module.mail.tracking.years.view" >
		<fr:layout name="tabular" />
	</fr:view>
	</logic:notEmpty>

</logic:equal>


<p><strong></strong><bean:message key="label.mail.tracking.create.year" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<fr:form action='<%= String.format("/mailTrackingOrganizationModel.do?method=createYear&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<fr:edit id="mail.tracking.year.bean" name="yearBean" schema="module.mail.tracking.years.create" />
	
	<html:submit><bean:message key="label.mail.tracking.years.create" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>


<p><strong><bean:message key="label.mail.tracking.rearrange.entries.by.year" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>

<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=rearrangeEntries&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.mail.tracking.organize.entries" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
