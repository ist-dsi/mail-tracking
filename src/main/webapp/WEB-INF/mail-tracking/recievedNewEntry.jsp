
<%@page import="module.mailtracking.domain.MailTracking"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum"%>
<%@ page import="module.mailtracking.presentationTier.MailTrackingAction.AssociateDocumentBean"%>
<%@ page import="module.mailtracking.domain.CorrespondenceType" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryLog" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean" %>
<%@ page import="module.mailtracking.domain.Document" %>
<%@ page import="module.mailtracking.domain.CorrespondenceEntryVisibility" %>
<%@ page import="module.organization.domain.Person" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.common.base.Strings" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>


<%
final String contextPath = request.getContextPath();
final CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("entryBean");

	final MailTracking mailTracking =(MailTracking)request.getAttribute("mailTracking");
	final String mailTrackingId=mailTracking.getExternalId();
 	final String message=(String)request.getAttribute("message");

	final Boolean check = ((Boolean)request.getAttribute("check"));
	final String yearId = (String)request.getAttribute("yearId");
	final String  options=(String)request.getAttribute("options");
    
	final String type=(String)request.getParameter("type");
	String str="checked";
	
%>

<script src='<%= contextPath + "/webjars/jquery-ui/1.11.1/jquery-ui.js" %>'></script>

<spring:url var="submitUrl"
	value="/mail-tracking/management/createNewEntry/" />

<form:form id="f1" commandName="entryBean" class="form-horizontal validar"
	method="POST" action="${submitUrl}" >
	${csrf.field()}
<input type="hidden" name="mailTrackingId" value="<%=mailTrackingId%>"/>
<input type="hidden" name="entryBean" value="<%=entryBean%>"/>
<input type="hidden" name="check" value="<%=check%>"/>
<input type="hidden" name="options" value="<%=options%>"/>
<input type="hidden" name="owner" value="<%=entryBean.getOwner()!=null?entryBean.getOwner().getExternalId():""%>"/>
<input type="hidden" name="type" id="tipo" value="<%=CorrespondenceType.RECEIVED%>"/>
<input type="hidden" name="yearId" value="<%=yearId%>"/>  

<h3><spring:message code="label.correspondence.details" text="label.correspondence.details" /></h3> 



	<div class="form-group">
		<form:label class="control-label col-sm-2" path="reference" id='reference' ><spring:message
				code="label.mission.processNumber"
				text="label.mission.processNumber" />
		</form:label>
		<div class="col-sm-10">
			<form:input class="form-control" name="reference" path="reference" disabled="true"/>		
		</div>
	</div> 
	 
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="whenReceived" ><spring:message code="label.whenReceived" text=""/></form:label>
    <div class="col-sm-10">
      <div class="input-group">
        <form:input size="10" maxlength="10" path="whenReceived" value="<%=new LocalDate().toString("yyyy-MM-dd") %>" class="form-control formataData datepickers" />
        <span class="input-group-addon">yyyy-mm-dd</span>
      </div>
    </div>
  </div>
 
  <div class="form-group">
    <form:label class="autocompi control-label col-sm-2" path="sender"><spring:message code="label.sender" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="sender" class="form-control autocompi" autocomplete="off"/>   
      </span>
      <span><form:errors path="sender"></form:errors>
      </span>
    </div>
  </div>
   
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="whenSent" id="sent"><spring:message code="label.correspondence.date.detailed" text=""/></form:label>
    <div class="col-sm-10">
      <div class="input-group">
        <form:input  id="dateR" size="10" maxlength="10" path="whenSent" value="<%=entryBean.getWhenSent()!=null?entryBean.getWhenSent().toString():"" %>" class="form-control formataData "/>
        <span class="input-group-addon">yyyy-mm-dd</span>
      </div>
    </div>
  </div>
    
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="senderLetterNumber"><spring:message code="label.sender.letter.number.detailed" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input path="senderLetterNumber" class="form-control" />
      </span>
    </div>
  </div>
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="subject"><spring:message code="label.subject" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="subject" class="form-control" />  
      </span>
    </div>
  </div>
  
  <div class="form-group">
    <form:label class="control-label col-sm-2" path="recipient" ><spring:message code="label.mailTracking.table.receiver" text=""/></form:label>
    <div class="col-sm-10">
     <span>
    	<form:input path="recipient" class="autocompi form-control" autocomplete="off"/>  
    </span>
    </div>
   </div>
   
 <div class="form-group">
    <form:label class="control-label col-sm-2" path="dispatchedToWhom"><spring:message code="label.dispatch.made.to" text=""/></form:label>
    <div class="col-sm-10">
      <span>
        <form:input size="60" path="dispatchedToWhom"  class="form-control" /> 
      </span>
    </div>
  </div>
  
  <div class="form-group">
    <form:label path="observations" class="control-label col-sm-2"><spring:message code="label.mailTracking.table.observations" text=""/></form:label>
    <div class="col-sm-10">
      <form:textarea path="observations" rows="4" class="form-control" cols="45"></form:textarea>
    </div>
  </div>
  <div class="form-group">
	<div class="col-sm-2">
	<input id="submitRecieve" class="btn btn-default" type="submit" value="<spring:message code="mailTracking.button.save" text=""/>" /> 
	<a id='cancelar' href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId%>&amp;YearId=<%=yearId%>&amp;check=<%=check %>&amp;options=<%=options %>' >
		<input class="btn btn-default" type="button" value="<spring:message code="mailTracking.button.cancel" text=""/>"/>
		</a>
	</div>
</div>

 </form:form>
 
 <script>
 
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
	      },"Data inválida - Formato: YYYY-MM-DD");
 
 $("#f1").validate({
	 rules:{
		 whenSent:{
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
		 whenReceived:'Campo Obrigatório',
		 sender: 'Campo Obrigatório',
		 recipient: 'Campo Obrigatório',
		 subject: 'Campo Obrigatório'
		 },
	 submitHandler: function(form) {
            form.submit();
	 }
});
 
 </script>




