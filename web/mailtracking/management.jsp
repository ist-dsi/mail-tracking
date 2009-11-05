<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<bean:define id="correspondenceType" name="correspondenceType" />


<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/dataTables/media/js/jquery.dataTables.js"%>"></script>

<style>
#list_tabs{
clear:both;
display:block;
border-bottom:1px solid #CECECE;
margin-bottom: 15px;
}
.tabMenu {
text-align:left;
margin: 15px 0 4px 0;
padding: 0;
}
.tabMenu li {
display:inline;
margin:0;
}
.tabMenu li#on_lists_tab {
padding-left: 10px;
}
.tabMenu li a {
margin-right:1px;
display:inline;
padding:7px 15px 7px 15px;
background-color: #eee;
text-decoration:none;
-moz-border-radius:3px 3px 0 0;
-webkit-border-top-left-radius:3px;
-webkit-border-top-right-radius:3px;
}
.tabMenu li a:hover {
background-color:#E6E6E6;
}
.tabMenu li.active a {
border:1px solid #c4c4c4;
color:#555;
text-decoration: none !important;
background-color:#fff;
border-bottom:1px solid #fff;
padding:6px 14px 7px 14px;
margin-right:1px;
}


a.view {
background: url(view01.gif) center center no-repeat;
display: block;
width: 28px;
height: 24px;
margin: 0 auto;
}
a.edit {
background: url(edit01.gif) center center no-repeat;
display: block;
width: 28px;
height: 24px;
margin: 0 auto;
}
a.delete {
background: url(delete01.gif) center center no-repeat;
display: block;
width: 28px;
height: 24px;
margin: 0 auto;
}
a.file {
background: url(docs01.gif) center center no-repeat;
display: block;
width: 28px;
height: 24px;
margin: 0 auto;
color: #555;
font-weight: normal;
padding-top: 2px;
text-decoration: none !important;
}
th.action, td.action {
text-align: center !important;
width: 37px;
padding-top: 0;
padding-bottom: 0;
}
</style>

<style type="text/css" title="currentStyle">
	@import "<%= request.getContextPath() + "/javaScript/dataTables/media/css/demo_table.css" %>";
	
	.saviourDiv {
		height: 30px;
	}
</style>


<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />


<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<logic:equal name="correspondenceType" value="<%= CorrespondenceType.SENT.name() %>">
	<h3><bean:message key="label.last.correspondence.sent.entries" bundle="MAIL_TRACKING_RESOURCES" /> </h3>
</logic:equal>

<logic:equal name="correspondenceType" value="<%=  CorrespondenceType.RECEIVED.name() %>">
	<h3><bean:message key="label.last.correspondence.received.entries" bundle="MAIL_TRACKING_RESOURCES" /> </h3>
</logic:equal>

<fr:form id="search.parameters.simple.form" action="/mailtracking.do?method=prepare">
	<fr:edit id="search.parameters.simple.bean" name="searchParametersBean" visible="false" />
</fr:form>

<div id="list_tabs">
<ul class="tabMenu">
<logic:equal name="correspondenceType" value="<%= CorrespondenceType.SENT.name() %>">
	<li id="on_lists_tab" class="active">
		<bean:message key="label.mail.tracking.sent.correspondence" bundle="MAIL_TRACKING_RESOURCES" />
	</li>
	<li id="my_lists_tab">
		<html:link page="<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + CorrespondenceType.RECEIVED.name() + "&amp;mailTrackingId=" + mailTrackingId %>"><bean:message key="label.mail.tracking.received.correspondence" bundle="MAIL_TRACKING_RESOURCES" /></html:link>
	</li>
</logic:equal>
<logic:equal name="correspondenceType" value="<%=  CorrespondenceType.RECEIVED.name() %>">
	<li id="on_lists_tab">
		<html:link page="<%= "/mailtracking.do?method=prepare&amp;correspondenceType=" + CorrespondenceType.SENT.name() + "&amp;mailTrackingId=" + mailTrackingId %>"><bean:message key="label.mail.tracking.sent.correspondence" bundle="MAIL_TRACKING_RESOURCES" /></html:link>
	</li>
	<li id="my_lists_tab" class="active">
		<bean:message key="label.mail.tracking.received.correspondence" bundle="MAIL_TRACKING_RESOURCES" />
	</li>
</logic:equal>
</ul>
</div>

<logic:equal name="mailTracking" property="currentUserOperator" value="true"> 
<p>
	<html:link page="<%= "/mailtracking.do?method=prepareCreateNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId %>">
		<bean:message key="label.mail.tracking.create.new.entry" bundle="MAIL_TRACKING_RESOURCES"/>
	</html:link>
</p>
</logic:equal>

<logic:empty name="searchEntries">
	<bean:message key="message.searched.correspondence.entries.empty" bundle="MAIL_TRACKING_RESOURCES" /> 
</logic:empty> 


<style type="text/css" title="currentStyle">
	th.actions {
		width : 140px;
	}
</style>

<logic:notEmpty name="searchEntries">
<fr:view name="searchEntries" schema="<%= CorrespondenceType.SENT.name().equals(correspondenceType) ? "module.mailtracking.correspondence.sent.entries.view" : "module.mailtracking.correspondence.received.entries.view" %>" >
	<fr:layout name="ajax-tabular">
		<fr:property name="classes" value="table display-sent"/>
		
		<fr:property name="headerClasses" value="<%= CorrespondenceType.SENT.name().equals(correspondenceType) ? ",,,,,actions" : ",,,,,,actions" %>" />
		<fr:property name="columnClasses" value="<%= CorrespondenceType.SENT.name().equals(correspondenceType) ? ",,,,,aleft" : ",,,,,,aleft" %>" />
		
		<fr:property name="ajaxSourceUrl" value="/mailtracking.do" />

		<fr:property name="linkFormat(view)" value="<%= "/mailtracking.do?method=viewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>" />
		<fr:property name="bundle(view)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(view)" value="link.view"/>
		<fr:property name="order(view)" value="2" />
		<fr:property name="visibleIf(view)" value="userAbleToView" />
		<fr:property name="icon(view)" value="view" />

		<fr:property name="linkFormat(edit)" value="<%= "/mailtracking.do?method=prepareEditEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(edit)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(edit)" value="link.edit"/>
		<fr:property name="order(edit)" value="3" />
		<fr:property name="visibleIf(edit)" value="userAbleToEdit" />
		<fr:property name="icon(edit)" value="edit" />

		<fr:property name="linkFormat(delete)" value="<%= "/mailtracking.do?method=prepareDeleteEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId=" + mailTrackingId + "&amp;entryId=${externalId}" %>"/>
		<fr:property name="bundle(delete)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(delete)" value="link.delete"/>
		<fr:property name="order(delete)" value="4" />
		<fr:property name="visibleIf(delete)" value="userAbleToDelete" />
		<fr:property name="icon(delete)" value="delete" />
		
		<fr:property name="linkFormat(document)" value="mailtracking.do" />
		<fr:property name="bundle(document)" value="MAIL_TRACKING_RESOURCES" />
		<fr:property name="key(document)" value="link.view.document" />
		<fr:property name="order(document)" value="5" />
		<fr:property name="visibleIf(document)" value="userAbleToViewDocument" />
		<fr:property name="icon(document)" value="document" />
				
		<fr:property name="extraParameter(method)" value="ajaxFilterCorrespondence" />
		<fr:property name="extraParameter(correspondenceType)" value="<%= (String) correspondenceType %>" />
		<fr:property name="extraParameter(mailTrackingId)" value="<%= (String) mailTrackingId %>" />
	</fr:layout>
</fr:view>

</logic:notEmpty>

