/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author mizoguch-ken
 */
public class LadderTreeTableIo {

    private final StringProperty address_, comment_;
    private final DoubleProperty value_;

    /**
     *
     * @param address
     */
    public LadderTreeTableIo(String address) {
        this(address, "", 0.0);
    }

    /**
     *
     * @param address
     * @param comment
     */
    public LadderTreeTableIo(String address, String comment) {
        this(address, comment, 0.0);
    }

    /**
     *
     * @param address
     * @param value
     */
    public LadderTreeTableIo(String address, double value) {
        this(address, "", value);
    }

    /**
     *
     * @param address
     * @param comment
     * @param value
     */
    public LadderTreeTableIo(String address, String comment, double value) {
        address_ = new SimpleStringProperty(address);
        comment_ = new SimpleStringProperty(comment);
        value_ = new SimpleDoubleProperty(value);
    }

    /**
     *
     * @return
     */
    public String getAddress() {
        return address_.get();
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        address_.set(address);
    }

    /**
     *
     * @return
     */
    public StringProperty addressProperty() {
        return address_;
    }

    /**
     *
     * @return
     */
    public String getComment() {
        return comment_.get();
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        comment_.set(comment);
    }

    /**
     *
     * @return
     */
    public StringProperty commentProperty() {
        return comment_;
    }

    /**
     *
     * @return
     */
    public double getValue() {
        return value_.get();
    }

    /**
     *
     * @param value
     */
    public void setValue(Double value) {
        if (value == null) {
            value_.set(0.0);
        } else {
            value_.set(value);
        }
    }

    /**
     *
     * @return
     */
    public DoubleProperty valueProperty() {
        return value_;
    }
}
