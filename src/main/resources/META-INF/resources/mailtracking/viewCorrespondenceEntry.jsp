<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<%@ page import="module.mailtracking.domain.CorrespondenceType" %>

<h2><bean:message key="title.mail.tracking.application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<bean:define id="entryId" name="correspondenceEntryBean" property="entry.externalId" />

<p class="mtop05">
	<html:link href="<%= request.getContextPath() + "/mailtracking.do?method=prepare&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType%>" >
		« <bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
	</html:link>
</p>

<fr:view 	name="correspondenceEntryBean"
			property="entry" 
			schema="<%=  CorrespondenceType.RECEIVED.name().equals(correspondenceType) ? "module.mailtracking.correspondence.received.entries.view.detailed" : "module.mailtracking.correspondence.sent.entries.view.detailed" %>" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2 thleft tdleft"/>
	</fr:layout>
</fr:view>

<h3 class="mtop2 mbottom05"><bean:message key="title.associated.documents" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<logic:empty name="correspondenceEntryBean" property="entry.documents" >
	<p class="mtop05">
		<em><bean:message key="message.associated.documents.empty" bundle="MAIL_TRACKING_RESOURCES" /></em>
	</p>
</logic:empty>

<logic:notEmpty name="correspondenceEntryBean" property="entry.documents" >

<bean:define id="associatedDocuments" name="correspondenceEntryBean" property="entry.activeDocuments"/>

<fr:view name="associatedDocuments" schema="module.mailtracking.associated.document.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table" />
		<fr:property name="linkFormat(view)" value="<%= "/mailtracking.do?method=downloadFile&amp;fileId=${externalId}&amp;mailTrackingId=" + mailTrackingId + "&amp;correspondenceType=" + correspondenceType + "&amp;entryId=" + entryId %>"/>
		<fr:property name="bundle(view)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(view)" value="link.view"/>
		<fr:property name="order(view)" value="2" />
		<fr:property name="icon(view)" value="view" />
	</fr:layout> 
</fr:view>
</logic:notEmpty>
