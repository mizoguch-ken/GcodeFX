/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import java.util.concurrent.TimeUnit;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.swing.event.EventListenerList;
import ken.mizoguch.console.Console;
import static ken.mizoguch.soem.SoemEthercatType.EC_TIMEOUTRET;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_ACK;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_ERROR;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_NONE;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_OPERATIONAL;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_SAFE_OP;
import static ken.mizoguch.soem.SoemOsal.FALSE;
import static ken.mizoguch.soem.SoemOsal.TRUE;
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPluginListener;

/**
 *
 * @author mizoguch-ken
 */
public class SoemEcatCheck extends Service<Void> {

    private final EventListenerList eventListenerList_;

    private final SoemLibrary soem_;

    private final SoemEthercatMain.ecx_contextt context_;
    private int wkc_;
    private int expectedWKC_;
    private boolean isNotifyCheck_;
    private boolean exit_;

    private static final int EC_TIMEOUTMON = 500;

    /**
     *
     * @param eventListenerList
     * @param soem
     * @param context
     */
    public SoemEcatCheck(EventListenerList eventListenerList, SoemLibrary soem, SoemEthercatMain.ecx_contextt context) {
        eventListenerList_ = eventListenerList;
        soem_ = soem;
        context_ = context;
        wkc_ = 0;
        expectedWKC_ = 0;
        isNotifyCheck_ = false;
        exit_ = true;
    }

    /**
     *
     */
    public void init() {
        exit_ = false;
    }

    /**
     *
     */
    public void exit() {
        exit_ = true;
    }

    /**
     *
     * @param wkc
     */
    public void setWkc(int wkc) {
        wkc_ = wkc;
    }

    /**
     *
     * @param expectedWKC
     */
    public void setExpectedWKC(int expectedWKC) {
        expectedWKC_ = expectedWKC;
    }

    /**
     *
     * @param bln
     * @return
     */
    public boolean setNotifyCheck(boolean bln) {
        isNotifyCheck_ = bln;
        return isNotifyCheck_;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    int slave;

                    while (!exit_) {
                        if ((wkc_ < expectedWKC_) || (context_.grouplist[0].docheckstate.get() > 0)) {
                            context_.grouplist[0].docheckstate.set(FALSE);
                            soem_.ecx_readstate(context_);
                            for (slave = 1; slave <= context_.slavecount.get(); slave++) {
                                if ((context_.slavelist[slave].group.get() == 0) && (context_.slavelist[slave].state.get() != EC_STATE_OPERATIONAL.intValue())) {
                                    context_.grouplist[0].docheckstate.set(TRUE);
                                    if (context_.slavelist[slave].state.get() == (EC_STATE_SAFE_OP.intValue() + EC_STATE_ERROR.intValue())) {
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.errorSafeOpErrorSoemEcatCheck(slave);
                                            }
                                        }
                                        context_.slavelist[slave].state.set(EC_STATE_SAFE_OP.intValue() + EC_STATE_ACK.intValue());
                                        soem_.ecx_writestate(context_, slave);
                                    } else if (context_.slavelist[slave].state.get() == EC_STATE_SAFE_OP.intValue()) {
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.warningSafeOpSoemEcatCheck(slave);
                                            }
                                        }
                                        context_.slavelist[slave].state.set(EC_STATE_OPERATIONAL.intValue());
                                        soem_.ecx_writestate(context_, slave);
                                    } else if (context_.slavelist[slave].state.get() > EC_STATE_NONE.intValue()) {
                                        if (soem_.ecx_reconfig_slave(context_, slave, EC_TIMEOUTMON) > 0) {
                                            context_.slavelist[slave].islost.set(FALSE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.messageReconfiguredSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    } else if (context_.slavelist[slave].islost.get() == FALSE) {
                                        soem_.ecx_statecheck(context_, slave, EC_STATE_OPERATIONAL.intValue(), EC_TIMEOUTRET);
                                        if (context_.slavelist[slave].state.get() == EC_STATE_NONE.intValue()) {
                                            context_.slavelist[slave].islost.set(TRUE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.errorLostSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (context_.slavelist[slave].islost.get() > 0) {
                                    if (context_.slavelist[slave].state.get() == EC_STATE_NONE.intValue()) {
                                        if (soem_.ecx_recover_slave(context_, slave, EC_TIMEOUTMON) > 0) {
                                            context_.slavelist[slave].islost.set(FALSE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.messageRecoveredSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    } else {
                                        context_.slavelist[slave].islost.set(FALSE);
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.messageFoundSoemEcatCheck(slave);
                                            }
                                        }
                                    }
                                }
                            }
                            if (isNotifyCheck_) {
                                if (context_.grouplist[0].docheckstate.get() == FALSE) {
                                    for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                        listener.messageAllSlavesResumedOperationalSoemEcatCheck();
                                    }
                                }
                            }
                        }
                        try {
                            TimeUnit.NANOSECONDS.sleep(10000);
                        } catch (InterruptedException ex) {
                        }
                    }
                } catch (Exception ex) {
                    Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
                }
                return null;
            }
        };
    }
}
