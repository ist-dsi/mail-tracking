<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>
<%@ page import="module.mailtracking.domain.CorrespondenceType" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryLog" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean" %>
<%@ page import="com.google.common.base.Strings" %>

<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
<%
    final String contextPath = request.getContextPath();
	final CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("correspondenceEntryBean");
	final String mailTrackingId=entryBean.getMailTracking().getExternalId();
	final String entryId=entryBean.getEntry().getExternalId();
	final CorrespondenceType correspondenceType=entryBean.getEntry().getType();
	final Boolean check = Boolean.valueOf(request.getAttribute("check").toString());
	final String message=(String)request.getAttribute("message");
%>

<div class="page-header">
	<h1><spring:message code="title.mail.tracking.application"
			text="Gestão de Correspondência" />
	</h1>
</div>
<br>

<p>
	<a id="back" href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId %>&amp;YearId=<%=entryBean.getEntry().getYear().getExternalId()%>&amp;check=<%=check %>' >
		<spring:message code="label.back" text="Go Back" />
	</a>
</p>
<p>
</p>
<% if(!Strings.isNullOrEmpty(message)) {%>
<div class="alert alert-danger ng-binding ng-hide" ng-show="error">
		<spring:message code="<%=message %>" text="<%=message %>" />
</div>
<%} %>
<p/>
<div>
	<h3>
	<%=entryBean.getMailTracking().getName().getContent()%>
	</h3>
</div>
<p>
</p>
<div class="ng-scope">
<table class="tstyle2 thleft tdleft table" id="entryHeader">
<tbody>
<tr>
<th scope="row"><spring:message code="label.registry.number.detailed" text=""/></th>
<td> <%=entryBean.getReference().toString()%></td>
</tr>
<tr>
<th scope="row"><spring:message code="label.mailTracking.table.mail" text="label.mailTracking.table.mail"/></th>
<td> <%=entryBean.getEntry().getType().getDescription()%></td>
</tr>
<tr>
      <th scope="row"><spring:message code="label.whenReceived" text=""/>
      </th>
      <td>
        <%=entryBean.getEntry().getType() == CorrespondenceType.SENT ? entryBean.getWhenSent() : entryBean.getWhenReceived()%>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.mailTracking.table.receiver" text=""/>
       
      </th>
      <td>
       <%=entryBean.getRecipient() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.subject" text=""/>
       
      </th>
      <td>
       <%=entryBean.getSubject() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.sender" text=""/>
        
      </th>
      <td>
        <%=entryBean.getSender() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.mailTracking.table.observations" text="label.mailTracking.table.observations"/>
       
      </th>
      <td>
        <%=entryBean.getObservations() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.visibility" text="label.visibility"/>  
      </th>
      <td>
        <spring:message code="<%=entryBean.getVisibility().getCustomEnum().getVisibilityDescriptionForEntry() %>" text=""/>
      </td>
    </tr>
</tbody>
</table>
</div>
<p><p>

<p><spring:message code="messsage.mail.tracking.deletion.reason.necessary" text="messsage.mail.tracking.deletion.reason.necessary"/></p>

<spring:url var="submitUrl"
	value="/mail-tracking/management/deleteEntry/" />
<form:form id="delForm" modelAttribute="correspondenceEntryBean" class="form-horizontal"
	method="POST" action="${submitUrl}">
<input type="hidden" name="mailTrackingId" value="<%=mailTrackingId%>"/>
<input type="hidden" name="entryId" value="<%=entryId%>"/>
<input type="hidden" name="entryBean" value="<%=entryBean%>"/>
<input type="hidden" name="check" value="<%=check%>"/>
<form:hidden path="entry.type"/>
	<div class="form-group">
		<form:label class="control-label col-sm-2" path="deletionReason" id='deletionReason' ><spring:message
				code="label.deletionReason"
				text="label.deletionReason" />
		</form:label>
		<div class="col-sm-10">
			<form:input class="form-control" path="deletionReason" />
		</div>
	</div>
	<div class="form-group">
		<div class="col-sm-2">
		<input class="btn btn-default" type="submit" value="<spring:message code="label.delete" text="label.delete"/>">
		</div>
	</div>
</form:form>

<script>
$("#delForm").validate({
	 rules:{
		 deletionReason: 'required',
		
	 },
	 messages:{
		 deletionReason: 'Campo Obrigatório',
		
		 },
	 submitHandler: function(form) {
           form.submit();
	 }
});
</script>



