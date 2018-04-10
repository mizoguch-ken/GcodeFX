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
public interface SoemEtherCATCoE {

    /**
     * max entries in Object Description list
     */
    public static final int EC_MAXODLIST = 1024;

    /**
     * max entries in Object Entry list
     */
    public static final int EC_MAXOELIST = 256;

    /* Storage for object description list */
    public class ec_ODlistt extends Struct {

        /**
         * slave number
         */
        public final Unsigned16 Slave;
        /**
         * number of entries in list
         */
        public final Unsigned16 Entries;
        /**
         * array of indexes
         */
        public final Unsigned16[] Index;
        /**
         * array of datatypes, see EtherCAT specification
         */
        public final Unsigned16[] DataType;
        /**
         * array of object codes, see EtherCAT specification
         */
        public final Unsigned8[] ObjectCode;
        /**
         * number of subindexes for each index
         */
        public final Unsigned8[] MaxSub;
        /**
         * textual description of each index
         */
        public final String[] Name;

        public ec_ODlistt(jnr.ffi.Runtime runtime) {
            super(runtime);

            Slave = new Unsigned16();
            Entries = new Unsigned16();
            Index = super.array(new Unsigned16[EC_MAXODLIST]);
            DataType = super.array(new Unsigned16[EC_MAXODLIST]);
            ObjectCode = super.array(new Unsigned8[EC_MAXODLIST]);
            MaxSub = super.array(new Unsigned8[EC_MAXODLIST]);
            Name = super.array(new UTF8String[EC_MAXODLIST], SoemEtherCATMain.EC_MAXNAME + 1);
        }
    }

    /* storage for object list entry information */
    public class ec_OElistt extends Struct {

        /**
         * number of entries in list
         */
        public final Unsigned16 Entries;
        /**
         * array of value infos, see EtherCAT specification
         */
        public final Unsigned8[] ValueInfo;
        /**
         * array of value infos, see EtherCAT specification
         */
        public final Unsigned16[] DataType;
        /**
         * array of bit lengths, see EtherCAT specification
         */
        public final Unsigned16[] BitLength;
        /**
         * array of object access bits, see EtherCAT specification
         */
        public final Unsigned16[] ObjAccess;
        /**
         * textual description of each index
         */
        public String[] Name;

        public ec_OElistt(jnr.ffi.Runtime runtime) {
            super(runtime);

            Entries = new Unsigned16();
            ValueInfo = super.array(new Unsigned8[EC_MAXOELIST]);
            DataType = super.array(new Unsigned16[EC_MAXOELIST]);
            BitLength = super.array(new Unsigned16[EC_MAXOELIST]);
            ObjAccess = super.array(new Unsigned16[EC_MAXOELIST]);
            Name = super.array(new UTF8String[EC_MAXOELIST], SoemEtherCATMain.EC_MAXNAME + 1);
        }
    }
}
