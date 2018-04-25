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
public interface SoemPluginListener extends EventListener {

    /**
     *
     * @param slave
     */
    public void errorSafeOpErrorSoemEcatCheck(int slave);

    /**
     *
     * @param slave
     */
    public void errorLostSoemEcatCheck(int slave);

    /**
     *
     * @param slave
     */
    public void warningSafeOpSoemEcatCheck(int slave);

    /**
     *
     * @param slave
     */
    public void messageReconfiguredSoemEcatCheck(int slave);

    /**
     *
     * @param slave
     */
    public void messageRecoveredSoemEcatCheck(int slave);

    /**
     *
     * @param slave
     */
    public void messageFoundSoemEcatCheck(int slave);

    /**
     *
     */
    public void messageAllSlavesResumedOperationalSoemEcatCheck();
}
