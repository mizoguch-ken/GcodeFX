/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import jnr.ffi.Struct;
import jnr.ffi.Union;
import jnr.ffi.util.EnumMapper;

/**
 *
 * @author mizoguch-ken
 */
public interface SoemEtherCATType {

    /**
     * return value no frame returned
     */
    public static final int EC_NOFRAME = -1;
    /**
     * return value unknown frame received
     */
    public static final int EC_OTHERFRAME = -2;
    /**
     * return value general error
     */
    public static final int EC_ERROR = -3;
    /**
     * return value too many slaves
     */
    public static final int EC_SLAVECOUNTEXCEEDED = -4;
    /**
     * return value request timeout
     */
    public static final int EC_TIMEOUT = -5;
    /**
     * maximum EtherCAT frame length in bytes
     */
    public static final int EC_MAXECATFRAME = 1518;
    /**
     * maximum EtherCAT LRW frame length in bytes
     */
    /* MTU - Ethernet header - length - datagram header - WCK - FCS */
    public static final int EC_MAXLRWDATA = (EC_MAXECATFRAME - 14 - 2 - 10 - 2 - 4);
    /**
     * size of DC datagram used in first LRW frame
     */
    public static final int EC_FIRSTDCDATAGRAM = 20;
    /**
     * standard frame buffer size in bytes
     */
    public static final int EC_BUFSIZE = EC_MAXECATFRAME;
    /**
     * datagram type EtherCAT
     */
    public static final int EC_ECATTYPE = 0x1000;
    /**
     * number of frame buffers per channel (tx, rx1 rx2)
     */
    public static final int EC_MAXBUF = 16;
    /**
     * timeout value in us for tx frame to return to rx
     */
    public static final int EC_TIMEOUTRET = 2000;
    /**
     * timeout value in us for safe data transfer, max. triple retry
     */
    public static final int EC_TIMEOUTRET3 = (EC_TIMEOUTRET * 3);
    /**
     * timeout value in us for return "safe" variant (f.e. wireless)
     */
    public static final int EC_TIMEOUTSAFE = 20000;
    /**
     * timeout value in us for EEPROM access
     */
    public static final int EC_TIMEOUTEEP = 20000;
    /**
     * timeout value in us for tx mailbox cycle
     */
    public static final int EC_TIMEOUTTXM = 20000;
    /**
     * timeout value in us for rx mailbox cycle
     */
    public static final int EC_TIMEOUTRXM = 700000;
    /**
     * timeout value in us for check statechange
     */
    public static final int EC_TIMEOUTSTATE = 2000000;
    /**
     * size of EEPROM bitmap cache
     */
    public static final int EC_MAXEEPBITMAP = 128;
    /**
     * size of EEPROM cache buffer
     */
    public static final int EC_MAXEEPBUF = EC_MAXEEPBITMAP << 5;
    /**
     * default number of retries if wkc <= 0
     */
    public static final int EC_DEFAULTRETRIES = 3;
    /**
     * default group size in 2^x
     */
    public static final int EC_LOGGROUPOFFSET = 16;

    /**
     * definition for frame buffers
     */
//    typedef uint8 ec_bufT[EC_BUFSIZE];
    /**
     * ethernet header definition
     */
    public class ec_etherheadert extends Struct {

        /**
         * destination MAC
         */
        public Unsigned16 da0, da1, da2;
        /**
         * source MAC
         */
        public Unsigned16 sa0, sa1, sa2;
        /**
         * ethernet type
         */
        public Unsigned16 etype;

        /**
         *
         * @param runtime
         */
        public ec_etherheadert(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            da0 = new Unsigned16();
            da1 = new Unsigned16();
            da2 = new Unsigned16();
            sa0 = new Unsigned16();
            sa1 = new Unsigned16();
            sa2 = new Unsigned16();
            etype = new Unsigned16();
        }
    }

    /**
     * ethernet header size
     */
//    #define ETH_HEADERSIZE      sizeof(ec_etherheadert)
    /**
     * EtherCAT datagram header definition
     */
    public class ec_comt extends Struct {

        /**
         * length of EtherCAT datagram
         */
        public Unsigned16 elength;
        /**
         * EtherCAT command, see ec_cmdtype
         */
        public Unsigned8 command;
        /**
         * index, used in SOEM for Tx to Rx recombination
         */
        public Unsigned8 index;
        /**
         * ADP
         */
        public Unsigned16 ADP;
        /**
         * ADO
         */
        public Unsigned16 ADO;
        /**
         * length of data portion in datagram
         */
        public Unsigned16 dlength;
        /**
         * interrupt, currently unused
         */
        public Unsigned16 irpt;

        public ec_comt(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            elength = new Unsigned16();
            command = new Unsigned8();
            index = new Unsigned8();
            ADP = new Unsigned16();
            ADO = new Unsigned16();
            dlength = new Unsigned16();
            irpt = new Unsigned16();
        }
    }

    /**
     * EtherCAT header size
     */
    public static final int EC_HEADERSIZE = 12;
    /**
     * size of ec_comt.elength item in EtherCAT header
     */
    public static final int EC_ELENGTHSIZE = 2;
    /**
     * offset position of command in EtherCAT header
     */
    public static final int EC_CMDOFFSET = EC_ELENGTHSIZE;
    /**
     * size of workcounter item in EtherCAT datagram
     */
    public static final int EC_WKCSIZE = 2;
    /**
     * definition of datagram follows bit in ec_comt.dlength
     */
    public static final int EC_DATAGRAMFOLLOWS = (1 << 15);

    /**
     * Possible error codes returned.
     */
    public enum ec_err implements EnumMapper.IntegerEnum {
        /**
         * No error
         */
        EC_ERR_OK(0),
        /**
         * Library already initialized.
         */
        EC_ERR_ALREADY_INITIALIZED(1),
        /**
         * Library not initialized.
         */
        EC_ERR_NOT_INITIALIZED(2),
        /**
         * Timeout occurred during execution of the function.
         */
        EC_ERR_TIMEOUT(3),
        /**
         * No slaves were found.
         */
        EC_ERR_NO_SLAVES(4),
        /**
         * Function failed.
         */
        EC_ERR_NOK(5);

        private final int value_;

        private ec_err(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * Possible EtherCAT slave states
     */
    public enum ec_state implements EnumMapper.IntegerEnum {
        /**
         * No valid state.
         */
        EC_STATE_NONE(0x00),
        /**
         * Init state
         */
        EC_STATE_INIT(0x01),
        /**
         * Pre-operational.
         */
        EC_STATE_PRE_OP(0x02),
        /**
         * Boot state
         */
        EC_STATE_BOOT(0x03),
        /**
         * Safe-operational.
         */
        EC_STATE_SAFE_OP(0x04),
        /**
         * Operational
         */
        EC_STATE_OPERATIONAL(0x08),
        /**
         * Error or ACK error
         */
        EC_STATE_ACK(0x10),
        EC_STATE_ERROR(0x10);

        private final int value_;

        private ec_state(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * Possible buffer states
     */
    public enum ec_bufstate implements EnumMapper.IntegerEnum {
        /**
         * Empty
         */
        EC_BUF_EMPTY(0x00),
        /**
         * Allocated, but not filled
         */
        EC_BUF_ALLOC(0x01),
        /**
         * Transmitted
         */
        EC_BUF_TX(0x02),
        /**
         * Received, but not consumed
         */
        EC_BUF_RCVD(0x03),
        /**
         * Cycle completed
         */
        EC_BUF_COMPLETE(0x04);

        private final int value_;

        private ec_bufstate(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * Ethercat data types
     */
    public enum ec_datatype implements EnumMapper.IntegerEnum {
        ECT_BOOLEAN(0x0001),
        ECT_INTEGER8(0x0002),
        ECT_INTEGER16(0x0003),
        ECT_INTEGER32(0x0004),
        ECT_UNSIGNED8(0x0005),
        ECT_UNSIGNED16(0x0006),
        ECT_UNSIGNED32(0x0007),
        ECT_REAL32(0x0008),
        ECT_VISIBLE_STRING(0x0009),
        ECT_OCTET_STRING(0x000A),
        ECT_UNICODE_STRING(0x000B),
        ECT_TIME_OF_DAY(0x000C),
        ECT_TIME_DIFFERENCE(0x000D),
        ECT_DOMAIN(0x000F),
        ECT_INTEGER24(0x0010),
        ECT_REAL64(0x0011),
        ECT_INTEGER64(0x0015),
        ECT_UNSIGNED24(0x0016),
        ECT_UNSIGNED64(0x001B),
        ECT_BIT1(0x0030),
        ECT_BIT2(0x0031),
        ECT_BIT3(0x0032),
        ECT_BIT4(0x0033),
        ECT_BIT5(0x0034),
        ECT_BIT6(0x0035),
        ECT_BIT7(0x0036),
        ECT_BIT8(0x0037);

        private final int value_;

        private ec_datatype(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * Ethercat command types
     */
    public enum ec_cmdtype implements EnumMapper.IntegerEnum {
        /**
         * No operation
         */
        EC_CMD_NOP(0x00),
        /**
         * Auto Increment Read
         */
        EC_CMD_APRD(0x01),
        /**
         * Auto Increment Write
         */
        EC_CMD_APWR(0x02),
        /**
         * Auto Increment Read Write
         */
        EC_CMD_APRW(0x03),
        /**
         * Configured Address Read
         */
        EC_CMD_FPRD(0x04),
        /**
         * Configured Address Write
         */
        EC_CMD_FPWR(0x05),
        /**
         * Configured Address Read Write
         */
        EC_CMD_FPRW(0x06),
        /**
         * Broadcast Read
         */
        EC_CMD_BRD(0x07),
        /**
         * Broadcast Write
         */
        EC_CMD_BWR(0x08),
        /**
         * Broadcast Read Write
         */
        EC_CMD_BRW(0x09),
        /**
         * Logical Memory Read
         */
        EC_CMD_LRD(0x0a),
        /**
         * Logical Memory Write
         */
        EC_CMD_LWR(0x0b),
        /**
         * Logical Memory Read Write
         */
        EC_CMD_LRW(0x0c),
        /**
         * Auto Increment Read Multiple Write
         */
        EC_CMD_ARMW(0x0d),
        /**
         * Configured Read Multiple Write
         */
        EC_CMD_FRMW(0x0e);
        /**
         * Reserved
         */

        private final int value_;

        private ec_cmdtype(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * Ethercat EEprom command types
     */
    public enum ec_ecmdtype implements EnumMapper.IntegerEnum {
        /**
         * No operation
         */
        EC_ECMD_NOP(0x0000),
        /**
         * Read
         */
        EC_ECMD_READ(0x0100),
        /**
         * Write
         */
        EC_ECMD_WRITE(0x0201),
        /**
         * Reload
         */
        EC_ECMD_RELOAD(0x0300);

        private final int value_;

        private ec_ecmdtype(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    /**
     * EEprom state machine read size
     */
    public static final int EC_ESTAT_R64 = 0x0040;
    /**
     * EEprom state machine busy flag
     */
    public static final int EC_ESTAT_BUSY = 0x8000;
    /**
     * EEprom state machine error flag mask
     */
    public static final int EC_ESTAT_EMASK = 0x7800;
    /**
     * EEprom state machine error acknowledge
     */
    public static final int EC_ESTAT_NACK = 0x2000;

    /* Ethercat SSI (Slave Information Interface) */
    /**
     * Start address SII sections in Eeprom
     */
    public static final int ECT_SII_START = 0x0040;

    /**
     * SII category strings
     */
    public static final int ECT_SII_STRING = 10;
    /**
     * SII category general
     */
    public static final int ECT_SII_GENERAL = 30;
    /**
     * SII category FMMU
     */
    public static final int ECT_SII_FMMU = 40;
    /**
     * SII category SM
     */
    public static final int ECT_SII_SM = 41;
    /**
     * SII category PDO
     */
    public static final int ECT_SII_PDO = 50;

    /**
     * Item offsets in SII general section
     */
    public static final int ECT_SII_MANUF = 0x0008;
    public static final int ECT_SII_ID = 0x000a;
    public static final int ECT_SII_REV = 0x000c;
    public static final int ECT_SII_BOOTRXMBX = 0x0014;
    public static final int ECT_SII_BOOTTXMBX = 0x0016;
    public static final int ECT_SII_MBXSIZE = 0x0019;
    public static final int ECT_SII_TXMBXADR = 0x001a;
    public static final int ECT_SII_RXMBXADR = 0x0018;
    public static final int ECT_SII_MBXPROTO = 0x001c;

    /**
     * Mailbox types definitions
     */
    /**
     * Error mailbox type
     */
    public static final int ECT_MBXT_ERR = 0x00;
    /**
     * ADS over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_AOE = 0x01;
    /**
     * Ethernet over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_EOE = 0x02;
    /**
     * CANopen over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_COE = 0x03;
    /**
     * File over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_FOE = 0x04;
    /**
     * Servo over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_SOE = 0x05;
    /**
     * Vendor over EtherCAT mailbox type
     */
    public static final int ECT_MBXT_VOE = 0x0f;

    /**
     * CoE mailbox types
     */
    public static final int ECT_COES_EMERGENCY = 0x01;
    public static final int ECT_COES_SDOREQ = 0x02;
    public static final int ECT_COES_SDORES = 0x03;
    public static final int ECT_COES_TXPDO = 0x04;
    public static final int ECT_COES_RXPDO = 0x05;
    public static final int ECT_COES_TXPDO_RR = 0x06;
    public static final int ECT_COES_RXPDO_RR = 0x07;
    public static final int ECT_COES_SDOINFO = 0x08;

    /**
     * CoE SDO commands
     */
    public static final int ECT_SDO_DOWN_INIT = 0x21;
    public static final int ECT_SDO_DOWN_EXP = 0x23;
    public static final int ECT_SDO_DOWN_INIT_CA = 0x31;
    public static final int ECT_SDO_UP_REQ = 0x40;
    public static final int ECT_SDO_UP_REQ_CA = 0x50;
    public static final int ECT_SDO_SEG_UP_REQ = 0x60;
    public static final int ECT_SDO_ABORT = 0x80;

    /**
     * CoE Object Description commands
     */
    public static final int ECT_GET_ODLIST_REQ = 0x01;
    public static final int ECT_GET_ODLIST_RES = 0x02;
    public static final int ECT_GET_OD_REQ = 0x03;
    public static final int ECT_GET_OD_RES = 0x04;
    public static final int ECT_GET_OE_REQ = 0x05;
    public static final int ECT_GET_OE_RES = 0x06;
    public static final int ECT_SDOINFO_ERROR = 0x07;

    /**
     * FoE opcodes
     */
    public static final int ECT_FOE_READ = 0x01;
    public static final int ECT_FOE_WRITE = 0x02;
    public static final int ECT_FOE_DATA = 0x03;
    public static final int ECT_FOE_ACK = 0x04;
    public static final int ECT_FOE_ERROR = 0x05;
    public static final int ECT_FOE_BUSY = 0x06;

    /**
     * SoE opcodes
     */
    public static final int ECT_SOE_READREQ = 0x01;
    public static final int ECT_SOE_READRES = 0x02;
    public static final int ECT_SOE_WRITEREQ = 0x03;
    public static final int ECT_SOE_WRITERES = 0x04;
    public static final int ECT_SOE_NOTIFICATION = 0x05;
    public static final int ECT_SOE_EMERGENCY = 0x06;

    /**
     * Ethercat registers
     */
    public static final int ECT_REG_TYPE = 0x0000;
    public static final int ECT_REG_PORTDES = 0x0007;
    public static final int ECT_REG_ESCSUP = 0x0008;
    public static final int ECT_REG_STADR = 0x0010;
    public static final int ECT_REG_ALIAS = 0x0012;
    public static final int ECT_REG_DLCTL = 0x0100;
    public static final int ECT_REG_DLPORT = 0x0101;
    public static final int ECT_REG_DLALIAS = 0x0103;
    public static final int ECT_REG_DLSTAT = 0x0110;
    public static final int ECT_REG_ALCTL = 0x0120;
    public static final int ECT_REG_ALSTAT = 0x0130;
    public static final int ECT_REG_ALSTATCODE = 0x0134;
    public static final int ECT_REG_PDICTL = 0x0140;
    public static final int ECT_REG_IRQMASK = 0x0200;
    public static final int ECT_REG_RXERR = 0x0300;
    public static final int ECT_REG_FRXERR = 0x0308;
    public static final int ECT_REG_EPUECNT = 0x030C;
    public static final int ECT_REG_PECNT = 0x030D;
    public static final int ECT_REG_PECODE = 0x030E;
    public static final int ECT_REG_LLCNT = 0x0310;
    public static final int ECT_REG_WDCNT = 0x0442;
    public static final int ECT_REG_EEPCFG = 0x0500;
    public static final int ECT_REG_EEPCTL = 0x0502;
    public static final int ECT_REG_EEPSTAT = 0x0502;
    public static final int ECT_REG_EEPADR = 0x0504;
    public static final int ECT_REG_EEPDAT = 0x0508;
    public static final int ECT_REG_FMMU0 = 0x0600;
    public static final int ECT_REG_FMMU1 = ECT_REG_FMMU0 + 0x10;
    public static final int ECT_REG_FMMU2 = ECT_REG_FMMU1 + 0x10;
    public static final int ECT_REG_FMMU3 = ECT_REG_FMMU2 + 0x10;
    public static final int ECT_REG_SM0 = 0x0800;
    public static final int ECT_REG_SM1 = ECT_REG_SM0 + 0x08;
    public static final int ECT_REG_SM2 = ECT_REG_SM1 + 0x08;
    public static final int ECT_REG_SM3 = ECT_REG_SM2 + 0x08;
    public static final int ECT_REG_SM0STAT = ECT_REG_SM0 + 0x05;
    public static final int ECT_REG_SM1STAT = ECT_REG_SM1 + 0x05;
    public static final int ECT_REG_SM1ACT = ECT_REG_SM1 + 0x06;
    public static final int ECT_REG_SM1CONTR = ECT_REG_SM1 + 0x07;
    public static final int ECT_REG_DCTIME0 = 0x0900;
    public static final int ECT_REG_DCTIME1 = 0x0904;
    public static final int ECT_REG_DCTIME2 = 0x0908;
    public static final int ECT_REG_DCTIME3 = 0x090C;
    public static final int ECT_REG_DCSYSTIME = 0x0910;
    public static final int ECT_REG_DCSOF = 0x0918;
    public static final int ECT_REG_DCSYSOFFSET = 0x0920;
    public static final int ECT_REG_DCSYSDELAY = 0x0928;
    public static final int ECT_REG_DCSYSDIFF = 0x092C;
    public static final int ECT_REG_DCSPEEDCNT = 0x0930;
    public static final int ECT_REG_DCTIMEFILT = 0x0934;
    public static final int ECT_REG_DCCUC = 0x0980;
    public static final int ECT_REG_DCSYNCACT = 0x0981;
    public static final int ECT_REG_DCSTART0 = 0x0990;
    public static final int ECT_REG_DCCYCLE0 = 0x09A0;
    public static final int ECT_REG_DCCYCLE1 = 0x09A4;

    /**
     * standard SDO Sync Manager Communication Type
     */
    public static final int ECT_SDO_SMCOMMTYPE = 0x1c00;
    /**
     * standard SDO PDO assignment
     */
    public static final int ECT_SDO_PDOASSIGN = 0x1c10;
    /**
     * standard SDO RxPDO assignment
     */
    public static final int ECT_SDO_RXPDOASSIGN = 0x1c12;
    /**
     * standard SDO TxPDO assignment
     */
    public static final int ECT_SDO_TXPDOASSIGN = 0x1c13;

    /**
     * Ethercat packet type
     */
    public static final int ETH_P_ECAT = 0x88A4;

    public enum ec_err_type implements EnumMapper.IntegerEnum {
        EC_ERR_TYPE_SDO_ERROR(0),
        EC_ERR_TYPE_EMERGENCY(1),
        EC_ERR_TYPE_PACKET_ERROR(3),
        EC_ERR_TYPE_SDOINFO_ERROR(4),
        EC_ERR_TYPE_FOE_ERROR(5),
        EC_ERR_TYPE_FOE_BUF2SMALL(6),
        EC_ERR_TYPE_FOE_PACKETNUMBER(7),
        EC_ERR_TYPE_SOE_ERROR(8),
        EC_ERR_TYPE_MBX_ERROR(9),
        EC_ERR_TYPE_FOE_FILE_NOTFOUND(10);

        private final int value_;

        private ec_err_type(int value) {
            value_ = value;
        }

        @Override
        public int intValue() {
            return value_;
        }
    }

    public class ec_errort_Union_Struct extends Struct {

        public Unsigned16 ErrorCode;
        public Unsigned8 ErrorReg;
        public Unsigned8 b1;
        public Unsigned16 w1;
        public Unsigned16 w2;

        public ec_errort_Union_Struct(jnr.ffi.Runtime runtime) {
            super(runtime);

            ErrorCode = new Unsigned16();
            ErrorReg = new Unsigned8();
            b1 = new Unsigned8();
            w1 = new Unsigned16();
            w2 = new Unsigned16();
        }
    }

    public class ec_errort_Union extends Union {

        /**
         * General abortcode
         */
        public Signed32 AbortCode;
        public ec_errort_Union_Struct struct;

        /**
         * Specific error for Emergency mailbox
         *
         * @param runtime
         */
        public ec_errort_Union(jnr.ffi.Runtime runtime) {
            super(runtime);

            AbortCode = new Signed32();
            struct = inner(new ec_errort_Union_Struct(runtime));
        }
    }

    public class ec_errort extends Struct {

        /**
         * Time at which the error was generated.
         */
        public SoemOsal.ec_timet Time;
        /**
         * Signal bit, error set but not read
         */
        public Boolean Signal;
        /**
         * Slave number that generated the error
         */
        public Unsigned16 Slave;
        /**
         * CoE SDO index that generated the error
         */
        public Unsigned16 Index;
        /**
         * CoE SDO subindex that generated the error
         */
        public Unsigned8 SubIdx;
        /**
         * Type of error
         */
        public Signed32 Etype;
        public ec_errort_Union union;

        public ec_errort(jnr.ffi.Runtime runtime) {
            super(runtime);

            Time = inner(new SoemOsal.ec_timet(runtime));
            Signal = new Boolean();
            Slave = new Unsigned16();
            Index = new Unsigned16();
            SubIdx = new Unsigned8();
            Etype = new Signed32();
            union = inner(new ec_errort_Union(runtime));
        }
    }

}
