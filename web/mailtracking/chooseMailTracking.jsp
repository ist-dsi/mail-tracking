<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<logic:empty name="mailTrackings" >
	<p><em><bean:message key="message.not.operator.in.any.mail.tracking" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:empty>

<logic:notEmpty name="mailTrackings">
	<p><strong><bean:message key="message.choose.one.mail.manager" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<fr:view name="mailTrackings" schema="module.mail.tracking.choose" >
		<fr:layout name="tabular" >
			<fr:property name="classes" value="tabular" />
			
			<fr:property name="linkFormat(manage)" value="/mailtracking.do?method=prepare&mailTrackingId=${externalId}" />
			<fr:property name="key(manage)" value="label.view" />
			<fr:property name="bundle(manage)" value="MAIL_TRACKING_RESOURCES" />
		</fr:layout>		
	</fr:view>
	
</logic:notEmpty>
