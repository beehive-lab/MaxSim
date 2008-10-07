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
/*VCSID=c037286b-db1e-43c6-a344-514646e6010b*/
package test.com.sun.max.vm.compiler.cir.optimize;

import junit.framework.*;
import test.com.sun.max.vm.compiler.*;

/**
 * Test whether the internal Snippets get translated at all.
 * This test is subsumed by each of the other translator tests.
 * 
 * @author Bernd Mathiske
 */
public class CirOptimizerTest_snippets extends CompilerTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CirOptimizerTest_snippets.suite());
    }

    public static Test suite() {
        final TestSuite suite = new TestSuite(CirOptimizerTest_snippets.class.getSimpleName());
        //$JUnit-BEGIN$
        suite.addTestSuite(CirOptimizerTest_snippets.class);
        //$JUnit-END$
        return new CirOptimizerTestSetup(suite); // This performs the test
    }

    public CirOptimizerTest_snippets(String name) {
        super(name);
    }

    public void test() {
        // new CirOptimizerTestSetup() performs: Snippet.translateAll() and Snippet.optimizeAll()
    }

}
