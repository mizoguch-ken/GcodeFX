/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author mizoguch-ken
 */
public class LadderTableIo {

    private final StringProperty name_, address_, block_, position_;
    private int column_, row_;

    /**
     *
     * @param name
     * @param address
     * @param block
     * @param column
     * @param row
     */
    public LadderTableIo(String name, String address, String block, int column, int row) {
        name_ = new SimpleStringProperty(name);
        address_ = new SimpleStringProperty(address);
        block_ = new SimpleStringProperty(block);
        column_ = column;
        row_ = row;
        position_ = new SimpleStringProperty(column_ + " : " + row_);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name_.get();
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        name_.set(name);
    }

    /**
     *
     * @return
     */
    public StringProperty nameProperty() {
        return name_;
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
    public String getBlock() {
        return block_.get();
    }

    /**
     *
     * @param block
     */
    public void setBlock(String block) {
        block_.set(block);
    }

    /**
     *
     * @return
     */
    public StringProperty blockProperty() {
        return block_;
    }

    /**
     *
     * @return
     */
    public String getPosition() {
        return position_.get();
    }

    /**
     *
     * @param position
     */
    public void setPosition(String position) {
        String[] pos = position.split(":");
        int column, row;

        try {
            column = Integer.parseInt(pos[0]);
            row = Integer.parseInt(pos[1]);
            column_ = column;
            row_ = row;
            position_.set(column_ + " : " + row_);
        } catch (NumberFormatException ex) {
        }
    }

    /**
     *
     * @return
     */
    public StringProperty positionProperty() {
        return position_;
    }

    /**
     *
     * @return
     */
    public int getColumn() {
        return column_;
    }

    /**
     *
     * @param column
     */
    public void setColumn(int column) {
        column_ = column;
        position_.set(column_ + " : " + row_);
    }

    /**
     *
     * @return
     */
    public int getRow() {
        return row_;
    }

    /**
     *
     * @param row
     */
    public void setRow(int row) {
        row_ = row;
        position_.set(column_ + " : " + row_);
    }
}
