/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.util.EventListener;

/**
 *
 * @author mizoguch-ken
 */
public interface GcodeVirtualMachinePluginListener extends EventListener {

    public void startGcodeVirtualMachine();

    public void abnormalEndGcodeVirtualMachine();

    public void errorEndGcodeVirtualMachine();

    public void normalEndGcodeVirtualMachine();
}
