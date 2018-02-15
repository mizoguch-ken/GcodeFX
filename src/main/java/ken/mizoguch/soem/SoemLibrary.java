/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Delegate;
import jnr.ffi.types.int16_t;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.u_int16_t;
import jnr.ffi.types.u_int32_t;
import jnr.ffi.types.u_int64_t;
import jnr.ffi.types.u_int8_t;

/**
 *
 * @author mizoguch-ken
 *
 */
public interface SoemLibrary {

    /*
    * Windows
    *  [CMakeLists.txt] change
    *  if(WIN32)
    *     set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON)
    *     set(OS "win32")
    *     include_directories(oshw/win32/wpcap/Include)
    *     link_directories(${CMAKE_SOURCE_DIR}/oshw/win32/wpcap/Lib)     # x86
    *     link_directories(${CMAKE_SOURCE_DIR}/oshw/win32/wpcap/Lib/x64) # x64
    *
    * All Platform
    *  [CMakeLists.txt] change
    * -add_library(soem STATIC ${SOEM_SOURCES} ${OSAL_SOURCES} ${OSHW_SOURCES} ${OSHW_ARCHSOURCES})
    * +add_library(soem SHARED ${SOEM_SOURCES} ${OSAL_SOURCES} ${OSHW_SOURCES} ${OSHW_ARCHSOURCES})
    *
    * All Platform
    *  [soem/ethercattype.h] change
    * -#define EC_VER1
    * +//#define EC_VER1
    *
    * All Platform
    *  [soem/ethercat.c] new file
    *  #include <stdlib.h>
    *  #include "ethercat.h"
    *  
    *  ecx_contextt * ec_malloc_context(void)
    *  {
    *       ec_slavet		*ec_slave	= malloc(sizeof(ec_slavet) * EC_MAXSLAVE);
    *       int			*ec_slavecount	= malloc(sizeof(int));
    *       ec_groupt		*ec_group	= malloc(sizeof(ec_groupt) * EC_MAXGROUP);
    *       uint8		*ec_esibuf	= malloc(sizeof(uint8) * EC_MAXEEPBUF);
    *       uint32		*ec_esimap	= malloc(sizeof(uint32) * EC_MAXEEPBITMAP);
    *       ec_eringt		*ec_elist	= malloc(sizeof(ec_eringt));
    *       ec_idxstackT	*ec_idxstack	= malloc(sizeof(ec_idxstackT));
    *       ec_SMcommtypet	*ec_SMcommtype	= malloc(sizeof(ec_SMcommtypet) * EC_MAX_MAPT);
    *       ec_PDOassignt	*ec_PDOassign	= malloc(sizeof(ec_PDOassignt) * EC_MAX_MAPT);
    *       ec_PDOdesct		*ec_PDOdesc	= malloc(sizeof(ec_PDOdesct) * EC_MAX_MAPT);
    *       ec_eepromSMt	*ec_SM		= malloc(sizeof(ec_eepromSMt));
    *       ec_eepromFMMUt	*ec_FMMU	= malloc(sizeof(ec_eepromFMMUt));
    *       boolean		*EcatError	= malloc(sizeof(boolean));
    *       int64		*ec_DCtime	= malloc(sizeof(int64));
    *       ecx_portt		*ecx_port	= malloc(sizeof(ecx_portt));
    *       ecx_redportt	*ecx_redport	= malloc(sizeof(ecx_redportt));
    *       ecx_contextt	*ecx_context	= malloc(sizeof(ecx_contextt));
    *       ecx_redport->sockhandle		= NULL;
    *       ecx_port->redport			= ecx_redport;
    *       ecx_context->port			= ecx_port;
    *       ecx_context->slavelist		= ec_slave;
    *       ecx_context->slavecount		= ec_slavecount;
    *       ecx_context->maxslave		= EC_MAXSLAVE;
    *       ecx_context->grouplist		= ec_group;
    *       ecx_context->maxgroup		= EC_MAXGROUP;
    *       ecx_context->esibuf			= ec_esibuf;
    *       ecx_context->esimap			= ec_esimap;
    *       ecx_context->esislave		= 0;
    *       ecx_context->elist			= ec_elist;
    *       ecx_context->idxstack		= ec_idxstack;
    *       ecx_context->ecaterror		= EcatError;
    *       ecx_context->DCtO			= 0;
    *       ecx_context->DCl			= 0;
    *       ecx_context->DCtime			= ec_DCtime;
    *       ecx_context->SMcommtype		= ec_SMcommtype;
    *       ecx_context->PDOassign		= ec_PDOassign;
    *       ecx_context->PDOdesc		= ec_PDOdesc;
    *       ecx_context->eepSM			= ec_SM;
    *       ecx_context->eepFMMU		= ec_FMMU;
    *       ecx_context->FOEhook		= NULL;
    *       return ecx_context;
    *  }
    *  void ec_free_context(ecx_contextt * context)
    *  {
    *       free(context->port->redport);
    *       free(context->port);
    *       free(context->slavelist);
    *       free(context->slavecount);
    *       free(context->grouplist);
    *       free(context->esibuf);
    *       free(context->esimap);
    *       free(context->elist);
    *       free(context->idxstack);
    *       free(context->ecaterror);
    *       free(context->DCtime);
    *       free(context->SMcommtype);
    *       free(context->PDOassign);
    *       free(context->PDOdesc);
    *       free(context->eepSM);
    *       free(context->eepFMMU);
    *       free(context);
    *  }
    *  ecx_redportt * ec_redport(ecx_contextt * context)
    *  {
    *       return context->port->redport;
    *  }
     */
    public class CallBack extends Struct {

        public interface PO2SOconfig {

            @Delegate
            int call(int slave);
        }

        public interface FOEhook {

            @Delegate
            public int call(int slave, int packetnumber, int datasize);
        }

        Pointer callback;

        PO2SOconfig PO2SOconfig;
        FOEhook FOEhook;

        public CallBack(jnr.ffi.Runtime runtime) {
            super(runtime);

            callback = new Pointer();
        }
    }

    public class Bool extends Struct {

        private final Struct.Unsigned8 value_;

        public Bool(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Unsigned8();
        }

        public short get() {
            return value_.shortValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(short value) {
            value_.set(value);
        }
    }

    public class Int8 extends Struct {

        private final Struct.Signed8 value_;

        public Int8(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Signed8();
        }

        public short get() {
            return value_.byteValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(short value) {
            value_.set(value);
        }
    }

    public class Int16 extends Struct {

        private final Struct.Signed16 value_;

        public Int16(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Signed16();
        }

        public short get() {
            return value_.shortValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(short value) {
            value_.set(value);
        }
    }

    public class Int32 extends Struct {

        private final Struct.Signed32 value_;

        public Int32(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Signed32();
        }

        public int get() {
            return value_.intValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(int value) {
            value_.set(value);
        }
    }

    public class Int64 extends Struct {

        private final Struct.Signed64 value_;

        public Int64(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Signed64();
        }

        public long get() {
            return value_.longValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(long value) {
            value_.set(value);
        }
    }

    public class Uint8 extends Struct {

        private final Struct.Unsigned8 value_;

        public Uint8(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Unsigned8();
        }

        public short get() {
            return value_.shortValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(short value) {
            value_.set(value);
        }
    }

    public class Uint16 extends Struct {

        private final Struct.Unsigned16 value_;

        public Uint16(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Unsigned16();
        }

        public int get() {
            return value_.intValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(int value) {
            value_.set(value);
        }
    }

    public class Uint32 extends Struct {

        private final Struct.Unsigned32 value_;

        public Uint32(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Unsigned32();
        }

        public long get() {
            return value_.longValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(long value) {
            value_.set(value);
        }
    }

    public class Uint64 extends Struct {

        private final Struct.Unsigned64 value_;

        public Uint64(jnr.ffi.Runtime runtime) {
            super(runtime);

            value_ = new Struct.Unsigned64();
        }

        public long get() {
            return value_.longValue();
        }

        public void set(Number value) {
            value_.set(value);
        }

        public void set(long value) {
            value_.set(value);
        }
    }

    public class UTF8String extends Struct {

        private final Struct.UTF8String value_;

        public UTF8String(jnr.ffi.Runtime runtime, int size) {
            super(runtime);

            value_ = new Struct.UTF8String(size);
        }

        public java.lang.String get() {
            return value_.get();
        }

        public void set(java.lang.String value) {
            value_.set(value);
        }
    }

    // SoemEthercat
    public SoemEthercatMain.ecx_contextt ec_malloc_context();

    public void ec_free_context(SoemEthercatMain.ecx_contextt context);

    public Pointer ec_redport(SoemEthercatMain.ecx_contextt context);

    // SoemEthercatBase
    public int ecx_setupdatagram(Pointer port, Pointer frame, @u_int8_t int com, @u_int8_t int idx, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data);

    public int ecx_adddatagram(Pointer port, Pointer frame, @u_int8_t int com, @u_int8_t int idx, @u_int8_t int more, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data);

    public int ecx_BWR(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_BRD(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_APRD(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_ARMW(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_FRMW(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public @u_int16_t
    int ecx_APRDw(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, int timeout);

    public int ecx_FPRD(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public @u_int16_t
    int ecx_FPRDw(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, int timeout);

    public int ecx_APWRw(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int data, int timeout);

    public int ecx_APWR(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_FPWRw(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int data, int timeout);

    public int ecx_FPWR(Pointer port, @u_int16_t int ADP, @u_int16_t int ADO, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_LRW(Pointer port, @u_int32_t long LogAdr, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_LRD(Pointer port, @u_int32_t long LogAdr, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_LWR(Pointer port, @u_int32_t long LogAdr, @u_int16_t int length, Pointer data, int timeout);

    public int ecx_LRWDC(Pointer port, @u_int32_t long LogAdr, @u_int16_t int length, Pointer data, @u_int16_t int DCrs, Pointer DCtime, int timeout);

    // SoemEthercatMain
    public SoemEthercatMain.ec_adaptert ec_find_adapters();

    public void ec_free_adapters(SoemEthercatMain.ec_adaptert adapter);

    public @u_int8_t
    int ec_nextmbxcnt(@u_int8_t int cnt);

    public void ec_clearmbx(@u_int8_t int[] Mbx);

    public void ecx_pusherror(SoemEthercatMain.ecx_contextt context, SoemEthercatType.ec_errort Ec);

    public @u_int8_t
    int ecx_poperror(SoemEthercatMain.ecx_contextt context, SoemEthercatType.ec_errort Ec);

    public @u_int8_t
    int ecx_iserror(SoemEthercatMain.ecx_contextt context);

    public void ecx_packeterror(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, @u_int16_t int Index, @u_int8_t int SubIdx, @u_int16_t int ErrorCode);

    public int ecx_init(SoemEthercatMain.ecx_contextt context, String ifname);

    public int ecx_init_redundant(SoemEthercatMain.ecx_contextt context, Pointer redport, String ifname, String if2name);

    public void ecx_close(SoemEthercatMain.ecx_contextt context);

    public @u_int8_t
    int ecx_siigetbyte(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int address);

    public @int16_t
    int ecx_siifind(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int cat);

    public void ecx_siistring(SoemEthercatMain.ecx_contextt context, Pointer str, @u_int16_t int slave, @u_int16_t int Sn);

    public @u_int16_t
    int ecx_siiFMMU(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, SoemEthercatMain.ec_eepromFMMUt FMMU);

    public @u_int16_t
    int ecx_siiSM(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, SoemEthercatMain.ec_eepromSMt SM);

    public @u_int16_t
    int ecx_siiSMnext(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, SoemEthercatMain.ec_eepromSMt SM, @u_int16_t int n);

    public int ecx_siiPDO(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, SoemEthercatMain.ec_eepromPDOt PDO, @u_int8_t int t);

    public int ecx_readstate(SoemEthercatMain.ecx_contextt context);

    public int ecx_writestate(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave);

    public @u_int16_t
    int ecx_statecheck(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int reqstate, int timeout);

    public int ecx_mbxempty(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, int timeout);

    public int ecx_mbxsend(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int[] mbx, int timeout);

    public int ecx_mbxreceive(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int[] mbx, int timeout);

    public void ecx_esidump(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int[] esibuf);

    public @u_int32_t
    long ecx_readeeprom(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int eeproma, int timeout);

    public int ecx_writeeeprom(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int eeproma, @u_int16_t int data, int timeout);

    public int ecx_eeprom2master(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave);

    public int ecx_eeprom2pdi(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave);

    public @u_int64_t
    long ecx_readeepromAP(SoemEthercatMain.ecx_contextt context, @u_int16_t int aiadr, @u_int16_t int eeproma, int timeout);

    public int ecx_writeeepromAP(SoemEthercatMain.ecx_contextt context, @u_int16_t int aiadr, @u_int16_t int eeproma, @u_int16_t int data, int timeout);

    public @u_int64_t
    long ecx_readeepromFP(SoemEthercatMain.ecx_contextt context, @u_int16_t int configadr, @u_int16_t int eeproma, int timeout);

    public int ecx_writeeepromFP(SoemEthercatMain.ecx_contextt context, @u_int16_t int configadr, @u_int16_t int eeproma, @u_int16_t int data, int timeout);

    public void ecx_readeeprom1(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int eeproma);

    public @u_int32_t
    long ecx_readeeprom2(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, int timeout);

    public int ecx_send_overlap_processdata_group(SoemEthercatMain.ecx_contextt context, @u_int8_t int group);

    public int ecx_receive_processdata_group(SoemEthercatMain.ecx_contextt context, @u_int8_t int group, int timeout);

    public int ecx_send_processdata(SoemEthercatMain.ecx_contextt context);

    public int ecx_send_overlap_processdata(SoemEthercatMain.ecx_contextt context);

    public int ecx_receive_processdata(SoemEthercatMain.ecx_contextt context, int timeout);

    // SoemEthercatDC
    public @u_int8_t
    int ecx_configdc(SoemEthercatMain.ecx_contextt context);

    public void ecx_dcsync0(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int act, @u_int32_t long CyclTime, @int32_t int CyclShift);

    public void ecx_dcsync01(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int act, @u_int32_t long CyclTime0, @u_int32_t long CyclTime1, @int32_t int CyclShift);

    // SoemEthercatCoE
    public void ecx_SDOerror(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, @u_int16_t int Index, @u_int8_t int SubIdx, @int32_t int AbortCode);

    public int ecx_SDOread(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int index, @u_int8_t int subindex, @u_int8_t int CA, Pointer psize, Pointer p, int timeout);

    public int ecx_SDOwrite(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, @u_int16_t int Index, @u_int8_t int SubIndex, @u_int8_t int CA, int psize, Pointer p, int Timeout);

    public int ecx_RxPDO(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, @u_int16_t int RxPDOnumber, int psize, Pointer p);

    public int ecx_TxPDO(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int16_t int TxPDOnumber, Pointer psize, Pointer p, int timeout);

    public int ecx_readPDOmap(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, Pointer Osize, Pointer Isize);

    public int ecx_readPDOmapCA(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, int Thread_n, Pointer Osize, Pointer Isize);

    public int ecx_readODlist(SoemEthercatMain.ecx_contextt context, @u_int16_t int Slave, SoemEthercatCoE.ec_ODlistt pODlist);

    public int ecx_readODdescription(SoemEthercatMain.ecx_contextt context, @u_int16_t int Item, SoemEthercatCoE.ec_ODlistt pODlist);

    public int ecx_readOEsingle(SoemEthercatMain.ecx_contextt context, @u_int16_t int Item, @u_int8_t int SubI, SoemEthercatCoE.ec_ODlistt pODlist, SoemEthercatCoE.ec_OElistt pOElist);

    public int ecx_readOE(SoemEthercatMain.ecx_contextt context, @u_int16_t int Item, SoemEthercatCoE.ec_ODlistt pODlist, SoemEthercatCoE.ec_OElistt pOElist);

    // SoemEthercatFoE
    public int ecx_FOEdefinehook(SoemEthercatMain.ecx_contextt context, CallBack.FOEhook hook);

    public int ecx_FOEread(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, Pointer filename, @u_int32_t long password, Pointer psize, Pointer p, int timeout);

    public int ecx_FOEwrite(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, Pointer filename, @u_int32_t long password, int psize, Pointer p, int timeout);

    // SoemEthercatSoE
    public int ecx_SoEread(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int driveNo, @u_int8_t int elementflags, @u_int16_t int idn, Pointer psize, Pointer p, int timeout);

    public int ecx_SoEwrite(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, @u_int8_t int driveNo, @u_int8_t int elementflags, @u_int16_t int idn, int psize, Pointer p, int timeout);

    public int ecx_readIDNmap(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, Pointer Osize, Pointer Isize);

    // SoemEthercatConfig
    public int ecx_config_init(SoemEthercatMain.ecx_contextt context, @u_int8_t int usetable);

    public int ecx_config_map_group(SoemEthercatMain.ecx_contextt context, Pointer pIOmap, @u_int8_t int group);

    public int ecx_config_overlap_map_group(SoemEthercatMain.ecx_contextt context, Pointer pIOmap, @u_int8_t int group);

    public int ecx_recover_slave(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, int timeout);

    public int ecx_reconfig_slave(SoemEthercatMain.ecx_contextt context, @u_int16_t int slave, int timeout);

    // SoemEthercatPrint
    String ec_sdoerror2string(@u_int32_t long sdoerrorcode);

    String ec_ALstatuscode2string(@u_int16_t int ALstatuscode);

    String ec_soeerror2string(@u_int16_t int errorcode);

    String ecx_elist2string(SoemEthercatMain.ecx_contextt context);
}
