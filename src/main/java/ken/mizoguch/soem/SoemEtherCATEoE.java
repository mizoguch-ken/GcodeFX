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
public interface SoemEtherCATEoE {

    /**
     * DNS length according to ETG 1000.6
     */
    public static final int EOE_DNS_NAME_LENGTH = 32;
    /**
     * Ethernet address length not including VLAN
     */
    public static final int EOE_ETHADDR_LENGTH = 6;

    /**
     * EOE param
     */
    public static final int EOE_PARAM_OFFSET = 4;
    public static final int EOE_PARAM_MAC_INCLUDE = (0x1 << 0);
    public static final int EOE_PARAM_IP_INCLUDE = (0x1 << 1);
    public static final int EOE_PARAM_SUBNET_IP_INCLUDE = (0x1 << 2);
    public static final int EOE_PARAM_DEFAULT_GATEWAY_INCLUDE = (0x1 << 3);
    public static final int EOE_PARAM_DNS_IP_INCLUDE = (0x1 << 4);
    public static final int EOE_PARAM_DNS_NAME_INCLUDE = (0x1 << 5);

    /**
     * EoE frame types
     */
    public static final int EOE_FRAG_DATA = 0;
    public static final int EOE_INIT_RESP_TIMESTAMP = 1;
    public static final int EOE_INIT_REQ = 2;
    /* Spec SET IP REQ */
    public static final int EOE_INIT_RESP = 3;
    /* Spec SET IP RESP */
    public static final int EOE_SET_ADDR_FILTER_REQ = 4;
    public static final int EOE_SET_ADDR_FILTER_RESP = 5;
    public static final int EOE_GET_IP_PARAM_REQ = 6;
    public static final int EOE_GET_IP_PARAM_RESP = 7;
    public static final int EOE_GET_ADDR_FILTER_REQ = 8;
    public static final int EOE_GET_ADDR_FILTER_RESP = 9;

    /**
     * EoE parameter result codes
     */
    public static final int EOE_RESULT_SUCCESS = 0x0000;
    public static final int EOE_RESULT_UNSPECIFIED_ERROR = 0x0001;
    public static final int EOE_RESULT_UNSUPPORTED_FRAME_TYPE = 0x0002;
    public static final int EOE_RESULT_NO_IP_SUPPORT = 0x0201;
    public static final int EOE_RESULT_NO_DHCP_SUPPORT = 0x0202;
    public static final int EOE_RESULT_NO_FILTER_SUPPORT = 0x0401;

    /**
     * EOE ip4 address in network order
     */
    public class eoe_ip4_addr_t extends Struct {

        public final Unsigned32 addr;

        public eoe_ip4_addr_t(jnr.ffi.Runtime runtime) {
            super(runtime);

            addr = new Unsigned32();
        }
    }

    /**
     * EOE ethernet address
     */
    public class eoe_ethaddr_t extends Struct {

        public final Unsigned8 addr[];

        public eoe_ethaddr_t(jnr.ffi.Runtime runtime) {
            super(runtime);

            addr = new Unsigned8[EOE_ETHADDR_LENGTH];
        }
    }
}
