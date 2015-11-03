<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryLog" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean" %>
<%@ page import="module.mailtracking.domain.Document" %>


<%
    final String contextPath = request.getContextPath();
	final CorrespondenceEntryBean entry = (CorrespondenceEntryBean) request.getAttribute("correspondenceEntryBean");
	final String mailTrackingId=entry.getMailTracking().getExternalId();
	final String entryId=entry.getEntry().getExternalId();
	final Boolean check = ((Boolean)request.getAttribute("check"));
	final String options = (String)request.getAttribute("options");

%>

<div class="page-header">
	<h1>
	<spring:message code="title.mail.tracking.application" text="title.mail.tracking.application" />
	</h1>
</div>
<br>
<p>
<span style="margin-right: 30px;">
	<a id="back" href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId %>&amp;YearId=<%=entry.getEntry().getYear().getExternalId()%>&amp;check=<%=check %>&amp;options=<%=options %>' >
		<spring:message code="label.back" text="Go Back" />
	</a>
	</span>
	<%if(entry.getEntry().isUserAbleToEdit() && entry.getEntry().isActive() ){ %>
		<span style="margin-right: 30px;">
		<a id="edit" href='<%=contextPath %>/mail-tracking/management/prepareEditEntry?entryId=<%=entryId %>&check=<%=check%>&amp;options=<%=options %>'>
		<spring:message code="label.edit" text="Edit" /></a>
		</span>
	<%} %>
</p>
<p>
</p>
<div>
	<h3>
	<%=entry.getMailTracking().getName().getContent()%>
	</h3>
</div>
<p>

<div class="ng-scope">
<table class="tstyle2 thleft tdleft table" id="entryHeader">
<tbody>
<tr>
<th scope="row"><spring:message code="label.registry.number.detailed" text=""/></th>
<td> <%=entry.getReference().toString()%></td>
</tr>
<tr>
<th scope="row"><spring:message code="label.mailTracking.table.mail" text="Correio"/></th>
<td> <%=entry.getEntry().getType().getDescription()%></td>
</tr>
     <% if(entry.getEntry().getType()==CorrespondenceType.RECEIVED) { %>
	<tr>
      <th scope="row"><spring:message code="label.whenReceived" text=""/>
      </th>
      <td>
       <%=entry.getWhenReceived()==null?"": entry.getWhenReceived() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.sender" text=""/>  
      </th>
      <td>
        <%=entry.getSender() %>
      </td>
    </tr>
    
    <tr>
      <th scope="row"><spring:message code="label.correspondence.date.detailed" text=""/>
       
      </th>
      <td>
       <%=entry.getWhenSent()==null?"":entry.getWhenSent() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.sender.letter.number.detailed" text=""/>
       
      </th>
      <td>
       <%=entry.getSenderLetterNumber() %>
      </td>
    </tr>
     <tr>
      <th scope="row"><spring:message code="label.subject" text=""/>
       
      </th>
      <td>
       <%=entry.getSubject() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.mailTracking.table.receiver" text=""/>
       
      </th>
      <td>
       <%=entry.getRecipient() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.dispatch.made.to" text=""/>
       
      </th>
      <td>
       <%=entry.getDispatchedToWhom()==null?"":entry.getDispatchedToWhom() %>
      </td>
    </tr>   
    <tr>
      <th scope="row"><spring:message code="label.mailTracking.table.observations" text=""/>
       
      </th>
      <td>
        <%=entry.getObservations() %>
      </td>
    </tr>
   <% } else if(entry.getEntry().getType() == CorrespondenceType.SENT){%>
   		 <tr>
      <th scope="row"><spring:message code="label.correspondence.date.detailed" text=""/>
       
      </th>
      <td>
       <%=entry.getWhenSent()==null?"":entry.getWhenSent() %>
      </td>
    </tr>
     <tr>
      <th scope="row"><spring:message code="label.dispatch" text=""/>
       
      </th>
      <td>
       <%=entry.getRecipient() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.subject" text=""/>
       
      </th>
      <td>
       <%=entry.getSubject() %>
      </td>
    </tr>
    <tr>
      <th scope="row"><spring:message code="label.sender" text=""/>  
      </th>
      <td>
        <%=entry.getSender() %>
      </td>
    </tr>
     <tr>
      <th scope="row"><spring:message code="label.mailTracking.table.observations" text=""/>
       
      </th>
      <td>
        <%=entry.getObservations() %>
      </td>
    </tr>
   <%} %>
</tbody>
</table>
</div>
<p><p>
<div>
<h3 class="mtop2 mbottom05"><spring:message code="title.mailtracking.associate.documents" text="Documentos Associados"></spring:message></h3>
</div>
<%if(entry.getEntry().getActiveDocuments().isEmpty()){%>
    <p class="mtop05">
		<em><spring:message code="message.associated.documents.empty" text="message.associated.documents.empty" /></em>
	</p>
<% }else{%>
<div class="ng-scope">
	
	<table class="table" id="documents">
		<thead>
			<tr>
				<th><spring:message code="label.name" text="label.name" /></th>
				<th><spring:message code="label.description" text="label.description" /></th>
			</tr>
		</thead>
		<tbody id="searchResultsRespPerson">
		<% for(Document doc : entry.getEntry().getActiveDocuments()){%>
			<tr class="ng-scope">
									<td>
										<%=doc.getFilename()%>
									</td>
									<td>
										<%=doc.getDescription()%>
									</td>
									<td>
										<span><a href="<%=contextPath%>/mail-tracking/management/downLoad/<%=doc.getExternalId()%>/<%=entry.getEntry().getExternalId() %>">
										<img src="<%= contextPath %>/images/view.gif" alt="Visualizar"></img></a></span>
												
									</td>
			<%} %>
		</tbody>
	</table>
</div>
<%} %>

<p><p>
<div>
<h3 class="mtop2 mbottom05"><spring:message code="title.mailtracking.access.record" text="Registos de Acesso"></spring:message></h3>
</div>
<%if(entry.getEntry().getSortedLogs().isEmpty()){%>
    <p class="mtop05">
		<em><spring:message code="message.logs.empty" text="Empty Logs" /></em>
	</p>
<% }else{%>
<div class="ng-scope">
<table class="table" id="logs">
		<thead>
			<tr>
				<th><spring:message code="label.logs.whenOperation" text="label.logs.whenOperation" /></th>
				<th><spring:message code="label.logs.username" text="label.logs.username" /></th>
				<th><spring:message code="label.logs.operation.description" text="Operações" /></th>
			</tr>
		</thead>
		<tbody id="logResult">
		<% for(CorrespondenceEntryLog log:entry.getEntry().getLogsSet()) {%>
			<tr class="ng-scope">
									<td>
										<%=log.getWhenOperation()%>
									</td>
									<td>
										<%=log.getUsername()%>
									</td>
									<td>
										<%=log.getOperationDescription()%>
									</td>
			<%}%>
		</tbody>
	</table>
</div>
<%}%>
