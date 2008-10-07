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
/*VCSID=8407bca6-a7d0-496a-997f-30691079448e*/
package com.sun.max.vm.tele;

import com.sun.max.annotate.*;
import com.sun.max.lang.*;
import com.sun.max.memory.*;

/**
 * Remote support regarding the object heap.
 *
 * @author Bernd Mathiske
 */
public final class TeleHeap {

    private TeleHeap() {
    }

    @INSPECTED
    private static MemoryRegion[] _memoryRegions;

    public static void registerMemoryRegions(MemoryRegion... memoryRegions) {
        if (MaxineMessenger.isVmInspected()) {
            if (_roots == null) {
                _roots = new Object[MAX_NUMBER_OF_ROOTS];
            }
            final MemoryRegion[] result = new MemoryRegion[memoryRegions.length];
            for (int i = 0; i < memoryRegions.length; i++) {
                result[i] = new FixedMemoryRegion(memoryRegions[i]);
            }
            _memoryRegions = result;
        }
    }

    public static final int MAX_NUMBER_OF_ROOTS = Ints.M / 8;

    @INSPECTED
    private static Object[] _roots = new Object[MAX_NUMBER_OF_ROOTS];

    @INSPECTED
    private static long _rootEpoch;

    @INSPECTED
    private static long _collectionEpoch;

    public static void beforeGarbageCollection() {
        _collectionEpoch++;
    }

    public static void afterGarbageCollection() {
        _rootEpoch = _collectionEpoch;
    }
}
