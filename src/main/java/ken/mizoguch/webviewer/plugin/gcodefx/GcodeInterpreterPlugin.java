/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

/**
 *
 * @author mizoguch-ken
 */
public interface GcodeInterpreterPlugin {

    public void addGcodeInterpreterListener(GcodeInterpreterPluginListener listener);

    public void removeGcodeInterpreterListener(GcodeInterpreterPluginListener listener);
}
