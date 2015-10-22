<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="pt.ist.fenixframework.FenixFramework"%>
<%@page import="java.util.Collection"%>
<%@page import="module.mailtracking.domain.MailTracking"%>
<%@page import="module.mailtracking.domain.Year"%>
<%@page import="module.organization.domain.Unit"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Collection"%>
<%@page import="module.mailtracking.presentationTier.YearBean"%>
<%@ page import="com.google.common.base.Strings" %>

<%
    final String contextPath = request.getContextPath();
final Year chosenYear = (Year)request.getAttribute("chosenYear");
final String mailTrackingId= (String)request.getAttribute("mailTrackingId");
final String yearId= (String)request.getAttribute("yearId");
final MailTracking mailTracking = (MailTracking)request.getAttribute("mailTracking");
final String message=(String)request.getAttribute("message");
final YearBean yearBean=(YearBean)request.getAttribute("yearBean");
final Boolean check = ((Boolean)request.getAttribute("check"));
%>


<div class="page-header">
<h1><spring:message code="title.mail.tracking.set.reference.counters" text="title.mail.tracking.set.reference.counters" /></h1>
</div>
<br>

<div>
	<h3>
	<%=mailTracking.getName().getContent()%>
	</h3>
</div>
<p>
</p>
<p>
</p>
<% if(!Strings.isNullOrEmpty(message)) {%>
<div class="alert alert-danger ng-binding ng-hide" ng-show="error">
		<spring:message code="<%=message %>" text="<%=message %>" />
</div>
<%} %>

<spring:url var="setCounterURL"
	value="/mail-tracking/management/setReferenceCounters/" />

	<form id="setCounter" class="form-horizontal" role="form"
		action="${setCounterURL}" method="POST">
		<input type="hidden" name="mailTrackingId" id="mailTrackingId" value="<%=mailTracking.getExternalId()%>" /> 
		<input type="hidden" name="yearId" id="yearId" value="<%=yearId%>" />
		<input type="hidden" name="yearBean" id="yearBean" value="<%=yearBean%>" />
		<input type="hidden" name="check" id="check" value="<%=check%>" />
		
		<div class="form-group">
			<label class="control-label col-sm-2" for="year"> <spring:message
					code="label.mailTracking.year" text="label.mailTracking.year" />
			</label>
			<div class="col-sm-10">  		
				<input class="form-control" id="year" name="year" value="<%=chosenYear.getName()%>"/>
	
			</div>
		</div>
		
		 <div class="form-group">
		    <label class="control-label col-sm-2"><spring:message
						code="label.mail.tracking.sent.next.counter" text="label.mail.tracking.sent.next.counter" /></label>
		    <div class="col-sm-10">
		      <span>
		        <input  name="nextSentEntryNumber" value="<%=yearBean.getNextSentEntryNumber() %>" class="form-control"/>   
		      </span>
		    </div>
  		</div>
   
 		<div class="form-group">
		    <label class="control-label col-sm-2"><spring:message
						code="label.mail.tracking.received.next.counter" text="label.mail.tracking.received.next.counter" /></label>
		    <div class="col-sm-10">
		      <span>
		        <input  name="nextReceivedEntryNumber" value="<%=yearBean.getNextReceivedEntryNumber() %>" class="form-control"/>   
		      </span>
		    </div>
  		</div>
		<div class="form-group">
		<div class="col-sm-2">
		<input class="btn btn-default" type="submit" value="<spring:message code="mailTracking.button.save" text=""/>">
		<a id='cancelar' href='<%=contextPath %>/mail-tracking/management/chooseMailTracking?mailTrackingId=<%=mailTracking.getExternalId()%>&amp;YearId=<%=yearId%>&amp;check=<%=check %>' >
		<input class="btn btn-default" type="button" value="<spring:message code="mailTracking.button.cancel" text=""/>"/>
		</a>			
		</div>
	</div>		 
	</form>


