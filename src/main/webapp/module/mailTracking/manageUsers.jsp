<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<bean:define id="partyOid" name="party" property="externalId" />
<bean:define id="organizationalModelOid" name="organizationalModel" property="externalId" />

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="title.mail.tracking.users.management" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p>
<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>

<logic:equal name="existsMailTrackingForUnit" value="false">
	<p><em><bean:message key="message.mail.tracking.for.unit.is.not.created" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:equal>

<logic:equal name="existsMailTrackingForUnit" value="true">
<p><strong><bean:message key="label.viewers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<logic:empty name="viewers" >
	<bean:message key="message.mail.tracking.viewers.empty" bundle="MAIL_TRACKING_RESOURCES" />
</logic:empty>
<logic:notEmpty name="viewers"  >
	<fr:view name="viewers" schema="module.mailtracking.manage.viewers.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/mailTrackingOrganizationModel.do?method=removeViewer&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid) %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationBundle(remove)" value="MAIL_TRACKING_RESOURCES" />
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
	<fr:view name="operators"  schema="module.mailtracking.manage.operators.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/mailTrackingOrganizationModel.do?method=removeOperator&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid)  %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationBundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
			<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" /> 
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<p><strong><bean:message key="label.managers" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<logic:empty name="managers"  >
	<bean:message key="message.mail.tracking.managers.empty" bundle="MAIL_TRACKING_RESOURCES" />
</logic:empty>
<logic:notEmpty name="managers"  >
	<fr:view name="managers" schema="module.mailtracking.manage.managers.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />
			
			<fr:property name="linkFormat(remove)" value='<%=  String.format("/mailTrackingOrganizationModel.do?method=removeManager&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid)  %>' />
			<fr:property name="bundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(remove)" value="label.remove" />
			<fr:property name="confirmationBundle(remove)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="confirmationKey(remove)" value="message.confirm.user.removal" />
			<fr:property name="confirmationTitleKey(remove)" value="title.confirm.user.removal" /> 
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<p><em><bean:message key="message.mail.tracking.add.operator.explanation" bundle="MAIL_TRACKING_RESOURCES" /></em></p>

<fr:form action='<%= String.format("/mailTrackingOrganizationModel.do?method=searchUser&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>' >
	<fr:edit id="search.user.bean" name="searchUserBean" schema="module.mailtracking.manage.operators.search"/>		
	<p><html:submit><bean:message key="label.search" bundle="MAIL_TRACKING_RESOURCES" /></html:submit></p>
</fr:form>

<logic:notEmpty name="searchResults">
	<fr:view name="searchResults" schema="module.mailtracking.manage.operators.view" >
		<fr:layout name="tabular">
			<fr:property name="classes" value="table" />

			<fr:property name="linkFormat(addViewer)" value='<%=  String.format("/mailTrackingOrganizationModel.do?method=addViewer&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid) %>' />
			<fr:property name="bundle(addViewer)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addViewer)" value="label.add.user.as.viewer" />
			
			<fr:property name="linkFormat(addOperator)" value='<%= String.format("/mailTrackingOrganizationModel.do?method=addOperator&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid) %>' />
			<fr:property name="bundle(addOperator)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addOperator)" value="label.add.user.as.operator" />				

			<fr:property name="linkFormat(addManager)" value='<%= String.format("/mailTrackingOrganizationModel.do?method=addManager&amp;organizationalModelOid=%s&amp;partyOid=%s&amp;userId=${externalId}", organizationalModelOid, partyOid) %>' />
			<fr:property name="bundle(addManager)" value="MAIL_TRACKING_RESOURCES" />
			<fr:property name="key(addManager)" value="label.add.user.as.manager" />				

		</fr:layout>
	</fr:view>
</logic:notEmpty>
</logic:equal>

<p>
<html:link page='<%= String.format("/mailTrackingOrganizationModel.do?method=back&amp;organizationalModelOid=%s&amp;partyOid=%s", organizationalModelOid, partyOid) %>'>
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>
</p>
