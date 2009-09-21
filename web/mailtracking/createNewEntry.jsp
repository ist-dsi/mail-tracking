<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<bean:define id="mailTrackingId" name="mailTracking" property="externalId" />
<bean:define id="correspondenceType" name="correspondenceType" />

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<h3><bean:message key="label.add.new.entry" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<div>
<html:messages id="message" message="true" bundle="MAIL_TRACKING_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>
</div>

<fr:form id="add.new.entry.form" action="<%= "/mailtracking.do?method=addNewEntry&amp;correspondenceType=" + correspondenceType + "&amp;mailTrackingId="+ mailTrackingId %>" encoding="multipart/form-data">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" schema="module.mailtracking.correspondence.entry.edit" />
	
	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.create" />
	
	<html:submit><bean:message key="label.add" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
