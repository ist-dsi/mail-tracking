package module.mailtracking.presentationTier.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.mailtracking.domain.CorrespondenceEntry;
import module.mailtracking.domain.CorrespondenceType;
import module.mailtracking.domain.MailTracking;
import module.mailtracking.domain.CorrespondenceEntry.CorrespondenceEntryBean;
import module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum;
import module.mailtracking.domain.exception.PermissionDeniedException;
import module.mailtracking.presentationTier.layout.EmptyContextLayout;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/ajax-mailtracking")
public class MailTrackingAction extends module.mailtracking.presentationTier.MailTrackingAction {

    public ActionForward prepareCreateFastNewEntry(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) {
	readCorrespondenceEntryBean(request);
	setAssociateDocumentBean(request, null);

	return forward(request, "/mailtracking/ajax/createNewEntry.jsp");
    }

    @Override
    public final ActionForward addNewEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	MailTracking mailTracking = readMailTracking(request);

	if (!mailTracking.isCurrentUserAbleToCreateEntries())
	    throw new PermissionDeniedException();

	if (!preValidate(readCorrespondenceEntryBean(request), request)) {
	    RenderUtils.invalidateViewState("associate.document.bean");
	    setAssociateDocumentBean(request, null);
	    return prepareCreateNewEntry(mapping, form, request, response);
	}

	CorrespondenceEntry entry = mailTracking.createNewEntry(readCorrespondenceEntryBean(request),
		readCorrespondenceTypeView(request), null);
	addMessage(request, "message.mail.tracking.add.entry.successfully");
	request.setAttribute("entryId", entry.getExternalId());

	return viewEntry(mapping, form, request, response);
    }

    @Override
    public ActionForward viewEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	super.viewEntry(mapping, form, request, response);

	return forward(request, "/mailtracking/ajax/viewCorrespondenceEntry.jsp");
    }

    public ActionForward prepareCopyEntry(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) {
	CorrespondenceEntryBean bean = readCorrespondenceEntryBean(request);
	CorrespondenceEntry entry = getCorrespondenceEntryWithExternalId(request);

	bean.setOwner(entry.getOwner());
	bean.setVisibility(new CustomEnum(entry.getVisibility()));
	bean.setMailTracking(entry.getMailTracking());
	bean.setObservations(entry.getObservations());

	if (CorrespondenceType.RECEIVED.equals(entry.getType())) {
	    if (entry.getWhenReceived() != null)
		bean.setWhenReceived(new LocalDate(entry.getWhenReceived().getYear(), entry.getWhenReceived().getMonthOfYear(),
			entry.getWhenReceived().getDayOfMonth()));
	    bean.setSender(entry.getSender());
	    if (entry.getWhenSent() != null)
		bean.setWhenSent(new LocalDate(entry.getWhenSent().getYear(), entry.getWhenSent().getMonthOfYear(), entry
			.getWhenSent().getDayOfMonth()));
	    bean.setSenderLetterNumber(entry.getSenderLetterNumber());
	    bean.setSubject(entry.getSubject());
	    bean.setRecipient(entry.getRecipient());
	    bean.setDispatchedToWhom(entry.getDispatchedToWhom());
	} else if (CorrespondenceType.SENT.equals(entry.getType())) {
	    if (entry.getWhenSent() != null)
		bean.setWhenSent(new LocalDate(entry.getWhenSent().getYear(), entry.getWhenSent().getMonthOfYear(), entry
			.getWhenSent().getDayOfMonth()));
	    bean.setRecipient(entry.getRecipient());
	    bean.setSubject(entry.getSubject());
	    bean.setSender(entry.getSender());
	}

	readCorrespondenceEntryBean(request);
	setAssociateDocumentBean(request, null);

	return forward(request, "/mailtracking/ajax/createNewEntry.jsp");
    }

    @Override
    public Context createContext(String contextPathString, HttpServletRequest request) {
	return new EmptyContextLayout();
    }

}
