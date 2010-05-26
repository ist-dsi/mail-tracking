package module.mailtracking.presentationTier.renderers;

import pt.ist.fenixWebFramework.rendererExtensions.AutoCompleteInputRenderer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlScript;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.HtmlTextInput;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixWebFramework.renderers.model.MetaSlot;
import pt.ist.fenixWebFramework.renderers.model.MetaSlotKey;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.renderers.utils.RendererPropertyUtils;

public class SimpleAutoCompleteInputRenderer extends AutoCompleteInputRenderer {

    public SimpleAutoCompleteInputRenderer() {
	super();
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
	return new Layout() {

	    @Override
	    public HtmlComponent createComponent(Object object, Class type) {

		HtmlInlineContainer container = new HtmlInlineContainer();

		addScripts(container);

		MetaSlotKey key = (MetaSlotKey) getContext().getMetaObject().getKey();

		HtmlHiddenField valueField = new HtmlHiddenField();

		valueField.setId(key.toString() + "_AutoComplete");
		valueField.setName(valueField.getId());

		container.addChild(valueField);

		if (object != null && object instanceof String) {
		    valueField.setValue((String) object);
		} else if (object != null) {
		    Object oid = RendererPropertyUtils.getProperty(object, getValueField(), false);
		    valueField.setValue(oid == null ? null : oid.toString());
		}

		HtmlHiddenField oldValueField = new HtmlHiddenField();
		oldValueField.setId(key.toString() + "_OldValue");
		oldValueField.setName(oldValueField.getId());
		container.addChild(oldValueField);

		HtmlTextInput textField = new HtmlTextInput();
		textField.setTargetSlot(key);
		textField.setId(key.toString());
		textField.setName(textField.getId());
		textField.setClasses(getTextFieldStyleClass());
		textField.setSize(getSize());
		container.addChild(textField);

		if (object != null && getLabelField() != null && object instanceof String) {
		    textField.setValue((String) object);
		} else if (object != null && getLabelField() != null) {
		    String label = (String) RendererPropertyUtils.getProperty(object, getLabelField(), false);
		    textField.setValue(label);
		} else if (getRawSlotName() != null) {
		    Object beanObject = getInputContext().getParentContext().getMetaObject().getObject();

		    if (beanObject != null) { // protect from a creation context
			String rawText = (String) RendererPropertyUtils.getProperty(beanObject, getRawSlotName(), false);
			textField.setValue(rawText);

			if (rawText != null) {
			    valueField.setValue(TYPING_VALUE);
			}
		    }
		}

		HtmlText errorMessage = new HtmlText(RenderUtils.getResourceString("fenix.renderers.autocomplete.error"));
		errorMessage.setId(key.toString() + "_Error");
		errorMessage.setClasses(getErrorStyleClass());
		errorMessage.setStyle("display: none;");
		container.addChild(errorMessage);

		addFinalScript(container, textField.getId());

		return container;
	    }

	    private void addScripts(HtmlInlineContainer container) {
		HtmlLink link = new HtmlLink();
		link.setModuleRelative(false);
		link.setContextRelative(true);

		String[] scriptNames = new String[] { "autoComplete.js", "autoCompleteHandlers.js" };
		for (String script : scriptNames) {
		    addSingleScript(container, link, script);
		}
	    }

	    private void addSingleScript(HtmlInlineContainer container, HtmlLink link, String scriptName) {
		link.setUrl("/javaScript/" + scriptName);
		HtmlScript script = new HtmlScript("text/javascript", link.calculateUrl(), true);
		container.addChild(script);
	    }

	    private void addFinalScript(HtmlInlineContainer container, String textFieldId) {

		HtmlLink link = new HtmlLink();
		link.setModuleRelative(false);
		link.setContextRelative(true);

		link.setUrl(SERVLET_URI);
		link.setParameter("args", getFormatedArgs());
		link.setParameter("labelField", getLabelField());
		link.setParameter("valueField", getValueField()); // TODO: allow
		// configuration,1
		// needs also
		// converter
		link.setParameter("styleClass", getAutoCompleteItemsStyleClass() == null ? "" : getAutoCompleteItemsStyleClass());
		link.setEscapeAmpersand(false);

		if (getFormat() != null) {
		    link.setParameter("format", getFormat());
		}

		if (getMaxCount() != null) {
		    link.setParameter("maxCount", String.valueOf(getMaxCount()));
		}

		String finalUri = link.calculateUrl();
		String scriptText = "$(\"input#"
			+ escapeId(textFieldId)
			+ "\").autocomplete(\""
			+ finalUri
			+ "\", { minChars: "
			+ getMinChars()
			+ ", validSelection: false"
			+ ", cleanSelection: clearAutoComplete, select: selectElement, after: updateCustomValue, error:showError});";

		HtmlScript script = new HtmlScript();
		script.setContentType("text/javascript");
		script.setScript(scriptText);

		container.addChild(script);
	    }

	    private String escapeId(String textFieldId) {
		return textFieldId.replace(".", "\\\\.").replaceAll(":", "\\\\\\\\:");
	    }

	    private String getFormatedArgs() {
		Object object = ((MetaSlot) getInputContext().getMetaObject()).getMetaObject().getObject();
		return RenderUtils.getFormattedProperties(getArgs(), object);
	    }

	};
    }
}
