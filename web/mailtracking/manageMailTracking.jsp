<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="partyOid" name="unit" property="externalId" />

<logic:equal name="existsMailTrackingForUnit" value="false">
	<p><em><bean:message key="message.mail.tracking.for.unit.is.not.created" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
	<html:link page='<%=  "/manageMailTracking.do?method=createMailTracking&amp;partyOid=" + partyOid %>'>
		<bean:message key="label.create.mail.tracking" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
</logic:equal>

<logic:equal name="existsMailTrackingForUnit" value="true">

	<fr:form action='<%=  "/manageMailTracking.do?method=editMailTrackingAttributes&amp;partyOid=" + partyOid %>'>
		<fr:edit id="mail.tracking.bean" name="mailTrackingBean" schema="module.mailtracking.manage.edit" />
		
		<p><html:submit><bean:message key="label.edit" bundle="MAIL_TRACKING_RESOURCES" /></html:submit></p>
	</fr:form>
	
	<bean:define id="mailTrackingId" name="mailTrackingBean" property="mailTracking.externalId" />

	<p><strong><bean:message key="label.viewers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<logic:empty name="mailTrackingBean" property="mailTracking.viewersGroup.members">
		<bean:message key="message.mail.tracking.viewers.empty" bundle="MAIL_TRACKING_RESOURCES" />
	</logic:empty>
	<logic:notEmpty name="mailTrackingBean" property="mailTracking.viewersGroup.members" >
		<fr:view name="mailTrackingBean" property="mailTracking.viewersGroup.members" schema="module.mailtracking.manage.viewers.view">
			<fr:layout name="tabular">
				<fr:property name="classes" value="table" />
				
				<fr:property name="linkFormat(remove)" value="<%=  "/manageMailTracking.do?method=removeViewer&amp;partyOid=" + partyOid + "&amp;userId=${externalId}" %>" />
				<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
				<fr:property name="key(remove)" value="label.remove" />
				<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
				<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" /> 
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
				
				<fr:property name="linkFormat(remove)" value="<%=  "/manageMailTracking.do?method=removeOperator&amp;partyOid=" + partyOid + "&amp;userId=${externalId}" %>" />
				<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
				<fr:property name="key(remove)" value="label.remove" />
				<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
				<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" /> 
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	
	<p><em><bean:message key="message.mail.tracking.add.operator.explanation" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
	
	<fr:form action="<%= "/manageMailTracking.do?method=searchUser&partyOid=" + partyOid %>" >
		<fr:edit id="search.user.bean" name="searchUserBean" schema="module.mailtracking.manage.operators.search"/>		
		<p><html:submit><bean:message key="label.search" bundle="MAIL_TRACKING_RESOURCES" /></html:submit></p>
	</fr:form>

	<logic:notEmpty name="searchResults">
		<fr:view name="searchResults" schema="module.mailtracking.manage.operators.view" >
			<fr:layout name="tabular">
				<fr:property name="classes" value="table" />

				<fr:property name="linkFormat(addViewer)" value="<%=  "/manageMailTracking.do?method=addViewer&amp;partyOid=" + partyOid + "&amp;userId=${externalId}" %>" />
				<fr:property name="bundle(addViewer)" value="MAIL_TRACKING_RESOURCES" />
				<fr:property name="key(addViewer)" value="label.add.user.as.viewer" />
				
				<fr:property name="linkFormat(addOperator)" value="<%= "/manageMailTracking.do?method=addOperator&amp;partyOid=" + partyOid + "&amp;userId=${externalId}" %>" />
				<fr:property name="bundle(addOperator)" value="MAIL_TRACKING_RESOURCES" />
				<fr:property name="key(addOperator)" value="label.add.user.as.operator" />				
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	
	
	
</logic:equal>

