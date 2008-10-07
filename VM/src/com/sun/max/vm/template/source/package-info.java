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
/*VCSID=a7bc5279-8c0f-4fbf-8eaa-a0e8bceecc04*/
/**
 * Package containing the <i>sources</i> for the template themselves. Templates are generated by compiling these <i>sources</i>
 * with a version of the optimizing compiler that generate target code in a format suitable for template-based code generation (in particular,
 * the target code is annotated with class constant dependencies and assembly instruction editors so that a template-based code generator can
 * modify a template according to its needs).
 * 
 * @author Laurent Daynes
 */
package com.sun.max.vm.template.source;
