/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ken.mizoguch.console.Console;

/**
 *
 * @author mizoguch-ken
 */
public class LadderCommand {

    private static final Pattern PATTERN_REAL_NUMBER = Pattern.compile("^[\\+\\-]?(?:((?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][\\+\\-]?\\d+|))|0[xX]([0-9a-fA-F]+)|0[bB]([0-1]+))$");

    private final Stage stage_;
    private final List<Image> icons_;
    private final Ladders ladders_;

    private final LadderHistoryManager historyManager_;
    private int historyGeneration_;
    private LadderHistory history_;
    private final List<LadderGrid> copyGrids_;
    private final List<LadderGridPane> blockChangingGridPane_;
    private boolean isDisableHistory_, isBlockChanging_;

    private final Gson gson_ = new Gson();

    /**
     *
     * @param stage
     * @param icons
     * @param ladders
     */
    public LadderCommand(Stage stage, List<Image> icons, Ladders ladders) {
        stage_ = stage;
        icons_ = icons;
        ladders_ = ladders;
        historyManager_ = new LadderHistoryManager();
        historyGeneration_ = -1;
        history_ = null;
        copyGrids_ = new ArrayList<>();
        blockChangingGridPane_ = new ArrayList<>();
        isDisableHistory_ = false;
        isBlockChanging_ = false;
    }

    /**
     *
     */
    public void clearHistoryManager() {
        historyManager_.clear();
    }

    /**
     *
     * @return
     */
    public LadderHistoryManager getHistoryManager() {
        return historyManager_;
    }

    /**
     *
     * @param historyManager
     */
    public void setHistoryManager(LadderHistoryManager historyManager) {
        historyManager_.set(historyManager, historyGeneration_);
    }

    /**
     *
     * @return
     */
    public int getHistoryGeneration() {
        return historyGeneration_;
    }

    /**
     *
     * @param historyGeneration
     */
    public void setHistoryGeneration(int historyGeneration) {
        historyGeneration_ = historyGeneration;
    }

    /**
     *
     * @return
     */
    public boolean undo() {
        history_ = historyManager_.undo(historyGeneration_);

        if (history_ != null) {
            ScrollPane scrollPane;
            LadderPane pane;
            LadderGrid grid;
            int columnIndex, rowIndex;

            isDisableHistory_ = true;
            try {
                switch (history_.getCommand()) {
                    case CHANGE_ADDRESS:
                        changeAddress(history_.getRevised().getIdx(), history_.getRevised().getAddress(), history_.getOriginal().getIdx(), history_.getOriginal().getAddress());
                        break;
                    case CHANGE_COMMENT:
                        changeComment(history_.getOriginal().getIdx(), history_.getOriginal().getAddress(), history_.getOriginal().getComment());
                        break;
                    case LADDER_CREATE:
                        ladderRemove(history_.getRevised().getIdx() - 1);
                        break;
                    case LADDER_REMOVE:
                        restoreBlocks(history_.getOriginal(), ladderCreate(history_.getOriginal().getIdx(), history_.getOriginal().getName(), history_.getOriginal().getColumn(), history_.getOriginal().getRow(), Ladders.LADDER_DEFAULT_GRID_MIN_SIZE, Ladders.LADDER_DEFAULT_GRID_MAX_SIZE, Ladders.LADDER_DEFAULT_GRID_CONTENTS_WIDTH, Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT), true);
                        ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx());
                        break;
                    case LADDER_REMOVE_ROW:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx() - 1);
                        scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getOriginal().getIdx() - 1).getContent();
                        pane = (LadderPane) scrollPane.getContent();
                        columnIndex = history_.getOriginal().getColumnIndex();
                        rowIndex = history_.getOriginal().getRowIndex();
                        if (ladderInsertRow(pane, pane.getLadder().findGrid(columnIndex, rowIndex))) {
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                            restoreBlocks(history_.getOriginal(), history_.getRevised(), pane, true, true);
                            if (grid.getUpLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getUpLadderGrid(), false);
                            } else if (grid.getDownLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getDownLadderGrid(), false);
                            } else {
                                blockChangeSelect(scrollPane, pane, null, false);
                            }
                        }
                        break;
                    case LADDER_INSERT_ROW:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent();
                        pane = (LadderPane) scrollPane.getContent();
                        columnIndex = history_.getRevised().getColumnIndex();
                        rowIndex = history_.getRevised().getRowIndex();
                        grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        if (ladderRemoveRow(pane, grid)) {
                            if (grid.getDownLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getDownLadderGrid(), false);
                            } else if (grid.getUpLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getUpLadderGrid(), false);
                            } else {
                                blockChangeSelect(scrollPane, pane, null, false);
                            }
                        }
                        break;
                    case LADDER_MOVE_LEFT:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        ladderMoveRight(((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent()).getContent()).getLadder());
                        break;
                    case LADDER_MOVE_RIGHT:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        ladderMoveLeft(((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent()).getContent()).getLadder());
                        break;
                    case LADDER_CHANGE_NAME:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        ladderChangeName((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent()).getContent(), history_.getOriginal().getName());
                        break;
                    case BLOCK_CHANGE:
                        if (history_.getOriginal() != null) {
                            ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx() - 1);
                            scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getOriginal().getIdx() - 1).getContent();
                            pane = (LadderPane) scrollPane.getContent();
                            columnIndex = history_.getOriginal().getColumnIndex();
                            rowIndex = history_.getOriginal().getRowIndex();
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        } else {
                            ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                            scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent();
                            pane = (LadderPane) scrollPane.getContent();
                            columnIndex = history_.getRevised().getColumnIndex();
                            rowIndex = history_.getRevised().getRowIndex();
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        }
                        restoreBlocks(history_.getOriginal(), history_.getRevised(), pane, true, true);
                        blockChangeSelect(scrollPane, pane, grid, false);
                        break;
                }
                isDisableHistory_ = false;
                return true;
            } catch (Exception ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            }
            isDisableHistory_ = false;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean redo() {
        history_ = historyManager_.redo(historyGeneration_);

        if (history_ != null) {
            ScrollPane scrollPane;
            LadderPane pane;
            LadderGrid grid;
            int columnIndex, rowIndex;

            isDisableHistory_ = true;
            try {
                switch (history_.getCommand()) {
                    case CHANGE_ADDRESS:
                        changeAddress(history_.getOriginal().getIdx(), history_.getOriginal().getAddress(), history_.getRevised().getIdx(), history_.getRevised().getAddress());
                        break;
                    case CHANGE_COMMENT:
                        changeComment(history_.getRevised().getIdx(), history_.getRevised().getAddress(), history_.getRevised().getComment());
                        break;
                    case LADDER_CREATE:
                        restoreBlocks(history_.getRevised(), ladderCreate(history_.getRevised().getIdx(), history_.getRevised().getName(), history_.getRevised().getColumn(), history_.getRevised().getRow(), Ladders.LADDER_DEFAULT_GRID_MIN_SIZE, Ladders.LADDER_DEFAULT_GRID_MAX_SIZE, Ladders.LADDER_DEFAULT_GRID_CONTENTS_WIDTH, Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT), true);
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        break;
                    case LADDER_REMOVE:
                        ladderRemove(history_.getOriginal().getIdx() - 1);
                        break;
                    case LADDER_REMOVE_ROW:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx() - 1);
                        scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getOriginal().getIdx() - 1).getContent();
                        pane = (LadderPane) scrollPane.getContent();
                        columnIndex = history_.getOriginal().getColumnIndex();
                        rowIndex = history_.getOriginal().getRowIndex();
                        grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        if (ladderRemoveRow(pane, grid)) {
                            if (grid.getDownLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getDownLadderGrid(), false);
                            } else if (grid.getUpLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getUpLadderGrid(), false);
                            } else {
                                blockChangeSelect(scrollPane, pane, null, false);
                            }
                        }
                        break;
                    case LADDER_INSERT_ROW:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent();
                        pane = (LadderPane) scrollPane.getContent();
                        columnIndex = history_.getRevised().getColumnIndex();
                        rowIndex = history_.getRevised().getRowIndex();
                        if (ladderInsertRow(pane, pane.getLadder().findGrid(columnIndex, rowIndex))) {
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                            restoreBlocks(history_.getRevised(), history_.getOriginal(), pane, true, true);
                            if (grid.getUpLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getUpLadderGrid(), false);
                            } else if (grid.getDownLadderGrid() != null) {
                                blockChangeSelect(scrollPane, pane, grid.getDownLadderGrid(), false);
                            } else {
                                blockChangeSelect(scrollPane, pane, null, false);
                            }
                        }
                        break;
                    case LADDER_MOVE_LEFT:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        ladderMoveLeft(((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent()).getContent()).getLadder());
                        break;
                    case LADDER_MOVE_RIGHT:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                        ladderMoveRight(((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent()).getContent()).getLadder());
                        break;
                    case LADDER_CHANGE_NAME:
                        ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx() - 1);
                        ladderChangeName((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getOriginal().getIdx() - 1).getContent()).getContent(), history_.getRevised().getName());
                        break;
                    case BLOCK_CHANGE:
                        if (history_.getRevised() != null) {
                            ladders_.getTabLadder().getSelectionModel().select(history_.getRevised().getIdx() - 1);
                            scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getRevised().getIdx() - 1).getContent();
                            pane = (LadderPane) scrollPane.getContent();
                            columnIndex = history_.getRevised().getColumnIndex();
                            rowIndex = history_.getRevised().getRowIndex();
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        } else {
                            ladders_.getTabLadder().getSelectionModel().select(history_.getOriginal().getIdx() - 1);
                            scrollPane = (ScrollPane) ladders_.getTabLadder().getTabs().get(history_.getOriginal().getIdx() - 1).getContent();
                            pane = (LadderPane) scrollPane.getContent();
                            columnIndex = history_.getOriginal().getColumnIndex();
                            rowIndex = history_.getOriginal().getRowIndex();
                            grid = pane.getLadder().findGrid(columnIndex, rowIndex);
                        }
                        restoreBlocks(history_.getRevised(), history_.getOriginal(), pane, true, true);
                        blockChangeSelect(scrollPane, pane, grid, false);
                        break;
                }
                isDisableHistory_ = false;
                return true;
            } catch (Exception ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            }
            isDisableHistory_ = false;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isDisableHistory() {
        return isDisableHistory_;
    }

    /**
     *
     * @param isDisableHistory
     */
    public void setDisableHistory(boolean isDisableHistory) {
        isDisableHistory_ = isDisableHistory;
    }

    /**
     *
     * @param oldIdx
     * @param oldAddress
     * @param newIdx
     * @param newAddress
     */
    public void changeAddress(int oldIdx, String oldAddress, int newIdx, String newAddress) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.CHANGE_ADDRESS);
            history_.setOriginal(new LadderJsonLadder(oldIdx, oldAddress, null));
            history_.setRevised(new LadderJsonLadder(newIdx, newAddress, null));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }

        LadderPane pane;
        Ladder ladder;
        LadderGrid ladderGrid;
        int index, size, index2, size2;

        synchronized (ladders_.getLock()) {
            if (newIdx == Ladders.LADDER_GLOBAL_ADDRESS_INDEX) {
                for (index = 0, size = ladders_.getLadders().length; index < size; index++) {
                    ladder = ladders_.getLadders()[index];
                    for (index2 = 0, size2 = ladder.getLadderGrids().size(); index2 < size2; index2++) {
                        ladderGrid = ladder.getLadderGrids().get(index2);
                        if (ladderGrid.getAddress().equals(oldAddress)) {
                            ladderGrid.setAddress(newAddress);
                        }
                    }
                }

                for (index = 0, size = ladders_.getTabLadder().getTabs().size(); index < size; index++) {
                    pane = ((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(index).getContent()).getContent());
                    ladder = pane.getLadder();
                    for (index2 = 0, size2 = ladder.getLadderGrids().size(); index2 < size2; index2++) {
                        ladderGrid = ladder.getLadderGrids().get(index2);
                        if (ladderGrid.getAddress().equals(oldAddress)) {
                            ladderGrid.setAddress(newAddress);
                            pane.findGridPane(ladderGrid).changeAddress();
                        }
                    }
                }
            } else {
                ladder = ladders_.getLadders()[newIdx - 1];
                for (index2 = 0, size2 = ladder.getLadderGrids().size(); index2 < size2; index2++) {
                    ladderGrid = ladder.getLadderGrids().get(index2);
                    if (ladderGrid.getAddress().equals(oldAddress)) {
                        ladderGrid.setAddress(newAddress);
                    }
                }

                pane = ((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(newIdx - 1).getContent()).getContent());
                ladder = pane.getLadder();
                for (index2 = 0, size2 = ladder.getLadderGrids().size(); index2 < size2; index2++) {
                    ladderGrid = ladder.getLadderGrids().get(index2);
                    if (ladderGrid.getAddress().equals(oldAddress)) {
                        ladderGrid.setAddress(newAddress);
                        pane.findGridPane(ladderGrid).changeAddress();
                    }
                }
            }

            ObservableList<TreeItem<LadderTreeTableIo>> ovNewLaddersAddress = ladders_.getTreeTableIo().getRoot().getChildren().get(newIdx).getChildren();
            for (index = 0, size = ovNewLaddersAddress.size(); index < size; index++) {
                if (ovNewLaddersAddress.get(index).getValue().getAddress().equals(oldAddress)) {
                    ladders_.getTreeTableIo().getRoot().getChildren().get(newIdx).getChildren().get(index).getValue().setAddress(newAddress);
                    break;
                }
            }

            if (ladders_.getIoMap().get(newIdx).containsKey(oldAddress)) {
                ladders_.getIoMap().get(newIdx).get(oldAddress).setAddress(newAddress);
                ladders_.getIoMap().get(newIdx).put(newAddress, ladders_.getIoMap().get(newIdx).get(oldAddress));
                ladders_.getIoMap().get(newIdx).remove(oldAddress);
            }

            if (ladders_.getCommentMap().get(newIdx).containsKey(oldAddress)) {
                ladders_.getCommentMap().get(newIdx).put(newAddress, ladders_.getCommentMap().get(newIdx).get(oldAddress));
                ladders_.getCommentMap().get(newIdx).remove(oldAddress);
            }

            if (ladders_.getScriptIoMap().get(newIdx).containsKey(oldAddress)) {
                ladders_.getScriptIoMap().get(newIdx).get(oldAddress).setAddress(newAddress);
                ladders_.getScriptIoMap().get(newIdx).put(newAddress, ladders_.getScriptIoMap().get(newIdx).get(oldAddress));
                ladders_.getScriptIoMap().get(newIdx).remove(oldAddress);
            }
        }
    }

    /**
     *
     * @param idx
     * @param address
     * @param comment
     * @return
     */
    public boolean changeComment(int idx, String address, String comment) {
        return changeComment(ladders_.getTabLadder(), ladders_.getTreeTableIo(), ladders_.getIoMap(), ladders_.getCommentMap(), idx, address, comment);
    }

    /**
     *
     * @param tabPane
     * @param treeTableView
     * @param ioMap
     * @param commentMap
     * @param idx
     * @param address
     * @param comment
     * @return
     */
    public boolean changeComment(TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, int idx, String address, String comment) {
        String oldComment = LadderGrid.LADDER_GRID_INITIAL_COMMENT;

        if (comment == null) {
            comment = LadderGrid.LADDER_GRID_INITIAL_COMMENT;
        }

        if (comment.isEmpty()) {
            if (commentMap.get(idx).containsKey(address)) {
                oldComment = commentMap.get(idx).get(address);
                commentMap.get(idx).remove(address);
            }
        } else {
            if (commentMap.get(idx).containsKey(address)) {
                if (comment.equals(commentMap.get(idx).get(address))) {
                    return false;
                }
                oldComment = commentMap.get(idx).get(address);
            }
            commentMap.get(idx).put(address, comment);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.CHANGE_COMMENT);
            history_.setOriginal(new LadderJsonLadder(idx, address, oldComment));
            history_.setRevised(new LadderJsonLadder(idx, address, comment));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }

        int index, size;

        if (idx == Ladders.LADDER_GLOBAL_ADDRESS_INDEX) {
            for (index = 0, size = tabPane.getTabs().size(); index < size; index++) {
                ((LadderPane) ((ScrollPane) tabPane.getTabs().get(index).getContent()).getContent()).setComments(address, comment);
            }
        } else {
            ((LadderPane) ((ScrollPane) tabPane.getTabs().get(idx - 1).getContent()).getContent()).setComments(address, comment);
        }

        if (!ioMap.get(idx).containsKey(address)) {
            ioMap.get(idx).put(address, new LadderIo(address));
            treeTableView.getRoot().getChildren().get(idx).getChildren().add(new TreeItem<>(new LadderTreeTableIo(address, comment)));
        }
        ioMap.get(idx).get(address).setComment(comment);

        ObservableList<TreeItem<LadderTreeTableIo>> ovLaddersAddress = treeTableView.getRoot().getChildren().get(idx).getChildren();
        for (index = 0, size = ovLaddersAddress.size(); index < size; index++) {
            if (ovLaddersAddress.get(index).getValue().getAddress().equals(address)) {
                ovLaddersAddress.get(index).getValue().setComment(comment);
            }
        }
        return true;
    }

    /**
     *
     * @param idx
     * @param name
     * @param gridColumn
     * @param gridRow
     * @param gridMinSize
     * @param gridMaxSize
     * @param gridContentsWidth
     * @param gridContentsHight
     * @return
     */
    public LadderPane ladderCreate(int idx, String name, int gridColumn, int gridRow, double gridMinSize, double gridMaxSize, double gridContentsWidth, double gridContentsHight) {
        return ladderCreate(ladders_, ladders_.getTabLadder(), ladders_.getTreeTableIo(), ladders_.getIoMap(), ladders_.getCommentMap(), ladders_.getScriptIoMap(), idx, name, gridColumn, gridRow, gridMinSize, gridMaxSize, gridContentsWidth, gridContentsHight);
    }

    /**
     *
     * @param ladders
     * @param tabPane
     * @param treeTableView
     * @param ioMap
     * @param commentMap
     * @param scriptIoMap
     * @param idx
     * @param name
     * @param gridColumn
     * @param gridRow
     * @param gridMinSize
     * @param gridMaxSize
     * @param gridContentsWidth
     * @param gridContentsHight
     * @return
     */
    public LadderPane ladderCreate(Ladders ladders, TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> scriptIoMap, int idx, String name, int gridColumn, int gridRow, double gridMinSize, double gridMaxSize, double gridContentsWidth, double gridContentsHight) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_CREATE);
            history_.setRevised(new LadderJsonLadder(idx, name, gridColumn, gridRow));
            historyManager_.push(history_, historyGeneration_);
        }

        treeTableView.getRoot().getChildren().add(idx, new TreeItem<>(new LadderTreeTableIo(name.replace(" ", "_"), idx)));
        ioMap.add(idx, new ConcurrentHashMap<>());
        commentMap.add(idx, new ConcurrentHashMap<>());
        if (scriptIoMap != null) {
            scriptIoMap.add(idx, new ConcurrentHashMap<>());
        }

        LadderPane pane = new LadderPane(ladders, idx, name, gridColumn, gridRow, gridMinSize, gridMaxSize, gridContentsWidth, gridContentsHight);
        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.hvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            scrollPane.requestLayout();
        });
        scrollPane.vvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            scrollPane.requestLayout();
        });
        scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            event.consume();

            if (ladders != null) {
                ladders.onKeyPressed(pane, event, scrollPane);
            }
        });
        tabPane.getTabs().add(idx - 1, new Tab(name, scrollPane));
        pane.startUp(stage_, icons_);
        return pane;
    }

    /**
     *
     * @param tabIndex
     */
    public void ladderRemove(int tabIndex) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            LadderPane pane = (LadderPane) ((ScrollPane) ladders_.getTabLadder().getSelectionModel().getSelectedItem().getContent()).getContent();
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_REMOVE);
            history_.setOriginal(new LadderJsonLadder(pane.getLadder().getIdx(), pane.getLadder().getName(), pane.getLadder().getColumn(), pane.getLadder().getRow()));
            backupBlocks(history_.getOriginal(), pane);
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }

        ladders_.getTreeTableIo().getRoot().getChildren().remove(tabIndex + 1);
        ladders_.getIoMap().remove(tabIndex + 1);
        ladders_.getCommentMap().remove(tabIndex + 1);
        ladders_.getScriptIoMap().remove(tabIndex + 1);

        int index, size;

        for (index = ladders_.getTabLadder().getSelectionModel().getSelectedIndex() + 1, size = ladders_.getTabLadder().getTabs().size(); index < size; index++) {
            ((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(index).getContent()).getContent()).getLadder().setIdx(index);
        }
        ladders_.getTabLadder().getTabs().remove(tabIndex);
    }

    /**
     *
     * @param pane
     * @param grid
     * @return
     */
    public boolean ladderRemoveRow(LadderPane pane, LadderGrid grid) {
        Ladder ladder = pane.getLadder();

        if (ladder.getRow() > 2) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_REMOVE_ROW);
                history_.setOriginal(new LadderJsonLadder(pane.getLadder().getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                backupBlocksRow(history_.getOriginal(), pane, grid.getRowIndex());
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }

            LadderGrid gridBuf, gridColumnBegin;
            LadderGridPane gridPaneBuf;
            int columnIndex, columnSize, rowIndex, rowSize;

            // delete line
            gridColumnBegin = ladder.findGrid(0, grid.getRowIndex());
            gridBuf = gridColumnBegin;

            for (columnIndex = 0, columnSize = ladder.getColumn(); columnIndex < columnSize; columnIndex++) {
                if (gridBuf.getUpLadderGrid() != null) {
                    gridBuf.getUpLadderGrid().setDownLadderGrid(gridBuf.getDownLadderGrid());
                }
                if (gridBuf.getDownLadderGrid() != null) {
                    gridBuf.getDownLadderGrid().setUpLadderGrid(gridBuf.getUpLadderGrid());
                    if (gridBuf.isVertical() != gridBuf.getDownLadderGrid().isVertical()) {
                        gridPaneBuf = pane.findGridPane(gridBuf.getDownLadderGrid());
                        gridBuf.getDownLadderGrid().setVertical(gridBuf.isVertical());
                        gridPaneBuf.changeStrokeVertical();
                        gridPaneBuf.setEditing(true);
                    }
                } else {
                    if (gridBuf.isVertical()) {
                        if (gridBuf.getUpLadderGrid() != null) {
                            gridPaneBuf = pane.findGridPane(gridBuf.getUpLadderGrid());
                            gridBuf.getUpLadderGrid().setVerticalOr(false);
                            gridPaneBuf.changeStrokeVerticalOr();
                            gridPaneBuf.setEditing(true);
                        }
                    }
                }
                ladder.removeLadderGrid(gridBuf);
                pane.getChildren().remove(pane.findGridPane(gridBuf));
                gridBuf = gridBuf.getRightLadderGrid();
            }

            // move line
            for (rowIndex = gridColumnBegin.getRowIndex() + 1, rowSize = ladder.getRow(); rowIndex < rowSize; rowIndex++) {
                gridColumnBegin = gridColumnBegin.getDownLadderGrid();
                gridBuf = gridColumnBegin;
                for (columnIndex = 0, columnSize = ladder.getColumn(); columnIndex < columnSize; columnIndex++) {
                    gridPaneBuf = pane.findGridPane(gridBuf);
                    gridBuf.setRowIndex(rowIndex - 1);
                    pane.setGridRowIndex(gridPaneBuf, rowIndex - 1);
                    if (gridBuf.getBlock() == Ladders.LADDER_BLOCK.CONTENTS) {
                        gridPaneBuf.changeContents();
                    }
                    gridBuf = gridBuf.getRightLadderGrid();
                }
            }
            ladder.setRow(ladder.getRow() - 1);
            pane.layout();
            return true;
        }
        return false;
    }

    /**
     *
     * @param pane
     * @param grid
     * @return
     */
    public boolean ladderInsertRow(LadderPane pane, LadderGrid grid) {
        Ladder ladder = pane.getLadder();
        LadderGrid gridNew, gridBuf, gridRight;
        LadderGridPane gridPaneNew;
        int columnIndex, columnSize, rowIndex;

        if (grid != null) {
            LadderGrid gridColumnBegin;
            LadderGridPane gridPaneBuf;
            int rowSize;

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_INSERT_ROW);
                history_.setRevised(new LadderJsonLadder(pane.getLadder().getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }

            // insert line
            gridColumnBegin = ladder.findGrid(0, ladder.getRow() - 1);
            rowIndex = grid.getRowIndex();
            gridBuf = ladder.findGrid(0, rowIndex);
            gridRight = null;

            for (columnIndex = 0, columnSize = ladder.getColumn(); columnIndex < columnSize; columnIndex++) {
                gridPaneNew = pane.addGrid(columnIndex, rowIndex);
                gridNew = gridPaneNew.getLadderGrid();
                gridNew.setLeftLadderGrid(gridRight);
                gridNew.setUpLadderGrid(gridBuf.getUpLadderGrid());
                if (gridRight != null) {
                    gridRight.setRightLadderGrid(gridNew);
                }
                gridNew.setDownLadderGrid(gridBuf);
                if (columnIndex > 0) {
                    gridNew.setVertical(gridBuf.isVertical());
                    gridPaneNew.changeStrokeVertical();
                    gridNew.setVerticalOr(gridBuf.isVertical());
                    gridPaneNew.changeStrokeVerticalOr();
                    gridPaneNew.setEditing(true);
                }
                gridRight = gridNew;
                if (gridBuf.getUpLadderGrid() != null) {
                    gridBuf.getUpLadderGrid().setDownLadderGrid(gridNew);
                }
                gridBuf.setUpLadderGrid(gridNew);
                gridBuf = gridBuf.getRightLadderGrid();
            }

            for (rowIndex = gridColumnBegin.getRowIndex(), rowSize = grid.getRowIndex(); rowIndex >= rowSize; rowIndex--) {
                gridBuf = gridColumnBegin;
                for (columnIndex = 0, columnSize = ladder.getColumn(); columnIndex < columnSize; columnIndex++) {
                    gridPaneBuf = pane.findGridPane(gridBuf);
                    gridBuf.setRowIndex(rowIndex + 1);
                    pane.setGridRowIndex(gridPaneBuf, rowIndex + 1);
                    if (gridBuf.getBlock() == Ladders.LADDER_BLOCK.CONTENTS) {
                        gridPaneBuf.changeContents();
                    }
                    gridBuf = gridBuf.getRightLadderGrid();
                }
                gridColumnBegin = gridColumnBegin.getUpLadderGrid();
            }
            ladder.setRow(ladder.getRow() + 1);
            pane.layout();
            return true;
        } else {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_INSERT_ROW);
                history_.setRevised(new LadderJsonLadder(pane.getLadder().getIdx(), 0, ladder.getRow()));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }

            // insert line
            rowIndex = ladder.getRow();
            gridBuf = ladder.findGrid(0, rowIndex - 1);
            gridRight = null;

            for (columnIndex = 0, columnSize = ladder.getColumn(); columnIndex < columnSize; columnIndex++) {
                gridPaneNew = pane.addGrid(columnIndex, rowIndex);
                gridNew = gridPaneNew.getLadderGrid();
                gridNew.setLeftLadderGrid(gridRight);
                gridNew.setUpLadderGrid(gridBuf);
                if (gridRight != null) {
                    gridRight.setRightLadderGrid(gridNew);
                }
                gridNew.setDownLadderGrid(null);
                if (columnIndex > 0) {
                    gridPaneNew.setEditing(true);
                }
                gridRight = gridNew;
                gridBuf.setDownLadderGrid(gridNew);
                gridBuf = gridBuf.getRightLadderGrid();
            }
        }
        ladder.setRow(ladder.getRow() + 1);
        pane.layout();
        return true;
    }

    /**
     *
     * @param ladder
     */
    public void ladderMoveLeft(Ladder ladder) {
        int idx = ladder.getIdx();

        if (idx > 1) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_MOVE_LEFT);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx()));
            }

            ((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(idx - 2).getContent()).getContent()).getLadder().setIdx(idx);
            ladder.setIdx(idx - 1);
            ladders_.getTabLadder().getTabs().sort((o1, o2) -> {
                return ((LadderPane) ((ScrollPane) o1.getContent()).getContent()).getLadder().getIdx() - ((LadderPane) ((ScrollPane) o2.getContent()).getContent()).getLadder().getIdx();
            });

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx()));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
        }
    }

    /**
     *
     * @param ladder
     */
    public void ladderMoveRight(Ladder ladder) {
        int idx = ladder.getIdx();

        if (idx < ladders_.getTabLadder().getTabs().size()) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_MOVE_RIGHT);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx()));
            }

            ((LadderPane) ((ScrollPane) ladders_.getTabLadder().getTabs().get(idx).getContent()).getContent()).getLadder().setIdx(idx);
            ladder.setIdx(idx + 1);
            ladders_.getTabLadder().getTabs().sort((o1, o2) -> {
                return ((LadderPane) ((ScrollPane) o1.getContent()).getContent()).getLadder().getIdx() - ((LadderPane) ((ScrollPane) o2.getContent()).getContent()).getLadder().getIdx();
            });

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx()));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
        }
    }

    /**
     *
     * @param pane
     * @param name
     */
    public void ladderChangeName(LadderPane pane, String name) {
        if (!pane.getLadder().getName().equals(name)) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.LADDER_CHANGE_NAME);
                history_.setOriginal(new LadderJsonLadder(pane.getLadder().getIdx(), pane.getLadder().getName()));
            }

            pane.getLadder().setName(name);

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(pane.getLadder().getIdx(), pane.getLadder().getName()));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
        }
        if (pane.isChanged()) {
            ladders_.getTabLadder().getTabs().get(pane.getLadder().getIdx() - 1).setText(name + " *");
        } else {
            ladders_.getTabLadder().getTabs().get(pane.getLadder().getIdx() - 1).setText(name);
        }
    }

    /**
     *
     * @param index
     */
    public void ladderChangeSelect(int index) {
        ladders_.getTabLadder().getSelectionModel().select(index);
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @return
     */
    public boolean blockClear(Ladder ladder, LadderGrid grid, LadderGridPane gridPane) {
        if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getOriginal().addBlock(backupBlock(grid));
            }

            grid.clear();
            gridPane.changeBlock();

            if (isBlockChanging_) {
                blockChangingGridPane_.add(gridPane);
            } else {
                gridPane.setEditing(true);
            }

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getRevised().addBlock(backupBlock(grid));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param pane
     * @param grids
     * @return
     */
    public boolean blocksClear(LadderPane pane, List<LadderGrid> grids) {
        Ladder ladder;
        LadderGrid grid;
        LadderGridPane gridPane;
        int index, size;

        ladder = pane.getLadder();
        blockChangeStart();
        for (index = 0, size = grids.size(); index < size; index++) {
            grid = grids.get(index);
            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                blockChangeOriginal(ladder, grid);
            }
        }

        for (index = 0, size = grids.size(); index < size; index++) {
            grid = grids.get(index);
            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                gridPane = pane.findGridPane(grid);
                grid.clear();
                gridPane.changeBlock();
                blockChangingGridPane_.add(gridPane);

                if (grid.isVertical()) {
                    if (grids.contains(grid.getUpLadderGrid())) {
                        grid.setVertical(false);
                        grid.getUpLadderGrid().setVerticalOr(false);
                        gridPane.changeStrokeVertical();
                        pane.findGridPane(grid.getUpLadderGrid()).changeStrokeVerticalOr();
                    }
                }

                if (grid.isVerticalOr()) {
                    if (grids.contains(grid.getDownLadderGrid())) {
                        grid.setVerticalOr(false);
                        grid.getDownLadderGrid().setVertical(false);
                        gridPane.changeStrokeVerticalOr();
                        pane.findGridPane(grid.getDownLadderGrid()).changeStrokeVertical();
                    }
                }
                blockChangeRevised(ladder, grid);
            }
        }
        return blockChangeEnd(ladder.getColumn(), ladder.getRow());
    }

    /**
     *
     * @param grids
     */
    public void blocksCopy(List<LadderGrid> grids) {
        LadderGrid grid;
        int index, size;

        copyGrids_.clear();
        for (index = 0, size = grids.size(); index < size; index++) {
            grid = grids.get(index);
            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                copyGrids_.add(grid.copy());
            }
        }
    }

    /**
     *
     * @param pane
     * @param grids
     * @return
     */
    public boolean blocksCut(LadderPane pane, List<LadderGrid> grids) {
        Ladder ladder;
        LadderGrid grid;
        LadderGridPane gridPane;
        int index, size;

        copyGrids_.clear();
        ladder = pane.getLadder();
        blockChangeStart();
        for (index = 0, size = grids.size(); index < size; index++) {
            grid = grids.get(index);
            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                blockChangeOriginal(ladder, grid);
                copyGrids_.add(grid.copy());
            }
        }

        for (index = 0, size = grids.size(); index < size; index++) {
            grid = grids.get(index);
            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                gridPane = pane.findGridPane(grid);
                grid.clear();
                gridPane.changeBlock();
                blockChangingGridPane_.add(gridPane);

                if (grid.isVertical()) {
                    if (grids.contains(grid.getUpLadderGrid())) {
                        grid.setVertical(false);
                        grid.getUpLadderGrid().setVerticalOr(false);
                        gridPane.changeStrokeVertical();
                        pane.findGridPane(grid.getUpLadderGrid()).changeStrokeVerticalOr();
                    }
                }

                if (grid.isVerticalOr()) {
                    if (grids.contains(grid.getDownLadderGrid())) {
                        grid.setVerticalOr(false);
                        grid.getDownLadderGrid().setVertical(false);
                        gridPane.changeStrokeVerticalOr();
                        pane.findGridPane(grid.getDownLadderGrid()).changeStrokeVertical();
                    }
                }
                blockChangeRevised(ladder, grid);
            }
        }
        return blockChangeEnd(ladder.getColumn(), ladder.getRow());
    }

    /**
     *
     * @param pane
     * @param grid
     * @return
     */
    public boolean blockPaste(LadderPane pane, LadderGrid grid) {
        if (!copyGrids_.isEmpty()) {
            Ladder ladder;
            LadderGrid copyGrid;
            LadderGridPane gridPane, gridPaneBuf;
            int columnIndex, rowIndex, minColumnIndex, minRowIndex, maxColumnIndex, maxRowIndex, index, size;
            boolean isOut = false;

            minColumnIndex = Integer.MAX_VALUE;
            minRowIndex = Integer.MAX_VALUE;
            maxColumnIndex = 0;
            maxRowIndex = 0;
            for (index = 0, size = copyGrids_.size(); index < size; index++) {
                copyGrid = copyGrids_.get(index);

                if (copyGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                    columnIndex = copyGrid.getColumnIndex();
                    if (columnIndex < minColumnIndex) {
                        minColumnIndex = columnIndex;
                    }
                    if (columnIndex > maxColumnIndex) {
                        maxColumnIndex = columnIndex;
                    }

                    rowIndex = copyGrid.getRowIndex();
                    if (rowIndex < minRowIndex) {
                        minRowIndex = rowIndex;
                    }
                    if (rowIndex > maxRowIndex) {
                        maxRowIndex = rowIndex;
                    }

                    switch (copyGrid.getBlock()) {
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
                        case TIMER:
                        case TIMER_NOT:
                        case COUNTER:
                        case COUNTER_NOT:
                        case MOVE:
                        case SCRIPT:
                            isOut = true;
                            break;
                    }
                }
            }

            ladder = pane.getLadder();
            columnIndex = grid.getColumnIndex();
            rowIndex = grid.getRowIndex();
            if (isOut) {
                if (((columnIndex + (maxColumnIndex - minColumnIndex)) != (ladder.getColumn() - 1)) || ((rowIndex + (maxRowIndex - minRowIndex)) > (ladder.getRow() - 1))) {
                    return false;
                }
            } else {
                if (((columnIndex + (maxColumnIndex - minColumnIndex)) > (ladder.getColumn() - 2)) || ((rowIndex + (maxRowIndex - minRowIndex)) > (ladder.getRow() - 1))) {
                    return false;
                }
            }

            blockChangeStart();
            for (index = 0, size = copyGrids_.size(); index < size; index++) {
                copyGrid = copyGrids_.get(index);

                if (copyGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                    grid = ladder.findGrid(columnIndex + copyGrid.getColumnIndex() - minColumnIndex, rowIndex + copyGrid.getRowIndex() - minRowIndex);
                    switch (copyGrid.getBlock()) {
                        case EMPTY:
                            if ((grid.getColumnIndex() > 0) && (grid.getColumnIndex() < ladder.getColumn())) {
                                blockChangeOriginal(ladder, grid);
                            }
                            break;
                        case BLOCK_COMMENT:
                        case CONNECT_LINE:
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
                            if ((grid.getColumnIndex() > 0) && (grid.getColumnIndex() < (ladder.getColumn() - 1))) {
                                blockChangeOriginal(ladder, grid);
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
                        case SCRIPT:
                            if (grid.getColumnIndex() == (ladder.getColumn() - 1)) {
                                blockChangeOriginal(ladder, grid);
                            }
                            break;
                    }
                }
            }

            for (index = 0, size = copyGrids_.size(); index < size; index++) {
                copyGrid = copyGrids_.get(index);

                if (copyGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                    grid = ladder.findGrid(columnIndex + copyGrid.getColumnIndex() - minColumnIndex, rowIndex + copyGrid.getRowIndex() - minRowIndex);
                    gridPane = pane.findGridPane(grid);
                    switch (copyGrid.getBlock()) {
                        case EMPTY:
                            if ((grid.getColumnIndex() > 0) && (grid.getColumnIndex() < ladder.getColumn())) {
                                grid.clear();
                                if ((grid.getColumnIndex() > 1) && (grid.getRowIndex() > 1)) {
                                    if (grid.isVertical() != copyGrid.isVertical()) {
                                        grid.setVertical(copyGrid.isVertical());
                                        gridPane.changeStrokeVertical();
                                    }
                                    if (grid.getUpLadderGrid().isVerticalOr() != copyGrid.isVertical()) {
                                        grid.getUpLadderGrid().setVerticalOr(copyGrid.isVertical());
                                        gridPaneBuf = pane.findGridPane(grid.getUpLadderGrid());
                                        gridPaneBuf.changeStrokeVerticalOr();
                                        gridPaneBuf.setEditing(true);
                                    }
                                }
                                gridPane.changeBlock();
                                gridPane.changeBlockIO();
                                gridPane.changeAddress();
                                gridPane.changeComment();
                                gridPane.changeBlockFunction();
                                gridPane.changeBlockScript();
                                gridPane.setEditing(true);

                                blockChangeRevised(ladder, grid);
                            }
                            break;
                        case BLOCK_COMMENT:
                            if ((grid.getColumnIndex() > 0) && (grid.getColumnIndex() < ladder.getColumn())) {
                                grid.setBlock(copyGrid.getBlock());
                                grid.setBlockFunctions(copyGrid.getBlockFunctions());
                                if ((grid.getColumnIndex() > 1) && (grid.getRowIndex() > 1)) {
                                    if (grid.isVertical() != copyGrid.isVertical()) {
                                        grid.setVertical(copyGrid.isVertical());
                                        gridPane.changeStrokeVertical();
                                    }
                                    if (grid.getUpLadderGrid().isVerticalOr() != copyGrid.isVertical()) {
                                        grid.getUpLadderGrid().setVerticalOr(copyGrid.isVertical());
                                        gridPaneBuf = pane.findGridPane(grid.getUpLadderGrid());
                                        gridPaneBuf.changeStrokeVerticalOr();
                                        gridPaneBuf.setEditing(true);
                                    }
                                }
                                grid.setAddress(copyGrid.getAddress());
                                gridPane.changeBlock();
                                grid.setComment(copyGrid.getComment());
                                gridPane.changeComment();
                                gridPane.setEditing(true);

                                blockChangeRevised(ladder, grid);
                            }
                            break;
                        case CONNECT_LINE:
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
                            if ((grid.getColumnIndex() > 0) && (grid.getColumnIndex() < (ladder.getColumn() - 1))) {
                                grid.setBlock(copyGrid.getBlock());
                                grid.setBlockFunctions(copyGrid.getBlockFunctions());
                                if ((grid.getColumnIndex() > 1) && (grid.getRowIndex() > 1)) {
                                    if (grid.isVertical() != copyGrid.isVertical()) {
                                        grid.setVertical(copyGrid.isVertical());
                                        gridPane.changeStrokeVertical();
                                    }
                                    if (grid.getUpLadderGrid().isVerticalOr() != copyGrid.isVertical()) {
                                        grid.getUpLadderGrid().setVerticalOr(copyGrid.isVertical());
                                        gridPaneBuf = pane.findGridPane(grid.getUpLadderGrid());
                                        gridPaneBuf.changeStrokeVerticalOr();
                                        gridPaneBuf.setEditing(true);
                                    }
                                }
                                grid.setAddress(copyGrid.getAddress());
                                gridPane.changeBlock();

                                if (LadderGrid.LADDER_GRID_INITIAL_ADDRESS.equals(copyGrid.getAddress())) {
                                    grid.setComment(ladders_.getComment(ladder.getIdx(), LadderGrid.LADDER_GRID_INITIAL_COMMENT));
                                } else {
                                    if (copyGrid.getAddress().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                        if (ladders_.isComment(ladder.getIdx(), copyGrid.getAddress())) {
                                            grid.setComment(ladders_.getComment(ladder.getIdx(), copyGrid.getAddress()));
                                        } else {
                                            changeComment(ladder.getIdx(), copyGrid.getAddress(), copyGrid.getComment());
                                        }
                                    } else {
                                        if (ladders_.isComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress())) {
                                            grid.setComment(ladders_.getComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress()));
                                        } else {
                                            changeComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress(), copyGrid.getComment());
                                        }
                                    }
                                }
                                gridPane.changeComment();
                                gridPane.setEditing(true);

                                blockChangeRevised(ladder, grid);
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
                        case SCRIPT:
                            if (grid.getColumnIndex() == (ladder.getColumn() - 1)) {
                                grid.setBlock(copyGrid.getBlock());
                                grid.setBlockValue(copyGrid.getBlockValue());
                                grid.setBlockFunctions(copyGrid.getBlockFunctions());
                                grid.setBlockScript(copyGrid.getBlockScript());
                                if ((grid.getColumnIndex() > 1) && (grid.getRowIndex() > 1)) {
                                    if (grid.isVertical() != copyGrid.isVertical()) {
                                        grid.setVertical(copyGrid.isVertical());
                                        gridPane.changeStrokeVertical();
                                    }
                                    if (grid.getUpLadderGrid().isVerticalOr() != copyGrid.isVertical()) {
                                        grid.getUpLadderGrid().setVerticalOr(copyGrid.isVertical());
                                        gridPaneBuf = pane.findGridPane(grid.getUpLadderGrid());
                                        gridPaneBuf.changeStrokeVerticalOr();
                                        gridPaneBuf.setEditing(true);
                                    }
                                }
                                grid.setAddress(copyGrid.getAddress());
                                gridPane.changeBlock();

                                if (LadderGrid.LADDER_GRID_INITIAL_ADDRESS.equals(copyGrid.getAddress())) {
                                    grid.setComment(ladders_.getComment(ladder.getIdx(), LadderGrid.LADDER_GRID_INITIAL_COMMENT));
                                } else {
                                    if (copyGrid.getAddress().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                        if (ladders_.isComment(ladder.getIdx(), copyGrid.getAddress())) {
                                            grid.setComment(ladders_.getComment(ladder.getIdx(), copyGrid.getAddress()));
                                        } else {
                                            changeComment(ladder.getIdx(), copyGrid.getAddress(), copyGrid.getComment());
                                        }
                                    } else {
                                        if (ladders_.isComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress())) {
                                            grid.setComment(ladders_.getComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress()));
                                        } else {
                                            changeComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, copyGrid.getAddress(), copyGrid.getComment());
                                        }
                                    }
                                }
                                gridPane.changeComment();
                                gridPane.setEditing(true);

                                blockChangeRevised(ladder, grid);
                            }
                            break;
                    }
                }
            }
            return blockChangeEnd(ladder.getColumn(), ladder.getRow());
        }
        return false;
    }

    /**
     *
     * @param scrollPane
     * @param pane
     * @param grid
     * @param isSelectRange
     */
    public void blockChangeSelect(ScrollPane scrollPane, LadderPane pane, LadderGrid grid, boolean isSelectRange) {
        if (pane != null) {
            LadderGrid previousGrid;
            LadderGridPane previousGridPane;

            if (grid != null) {
                LadderGridPane gridPane;

                if (pane.getPreviousGrids().isEmpty()) {
                    gridPane = pane.findGridPane(grid);

                    grid.setSelect(true);
                    gridPane.changeStrokeSelect();
                    if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                        grid.setSelectRange(false);
                        gridPane.changeBackGroundSelect();
                    }

                    blockChangeView(scrollPane, gridPane);
                    pane.getPreviousGrids().add(grid);
                } else {
                    int index, size;

                    if (isSelectRange) {
                        for (index = 0, size = pane.getPreviousGrids().size(); index < size; index++) {
                            if (pane.getPreviousGrids().get(index).equals(grid)) {
                                pane.getPreviousGrids().remove(index);
                                break;
                            }
                        }

                        if (!grid.isSelect() && !grid.isSelectRange()) {
                            gridPane = pane.findGridPane(grid);
                            grid.setSelect(true);
                            gridPane.changeStrokeSelect();
                            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                grid.setSelectRange(false);
                                gridPane.changeBackGroundSelect();
                            }
                        } else {
                            gridPane = pane.findGridPane(grid);
                            grid.setSelect(grid.isSelectRange());
                            gridPane.changeStrokeSelect();
                            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                grid.setSelectRange(!grid.isSelectRange());
                                gridPane.changeBackGroundSelect();
                            }
                        }

                        previousGrid = pane.getPreviousGrids().get(pane.getPreviousGrids().size() - 1);
                        previousGridPane = pane.findGridPane(previousGrid);
                        previousGrid.setSelect(false);
                        previousGridPane.changeStrokeSelect();
                        if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                            previousGrid.setSelectRange(true);
                            previousGridPane.changeBackGroundSelect();
                        }

                        blockChangeView(scrollPane, gridPane);
                        pane.getPreviousGrids().add(grid);
                    } else {
                        size = pane.getPreviousGrids().size();
                        if (size == 1) {
                            if (!grid.equals(pane.getPreviousGrids().get(pane.getPreviousGrids().size() - 1))) {
                                gridPane = pane.findGridPane(grid);
                                grid.setSelect(true);
                                gridPane.changeStrokeSelect();
                                if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                    grid.setSelectRange(false);
                                    gridPane.changeBackGroundSelect();
                                }

                                previousGrid = pane.getPreviousGrids().get(0);
                                previousGridPane = pane.findGridPane(previousGrid);
                                if (previousGridPane != null) {
                                    previousGrid.setSelect(false);
                                    previousGridPane.changeStrokeSelect();
                                    if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                        previousGrid.setSelectRange(false);
                                        previousGridPane.changeBackGroundSelect();
                                    }
                                }

                                blockChangeView(scrollPane, gridPane);
                                pane.getPreviousGrids().set(0, grid);
                            }
                        } else {
                            for (index = 0; index < size; index++) {
                                previousGrid = pane.getPreviousGrids().get(index);
                                if (!grid.equals(previousGrid)) {
                                    previousGridPane = pane.findGridPane(previousGrid);
                                    if (previousGridPane != null) {
                                        previousGrid.setSelect(false);
                                        previousGridPane.changeStrokeSelect();
                                        if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                            previousGrid.setSelectRange(false);
                                            previousGridPane.changeBackGroundSelect();
                                        }
                                    }
                                }
                            }
                            pane.getPreviousGrids().clear();

                            gridPane = pane.findGridPane(grid);
                            grid.setSelect(true);
                            gridPane.changeStrokeSelect();
                            if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                                grid.setSelectRange(false);
                                gridPane.changeBackGroundSelect();
                            }

                            blockChangeView(scrollPane, gridPane);
                            pane.getPreviousGrids().add(grid);
                        }
                    }
                }
            } else {
                if (!pane.getPreviousGrids().isEmpty()) {
                    int index, size;

                    for (index = 0, size = pane.getPreviousGrids().size(); index < size; index++) {
                        previousGrid = pane.getPreviousGrids().get(index);
                        previousGrid.setSelect(false);
                        previousGrid.setSelectRange(false);
                        previousGridPane = pane.findGridPane(previousGrid);
                        if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                            previousGridPane.changeBackGroundSelect();
                            previousGridPane.changeStrokeSelect();
                        }
                    }
                    pane.getPreviousGrids().clear();
                }
            }
        }
    }

    /**
     *
     * @param scrollPane
     * @param pane
     * @param rowIndex
     * @param grid
     * @param isSelectRange
     */
    public void blockChangeSelectRow(ScrollPane scrollPane, LadderPane pane, int rowIndex, LadderGrid grid, boolean isSelectRange) {
        if ((pane != null) && (grid != null)) {
            LadderGrid gridBuf, previousGrid;
            LadderGridPane gridPane, previousGridPane;
            int index, size;

            gridBuf = grid;
            if (!pane.getPreviousGrids().isEmpty()) {
                if (isSelectRange) {
                    previousGrid = pane.getPreviousGrids().get(pane.getPreviousGrids().size() - 1);

                    if (!gridBuf.equals(previousGrid)) {
                        previousGridPane = pane.findGridPane(previousGrid);
                        previousGrid.setSelect(false);
                        previousGridPane.changeStrokeSelect();
                        if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                            previousGrid.setSelectRange(true);
                            previousGridPane.changeBackGroundSelect();
                        }
                    }
                } else {
                    for (index = 0, size = pane.getPreviousGrids().size(); index < size; index++) {
                        previousGrid = pane.getPreviousGrids().get(index);
                        previousGrid.setSelect(false);
                        previousGrid.setSelectRange(false);
                        previousGridPane = pane.findGridPane(previousGrid);
                        if (previousGrid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                            previousGridPane.changeBackGroundSelect();
                            previousGridPane.changeStrokeSelect();
                        }
                    }
                    pane.getPreviousGrids().clear();
                }
            }

            for (index = 0, size = pane.getPreviousGrids().size(); index < size; index++) {
                if (pane.getPreviousGrids().get(index).equals(gridBuf)) {
                    pane.getPreviousGrids().remove(index);
                    break;
                }
            }

            grid = pane.getLadder().findGrid(0, rowIndex);
            for (index = 1, size = pane.getLadder().getColumn(); index < size; index++) {
                grid = grid.getRightLadderGrid();
                gridPane = pane.findGridPane(grid);
                if ((!grid.equals(gridBuf)) && (!grid.isSelectRange())) {
                    grid.setSelect(false);
                    gridPane.changeStrokeSelect();
                    if (grid.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                        grid.setSelectRange(true);
                        gridPane.changeBackGroundSelect();
                    }

                    if (!grid.isSelect()) {
                        pane.getPreviousGrids().add(grid);
                    }
                }
            }

            gridPane = pane.findGridPane(gridBuf);
            gridBuf.setSelect(true);
            gridPane.changeStrokeSelect();
            if (gridBuf.getBlock() != Ladders.LADDER_BLOCK.CONTENTS) {
                gridBuf.setSelectRange(false);
                gridPane.changeBackGroundSelect();
            }
            blockChangeView(scrollPane, gridPane);
            pane.getPreviousGrids().add(gridBuf);
        }
    }

    private void blockChangeView(ScrollPane scrollPane, LadderGridPane gridPane) {
        if (scrollPane != null) {
            double viewMinX, viewMinY, viewMaxX, viewMaxY;
            double gridMinX, gridMinY, gridMaxX, gridMaxY;
            double hMin, hSub, contWidth, contHeight;
            double vMin, vSub, viewWidth, viewHeight;

            viewMinX = -scrollPane.getViewportBounds().getMinX();
            viewMinY = -scrollPane.getViewportBounds().getMinY();
            viewMaxX = scrollPane.getViewportBounds().getWidth() + viewMinX;
            viewMaxY = scrollPane.getViewportBounds().getHeight() + viewMinY;

            gridMinX = gridPane.getBoundsInParent().getMinX();
            gridMinY = gridPane.getBoundsInParent().getMinY();
            gridMaxX = gridPane.getBoundsInParent().getMaxX();
            gridMaxY = gridPane.getBoundsInParent().getMaxY();

            if (gridMinX < viewMinX) {
                hMin = scrollPane.getHmin();
                hSub = scrollPane.getHmax() - hMin;
                contWidth = scrollPane.getContent().getLayoutBounds().getWidth();
                viewWidth = scrollPane.getViewportBounds().getWidth();
                scrollPane.setHvalue(((gridMinX / (contWidth - viewWidth)) * hSub) + hMin);
            } else if (gridMaxX > viewMaxX) {
                hMin = scrollPane.getHmin();
                hSub = scrollPane.getHmax() - hMin;
                contWidth = scrollPane.getContent().getLayoutBounds().getWidth();
                viewWidth = scrollPane.getViewportBounds().getWidth();
                scrollPane.setHvalue((((gridMaxX - viewWidth) / (contWidth - viewWidth)) * hSub) + hMin);
            }

            if (gridMinY < viewMinY) {
                vMin = scrollPane.getVmin();
                vSub = scrollPane.getVmax() - vMin;
                contHeight = scrollPane.getContent().getLayoutBounds().getHeight();
                viewHeight = scrollPane.getViewportBounds().getHeight();
                scrollPane.setVvalue(((gridMinY / (contHeight - viewHeight)) * vSub) + vMin);
            } else if (gridMaxY > viewMaxY) {
                vMin = scrollPane.getVmin();
                vSub = scrollPane.getVmax() - vMin;
                contHeight = scrollPane.getContent().getLayoutBounds().getHeight();
                viewHeight = scrollPane.getViewportBounds().getHeight();
                scrollPane.setVvalue((((gridMaxY - viewHeight) / (contHeight - viewHeight)) * vSub) + vMin);
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean blockChangeStart() {
        if (!isDisableHistory_ && !isBlockChanging_) {
            blockChangingGridPane_.clear();
            isBlockChanging_ = true;
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @return
     */
    public boolean blockChangeOriginal(Ladder ladder, LadderGrid grid) {
        if (!isDisableHistory_ && isBlockChanging_) {
            if (history_.getOriginal() == null) {
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            }
            history_.getOriginal().addBlock(backupBlock(grid));
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @return
     */
    public boolean blockChangeRevised(Ladder ladder, LadderGrid grid) {
        if (!isDisableHistory_ && isBlockChanging_) {
            if (history_.getRevised() == null) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            }
            history_.getRevised().addBlock(backupBlock(grid));
            return true;
        }
        return false;
    }

    /**
     *
     * @param column
     * @param row
     * @return
     */
    public boolean blockChangeEnd(int column, int row) {
        if (!isDisableHistory_ && isBlockChanging_) {
            isBlockChanging_ = false;

            if (history_.getOriginal() != null) {
                if (history_.getOriginal().getBlocks() != null) {
                    if (!history_.getOriginal().getBlocks().isEmpty()) {
                        history_.getOriginal().sortBlocks(column, row);
                    } else {
                        history_.setOriginal(null);
                    }
                } else {
                    history_.setOriginal(null);
                }
            }

            if (history_.getRevised() != null) {
                if (history_.getRevised().getBlocks() != null) {
                    if (!history_.getRevised().getBlocks().isEmpty()) {
                        history_.getRevised().sortBlocks(column, row);
                    } else {
                        history_.setRevised(null);
                    }
                } else {
                    history_.setRevised(null);
                }
            }

            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
                for (int index = 0, size = blockChangingGridPane_.size(); index < size; index++) {
                    blockChangingGridPane_.get(index).setEditing(true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param previousGrid
     * @param previousGridPane
     * @param grid
     * @param gridPane
     * @param isVertical
     */
    public void blockChangeVerticalOrVertical(Ladder ladder, LadderGrid previousGrid, LadderGridPane previousGridPane, LadderGrid grid, LadderGridPane gridPane, boolean isVertical) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), previousGrid.getColumnIndex(), previousGrid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
            history_.getOriginal().addBlock(backupBlock(previousGrid));
        }

        grid.setVerticalOr(isVertical);
        gridPane.changeStrokeVerticalOr();
        previousGrid.setVertical(isVertical);
        previousGridPane.changeStrokeVertical();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(gridPane);
            blockChangingGridPane_.add(previousGridPane);
        } else {
            gridPane.setEditing(true);
            previousGridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            history_.getRevised().addBlock(backupBlock(previousGrid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param previousGrid
     * @param grid
     * @param previousGridPane
     * @param gridPane
     * @param isVertical
     */
    public void blockChangeVerticalVerticalOr(Ladder ladder, LadderGrid previousGrid, LadderGridPane previousGridPane, LadderGrid grid, LadderGridPane gridPane, boolean isVertical) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), previousGrid.getColumnIndex(), previousGrid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
            history_.getOriginal().addBlock(backupBlock(previousGrid));
        }

        grid.setVertical(isVertical);
        gridPane.changeStrokeVertical();
        previousGrid.setVerticalOr(isVertical);
        previousGridPane.changeStrokeVerticalOr();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(gridPane);
            blockChangingGridPane_.add(previousGridPane);
        } else {
            gridPane.setEditing(true);
            previousGridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            history_.getRevised().addBlock(backupBlock(previousGrid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param isVertical
     */
    public void blockChangeVertical(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, boolean isVertical) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
        }

        grid.setVertical(isVertical);
        gridPane.changeStrokeVertical();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(gridPane);
        } else {
            gridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param isVertical
     */
    public void blockChangeVerticalOr(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, boolean isVertical) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
        }

        grid.setVerticalOr(isVertical);
        gridPane.changeStrokeVerticalOr();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(gridPane);
        } else {
            gridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param previousGrid
     * @param grid
     * @param gridPane
     * @param isConnect
     */
    public void blockChangeConnectLineLeft(Ladder ladder, LadderGrid previousGrid, LadderGrid grid, LadderGridPane gridPane, boolean isConnect) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), previousGrid.getColumnIndex(), previousGrid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
            history_.getOriginal().addBlock(backupBlock(previousGrid));
        }

        grid.clear();
        if (isConnect) {
            grid.setBlock(Ladders.LADDER_BLOCK.CONNECT_LINE);
        } else {
            grid.setBlock(Ladders.LADDER_BLOCK.EMPTY);
        }
        gridPane.changeBlock();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(gridPane);
        } else {
            gridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            history_.getRevised().addBlock(backupBlock(previousGrid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param previousGrid
     * @param grid
     * @param previousGridPane
     * @param isConnect
     */
    public void blockChangeConnectLineRight(Ladder ladder, LadderGrid previousGrid, LadderGrid grid, LadderGridPane previousGridPane, boolean isConnect) {
        if (!isDisableHistory_ && !isBlockChanging_) {
            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), previousGrid.getColumnIndex(), previousGrid.getRowIndex()));
            history_.getOriginal().addBlock(backupBlock(grid));
            history_.getOriginal().addBlock(backupBlock(previousGrid));
        }

        previousGrid.clear();
        if (isConnect) {
            previousGrid.setBlock(Ladders.LADDER_BLOCK.CONNECT_LINE);
        } else {
            previousGrid.setBlock(Ladders.LADDER_BLOCK.EMPTY);
        }
        previousGridPane.changeBlock();

        if (isBlockChanging_) {
            blockChangingGridPane_.add(previousGridPane);
        } else {
            previousGridPane.setEditing(true);
        }

        if (!isDisableHistory_ && !isBlockChanging_) {
            history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
            history_.getRevised().addBlock(backupBlock(grid));
            history_.getRevised().addBlock(backupBlock(previousGrid));
            if (checkDelta()) {
                historyManager_.push(history_, historyGeneration_);
            }
        }
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param block
     * @return
     */
    public boolean blockChangeBlock(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, Ladders.LADDER_BLOCK block) {
        if (!grid.getBlock().equals(block)) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getOriginal().addBlock(backupBlock(grid));
            }

            grid.setBlock(block);
            gridPane.changeBlock();

            if (isBlockChanging_) {
                blockChangingGridPane_.add(gridPane);
            } else {
                gridPane.setEditing(true);
            }

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getRevised().addBlock(backupBlock(grid));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param address
     * @return
     */
    public boolean blockChangeAddress(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, String address) {
        if (!grid.getAddress().equals(address)) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getOriginal().addBlock(backupBlock(grid));
            }

            grid.setAddress(address);
            gridPane.changeAddress();
            gridPane.changeBlockIO();

            if (LadderGrid.LADDER_GRID_INITIAL_ADDRESS.equals(address)) {
                grid.setComment(ladders_.getComment(ladder.getIdx(), LadderGrid.LADDER_GRID_INITIAL_COMMENT));
            } else {
                if (address.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                    if (ladders_.isComment(ladder.getIdx(), address)) {
                        grid.setComment(ladders_.getComment(ladder.getIdx(), address));
                    } else {
                        grid.setComment(LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                    }
                } else {
                    if (ladders_.isComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, address)) {
                        grid.setComment(ladders_.getComment(Ladders.LADDER_GLOBAL_ADDRESS_INDEX, address));
                    } else {
                        grid.setComment(LadderGrid.LADDER_GRID_INITIAL_COMMENT);
                    }
                }
            }
            gridPane.changeComment();

            if (isBlockChanging_) {
                blockChangingGridPane_.add(gridPane);
            } else {
                gridPane.setEditing(true);
            }

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getRevised().addBlock(backupBlock(grid));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param comment
     * @return
     */
    public boolean blockChangeComment(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, String comment) {
        if (!grid.getComment().equals(comment)) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getOriginal().addBlock(backupBlock(grid));
            }

            grid.setComment(comment);
            gridPane.changeComment();

            if (isBlockChanging_) {
                blockChangingGridPane_.add(gridPane);
            } else {
                gridPane.setEditing(true);
            }

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getRevised().addBlock(backupBlock(grid));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param blockFunctions
     * @return
     */
    public boolean blockChangeFunction(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, List<LadderJsonBlock.JsonBlockFunction> blockFunctions) {
        boolean result = false;

        if (blockFunctions != null) {
            boolean changed = false;
            int i;

            result = true;
            for (i = blockFunctions.size(); i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                grid.getBlockFunctions()[i - 1].clear();
            }
            for (i = 0; i < blockFunctions.size(); i++) {
                if (blockFunctions.get(i).address != null) {
                    if (!blockFunctions.get(i).address.equals(grid.getBlockFunctions()[i].getAddress())) {
                        if (!changed && !isDisableHistory_ && !isBlockChanging_) {
                            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                            history_.getOriginal().addBlock(backupBlock(grid));
                        }

                        grid.getBlockFunctions()[i].setAddress(blockFunctions.get(i).address);
                        grid.getBlockFunctions()[i].setValue(LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_VALUE);
                        grid.getBlockFunctions()[i].setRadix(LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX);
                        grid.getBlockFunctions()[i].setNumber(false);
                        changed = true;
                    }
                } else if ((blockFunctions.get(i).value != null) && (blockFunctions.get(i).radix != null)) {
                    if ((blockFunctions.get(i).value != grid.getBlockFunctions()[i].getValue()) || (blockFunctions.get(i).radix != grid.getBlockFunctions()[i].getRadix())) {
                        if (!isDisableHistory_ && !isBlockChanging_) {
                            history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                            history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                            history_.getOriginal().addBlock(backupBlock(grid));
                        }

                        grid.getBlockFunctions()[i].setAddress(LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_ADDRESS);
                        grid.getBlockFunctions()[i].setValue(blockFunctions.get(i).value);
                        grid.getBlockFunctions()[i].setRadix(blockFunctions.get(i).radix);
                        grid.getBlockFunctions()[i].setNumber(true);
                        changed = true;
                    }
                } else {
                    result = false;
                }
            }

            if (changed) {
                gridPane.changeBlockFunction();

                if (isBlockChanging_) {
                    blockChangingGridPane_.add(gridPane);
                } else {
                    gridPane.setEditing(true);
                }

                if (!isDisableHistory_ && !isBlockChanging_) {
                    history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                    history_.getRevised().addBlock(backupBlock(grid));
                    if (checkDelta()) {
                        historyManager_.push(history_, historyGeneration_);
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param functionValue
     * @param index
     * @return
     */
    public boolean blockChangeFunctionValue(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, String functionValue, int index) {
        Matcher mRealNumber = PATTERN_REAL_NUMBER.matcher(functionValue);

        if (mRealNumber.find()) {
            if (mRealNumber.group(1) != null) {
                if ((grid.getBlockFunctions()[index].getValue() != Double.parseDouble(mRealNumber.group(1))) || (grid.getBlockFunctions()[index].getRadix() != 10)) {
                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                        history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getOriginal().addBlock(backupBlock(grid));
                    }

                    grid.getBlockFunctions()[index].setAddress(LadderGrid.LADDER_GRID_INITIAL_ADDRESS);
                    grid.getBlockFunctions()[index].setValue(Double.parseDouble(mRealNumber.group(1)));
                    grid.getBlockFunctions()[index].setRadix(10);
                    grid.getBlockFunctions()[index].setNumber(true);
                    gridPane.changeBlockFunction();

                    if (isBlockChanging_) {
                        blockChangingGridPane_.add(gridPane);
                    } else {
                        gridPane.setEditing(true);
                    }

                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getRevised().addBlock(backupBlock(grid));
                        if (checkDelta()) {
                            historyManager_.push(history_, historyGeneration_);
                        }
                    }
                    return true;
                }
            } else if (mRealNumber.group(2) != null) {
                if ((grid.getBlockFunctions()[index].getValue() != Long.parseLong(mRealNumber.group(2), 16)) || (grid.getBlockFunctions()[index].getRadix() != 16)) {
                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                        history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getOriginal().addBlock(backupBlock(grid));
                    }

                    grid.getBlockFunctions()[index].setAddress(LadderGrid.LADDER_GRID_INITIAL_ADDRESS);
                    grid.getBlockFunctions()[index].setValue(Long.parseLong(mRealNumber.group(2), 16));
                    grid.getBlockFunctions()[index].setRadix(16);
                    grid.getBlockFunctions()[index].setNumber(true);
                    gridPane.changeBlockFunction();

                    if (isBlockChanging_) {
                        blockChangingGridPane_.add(gridPane);
                    } else {
                        gridPane.setEditing(true);
                    }

                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getRevised().addBlock(backupBlock(grid));
                        if (checkDelta()) {
                            historyManager_.push(history_, historyGeneration_);
                        }
                    }
                    return true;
                }
            } else if (mRealNumber.group(3) != null) {
                if ((grid.getBlockFunctions()[index].getValue() != Long.parseLong(mRealNumber.group(3), 2)) || (grid.getBlockFunctions()[index].getRadix() != 2)) {
                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                        history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getOriginal().addBlock(backupBlock(grid));
                    }

                    grid.getBlockFunctions()[index].setAddress(LadderGrid.LADDER_GRID_INITIAL_ADDRESS);
                    grid.getBlockFunctions()[index].setValue(Long.parseLong(mRealNumber.group(3), 2));
                    grid.getBlockFunctions()[index].setRadix(2);
                    grid.getBlockFunctions()[index].setNumber(true);
                    gridPane.changeBlockFunction();

                    if (isBlockChanging_) {
                        blockChangingGridPane_.add(gridPane);
                    } else {
                        gridPane.setEditing(true);
                    }

                    if (!isDisableHistory_ && !isBlockChanging_) {
                        history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                        history_.getRevised().addBlock(backupBlock(grid));
                        if (checkDelta()) {
                            historyManager_.push(history_, historyGeneration_);
                        }
                    }
                    return true;
                }
            }
        } else {
            if (!functionValue.equals(grid.getBlockFunctions()[index].getAddress())) {
                if (!isDisableHistory_ && !isBlockChanging_) {
                    history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                    history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                    history_.getOriginal().addBlock(backupBlock(grid));
                }

                grid.getBlockFunctions()[index].setAddress(functionValue);
                grid.getBlockFunctions()[index].setValue(LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_VALUE);
                grid.getBlockFunctions()[index].setRadix(LadderGrid.LADDER_GRID_INITIAL_BLOCK_FUNCTION_RADIX);
                grid.getBlockFunctions()[index].setNumber(false);
                gridPane.changeBlockFunction();

                if (isBlockChanging_) {
                    blockChangingGridPane_.add(gridPane);
                } else {
                    gridPane.setEditing(true);
                }

                if (!isDisableHistory_ && !isBlockChanging_) {
                    history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                    history_.getRevised().addBlock(backupBlock(grid));
                    if (checkDelta()) {
                        historyManager_.push(history_, historyGeneration_);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @param grid
     * @param gridPane
     * @param script
     * @return
     */
    public boolean blockChangeScript(Ladder ladder, LadderGrid grid, LadderGridPane gridPane, String script) {
        if (!script.equals(grid.getBlockScript())) {
            if (!isDisableHistory_ && !isBlockChanging_) {
                history_ = new LadderHistory(Ladders.LADDER_COMMAND.BLOCK_CHANGE);
                history_.setOriginal(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getOriginal().addBlock(backupBlock(grid));
            }

            grid.setBlockScript(script);
            gridPane.changeBlockScript();

            if (isBlockChanging_) {
                blockChangingGridPane_.add(gridPane);
            } else {
                gridPane.setEditing(true);
            }

            if (!isDisableHistory_ && !isBlockChanging_) {
                history_.setRevised(new LadderJsonLadder(ladder.getIdx(), grid.getColumnIndex(), grid.getRowIndex()));
                history_.getRevised().addBlock(backupBlock(grid));
                if (checkDelta()) {
                    historyManager_.push(history_, historyGeneration_);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladderJson
     * @param tabPane
     */
    public void backupLadders(LadderJson ladderJson, TabPane tabPane) {
        Tab tab;
        LadderPane pane;
        int index, size;

        for (index = 0, size = tabPane.getTabs().size(); index < size; index++) {
            tab = tabPane.getTabs().get(index);
            pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
            ladderJson.addLadder(backupLadder(pane));
        }
        ladderJson.sortLadders();
    }

    /**
     *
     * @param pane
     * @return
     */
    public LadderJsonLadder backupLadder(LadderPane pane) {
        Ladder ladder = pane.getLadder();
        LadderJsonLadder jsonLadder = new LadderJsonLadder(ladder.getIdx(), ladder.getName(), ladder.getColumn(), ladder.getRow());
        backupBlocks(jsonLadder, pane);
        return jsonLadder;
    }

    /**
     *
     * @param jsonLadder
     * @param pane
     */
    public void backupBlocks(LadderJsonLadder jsonLadder, LadderPane pane) {
        LadderGrid grid;
        LadderGridPane ladderGridPane;

        for (Node node : pane.getChildren()) {
            ladderGridPane = (LadderGridPane) node;
            grid = ladderGridPane.getLadderGrid();
            jsonLadder.addBlock(backupBlock(grid));
        }
        if (jsonLadder.getBlocks() != null) {
            jsonLadder.sortBlocks(jsonLadder.getColumn(), jsonLadder.getRow());
        }
    }

    /**
     *
     * @param jsonLadder
     * @param pane
     * @param rowIndex
     */
    public void backupBlocksRow(LadderJsonLadder jsonLadder, LadderPane pane, int rowIndex) {
        LadderGrid grid;
        LadderGridPane ladderGridPane;

        for (Node node : pane.getChildren()) {
            ladderGridPane = (LadderGridPane) node;
            grid = ladderGridPane.getLadderGrid();
            if (grid.getRowIndex() == rowIndex) {
                jsonLadder.addBlock(backupBlock(grid));
            }
        }
    }

    /**
     *
     * @param grid
     * @return
     */
    public LadderJsonBlock backupBlock(LadderGrid grid) {
        switch (grid.getBlock()) {
            case EMPTY:
                if (grid.isVertical() || grid.isVerticalOr()) {
                    return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                            grid.getBlock().toString(),
                            grid.isVertical(), grid.isVerticalOr(),
                            null,
                            null,
                            null,
                            null);
                }
                break;
            case BLOCK_COMMENT:
                if (grid.getComment() != null) {
                    return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                            grid.getBlock().toString(),
                            grid.isVertical(), grid.isVerticalOr(),
                            null,
                            grid.getComment(),
                            null,
                            null);
                }
                break;
            case CONNECT_LINE:
                return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                        grid.getBlock().toString(),
                        grid.isVertical(), grid.isVerticalOr(),
                        null,
                        null,
                        null,
                        null);
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
            case SET:
            case RESET:
            case RANDOM:
                return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                        grid.getBlock().toString(),
                        grid.isVertical(), grid.isVerticalOr(),
                        grid.getAddress(),
                        null,
                        null,
                        null);
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
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
            case TIMER:
            case TIMER_NOT:
            case COUNTER:
            case COUNTER_NOT:
            case MOVE:
                return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                        grid.getBlock().toString(),
                        grid.isVertical(), grid.isVerticalOr(),
                        grid.getAddress(),
                        null,
                        Arrays.asList(grid.getBlockFunctions()),
                        null);
            case SCRIPT:
                return new LadderJsonBlock(grid.getColumnIndex(), grid.getRowIndex(),
                        grid.getBlock().toString(),
                        grid.isVertical(), grid.isVerticalOr(),
                        grid.getAddress(),
                        null,
                        null,
                        grid.getBlockScript());
        }
        return null;
    }

    /**
     *
     * @param ladderJson
     * @param ioMap
     * @param commentMap
     */
    public void backupComments(LadderJson ladderJson, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap) {
        for (int idx = 0; idx < commentMap.size(); idx++) {
            for (Map.Entry<String, String> entry : commentMap.get(idx).entrySet()) {
                ladderJson.addComment(new LadderJsonComment(idx, entry.getKey(), entry.getValue(), ioMap.get(idx).get(entry.getKey()).getValue()));
            }
        }
        ladderJson.sortComments();
    }

    /**
     *
     * @param ladderJson
     * @param isEditing
     * @return
     */
    public boolean restoreLadders(LadderJson ladderJson, boolean isEditing) {
        return restoreLadders(ladders_, ladders_.getTabLadder(), ladders_.getTreeTableIo(), ladders_.getIoMap(), ladders_.getCommentMap(), ladders_.getScriptIoMap(), ladderJson, isEditing);
    }

    /**
     *
     * @param ladders
     * @param tabPane
     * @param treeTableView
     * @param ioMap
     * @param commentMap
     * @param scriptIoMap
     * @param ladderJson
     * @param isEditing
     * @return
     */
    public boolean restoreLadders(Ladders ladders, TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> scriptIoMap, LadderJson ladderJson, boolean isEditing) {
        if (ladderJson.getLadders() != null) {
            LadderJsonLadder jsonLadder;
            int index, size;

            for (index = 0, size = ladderJson.getLadders().size(); index < size; index++) {
                jsonLadder = ladderJson.getLadders().get(index);

                if (!restoreLadder(ladders, tabPane, treeTableView, ioMap, commentMap, scriptIoMap, jsonLadder, isEditing)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param ladders
     * @param tabPane
     * @param treeTableView
     * @param ioMap
     * @param jsonLadder
     * @param commentMap
     * @param scriptIoMap
     * @param isEditing
     * @return
     */
    public boolean restoreLadder(Ladders ladders, TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> scriptIoMap, LadderJsonLadder jsonLadder, boolean isEditing) {
        if ((jsonLadder.getIdx() == null) || (jsonLadder.getName() == null) || (jsonLadder.getColumn() == null) || (jsonLadder.getRow() == null)) {
            writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [idx][name][column][row]", true);
            return false;
        }
        return restoreBlocks(jsonLadder, ladderCreate(ladders, tabPane, treeTableView, ioMap, commentMap, scriptIoMap, jsonLadder.getIdx(), jsonLadder.getName(), jsonLadder.getColumn(), jsonLadder.getRow(), Ladders.LADDER_DEFAULT_GRID_MIN_SIZE, Ladders.LADDER_DEFAULT_GRID_MAX_SIZE, Ladders.LADDER_DEFAULT_GRID_CONTENTS_WIDTH, Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT), isEditing);
    }

    /**
     *
     * @param jsonLadder
     * @param pane
     * @param isEditing
     * @return
     */
    public boolean restoreBlocks(LadderJsonLadder jsonLadder, LadderPane pane, boolean isEditing) {
        if (jsonLadder.getBlocks() != null) {
            LadderJsonBlock jsonBlock;
            int index, size;

            for (index = 0, size = jsonLadder.getBlocks().size(); index < size; index++) {
                jsonBlock = jsonLadder.getBlocks().get(index);
                if (!restoreBlock(jsonBlock, pane, isEditing, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param jsonLadderOriginal
     * @param jsonLadderRevised
     * @param pane
     * @param isEditing
     * @param isVerticalCheck
     * @return
     */
    public boolean restoreBlocks(LadderJsonLadder jsonLadderOriginal, LadderJsonLadder jsonLadderRevised, LadderPane pane, boolean isEditing, boolean isVerticalCheck) {
        if ((jsonLadderOriginal != null) && (jsonLadderRevised != null)) {
            if ((jsonLadderOriginal.getBlocks() != null) && (jsonLadderRevised.getBlocks() != null)) {
                LadderJsonBlock jsonBlock;
                int index, size;

                for (index = 0, size = jsonLadderRevised.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderRevised.getBlocks().get(index);
                    if (!restoreBlock(
                            new LadderJsonBlock(jsonBlock.getColumnIndex(), jsonBlock.getRowIndex()),
                            pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }

                for (index = 0, size = jsonLadderOriginal.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderOriginal.getBlocks().get(index);
                    if (!restoreBlock(jsonBlock, pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }
            } else if (jsonLadderOriginal.getBlocks() != null) {
                LadderJsonBlock jsonBlock;
                int index, size;

                for (index = 0, size = jsonLadderOriginal.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderOriginal.getBlocks().get(index);
                    if (!restoreBlock(jsonBlock, pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }
            } else if (jsonLadderRevised.getBlocks() != null) {
                LadderJsonBlock jsonBlock;
                int index, size;

                for (index = 0, size = jsonLadderRevised.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderRevised.getBlocks().get(index);
                    if (!restoreBlock(
                            new LadderJsonBlock(jsonBlock.getColumnIndex(), jsonBlock.getRowIndex()),
                            pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }
            }
        } else if (jsonLadderOriginal != null) {
            if (jsonLadderOriginal.getBlocks() != null) {
                LadderJsonBlock jsonBlock;
                int index, size;

                for (index = 0, size = jsonLadderOriginal.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderOriginal.getBlocks().get(index);
                    if (!restoreBlock(jsonBlock, pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }
            }
        } else if (jsonLadderRevised != null) {
            if (jsonLadderRevised.getBlocks() != null) {
                LadderJsonBlock jsonBlock;
                int index, size;

                for (index = 0, size = jsonLadderRevised.getBlocks().size(); index < size; index++) {
                    jsonBlock = jsonLadderRevised.getBlocks().get(index);
                    if (!restoreBlock(
                            new LadderJsonBlock(jsonBlock.getColumnIndex(), jsonBlock.getRowIndex()),
                            pane, isEditing, isVerticalCheck)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     *
     * @param pane
     * @param jsonBlock
     * @param isEditing
     * @param isVerticalCheck
     * @return
     */
    public boolean restoreBlock(LadderJsonBlock jsonBlock, LadderPane pane, boolean isEditing, boolean isVerticalCheck) {
        LadderGrid grid;
        LadderGridPane gridPane;
        int i;

        if ((jsonBlock.getColumnIndex() == null) || (jsonBlock.getRowIndex() == null)) {
            writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [columnIndex][rowIndex]", true);
            return false;
        }
        grid = pane.getLadder().findGrid(jsonBlock.getColumnIndex(), jsonBlock.getRowIndex());
        gridPane = pane.findGridPane(grid);

        if (jsonBlock.getBlock() == null) {
            writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [block]", true);
            return false;
        }
        blockChangeBlock(pane.getLadder(), grid, gridPane, Ladders.LADDER_BLOCK.valueOf(jsonBlock.getBlock()));

        if (jsonBlock.isVertical() == null) {
            writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [vertical]", true);
            return false;
        }
        blockChangeVertical(pane.getLadder(), grid, gridPane, jsonBlock.isVertical());
        if (isVerticalCheck) {
            if (grid.getUpLadderGrid() != null) {
                grid.getUpLadderGrid().setVerticalOr(jsonBlock.isVertical());
                pane.findGridPane(grid.getUpLadderGrid()).changeStrokeVerticalOr();
            }
        }

        if (jsonBlock.isVerticalOr() == null) {
            writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [verticalOr]", true);
            return false;
        }
        blockChangeVerticalOr(pane.getLadder(), grid, gridPane, jsonBlock.isVerticalOr());
        if (isVerticalCheck) {
            if (grid.getDownLadderGrid() != null) {
                grid.getDownLadderGrid().setVertical(jsonBlock.isVerticalOr());
                pane.findGridPane(grid.getDownLadderGrid()).changeStrokeVertical();
            }
        }

        switch (grid.getBlock()) {
            case EMPTY:
            case CONNECT_LINE:
                grid.setAddress(LadderGrid.LADDER_GRID_INITIAL_ADDRESS);
                grid.setBlockValue(LadderGrid.LADDER_GRID_INITIAL_BLOCK_VALUE);
                for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                    grid.getBlockFunctions()[i].clear();
                }
                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
                break;
            case BLOCK_COMMENT:
                if (jsonBlock.getComment() == null) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [comment]", true);
                    return false;
                }
                blockChangeComment(pane.getLadder(), grid, gridPane, jsonBlock.getComment());

                grid.setAddress(LadderGrid.LADDER_GRID_INITIAL_ADDRESS);
                grid.setBlockValue(LadderGrid.LADDER_GRID_INITIAL_BLOCK_VALUE);
                for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                    grid.getBlockFunctions()[i].clear();
                }
                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
                break;
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
            case SET:
            case RESET:
            case RANDOM:
                if (jsonBlock.getAddress() == null) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [address]", true);
                    return false;
                }
                blockChangeAddress(pane.getLadder(), grid, gridPane, jsonBlock.getAddress());

                for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                    grid.getBlockFunctions()[i].clear();
                }
                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
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
            case TIMER:
            case TIMER_NOT:
            case COUNTER:
            case COUNTER_NOT:
            case MOVE:
                if (jsonBlock.getAddress() == null) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [address]", true);
                    return false;
                }
                blockChangeAddress(pane.getLadder(), grid, gridPane, jsonBlock.getAddress());

                if (!blockChangeFunction(pane.getLadder(), grid, gridPane, jsonBlock.getBlockFunctions())) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [blockFunctionAddress][blockFunctionValue][blockFunctionValueRadix]", true);
                    return false;
                }

                grid.setBlockScript(LadderGrid.LADDER_GRID_INITIAL_BLOCK_SCRIPT);
                break;
            case SCRIPT:
                if (jsonBlock.getAddress() == null) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [address]", true);
                    return false;
                }
                blockChangeAddress(pane.getLadder(), grid, gridPane, jsonBlock.getAddress());

                for (i = 0; i < LadderGrid.LADDER_BLOCK_FUNCTIONS; i++) {
                    grid.getBlockFunctions()[i].clear();
                }

                if (jsonBlock.getBlockScript() == null) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [blockScript]", true);
                    return false;
                }
                blockChangeScript(pane.getLadder(), grid, gridPane, jsonBlock.getBlockScript());
                break;
        }
        if (isEditing) {
            gridPane.setEditing(true);
        }
        return true;
    }

    /**
     *
     * @param ladderJson
     * @return
     */
    public boolean restoreComments(LadderJson ladderJson) {
        return restoreComments(ladders_.getTabLadder(), ladders_.getTreeTableIo(), ladders_.getIoMap(), ladders_.getCommentMap(), ladderJson);
    }

    /**
     *
     * @param tabPane
     * @param treeTableView
     * @param ioMap
     * @param commentMap
     * @param ladderJson
     * @return
     */
    public boolean restoreComments(TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, LadderJson ladderJson) {
        if (ladderJson.getComments() != null) {
            LadderJsonComment jsonComment;
            int index, size;

            for (index = 0, size = ladderJson.getComments().size(); index < size; index++) {
                jsonComment = ladderJson.getComments().get(index);

                if ((jsonComment.getIdx() == null) || (jsonComment.getAddress() == null) || (jsonComment.getComment() == null) || (jsonComment.getValue() == null)) {
                    writeLog(LadderEnums.DATA_FORMAT_UNEXPECTED_ERROR.toString() + " [idx][address][comment][value]", true);
                    return false;
                }
                changeComment(tabPane, treeTableView, ioMap, commentMap, jsonComment.getIdx(), jsonComment.getAddress(), jsonComment.getComment());
                ladders_.setValue(jsonComment.getIdx(), jsonComment.getAddress(), jsonComment.getValue());
            }
        }
        return true;
    }

    private boolean checkDelta() {
        if ((history_.getOriginal() != null) || (history_.getRevised() != null)) {
            if ((history_.getOriginal() != null) && (history_.getRevised() != null)) {
                return !gson_.toJson(history_.getOriginal()).equals(gson_.toJson(history_.getRevised()));
            }
            return true;
        }
        return false;
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(LadderEnums.LADDER.toString(), msg, err);
    }
}
