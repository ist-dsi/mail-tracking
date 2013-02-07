/*
 * @(#)LocalDateConverter.java
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
package module.mailtracking.presentationTier.renderers.converters;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pt.ist.fenixWebFramework.renderers.components.converters.ConversionException;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.converters.DateConverter;

/**
 * 
 * @author Anil Kassamali
 * 
 */
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
