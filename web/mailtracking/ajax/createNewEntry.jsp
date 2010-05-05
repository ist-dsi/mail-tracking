<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<h2><bean:message key="title.mail.tracking.create.new.entry" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<div>
	<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
		<p>
			<span class="error0"> <bean:write name="message" /></span>
		</p>
	</html:messages>
</div>

<fr:form id="entryForm" action="<%= "/mailtracking.do?method=addNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>" encoding="multipart/form-data">
	
	<h3><bean:message key="label.correspondence.details" bundle="MAIL_TRACKING_RESOURCES" /></h3>

	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.data" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.create" : "module.mailtracking.correspondence.sent.entry.create" %>" >
		<fr:layout name="tabular">
			<fr:property name="columnClasses" value=",,tderror"/>
			<fr:property name="requiredMarkShown" value="true" />
		</fr:layout>
	</fr:edit>

	
	<html:submit onclick="<%=  String.format("submitForm('%s'); return false;", CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "fast-received-entry-creation-submission-link" : "fast-sent-entry-creation-submission-link") %>">
		<bean:message key="label.add" bundle="MAIL_TRACKING_RESOURCES" />
	</html:submit>
	<html:cancel onclick="closeFastEntryCreation(); return false;">
		<bean:message key="label.cancel" bundle="MAIL_TRACKING_RESOURCES" />
	</html:cancel>
	
</fr:form>


<script type="text/javascript">	
	$('document').ready(function() {
		$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("disabled", true);
		
		$("input[name$='owner_text']").change(function() {
			$('form').find("input[name$='owner']").attr("value", $("input[name$='owner_text']").attr("value"));
			$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("disabled", true);
			$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("checked", false);
		});

		$("input[name$='_AutoComplete']").change(function() {
			var auto_complete_input = $("input[name$='owner_AutoComplete']");
			var is_person_selected = auto_complete_input.attr("value") != 'custom' || auto_complete_input.attr("value") != '';
			$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("disabled", false);
		});
	});

</script>
