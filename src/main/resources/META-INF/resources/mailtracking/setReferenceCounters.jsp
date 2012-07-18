<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />

<h2><bean:message key="title.mail.tracking.set.reference.counters" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<div>
	<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
		<p>
			<span class="error0"> <bean:write name="message" /></span>
		</p>
	</html:messages>
</div>

<fr:form id="set.reference.counters" action="<%= "/mailtracking.do?method=setReferenceCounters&amp;mailTrackingId="+ mailTrackingId %>" >
	<fr:edit	id="year.bean" name="yearBean" visible="false" />

	<fr:edit	name="yearBean"
				id="year.bean.choose"
				schema="module.mail.tracking.set.reference.counters.years.list" >
		<fr:destination name="postback" path="<%= "/mailtracking.do?method=chooseYearForReferenceCountSet&amp;mailTrackingId=" + mailTrackingId %>" />
	</fr:edit>
	
	<logic:empty name="yearBean" property="chosenYear">
		<em><bean:message key="message.mail.tracking.set.counters.choose.year" bundle="MAIL_TRACKING_RESOURCES" /></em>
	</logic:empty>
	
	<logic:notEmpty name="yearBean" property="chosenYear">
		<fr:edit	name="yearBean"
					id="year.bean.set.reference.counter"
					schema="module.mail.tracking.set.reference.counters.edit" >
			<fr:layout name="tabular">
				<fr:property name="columnClasses" value=",,tderror"/>
				<fr:property name="requiredMarkShown" value="true" />
			</fr:layout>
			<fr:destination name="invalid" path="<%= "/mailtracking.do?method=setReferenceCountersInvalid&amp;mailTrackingId=" + mailTrackingId %>" />
			<fr:destination name="cancel" path="<%= "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId %>" />			
		</fr:edit>
		
		<html:submit><bean:message key="label.edit" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
		<html:cancel><bean:message key="label.cancel" bundle="MAIL_TRACKING_RESOURCES" /></html:cancel>
	</logic:notEmpty>
</fr:form>
