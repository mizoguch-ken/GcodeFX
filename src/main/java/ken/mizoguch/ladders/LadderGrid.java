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
public class LadderGrid {

    public static final Ladders.LADDER_BLOCK LADDER_GRID_INITIAL_BLOCK = Ladders.LADDER_BLOCK.EMPTY;
    public static final double LADDER_GRID_INITIAL_BLOCK_VALUE = 0.0;
    public static final String LADDER_GRID_INITIAL_BLOCK_FUNCTION_ADDRESS = null;
    public static final double LADDER_GRID_INITIAL_BLOCK_FUNCTION_VALUE = 0.0;
    public static final int LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX = -1;
    public static final boolean LADDER_GRID_INITIAL_BLOCK_FUNCTION_NUMBER = false;
    public static final String LADDER_GRID_INITIAL_BLOCK_SCRIPT = null;
    public static final boolean LADDER_GRID_INITIAL_VERTICAL = false;
    public static final boolean LADDER_GRID_INITIAL_VERTICAL_OR = false;
    public static final String LADDER_GRID_INITIAL_ADDRESS = "";
    public static final String LADDER_GRID_INITIAL_COMMENT = "";
    public static final double LADDER_GRID_INITIAL_CUMULATIVE_VALUE = 0.0;
    public static final int LADDER_BLOCK_FUNCTIONS = 2;

    public static class BlockFunction {

        private String address;
        private double value;
        private int radix;
        private boolean isNumber;

        public BlockFunction() {
            this.address = LADDER_GRID_INITIAL_BLOCK_FUNCTION_ADDRESS;
            this.value = LADDER_GRID_INITIAL_BLOCK_FUNCTION_VALUE;
            this.radix = LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX;
            this.isNumber = LADDER_GRID_INITIAL_BLOCK_FUNCTION_NUMBER;
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public int getRadix() {
            return this.radix;
        }

        public void setRadix(int radix) {
            this.radix = radix;
        }

        public boolean isNumber() {
            return this.isNumber;
        }

        public void setNumber(boolean isNumber) {
            this.isNumber = isNumber;
        }

        public void clear() {
            this.address = LADDER_GRID_INITIAL_BLOCK_FUNCTION_ADDRESS;
            this.value = LADDER_GRID_INITIAL_BLOCK_FUNCTION_VALUE;
            this.radix = LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX;
            this.isNumber = LADDER_GRID_INITIAL_BLOCK_FUNCTION_NUMBER;
        }
    }

    private int columnIndex_, rowIndex_, colspan_, rowspan_;
    private LadderGrid leftLadderGrid_, upLadderGrid_, rightLadderGrid_, downLadderGrid_;
    private int inConnectNumber_, outConnectNumber_;

    private Ladders.LADDER_BLOCK block_;
    private double blockValue_;
    private String blockScript_;
    private boolean isBlockLd_;

    private BlockFunction[] blockFunctions_;

    private boolean isSelect_, isSelectRange_;
    private boolean isVertical_, isVerticalOr_;
    private String address_, comment_;
    private double cumulativeValue_;

    /**
     *
     * @param columnIndex
     * @param rowIndex
     */
    public LadderGrid(int columnIndex, int rowIndex) {
        this(columnIndex, rowIndex, 1, 1, null, null, null, null);
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param colspan
     * @param rowspan
     */
    public LadderGrid(int columnIndex, int rowIndex, int colspan, int rowspan) {
        this(columnIndex, rowIndex, colspan, rowspan, null, null, null, null);
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param colspan
     * @param rowspan
     * @param leftLadderGrid
     * @param upLadderGrid
     * @param rightLadderGrid
     * @param downLadderGrid
     */
    public LadderGrid(int columnIndex, int rowIndex, int colspan, int rowspan, LadderGrid leftLadderGrid, LadderGrid upLadderGrid, LadderGrid rightLadderGrid, LadderGrid downLadderGrid) {
        columnIndex_ = columnIndex;
        rowIndex_ = rowIndex;
        colspan_ = colspan;
        rowspan_ = rowspan;
        blockFunctions_ = new BlockFunction[LADDER_BLOCK_FUNCTIONS];
        for (int i = 0; i < LADDER_BLOCK_FUNCTIONS; i++) {
            blockFunctions_[i] = new BlockFunction();
        }

        init();
    }

    private void init() {
        clear();
        leftLadderGrid_ = null;
        upLadderGrid_ = null;
        rightLadderGrid_ = null;
        downLadderGrid_ = null;
        isSelect_ = false;
        isSelectRange_ = false;
        isVertical_ = LADDER_GRID_INITIAL_VERTICAL;
        isVerticalOr_ = LADDER_GRID_INITIAL_VERTICAL_OR;
    }

    /**
     *
     */
    public void clear() {
        inConnectNumber_ = 0;
        outConnectNumber_ = 0;

        block_ = LADDER_GRID_INITIAL_BLOCK;
        blockValue_ = LADDER_GRID_INITIAL_BLOCK_VALUE;
        for (int i = 0; i < LADDER_BLOCK_FUNCTIONS; i++) {
            blockFunctions_[i].clear();
        }
        blockScript_ = LADDER_GRID_INITIAL_BLOCK_SCRIPT;
        isBlockLd_ = false;
        isSelect_ = false;
        isSelectRange_ = false;

        address_ = LADDER_GRID_INITIAL_ADDRESS;
        comment_ = LADDER_GRID_INITIAL_COMMENT;
        cumulativeValue_ = LADDER_GRID_INITIAL_CUMULATIVE_VALUE;
    }

    /**
     *
     * @return
     */
    public LadderGrid copy() {
        int i;

        LadderGrid grid = new LadderGrid(columnIndex_, rowIndex_, colspan_, rowspan_, leftLadderGrid_, upLadderGrid_, rightLadderGrid_, downLadderGrid_);
        grid.setInConnectNumber(inConnectNumber_);
        grid.setOutConnectNumber(outConnectNumber_);
        grid.setBlock(block_);
        grid.setBlockValue(blockValue_);
        for (i = 0; i < LADDER_BLOCK_FUNCTIONS; i++) {
            grid.getBlockFunctions()[i].setAddress(blockFunctions_[i].getAddress());
            grid.getBlockFunctions()[i].setValue(blockFunctions_[i].getValue());
            grid.getBlockFunctions()[i].setRadix(blockFunctions_[i].getRadix());
            grid.getBlockFunctions()[i].setNumber(blockFunctions_[i].isNumber());
        }
        grid.setBlockScript(blockScript_);
        grid.setBlockLd(isBlockLd_);
        grid.setVertical(isVertical_);
        grid.setVerticalOr(isVerticalOr_);
        grid.setAddress(address_);
        grid.setComment(comment_);
        grid.setCumulativeValue(cumulativeValue_);
        return grid;
    }

    /**
     *
     * @return
     */
    public int getColumnIndex() {
        return columnIndex_;
    }

    /**
     *
     * @param columnIndex
     */
    public void setColumnIndex(int columnIndex) {
        columnIndex_ = columnIndex;
    }

    /**
     *
     * @return
     */
    public int getRowIndex() {
        return rowIndex_;
    }

    /**
     *
     * @param rowIndex
     */
    public void setRowIndex(int rowIndex) {
        rowIndex_ = rowIndex;
    }

    /**
     *
     * @return
     */
    public int getColSpan() {
        return colspan_;
    }

    /**
     *
     * @param colspan
     */
    public void setColSpan(int colspan) {
        colspan_ = colspan;
    }

    /**
     *
     * @return
     */
    public int getRowSpan() {
        return rowspan_;
    }

    /**
     *
     * @param rowspan
     */
    public void setRowSpan(int rowspan) {
        rowspan_ = rowspan;
    }

    /**
     *
     * @return
     */
    public LadderGrid getLeftLadderGrid() {
        return leftLadderGrid_;
    }

    /**
     *
     * @param leftLadderGrid
     */
    public void setLeftLadderGrid(LadderGrid leftLadderGrid) {
        leftLadderGrid_ = leftLadderGrid;
    }

    /**
     *
     * @return
     */
    public LadderGrid getUpLadderGrid() {
        return upLadderGrid_;
    }

    /**
     *
     * @param upLadderGrid
     */
    public void setUpLadderGrid(LadderGrid upLadderGrid) {
        upLadderGrid_ = upLadderGrid;
    }

    /**
     *
     * @return
     */
    public LadderGrid getRightLadderGrid() {
        return rightLadderGrid_;
    }

    /**
     *
     * @param rightLadderGrid
     */
    public void setRightLadderGrid(LadderGrid rightLadderGrid) {
        rightLadderGrid_ = rightLadderGrid;
    }

    /**
     *
     * @return
     */
    public LadderGrid getDownLadderGrid() {
        return downLadderGrid_;
    }

    /**
     *
     * @param downLadderGrid
     */
    public void setDownLadderGrid(LadderGrid downLadderGrid) {
        downLadderGrid_ = downLadderGrid;
    }

    /**
     *
     * @return
     */
    public int getInConnectNumber() {
        return inConnectNumber_;
    }

    /**
     *
     * @param connectNumber
     */
    public void setInConnectNumber(int connectNumber) {
        inConnectNumber_ = connectNumber;
    }

    /**
     *
     * @return
     */
    public int getOutConnectNumber() {
        return outConnectNumber_;
    }

    /**
     *
     * @param connectNumber
     */
    public void setOutConnectNumber(int connectNumber) {
        outConnectNumber_ = connectNumber;
    }

    /**
     *
     * @return
     */
    public Ladders.LADDER_BLOCK getBlock() {
        return block_;
    }

    /**
     *
     * @param block
     * @return
     */
    public boolean setBlock(Ladders.LADDER_BLOCK block) {
        if (block_ != block) {
            block_ = block;
            blockScript_ = LADDER_GRID_INITIAL_BLOCK_SCRIPT;
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public double getBlockValue() {
        return blockValue_;
    }

    /**
     *
     * @param blockValue
     */
    public void setBlockValue(double blockValue) {
        blockValue_ = blockValue;
    }

    /**
     *
     * @return
     */
    public BlockFunction[] getBlockFunctions() {
        return blockFunctions_;
    }

    /**
     *
     * @param blockFunctions
     */
    public void setBlockFunctions(BlockFunction[] blockFunctions) {
        for (int i = 0; i < LADDER_BLOCK_FUNCTIONS; i++) {
            blockFunctions_[i].setAddress(blockFunctions[i].getAddress());
            blockFunctions_[i].setValue(blockFunctions[i].getValue());
            blockFunctions_[i].setRadix(blockFunctions[i].getRadix());
            blockFunctions_[i].setNumber(blockFunctions[i].isNumber());
        }
    }

    /**
     *
     * @return
     */
    public String getBlockScript() {
        return blockScript_;
    }

    /**
     *
     * @param blockScript
     */
    public void setBlockScript(String blockScript) {
        blockScript_ = blockScript;
    }

    /**
     *
     * @return
     */
    public boolean isBlockLd() {
        return isBlockLd_;
    }

    /**
     *
     * @param isBlockLd
     */
    public void setBlockLd(boolean isBlockLd) {
        isBlockLd_ = isBlockLd;
    }

    /**
     *
     * @return
     */
    public boolean isSelect() {
        return isSelect_;
    }

    /**
     *
     * @param isSelect
     * @return
     */
    public boolean setSelect(boolean isSelect) {
        if (isSelect_ != isSelect) {
            isSelect_ = isSelect;
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isSelectRange() {
        return isSelectRange_;
    }

    /**
     *
     * @param isSelectRange
     * @return
     */
    public boolean setSelectRange(boolean isSelectRange) {
        if (isSelectRange_ != isSelectRange) {
            isSelectRange_ = isSelectRange;
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isVertical() {
        return isVertical_;
    }

    /**
     *
     * @param isVertical
     * @return
     */
    public boolean setVertical(boolean isVertical) {
        if (isVertical_ != isVertical) {
            isVertical_ = isVertical;
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isVerticalOr() {
        return isVerticalOr_;
    }

    /**
     *
     * @param isVerticalOr
     * @return
     */
    public boolean setVerticalOr(boolean isVerticalOr) {
        if (isVerticalOr_ != isVerticalOr) {
            isVerticalOr_ = isVerticalOr;
            return true;
        }
        return false;
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
     * @param address
     */
    public void setAddress(String address) {
        address_ = address;
    }

    /**
     *
     * @return
     */
    public String getComment() {
        return comment_;
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        comment_ = comment;
    }

    /**
     *
     * @return
     */
    public double getCumulativeValue() {
        return cumulativeValue_;
    }

    /**
     *
     * @param cumulativeValue
     */
    public void setCumulativeValue(double cumulativeValue) {
        cumulativeValue_ = cumulativeValue;
    }
}
