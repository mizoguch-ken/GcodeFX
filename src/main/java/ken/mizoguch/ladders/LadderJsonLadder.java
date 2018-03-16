/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mizoguch-ken
 */
public class LadderJsonLadder {

    private final Integer idx;
    private final String name;
    private final Integer column;
    private final Integer row;
    private final String address;           // *history
    private final String comment;           // *history
    private final Integer columnIndex;      // *history
    private final Integer rowIndex;         // *history
    private List<LadderJsonBlock> blocks;

    /**
     *
     * @param idx
     * @param name
     * @param column
     * @param row
     */
    public LadderJsonLadder(Integer idx, String name, Integer column, Integer row) {
        this.idx = idx;
        this.name = name;
        this.column = column;
        this.row = row;
        this.address = null;
        this.comment = null;
        this.columnIndex = null;
        this.rowIndex = null;
        this.blocks = null;
    }

    /**
     *
     * @param idx
     */
    public LadderJsonLadder(Integer idx) {
        this.idx = idx;
        this.name = null;
        this.column = null;
        this.row = null;
        this.address = null;
        this.comment = null;
        this.columnIndex = null;
        this.rowIndex = null;
        this.blocks = null;
    }

    /**
     *
     * @param idx
     * @param name
     */
    public LadderJsonLadder(Integer idx, String name) {
        this.idx = idx;
        this.name = name;
        this.column = null;
        this.row = null;
        this.address = null;
        this.comment = null;
        this.columnIndex = null;
        this.rowIndex = null;
        this.blocks = null;
    }

    /**
     *
     * @param idx
     * @param columnIndex
     * @param rowIndex
     */
    public LadderJsonLadder(Integer idx, Integer columnIndex, Integer rowIndex) {
        this.idx = idx;
        this.name = null;
        this.column = null;
        this.row = null;
        this.address = null;
        this.comment = null;
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.blocks = null;
    }

    /**
     *
     * @param idx
     * @param address
     * @param comment
     */
    public LadderJsonLadder(Integer idx, String address, String comment) {
        this.idx = idx;
        this.name = null;
        this.column = null;
        this.row = null;
        this.address = address;
        this.comment = comment;
        this.columnIndex = null;
        this.rowIndex = null;
        this.blocks = null;
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
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public Integer getColumn() {
        return column;
    }

    /**
     *
     * @return
     */
    public Integer getRow() {
        return row;
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
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     *
     * @return
     */
    public Integer getRowIndex() {
        return rowIndex;
    }

    /**
     *
     * @return
     */
    public List<LadderJsonBlock> getBlocks() {
        return blocks;
    }

    /**
     *
     * @param block
     */
    public void addBlock(LadderJsonBlock block) {
        if (block != null) {
            if (blocks == null) {
                blocks = new ArrayList<>();
            }
            blocks.add(block);
        }
    }

    /**
     *
     * @param column
     * @param row
     */
    public void sortBlocks(int column, int row) {
        if (blocks != null) {
            blocks.sort((o1, o2) -> {
                return (o1.getColumnIndex() + (o1.getRowIndex() * column)) - (o2.getColumnIndex() + (o2.getRowIndex() * column));
            });
        }
    }
}
