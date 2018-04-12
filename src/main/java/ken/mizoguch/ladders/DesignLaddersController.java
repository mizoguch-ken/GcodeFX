/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.DesignEnums;

/**
 *
 * @author mizoguch-ken
 */
public class DesignLaddersController implements Initializable {

    @FXML
    private VBox paneRoot;
    // menu
    @FXML
    private MenuBar menuBar;
    // menu file
    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem menuFileNew;
    @FXML
    private MenuItem menuFileOpen;
    @FXML
    private MenuItem menuFileSave;
    @FXML
    private MenuItem menuFileSaveAs;
    @FXML
    private MenuItem menuFileDifference;
    @FXML
    private MenuItem menuFileRecentFile1;
    @FXML
    private MenuItem menuFileRecentFile2;
    @FXML
    private MenuItem menuFileRecentFile3;
    @FXML
    private MenuItem menuFileRecentFile4;
    @FXML
    private MenuItem menuFileRecentFile5;
    @FXML
    private MenuItem menuFileRecentFile6;
    @FXML
    private MenuItem menuFileRecentFile7;
    @FXML
    private MenuItem menuFileRecentFile8;
    @FXML
    private MenuItem menuFileRecentFile9;
    @FXML
    private MenuItem menuFileRecentFile10;
    // edit
    @FXML
    private Menu menuEdit;
    @FXML
    private MenuItem menuEditUndo;
    @FXML
    private MenuItem menuEditRedo;
    @FXML
    private MenuItem menuEditCut;
    @FXML
    private MenuItem menuEditCopy;
    @FXML
    private MenuItem menuEditPaste;
    @FXML
    private MenuItem menuEditSelectLeft;
    @FXML
    private MenuItem menuEditSelectUp;
    @FXML
    private MenuItem menuEditSelectRight;
    @FXML
    private MenuItem menuEditSelectDown;
    @FXML
    private MenuItem menuEditTabNew;
    @FXML
    private MenuItem menuEditTabClose;
    @FXML
    private MenuItem menuEditTabRename;
    @FXML
    private MenuItem menuEditTabMoveLeft;
    @FXML
    private MenuItem menuEditTabMoveRight;
    // view
    @FXML
    private Menu menuView;
    @FXML
    private MenuItem menuViewTabSelectNext;
    @FXML
    private MenuItem menuViewTabSelectPrevious;
    // menu tools
    @FXML
    private Menu menuTools;
    @FXML
    private MenuItem menuToolsConnect;
    @FXML
    private MenuItem menuToolsRun;
    @FXML
    private MenuItem menuToolsStop;
    // main
    @FXML
    private SplitPane splitIoLadder;
    @FXML
    private TreeTableView<LadderTreeTableIo> treeTableIo;
    @FXML
    private TreeTableColumn<LadderTreeTableIo, String> treeTableIoAddress;
    @FXML
    private TreeTableColumn<LadderTreeTableIo, String> treeTableIoComment;
    @FXML
    private TreeTableColumn<LadderTreeTableIo, Double> treeTableIoValue;
    @FXML
    private TabPane tabLadder;
    // status
    @FXML
    private HBox statusBar;
    @FXML
    private Label lblCycleTime;
    @FXML
    private Label lblMinCycleTime;
    @FXML
    private Label lblMaxCycleTime;
    @FXML
    private Label lblIdealCycleTime;

    /**
     * stage settings
     */
    public class StageSettings {

        private boolean stageMaximized;
        private double stageWidth, stageHeight, rootWidth, rootHeight, ioLadderDividerPositions;

        /**
         *
         * @return
         */
        public boolean isStageMaximized() {
            return stageMaximized;
        }

        /**
         *
         * @param bln
         * @param update
         */
        public void setStageMaximized(boolean bln, boolean update) {
            if (update || isShowing_) {
                stageMaximized = bln;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        stage_.setMaximized(stageMaximized);
                    } else {
                        Platform.runLater(() -> {
                            stage_.setMaximized(stageMaximized);
                        });
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public double getStageWidth() {
            return stageWidth;
        }

        /**
         *
         * @param value
         * @param update
         */
        public void setStageWidth(double value, boolean update) {
            if (update || isShowing_) {
                stageWidth = value;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        if (!stageMaximized) {
                            stage_.setWidth(stageWidth);
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!stageMaximized) {
                                stage_.setWidth(stageWidth);
                            }
                        });
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public double getStageHeight() {
            return stageHeight;
        }

        /**
         *
         * @param value
         * @param update
         */
        public void setStageHeight(double value, boolean update) {
            if (update || isShowing_) {
                stageHeight = value;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        if (!stageMaximized) {
                            stage_.setHeight(stageHeight);
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!stageMaximized) {
                                stage_.setHeight(stageHeight);
                            }
                        });
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public double getRootWidth() {
            return rootWidth;
        }

        /**
         *
         * @param value
         * @param update
         */
        public void setRootWidth(double value, boolean update) {
            if (update || isShowing_) {
                rootWidth = value;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        if (!stageMaximized) {
                            paneRoot.setPrefWidth(rootWidth);
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!stageMaximized) {
                                paneRoot.setPrefWidth(rootWidth);
                            }
                        });
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public double getRootHeight() {
            return rootHeight;
        }

        /**
         *
         * @param value
         * @param update
         */
        public void setRootHeight(double value, boolean update) {
            if (update || isShowing_) {
                rootHeight = value;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        if (!stageMaximized) {
                            paneRoot.setPrefHeight(rootHeight);
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!stageMaximized) {
                                paneRoot.setPrefHeight(rootHeight);
                            }
                        });
                    }
                }
            }
        }

        /**
         *
         * @return
         */
        public double getIoLadderDividerPositions() {
            return ioLadderDividerPositions;
        }

        /**
         *
         * @param value
         * @param update
         */
        public void setIoLadderDividerPositions(double value, boolean update) {
            if (update || isShown_) {
                ioLadderDividerPositions = value;
                if (update) {
                    if (Platform.isFxApplicationThread()) {
                        splitIoLadder.getDividers().get(0).setPosition(ioLadderDividerPositions);
                    } else {
                        Platform.runLater(() -> {
                            splitIoLadder.getDividers().get(0).setPosition(ioLadderDividerPositions);
                        });
                    }
                }
            }
        }
    }

    private Stage stage_;
    private List<Image> icons_;
    private StageSettings stageSettings_;

    private Ladders ladders_;
    private int historyGeneration_;
    private long idealCycleTime_;
    private Path ladderPath_;

    private CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap_;
    private Tab selectedTab_;
    private ScrollPane selectedScrollPane_;
    private ObservableList<TreeItem<LadderTreeTableIo>> ovTreeTable_, ovTreeTableChild_;
    private LadderTreeTableIo ladderTreeTableIo_;
    private int treeTableIndex_, treeTableChildIndex_, treeTableSize_, treeTableChildSize_;
    private double treeTableValue_;

    private boolean viewRefresh_, isShowing_, isShown_;

    private void addEventDesign() {
        // root
        stage_.setOnShowing((WindowEvent event) -> {
            stageSettings_.setStageMaximized(stageSettings_.isStageMaximized(), true);
            stageSettings_.setStageWidth(stageSettings_.getStageWidth(), true);
            stageSettings_.setStageHeight(stageSettings_.getStageHeight(), true);
            stageSettings_.setRootWidth(stageSettings_.getRootWidth(), true);
            stageSettings_.setRootHeight(stageSettings_.getRootHeight(), true);
            isShowing_ = true;
        });
        stage_.setOnShown((WindowEvent event) -> {
            stageSettings_.setIoLadderDividerPositions(stageSettings_.getIoLadderDividerPositions(), true);
            isShown_ = true;
        });
        stage_.setOnHiding((WindowEvent event) -> {
            isShowing_ = false;
        });
        stage_.setOnHidden((WindowEvent event) -> {
            isShown_ = false;
        });
        stage_.maximizedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue != null) {
                stageSettings_.setStageMaximized(newValue, false);
                if (!newValue) {
                    stageSettings_.setStageWidth(stageSettings_.getStageWidth(), false);
                    stageSettings_.setStageHeight(stageSettings_.getStageHeight(), false);
                }
            }
        });
        stage_.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!stageSettings_.isStageMaximized()) {
                stageSettings_.setStageWidth(stage_.getWidth(), false);
                stageSettings_.setRootWidth(paneRoot.getWidth(), false);
            }
        });
        stage_.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!stageSettings_.isStageMaximized()) {
                stageSettings_.setStageHeight(stage_.getHeight(), false);
                stageSettings_.setRootHeight(paneRoot.getHeight(), false);
            }
        });
        splitIoLadder.getDividers().get(0).positionProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                stageSettings_.setIoLadderDividerPositions(newValue.doubleValue(), false);
            }
        });

        // menu file
        menuFileNew.setOnAction((ActionEvent event) -> {
            ladders_.fileNew();
        });
        menuFileOpen.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(null)) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileSave.setOnAction((ActionEvent event) -> {
            if (ladders_.fileSave()) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileSaveAs.setOnAction((ActionEvent event) -> {
            if (ladders_.fileSaveAs()) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileDifference.setOnAction((ActionEvent event) -> {
            // ladder
            try {
                FileChooser fileChooser = new FileChooser();

                if (ladders_.getFilePath() != null) {
                    if (ladders_.getFilePath().getParent() != null) {
                        if (Files.exists(ladders_.getFilePath().getParent())) {
                            fileChooser.setInitialDirectory(ladders_.getFilePath().getParent().toFile());
                        }
                    }
                }
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
                File fcfile = fileChooser.showOpenDialog(stage_);
                if (fcfile != null) {
                    Path file = fcfile.toPath();

                    Stage stage = new Stage(StageStyle.DECORATED);
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignLaddersDifference.fxml"));
                    Parent root = (Parent) loader.load();
                    DesignLaddersDifferenceController ladderDifferenceController = (DesignLaddersDifferenceController) loader.getController();
                    stage.setScene(new Scene(root));
                    if (!icons_.isEmpty()) {
                        stage.getIcons().addAll(icons_);
                    }
                    ladderDifferenceController.startUp(stage, ladders_);
                    ladderDifferenceController.diffarence(ladders_.ladderJsonOpen(file), file, ladders_.ladderJsonSave(tabLadder), ladders_.getFilePath());
                    stage.setTitle(LadderEnums.FILE_DIFFERENCE.toString());
                    stage.showAndWait();
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            }
        });
        menuFileRecentFile1.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile1.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile2.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile2.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile3.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile3.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile4.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile4.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile5.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile5.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile6.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile6.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile7.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile7.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile8.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile8.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile9.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile9.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });
        menuFileRecentFile10.setOnAction((ActionEvent event) -> {
            if (ladders_.fileOpen(Paths.get(menuFileRecentFile10.getText()))) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        });

        // menu edit
        menuEditUndo.setOnAction((ActionEvent event) -> {
            Tab tab = tabLadder.getSelectionModel().getSelectedItem();
            LadderPane pane = null;

            if (tab != null) {
                pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
            }
            undo(tab, pane);
        });
        menuEditRedo.setOnAction((ActionEvent event) -> {
            Tab tab = tabLadder.getSelectionModel().getSelectedItem();
            LadderPane pane = null;

            if (tab != null) {
                pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
            }
            redo(tab, pane);
        });
        menuEditCut.setOnAction((ActionEvent event) -> {
        });
        menuEditCopy.setOnAction((ActionEvent event) -> {
        });
        menuEditPaste.setOnAction((ActionEvent event) -> {
        });
        menuEditSelectLeft.setOnAction((ActionEvent event) -> {
        });
        menuEditSelectUp.setOnAction((ActionEvent event) -> {
        });
        menuEditSelectRight.setOnAction((ActionEvent event) -> {
        });
        menuEditSelectDown.setOnAction((ActionEvent event) -> {
        });
        menuEditTabNew.setOnAction((ActionEvent event) -> {
            tabNew();
        });
        menuEditTabClose.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabClose();
            }
        });
        menuEditTabRename.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabRename((LadderPane) ((ScrollPane) tabLadder.getSelectionModel().getSelectedItem().getContent()).getContent());
            }
        });
        menuEditTabMoveLeft.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabMoveLeft((LadderPane) ((ScrollPane) tabLadder.getSelectionModel().getSelectedItem().getContent()).getContent());
            }
        });
        menuEditTabMoveRight.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabMoveRight((LadderPane) ((ScrollPane) tabLadder.getSelectionModel().getSelectedItem().getContent()).getContent());
            }
        });

        // menu view
        menuViewTabSelectNext.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabSelectNext();
            }
        });
        menuViewTabSelectPrevious.setOnAction((ActionEvent event) -> {
            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                tabSelectPrevious();
            }
        });

        // menu tools
        menuToolsConnect.setOnAction((ActionEvent event) -> {
            ladders_.connectLadder();
        });
        menuToolsRun.setOnAction((ActionEvent event) -> {
            viewRefresh_ = true;
            ladders_.runStartLadder();
        });
        menuToolsStop.setOnAction((ActionEvent event) -> {
            viewRefresh_ = false;
            ladders_.stopLadder();
        });

        // io
        treeTableIoAddress.setCellFactory((TreeTableColumn<LadderTreeTableIo, String> param) -> {
            return new TextFieldTreeTableCell<>(new LadderAddressStringConverter());
        });
        treeTableIoAddress.setCellValueFactory((TreeTableColumn.CellDataFeatures<LadderTreeTableIo, String> param) -> {
            return param.getValue().getValue().addressProperty();
        });
        treeTableIoAddress.setOnEditCommit((TreeTableColumn.CellEditEvent<LadderTreeTableIo, String> event) -> {
            if ((event.getOldValue() != null) && (event.getNewValue() != null)) {
                if (!event.getOldValue().equals(event.getNewValue())) {
                    TreeItem<LadderTreeTableIo> parent = event.getRowValue().getParent();

                    if (parent == event.getTreeTableView().getRoot()) {
                        // tab name
                        int idx;
                        ObservableList<TreeItem<LadderTreeTableIo>> ovTreeTable = event.getTreeTableView().getRoot().getChildren();
                        for (idx = 0; idx < ovTreeTable.size(); idx++) {
                            if (ovTreeTable.get(idx) == event.getRowValue()) {
                                break;
                            }
                        }
                        if ((Ladders.LADDER_GLOBAL_ADDRESS_INDEX < idx) && (idx < ovTreeTable.size())) {
                            ladders_.ladderChangeTabName(idx, event.getNewValue());
                        }
                    } else {
                        // address
                        int idx;
                        ObservableList<TreeItem<LadderTreeTableIo>> ovTreeTable = event.getTreeTableView().getRoot().getChildren();
                        for (idx = 0; idx < ovTreeTable.size(); idx++) {
                            if (ovTreeTable.get(idx) == parent) {
                                break;
                            }
                        }
                        if (idx < ovTreeTable.size()) {
                            int oldIdx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
                            if (event.getOldValue().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                oldIdx = idx;
                            }

                            int newIdx = Ladders.LADDER_GLOBAL_ADDRESS_INDEX;
                            if (event.getNewValue().startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX)) {
                                newIdx = idx;
                            }

                            ladders_.changeAddress(oldIdx, event.getOldValue(), newIdx, event.getNewValue());
                        }
                    }
                }
            }
        });
        treeTableIoComment.setCellFactory((TreeTableColumn<LadderTreeTableIo, String> param) -> {
            return new TextFieldTreeTableCell<>(new LadderCommentStringConverter());
        });
        treeTableIoComment.setCellValueFactory((TreeTableColumn.CellDataFeatures<LadderTreeTableIo, String> param) -> {
            return param.getValue().getValue().commentProperty();
        });
        treeTableIoComment.setOnEditCommit((TreeTableColumn.CellEditEvent<LadderTreeTableIo, String> event) -> {
            if ((event.getOldValue() != null) && (event.getNewValue() != null)) {
                if (!event.getOldValue().equals(event.getNewValue())) {
                    TreeItem<LadderTreeTableIo> parent = event.getRowValue().getParent();

                    if (parent != event.getTreeTableView().getRoot()) {
                        // comment
                        int idx;
                        ObservableList<TreeItem<LadderTreeTableIo>> ovTreeTable = event.getTreeTableView().getRoot().getChildren();
                        for (idx = 0; idx < ovTreeTable.size(); idx++) {
                            if (ovTreeTable.get(idx) == parent) {
                                break;
                            }
                        }
                        if (idx < ovTreeTable.size()) {
                            ladders_.changeComment(idx, event.getRowValue().getValue().getAddress(), event.getNewValue());
                        }
                    }
                }
            }
        });
        treeTableIoValue.setCellFactory((TreeTableColumn<LadderTreeTableIo, Double> param) -> {
            return new TextFieldTreeTableCell<>(new LadderValueStringConverter());
        });
        treeTableIoValue.setCellValueFactory((TreeTableColumn.CellDataFeatures<LadderTreeTableIo, Double> param) -> {
            return param.getValue().getValue().valueProperty().asObject();
        });
        treeTableIoValue.setOnEditCommit((TreeTableColumn.CellEditEvent<LadderTreeTableIo, Double> event) -> {
            if ((event.getOldValue() != null) && (event.getNewValue() != null)) {
                if (!event.getOldValue().equals(event.getNewValue())) {
                    ladders_.setValue(event.getRowValue().getValue().getAddress(), event.getNewValue());
                }
            }
        });

        // ladder
        tabLadder.setOnKeyPressed((KeyEvent event) -> {
            event.consume();

            if (tabLadder.getSelectionModel().getSelectedItem() != null) {
                ScrollPane scrollPane = (ScrollPane) tabLadder.getSelectionModel().getSelectedItem().getContent();
                LadderPane pane = (LadderPane) scrollPane.getContent();
                ladders_.onKeyPressed(pane, event, scrollPane);
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
        // menu file
        menuFile.setText(LadderEnums.FILE.toString());
        menuFileNew.setText(LadderEnums.FILE_NEW.toString());
        menuFileOpen.setText(LadderEnums.FILE_OPEN.toString());
        menuFileSave.setText(LadderEnums.FILE_SAVE.toString());
        menuFileSaveAs.setText(LadderEnums.FILE_SAVE_AS.toString());
        menuFileDifference.setText(LadderEnums.FILE_DIFFERENCE.toString());

        // menu edit
        menuEdit.setText(LadderEnums.EDIT.toString());
        menuEditUndo.setText(LadderEnums.EDIT_UNDO.toString());
        menuEditRedo.setText(LadderEnums.EDIT_REDO.toString());
        menuEditCut.setText(LadderEnums.EDIT_CUT.toString());
        menuEditCopy.setText(LadderEnums.EDIT_COPY.toString());
        menuEditPaste.setText(LadderEnums.EDIT_PASTE.toString());
        menuEditSelectLeft.setText(LadderEnums.EDIT_SELECT_LEFT.toString());
        menuEditSelectUp.setText(LadderEnums.EDIT_SELECT_UP.toString());
        menuEditSelectRight.setText(LadderEnums.EDIT_SELECT_RIGHT.toString());
        menuEditSelectDown.setText(LadderEnums.EDIT_SELECT_DOWN.toString());
        menuEditTabNew.setText(LadderEnums.EDIT_TAB_NEW.toString());
        menuEditTabClose.setText(LadderEnums.EDIT_TAB_CLOSE.toString());
        menuEditTabRename.setText(LadderEnums.EDIT_TAB_RENAME.toString());
        menuEditTabMoveLeft.setText(LadderEnums.EDIT_TAB_MOVE_LEFT.toString());
        menuEditTabMoveRight.setText(LadderEnums.EDIT_TAB_MOVE_RIGHT.toString());

        // menu view
        menuView.setText(LadderEnums.VIEW.toString());
        menuViewTabSelectNext.setText(LadderEnums.VIEW_TAB_SELECT_NEXT.toString());
        menuViewTabSelectPrevious.setText(LadderEnums.VIEW_TAB_SELECT_PREVIOUS.toString());

        // menu tool
        menuTools.setText(LadderEnums.TOOLS.toString());
        menuToolsConnect.setText(LadderEnums.TOOLS_CONNECT.toString());
        menuToolsRun.setText(LadderEnums.TOOLS_RUN.toString());
        menuToolsStop.setText(LadderEnums.TOOLS_STOP.toString());
        menuToolsStop.setDisable(true);

        // io
        treeTableIo.setCache(true);
        treeTableIoAddress.setText(LadderEnums.IO_ADDRESS.toString());
        treeTableIoComment.setText(LadderEnums.IO_COMMENT.toString());
        treeTableIoValue.setText(LadderEnums.IO_VALUE.toString());
    }

    /**
     *
     * @return
     */
    public Ladders getLadders() {
        return ladders_;
    }

    /**
     *
     * @return
     */
    public TabPane getTabLadder() {
        return tabLadder;
    }

    /**
     *
     * @return
     */
    public TreeTableView<LadderTreeTableIo> getTreeTableIo() {
        return treeTableIo;
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
     * @param idealCycleTime
     */
    public void setIdealCycleTime(long idealCycleTime) {
        idealCycleTime_ = idealCycleTime;
        Platform.runLater(() -> {
            lblIdealCycleTime.setText(Long.toString(idealCycleTime_) + "ns(" + LadderEnums.CYCLE_IDEAL.toString() + ")");
        });
    }

    /**
     *
     * @param bln
     */
    public void runViewRunning(boolean bln) {
        if (Platform.isFxApplicationThread()) {
            viewRunning(bln);
        } else {
            Platform.runLater(() -> {
                viewRunning(bln);
            });
        }
    }

    private void viewRunning(boolean bln) {
        menuEditTabMoveLeft.setDisable(bln);
        menuEditTabMoveRight.setDisable(bln);
        menuToolsRun.setDisable(bln);
        menuToolsStop.setDisable(!bln);
    }

    /**
     *
     * @param minCycleTime
     */
    public void setMinCycleTime(long minCycleTime) {
        Platform.runLater(() -> {
            lblMinCycleTime.setText(Long.toString(minCycleTime) + "ns(" + LadderEnums.CYCLE_MIN.toString() + ")");
        });
    }

    /**
     *
     * @param maxCycleTime
     */
    public void setMaxCycleTime(long maxCycleTime) {
        Platform.runLater(() -> {
            lblMaxCycleTime.setText(Long.toString(maxCycleTime) + "ns(" + LadderEnums.CYCLE_MAX.toString() + ")");
        });
    }

    /**
     *
     * @param cumulativeCycleTime
     * @param cumulativeCycleTimeCount
     */
    public void refreshLadder(long cumulativeCycleTime, long cumulativeCycleTimeCount) {
        if (isShown_ && viewRefresh_) {
            viewRefresh_ = false;
            Platform.runLater(() -> {
                // cycletime
                lblCycleTime.setText(Long.toString(cumulativeCycleTime / cumulativeCycleTimeCount) + "ns");

                // io map
                ioMap_ = ladders_.getIoMap();

                // ladder refresh
                selectedTab_ = tabLadder.getSelectionModel().getSelectedItem();
                if (selectedTab_ != null) {
                    selectedScrollPane_ = (ScrollPane) selectedTab_.getContent();
                    ((LadderPane) selectedScrollPane_.getContent()).refreshView(ioMap_, selectedScrollPane_);
                }

                // tree table refresh
                ovTreeTable_ = treeTableIo.getRoot().getChildren();
                for (treeTableIndex_ = 0, treeTableSize_ = ovTreeTable_.size(); treeTableIndex_ < treeTableSize_; treeTableIndex_++) {
                    ovTreeTableChild_ = ovTreeTable_.get(treeTableIndex_).getChildren();
                    if (ovTreeTable_.get(treeTableIndex_).isExpanded()) {
                        for (treeTableChildIndex_ = 0, treeTableChildSize_ = ovTreeTableChild_.size(); treeTableChildIndex_ < treeTableChildSize_; treeTableChildIndex_++) {
                            ladderTreeTableIo_ = ovTreeTableChild_.get(treeTableChildIndex_).getValue();
                            treeTableValue_ = ioMap_.get(treeTableIndex_).get(ladderTreeTableIo_.getAddress()).getValue();
                            if (treeTableValue_ != ladderTreeTableIo_.getValue()) {
                                ladderTreeTableIo_.setValue(treeTableValue_);
                            }
                        }
                    }
                }
                viewRefresh_ = true;
            });
        }
    }

    /**
     *
     * @param filename
     */
    public void addRecentFile(String filename) {
        if (filename != null) {
            if (!filename.isEmpty()) {
                String[] recent = new String[11];
                int cnt, offset = 0;

                recent[0] = filename;
                recent[1] = menuFileRecentFile1.getText();
                recent[2] = menuFileRecentFile2.getText();
                recent[3] = menuFileRecentFile3.getText();
                recent[4] = menuFileRecentFile4.getText();
                recent[5] = menuFileRecentFile5.getText();
                recent[6] = menuFileRecentFile6.getText();
                recent[7] = menuFileRecentFile7.getText();
                recent[8] = menuFileRecentFile8.getText();
                recent[9] = menuFileRecentFile9.getText();
                recent[10] = menuFileRecentFile10.getText();

                for (cnt = 1; cnt < recent.length; cnt++) {
                    if (recent[cnt].equals(recent[0])) {
                        offset++;
                    }

                    if (cnt + offset < recent.length) {
                        recent[cnt] = recent[cnt + offset];
                    } else {
                        recent[cnt] = "";
                    }
                }

                menuFileRecentFile1.setText(recent[0]);
                menuFileRecentFile2.setText(recent[1]);
                menuFileRecentFile3.setText(recent[2]);
                menuFileRecentFile4.setText(recent[3]);
                menuFileRecentFile5.setText(recent[4]);
                menuFileRecentFile6.setText(recent[5]);
                menuFileRecentFile7.setText(recent[6]);
                menuFileRecentFile8.setText(recent[7]);
                menuFileRecentFile9.setText(recent[8]);
                menuFileRecentFile10.setText(recent[9]);

                runViewRecentFile();
            }
        }
    }

    private void runViewRecentFile() {
        if (Platform.isFxApplicationThread()) {
            viewRecentFile();
        } else {
            Platform.runLater(() -> {
                viewRecentFile();
            });
        }
    }

    private void viewRecentFile() {
        menuFileRecentFile1.setVisible(!menuFileRecentFile1.getText().isEmpty());
        menuFileRecentFile2.setVisible(!menuFileRecentFile2.getText().isEmpty());
        menuFileRecentFile3.setVisible(!menuFileRecentFile3.getText().isEmpty());
        menuFileRecentFile4.setVisible(!menuFileRecentFile4.getText().isEmpty());
        menuFileRecentFile5.setVisible(!menuFileRecentFile5.getText().isEmpty());
        menuFileRecentFile6.setVisible(!menuFileRecentFile6.getText().isEmpty());
        menuFileRecentFile7.setVisible(!menuFileRecentFile7.getText().isEmpty());
        menuFileRecentFile8.setVisible(!menuFileRecentFile8.getText().isEmpty());
        menuFileRecentFile9.setVisible(!menuFileRecentFile9.getText().isEmpty());
        menuFileRecentFile10.setVisible(!menuFileRecentFile10.getText().isEmpty());
    }

    private void undo(Tab tab, LadderPane pane) {
        ladders_.undo(tab, pane);
    }

    private void redo(Tab tab, LadderPane pane) {
        ladders_.redo(tab, pane);
    }

    private void tabNew() {
        ladders_.ladderNewTab();
    }

    private void tabClose() {
        ladders_.ladderRemoveTab(tabLadder.getSelectionModel().getSelectedIndex());
    }

    private void tabRename(LadderPane pane) {
        ladders_.ladderChangeTabName(pane);
    }

    private void tabMoveLeft(LadderPane pane) {
        ladders_.ladderMoveLeft(pane.getLadder());
    }

    private void tabMoveRight(LadderPane pane) {
        ladders_.ladderMoveRight(pane.getLadder());
    }

    private void tabSelectNext() {
        ladders_.ladderChangeSelectNext(tabLadder);
    }

    private void tabSelectPrevious() {
        ladders_.ladderChangeSelectPrevious(tabLadder);
    }

    private void initProperties() {
        // stage
        stageSettings_.setStageMaximized(false, true);
        stageSettings_.setStageWidth(640.0, true);
        stageSettings_.setStageHeight(480.0, true);
        stageSettings_.setRootWidth(640.0, true);
        stageSettings_.setRootHeight(480.0, true);
        stageSettings_.setIoLadderDividerPositions(0.382, true);

        // history generation
        historyGeneration_ = -1;

        // ideal cycletime
        idealCycleTime_ = 0;

        // menu
        menuFileRecentFile1.setText("");
        menuFileRecentFile2.setText("");
        menuFileRecentFile3.setText("");
        menuFileRecentFile4.setText("");
        menuFileRecentFile5.setText("");
        menuFileRecentFile6.setText("");
        menuFileRecentFile7.setText("");
        menuFileRecentFile8.setText("");
        menuFileRecentFile9.setText("");
        menuFileRecentFile10.setText("");
    }

    private boolean loadProperties(Path propertyFile) {
        if (Files.exists(propertyFile) && Files.isRegularFile(propertyFile) && Files.isReadable(propertyFile)) {
            Properties properties = new Properties();
            try {
                properties.loadFromXML(Files.newInputStream(propertyFile));

                // stage
                stageSettings_.setStageMaximized((Boolean.valueOf(properties.getProperty("STAGE_MAXIMIZED", "false"))), true);
                stageSettings_.setStageWidth(Double.parseDouble(properties.getProperty("STAGE_WIDTH", "640.0")), true);
                stageSettings_.setStageHeight(Double.parseDouble(properties.getProperty("STAGE_HEIGHT", "480.0")), true);
                stageSettings_.setRootWidth(Double.parseDouble(properties.getProperty("PANE_ROOT_WIDTH", "640.0")), true);
                stageSettings_.setRootHeight(Double.parseDouble(properties.getProperty("PANE_ROOT_HEIGHT", "480.0")), true);
                stageSettings_.setIoLadderDividerPositions(Double.parseDouble(properties.getProperty("SPLIT_DIVIDER_IO_LADDER", "0.382")), true);
                ladders_.setWorkFilePath(properties.getProperty("WORK_FILE_PATH", ""));

                // history generation
                ladders_.setHistoryGeneration(Integer.parseInt(properties.getProperty("HISTORY_GENERATION", "-1")));

                // ideal cycletime
                ladders_.setIdealCycleTime(Long.parseLong(properties.getProperty("IDEAL_CYCLE_TIME", "0")));

                // menu
                menuFileRecentFile1.setText(properties.getProperty("MENU_RECENT_FILE_1", ""));
                menuFileRecentFile2.setText(properties.getProperty("MENU_RECENT_FILE_2", ""));
                menuFileRecentFile3.setText(properties.getProperty("MENU_RECENT_FILE_3", ""));
                menuFileRecentFile4.setText(properties.getProperty("MENU_RECENT_FILE_4", ""));
                menuFileRecentFile5.setText(properties.getProperty("MENU_RECENT_FILE_5", ""));
                menuFileRecentFile6.setText(properties.getProperty("MENU_RECENT_FILE_6", ""));
                menuFileRecentFile7.setText(properties.getProperty("MENU_RECENT_FILE_7", ""));
                menuFileRecentFile8.setText(properties.getProperty("MENU_RECENT_FILE_8", ""));
                menuFileRecentFile9.setText(properties.getProperty("MENU_RECENT_FILE_9", ""));
                menuFileRecentFile10.setText(properties.getProperty("MENU_RECENT_FILE_10", ""));
                return true;
            } catch (FileNotFoundException ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            } catch (IOException ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            }
        } else {
            // stage
            stageSettings_.setStageMaximized(stageSettings_.isStageMaximized(), true);
            stageSettings_.setStageWidth(stageSettings_.getStageWidth(), true);
            stageSettings_.setStageHeight(stageSettings_.getStageHeight(), true);
            stageSettings_.setRootWidth(stageSettings_.getRootWidth(), true);
            stageSettings_.setRootHeight(stageSettings_.getRootHeight(), true);
            stageSettings_.setIoLadderDividerPositions(stageSettings_.getIoLadderDividerPositions(), true);
        }
        return false;
    }

    private boolean saveProperties(Path propertyFile) {
        try {
            if (!Files.exists(propertyFile)) {
                Files.createFile(propertyFile);
            }
            if (Files.isWritable(propertyFile)) {
                Properties properties = new Properties();

                // stage
                properties.setProperty("STAGE_MAXIMIZED", Boolean.toString(stageSettings_.isStageMaximized()));
                properties.setProperty("STAGE_WIDTH", Double.toString(stageSettings_.getStageWidth()));
                properties.setProperty("STAGE_HEIGHT", Double.toString(stageSettings_.getStageHeight()));
                properties.setProperty("PANE_ROOT_WIDTH", Double.toString(stageSettings_.getRootWidth()));
                properties.setProperty("PANE_ROOT_HEIGHT", Double.toString(stageSettings_.getRootHeight()));
                properties.setProperty("SPLIT_DIVIDER_IO_LADDER", Double.toString(stageSettings_.getIoLadderDividerPositions()));
                properties.setProperty("WORK_FILE_PATH", ladders_.getWorkFilePath());

                // history generation
                properties.setProperty("HISTORY_GENERATION", Integer.toString(ladders_.getHistoryGeneration()));

                // ideal cycletime
                properties.setProperty("IDEAL_CYCLE_TIME", Long.toString(ladders_.getIdealCycleTime()));

                // menu
                properties.setProperty("MENU_RECENT_FILE_1", menuFileRecentFile1.getText());
                properties.setProperty("MENU_RECENT_FILE_2", menuFileRecentFile2.getText());
                properties.setProperty("MENU_RECENT_FILE_3", menuFileRecentFile3.getText());
                properties.setProperty("MENU_RECENT_FILE_4", menuFileRecentFile4.getText());
                properties.setProperty("MENU_RECENT_FILE_5", menuFileRecentFile5.getText());
                properties.setProperty("MENU_RECENT_FILE_6", menuFileRecentFile6.getText());
                properties.setProperty("MENU_RECENT_FILE_7", menuFileRecentFile7.getText());
                properties.setProperty("MENU_RECENT_FILE_8", menuFileRecentFile8.getText());
                properties.setProperty("MENU_RECENT_FILE_9", menuFileRecentFile9.getText());
                properties.setProperty("MENU_RECENT_FILE_10", menuFileRecentFile10.getText());

                properties.storeToXML(Files.newOutputStream(propertyFile), LadderEnums.LADDERS.toString());
                return true;
            }
        } catch (IOException ex) {
            Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
        }
        return false;
    }

    /**
     *
     * @param stage
     * @param icons
     * @param currentPath
     */
    public void startUp(Stage stage, List<Image> icons, Path currentPath) {
        stage_ = stage;
        icons_ = icons;

        // ladder path
        ladderPath_ = currentPath.resolve("ladder");
        if (!Files.exists(ladderPath_)) {
            try {
                Files.createDirectories(ladderPath_);
            } catch (IOException ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            }
        }

        // load properties
        initProperties();
        loadProperties(ladderPath_.resolve("properties.xml"));

        // design
        addEventDesign();
        runInitDesign();
        runViewRecentFile();

        // ladders
        ladders_.startUp(stage_, icons_, ladderPath_);
        ladders_.setHistoryGeneration(historyGeneration_);
        ladders_.setIdealCycleTime(idealCycleTime_);
    }

    /**
     *
     * @return
     */
    public boolean cleanUp() {
        // save check
        ButtonType response = ladders_.fileSavedCheck();
        if ((response == ButtonType.YES) || ((ladders_.getFileName() != null) && (response == null))) {
            if (ladders_.fileSave()) {
                addRecentFile(ladders_.getFilePath().toString());
            }
        } else if ((response == ButtonType.CANCEL) || (response == ButtonType.CLOSE)) {
            return false;
        }

        // save properties
        saveProperties(ladderPath_.resolve("properties.xml"));

        // clean up
        ladders_.cleanUp();

        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stage_ = null;
        icons_ = null;
        stageSettings_ = new StageSettings();

        ladders_ = new Ladders(this);
        historyGeneration_ = -1;
        idealCycleTime_ = 0;
        ladderPath_ = null;

        ioMap_ = null;
        selectedTab_ = null;
        selectedScrollPane_ = null;
        ladderTreeTableIo_ = null;
        treeTableIndex_ = 0;
        treeTableChildIndex_ = 0;
        treeTableSize_ = 0;
        treeTableChildSize_ = 0;
        treeTableValue_ = 0.0;

        viewRefresh_ = true;
        isShowing_ = false;
        isShown_ = false;
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(LadderEnums.LADDER.toString(), msg, err);
    }
}
