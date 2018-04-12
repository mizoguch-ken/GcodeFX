/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ken.mizoguch.console.Console;
import ken.mizoguch.soem.Soem;
import ken.mizoguch.webviewer.plugin.gcodefx.LaddersPlugin;
import netscape.javascript.JSException;

/**
 *
 * @author mizoguch-ken
 */
public class Ladders extends Service<Void> implements LaddersPlugin {

    public static final long LADDER_VIEW_REFRESH_CYCLE_TIME = TimeUnit.MILLISECONDS.toNanos(100);
    public static final int LADDER_DEFAULT_GRID_COLUMN = 10;
    public static final int LADDER_DEFAULT_GRID_ROW = 10;
    public static final double LADDER_DEFAULT_GRID_MIN_SIZE = 78.912;
    public static final double LADDER_DEFAULT_GRID_MAX_SIZE = LADDER_DEFAULT_GRID_MIN_SIZE * 122.966;
    public static final double LADDER_DEFAULT_GRID_CONTENTS_WIDTH = LADDER_DEFAULT_GRID_MIN_SIZE * 0.382;
    public static final double LADDER_DEFAULT_GRID_CONTENTS_HIGHT = LADDER_DEFAULT_GRID_CONTENTS_WIDTH * 0.618;

    public static final int LADDER_GLOBAL_ADDRESS_INDEX = 0;
    public static final String LADDER_LOCAL_ADDRESS_PREFIX = ".";

    /**
     *
     */
    public static enum LADDER_BLOCK {
        // Contents
        CONTENTS,
        // Empty
        EMPTY,
        // Connect
        CONNECT_LINE,
        // Load
        LOAD,
        LOAD_NOT,
        LOAD_RISING,
        LOAD_RISING_NOT,
        LOAD_FALLING,
        LOAD_FALLING_NOT,
        // Out
        OUT,
        OUT_NOT,
        OUT_RISING,
        OUT_RISING_NOT,
        OUT_FALLING,
        OUT_FALLING_NOT,
        // Load Function
        COMPARISON_EQUAL,
        COMPARISON_NOT_EQUAL,
        COMPARISON_LESS,
        COMPARISON_LESS_EQUAL,
        COMPARISON_GREATER,
        COMPARISON_GREATER_EQUAL,
        COMPARISON_AND_BITS,
        COMPARISON_OR_BITS,
        COMPARISON_XOR_BITS,
        // Out Function
        SET,
        RESET,
        AND_BITS,
        OR_BITS,
        XOR_BITS,
        NOT_BITS,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        AVERAGE,
        SHIFT_LEFT_BITS,
        SHIFT_RIGHT_BITS,
        SIGMOID,
        RANDOM,
        TIMER,
        TIMER_NOT,
        COUNTER,
        COUNTER_NOT,
        MOVE,
        // Collaboration
        SCRIPT;
    }

    /**
     *
     */
    public static enum LADDER_COMMAND {
        // root
        CHANGE_ADDRESS,
        CHANGE_COMMENT,
        // ladder
        LADDER_CREATE,
        LADDER_REMOVE,
        LADDER_REMOVE_ROW,
        LADDER_INSERT_ROW,
        LADDER_MOVE_LEFT,
        LADDER_MOVE_RIGHT,
        LADDER_CHANGE_NAME,
        // block
        BLOCK_CHANGE;
    }

    private final DesignLaddersController ladderController_;
    private final TabPane tabLadder;
    private final TreeTableView<LadderTreeTableIo> treeTableIo;

    private Stage stage_;
    private List<Image> icons_;
    private WebEngine webEngine_;
    private Worker.State state_;
    private Soem soem_;
    private LadderCommand ladderCommand_;
    private int historyGeneration_;
    private final CopyOnWriteArrayList<LadderRegisterSoemIo> registerSoemIn_, registerSoemOut_;
    private Path filePath_;
    private final String newLineCharacter_ = "\r\n";

    private final Object lock_ = new Object();
    private Ladder[] ladders_;
    private ObservableList<TreeItem<LadderTreeTableIo>> ovScript_;
    private int laddersSize_, scriptIndex_, scriptSize_;
    private final CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap_;
    private final CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap_;
    private final CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> scriptIoMap_;
    private long idealCycleTime_;
    private boolean isCycling_, isChanged_;

    private final Gson gson_ = new GsonBuilder().setPrettyPrinting().create();

    /**
     *
     * @param ladderController
     */
    public Ladders(DesignLaddersController ladderController) {
        ladderController_ = ladderController;
        tabLadder = ladderController_.getTabLadder();
        treeTableIo = ladderController_.getTreeTableIo();
        treeTableIo.setRoot(new TreeItem());

        stage_ = null;
        icons_ = null;
        webEngine_ = null;
        state_ = Worker.State.READY;
        soem_ = null;
        ladderCommand_ = null;
        historyGeneration_ = 0;
        registerSoemIn_ = new CopyOnWriteArrayList<>();
        registerSoemOut_ = new CopyOnWriteArrayList<>();
        filePath_ = null;

        ladders_ = null;
        laddersSize_ = 0;
        scriptIndex_ = 0;
        scriptSize_ = 0;
        ioMap_ = new CopyOnWriteArrayList<>();
        commentMap_ = new CopyOnWriteArrayList<>();
        scriptIoMap_ = new CopyOnWriteArrayList<>();
        idealCycleTime_ = 0;
        isChanged_ = false;
        isCycling_ = false;
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
     * @return
     */
    public Object getLock() {
        return lock_;
    }

    /**
     *
     * @return
     */
    public Ladder[] getLadders() {
        return ladders_;
    }

    /**
     *
     * @return
     */
    public LadderCommand getLadderCommand() {
        return ladderCommand_;
    }

    /**
     *
     * @return
     */
    public CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> getIoMap() {
        return ioMap_;
    }

    /**
     *
     * @return
     */
    public CopyOnWriteArrayList<ConcurrentHashMap<String, String>> getCommentMap() {
        return commentMap_;
    }

    /**
     *
     * @return
     */
    public CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> getScriptIoMap() {
        return scriptIoMap_;
    }

    /**
     *
     * @param oldIdx
     * @param oldAddress
     * @param newAddress
     * @param newIdx
     */
    public void changeAddress(int oldIdx, String oldAddress, int newIdx, String newAddress) {
        ladderCommand_.changeAddress(oldIdx, oldAddress, newIdx, newAddress);
        isChanged_ = true;
        setTitle();
    }

    /**
     *
     * @param idx
     * @param address
     * @return
     */
    public boolean isComment(int idx, String address) {
        return commentMap_.get(idx).containsKey(address);
    }

    /**
     *
     * @param idx
     * @param address
     * @return
     */
    public String getComment(int idx, String address) {
        return commentMap_.get(idx).get(address);
    }

    /**
     *
     * @param idx
     * @param address
     * @param comment
     * @return
     */
    public boolean changeComment(int idx, String address, String comment) {
        if (ladderCommand_.changeComment(idx, address, comment)) {
            isChanged_ = true;
            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     * @param address
     * @param value
     */
    public void setValueDirect(String address, double value) {
        setValueDirect(LADDER_GLOBAL_ADDRESS_INDEX, address, value);
    }

    /**
     *
     * @param idx
     * @param address
     * @param value
     */
    public void setValueDirect(int idx, String address, double value) {
        if (!ioMap_.get(idx).containsKey(address)) {
            ioMap_.get(idx).put(address, new LadderIo(address));
            treeTableIo.getRoot().getChildren().get(idx).getChildren().add(new TreeItem<>(new LadderTreeTableIo(address, value)));
        }

        ovScript_ = treeTableIo.getRoot().getChildren().get(idx).getChildren();
        for (scriptIndex_ = 0, scriptSize_ = ovScript_.size(); scriptIndex_ < scriptSize_; scriptIndex_++) {
            if (address.equals(ovScript_.get(scriptIndex_).getValue().getAddress())) {
                ovScript_.get(scriptIndex_).getValue().setValue(value);
                break;
            }
        }

        synchronized (lock_) {
            ioMap_.get(idx).get(address).setValue(value);
            scriptIoMap_.get(idx).putIfAbsent(address, new LadderIo(address));
            scriptIoMap_.get(idx).get(address).setValue(value);
            scriptIoMap_.get(idx).get(address).setCycled(true);
        }
    }

    private void allClear() {
        stopLadder();

        if (registerSoemIn_ != null) {
            registerSoemIn_.clear();
        }
        if (registerSoemOut_ != null) {
            registerSoemOut_.clear();
        }

        tabLadder.getTabs().clear();
        treeTableIo.getRoot().getChildren().clear();
        ladderCommand_.clearHistoryManager();
        ladders_ = null;
        ioMap_.clear();
        commentMap_.clear();
        scriptIoMap_.clear();
    }

    /**
     *
     * @param workFilePath
     */
    public void setWorkFilePath(String workFilePath) {
        if (workFilePath != null) {
            if (workFilePath.isEmpty()) {
                filePath_ = null;
            } else {
                filePath_ = Paths.get(workFilePath);
                if (!Files.isDirectory(filePath_)) {
                    filePath_ = filePath_.getParent();
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public String getWorkFilePath() {
        if (filePath_ == null) {
            return "";
        } else {
            return filePath_.toString();
        }
    }

    /**
     *
     */
    public void ladderNew() {
        allClear();

        // global
        treeTableIo.getRoot().getChildren().add(LADDER_GLOBAL_ADDRESS_INDEX, new TreeItem<>(new LadderTreeTableIo(LadderEnums.ADDRESS_GLOBAL.toString().replace(" ", "_"), LADDER_GLOBAL_ADDRESS_INDEX)));
        ioMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());
        commentMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());
        scriptIoMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());

        ladderCommand_.ladderCreate(LADDER_GLOBAL_ADDRESS_INDEX + 1, LadderEnums.LADDERS.toString(), LADDER_DEFAULT_GRID_COLUMN, LADDER_DEFAULT_GRID_ROW, LADDER_DEFAULT_GRID_MIN_SIZE, LADDER_DEFAULT_GRID_MAX_SIZE, LADDER_DEFAULT_GRID_CONTENTS_WIDTH, LADDER_DEFAULT_GRID_CONTENTS_HIGHT);
    }

    /**
     *
     * @return
     */
    public boolean ladderNewTab() {
        StringBuilder name = new StringBuilder(LadderEnums.LADDERS.toString());
        int index, size;

        for (index = 0, size = tabLadder.getTabs().size(); index < size; index++) {
            if (checkTabName(name.toString())) {
                break;
            }
            name.delete(0, name.length());
            name.append(LadderEnums.LADDERS.toString()).append(" ").append(index + 1);
        }
        index = tabLadder.getTabs().size() + 1;
        LadderPane pane = ladderCommand_.ladderCreate(index, name.toString(), LADDER_DEFAULT_GRID_COLUMN, LADDER_DEFAULT_GRID_ROW, LADDER_DEFAULT_GRID_MIN_SIZE, LADDER_DEFAULT_GRID_MAX_SIZE, LADDER_DEFAULT_GRID_CONTENTS_WIDTH, LADDER_DEFAULT_GRID_CONTENTS_HIGHT);
        tabLadder.getSelectionModel().select(pane.getLadder().getIdx() - 1);
        isChanged_ = true;

        setTitle();
        return true;
    }

    /**
     *
     * @param index
     * @return
     */
    public boolean ladderRemoveTab(int index) {
        ladderCommand_.ladderRemove(index);
        isChanged_ = true;

        setTitle();
        return true;
    }

    /**
     *
     * @param pane
     * @return
     */
    public boolean ladderChangeTabName(LadderPane pane) {
        TextInputDialog alert = new TextInputDialog(pane.getLadder().getName());

        alert.initOwner(stage_);
        alert.initStyle(StageStyle.UTILITY);
        if (!icons_.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
        }

        alert.setTitle(LadderEnums.RENAME.toString());
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.setGraphic(null);
        Optional<String> result = alert.showAndWait();
        stage_.requestFocus();

        if (result.isPresent()) {
            String name = result.get().trim();
            if (checkTabName(name)) {
                treeTableIo.getRoot().getChildren().get(pane.getLadder().getIdx()).getValue().setAddress(name.replace(" ", "_"));
                pane.setChanged(true);
                ladderCommand_.ladderChangeName(pane, name);
                isChanged_ = true;

                setTitle();
                return true;
            } else {
                writeLog(LadderEnums.DUPLICATE_NAMES_ERROR.toString(), true);
            }
        }
        return false;
    }

    /**
     *
     * @param idx
     * @param name
     * @return
     */
    public boolean ladderChangeTabName(int idx, String name) {
        name = name.trim();
        if (checkTabName(name)) {
            LadderPane pane = (LadderPane) ((ScrollPane) tabLadder.getTabs().get(idx - 1).getContent()).getContent();
            treeTableIo.getRoot().getChildren().get(idx).getValue().setAddress(name.replace(" ", "_"));
            pane.setChanged(true);
            ladderCommand_.ladderChangeName(pane, name);
            isChanged_ = true;

            setTitle();
            return true;
        } else {
            writeLog(LadderEnums.DUPLICATE_NAMES_ERROR.toString(), true);
        }
        return false;
    }

    /**
     *
     * @param name
     * @return
     */
    private boolean checkTabName(String name) {
        if (name != null) {
            int index, size;

            name = name.replace(" ", "_");
            for (index = 0, size = tabLadder.getTabs().size(); index < size; index++) {
                if (((LadderPane) ((ScrollPane) tabLadder.getTabs().get(index).getContent()).getContent()).getLadder().getName().equals(name)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Path ladderOpen(TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, Path file) {
        return ladderOpen(tabPane, treeTableView, ioMap_, commentMap_, file);
    }

    private Path ladderOpen(TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, Path file) {
        if (file == null) {
            FileChooser fileChooser = new FileChooser();

            if (filePath_ != null) {
                if (filePath_.getParent() != null) {
                    if (Files.exists(filePath_.getParent())) {
                        fileChooser.setInitialDirectory(filePath_.getParent().toFile());
                    }
                }
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
            File fcfile = fileChooser.showOpenDialog(stage_);
            if (fcfile == null) {
                return null;
            }
            file = fcfile.toPath();
        }

        LadderJson ladderJson = ladderJsonOpen(file);
        if (ladderJson != null) {
            allClear();

            // global
            treeTableIo.getRoot().getChildren().add(LADDER_GLOBAL_ADDRESS_INDEX, new TreeItem<>(new LadderTreeTableIo(LadderEnums.ADDRESS_GLOBAL.toString().replace(" ", "_"), LADDER_GLOBAL_ADDRESS_INDEX)));
            ioMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());
            commentMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());
            scriptIoMap_.add(LADDER_GLOBAL_ADDRESS_INDEX, new ConcurrentHashMap<>());

            if (ladderJsonLoad(this, tabPane, treeTableView, ioMap, commentMap, scriptIoMap_, ladderJson)) {
                return file;
            }
        }
        return null;
    }

    private Path ladderSave() {
        if (filePath_ == null) {
            return ladderSaveAs();
        } else if (Files.exists(filePath_)) {
            if (Files.isRegularFile(filePath_)) {
                return ladderSave(filePath_);
            } else {
                return ladderSaveAs();
            }
        } else {
            return ladderSaveAs();
        }
    }

    private Path ladderSave(Path file) {
        if (file != null) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(new StringReader(gson_.toJson(ladderJsonSave(tabLadder, ioMap_, commentMap_))))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outputStreamWriter.write(line);
                    if (bufferedReader.ready()) {
                        outputStreamWriter.write(newLineCharacter_);
                    }
                }

                Tab tab;
                LadderPane pane;
                int index, size;

                for (index = 0, size = tabLadder.getTabs().size(); index < size; index++) {
                    tab = tabLadder.getTabs().get(index);
                    pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
                    pane.setChanged(false);
                    tab.setText(pane.getLadder().getName());
                }
                return file;
            } catch (IOException ex) {
                Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
            }
        }
        return null;
    }

    private Path ladderSaveAs() {
        FileChooser fileChooser = new FileChooser();

        if (filePath_ != null) {
            if (filePath_.getParent() != null) {
                if (Files.exists(filePath_.getParent())) {
                    fileChooser.setInitialDirectory(filePath_.getParent().toFile());
                }
            }
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File fcfile = fileChooser.showSaveDialog(stage_);
        if (fcfile != null) {
            return ladderSave(fcfile.toPath());
        }
        return null;
    }

    /**
     *
     * @param file
     * @return
     */
    public LadderJson ladderJsonOpen(Path file) {
        if (file != null) {
            if (Files.exists(file)) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file), "UTF-8"))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                    return gson_.fromJson(builder.toString(), LadderJson.class);
                } catch (FileNotFoundException ex) {
                    Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
                } catch (JSException | IOException ex) {
                    Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
                }
            }
        }
        return null;
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
     * @return
     */
    public boolean ladderJsonLoad(Ladders ladders, TabPane tabPane, TreeTableView<LadderTreeTableIo> treeTableView, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> scriptIoMap, LadderJson ladderJson) {
        ladderCommand_.setDisableHistory(true);
        try {
            // ladder
            if (!ladderCommand_.restoreLadders(ladders, tabPane, treeTableView, ioMap, commentMap, scriptIoMap, ladderJson, false)) {
                return false;
            }

            // comments
            if (!ladderCommand_.restoreComments(tabPane, treeTableView, ioMap, commentMap, ladderJson)) {
                return false;
            }

            // history
            if (ladders != null) {
                if (ladderJson.getHistoryManager() != null) {
                    ladderCommand_.setHistoryManager(ladderJson.getHistoryManager());
                }
            }

            ladderCommand_.setDisableHistory(false);
            return true;
        } catch (Exception ex) {
            Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
        }
        ladderCommand_.setDisableHistory(false);
        return false;
    }

    /**
     *
     * @param tabPane
     * @return
     */
    public LadderJson ladderJsonSave(TabPane tabPane) {
        return ladderJsonSave(tabPane, ioMap_, commentMap_);
    }

    private LadderJson ladderJsonSave(TabPane tabPane, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap, CopyOnWriteArrayList<ConcurrentHashMap<String, String>> commentMap) {
        LadderJson ladderJson = new LadderJson();

        // ladder
        ladderCommand_.backupLadders(ladderJson, tabPane);

        // comments
        ladderCommand_.backupComments(ladderJson, ioMap, commentMap);

        // historys
        ladderJson.setHistoryManager(ladderCommand_.getHistoryManager(), -1);

        return ladderJson;
    }

    /**
     *
     * @param ladder
     * @return
     */
    public boolean ladderMoveLeft(Ladder ladder) {
        int idx = ladder.getIdx();

        if (idx > 1) {
            TreeItem<LadderTreeTableIo> treeItem = treeTableIo.getRoot().getChildren().get(idx);
            treeTableIo.getRoot().getChildren().set(idx, treeTableIo.getRoot().getChildren().get(idx - 1));
            treeTableIo.getRoot().getChildren().set(idx - 1, treeItem);

            ConcurrentHashMap<String, LadderIo> ioMap = ioMap_.get(idx);
            ioMap_.set(idx, ioMap_.get(idx - 1));
            ioMap_.set(idx - 1, ioMap);

            ConcurrentHashMap<String, String> commentMap = commentMap_.get(idx);
            commentMap_.set(idx, commentMap_.get(idx - 1));
            commentMap_.set(idx - 1, commentMap);

            ConcurrentHashMap<String, LadderIo> scriptIoMap = scriptIoMap_.get(idx);
            scriptIoMap_.set(idx, scriptIoMap_.get(idx - 1));
            scriptIoMap_.set(idx - 1, scriptIoMap);

            ladderCommand_.ladderMoveLeft(ladder);
            isChanged_ = true;

            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     * @param ladder
     * @return
     */
    public boolean ladderMoveRight(Ladder ladder) {
        int idx = ladder.getIdx();

        if (idx < getTabLadder().getTabs().size()) {
            TreeItem<LadderTreeTableIo> treeItem = treeTableIo.getRoot().getChildren().get(idx);
            treeTableIo.getRoot().getChildren().set(idx, treeTableIo.getRoot().getChildren().get(idx + 1));
            treeTableIo.getRoot().getChildren().set(idx + 1, treeItem);

            ConcurrentHashMap<String, LadderIo> ioMap = ioMap_.get(idx);
            ioMap_.set(idx, ioMap_.get(idx + 1));
            ioMap_.set(idx + 1, ioMap);

            ConcurrentHashMap<String, String> commentMap = commentMap_.get(idx);
            commentMap_.set(idx, commentMap_.get(idx + 1));
            commentMap_.set(idx + 1, commentMap);

            ConcurrentHashMap<String, LadderIo> scriptIoMap = scriptIoMap_.get(idx);
            scriptIoMap_.set(idx, scriptIoMap_.get(idx + 1));
            scriptIoMap_.set(idx + 1, scriptIoMap);

            ladderCommand_.ladderMoveRight(ladder);
            isChanged_ = true;

            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     * @param index
     */
    public void ladderChangeSelect(int index) {
        ladderCommand_.ladderChangeSelect(index);
    }

    /**
     *
     * @param tabPane
     */
    public void ladderChangeSelectNext(TabPane tabPane) {
        int index = tabPane.getSelectionModel().getSelectedIndex();
        int size = tabPane.getTabs().size();

        if (index < (size - 1)) {
            ladderCommand_.ladderChangeSelect(index + 1);
        } else {
            ladderCommand_.ladderChangeSelect(0);
        }
    }

    /**
     *
     * @param tabPane
     */
    public void ladderChangeSelectPrevious(TabPane tabPane) {
        int index = tabLadder.getSelectionModel().getSelectedIndex();
        int size = tabLadder.getTabs().size();

        if (index > 0) {
            ladderCommand_.ladderChangeSelect(index - 1);
        } else {
            ladderCommand_.ladderChangeSelect(size - 1);
        }
    }

    /**
     *
     * @param tab
     * @param pane
     * @return
     */
    public boolean undo(Tab tab, LadderPane pane) {
        if (ladderCommand_.undo()) {
            isChanged_ = true;

            if (tab == null) {
                tab = tabLadder.getSelectionModel().getSelectedItem();
                if (tab != null) {
                    pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
                }
            }

            if (tab != null) {
                pane.setChanged(true);
                tab.setText(pane.getLadder().getName() + " *");
            }

            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     * @param tab
     * @param pane
     * @return
     */
    public boolean redo(Tab tab, LadderPane pane) {
        if (ladderCommand_.redo()) {
            isChanged_ = true;

            if (tab == null) {
                tab = tabLadder.getSelectionModel().getSelectedItem();
                if (tab != null) {
                    pane = (LadderPane) ((ScrollPane) tab.getContent()).getContent();
                }
            }

            if (tab != null) {
                pane.setChanged(true);
                tab.setText(pane.getLadder().getName() + " *");
            }

            setTitle();
            return true;
        }
        return false;
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
     * @return
     */
    public boolean isCycling() {
        return isCycling_;
    }

    /**
     *
     * @param pane
     * @param event
     * @param scrollPane
     * @return
     */
    public boolean onKeyPressed(LadderPane pane, KeyEvent event, ScrollPane scrollPane) {
        Tab tab = tabLadder.getSelectionModel().getSelectedItem();
        if (tab != null) {
            switch (event.getCode()) {
                case TAB:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        ladderChangeSelectNext(tabLadder);
                        return true;
                    } else if (event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        ladderChangeSelectPrevious(tabLadder);
                        return true;
                    }
                    break;
                case F2:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        ladderChangeTabName(pane);
                        return true;
                    }
                    break;
                case F4:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        ladderRemoveTab(tabLadder.getSelectionModel().getSelectedIndex());
                        return true;
                    }
                    break;
                case LEFT:
                    if (!event.isShiftDown() && !event.isShortcutDown() && event.isAltDown()) {
                        if (!isCycling_) {
                            ladderMoveLeft(pane.getLadder());
                            return true;
                        }
                        return false;
                    }
                    break;
                case RIGHT:
                    if (!event.isShiftDown() && !event.isShortcutDown() && event.isAltDown()) {
                        if (!isCycling_) {
                            ladderMoveRight(pane.getLadder());
                            return true;
                        }
                        return false;
                    }
                    break;
                case N:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        fileNew();
                        return true;
                    }
                    break;
                case O:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        if (fileOpen(null)) {
                            ladderController_.addRecentFile(getFilePath().toString());
                        }
                        return true;
                    }
                    break;
                case S:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        if (fileSave()) {
                            ladderController_.addRecentFile(getFilePath().toString());
                        }
                        return true;
                    } else if (!event.isShiftDown() && event.isShortcutDown() && event.isAltDown()) {
                        if (fileSaveAs()) {
                            ladderController_.addRecentFile(getFilePath().toString());
                        }
                        return true;
                    }
                    break;
                case T:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        ladderNewTab();
                        return true;
                    }
                    break;
                case Y:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        redo(tab, pane);
                        return true;
                    }
                    break;
                case Z:
                    if (!event.isShiftDown() && event.isShortcutDown() && !event.isAltDown()) {
                        undo(tab, pane);
                        return true;
                    }
                    break;
            }

            if (pane.onKeyPressed(event, scrollPane)) {
                if (pane.isChanged()) {
                    isChanged_ = true;

                    tab.setText(pane.getLadder().getName() + " *");
                    setTitle();
                    return true;
                }
            }
        }
        return false;
    }

    private void setTitle() {
        String fileName = getFileName();

        if (fileName == null) {
            fileName = "";
        }

        if (isChanged()) {
            stage_.setTitle(fileName + " *");
        } else {
            stage_.setTitle(fileName);
        }
    }

    /**
     *
     * @param stage
     * @param icons
     * @param ladderPath
     * @return
     */
    public boolean startUp(Stage stage, List<Image> icons, Path ladderPath) {
        stage_ = stage;
        icons_ = icons;

        // command
        ladderCommand_ = new LadderCommand(stage_, icons_, this);
        ladderCommand_.setHistoryGeneration(historyGeneration_);

        filePath_ = ladderPath;
        ladderNew();
        return false;
    }

    /**
     *
     */
    public void cleanUp() {
        // clear
        allClear();
        cancel();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Map.Entry<String, LadderIo> entry;
                    LadderRegisterSoemIo registerSoemIo;
                    Long registerSoemValue;
                    long cycleTime, minCycleTime, maxCycleTime, cumulativeCycleTime, cumulativeCycleTimeCount, nanoTime, nanoTimeOld, waitTime;
                    int index, size;

                    ladderController_.runViewRunning(true);

                    cycleTime = 0;
                    minCycleTime = Long.MAX_VALUE;
                    maxCycleTime = 0;
                    cumulativeCycleTime = 0;
                    cumulativeCycleTimeCount = 0;

                    nanoTimeOld = System.nanoTime();
                    while (isCycling_) {
                        synchronized (lock_) {
                            // soem
                            if (soem_ != null) {
                                for (index = 0, size = registerSoemOut_.size(); index < size; index++) {
                                    registerSoemIo = registerSoemOut_.get(index);
                                    soem_.out(registerSoemIo.getSlave(), registerSoemIo.getBitsOffset(), registerSoemIo.getBitsMask(), (long) ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).get(registerSoemIo.getAddress()).getValue());
                                }
                                for (index = 0, size = registerSoemIn_.size(); index < size; index++) {
                                    registerSoemIo = registerSoemIn_.get(index);
                                    registerSoemValue = soem_.in(registerSoemIo.getSlave(), registerSoemIo.getBitsOffset(), registerSoemIo.getBitsMask());
                                    if (registerSoemValue != null) {
                                        ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).get(registerSoemIo.getAddress()).setValue(registerSoemValue);
                                    }
                                }
                            }

                            // script io
                            for (index = 0; index < scriptIoMap_.size(); index++) {
                                if (!scriptIoMap_.get(index).isEmpty()) {
                                    for (Iterator<Map.Entry<String, LadderIo>> iterator = scriptIoMap_.get(index).entrySet().iterator(); iterator.hasNext();) {
                                        entry = iterator.next();
                                        if (entry.getValue().isCycled()) {
                                            ioMap_.get(index).get(entry.getKey()).chehckEdge();
                                            iterator.remove();
                                        } else {
                                            ioMap_.get(index).get(entry.getKey()).setValue(entry.getValue().getValue());
                                            entry.getValue().setCycled(true);
                                        }
                                    }
                                }
                            }

                            // ladder
                            for (index = 0; index < laddersSize_; index++) {
                                ladders_[index].run(ioMap_, cycleTime);
                            }

                            // refresh view
                            if ((LADDER_VIEW_REFRESH_CYCLE_TIME - cumulativeCycleTime) < 0) {
                                ladderController_.refreshLadder(cumulativeCycleTime, cumulativeCycleTimeCount);
                                cumulativeCycleTime = 0;
                                cumulativeCycleTimeCount = 0;
                            }
                        }

                        // ideal cycletime
                        waitTime = idealCycleTime_ - cycleTime;
                        if (waitTime > 0) {
                            try {
                                TimeUnit.NANOSECONDS.sleep(waitTime);
                            } catch (InterruptedException ex) {
                            }
                        }

                        // cycletimme
                        nanoTime = System.nanoTime();
                        cycleTime = nanoTime - nanoTimeOld;
                        if (cycleTime > 0) {
                            if ((cycleTime - minCycleTime) < 0) {
                                minCycleTime = cycleTime;
                                ladderController_.setMinCycleTime(minCycleTime);
                            } else if ((cycleTime - maxCycleTime) > 0) {
                                maxCycleTime = cycleTime;
                                ladderController_.setMaxCycleTime(maxCycleTime);
                            }
                            cumulativeCycleTime += cycleTime;
                            cumulativeCycleTimeCount++;
                            nanoTimeOld = nanoTime;
                        }
                    }
                } catch (Exception ex) {
                    Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
                }
                ladderController_.runViewRunning(false);
                isCycling_ = false;
                return null;
            }
        };
    }

    @Override
    public int getHistoryGeneration() {
        return historyGeneration_;
    }

    @Override
    public void setHistoryGeneration(int historyGeneration) {
        historyGeneration_ = historyGeneration;
        ladderController_.setHistoryGeneration(historyGeneration_);
        if (ladderCommand_ != null) {
            ladderCommand_.setHistoryGeneration(historyGeneration_);
        }
    }

    @Override
    public long getIdealCycleTime() {
        return idealCycleTime_;
    }

    @Override
    public void setIdealCycleTime(long idealCycleTime) {
        idealCycleTime_ = idealCycleTime;
        ladderController_.setIdealCycleTime(idealCycleTime_);
    }

    @Override
    public Boolean isRegistered(Object object) {
        if (object != null) {
            if (object instanceof WebEngine) {
                if (webEngine_ != null) {
                    return webEngine_.equals(object);
                }
            }
            return false;
        }
        return null;
    }

    @Override
    public void register(WebEngine webEngine, Worker.State state) {
        if ((webEngine != null) && (state != null)) {
            webEngine_ = webEngine;
            state_ = state;
            for (int idx = 0; idx < ioMap_.size(); idx++) {
                ioMap_.get(idx).entrySet().forEach((entry) -> {
                    entry.getValue().setWebEngineState(webEngine_, state_);
                });
            }
        }
    }

    @Override
    public Boolean registerIn(Object... objects) {
        if (objects != null) {
            switch (objects.length) {
                case 5:
                    if ((objects[0] instanceof Soem) && (objects[1] instanceof String) && (objects[2] instanceof Integer) && (objects[3] instanceof Long) && (objects[4] instanceof Long)) {
                        if (soem_ == null) {
                            soem_ = (Soem) objects[0];
                            if (registerSoemIn_ != null) {
                                registerSoemIn_.clear();
                            }
                            if (registerSoemOut_ != null) {
                                registerSoemOut_.clear();
                            }
                        }

                        String address = ((String) objects[1]).trim();
                        if (!address.startsWith(LADDER_LOCAL_ADDRESS_PREFIX) && !address.contains(" ")) {
                            if (!ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).containsKey(address)) {
                                ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).put(address, new LadderIo(address));
                                treeTableIo.getRoot().getChildren().get(LADDER_GLOBAL_ADDRESS_INDEX).getChildren().add(new TreeItem<>(new LadderTreeTableIo(address)));
                            }

                            int slave = (int) objects[2];
                            long bitsOffset = (long) objects[3];
                            long bitsMask = (long) objects[4];
                            for (int index = 0, size = registerSoemIn_.size(); index < size; index++) {
                                if (registerSoemIn_.get(index).getAddress().equals(address)) {
                                    if ((registerSoemIn_.get(index).getSlave() != slave) || (registerSoemIn_.get(index).getBitsOffset() != bitsOffset) || (registerSoemIn_.get(index).getBitsMask() != bitsMask)) {
                                        registerSoemIn_.remove(index);
                                        registerSoemIn_.add(new LadderRegisterSoemIo(address, slave, bitsOffset, bitsMask));
                                    }
                                    return true;
                                }
                            }
                            registerSoemIn_.add(new LadderRegisterSoemIo(address, slave, bitsOffset, bitsMask));
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }
        return null;
    }

    @Override
    public Boolean registerOut(Object... objects) {
        if (objects != null) {
            switch (objects.length) {
                case 5:
                    if ((objects[0] instanceof Soem) && (objects[1] instanceof String) && (objects[2] instanceof Integer) && (objects[3] instanceof Long) && (objects[4] instanceof Long)) {
                        if (soem_ == null) {
                            soem_ = (Soem) objects[0];
                            if (registerSoemIn_ != null) {
                                registerSoemIn_.clear();
                            }
                            if (registerSoemOut_ != null) {
                                registerSoemOut_.clear();
                            }
                        }

                        String address = ((String) objects[1]).trim();
                        if (!address.startsWith(LADDER_LOCAL_ADDRESS_PREFIX) && !address.contains(" ")) {
                            if (!ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).containsKey(address)) {
                                ioMap_.get(LADDER_GLOBAL_ADDRESS_INDEX).put(address, new LadderIo(address));
                                treeTableIo.getRoot().getChildren().get(LADDER_GLOBAL_ADDRESS_INDEX).getChildren().add(new TreeItem<>(new LadderTreeTableIo(address)));
                            }

                            int slave = (int) objects[2];
                            long bitsOffset = (long) objects[3];
                            long bitsMask = (long) objects[4];
                            for (int index = 0, size = registerSoemOut_.size(); index < size; index++) {
                                if (registerSoemOut_.get(index).getAddress().equals(address)) {
                                    if ((registerSoemOut_.get(index).getSlave() != slave) || (registerSoemOut_.get(index).getBitsOffset() != bitsOffset) || (registerSoemOut_.get(index).getBitsMask() != bitsMask)) {
                                        registerSoemOut_.remove(index);
                                        registerSoemOut_.add(new LadderRegisterSoemIo(address, slave, bitsOffset, bitsMask));
                                    }
                                    return true;
                                }
                            }
                            registerSoemOut_.add(new LadderRegisterSoemIo(address, (int) objects[2], (long) objects[3], (long) objects[4]));
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }
        return null;
    }

    @Override
    public void unregister(Object object) {
        if (object != null) {
            if (object instanceof WebEngine) {
                webEngine_ = null;
                state_ = Worker.State.READY;
            } else if (object instanceof Soem) {
                if (registerSoemIn_ != null) {
                    registerSoemIn_.clear();
                }
                if (registerSoemOut_ != null) {
                    registerSoemOut_.clear();
                }
                soem_ = null;
            }
        }
    }

    @Override
    public Double getValue(String address) {
        if (!address.startsWith(LADDER_LOCAL_ADDRESS_PREFIX)) {
            return getValue(LADDER_GLOBAL_ADDRESS_INDEX, address);
        }
        return null;
    }

    /**
     *
     * @param idx
     * @param address
     * @return
     */
    public Double getValue(int idx, String address) {
        if (ioMap_.get(idx).containsKey(address)) {
            return ioMap_.get(idx).get(address).getValue();
        }
        return null;
    }

    @Override
    public void setValue(String address, double value) {
        if (!address.startsWith(LADDER_LOCAL_ADDRESS_PREFIX)) {
            setValue(LADDER_GLOBAL_ADDRESS_INDEX, address, value);
        }
    }

    /**
     *
     * @param idx
     * @param address
     * @param value
     */
    public void setValue(int idx, String address, double value) {
        if (!ioMap_.get(idx).containsKey(address)) {
            ioMap_.get(idx).put(address, new LadderIo(address));
            treeTableIo.getRoot().getChildren().get(idx).getChildren().add(new TreeItem(new LadderTreeTableIo(address, value)));
        }

        ovScript_ = treeTableIo.getRoot().getChildren().get(idx).getChildren();
        for (scriptIndex_ = 0, scriptSize_ = ovScript_.size(); scriptIndex_ < scriptSize_; scriptIndex_++) {
            if (address.equals(ovScript_.get(scriptIndex_).getValue().getAddress())) {
                ovScript_.get(scriptIndex_).getValue().setValue(value);
                break;
            }
        }

        synchronized (lock_) {
            scriptIoMap_.get(idx).putIfAbsent(address, new LadderIo(address));
            scriptIoMap_.get(idx).get(address).setValue(value);
            scriptIoMap_.get(idx).get(address).setCycled(false);
        }
    }

    @Override
    public boolean connectLadder() {
        LadderPane pane;
        int index;

        // check connect
        if (checkConnectLadder(tabLadder, treeTableIo, ioMap_)) {
            synchronized (lock_) {
                // connect
                laddersSize_ = tabLadder.getTabs().size();
                ladders_ = new Ladder[laddersSize_];
                for (index = 0; index < laddersSize_; index++) {
                    pane = (LadderPane) ((ScrollPane) tabLadder.getTabs().get(index).getContent()).getContent();
                    ladders_[index] = pane.getLadder().copy();
                    ladders_[index].connectLadder(ioMap_, treeTableIo);
                }
            }
            register(webEngine_, state_);
            return true;
        }
        return false;
    }

    /**
     *
     * @param tabPane
     * @param ioTreeTable
     * @param ioMap
     * @return
     */
    public boolean checkConnectLadder(TabPane tabPane, TreeTableView<LadderTreeTableIo> ioTreeTable, CopyOnWriteArrayList<ConcurrentHashMap<String, LadderIo>> ioMap) {
        LadderPane pane;
        int index, size;

        for (index = 0, size = tabPane.getTabs().size(); index < size; index++) {
            pane = (LadderPane) ((ScrollPane) tabPane.getTabs().get(index).getContent()).getContent();
            if (pane.getLadder().connectLadder(ioMap, ioTreeTable)) {
                pane.clearEditing();
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean runStartLadder() {
        if (!isCycling_ && (ladders_ != null) && (!ioMap_.isEmpty())) {
            isCycling_ = true;
            if (Platform.isFxApplicationThread()) {
                startLadder();
            } else {
                Platform.runLater(() -> {
                    startLadder();
                });
            }
            return true;
        }
        return false;
    }

    private void startLadder() {
        if (getState() == State.READY) {
            start();
        } else {
            restart();
        }
    }

    @Override
    public void stopLadder() {
        if (isCycling_) {
            isCycling_ = false;
            try {
                TimeUnit.NANOSECONDS.sleep(idealCycleTime_ + LADDER_VIEW_REFRESH_CYCLE_TIME);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void fileNotFound(Path file) {
        if (file != null) {
            writeLog(LadderEnums.FILE_NOT_FOUND.toString() + ": " + file.toString(), true);
        } else {
            writeLog(LadderEnums.FILE_NOT_FOUND.toString(), true);
        }
    }

    /**
     *
     * @return
     */
    public ButtonType fileSavedCheck() {
        ButtonType response = null;

        if (isChanged_) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setTitle(LadderEnums.LADDER.toString());
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContentText(LadderEnums.SAVE_CHANGE_PROGRAM.toString());
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            response = result.get();
        }
        return response;
    }

    @Override
    public boolean fileNew() {
        ButtonType response = fileSavedCheck();

        if (response == null) {
            ladderNew();
            filePath_ = null;
            isChanged_ = false;
            setTitle();
            return true;
        } else if (response == ButtonType.YES) {
            ladderSave();
            ladderNew();
            filePath_ = null;
            isChanged_ = false;
            setTitle();
            return true;
        } else if (response == ButtonType.NO) {
            ladderNew();
            filePath_ = null;
            isChanged_ = false;
            setTitle();
            return true;
        }
        return false;
    }

    @Override
    public boolean fileOpen(Path filePath) {
        Path file;
        ButtonType response = fileSavedCheck();

        if (response == null) {
            file = ladderOpen(tabLadder, treeTableIo, filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound(filePath);
                }
            } else {
                filePath_ = file;
                isChanged_ = false;
                setTitle();
                return true;
            }
        } else if (response == ButtonType.YES) {
            ladderSave();
            file = ladderOpen(tabLadder, treeTableIo, filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound(filePath);
                }
            } else {
                filePath_ = file;
                isChanged_ = false;
                setTitle();
                return true;
            }
        } else if (response == ButtonType.NO) {
            file = ladderOpen(tabLadder, treeTableIo, filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound(filePath);
                }
            } else {
                filePath_ = file;
                isChanged_ = false;
                setTitle();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean fileSave() {
        Path file = ladderSave();

        if (file != null) {
            filePath_ = file;
            isChanged_ = false;
            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean fileSaveAs() {
        Path file = ladderSaveAs();

        if (file != null) {
            filePath_ = file;
            isChanged_ = false;
            setTitle();
            return true;
        }
        return false;
    }

    @Override
    public String getFileName() {
        if (filePath_ == null) {
            return null;
        }
        if (Files.isDirectory(filePath_)) {
            return null;
        }
        return filePath_.getFileName().toString();
    }

    /**
     *
     * @return
     */
    public Path getFilePath() {
        return filePath_;
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(LadderEnums.LADDERS.toString(), msg, err);
    }
}
