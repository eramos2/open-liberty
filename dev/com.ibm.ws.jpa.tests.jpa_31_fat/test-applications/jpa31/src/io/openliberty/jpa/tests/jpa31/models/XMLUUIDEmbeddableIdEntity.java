/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package io.openliberty.jpa.tests.jpa31.models;

public class XMLUUIDEmbeddableIdEntity {
    private XMLEmbeddableUUID_ID id;

    private String strData;

    public XMLUUIDEmbeddableIdEntity() {

    }

    /**
     * @return the id
     */
    public XMLEmbeddableUUID_ID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(XMLEmbeddableUUID_ID id) {
        this.id = id;
    }

    /**
     * @return the strData
     */
    public String getStrData() {
        return strData;
    }

    /**
     * @param strData the strData to set
     */
    public void setStrData(String strData) {
        this.strData = strData;
    }

    @Override
    public String toString() {
        return "XMLUUIDEmbeddableIdEntity [id=" + id + ", strData=" + strData + "]";
    }

}
