/*******************************************************************************
 * Copyright (c) 1997, 2008 IBM Corporation and others.
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

package com.ibm.ws.security.oauth20.exception;

public class OAuthDataException extends Exception {

    private static final long serialVersionUID = 402L;

    public OAuthDataException(Exception e) {
        super(e);
    }

    public OAuthDataException(String msg) {
        super(msg);
    }

}
