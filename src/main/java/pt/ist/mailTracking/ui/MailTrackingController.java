package pt.ist.mailTracking.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.domain.CorrespondenceEntryVisibility;
import module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.Document;
import module.mailtracking.domain.DocumentType;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.MailTrackingDomainException;
import module.mailtracking.domain.Year;
import module.mailtracking.presentationTier.MailTrackingAction.AssociateDocumentBean;
import module.mailtracking.presentationTier.MailTrackingActionOperations;
import module.mailtracking.presentationTier.YearBean;
import module.organization.domain.Person;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.StringNormalizer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SpringApplication(group = "logged", path = "mail-tracking", title = "hint.sideBar.mailtracking.manageMailing",
        hint = "mail-tracking")
@SpringFunctionality(app = MailTrackingController.class, title = "hint.sideBar.mailtracking.manageMailing")
@RequestMapping("/mail-tracking/management")
public class MailTrackingController {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    protected MailTracking readMailTracking(String mailTStringId) {
        MailTracking mailTracking = FenixFramework.getDomainObject(mailTStringId);
        return mailTracking;
    }

    protected CorrespondenceType readCorrespondenceTypeView(String typeValue, Model model) {

        CorrespondenceType type = Strings.isNullOrEmpty(typeValue) ? null : CorrespondenceType.valueOf(typeValue);

        if (type == null) {
            type = CorrespondenceType.SENT;
        }

        model.addAttribute("correspondenceType", type.name());
        return type;
    }

    @RequestMapping
    public final String prepare(@RequestParam(required = false, value = "mailTrackingId") String mailTrackingId,
            HttpServletRequest request, final Model model) throws Exception {
        final User currentUser = Authenticate.getUser();
        Collection<MailTracking> mailTrackings = MailTracking.getMailTrackingsWhereUserHasSomeRole(currentUser);
        model.addAttribute("mailTrackings", mailTrackings);
        model.addAttribute("check", false);
        model.addAttribute("options", "");
        if (!Strings.isNullOrEmpty(mailTrackingId)) {
            final MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);
            model.addAttribute("mailTracking", mailTracking);
            model.addAttribute("year", mailTracking.getCurrentYear());
        }
        return "mail-tracking/chooseMailTracking";
    }

    @RequestMapping(value = "/chooseMailTracking", method = RequestMethod.GET)
    public final String chooseMailTracking(@RequestParam String mailTrackingId, @RequestParam String YearId,
            @RequestParam Boolean check, @RequestParam String options, HttpServletRequest request, final Model model)
            throws Exception {
        final User currentUser = Authenticate.getUser();
        Collection<MailTracking> mailTrackings = MailTracking.getMailTrackingsWhereUserHasSomeRole(currentUser);

        final MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);
        if (!mailTracking.isUserAbleToViewMailTracking(currentUser)) {
            return "mail-tracking/permissionDenied";
        }
        final Year year = FenixFramework.getDomainObject(YearId);

        model.addAttribute("mailTrackings", mailTrackings);
        model.addAttribute("mailTracking", mailTracking);
        model.addAttribute("year", year);
        model.addAttribute("check", check);
        model.addAttribute("options", options);

        return "mail-tracking/chooseMailTracking";
    }

    public static final Comparator<Year> SORT_BY_NAME_COMPARATOR = new Comparator<Year>() {

        @Override
        public int compare(Year o1, Year o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    @RequestMapping(value = "/getYearByUnit/json", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody String populateYear(@RequestParam(required = false, value = "term") String term, final Model model) {

        if (term != null) {
            final JsonArray result = new JsonArray();
            final MailTracking mailTracking = FenixFramework.getDomainObject(term);
            mailTracking.getYearsSet().stream().sorted(SORT_BY_NAME_COMPARATOR.reversed()).forEach(y -> addYearToJson(result, y));
            model.addAttribute("mailTracking", mailTracking);
            return result.toString();
        }
        return term;
    }

    @RequestMapping(value = "/getCurrMailTracking/json", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody String populateMailTracking(@RequestParam(required = false, value = "term") String term, @RequestParam(
            required = false, value = "year") String year, @RequestParam(required = false, value = "check") Boolean check,
            @RequestParam(required = false, value = "options") String options, HttpServletRequest request, final Model model) {

        if (Strings.isNullOrEmpty(term) || Strings.isNullOrEmpty(year)) {
            return null;
        }

        final Year chosenYear = FenixFramework.getDomainObject(year);
        final MailTracking mailTracking = FenixFramework.getDomainObject(term);
        if (!mailTracking.isUserAbleToViewMailTracking(Authenticate.getUser())) {
            return "mail-tracking/permissionDenied";
        }

        final JsonArray result = new JsonArray();
        Set<CorrespondenceEntry> entries = chosenYear.getEntriesSet();

        entries.stream().filter(e -> match(check, e)).collect(Collectors.toSet())
                .forEach(u -> addCorrespondenceToJson(result, u, request, mailTracking.getExternalId(), check, options));
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"aaData\": ").append(result.toString()).append("\n");
        stringBuilder.append("}");

        return stringBuilder.toString();

    }

    @RequestMapping(value = "/isAbleToCreate", method = RequestMethod.GET)
    public @ResponseBody String isAbleToCreate(@RequestParam(value = "data") String data, final Model model) {
        if (data != null) {
            final MailTracking mailTracking = FenixFramework.getDomainObject(data);

            if (!mailTracking.isUserAbleToCreateEntries(Authenticate.getUser())) {
                return "false";
            }
            return Boolean.toString(mailTracking.isCurrentUserAbleToCreateEntries());
        }

        return "false";

    }

    @RequestMapping(value = "/isAbleToSetCount", method = RequestMethod.GET)
    public @ResponseBody String isAbleToSetCount(@RequestParam(value = "data") String data, final Model model) {
        if (data != null) {
            final MailTracking mailTracking = FenixFramework.getDomainObject(data);

            if (!mailTracking.isUserAbleToSetReferenceCounters(Authenticate.getUser())) {
                return "false";
            }

            return Boolean.toString(mailTracking.isCurrentUserAbleToSetReferenceCounters());
        }

        return "false";

    }

    private boolean match(Boolean check, CorrespondenceEntry e) {

        if (e.isUserAbleToView() && check) {
            return true;
        }
        if (e.isUserAbleToView() && !check && e.isActive()) {
            return true;
        }

        return false;
    }

    private void addCorrespondenceToJson(JsonArray result, CorrespondenceEntry u, HttpServletRequest request,
            String mailTrackingId, Boolean check, String options) {

        final JsonObject o = new JsonObject();

        o.addProperty("Type", u.getType().getDescription());
        o.addProperty("Process", u.getReference());
        o.addProperty("Data", (u.getType() == CorrespondenceType.SENT ? u.getWhenSent().toString("yyyy-MM-dd") : u
                .getWhenReceived().toString("yyyy-MM-dd")));
        o.addProperty("Recipient", u.getRecipient());

        o.addProperty("AvatUrlRec", findUserByName(u.getRecipient()));

        o.addProperty("Sender", u.getSender());

        o.addProperty("AvatUrlSend", findUserByName(u.getSender()));

        o.addProperty("SenderLetterNumber", u.getSenderLetterNumber());
        o.addProperty("Subject", u.getSubject());

        o.addProperty("State", (u.getState().name()));

        o.addProperty(
                "View",
                u.isUserAbleToView(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryView(request, u, check, options) : "permission_not_granted");
        o.addProperty(
                "Edit",
                u.isUserAbleToEdit(Authenticate.getUser()) && u.isActive() ? generateLinkForCorrespondenceEntryEdition(request,
                        u, check, options) : "permission_not_granted");
        o.addProperty(
                "Delete",
                u.isUserAbleToDelete(Authenticate.getUser()) && u.isActive() ? generateLinkForCorrespondenceEntryRemoval(request,
                        u, check, options) : "permission_not_granted");
        o.addProperty(
                "Document",
                u.isUserAbleToViewMainDocument(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryMainDocument(request,
                        u) : "permission_not_granted");
        o.addProperty(
                "CopyEntry",
                u.isUserAbleToCopyEntry(Authenticate.getUser()) ? generateLinkForCorrespondenceEntryCopy(request, u, check,
                        options) : "permission_not_granted");
        result.add(o);
    }

    private String findUserByName(String recipient) {
        String avatarUrl = null;
        int ini = recipient.indexOf("(ist");
        if (ini == -1) {
            return avatarUrl;
        }
        if (ini > 0) {
            int fim = recipient.indexOf(')', ini);

            if (ini != -1 && fim != -1) {
                User u = User.findByUsername(recipient.substring(ini + 1, fim));
                avatarUrl = (u == null ? null : u.getProfile().getAvatarUrl());
            }
        }
        return avatarUrl;
    }

    private void addYearToJson(JsonArray result, Year u) {

        final JsonObject o = new JsonObject();
        o.addProperty("id", u.getExternalId());
        o.addProperty("name", u.getName());
        o.addProperty("current",
                u.getMailTracking().getCurrentYear() != null ? u.getMailTracking().getCurrentYear().getName() : "");
        result.add(o);

    }

    private String generateLinkForCorrespondenceEntryView(HttpServletRequest request, CorrespondenceEntry entry, Boolean check,
            String options) {
        String contextPath = request.getContextPath();

        String realLink =
                contextPath
                        + String.format("/mail-tracking/management/viewEntry?entryId=%s&amp;check=%s&amp;options=%s",
                                entry.getExternalId(), check, options);

        return realLink;
    }

    private String generateLinkForCorrespondenceEntryEdition(HttpServletRequest request, CorrespondenceEntry entry,
            Boolean check, String options) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format("/mail-tracking/management/prepareEditEntry?entryId=%s&amp;check=%s&amp;options=%s",
                                entry.getExternalId(), check, options);

        return realLink;
    }

    private String generateLinkForCorrespondenceEntryCopy(HttpServletRequest request, CorrespondenceEntry entry, Boolean check,
            String options) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format(
                                "/mail-tracking/management/prepareCopyEntry/?entryId=%s&amp;check=%s&amp;message=%s&amp;options=%s",
                                entry.getExternalId(), check, "", options);
        return realLink;
    }

    private String generateLinkForCorrespondenceEntryMainDocument(HttpServletRequest request, CorrespondenceEntry entry) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format("/mail-tracking/management/downLoad/%s/%s", entry.getMainDocument().getExternalId(),
                                entry.getExternalId());

        return realLink;
    }

    private String generateLinkForCorrespondenceEntryRemoval(HttpServletRequest request, CorrespondenceEntry entry,
            boolean check, String options) {
        String contextPath = request.getContextPath();
        String realLink =
                contextPath
                        + String.format("/mail-tracking/management/prepareDeleteEntry?entryId=%s&amp;check=%s&amp;options=%s",
                                entry.getExternalId(), check, options);

        return realLink;
    }

    @RequestMapping(value = "/viewEntry", method = RequestMethod.GET)
    public String viewEntry(@RequestParam(required = true) String entryId, @RequestParam(required = true) Boolean check,
            @RequestParam(required = true) String options, final Model model) {

        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(Authenticate.getUser()) || !entry.isUserAbleToView()) {
            return "mail-tracking/permissionDenied";
        }

        entry.logView();

        model.addAttribute("correspondenceEntryBean", entry.createBean());
        model.addAttribute("check", check);
        model.addAttribute("options", options);
        return "mail-tracking/viewCorrespondenceEntry";

    }

    @RequestMapping(value = "/prepareEditEntry", method = RequestMethod.GET)
    public String prepareEditEntry(@RequestParam(required = true) String entryId, @RequestParam(required = false) Boolean check,
            @RequestParam(required = false) String options, final Model model) {
        final User u = Authenticate.getUser();
        CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u) || !entry.isUserAbleToEdit(u)) {
            return "mail-tracking/permissionDenied";
        }

        CorrespondenceEntryBean bean = entry.createBean();

        model.addAttribute("entryBean", bean);

        model.addAttribute("visibilities", provideEntryVisibility());
        model.addAttribute("check", check);
        model.addAttribute("options", options);

        setAssociateDocumentBean(model, entry);

        return "mail-tracking/editCorrespondenceEntry";
    }

    public AssociateDocumentBean setAssociateDocumentBean(final Model model, final CorrespondenceEntry entry) {
        AssociateDocumentBean bean = new AssociateDocumentBean(entry);
        model.addAttribute("associateDocumentBean", bean);

        return bean;
    }

    @RequestMapping(value = "/editEntry", method = RequestMethod.POST)
    public String editEntry(HttpServletRequest request, Model model) throws Exception {
        final User u = Authenticate.getUser();
        CorrespondenceEntry entry = FenixFramework.getDomainObject(request.getParameter("entryId"));
        MailTracking mailTracking = entry.getMailTracking();

        Boolean check = Boolean.valueOf(request.getParameter("check"));
        String opt = request.getParameter("options");

        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u) || !entry.isUserAbleToEdit(u)) {
            return "mail-tracking/permissionDenied";
        }

        CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request, mailTracking, model);
        bean.setEntry(entry);

        if (!preValidate(bean, request, readCorrespondenceTypeView(request.getParameter("entry.type"), model), model)) {
            model.addAttribute("entryBean", bean);

            model.addAttribute("visibilities", provideEntryVisibility());
            model.addAttribute("check", check);
            model.addAttribute("options", opt);

            setAssociateDocumentBean(model, entry);

            return "mail-tracking/editCorrespondenceEntry";

        }

        model.addAttribute("check", check);
        model.addAttribute("options", opt);
        try {
            mailTracking.editEntry(bean);
        } catch (MailTrackingDomainException e) {
            addMessage(model, e.getMessage(), null);
        }
        return prepareEditEntry(entry.getExternalId(), Boolean.valueOf(request.getParameter("check")), opt, model);

    }

    @RequestMapping(value = "/prepareDeleteEntry", method = RequestMethod.GET)
    public final String prepareDeleteEntry(@RequestParam(required = true) String entryId, boolean check,
            @RequestParam String options, HttpServletRequest request, final Model model) {
        final User u = Authenticate.getUser();
        CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u) || !entry.isUserAbleToDelete(u)) {
            return "mail-tracking/permissionDenied";
        }
        CorrespondenceEntryBean entryBean = entry.createBean();
        model.addAttribute("correspondenceEntryBean", entryBean);
        model.addAttribute("check", check);
        model.addAttribute("options", options);

        return "mail-tracking/deleteCorrespondenceEntry";
    }

    @RequestMapping(value = "/deleteEntry", method = RequestMethod.POST)
    public String deleteEntry(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        final String entryId = request.getParameter("entryId");
        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        Boolean check = Boolean.valueOf(request.getParameter("check"));
        String opt = request.getParameter("options");
        model.addAttribute("check", check);
        model.addAttribute("entryId", entryId);
        model.addAttribute("options", opt);
        if (!entry.isUserAbleToDelete(Authenticate.getUser())) {
            return "mail-tracking/permissionDenied";
        }

        try {
            entry.delete(request.getParameter("deletionReason"));
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return prepareDeleteEntry(entryId, check, opt, request, model);
        }
        MailTracking mailTracking = entry.getMailTracking();
        Year year = entry.getYear();

        return chooseMailTracking(mailTracking.getExternalId(), year.getExternalId(), check, opt, request, model);
    }

    @RequestMapping(value = "/downLoad/{fileId}/{entryId}", method = RequestMethod.GET)
    public String downloadFile(@PathVariable String fileId, final @PathVariable String entryId,
            final HttpServletResponse response, Model model) throws IOException {
        final User u = Authenticate.getUser();
        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u)) {
            return "mail-tracking/permissionDenied";
        }

        final Document document = FenixFramework.getDomainObject(fileId);

        entry.logDownload(document);

        return download(response, document.getFilename(), document.getContent(), document.getContentType());
    }

    @RequestMapping(value = "/deleteDocument/{fileId}/{entryId}", method = RequestMethod.GET)
    public String deleteDocument(@PathVariable String fileId, final @PathVariable String entryId, @RequestParam Boolean check,
            @RequestParam(value = "options") String options, final HttpServletRequest request, Model model) throws Exception {
        final User u = Authenticate.getUser();
        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u) || !entry.isUserAbleToDelete(u)) {
            return "mail-tracking/permissionDenied";
        }
        final Document doc = FenixFramework.getDomainObject(fileId);
        doc.getCorrespondenceEntry().deleteDocument(doc);

        return prepareEditEntry(doc.getCorrespondenceEntry().getExternalId(), check, options, model);
    }

    @RequestMapping(value = "/associateDocument", method = RequestMethod.POST)
    public String associateDocument(@RequestParam(value = "entryId", required = true) String entryId, @RequestParam(
            value = "stream", required = true) MultipartFile stream,
            @RequestParam(value = "description", required = true) String description, final HttpServletRequest request,
            final Model model) throws Exception {

        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);

        final DocumentType docType = (entry.hasMainDocument() ? DocumentType.OTHER_DOCUMENT : DocumentType.MAIN_DOCUMENT);

        model.addAttribute("entry", entry);

        try {
            Document document =
                    createDocument(stream.getInputStream(), stream.getSize(), description, stream.getOriginalFilename(), docType);
            entry.associateDocument(document);
        } catch (DocumentUploadException e) {
            model.addAttribute("message", e.getMessage());
        }

        setAssociateDocumentBean(model, entry);

        return prepareEditEntry(entry.getExternalId(), Boolean.valueOf(request.getParameter("check")),
                request.getParameter("options"), model);
    }

    @RequestMapping(value = "/prepareCopyEntry", method = RequestMethod.GET)
    public String prepareCopyEntry(@RequestParam(required = true) String entryId, @RequestParam Boolean check, @RequestParam(
            value = "message") String message, @RequestParam(value = "options") String options, final HttpServletRequest request,
            final Model model) {
        final User u = Authenticate.getUser();
        final CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);
        if (!entry.getMailTracking().isUserAbleToViewMailTracking(u) || !entry.isUserAbleToCopyEntry(u)) {
            return "mail-tracking/permissionDenied";
        }
        final CorrespondenceEntryBean bean = entry.createBean();

        model.addAttribute("entryBean", bean);
        model.addAttribute("check", check);
        model.addAttribute("options", options);
        setAssociateDocumentBean(model, entry);

        if (entry.getType().getSimpleName().equals("RECEIVED")) {
            return "mail-tracking/recieveCopyEntry";
        } else if (entry.getType().getSimpleName().equals("SENT")) {
            return "mail-tracking/sentCopyEntry";
        }

        addMessage(model, "erro", null);
        return viewEntry(entry.getExternalId(), check, options, model);

    }

    @RequestMapping(value = "/createCopyEntry", method = RequestMethod.POST)
    public String addNewEntry(@RequestParam String mailTrackingId, @RequestParam String entryId,
            final HttpServletRequest request, final HttpServletResponse response, Model model, RedirectAttributes ra)
            throws Exception {
        MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);
        CorrespondenceEntry entry = FenixFramework.getDomainObject(entryId);

        Boolean check = Boolean.valueOf(request.getParameter("check"));
        String opt = request.getParameter("options");
        model.addAttribute("check", check);
        model.addAttribute("options", opt);

        CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request, mailTracking, model);
        if (!mailTracking.isUserAbleToCreateEntries(Authenticate.getUser())) {
            return "mail-tracking/permissionDenied";
        }
        if (ifRefExist(mailTracking, entry.getType(), bean.getReference(), entry.getYear())) {
            String message = "error.mail.tracking.reference.duplicated";
            model.addAttribute("entryBean", bean);

            model.addAttribute("visibilities", provideEntryVisibility());
            model.addAttribute("check", check);
            model.addAttribute("options", opt);

            setAssociateDocumentBean(model, null);
            String contextpath =
                    "/mail-tracking/management/prepareCopyEntry?entryId=" + entryId + "&check=" + check + "&message=" + message
                            + "&options=" + opt;
            return redirect(contextpath, bean, ra);

        }

        try {
            CorrespondenceEntry newEntry =
                    mailTracking
                            .createNewEntry(bean, readCorrespondenceTypeView(request.getParameter("entry.type"), model), null);
            model.addAttribute("entryId", newEntry.getExternalId());

            if (newEntry.getType().equals(CorrespondenceType.SENT)) {
                return chooseMailTracking(mailTracking.getExternalId(), newEntry.getYear().getExternalId(), check, opt, request,
                        model);
            }
            return prepareEditEntry(newEntry.getExternalId(), check, opt, model);
        } catch (Exception e) {
            String message = e.getMessage();

            String contextpath =
                    "/mail-tracking/management/prepareCopyEntry?entryId=" + entryId + "&check=" + check + "&message=" + message;

            return redirect(contextpath, bean, ra);

        }
    }

    @RequestMapping(value = "/prepareCreateNewEntry", method = RequestMethod.GET)
    public String prepareCreateNewEntry(@RequestParam(required = true, value = "mailTrackingId") String mailTraclingId,
            @RequestParam(value = "check") Boolean check, @RequestParam(value = "yearId") String yearId, @RequestParam(
                    value = "type") String type, @RequestParam(value = "message") String message,
            @RequestParam(value = "options") String options, final HttpServletRequest request, Model model) {
        final User u = Authenticate.getUser();
        final MailTracking mailTracking = FenixFramework.getDomainObject(mailTraclingId);
        if (!mailTracking.isUserAbleToViewMailTracking(u) || !mailTracking.isUserAbleToCreateEntries(u)) {
            return "mail-tracking/permissionDenied";
        }
        CorrespondenceEntryBean bean = (CorrespondenceEntryBean) request.getAttribute("entryBean");

        if (bean == null && request.getParameter("reference") == null) {
            bean = new CorrespondenceEntryBean(mailTracking);

        } else {

            bean = readCorrespondenceEntryBean(request, mailTracking, model);

        }

        setAssociateDocumentBean(model, null);

        model.addAttribute("entryBean", bean);
        model.addAttribute("mailTracking", mailTracking);

        model.addAttribute("check", check);
        model.addAttribute("yearId", yearId);
        model.addAttribute("options", options);

        return "mail-tracking/createNewEntry";
    }

    @RequestMapping(value = "/createNewEntry", method = RequestMethod.POST)
    public String createNewEntry(@RequestParam String mailTrackingId, @RequestParam String yearId,
            final HttpServletRequest request, final HttpServletResponse response, Model model, RedirectAttributes ra)
            throws Exception {
        final User u = Authenticate.getUser();
        MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);

        if (!mailTracking.isUserAbleToViewMailTracking(u) || !mailTracking.isUserAbleToCreateEntries(u)) {
            return "mail-tracking/permissionDenied";
        }
        String type = request.getParameter("type");
        CorrespondenceType c = CorrespondenceType.valueOf(type);
        Year year = FenixFramework.getDomainObject(yearId);

        Boolean check = Boolean.valueOf(request.getParameter("check"));
        String opt = request.getParameter("options");
        CorrespondenceEntryBean bean = new CorrespondenceEntryBean(mailTracking);
        bean = readCorrespondenceEntryBean(request, mailTracking, model);

        if (ifRefExist(mailTracking, c, bean.getReference(), year)) {
            String message = "error.mail.tracking.reference.duplicated";
            model.addAttribute("entryBean", bean);

            model.addAttribute("visibilities", provideEntryVisibility());
            model.addAttribute("check", check);
            model.addAttribute("options", opt);

            setAssociateDocumentBean(model, null);
            String contextpath =
                    "/mail-tracking/management/prepareCreateNewEntry?mailTrackingId=" + mailTrackingId + "&check=" + check
                            + "&yearId=" + yearId + "&type=" + type + "&message=" + message + "&options=" + opt;
            return redirect(contextpath, bean, ra);

        }
        try {
            CorrespondenceEntry newEntry = mailTracking.createNewEntry(bean, readCorrespondenceTypeView(type, model), null);
            model.addAttribute("entryId", newEntry.getExternalId());
            if (newEntry.getType().equals(CorrespondenceType.SENT)) {
                return chooseMailTracking(mailTracking.getExternalId(), newEntry.getYear().getExternalId(), check, opt, request,
                        model);
            }
            return prepareEditEntry(newEntry.getExternalId(), check, opt, model);
        } catch (MailTrackingDomainException e) {
            String message = e.getMessage();

            String contextpath =
                    "/mail-tracking/management/prepareCreateNewEntry?mailTrackingId=" + mailTrackingId + "&check=" + check
                            + "&yearId=" + yearId + "&type=" + type + "&message=" + message + "&options=" + opt;

            return redirect(contextpath, bean, ra);

        }

    }

    private boolean ifRefExist(MailTracking mailTracking, CorrespondenceType type, String reference, Year year) {

        if (CollectionUtils.select(mailTracking.getAnyStateEntries(type), new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {

                return ((CorrespondenceEntry) arg0).getReference().equals(reference)
                        && ((CorrespondenceEntry) arg0).getYear().getName().equals(year.getName());
            }
        }).size() >= 1) {

            return true;

        }
        return false;
    }

    private String redirect(String url, CorrespondenceEntryBean bean, RedirectAttributes ra) {

        if (bean == null && ra == null) {

            return "redirect:" + url;
        }
        ra.addAttribute("reference", bean.getReference());
        if (bean.getWhenReceived() != null) {
            ra.addAttribute("whenReceived", new LocalDate(bean.getWhenReceived().getYear(), bean.getWhenReceived()
                    .getMonthOfYear(), bean.getWhenReceived().getDayOfMonth()).toString());
        }
        if (bean.getWhenSent() != null) {
            ra.addAttribute("whenSent", new LocalDate(bean.getWhenSent().getYear(), bean.getWhenSent().getMonthOfYear(), bean
                    .getWhenSent().getDayOfMonth()).toString());

        }
        ra.addAttribute("sender", bean.getSender());
        ra.addAttribute("senderLetterNumber", bean.getSenderLetterNumber());
        ra.addAttribute("subject", bean.getSubject());
        ra.addAttribute("recipient", bean.getRecipient());
        ra.addAttribute("dispatchedToWhom", bean.getDispatchedToWhom());
        ra.addAttribute("observations", bean.getObservations());

        return "redirect:" + url;
    }

    @RequestMapping(value = "/prepareSetReferenceCounters", method = RequestMethod.GET)
    public String prepareSetReferenceCounters(@RequestParam(required = true, value = "mailTrackingId") String mailTrackingId,
            @RequestParam(value = "check") Boolean check, @RequestParam(required = true, value = "yearId") String yearId,
            @RequestParam(required = false, value = "options") String options, final HttpServletRequest request, Model model) {
        MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);

        if (!mailTracking.isUserAbleToSetReferenceCounters(Authenticate.getUser())) {
            return "mail-tracking/permissionDenied";
        }
        Year chosenYear = FenixFramework.getDomainObject(yearId);

        YearBean yearBean = new YearBean(mailTracking, chosenYear);

        model.addAttribute("mailTracking", mailTracking);
        model.addAttribute("yearBean", yearBean);
        model.addAttribute("chosenYear", chosenYear);
        model.addAttribute("yearId", yearId);
        model.addAttribute("check", check);
        model.addAttribute("options", options);

        return "/mail-tracking/setReferenceCounters";
    }

    @RequestMapping(value = "/setReferenceCounters", method = RequestMethod.POST)
    public String setReferenceCounters(@RequestParam(value = "mailTrackingId") String mailTrackingId, @RequestParam(
            value = "check") Boolean check, @RequestParam(required = true, value = "yearId") String yearId, @RequestParam(
            required = false, value = "options") String options, final HttpServletRequest request, Model model) throws Exception {
        final MailTracking mailTracking = FenixFramework.getDomainObject(mailTrackingId);
        if (!mailTracking.isUserAbleToSetReferenceCounters(Authenticate.getUser())) {
            return "mail-tracking/permissionDenied";
        }
        Year chosenYear = FenixFramework.getDomainObject(request.getParameter("yearId"));

        YearBean bean = new YearBean(chosenYear.getMailTracking(), chosenYear);

        bean.setNextReceivedEntryNumber(Integer.valueOf(request.getParameter("nextReceivedEntryNumber")));
        bean.setNextSentEntryNumber(Integer.valueOf(request.getParameter("nextSentEntryNumber")));

        try {
            MailTrackingActionOperations.setReferenceCounters(bean);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return prepareSetReferenceCounters(mailTrackingId, check, yearId, options, request, model);
        }

        return chooseMailTracking(mailTrackingId, yearId, check, options, request, model);
    }

    private static final String DOCUMENT_NOT_SPECIFIED_MESSAGE = "error.correspondence.entry.document.not.specified";
    private static final String DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE =
            "error.correspondence.entry.document.description.mandatory";
    private static final String MAX_FILE_EXCEEDED_MESSAGE = "error.correspondence.entry.document.file.size.exceeded";

    private Document createDocument(InputStream stream, Long fileSize, String description, String fileName, DocumentType type)
            throws IOException, DocumentUploadException {

        if (stream == null || fileSize == 0) {
            throw new DocumentUploadException(DOCUMENT_NOT_SPECIFIED_MESSAGE);
        }

        if (stream != null && Strings.isNullOrEmpty(description)) {
            throw new DocumentUploadException(DOCUMENT_DESCRIPTION_MANDATORY_MESSAGE);
        }

        if (fileSize > Document.MAX_DOCUMENT_FILE_SIZE) {
            throw new DocumentUploadException(MAX_FILE_EXCEEDED_MESSAGE);
        }

        byte[] content = consumeStream(fileSize, stream);

        return Document.saveDocument(description, fileName, content, description, type);
    }

    private static class DocumentUploadException extends java.lang.Exception {

        private static final long serialVersionUID = 1L;

        public DocumentUploadException(String message) {
            super(message);
        }
    }

    private byte[] consumeStream(Long fileSize, InputStream stream) throws IOException {
        byte[] content = new byte[fileSize.intValue()];
        stream.read(content);

        return content;
    }

    protected CorrespondenceEntryBean readCorrespondenceEntryBean(HttpServletRequest request, MailTracking mailTracking,
            Model model) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        final CorrespondenceEntryBean entryBean = new CorrespondenceEntryBean(mailTracking);
        entryBean.setReference(request.getParameter("reference"));
        if (!Strings.isNullOrEmpty(request.getParameter("owner"))) {
            Person person = FenixFramework.getDomainObject(request.getParameter("owner"));
            entryBean.setOwner(person);
        }
        if (!Strings.isNullOrEmpty(request.getParameter("whenReceived"))) {
            entryBean.setWhenReceived(formatter.parseLocalDate(request.getParameter("whenReceived")));
        }
        if (!Strings.isNullOrEmpty(request.getParameter("whenSent"))) {
            entryBean.setWhenSent(formatter.parseLocalDate(request.getParameter("whenSent")));
        }
        entryBean.setSender(request.getParameter("sender"));

        entryBean.setSenderLetterNumber(request.getParameter("senderLetterNumber"));
        entryBean.setSubject(request.getParameter("subject"));
        entryBean.setRecipient(request.getParameter("recipient"));
        entryBean.setDispatchedToWhom(request.getParameter("dispatchedToWhom"));
        entryBean.setObservations(request.getParameter("observations"));
        CustomEnum customEnum = entryBean.getVisibility();
        if (!Strings.isNullOrEmpty(request.getParameter("entry.visibility"))) {
            customEnum = new CustomEnum(CorrespondenceEntryVisibility.valueOf(request.getParameter("entry.visibility")));
        }

        entryBean.setVisibility(customEnum);

        model.addAttribute("correspondenceEntryBean", entryBean);
        return entryBean;
    }

    protected boolean preValidate(CorrespondenceEntryBean correspondenceEntryBean, HttpServletRequest request,
            CorrespondenceType correspondenceType, Model model) {

        if (Strings.isNullOrEmpty(correspondenceEntryBean.getSender())) {
            addMessage(model, "error.mail.tracking.sender.is.required", null);
            return false;
        }

        if (Strings.isNullOrEmpty(correspondenceEntryBean.getRecipient())) {
            addMessage(model, "error.mail.tracking.recipient.is.required", null);
            return false;
        }

        final String reference = correspondenceEntryBean.getReference();
        final String year = correspondenceEntryBean.getEntry().getYear().getName();
        final CorrespondenceEntry entry = correspondenceEntryBean.getEntry();
        final CorrespondenceType type = correspondenceEntryBean.getEntry().getType();

        if (CollectionUtils.select(correspondenceEntryBean.getMailTracking().getAnyStateEntries(correspondenceType),
                new Predicate() {

                    @Override
                    public boolean evaluate(Object arg0) {

                        return ((CorrespondenceEntry) arg0).getReference().equals(reference)
                                && ((CorrespondenceEntry) arg0).getYear().getName().equals(year)
                                && ((CorrespondenceEntry) arg0) != entry && ((CorrespondenceEntry) arg0).getType() == type;
                    }
                }).size() >= 1) {
            addMessage(model, "error.mail.tracking.reference.duplicated", null);
            return false;
        }

        return true;
    }

    private void addMessage(Model model, String string, String[] strings) {
        model.addAttribute("message", string);
    }

    @RequestMapping(value = "/populate/json/{mailId}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody String populate(@PathVariable String mailId,
            @RequestParam(required = false, value = "term") String term, final Model model) {
        final JsonArray result = new JsonArray();
        try {
            final String trimmedValue = URLDecoder.decode(term, "UTF-8").trim();
            final String[] input = StringNormalizer.normalize(trimmedValue).split(" ");
            findPeople(result, input);
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    private void findPeople(JsonArray result, String[] input) {
        findPeople(input).forEach(u -> addPersonToJson(result, u));
    }

    private Stream<User> findPeople(String[] input) {
        final Stream<User> users = Bennu.getInstance().getUserSet().stream();

        return users.filter(u -> match(input, u));
    }

    private void addPersonToJson(JsonArray result, User u) {

        final JsonObject o = new JsonObject();
        o.addProperty("id", u.getPerson().getExternalId());
        o.addProperty("name", u.getPerson().getPresentationName());
        result.add(o);
    }

    private boolean match(String[] values, User u) {

        return (values.length == 1 && u.getUsername().equalsIgnoreCase(values[0]))
                || (u.getProfile() != null && hasMatch(values, StringNormalizer.normalize(u.getProfile().getFullName())
                        .toLowerCase()));
    }

    private boolean hasMatch(final String[] input, final String unitNameParts) {

        for (final String namePart : input) {
            if (unitNameParts.indexOf(namePart) == -1) {
                return false;
            }
        }
        return true;
    }

    protected List<CustomEnum> provideEntryVisibility() {
        java.util.List<CustomEnum> values = new java.util.ArrayList<CustomEnum>();
        values.add(new CustomEnum(CorrespondenceEntryVisibility.TO_PUBLIC));
        values.add(new CustomEnum(CorrespondenceEntryVisibility.ONLY_OWNER_AND_OPERATOR));
        values.add(new CustomEnum(CorrespondenceEntryVisibility.ONLY_OPERATOR));
        return values;
    }

    protected String download(final HttpServletResponse response, final String filename, final byte[] bytes,
            final String contentType) throws IOException {

        try (final OutputStream outputStream = response.getOutputStream()) {
            response.setContentType(contentType);
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            response.setContentLength(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
            return null;
        }
    }

}
