/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.swing.event.EventListenerList;
import jnr.ffi.Pointer;
import ken.mizoguch.console.Console;
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPluginListener;

/**
 *
 * @author mizoguch-ken
 */
public class SoemEcatThread extends Service<Void> {

    private final EventListenerList eventListenerList_;

    private class EcatPO2SO {

        private long eep_man;
        private long eep_id;
        private Callable<Integer> func;

        public EcatPO2SO(long eep_man, long eep_id, Callable<Integer> func) {
            this.eep_man = eep_man;
            this.eep_id = eep_id;
            this.func = func;
        }
    }

    private static class EcatData {

        private int bitMask;
        private int value;

        public EcatData(int bitMask, int value) {
            this.bitMask = bitMask;
            this.value = value;
        }
    }

    private final SoemLibrary soem_;
    private final Object lockOut_ = new Object();
    private final SoemEtherCAT.ecx_parcelt parcel_;
    private final SoemEtherCATMain.ecx_contextt context_;
    private final ConcurrentMap<Integer, EcatPO2SO> po2so_;
    private final ConcurrentMap<Long, EcatData> out_;
    private SoemEcatCheck ecatCheck_;
    private boolean init_, exit_;

    /**
     *
     * @param soem
     * @param parcel
     * @param context
     */
    public SoemEcatThread(SoemLibrary soem, SoemEtherCAT.ecx_parcelt parcel, SoemEtherCATMain.ecx_contextt context) {
        eventListenerList_ = new EventListenerList();
        soem_ = soem;
        parcel_ = parcel;
        context_ = context;
        po2so_ = new ConcurrentHashMap<>();
        out_ = new ConcurrentHashMap<>();
        ecatCheck_ = null;
        init_ = false;
        exit_ = true;
    }

    /**
     *
     * @param listener
     */
    public void addSoemEcatListener(SoemPluginListener listener) {
        boolean isListener = false;
        for (SoemPluginListener sel : eventListenerList_.getListeners(SoemPluginListener.class)) {
            if (sel == listener) {
                isListener = true;
                break;
            }
        }
        if (!isListener) {
            eventListenerList_.add(SoemPluginListener.class, listener);
        }
    }

    /**
     *
     * @param listener
     */
    public void removeSoemEcatListener(SoemPluginListener listener) {
        eventListenerList_.remove(SoemPluginListener.class, listener);
    }

    /**
     *
     * @param slave
     * @param eep_man
     * @param eep_id
     * @param func
     */
    public void po2so(int slave, long eep_man, long eep_id, Callable<Integer> func) {
        if (po2so_.containsKey(slave)) {
            po2so_.get(slave).eep_man = eep_man;
            po2so_.get(slave).eep_id = eep_id;
            po2so_.get(slave).func = func;
        } else {
            po2so_.put(slave, new EcatPO2SO(eep_man, eep_id, func));
        }
    }

    /**
     *
     * @param ifname
     * @param cycletime
     * @param pIOmap
     * @return
     */
    public boolean init(String ifname, long cycletime, Pointer pIOmap) {
        if (ecatCheck_ == null) {
            try {
                int index, size, chk;

                exit_ = false;
                if (soem_.ecx_init(context_, ifname) > 0) {
                    if (soem_.ecx_config_init(context_, SoemOsal.FALSE) > 0) {
                        if (context_.slavecount.get() > 0) {
                            for (index = 1, size = context_.slavecount.get(); index <= size; index++) {
                                if (po2so_.containsKey(index)) {
                                    if ((context_.slavelist[index].eep_man.get() == po2so_.get(index).eep_man) && (context_.slavelist[index].eep_id.get() == po2so_.get(index).eep_id)) {
                                        context_.slavelist[index].PO2SOconfig.PO2SOconfig = (int slave) -> {
                                            if (po2so_ != null) {
                                                if (po2so_.containsKey(slave)) {
                                                    RunnableFuture<Integer> future = new FutureTask<>(po2so_.get(slave).func);
                                                    if (Platform.isFxApplicationThread()) {
                                                        try {
                                                            return future.get();
                                                        } catch (InterruptedException | ExecutionException ex) {
                                                            Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
                                                        }
                                                    } else {
                                                        try {
                                                            Platform.runLater(future);
                                                            return future.get();
                                                        } catch (InterruptedException | ExecutionException ex) {
                                                            Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
                                                        }
                                                    }
                                                }
                                            }
                                            return 0;
                                        };
                                    } else {
                                        writeLog("PO2SO: EEP_MAN & EEP_ID of Slave[" + index + "] are different", true);
                                        return false;
                                    }
                                }
                            }
                        }
                        soem_.ecx_config_map_group(context_, pIOmap, 0);
                        soem_.ecx_configdc(context_);
                        soem_.ecx_statecheck(context_, 0, SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue(), SoemEtherCATType.EC_TIMEOUTSTATE * 4);
                        ecatCheck_ = new SoemEcatCheck(eventListenerList_, soem_, parcel_, context_);
                        ecatCheck_.setExpectedWKC((context_.grouplist[0].outputsWKC.get() * 2) + context_.grouplist[0].inputsWKC.get());
                        context_.slavelist[0].state.set(SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue());
                        soem_.ecx_send_processdata(context_);
                        soem_.ecx_receive_processdata(context_, SoemEtherCATType.EC_TIMEOUTRET);
                        soem_.ecx_writestate(context_, 0);
                        chk = 40;
                        do {
                            soem_.ecx_statecheck(context_, 0, SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue(), 50000);
                        } while ((chk-- > 0) && (context_.slavelist[0].state.get() != SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue()));
                        if (context_.slavelist[0].state.get() == SoemEtherCATType.ec_state.EC_STATE_OPERATIONAL.intValue()) {
                            ecatCheck_.init();
                            if (Platform.isFxApplicationThread()) {
                                runEcatCheck();
                            } else {
                                Platform.runLater(() -> {
                                    runEcatCheck();
                                });
                            }
                            init_ = true;
                            return true;
                        }
                    }
                }
            } catch (Exception ex) {
                Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
            }
        }
        return false;
    }

    private void runEcatCheck() {
        if (ecatCheck_.getState() == State.READY) {
            ecatCheck_.start();
        } else {
            ecatCheck_.restart();
        }
    }

    /**
     *
     */
    public void exit() {
        int chk;

        exit_ = true;
        if (ecatCheck_ != null) {
            ecatCheck_.exit();
            ecatCheck_.cancel();
            ecatCheck_ = null;
        }
        if (init_ && (soem_ != null) && (context_ != null)) {
            init_ = false;
            context_.slavelist[0].state.set(SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue());
            soem_.ecx_writestate(context_, 0);
            chk = 40;
            do {
                soem_.ecx_statecheck(context_, 0, SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue(), 50000);
            } while ((chk-- > 0) && (context_.slavelist[0].state.get() != SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue()));
        }
    }

    /**
     *
     * @param bln
     * @return
     */
    public Boolean setNotifyCheck(boolean bln) {
        if (ecatCheck_ != null) {
            return ecatCheck_.setNotifyCheck(bln);
        }
        return null;
    }

    /**
     *
     * @param slave
     * @param bitsOffset
     * @param bitsMask
     * @param value
     * @return
     */
    public Long out(int slave, long bitsOffset, long bitsMask, long value) {
        if (context_.slavelist[slave].outputs.get() != null) {
            Pointer pointer = context_.slavelist[0].outputs.get();
            long address = context_.slavelist[slave].outputs.get().address() - context_.slavelist[0].outputs.get().address() + (bitsOffset / 8);
            int startbit = context_.slavelist[slave].Ostartbit.get() + ((int) (bitsOffset % 8));
            long bits = (context_.slavelist[slave].Obits.get() - bitsOffset);
            long notBitsMask;
            int val, bytes, cnt;

            if ((bitsMask > 0) && (bits > 0)) {
                if (bits < 64) {
                    bitsMask &= (1 << bits) - 1;
                }

                if (bitsMask < 0xff) {
                    bytes = 1;
                } else if (bitsMask < 0xffff) {
                    bytes = 2;
                } else if (bitsMask < 0xffffff) {
                    bytes = 3;
                } else if (bitsMask < 0xffffffff) {
                    bytes = 4;
                } else if (bitsMask < 0xffffffffffL) {
                    bytes = 5;
                } else if (bitsMask < 0xffffffffffffL) {
                    bytes = 6;
                } else if (bitsMask < 0xffffffffffffffL) {
                    bytes = 7;
                } else {
                    bytes = 8;
                }

                bitsMask <<= startbit;
                notBitsMask = ~bitsMask;
                value = (value << startbit) & bitsMask;
                synchronized (lockOut_) {
                    for (cnt = 0; cnt < bytes; cnt++) {
                        if (out_.containsKey(address + cnt)) {
                            out_.get(address + cnt).bitMask &= (notBitsMask >>> (cnt * Byte.SIZE)) & 0xff;
                            out_.get(address + cnt).value |= (value >>> (cnt * Byte.SIZE)) & 0xff;
                        } else {
                            val = Byte.toUnsignedInt(pointer.getByte(address + cnt));
                            if (val != (((val & (notBitsMask >>> (cnt * Byte.SIZE))) | (value >>> (cnt * Byte.SIZE))) & 0xff)) {
                                out_.put(address + cnt, new EcatData((int) ((notBitsMask >>> (cnt * Byte.SIZE)) & 0xff), (int) ((value >>> (cnt * Byte.SIZE)) & 0xff)));
                            }
                        }
                    }
                }
                return address;
            }
        }
        return null;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Pointer pointer;
                    Map.Entry<Long, EcatData> entry;
                    int value;

                    soem_.ec_run(parcel_);
                    while (!exit_) {
                        if (parcel_.isprocess.get() == SoemOsal.FALSE) {
                            if (!out_.isEmpty()) {
                                pointer = context_.slavelist[0].outputs.get();
                                synchronized (lockOut_) {
                                    for (Iterator<Map.Entry<Long, EcatData>> iterator = out_.entrySet().iterator(); iterator.hasNext();) {
                                        entry = iterator.next();
                                        value = pointer.getByte(entry.getKey());
                                        pointer.putByte(entry.getKey(), (byte) ((value & entry.getValue().bitMask) | entry.getValue().value));
                                        iterator.remove();
                                    }
                                }
                            }
                            parcel_.isprocess.set(SoemOsal.TRUE);
                        }
                    }
                } catch (Exception ex) {
                    Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
                }
                return null;
            }
        };
    }

    private void writeLog(final String msg, boolean err) {
        Console.write(SoemEnums.SOEM.toString(), msg, err);
    }
}
