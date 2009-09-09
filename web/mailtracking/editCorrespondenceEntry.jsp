<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="title.mail.tracking,application" bundle="MAIL_TRACKING_RESOURCES" /></h2>

<html:link href="<%= request.getContextPath() + "/mailtracking.do?method=prepare" %>" >
	<bean:message key="label.back" bundle="MAIL_TRACKING_RESOURCES" />
</html:link>

<h3><bean:message key="title.mail.tracking.correspondence.entry.edit" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<fr:form id="add.new.entry.form" action="/mailtracking.do?method=editEntry">
	<fr:edit id="correspondence.entry.bean" name="correspondenceEntryBean" schema="module.mailtracking.correspondence.entry.edit" />
	
	<html:submit><bean:message key="label.edit" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>


<h3><bean:message key="title.associated.documents" bundle="MAIL_TRACKING_RESOURCES" /></h3>

<logic:empty name="correspondenceEntryBean" property="entry.documents" >
	<em><bean:message key="message.associated.documents.empty" bundle="MAIL_TRACKING_RESOURCES" /></em>
</logic:empty>

<logic:notEmpty name="correspondenceEntryBean" property="entry.documents" >

<bean:define id="associatedDocuments" name="correspondenceEntryBean" property="entry.documents"/>

<fr:view name="associatedDocuments" schema="module.mailtracking.associated.document.view" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="table" />
		
		<fr:property name="linkFormat(view)" value="/mailtracking.do?method=downloadFile&amp;fileId=${externalId}" />
		<fr:property name="bundle(view)" value="MAIL_TRACKING_RESOURCES"/>
		<fr:property name="key(view)" value="link.view"/>
		<fr:property name="order(view)" value="2" />
		
	</fr:layout> 
</fr:view>
</logic:notEmpty>

<p></p><strong><bean:message key="label.associate.new.document" bundle="MAIL_TRACKING_RESOURCES" /></strong></p>
<fr:form id="associate.document.entry.form" action="/mailtracking.do?method=associateDocument" encoding="multipart/form-data">
	<fr:edit id="associate.document.bean" name="associateDocumentBean" schema="module.mailtracking.associate.document.edit" />
	
	<html:submit><bean:message key="label.bind" bundle="MAIL_TRACKING_RESOURCES" /></html:submit>
</fr:form>
