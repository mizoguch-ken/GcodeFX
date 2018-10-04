/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import static javafx.scene.layout.GridPane.setColumnIndex;
import static javafx.scene.layout.GridPane.setRowIndex;
import javafx.stage.Stage;

/**
 *
 * @author mizoguch-ken
 */
public class LadderPane extends GridPane {

    private Stage stage_;
    private List<Image> icons_;

    private final Ladders ladders_;
    private final LadderCommand ladderCommand_;
    private final Ladder ladder_;

    private final List<LadderGrid> previousGrids_;
    private final double gridMinSize_, gridMaxSize_, gridContentsWidth_, gridContentsHight_;
    private boolean isChanged_;
    private String address_;
    private Double value_;

    private static final Pattern PATTERN_REAL_NUMBER = Pattern.compile("^[\\+\\-]?(?:((?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][\\+\\-]?\\d+|))|0[xX]([0-9a-fA-F]+)|0[bB]([0-1]+))$");
    private static final Pattern PATTERN_INPUT = Pattern.compile("[\\s]*;[\\s]*");
    private static final Pattern PATTERN_BLOCK = Pattern.compile("[\\s]+");

    /**
     *
     * @param ladders
     * @param idx
     * @param name
     * @param gridColumn
     * @param gridRow
     * @param gridMinSize
     * @param gridMaxSize
     * @param gridContentsWidth
     * @param gridContentsHight
     */
    public LadderPane(Ladders ladders, int idx, String name, int gridColumn, int gridRow, double gridMinSize, double gridMaxSize, double gridContentsWidth, double gridContentsHight) {
        ladders_ = ladders;
        if (ladders_ == null) {
            ladderCommand_ = null;
        } else {
            ladderCommand_ = ladders_.getLadderCommand();
        }
        ladder_ = new Ladder(idx, name, gridColumn, gridRow);

        previousGrids_ = new ArrayList<>();
        gridMinSize_ = gridMinSize;
        gridMaxSize_ = gridMaxSize;
        gridContentsWidth_ = gridContentsWidth;
        gridContentsHight_ = gridContentsHight;
        isChanged_ = false;

        createGrids();
    }

    /**
     *
     * @param stage
     * @param icons
     */
    public void startUp(Stage stage, List<Image> icons) {
        stage_ = stage;
        icons_ = icons;
    }

    /**
     *
     * @return
     */
    public boolean cleanUp() {
        getChildren().clear();
        return true;
    }

    /**
     *
     * @return
     */
    public Ladder getLadder() {
        return ladder_;
    }

    /**
     *
     * @return
     */
    public List<LadderGrid> getPreviousGrids() {
        return previousGrids_;
    }

    /**
     *
     * @return
     */
    public boolean isChanged() {
        return isChanged_;
    }

    /**
     *
     * @param isChanged
     */
    public void setChanged(boolean isChanged) {
        isChanged_ = isChanged;
    }

    /**
     *
     */
    public void clearEditing() {
        LadderGridPane gridPane;
        int index, size;

        for (index = 0, size = getChildren().size(); index < size; index++) {
            gridPane = (LadderGridPane) getChildren().get(index);
            gridPane.setEditing(false);
        }
    }

    /**
     *
     * @param address
     * @param comment
     */
    public void setComments(String address, String comment) {
        LadderGridPane grid;
        int index, size;

        for (index = 0, size = getChildren().size(); index < size; index++) {
            grid = (LadderGridPane) getChildren().get(index);
            if (address.equals(grid.getLadderGrid().getAddress())) {
                grid.getLadderGrid().setComment(comment);
                grid.changeComment();
            }
        }
    }

    /**
     *
     * @param ioMap
     * @param scrollPane
     */
    public void refreshView(CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, ScrollPane scrollPane) {
        LadderGrid grid;
        LadderGridPane gridPane;
        double viewMinX, viewMinY, viewMaxX, viewMaxY;
        double gridMinX, gridMinY, gridMaxX, gridMaxY;
        String address;
        int idx, index, size;

        viewMinX = -scrollPane.getViewportBounds().getMinX();
        viewMinY = -scrollPane.getViewportBounds().getMinY();
        viewMaxX = scrollPane.getViewportBounds().getWidth() + viewMinX;
        viewMaxY = scrollPane.getViewportBounds().getHeight() + viewMinY;

        for (index = 0, size = ladder_.getInGrids().size(); index < size; index++) {
            grid = ladder_.getInGrids().get(index);
            if (grid != null) {
                gridPane = findGridPane(grid);
                idx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
                if (grid.getAddress().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                    idx = ladder_.getIdx();
                }
                LadderIo io = ioMap.get(idx).get(grid.getAddress());
                if ((gridPane != null) && (io != null)) {
                    gridMinX = gridPane.getBoundsInParent().getMinX() + gridPane.getWidth();
                    gridMinY = gridPane.getBoundsInParent().getMinY() + gridPane.getHeight();
                    gridMaxX = gridPane.getBoundsInParent().getMaxX() - gridPane.getWidth();
                    gridMaxY = gridPane.getBoundsInParent().getMaxY() - gridPane.getHeight();

                    if ((gridMinX > viewMinX) && (gridMinY > viewMinY) && (gridMaxX < viewMaxX) && (gridMaxY < viewMaxY)) {
                        switch (grid.getBlock()) {
                            case LOAD:
                            case LOAD_NOT:
                            case LOAD_RISING:
                            case LOAD_RISING_NOT:
                            case LOAD_FALLING:
                            case LOAD_FALLING_NOT:
                                grid.setBlockValue(io.getValue());
                                grid.setBlockLd(io.isLd());
                                gridPane.changeBlockIO();
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
                                grid.setBlockValue(io.getValue());
                                if (!grid.getBlockFunctions()[0].isNumber()) {
                                    address = grid.getBlockFunctions()[0].getAddress();
                                    idx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
                                    if (address.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                        idx = ladder_.getIdx();
                                    }
                                    if (ioMap.get(idx).containsKey(address)) {
                                        grid.getBlockFunctions()[0].setValue(ioMap.get(idx).get(address).getValue());
                                    }
                                }
                                gridPane.changeBlockIO();
                                gridPane.changeBlockFunction();
                                break;
                        }
                    }
                }
            }
        }

        for (index = 0, size = ladder_.getOutGrids().size(); index < size; index++) {
            grid = ladder_.getOutGrids().get(index);
            if (grid != null) {
                gridPane = findGridPane(grid);
                idx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
                if (grid.getAddress().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                    idx = ladder_.getIdx();
                }
                LadderIo io = ioMap.get(idx).get(grid.getAddress());
                if ((gridPane != null) && (io != null)) {
                    gridMinX = gridPane.getBoundsInParent().getMinX() + gridPane.getWidth();
                    gridMinY = gridPane.getBoundsInParent().getMinY() + gridPane.getHeight();
                    gridMaxX = gridPane.getBoundsInParent().getMaxX() - gridPane.getWidth();
                    gridMaxY = gridPane.getBoundsInParent().getMaxY() - gridPane.getHeight();

                    if ((gridMinX > viewMinX) && (gridMinY > viewMinY) && (gridMaxX < viewMaxX) && (gridMaxY < viewMaxY)) {
                        switch (grid.getBlock()) {
                            case OUT:
                            case OUT_NOT:
                            case OUT_RISING:
                            case OUT_RISING_NOT:
                            case OUT_FALLING:
                            case OUT_FALLING_NOT:
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
                            case MOVE:
                            case SCRIPT:
                                grid.setBlockValue(io.getValue());
                                grid.setBlockLd(io.isLd());
                                gridPane.changeBlockIO();
                                break;
                            case TIMER:
                            case TIMER_NOT:
                            case COUNTER:
                            case COUNTER_NOT:
                                grid.setBlockValue(io.getValue());
                                grid.setBlockLd(io.isLd());
                                if (grid.getBlockFunctions()[0].isNumber()) {
                                    grid.setCumulativeValue(io.getCumulativeValue());
                                }
                                gridPane.changeBlockIO();
                                gridPane.changeBlockFunction();
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param event
     * @param scrollPane
     * @return
     */
    public boolean onKeyPressed(KeyEvent event, ScrollPane scrollPane) {
        boolean changed = false;

        if ((ladders_ != null) && (!previousGrids_.isEmpty())) {
            LadderGrid grid, previousGrid;
            LadderGridPane gridPane, previousGridPane;
            StringBuilder defaultValue;

            switch (event.getCode()) {
                case ENTER:
                    if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        if (previousGrids_.size() == 1) {
                            previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                            if ((previousGrid.getColumnIndex() > 0) && (previousGrid.getRowIndex() > 0)) {
                                defaultValue = new StringBuilder();
                                previousGridPane = findGridPane(previousGrid);
                                switch (previousGrid.getBlock()) {
                                    case LOAD:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case LOAD_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case LOAD_RISING:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD_RISING.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case LOAD_RISING_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD_RISING_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case LOAD_FALLING:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD_FALLING.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case LOAD_FALLING_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.LOAD_FALLING_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT_RISING:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT_RISING.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT_RISING_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT_RISING_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT_FALLING:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT_FALLING.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case OUT_FALLING_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OUT_FALLING_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case COMPARISON_EQUAL:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_EQUAL.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_NOT_EQUAL:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_NOT_EQUAL.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_LESS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_LESS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_LESS_EQUAL:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_LESS_EQUAL.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_GREATER:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_GREATER.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_GREATER_EQUAL:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_GREATER_EQUAL.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_AND_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_AND_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_OR_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_OR_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COMPARISON_XOR_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COMPARISON_XOR_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case SET:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SET.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case RESET:
                                        defaultValue.append(Ladders.LADDER_BLOCK.RESET.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case AND_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.AND_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case OR_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.OR_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case XOR_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.XOR_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case NOT_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.NOT_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case ADDITION:
                                        defaultValue.append(Ladders.LADDER_BLOCK.ADDITION.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case SUBTRACTION:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SUBTRACTION.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case MULTIPLICATION:
                                        defaultValue.append(Ladders.LADDER_BLOCK.MULTIPLICATION.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case DIVISION:
                                        defaultValue.append(Ladders.LADDER_BLOCK.DIVISION.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case AVERAGE:
                                        defaultValue.append(Ladders.LADDER_BLOCK.AVERAGE.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case SHIFT_LEFT_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SHIFT_LEFT_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case SHIFT_RIGHT_BITS:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SHIFT_RIGHT_BITS.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case SIGMOID:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SIGMOID.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[1].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[1].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[1].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[1].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[1].getAddress());
                                        }
                                        break;
                                    case RANDOM:
                                        defaultValue.append(Ladders.LADDER_BLOCK.RANDOM.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        break;
                                    case TIMER:
                                        defaultValue.append(Ladders.LADDER_BLOCK.TIMER.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case TIMER_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.TIMER_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COUNTER:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COUNTER.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case COUNTER_NOT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.COUNTER_NOT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case MOVE:
                                        defaultValue.append(Ladders.LADDER_BLOCK.MOVE.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress());
                                        defaultValue.append(" ");
                                        if (previousGrid.getBlockFunctions()[0].isNumber()) {
                                            switch (previousGrid.getBlockFunctions()[0].getRadix()) {
                                                case 10:
                                                    defaultValue.append(previousGrid.getBlockFunctions()[0].getValue());
                                                    break;
                                                case 16:
                                                    defaultValue.append("0x").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 16));
                                                    break;
                                                case 2:
                                                    defaultValue.append("0b").append(Long.toString((long) previousGrid.getBlockFunctions()[0].getValue(), 2));
                                                    break;
                                            }
                                        } else {
                                            defaultValue.append(previousGrid.getBlockFunctions()[0].getAddress());
                                        }
                                        break;
                                    case SCRIPT:
                                        defaultValue.append(Ladders.LADDER_BLOCK.SCRIPT.toCommand());
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getAddress()).append(";");
                                        defaultValue.append(" ");
                                        defaultValue.append(previousGrid.getBlockScript());
                                        break;
                                }
                                changed = keyInput(defaultValue.toString(), previousGrid, previousGridPane);
                                stage_.requestFocus();
                            }
                        }
                    }
                    break;
                case LEFT:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getColumnIndex() > 0) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getLeftLadderGrid(), true);
                        }
                    } else if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 1) && (previousGrid.getRowIndex() > 0)) {
                            grid = previousGrid.getLeftLadderGrid();
                            gridPane = findGridPane(grid);
                            ladderCommand_.blockChangeConnectLineLeft(ladder_, previousGrid, grid, gridPane, grid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE);
                            ladderCommand_.blockChangeSelect(scrollPane, this, grid, false);
                            changed = true;
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getColumnIndex() > 0) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getLeftLadderGrid(), false);
                        }
                    }
                    break;
                case UP:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() > 0) {
                            if (previousGrid.getColumnIndex() == 0) {
                                ladderCommand_.blockChangeSelectRow(scrollPane, this, previousGrid.getRowIndex(), previousGrid.getUpLadderGrid(), true);
                            } else {
                                ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getUpLadderGrid(), true);
                            }
                        }
                    } else if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 1) && (previousGrid.getRowIndex() > 1)) {
                            grid = previousGrid.getUpLadderGrid();
                            gridPane = findGridPane(grid);
                            previousGridPane = findGridPane(previousGrid);
                            ladderCommand_.blockChangeVerticalOrVertical(ladder_, previousGrid, previousGridPane, grid, gridPane, !previousGrid.isVertical());
                            ladderCommand_.blockChangeSelect(scrollPane, this, grid, false);
                            changed = true;
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() > 0) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getUpLadderGrid(), false);
                        }
                    }
                    break;
                case RIGHT:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getColumnIndex() < (ladder_.getColumn() - 1)) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getRightLadderGrid(), true);
                        }
                    } else if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 0) && (previousGrid.getRowIndex() > 0)) {
                            grid = previousGrid.getRightLadderGrid();
                            previousGridPane = findGridPane(previousGrid);
                            ladderCommand_.blockChangeConnectLineRight(ladder_, previousGrid, grid, previousGridPane, previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE);
                            ladderCommand_.blockChangeSelect(scrollPane, this, grid, false);
                            changed = true;
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getColumnIndex() < (ladder_.getColumn() - 1)) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getRightLadderGrid(), false);
                        }
                    }
                    break;
                case DOWN:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() < (ladder_.getRow() - 1)) {
                            if (previousGrid.getColumnIndex() == 0) {
                                ladderCommand_.blockChangeSelectRow(scrollPane, this, previousGrid.getRowIndex(), previousGrid.getDownLadderGrid(), true);
                            } else {
                                ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getDownLadderGrid(), true);
                            }
                        }
                    } else if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 1) && (previousGrid.getRowIndex() > 0)) {
                            grid = previousGrid.getDownLadderGrid();
                            gridPane = findGridPane(grid);
                            previousGridPane = findGridPane(previousGrid);
                            ladderCommand_.blockChangeVerticalVerticalOr(ladder_, previousGrid, previousGridPane, grid, gridPane, !grid.isVertical());
                            ladderCommand_.blockChangeSelect(scrollPane, this, grid, false);
                            changed = true;
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() < (ladder_.getRow() - 1)) {
                            ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getDownLadderGrid(), false);
                        }
                    }
                    break;
                case SLASH:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 0) && (previousGrid.getRowIndex() > 0)) {
                            previousGridPane = findGridPane(previousGrid);
                            // change
                            switch (previousGrid.getBlock()) {
                                case LOAD:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_RISING);
                                    changed = true;
                                    break;
                                case LOAD_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_RISING_NOT);
                                    changed = true;
                                    break;
                                case LOAD_RISING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_FALLING);
                                    changed = true;
                                    break;
                                case LOAD_RISING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_FALLING_NOT);
                                    changed = true;
                                    break;
                                case LOAD_FALLING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD);
                                    changed = true;
                                    break;
                                case LOAD_FALLING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_NOT);
                                    changed = true;
                                    break;
                                case OUT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_RISING);
                                    changed = true;
                                    break;
                                case OUT_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_RISING_NOT);
                                    changed = true;
                                    break;
                                case OUT_RISING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_FALLING);
                                    changed = true;
                                    break;
                                case OUT_RISING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_FALLING_NOT);
                                    changed = true;
                                    break;
                                case OUT_FALLING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT);
                                    changed = true;
                                    break;
                                case OUT_FALLING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_NOT);
                                    changed = true;
                                    break;
                                case COMPARISON_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_NOT_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_NOT_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_LESS);
                                    changed = true;
                                    break;
                                case COMPARISON_LESS:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_LESS_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_LESS_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_GREATER);
                                    changed = true;
                                    break;
                                case COMPARISON_GREATER:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_GREATER_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_GREATER_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_AND_BITS);
                                    changed = true;
                                    break;
                            }
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 0) && (previousGrid.getRowIndex() > 0)) {
                            previousGridPane = findGridPane(previousGrid);
                            // not
                            switch (previousGrid.getBlock()) {
                                case LOAD:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_NOT);
                                    changed = true;
                                    break;
                                case LOAD_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD);
                                    changed = true;
                                    break;
                                case LOAD_RISING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_RISING_NOT);
                                    changed = true;
                                    break;
                                case LOAD_RISING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_RISING);
                                    changed = true;
                                    break;
                                case LOAD_FALLING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_FALLING_NOT);
                                    changed = true;
                                    break;
                                case LOAD_FALLING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.LOAD_FALLING);
                                    changed = true;
                                    break;
                                case OUT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_NOT);
                                    changed = true;
                                    break;
                                case OUT_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT);
                                    changed = true;
                                    break;
                                case OUT_RISING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_RISING_NOT);
                                    changed = true;
                                    break;
                                case OUT_RISING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_RISING);
                                    changed = true;
                                    break;
                                case OUT_FALLING:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_FALLING_NOT);
                                    changed = true;
                                    break;
                                case OUT_FALLING_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.OUT_FALLING);
                                    changed = true;
                                    break;
                                case COMPARISON_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_NOT_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_NOT_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_LESS:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_GREATER_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_LESS_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_GREATER);
                                    changed = true;
                                    break;
                                case COMPARISON_GREATER:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_LESS_EQUAL);
                                    changed = true;
                                    break;
                                case COMPARISON_GREATER_EQUAL:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COMPARISON_LESS);
                                    changed = true;
                                    break;
                                case SET:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.RESET);
                                    changed = true;
                                    break;
                                case RESET:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.SET);
                                    changed = true;
                                    break;
                                case TIMER:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.TIMER_NOT);
                                    changed = true;
                                    break;
                                case TIMER_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.TIMER);
                                    changed = true;
                                    break;
                                case COUNTER:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COUNTER_NOT);
                                    changed = true;
                                    break;
                                case COUNTER_NOT:
                                    ladderCommand_.blockChangeBlock(ladder_, previousGrid, previousGridPane, Ladders.LADDER_BLOCK.COUNTER);
                                    changed = true;
                                    break;
                            }
                        }
                    }
                    break;
                case C:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        // copy
                        ladderCommand_.blocksCopy(previousGrids_);
                    }
                    break;
                case F:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) && (previousGrid.getBlock() != Ladders.LADDER_BLOCK.EMPTY) && (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE)) {
                            ladders_.getTableIo().getItems().clear();
                            addTableIo(previousGrid.getAddress(), previousGrid);
                            for (int i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                                addTableIo(previousGrid.getBlockFunctions()[i].getAddress(), previousGrid);
                            }
                        }
                    }
                    break;
                case V:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if ((previousGrid.getColumnIndex() > 0) && (previousGrid.getRowIndex() > 0)) {
                            // paste
                            changed = ladderCommand_.blockPaste(this, previousGrid);
                        }
                    }
                    break;
                case X:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        // cut
                        if (ladderCommand_.blocksCut(this, previousGrids_)) {
                            changed = true;
                        }
                    }
                    break;
                case DELETE:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() > 0) {
                            if (ladderCommand_.ladderRemoveRow(this, previousGrid)) {
                                if (previousGrid.getDownLadderGrid() != null) {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getDownLadderGrid(), false);
                                } else if (previousGrid.getUpLadderGrid() != null) {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getUpLadderGrid(), false);
                                } else {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, null, false);
                                }
                                changed = true;
                            }
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        // delete block
                        if (ladderCommand_.blocksClear(this, previousGrids_)) {
                            changed = true;
                        }
                    }
                    break;
                case INSERT:
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        previousGrid = previousGrids_.get(previousGrids_.size() - 1);
                        if (previousGrid.getRowIndex() > 0) {
                            // insert line
                            if (ladderCommand_.ladderInsertRow(this, previousGrid)) {
                                if (previousGrid.getUpLadderGrid() != null) {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getUpLadderGrid(), false);
                                } else if (previousGrid.getDownLadderGrid() != null) {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, previousGrid.getDownLadderGrid(), false);
                                } else {
                                    ladderCommand_.blockChangeSelect(scrollPane, this, null, false);
                                }
                                changed = true;
                            }
                        }
                    }
                    break;
            }
        }
        isChanged_ |= changed;
        return changed;
    }

    private boolean keyInput(String defaultValue, LadderGrid grid, LadderGridPane gridPane) {
        Ladders.LADDER_BLOCK block = Ladders.LADDER_BLOCK.EMPTY;
        String[] mInput, mBlock;
        int cInput, columnIndex, idx, index;
        TextInputDialog alert = new TextInputDialog(defaultValue);

        alert.initOwner(stage_);
        if (!icons_.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
        }
        alert.setTitle(LadderEnums.INPUT.toString());
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.setGraphic(null);
        Optional<String> result = alert.showAndWait();

        if (result.isPresent()) {
            mInput = PATTERN_INPUT.split(result.get().trim());
            columnIndex = grid.getColumnIndex();
            idx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
            for (cInput = 0; cInput < mInput.length; cInput++) {
                switch (cInput) {
                    case 0:
                        // command address
                        mBlock = PATTERN_BLOCK.split(mInput[cInput]);
                        if ((columnIndex > 0) && (mInput.length >= 1) && (mBlock.length >= 2)) {
                            if (!PATTERN_REAL_NUMBER.matcher(mBlock[1]).find()) {
                                switch (Ladders.LADDER_BLOCK.get(mBlock[0])) {
                                    case LOAD:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD;
                                            }
                                        }
                                        break;
                                    case LOAD_NOT:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD_NOT;
                                            }
                                        }
                                        break;
                                    case LOAD_RISING:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD_RISING;
                                            }
                                        }
                                        break;
                                    case LOAD_RISING_NOT:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD_RISING_NOT;
                                            }
                                        }
                                        break;
                                    case LOAD_FALLING:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD_FALLING;
                                            }
                                        }
                                        break;
                                    case LOAD_FALLING_NOT:
                                        if (columnIndex < (ladder_.getColumn() - 1)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.LOAD_FALLING_NOT;
                                            }
                                        }
                                        break;
                                    case OUT:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT;
                                            }
                                        }
                                        break;
                                    case OUT_NOT:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT_NOT;
                                            }
                                        }
                                        break;
                                    case OUT_RISING:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT_RISING;
                                            }
                                        }
                                        break;
                                    case OUT_RISING_NOT:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT_RISING_NOT;
                                            }
                                        }
                                        break;
                                    case OUT_FALLING:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT_FALLING;
                                            }
                                        }
                                        break;
                                    case OUT_FALLING_NOT:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OUT_FALLING_NOT;
                                            }
                                        }
                                        break;
                                    case COMPARISON_EQUAL:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_EQUAL;
                                            }
                                        }
                                        break;
                                    case COMPARISON_NOT_EQUAL:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_NOT_EQUAL;
                                            }
                                        }
                                        break;
                                    case COMPARISON_LESS:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_LESS;
                                            }
                                        }
                                        break;
                                    case COMPARISON_LESS_EQUAL:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_LESS_EQUAL;
                                            }
                                        }
                                        break;
                                    case COMPARISON_GREATER:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_GREATER;
                                            }
                                        }
                                        break;
                                    case COMPARISON_GREATER_EQUAL:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_GREATER_EQUAL;
                                            }
                                        }
                                        break;
                                    case COMPARISON_AND_BITS:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_AND_BITS;
                                            }
                                        }
                                        break;
                                    case COMPARISON_OR_BITS:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_OR_BITS;
                                            }
                                        }
                                        break;
                                    case COMPARISON_XOR_BITS:
                                        if ((columnIndex < (ladder_.getColumn() - 1)) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COMPARISON_XOR_BITS;
                                            }
                                        }
                                        break;
                                    case SET:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SET;
                                            }
                                        }
                                        break;
                                    case RESET:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.RESET;
                                            }
                                        }
                                        break;
                                    case AND_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.AND_BITS;
                                            }
                                        }
                                        break;
                                    case OR_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.OR_BITS;
                                            }
                                        }
                                        break;
                                    case XOR_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.XOR_BITS;
                                            }
                                        }
                                        break;
                                    case NOT_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.NOT_BITS;
                                            }
                                        }
                                        break;
                                    case ADDITION:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.ADDITION;
                                            }
                                        }
                                        break;
                                    case SUBTRACTION:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SUBTRACTION;
                                            }
                                        }
                                        break;
                                    case MULTIPLICATION:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.MULTIPLICATION;
                                            }
                                        }
                                        break;
                                    case DIVISION:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.DIVISION;
                                            }
                                        }
                                        break;
                                    case AVERAGE:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.AVERAGE;
                                            }
                                        }
                                        break;
                                    case SHIFT_LEFT_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SHIFT_LEFT_BITS;
                                            }
                                        }
                                        break;
                                    case SHIFT_RIGHT_BITS:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SHIFT_RIGHT_BITS;
                                            }
                                        }
                                        break;
                                    case SIGMOID:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 4)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty() && !mBlock[3].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SIGMOID;
                                            }
                                        }
                                        break;
                                    case RANDOM:
                                        if (columnIndex < ladder_.getColumn()) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.RANDOM;
                                            }
                                        }
                                        break;
                                    case TIMER:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.TIMER;
                                            }
                                        }
                                        break;
                                    case TIMER_NOT:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.TIMER_NOT;
                                            }
                                        }
                                        break;
                                    case COUNTER:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COUNTER;
                                            }
                                        }
                                        break;
                                    case COUNTER_NOT:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.COUNTER_NOT;
                                            }
                                        }
                                        break;
                                    case MOVE:
                                        if ((columnIndex < ladder_.getColumn()) && (mBlock.length >= 3)) {
                                            if (!mBlock[0].isEmpty() && !mBlock[1].isEmpty() && !mBlock[2].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.MOVE;
                                            }
                                        }
                                        break;
                                    case SCRIPT:
                                        if ((columnIndex < ladder_.getColumn()) && (mInput.length >= 2)) {
                                            if (!mInput[1].isEmpty() && !mBlock[0].isEmpty() && !mBlock[1].isEmpty()) {
                                                block = Ladders.LADDER_BLOCK.SCRIPT;
                                            }
                                        }
                                        break;
                                }
                            }
                        }

                        // block check
                        if (block == Ladders.LADDER_BLOCK.EMPTY) {
                            return false;
                        }

                        if (mBlock[1].startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                            idx = ladder_.getIdx();
                        }

                        ladderCommand_.blockChangeStart();
                        switch (block) {
                            case LOAD:
                            case LOAD_NOT:
                            case LOAD_RISING:
                            case LOAD_RISING_NOT:
                            case LOAD_FALLING:
                            case LOAD_FALLING_NOT:
                                // block old
                                ladderCommand_.blockChangeOriginal(ladder_, grid);

                                // block
                                if (grid.getBlock() != block) {
                                    ladderCommand_.blockClear(ladder_, grid, gridPane);
                                    ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, block);
                                }

                                // address
                                ladderCommand_.blockChangeAddress(ladder_, grid, gridPane, mBlock[1]);

                                // comment
                                if (ladders_.isComment(idx, mBlock[1])) {
                                    ladderCommand_.blockChangeComment(ladder_, grid, gridPane, ladders_.getComment(idx, mBlock[1]));
                                } else {
                                    ladderCommand_.blockChangeComment(ladder_, grid, gridPane, LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                                }

                                // function value
                                grid.getBlockFunctions()[0].clear();
                                grid.getBlockFunctions()[1].clear();

                                // script
                                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);

                                // select
                                ladderCommand_.blockChangeSelect(null, this, grid.getRightLadderGrid(), false);
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
                                // block old
                                ladderCommand_.blockChangeOriginal(ladder_, grid);

                                // block
                                if (grid.getBlock() != block) {
                                    ladderCommand_.blockClear(ladder_, grid, gridPane);
                                    ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, block);
                                }

                                // address
                                ladderCommand_.blockChangeAddress(ladder_, grid, gridPane, mBlock[1]);

                                // comment
                                if (ladders_.isComment(idx, mBlock[1])) {
                                    ladderCommand_.blockChangeComment(ladder_, grid, gridPane, ladders_.getComment(idx, mBlock[1]));
                                } else {
                                    ladderCommand_.blockChangeComment(ladder_, grid, gridPane, LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                                }

                                // function value
                                ladderCommand_.blockChangeFunctionValue(ladder_, grid, gridPane, mBlock[2], 0);
                                grid.getBlockFunctions()[1].clear();

                                // script
                                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);

                                // select
                                ladderCommand_.blockChangeSelect(null, this, grid.getRightLadderGrid(), false);
                                break;
                            case OUT:
                            case OUT_NOT:
                            case OUT_RISING:
                            case OUT_RISING_NOT:
                            case OUT_FALLING:
                            case OUT_FALLING_NOT:
                            case SET:
                            case RESET:
                            case RANDOM:
                            case SCRIPT:
                                if (ladder_.checkGridOut(grid)) {
                                    // connect
                                    for (index = columnIndex; index < (ladder_.getColumn() - 1); index++) {
                                        if (grid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE) {
                                            ladderCommand_.blockChangeOriginal(ladder_, grid);
                                            ladderCommand_.blockClear(ladder_, grid, gridPane);
                                            ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, Ladders.LADDER_BLOCK.CONNECT_LINE);
                                            ladderCommand_.blockChangeRevised(ladder_, grid);
                                        }
                                        grid = grid.getRightLadderGrid();
                                        gridPane = findGridPane(grid);
                                    }

                                    // block old
                                    ladderCommand_.blockChangeOriginal(ladder_, grid);

                                    // block
                                    if (grid.getBlock() != block) {
                                        ladderCommand_.blockClear(ladder_, grid, gridPane);
                                        ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, block);
                                    }

                                    // address
                                    ladderCommand_.blockChangeAddress(ladder_, grid, gridPane, mBlock[1]);

                                    // comment
                                    if (ladders_.isComment(idx, mBlock[1])) {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, ladders_.getComment(idx, mBlock[1]));
                                    } else {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                                    }

                                    // function value
                                    grid.getBlockFunctions()[0].clear();
                                    grid.getBlockFunctions()[1].clear();

                                    // script
                                    grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
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
                                if (ladder_.checkGridOut(grid)) {
                                    // connect
                                    for (index = columnIndex; index < (ladder_.getColumn() - 1); index++) {
                                        if (grid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE) {
                                            ladderCommand_.blockChangeOriginal(ladder_, grid);
                                            ladderCommand_.blockClear(ladder_, grid, gridPane);
                                            ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, Ladders.LADDER_BLOCK.CONNECT_LINE);
                                            ladderCommand_.blockChangeRevised(ladder_, grid);
                                        }
                                        grid = grid.getRightLadderGrid();
                                        gridPane = findGridPane(grid);
                                    }

                                    // block old
                                    ladderCommand_.blockChangeOriginal(ladder_, grid);

                                    // block
                                    if (grid.getBlock() != block) {
                                        ladderCommand_.blockClear(ladder_, grid, gridPane);
                                        ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, block);
                                    }

                                    // address
                                    ladderCommand_.blockChangeAddress(ladder_, grid, gridPane, mBlock[1]);

                                    // comment
                                    if (ladders_.isComment(idx, mBlock[1])) {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, ladders_.getComment(idx, mBlock[1]));
                                    } else {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                                    }

                                    // function value
                                    ladderCommand_.blockChangeFunctionValue(ladder_, grid, gridPane, mBlock[2], 0);
                                    ladderCommand_.blockChangeFunctionValue(ladder_, grid, gridPane, mBlock[3], 1);

                                    // script
                                    grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
                                }
                                break;
                            case NOT_BITS:
                            case TIMER:
                            case TIMER_NOT:
                            case COUNTER:
                            case COUNTER_NOT:
                            case MOVE:
                                if (ladder_.checkGridOut(grid)) {
                                    // connect
                                    for (index = columnIndex; index < (ladder_.getColumn() - 1); index++) {
                                        if (grid.getBlock() != Ladders.LADDER_BLOCK.CONNECT_LINE) {
                                            ladderCommand_.blockChangeOriginal(ladder_, grid);
                                            ladderCommand_.blockClear(ladder_, grid, gridPane);
                                            ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, Ladders.LADDER_BLOCK.CONNECT_LINE);
                                            ladderCommand_.blockChangeRevised(ladder_, grid);
                                        }
                                        grid = grid.getRightLadderGrid();
                                        gridPane = findGridPane(grid);
                                    }

                                    // block old
                                    ladderCommand_.blockChangeOriginal(ladder_, grid);

                                    // block
                                    if (grid.getBlock() != block) {
                                        ladderCommand_.blockClear(ladder_, grid, gridPane);
                                        ladderCommand_.blockChangeBlock(ladder_, grid, gridPane, block);
                                    }

                                    // address
                                    ladderCommand_.blockChangeAddress(ladder_, grid, gridPane, mBlock[1]);

                                    // comment
                                    if (ladders_.isComment(idx, mBlock[1])) {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, ladders_.getComment(idx, mBlock[1]));
                                    } else {
                                        ladderCommand_.blockChangeComment(ladder_, grid, gridPane, LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                                    }

                                    // function value
                                    ladderCommand_.blockChangeFunctionValue(ladder_, grid, gridPane, mBlock[2], 0);
                                    grid.getBlockFunctions()[1].clear();

                                    // script
                                    grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch (block) {
                            case LOAD:
                            case LOAD_NOT:
                            case LOAD_RISING:
                            case LOAD_RISING_NOT:
                            case LOAD_FALLING:
                            case LOAD_FALLING_NOT:
                            case OUT:
                            case OUT_NOT:
                            case OUT_RISING:
                            case OUT_RISING_NOT:
                            case OUT_FALLING:
                            case OUT_FALLING_NOT:
                            case COMPARISON_EQUAL:
                            case COMPARISON_NOT_EQUAL:
                            case COMPARISON_LESS:
                            case COMPARISON_LESS_EQUAL:
                            case COMPARISON_GREATER:
                            case COMPARISON_GREATER_EQUAL:
                            case COMPARISON_AND_BITS:
                            case COMPARISON_OR_BITS:
                            case COMPARISON_XOR_BITS:
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
                            case TIMER:
                            case TIMER_NOT:
                            case COUNTER:
                            case COUNTER_NOT:
                            case MOVE:
                                // comment
                                if (ladderCommand_.blockChangeComment(ladder_, grid, gridPane, mInput[cInput])) {
                                    ladders_.changeComment(idx, grid.getAddress(), mInput[cInput]);
                                }
                                break;
                            case SCRIPT:
                                // script
                                ladderCommand_.blockChangeScript(ladder_, grid, gridPane, mInput[cInput]);
                                break;
                        }
                        break;
                    case 2:
                        switch (block) {
                            case SCRIPT:
                                // comment
                                if (ladderCommand_.blockChangeComment(ladder_, grid, gridPane, mInput[cInput])) {
                                    ladders_.changeComment(idx, grid.getAddress(), mInput[cInput]);
                                }
                                break;
                        }
                        break;
                }
            }
            ladderCommand_.blockChangeRevised(ladder_, grid);
            return ladderCommand_.blockChangeEnd(ladder_.getColumn(), ladder_.getRow());
        }
        return false;
    }

    private void createGrids() {
        LadderGrid[] gridUps = new LadderGrid[ladder_.getColumn()];
        LadderGrid grid, gridLeft;
        int columnIndex, columnSize, rowIndex, rowSize;

        columnSize = ladder_.getColumn();
        rowSize = ladder_.getRow();
        for (rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            gridLeft = null;
            for (columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                grid = addGrid(columnIndex, rowIndex).getLadderGrid();
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
     * @param columnIndex
     * @param rowIndex
     * @return
     */
    public LadderGridPane addGrid(int columnIndex, int rowIndex) {
        return addGrid(columnIndex, rowIndex, 1, 1);
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param colspan
     * @param rowspan
     * @return
     */
    public LadderGridPane addGrid(int columnIndex, int rowIndex, int colspan, int rowspan) {
        LadderGridPane gridPane = new LadderGridPane(columnIndex, rowIndex, colspan, rowspan);
        LadderGrid grid = gridPane.getLadderGrid();

        gridPane.setCache(true);
        if ((columnIndex == 0) && (rowIndex == 0)) {
            gridPane.setMinSize(gridContentsWidth_, gridContentsHight_);
            gridPane.setPrefSize(gridContentsWidth_, gridContentsHight_);
            gridPane.setMaxSize(gridContentsWidth_, gridContentsHight_);
            grid.setBlock(Ladders.LADDER_BLOCK.CONTENTS);
            gridPane.changeBlock();
        } else if (columnIndex == 0) {
            gridPane.setMinSize(gridContentsWidth_, gridMinSize_);
            gridPane.setPrefSize(gridContentsWidth_, gridMaxSize_);
            gridPane.setMaxSize(gridContentsWidth_, gridMaxSize_);
            grid.setBlock(Ladders.LADDER_BLOCK.CONTENTS);
            gridPane.changeBlock();
        } else if (rowIndex == 0) {
            gridPane.setMinSize(gridMinSize_, gridContentsHight_);
            gridPane.setPrefSize(gridMaxSize_, gridContentsHight_);
            gridPane.setMaxSize(gridMaxSize_, gridContentsHight_);
            grid.setBlock(Ladders.LADDER_BLOCK.CONTENTS);
            gridPane.changeBlock();
        } else {
            gridPane.setMinSize(gridMinSize_, gridMinSize_);
            gridPane.setPrefSize(gridMaxSize_, gridMaxSize_);
            gridPane.setMaxSize(gridMaxSize_, gridMaxSize_);
        }

        if (ladders_ != null) {
            gridPane.setOnMouseClicked((MouseEvent event) -> {
                event.consume();
                if (event.getButton() == MouseButton.PRIMARY) {
                    switch (event.getClickCount()) {
                        case 1:
                            if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                                if ((grid.getColumnIndex() == 0)) {
                                    ladderCommand_.blockChangeSelectRow(null, this, grid.getRowIndex(), grid, true);
                                } else {
                                    ladderCommand_.blockChangeSelect(null, this, grid, true);
                                }
                            } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                                ladderCommand_.blockChangeSelect(null, this, grid, false);
                            }
                            break;
                        case 2:
                            if (!PATTERN_REAL_NUMBER.matcher(grid.getAddress()).find()) {
                                int idx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;

                                address_ = grid.getAddress();

                                if (address_.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                    idx = ladder_.getIdx();
                                }

                                value_ = ladders_.getValue(idx, address_);
                                if (value_ != null) {
                                    if (value_ == 0) {
                                        ladders_.setValue(idx, address_, 1.0);
                                    } else {
                                        ladders_.setValue(idx, address_, 0.0);
                                    }
                                }
                            }
                            break;
                    }
                }
            });
            gridPane.setOnDragDetected((MouseEvent event) -> {
                event.consume();
                startFullDrag();
            });
            gridPane.setOnMouseDragEntered((MouseDragEvent event) -> {
                event.consume();
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        if ((grid.getColumnIndex() == 0)) {
                            ladderCommand_.blockChangeSelectRow(null, this, grid.getRowIndex(), grid, true);
                        } else {
                            ladderCommand_.blockChangeSelect(null, this, grid, true);
                        }
                    } else if (!event.isShiftDown() && !event.isShortcutDown() && !event.isAltDown()) {
                        ladderCommand_.blockChangeSelect(null, this, grid, false);
                    }
                }
            });
        }
        add(gridPane, columnIndex, rowIndex, colspan, rowspan);
        ladder_.addLadderGrid(grid);
        return gridPane;
    }

    private void addTableIo(String address, LadderGrid previousGrid) {
        if (address != null) {
            TableView<LadderTableIo> tableIo;
            LadderGrid grid;
            int idx, siz, index, size, i;

            tableIo = ladders_.getTableIo();
            if (address.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                for (index = 0, size = getChildren().size(); index < size; index++) {
                    grid = ((LadderGridPane) getChildren().get(index)).getLadderGrid();
                    if (address.equals(grid.getAddress())) {
                        tableIo.getItems().add(new LadderTableIo(ladder_.getName(), address, grid.getBlock().toString(), grid.getColumnIndex(), grid.getRowIndex()));
                        if ((previousGrid.getColumnIndex() == grid.getColumnIndex()) && (previousGrid.getRowIndex() == grid.getRowIndex())) {
                            int sel = tableIo.getItems().size() - 1;
                            Platform.runLater(() -> {
                                tableIo.scrollTo(sel);
                                tableIo.getSelectionModel().select(sel);
                            });
                        }
                    } else {
                        for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                            if (address.equals(grid.getBlockFunctions()[i].getAddress())) {
                                tableIo.getItems().add(new LadderTableIo(ladder_.getName(), address, grid.getBlock().toString(), grid.getColumnIndex(), grid.getRowIndex()));
                                if ((previousGrid.getColumnIndex() == grid.getColumnIndex()) && (previousGrid.getRowIndex() == grid.getRowIndex())) {
                                    int sel = tableIo.getItems().size() - 1;
                                    Platform.runLater(() -> {
                                        tableIo.scrollTo(sel);
                                        tableIo.getSelectionModel().select(sel);
                                    });
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                LadderPane pane;

                for (idx = 0, siz = ladders_.getTabLadder().getTabs().size(); idx < siz; idx++) {
                    pane = (LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(idx).getContent()).getContent();
                    for (index = 0, size = pane.getChildren().size(); index < size; index++) {
                        grid = ((LadderGridPane) pane.getChildren().get(index)).getLadderGrid();
                        if (address.equals(grid.getAddress())) {
                            tableIo.getItems().add(new LadderTableIo(pane.getLadder().getName(), address, grid.getBlock().toString(), grid.getColumnIndex(), grid.getRowIndex()));
                            if ((ladder_.getIdx() == pane.getLadder().getIdx()) && (previousGrid.getColumnIndex() == grid.getColumnIndex()) && (previousGrid.getRowIndex() == grid.getRowIndex())) {
                                int sel = tableIo.getItems().size() - 1;
                                Platform.runLater(() -> {
                                    tableIo.scrollTo(sel);
                                    tableIo.getSelectionModel().select(sel);
                                });
                            }
                        } else {
                            for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                                if (address.equals(grid.getBlockFunctions()[i].getAddress())) {
                                    tableIo.getItems().add(new LadderTableIo(pane.getLadder().getName(), address, grid.getBlock().toString(), grid.getColumnIndex(), grid.getRowIndex()));
                                    if ((ladder_.getIdx() == pane.getLadder().getIdx()) && (previousGrid.getColumnIndex() == grid.getColumnIndex()) && (previousGrid.getRowIndex() == grid.getRowIndex())) {
                                        int sel = tableIo.getItems().size() - 1;
                                        Platform.runLater(() -> {
                                            tableIo.scrollTo(sel);
                                            tableIo.getSelectionModel().select(sel);
                                        });
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param grid
     * @return
     */
    public LadderGridPane findGridPane(LadderGrid grid) {
        if (grid != null) {
            return findGridPane(grid.getColumnIndex(), grid.getRowIndex());
        }
        return null;
    }

    /**
     *
     * @param column
     * @param row
     * @return
     */
    public LadderGridPane findGridPane(int column, int row) {
        LadderGrid findGrid;
        LadderGridPane findGridPane;
        int index, size;

        for (index = 0, size = getChildren().size(); index < size; index++) {
            findGridPane = (LadderGridPane) getChildren().get(index);
            findGrid = findGridPane.getLadderGrid();
            if ((findGrid.getColumnIndex() == column) && (findGrid.getRowIndex() == row)) {
                return findGridPane;
            }
        }
        return null;
    }

    /**
     *
     * @param gridPane
     * @param columnIndex
     */
    public void setGridColumnIndex(LadderGridPane gridPane, int columnIndex) {
        setColumnIndex(gridPane, columnIndex);
    }

    /**
     *
     * @param gridPane
     * @param rowIndex
     */
    public void setGridRowIndex(LadderGridPane gridPane, int rowIndex) {
        setRowIndex(gridPane, rowIndex);
    }
}
