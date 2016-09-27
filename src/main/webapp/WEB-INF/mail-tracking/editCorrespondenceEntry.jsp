

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import="java.beans.Visibility"%>
<%@page import="module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum"%>
<%@page import="module.mailtracking.presentationTier.MailTrackingAction.AssociateDocumentBean"%>
<%@ page import="module.mailtracking.domain.CorrespondenceType" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryLog" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean" %>
<%@ page import="module.mailtracking.domain.Document" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryVisibility" %>
<%@ page import="module.organization.domain.Person" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.common.base.Strings" %>


<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>



<%
    final String contextPath = request.getContextPath();
	final CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("entryBean");
	final AssociateDocumentBean doc =(AssociateDocumentBean)request.getAttribute("associateDocumentBean");
	final String mailTrackingId=entryBean.getMailTracking().getExternalId();
	final String entryId=entryBean.getEntry().getExternalId();
	final List<CustomEnum> entryVisibility = (List<CustomEnum>) request.getAttribute("visibilities");
	final String message=(String)request.getAttribute("message");
	final Boolean check = Boolean.valueOf(request.getAttribute("check").toString());
	final String options = (String)request.getAttribute("options");
%>


<script src='<%= contextPath + "/webjars/jquery-ui/1.11.1/jquery-ui.js" %>'></script>

<style>
.ui-autocomplete-loading{background: url(<%= contextPath %>/images/spinner.gif) no-repeat right center}
</style>

<div class="page-header">
	<h1>
	<spring:message code="title.mail.tracking.application"
			text="Gestão de Correspondência" />
	</h1>
</div>
<br>
<p>
<span style="margin-right: 30px;">
	<a id="back" href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId %>&amp;YearId=<%=entryBean.getEntry().getYear().getExternalId()%>&amp;check=<%=check %>&amp;options=<%=options %>' >
		<spring:message code="label.back" text="Go Back" />
	</a>
	</span>
</p>
<div>
	<h3>
	<%=entryBean.getMailTracking().getName().getContent()%>
	</h3>
</div>
<p>
</p>

<% if(!Strings.isNullOrEmpty(message)) {%>
<div class="alert alert-danger ng-binding ng-hide" ng-show="error">
		<spring:message code="<%=message %>" text="<%=message %>" />
</div>
<%} %>


<spring:url var="submitUrl"
	value="/mail-tracking/management/editEntry/" />
<form:form id="editForm" modelAttribute="entryBean" class="form-horizontal"
	method="POST" action="${submitUrl}" >
	${csrf.field()}
<input type="hidden" name="mailTrackingId" value="<%=mailTrackingId%>"/>
<input type="hidden" name="entryId" value="<%=entryId%>"/>
<input type="hidden" name="entryBean" value="<%=entryBean%>"/>
<input type="hidden" name="check" value="<%=check%>"/>
<input type="hidden" name="options" value="<%=options%>"/>
<input type="hidden" name="owner" value="<%=entryBean.getOwner()!=null?entryBean.getOwner().getExternalId():""%>"/>
<input type="hidden" name="reference" value="<%=entryBean.getReference()%>"/>  
<form:hidden path="entry.type"/>



	
	<div class="form-group">
		<form:label class="control-label col-sm-2" path="entry.type"  ><spring:message
				code="label.mailTracking.table.mail"
				text="label.mailTracking.table.mail" />
		</form:label>
		<div class="col-sm-10">
			<form:input id='type' class="form-control" path="entry.type.description" disabled="true"/>
		</div>
	</div>


	<div class="form-group">
		<form:label class="control-label col-sm-2" path="reference" id='reference' ><spring:message
				code="label.mission.processNumber"
				text="label.mission.processNumber" />
		</form:label>
		<div class="col-sm-10">
			<form:input class="form-control" path="reference" disabled="true"/>
		</div>
	</div>

  <%if(entryBean.getEntry().getType() == CorrespondenceType.RECEIVED) {%>  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="whenReceived" >
    <spring:message code="label.whenReceived" text=""/></form:label>
    <div class="col-sm-10">
      <div class="input-group">

        <form:input size="10" maxlength="10" path="whenReceived" value="<%=entryBean.getWhenReceived()==null?"":entryBean.getWhenReceived().toString() %>" class="form-control datepickers" />
        <span class="input-group-addon">yyyy-mm-dd</span>
      </div>
    </div>
  </div>
 
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="sender">
	<spring:message code="label.sender" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="sender" class="form-control autocompi" autocomplete="off"/>   
      </span>
    </div>
  </div>
   
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="whenSent" id="sent">
	<spring:message code="label.correspondence.date.detailed" text=""/></form:label>
    <div class="col-sm-10">
      <div class="input-group">
        <form:input size="10" maxlength="10" path="whenSent" value="<%=entryBean.getWhenSent()!=null?entryBean.getWhenSent().toString():"" %>" class="form-control datepickers"/>
        <span class="input-group-addon">yyyy-mm-dd</span>
      </div>
    </div>
  </div>
    
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="senderLetterNumber">
    <spring:message code="label.sender.letter.number.detailed" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input path="senderLetterNumber" class="form-control" />
      </span>
    </div>
  </div>
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="subject">
    <spring:message code="label.subject" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="subject" class="form-control" />  
      </span>
    </div>
  </div>
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="recipient" >
    <spring:message code="label.mailTracking.table.receiver" text=""/></form:label>
    <div class="col-sm-10">
    	<form:input path="recipient" class="autocompi form-control" />  
    </div>
   </div>
   
 <div class="form-group">
    <form:label class="control-label col-sm-2" path="dispatchedToWhom">
	<spring:message code="label.dispatch.made.to" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="dispatchedToWhom"  class="form-control" />
        
      </span>
    </div>
  </div>
  
  
  <%}else if(entryBean.getEntry().getType() == CorrespondenceType.SENT){ %>
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="whenSent">
    <spring:message code="label.correspondence.date.detailed" text=""/></form:label>
    <div class="col-sm-10">
      <div class="input-group">
      
        <form:input size="10" maxlength="10" path="whenSent" value="<%=entryBean.getWhenSent()!=null?entryBean.getWhenSent().toString():"" %>" class="form-control formataData datepickers"/>
        <span class="input-group-addon">yyyy-mm-dd</span>
      </div>
    </div>
  </div>
   <div class="form-group">
    <form:label class="control-label col-sm-2" path="recipient" >
    <spring:message code="label.mailTracking.table.receiver" text=""/></form:label>
    <div class="col-sm-10">
    	<form:input path="recipient" class="autocompi form-control" />  
    </div>
   </div>
    <div class="form-group">
    <form:label class="control-label col-sm-2" path="subject">
	<spring:message code="label.subject" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="subject" class="form-control" />  
      </span>
    </div>
  </div>
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="sender">
	<spring:message code="label.sender" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="sender" class="form-control autocompi" autocomplete="off"/>   
      </span>
    </div>
  </div>
  
  <%} %>
  <div class="form-group">
    <form:label path="observations" class="control-label col-sm-2">
    <spring:message code="label.mailTracking.table.observations" text=""/></form:label>
    <div class="col-sm-10">
      <form:textarea path="observations" rows="4" class="form-control" cols="45"></form:textarea>
    </div>
  </div> 
	<p class="mbottom05"><spring:message code="message.mailtracking.edit.correspondence.visibility" text=""/></p>
  <div class="mtop05">
  <div class="form-group">
    <label for="visibility" class="control-label col-sm-2"><spring:message code="label.visibility" text="Visible for"/></label>
    <div class="col-sm-10">
      <ul style="list-style: none; padding: 0; margin: 0;" id="visibility" class="nobullet" >
        <% for (CustomEnum c : entryVisibility){ %>
        <li>
          <span>
            <form:radiobutton path="entry.visibility" value="<%=c.getCustomEnum() %>" /><spring:message code="<%= c.getCustomEnum().getVisibilityDescriptionForEntry() %>" text=""/>  
          </span>
        </li>
        <%} %>
        
      </ul>
    </div>
  </div>
</div>
<div class="form-group">
	<div class="col-sm-2">
	<input class="btn btn-default" type="submit" value="<spring:message code="mailTracking.button.save" text=""/>">
	<a id="back" href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId %>&amp;YearId=<%=entryBean.getEntry().getYear().getExternalId()%>&amp;check=<%=check %>&amp;options=<%=options %>' >
	<input class="btn btn-default" type="button" value="<spring:message code="mailTracking.button.cancel" text=""/>"/>
	</a>	
	</div>
</div>
</form:form>
<p><p>
<div>
<h3 class="mtop2 mbottom05"><spring:message code="title.mailtracking.associate.documents" text="Documentos Associados"/></h3>
</div>
<%if(entryBean.getEntry().getActiveDocuments().isEmpty()){%>
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
		<% for(Document document : entryBean.getEntry().getActiveDocuments()){%>
			<tr class="ng-scope">
									<td>
										<%=document.getFilename()%>
									</td>
									<td>
										<%=document.getDescription()%>
									</td>
									<td>
										<span><a href="<%=contextPath%>/mail-tracking/management/downLoad/<%=document.getExternalId()%>/<%=entryBean.getEntry().getExternalId() %>">
										<img src="<%= contextPath %>/images/view.gif" alt="Visualizar"></img></a> <a href="<%=contextPath%>/mail-tracking/management/deleteDocument/<%=document.getExternalId()%>/<%=entryBean.getEntry().getExternalId() %>?check=<%=check %>&amp;options=<%=options %>">
										<img src="<%= contextPath %>/images/delete.gif" alt="Eliminar"></img></a></span>	
									</td>
			<%} %>
		</tbody>
	</table>
</div>
<%} %>

<p class="mtop2 mbottom05">
	<strong><spring:message code="label.associate.new.document" text="Associate new document" /></strong>
</p>
<spring:url var="newDocumentURL" value="/mail-tracking/management/associateDocument/" />

<form id="associateDoc" action='${newDocumentURL}' enctype="multipart/form-data" method="post">
${csrf.field()}
<input type="hidden" name="mailTrackingId" value="<%=mailTrackingId%>"/>
<input type="hidden" name="entryBean" value="<%=entryBean%>"/>
<input type="hidden" name="check" value="<%=check%>"/>
<input type="hidden" name="options" value="<%=options%>"/>
<input type="hidden" name="entryId" value="<%=entryBean.getEntry().getExternalId()%>"/>
	<div class="mtop05">
  <div class="form-group">
    <label for="stream" class="control-label col-sm-2"><spring:message code="label.document" text=""/></label>
    <div class="col-sm-10">
      <input size="40" name="stream" id="stream" type="file"></input>
    </div>
  </div>
  <div class="form-group">
    <label for="description" class="control-label col-sm-2"><spring:message code="label.description" text=""/></label>
    <div class="col-sm-10">
      <span>
        <input size="80" name="description" id="description" type="text" class="form-control"></input>
        
      </span>
    </div>
  </div>
</div>
	<input type="submit" value="<spring:message code="label.bind" text=""/>"/>
</form>



<script type="text/javascript" >
	var pageContext= '<%=contextPath%>';
	var mailId='<%=mailTrackingId%>';
	var value='<%=entryBean.getEntry().getType()%>';
	
	$(function() {

			
		    $( ".datepickers" ).datepicker({
			 autoclose: true,			 
			 dateFormat: 'yy-mm-dd',
			 onClose: function(){
				 $(this).valid();
			 }
			 });

		   $('.autocompi').autocomplete({
			
			focus: function(event, ui) {				
 				return false;
 			},
 			minLength: 2,	
			contentType: "application/json; charset=UTF-8",
			search  : function(){$(this).addClass('ui-autocomplete-loading');},
			open    : function(){$(this).removeClass('ui-autocomplete-loading');},
			close    :function(){$(this).removeClass('ui-autocomplete-loading');},
			source : function(request,response){
				request.term=encodeURIComponent(request.term);
				$.get(pageContext + "/mail-tracking/management/populate/json/"+mailId, request,function(result) {
					response($.map(result,function(item) {
						if(!result) return;
						return{
							label: item.name,
							value: item.id
						}
					}));
				});
			},
		  change: function(event, ui) {
		        
			  if (ui.item == null) {
			         if(($(this).attr("name") === 'recipient' && value ==='RECEIVED')
			           || ($(this).attr("name") === 'sender' && value ==='SENT')){
				        	$(this.form).find("input[name$='owner']").val("");
			         }
			  }
			  $(this).removeClass('ui-autocomplete-loading');
			  return false;
		  },
			
			select: function( event, ui ) {
				
				$( this ).val( ui.item.label );
				if(($(this).attr("name") === 'recipient' && value ==='RECEIVED')
					    || ($(this).attr("name") === 'sender' && value ==='SENT')){
							$(this.form).find("input[name$='owner']").val(ui.item.value);
					}
				return false;
			}
		});
		   $.validator.addMethod(
				      "fmtDate",
				      function (value, element) {
				    	 
				        if(value==""){
				        	return true;
				        } 
				    	
				    	var dt= value.match(/((20)[0-9]{2})[-]((0[1-9])|(1[0-2]))[-]((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))/);
				    	if(dt==null){
				    		return false;
				    	}else{
				    		return true;
				    		}
				      },"Data inválida");

		   $("#editForm").validate({
				 rules:{
					 whenSent:{
						 'required':{
							 depends:function()
						        {
							          var sel =$('#type').val();						           
							          if(sel =='Expedido' ){
							        	return true;  
							          }else{
							        	 return false;
							          }
						          }
						 		},
						 		'fmtDate':true
							 },
						
					 whenReceived: {
						 'required':{
						 depends:function()
					        {
						          var sel =$('#type').val();					           
						          if(sel =='Recebido' ){
						        	return true;  
						          }else{
						        	 return false;
						          }
					          }
					 		},
					 		'fmtDate':true
						 },
					 sender: 'required',
					 recipient:'required',
					 subject: 'required'
				 },
				 messages:{
					 whenSent:{
						 required:'Campo Obrigatório'
					 },
					 whenReceived:{
						 required:'Campo Obrigatório'

					 	 },				
					 sender: 'Campo Obrigatório',
					 recipient: 'Campo Obrigatório',
					 subject: 'Campo Obrigatório'
					 },
				 submitHandler: function(form) {
			            form.submit();
				 }
			});
		   
		   $("#associateDoc").validate({
				 rules:{
					 stream:'required',
					 description: 'required'
				 },
				 messages:{
					 stream: 'Campo Obrigatório',
					 description: 'Campo Obrigatório'
					 },
				 submitHandler: function(form) {
			            form.submit();
				 }
			});
		   
	});
</script>






