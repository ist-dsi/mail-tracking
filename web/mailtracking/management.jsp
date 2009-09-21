<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/dataTables/media/js/jquery.dataTables.js"%>"></script>

<style type="text/css" title="currentStyle">
	@import "<%= request.getContextPath() + "/javaScript/dataTables/media/css/demo_table.css" %>";
</style>


<script type="text/javascript">

$(document).ready(function() {
			$("#link.ajax.filter.correspondence > a").visible = false;
			var ajaxPostUrl = $("#filterCorrespondence > a").attr("href");

			$("#correspondenceTypeSpan").visible = false;
			var correspondenceTypeValue = $("#correspondenceTypeSpan").text();

			$(".display-sent").dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"sAjaxSource": ajaxPostUrl,
				"fnServerData": function(sSource, aoData, fnCallback){
					aoData.push({"name" : "correspondenceType", "value" : correspondenceTypeValue });
					
					$.ajax({
						"dataType": 'json',
						"type": "POST",
						"url": sSource,
						"data": aoData,
						"success": fnCallback
					})
				},
				"aoColumns": [
					/* Entry Number */ null,
					/* whenSent */ null,
					/* Recipient */ null,
					/* Subject */ null,
					/* Sender */ null,
					/* whenReceived */ null,
					/* links */ { "bSortable": false,
						"fnRender": function(oObj) {
							var links = "<" + "a href=\"" + oObj.aData[4].split(",")[0] + "\">Editar</a>,";
							links += "<" + "a href=\"" + oObj.aData[4].split(",")[1] + "\">Remover</a>";
							
							return links;
						} 
					}
				]
			}); 
	    } 
	); 

</script>

<script type="text/javascript">

$(document).ready(function() {
			$("#link.ajax.filter.correspondence > a").visible = false;
			var ajaxPostUrl = $("#filterCorrespondence > a").attr("href");

			$("#correspondenceTypeSpan").visible = false;
			var correspondenceTypeValue = $("#correspondenceTypeSpan").text();

			$(".display-received").dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"sAjaxSource": ajaxPostUrl,
				"fnServerData": function(sSource, aoData, fnCallback){
					aoData.push({"name" : "correspondenceType", "value" : correspondenceTypeValue });
					
					$.ajax({
						"dataType": 'json',
						"type": "POST",
						"url": sSource,
						"data": aoData,
						"success": fnCallback
					})
				},
				"aoColumns": [
					/* Entry Number */ null,
					/* whenReceived */ null,
					/* Sender */ null,
					/* whenSent */ null,
					/* senderLetterNumber */ null,
					/* Subject */ null,
					/* dispatch */ null,
					/* dispatchToWhom */ null,
					/* links */ { "bSortable": false,
						"fnRender": function(oObj) {
							var links = "<" + "a href=\"" + oObj.aData[4].split(",")[0] + "\">Editar</a>,";
							links += "<" + "a href=\"" + oObj.aData[4].split(",")[1] + "\">Remover</a>";
							
							return links;
						} 
					}
				]
			}); 
	    } 
	); 

</script>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />

<span id="filterCorrespondence"><html:link page='<%= "/mailtracking.do?method=ajaxFilterCorrespondence&amp;mailTrackingId=" + mailTrackingId %>'></html:link></span>

<bean:define id="correspondenceType" name="correspondenceType" />

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="label.last.correspondence.entries" bundle="MAIL_TRACKING_RESOURCES" /> </h3>

<fr:form id="search.parameters.simple.form" action="/mailtracking.do?method=prepare">
	<fr:edit id="search.parameters.simple.bean" name="searchParametersBean" visible="false" />
</fr:form>

<p>
<logic:equal name="correspondenceType" value="<%= CorrespondenceType.SENT.name() %>">
	<bean:message key="label.mail.tracking.sent.correspondence" bundle="MAIL_TRACKING_RESOURCES" />
	<html:link page="<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + CorrespondenceType.RECEIVED.name() + "&amp;mailTrackingId=" + mailTrackingId %>"><bean:message key="label.mail.tracking.received.correspondence" bundle="MAIL_TRACKING_RESOURCES" /></html:link>
</logic:equal>
<logic:equal name="correspondenceType" value="<%=  CorrespondenceType.RECEIVED.name() %>">
	<html:link page="<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + CorrespondenceType.SENT.name() + "&amp;mailTrackingId=" + mailTrackingId %>"><bean:message key="label.mail.tracking.sent.correspondence" bundle="MAIL_TRACKING_RESOURCES" /></html:link>
	<bean:message key="label.mail.tracking.received.correspondence" bundle="MAIL_TRACKING_RESOURCES" />
</logic:equal> 
</p>

<p>
	<html:link page="<%= "/mailtracking.do?method=prepareCreateNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId %>">
		<bean:message key="label.mail.tracking.create.new.entry" bundle="MAIL_TRACKING_RESOURCES"/>
	</html:link>
</p>
<logic:empty name="searchEntries">
	<bean:message key="message.searched.correspondence.entries.empty" bundle="MAIL_TRACKING_RESOURCES" /> 
</logic:empty>


<logic:notEmpty name="searchEntries">
<logic:equal name="correspondenceType" value="<%= CorrespondenceType.SENT.name() %>">
<fr:view name="searchEntries" schema="module.mailtracking.correspondence.sent.entries.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table display-sent"/>
		<fr:property name="renderCompliantTable" value="true" />

		<fr:property name="linkFormat(edit)" value="<%= "/mailtracking.do?method=prepareEditEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(edit)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(edit)" value="link.edit"/>
		<fr:property name="order(edit)" value="2" />		

		<fr:property name="linkFormat(delete)" value="<%= "/mailtracking.do?method=deleteEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(delete)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(delete)" value="link.delete"/>
		<fr:property name="confirmationKey(delete)" value="message.confirm.entry.delete" />
		<fr:property name="confirmationTitleKey(delete)" value="title.confirm.entry.delete" />
		<fr:property name="order(delete)" value="3" />
	</fr:layout>
</fr:view>
</logic:equal>

<logic:equal name="correspondenceType" value="<%= CorrespondenceType.RECEIVED.name() %>">
<fr:view name="searchEntries" schema="module.mailtracking.correspondence.received.entries.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table display-received"/>
		<fr:property name="renderCompliantTable" value="true" />

		<fr:property name="linkFormat(edit)" value="<%= "/mailtracking.do?method=prepareEditEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(edit)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(edit)" value="link.edit"/>
		<fr:property name="order(edit)" value="2" />		

		<fr:property name="linkFormat(delete)" value="<%= "/mailtracking.do?method=deleteEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(delete)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(delete)" value="link.delete"/>
		<fr:property name="confirmationKey(delete)" value="message.confirm.entry.delete" />
		<fr:property name="confirmationTitleKey(delete)" value="title.confirm.entry.delete" />
		<fr:property name="order(delete)" value="3" />
	</fr:layout>
</fr:view>
</logic:equal>

</logic:notEmpty>

