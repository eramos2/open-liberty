/*******************************************************************************
 * Copyright (c) 2020, 2021 IBM Corporation and others.
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

package com.ibm.ws.jpa.fvt.callback.listeners.orderofinvocation.b2;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.ibm.ws.jpa.fvt.callback.AbstractCallbackListener;

public class AnoCallbackListenerProtectedB2 extends AbstractCallbackListener {
    private static final AnoCallbackListenerProtectedB2 _singleton = new AnoCallbackListenerProtectedB2();

    public final static AbstractCallbackListener getSingleton() {
        return _singleton;
    }

    public final static void reset() {
        _singleton.resetCallbackData();
    }

    @PrePersist
    protected void prePersistCallback(Object entity) {
        _singleton.doPrePersist(ProtectionType.PT_PROTECTED);
    }

    @PostPersist
    protected void postPersistCallback(Object entity) {
        _singleton.doPostPersist(ProtectionType.PT_PROTECTED);
    }

    @PreUpdate
    protected void preUpdateCallback(Object entity) {
        _singleton.doPreUpdate(ProtectionType.PT_PROTECTED);
    }

    @PostUpdate
    protected void postUpdateCallback(Object entity) {
        _singleton.doPostUpdate(ProtectionType.PT_PROTECTED);
    }

    @PreRemove
    protected void preRemoveCallback(Object entity) {
        _singleton.doPreRemove(ProtectionType.PT_PROTECTED);
    }

    @PostRemove
    protected void postRemoveCallback(Object entity) {
        _singleton.doPostRemove(ProtectionType.PT_PROTECTED);
    }

    @PostLoad
    protected void postLoadCallback(Object entity) {
        _singleton.doPostLoad(ProtectionType.PT_PROTECTED);
    }
}