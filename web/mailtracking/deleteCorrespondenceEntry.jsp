<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<html:link href="<%= request.getContextPath() + "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType%>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<h3><bean:message key="title.mail.tracking.correspondence.entry.delete" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<p><bean:message key="messsage.mail.tracking.deletion.reason.necessary" /></p>

<fr:form id="delete.entry.form" action="<%= "/mailtracking.do?method=deleteEntry&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.delete" name="correspondenceEntryBean" schema="module.mailtracking.correspondence.entry.delete" />
	
	<html:submit><bean:message key="label.delete" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>

