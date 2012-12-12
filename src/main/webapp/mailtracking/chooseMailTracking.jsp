<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>


<%@page import="module.mailtracking.domain.MailTracking" %>

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<logic:empty name="mailTrackings" >
	<p><em><bean:message key="message.not.operator.in.any.mail.tracking" bundle="MAIL_TRACKING_RESOURCES" /></em></p>
</logic:empty>

<logic:notEmpty name="mailTrackings">
	<p><strong><bean:message key="message.choose.one.mail.manager" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
	<table class="tstyle2">
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
					<td class="aleft">
						<ul style="margin: 0 0 0 2em; padding: 0;">
							<% if(((MailTracking) mailTracking).isCurrentUserAbleToViewMailTracking()) { %>
							<li>
								<html:link page="<%= String.format("/mailtracking.do?method=prepare&mailTrackingId=%s", mailTrackingId) %>" >
									<bean:message key="label.mail.tracking.view.entries.action" bundle="MAIL_TRACKING_RESOURCES" />
								</html:link>
							</li>
							<% } %>
							
							<% if(((MailTracking) mailTracking).isCurrentUserAbleToManageUsers()) { %>
							<li>
								<html:link page="<%= String.format("/manageMailTracking.do?method=prepareUsersManagement&amp;mailTrackingId=%s", mailTrackingId) %>" >
									<bean:message key="label.mail.tracking.users.management" bundle="MAIL_TRACKING_RESOURCES" />
								</html:link>
							</li>
							<% } %>
							
							<% if(((MailTracking) mailTracking).isCurrentUserAbleToEditMailTrackingAttributes()) { %>
							<li>
								<html:link page="<%= String.format("/manageMailTracking.do?method=prepareMailTrackingAttributesManagement&amp;mailTrackingId=%s", mailTrackingId) %>" >
									<bean:message key="label.mail.tracking.module.management" bundle="MAIL_TRACKING_RESOURCES" />
								</html:link>
							</li>
							<% } %>
							
							<% if(((MailTracking) mailTracking).isCurrentUserAbleToImportEntries()) { %>
							<li>
								<html:link page="<%= String.format("") %>" >
									<bean:message key="label.mail.tracking.entries.importation.action" bundle="MAIL_TRACKING_RESOURCES" />
								</html:link>
							</li>
							<% } %>
						</ul>
					</td>
				</tr>
			</logic:iterate>
		</tbody>
	</table>
	
</logic:notEmpty>
