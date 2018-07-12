/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.util.EnumMapper;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.JavaLibrary;
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPluginListener;

/**
 *
 * @author mizoguch-ken
 */
public class Soem implements SoemPlugin {

    public class SlaveInfo {

        public class SM {

            Integer Index;
            Integer StartAddress;
            Integer SMLength;
            Long SMFlags;
            Short SMType;

        }

        public class FMMU {

            Integer Index;
            Long LogStart;
            Integer LogLength;
            Short LogStartbit;
            Short LogEndbit;
            Integer PhysStart;
            Short PhysStartBit;
            Short FMMUType;
            Short FMMUActive;
        }

        public class PDO {

            public class SII {

                Integer AbsOffset;
                Integer AbsBit;
                Integer ObjectIndex;
                Integer ObjectSubIndex;
                Integer BitLength;
                String ObjectDataType;
                String Name;
            }

            Integer SyncM;
            Integer Index;
            String Name;
            List<SII> SIIlist;

            public SII newSII() {
                SII sii = new SII();
                if (SIIlist == null) {
                    SIIlist = new ArrayList<>();
                }
                SIIlist.add(sii);
                return sii;
            }
        }

        public class SDO {

            public class OD {

                public class OE {

                    Integer Index;
                    Integer DataType;
                    Integer BitLength;
                    Integer ObjectAccess;
                    String Name;
                    String SDO;
                }

                Integer Entries;
                Integer Index;
                Integer DataType;
                Short ObjectCode;
                String Name;
                List<OE> OElist;
                String EcatError;

                public OE newOE() {
                    OE oe = new OE();
                    if (OElist == null) {
                        OElist = new ArrayList<>();
                    }
                    OElist.add(oe);
                    return oe;
                }
            }

            Integer ODlistEntries;
            List<OD> ODlist;
            String EcatError;

            public OD newOD() {
                OD od = new OD();
                if (ODlist == null) {
                    ODlist = new ArrayList<>();
                }
                ODlist.add(od);
                return od;
            }
        }

        public class Slave {

            Integer Index;
            String Name;
            Integer OutputBits;
            Integer InputBits;
            Integer State;
            Integer ALStatusCode;
            String ALStatusCodeString;
            Integer PropagationDelay;
            Short HasDC;
            Short ParentPort;
            Short ActivePorts;
            Integer ConfigAddress;
            Long EEP_MAN;
            Long EEP_ID;
            Long EEP_REV;
            List<SM> SMlist;
            List<FMMU> FMMUlist;
            Short FMMU0Func;
            Short FMMU1Func;
            Short FMMU2Func;
            Short FMMU3Func;
            Integer MailboxWriteLength;
            Integer MailboxReadLength;
            Integer MailboxProtocols;
            Short CoEDetails;
            Short FoEDetails;
            Short EoEDetails;
            Short SoEDetails;
            Short EbusCurrent;
            Short BlockLRW;
            List<PDO> RxPDOlist;
            List<PDO> TxPDOlist;
            SDO SDO;
            String EcatError;

            public SM newSM() {
                SM sm = new SM();
                if (SMlist == null) {
                    SMlist = new ArrayList<>();
                }
                SMlist.add(sm);
                return sm;
            }

            public FMMU newFMMU() {
                FMMU fmmu = new FMMU();
                if (FMMUlist == null) {
                    FMMUlist = new ArrayList<>();
                }
                FMMUlist.add(fmmu);
                return fmmu;
            }

            public PDO newRxPDO() {
                PDO pdo = new PDO();
                if (RxPDOlist == null) {
                    RxPDOlist = new ArrayList<>();
                }
                RxPDOlist.add(pdo);
                return pdo;
            }

            public PDO newTxPDO() {
                PDO pdo = new PDO();
                if (TxPDOlist == null) {
                    TxPDOlist = new ArrayList<>();
                }
                TxPDOlist.add(pdo);
                return pdo;
            }

            public SDO newSDO() {
                SDO sdo = new SDO();
                SDO = sdo;
                return sdo;
            }
        }

        String InterfaceName;
        int SlaveCount;
        int ExpectedWKC;
        List<Slave> Slavelist;
        String EcatError;

        public Slave newSlave() {
            Slave slave = new Slave();
            if (Slavelist == null) {
                Slavelist = new ArrayList<>();
            }
            Slavelist.add(slave);
            return slave;
        }
    }

    private SoemLibrary soem_;
    private jnr.ffi.Runtime runtime_;

    private SoemEtherCAT.ecx_parcelt parcel_;
    private SoemEtherCATMain.ecx_contextt context_;
    private SoemEcatThread ecatThread_;

    private Pointer pIOmap_;

    private final Gson gson_ = new Gson();

    /**
     *
     */
    public Soem() {
        soem_ = null;
        runtime_ = null;
        parcel_ = null;
        context_ = null;
        ecatThread_ = null;
        pIOmap_ = null;
    }

    @Override
    public Boolean load(Path libraryPath) {
        if ((soem_ == null) && (runtime_ == null)) {
            try {
                String libraryName = JavaLibrary.removeFileExtension(libraryPath.getFileName());
                if (JavaLibrary.isWindows()) {
                } else if (JavaLibrary.isLinux()) {
                    libraryName = libraryName.substring(libraryName.indexOf("lib") + 3);
                } else if (JavaLibrary.isMac()) {
                    libraryName = libraryName.substring(libraryName.indexOf("lib") + 3);
                } else {
                }

                if (JavaLibrary.addLibraryPath(libraryPath.getParent(), libraryName, false)) {
                    soem_ = LibraryLoader.create(SoemLibrary.class).load(libraryName);
                    runtime_ = jnr.ffi.Runtime.getRuntime(soem_);
                    return true;
                }
            } catch (Exception ex) {
                Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
                soem_ = null;
                runtime_ = null;
                return null;
            }
        }
        return false;
    }

    @Override
    public Boolean init(int ioSize) {
        if (soem_ != null) {
            if ((parcel_ == null) && (context_ == null) && (pIOmap_ == null) && (ecatThread_ == null)) {
                pIOmap_ = Memory.allocateDirect(runtime_, ioSize, true);
                context_ = soem_.ec_malloc_context().register();
                parcel_ = soem_.ec_malloc_parcel(context_).register();
                ecatThread_ = new SoemEcatThread(soem_, parcel_, context_);
                return true;
            }
            return false;
        }
        return null;
    }

    @Override
    public Boolean po2so(int slave, long eep_man, long eep_id, Callable<Integer> func) {
        if (soem_ != null) {
            if (ecatThread_ != null) {
                ecatThread_.po2so(slave, eep_man, eep_id, func);
                return true;
            }
            return false;
        }
        return null;
    }

    @Override
    public Boolean start(String ifname, long cycletime, SoemPluginListener listener) {
        if (soem_ != null) {
            if (ecatThread_ != null) {
                ecatThread_.addSoemEcatListener(listener);
                if (ecatThread_.init(ifname, cycletime, pIOmap_)) {
                    parcel_.wkc.set(0);
                    parcel_.cycletime.set(cycletime);
                    parcel_.dorun.set(SoemOsal.TRUE);
                    if (Platform.isFxApplicationThread()) {
                        runEcatThread();
                    } else {
                        Platform.runLater(() -> {
                            runEcatThread();
                        });
                    }
                    return true;
                }
            }
            return false;
        }
        return null;
    }

    private void runEcatThread() {
        if (ecatThread_.getState() == Worker.State.READY) {
            ecatThread_.start();
        } else {
            ecatThread_.restart();
        }
    }

    @Override
    public Boolean setNotifyCheck(boolean state) {
        if (ecatThread_ != null) {
            return ecatThread_.setNotifyCheck(state);
        }
        return null;
    }

    @Override
    public Boolean close(SoemPluginListener listener) {
        if (soem_ != null) {
            if (ecatThread_ != null) {
                ecatThread_.exit();
                ecatThread_.cancel();
                ecatThread_.removeSoemEcatListener(listener);
                ecatThread_ = null;
            }
            if (parcel_ != null) {
                parcel_.dorun.set(SoemOsal.FALSE);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException ex) {
                }
                soem_.ec_free_parcel(parcel_);
                parcel_ = null;
            }
            if (context_ != null) {
                soem_.ecx_close(context_);
                soem_.ec_free_context(context_);
                context_ = null;
            }
            if (pIOmap_ != null) {
                pIOmap_ = null;
            }
            return true;
        }
        return null;
    }

    @Override
    public Integer slavecount() {
        if (context_ != null) {
            return context_.slavecount.get();
        }
        return null;
    }

    @Override
    public Integer state(int slave) {
        if (context_ != null) {
            return context_.slavelist[slave].state.get();
        }
        return null;
    }

    @Override
    public Boolean islost(int slave) {
        if (context_ != null) {
            return (context_.slavelist[slave].islost.get() == SoemOsal.TRUE);
        }
        return null;
    }

    @Override
    public Boolean docheckstate() {
        if (context_ != null) {
            return (context_.grouplist[0].docheckstate.get() == SoemOsal.TRUE);
        }
        return null;
    }

    @Override
    public Byte[] sdoRead(int slave, int index, int subIndex, int byteSize) {
        if (context_ != null) {
            Pointer psize = Memory.allocate(runtime_, Integer.BYTES);
            Pointer p = Memory.allocate(runtime_, byteSize);

            psize.putInt(0, byteSize);
            if (soem_.ecx_SDOread(context_, slave, index, subIndex, SoemOsal.FALSE, psize, p, SoemEtherCATType.EC_TIMEOUTRXM) > 0) {
                Byte[] result = new Byte[psize.getInt(0)];
                for (int i = 0; i < result.length; i++) {
                    result[i] = p.getByte(i);
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public Integer sdoWrite(int slave, int index, int subIndex, int byteSize, byte[] value) {
        if (context_ != null) {
            Pointer p = Memory.allocate(runtime_, byteSize);

            p.put(0, value, 0, value.length);
            return soem_.ecx_SDOwrite(context_, slave, index, subIndex, SoemOsal.FALSE, byteSize, p, SoemEtherCATType.EC_TIMEOUTRXM);
        }
        return null;
    }

    @Override
    public Long in(int slave, long bitsOffset, long bitsMask) {
        if (context_ != null) {
            if ((bitsOffset >= 0) && (bitsOffset < context_.slavelist[slave].Ibits.get())) {
                if (context_.slavelist[slave].inputs.get() != null) {
                    long bits = (context_.slavelist[slave].Ibits.get() - bitsOffset);
                    if ((bitsMask > 0) && (bits > 0)) {
                        if (bits < 64) {
                            bitsMask &= (1 << bits) - 1;
                        }

                        if (bitsMask < 0xff) {
                            return ((context_.slavelist[slave].inputs.get().getByte(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffff) {
                            return ((context_.slavelist[slave].inputs.get().getShort(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffff) {
                            return ((context_.slavelist[slave].inputs.get().getInt(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffff) {
                            return ((context_.slavelist[slave].inputs.get().getInt(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffL) {
                            return ((context_.slavelist[slave].inputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffffL) {
                            return ((context_.slavelist[slave].inputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffffffL) {
                            return ((context_.slavelist[slave].inputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else {
                            return ((context_.slavelist[slave].inputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Istartbit.get() + (bitsOffset % 8))) & bitsMask);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Long out(int slave, long bitsOffset, long bitsMask) {
        if (context_ != null) {
            if ((bitsOffset >= 0) && (bitsOffset < context_.slavelist[slave].Obits.get())) {
                if (context_.slavelist[slave].outputs.get() != null) {
                    long bits = (context_.slavelist[slave].Obits.get() - bitsOffset);
                    if ((bitsMask > 0) && (bits > 0)) {
                        if (bits < 64) {
                            bitsMask &= (1 << bits) - 1;
                        }

                        if (bitsMask < 0xff) {
                            return ((context_.slavelist[slave].outputs.get().getByte(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffff) {
                            return ((context_.slavelist[slave].outputs.get().getShort(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffff) {
                            return ((context_.slavelist[slave].outputs.get().getInt(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffff) {
                            return ((context_.slavelist[slave].outputs.get().getInt(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffL) {
                            return ((context_.slavelist[slave].outputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffffL) {
                            return ((context_.slavelist[slave].outputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else if (bitsMask < 0xffffffffffffffL) {
                            return ((context_.slavelist[slave].outputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        } else {
                            return ((context_.slavelist[slave].outputs.get().getLong(bitsOffset / 8) >> (context_.slavelist[slave].Ostartbit.get() + (bitsOffset % 8))) & bitsMask);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Long out(int slave, long bitsOffset, long bitsMask, long value) {
        if ((context_ != null) && (ecatThread_ != null)) {
            if ((bitsOffset >= 0) && (bitsOffset < context_.slavelist[slave].Obits.get())) {
                return ecatThread_.out(slave, bitsOffset, bitsMask, value);
            }
        }
        return null;
    }

    @Override
    public String find_adapters() {
        if (soem_ != null) {
            Map<String, String> adapters = new HashMap<>();

            try {
                SoemEtherCATMain.ec_adaptert findAdapters = soem_.ec_find_adapters();
                SoemEtherCATMain.ec_adaptert findAdapter = findAdapters;
                while (findAdapter != null) {
                    adapters.put(findAdapter.desc.get(), findAdapter.name.get());
                    findAdapter = findAdapter.getNext(runtime_);
                }
                soem_.ec_free_adapters(findAdapters);
                return gson_.toJson(adapters);
            } catch (IllegalArgumentException | StackOverflowError | ClassCastException ex) {
                Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
            }
        }
        return null;
    }

    @Override
    public String slaveinfo(String ifname, boolean printSDO, boolean printMAP) {
        if (soem_ != null) {
            SlaveInfo info = new SlaveInfo();
            SlaveInfo.Slave slave;
            SlaveInfo.SM sm;
            SlaveInfo.FMMU fmmu;

            try {
                int wkc, ssigen, cnt, nSM, chk, i, j;
                SoemEtherCATMain.ecx_contextt context = soem_.ec_malloc_context().register();
                if (soem_.ecx_init(context, ifname) > 0) {
                    info.InterfaceName = ifname;
                    Pointer pIOmap = Memory.allocateDirect(runtime_, 4096);
                    wkc = soem_.ecx_config_init(context, SoemOsal.FALSE);
                    if (wkc > 0) {
                        soem_.ecx_config_map_group(context, pIOmap, 0);
                        soem_.ecx_configdc(context);
                        if (context.ecaterror.get() > 0) {
                            info.EcatError = soem_.ecx_elist2string(context);
                        }
                        info.SlaveCount = context.slavecount.get();
                        info.ExpectedWKC = (context.grouplist[0].outputsWKC.get() * 2) + context.grouplist[0].inputsWKC.get();
                        soem_.ecx_statecheck(context, 0, SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue(), SoemEtherCATType.EC_TIMEOUTSTATE * 3);
                        if (context.slavelist[0].state.get() != SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue()) {
                            soem_.ecx_readstate(context);
                            for (i = 1; i <= context.slavecount.get(); i++) {
                                if (context.slavelist[i].state.get() != SoemEtherCATType.ec_state.EC_STATE_SAFE_OP.intValue()) {
                                    slave = info.newSlave();
                                    slave.Index = i;
                                    slave.State = context.slavelist[i].state.get();
                                    slave.ALStatusCode = context.slavelist[i].ALstatuscode.get();
                                    slave.ALStatusCodeString = soem_.ec_ALstatuscode2string(context.slavelist[i].ALstatuscode.get());
                                }
                            }
                        }

                        soem_.ecx_readstate(context);
                        for (cnt = 1; cnt <= context.slavecount.get(); cnt++) {
                            slave = info.newSlave();
                            slave.Index = cnt;
                            slave.Name = context.slavelist[cnt].name.get();
                            slave.OutputBits = context.slavelist[cnt].Obits.get();
                            slave.InputBits = context.slavelist[cnt].Ibits.get();
                            slave.State = context.slavelist[cnt].state.get();
                            slave.PropagationDelay = context.slavelist[cnt].pdelay.get();
                            slave.HasDC = context.slavelist[cnt].hasdc.get();
                            if (context.slavelist[cnt].hasdc.get() > 0) {
                                slave.ParentPort = context.slavelist[cnt].parentport.get();
                            }
                            slave.ActivePorts = context.slavelist[cnt].activeports.get();
                            slave.ConfigAddress = context.slavelist[cnt].configadr.get();
                            slave.EEP_MAN = context.slavelist[cnt].eep_man.get();
                            slave.EEP_ID = context.slavelist[cnt].eep_id.get();
                            slave.EEP_REV = context.slavelist[cnt].eep_rev.get();
                            for (nSM = 0; nSM < SoemEtherCATMain.EC_MAXSM; nSM++) {
                                if (context.slavelist[cnt].SM[nSM].StartAddr.get() > 0) {
                                    sm = slave.newSM();
                                    sm.Index = nSM;
                                    sm.StartAddress = context.slavelist[cnt].SM[nSM].StartAddr.get();
                                    sm.SMLength = context.slavelist[cnt].SM[nSM].SMlength.get();
                                    sm.SMFlags = context.slavelist[cnt].SM[nSM].SMflags.get();
                                    sm.SMType = context.slavelist[cnt].SMtype[nSM].get();
                                }
                            }
                            for (j = 0; j < context.slavelist[cnt].FMMUunused.get(); j++) {
                                fmmu = slave.newFMMU();
                                fmmu.Index = j;
                                fmmu.LogStart = context.slavelist[cnt].FMMU[j].LogStart.get();
                                fmmu.LogLength = context.slavelist[cnt].FMMU[j].LogLength.get();
                                fmmu.LogStartbit = context.slavelist[cnt].FMMU[j].LogStartbit.get();
                                fmmu.LogEndbit = context.slavelist[cnt].FMMU[j].LogEndbit.get();
                                fmmu.PhysStart = context.slavelist[cnt].FMMU[j].PhysStart.get();
                                fmmu.PhysStartBit = context.slavelist[cnt].FMMU[j].PhysStartBit.get();
                                fmmu.FMMUType = context.slavelist[cnt].FMMU[j].FMMUtype.get();
                                fmmu.FMMUActive = context.slavelist[cnt].FMMU[j].FMMUactive.get();
                            }
                            slave.FMMU0Func = context.slavelist[cnt].FMMU0func.get();
                            slave.FMMU1Func = context.slavelist[cnt].FMMU1func.get();
                            slave.FMMU2Func = context.slavelist[cnt].FMMU2func.get();
                            slave.FMMU3Func = context.slavelist[cnt].FMMU3func.get();
                            slave.MailboxWriteLength = context.slavelist[cnt].mbx_l.get();
                            slave.MailboxReadLength = context.slavelist[cnt].mbx_rl.get();
                            slave.MailboxProtocols = context.slavelist[cnt].mbx_proto.get();
                            ssigen = soem_.ecx_siifind(context, cnt, SoemEtherCATType.ECT_SII_GENERAL);
                            if (ssigen > 0) {
                                context.slavelist[cnt].CoEdetails.set(soem_.ecx_siigetbyte(context, cnt, ssigen + 0x07));
                                context.slavelist[cnt].FoEdetails.set(soem_.ecx_siigetbyte(context, cnt, ssigen + 0x08));
                                context.slavelist[cnt].EoEdetails.set(soem_.ecx_siigetbyte(context, cnt, ssigen + 0x09));
                                context.slavelist[cnt].SoEdetails.set(soem_.ecx_siigetbyte(context, cnt, ssigen + 0x0a));
                                if ((soem_.ecx_siigetbyte(context, cnt, ssigen + 0x0d) & 0x02) > 0) {
                                    context.slavelist[cnt].blockLRW.set(1);
                                    context.slavelist[0].blockLRW.set(context.slavelist[0].blockLRW.get() + 1);
                                }
                                context.slavelist[cnt].Ebuscurrent.set(soem_.ecx_siigetbyte(context, cnt, ssigen + 0x0e));
                                context.slavelist[cnt].Ebuscurrent.set(context.slavelist[cnt].Ebuscurrent.get() + (soem_.ecx_siigetbyte(context, cnt, ssigen + 0x0f) << 8));
                                context.slavelist[0].Ebuscurrent.set(context.slavelist[0].Ebuscurrent.get() + context.slavelist[cnt].Ebuscurrent.get());
                                slave.CoEDetails = context.slavelist[cnt].CoEdetails.get();
                                slave.FoEDetails = context.slavelist[cnt].FoEdetails.get();
                                slave.EoEDetails = context.slavelist[cnt].EoEdetails.get();
                                slave.SoEDetails = context.slavelist[cnt].SoEdetails.get();
                                slave.EbusCurrent = context.slavelist[cnt].Ebuscurrent.get();
                                slave.BlockLRW = context.slavelist[cnt].blockLRW.get();
                                if (((context.slavelist[cnt].mbx_proto.get() & SoemEtherCATMain.ECT_MBXPROT_COE) > 0) && printSDO) {
                                    si_sdo(context, cnt, slave.newSDO());
                                }
                                if (printMAP) {
                                    if ((context.slavelist[cnt].mbx_proto.get() & SoemEtherCATMain.ECT_MBXPROT_COE) > 0) {
                                        si_map_sdo(context, pIOmap, cnt, slave);
                                    } else {
                                        si_map_sii(context, pIOmap, cnt, slave);
                                    }
                                }
                            }
                        }
                        context.slavelist[0].state.set(SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue());
                        soem_.ecx_writestate(context, 0);
                        chk = 40;
                        do {
                            soem_.ecx_statecheck(context, 0, SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue(), 50000);
                        } while ((chk-- > 0) && (context.slavelist[0].state.get() != SoemEtherCATType.ec_state.EC_STATE_PRE_OP.intValue()));
                    } else {
                        info.EcatError = "No slaves found!";
                    }
                    soem_.ecx_close(context);
                    soem_.ec_free_context(context);
                } else {
                    info.EcatError = "No socket connection on " + ifname + " Excecute as root";
                }
            } catch (ClassCastException | IllegalArgumentException | IndexOutOfBoundsException | NullPointerException | StackOverflowError | UnsatisfiedLinkError ex) {
                Console.writeStackTrace(SoemEnums.SOEM.toString(), ex);
            }
            return gson_.toJson(info);
        }
        return null;
    }

    private int si_siiPDO(SoemEtherCATMain.ecx_contextt context, int index, int t, int mapoffset, int bitoffset, SlaveInfo.Slave slave) {
        SlaveInfo.PDO pdo;
        SlaveInfo.PDO.SII sii;
        int a, w, c, e, er, Size;
        int eectl;
        int obj_idx;
        int obj_subidx;
        int obj_name;
        int obj_datatype;
        int bitlen;
        int totalsize;
        SoemEtherCATMain.ec_eepromPDOt PDO = new SoemEtherCATMain.ec_eepromPDOt(runtime_);
        Pointer pPDO = Memory.allocateDirect(runtime_, Struct.size(PDO));
        PDO.useMemory(pPDO);
        int abs_offset, abs_bit;
        SoemLibrary.UTF8String str_name = new SoemLibrary.UTF8String(runtime_, SoemEtherCATMain.EC_MAXNAME + 1);
        Pointer pStr_name = Memory.allocateDirect(runtime_, Struct.size(str_name));
        str_name.useMemory(pStr_name);

        eectl = context.slavelist[index].eep_pdi.get();
        Size = 0;
        totalsize = 0;
        PDO.nPDO.set(0);
        PDO.Length.set(0);
        PDO.Index[1].set(0);
        for (c = 0; c < SoemEtherCATMain.EC_MAXSM; c++) {
            PDO.SMbitsize[c].set(0);
        }
        if (t > 1) {
            t = 1;
        }
        PDO.Startpos.set(soem_.ecx_siifind(context, index, SoemEtherCATType.ECT_SII_PDO + t));
        if (PDO.Startpos.get() > 0) {
            a = PDO.Startpos.get();
            w = soem_.ecx_siigetbyte(context, index, a++);
            w += (soem_.ecx_siigetbyte(context, index, a++) << 8);
            PDO.Length.set(w);
            c = 1;
            /* traverse through all PDOs */
            do {
                PDO.nPDO.set(PDO.nPDO.get() + 1);
                PDO.Index[PDO.nPDO.get()].set(soem_.ecx_siigetbyte(context, index, a++));
                PDO.Index[PDO.nPDO.get()].set(PDO.Index[PDO.nPDO.get()].get() + (soem_.ecx_siigetbyte(context, index, a++) << 8));
                PDO.BitSize[PDO.nPDO.get()].set(0);
                c++;
                /* number of entries in PDO */
                e = soem_.ecx_siigetbyte(context, index, a++);
                PDO.SyncM[PDO.nPDO.get()].set(soem_.ecx_siigetbyte(context, index, a++));
                a++;
                obj_name = soem_.ecx_siigetbyte(context, index, a++);
                a += 2;
                c += 2;
                if (PDO.SyncM[PDO.nPDO.get()].get() < SoemEtherCATMain.EC_MAXSM) /* active and in range SM? */ {
                    str_name.set("");
                    if (obj_name > 0) {
                        soem_.ecx_siistring(context, pStr_name, index, obj_name);
                    }
                    if (t > 0) {
                        pdo = slave.newRxPDO();
                        pdo.SyncM = PDO.SyncM[PDO.nPDO.get()].get();
                        pdo.Index = PDO.Index[PDO.nPDO.get()].get();
                        pdo.Name = str_name.get();
                    } else {
                        pdo = slave.newTxPDO();
                        pdo.SyncM = PDO.SyncM[PDO.nPDO.get()].get();
                        pdo.Index = PDO.Index[PDO.nPDO.get()].get();
                        pdo.Name = str_name.get();
                    }
                    /* read all entries defined in PDO */
                    for (er = 1; er <= e; er++) {
                        sii = pdo.newSII();
                        c += 4;
                        obj_idx = soem_.ecx_siigetbyte(context, index, a++);
                        obj_idx += (soem_.ecx_siigetbyte(context, index, a++) << 8);
                        obj_subidx = soem_.ecx_siigetbyte(context, index, a++);
                        obj_name = soem_.ecx_siigetbyte(context, index, a++);
                        obj_datatype = soem_.ecx_siigetbyte(context, index, a++);
                        bitlen = soem_.ecx_siigetbyte(context, index, a++);
                        abs_offset = mapoffset + (bitoffset / 8);
                        abs_bit = bitoffset % 8;
                        PDO.BitSize[PDO.nPDO.get()].set(PDO.BitSize[PDO.nPDO.get()].get() + bitlen);
                        a += 2;

                        str_name.set("");
                        if (obj_name > 0) {
                            soem_.ecx_siistring(context, pStr_name, index, obj_name);
                        }
                        sii.AbsOffset = abs_offset;
                        sii.AbsBit = abs_bit;
                        sii.ObjectIndex = obj_idx;
                        sii.ObjectSubIndex = obj_subidx;
                        sii.BitLength = bitlen;
                        sii.ObjectDataType = dtype2string(obj_datatype);
                        sii.Name = str_name.get();
                        bitoffset += bitlen;
                        totalsize += bitlen;
                    }
                    PDO.SMbitsize[PDO.SyncM[PDO.nPDO.get()].get()].set(PDO.SMbitsize[PDO.SyncM[PDO.nPDO.get()].get()].get() + PDO.BitSize[PDO.nPDO.get()].get());
                    Size += PDO.BitSize[PDO.nPDO.get()].get();
                    c++;
                } else /* PDO deactivated because SM is 0xff or > EC_MAXSM */ {
                    c += 4 * e;
                    a += 8 * e;
                    c++;
                }
                if (PDO.nPDO.get() >= (SoemEtherCATMain.EC_MAXEEPDO - 1)) {
                    c = PDO.Length.get();
                    /* limit number of PDO entries in buffer */
                }
            } while (c < PDO.Length.get());
        }
        if (eectl > 0) {
            soem_.ecx_eeprom2pdi(context, index);
            /* if eeprom control was previously pdi then restore */
        }
        return totalsize;
    }

    private int si_map_sii(SoemEtherCATMain.ecx_contextt context, Pointer pIOmap, int index, SlaveInfo.Slave slave) {
        int retVal = 0;
        int Tsize, outputs_bo, inputs_bo;
        long address;

        outputs_bo = 0;
        inputs_bo = 0;
        /* read the assign RXPDOs */
        if (context.slavelist[index].outputs.get() == null) {
            address = 0;
        } else {
            address = context.slavelist[index].outputs.get().address();
        }
        Tsize = si_siiPDO(context, index, 1, (int) (address - pIOmap.address()), outputs_bo, slave);
        outputs_bo += Tsize;
        /* read the assign TXPDOs */
        if (context.slavelist[index].inputs.get() == null) {
            address = 0;
        } else {
            address = context.slavelist[index].inputs.get().address();
        }
        Tsize = si_siiPDO(context, index, 0, (int) (address - pIOmap.address()), inputs_bo, slave);
        inputs_bo += Tsize;
        /* found some I/O bits ? */
        if ((outputs_bo > 0) || (inputs_bo > 0)) {
            retVal = 1;
        }
        return retVal;
    }

    private String dtype2string(int dtype) {
        String hstr;

        try {
            SoemEtherCATType.ec_datatype edtype = (SoemEtherCATType.ec_datatype) EnumMapper.getInstance(SoemEtherCATType.ec_datatype.class).valueOf(dtype);
            switch (edtype) {
                case ECT_BOOLEAN:
                    hstr = "BOOLEAN";
                    break;
                case ECT_INTEGER8:
                    hstr = "INTEGER8";
                    break;
                case ECT_INTEGER16:
                    hstr = "INTEGER16";
                    break;
                case ECT_INTEGER32:
                    hstr = "INTEGER32";
                    break;
                case ECT_INTEGER24:
                    hstr = "INTEGER24";
                    break;
                case ECT_INTEGER64:
                    hstr = "INTEGER64";
                    break;
                case ECT_UNSIGNED8:
                    hstr = "UNSIGNED8";
                    break;
                case ECT_UNSIGNED16:
                    hstr = "UNSIGNED16";
                    break;
                case ECT_UNSIGNED32:
                    hstr = "UNSIGNED32";
                    break;
                case ECT_UNSIGNED24:
                    hstr = "UNSIGNED24";
                    break;
                case ECT_UNSIGNED64:
                    hstr = "UNSIGNED64";
                    break;
                case ECT_REAL32:
                    hstr = "REAL32";
                    break;
                case ECT_REAL64:
                    hstr = "REAL64";
                    break;
                case ECT_BIT1:
                    hstr = "BIT1";
                    break;
                case ECT_BIT2:
                    hstr = "BIT2";
                    break;
                case ECT_BIT3:
                    hstr = "BIT3";
                    break;
                case ECT_BIT4:
                    hstr = "BIT4";
                    break;
                case ECT_BIT5:
                    hstr = "BIT5";
                    break;
                case ECT_BIT6:
                    hstr = "BIT6";
                    break;
                case ECT_BIT7:
                    hstr = "BIT7";
                    break;
                case ECT_BIT8:
                    hstr = "BIT8";
                    break;
                case ECT_VISIBLE_STRING:
                    hstr = "VISIBLE_STRING";
                    break;
                case ECT_OCTET_STRING:
                    hstr = "OCTET_STRING";
                    break;
                default:
                    hstr = String.format("Type 0x%04X", dtype);
            }
        } catch (IllegalArgumentException ex) {
            hstr = String.format("Type 0x%04X", dtype);
        }
        return hstr;
    }

    private int si_PDOassign(SoemEtherCATMain.ecx_contextt context, int index, int PDOassign, int mapoffset, int bitoffset, SlaveInfo.PDO pdo) {
        SlaveInfo.PDO.SII sii;
        int idxloop, nidx, subidxloop, idx, subidx;
        SoemLibrary.Uint16 rdat;
        SoemLibrary.Uint8 subcnt;
        int wkc, bsize = 0;
        SoemLibrary.Int32 rdl, rdat2;
        int bitlen, obj_subidx;
        int obj_idx;
        int abs_offset, abs_bit;
        SoemEtherCATCoE.ec_ODlistt ODlist = new SoemEtherCATCoE.ec_ODlistt(runtime_);
        SoemEtherCATCoE.ec_OElistt OElist = new SoemEtherCATCoE.ec_OElistt(runtime_);

        rdat = new SoemLibrary.Uint16(runtime_);
        subcnt = new SoemLibrary.Uint8(runtime_);
        rdl = new SoemLibrary.Int32(runtime_);
        rdat2 = new SoemLibrary.Int32(runtime_);

        Pointer pRdat = Memory.allocateDirect(runtime_, Struct.size(rdat));
        Pointer pSubcnt = Memory.allocateDirect(runtime_, Struct.size(subcnt));
        Pointer pRdl = Memory.allocateDirect(runtime_, Struct.size(rdl));
        Pointer pRdat2 = Memory.allocateDirect(runtime_, Struct.size(rdat2));
        Pointer pODlist = Memory.allocateDirect(runtime_, Struct.size(ODlist));
        Pointer pOElist = Memory.allocateDirect(runtime_, Struct.size(OElist));

        rdat.useMemory(pRdat);
        subcnt.useMemory(pSubcnt);
        rdl.useMemory(pRdl);
        rdat2.useMemory(pRdat2);
        ODlist.useMemory(pODlist);
        OElist.useMemory(pOElist);

        rdl.set(Struct.size(rdat));
        rdat.set(0);
        /* read PDO assign subindex 0 ( = number of PDO's) */
        wkc = soem_.ecx_SDOread(context, index, PDOassign, 0x00, SoemOsal.FALSE, pRdl, pRdat, SoemEtherCATType.EC_TIMEOUTRXM);
        /* positive result from slave ? */
        if ((wkc > 0) && (rdat.get() > 0)) {
            /* number of available sub indexes */
            nidx = rdat.get();
            bsize = 0;
            /* read all PDO's */
            for (idxloop = 1; idxloop <= nidx; idxloop++) {
                rdl.set(Struct.size(rdat));
                rdat.set(0);
                /* read PDO assign */
                soem_.ecx_SDOread(context, index, PDOassign, idxloop, SoemOsal.FALSE, pRdl, pRdat, SoemEtherCATType.EC_TIMEOUTRXM);
                /* result is index of PDO */
                idx = rdat.get();
                if (idx > 0) {
                    rdl.set(Struct.size(subcnt));
                    subcnt.set(0);
                    /* read number of subindexes of PDO */
                    soem_.ecx_SDOread(context, index, idx, 0x00, SoemOsal.FALSE, pRdl, pSubcnt, SoemEtherCATType.EC_TIMEOUTRXM);
                    subidx = subcnt.get();
                    /* for each subindex */
                    for (subidxloop = 1; subidxloop <= subidx; subidxloop++) {
                        rdl.set(Struct.size(rdat2));
                        rdat2.set(0);
                        /* read SDO that is mapped in PDO */
                        soem_.ecx_SDOread(context, index, idx, subidxloop, SoemOsal.FALSE, pRdl, pRdat2, SoemEtherCATType.EC_TIMEOUTRXM);
                        /* extract bitlength of SDO */
                        bitlen = (rdat2.get() & 0xff);
                        bsize += bitlen;
                        obj_idx = (rdat2.get() >> 16);
                        obj_subidx = ((rdat2.get() >> 8) & 0x000000ff);
                        abs_offset = mapoffset + (bitoffset / 8);
                        abs_bit = bitoffset % 8;
                        ODlist.Slave.set(index);
                        ODlist.Index[0].set(obj_idx);
                        OElist.Entries.set(0);
                        wkc = 0;
                        /* read object entry from dictionary if not a filler (0x0000:0x00) */
                        if ((obj_idx > 0) || (obj_subidx > 0)) {
                            wkc = soem_.ecx_readOEsingle(context, 0, obj_subidx, ODlist, OElist);
                        }
                        sii = pdo.newSII();
                        sii.AbsOffset = abs_offset;
                        sii.AbsBit = abs_bit;
                        sii.ObjectIndex = obj_idx;
                        sii.ObjectSubIndex = obj_subidx;
                        sii.BitLength = bitlen;
                        if ((wkc > 0) && (OElist.Entries.get() > 0)) {
                            sii.ObjectDataType = dtype2string(OElist.DataType[obj_subidx].get());
                            sii.Name = OElist.Name[obj_subidx].get();
                        }
                        bitoffset += bitlen;
                    }
                }
            }
        }
        /* return total found bitlength (PDO) */
        return bsize;
    }

    private int si_map_sdo(SoemEtherCATMain.ecx_contextt context, Pointer pIOmap, int index, SlaveInfo.Slave slave) {
        SlaveInfo.PDO pdo;
        int wkc;
        SoemLibrary.Int32 rdl;
        int retVal = 0;
        SoemLibrary.Uint8 nSM, tSM;
        int iSM;
        int Tsize, outputs_bo, inputs_bo;
        int SMt_bug_add;

        rdl = new SoemLibrary.Int32(runtime_);
        nSM = new SoemLibrary.Uint8(runtime_);
        tSM = new SoemLibrary.Uint8(runtime_);

        Pointer pRdl = Memory.allocateDirect(runtime_, Struct.size(rdl));
        Pointer pNSM = Memory.allocateDirect(runtime_, Struct.size(nSM));
        Pointer pTSM = Memory.allocateDirect(runtime_, Struct.size(tSM));

        rdl.useMemory(pRdl);
        nSM.useMemory(pNSM);
        tSM.useMemory(pTSM);

        SMt_bug_add = 0;
        outputs_bo = 0;
        inputs_bo = 0;
        rdl.set(Struct.size(nSM));
        nSM.set(0);
        /* read SyncManager Communication Type object count */
        wkc = soem_.ecx_SDOread(context, index, SoemEtherCATType.ECT_SDO_SMCOMMTYPE, 0x00, SoemOsal.FALSE, pRdl, pNSM, SoemEtherCATType.EC_TIMEOUTRXM);
        /* positive result from slave ? */
        if ((wkc > 0) && (nSM.get() > 2)) {
            /* make nSM equal to number of defined SM */
            nSM.set(nSM.get() - 1);
            /* limit to maximum number of SM defined, if true the slave can't be configured */
            if (nSM.get() > SoemEtherCATMain.EC_MAXSM) {
                nSM.set(SoemEtherCATMain.EC_MAXSM);
            }
            /* iterate for every SM type defined */
            for (iSM = 2; iSM <= nSM.get(); iSM++) {
                rdl.set(Struct.size(tSM));
                tSM.set(0);
                /* read SyncManager Communication Type */
                wkc = soem_.ecx_SDOread(context, index, SoemEtherCATType.ECT_SDO_SMCOMMTYPE, iSM + 1, SoemOsal.FALSE, pRdl, pTSM, SoemEtherCATType.EC_TIMEOUTRXM);
                if (wkc > 0) {
                    if ((iSM == 2) && (tSM.get() == 2)) // SM2 has type 2 == mailbox out, this is a bug in the slave!
                    {
                        SMt_bug_add = 1; // try to correct, this works if the types are 0 1 2 3 and should be 1 2 3 4
                    }
                    if (tSM.get() > 0) {
                        tSM.set(tSM.get() + SMt_bug_add); // only add if SMt > 0
                    }
                    if (tSM.get() == 3) // outputs
                    {
                        /* read the assign RXPDO */
                        pdo = slave.newRxPDO();
                        pdo.Index = iSM;
                        Tsize = si_PDOassign(context, index, SoemEtherCATType.ECT_SDO_PDOASSIGN + iSM, (int) (context.slavelist[index].outputs.get().address() - pIOmap.address()), outputs_bo, pdo);
                        outputs_bo += Tsize;
                    }
                    if (tSM.get() == 4) // inputs
                    {
                        /* read the assign TXPDO */
                        pdo = slave.newTxPDO();
                        pdo.Index = iSM;
                        Tsize = si_PDOassign(context, index, SoemEtherCATType.ECT_SDO_PDOASSIGN + iSM, (int) (context.slavelist[index].inputs.get().address() - pIOmap.address()), inputs_bo, pdo);
                        inputs_bo += Tsize;
                    }
                }
            }
        }

        /* found some I/O bits ? */
        if ((outputs_bo > 0) || (inputs_bo > 0)) {
            retVal = 1;
        }
        return retVal;
    }

    private String SDO2string(SoemEtherCATMain.ecx_contextt context, int index, int idx, int subidx, int dtype) {
        StringBuilder hstr;
        SoemLibrary.UTF8String usdo = new SoemLibrary.UTF8String(runtime_, 128);
        SoemLibrary.Int32 l = new SoemLibrary.Int32(runtime_);
        Pointer pUsdo = Memory.allocateDirect(runtime_, Struct.size(usdo));
        Pointer pL = Memory.allocateDirect(runtime_, Struct.size(l));
        usdo.useMemory(pUsdo);
        l.useMemory(pL);
        l.set(Struct.size(usdo));

        soem_.ecx_SDOread(context, index, idx, subidx, SoemOsal.FALSE, pL, pUsdo, SoemEtherCATType.EC_TIMEOUTRXM);
        if (context.ecaterror.get() > 0) {
            return soem_.ecx_elist2string(context);
        } else {
            try {
                SoemEtherCATType.ec_datatype edtype = (SoemEtherCATType.ec_datatype) EnumMapper.getInstance(SoemEtherCATType.ec_datatype.class).valueOf(dtype);
                switch (edtype) {
                    case ECT_BOOLEAN:
                        if (pUsdo.getByte(0) == SoemOsal.TRUE) {
                            hstr = new StringBuilder("TRUE");
                        } else {
                            hstr = new StringBuilder("FALSE");
                        }
                        break;
                    case ECT_INTEGER8:
                        hstr = new StringBuilder(String.format("0x%02X %d", pUsdo.getByte(0), pUsdo.getByte(0)));
                        break;
                    case ECT_INTEGER16:
                        hstr = new StringBuilder(String.format("0x%04X %d", pUsdo.getShort(0), pUsdo.getShort(0)));
                        break;
                    case ECT_INTEGER32:
                    case ECT_INTEGER24:
                        hstr = new StringBuilder(String.format("0x%08X %d", pUsdo.getInt(0), pUsdo.getInt(0)));
                        break;
                    case ECT_INTEGER64:
                        hstr = new StringBuilder(String.format("0x%016X %d", pUsdo.getLong(0), pUsdo.getLong(0)));
                        break;
                    case ECT_UNSIGNED8:
                        hstr = new StringBuilder(String.format("0x%02X %d", pUsdo.getShort(0), pUsdo.getShort(0)));
                        break;
                    case ECT_UNSIGNED16:
                        hstr = new StringBuilder(String.format("0x%04X %d", pUsdo.getInt(0), pUsdo.getInt(0)));
                        break;
                    case ECT_UNSIGNED32:
                    case ECT_UNSIGNED24:
                        hstr = new StringBuilder(String.format("0x%08X %d", pUsdo.getLong(0), pUsdo.getLong(0)));
                        break;
                    case ECT_UNSIGNED64:
                        hstr = new StringBuilder(String.format("0x%016X %d", pUsdo.getLongLong(0), pUsdo.getLongLong(0)));
                        break;
                    case ECT_REAL32:
                        hstr = new StringBuilder(String.format("%f", pUsdo.getFloat(0)));
                        break;
                    case ECT_REAL64:
                        hstr = new StringBuilder(String.format("%f", pUsdo.getDouble(0)));
                        break;
                    case ECT_BIT1:
                    case ECT_BIT2:
                    case ECT_BIT3:
                    case ECT_BIT4:
                    case ECT_BIT5:
                    case ECT_BIT6:
                    case ECT_BIT7:
                    case ECT_BIT8:
                        hstr = new StringBuilder(String.format("0x%X", pUsdo.getShort(0)));
                        break;
                    case ECT_VISIBLE_STRING:
                        hstr = new StringBuilder(usdo.get());
                        break;
                    case ECT_OCTET_STRING:
                        hstr = new StringBuilder();
                        for (int i = 0; i < l.get(); i++) {
                            hstr.append(String.format("0x%02X ", pUsdo.getByte(i)));
                        }
                        break;
                    default:
                        hstr = new StringBuilder("Unknown type");
                }
            } catch (IllegalArgumentException ex) {
                hstr = new StringBuilder("Unknown type");
            }
            return hstr.toString();
        }
    }

    private void si_sdo(SoemEtherCATMain.ecx_contextt context, int cnt, SlaveInfo.SDO sdo) {
        SlaveInfo.SDO.OD od;
        SlaveInfo.SDO.OD.OE oe;
        SoemEtherCATCoE.ec_ODlistt ODlist = new SoemEtherCATCoE.ec_ODlistt(runtime_);
        SoemEtherCATCoE.ec_OElistt OElist = new SoemEtherCATCoE.ec_OElistt(runtime_);
        int i, j;

        Pointer pODlist = Memory.allocateDirect(runtime_, Struct.size(ODlist));
        ODlist.useMemory(pODlist);
        ODlist.Entries.set(0);
        if (soem_.ecx_readODlist(context, cnt, ODlist) > 0) {
            sdo.ODlistEntries = ODlist.Entries.get();
            for (i = 0; i < ODlist.Entries.get(); i++) {
                od = sdo.newOD();
                soem_.ecx_readODdescription(context, i, ODlist);
                if (context.ecaterror.get() > 0) {
                    od.EcatError = soem_.ecx_elist2string(context);
                }
                od.Index = ODlist.Index[i].get();
                od.DataType = ODlist.DataType[i].get();
                od.ObjectCode = ODlist.ObjectCode[i].get();
                od.Name = ODlist.Name[i].toString();
                Pointer pOElist = Memory.allocateDirect(runtime_, Struct.size(OElist));
                OElist.useMemory(pOElist);
                soem_.ecx_readOE(context, i, ODlist, OElist);
                if (context.ecaterror.get() > 0) {
                    od.EcatError = soem_.ecx_elist2string(context);
                }
                for (j = 0; j < ODlist.MaxSub[i].get() + 1; j++) {
                    if ((OElist.DataType[j].get() > 0) && (OElist.BitLength[j].get() > 0)) {
                        oe = od.newOE();
                        oe.Index = j;
                        oe.DataType = OElist.DataType[j].get();
                        oe.BitLength = OElist.BitLength[j].get();
                        oe.ObjectAccess = OElist.ObjAccess[j].get();
                        oe.Name = OElist.Name[j].toString();
                        if ((OElist.ObjAccess[j].get() & 0x0007) > 0) {
                            oe.SDO = SDO2string(context, cnt, ODlist.Index[i].get(), j, OElist.DataType[j].get());
                        }
                    }
                }
            }
        } else {
            if (context.ecaterror.get() > 0) {
                sdo.EcatError = soem_.ecx_elist2string(context);
            }
        }
    }
}
