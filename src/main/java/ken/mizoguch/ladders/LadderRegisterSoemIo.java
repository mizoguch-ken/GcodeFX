/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

/**
 *
 * @author mizoguch-ken
 */
public class LadderRegisterSoemIo {

    private final String address_;
    private final int slave_;
    private final long bitsOffset_;
    private final long bitsMask_;

    /**
     *
     * @param address
     * @param slave
     * @param bitsOffset
     * @param bitsMask
     */
    public LadderRegisterSoemIo(String address, int slave, long bitsOffset, long bitsMask) {
        address_ = address;
        slave_ = slave;
        bitsOffset_ = bitsOffset;
        bitsMask_ = bitsMask;
    }

    /**
     *
     * @return
     */
    public String getAddress() {
        return address_;
    }

    /**
     *
     * @return
     */
    public int getSlave() {
        return slave_;
    }

    /**
     *
     * @return
     */
    public long getBitsOffset() {
        return bitsOffset_;
    }

    /**
     *
     * @return
     */
    public long getBitsMask() {
        return bitsMask_;
    }
}
