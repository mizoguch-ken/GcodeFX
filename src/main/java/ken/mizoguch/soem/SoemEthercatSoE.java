/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.soem;

import jnr.ffi.Struct;
import jnr.ffi.Union;

/**
 *
 * @author mizoguch-ken
 */
public interface SoemEtherCATSoE {

    public static final int EC_SOE_DATASTATE_B = 0x01;
    public static final int EC_SOE_NAME_B = 0x02;
    public static final int EC_SOE_ATTRIBUTE_B = 0x04;
    public static final int EC_SOE_UNIT_B = 0x08;
    public static final int EC_SOE_MIN_B = 0x10;
    public static final int EC_SOE_MAX_B = 0x20;
    public static final int EC_SOE_VALUE_B = 0x40;
    public static final int EC_SOE_DEFAULT_B = 0x80;

    public static final int EC_SOE_MAXNAME = 60;
    public static final int EC_SOE_MAXMAPPING = 64;

    public static final int EC_IDN_MDTCONFIG = 24;
    public static final int EC_IDN_ATCONFIG = 16;

    /**
     * SoE name structure
     */
    public class ec_SoEnamet extends Struct {

        /**
         * current length in bytes of list
         */
        public final Unsigned16 currentlength;
        /**
         * maximum length in bytes of list
         */
        public final Unsigned16 maxlength;
        public final String name;

        public ec_SoEnamet(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            currentlength = new Unsigned16();
            maxlength = new Unsigned16();
            name = new UTF8String(EC_SOE_MAXNAME);
        }
    }

    /**
     * SoE list structure
     */
    public class ec_SoElistt_Union extends Union {

        public final Unsigned8[] hword;
        public final Unsigned16[] word;
        public final Unsigned32[] dword;
        public final Unsigned64[] lword;

        public ec_SoElistt_Union(jnr.ffi.Runtime runtime) {
            super(runtime);

            hword = super.array(new Unsigned8[8]);
            word = super.array(new Unsigned16[4]);
            dword = super.array(new Unsigned32[2]);
            lword = super.array(new Unsigned64[1]);
        }
    }

    public class ec_SoElistt extends Struct {

        /**
         * current length in bytes of list
         */
        public final Unsigned16 currentlength;
        /**
         * maximum length in bytes of list
         */
        public final Unsigned16 maxlength;
        public final ec_SoElistt_Union union;

        public ec_SoElistt(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            currentlength = new Unsigned16();
            maxlength = new Unsigned16();
            union = inner(new ec_SoElistt_Union(runtime));
        }
    }

    /**
     * SoE IDN mapping structure
     */
    public class ec_SoEmappingt extends Struct {

        /**
         * current length in bytes of list
         */
        public final Unsigned16 currentlength;
        /**
         * maximum length in bytes of list
         */
        public final Unsigned16 maxlength;
        public final Unsigned16[] idn;

        public ec_SoEmappingt(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            currentlength = new Unsigned16();
            maxlength = new Unsigned16();
            idn = super.array(new Unsigned16[EC_SOE_MAXMAPPING]);
        }
    }

    public static final int EC_SOE_LENGTH_1 = 0x00;
    public static final int EC_SOE_LENGTH_2 = 0x01;
    public static final int EC_SOE_LENGTH_4 = 0x02;
    public static final int EC_SOE_LENGTH_8 = 0x03;
    public static final int EC_SOE_TYPE_BINARY = 0x00;
    public static final int EC_SOE_TYPE_UINT = 0x01;
    public static final int EC_SOE_TYPE_INT = 0x02;
    public static final int EC_SOE_TYPE_HEX = 0x03;
    public static final int EC_SOE_TYPE_STRING = 0x04;
    public static final int EC_SOE_TYPE_IDN = 0x05;
    public static final int EC_SOE_TYPE_FLOAT = 0x06;
    public static final int EC_SOE_TYPE_PARAMETER = 0x07;

    /**
     * SoE attribute structure
     */
    public class ec_SoEattributet extends Struct {

        public final Unsigned32 attribute;
//        /**
//         * evaluation factor for display purposes
//         */
//        public Unsigned32 evafactor;
//        /**
//         * length of IDN element(s)
//         */
//        public Unsigned32 length;
//        /**
//         * IDN is list
//         */
//        public Unsigned32 list;
//        /**
//         * IDN is command
//         */
//        public Unsigned32 command;
//        /**
//         * datatype
//         */
//        public Unsigned32 datatype;
//        public Unsigned32 reserved1;
//        /**
//         * decimals to display if float datatype
//         */
//        public Unsigned32 decimals;
//        /**
//         * write protected in pre-op
//         */
//        public Unsigned32 wppreop;
//        /**
//         * write protected in safe-op
//         */
//        public Unsigned32 wpsafeop;
//        /**
//         * write protected in op
//         */
//        public Unsigned32 wpop;
//        public Unsigned32 reserved2;

        public ec_SoEattributet(jnr.ffi.Runtime runtime) {
            super(runtime, new Struct.Alignment(1));

            attribute = new Unsigned32();
        }
    }
}
