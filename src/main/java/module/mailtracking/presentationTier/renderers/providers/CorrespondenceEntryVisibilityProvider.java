/*
 * @(#)CorrespondenceEntryVisibilityProvider.java
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
package module.mailtracking.presentationTier.renderers.providers;

import module.mailtracking.domain.CorrespondenceEntryVisibility;
import module.mailtracking.domain.CorrespondenceEntryVisibility.CustomEnum;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.BiDirectionalConverter;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * 
 * @author Anil Kassamali
 * 
 */
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
