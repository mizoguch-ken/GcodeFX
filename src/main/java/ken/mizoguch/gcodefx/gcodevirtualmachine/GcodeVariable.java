/*
 * To change (new GcodeVariable(_value)) license header, choose License Headers in Project Properties.
 * To change (new GcodeVariable(_value)) template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.gcodevirtualmachine;

/**
 *
 * @author mizoguch-ken
 */
public class GcodeVariable {

    public static final int TRUE = -1;
    public static final int FALSE = 0;

    private double value_;
    private boolean nil_;
    private int number_;
    private String string_;
    private String name_;

    /**
     *
     * @param num
     */
    public GcodeVariable(int num) {
        value_ = 0;
        nil_ = true;
        number_ = num;
        string_ = null;
        name_ = null;
    }

    /**
     *
     * @param gv
     */
    public GcodeVariable(GcodeVariable gv) {
        value_ = gv.getDouble();
        nil_ = gv.isNil();
        number_ = gv.getNumber();
        string_ = gv.getString();
        name_ = gv.getName();
    }

    /**
     *
     * @param val
     * @param num
     * @param str
     * @param name
     */
    public GcodeVariable(Double val, int num, String str, String name) {
        if (val == null) {
            value_ = 0;
            nil_ = true;
        } else {
            value_ = val;
            nil_ = false;
        }
        number_ = num;
        string_ = str;
        name_ = name;
    }

    /**
     *
     * @param name
     */
    public GcodeVariable(String name) {
        value_ = 0;
        nil_ = false;
        number_ = 0;
        string_ = null;
        name_ = name;
    }

    /**
     *
     * @param gv
     */
    public void copy(GcodeVariable gv) {
        value_ = gv.getDouble();
        nil_ = gv.isNil();
        number_ = gv.getNumber();
        string_ = gv.getString();
        name_ = gv.getName();
    }

    /**
     *
     * @return
     */
    public boolean isNil() {
        return nil_;
    }

    /**
     *
     * @return
     */
    public double getDouble() {
        if (Double.isNaN(value_)) {
            return 0;
        } else {
            return value_;
        }
    }

    /**
     *
     * @param val
     * @return
     */
    public GcodeVariable setDouble(Double val) {
        if (val == null) {
            value_ = 0;
            string_ = null;
            nil_ = true;
        } else {
            value_ = val;
            string_ = val.toString();
            nil_ = false;
        }
        return this;
    }

    /**
     *
     * @return
     */
    public int getNumber() {
        return number_;
    }

    /**
     *
     * @param num
     * @return
     */
    public GcodeVariable setNumber(int num) {
        number_ = num;
        return this;
    }

    /**
     *
     * @return
     */
    public String getString() {
        return string_;
    }

    /**
     *
     * @param str
     * @return
     */
    public GcodeVariable setString(String str) {
        string_ = str;
        return this;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name_;
    }

    /**
     *
     * @param name
     * @return
     */
    public GcodeVariable setName(String name) {
        name_ = name;
        return this;
    }

    /**
     *
     * @return
     */
    public int getInt() {
        return (int) Math.round(value_);
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable add(GcodeVariable gv) {
        value_ += gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable sub(GcodeVariable gv) {
        value_ -= gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable mul(GcodeVariable gv) {
        value_ *= gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable div(GcodeVariable gv) {
        value_ /= gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable neg() {
        value_ = -value_;
        string_ = Double.toString(value_);
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable and(GcodeVariable gv) {
        value_ = (int) value_ & (int) gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable or(GcodeVariable gv) {
        value_ = (int) value_ | (int) gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable xor(GcodeVariable gv) {
        value_ = (int) value_ ^ (int) gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable mod(GcodeVariable gv) {
        value_ %= gv.getDouble();
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable eq(GcodeVariable gv) {
        value_ = ((nil_ == gv.isNil()) && (value_ == gv.getDouble())) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable ne(GcodeVariable gv) {
        value_ = ((nil_ != gv.isNil()) || (value_ != gv.getDouble())) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable lt(GcodeVariable gv) {
        value_ = (value_ < gv.getDouble()) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable le(GcodeVariable gv) {
        value_ = (value_ <= gv.getDouble()) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable gt(GcodeVariable gv) {
        value_ = (value_ > gv.getDouble()) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable ge(GcodeVariable gv) {
        value_ = (value_ >= gv.getDouble()) ? TRUE : FALSE;
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable sin() {
        value_ = Math.sin(Math.toRadians(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable cos() {
        value_ = Math.cos(Math.toRadians(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable tan() {
        value_ = Math.tan(Math.toRadians(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable asin() {
        value_ = Math.toDegrees(Math.asin(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable acos() {
        value_ = Math.toDegrees(Math.acos(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable atan() {
        value_ = Math.toDegrees(Math.atan(value_));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable sqrt() {
        value_ = Math.sqrt(value_);
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable abs() {
        value_ = Math.abs(value_);
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable bin() {
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable bcd() {
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable round() {
        value_ = Math.round(value_);
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable fix() {
        if (value_ < 0) {
            value_ = Math.ceil(value_);
        } else {
            value_ = Math.floor(value_);
        }
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable fup() {
        if (value_ < 0) {
            value_ = Math.floor(value_);
        } else {
            value_ = Math.ceil(value_);
        }
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable ln() {
        value_ = Math.log(value_);
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable exp() {
        value_ = Math.exp(value_);
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @param v
     * @return
     */
    public GcodeVariable pow(GcodeVariable v) {
        value_ = Math.pow(value_, v.getDouble());
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }

    /**
     *
     * @return
     */
    public GcodeVariable adp() {
        nil_ = false;
        return this;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GcodeVariable atan2(GcodeVariable gv) {
        value_ = Math.toDegrees(Math.atan2(value_, gv.getDouble()));
        string_ = Double.toString(value_);
        nil_ = false;
        return this;
    }
}
