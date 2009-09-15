<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/dataTables/media/js/jquery.dataTables.js"%>"></script>

<style type="text/css" title="currentStyle">
	@import "<%= request.getContextPath() + "/javaScript/dataTables/media/css/demo_table.css" %>";
</style>


<script type="text/javascript">

$(document).ready(function() {
			$("#link.ajax.filter.correspondence > a").visible = false;
			var ajaxPostUrl = $("#filterCorrespondence > a").attr("href");

			$(".display").dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"sAjaxSource": ajaxPostUrl,
				"fnServerData": function(sSource, aoData, fnCallback){
					$.ajax({
						"dataType": 'json',
						"type": "POST",
						"url": sSource,
						"data": aoData,
						"success": fnCallback
					})
				},
				"aoColumns": [
					/* Sender */ null,
					/* Recipient */ null,
					/* Subject */ null,
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

<span id="filterCorrespondence"><html:link page="/mailtracking.do?method=ajaxFilterCorrespondence" ></html:link></span>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="label.last.correspondence.entries" bundle="MAIL_TRACKING_RESOURCES" /> </h3>

<fr:form id="search.parameters.simple.form" action="/mailtracking.do?method=prepare">
	<fr:edit id="search.parameters.simple.bean" name="searchParametersBean" visible="false" />
<%-- 	<fr:edit id="search.parameters.simple.bean.slot" name="searchParametersBean" slot="allStringFieldsFilter" />
	<html:submit><bean:message key="label.filter" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>--%>
</fr:form>
<%-- 
<div>
<fr:form id="search.parameters.extended.form" action="/mailtracking.do?method=prepare">
	<fr:edit id="search.parameters.extended.bean" name="searchParametersBean" schema="module.mailtracking.extended.search.edit" />
	<html:submit><bean:message key="label.filter" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
</div>
--%>

<logic:empty name="searchEntries">
	<bean:message key="message.searched.correspondence.entries.empty" bundle="MAIL_TRACKING_RESOURCES" /> 
</logic:empty>

<logic:notEmpty name="searchEntries">
<fr:view name="searchEntries" schema="module.mailtracking.correspondence.entries.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table display"/>
		<fr:property name="renderCompliantTable" value="true" />

		<fr:property name="linkFormat(edit)" value="/mailtracking.do?method=prepareEditEntry&entryId=${externalId}"/>
		<fr:property name="bundle(edit)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(edit)" value="link.edit"/>
		<fr:property name="order(edit)" value="2" />		

		<fr:property name="linkFormat(delete)" value="/mailtracking.do?method=deleteEntry&entryId=${externalId}"/>
		<fr:property name="bundle(delete)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(delete)" value="link.delete"/>
		<fr:property name="confirmationKey(delete)" value="message.confirm.entry.delete" />
		<fr:property name="confirmationTitleKey(delete)" value="title.confirm.entry.delete" />
		<fr:property name="order(delete)" value="3" />
	</fr:layout>
</fr:view>

</logic:notEmpty>

<h3><bean:message key="label.add.new.entry" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<fr:form id="add.new.entry.form" action="/mailtracking.do?method=addNewEntry">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" schema="module.mailtracking.correspondence.entry.edit" />
	
	<html:submit><bean:message key="label.add" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
