/*
 * @(#)ManageMailTrackingOrganizationAction.java
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
package module.mailtracking.presentationTier.organization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTracking.MailTrackingBean;
import module.mailtracking.domain.MailTrackingImportationHelper.ImportationReportEntry;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.mailtracking.presentationTier.ImportationFileBean;
import module.mailtracking.presentationTier.MailTrackingActionOperations;
import module.mailtracking.presentationTier.MailTrackingView;
import module.mailtracking.presentationTier.SearchUserBean;
import module.mailtracking.presentationTier.YearBean;
import module.organization.domain.OrganizationalModel;
import module.organization.domain.Person;
import module.organization.domain.Unit;
import module.organization.presentationTier.actions.OrganizationModelAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/mailTrackingOrganizationModel")
/**
 * 
 * @author Anil Kassamali
 * 
 */
public class ManageMailTrackingOrganizationAction extends OrganizationModelAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        request.setAttribute("party", readOrganizationalUnit(request));
        request.setAttribute("organizationalModel", readOrganizationalModel(request));
        request.setAttribute("existsMailTrackingForUnit", readOrganizationalUnit(request).getMailTracking() != null);
        request.setAttribute("viewName", MailTrackingView.VIEW_NAME);

        return super.execute(mapping, form, request, response);
    }

    public ActionForward back(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return viewModel(mapping, form, request, response);
    }

    public ActionForward createMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        if (!MailTracking.isCurrentUserAbleToCreateMailTrackingModule()) {
            throw new PermissionDeniedException();
        }

        Unit unit = readOrganizationalUnit(request);
        MailTracking.createMailTracking(unit);

        return viewModel(mapping, form, request, response);
    }

    private Unit readOrganizationalUnit(final HttpServletRequest request) {
        return this.getDomainObject(request, "partyOid");
    }

    private OrganizationalModel readOrganizationalModel(final HttpServletRequest request) {
        return this.getDomainObject(request, "organizationalModelOid");
    }

    public ActionForward prepareMailTrackingAttributesManagement(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("mailTrackingBean", readOrganizationalUnit(request).getMailTracking().createBean());
        return forward("/module/mailTracking/manageAttributes.jsp");
    }

    public ActionForward prepareUsersManagement(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        request.setAttribute("searchUserBean", readSearchUserBean(request));

        request.setAttribute("mailTrackingBean", readOrganizationalUnit(request).getMailTracking().createBean());
        return forward("/module/mailTracking/manageUsers.jsp");
    }

    public ActionForward removeOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.removeOperator(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addOperator(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.addOperator(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.addViewer(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeViewer(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.removeViewer(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward addManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.addManager(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward removeManager(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.removeManager(readMailTrackingBean(request), readUser(request));
        return prepareUsersManagement(mapping, form, request, response);
    }

    private MailTrackingBean readMailTrackingBean(final HttpServletRequest request) {
        MailTrackingBean bean = (MailTrackingBean) request.getAttribute("mailTrackingBean");

        if (bean == null) {
            bean = this.getRenderedObject("mail.tracking.bean");
        }

        if (bean == null) {
            Unit unit = readOrganizationalUnit(request);
            bean = unit.getMailTracking().createBean();
        }

        request.setAttribute("mailTrackingBean", bean);

        return bean;
    }

    private User readUser(HttpServletRequest request) {
        return FenixFramework.getDomainObject(request.getParameter("userId"));
    }

    private SearchUserBean readSearchUserBean(HttpServletRequest request) {
        SearchUserBean searchBean = (SearchUserBean) request.getAttribute("searchUserBean");

        if (searchBean == null) {
            searchBean = this.getRenderedObject("search.user.bean");
        }

        if (searchBean == null) {
            searchBean = new SearchUserBean();
        }

        return searchBean;
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
        request.setAttribute("searchUserBean", new SearchUserBean());

        return prepareUsersManagement(mapping, form, request, response);
    }

    public ActionForward editMailTrackingAttributes(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, HttpServletResponse response) throws Exception {

        MailTrackingBean bean = readMailTrackingBean(request);

        if (!bean.getMailTracking().isCurrentUserAbleToEditMailTrackingAttributes()) {
            throw new PermissionDeniedException();
        }

        bean.getMailTracking().edit(bean);

        return viewModel(mapping, form, request, response);
    }

    public ActionForward prepareMailTrackingImportation(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {
        request.setAttribute("importationFileBean", new ImportationFileBean());

        return forward("/module/mailTracking/importEntries.jsp");
    }

    private ImportationFileBean readImportationFileBean(final HttpServletRequest request) {
        ImportationFileBean importFileBean = (ImportationFileBean) request.getAttribute("importationFileBean");

        if (importFileBean == null) {
            importFileBean = this.getRenderedObject("importation.file.bean");
        }

        return importFileBean;
    }

    public ActionForward importMailTracking(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        java.util.List<ImportationReportEntry> importationResults = new java.util.ArrayList<ImportationReportEntry>();

        request.setAttribute("errorOccurred", MailTrackingActionOperations.importMailTracking(readMailTrackingBean(request),
                readImportationFileBean(request), importationResults));
        request.setAttribute("importationFileResults", importationResults);

        return forward("/mailtracking/manager/viewImportationResults.jsp");
    }

    public ActionForward prepareYearsManagement(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        RenderUtils.invalidateViewState("mail.tracking.year.bean");

        request.setAttribute("yearBean", new YearBean(readMailTrackingBean(request).getMailTracking()));
        return forward("/module/mailTracking/manageYears.jsp");
    }

//    @Override
//    public Context createContext(String contextPathString, HttpServletRequest request) {
//        LayoutContext context = (LayoutContext) super.createContext(contextPathString, request);
//        context.addHead("/mailtracking/layoutHead.jsp");
//        return context;
//    }

    public ActionForward createYear(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.createYearFor(getYearBean(request));
        return prepareYearsManagement(mapping, form, request, response);
    }

    private YearBean getYearBean(final HttpServletRequest request) {
        return this.getRenderedObject("mail.tracking.year.bean");
    }

    public ActionForward rearrangeEntries(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        MailTrackingActionOperations.rearrangeEntries(readMailTrackingBean(request));
        return prepareYearsManagement(mapping, form, request, response);
    }
}
