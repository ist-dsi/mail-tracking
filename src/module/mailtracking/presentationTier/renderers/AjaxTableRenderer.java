package module.mailtracking.presentationTier.renderers;

import java.util.Collection;

import myorg.util.BundleUtil;
import pt.ist.fenixWebFramework.renderers.CollectionRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableHeader;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

public class AjaxTableRenderer extends CollectionRenderer {

    private String ajaxSourceUrl;

    private java.util.Map<String, String> extraParameter = new java.util.HashMap<String, String>();

    public AjaxTableRenderer() {
	super();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	Collection sortedCollection = RenderUtils.sortCollectionWithCriteria((Collection) object, getSortBy());

	return new AjaxTabularLayout(sortedCollection);
    }

    public class AjaxTabularLayout extends CollectionTabularLayout {

	public AjaxTabularLayout(Collection object) {
	    super(object);
	}

	@Override
	public HtmlComponent createComponent(Object object, Class type) {

	    HtmlInlineContainer container = new HtmlInlineContainer();

	    addAjaxDataTableScript(container);

	    int columnNumber = getNumberOfColumns();

	    initializeAjaxDataTable(container, columnNumber);

	    HtmlTable table = new HtmlTable();
	    container.addChild(table);

	    setTable(table);

	    if (hasHeader()) {
		HtmlTableHeader header = table.createHeader();

		HtmlTableRow firstRow = header.createRow();
		HtmlTableRow secondRow = null;

		if (hasHeaderGroups()) {
		    secondRow = header.createRow();
		}

		String lastGroup = null;
		HtmlTableCell lastGroupCell = null;

		for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++) {
		    String group = getHeaderGroup(columnIndex);

		    if (hasHeaderGroups() && group != null) {
			if (lastGroup != null && lastGroup.equals(group)) {
			    if (lastGroupCell.getColspan() == null) {
				lastGroupCell.setColspan(2);
			    } else {
				lastGroupCell.setColspan(lastGroupCell.getColspan() + 1);
			    }
			} else {
			    HtmlTableCell cell = firstRow.createCell();
			    cell.setBody(new HtmlText(group));

			    lastGroup = group;
			    lastGroupCell = cell;
			}

			HtmlTableCell cell = secondRow.createCell();
			cell.setBody(getHeaderComponent(columnIndex));
		    } else {
			lastGroup = null;
			lastGroupCell = null;

			HtmlTableCell cell = firstRow.createCell();
			cell.setBody(getHeaderComponent(columnIndex));

			if (hasHeaderGroups()) {
			    cell.setRowspan(2);
			}
		    }
		}
	    }

	    return container;
	}

	private void initializeAjaxDataTable(HtmlInlineContainer container, int columnNumber) {
	    HtmlScript script = new HtmlScript();

	    String scriptValue = "\n";
	    scriptValue += "$(document).ready(function() {\n";
	    scriptValue += "	$(\".ajax-table\").dataTable({\n";
	    scriptValue += getAjaxTableLanguageConfiguration() + ",\n";
	    scriptValue += "'bProcessing': true,\n";
	    scriptValue += "'bServerSide': true,\n";
	    scriptValue += "'sAjaxSource': \"" + getAjaxSourceUrlWithChecksum() + "\",\n";
	    scriptValue += "'fnServerData': function(sSource, aoData, fnCallback){\n";
	    for (java.util.Map.Entry<String, String> entry : getExtraParameter().entrySet())
		scriptValue += "aoData.push({'name' : '" + entry.getKey() + "', 'value' : '" + entry.getValue() + "' });\n";

	    scriptValue += "$.ajax({\n";
	    scriptValue += "'dataType': 'json',\n";
	    scriptValue += "'type': 'POST',\n";
	    scriptValue += "'url': sSource,\n";
	    scriptValue += "'data': aoData,\n";
	    scriptValue += "'success': fnCallback\n";
	    scriptValue += "});\n";
	    scriptValue += "},\n";
	    scriptValue += "'aoColumns': [\n";
	    for (int i = 0; i < (columnNumber - (getSortedLinksSize() > 0 ? 1 : 0)); i++) {
		scriptValue += "/*" + ((HtmlText) getHeaderComponent(i)).getText() + " */ ";
		scriptValue += String.format("{ \"sClass\": \"%s\" },\n", getColumnClassesFor(i));
	    }

	    if (AjaxTableRenderer.this.getSortedLinksSize() > 0) {
		scriptValue += "/*Links */ { 'bSortable': false,\n";
		scriptValue += String.format("\"sClass\" : \"%s\",", getColumnClassesFor(columnNumber - 1));
		scriptValue += "'fnRender': function(oObj) {\n";
		scriptValue += "var links='';\n";

		for (int i = 0; i < getSortedLinksSize(); i++) {
		    TableLink link = getTableLink(i);

		    String value = "";

		    if (link.getIcon() != null && !link.getIcon().equals("none")) {
			HtmlLink forImage = new HtmlLink();
			forImage.setModuleRelative(false);
			forImage.setContextRelative(true);
			forImage.setUrl("/images/" + link.getIcon() + ".gif");

			value = String.format("<img src='%s' alt='%s' />", forImage.calculateUrl(), link.getLinkText(link, null));
		    } else {
			value = link.getLinkText(link, null);
		    }

		    scriptValue += "if(oObj.aData[" + (columnNumber - 1) + "].split(',')[" + i
			    + "] != 'permission_not_granted')\n";
		    scriptValue += "links += \"<\" + \"a href='\" + oObj.aData[" + (columnNumber - 1) + "].split(',')[" + i
			    + "] + \"'>" + value + "</a>";

		    if (AjaxTableRenderer.this.getSortedLinksSize() > 1 && i < (AjaxTableRenderer.this.getSortedLinksSize() - 1)) {
			scriptValue += " ";
		    }

		    scriptValue += "\"\n";
		}

		scriptValue += "return links;\n";

	    }

	    scriptValue += "}\n";
	    scriptValue += "}\n";
	    scriptValue += "]\n";
	    scriptValue += "});\n";
	    scriptValue += "}\n";
	    scriptValue += ");\n";

	    script.setScript(scriptValue);

	    container.addChild(script);
	}

	private Object getColumnClassesFor(int i) {
	    String[] columnClasses = getColumnClasses().split(",");
	    if (i < columnClasses.length) {
		return columnClasses[i];
	    }

	    return "";
	}

	private String getAjaxTableLanguageConfiguration() {
	    String sProcessing = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sProcessing");
	    String sLengthMenu = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sLengthMenu");
	    String sZeroRecords = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sZeroRecords");
	    String sInfo = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sInfo");
	    String sInfoEmpty = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sInfoEmpty");
	    String sInfoFiltered = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sInfoFiltered");
	    String sInfoPostFix = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sInfoPostFix");
	    String sSearch = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sSearch");
	    String sFirst = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sFirst");
	    String sPrevious = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sPrevious");
	    String sNext = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sNext");
	    String sLast = BundleUtil.getFormattedStringFromResourceBundle("resources/MailTrackingResources",
		    "label.mail.tracking.ajax.table.sLast");

	    String returnValue = "";
	    returnValue += "'oLanguage': {\n";
	    returnValue += "\t'sProcessing': '" + sProcessing + "',\n";
	    returnValue += "\t'sLengthMenu': '" + sLengthMenu + "',\n";
	    returnValue += "\t'sZeroRecords': '" + sZeroRecords + "',\n";
	    returnValue += "\t'sInfo': '" + sInfo + "',\n";
	    returnValue += "\t'sInfoEmpty': '" + sInfoEmpty + "',\n";
	    returnValue += "\t'sInfoFiltered': '" + sInfoFiltered + "',\n";
	    returnValue += "\t'sInfoPostFix': '" + sInfoPostFix + "',\n";
	    returnValue += "\t'sSearch': '" + sSearch + "',\n";
	    returnValue += "\t'oPaginate': {\n";
	    returnValue += "\t\t'sFirst': '" + sFirst + "',\n";
	    returnValue += "\t\t'sPrevious': '" + sPrevious + "',\n";
	    returnValue += "\t\t'sNext': '" + sNext + "',\n";
	    returnValue += "\t\t'sLast': '" + sLast + "'\n";
	    returnValue += "\t}\n";
	    returnValue += "}";

	    return returnValue;
	}

	private void addAjaxDataTableScript(HtmlInlineContainer container) {
	    HtmlLink link = new HtmlLink();
	    link.setModuleRelative(false);
	    link.setContextRelative(true);

	    link.setUrl("/javaScript/dataTables/media/js/jquery.dataTables.js");

	    HtmlScript script = new HtmlScript("text/javascript", link.calculateUrl(), true);

	    container.addChild(script);
	}

	@Override
	public void applyStyle(HtmlComponent component) {

	    HtmlInlineContainer container = (HtmlInlineContainer) component;

	    HtmlTable table = (HtmlTable) container.getChildren().get(2);

	    table.setClasses(getClasses() + " ajax-table");
	    table.setStyle(getStyle());
	    table.setTitle(getTitle());

	    table.setCaption(getCaption());
	    table.setSummary(getSummary());

	    // header
	    if (getHeaderClasses() != null) {
		// decompose header cell classes
		String[] headerClasses = null;
		if (getHeaderClasses() != null) {
		    headerClasses = getHeaderClasses().split(",", -1);
		}

		HtmlTableHeader header = table.getHeader();
		if (header != null) {
		    for (HtmlTableRow row : header.getRows()) {
			int cellIndex = 0;
			for (HtmlTableCell cell : row.getCells()) {
			    String choosenCellClass = headerClasses[cellIndex % headerClasses.length];
			    cell.setClasses(choosenCellClass);

			    cellIndex++;
			}
		    }
		}
	    }

	    // decompose row and cell classes
	    String[] rowClasses = null;
	    if (getRowClasses() != null) {
		rowClasses = getRowClasses().split(",", -1);
	    }

	    String[] cellClasses = null;
	    if (getColumnClasses() != null) {
		cellClasses = getColumnClasses().split(",", -1);
	    }

	    // check if additional styling is needed
	    if (rowClasses == null && cellClasses == null) {
		return;
	    }

	    // apply style by rows and columns
	    int rowIndex = 0;
	    for (HtmlTableRow row : table.getRows()) {
		if (rowClasses != null) {
		    String chooseRowClass = rowClasses[rowIndex % rowClasses.length];
		    if (!chooseRowClass.equals("")) {
			row.setClasses(chooseRowClass);
		    }
		}

		if (cellClasses != null) {
		    int cellIndex = 0;
		    for (HtmlTableCell cell : row.getCells()) {
			String chooseCellClass = cellClasses[cellIndex % cellClasses.length];
			if (!chooseCellClass.equals("")) {
			    cell.setClasses(chooseCellClass);
			}

			cellIndex++;
		    }
		}

		rowIndex++;
	    }
	}

	@Override
	protected void setExtraComponentOptions(Object object, HtmlComponent component, Class type) {
	    HtmlInlineContainer container = (HtmlInlineContainer) component;
	    HtmlTable table = (HtmlTable) container.getChildren().get(2);

	    table.setRenderCompliantTable(getRenderCompliantTable());
	}

    }

    public String getAjaxSourceUrl() {
	return this.ajaxSourceUrl;
    }

    public void setAjaxSourceUrl(String value) {
	this.ajaxSourceUrl = value;
    }

    public String getAjaxSourceUrlWithChecksum() {
	HtmlLink link = new HtmlLink();
	link.setUrl(getAjaxSourceUrl());
	link.setModuleRelative(false);
	link.setContextRelative(true);

	String urlParametersBoundaryCharacter = "&";
	if (link.calculateUrl().indexOf("?") == -1)
	    urlParametersBoundaryCharacter = "?";

	return link.calculateUrl()
		+ String.format(urlParametersBoundaryCharacter + "%s=%s", GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME,
			GenericChecksumRewriter.calculateChecksum(link.calculateUrl()));
    }

    public java.util.Map<String, String> getExtraParameter() {
	return this.extraParameter;
    }

    public void setExtraParameter(String key, String value) {
	this.extraParameter.put(key, value);
    }

    /**
     * The counter isnt used in this kind of tables
     * 
     * @property
     */
    public void setCounter(String name, String value) {
	/* Do nothing */
    }

    public String getCounter(String name) {
	return null;
    }

}
