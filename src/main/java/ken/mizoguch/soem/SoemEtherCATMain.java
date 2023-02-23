/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import jnr.ffi.Struct;

/**
 *
 * @author mizoguch-ken
 */
public interface SoemEtherCATMain {

    /**
     * max. entries in EtherCAT error list
     */
    public static final int EC_MAXELIST = 64;
    /**
     * max. length of readable name in slavelist and Object Description List
     */
    public static final int EC_MAXNAME = 40;
    /**
     * max. number of slaves in array
     */
    public static final int EC_MAXSLAVE = 200;
    /**
     * max. number of groups
     */
    public static final int EC_MAXGROUP = 2;
    /**
     * max. number of IO segments per group
     */
    public static final int EC_MAXIOSEGMENTS = 64;
    /**
     * max. mailbox size
     */
    public static final int EC_MAXMBX = 1486;
    /**
     * max. eeprom PDO entries
     */
    public static final int EC_MAXEEPDO = 0x200;
    /**
     * max. SM used
     */
    public static final int EC_MAXSM = 8;
    /**
     * max. FMMU used
     */
    public static final int EC_MAXFMMU = 4;
    /**
     * max. Adapter
     */
    public static final int EC_MAXLEN_ADAPTERNAME = 128;
    /**
     * define maximum number of concurrent threads in mapping
     */
    public static final int EC_MAX_MAPT = 1;

    public class ec_adaptert extends Struct {

        public final String name;
        public final String desc;
        private final Pointer _next;

        public ec_adaptert(jnr.ffi.Runtime runtime) {
            super(runtime);

            name = new UTF8String(EC_MAXLEN_ADAPTERNAME);
            desc = new UTF8String(EC_MAXLEN_ADAPTERNAME);
            _next = new Pointer();
        }

        public ec_adaptert getNext(jnr.ffi.Runtime runtime) {
            ec_adaptert adapter = new ec_adaptert(runtime);
            jnr.ffi.Pointer pNext = _next.get();
            if (pNext == null) {
                return null;
            } else {
                adapter.useMemory(_next.get());
                return adapter;
            }
        }
    }

    /**
     * record for FMMU
     */
    public class ec_fmmut extends Struct {

        public final Unsigned32 LogStart;
        public final Unsigned16 LogLength;
        public final Unsigned8 LogStartbit;
        public final Unsigned8 LogEndbit;
        public final Unsigned16 PhysStart;
        public final Unsigned8 PhysStartBit;
        public final Unsigned8 FMMUtype;
        public final Unsigned8 FMMUactive;
        public final Unsigned8 unused1;
        public final Unsigned16 unused2;

        public ec_fmmut(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            LogStart = new Unsigned32();
            LogLength = new Unsigned16();
            LogStartbit = new Unsigned8();
            LogEndbit = new Unsigned8();
            PhysStart = new Unsigned16();
            PhysStartBit = new Unsigned8();
            FMMUtype = new Unsigned8();
            FMMUactive = new Unsigned8();
            unused1 = new Unsigned8();
            unused2 = new Unsigned16();
        }
    }

    /**
     * record for sync manager
     */
    public class ec_smt extends Struct {

        public final Unsigned16 StartAddr;
        public final Unsigned16 SMlength;
        public final Unsigned32 SMflags;

        public ec_smt(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            StartAddr = new Unsigned16();
            SMlength = new Unsigned16();
            SMflags = new Unsigned32();
        }
    }

    public class ec_state_status extends Struct {

        public final Unsigned16 State;
        public final Unsigned16 Unused;
        public final Unsigned16 ALstatuscode;

        public ec_state_status(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            State = new Unsigned16();
            Unused = new Unsigned16();
            ALstatuscode = new Unsigned16();
        }
    }

    public static final int ECT_MBXPROT_AOE = 0x0001;
    public static final int ECT_MBXPROT_EOE = 0x0002;
    public static final int ECT_MBXPROT_COE = 0x0004;
    public static final int ECT_MBXPROT_FOE = 0x0008;
    public static final int ECT_MBXPROT_SOE = 0x0010;
    public static final int ECT_MBXPROT_VOE = 0x0020;

    public static final int ECT_COEDET_SDO = 0x01;
    public static final int ECT_COEDET_SDOINFO = 0x02;
    public static final int ECT_COEDET_PDOASSIGN = 0x04;
    public static final int ECT_COEDET_PDOCONFIG = 0x08;
    public static final int ECT_COEDET_UPLOAD = 0x10;
    public static final int ECT_COEDET_SDOCA = 0x20;

    public static final int EC_SMENABLEMASK = 0xfffeffff;

    /**
     * for list of ethercat slaves detected
     */
    public class ec_slavet extends Struct {

        /**
         * state of slave
         */
        public final Unsigned16 state;
        /**
         * AL status code
         */
        public final Unsigned16 ALstatuscode;
        /**
         * Configured address
         */
        public final Unsigned16 configadr;
        /**
         * Alias address
         */
        public final Unsigned16 aliasadr;
        /**
         * Manufacturer from EEprom
         */
        public final Unsigned32 eep_man;
        /**
         * ID from EEprom
         */
        public final Unsigned32 eep_id;
        /**
         * revision from EEprom
         */
        public final Unsigned32 eep_rev;
        /**
         * Interface type
         */
        public final Unsigned16 Itype;
        /**
         * Device type
         */
        public final Unsigned16 Dtype;
        /**
         * output bits
         */
        public final Unsigned16 Obits;
        /**
         * output bytes, if Obits < 8 then Obytes = 0
         */
        public final Unsigned32 Obytes;
        /**
         * output pointer in IOmap buffer
         */
        public final Pointer outputs;
        /**
         * startbit in first output byte
         */
        public final Unsigned8 Ostartbit;
        /**
         * input bits
         */
        public final Unsigned16 Ibits;
        /**
         * input bytes, if Ibits < 8 then Ibytes = 0
         */
        public final Unsigned32 Ibytes;
        /**
         * input pointer in IOmap buffer
         */
        public final Pointer inputs;
        /**
         * startbit in first input byte
         */
        public final Unsigned8 Istartbit;
        /**
         * SM structure
         */
        public final ec_smt[] SM;
        /**
         * SM type 0=unused 1=MbxWr 2=MbxRd 3=Outputs 4=Inputs
         */
        public final Unsigned8[] SMtype;
        /**
         * FMMU structure
         */
        public final ec_fmmut[] FMMU;
        /**
         * FMMU0 function
         */
        public final Unsigned8 FMMU0func;
        /**
         * FMMU1 function
         */
        public final Unsigned8 FMMU1func;
        /**
         * FMMU2 function
         */
        public final Unsigned8 FMMU2func;
        /**
         * FMMU3 function
         */
        public final Unsigned8 FMMU3func;
        /**
         * length of write mailbox in bytes, if no mailbox then 0
         */
        public final Unsigned16 mbx_l;
        /**
         * mailbox write offset
         */
        public final Unsigned16 mbx_wo;
        /**
         * length of read mailbox in bytes
         */
        public final Unsigned16 mbx_rl;
        /**
         * mailbox read offset
         */
        public final Unsigned16 mbx_ro;
        /**
         * mailbox supported protocols
         */
        public final Unsigned16 mbx_proto;
        /**
         * Counter value of mailbox link layer protocol 1..7
         */
        public final Unsigned8 mbx_cnt;
        /**
         * has DC capability
         */
        public final Unsigned8 hasdc;
        /**
         * Physical type; Ebus, EtherNet combinations
         */
        public final Unsigned8 ptype;
        /**
         * topology: 1 to 3 links
         */
        public final Unsigned8 topology;
        /**
         * active ports bitmap : ....3210 , set if respective port is active *
         */
        public final Unsigned8 activeports;
        /**
         * consumed ports bitmap : ....3210, used for internal delay measurement
         * *
         */
        public final Unsigned8 consumedports;
        /**
         * slave number for parent, 0=master
         */
        public final Unsigned16 parent;
        /**
         * port number on parent this slave is connected to *
         */
        public final Unsigned8 parentport;
        /**
         * port number on this slave the parent is connected to *
         */
        public final Unsigned8 entryport;
        /**
         * DC receivetimes on port A
         */
        public final Signed32 DCrtA;
        /**
         * DC receivetimes on port B
         */
        public final Signed32 DCrtB;
        /**
         * DC receivetimes on port C
         */
        public final Signed32 DCrtC;
        /**
         * DC receivetimes on port D
         */
        public final Signed32 DCrtD;
        /**
         * propagation delay
         */
        public final Signed32 pdelay;
        /**
         * next DC slave
         */
        public final Unsigned16 DCnext;
        /**
         * previous DC slave
         */
        public final Unsigned16 DCprevious;
        /**
         * DC cycle time in ns
         */
        public final Signed32 DCcycle;
        /**
         * DC shift from clock modulus boundary
         */
        public final Signed32 DCshift;
        /**
         * DC sync activation, 0=off, 1=on
         */
        public final Unsigned8 DCactive;
        /**
         * link to config table
         */
        public final Unsigned16 configindex;
        /**
         * link to SII config
         */
        public final Unsigned16 SIIindex;
        /**
         * 1 = 8 bytes per read, 0 = 4 bytes per read
         */
        public final Unsigned8 eep_8byte;
        /**
         * 0 = eeprom to master , 1 = eeprom to PDI
         */
        public final Unsigned8 eep_pdi;
        /**
         * CoE details
         */
        public final Unsigned8 CoEdetails;
        /**
         * FoE details
         */
        public final Unsigned8 FoEdetails;
        /**
         * EoE details
         */
        public final Unsigned8 EoEdetails;
        /**
         * SoE details
         */
        public final Unsigned8 SoEdetails;
        /**
         * E-bus current
         */
        public final Signed16 Ebuscurrent;
        /**
         * if >0 block use of LRW in processdata
         */
        public final Unsigned8 blockLRW;
        /**
         * group
         */
        public final Unsigned8 group;
        /**
         * first unused FMMU
         */
        public final Unsigned8 FMMUunused;
        /**
         * Boolean for tracking whether the slave is (not) responding, not
         * used/set by the SOEM library
         */
        public final Unsigned8 islost;
        /**
         * registered configuration function PO->SO
         */
        public final SoemLibrary.CallBackPO2SOconfig PO2SOconfig;
        /**
         * registered configuration function PO->SO
         */
        public final SoemLibrary.CallBackPO2SOconfigx PO2SOconfigx;
        /**
         * readable name
         */
        public String name;

        public ec_slavet(jnr.ffi.Runtime runtime) {
            super(runtime);

            state = new Unsigned16();
            ALstatuscode = new Unsigned16();
            configadr = new Unsigned16();
            aliasadr = new Unsigned16();
            eep_man = new Unsigned32();
            eep_id = new Unsigned32();
            eep_rev = new Unsigned32();
            Itype = new Unsigned16();
            Dtype = new Unsigned16();
            Obits = new Unsigned16();
            Obytes = new Unsigned32();
            outputs = new Pointer();
            Ostartbit = new Unsigned8();
            Ibits = new Unsigned16();
            Ibytes = new Unsigned32();
            inputs = new Pointer();
            Istartbit = new Unsigned8();
            SM = super.array(new ec_smt[EC_MAXSM]);
            SMtype = super.array(new Unsigned8[EC_MAXSM]);
            FMMU = super.array(new ec_fmmut[EC_MAXFMMU]);
            FMMU0func = new Unsigned8();
            FMMU1func = new Unsigned8();
            FMMU2func = new Unsigned8();
            FMMU3func = new Unsigned8();
            mbx_l = new Unsigned16();
            mbx_wo = new Unsigned16();
            mbx_rl = new Unsigned16();
            mbx_ro = new Unsigned16();
            mbx_proto = new Unsigned16();
            mbx_cnt = new Unsigned8();
            hasdc = new Unsigned8();
            ptype = new Unsigned8();
            topology = new Unsigned8();
            activeports = new Unsigned8();
            consumedports = new Unsigned8();
            parent = new Unsigned16();
            parentport = new Unsigned8();
            entryport = new Unsigned8();
            DCrtA = new Signed32();
            DCrtB = new Signed32();
            DCrtC = new Signed32();
            DCrtD = new Signed32();
            pdelay = new Signed32();
            DCnext = new Unsigned16();
            DCprevious = new Unsigned16();
            DCcycle = new Signed32();
            DCshift = new Signed32();
            DCactive = new Unsigned8();
            configindex = new Unsigned16();
            SIIindex = new Unsigned16();
            eep_8byte = new Unsigned8();
            eep_pdi = new Unsigned8();
            CoEdetails = new Unsigned8();
            FoEdetails = new Unsigned8();
            EoEdetails = new Unsigned8();
            SoEdetails = new Unsigned8();
            Ebuscurrent = new Signed16();
            blockLRW = new Unsigned8();
            group = new Unsigned8();
            FMMUunused = new Unsigned8();
            islost = new Unsigned8();
            PO2SOconfig = inner(new SoemLibrary.CallBackPO2SOconfig(runtime));
            PO2SOconfigx = inner(new SoemLibrary.CallBackPO2SOconfigx(runtime));
            name = new UTF8String(EC_MAXNAME + 1);
        }
    }

    /**
     * for list of ethercat slave groups
     */
    public class ec_groupt extends Struct {

        /**
         * logical start address for this group
         */
        public final Unsigned32 logstartaddr;
        /**
         * output bytes, if Obits < 8 then Obytes = 0
         */
        public final Unsigned32 Obytes;
        /**
         * output pointer in IOmap buffer
         */
        public final Pointer outputs;
        /**
         * input bytes, if Ibits < 8 then Ibytes = 0
         */
        public final Unsigned32 Ibytes;
        /**
         * input pointer in IOmap buffer
         */
        public final Pointer inputs;
        /**
         * has DC capabillity
         */
        public final Unsigned8 hasdc;
        /**
         * next DC slave
         */
        public final Unsigned16 DCnext;
        /**
         * E-bus current
         */
        public final Signed16 Ebuscurrent;
        /**
         * if >0 block use of LRW in processdata
         */
        public final Unsigned8 blockLRW;
        /**
         * IO segegments used
         */
        public final Unsigned16 nsegments;
        /**
         * 1st input segment
         */
        public final Unsigned16 Isegment;
        /**
         * Offset in input segment
         */
        public final Unsigned16 Ioffset;
        /**
         * Expected workcounter outputs
         */
        public final Unsigned16 outputsWKC;
        /**
         * Expected workcounter inputs
         */
        public final Unsigned16 inputsWKC;
        /**
         * check slave states
         */
        public final Unsigned8 docheckstate;
        /**
         * IO segmentation list. Datagrams must not break SM in two.
         */
        public final Unsigned32[] IOsegment;

        public ec_groupt(jnr.ffi.Runtime runtime) {
            super(runtime);

            logstartaddr = new Unsigned32();
            Obytes = new Unsigned32();
            outputs = new Pointer();
            Ibytes = new Unsigned32();
            inputs = new Pointer();
            hasdc = new Unsigned8();
            DCnext = new Unsigned16();
            Ebuscurrent = new Signed16();
            blockLRW = new Unsigned8();
            nsegments = new Unsigned16();
            Isegment = new Unsigned16();
            Ioffset = new Unsigned16();
            outputsWKC = new Unsigned16();
            inputsWKC = new Unsigned16();
            docheckstate = new Unsigned8();
            IOsegment = super.array(new Unsigned32[EC_MAXIOSEGMENTS]);
        }
    }

    /**
     * SII FMMU structure
     */
    public class ec_eepromFMMUt extends Struct {

        public final Unsigned16 Startpos;
        public final Unsigned8 nFMMU;
        public final Unsigned8 FMMU0;
        public final Unsigned8 FMMU1;
        public final Unsigned8 FMMU2;
        public final Unsigned8 FMMU3;

        public ec_eepromFMMUt(jnr.ffi.Runtime runtime) {
            super(runtime);

            Startpos = new Unsigned16();
            nFMMU = new Unsigned8();
            FMMU0 = new Unsigned8();
            FMMU1 = new Unsigned8();
            FMMU2 = new Unsigned8();
            FMMU3 = new Unsigned8();
        }
    }

    /**
     * SII SM structure
     */
    public class ec_eepromSMt extends Struct {

        public final Unsigned16 Startpos;
        public final Unsigned8 nSM;
        public final Unsigned16 PhStart;
        public final Unsigned16 Plength;
        public final Unsigned8 Creg;
        public final Unsigned8 Sreg;
        /* dont care */
        public final Unsigned8 Activate;
        public final Unsigned8 PDIctrl;

        /* dont care */
        public ec_eepromSMt(jnr.ffi.Runtime runtime) {
            super(runtime);

            Startpos = new Unsigned16();
            nSM = new Unsigned8();
            PhStart = new Unsigned16();
            Plength = new Unsigned16();
            Creg = new Unsigned8();
            Sreg = new Unsigned8();
            Activate = new Unsigned8();
            PDIctrl = new Unsigned8();
        }
    }

    /**
     * record to store rxPDO and txPDO table from eeprom
     */
    public class ec_eepromPDOt extends Struct {

        public final Unsigned16 Startpos;
        public final Unsigned16 Length;
        public final Unsigned16 nPDO;
        public final Unsigned16[] Index;
        public final Unsigned16[] SyncM;
        public final Unsigned16[] BitSize;
        public final Unsigned16[] SMbitsize;

        public ec_eepromPDOt(jnr.ffi.Runtime runtime) {
            super(runtime);

            Startpos = new Unsigned16();
            Length = new Unsigned16();
            nPDO = new Unsigned16();
            Index = super.array(new Unsigned16[EC_MAXEEPDO]);
            SyncM = super.array(new Unsigned16[EC_MAXEEPDO]);
            BitSize = super.array(new Unsigned16[EC_MAXEEPDO]);
            SMbitsize = super.array(new Unsigned16[EC_MAXSM]);
        }
    }

    /**
     * mailbox buffer array
     */
//    public final SoemLibrary.Uint8[] ec_mbxbuft = new SoemLibrary.Uint8[EC_MAXMBX + 1];
    /**
     * standard ethercat mailbox header
     */
    public class ec_mbxheadert extends Struct {

        public final Unsigned16 length;
        public final Unsigned16 address;
        public final Unsigned8 priority;
        public final Unsigned8 mbxtype;

        public ec_mbxheadert(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            length = new Unsigned16();
            address = new Unsigned16();
            priority = new Unsigned8();
            mbxtype = new Unsigned8();
        }
    }

    /**
     * ALstatus and ALstatus code
     */
    public class ec_alstatust extends Struct {

        public final Unsigned16 alstatus;
        public final Unsigned16 unused;
        public final Unsigned16 alstatuscode;

        public ec_alstatust(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            alstatus = new Unsigned16();
            unused = new Unsigned16();
            alstatuscode = new Unsigned16();
        }
    }

    /**
     * stack structure to store segmented LRD/LWR/LRW constructs
     */
    public class ec_idxstackT extends Struct {

        public final Unsigned8 pushed;
        public final Unsigned8 pulled;
        public final Unsigned8[] idx;
        public final Pointer[] data;
        public final Unsigned16[] length;
        public final Unsigned16[] dcoffset;

        public ec_idxstackT(jnr.ffi.Runtime runtime) {
            super(runtime);

            pushed = new Unsigned8();
            pulled = new Unsigned8();
            idx = super.array(new Unsigned8[SoemEtherCATType.EC_MAXBUF]);
            data = super.array(new Pointer[SoemEtherCATType.EC_MAXBUF]);
            length = super.array(new Unsigned16[SoemEtherCATType.EC_MAXBUF]);
            dcoffset = super.array(new Unsigned16[SoemEtherCATType.EC_MAXBUF]);
        }
    }

    /**
     * ringbuf for error storage
     */
    public class ec_eringt extends Struct {

        public final Signed16 head;
        public final Signed16 tail;
        public final SoemEtherCATType.ec_errort[] Error;

        public ec_eringt(jnr.ffi.Runtime runtime) {
            super(runtime);

            head = new Signed16();
            tail = new Signed16();
            Error = super.array(new SoemEtherCATType.ec_errort[EC_MAXELIST + 1]);
        }
    }

    /**
     * SyncManager Communication Type structure for CA
     */
    public class ec_SMcommtypet extends Struct {

        public final Unsigned8 n;
        public final Unsigned8 nu1;
        public final Unsigned8[] SMtype;

        public ec_SMcommtypet(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            n = new Unsigned8();
            nu1 = new Unsigned8();
            SMtype = super.array(new Unsigned8[EC_MAXSM]);
        }
    }

    /**
     * SDO assign structure for CA
     */
    public class ec_PDOassignt extends Struct {

        public final Unsigned8 n;
        public final Unsigned8 nu1;
        public final Unsigned16[] index;

        public ec_PDOassignt(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            n = new Unsigned8();
            nu1 = new Unsigned8();
            index = super.array(new Unsigned16[256]);
        }
    }

    /**
     * SDO description structure for CA
     */
    public class ec_PDOdesct extends Struct {

        public final Unsigned8 n;
        public final Unsigned8 nu1;
        public final Unsigned32[] PDO;

        public ec_PDOdesct(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            n = new Unsigned8();
            nu1 = new Unsigned8();
            PDO = super.array(new Unsigned32[256]);
        }
    }

    /**
     * Context structure , referenced by all ecx functions
     */
    public class ecx_contextt extends Struct {

        /**
         * port reference, may include red_port
         */
        public final Pointer _port;
        /**
         * slavelist reference
         */
        private final Pointer _slavelist;
        public final ec_slavet[] slavelist;
        /**
         * number of slaves found in configuration
         */
        private final Pointer _slavecount;
        public final SoemLibrary.Int32 slavecount;
        /**
         * maximum number of slaves allowed in slavelist
         */
        public final Signed32 maxslave;
        /**
         * grouplist reference
         */
        private final Pointer _grouplist;
        public final ec_groupt[] grouplist;
        /**
         * maximum number of groups allowed in grouplist
         */
        public final Signed32 maxgroup;
        /**
         * internal, reference to eeprom cache buffer
         */
        private final Pointer _esibuf;
        public final SoemLibrary.Uint8[] esibuf;
        /**
         * internal, reference to eeprom cache map
         */
        private final Pointer _esimap;
        public final SoemLibrary.Uint32[] esimap;
        /**
         * internal, current slave for eeprom cache
         */
        public final Unsigned16 esislave;
        /**
         * internal, reference to error list
         */
        private final Pointer _elist;
        public final ec_eringt elist;
        /**
         * internal, reference to processdata stack buffer info
         */
        private final Pointer _idxstack;
        public final ec_idxstackT idxstack;
        /**
         * reference to ecaterror state
         */
        private final Pointer _ecaterror;
        public final SoemLibrary.Bool ecaterror;
        /**
         * reference to last DC time from slaves
         */
        private final Pointer _DCtime;
        public final SoemLibrary.Int64 DCtime;
        /**
         * internal, SM buffer
         */
        private final Pointer _SMcommtype;
        public final ec_SMcommtypet[] SMcommtype;
        /**
         * internal, PDO assign list
         */
        private final Pointer _PDOassign;
        public final ec_PDOassignt[] PDOassign;
        /**
         * internal, PDO description list
         */
        private final Pointer _PDOdesc;
        public final ec_PDOdesct[] PDOdesc;
        /**
         * internal, SM list from eeprom
         */
        private final Pointer _eepSM;
        public final ec_eepromSMt eepSM;
        /**
         * internal, FMMU list from eeprom
         */
        private final Pointer _eepFMMU;
        public final ec_eepromFMMUt eepFMMU;
        /**
         * registered FoE hook
         */
        public final SoemLibrary.CallBackFOEhook FOEhook;
        /**
         * registered EoE hook
         */
        public final SoemLibrary.CallBackEOEhook EOEhook;
        /**
         * flag to control legacy automatic state change or manual state change
         */
        public final Signed32 manualstatechange;
        /**
         * userdata, promotes application configuration esp. in EC_VER2 with
         * multiple ec_context instances. Note: userdata memory is managed by
         * application, not SOEM
         */
        public final Pointer userdata;

        public ecx_contextt(jnr.ffi.Runtime runtime) {
            super(runtime);

            int i;
            _port = new Pointer();
            _slavelist = new Pointer();
            slavelist = new ec_slavet[EC_MAXSLAVE];
            for (i = 0; i < slavelist.length; i++) {
                slavelist[i] = new ec_slavet(runtime);
            }
            _slavecount = new Pointer();
            slavecount = new SoemLibrary.Int32(getRuntime());
            maxslave = new Signed32();
            _grouplist = new Pointer();
            grouplist = new ec_groupt[EC_MAXGROUP];
            for (i = 0; i < grouplist.length; i++) {
                grouplist[i] = new ec_groupt(runtime);
            }
            maxgroup = new Signed32();
            _esibuf = new Pointer();
            esibuf = new SoemLibrary.Uint8[SoemEtherCATType.EC_MAXEEPBUF];
            for (i = 0; i < esibuf.length; i++) {
                esibuf[i] = new SoemLibrary.Uint8(runtime);
            }
            _esimap = new Pointer();
            esimap = new SoemLibrary.Uint32[SoemEtherCATType.EC_MAXEEPBITMAP];
            for (i = 0; i < esimap.length; i++) {
                esimap[i] = new SoemLibrary.Uint32(runtime);
            }
            esislave = new Unsigned16();
            _elist = new Pointer();
            elist = new ec_eringt(getRuntime());
            _idxstack = new Pointer();
            idxstack = new ec_idxstackT(getRuntime());
            _ecaterror = new Pointer();
            ecaterror = new SoemLibrary.Bool(getRuntime());
            _DCtime = new Pointer();
            DCtime = new SoemLibrary.Int64(getRuntime());
            _SMcommtype = new Pointer();
            SMcommtype = new ec_SMcommtypet[EC_MAX_MAPT];
            for (i = 0; i < SMcommtype.length; i++) {
                SMcommtype[i] = new ec_SMcommtypet(runtime);
            }
            _PDOassign = new Pointer();
            PDOassign = new ec_PDOassignt[EC_MAX_MAPT];
            for (i = 0; i < PDOassign.length; i++) {
                PDOassign[i] = new ec_PDOassignt(runtime);
            }
            _PDOdesc = new Pointer();
            PDOdesc = new ec_PDOdesct[EC_MAX_MAPT];
            for (i = 0; i < PDOdesc.length; i++) {
                PDOdesc[i] = new ec_PDOdesct(runtime);
            }
            _eepSM = new Pointer();
            eepSM = new ec_eepromSMt(getRuntime());
            _eepFMMU = new Pointer();
            eepFMMU = new ec_eepromFMMUt(getRuntime());
            FOEhook = inner(new SoemLibrary.CallBackFOEhook(runtime));
            EOEhook = inner(new SoemLibrary.CallBackEOEhook(runtime));
            manualstatechange = new Signed32();
            userdata = new Pointer();
        }

        public ecx_contextt register() {
            int alignment, i;
            long offset, size;

            offset = 0;
            for (i = 0; i < slavelist.length; i++) {
                alignment = Struct.alignment(slavelist[i]);
                size = (long) (Math.ceil((double) Struct.size(slavelist[i]) / alignment) * alignment);
                slavelist[i].useMemory(_slavelist.get().slice(offset, size));
                offset += size;
            }
            slavecount.useMemory(_slavecount.get());
            offset = 0;
            for (i = 0; i < grouplist.length; i++) {
                alignment = Struct.alignment(grouplist[i]);
                size = (long) (Math.ceil((double) Struct.size(grouplist[i]) / alignment) * alignment);
                grouplist[i].useMemory(_grouplist.get().slice(offset, size));
                offset += size;
            }
            offset = 0;
            for (i = 0; i < esibuf.length; i++) {
                alignment = Struct.alignment(esibuf[i]);
                size = (long) (Math.ceil((double) Struct.size(esibuf[i]) / alignment) * alignment);
                esibuf[i].useMemory(_esibuf.get().slice(offset, size));
                offset += size;
            }
            offset = 0;
            for (i = 0; i < esimap.length; i++) {
                alignment = Struct.alignment(esimap[i]);
                size = (long) (Math.ceil((double) Struct.size(esimap[i]) / alignment) * alignment);
                esimap[i].useMemory(_esimap.get().slice(offset, size));
                offset += size;
            }
            elist.useMemory(_elist.get());
            idxstack.useMemory(_idxstack.get());
            ecaterror.useMemory(_ecaterror.get());
            DCtime.useMemory(_DCtime.get());
            offset = 0;
            for (i = 0; i < SMcommtype.length; i++) {
                alignment = Struct.alignment(SMcommtype[i]);
                size = (long) (Math.ceil((double) Struct.size(SMcommtype[i]) / alignment) * alignment);
                SMcommtype[i].useMemory(_SMcommtype.get().slice(offset, size));
                offset += size;
            }
            offset = 0;
            for (i = 0; i < PDOassign.length; i++) {
                alignment = Struct.alignment(PDOassign[i]);
                size = (long) (Math.ceil((double) Struct.size(PDOassign[i]) / alignment) * alignment);
                PDOassign[i].useMemory(_PDOassign.get().slice(offset, size));
                offset += size;
            }
            offset = 0;
            for (i = 0; i < PDOdesc.length; i++) {
                alignment = Struct.alignment(PDOdesc[i]);
                size = (long) (Math.ceil((double) Struct.size(PDOdesc[i]) / alignment) * alignment);
                PDOdesc[i].useMemory(_PDOdesc.get().slice(offset, size));
                offset += size;
            }
            eepSM.useMemory(_eepSM.get());
            eepFMMU.useMemory(_eepFMMU.get());
            return this;
        }
    }
}
