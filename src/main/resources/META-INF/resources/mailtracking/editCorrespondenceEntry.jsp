<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking.edit.entry" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<bean:define id="entryId" name="correspondenceEntryBean" property="entry.externalId" />

<p class="mbottom05">
	<html:link href="<%= request.getContextPath() + "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType%>" >
		« <bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
</p>

<div>
	<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
		<p>
			<span class="error0"> <bean:write name="message" /></span>
		</p>
	</html:messages>
</div>


<fr:form id="add.new.entry.form" action="<%= "/mailtracking.do?method=editEntry&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" visible="false" />
	
	<fr:edit id="correspondence.entry.bean.data" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.edit" : "module.mailtracking.correspondence.sent.entry.edit" %>" >
		<fr:destination name="invalid" path="<%= "/mailtracking.do?method=addNewEntryInvalid&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>" />
		<fr:destination name="cancel" path="<%= "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>" />
		<fr:layout name="tabular">
			<fr:property name="columnClasses" value=",,tderror"/>
		</fr:layout>		
	</fr:edit>
	
	<p class="mbottom05"><bean:message key="message.correspondence.sent.entry.visibility" bundle="MAIL_TRACKING_RESOURCES" /></p>
	
	<fr:edit id="correspondence.entry.bean.visibility" name="correspondenceEntryBean" schema="<%= CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entry.visibility.edit" : "module.mailtracking.correspondence.sent.entry.visibility.edit" %>" >
		<fr:destination name="cancel" path="<%= "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType %>" />
		<fr:layout name="tabular">
			<fr:property name="classes" value="mtop05"/>
			<fr:property name="columnClasses" value=",,tderror"/>
		</fr:layout>
	</fr:edit>	
	
	<html:submit><bean:message key="label.save" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
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

<h3 class="mtop2"><bean:message key="title.associated.documents" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<logic:empty name="correspondenceEntryBean" property="entry.documents" >
	<p>
		<em><bean:message key="message.associated.documents.empty" bundle="MAIL_TRACKING_RESOURCES" /></em>
	</p>
</logic:empty>


<logic:notEmpty name="correspondenceEntryBean" property="entry.documents" >
	<bean:define id="associatedDocuments" name="correspondenceEntryBean" property="entry.activeDocuments"/>
	<fr:view name="associatedDocuments" schema="module.mailtracking.associated.document.view" >
		<fr:layout name="tabular">
			<fr:property name="classes" value="" />
			<fr:property name="linkFormat(view)" value="<%= "/mailtracking.do?method=downloadFile&amp;fileId=${externalId}&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType + "&amp;entryId=" + entryId %>"/>
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

<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
	<p>
		<span class="error0"><bean:write name="message" /></span>
	</p>
</html:messages>

<p class="mtop2 mbottom05">
	<strong><bean:message key="label.associate.new.document" bundle="MAIL_TRACKING_RESOURCES" /></strong>
</p>
<fr:form id="associate.document.entry.form" action="<%= "/mailtracking.do?method=associateDocument&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType + "&amp;entryId=" + entryId %>" encoding="multipart/form-data">
	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.edit">
		<fr:layout name="tabular">
			<fr:property name="classes" value="mtop05" />
		</fr:layout> 
	</fr:edit>
	<html:submit><bean:message key="label.bind" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
