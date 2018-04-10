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
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPluginListener;

/**
 *
 * @author mizoguch-ken
 */
public class SoemEcatCheck extends Service<Void> {

    private final EventListenerList eventListenerList_;

    private final SoemLibrary soem_;

    private final SoemEtherCAT.ecx_parcelt parcel_;
    private final SoemEtherCATMain.ecx_contextt context_;
    private int expectedWKC_;
    private boolean isNotifyCheck_;
    private boolean exit_;

    private static final int EC_TIMEOUTMON = 500;

    /**
     *
     * @param eventListenerList
     * @param soem
     * @param parcel
     * @param context
     */
    public SoemEcatCheck(EventListenerList eventListenerList, SoemLibrary soem, SoemEtherCAT.ecx_parcelt parcel, SoemEtherCATMain.ecx_contextt context) {
        eventListenerList_ = eventListenerList;
        soem_ = soem;
        parcel_ = parcel;
        context_ = context;
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
                    int slave, docheckstate;

                    while (!exit_) {
                        docheckstate = context_.grouplist[0].docheckstate.get();
                        if ((parcel_.wkc.get() < expectedWKC_) || (docheckstate == SoemOsal.TRUE)) {
                            context_.grouplist[0].docheckstate.set(SoemOsal.FALSE);
                            soem_.ecx_readstate(context_);
                            for (slave = 1; slave <= context_.slavecount.get(); slave++) {
                                if ((context_.slavelist[slave].group.get() == 0) && (context_.slavelist[slave].state.get() != SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue())) {
                                    context_.grouplist[0].docheckstate.set(SoemOsal.TRUE);
                                    if (context_.slavelist[slave].state.get() == (SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue() + SoemEtherCATType.ec_state.EC_STATE_ERROR.intValue())) {
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.errorSafeOpErrorSoemEcatCheck(slave);
                                            }
                                        }
                                        context_.slavelist[slave].state.set(SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue() + SoemEtherCATType.ec_state.EC_STATE_ACK.intValue());
                                        soem_.ecx_writestate(context_, slave);
                                    } else if (context_.slavelist[slave].state.get() == SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue()) {
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.warningSafeOpSoemEcatCheck(slave);
                                            }
                                        }
                                        context_.slavelist[slave].state.set(SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue());
                                        soem_.ecx_writestate(context_, slave);
                                    } else if (context_.slavelist[slave].state.get() > SoemEtherCATType.ec_state.EC_STATE_NONE.intValue()) {
                                        if (soem_.ecx_reconfig_slave(context_, slave, EC_TIMEOUTMON) == SoemOsal.TRUE) {
                                            context_.slavelist[slave].islost.set(SoemOsal.FALSE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.messageReconfiguredSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    } else if (context_.slavelist[slave].islost.get() == SoemOsal.FALSE) {
                                        soem_.ecx_statecheck(context_, slave, SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue(), SoemEtherCATType.EC_TIMEOUTRET);
                                        if (context_.slavelist[slave].state.get() == SoemEtherCATType.ec_state.EC_STATE_NONE.intValue()) {
                                            context_.slavelist[slave].islost.set(SoemOsal.TRUE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.errorLostSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (context_.slavelist[slave].islost.get() == SoemOsal.TRUE) {
                                    if (context_.slavelist[slave].state.get() == SoemEtherCATType.ec_state.EC_STATE_NONE.intValue()) {
                                        if (soem_.ecx_recover_slave(context_, slave, EC_TIMEOUTMON) == SoemOsal.TRUE) {
                                            context_.slavelist[slave].islost.set(SoemOsal.FALSE);
                                            if (isNotifyCheck_) {
                                                for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                    listener.messageRecoveredSoemEcatCheck(slave);
                                                }
                                            }
                                        }
                                    } else {
                                        context_.slavelist[slave].islost.set(SoemOsal.FALSE);
                                        if (isNotifyCheck_) {
                                            for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                                listener.messageFoundSoemEcatCheck(slave);
                                            }
                                        }
                                    }
                                }
                            }
                            if (isNotifyCheck_) {
                                if ((docheckstate == SoemOsal.TRUE) && (context_.grouplist[0].docheckstate.get() == SoemOsal.FALSE)) {
                                    for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                        listener.messageAllSlavesResumedOperationalSoemEcatCheck();
                                    }
                                }
                            }
                        }
                        try {
                            TimeUnit.MICROSECONDS.sleep(10000);
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
