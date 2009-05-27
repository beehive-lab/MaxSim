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
package com.sun.max;

import java.lang.reflect.*;
import java.util.*;

import com.sun.max.collect.*;
import com.sun.max.lang.*;
import com.sun.max.program.*;

/**
 * Describes a package in the com.sun.max project,
 * providing programmatic package information manipulation,
 * which is lacking in java.lang.Package.
 * <p>
 * You must create a class called 'Package extends MaxPackage'
 * in each and every package in this project.
 * <p>
 * For example you can call:
 *
 *     new com.sun.max.program.Package().superPackage()
 *
 * Also make sure that you have a file called 'package-info.java' in every package.
 * This is where you can put package-related JavaDoc comments.
 *
 * @author Bernd Mathiske
 * @author Doug Simon
 */
public abstract class MaxPackage implements Comparable<MaxPackage> {

    private final String _packageName;

    protected MaxPackage() {
        _packageName = toJava().getName();
        assert getClass().getSimpleName().equals(Package.class.getSimpleName());
    }

    /**
     * The package prefix of all classes that are part of the Maxine code base.
     */
    public static final String MAX_CLASS_PACKAGE_PREFIX = new Package().name();

    /**
     * Determines if a given class is part of the Maxine code base.
     *
     * @param javaClass the class to test
     * @return true if {@code javaClass.getName()} starts with {@link #MAX_CLASS_PACKAGE_PREFIX}
     */
    public static boolean isMaxClass(Class javaClass) {
        return javaClass.getName().startsWith(MAX_CLASS_PACKAGE_PREFIX);
    }

    /**
     * Gets an instance of the class named "Package" in a named package.
     *
     * @param packageName denotes the name of a package which may contain a subclass of {@link MaxPackage} named
     *            "Package"
     * @return an instance of the class name "Package" in {@code packageName}. If such a class does not exist or there
     *         was an error {@linkplain Class#newInstance() instantiating} it, then {@code null} is returned
     */
    public static MaxPackage fromName(String packageName) {
        final String name = packageName + "." + Package.class.getSimpleName();
        if (name.equals(java.lang.Package.class.getName())) {
            // Special case!
            return null;
        }
        try {
            final Class packageClass = Class.forName(name);
            return (MaxPackage) packageClass.newInstance();
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MaxPackage(String packageName) {
        _packageName = packageName;
    }

    public static MaxPackage fromJava(String name) {
        return new MaxPackage(name) {
            @Override
            public java.lang.Package toJava() {
                return java.lang.Package.getPackage(name());
            }
        };
    }

    public static MaxPackage fromClass(Class javaClass) {
        final java.lang.Package javaPackage = javaClass.getPackage();
        if (javaPackage == null) {
            return null;
        }
        return fromName(javaPackage.getName());
    }

    public java.lang.Package toJava() {
        return getClass().getPackage();
    }

    public String name() {
        return _packageName;
    }

    public String lastIdentifier() {
        return _packageName.substring(_packageName.lastIndexOf('.') + 1);
    }

    public MaxPackage superPackage() {
        final int end = name().lastIndexOf('.');
        if (end < 0) {
            return null;
        }
        return fromName(name().substring(0, end));
    }

    public MaxPackage subPackage(String... suffices) {
        String name = name();
        for (String suffix : suffices) {
            name += "." + suffix;
        }
        final MaxPackage subPackage = fromName(name);
        ProgramError.check(subPackage != null, "Could not find sub-package of " + this + " named " + name);
        return subPackage;
    }

    public boolean isSubPackageOf(MaxPackage superPackage) {
        return name().startsWith(superPackage.name());
    }

    public Sequence<MaxPackage> getTransitiveSubPackages(Classpath classpath) {
        final Set<String> packageNames = new TreeSet<String>();
        new ClassSearch() {
            @Override
            protected boolean visitClass(String className) {
                final String packageName = Classes.getPackageName(className);
                if (packageName.startsWith(name())) {
                    packageNames.add(packageName);
                }
                return true;
            }
        }.run(classpath, name().replace('.', '/'));


        final AppendableSequence<MaxPackage> packages = new ArrayListSequence<MaxPackage>(packageNames.size());
        for (String packageName : packageNames) {
            final MaxPackage maxPackage = MaxPackage.fromName(packageName);
            if (maxPackage == null) {
                System.err.println("WARNING: missing Package class in package: " + packageName);
            } else {
                packages.append(maxPackage);
            }
        }
        return packages;
    }

    private Map<Class<? extends Scheme>, Class<? extends Scheme>> _schemeTypeToImplementation;

    public synchronized <Scheme_Type extends Scheme> void registerScheme(Class<Scheme_Type> schemeType, Class<? extends Scheme_Type> schemeImplementation) {
        assert schemeType.isInterface() || Modifier.isAbstract(schemeType.getModifiers());
        assert schemeImplementation.getPackage().getName().equals(name());
        if (_schemeTypeToImplementation == null) {
            _schemeTypeToImplementation = new IdentityHashMap<Class<? extends Scheme>, Class<? extends Scheme>>();
        }
        _schemeTypeToImplementation.put(schemeType, schemeImplementation);
    }

    /**
     * Gets the class within this package implementing a given scheme type (represented as an as abstract class or interface).
     *
     * @return the class directly within this package that implements {@code scheme} or null if no such class
     *         exists
     */
    public synchronized <Scheme_Type extends Scheme> Class<? extends Scheme_Type> schemeTypeToImplementation(Class<Scheme_Type> schemeType) {
        if (_schemeTypeToImplementation == null) {
            return null;
        }
        final Class< ? extends Scheme> implementation = _schemeTypeToImplementation.get(schemeType);
        if (implementation == null) {
            return null;
        }
        return implementation.asSubclass(schemeType);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MaxPackage) {
            return _packageName.equals(((MaxPackage) other)._packageName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    public Set<MaxPackage> prerequisites() {
        return Sets.empty(MaxPackage.class);
    }

    /**
     * Gets the set of packages excluded by this package. Excluded packages will not be loaded into a system that is
     * configured by package loading (such as the Maxine VM) if the package represented by this object is loaded. Such a
     * system should ensure that configuration fails if any excluded packages encountered on the class path before the
     * package that excludes them.
     */
    public Set<MaxPackage> excludes() {
        return Sets.empty(MaxPackage.class);
    }

    public int compareTo(MaxPackage other) {
        final Set<MaxPackage> myPrerequisites = prerequisites();
        final Set<MaxPackage> otherPrerequisites = other.prerequisites();
        if (myPrerequisites.isEmpty()) {
            if (otherPrerequisites.isEmpty()) {
                return _packageName.compareTo(other._packageName);
            }
            return -1;
        }
        for (MaxPackage myPrerequisite : myPrerequisites) {
            if (other.equals(myPrerequisite)) {
                return 1;
            }
        }
        if (otherPrerequisites.isEmpty()) {
            return 1;
        }
        for (MaxPackage otherPrerequisite : otherPrerequisites) {
            if (equals(otherPrerequisite)) {
                return -1;
            }
        }
        return _packageName.compareTo(other._packageName);
    }

    private synchronized <Scheme_Type extends Scheme> Class<? extends Scheme_Type> loadSchemeImplementation(Class<Scheme_Type> schemeType) {
        final Class<? extends Scheme_Type> schemeImplementation = schemeTypeToImplementation(schemeType);
        if (schemeImplementation == null) {
            ProgramError.unexpected("could not find subclass of " + schemeType + " in " + this);
        } else {
            final Class<?> loadedImplementation = Classes.load(schemeType.getClassLoader(), schemeImplementation.getName());
            return loadedImplementation.asSubclass(schemeType);
        }
        return null;
    }

    /**
     * Instantiates the scheme implementation class in this package implementing a given scheme type.
     *
     * @param schemeType the interface or abstract class defining a scheme type
     * @param arguments arguments passed to constructor of the scheme implementation class
     * @return a new instance of the scheme implementation class
     */
    public synchronized <Scheme_Type extends Scheme> Scheme_Type loadAndInstantiateScheme(Class<Scheme_Type> schemeType, Object... arguments) {
        final Class<? extends Scheme_Type> schemeImplementation = loadSchemeImplementation(schemeType);
        try {
            final Class[] argumentTypes = new Class[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                argumentTypes[i] = arguments[i].getClass();
            }
            final Constructor constructor = Classes.findConstructor(schemeImplementation, arguments);
            if (constructor != null) {
                return schemeImplementation.cast(constructor.newInstance(arguments));
            }
            throw ProgramError.unexpected("could not find matching constructor of class: " + schemeImplementation.getName());
        } catch (Throwable throwable) {
            throw ProgramError.unexpected("could not instantiate class: " + schemeImplementation.getName(), throwable);
        }
    }

    @Override
    public String toString() {
        return name();
    }
}
