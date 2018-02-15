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
public interface GcodeVirtualMachinePlugin {

    public void addGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener);

    public void removeGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener);
}
