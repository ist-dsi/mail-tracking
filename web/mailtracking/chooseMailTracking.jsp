<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>


<%@page import="module.mailtracking.domain.MailTracking" %>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<logic:empty name="mailTrackings" >
	<p><em><bean:message key="message.not.operator.in.any.mail.tracking" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:empty>

<logic:notEmpty name="mailTrackings">
	<p><strong><bean:message key="message.choose.one.mail.manager" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<table class="classes">
		<thead>
		<th><bean:message key="label.mail.tracking.unit" bundle="MAIL_TRACKING_RESOURCES" /></th>
		<th><bean:message key="mail.tracking.management.operations" bundle="MAIL_TRACKING_RESOURCES" /></th>
		</thead>
		<tbody>
			<logic:iterate id="mailTracking" name="mailTrackings">
				<bean:define id="mailTracking" name="mailTracking" type="module.mailtracking.domain.MailTracking"/>
				<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
				<tr>
					<td><bean:write name="mailTracking" property="name.content" /></td>
					<td>
						<% if(((MailTracking) mailTracking).isCurrentUserAbleToViewMailTracking()) { %>
						<p>
						<html:link page="<%= String.format("/mailtracking.do?method=prepare&mailTrackingId=%s", mailTrackingId) %>" >
							<bean:message key="label.mail.tracking.view.entries.action" bundle="MAIL_TRACKING_RESOURCES" />
						</html:link>
						</p>
						<% } %>
						
						<% if(((MailTracking) mailTracking).isCurrentUserAbleToManageUsers()) { %>
						<p>
						<html:link page="<%= String.format("/manageMailTracking.do?method=prepareUsersManagement&amp;mailTrackingId=%s", mailTrackingId) %>" >
							<bean:message key="label.mail.tracking.users.management" bundle="MAIL_TRACKING_RESOURCES" />
						</html:link>
						</p>
						<% } %>
						
						<% if(((MailTracking) mailTracking).isCurrentUserAbleToEditMailTrackingAttributes()) { %>
						<p>
						<html:link page="<%= String.format("/manageMailTracking.do?method=prepareMailTrackingAttributesManagement&amp;mailTrackingId=%s", mailTrackingId) %>" >
							<bean:message key="label.mail.tracking.module.management" bundle="MAIL_TRACKING_RESOURCES" />
						</html:link>
						</p>
						<% } %>
						
						<% if(((MailTracking) mailTracking).isCurrentUserAbleToImportEntries()) { %>
						<p>
						<html:link page="<%= String.format("") %>" >
							<bean:message key="label.mail.tracking.entries.importation.action" bundle="MAIL_TRACKING_RESOURCES" />
						</html:link>
						</p>
						<% } %>
					</td>
				</tr>
			</logic:iterate>
		</tbody>
	</table>
	
</logic:notEmpty>
