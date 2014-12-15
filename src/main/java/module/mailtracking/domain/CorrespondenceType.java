/*
 * @(#)CorrespondenceType.java
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
package module.mailtracking.domain;

import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author Anil Kassamali
 * 
 */
public enum CorrespondenceType {
    SENT, RECEIVED;

    public String getSimpleName() {
        return this.name();
    }

    public String getQualifiedName() {
        return this.getClass().getName() + "." + this.getSimpleName();
    }

    public String getDescription() {
        return BundleUtil.getString("resources/MailTrackingResources", this.getQualifiedName());
    }

}
