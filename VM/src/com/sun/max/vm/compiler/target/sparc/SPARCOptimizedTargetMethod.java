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
/*VCSID=d51fd2c1-95d9-4751-9df2-6b988df86430*/
package com.sun.max.vm.compiler.target.sparc;

import com.sun.max.asm.*;
import com.sun.max.program.*;
import com.sun.max.unsafe.*;
import com.sun.max.vm.actor.member.*;
import com.sun.max.vm.compiler.target.*;

/**
 * @author Bernd Mathiske
 */
public class SPARCOptimizedTargetMethod extends OptimizedTargetMethod implements SPARCTargetMethod {

    public SPARCOptimizedTargetMethod(ClassMethodActor classMethodActor) {
        super(classMethodActor);
    }

    @Override
    public InstructionSet instructionSet() {
        return SPARCTargetMethod.Static.instructionSet();
    }

    @Override
    public final int registerReferenceMapSize() {
        return SPARCTargetMethod.Static.registerReferenceMapSize();
    }

    @Override
    public final void patchCallSite(int callOffset, Word callEntryPoint) {
        SPARCTargetMethod.Static.patchCallSite(this, callOffset, callEntryPoint);
    }

    @Override
    public void forwardTo(TargetMethod newTargetMethod) {
        throw Problem.unimplemented();
    }
}
