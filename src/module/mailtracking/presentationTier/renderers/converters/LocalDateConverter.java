package module.mailtracking.presentationTier.renderers.converters;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.DateConverter;

public class LocalDateConverter extends Converter {

    private Converter dateConverter;

    public LocalDateConverter(String dateFormat) {
	this.dateConverter = new DateConverter(new SimpleDateFormat(dateFormat, Locale.getDefault()));
    }

    @Override
    public Object convert(Class type, Object value) {
	Date date = (Date) this.dateConverter.convert(type, value);

	if (date == null) {
	    return null;
	}

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);

	try {
	    return convertCalendarToPartial(type, calendar);
	} catch (Exception e) {
	    throw new ConversionException();
	}

    }

    private Object convertCalendarToPartial(Class type, Calendar calendar) throws Exception {
	Method method = type.getMethod("fromCalendarFields", new Class[] { Calendar.class });

	return method.invoke(null, new Object[] { calendar });
    }

}
