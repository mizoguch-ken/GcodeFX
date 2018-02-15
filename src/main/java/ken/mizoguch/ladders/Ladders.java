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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ken.mizoguch.console.Console;
import ken.mizoguch.soem.Soem;
import ken.mizoguch.webviewer.plugin.gcodefx.LaddersPlugin;
import ken.mizoguch.webviewer.plugin.WebViewerPlugin;
import netscape.javascript.JSException;

/**
 *
 * @author mizoguch-ken
 */
public class Ladders extends Service<Void> implements LaddersPlugin {

    private WebViewerPlugin webViewer_;
    private static final String FUNCTION_NAME = "ladder";

    public static final long LADDER_VIEW_REFRESH_CYCLE_TIME = TimeUnit.MILLISECONDS.toNanos(100);
    public static final int LADDER_DEFAULT_GRID_COLUMN = 10;
    public static final int LADDER_DEFAULT_GRID_ROW = 10;
    public static final double LADDER_DEFAULT_GRID_MIN_SIZE = 78.912;
    public static final double LADDER_DEFAULT_GRID_MAX_SIZE = LADDER_DEFAULT_GRID_MIN_SIZE * 122.966;
    public static final double LADDER_DEFAULT_GRID_CONTENTS_WIDTH = LADDER_DEFAULT_GRID_MIN_SIZE * 0.382;
    public static final double LADDER_DEFAULT_GRID_CONTENTS_HIGHT = LADDER_DEFAULT_GRID_CONTENTS_WIDTH * 0.618;

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
    private final TableView<LadderTableIo> tableIo;

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
    private int laddersSize_, scriptIndex_, scriptSize_;
    private final ConcurrentHashMap<String, LadderIo> ioMap_;
    private final ConcurrentHashMap<String, String> commentMap_;
    private final ConcurrentHashMap<String, LadderIo> scriptIoMap_;
    private long idealCycleTime_, cycleTime_, minCycleTime_, maxCycleTime_, cumulativeCycleTime_, cumulativeCycleTimeCount_;
    private boolean isCycling_, isChanged_;

    private final Gson gson_ = new GsonBuilder().setPrettyPrinting().create();

    /**
     *
     * @param ladderController
     */
    public Ladders(DesignLaddersController ladderController) {
        ladderController_ = ladderController;
        tabLadder = ladderController_.getTabLadder();
        tableIo = ladderController_.getTableIo();

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
        ioMap_ = new ConcurrentHashMap<>();
        commentMap_ = new ConcurrentHashMap<>();
        scriptIoMap_ = new ConcurrentHashMap<>();
        idealCycleTime_ = 0;
        cycleTime_ = 0;
        minCycleTime_ = 0;
        maxCycleTime_ = 0;
        cumulativeCycleTime_ = 0;
        cumulativeCycleTimeCount_ = 0;
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
    public TableView<LadderTableIo> getTableIo() {
        return tableIo;
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
    public ConcurrentHashMap<String, LadderIo> getIoMap() {
        return ioMap_;
    }

    /**
     *
     * @return
     */
    public ConcurrentHashMap<String, String> getCommentMap() {
        return commentMap_;
    }

    /**
     *
     * @return
     */
    public ConcurrentHashMap<String, LadderIo> getScriptIoMap() {
        return scriptIoMap_;
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
        if (ladderCommand_ != null) {
            ladderCommand_.setHistoryGeneration(historyGeneration_);
        }
    }

    /**
     *
     * @return
     */
    public long getIdealCycleTime() {
        return idealCycleTime_;
    }

    /**
     *
     * @param idealCycleTime
     */
    public void setIdealCycleTime(long idealCycleTime) {
        idealCycleTime_ = idealCycleTime;
    }

    /**
     *
     * @return
     */
    public boolean connectLadder() {
        LadderPane pane;
        int index;

        // check connect
        if (checkConnectLadder(tabLadder, tableIo, ioMap_)) {
            synchronized (lock_) {
                // connect
                laddersSize_ = tabLadder.getTabs().size();
                ladders_ = new Ladder[laddersSize_];
                for (index = 0; index < laddersSize_; index++) {
                    pane = (LadderPane) ((ScrollPane) tabLadder.getTabs().get(index).getContent()).getContent();
                    ladders_[index] = pane.getLadder().copy();
                    ladders_[index].connectLadder(ioMap_, tableIo);
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
     * @param ioTable
     * @param ioMap
     * @return
     */
    public boolean checkConnectLadder(TabPane tabPane, TableView<LadderTableIo> ioTable, ConcurrentHashMap<String, LadderIo> ioMap) {
        LadderPane pane;
        int index, size;

        for (index = 0, size = tabPane.getTabs().size(); index < size; index++) {
            pane = (LadderPane) ((ScrollPane) tabPane.getTabs().get(index).getContent()).getContent();
            if (pane.getLadder().connectLadder(ioMap, ioTable)) {
                pane.clearEditing();
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     *
     */
    public void runStartLadder() {
        if ((ladders_ != null) && (!ioMap_.isEmpty())) {
            isCycling_ = true;
            if (Platform.isFxApplicationThread()) {
                startLadder();
            } else {
                Platform.runLater(() -> {
                    startLadder();
                });
            }
        }
    }

    private void startLadder() {
        if (getState() == State.READY) {
            start();
        } else {
            restart();
        }
    }

    /**
     *
     */
    public void stopLadder() {
        if (isCycling_) {
            isCycling_ = false;
            try {
                TimeUnit.NANOSECONDS.sleep(idealCycleTime_ + LADDER_VIEW_REFRESH_CYCLE_TIME);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     *
     * @param oldAddress
     * @param newAddress
     */
    public void changeAddress(String oldAddress, String newAddress) {
        ladderCommand_.changeAddress(oldAddress, newAddress);
        isChanged_ = true;
        setTitle();
    }

    /**
     *
     * @param address
     * @return
     */
    public boolean isComment(String address) {
        return commentMap_.containsKey(address);
    }

    /**
     *
     * @param address
     * @return
     */
    public String getComment(String address) {
        return commentMap_.get(address);
    }

    /**
     *
     * @param address
     * @param comment
     * @return
     */
    public boolean changeComment(String address, String comment) {
        if (ladderCommand_.changeComment(address, comment)) {
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
        if (!ioMap_.containsKey(address)) {
            ioMap_.put(address, new LadderIo(address));
            tableIo.getItems().add(new LadderTableIo(address, value));
        }

        for (scriptIndex_ = 0, scriptSize_ = tableIo.getItems().size(); scriptIndex_ < scriptSize_; scriptIndex_++) {
            if (address.equals(tableIo.getItems().get(scriptIndex_).getAddress())) {
                tableIo.getItems().get(scriptIndex_).setValue(value);
                break;
            }
        }

        synchronized (lock_) {
            ioMap_.get(address).setValue(value);
            scriptIoMap_.putIfAbsent(address, new LadderIo(address));
            scriptIoMap_.get(address).setValue(value);
            scriptIoMap_.get(address).setCycled(true);
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
        tableIo.getItems().clear();
        ladderCommand_.clearHistoryManager();
        ladders_ = null;
        ioMap_.clear();
        commentMap_.clear();
        scriptIoMap_.clear();
    }

    private void fileNotFound(Path file) {
        if (file != null) {
            webViewer_.write(FUNCTION_NAME, LadderEnums.FILE_NOT_FOUND.toString() + ": " + file.toString(), true);
        } else {
            webViewer_.write(FUNCTION_NAME, LadderEnums.FILE_NOT_FOUND.toString(), true);
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

    /**
     *
     * @return
     */
    public Path getFilePath() {
        return filePath_;
    }

    /**
     *
     * @return
     */
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
     * @return
     */
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

    /**
     *
     * @param filePath
     * @return
     */
    public boolean fileOpen(Path filePath) {
        Path file;
        ButtonType response = fileSavedCheck();

        if (response == null) {
            file = ladderOpen(tabLadder, tableIo, filePath);
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
            file = ladderOpen(tabLadder, tableIo, filePath);
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
            file = ladderOpen(tabLadder, tableIo, filePath);
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

    /**
     *
     * @return
     */
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
            setTitle();
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void ladderNew() {
        allClear();
        ladderCommand_.ladderCreate(0, LadderEnums.LADDER.toString(), Ladders.LADDER_DEFAULT_GRID_COLUMN, Ladders.LADDER_DEFAULT_GRID_ROW, Ladders.LADDER_DEFAULT_GRID_MIN_SIZE, Ladders.LADDER_DEFAULT_GRID_MAX_SIZE, Ladders.LADDER_DEFAULT_GRID_CONTENTS_WIDTH, Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT);
    }

    /**
     *
     * @return
     */
    public boolean ladderNewTab() {
        StringBuilder name = new StringBuilder(LadderEnums.LADDER.toString());
        int index, size;

        for (index = 0, size = tabLadder.getTabs().size(); index < size; index++) {
            if (checkTabName(name.toString())) {
                break;
            }
            name.delete(0, name.length());
            name.append(LadderEnums.LADDER.toString()).append(" ").append(index + 1);
        }
        LadderPane pane = ladderCommand_.ladderCreate(tabLadder.getTabs().size(), name.toString(), Ladders.LADDER_DEFAULT_GRID_COLUMN, Ladders.LADDER_DEFAULT_GRID_ROW, Ladders.LADDER_DEFAULT_GRID_MIN_SIZE, Ladders.LADDER_DEFAULT_GRID_MAX_SIZE, Ladders.LADDER_DEFAULT_GRID_CONTENTS_WIDTH, Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT);
        tabLadder.getSelectionModel().select(pane.getLadder().getIndex());
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
                pane.setChanged(true);
                ladderCommand_.ladderChangeName(pane, name);
                isChanged_ = true;

                setTitle();
                return true;
            } else {
                webViewer_.write(FUNCTION_NAME, LadderEnums.DUPLICATE_NAMES_ERROR.toString(), true);
            }
        }
        return false;
    }

    private boolean checkTabName(String name) {
        if (name != null) {
            int index, size;

            for (index = 0, size = tabLadder.getTabs().size(); index < size; index++) {
                if (((LadderPane) ((ScrollPane) tabLadder.getTabs().get(index).getContent()).getContent()).getLadder().getName().equals(name)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Path ladderOpen(TabPane tabPane, TableView<LadderTableIo> tableView, Path file) {
        return ladderOpen(tabPane, tableView, ioMap_, commentMap_, file);
    }

    private Path ladderOpen(TabPane tabPane, TableView<LadderTableIo> tableView, ConcurrentHashMap<String, LadderIo> ioMap, ConcurrentHashMap<String, String> commentMap, Path file) {
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
            if (ladderJsonLoad(this, tabPane, tableView, ioMap, commentMap, ladderJson)) {
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
     * @param ladderController
     * @param tabPane
     * @param tableView
     * @param ioMap
     * @param commentMap
     * @param ladderJson
     * @return
     */
    public boolean ladderJsonLoad(Ladders ladderController, TabPane tabPane, TableView<LadderTableIo> tableView, ConcurrentHashMap<String, LadderIo> ioMap, ConcurrentHashMap<String, String> commentMap, LadderJson ladderJson) {
        ladderCommand_.setDisableHistory(true);
        try {
            // ladder
            if (!ladderCommand_.restoreLadders(ladderController, tabPane, ladderJson, false)) {
                return false;
            }

            // comments
            if (!ladderCommand_.restoreComments(tabPane, tableView, ioMap, commentMap, ladderJson)) {
                return false;
            }

            // history
            if (ladderController != null) {
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

    private LadderJson ladderJsonSave(TabPane tabPane, ConcurrentHashMap<String, LadderIo> ioMap, ConcurrentHashMap<String, String> commentMap) {
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
        ladderCommand_.ladderMoveLeft(ladder);
        isChanged_ = true;

        setTitle();
        return true;
    }

    /**
     *
     * @param ladder
     * @return
     */
    public boolean ladderMoveRight(Ladder ladder) {
        ladderCommand_.ladderMoveRight(ladder);
        isChanged_ = true;

        setTitle();
        return true;
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
                        ladderMoveLeft(pane.getLadder());
                        return true;
                    }
                    break;
                case RIGHT:
                    if (!event.isShiftDown() && !event.isShortcutDown() && event.isAltDown()) {
                        ladderMoveRight(pane.getLadder());
                        return true;
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

        ladderPath = Paths.get(ladderPath.toUri().resolve("ladder.json"));
        if (Files.exists(ladderPath)) {
            // load and start
            if (ladderOpen(tabLadder, tableIo, ioMap_, commentMap_, ladderPath) != null) {
                if (connectLadder()) {
                    runStartLadder();
                }
                filePath_ = ladderPath;

                setTitle();
                return true;
            }
        } else {
            // create
            ladderNew();
        }
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
                    Integer registerSoemValue;
                    long nanoTime, nanoTimeOld;
                    int index, size;

                    ladderController_.runViewRunning(true);

                    minCycleTime_ = Long.MAX_VALUE;
                    maxCycleTime_ = 0;
                    cumulativeCycleTime_ = 0;
                    cumulativeCycleTimeCount_ = 0;

                    nanoTimeOld = System.nanoTime();
                    while (isCycling_) {
                        synchronized (lock_) {
                            // cycletimme
                            nanoTime = System.nanoTime();
                            cycleTime_ = nanoTime - nanoTimeOld;
                            if ((cycleTime_ > 0) && (cycleTime_ < minCycleTime_)) {
                                minCycleTime_ = cycleTime_;
                                ladderController_.setMinCycleTime(minCycleTime_);
                            }
                            if (cycleTime_ > maxCycleTime_) {
                                maxCycleTime_ = cycleTime_;
                                ladderController_.setMaxCycleTime(maxCycleTime_);
                            }
                            nanoTimeOld = nanoTime;

                            // soem
                            if (soem_ != null) {
                                for (index = 0, size = registerSoemOut_.size(); index < size; index++) {
                                    registerSoemIo = registerSoemOut_.get(index);
                                    soem_.out(registerSoemIo.getSlave(), registerSoemIo.getBitsOffset(), registerSoemIo.getBitsMask(), (int) ioMap_.get(registerSoemIo.getAddress()).getValue());
                                }
                                for (index = 0, size = registerSoemIn_.size(); index < size; index++) {
                                    registerSoemIo = registerSoemIn_.get(index);
                                    registerSoemValue = soem_.in(registerSoemIo.getSlave(), registerSoemIo.getBitsOffset(), registerSoemIo.getBitsMask());
                                    if (registerSoemValue != null) {
                                        ioMap_.get(registerSoemIo.getAddress()).setValue(registerSoemValue);
                                    }
                                }
                            }

                            // script io
                            if (!scriptIoMap_.isEmpty()) {
                                for (Iterator<Map.Entry<String, LadderIo>> iterator = scriptIoMap_.entrySet().iterator(); iterator.hasNext();) {
                                    entry = iterator.next();
                                    if (entry.getValue().isCycled()) {
                                        ioMap_.get(entry.getKey()).chehckEdge();
                                        iterator.remove();
                                    } else {
                                        ioMap_.get(entry.getKey()).setValue(entry.getValue().getValue());
                                        entry.getValue().setCycled(true);
                                    }
                                }
                            }

                            // refresh view
                            cumulativeCycleTime_ += cycleTime_;
                            cumulativeCycleTimeCount_++;
                            if (cumulativeCycleTime_ > Ladders.LADDER_VIEW_REFRESH_CYCLE_TIME) {
                                ladderController_.refreshLadder(cumulativeCycleTime_, cumulativeCycleTimeCount_);
                                cumulativeCycleTime_ = 0;
                                cumulativeCycleTimeCount_ = 0;
                            }

                            // ladder
                            for (index = 0; index < laddersSize_; index++) {
                                ladders_[index].run(ioMap_, cycleTime_);
                            }
                        }

                        // ideal cycletime
                        if (idealCycleTime_ > cycleTime_) {
                            try {
                                TimeUnit.NANOSECONDS.sleep(idealCycleTime_ - cycleTime_);
                            } catch (InterruptedException ex) {
                            }
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
            ioMap_.entrySet().forEach((entry) -> {
                entry.getValue().setWebEngineState(webEngine_, state_);
            });
        }
    }

    @Override
    public Boolean registerIn(Object... objects) {
        if (objects != null) {
            switch (objects.length) {
                case 5:
                    if ((objects[0] instanceof Soem) && (objects[1] instanceof String) && (objects[2] instanceof Integer) && (objects[3] instanceof Long) && (objects[4] instanceof Integer)) {
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
                        if (!address.contains(" ")) {
                            if (!ioMap_.containsKey(address)) {
                                ioMap_.put(address, new LadderIo(address));
                                tableIo.getItems().add(new LadderTableIo(address));
                            }

                            int slave = (int) objects[2];
                            long bitsOffset = (long) objects[3];
                            int bitsMask = (int) objects[4];
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
                    if ((objects[0] instanceof Soem) && (objects[1] instanceof String) && (objects[2] instanceof Integer) && (objects[3] instanceof Long) && (objects[4] instanceof Integer)) {
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
                        if (!address.contains(" ")) {
                            if (!ioMap_.containsKey(address)) {
                                ioMap_.put(address, new LadderIo(address));
                                tableIo.getItems().add(new LadderTableIo(address));
                            }

                            int slave = (int) objects[2];
                            long bitsOffset = (long) objects[3];
                            int bitsMask = (int) objects[4];
                            for (int index = 0, size = registerSoemOut_.size(); index < size; index++) {
                                if (registerSoemOut_.get(index).getAddress().equals(address)) {
                                    if ((registerSoemOut_.get(index).getSlave() != slave) || (registerSoemOut_.get(index).getBitsOffset() != bitsOffset) || (registerSoemOut_.get(index).getBitsMask() != bitsMask)) {
                                        registerSoemOut_.remove(index);
                                        registerSoemOut_.add(new LadderRegisterSoemIo(address, slave, bitsOffset, bitsMask));
                                    }
                                    return true;
                                }
                            }
                            registerSoemOut_.add(new LadderRegisterSoemIo(address, (int) objects[2], (long) objects[3], (int) objects[4]));
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
        if (ioMap_.containsKey(address)) {
            return ioMap_.get(address).getValue();
        }
        return null;
    }

    @Override
    public void setValue(String address, double value) {
        if (!ioMap_.containsKey(address)) {
            ioMap_.put(address, new LadderIo(address));
            tableIo.getItems().add(new LadderTableIo(address, value));
        }

        for (scriptIndex_ = 0, scriptSize_ = tableIo.getItems().size(); scriptIndex_ < scriptSize_; scriptIndex_++) {
            if (address.equals(tableIo.getItems().get(scriptIndex_).getAddress())) {
                tableIo.getItems().get(scriptIndex_).setValue(value);
                break;
            }
        }

        synchronized (lock_) {
            scriptIoMap_.putIfAbsent(address, new LadderIo(address));
            scriptIoMap_.get(address).setValue(value);
            scriptIoMap_.get(address).setCycled(false);
        }
    }
}
