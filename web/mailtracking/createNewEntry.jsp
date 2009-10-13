<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="label.add.new.entry" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<div>
<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>
</div>

<fr:form id="add.new.entry.form" action="<%= "/mailtracking.do?method=addNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>" encoding="multipart/form-data">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.data" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.edit" : "module.mailtracking.correspondence.sent.entry.edit" %>" />
	
	<p><bean:message key="message.correspondence.sent.entry.visibility" bundle="MAIL_TRACKING_RESOURCES" /></p>
	
	<fr:edit id="correspondence.entry.bean.visibility" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.visibility.edit" : "module.mailtracking.correspondence.sent.entry.visibility.edit" %>" />
	
	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.create" />
	
	<html:submit><bean:message key="label.add" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>

<script type="text/javascript">
	$('document').ready(function() {
		$('form').submit(function() {
			if($('form').find("input[name$='owner']").attr("value").length == 0) {
				var value = $('form').find("input[name$='owner_OldValue']").attr("value");
				$('form').find("input[name$='owner']").attr("value", value);
			}
		})
	});
</script>
