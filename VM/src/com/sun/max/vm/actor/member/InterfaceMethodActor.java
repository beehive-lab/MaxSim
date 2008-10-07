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
/*VCSID=c30211dc-ed61-47c6-afb1-698af2adae4c*/
package com.sun.max.vm.actor.member;

import com.sun.max.annotate.*;
import com.sun.max.vm.classfile.constant.*;
import com.sun.max.vm.type.*;

/**
 * Interface method declarations.
 *
 * @author Bernd Mathiske
 */
public class InterfaceMethodActor extends MethodActor {

    public InterfaceMethodActor(Utf8Constant name, SignatureDescriptor descriptor, int flags) {
        super(name, descriptor, flags);
    }

    @CONSTANT
    private int _iIndexInInterface;

    @INLINE
    public final int iIndexInInterface() {
        return _iIndexInInterface;
    }

    public void setIIndexInInterface(int iIndexInInterface) {
        assert _iIndexInInterface == 0;
        assert iIndexInInterface > 0;
        _iIndexInInterface = iIndexInInterface;
    }
}
