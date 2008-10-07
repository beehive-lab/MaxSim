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
/*VCSID=a5d23ee0-74d8-4c5c-8ef1-2786f53fc927*/
package com.sun.max.vm.compiler.tir;

import com.sun.max.vm.compiler.dir.*;
import com.sun.max.vm.compiler.tir.pipeline.*;

public class TirDirCall extends TirCall {
    private DirMethod _dirMethod;
    public TirDirCall(DirMethod dirMethod, TirInstruction... arguments) {
        super(dirMethod.classMethodActor(), arguments);
        _dirMethod = dirMethod;
    }

    @Override
    public void accept(TirInstructionVisitor visitor) {
        visitor.visit(this);
    }

    public DirMethod dirMethod() {
        return _dirMethod;
    }

    @Override
    public String toString() {
        return "DIR " + method().name().toString();
    }
}
