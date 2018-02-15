/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import com.google.gson.Gson;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author mizoguch-ken
 */
public class DesignLaddersDifferenceController implements Initializable {

    @FXML
    private HBox paneRoot;
    // main
    @FXML
    private VBox vboxIoLadderOriginal;
    @FXML
    private Label labelIoLadderOriginal;
    @FXML
    private SplitPane splitIoLadderOriginal;
    @FXML
    private TableView<LadderTableIo> tableIoOriginal;
    @FXML
    private TableColumn<LadderTableIo, String> tableIoAddressOriginal;
    @FXML
    private TableColumn<LadderTableIo, String> tableIoCommentOriginal;
    @FXML
    private TabPane tabLadderOriginal;
    @FXML
    private VBox vboxIoLadderRevised;
    @FXML
    private Label labelIoLadderRevised;
    @FXML
    private SplitPane splitIoLadderRevised;
    @FXML
    private TableView<LadderTableIo> tableIoRevised;
    @FXML
    private TableColumn<LadderTableIo, String> tableIoAddressRevised;
    @FXML
    private TableColumn<LadderTableIo, String> tableIoCommentRevised;
    @FXML
    private TabPane tabLadderRevised;

    private class Deltab {

        Tab tabOriginal;
        int indexOriginal;

        Tab tabRevised;
        int indexRevised;

        private Deltab() {
            tabOriginal = null;
            indexOriginal = -1;

            tabRevised = null;
            indexRevised = -1;
        }
    }

    private Stage stage_;
    private Ladders ladders_;
    private final Map<String, Deltab> deltabs_ = new HashMap<>();

    private void addEventDesign() {
        // root
        stage_.setOnShown((WindowEvent event) -> {
            splitIoLadderOriginal.setDividerPositions(0.382);
            splitIoLadderRevised.setDividerPositions(0.382);
        });

        // split
        splitIoLadderOriginal.getDividers().get(0).positionProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                splitIoLadderRevised.setDividerPositions(newValue.doubleValue());
            }
        });
        splitIoLadderRevised.getDividers().get(0).positionProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                splitIoLadderOriginal.setDividerPositions(newValue.doubleValue());
            }
        });

        // io
        tableIoAddressOriginal.setCellValueFactory((TableColumn.CellDataFeatures<LadderTableIo, String> param) -> {
            return param.getValue().addressProperty();
        });
        tableIoCommentOriginal.setCellValueFactory((TableColumn.CellDataFeatures<LadderTableIo, String> param) -> {
            return param.getValue().commentProperty();
        });
        tableIoAddressRevised.setCellValueFactory((TableColumn.CellDataFeatures<LadderTableIo, String> param) -> {
            return param.getValue().addressProperty();
        });
        tableIoCommentRevised.setCellValueFactory((TableColumn.CellDataFeatures<LadderTableIo, String> param) -> {
            return param.getValue().commentProperty();
        });

        // tabpane
        tabLadderOriginal.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                String name = tabLadderOriginal.getTabs().get(newValue.intValue()).getText();
                if (deltabs_.containsKey(name)) {
                    if (deltabs_.get(name).tabRevised != null) {
                        tabLadderRevised.getSelectionModel().select(deltabs_.get(name).indexRevised);
                    }
                }
            }
        });
        tabLadderRevised.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                String name = tabLadderRevised.getTabs().get(newValue.intValue()).getText();
                if (deltabs_.containsKey(name)) {
                    if (deltabs_.get(name).tabOriginal != null) {
                        tabLadderOriginal.getSelectionModel().select(deltabs_.get(name).indexOriginal);
                    }
                }
            }
        });
    }

    private void runInitDesign() {
        if (Platform.isFxApplicationThread()) {
            initDesign();
        } else {
            Platform.runLater(() -> {
                initDesign();
            });
        }
    }

    private void initDesign() {
        // io
        labelIoLadderOriginal.setText(LadderEnums.IO_REFERENCE.toString());
        tableIoAddressOriginal.setText(LadderEnums.IO_ADDRESS.toString());
        tableIoCommentOriginal.setText(LadderEnums.IO_COMMENT.toString());
        labelIoLadderRevised.setText(LadderEnums.IO_CURRENT.toString());
        tableIoAddressRevised.setText(LadderEnums.IO_ADDRESS.toString());
        tableIoCommentRevised.setText(LadderEnums.IO_COMMENT.toString());
    }

    /**
     *
     * @param ladderJsonOriginal
     * @param pathOriginal
     * @param ladderJsonRevised
     * @param pathRevised
     */
    public void diffarence(LadderJson ladderJsonOriginal, Path pathOriginal, LadderJson ladderJsonRevised, Path pathRevised) {
        if ((ladderJsonOriginal.getLadders() != null) && (ladderJsonRevised.getLadders() != null)) {
            int columnIndex, columnSize, rowIndex, rowSize;
            int index, size, index2, size2;

            // original ladder
            labelIoLadderOriginal.setText(LadderEnums.IO_REFERENCE.toString() + "(" + pathOriginal.toString() + ")");
            ConcurrentHashMap<String, LadderIo> ioMapOriginal = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, String> commentMapOriginal = new ConcurrentHashMap<>();
            List<String> jsonOriginal = new ArrayList<>();

            // revised ladder
            if (pathRevised == null) {
                labelIoLadderRevised.setText(LadderEnums.IO_CURRENT.toString());
            } else {
                labelIoLadderRevised.setText(LadderEnums.IO_CURRENT.toString() + "(" + pathRevised.toString() + ")");
            }
            ConcurrentHashMap<String, LadderIo> ioMapRevised = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, String> commentMapRevised = new ConcurrentHashMap<>();
            List<String> jsonRevised = new ArrayList<>();

            // connect
            ladders_.ladderJsonLoad(null, tabLadderOriginal, tableIoOriginal, ioMapOriginal, commentMapOriginal, ladderJsonOriginal);
            ladders_.checkConnectLadder(tabLadderOriginal, tableIoOriginal, ioMapOriginal);
            ladders_.ladderJsonLoad(null, tabLadderRevised, tableIoRevised, ioMapRevised, commentMapRevised, ladderJsonRevised);
            ladders_.checkConnectLadder(tabLadderRevised, tableIoRevised, ioMapRevised);

            // diff
            Chunk<String> chunk;
            List<LadderJsonBlock> blocks;
            LadderJsonBlock block;
            LadderGridPane gridPane;
            LadderGrid grid, gridNew, gridBuf;
            Patch<String> patch;
            Delta<String> delta;
            Tab tab;
            Gson gson = new Gson();

            for (index = 0, size = tabLadderOriginal.getTabs().size(); index < size; index++) {
                tab = tabLadderOriginal.getTabs().get(index);
                if (!deltabs_.containsKey(tab.getText())) {
                    deltabs_.put(tab.getText(), new Deltab());
                }
                deltabs_.get(tab.getText()).tabOriginal = tab;
                deltabs_.get(tab.getText()).indexOriginal = index;
            }

            for (index = 0, size = tabLadderRevised.getTabs().size(); index < size; index++) {
                tab = tabLadderRevised.getTabs().get(index);
                if (!deltabs_.containsKey(tab.getText())) {
                    deltabs_.put(tab.getText(), new Deltab());
                }
                deltabs_.get(tab.getText()).tabRevised = tab;
                deltabs_.get(tab.getText()).indexRevised = index;
            }

            for (Map.Entry<String, Deltab> entry : deltabs_.entrySet()) {
                if ((entry.getValue().tabOriginal != null) && (entry.getValue().tabRevised != null)) {
                    ScrollPane scrollPaneOriginal = (ScrollPane) entry.getValue().tabOriginal.getContent();
                    LadderPane paneOriginal = (LadderPane) scrollPaneOriginal.getContent();
                    ScrollPane scrollPaneRevised = (ScrollPane) entry.getValue().tabRevised.getContent();
                    LadderPane paneRevised = (LadderPane) scrollPaneRevised.getContent();

                    // scroll synchronization
                    scrollPaneOriginal.vvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        if (newValue != null) {
                            scrollPaneRevised.setVvalue(newValue.doubleValue());
                        }
                    });
                    scrollPaneOriginal.hvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        if (newValue != null) {
                            scrollPaneRevised.setHvalue(newValue.doubleValue());
                        }
                    });
                    scrollPaneRevised.vvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        if (newValue != null) {
                            scrollPaneOriginal.setVvalue(newValue.doubleValue());
                        }
                    });
                    scrollPaneRevised.hvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                        if (newValue != null) {
                            scrollPaneOriginal.setHvalue(newValue.doubleValue());
                        }
                    });

                    // to match grid size
                    if (ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getRow() < ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getRow()) {
                        for (rowIndex = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getRow(), rowSize = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getRow(); rowIndex < rowSize; rowIndex++) {
                            grid = paneOriginal.findGridPane(0, ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getRow() - 1).getLadderGrid();
                            gridBuf = null;
                            for (columnIndex = 0, columnSize = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getColumn(); columnIndex < columnSize; columnIndex++) {
                                gridPane = paneOriginal.addGrid(columnIndex, rowIndex);
                                gridNew = gridPane.getLadderGrid();
                                gridNew.setLeftLadderGrid(gridBuf);
                                gridNew.setUpLadderGrid(grid.getUpLadderGrid());
                                if (gridBuf != null) {
                                    gridBuf.setRightLadderGrid(gridNew);
                                }
                                gridNew.setDownLadderGrid(grid);
                                if (columnIndex == 0) {
                                    grid.setBlock(Ladders.LADDER_BLOCK.CONTENTS);
                                    gridPane.changeBlock();
                                } else {
                                    gridPane.changeDifference(Delta.TYPE.DELETE);
                                }
                                gridBuf = gridNew;
                                if (grid.getUpLadderGrid() != null) {
                                    grid.getUpLadderGrid().setDownLadderGrid(gridNew);
                                }
                                grid.setUpLadderGrid(gridNew);
                                grid = grid.getRightLadderGrid();
                            }
                        }
                    } else if (ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getRow() > ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getRow()) {
                        for (rowIndex = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getRow(), rowSize = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getRow(); rowIndex < rowSize; rowIndex++) {
                            grid = paneRevised.findGridPane(0, ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getRow() - 1).getLadderGrid();
                            gridBuf = null;
                            for (columnIndex = 0, columnSize = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getColumn(); columnIndex < columnSize; columnIndex++) {
                                gridPane = paneRevised.addGrid(columnIndex, rowIndex);
                                gridNew = gridPane.getLadderGrid();
                                gridNew.setLeftLadderGrid(gridBuf);
                                gridNew.setUpLadderGrid(grid.getUpLadderGrid());
                                if (gridBuf != null) {
                                    gridBuf.setRightLadderGrid(gridNew);
                                }
                                gridNew.setDownLadderGrid(grid);
                                if (columnIndex == 0) {
                                    grid.setBlock(Ladders.LADDER_BLOCK.CONTENTS);
                                    gridPane.changeBlock();
                                } else {
                                    gridPane.changeDifference(Delta.TYPE.DELETE);
                                }
                                gridBuf = gridNew;
                                if (grid.getUpLadderGrid() != null) {
                                    grid.getUpLadderGrid().setDownLadderGrid(gridNew);
                                }
                                grid.setUpLadderGrid(gridNew);
                                grid = grid.getRightLadderGrid();
                            }
                        }
                    }

                    // original block list
                    jsonOriginal.clear();
                    blocks = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getBlocks();
                    if (blocks != null) {
                        for (index = 0, size = blocks.size(); index < size; index++) {
                            jsonOriginal.add(gson.toJson(blocks.get(index)));
                        }
                    }

                    // revised block list
                    jsonRevised.clear();
                    blocks = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getBlocks();
                    if (blocks != null) {
                        for (index = 0, size = blocks.size(); index < size; index++) {
                            jsonRevised.add(gson.toJson(blocks.get(index)));
                        }
                    }

                    // diff patch
                    patch = DiffUtils.diff(jsonOriginal, jsonRevised);
                    for (index = 0, size = patch.getDeltas().size(); index < size; index++) {
                        delta = patch.getDeltas().get(index);
                        switch (delta.getType()) {
                            case CHANGE:
                                chunk = delta.getOriginal();
                                blocks = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getBlocks();
                                if (blocks != null) {
                                    for (index2 = 0, size2 = chunk.getLines().size(); index2 < size2; index2++) {
                                        block = blocks.get(chunk.getPosition() + index2);
                                        paneOriginal.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.CHANGE);
                                    }
                                }
                                chunk = delta.getRevised();
                                blocks = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getBlocks();
                                if (blocks != null) {
                                    for (index2 = 0, size2 = chunk.getLines().size(); index2 < size2; index2++) {
                                        block = blocks.get(chunk.getPosition() + index2);
                                        paneRevised.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.CHANGE);
                                    }
                                }
                                break;
                            case DELETE:
                                chunk = delta.getOriginal();
                                blocks = ladderJsonOriginal.getLadders().get(entry.getValue().indexOriginal).getBlocks();
                                if (blocks != null) {
                                    for (index2 = 0, size2 = chunk.getLines().size(); index2 < size2; index2++) {
                                        block = blocks.get(chunk.getPosition() + index2);
                                        paneOriginal.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.DELETE);
                                        paneRevised.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.INSERT);
                                    }
                                }
                                break;
                            case INSERT:
                                chunk = delta.getRevised();
                                blocks = ladderJsonRevised.getLadders().get(entry.getValue().indexRevised).getBlocks();
                                if (blocks != null) {
                                    for (index2 = 0, size2 = chunk.getLines().size(); index2 < size2; index2++) {
                                        block = blocks.get(chunk.getPosition() + index2);
                                        paneRevised.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.INSERT);
                                        paneOriginal.findGridPane(block.getColumnIndex(), block.getRowIndex()).changeDifference(Delta.TYPE.DELETE);
                                    }
                                }
                                break;
                        }
                    }
                }
            }

            // initial select
            String name = tabLadderRevised.getTabs().get(0).getText();
            if (deltabs_.containsKey(name)) {
                if (deltabs_.get(name).tabOriginal != null) {
                    tabLadderOriginal.getSelectionModel().select(deltabs_.get(name).indexOriginal);
                }
            }
        }
    }

    /**
     *
     * @param stage
     * @param ladders
     */
    public void startUp(Stage stage, Ladders ladders) {
        stage_ = stage;
        ladders_ = ladders;

        // design
        addEventDesign();
        runInitDesign();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
