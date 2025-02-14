/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
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
package com.ibm.ws.cdi.internal.archive.liberty;

import java.net.URL;

import com.ibm.ws.cdi.internal.interfaces.Resource;
import com.ibm.wsspi.adaptable.module.Entry;

/**
 *
 */
public class ResourceImpl implements Resource {

    private final Entry entry;

    /**
     * @param entry
     */
    public ResourceImpl(Entry entry) {
        this.entry = entry;
    }

    /** {@inheritDoc} */
    @Override
    public URL getURL() {
        return entry.getResource();
    }

    @Override
    public String toString() {
        return "ResourceImpl: " + entry.getPath();
    }

}
