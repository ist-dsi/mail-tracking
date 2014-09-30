<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking.correspondence.entry.delete" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<p>
	<html:link href='<%= request.getContextPath() + "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType%>' >
		<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
</p>

<p><bean:message key="messsage.mail.tracking.deletion.reason.necessary" bundle="MAIL_TRACKING_RESOURCES"/></p>

<fr:form id="delete.entry.form" action='<%= "/mailtracking.do?method=deleteEntry&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>'>
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.delete" name="correspondenceEntryBean" schema="module.mailtracking.correspondence.entry.delete" >
		<fr:layout name="tabular">
			<fr:property name="columnClasses" value=",,tderror"/>
		</fr:layout>
	</fr:edit>
	
	<html:submit><bean:message key="label.delete" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>

