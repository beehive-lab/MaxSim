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
/*VCSID=d725708d-f679-4023-a578-460de18e0bfa*/
package com.sun.max.asm.dis.amd64;

import java.util.*;

import com.sun.max.asm.*;
import com.sun.max.asm.amd64.complete.*;
import com.sun.max.asm.dis.x86.*;
import com.sun.max.asm.gen.*;
import com.sun.max.asm.gen.cisc.amd64.*;
import com.sun.max.asm.gen.cisc.x86.*;
import com.sun.max.collect.*;
import com.sun.max.lang.*;
import com.sun.max.util.*;

/**
 * Instantiate this class to disassemble AMD64 instruction streams.
 *
 * @author Bernd Mathiske
 */
public class AMD64Disassembler extends X86Disassembler<AMD64Template, AMD64DisassembledInstruction> {

    private final long _startAddress;

    public AMD64Disassembler(long startAddress) {
        super(AMD64Assembly.ASSEMBLY, WordWidth.BITS_64);
        _startAddress = startAddress;
        _instructionScanners.add(new AMD64SwitchDisassembler(this));
    }

    @Override
    public Class<AMD64DisassembledInstruction> disassembledInstructionType() {
        return AMD64DisassembledInstruction.class;
    }

    @Override
    protected boolean isRexPrefix(HexByte opcode) {
        return X86Opcode.isRexPrefix(opcode);
    }

    @Override
    protected AMD64DisassembledInstruction createDisassembledInstruction(int position, byte[] bytes, AMD64Template template, IndexedSequence<Argument> arguments) {
        return new AMD64DisassembledInstruction(_startAddress, position, bytes, template, arguments);
    }

    @Override
    protected Assembler createAssembler(int position) {
        return new AMD64Assembler(_startAddress + position);
    }

    private static Map<X86InstructionHeader, AppendableSequence<AMD64Template>> _headerToTemplates = X86InstructionHeader.createMapping(AMD64Assembly.ASSEMBLY, WordWidth.BITS_64);

    @Override
    protected Map<X86InstructionHeader, AppendableSequence<AMD64Template>> headerToTemplates() {
        return _headerToTemplates;
    }

    @Override
    protected AMD64DisassembledInstruction createDisassembledInlineBytesInstruction(int position, byte[] bytes) {
        final AppendableIndexedSequence<Argument> arguments = new ArrayListSequence<Argument>();
        for (byte b : bytes) {
            arguments.append(new Immediate8Argument(b));
        }
        return new AMD64DisassembledInstruction(_startAddress, position, bytes, AMD64Assembly.ASSEMBLY.inlineByteTemplate(), arguments);
    }
}
