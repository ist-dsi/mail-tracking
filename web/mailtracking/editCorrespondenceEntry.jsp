<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<bean:define id="entryId" name="correspondenceEntryBean" property="entry.externalId" />

<html:link href="<%= request.getContextPath() + "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType%>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<h3><bean:message key="title.mail.tracking.correspondence.entry.edit" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<fr:form id="add.new.entry.form" action="<%= "/mailtracking.do?method=editEntry&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.data" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.edit" : "module.mailtracking.correspondence.sent.entry.edit" %>" />
	
	<p><bean:message key="message.correspondence.sent.entry.visibility" bundle="MAIL_TRACKING_RESOURCES" /></p>
	
	<fr:edit id="correspondence.entry.bean.visibility" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.visibility.edit" : "module.mailtracking.correspondence.sent.entry.visibility.edit" %>" />	
	
	<html:submit><bean:message key="label.edit" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
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

<script type="text/javascript">
	$('document').ready(function() {
		var auto_complete_input = $("input[name$='owner_AutoComplete']");
		var is_person_selected = auto_complete_input.attr("value") != 'custom' || auto_complete_input.attr("value") != '';
		$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("disabled", !is_person_selected);
		
		var value = $('form').find("input[name$='owner_text']").attr("value");
		$('form').find("input[name$='owner']").attr("value", value);
		
		$("input[name$='owner_text']").change(function() {
			$('form').find("input[name$='owner']").attr("value", $("input[name$='owner_text']").attr("value"));
		});		

		$("input[name$='_AutoComplete']").change(function() {
			var auto_complete_input = $("input[name$='owner_AutoComplete']");
			var is_person_selected = auto_complete_input.attr("value") != 'custom' || auto_complete_input.attr("value") != '';
			$("input[value='ONLY_OWNER_AND_OPERATOR']").attr("disabled", false);
		});
		
	});
</script>

<h3><bean:message key="title.associated.documents" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<logic:empty name="correspondenceEntryBean" property="entry.documents" >
	<em><bean:message key="message.associated.documents.empty" bundle="MAIL_TRACKING_RESOURCES" /></em>
</logic:empty>


<logic:notEmpty name="correspondenceEntryBean" property="entry.documents" >

<bean:define id="associatedDocuments" name="correspondenceEntryBean" property="entry.activeDocuments"/>

<fr:view name="associatedDocuments" schema="module.mailtracking.associated.document.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table" />
		
		<fr:property name="linkFormat(view)" value="<%= "/mailtracking.do?method=downloadFile&amp;fileId=${externalId}&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>"/>
		<fr:property name="bundle(view)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(view)" value="link.view"/>
		<fr:property name="order(view)" value="2" />
		<fr:property name="icon(view)" value="view" />

		<fr:property name="linkFormat(delete)" value="<%= "/mailtracking.do?method=deleteDocument&amp;fileId=${externalId}&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType + "&amp;entryId=" + entryId %>"/>
		<fr:property name="bundle(delete)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(delete)" value="link.delete"/>
		<fr:property name="order(delete)" value="3" />
		<fr:property name="icon(delete)" value="delete" />
		
	</fr:layout> 
</fr:view>
</logic:notEmpty>

<div>
<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>
</div>

<p></p><strong><bean:message key="label.associate.new.document" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<fr:form id="associate.document.entry.form" action="<%= "/mailtracking.do?method=associateDocument&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType + "&amp;entryId=" + entryId %>" encoding="multipart/form-data">
	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.edit" />
	
	<html:submit><bean:message key="label.bind" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
