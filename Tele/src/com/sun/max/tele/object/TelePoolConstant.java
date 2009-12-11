/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
package com.sun.max.tele.object;

import com.sun.max.tele.*;
import com.sun.max.vm.reference.*;

/**
 * Inspector's canonical surrogate for an object of type {@link AbstractPoolConstant} in the tele VM.
 *
 * @author Michael Van De Vanter
 */
public class TelePoolConstant extends TeleTupleObject {

    TelePoolConstant(TeleVM teleVM, Reference poolConstantReference) {
        super(teleVM, poolConstantReference);
    }

    /**
     * @return whether a reference constant in the {@link TeleVM} is resolved; true for non-reference constants.
     * Does not read from the {@link TeleVM}.
     */
    public boolean isResolved() {
        return true;
    }

    @Override
    public String maxineRole() {
        return "PoolConstant";
    }

    @Override
    public String maxineTerseRole() {
        return "PoolConstant";
    }

}
