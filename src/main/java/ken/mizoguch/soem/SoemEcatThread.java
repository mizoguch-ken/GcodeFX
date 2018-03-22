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
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.swing.event.EventListenerList;
import jnr.ffi.Pointer;
import ken.mizoguch.console.Console;
import static ken.mizoguch.soem.SoemEthercatType.EC_TIMEOUTRET;
import static ken.mizoguch.soem.SoemEthercatType.EC_TIMEOUTSTATE;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_OPERATIONAL;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_PRE_OP;
import static ken.mizoguch.soem.SoemEthercatType.ec_state.EC_STATE_SAFE_OP;
import static ken.mizoguch.soem.SoemOsal.FALSE;
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
    private final Object lockNotify_ = new Object();
    private final Object lockOut_ = new Object();
    private final SoemEthercatMain.ecx_contextt context_;
    private final ConcurrentMap<Integer, EcatPO2SO> po2so_;
    private final ConcurrentMap<Long, EcatData> notify_;
    private final ConcurrentMap<Long, EcatData> out_;
    private SoemEcatCheck ecatCheck_;
    private long cycletime_;
    private boolean init_, exit_;

    /**
     *
     * @param soem
     * @param context
     */
    public SoemEcatThread(SoemLibrary soem, SoemEthercatMain.ecx_contextt context) {
        eventListenerList_ = new EventListenerList();
        soem_ = soem;
        context_ = context;
        po2so_ = new ConcurrentHashMap<>();
        notify_ = new ConcurrentHashMap<>();
        out_ = new ConcurrentHashMap<>();
        ecatCheck_ = null;
        cycletime_ = 0;
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

                cycletime_ = cycletime;
                exit_ = false;
                if (soem_.ecx_init(context_, ifname) > 0) {
                    if (soem_.ecx_config_init(context_, FALSE) > 0) {
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
                        soem_.ecx_statecheck(context_, 0, EC_STATE_SAFE_OP.intValue(), EC_TIMEOUTSTATE * 4);
                        ecatCheck_ = new SoemEcatCheck(eventListenerList_, soem_, context_);
                        ecatCheck_.setExpectedWKC((context_.grouplist[0].outputsWKC.get() * 2) + context_.grouplist[0].inputsWKC.get());
                        context_.slavelist[0].state.set(EC_STATE_OPERATIONAL.intValue());
                        soem_.ecx_send_processdata(context_);
                        soem_.ecx_receive_processdata(context_, EC_TIMEOUTRET);
                        soem_.ecx_writestate(context_, 0);
                        chk = 40;
                        do {
                            soem_.ecx_statecheck(context_, 0, EC_STATE_OPERATIONAL.intValue(), 50000);
                        } while ((chk-- > 0) && (context_.slavelist[0].state.get() != EC_STATE_OPERATIONAL.intValue()));
                        if (context_.slavelist[0].state.get() == EC_STATE_OPERATIONAL.intValue()) {
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

        cycletime_ = 0;
        exit_ = true;
        if (ecatCheck_ != null) {
            ecatCheck_.exit();
            ecatCheck_.cancel();
            ecatCheck_ = null;
        }
        if (init_ && (soem_ != null) && (context_ != null)) {
            init_ = false;
            context_.slavelist[0].state.set(EC_STATE_PRE_OP.intValue());
            soem_.ecx_writestate(context_, 0);
            chk = 40;
            do {
                soem_.ecx_statecheck(context_, 0, EC_STATE_PRE_OP.intValue(), 50000);
            } while ((chk-- > 0) && (context_.slavelist[0].state.get() != EC_STATE_PRE_OP.intValue()));
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
     * @param mask
     * @param register
     * @return
     */
    public Long notify(int slave, long bitsOffset, int mask, boolean register) {
        Pointer pointer = context_.slavelist[slave].inputs.get();
        if (pointer != null) {
            long address = pointer.address() - context_.slavelist[0].inputs.get().address() + (bitsOffset / 8);
            int startbit = context_.slavelist[slave].Istartbit.get() + ((int) (bitsOffset % 8));
            long bits = (context_.slavelist[slave].Ibits.get() - bitsOffset);
            int value, bitMask;

            if (bits > 8) {
                bits = 8;
            }
            bitMask = (((1 << bits) - 1) & mask) << startbit;
            synchronized (lockNotify_) {
                if (register) {
                    if ((startbit + bits) > 8) {
                        value = pointer.getShort(bitsOffset / 8) & bitMask;
                        if (notify_.containsKey(address)) {
                            notify_.get(address).bitMask |= bitMask & 0xff;
                            notify_.get(address).value |= value & 0xff;
                        } else {
                            notify_.put(address, new EcatData(bitMask & 0xff, value & 0xff));
                        }
                        if (notify_.containsKey(address + 1)) {
                            notify_.get(address + 1).bitMask |= bitMask >>> 8;
                            notify_.get(address + 1).value |= value >>> 8;
                        } else {
                            notify_.put(address + 1, new EcatData(bitMask >>> 8, value >>> 8));
                        }
                    } else {
                        value = pointer.getByte(bitsOffset / 8) & bitMask;
                        if (notify_.containsKey(address)) {
                            notify_.get(address).bitMask |= bitMask;
                            notify_.get(address).value |= value;
                        } else {
                            notify_.put(address, new EcatData(bitMask, value));
                        }
                    }
                } else {
                    if (notify_.containsKey(address)) {
                        notify_.get(address).bitMask &= (~bitMask) & 0xff;
                        if ((notify_.get(address).bitMask) == 0) {
                            notify_.remove(address);
                        }
                    }
                    if ((startbit + bits) > 8) {
                        if (notify_.containsKey(address + 1)) {
                            notify_.get(address + 1).bitMask &= (~bitMask >>> 8) & 0xff;
                            if ((notify_.get(address + 1).bitMask) == 0) {
                                notify_.remove(address + 1);
                            }
                        }
                    }
                }
            }
            return address;
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
    public Integer out(int slave, long bitsOffset, int bitsMask, int value) {
        if (context_.slavelist[slave].outputs.get() != null) {
            long address = context_.slavelist[slave].outputs.get().address() - context_.slavelist[0].outputs.get().address() + (bitsOffset / 8);
            int startbit = context_.slavelist[slave].Ostartbit.get() + ((int) (bitsOffset % 8));
            long bits = (context_.slavelist[slave].Obits.get() - bitsOffset);
            int bitMask, notBitMask;

            if (bits > 8) {
                bits = 8;
            }
            bitMask = (((1 << bits) - 1) & bitsMask) << startbit;
            value = (value << startbit) & bitMask;
            synchronized (lockOut_) {
                if ((startbit + bits) > 8) {
                    notBitMask = (~bitMask) & 0xffff;
                    if (out_.containsKey(address)) {
                        out_.get(address).bitMask &= notBitMask & 0xff;
                        out_.get(address).value = ((out_.get(address).value & notBitMask) | value) & 0xff;
                    } else {
                        out_.put(address, new EcatData(notBitMask & 0xff, value & 0xff));
                    }
                    if (out_.containsKey(address + 1)) {
                        out_.get(address + 1).bitMask |= notBitMask >>> 8;
                        out_.get(address + 1).value = (out_.get(address + 1).value & (notBitMask >>> 8)) | value >>> 8;
                    } else {
                        out_.put(address + 1, new EcatData(bitMask >>> 8, value >>> 8));
                    }
                } else {
                    notBitMask = (~bitMask) & 0xff;
                    if (out_.containsKey(address)) {
                        out_.get(address).bitMask &= notBitMask;
                        out_.get(address).value = (out_.get(address).value & notBitMask) | value;
                    } else {
                        out_.put(address, new EcatData(notBitMask, value));
                    }
                }
            }
            return value;
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
                    long toff, delta, integral;
                    int value;

                    toff = 0;
                    integral = 0;
                    soem_.ecx_send_processdata(context_);
                    while (!exit_) {
                        ecatCheck_.setWkc(soem_.ecx_receive_processdata(context_, EC_TIMEOUTRET));

                        if (!notify_.isEmpty()) {
                            pointer = context_.slavelist[0].inputs.get();
                            synchronized (lockNotify_) {
                                for (Iterator<Map.Entry<Long, EcatData>> iterator = notify_.entrySet().iterator(); iterator.hasNext();) {
                                    entry = iterator.next();
                                    value = pointer.getByte(entry.getKey()) & entry.getValue().bitMask;
                                    if (entry.getValue().value != value) {
                                        entry.getValue().value = value;
                                        for (SoemPluginListener listener : eventListenerList_.getListeners(SoemPluginListener.class)) {
                                            listener.onChangeSoemEcatThread(entry.getKey(), value);
                                        }
                                    }
                                }
                            }
                        }

                        if (!out_.isEmpty()) {
                            pointer = context_.slavelist[0].outputs.get();
                            synchronized (lockOut_) {
                                for (Iterator<Map.Entry<Long, EcatData>> iterator = out_.entrySet().iterator(); iterator.hasNext();) {
                                    entry = iterator.next();
                                    pointer.putByte(entry.getKey(), (byte) ((pointer.getByte(entry.getKey()) & entry.getValue().bitMask) | entry.getValue().value));
                                    iterator.remove();
                                }
                            }
                        }

                        if (context_.slavelist[0].hasdc.get() > 0) {
                            delta = (context_.DCtime.get() - 50000) % cycletime_;
                            if (delta > (cycletime_ / 2)) {
                                delta = delta - cycletime_;
                            }
                            if (delta > 0) {
                                integral++;
                            }
                            if (delta < 0) {
                                integral--;
                            }
                            toff = -(delta / 100) - (integral / 20);
                        }
                        soem_.ecx_send_processdata(context_);

                        try {
                            TimeUnit.NANOSECONDS.sleep(cycletime_ + toff);
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

    private void writeLog(final String msg, boolean err) {
        Console.write(SoemEnums.SOEM.toString(), msg, err);
    }
}
