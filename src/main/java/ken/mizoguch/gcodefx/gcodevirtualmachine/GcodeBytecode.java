/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.gcodevirtualmachine;

/**
 *
 * @author mizoguch-ken
 */
public class GcodeBytecode {

    private final int opCode_;
    private final double dblValue_;
    private final String strValue_;
    private final boolean debug_;

    /**
     *
     * @param opCode
     * @param debug
     */
    public GcodeBytecode(int opCode, boolean debug) {
        opCode_ = opCode;
        dblValue_ = 0;
        strValue_ = null;
        debug_ = debug;
    }

    /**
     *
     * @param opCode
     * @param value
     * @param debug
     */
    public GcodeBytecode(int opCode, double value, boolean debug) {
        opCode_ = opCode;
        dblValue_ = value;
        strValue_ = Double.toString(value);
        debug_ = debug;
    }

    /**
     *
     * @param opCode
     * @param value
     * @param debug
     */
    public GcodeBytecode(int opCode, String value, boolean debug) {
        double dbl;

        try {
            dbl = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            dbl = 0;
        }

        opCode_ = opCode;
        dblValue_ = dbl;
        strValue_ = value;
        debug_ = debug;
    }

    /**
     *
     * @return
     */
    public int getOpCode() {
        return opCode_;
    }

    /**
     *
     * @return
     */
    public boolean isDebug() {
        return debug_;
    }

    /**
     *
     * @return
     */
    public double getDblValue() {
        return dblValue_;
    }

    /**
     *
     * @return
     */
    public String getStrValue() {
        return strValue_;
    }

    /**
     *
     * @return
     */
    public int getIntValue() {
        return (int) dblValue_;
    }
}
