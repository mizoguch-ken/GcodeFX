/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javafx.scene.control.TableView;
import ken.mizoguch.console.Console;

/**
 *
 * @author mizoguch-ken
 */
public class Ladder {

    private final List<LadderGrid> ladderGrids_;
    private final List<LadderGrid> inGrids_;
    private final List<LadderGrid> outGrids_;
    private final Map<Integer, List<LadderGrid>> inConnectGrids_;
    private final Map<Integer, List<LadderGrid>> outConnectGrids_;

    private int[] connectNumbers_;
    private int[] runConnectNumbers_;
    private int column_, row_;
    private int index_;
    private String name_;
    private boolean isConnectFailed_;

    private static final Pattern PATTERN_REAL_NUMBER = Pattern.compile("^[\\+\\-]?(?:((?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][\\+\\-]?\\d+|))|0[xX]([0-9a-fA-F]+)|0[bB]([0-1]+))$");

    /**
     *
     * @param index
     * @param name
     * @param gridColumn
     * @param gridRow
     */
    public Ladder(int index, String name, int gridColumn, int gridRow) {
        ladderGrids_ = new ArrayList<>();
        inGrids_ = new ArrayList<>();
        outGrids_ = new ArrayList<>();
        inConnectGrids_ = new HashMap<>();
        outConnectGrids_ = new HashMap<>();

        column_ = gridColumn;
        row_ = gridRow;
        connectNumbers_ = new int[column_ * row_];
        runConnectNumbers_ = new int[column_ * row_];

        index_ = index;
        name_ = name;
        isConnectFailed_ = false;
    }

    /**
     *
     * @return
     */
    public Ladder copy() {
        Ladder ladder = new Ladder(index_, name_, column_, row_);
        int index, size;

        for (index = 0, size = ladderGrids_.size(); index < size; index++) {
            ladder.getLadderGrids().add(ladderGrids_.get(index).copy());
        }
        ladder.connectGrids();
        return ladder;
    }

    /**
     *
     * @param grid
     */
    public void removeLadderGrid(LadderGrid grid) {
        ladderGrids_.remove(grid);
    }

    /**
     *
     * @param grid
     */
    public void addLadderGrid(LadderGrid grid) {
        ladderGrids_.add(grid);
    }

    /**
     *
     * @return
     */
    public List<LadderGrid> getLadderGrids() {
        return ladderGrids_;
    }

    /**
     *
     * @return
     */
    public List<LadderGrid> getInGrids() {
        return inGrids_;
    }

    /**
     *
     * @param inGrids
     */
    public void setInGrids(List<LadderGrid> inGrids) {
        int index, size;

        inGrids_.clear();
        for (index = 0, size = inGrids.size(); index < size; index++) {
            inGrids_.add(inGrids.get(index));
        }
    }

    /**
     *
     * @return
     */
    public List<LadderGrid> getOutGrids() {
        return outGrids_;
    }

    /**
     *
     * @param OutGrids
     */
    public void setOutGrids(List<LadderGrid> OutGrids) {
        int index, size;

        outGrids_.clear();
        for (index = 0, size = OutGrids.size(); index < size; index++) {
            outGrids_.add(OutGrids.get(index));
        }
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
        if (column_ != column) {
            column_ = column;
            connectNumbers_ = new int[column_ * row_];
        }
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
        if (row_ != row) {
            row_ = row;
            connectNumbers_ = new int[column_ * row_];
        }
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return index_;
    }

    /**
     *
     * @param index
     */
    public void setIndex(int index) {
        index_ = index;
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
     */
    public void setName(String name) {
        name_ = name;
    }

    /**
     *
     * @param address
     * @param comment
     */
    public void setComments(String address, String comment) {
        LadderGrid grid;
        int index, size;

        for (index = 0, size = ladderGrids_.size(); index < size; index++) {
            grid = ladderGrids_.get(index);
            if (address.equals(grid.getAddress())) {
                grid.setComment(comment);
            }
        }
    }

    /**
     *
     */
    public void connectGrids() {
        LadderGrid[] gridUps = new LadderGrid[column_];
        LadderGrid grid, gridLeft;
        int columnIndex, columnSize, rowIndex, rowSize;

        for (rowIndex = 1, rowSize = row_; rowIndex < rowSize; rowIndex++) {
            gridLeft = null;
            for (columnIndex = 1, columnSize = column_; columnIndex < columnSize; columnIndex++) {
                grid = findGrid(columnIndex, rowIndex);
                grid.setLeftLadderGrid(gridLeft);
                grid.setUpLadderGrid(gridUps[columnIndex]);
                if (gridLeft != null) {
                    gridLeft.setRightLadderGrid(grid);
                }
                if (gridUps[columnIndex] != null) {
                    gridUps[columnIndex].setDownLadderGrid(grid);
                }
                gridUps[columnIndex] = grid;
                gridLeft = grid;
            }
        }
    }

    /**
     *
     * @param ioMap
     * @param ioTable
     * @return
     */
    public boolean connectLadder(ConcurrentHashMap<String, LadderIo> ioMap, TableView<LadderTableIo> ioTable) {
        LadderGrid grid = findGrid(1, 1);
        int connectNumberLast;

        Arrays.fill(connectNumbers_, 0);
        inGrids_.clear();
        outGrids_.clear();
        inConnectGrids_.clear();
        outConnectGrids_.clear();
        isConnectFailed_ = false;

        connectNumberLast = 1;
        while (grid != null) {
            if (grid.getBlock() != Ladders.LADDER_BLOCK.EMPTY) {
                if (connectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] == 0) {
                    connectNumberLast = connectRoute(ioMap, ioTable, grid, 1, connectNumberLast);
                }
            }
            grid = grid.getDownLadderGrid();
        }
        outGrids_.sort((LadderGrid t, LadderGrid t1) -> {
            return t.getRowIndex() - t1.getRowIndex();
        });

        runConnectNumbers_ = new int[column_ * row_];
        return !isConnectFailed_;
    }

    private int connectRoute(ConcurrentHashMap<String, LadderIo> ioMap, TableView<LadderTableIo> ioTable, LadderGrid grid, int connectNumber, int connectNumberLast) {
        LadderGrid lgrid, ugrid, rgrid, dgrid;
        String address, functionAddress;

        if (grid != null) {
            lgrid = grid.getLeftLadderGrid();
            ugrid = grid.getUpLadderGrid();
            rgrid = grid.getRightLadderGrid();
            dgrid = grid.getDownLadderGrid();

            if (connectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] == 0) {
                switch (grid.getBlock()) {
                    case LOAD:
                    case LOAD_NOT:
                    case LOAD_RISING:
                    case LOAD_RISING_NOT:
                    case LOAD_FALLING:
                    case LOAD_FALLING_NOT:
                        address = grid.getAddress();
                        if (!PATTERN_REAL_NUMBER.matcher(address).find()) {
                            if (!ioMap.containsKey(address)) {
                                ioMap.put(address, new LadderIo(address));
                                ioTable.getItems().add(new LadderTableIo(address));
                            }
                            grid.setInConnectNumber(connectNumber);
                            grid.setOutConnectNumber(0);
                            inGrids_.add(grid);
                            inConnectGrids_.putIfAbsent(connectNumber, new ArrayList<>());
                            inConnectGrids_.get(connectNumber).add(grid);
                        } else {
                            writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                    case COMPARISON_EQUAL:
                    case COMPARISON_NOT_EQUAL:
                    case COMPARISON_LESS:
                    case COMPARISON_LESS_EQUAL:
                    case COMPARISON_GREATER:
                    case COMPARISON_GREATER_EQUAL:
                    case COMPARISON_AND_BITS:
                    case COMPARISON_OR_BITS:
                    case COMPARISON_XOR_BITS:
                        address = grid.getAddress();
                        if (!PATTERN_REAL_NUMBER.matcher(address).find()) {
                            if (!ioMap.containsKey(address)) {
                                ioMap.put(address, new LadderIo(address));
                                ioTable.getItems().add(new LadderTableIo(address));
                            }
                            if (!grid.getBlockFunctions()[0].isNumber()) {
                                functionAddress = grid.getBlockFunctions()[0].getAddress();
                                if (!PATTERN_REAL_NUMBER.matcher(functionAddress).find()) {
                                    if (!ioMap.containsKey(functionAddress)) {
                                        ioMap.put(functionAddress, new LadderIo(functionAddress));
                                        ioTable.getItems().add(new LadderTableIo(functionAddress));
                                    }
                                } else {
                                    writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                                    isConnectFailed_ = true;
                                }
                            }
                            grid.setInConnectNumber(connectNumber);
                            grid.setOutConnectNumber(0);
                            inGrids_.add(grid);
                            inConnectGrids_.putIfAbsent(connectNumber, new ArrayList<>());
                            inConnectGrids_.get(connectNumber).add(grid);
                        } else {
                            writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                    case OUT:
                    case OUT_NOT:
                    case OUT_RISING:
                    case OUT_RISING_NOT:
                    case OUT_FALLING:
                    case OUT_FALLING_NOT:
                    case SET:
                    case RESET:
                    case NOT_BITS:
                    case RANDOM:
                    case SCRIPT:
                        address = grid.getAddress();
                        if (!PATTERN_REAL_NUMBER.matcher(address).find()) {
                            if (!ioMap.containsKey(address)) {
                                ioMap.put(address, new LadderIo(address));
                                ioTable.getItems().add(new LadderTableIo(address));
                            }
                            grid.setInConnectNumber(connectNumber);
                            grid.setOutConnectNumber(0);
                            outGrids_.add(grid);
                            inConnectGrids_.putIfAbsent(connectNumber, new ArrayList<>());
                            inConnectGrids_.get(connectNumber).add(grid);
                        } else {
                            writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                    case AND_BITS:
                    case OR_BITS:
                    case XOR_BITS:
                    case ADDITION:
                    case SUBTRACTION:
                    case MULTIPLICATION:
                    case DIVISION:
                    case AVERAGE:
                    case SHIFT_LEFT_BITS:
                    case SHIFT_RIGHT_BITS:
                    case SIGMOID:
                        address = grid.getAddress();
                        if (!PATTERN_REAL_NUMBER.matcher(address).find()) {
                            if (!ioMap.containsKey(address)) {
                                ioMap.put(address, new LadderIo(address));
                                ioTable.getItems().add(new LadderTableIo(address));
                            }
                            if (!grid.getBlockFunctions()[0].isNumber()) {
                                functionAddress = grid.getBlockFunctions()[0].getAddress();
                                if (!PATTERN_REAL_NUMBER.matcher(functionAddress).find()) {
                                    if (!ioMap.containsKey(functionAddress)) {
                                        ioMap.put(functionAddress, new LadderIo(functionAddress));
                                        ioTable.getItems().add(new LadderTableIo(functionAddress));
                                    }
                                } else {
                                    writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                                    isConnectFailed_ = true;
                                }
                            }
                            if (!grid.getBlockFunctions()[1].isNumber()) {
                                functionAddress = grid.getBlockFunctions()[1].getAddress();
                                if (!PATTERN_REAL_NUMBER.matcher(functionAddress).find()) {
                                    if (!ioMap.containsKey(functionAddress)) {
                                        ioMap.put(functionAddress, new LadderIo(functionAddress));
                                        ioTable.getItems().add(new LadderTableIo(functionAddress));
                                    }
                                } else {
                                    writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                                    isConnectFailed_ = true;
                                }
                            }
                            grid.setInConnectNumber(connectNumber);
                            grid.setOutConnectNumber(0);
                            outGrids_.add(grid);
                            inConnectGrids_.putIfAbsent(connectNumber, new ArrayList<>());
                            inConnectGrids_.get(connectNumber).add(grid);
                        } else {
                            writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                    case TIMER:
                    case TIMER_NOT:
                    case COUNTER:
                    case COUNTER_NOT:
                    case MOVE:
                        address = grid.getAddress();
                        if (!PATTERN_REAL_NUMBER.matcher(address).find()) {
                            if (!ioMap.containsKey(address)) {
                                ioMap.put(address, new LadderIo(address));
                                ioTable.getItems().add(new LadderTableIo(address));
                            }
                            if (!grid.getBlockFunctions()[0].isNumber()) {
                                functionAddress = grid.getBlockFunctions()[0].getAddress();
                                if (!PATTERN_REAL_NUMBER.matcher(functionAddress).find()) {
                                    if (!ioMap.containsKey(functionAddress)) {
                                        ioMap.put(functionAddress, new LadderIo(functionAddress));
                                        ioTable.getItems().add(new LadderTableIo(functionAddress));
                                    }
                                } else {
                                    writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                                    isConnectFailed_ = true;
                                }
                            }
                            grid.setInConnectNumber(connectNumber);
                            grid.setOutConnectNumber(0);
                            outGrids_.add(grid);
                            inConnectGrids_.putIfAbsent(connectNumber, new ArrayList<>());
                            inConnectGrids_.get(connectNumber).add(grid);
                        } else {
                            writeLog(LadderEnums.ADDRESS_NUMERIC_ONLY_ERROR.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                }
                connectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = connectNumber;
            } else {
                writeLog(LadderEnums.CONNECTION_FAILED.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]", true);
                isConnectFailed_ = true;
            }

            if (lgrid != null) {
                switch (lgrid.getBlock()) {
                    case CONNECT_LINE:
                        if (connectNumbers_[lgrid.getColumnIndex() + (lgrid.getRowIndex() * column_)] == 0) {
                            connectNumberLast = connectRoute(ioMap, ioTable, lgrid, connectNumber, connectNumberLast);
                        } else if (connectNumbers_[lgrid.getColumnIndex() + (lgrid.getRowIndex() * column_)] != connectNumber) {
                            writeLog(LadderEnums.CONNECTION_FAILED.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]->[" + lgrid.getColumnIndex() + ":" + lgrid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                }
            }

            if (ugrid != null) {
                if (grid.isVertical()) {
                    if (connectNumbers_[ugrid.getColumnIndex() + (ugrid.getRowIndex() * column_)] == 0) {
                        connectNumberLast = connectRoute(ioMap, ioTable, ugrid, connectNumber, connectNumberLast);
                    } else if (connectNumbers_[ugrid.getColumnIndex() + (ugrid.getRowIndex() * column_)] != connectNumber) {
                        writeLog(LadderEnums.CONNECTION_FAILED.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]->[" + ugrid.getColumnIndex() + ":" + ugrid.getRowIndex() + "]", true);
                        isConnectFailed_ = true;
                    }
                }
            }

            if (rgrid != null) {
                switch (grid.getBlock()) {
                    case CONNECT_LINE:
                        if (connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)] == 0) {
                            connectNumberLast = connectRoute(ioMap, ioTable, rgrid, connectNumber, connectNumberLast);
                        } else if (connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)] != connectNumber) {
                            writeLog(LadderEnums.CONNECTION_FAILED.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]->[" + rgrid.getColumnIndex() + ":" + rgrid.getRowIndex() + "]", true);
                            isConnectFailed_ = true;
                        }
                        break;
                    case LOAD:
                    case LOAD_NOT:
                    case LOAD_RISING:
                    case LOAD_RISING_NOT:
                    case LOAD_FALLING:
                    case LOAD_FALLING_NOT:
                    case COMPARISON_EQUAL:
                    case COMPARISON_NOT_EQUAL:
                    case COMPARISON_LESS:
                    case COMPARISON_LESS_EQUAL:
                    case COMPARISON_GREATER:
                    case COMPARISON_GREATER_EQUAL:
                    case COMPARISON_AND_BITS:
                    case COMPARISON_OR_BITS:
                    case COMPARISON_XOR_BITS:
                        if (connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)] == 0) {
                            grid.setOutConnectNumber(connectNumberLast + 1);
                            outConnectGrids_.putIfAbsent(connectNumberLast + 1, new ArrayList<>());
                            outConnectGrids_.get(connectNumberLast + 1).add(grid);
                            connectNumberLast = connectRoute(ioMap, ioTable, rgrid, connectNumberLast + 1, connectNumberLast + 1);
                        } else {
                            if (grid.getOutConnectNumber() == 0) {
                                grid.setOutConnectNumber(connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)]);
                                outConnectGrids_.putIfAbsent(connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)], new ArrayList<>());
                                outConnectGrids_.get(connectNumbers_[rgrid.getColumnIndex() + (rgrid.getRowIndex() * column_)]).add(grid);
                            }
                        }
                        break;
                }
            }

            if (dgrid != null) {
                if (grid.isVerticalOr()) {
                    if (connectNumbers_[dgrid.getColumnIndex() + (dgrid.getRowIndex() * column_)] == 0) {
                        connectNumberLast = connectRoute(ioMap, ioTable, dgrid, connectNumber, connectNumberLast);
                    } else if (connectNumbers_[dgrid.getColumnIndex() + (dgrid.getRowIndex() * column_)] != connectNumber) {
                        writeLog(LadderEnums.CONNECTION_FAILED.toString() + " [" + grid.getColumnIndex() + ":" + grid.getRowIndex() + "]->[" + dgrid.getColumnIndex() + ":" + dgrid.getRowIndex() + "]", true);
                        isConnectFailed_ = true;
                    }
                }
            }
        }
        return connectNumberLast;
    }

    /**
     *
     * @param ioMap
     * @param cycleTime
     */
    public void run(ConcurrentHashMap<String, LadderIo> ioMap, long cycleTime) {
        LadderGrid grid;
        int index, size;

        Arrays.fill(runConnectNumbers_, 0);
        for (index = 0, size = outGrids_.size(); index < size; index++) {
            grid = outGrids_.get(index);
            if (runRouteBegin(ioMap, grid)) {
                switch (grid.getBlock()) {
                    case OUT:
                        ioMap.get(grid.getAddress()).setValue(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OUT_NOT:
                        ioMap.get(grid.getAddress()).setValueNot(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OUT_RISING:
                        ioMap.get(grid.getAddress()).setValueRising(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OUT_RISING_NOT:
                        ioMap.get(grid.getAddress()).setValueRisingNot(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OUT_FALLING:
                        ioMap.get(grid.getAddress()).setValueFalling(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OUT_FALLING_NOT:
                        ioMap.get(grid.getAddress()).setValueFallingNot(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SET:
                        ioMap.get(grid.getAddress()).setValue(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case RESET:
                        ioMap.get(grid.getAddress()).resetValue();
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case AND_BITS:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() & (long) grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() & (long) grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() & (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() & (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case OR_BITS:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() | (long) grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() | (long) grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() | (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() | (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case XOR_BITS:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() ^ (long) grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() ^ (long) grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() ^ (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() ^ (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case NOT_BITS:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(~(long) grid.getBlockFunctions()[0].getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(~(long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case ADDITION:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() + grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() + grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() + ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() + ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SUBTRACTION:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() - grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() - grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() - ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() - ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case MULTIPLICATION:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() * grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() * grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() * ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() * ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case DIVISION:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() / grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() / grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue() / ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() / ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case AVERAGE:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((grid.getBlockFunctions()[0].getValue() + grid.getBlockFunctions()[1].getValue()) / 2.0);
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() + grid.getBlockFunctions()[1].getValue()) / 2.0);
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((grid.getBlockFunctions()[0].getValue() + ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue()) / 2.0);
                        } else {
                            ioMap.get(grid.getAddress()).setValue((ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() + ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue()) / 2.0);
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SHIFT_LEFT_BITS:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() << (long) grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() << (long) grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() << (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() << (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SHIFT_RIGHT_BITS:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() >> (long) grid.getBlockFunctions()[1].getValue());
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() >> (long) grid.getBlockFunctions()[1].getValue());
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((long) grid.getBlockFunctions()[0].getValue() >> (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() >> (long) ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SIGMOID:
                        if (grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((Math.tanh(grid.getBlockFunctions()[0].getValue() * grid.getBlockFunctions()[1].getValue() / 2.0) + 1.0) / 2.0);
                        } else if (!grid.getBlockFunctions()[0].isNumber() && grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((Math.tanh(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() * grid.getBlockFunctions()[1].getValue() / 2.0) + 1.0) / 2.0);
                        } else if (grid.getBlockFunctions()[0].isNumber() && !grid.getBlockFunctions()[1].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue((Math.tanh(grid.getBlockFunctions()[0].getValue() * ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue() / 2.0) + 1.0) / 2.0);
                        } else {
                            ioMap.get(grid.getAddress()).setValue((Math.tanh(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue() * ioMap.get(grid.getBlockFunctions()[1].getAddress()).getValue() / 2.0) + 1.0) / 2.0);
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case RANDOM:
                        ioMap.get(grid.getAddress()).setValue(Math.random());
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case TIMER:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValueTimer(1, grid.getBlockFunctions()[0].getValue(), cycleTime);
                        } else {
                            ioMap.get(grid.getAddress()).setValueTimer(1, ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue(), cycleTime);
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case TIMER_NOT:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValueTimerNot(1, grid.getBlockFunctions()[0].getValue(), cycleTime);
                        } else {
                            ioMap.get(grid.getAddress()).setValueTimerNot(1, ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue(), cycleTime);
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case COUNTER:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValueCounter(1, grid.getBlockFunctions()[0].getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValueCounter(1, ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case COUNTER_NOT:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValueCounterNot(1, grid.getBlockFunctions()[0].getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValueCounterNot(1, ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case MOVE:
                        if (grid.getBlockFunctions()[0].isNumber()) {
                            ioMap.get(grid.getAddress()).setValue(grid.getBlockFunctions()[0].getValue());
                        } else {
                            ioMap.get(grid.getAddress()).setValue(ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue());
                        }
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                    case SCRIPT:
                        ioMap.get(grid.getAddress()).setScript(grid.getBlockScript());
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = 1;
                        break;
                }
            } else {
                switch (grid.getBlock()) {
                    case OUT:
                        ioMap.get(grid.getAddress()).setValue(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case OUT_NOT:
                        ioMap.get(grid.getAddress()).setValueNot(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case OUT_RISING:
                        ioMap.get(grid.getAddress()).setValueRising(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case OUT_RISING_NOT:
                        ioMap.get(grid.getAddress()).setValueRisingNot(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case OUT_FALLING:
                        ioMap.get(grid.getAddress()).setValueFalling(0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case OUT_FALLING_NOT:
                        ioMap.get(grid.getAddress()).setValueFallingNot(1);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case TIMER:
                        ioMap.get(grid.getAddress()).setValueTimer(0, 0, 0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case TIMER_NOT:
                        ioMap.get(grid.getAddress()).setValueTimerNot(0, 0, 0);
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                    case SET:
                    case RESET:
                    case AND_BITS:
                    case OR_BITS:
                    case XOR_BITS:
                    case NOT_BITS:
                    case ADDITION:
                    case SUBTRACTION:
                    case MULTIPLICATION:
                    case DIVISION:
                    case AVERAGE:
                    case SHIFT_LEFT_BITS:
                    case SHIFT_RIGHT_BITS:
                    case SIGMOID:
                    case RANDOM:
                    case COUNTER:
                    case COUNTER_NOT:
                    case MOVE:
                    case SCRIPT:
                        ioMap.get(grid.getAddress()).chehckEdge();
                        runConnectNumbers_[grid.getColumnIndex() + (grid.getRowIndex() * column_)] = -1;
                        break;
                }
            }
        }
    }

    private boolean runRouteBegin(ConcurrentHashMap<String, LadderIo> ioMap, LadderGrid grid) {
        if (grid != null) {
            int connectNumber = grid.getInConnectNumber();

            if (connectNumber > 1) {
                LadderGrid ladderGrid;
                int index, size;
                for (index = 0, size = outConnectGrids_.get(connectNumber).size(); index < size; index++) {
                    ladderGrid = outConnectGrids_.get(connectNumber).get(index);
                    if (runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] == 0) {
                        if (runRoute(ioMap, ladderGrid)) {
                            runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] = 1;
                            return true;
                        }
                        runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] = -1;
                    } else if (runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean runRoute(ConcurrentHashMap<String, LadderIo> ioMap, LadderGrid grid) {
        if (grid != null) {
            boolean result;

            switch (grid.getBlock()) {
                case LOAD:
                    result = ioMap.get(grid.getAddress()).isLd();
                    break;
                case LOAD_NOT:
                    result = !ioMap.get(grid.getAddress()).isLd();
                    break;
                case LOAD_RISING:
                    result = ioMap.get(grid.getAddress()).isLdRising();
                    break;
                case LOAD_RISING_NOT:
                    result = !ioMap.get(grid.getAddress()).isLdRising();
                    break;
                case LOAD_FALLING:
                    result = ioMap.get(grid.getAddress()).isLdFalling();
                    break;
                case LOAD_FALLING_NOT:
                    result = !ioMap.get(grid.getAddress()).isLdFalling();
                    break;
                case COMPARISON_EQUAL:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() == grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() == ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_NOT_EQUAL:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() != grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() != ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_LESS:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() < grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() < ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_LESS_EQUAL:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() <= grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() <= ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_GREATER:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() > grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() > ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_GREATER_EQUAL:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = ioMap.get(grid.getAddress()).getValue() >= grid.getBlockFunctions()[0].getValue();
                    } else {
                        result = ioMap.get(grid.getAddress()).getValue() >= ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue();
                    }
                    break;
                case COMPARISON_AND_BITS:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) & ((long) grid.getBlockFunctions()[0].getValue())) != 0;
                    } else {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) & ((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue())) != 0;
                    }
                    break;
                case COMPARISON_OR_BITS:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) | ((long) grid.getBlockFunctions()[0].getValue())) != 0;
                    } else {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) | ((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue())) != 0;
                    }
                    break;
                case COMPARISON_XOR_BITS:
                    if (grid.getBlockFunctions()[0].isNumber()) {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) ^ ((long) grid.getBlockFunctions()[0].getValue())) != 0;
                    } else {
                        result = (((long) ioMap.get(grid.getAddress()).getValue()) ^ ((long) ioMap.get(grid.getBlockFunctions()[0].getAddress()).getValue())) != 0;
                    }
                    break;
                default:
                    result = false;
                    break;
            }

            if (result) {
                int connectNumber = grid.getInConnectNumber();

                if (connectNumber > 1) {
                    LadderGrid ladderGrid;
                    int index, size;

                    for (index = 0, size = outConnectGrids_.get(connectNumber).size(); index < size; index++) {
                        ladderGrid = outConnectGrids_.get(connectNumber).get(index);
                        if (runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] == 0) {
                            if (runRoute(ioMap, ladderGrid)) {
                                runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] = 1;
                                return true;
                            }
                            runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] = -1;
                        } else if (runConnectNumbers_[ladderGrid.getColumnIndex() + (ladderGrid.getRowIndex() * column_)] > 0) {
                            return true;
                        }
                    }
                } else {
                    return result;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param column
     * @param row
     * @return
     */
    public LadderGrid findGrid(int column, int row) {
        LadderGrid grid;
        int index, size;

        for (index = 0, size = ladderGrids_.size(); index < size; index++) {
            grid = ladderGrids_.get(index);
            if ((grid.getColumnIndex() == column) && (grid.getRowIndex() == row)) {
                return grid;
            }
        }
        return null;
    }

    /**
     *
     * @param grid
     * @return
     */
    public boolean checkGridOut(LadderGrid grid) {
        LadderGrid rgrid;

        while (grid != null) {
            rgrid = grid.getRightLadderGrid();
            if (rgrid != null) {
                if (rgrid.isVertical() && rgrid.isVerticalOr()) {
                    return false;
                }
            }

            switch (grid.getBlock()) {
                case LOAD:
                case LOAD_NOT:
                case LOAD_RISING:
                case LOAD_RISING_NOT:
                case LOAD_FALLING:
                case LOAD_FALLING_NOT:
                case COMPARISON_EQUAL:
                case COMPARISON_NOT_EQUAL:
                case COMPARISON_LESS:
                case COMPARISON_LESS_EQUAL:
                case COMPARISON_GREATER:
                case COMPARISON_GREATER_EQUAL:
                case COMPARISON_AND_BITS:
                case COMPARISON_OR_BITS:
                case COMPARISON_XOR_BITS:
                    return false;
            }
            grid = rgrid;
        }
        return true;
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(LadderEnums.LADDER.toString(), name_ + " : " + msg, err);
    }
}
