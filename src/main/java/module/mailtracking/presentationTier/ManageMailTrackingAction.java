/*
 * @(#)ManageMailTrackingAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Anil Kassamali
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Correspondence Registry Module.
 *
 *   The Correspondence Registry Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Correspondence Registry Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Correspondence Registry Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.mailtracking.presentationTier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.MailTrackingImportationHelper.ImportationReportEntry;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.organization.domain.Person;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.base.BaseAction;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = MailTrackingAction.class, path = "manageMailTracking", titleKey = "link.sideBar.mailtracking.manage")
@Mapping(path = "/manageMailTracking")
/**
 * 
 * @author Anil Kassamali
 * 
 */
public class ManageMailTrackingAction extends BaseAction {

    @Override
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return super.execute(mapping, form, request, response);
    }

    @EntryPoint
    public ActionForward prepareUsersManagement(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        request.setAttribute("searchUserBean", new SearchUserBean());
        request.setAttribute("mailTrackingBean", readMailTracking(request).createBean());

        return forward("/mailtracking/management/manageUsers.jsp");
    }

    public ActionForward removeOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.removeOperator(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.addOperator(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    private static User readUser(HttpServletRequest request) {
        return FenixFramework.getDomainObject(request.getParameter("userId"));
    }

    public ActionForward removeViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.removeViewer(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.addViewer(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.addManager(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTracking mailTracking = readMailTracking(request);
        MailTrackingBean bean = new MailTrackingBean(mailTracking);

        MailTrackingActionOperations.removeManager(bean, readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward prepareMailTrackingAttributesManagement(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("mailTrackingBean", readMailTracking(request).createBean());

        return forward("/mailtracking//management/manageAttributes.jsp");
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, HttpServletResponse response) throws Exception {
        MailTrackingBean bean = readMailTrackingBean(request);

        if (!bean.getMailTracking().isCurrentUserAbleToEditMailTrackingAttributes()) {
            throw new PermissionDeniedException();
        }

        bean.getMailTracking().edit(bean);

        addMessage(request, "mailtracking.operations", "message.mail.tracking.attributes.updated", new String[0]);

        return prepareMailTrackingAttributesManagement(mapping, form, request, response);
    }

    public ActionForward searchUser(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        SearchUserBean searchBean = readSearchUserBean(request);

        java.util.List<User> usersResult = new java.util.ArrayList<User>();
        if (SearchUserBean.SearchUserMode.USERNAME.equals(searchBean.getMode())) {
            User user = User.findByUsername(searchBean.getValue());
            usersResult.add(user);
        } else if (SearchUserBean.SearchUserMode.NAME.equals(searchBean.getMode())) {
            java.util.List<Person> matchPersons = Person.searchPersons(searchBean.getValue());
            for (Person person : matchPersons) {
                if (person.getUser() != null) {
                    usersResult.add(person.getUser());
                }
            }
        }

        if (usersResult.isEmpty()) {
            this.addMessage(request, "coolThing");
        }

        request.setAttribute("searchResults", usersResult);

        return prepareUsersManagement(mapping, form, request, response);
    }

    private MailTrackingBean readMailTrackingBean(final HttpServletRequest request) {
        return this.getRenderedObject("mail.tracking.bean");
    }

    public ActionForward prepareMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        request.setAttribute("importationFileBean", new ImportationFileBean());

        return forward("/mailtracking/manager/importMailTracking.jsp");
    }

    public ActionForward importMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        java.util.List<ImportationReportEntry> importationResults = new java.util.ArrayList<ImportationReportEntry>();

        request.setAttribute("errorOccurred", MailTrackingActionOperations.importMailTracking(readMailTrackingBean(request),
                readImportationFileBean(request), importationResults));
        request.setAttribute("importationFileResults", importationResults);

        return forward("/mailtracking/manager/viewImportationResults.jsp");
    }

    private ImportationFileBean readImportationFileBean(final HttpServletRequest request) {
        ImportationFileBean importFileBean = (ImportationFileBean) request.getAttribute("importationFileBean");

        if (importFileBean == null) {
            importFileBean = this.getRenderedObject("importation.file.bean");
        }

        return importFileBean;
    }

    private MailTracking readMailTracking(final HttpServletRequest request) {
        return this.getDomainObject(request, "mailTrackingId");
    }

    private SearchUserBean readSearchUserBean(HttpServletRequest request) {
        return this.getRenderedObject("search.user.bean");
    }

//    @Override
//    public Context createContext(String contextPathString, HttpServletRequest request) {
//        LayoutContext context = (LayoutContext) super.createContext(contextPathString, request);
//        context.addHead("/mailtracking/layoutHead.jsp");
//        return context;
//    }
}
