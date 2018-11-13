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
public class LadderJsonBlock {

    public static class JsonBlockFunction {

        public final String address;
        public final Double value;
        public final Integer radix;

        public JsonBlockFunction(String address) {
            this.address = address;
            this.value = null;
            this.radix = null;
        }

        public JsonBlockFunction(double value, int radix) {
            this.address = null;
            this.value = value;
            this.radix = radix;
        }
    }

    private final Integer columnIndex;
    private final Integer rowIndex;
    private final String block;
    private final Boolean vertical;
    private final Boolean verticalOr;
    private final String address;
    private final String comment;
    private List<JsonBlockFunction> blockFunctions;
    private final String blockScript;

    /**
     *
     * @param columnIndex
     * @param rowIndex
     */
    public LadderJsonBlock(Integer columnIndex, Integer rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.block = LadderGrid.LADDER_GRID_INITIAL_BLOCK.toString();
        this.vertical = LadderGrid.LADDER_GRID_INITIAL_VERTICAL;
        this.verticalOr = LadderGrid.LADDER_GRID_INITIAL_VERTICAL_OR;
        this.address = LadderGrid.LADDER_GRID_INITIAL_ADDRESS;
        this.comment = null;
        this.blockFunctions = null;
        this.blockScript = LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT;
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param block
     * @param vertical
     * @param verticalOr
     * @param address
     * @param comment
     * @param blockFunctions
     * @param blockScript
     */
    public LadderJsonBlock(Integer columnIndex, Integer rowIndex, String block, Boolean vertical, Boolean verticalOr, String address, String comment, List<LadderGrid.BlockFunction> blockFunctions, String blockScript) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.block = block;
        this.vertical = vertical;
        this.verticalOr = verticalOr;
        this.address = address;
        this.comment = comment;
        if (blockFunctions != null) {
            this.blockFunctions = new ArrayList<>();
            for (int i = 0, size = blockFunctions.size(); i < size; i++) {
                if (blockFunctions.get(i).isNumber() && (blockFunctions.get(i).getRadix() != LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX)) {
                    this.blockFunctions.add(new JsonBlockFunction(blockFunctions.get(i).getValue(), blockFunctions.get(i).getRadix()));
                } else if (blockFunctions.get(i).getAddress() != null) {
                    this.blockFunctions.add(new JsonBlockFunction(blockFunctions.get(i).getAddress()));
                } else {
                    break;
                }
            }
        }
        this.blockScript = blockScript;
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
    public String getBlock() {
        return block;
    }

    /**
     *
     * @return
     */
    public Boolean isVertical() {
        return vertical;
    }

    /**
     *
     * @return
     */
    public Boolean isVerticalOr() {
        return verticalOr;
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
    public List<JsonBlockFunction> getBlockFunctions() {
        return blockFunctions;
    }

    /**
     *
     * @return
     */
    public String getBlockScript() {
        return blockScript;
    }
}
