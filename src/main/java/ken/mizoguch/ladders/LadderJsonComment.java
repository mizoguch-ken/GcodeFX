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
public class LadderJsonComment {

    private final Integer idx;
    private final String address;
    private final String comment;
    private final Double value;

    /**
     *
     * @param idx
     * @param address
     * @param comment
     * @param value
     */
    public LadderJsonComment(Integer idx, String address, String comment, Double value) {
        this.idx = idx;
        this.address = address;
        this.comment = comment;
        if (value.isNaN()) {
            this.value = 0.0;
        } else if (value == Double.NEGATIVE_INFINITY) {
            this.value = Double.MIN_VALUE;
        } else if (value == Double.POSITIVE_INFINITY) {
            this.value = Double.MAX_VALUE;
        } else {
            this.value = value;
        }
    }

    /**
     *
     * @return
     */
    public Integer getIdx() {
        return idx;
    }

    /**
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @return
     */
    public Double getValue() {
        return value;
    }
}
