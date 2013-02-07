/*
 * @(#)SearchUserBean.java
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

import pt.ist.bennu.core.util.BundleUtil;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public class SearchUserBean implements java.io.Serializable {

    public static enum SearchUserMode {
        NAME, USERNAME;

        public String getSimpleName() {
            return this.name();
        }

        public String getQualifiedName() {
            return this.getClass().getName() + "." + this.getSimpleName();
        }

        public String getDescription() {
            return BundleUtil.getStringFromResourceBundle("resources/MailTrackingResources", this.getQualifiedName());
        }
    }

    private static final long serialVersionUID = 1L;

    private String value;
    private SearchUserMode mode;

    public SearchUserBean() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SearchUserMode getMode() {
        return mode;
    }

    public void setMode(SearchUserMode mode) {
        this.mode = mode;
    }

}
