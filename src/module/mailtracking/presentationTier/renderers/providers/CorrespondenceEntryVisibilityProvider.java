package module.mailtracking.presentationTier.renderers.providers;

import module.mailtracking.domain.CorrespondenceEntryVisibility;
import module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class CorrespondenceEntryVisibilityProvider implements DataProvider {

    @Override
    public Converter getConverter() {
	return new CustomConverter();
    }

    @Override
    public Object provide(Object source, Object currentValue) {
	java.util.List<CustomEnum> values = new java.util.ArrayList<CustomEnum>();
	values.add(new CustomEnum(CorrespondenceEntryVisibility.TO_PUBLIC));
	values.add(new CustomEnum(CorrespondenceEntryVisibility.ONLY_OWNER_AND_OPERATOR));
	values.add(new CustomEnum(CorrespondenceEntryVisibility.ONLY_OPERATOR));
	return values;
    }

    public static class CustomConverter extends BiDirectionalConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object convert(Class type, Object value) {
	    return new CustomEnum(CorrespondenceEntryVisibility.valueOf((String) value));
	}

	@Override
	public String deserialize(Object object) {
	    return ((CustomEnum) object).getCustomEnum().name();
	}
    }

}
