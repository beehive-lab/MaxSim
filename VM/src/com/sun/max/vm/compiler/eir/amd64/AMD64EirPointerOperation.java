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
/*VCSID=39d73404-eaad-4f61-a489-62d3486488cd*/
package com.sun.max.vm.compiler.eir.amd64;

import static com.sun.max.vm.compiler.eir.EirLocationCategory.*;

import com.sun.max.asm.amd64.*;
import com.sun.max.collect.*;
import com.sun.max.lang.*;
import com.sun.max.program.*;
import com.sun.max.vm.compiler.eir.*;
import com.sun.max.vm.type.*;
import com.sun.max.vm.value.*;

/**
 * @author Bernd Mathiske
 */
public abstract class AMD64EirPointerOperation extends AMD64EirBinaryOperation {

    private final Kind _kind;

    public Kind kind() {
        return _kind;
    }

    private final Kind _offsetKind;

    private final EirOperand _offsetOperand;

    public EirOperand offsetOperand() {
        return _offsetOperand;
    }

    private final EirOperand _indexOperand;

    public EirOperand indexOperand() {
        return _indexOperand;
    }

    public AMD64EirRegister.General indexGeneralRegister() {
        return (AMD64EirRegister.General) indexOperand().location();
    }

    private static PoolSet<EirLocationCategory> offsetLocationCategories(Kind kind) {
        switch (kind.asEnum()) {
            case INT:
                return G_I8_I32;
            case LONG:
            case WORD:
                return G;
            default:
                throw ProgramError.unexpected("pointer offset not integer");
        }
    }

    protected AMD64EirPointerOperation(
        EirBlock block,
        Kind kind,
        EirValue destination,
        EirOperand.Effect destinationEffect,
        PoolSet<EirLocationCategory> destinationLocationCategories,
        EirValue pointer) {

        super(block, destination, destinationEffect, destinationLocationCategories,
                     pointer, EirOperand.Effect.USE, G);
        _kind = kind;
        _offsetKind = null;
        _offsetOperand = null;
        _indexOperand = null;
    }

    protected AMD64EirPointerOperation(
        EirBlock block,
        Kind kind,
        EirValue destination,
        EirOperand.Effect destinationEffect,
        PoolSet<EirLocationCategory> destinationLocationCategories,
        EirValue pointer,
        Kind offsetKind,
        EirValue offset) {

        super(block, destination, destinationEffect, destinationLocationCategories,
                     pointer, EirOperand.Effect.USE, G);
        _kind = kind;
        _offsetKind = offsetKind;
        _offsetOperand = new EirOperand(this, EirOperand.Effect.USE, offsetLocationCategories(offsetKind));
        _offsetOperand.setEirValue(offset);
        _indexOperand = null;
    }

    protected AMD64EirPointerOperation(
        EirBlock block,
        Kind kind,
        EirValue destination,
        EirOperand.Effect destinationEffect,
        PoolSet<EirLocationCategory> destinationLocationCategories,
        EirValue pointer,
        EirValue index) {

        super(block, destination, destinationEffect, destinationLocationCategories,
                     pointer, EirOperand.Effect.USE, G);
        _kind = kind;
        _offsetKind = null;
        _offsetOperand = null;
        _indexOperand = new EirOperand(this, EirOperand.Effect.USE, G);
        _indexOperand.setEirValue(index);
    }

    protected AMD64EirPointerOperation(
        EirBlock block,
        Kind kind,
        EirValue destination,
        EirOperand.Effect destinationEffect,
        PoolSet<EirLocationCategory> destinationLocationCategories,
        EirValue pointer,
        EirValue displacement,
        EirValue index) {

        super(block, destination, destinationEffect, destinationLocationCategories,
                     pointer, EirOperand.Effect.USE, G);
        _kind = kind;
        _offsetKind = Kind.INT;
        _offsetOperand = new EirOperand(this, EirOperand.Effect.USE, I8_I32);
        _offsetOperand.setEirValue(displacement);
        _indexOperand = new EirOperand(this, EirOperand.Effect.USE, G);
        _indexOperand.setEirValue(index);
    }

    public EirOperand pointerOperand() {
        return sourceOperand();
    }

    public AMD64EirRegister.General pointerGeneralRegister() {
        return (AMD64EirRegister.General) pointerOperand().location();
    }

    public AMD64EirRegister.General offsetGeneralRegister() {
        return (AMD64EirRegister.General) offsetOperand().location();
    }

    @Override
    public void visitOperands(EirOperand.Procedure visitor) {
        super.visitOperands(visitor);
        if (_offsetOperand != null) {
            visitor.run(_offsetOperand);
        }
        if (_indexOperand != null) {
            visitor.run(_indexOperand);
        }
    }

    public String addressString() {
        if (indexOperand() == null) {
            if (offsetOperand() == null) {
                return "[" + pointerOperand() + "]";
            }
            return "[" + pointerOperand() + " + " + offsetOperand() + "]";
        }
        if (offsetOperand() == null) {
            return pointerOperand() + "[" + indexOperand() + " * " + kind().size() + "]";
        }
        return pointerOperand() + "[" + indexOperand() + " * " + kind().size() + " + " + offsetOperand() + "]";
    }

    protected abstract void translateWithoutOffsetWithoutIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister);
    protected abstract void translateWithoutOffsetWithIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, AMD64GeneralRegister64 indexRegister);
    protected abstract void translateWithRegisterOffsetWithoutIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, AMD64GeneralRegister64 offsetRegister);
    protected abstract void translateWithRegisterOffsetWithIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, AMD64GeneralRegister64 offsetRegister, AMD64GeneralRegister64 indexRegister);
    protected abstract void translateWithImmediateOffset8WithoutIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, byte offset8);
    protected abstract void translateWithImmediateOffset8WithIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, byte offset8, AMD64GeneralRegister64 indexRegister);
    protected abstract void translateWithImmediateOffset32WithoutIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, int offset32);
    protected abstract void translateWithImmediateOffset32WithIndex(AMD64EirTargetEmitter emitter, AMD64GeneralRegister64 pointerRegister, int offset32, AMD64GeneralRegister64 indexRegister);


    @Override
    public void emit(AMD64EirTargetEmitter emitter) {
        final AMD64GeneralRegister64 pointerRegister = pointerGeneralRegister().as64();
        if (offsetOperand() == null) {
            if (_indexOperand == null) {
                translateWithoutOffsetWithoutIndex(emitter, pointerRegister);
            } else {
                final AMD64GeneralRegister64 indexRegister = indexGeneralRegister().as64();
                translateWithoutOffsetWithIndex(emitter, pointerRegister, indexRegister);
            }
            return;
        }
        switch (offsetOperand().location().category()) {
            case INTEGER_REGISTER: {
                final AMD64GeneralRegister64 offsetRegister = offsetGeneralRegister().as64();
                if (_indexOperand == null) {
                    translateWithRegisterOffsetWithoutIndex(emitter, pointerRegister, offsetRegister);
                } else {
                    final AMD64GeneralRegister64 indexRegister = indexGeneralRegister().as64();
                    translateWithRegisterOffsetWithIndex(emitter, pointerRegister, offsetRegister, indexRegister);
                }
                break;
            }
            case IMMEDIATE_8:
            case IMMEDIATE_32: {
                final Value immediateOffsetValue = offsetOperand().location().asImmediate().value();
                final WordWidth offsetWidth = immediateOffsetValue.signedEffectiveWidth();
                switch (offsetWidth) {
                    case BITS_8: {
                        final byte offset8 = immediateOffsetValue.toByte();
                        if (_indexOperand == null) {
                            translateWithImmediateOffset8WithoutIndex(emitter, pointerRegister, offset8);
                        } else {
                            final AMD64GeneralRegister64 indexRegister = indexGeneralRegister().as64();
                            translateWithImmediateOffset8WithIndex(emitter, pointerRegister, offset8, indexRegister);
                        }
                        break;
                    }
                    case BITS_16:
                    case BITS_32: {
                        final int offset32 = immediateOffsetValue.toInt();
                        if (_indexOperand == null) {
                            translateWithImmediateOffset32WithoutIndex(emitter, pointerRegister, offset32);
                        } else {
                            final AMD64GeneralRegister64 indexRegister = indexGeneralRegister().as64();
                            translateWithImmediateOffset32WithIndex(emitter, pointerRegister, offset32, indexRegister);
                        }
                        break;
                    }
                    case BITS_64: {
                        impossibleImmediateWidth();
                        break;
                    }
                }
                break;
            }
            default: {
                impossibleLocationCategory();
                break;
            }
        }
    }
}
