
<%@page import="module.mailtracking.domain.MailTracking"%>
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
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">



<%
final String contextPath = request.getContextPath();
 final CorrespondenceEntryBean entryBean = (CorrespondenceEntryBean) request.getAttribute("entryBean");

	final MailTracking mailTracking =(MailTracking)request.getAttribute("mailTracking");
	final String mailTrackingId=mailTracking.getExternalId();
 	String message=(String)request.getParameter("message");

	final Boolean check = Boolean.valueOf(request.getAttribute("check").toString());
	final String yearId = (String)request.getAttribute("yearId");

	final String type=(String)request.getParameter("type");
	
	String str="checked";
	
%>

<script src='<%= contextPath + "/webjars/jquery-ui/1.11.1/jquery-ui.js" %>'></script>
<div class="page-header">
	<h1>
	<%=entryBean.getMailTracking().getName().getContent()%> - <spring:message code="title.mailtracking.create.process" text=""/>
	</h1>
</div>
<br>
<p>
<span style="margin-right: 30px;">
	<a id="back" href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTrackingId%>&amp;YearId=<%=yearId%>&amp;check=<%=check %>' >
		<spring:message code="label.back" text="Go Back" />
	</a>
	</span>
</p>

<% if(!Strings.isNullOrEmpty(message)) {%>
<div class="alert alert-danger ng-binding ng-hide" ng-show="error">
		<spring:message code="<%= message %>" text="<%= message %>" />
</div>
<%} %>


<form class="form-horizontal" id="selectType">
<p><spring:message code="title.mailTracking.select.correspence.type" text="Seleccione o Tipo de correspondência que pretende criar"></spring:message></p>

<label><spring:message code="label.type.receive" text="label.type.receive" /></label>
   
	<input id="rec" type="radio" name="entryType" value="<%=CorrespondenceType.RECEIVED%>" >

<label><spring:message code="label.type.sent" text="label.type.sent" /></label>
	<input id = "env" type="radio" name=entryType value="<%=CorrespondenceType.SENT%>" >
</form>
<br/>

<div id="receber" style="display: none;">
<jsp:include page="recievedNewEntry.jsp"></jsp:include>
</div>
<div id="enviar" style="display:none;">
<jsp:include page="sentNewEntry.jsp"></jsp:include>
</div>

 
<style type="text/css">

label.error {
  color:#FB3A3A;
  font-weight:bold;
}
.ui-autocomplete-loading{background: url(<%=contextPath%>/images/spinner.gif) no-repeat right center}
</style>

<script type="text/javascript">	

function is_undefined(value) {
	   if(value === undefined || value ===""){
		   return true;
	   } else{
	       return false;
	   }
	};
	
	

		


$(function() {
	var pageContext='<%=contextPath%>';
	var mailId='<%=mailTrackingId%>';
	var t='<%=type%>';
	var value='';
	
	

	
	 
	 if(!is_undefined(t)){
		
			if(t === 'RECEIVED'){
				$("input#rec").focus();
				$("input#rec").attr('checked','true');
				
			    $("#tipo").val(t);
			    $("#enviar").css("display", "none"); 
			
				$("#receber").css("display", "block"); 
				
			
			}else if(t === 'SENT'){
				$("input#env").focus();
				$("input#env").attr('checked','true');						 
				 $("#tipo").val(t);
				
				 $("#receber").css("display", "none"); 
			
				 $("#enviar").css("display", "block"); 
				 
			};
		};

	
	
	
	
	
	$("#selectType input:radio").on('click',function(){
		<%message="";%>;
		
		value=$(this).val();
    	$(this).attr('checked','true');
		$("#tipo").val(value);
	if(value === 'RECEIVED'){
		 $("#enviar").css("display", "none"); 
		
		$("#receber").css("display", "block"); 
		
	}else if(value === 'SENT'){
	
		 $("#receber").css("display", "none"); 
	
		 $("#enviar").css("display", "block"); 
	};
	
	return true;
});


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

	
$('.autocompi').autocomplete({
	

	focus: function(event, ui) {
			return false;
		},
	minLength: 2,	
	contentType: "application/json; charset=UTF-8",
	search  : function(){$(this).addClass('ui-autocomplete-loading');},
	open    : function(){$(this).removeClass('ui-autocomplete-loading');},
	close    : function(){$(this).removeClass('ui-autocomplete-loading');},
	source : function(request,response){
		$.post(pageContext + "/mail-tracking/management/populate/json/"+mailId, request,function(result) {
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
		
		if(($(this).attr("name") === 'recipient' && value ==='RECEIVED')
		    || ($(this).attr("name") === 'sender' && value ==='SENT')){
				$(this.form).find("input[name$='owner']").val(ui.item.value);
		}
		$( this ).val( ui.item.label );	
		return false;
	}
});
$(".datepickers, #dateR, #dateS").datepicker({
	 autoclose: true,
	 dateFormat: 'yy-mm-dd'
	 }).val();
});

</script>
