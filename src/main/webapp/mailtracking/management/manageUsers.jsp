<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<bean:define id="mailTrackingId" name="mailTrackingBean" property="mailTracking.externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.users.management" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page="/mailtracking.do?method=prepare">
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<p><strong><bean:message key="label.viewers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<logic:empty name="viewers" >
	<bean:message key="message.mail.tracking.viewers.empty" bundle="MAIL_TRACKING_RESOURCES" />
</logic:empty>
<logic:notEmpty name="viewers" >
	<fr:view name="viewers" schema="module.mailtracking.manage.viewers.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/manageMailTracking.do?method=removeViewer&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
			<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" />
		</fr:layout>	
	</fr:view>
</logic:notEmpty>

<p><strong><bean:message key="label.operators" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<logic:empty name="operators"  >
	<bean:message key="message.mail.tracking.operators.empty" bundle="MAIL_TRACKING_RESOURCES" />
</logic:empty>
<logic:notEmpty name="operators"  >
	<fr:view name="operators" schema="module.mailtracking.manage.operators.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/manageMailTracking.do?method=removeOperator&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId)  %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
			<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>


<logic:equal name="mailTrackingBean" property="mailTracking.currentUserAbleToManageManagers" value="true">

<p><strong><bean:message key="label.managers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<logic:empty name="managers" >
	<bean:message key="message.mail.tracking.managers.empty" bundle="MAIL_TRACKING_RESOURCES" />
</logic:empty>
<logic:notEmpty name="managers"  >
	<fr:view name="managers" schema="module.mailtracking.manage.managers.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/manageMailTracking.do?method=removeManager&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId)  %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
			<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

</logic:equal>

<p><em><bean:message key="message.mail.tracking.add.operator.explanation" bundle="MAIL_TRACKING_RESOURCES" /></em></p>

<fr:form action='<%= String.format("/manageMailTracking.do?method=searchUser&amp;mailTrackingId=%s", mailTrackingId) %>' >
	<fr:edit id="search.user.bean" name="searchUserBean" schema="module.mailtracking.manage.operators.search"/>		
	<p><html:submit><bean:message key="label.search" bundle="MAIL_TRACKING_RESOURCES" /></html:submit></p>
</fr:form>

<logic:equal name="mailTrackingBean" property="mailTracking.currentUserAbleToManageManagers" value="true">

<logic:notEmpty name="searchResults">
	<fr:view name="searchResults" schema="module.mailtracking.manage.operators.view" >
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />

			<fr:property name="linkFormat(addViewer)" value='<%=  String.format("/manageMailTracking.do?method=addViewer&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(addViewer)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addViewer)" value="label.add.user.as.viewer" />
			
			<fr:property name="linkFormat(addOperator)" value='<%= String.format("/manageMailTracking.do?method=addOperator&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(addOperator)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addOperator)" value="label.add.user.as.operator" />				

			<fr:property name="linkFormat(addManager)" value='<%= String.format("/manageMailTracking.do?method=addManager&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(addManager)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addManager)" value="label.add.user.as.manager" />				

		</fr:layout>
	</fr:view>
</logic:notEmpty>

</logic:equal>

<logic:equal name="mailTrackingBean" property="mailTracking.currentUserAbleToManageManagers" value="false">

<logic:notEmpty name="searchResults">
	<fr:view name="searchResults" schema="module.mailtracking.manage.operators.view" >
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />

			<fr:property name="linkFormat(addViewer)" value='<%=  String.format("/manageMailTracking.do?method=addViewer&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(addViewer)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addViewer)" value="label.add.user.as.viewer" />
			
			<fr:property name="linkFormat(addOperator)" value='<%= String.format("/manageMailTracking.do?method=addOperator&amp;mailTrackingId=%s&amp;userId=${externalId}", mailTrackingId) %>' />
			<fr:property name="bundle(addOperator)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addOperator)" value="label.add.user.as.operator" />				

		</fr:layout>
	</fr:view>
</logic:notEmpty>

</logic:equal>

<p>
<html:link page="/mailtracking.do?method=prepare">
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>
