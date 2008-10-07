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
/*VCSID=892da7a6-23d9-496b-b622-d091596eb778*/
package com.sun.max.vm.compiler.cir.dir;

import java.util.*;

import com.sun.max.collect.*;
import com.sun.max.vm.compiler.cir.*;
import com.sun.max.vm.compiler.cir.transform.*;
import com.sun.max.vm.compiler.cir.variable.*;
import com.sun.max.vm.type.*;

/**
 * Determines the Kinds of the arguments of all normal continuation variables
 * by searching for continuation abstractions
 * that these variables get bound to directly or transitively.
 *
 * @author Bernd Mathiske
 */
public class CirContinuationKindScout extends CirTraversal {

    private final Kind _outerResultKind;

    private final Bag<CirVariable, CirVariable, Sequence<CirVariable>> _bindings = new SequenceBag<CirVariable, CirVariable>(SequenceBag.MapType.IDENTITY);
    private final GrowableMapping<CirVariable, Kind> _cirContinuationParameterToKind = HashMapping.createIdentityMapping();

    CirContinuationKindScout(Kind outerResultKind, CirClosure closure) {
        super(closure);
        _outerResultKind = outerResultKind;
        final CirVariable normalContinuationParameter = closure.parameters()[closure.parameters().length - 2];
        _cirContinuationParameterToKind.put(normalContinuationParameter, outerResultKind);
    }

    private void registerNormalContinuationParameters(CirClosure closure, CirValue[] arguments) {
        for (int i = 0; i < arguments.length; i++) {
            final CirVariable parameter = closure.parameters()[i];
            if (parameter instanceof CirNormalContinuationParameter) {
                final CirValue argument = arguments[i];
                if (argument instanceof CirClosure) {
                    final CirClosure continuation = (CirClosure) argument;
                    if (continuation.parameters().length == 0) {
                        _cirContinuationParameterToKind.put(parameter, Kind.VOID);
                    } else {
                        assert continuation.parameters().length == 1;
                        _cirContinuationParameterToKind.put(parameter, continuation.parameters()[0].kind());
                    }
                } else {
                    _bindings.add(closure.parameters()[i], (CirVariable) argument);
                }
            }
        }
    }

    @Override
    public void visitCall(CirCall call) {
        if (call.procedure() instanceof CirBlock) {
            final CirBlock block = (CirBlock) call.procedure();
            registerNormalContinuationParameters(block.closure(), call.arguments());
        } else if (call.procedure() instanceof CirClosure) {
            registerNormalContinuationParameters((CirClosure) call.procedure(), call.arguments());
        }
        super.visitCall(call);
    }

    private boolean _initialized;

    public Kind getKind(CirNormalContinuationParameter continuationParameter) {
        if (!_initialized) {
            run();
        }
        final IdentityHashSet<CirVariable> done = new IdentityHashSet<CirVariable>();
        final LinkedList<CirVariable> todo = new LinkedList<CirVariable>();
        todo.add(continuationParameter);

        while (!todo.isEmpty()) {
            final CirVariable variable = todo.remove();
            if (!done.contains(variable)) {
                final Kind kind = _cirContinuationParameterToKind.get(variable);
                if (kind != null) {
                    if (variable != continuationParameter) {
                        // Remember the result, in case the same query comes up again:
                        _cirContinuationParameterToKind.put(continuationParameter, kind);
                    }
                    return kind;
                }
                done.add(variable);
                final Sequence<CirVariable> arguments = _bindings.get(variable);
                for (CirVariable argument : arguments) {
                    todo.add(argument);
                }
            }
        }
        // The variable was never used, use an arbitrary kind for it:
        _cirContinuationParameterToKind.put(continuationParameter, Kind.VOID);
        return Kind.VOID;
    }
}
