/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 *
 * @author mizoguch-ken
 */
public interface SoemPlugin {

    public Boolean load(Path libraryPath);

    public Boolean init(int ioSize);

    public Boolean po2so(int slave, long eep_man, long eep_id, Callable<Integer> func);

    public Boolean start(String ifname, long cycletime, SoemPluginListener listener);

    public Boolean close(SoemPluginListener listener);

    public Boolean setNotifyCheck(boolean state);

    public Integer in(int slave, long bitsOffset, int bitsMask);

    public Long notify(int slave, long bitsOffset, int bitsMask, boolean register);

    public Integer out(int slave, long bitsOffset, int bitsMask, int value);

    public String find_adapters();

    public String slaveinfo(String ifname, boolean printSDO, boolean printMAP);
}
