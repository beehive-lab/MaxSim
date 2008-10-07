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
/*VCSID=8eb2ea56-2e65-4ba1-9752-944f5a5a98d3*/
package test.com.sun.max.vm.jit;

import test.com.sun.max.vm.compiler.*;

import com.sun.max.vm.template.source.*;
import com.sun.max.vm.type.*;


/**
 * Testing the JIT-compiler with methods performing static field access to initialized and uninitialized class.
 * This suite of tests exercises the part of the template-based JIT that (a) selects an appropriate template
 * based on the initialization state of the refered class (unresolved, loaded, initialized), and customize
 * the template appropriately (i.e., replace ResolutionGuard of the template with appropriate ResolutionGuard,
 * replace reference literals to static tuple with static tuple obtained from the constant pool of the
 * compiled method, modified field offsets with appropriate offset values, etc.).
 *
 * @author Laurent Daynes
 */
public class JITTest_compileMethodWithStaticFieldAccess extends JitCompilerTestCase {
    static byte _staticByteField;
    static boolean _staticBooleanField;
    static char _staticCharField;
    static short _staticShortField;
    static int _staticIntField;
    static float _staticFloatField;
    static long _staticLongField;
    static double _staticDoubleField;
    static JITTest_compileMethodWithStaticFieldAccess _staticObjectField;

    String methodNameFor(String typeName, boolean resolved) {
        return "use" + (resolved ? "Resolved" : "Unresolved") + "Static" + typeName + "Field";
    }

    String methodNameForKind(Kind kind, boolean resolved) {
        final String kindName = kind.name().toString();
        return  methodNameFor(kindName.substring(0, 1).toUpperCase() +  kindName.substring(1).toLowerCase(), resolved);
    }

    /////////////////////////////////////////////// RESOLVED & INITIALIZED CLASS
    void useResolvedStaticByteField() {
        _staticByteField++;
    }

    void useResolvedStaticBooleanField() {
        _staticBooleanField ^= true;
    }

    void useResolvedStaticShortField() {
        _staticShortField++;
    }

    void useResolvedStaticIntField() {
        _staticIntField++;
    }

    void useResolvedStaticLongField() {
        _staticLongField++;
    }

    void useResolvedStaticFloatField() {
        _staticFloatField *= 0.15F;
    }

    void useResolvedStaticDoubleField() {
        _staticDoubleField *= 0.125D;
    }

    @SuppressWarnings("unused")
    void useResolvedStaticObjectField() {
        final Object o = _staticObjectField;
        _staticObjectField =  null;
    }

    public void test_useResolvedStaticByteField() {
        compileMethod(methodNameForKind(Kind.BYTE, true));
    }

    public void test_useResolvedStaticBooleanField() {
        compileMethod(methodNameForKind(Kind.BOOLEAN, true));
    }

    public void test_useResolvedStaticShortField() {
        compileMethod(methodNameForKind(Kind.SHORT, true));
    }

    public void test_useResolvedStaticIntField() {
        compileMethod(methodNameForKind(Kind.INT, true));
    }

    public void test_useResolvedStaticLongField() {
        compileMethod(methodNameForKind(Kind.LONG, true));
    }

    public void test_useResolvedStaticFloatField() {
        compileMethod(methodNameForKind(Kind.FLOAT, true));
    }

    public void test_useResolvedStaticDoubleField() {
        compileMethod(methodNameForKind(Kind.DOUBLE, true));
    }

    public void test_useResolvedStaticObjectField() {
        compileMethod(methodNameFor("Object", true));
    }

    /////////////////////////////////////////////// UNRESOLVED & UNINITIALIZED CLASS
    void useUnresolvedStaticByteField()  {
        UnresolvedAtTestTime._staticByteField++;
    }

    void useUnresolvedStaticBooleanField() {
        UnresolvedAtTestTime._staticBooleanField ^= true;
    }

    void useUnresolvedStaticShortField()  {
        UnresolvedAtTestTime._staticShortField++;
    }

    void useUnresolvedStaticIntField()  {
        UnresolvedAtTestTime._staticIntField++;
    }

    void useUnresolvedStaticLongField() {
        UnresolvedAtTestTime._staticLongField++;
    }

    void useUnresolvedStaticFloatField() {
        UnresolvedAtTestTime._staticFloatField *= 0.15F;
    }

    void useUnresolvedStaticDoubleField() {
        UnresolvedAtTestTime._staticDoubleField *= 0.125D;
    }

    @SuppressWarnings("unused")
    void useUnresolvedStaticObjectField() {
        final Object o = UnresolvedAtTestTime._staticObjectField;
        UnresolvedAtTestTime._staticObjectField =  null;
    }

    public void test_useUnresolvedStaticByteField() {
        compileMethod(methodNameForKind(Kind.BYTE, false));
    }

    public void test_useUnresolvedStaticBooleanField() {
        compileMethod(methodNameForKind(Kind.BOOLEAN, false));
    }

    public void test_useUnresolvedStaticShortField() {
        compileMethod(methodNameForKind(Kind.SHORT, false));
    }

    public void test_useUnresolvedStaticIntField() {
        compileMethod(methodNameForKind(Kind.INT, false));
    }

    public void test_useUnresolvedStaticLongField() {
        compileMethod(methodNameForKind(Kind.LONG, false));
    }

    public void test_useUnresolvedStaticFloatField() {
        compileMethod(methodNameForKind(Kind.FLOAT, false));
    }

    public void test_useUnresolvedStaticDoubleField() {
        compileMethod(methodNameForKind(Kind.DOUBLE, false));
    }

    public void test_useUnresolvedStaticObjectField() {
        compileMethod(methodNameFor("Object", false));
    }

    /**
     * Testing with unresolved, resolved, and initialized class constant.
     */
    @Override
    protected Class[] templateSources() {
        return new Class[]{UnoptimizedBytecodeTemplateSource.class, ResolvedFieldAccessTemplateSource.class, InitializedStaticFieldAccessTemplateSource.class, InstrumentedBytecodeSource.class};
    }

}
