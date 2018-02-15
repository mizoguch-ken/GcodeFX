/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeInterpreter;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVirtualMachine;
import ken.mizoguch.webviewer.plugin.gcodefx.WebEditorPluginListener;
import ken.mizoguch.gcodefx.webeditor.WebEditor;
import ken.mizoguch.gcodefx.webeditor.WebEditorListener;
import ken.mizoguch.ladders.Ladders;
import ken.mizoguch.soem.Soem;

/**
 *
 * @author mizoguch-ken
 */
public class DesignEditorController implements Initializable, WebEditorListener, WebEditorPluginListener {

    // root
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
    // program
    @FXML
    private WebView webView;

    private Stage stage_;
    private List<Image> icons_;
    private WebEditor webEditor_;
    private Path webDragboardPath_;
    private boolean isExit_;

    private GcodeVirtualMachine gcodeVirtualMachine_;
    private GcodeInterpreter gcodeInterpreter_;
    private DesignController.StageSettings stageSettings_;
    private DesignController.BaseSettings baseSettings_;
    private DesignController.VirtualMachineSettings virtualMachineSettings_;
    private Ladders ladders_;
    private Soem soem_;

    private void addEventDesign() {
        // root
        stage_.setOnCloseRequest((WindowEvent event) -> {
            if (!cleanUp()) {
                event.consume();
            }
        });
        paneRoot.setOnKeyPressed((KeyEvent event) -> {
            event.consume();
        });

        // menu file
        menuFileNew.setOnAction((ActionEvent event) -> {
            fileNew();
        });
        menuFileOpen.setOnAction((ActionEvent event) -> {
            fileOpen(null);
        });
        menuFileSave.setOnAction((ActionEvent event) -> {
            webEditor_.fileSave();
        });
        menuFileSaveAs.setOnAction((ActionEvent event) -> {
            webEditor_.fileSaveAs();
        });
        menuFileRecentFile1.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile1.getText()));
        });
        menuFileRecentFile2.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile2.getText()));
        });
        menuFileRecentFile3.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile3.getText()));
        });
        menuFileRecentFile4.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile4.getText()));
        });
        menuFileRecentFile5.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile5.getText()));
        });
        menuFileRecentFile6.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile6.getText()));
        });
        menuFileRecentFile7.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile7.getText()));
        });
        menuFileRecentFile8.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile8.getText()));
        });
        menuFileRecentFile9.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile9.getText()));
        });
        menuFileRecentFile10.setOnAction((ActionEvent event) -> {
            fileOpen(Paths.get(menuFileRecentFile10.getText()));
        });
        // webview
        webView.setOnDragDetected((MouseEvent event) -> {
            event.consume();
        });
        webView.setOnDragOver((DragEvent event) -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });
        webView.setOnDragDropped((DragEvent event) -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            webDragboardPath_ = null;
            if (dragboard.hasString()) {
                try {
                    webDragboardPath_ = Paths.get(new URL(dragboard.getString().replace("file:///", "file://///")).toURI());
                    success = true;
                } catch (URISyntaxException | MalformedURLException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                }
            } else if (dragboard.hasHtml()) {
            } else if (dragboard.hasRtf()) {
            } else if (dragboard.hasUrl()) {
                try {
                    webDragboardPath_ = Paths.get(new URL(dragboard.getUrl().replace("file:///", "file://///")).toURI());
                    success = true;
                } catch (URISyntaxException | MalformedURLException ex) {
                    Console.writeStackTrace(DesignEnums.EDITOR.toString(), ex);
                }
            } else if (dragboard.hasImage()) {
            } else if (dragboard.hasFiles()) {
                if (dragboard.getFiles().size() == 1) {
                    webDragboardPath_ = dragboard.getFiles().get(0).getAbsoluteFile().toPath();
                    success = true;
                } else {
                    writeLog(DesignEnums.MULTIPLE_SELECTION_DISABLED.toString(), false);
                }
            }

            if (webDragboardPath_ != null) {
                fileOpen(webDragboardPath_);
            }
            event.setDropCompleted(success);
            event.consume();
        });
        webView.setOnDragDone((DragEvent event) -> {
            event.consume();
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
        menuFile.setText(DesignEnums.FILE.toString());
        menuFileNew.setText(DesignEnums.FILE_NEW.toString());
        menuFileOpen.setText(DesignEnums.FILE_OPEN.toString());
        menuFileSave.setText(DesignEnums.FILE_SAVE.toString());
        menuFileSaveAs.setText(DesignEnums.FILE_SAVE_AS.toString());
    }

    private void addRecentFile(String filename) {
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

                runViewRecentFile(true);
            }
        }
    }

    private void runViewRecentFile(boolean bln) {
        if (Platform.isFxApplicationThread()) {
            viewRecentFile(bln);
        } else {
            Platform.runLater(() -> {
                viewRecentFile(bln);
            });
        }
    }

    private void viewRecentFile(boolean bln) {
        if (bln) {
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
        } else {
            menuFileRecentFile1.setVisible(false);
            menuFileRecentFile2.setVisible(false);
            menuFileRecentFile3.setVisible(false);
            menuFileRecentFile4.setVisible(false);
            menuFileRecentFile5.setVisible(false);
            menuFileRecentFile6.setVisible(false);
            menuFileRecentFile7.setVisible(false);
            menuFileRecentFile8.setVisible(false);
            menuFileRecentFile9.setVisible(false);
            menuFileRecentFile10.setVisible(false);
        }
    }

    private void fileNotFound() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (!icons_.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
        }
        alert.setTitle(DesignEnums.GCODE_FX.toString());
        alert.getDialogPane().setHeaderText(null);
        alert.getDialogPane().setContentText(DesignEnums.FILE_NOT_FOUND.toString());
        alert.showAndWait();
    }

    private ButtonType fileSavedCheck() {
        ButtonType response = null;
        if (!webEditor_.undoManager_isClean()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setTitle(DesignEnums.GCODE_FX.toString());
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContentText(DesignEnums.SAVE_CHANGE_PROGRAM.toString());
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            response = result.get();
        }
        return response;
    }

    private void fileNew() {
        ButtonType response = fileSavedCheck();
        if (response == null) {
            webEditor_.fileNew();
        } else if (response == ButtonType.YES) {
            webEditor_.fileSave();
            webEditor_.fileNew();
        } else if (response == ButtonType.NO) {
            webEditor_.fileNew();
        }
    }

    private void fileOpen(Path filePath) {
        Path file;
        ButtonType response = fileSavedCheck();
        if (response == null) {
            file = webEditor_.fileOpen(filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound();
                }
            }
        } else if (response == ButtonType.YES) {
            webEditor_.fileSave();
            file = webEditor_.fileOpen(filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound();
                }
            }
        } else if (response == ButtonType.NO) {
            file = webEditor_.fileOpen(filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound();
                }
            }
        }
    }

    /**
     *
     * @param virtualMachine
     */
    public void setGcodeVirtualMachine(GcodeVirtualMachine virtualMachine) {
        gcodeVirtualMachine_ = virtualMachine;
    }

    /**
     *
     * @param interpreter
     */
    public void setGcodeInterpreter(GcodeInterpreter interpreter) {
        gcodeInterpreter_ = interpreter;
    }

    /**
     *
     * @param stageSettings
     */
    public void setStageSettings(DesignController.StageSettings stageSettings) {
        stageSettings_ = stageSettings;
    }

    /**
     *
     * @param baseSettings
     */
    public void setBaseSettings(DesignController.BaseSettings baseSettings) {
        baseSettings_ = baseSettings;
    }

    /**
     *
     * @param virtualMachineSettings
     */
    public void setVirtualMachineSettings(DesignController.VirtualMachineSettings virtualMachineSettings) {
        virtualMachineSettings_ = virtualMachineSettings;
    }

    /**
     *
     * @param ladders
     */
    public void setLadders(Ladders ladders) {
        ladders_ = ladders;
    }

    /**
     *
     * @param soem
     */
    public void setSoem(Soem soem) {
        soem_ = soem;
    }

    /**
     *
     * @param stage
     * @param icons
     * @param currentPath
     * @param webPath
     * @param recentFiles
     * @param startFile
     */
    public void startUp(Stage stage, List<Image> icons, Path currentPath, Path webPath, List<String> recentFiles, Path startFile) {
        stage_ = stage;
        icons_ = icons;

        menuFileRecentFile1.setText(recentFiles.get(0));
        menuFileRecentFile2.setText(recentFiles.get(1));
        menuFileRecentFile3.setText(recentFiles.get(2));
        menuFileRecentFile4.setText(recentFiles.get(3));
        menuFileRecentFile5.setText(recentFiles.get(4));
        menuFileRecentFile6.setText(recentFiles.get(5));
        menuFileRecentFile7.setText(recentFiles.get(6));
        menuFileRecentFile8.setText(recentFiles.get(7));
        menuFileRecentFile9.setText(recentFiles.get(8));
        menuFileRecentFile10.setText(recentFiles.get(9));

        addEventDesign();
        runInitDesign();
        runViewRecentFile(true);

        // editor
        webEditor_.setGcodeVirtualMachine(gcodeVirtualMachine_);
        webEditor_.setGcodeInterpreter(gcodeInterpreter_);
        webEditor_.setStageSettings(stageSettings_);
        webEditor_.setBaseSettings(baseSettings_);
        webEditor_.setVirtualMachineSettings(virtualMachineSettings_);
        webEditor_.setLadders(ladders_);
        webEditor_.setSoem(soem_);
        webEditor_.startUp(stage_, icons_, currentPath, webPath, startFile);
    }

    /**
     *
     * @return
     */
    public boolean cleanUp() {
        try {
            if (!isExit_) {
                ButtonType response = fileSavedCheck();
                if (response == ButtonType.YES) {
                    webEditor_.fileSave();
                } else if ((response == ButtonType.CANCEL) || (response == ButtonType.CLOSE)) {
                    return false;
                }

                webEditor_.removeWebEditorListener(this);
                webEditor_.cleanUp();

                isExit_ = true;
            }
        } catch (NoClassDefFoundError ex) {
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEditor_ = new WebEditor(webView);
        webEditor_.addWebEditorListener(this);
        isExit_ = false;
    }

    @Override
    public void notifyChangedWebEditor(String getValue, boolean isClean) {
        webEditor_.update(getValue, isClean, false);
    }

    @Override
    public void execFileNewWebEditor() {
        fileNew();
    }

    @Override
    public void execFileOpenWebEditor() {
        fileOpen(null);
    }

    @Override
    public void execFileSaveWebEditor() {
        webEditor_.fileSave();
    }

    @Override
    public void execFileSaveAsWebEditor() {
        webEditor_.fileSaveAs();
    }

    @Override
    public void execNextGenerationController() {
    }

    @Override
    public void notifyFileNew() {
    }

    @Override
    public void notifyFileOpen(String filename) {
        addRecentFile(filename);
    }

    @Override
    public void notifyFileSave(String filename) {
        addRecentFile(filename);
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(DesignEnums.EDITOR.toString(), msg, err);
    }

}
