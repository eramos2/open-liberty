/*******************************************************************************
 * Copyright (c) 2002, 2019 IBM Corporation and others.
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
package com.ibm.ejb2x.base.spec.sfr.ejb;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (08/18/2000 11:27:51 AM)
 *
 * @author Administrator
 */
//
//
//  SFRaFake PrimaryKey to execute remove(PrimaryKey) method of Home interface.
//
//
@SuppressWarnings("serial")
public class SFRaFakeKey implements Serializable {
    public String keyid;

    /**
     * SFRaFakeKey constructor comment.
     */
    public SFRaFakeKey() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (08/18/2000 12:26:44 PM)
     *
     * @param id java.lang.String
     */
    public SFRaFakeKey(String id) {
        this.keyid = id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (08/18/2000 12:09:13 PM)
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return keyid;
    }
}