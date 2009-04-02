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
package com.sun.max.ins.debug;

import com.sun.max.ins.*;
import com.sun.max.ins.InspectionSettings.*;
import com.sun.max.ins.gui.*;
import com.sun.max.ins.gui.TableColumnVisibilityPreferences.*;
import com.sun.max.program.*;
import com.sun.max.tele.*;
import com.sun.max.tele.debug.*;


/**
 * A singleton inspector that displays register contents for the thread the {@link TeleVM} that is the current user focus.
 *
 * @author Michael Van De Vanter
 */
public final class RegistersInspector extends Inspector implements TableColumnViewPreferenceListener {


    // Set to null when inspector closed.
    private static RegistersInspector _registersInspector;

    /**
     * Displays the (singleton) registers  inspector, creating it if needed.
     */
    public static RegistersInspector make(Inspection inspection) {
        if (_registersInspector == null) {
            _registersInspector = new RegistersInspector(inspection);
        }
        return _registersInspector;
    }

    private final SaveSettingsListener _saveSettingsListener = createBasicSettingsClient(this, "registersInspector");

    // This is a singleton viewer, so only use a single level of view preferences.
    private final RegistersViewPreferences _viewPreferences;

    private TeleNativeThread _teleNativeThread;
    private RegistersTable _table;

    private RegistersInspector(Inspection inspection) {
        super(inspection);
        Trace.begin(1,  tracePrefix() + " initializing");
        _viewPreferences = RegistersViewPreferences.globalPreferences(inspection());
        _viewPreferences.addListener(this);
        createFrame(null);
        refreshView(inspection.teleVM().epoch(), true);
        if (!inspection.settings().hasComponentLocation(_saveSettingsListener)) {
            frame().setLocation(inspection().geometry().registersFrameDefaultLocation());
            frame().getContentPane().setPreferredSize(inspection().geometry().registersFramePrefSize());
        }
        Trace.end(1,  tracePrefix() + " initializing");
    }

    @Override
    protected void createView(long epoch) {
        _teleNativeThread = inspection().focus().thread();
        if (_teleNativeThread == null) {
            _table = null;
            frame().setTitle(getTextForTitle());
        } else {
            _table = new RegistersTable(inspection(), _teleNativeThread, _viewPreferences);
            frame().setTitle(getTextForTitle() + " " + inspection().nameDisplay().longName(_teleNativeThread));
        }
        frame().setContentPane(new InspectorScrollPane(inspection(), _table));
    }

    @Override
    public SaveSettingsListener saveSettingsListener() {
        return _saveSettingsListener;
    }

    @Override
    public String getTextForTitle() {
        return "Registers: ";
    }

    @Override
    public InspectorAction getViewOptionsAction() {
        return new InspectorAction(inspection(), "View Options") {
            @Override
            public void procedure() {
                new TableColumnVisibilityPreferences.Dialog<RegistersColumnKind>(inspection(), "Registers View Options", _viewPreferences);
            }
        };
    }

    @Override
    public void refreshView(long epoch, boolean force) {
        _table.refresh(epoch, force);
        super.refreshView(epoch, force);
    }

    @Override
    public void threadFocusSet(TeleNativeThread oldTeleNativeThread, TeleNativeThread teleNativeThread) {
        reconstructView();
    }

    @Override
    public void tableColumnViewPreferencesChanged() {
        reconstructView();
    }

    @Override
    public void viewConfigurationChanged(long epoch) {
        reconstructView();
    }

    @Override
    public void inspectorClosing() {
        Trace.line(1, tracePrefix() + " closing");
        _registersInspector = null;
        _viewPreferences.removeListener(this);
        super.inspectorClosing();
    }

}
