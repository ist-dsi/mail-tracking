<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

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


<fr:form id="add.new.entry.form" action='<%= "/mailtracking.do?method=addNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' encoding="multipart/form-data">
	
	<h3><bean:message key="label.correspondence.details" bundle="MAIL_TRACKING_RESOURCES" /></h3>

	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.data" name="correspondenceEntryBean" schema='<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.create" : "module.mailtracking.correspondence.sent.entry.create" %>' >
		<fr:destination name="invalid" path='<%= "/mailtracking.do?method=addNewEntryInvalid&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:destination name="cancel" path='<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:layout name="tabular">
			<fr:property name="columnClasses" value=",,tderror"/>
			<fr:property name="requiredMarkShown" value="true" />
		</fr:layout>
	</fr:edit>
	
	<h3><bean:message key="label.correspondence.visibility" bundle="MAIL_TRACKING_RESOURCES" /></h3>
	
	<p class="mtop15 mbottom05"><bean:message key="message.correspondence.sent.entry.visibility" bundle="MAIL_TRACKING_RESOURCES" /></p>
	
	<fr:edit id="correspondence.entry.bean.visibility" name="correspondenceEntryBean" schema='<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.visibility.edit" : "module.mailtracking.correspondence.sent.entry.visibility.edit" %>' >
		<fr:destination name="invalid" path='<%= "/mailtracking.do?method=addNewEntryInvalid&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:destination name="cancel" path='<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:layout name="tabular">
			<fr:property name="classes" value="mtop05"/>
			<fr:property name="columnClasses" value=",,tderror"/>
			<fr:property name="requiredMarkShown" value="true" />
		</fr:layout>
	</fr:edit>
	
	<h3><bean:message key="label.correspondence.document" bundle="MAIL_TRACKING_RESOURCES" /></h3>

	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.create" >
		<fr:destination name="invalid" path='<%= "/mailtracking.do?method=addNewEntryInvalid&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:destination name="cancel" path='<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>' />
		<fr:layout name="tabular">
			<fr:property name="requiredMarkShown" value="true" />
		</fr:layout>
	</fr:edit>
	
	<html:submit><bean:message key="label.add" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
	<html:cancel><bean:message key="label.cancel" bundle="MAIL_TRACKING_RESOURCES" /></html:cancel>
	
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
