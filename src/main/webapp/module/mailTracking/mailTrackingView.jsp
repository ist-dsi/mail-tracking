<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />


<logic:equal name="existsMailTrackingForUnit" value="false">
	<p><em><bean:message key="message.mail.tracking.for.unit.is.not.created" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
	<html:link page='<%=  String.format("/mailTrackingOrganizationModel.do?method=createMailTracking&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
		<bean:message key="label.create.mail.tracking" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
</logic:equal>

<logic:equal name="existsMailTrackingForUnit" value="true">
	<bean:define id="mailTracking" name="party" property="mailTracking" />
	<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
	
	<html:link action='<%= String.format("/mailTrackingOrganizationModel.do?method=prepareMailTrackingAttributesManagement&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
		<bean:message key="label.mail.tracking.module.management" bundle="MAIL_TRACKING_RESOURCES"/>
	</html:link>
	|
	<html:link action='<%= String.format("/mailTrackingOrganizationModel.do?method=prepareUsersManagement&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>' >
		<bean:message key="label.mail.tracking.users.management" bundle="MAIL_TRACKING_RESOURCES"/>
	</html:link>
	|
	<html:link action='<%= String.format("/mailTrackingOrganizationModel.do?method=prepareMailTrackingImportation&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>' >
		<bean:message key="label.mail.tracking.entries.importation" bundle="MAIL_TRACKING_RESOURCES"/>
	</html:link>
	|
	<html:link action='<%= String.format("/mailTrackingOrganizationModel.do?method=prepareYearsManagement&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
		<bean:message key="label.mail.tracking.entries.manage.years" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
	|
	<html:link action='<%= String.format("/mailtracking.do?method=prepare&mailTrackingId=%s", mailTrackingId) %>'>
		<bean:message key="label.mail.tracking.view.entries" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>

	<h3><bean:message key="title.mail.tracking.attributes" bundle="MAIL_TRACKING_RESOURCES"/></h3>
	<fr:view name="mailTrackingBean" property="mailTracking" schema="module.mailtracking.manage.view" />
	
	<h3><bean:message key="mail.tracking.users" bundle="MAIL_TRACKING_RESOURCES" /></h3>
		
	<bean:define id="mailTrackingId" name="mailTrackingBean" property="mailTracking.externalId" />

	<p><strong><bean:message key="label.viewers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<logic:empty name="mailTrackingBean" property="mailTracking.viewersGroup.members">
		<bean:message key="message.mail.tracking.viewers.empty" bundle="MAIL_TRACKING_RESOURCES" />
	</logic:empty>
	<logic:notEmpty name="mailTrackingBean" property="mailTracking.viewersGroup.members" >
		<fr:view name="mailTrackingBean" property="mailTracking.viewersGroup.members" schema="module.mailtracking.manage.viewers.view">
			<fr:layout name="tabular">
				<fr:property name="classes" value="table" />
			</fr:layout>	
		</fr:view>
	</logic:notEmpty>
	
	<p><strong><bean:message key="label.operators" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<logic:empty name="mailTrackingBean" property="mailTracking.operatorsGroup.members" >
		<bean:message key="message.mail.tracking.operators.empty" bundle="MAIL_TRACKING_RESOURCES" />
	</logic:empty>
	<logic:notEmpty name="mailTrackingBean" property="mailTracking.operatorsGroup.members" >
		<fr:view name="mailTrackingBean" property="mailTracking.operatorsGroup.members" schema="module.mailtracking.manage.operators.view">
			<fr:layout name="tabular">
				<fr:property name="classes" value="table" />
				
			</fr:layout>
		</fr:view>
	</logic:notEmpty>

	<p><strong><bean:message key="label.managers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<logic:empty name="mailTrackingBean" property="mailTracking.managersGroup.members" >
		<bean:message key="message.mail.tracking.managers.empty" bundle="MAIL_TRACKING_RESOURCES" />
	</logic:empty>
	<logic:notEmpty name="mailTrackingBean" property="mailTracking.managersGroup.members" >
		<fr:view name="mailTrackingBean" property="mailTracking.managersGroup.members" schema="module.mailtracking.manage.managers.view">
			<fr:layout name="tabular">
				<fr:property name="classes" value="table" />
	
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	
	
	<h3><bean:message key="title.mail.tracking.statistics" bundle="MAIL_TRACKING_RESOURCES"/></h3>
	<fr:view name="mailTrackingBean" property="mailTracking" schema="module.mail.tracking.statistics.simple.view" >
		<fr:layout name="tabular">
			<fr:property name="columnClasses" value="aleft," />
		</fr:layout>
	</fr:view>
	
</logic:equal>

