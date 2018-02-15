/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import ken.mizoguch.webviewer.plugin.*;

/**
 *
 * @author mizoguch-ken
 */
public interface GcodeFXWebViewerPlugin extends WebViewerPlugin {

    // plugin -> webviewer
    public StageSettingsPlugin stageSettings();

    public BaseSettingsPlugin baseSettings();

    public VirtualMachineSettingsPlugin virtualMachineSettings();

    public WebEditorPlugin webEditor();

    public GcodePlugin gcode();

    public LaddersPlugin ladders();

    public SoemPlugin soem();
}
