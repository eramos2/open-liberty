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

package com.ibm.ws.jpa.fvt.inheritance.entities.jointable.ano;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ibm.ws.jpa.fvt.inheritance.entities.ITreeLeaf3;

@Entity
@Table(name = "AnoJTCDLeaf3")
@DiscriminatorValue("D")
public class AnoJTCDTreeLeaf3Entity extends AnoJTCDTreeMSC implements ITreeLeaf3 {
    public AnoJTCDTreeLeaf3Entity() {
        super();
    }

    private String stringVal2;

    @Override
    public String getStringVal2() {
        return stringVal2;
    }

    @Override
    public void setStringVal2(String stringVal2) {
        this.stringVal2 = stringVal2;
    }

    @Override
    public String toString() {
        return "AnoJTCDTreeLeaf3Entity [stringVal2=" + stringVal2 + ", getStringVal1()=" + getStringVal1()
               + ", getId()=" + getId() + ", getName()=" + getName() + "]";
    }
}
