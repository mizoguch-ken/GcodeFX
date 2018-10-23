/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.webeditor.WebEditor;
import ken.mizoguch.gcodefx.webeditor.WebEditorEnums;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeInterpreter;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeInterpreterEnums;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVirtualMachine;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVirtualMachineEnums;
import ken.mizoguch.webviewer.plugin.gcodefx.BaseSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeVirtualMachinePluginListener;
import ken.mizoguch.webviewer.plugin.gcodefx.StageSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.VirtualMachineSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.WebEditorPluginListener;
import ken.mizoguch.gcodefx.serial.Serial;
import ken.mizoguch.gcodefx.serial.SerialEnums;
import ken.mizoguch.gcodefx.serial.SerialListener;
import ken.mizoguch.gcodeparser.GcodeParser;
import ken.mizoguch.webviewer.DesignWebController;
import ken.mizoguch.webviewer.WebEnums;
import ken.mizoguch.gcodefx.webeditor.WebEditorListener;
import ken.mizoguch.ladders.DesignLaddersController;
import ken.mizoguch.ladders.LadderEnums;
import ken.mizoguch.soem.Soem;

/**
 *
 * @author mizoguch-ken
 */
public class DesignController implements Initializable, GcodeVirtualMachinePluginListener, WebEditorListener, WebEditorPluginListener, SerialListener {

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
    private MenuItem menuFileExportPdf;
    @FXML
    private MenuItem menuFileNextGenerationController;
    @FXML
    private MenuItem menuFileGcodeByteCode;
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
    // menu view
    @FXML
    private Menu menuView;
    @FXML
    private MenuItem menuViewControlPanel;
    @FXML
    private MenuItem menuViewConsole;
    // menu extension
    @FXML
    private Menu menuExtension;
    @FXML
    private MenuItem menuExtensionWeb;
    @FXML
    private MenuItem menuExtensionLadder;
    // menu settings
    @FXML
    private Menu menuSettings;
    @FXML
    private MenuItem menuSettingsVirtualMachine;
    // menu help
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem menuHelpLanguage;
    @FXML
    private MenuItem menuHelpShowLicenses;
    @FXML
    private MenuItem menuHelpShowVersion;
    // control panel
    @FXML
    private AnchorPane anchorControlPanel;
    @FXML
    private TextField txtProgramNumber;
    @FXML
    private CheckBox chkOptionalSkip0;
    @FXML
    private CheckBox chkOptionalSkip2;
    @FXML
    private CheckBox chkOptionalSkip3;
    @FXML
    private CheckBox chkOptionalSkip4;
    @FXML
    private CheckBox chkOptionalSkip5;
    @FXML
    private CheckBox chkOptionalSkip6;
    @FXML
    private CheckBox chkOptionalSkip7;
    @FXML
    private CheckBox chkOptionalSkip8;
    @FXML
    private CheckBox chkOptionalSkip9;
    @FXML
    private Button btnAddEditor;
    @FXML
    private TextField txtVirtualMachineName;
    @FXML
    private Button btnVirtualMachineLoad;
    @FXML
    private TextField txtSerialPort;
    @FXML
    private Button btnSerialOpen;
    @FXML
    private Button btnSerialClose;
    @FXML
    private SplitPane splitWebviewConsole;
    @FXML
    private WebView webView;
    @FXML
    private TextArea txtConsole;

    public static final int LADDER_N1 = 0, LADDER_N2 = 1, LADDER_G1 = 2, LADDER_G2 = 3,
            LADDER_X1 = 4, LADDER_X2 = 5, LADDER_Y1 = 6, LADDER_Y2 = 7, LADDER_Z1 = 8, LADDER_Z2 = 9,
            LADDER_A1 = 10, LADDER_A2 = 11, LADDER_B1 = 12, LADDER_B2 = 13, LADDER_C1 = 14, LADDER_C2 = 15,
            LADDER_U1 = 16, LADDER_U2 = 17, LADDER_V1 = 18, LADDER_V2 = 19, LADDER_W1 = 20, LADDER_W2 = 21,
            LADDER_M1 = 22, LADDER_M2 = 23, LADDER_R1 = 24, LADDER_R2 = 25,
            LADDER_I1 = 26, LADDER_I2 = 27, LADDER_J1 = 28, LADDER_J2 = 29, LADDER_K1 = 30, LADDER_K2 = 31,
            LADDER_F1 = 32, LADDER_F2 = 33, LADDER_S1 = 34, LADDER_S2 = 35, LADDER_T1 = 36, LADDER_T2 = 37;

    // stage settings
    public class StageSettings implements StageSettingsPlugin {

        private boolean bExit;
        private LANG language;
        private boolean stageMaximized, viewControlPanel, viewConsole;
        private double stageWidth, stageHeight, rootWidth, rootHeight, programConsoleDividerPositions;
        private Path startFile;

        public boolean isExit() {
            return bExit;
        }

        private void setExit(boolean bln) {
            bExit = bln;
        }

        public LANG getLanguage() {
            return language;
        }

        private void setLanguage(LANG lang, boolean update) {
            language = lang;
            if (update) {
                for (DesignEnums enums : DesignEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (GcodeVirtualMachineEnums enums : GcodeVirtualMachineEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (GcodeInterpreterEnums enums : GcodeInterpreterEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (SerialEnums enums : SerialEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (WebEnums enums : WebEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (WebEditorEnums enums : WebEditorEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                for (LadderEnums enums : LadderEnums.values()) {
                    enums.setLang(language.toInteger());
                }
                if (Platform.isFxApplicationThread()) {
                    webEditor_.setLanguage(language);
                } else {
                    Platform.runLater(() -> {
                        webEditor_.setLanguage(language);
                    });
                }
            }
        }

        @Override
        public boolean isStageMaximized() {
            return stageMaximized;
        }

        @Override
        public void setStageMaximized(boolean bln) {
            setStageMaximized(bln, true);
        }

        public void setStageMaximized(boolean bln, boolean update) {
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

        @Override
        public double getStageWidth() {
            return stageWidth;
        }

        @Override
        public void setStageWidth(double value) {
            setStageWidth(value, true);
        }

        public void setStageWidth(double value, boolean update) {
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

        @Override
        public double getStageHeight() {
            return stageHeight;
        }

        @Override
        public void setStageHeight(double value) {
            setStageHeight(value, true);
        }

        public void setStageHeight(double value, boolean update) {
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

        @Override
        public double getRootWidth() {
            return rootWidth;
        }

        @Override
        public void setRootWidth(double value) {
            setRootWidth(value, true);
        }

        public void setRootWidth(double value, boolean update) {
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

        @Override
        public double getRootHeight() {
            return rootHeight;
        }

        @Override
        public void setRootHeight(double value) {
            setRootHeight(value, true);
        }

        public void setRootHeight(double value, boolean update) {
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

        @Override
        public boolean isViewControlPanel() {
            return viewControlPanel;
        }

        @Override
        public void setViewControlPanel(boolean state) {
            setViewControlPanel(state, true);
        }

        public void setViewControlPanel(boolean state, boolean update) {
            viewControlPanel = state;
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    if (viewControlPanel && !anchorControlPanel.isVisible()) {
                        anchorControlPanel.setVisible(true);
                        anchorControlPanel.setMinHeight(Region.USE_COMPUTED_SIZE);
                        anchorControlPanel.setMaxHeight(Region.USE_COMPUTED_SIZE);
                    } else if (!viewControlPanel && anchorControlPanel.isVisible()) {
                        anchorControlPanel.setVisible(false);
                        anchorControlPanel.setMinHeight(0);
                        anchorControlPanel.setMaxHeight(0);
                    }
                } else {
                    Platform.runLater(() -> {
                        if (viewControlPanel && !anchorControlPanel.isVisible()) {
                            anchorControlPanel.setVisible(true);
                            anchorControlPanel.setMinHeight(Region.USE_COMPUTED_SIZE);
                            anchorControlPanel.setMaxHeight(Region.USE_COMPUTED_SIZE);
                        } else if (!viewControlPanel && anchorControlPanel.isVisible()) {
                            anchorControlPanel.setVisible(false);
                            anchorControlPanel.setMinHeight(0);
                            anchorControlPanel.setMaxHeight(0);
                        }
                    });
                }
            }
        }

        @Override
        public boolean isViewConsole() {
            return viewConsole;
        }

        @Override
        public void setViewConsole(boolean state) {
            setViewConsole(state, true);
        }

        public void setViewConsole(boolean state, boolean update) {
            viewConsole = state;
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    if (viewConsole && !txtConsole.isVisible()) {
                        txtConsole.setVisible(true);
                        txtConsole.setMinHeight(Region.USE_COMPUTED_SIZE);
                        txtConsole.setMaxHeight(Region.USE_COMPUTED_SIZE);
                        splitWebviewConsole.setDividerPositions(stageSettings_.getWebviewConsoleDividerPositions());
                    } else if (!viewConsole && txtConsole.isVisible()) {
                        txtConsole.setVisible(false);
                        txtConsole.setMinHeight(0);
                        txtConsole.setMaxHeight(0);
                        splitWebviewConsole.setDividerPositions(1.0);
                    }
                } else {
                    Platform.runLater(() -> {
                        if (viewConsole && !txtConsole.isVisible()) {
                            txtConsole.setVisible(true);
                            txtConsole.setMinHeight(Region.USE_COMPUTED_SIZE);
                            txtConsole.setMaxHeight(Region.USE_COMPUTED_SIZE);
                            splitWebviewConsole.setDividerPositions(stageSettings_.getWebviewConsoleDividerPositions());
                        } else if (!viewConsole && txtConsole.isVisible()) {
                            txtConsole.setVisible(false);
                            txtConsole.setMinHeight(0);
                            txtConsole.setMaxHeight(0);
                            splitWebviewConsole.setDividerPositions(1.0);
                        }
                    });
                }
            }
        }

        @Override
        public String getConsole() {
            return txtConsole.getText();
        }

        @Override
        public void setConsole(String value) {
            txtConsole.setText(value);
        }

        @Override
        public double getWebviewConsoleDividerPositions() {
            return programConsoleDividerPositions;
        }

        @Override
        public void setWebviewConsoleDividerPositions(double value) {
            setWebviewConsoleDividerPositions(value, true);
        }

        public void setWebviewConsoleDividerPositions(double value, boolean update) {
            programConsoleDividerPositions = value;
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    splitWebviewConsole.getDividers().get(0).setPosition(programConsoleDividerPositions);
                } else {
                    Platform.runLater(() -> {
                        splitWebviewConsole.getDividers().get(0).setPosition(programConsoleDividerPositions);
                    });
                }
            }
        }

        /**
         *
         * @param file
         */
        @Override
        public void addEditor(Path file) {
            startFile = file;
            if (Platform.isFxApplicationThread()) {
                try {
                    Stage stage = new Stage(StageStyle.DECORATED);
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignEditor.fxml"));
                    Parent root = (Parent) loader.load();
                    DesignEditorController controller = (DesignEditorController) loader.getController();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    if (!icons_.isEmpty()) {
                        stage.getIcons().addAll(icons_);
                    }
                    List<String> recentFiles = new ArrayList<>();
                    recentFiles.add(menuFileRecentFile1.getText());
                    recentFiles.add(menuFileRecentFile2.getText());
                    recentFiles.add(menuFileRecentFile3.getText());
                    recentFiles.add(menuFileRecentFile4.getText());
                    recentFiles.add(menuFileRecentFile5.getText());
                    recentFiles.add(menuFileRecentFile6.getText());
                    recentFiles.add(menuFileRecentFile7.getText());
                    recentFiles.add(menuFileRecentFile8.getText());
                    recentFiles.add(menuFileRecentFile9.getText());
                    recentFiles.add(menuFileRecentFile10.getText());
                    controller.setGcodeVirtualMachine(gcodeVirtualMachine_);
                    controller.setGcodeInterpreter(gcodeInterpreter_);
                    controller.setStageSettings(stageSettings_);
                    controller.setBaseSettings(baseSettings_);
                    controller.setVirtualMachineSettings(virtualMachineSettings_);
                    controller.setLadders(laddersController_.getLadders());
                    controller.setSoem(soem_);
                    controller.startUp(stage, icons_, currentPath_, webPath_, recentFiles, startFile);
                    stage.show();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                }
            } else {
                Platform.runLater(() -> {
                    try {
                        Stage stage = new Stage(StageStyle.DECORATED);
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignEditor.fxml"));
                        Parent root = (Parent) loader.load();
                        DesignEditorController controller = (DesignEditorController) loader.getController();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        if (!icons_.isEmpty()) {
                            stage.getIcons().addAll(icons_);
                        }
                        List<String> recentFiles = new ArrayList<>();
                        recentFiles.add(menuFileRecentFile1.getText());
                        recentFiles.add(menuFileRecentFile2.getText());
                        recentFiles.add(menuFileRecentFile3.getText());
                        recentFiles.add(menuFileRecentFile4.getText());
                        recentFiles.add(menuFileRecentFile5.getText());
                        recentFiles.add(menuFileRecentFile6.getText());
                        recentFiles.add(menuFileRecentFile7.getText());
                        recentFiles.add(menuFileRecentFile8.getText());
                        recentFiles.add(menuFileRecentFile9.getText());
                        recentFiles.add(menuFileRecentFile10.getText());
                        controller.setGcodeVirtualMachine(gcodeVirtualMachine_);
                        controller.setGcodeInterpreter(gcodeInterpreter_);
                        controller.setStageSettings(stageSettings_);
                        controller.setBaseSettings(baseSettings_);
                        controller.setVirtualMachineSettings(virtualMachineSettings_);
                        controller.setLadders(laddersController_.getLadders());
                        controller.setSoem(soem_);
                        controller.startUp(stage, icons_, currentPath_, webPath_, recentFiles, startFile);
                        stage.show();
                    } catch (IOException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    }
                });
            }
        }
    }

    // base settings
    public class BaseSettings implements BaseSettingsPlugin {

        private boolean bSerial;
        private boolean bViewSerial;
        private boolean bViewRecentFile;

        private String serialPortValue;
        private Boolean serialCharacterCheck;
        private Boolean serialObserveCTSCheck;
        private Boolean serialObserveDSRCheck;
        private Boolean serialObserveDC2DC4Check;
        private String serialBaudrateValue;
        private String serialDataBitsValue;
        private String serialStopBitsValue;
        private String serialParityValue;
        private String serialEOBValue;
        private String serialBufferLimitValue;
        private String serialDelayValue;
        private String serialStartCodeValue;
        private String serialEndCodeValue;

        @Override
        public boolean isSerial() {
            return bSerial;
        }

        private void setSerial(boolean bln) {
            bSerial = bln;
        }

        @Override
        public boolean isViewSerial() {
            return bViewSerial;
        }

        @Override
        public void setViewSerial(boolean bln) {
            bViewSerial = bln;
        }

        private boolean isViewRecentFile() {
            return bViewRecentFile;
        }

        private void setViewRecentFile(boolean bln) {
            bViewRecentFile = bln;
        }

        @Override
        public String getSerialPortValue() {
            return serialPortValue;
        }

        @Override
        public void setSerialPortValue(String value) {
            setSerialPortValue(value, true);
        }

        public void setSerialPortValue(String value, boolean update) {
            serialPortValue = value;
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    txtSerialPort.setText(serialPortValue);
                } else {
                    Platform.runLater(() -> {
                        txtSerialPort.setText(serialPortValue);
                    });
                }
            }
        }

        @Override
        public boolean actionSerialPortList() {
            ListView<String> lstPort = new ListView<>();
            lstPort.getItems().addAll(serial_.getPort());
            Alert alert = new Alert(Alert.AlertType.NONE);
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setTitle(DesignEnums.SERIAL_PORT_LIST_TIP.toString());
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContent(lstPort);
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (lstPort.getSelectionModel().getSelectedIndex() > -1) {
                    baseSettings_.setSerialPortValue(lstPort.getSelectionModel().getSelectedItem(), true);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void actionSerialOpen() {
            if (!baseSettings_.getSerialPortValue().isEmpty()) {
                serialOpen();
            } else {
                runViewSerial(true);
            }
        }

        @Override
        public void actionSerialClose() {
            serialClose();
        }

        @Override
        public String getSerialBaudrateValue() {
            return serialBaudrateValue;
        }

        @Override
        public void setSerialBaudrateValue(String value) {
            setSerialBaudrateValue(value, true);
        }

        public void setSerialBaudrateValue(String value, boolean update) {
            serialBaudrateValue = value;
        }

        @Override
        public String getSerialDataBitsValue() {
            return serialDataBitsValue;
        }

        @Override
        public void setSerialDataBitsValue(String value) {
            setSerialDataBitsValue(value, true);
        }

        public void setSerialDataBitsValue(String value, boolean update) {
            serialDataBitsValue = value;
        }

        @Override
        public String getSerialStopBitsValue() {
            return serialStopBitsValue;
        }

        @Override
        public void setSerialStopBitsValue(String value) {
            setSerialStopBitsValue(value, true);
        }

        public void setSerialStopBitsValue(String value, boolean update) {
            serialStopBitsValue = value;
        }

        @Override
        public String getSerialParityValue() {
            return serialParityValue;
        }

        @Override
        public void setSerialParityValue(String value) {
            setSerialParityValue(value, true);
        }

        public void setSerialParityValue(String value, boolean update) {
            serialParityValue = value;
        }

        @Override
        public String getSerialEOBValue() {
            return serialEOBValue;
        }

        @Override
        public void setSerialEOBValue(String value) {
            setSerialEOBValue(value, true);
        }

        public void setSerialEOBValue(String value, boolean update) {
            serialEOBValue = value;
            putChange(GcodeVirtualMachine.CHG.SETTINGS, true);
        }

        @Override
        public String getSerialBufferLimitValue() {
            return serialBufferLimitValue;
        }

        @Override
        public void setSerialBufferLimitValue(String value) {
            setSerialBufferLimitValue(value, true);
        }

        public void setSerialBufferLimitValue(String value, boolean update) {
            serialBufferLimitValue = value;
        }

        @Override
        public String getSerialDelayValue() {
            return serialDelayValue;
        }

        @Override
        public void setSerialDelayValue(String value) {
            setSerialDelayValue(value, true);
        }

        public void setSerialDelayValue(String value, boolean update) {
            serialDelayValue = value;
        }

        @Override
        public String getSerialStartCodeValue() {
            return serialStartCodeValue;
        }

        @Override
        public void setSerialStartCodeValue(String value) {
            setSerialStartCodeValue(value, true);
        }

        public void setSerialStartCodeValue(String value, boolean update) {
            serialStartCodeValue = value;
            putChange(GcodeVirtualMachine.CHG.SETTINGS, true);
        }

        @Override
        public String getSerialEndCodeValue() {
            return serialEndCodeValue;
        }

        @Override
        public void setSerialEndCodeValue(String value) {
            setSerialEndCodeValue(value, true);
        }

        public void setSerialEndCodeValue(String value, boolean update) {
            serialEndCodeValue = value;
            putChange(GcodeVirtualMachine.CHG.SETTINGS, true);
        }

        @Override
        public Boolean isSerialCharacterCheck() {
            return serialCharacterCheck;
        }

        @Override
        public void setSerialCharacterCheck(Boolean check) {
            setSerialCharacterCheck(check, true);
        }

        public void setSerialCharacterCheck(Boolean check, boolean update) {
            serialCharacterCheck = check;
        }

        @Override
        public Boolean isSerialObserveCTSCheck() {
            return serialObserveCTSCheck;
        }

        @Override
        public void setSerialObserveCTSCheck(Boolean check) {
            setSerialObserveCTSCheck(check, true);
        }

        public void setSerialObserveCTSCheck(Boolean check, boolean update) {
            serialObserveCTSCheck = check;
        }

        @Override
        public Boolean isSerialObserveDSRCheck() {
            return serialObserveDSRCheck;
        }

        @Override
        public void setSerialObserveDSRCheck(Boolean check) {
            setSerialObserveDSRCheck(check, true);
        }

        public void setSerialObserveDSRCheck(Boolean check, boolean update) {
            serialObserveDSRCheck = check;
        }

        @Override
        public Boolean isSerialObserveDC2DC4Check() {
            return serialObserveDC2DC4Check;
        }

        @Override
        public void setSerialObserveDC2DC4Check(Boolean check) {
            setSerialObserveDC2DC4Check(check, true);
        }

        public void setSerialObserveDC2DC4Check(Boolean check, boolean update) {
            serialObserveDC2DC4Check = check;
        }
    }

    // virtual machine settings
    public class VirtualMachineSettings implements VirtualMachineSettingsPlugin {

        private boolean bRunning;
        private boolean bSettings;
        private boolean bViewRunning;

        private String virtualMachineNameValue;
        private Boolean logCheck;
        private Boolean debugCheck;
        private String programNumberValue;
        private Boolean[] optionalSkipCheck;
        private String[] backGroundFileValue;
        private String externalSubProgramDirectoryValue;
        private String[] gcodeGroupValue;
        private String codeChangeProgramCallValue;
        private String codeChangeGValue;
        private String codeChangeMValue;
        private String codeChangeTValue;
        private String codeChangeDValue;
        private String codeChangeHValue;
        private String macroCallG;
        private String macroCallM;
        private Boolean[] originMachineCheck;
        private String[] originMachineValue;
        private Boolean[] coordinateExtCheck;
        private String[] coordinateExtValue;
        private Boolean[] coordinateG92Check;
        private String[] coordinateG92Value;
        private Boolean coordinateToolCheck;
        private Boolean coordinateMirrorCheck;
        private String[] ladderValue;
        private Boolean[] coordinateG54Check;
        private String[] coordinateG54Value;
        private Boolean[] coordinateG55Check;
        private String[] coordinateG55Value;
        private Boolean[] coordinateG56Check;
        private String[] coordinateG56Value;
        private Boolean[] coordinateG57Check;
        private String[] coordinateG57Value;
        private Boolean[] coordinateG58Check;
        private String[] coordinateG58Value;
        private Boolean[] coordinateG59Check;
        private String[] coordinateG59Value;
        private String toolOffsetValue;
        private String variablesValue;
        private Boolean toolChangeProgramCheck;
        private Boolean toolChangeXYCheck;
        private String toolChangeProgramValue;
        private Boolean toolChangeMCodeCheck;
        private String toolChangeMCodeValue;
        private Boolean skipFunctionProgramCheck;
        private String skipFunctionProgramValue;
        private Boolean optionOptimization;
        private Boolean optionExComment;
        private Boolean optionDisable30033004;
        private Boolean optionReplace3006M0;
        private Boolean optionOnlyS;
        private Boolean optionRS274NGC;
        private Boolean optionDebugJson;
        private String optionMaxFeedRate;
        private String optionMaxRevolution;
        private String optionStartProgram;
        private String optionBlockProgram;
        private String optionEndProgram;

        @Override
        public boolean isRunning() {
            return bRunning;
        }

        private void setRunning(boolean bln) {
            bRunning = bln;
        }

        @Override
        public boolean isSettings() {
            return bSettings;
        }

        private void setSettings(boolean bln) {
            bSettings = bln;
        }

        @Override
        public boolean isViewRunning() {
            return bViewRunning;
        }

        @Override
        public void setViewRunning(boolean bln) {
            bViewRunning = bln;
        }

        @Override
        public String getVirtualMachineNameValue() {
            return virtualMachineNameValue;
        }

        private void setVirtualMachineNameValue(String value, boolean update) {
            virtualMachineNameValue = value;
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    txtVirtualMachineName.setText(virtualMachineNameValue);
                } else {
                    Platform.runLater(() -> {
                        txtVirtualMachineName.setText(virtualMachineNameValue);
                    });
                }
            }
        }

        @Override
        public boolean actionLoadVirtualMachine(Path file) {
            if (file == null) {
                FileChooser fc = new FileChooser();

                if (openFile_ != null) {
                    fc.setInitialDirectory(openFile_.getParent().toFile());
                }
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
                File fcfile = fc.showOpenDialog(stage_);
                if (fcfile != null) {
                    file = fcfile.toPath();
                }
            }
            return loadVirtualMachine(file);
        }

        @Override
        public boolean actionSaveVirtualMachine(Path file) {
            if (file == null) {
                FileChooser fc = new FileChooser();

                if (openFile_ != null) {
                    fc.setInitialDirectory(openFile_.getParent().toFile());
                }
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
                File fcfile = fc.showSaveDialog(stage_);
                if (fcfile != null) {
                    file = fcfile.toPath();
                }
            }
            return saveVirtualMachine(file);
        }

        @Override
        public Boolean isLogCheck() {
            return logCheck;
        }

        @Override
        public void setLogCheck(Boolean check) {
            setLogCheck(check, true);
        }

        public void setLogCheck(Boolean check, boolean update) {
            logCheck = check;
            if (logCheck) {
                if (logPath_ != null) {
                    try {
                        if (!Files.exists(logPath_)) {
                            Files.createDirectories(logPath_);
                        }
                        SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMddHHmmss");
                        Console.setPrintStream(logPath_.resolve("Console_" + sdfFile.format(new Date()) + ".txt"));
                    } catch (IOException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    }
                }
            } else {
                Console.setPrintStream(null);
            }
        }

        @Override
        public Boolean isDebugCheck() {
            return debugCheck;
        }

        @Override
        public void setDebugCheck(Boolean check) {
            setDebugCheck(check, true);
        }

        public void setDebugCheck(Boolean check, boolean update) {
            debugCheck = check;
        }

        @Override
        public String getProgramNumberValue() {
            return programNumberValue;
        }

        @Override
        public void setProgramNumberValue(String value) {
            setProgramNumberValue(value, true);
        }

        public void setProgramNumberValue(String value, boolean update) {
            programNumberValue = value;
            putChange(GcodeVirtualMachine.CHG.PROGRAMNUMBER, true);
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    txtProgramNumber.setText(programNumberValue);
                } else {
                    Platform.runLater(() -> {
                        txtProgramNumber.setText(programNumberValue);
                    });
                }
            }
        }

        @Override
        public Boolean[] getOptionalSkipCheck() {
            return optionalSkipCheck;
        }

        @Override
        public Boolean getOptionalSkipCheck(int index) {
            return optionalSkipCheck[index];
        }

        @Override
        public void setOptionalSkipCheck(Boolean[] checks) {
            setOptionalSkipCheck(checks, true);
        }

        public void setOptionalSkipCheck(Boolean[] checks, boolean update) {
            optionalSkipCheck = checks;
            putChange(GcodeVirtualMachine.CHG.BLOCKSKIP, true);
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    chkOptionalSkip0.setSelected(optionalSkipCheck[0]);
                    chkOptionalSkip2.setSelected(optionalSkipCheck[2]);
                    chkOptionalSkip3.setSelected(optionalSkipCheck[3]);
                    chkOptionalSkip4.setSelected(optionalSkipCheck[4]);
                    chkOptionalSkip5.setSelected(optionalSkipCheck[5]);
                    chkOptionalSkip6.setSelected(optionalSkipCheck[6]);
                    chkOptionalSkip7.setSelected(optionalSkipCheck[7]);
                    chkOptionalSkip8.setSelected(optionalSkipCheck[8]);
                    chkOptionalSkip9.setSelected(optionalSkipCheck[9]);
                } else {
                    Platform.runLater(() -> {
                        chkOptionalSkip0.setSelected(optionalSkipCheck[0]);
                        chkOptionalSkip2.setSelected(optionalSkipCheck[2]);
                        chkOptionalSkip3.setSelected(optionalSkipCheck[3]);
                        chkOptionalSkip4.setSelected(optionalSkipCheck[4]);
                        chkOptionalSkip5.setSelected(optionalSkipCheck[5]);
                        chkOptionalSkip6.setSelected(optionalSkipCheck[6]);
                        chkOptionalSkip7.setSelected(optionalSkipCheck[7]);
                        chkOptionalSkip8.setSelected(optionalSkipCheck[8]);
                        chkOptionalSkip9.setSelected(optionalSkipCheck[9]);
                    });
                }
            }
        }

        @Override
        public void setOptionalSkipCheck(int index, Boolean check) {
            setOptionalSkipCheck(index, check, true);
        }

        public void setOptionalSkipCheck(int index, Boolean check, boolean update) {
            optionalSkipCheck[index] = check;
            putChange(GcodeVirtualMachine.CHG.BLOCKSKIP, true);
            if (update) {
                if (Platform.isFxApplicationThread()) {
                    switch (index) {
                        case 0:
                            chkOptionalSkip0.setSelected(optionalSkipCheck[0]);
                            break;
                        case 2:
                            chkOptionalSkip2.setSelected(optionalSkipCheck[2]);
                            break;
                        case 3:
                            chkOptionalSkip3.setSelected(optionalSkipCheck[3]);
                            break;
                        case 4:
                            chkOptionalSkip4.setSelected(optionalSkipCheck[4]);
                            break;
                        case 5:
                            chkOptionalSkip5.setSelected(optionalSkipCheck[5]);
                            break;
                        case 6:
                            chkOptionalSkip6.setSelected(optionalSkipCheck[6]);
                            break;
                        case 7:
                            chkOptionalSkip7.setSelected(optionalSkipCheck[7]);
                            break;
                        case 8:
                            chkOptionalSkip8.setSelected(optionalSkipCheck[8]);
                            break;
                        case 9:
                            chkOptionalSkip9.setSelected(optionalSkipCheck[9]);
                            break;
                        default:
                            break;
                    }
                } else {
                    Platform.runLater(() -> {
                        switch (index) {
                            case 0:
                                chkOptionalSkip0.setSelected(optionalSkipCheck[0]);
                                break;
                            case 2:
                                chkOptionalSkip2.setSelected(optionalSkipCheck[2]);
                                break;
                            case 3:
                                chkOptionalSkip3.setSelected(optionalSkipCheck[3]);
                                break;
                            case 4:
                                chkOptionalSkip4.setSelected(optionalSkipCheck[4]);
                                break;
                            case 5:
                                chkOptionalSkip5.setSelected(optionalSkipCheck[5]);
                                break;
                            case 6:
                                chkOptionalSkip6.setSelected(optionalSkipCheck[6]);
                                break;
                            case 7:
                                chkOptionalSkip7.setSelected(optionalSkipCheck[7]);
                                break;
                            case 8:
                                chkOptionalSkip8.setSelected(optionalSkipCheck[8]);
                                break;
                            case 9:
                                chkOptionalSkip9.setSelected(optionalSkipCheck[9]);
                                break;
                            default:
                                break;
                        }
                    });
                }
            }
        }

        @Override
        public boolean actionBackGroundFileValue(int index) {
            FileChooser fc = new FileChooser();

            if (openFile_ != null) {
                fc.setInitialDirectory(openFile_.getParent().toFile());
            }
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All types", "*"));
            File fcfile = fc.showOpenDialog(stage_);
            if (fcfile != null) {
                openFile_ = fcfile.toPath();
                backGroundFileValue[index] = openFile_.toAbsolutePath().toString();
                return true;
            }
            return false;
        }

        @Override
        public String[] getBackGroundFileValue() {
            return backGroundFileValue;
        }

        @Override
        public String getBackGroundFileValue(int index) {
            return backGroundFileValue[index];
        }

        @Override
        public void setBackGroundFileValue(String[] values) {
            setBackGroundFileValue(values, true);
        }

        public void setBackGroundFileValue(String[] values, boolean update) {
            backGroundFileValue = values;
            putChange(GcodeVirtualMachine.CHG.BACK_PROGRAM, true);
        }

        @Override
        public void setBackGroundFileValue(int index, String value) {
            setBackGroundFileValue(index, value, true);
        }

        public void setBackGroundFileValue(int index, String value, boolean update) {
            backGroundFileValue[index] = value;
            putChange(GcodeVirtualMachine.CHG.BACK_PROGRAM, true);
        }

        @Override
        public boolean actionExternalSubProgramDirectoryValue() {
            DirectoryChooser dc = new DirectoryChooser();

            if (openFile_ != null) {
                dc.setInitialDirectory(openFile_.getParent().toFile());
            }
            File dcfile = dc.showDialog(stage_);
            if (dcfile != null) {
                openFile_ = dcfile.toPath();
                externalSubProgramDirectoryValue = openFile_.toAbsolutePath().toString();
                return true;
            }
            return false;
        }

        @Override
        public String getExternalSubProgramDirectoryValue() {
            return externalSubProgramDirectoryValue;
        }

        @Override
        public void setExternalSubProgramDirectoryValue(String value) {
            setExternalSubProgramDirectoryValue(value, true);
        }

        public void setExternalSubProgramDirectoryValue(String value, boolean update) {
            externalSubProgramDirectoryValue = value;
            putChange(GcodeVirtualMachine.CHG.EXTERNAL_SUBPROGRAM_DIR, true);
        }

        @Override
        public String[] getGcodeGroupValue() {
            return gcodeGroupValue;
        }

        @Override
        public String getGcodeGroupValue(int index) {
            return gcodeGroupValue[index];
        }

        @Override
        public void setGcodeGroupValue(String[] values) {
            gcodeGroupValue = values;
            putChange(GcodeVirtualMachine.CHG.GGROUP, true);
        }

        @Override
        public void setGcodeGroupValue(int index, String value) {
            gcodeGroupValue[index] = value;
            putChange(GcodeVirtualMachine.CHG.GGROUP, true);
        }

        @Override
        public String getCodeChangeProgramCallValue() {
            return codeChangeProgramCallValue;
        }

        @Override
        public void setCodeChangeProgramCallValue(String value) {
            codeChangeProgramCallValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getCodeChangeGValue() {
            return codeChangeGValue;
        }

        @Override
        public void setCodeChangeGValue(String value) {
            codeChangeGValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getCodeChangeMValue() {
            return codeChangeMValue;
        }

        @Override
        public void setCodeChangeMValue(String value) {
            codeChangeMValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getCodeChangeTValue() {
            return codeChangeTValue;
        }

        @Override
        public void setCodeChangeTValue(String value) {
            codeChangeTValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getCodeChangeDValue() {
            return codeChangeDValue;
        }

        @Override
        public void setCodeChangeDValue(String value) {
            codeChangeDValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getCodeChangeHValue() {
            return codeChangeHValue;
        }

        @Override
        public void setCodeChangeHValue(String value) {
            codeChangeHValue = value;
            putChange(GcodeVirtualMachine.CHG.CODECHANGE, true);
        }

        @Override
        public String getMacroCallGValue() {
            return macroCallG;
        }

        @Override
        public void setMacroCallGValue(String value) {
            macroCallG = value;
            putChange(GcodeVirtualMachine.CHG.MACROCALL, true);
        }

        @Override
        public String getMacroCallMValue() {
            return macroCallM;
        }

        @Override
        public void setMacroCallMValue(String value) {
            macroCallM = value;
            putChange(GcodeVirtualMachine.CHG.MACROCALL, true);
        }

        @Override
        public Boolean[] getOriginMachineCheck() {
            return originMachineCheck;
        }

        @Override
        public Boolean getOriginMachineCheck(int index) {
            return originMachineCheck[index];
        }

        @Override
        public void setOriginMachineCheck(Boolean[] checks) {
            originMachineCheck = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setOriginMachineCheck(int index, Boolean check) {
            originMachineCheck[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getOriginMachineValue() {
            return originMachineValue;
        }

        @Override
        public String getOriginMachineValue(int index) {
            return originMachineValue[index];
        }

        @Override
        public void setOriginMachineValue(String[] values) {
            originMachineValue = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setOriginMachineValue(int index, String value) {
            originMachineValue[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateExtCheck() {
            return coordinateExtCheck;
        }

        @Override
        public Boolean getCoordinateExtCheck(int index) {
            return coordinateExtCheck[index];
        }

        @Override
        public void setCoordinateExtCheck(Boolean[] checks) {
            coordinateExtCheck = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateExtCheck(int index, Boolean check) {
            coordinateExtCheck[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateExtValue() {
            return coordinateExtValue;
        }

        @Override
        public String getCoordinateExtValue(int index) {
            return coordinateExtValue[index];
        }

        @Override
        public void setCoordinateExtValue(String[] values) {
            coordinateExtValue = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateExtValue(int index, String value) {
            coordinateExtValue[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG92Check() {
            return coordinateG92Check;
        }

        @Override
        public Boolean getCoordinateG92Check(int index) {
            return coordinateG92Check[index];
        }

        @Override
        public void setCoordinateG92Check(Boolean[] checks) {
            coordinateG92Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG92Check(int index, Boolean check) {
            coordinateG92Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG92Value() {
            return coordinateG92Value;
        }

        @Override
        public String getCoordinateG92Value(int index) {
            return coordinateG92Value[index];
        }

        @Override
        public void setCoordinateG92Value(String[] values) {
            coordinateG92Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG92Value(int index, String value) {
            coordinateG92Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean getCoordinateToolCheck() {
            return coordinateToolCheck;
        }

        @Override
        public void setCoordinateToolCheck(Boolean check) {
            coordinateToolCheck = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean getCoordinateMirrorCheck() {
            return coordinateMirrorCheck;
        }

        @Override
        public void setCoordinateMirrorCheck(Boolean check) {
            coordinateMirrorCheck = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG54Check() {
            return coordinateG54Check;
        }

        @Override
        public Boolean getCoordinateG54Check(int index) {
            return coordinateG54Check[index];
        }

        @Override
        public void setCoordinateG54Check(Boolean[] checks) {
            coordinateG54Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG54Check(int index, Boolean check) {
            coordinateG54Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG54Value() {
            return coordinateG54Value;
        }

        @Override
        public String getCoordinateG54Value(int index) {
            return coordinateG54Value[index];
        }

        @Override
        public void setCoordinateG54Value(String[] values) {
            coordinateG54Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG54Value(int index, String value) {
            coordinateG54Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG55Check() {
            return coordinateG55Check;
        }

        @Override
        public Boolean getCoordinateG55Check(int index) {
            return coordinateG55Check[index];
        }

        @Override
        public void setCoordinateG55Check(Boolean[] checks) {
            coordinateG55Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG55Check(int index, Boolean check) {
            coordinateG55Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG55Value() {
            return coordinateG55Value;
        }

        @Override
        public String getCoordinateG55Value(int index) {
            return coordinateG55Value[index];
        }

        @Override
        public void setCoordinateG55Value(String[] values) {
            coordinateG55Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG55Value(int index, String value) {
            coordinateG55Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG56Check() {
            return coordinateG56Check;
        }

        @Override
        public Boolean getCoordinateG56Check(int index) {
            return coordinateG56Check[index];
        }

        @Override
        public void setCoordinateG56Check(Boolean[] checks) {
            coordinateG56Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG56Check(int index, Boolean check) {
            coordinateG56Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG56Value() {
            return coordinateG56Value;
        }

        @Override
        public String getCoordinateG56Value(int index) {
            return coordinateG56Value[index];
        }

        @Override
        public void setCoordinateG56Value(String[] values) {
            coordinateG56Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG56Value(int index, String value) {
            coordinateG56Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG57Check() {
            return coordinateG57Check;
        }

        @Override
        public Boolean getCoordinateG57Check(int index) {
            return coordinateG57Check[index];
        }

        @Override
        public void setCoordinateG57Check(Boolean[] checks) {
            coordinateG57Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG57Check(int index, Boolean check) {
            coordinateG57Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG57Value() {
            return coordinateG57Value;
        }

        @Override
        public String getCoordinateG57Value(int index) {
            return coordinateG57Value[index];
        }

        @Override
        public void setCoordinateG57Value(String[] values) {
            coordinateG57Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG57Value(int index, String value) {
            coordinateG57Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG58Check() {
            return coordinateG58Check;
        }

        @Override
        public Boolean getCoordinateG58Check(int index) {
            return coordinateG58Check[index];
        }

        @Override
        public void setCoordinateG58Check(Boolean[] checks) {
            coordinateG58Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG58Check(int index, Boolean check) {
            coordinateG58Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG58Value() {
            return coordinateG58Value;
        }

        @Override
        public String getCoordinateG58Value(int index) {
            return coordinateG58Value[index];
        }

        @Override
        public void setCoordinateG58Value(String[] values) {
            coordinateG58Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG58Value(int index, String value) {
            coordinateG58Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public Boolean[] getCoordinateG59Check() {
            return coordinateG59Check;
        }

        @Override
        public Boolean getCoordinateG59Check(int index) {
            return coordinateG59Check[index];
        }

        @Override
        public void setCoordinateG59Check(Boolean[] checks) {
            coordinateG59Check = checks;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG59Check(int index, Boolean check) {
            coordinateG59Check[index] = check;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String[] getCoordinateG59Value() {
            return coordinateG59Value;
        }

        @Override
        public String getCoordinateG59Value(int index) {
            return coordinateG59Value[index];
        }

        @Override
        public void setCoordinateG59Value(String[] values) {
            coordinateG59Value = values;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public void setCoordinateG59Value(int index, String value) {
            coordinateG59Value[index] = value;
            putChange(GcodeVirtualMachine.CHG.COORDINATE, true);
        }

        @Override
        public String getToolOffsetValue() {
            return toolOffsetValue;
        }

        @Override
        public void setToolOffsetValue(String value) {
            toolOffsetValue = value;
            putChange(GcodeVirtualMachine.CHG.TOOLOFFSET, true);
        }

        @Override
        public String getVariablesValue() {
            return variablesValue;
        }

        @Override
        public void setVariablesValue(String value) {
            variablesValue = value;
            putChange(GcodeVirtualMachine.CHG.VARIABLES, true);
        }

        @Override
        public Boolean getToolChangeProgramCheck() {
            return toolChangeProgramCheck;
        }

        @Override
        public void setToolChangeProgramCheck(Boolean check) {
            toolChangeProgramCheck = check;
            putChange(GcodeVirtualMachine.CHG.TOOLCHANGE, true);
        }

        @Override
        public Boolean getToolChangeXYCheck() {
            return toolChangeXYCheck;
        }

        @Override
        public void setToolChangeXYCheck(Boolean check) {
            toolChangeXYCheck = check;
            putChange(GcodeVirtualMachine.CHG.TOOLCHANGE, true);
        }

        @Override
        public String getToolChangeProgramValue() {
            return toolChangeProgramValue;
        }

        @Override
        public void setToolChangeProgramValue(String value) {
            toolChangeProgramValue = value;
            putChange(GcodeVirtualMachine.CHG.TOOLCHANGE, true);
        }

        @Override
        public Boolean getToolChangeMCodeCheck() {
            return toolChangeMCodeCheck;
        }

        @Override
        public void setToolChangeMCodeCheck(Boolean check) {
            toolChangeMCodeCheck = check;
            putChange(GcodeVirtualMachine.CHG.TOOLCHANGE, true);
        }

        @Override
        public String getToolChangeMCodeValue() {
            return toolChangeMCodeValue;
        }

        @Override
        public void setToolChangeMCodeValue(String value) {
            toolChangeMCodeValue = value;
            putChange(GcodeVirtualMachine.CHG.TOOLCHANGE, true);
        }

        @Override
        public Boolean getSkipFunctionProgramCheck() {
            return skipFunctionProgramCheck;
        }

        @Override
        public void setSkipFunctionProgramCheck(Boolean check) {
            skipFunctionProgramCheck = check;
            putChange(GcodeVirtualMachine.CHG.SKIPFUNCTION, true);
        }

        @Override
        public String getSkipFunctionProgramValue() {
            return skipFunctionProgramValue;
        }

        @Override
        public void setSkipFunctionProgramValue(String value) {
            skipFunctionProgramValue = value;
            putChange(GcodeVirtualMachine.CHG.SKIPFUNCTION, true);
        }

        @Override
        public String[] getLadderValue() {
            return ladderValue;
        }

        @Override
        public String getLadderValue(int index) {
            return ladderValue[index];
        }

        @Override
        public void setLadderValue(String[] values) {
            ladderValue = values;
            putChange(GcodeVirtualMachine.CHG.LADDER, true);
        }

        @Override
        public void setLadderValue(int index, String value) {
            ladderValue[index] = value;
            putChange(GcodeVirtualMachine.CHG.LADDER, true);
        }

        @Override
        public Boolean getOptionOptimization() {
            return optionOptimization;
        }

        @Override
        public void setOptionOptimization(Boolean check) {
            optionOptimization = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionExComment() {
            return optionExComment;
        }

        @Override
        public void setOptionExComment(Boolean check) {
            optionExComment = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionDisable30033004() {
            return optionDisable30033004;
        }

        @Override
        public void setOptionDisable30033004(Boolean check) {
            optionDisable30033004 = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionReplace3006M0() {
            return optionReplace3006M0;
        }

        @Override
        public void setOptionReplace3006M0(Boolean check) {
            optionReplace3006M0 = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionOnlyS() {
            return optionOnlyS;
        }

        @Override
        public void setOptionOnlyS(Boolean check) {
            optionOnlyS = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionRS274NGC() {
            return optionRS274NGC;
        }

        @Override
        public void setOptionRS274NGC(Boolean check) {
            optionRS274NGC = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public Boolean getOptionDebugJson() {
            return optionDebugJson;
        }

        @Override
        public void setOptionDebugJson(Boolean check) {
            optionDebugJson = check;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public String getOptionMaxFeedRate() {
            return optionMaxFeedRate;
        }

        @Override
        public void setOptionMaxFeedRate(String value) {
            optionMaxFeedRate = value;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public String getOptionMaxRevolution() {
            return optionMaxRevolution;
        }

        @Override
        public void setOptionMaxRevolution(String value) {
            optionMaxRevolution = value;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public String getOptionStartProgram() {
            return optionStartProgram;
        }

        @Override
        public void setOptionStartProgram(String value) {
            optionStartProgram = value;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public String getOptionBlockProgram() {
            return optionBlockProgram;
        }

        @Override
        public void setOptionBlockProgram(String value) {
            optionBlockProgram = value;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }

        @Override
        public String getOptionEndProgram() {
            return optionEndProgram;
        }

        @Override
        public void setOptionEndProgram(String value) {
            optionEndProgram = value;
            putChange(GcodeVirtualMachine.CHG.OPTION, true);
        }
    }

    // enum language
    public static enum LANG {
        EN("EN", 0),
        JA("JA", 1);

        private final String text;
        private final Integer num;

        private LANG(String message, Integer number) {
            text = message;
            num = number;
        }

        public Integer toInteger() {
            return num;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public Stage stage_, stageWeb_, stageLadder_;

    private List<Image> icons_;
    private DesignWebController webController_;
    private DesignLaddersController laddersController_;
    private DesignVirtualMachineSettingsController virtualMachineSettingsController_;
    private Path currentPath_, webPath_, logPath_, debugPath_;
    private GcodeVirtualMachine gcodeVirtualMachine_;
    private WebEditor webEditor_;
    private GcodeInterpreter gcodeInterpreter_;
    private Serial serial_;
    private Soem soem_;

    private StageSettings stageSettings_;
    private BaseSettings baseSettings_;
    private VirtualMachineSettings virtualMachineSettings_;

    private Path openFile_;
    private Map<GcodeVirtualMachine.CHG, Boolean> change_;
    private Path webDragboardPath_;

    private final Pattern pReal = Pattern.compile("^[\\+\\-]?(?:((?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][\\+\\-]?\\d+|))|0[xX]([0-9a-fA-F]+)|0[bB]([0-1]+))$");
    private final Pattern pUInt = Pattern.compile("^[0-9]+$");

    private void addEventDesign() {
        // root
        stage_.setOnCloseRequest((WindowEvent event) -> {
            if (!cleanUp()) {
                event.consume();
            }
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
        splitWebviewConsole.getDividers().get(0).positionProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                if (stageSettings_.isViewConsole()) {
                    stageSettings_.setWebviewConsoleDividerPositions(newValue.doubleValue(), false);
                }
            }
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
        menuFileExportPdf.setOnAction((ActionEvent event) -> {
            webEditor_.exportPdf();
        });
        menuFileNextGenerationController.setOnAction((ActionEvent event) -> {
            if (compile(false) < 0) {
                virtualMachineSettings_.setRunning(false);
                runViewRunning(true);
            }
        });
        menuFileGcodeByteCode.setOnAction((ActionEvent event) -> {
            if (!webEditor_.getValue().isEmpty()) {
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(webEditor_.getValue().getBytes("UTF-8"))) {
                    List<String> bytecodes = GcodeVirtualMachine.parse(byteArrayInputStream);
                    if (bytecodes != null) {
                        StringBuilder lines = new StringBuilder();
                        lines.append(DesignEnums.GCODE.toString()).append(DesignEnums.BYTECODE.toString()).append("\n");
                        lines.append(GcodeParser.VERSION).append("\n");
                        bytecodes.forEach((line) -> {
                            lines.append(line).append("\n");
                        });
                        byte[] encrypted = Crypto.encrypt(DesignEnums.GCODE.toString() + DesignEnums.BYTECODE.toString(), lines.toString(), DesignEnums.ALGORITHM.toString());
                        String filename = webEditor_.getFileName();
                        if (encrypted != null) {
                            if (filename == null) {
                                filename = "no_name";
                            } else {
                                filename = JavaLibrary.removeFileExtension(filename);
                            }
                            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(currentPath_.resolve(filename + ".gbc")))) {
                                bufferedOutputStream.write(encrypted);
                                bufferedOutputStream.flush();
                            } catch (FileNotFoundException ex) {
                                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                            } catch (IOException ex) {
                                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                            }
                        }
                        writeLog(DesignEnums.GBC_COMPLETION.toString() + " [" + filename + ".gbc]", false);
                    }
                } catch (IOException | NegativeArraySizeException | NullPointerException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    writeLog(DesignEnums.GBC_FAILED.toString(), false);
                }
            }
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

        // menu view
        menuViewControlPanel.setOnAction((ActionEvent event) -> {
            stageSettings_.setViewControlPanel(!stageSettings_.isViewControlPanel(), true);
        });
        menuViewConsole.setOnAction((ActionEvent event) -> {
            stageSettings_.setViewConsole(!stageSettings_.isViewConsole(), true);
        });

        // menu extension
        menuExtensionWeb.setOnAction((ActionEvent event) -> {
            if (stageWeb_.isShowing()) {
                stageWeb_.hide();
            } else {
                stageWeb_.show();
            }
        });
        menuExtensionLadder.setOnAction((ActionEvent event) -> {
            if (stageLadder_.isShowing()) {
                stageLadder_.hide();
            } else {
                stageLadder_.show();
            }
        });

        // menu settings
        menuSettingsVirtualMachine.setOnAction((ActionEvent event) -> {
            virtualMachineSettings_.setSettings(true);
            try {
                Stage stage = new Stage(StageStyle.DECORATED);
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignVirtualMachineSettings.fxml"));
                Parent root = (Parent) loader.load();
                virtualMachineSettingsController_ = (DesignVirtualMachineSettingsController) loader.getController();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                if (!icons_.isEmpty()) {
                    stage.getIcons().addAll(icons_);
                }
                virtualMachineSettingsController_.startUp(baseSettings_, virtualMachineSettings_);
                stage.setTitle(DesignEnums.VIRTUAL_MACHINE.toString() + DesignEnums.SETTINGS.toString());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            }
            virtualMachineSettingsController_ = null;
            virtualMachineSettings_.setSettings(false);
        });

        // menu help
        menuHelpLanguage.setOnAction((ActionEvent event) -> {
            switch (stageSettings_.getLanguage()) {
                case EN:
                    stageSettings_.setLanguage(LANG.JA, true);
                    break;
                case JA:
                    stageSettings_.setLanguage(LANG.EN, true);
                    break;
            }
            runInitDesign();
        });
        menuHelpShowLicenses.setOnAction((ActionEvent event) -> {
            showLicenses();
        });
        menuHelpShowVersion.setOnAction((ActionEvent event) -> {
            showVersion();
        });

        // program tab
        txtProgramNumber.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setProgramNumberValue(newValue, false);
        });
        chkOptionalSkip0.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(0, newValue, false);
        });
        chkOptionalSkip2.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(2, newValue, false);
        });
        chkOptionalSkip3.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(3, newValue, false);
        });
        chkOptionalSkip4.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(4, newValue, false);
        });
        chkOptionalSkip5.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(5, newValue, false);
        });
        chkOptionalSkip6.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(6, newValue, false);
        });
        chkOptionalSkip7.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(7, newValue, false);
        });
        chkOptionalSkip8.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(8, newValue, false);
        });
        chkOptionalSkip9.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionalSkipCheck(9, newValue, false);
        });
        btnAddEditor.setOnAction((ActionEvent event) -> {
            stageSettings_.addEditor(null);
        });
        btnVirtualMachineLoad.setOnAction((ActionEvent event) -> {
            virtualMachineSettings_.actionLoadVirtualMachine(null);
        });
        btnSerialOpen.setOnAction((ActionEvent event) -> {
            if (!baseSettings_.getSerialPortValue().isEmpty()) {
                baseSettings_.actionSerialOpen();
            }
        });
        btnSerialClose.setOnAction((ActionEvent event) -> {
            baseSettings_.actionSerialClose();
        });
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
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
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
        if (virtualMachineSettingsController_ != null) {
            virtualMachineSettingsController_.runInitDesign();
        }
    }

    private void initDesign() {
        // menu file
        menuFile.setText(DesignEnums.FILE.toString());
        menuFileNew.setText(DesignEnums.FILE_NEW.toString());
        menuFileOpen.setText(DesignEnums.FILE_OPEN.toString());
        menuFileSave.setText(DesignEnums.FILE_SAVE.toString());
        menuFileSaveAs.setText(DesignEnums.FILE_SAVE_AS.toString());
        menuFileExportPdf.setText(DesignEnums.FILE_EXPORT_PDF.toString());
        menuFileNextGenerationController.setText(DesignEnums.FILE_NEXT_GENERATION_CONTROLLER.toString());
        menuFileGcodeByteCode.setText(DesignEnums.FILE_GCODE_BYTE_CODE.toString());

        // menu view
        menuView.setText(DesignEnums.VIEW.toString());
        menuViewControlPanel.setText(DesignEnums.VIEW_CONTROL_PANEL.toString());
        menuViewConsole.setText(DesignEnums.VIEW_CONSOLE.toString());

        // menu extension
        menuExtension.setText(DesignEnums.EXTENSION.toString());
        menuExtensionWeb.setText(DesignEnums.EXTENSION＿WEB.toString());
        menuExtensionLadder.setText(DesignEnums.EXTENSION＿LADDER.toString());

        // menu settings
        menuSettings.setText(DesignEnums.SETTINGS.toString());
        menuSettingsVirtualMachine.setText(DesignEnums.VIRTUAL_MACHINE.toString());

        // menu help
        menuHelp.setText(DesignEnums.HELP.toString());
        menuHelpLanguage.setText(DesignEnums.HELP_LANGUAGE.toString());
        menuHelpShowLicenses.setText(DesignEnums.HELP_SHOW_LICENSES.toString());
        menuHelpShowVersion.setText(DesignEnums.HELP_SHOW_VERSION.toString());

        // program
        txtProgramNumber.setTooltip(new Tooltip(DesignEnums.PROGRAM_NUMBER_TIP.toString()));
        chkOptionalSkip0.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip0.setText(DesignEnums.OPTIONAL_SKIP0.toString());
        chkOptionalSkip2.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip2.setText(DesignEnums.OPTIONAL_SKIP2.toString());
        chkOptionalSkip3.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip3.setText(DesignEnums.OPTIONAL_SKIP3.toString());
        chkOptionalSkip4.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip4.setText(DesignEnums.OPTIONAL_SKIP4.toString());
        chkOptionalSkip5.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip5.setText(DesignEnums.OPTIONAL_SKIP5.toString());
        chkOptionalSkip6.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip6.setText(DesignEnums.OPTIONAL_SKIP6.toString());
        chkOptionalSkip7.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip7.setText(DesignEnums.OPTIONAL_SKIP7.toString());
        chkOptionalSkip8.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip8.setText(DesignEnums.OPTIONAL_SKIP8.toString());
        chkOptionalSkip9.setTooltip(new Tooltip(DesignEnums.OPTIONAL_SKIP_TIP.toString()));
        chkOptionalSkip9.setText(DesignEnums.OPTIONAL_SKIP9.toString());
        btnAddEditor.setTooltip(new Tooltip(DesignEnums.ADD_EDITOR.toString()));
        txtVirtualMachineName.setTooltip(new Tooltip(DesignEnums.VIRTUAL_MACHINE_NAME_TIP.toString()));
        txtVirtualMachineName.setEditable(false);
        btnVirtualMachineLoad.setTooltip(new Tooltip(DesignEnums.VIRTUAL_MACHINE_LOAD_TIP.toString()));
        btnVirtualMachineLoad.setText(DesignEnums.VIRTUAL_MACHINE_LOAD.toString());
        txtSerialPort.setTooltip(new Tooltip(DesignEnums.SERIAL_PORT_TIP.toString()));
        txtSerialPort.setEditable(false);
        btnSerialOpen.setTooltip(new Tooltip(DesignEnums.SERIAL_CONNECT_TIP.toString()));
        btnSerialOpen.setText(DesignEnums.SERIAL_CONNECT.toString());
        btnSerialClose.setTooltip(new Tooltip(DesignEnums.SERIAL_DISCONNECT_TIP.toString()));
        btnSerialClose.setText(DesignEnums.SERIAL_DISCONNECT.toString());
    }

    private void initSerial() {
        serial_.addSerialListener(this);
        if (!baseSettings_.getSerialPortValue().isEmpty()) {
            serialOpen();
        } else {
            runViewSerial(true);
        }
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
        baseSettings_.setViewRecentFile(bln);
        if (Platform.isFxApplicationThread()) {
            viewRecentFile(baseSettings_.isViewRecentFile());
        } else {
            Platform.runLater(() -> {
                viewRecentFile(baseSettings_.isViewRecentFile());
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

    private void runViewRunning(boolean bln) {
        virtualMachineSettings_.setViewRunning(bln);
        if (Platform.isFxApplicationThread()) {
            viewRunning(virtualMachineSettings_.isViewRunning());
        } else {
            Platform.runLater(() -> {
                viewRunning(virtualMachineSettings_.isViewRunning());
            });
        }
        if (virtualMachineSettingsController_ != null) {
            virtualMachineSettingsController_.runViewRunning();
        }
    }

    private void viewRunning(boolean bln) {
        menuFile.setDisable(!bln);
        menuFileNew.setDisable(!bln);
        menuFileOpen.setDisable(!bln);
        menuFileSave.setDisable(!bln);
        menuFileSaveAs.setDisable(!bln);
        menuFileNextGenerationController.setDisable(!bln);
        menuFileGcodeByteCode.setDisable(!bln);
        menuFileRecentFile1.setDisable(!bln);
        menuFileRecentFile2.setDisable(!bln);
        menuFileRecentFile3.setDisable(!bln);
        menuFileRecentFile4.setDisable(!bln);
        menuFileRecentFile5.setDisable(!bln);
        menuFileRecentFile6.setDisable(!bln);
        menuFileRecentFile7.setDisable(!bln);
        menuFileRecentFile8.setDisable(!bln);
        menuFileRecentFile9.setDisable(!bln);
        menuFileRecentFile10.setDisable(!bln);
        menuView.setDisable(!bln);
        menuViewControlPanel.setDisable(!bln);
        menuViewConsole.setDisable(!bln);
        menuExtension.setDisable(false);
        menuExtensionWeb.setDisable(false);
        menuExtensionLadder.setDisable(false);
        menuSettings.setDisable(!bln);
        menuSettingsVirtualMachine.setDisable(!bln);
        menuHelp.setDisable(false);
        menuHelpLanguage.setDisable(false);
        menuHelpShowLicenses.setDisable(false);
        menuHelpShowVersion.setDisable(false);
        txtProgramNumber.setDisable(!bln);
        chkOptionalSkip0.setDisable(!bln);
        chkOptionalSkip2.setDisable(!bln);
        chkOptionalSkip3.setDisable(!bln);
        chkOptionalSkip4.setDisable(!bln);
        chkOptionalSkip5.setDisable(!bln);
        chkOptionalSkip6.setDisable(!bln);
        chkOptionalSkip7.setDisable(!bln);
        chkOptionalSkip8.setDisable(!bln);
        chkOptionalSkip9.setDisable(!bln);
        webEditor_.setReadOnly(!bln);
    }

    private void runViewSerial(boolean bln) {
        baseSettings_.setViewSerial(bln);
        if (Platform.isFxApplicationThread()) {
            viewSerial(baseSettings_.isViewSerial());
        } else {
            Platform.runLater(() -> {
                viewSerial(baseSettings_.isViewSerial());
            });
        }
        if (virtualMachineSettingsController_ != null) {
            virtualMachineSettingsController_.runViewSerial();
        }
    }

    private void viewSerial(boolean bln) {
        btnVirtualMachineLoad.setDisable(!bln);
        txtSerialPort.setDisable(!bln);
        btnSerialOpen.setDisable(!bln);
        btnSerialClose.setDisable(bln);
    }

    synchronized private int compile(boolean useSerial) {
        if (!virtualMachineSettings_.isRunning() && !virtualMachineSettings_.isSettings()) {
            virtualMachineSettings_.setRunning(true);
            runViewRunning(false);
            serial_.clear();

            // bytecode clear
            if (change_.get(GcodeVirtualMachine.CHG.PROGRAM) || change_.get(GcodeVirtualMachine.CHG.PROGRAMNUMBER)) {
                gcodeVirtualMachine_.clearBytecodesFore();
                gcodeVirtualMachine_.clearProgramNumbersFore();
            }

            if (change_.get(GcodeVirtualMachine.CHG.BACK_PROGRAM)) {
                gcodeVirtualMachine_.clearBytecodesBack();
                gcodeVirtualMachine_.clearProgramNumbersBack();
            }

            // program
            if (change_.get(GcodeVirtualMachine.CHG.PROGRAM) || change_.get(GcodeVirtualMachine.CHG.PROGRAMNUMBER)) {
                String strval = virtualMachineSettings_.getProgramNumberValue().trim();
                if (!strval.isEmpty()) {
                    if (strval.startsWith("O")) {
                        if (pUInt.matcher(strval.substring(1)).find()) {
                            strval = "O" + Integer.toString(Integer.parseInt(strval.substring(1)));
                        }
                    }
                    // fore program
                    if (!webEditor_.getValue().isEmpty()) {
                        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(webEditor_.getValue().getBytes("UTF-8"))) {
                            if (!gcodeVirtualMachine_.addBytecodesFore(byteArrayInputStream, 0, true)) {
                                return -1;
                            }
                        } catch (IOException | NegativeArraySizeException ex) {
                            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                    gcodeVirtualMachine_.setProgram(null);
                    gcodeVirtualMachine_.setProgramNumber(strval);
                } else {
                    gcodeVirtualMachine_.setProgram(webEditor_.getValue());
                    gcodeVirtualMachine_.setProgramNumber(null);
                }
            }

            // block skip
            if (change_.get(GcodeVirtualMachine.CHG.BLOCKSKIP)) {
                gcodeVirtualMachine_.putBlockSkip(0, virtualMachineSettings_.getOptionalSkipCheck(0));
                gcodeVirtualMachine_.putBlockSkip(1, virtualMachineSettings_.getOptionalSkipCheck(0));
                gcodeVirtualMachine_.putBlockSkip(2, virtualMachineSettings_.getOptionalSkipCheck(2));
                gcodeVirtualMachine_.putBlockSkip(3, virtualMachineSettings_.getOptionalSkipCheck(3));
                gcodeVirtualMachine_.putBlockSkip(4, virtualMachineSettings_.getOptionalSkipCheck(4));
                gcodeVirtualMachine_.putBlockSkip(5, virtualMachineSettings_.getOptionalSkipCheck(5));
                gcodeVirtualMachine_.putBlockSkip(6, virtualMachineSettings_.getOptionalSkipCheck(6));
                gcodeVirtualMachine_.putBlockSkip(7, virtualMachineSettings_.getOptionalSkipCheck(7));
                gcodeVirtualMachine_.putBlockSkip(8, virtualMachineSettings_.getOptionalSkipCheck(8));
                gcodeVirtualMachine_.putBlockSkip(9, virtualMachineSettings_.getOptionalSkipCheck(9));
            }

            // settings
            if (change_.get(GcodeVirtualMachine.CHG.SETTINGS)) {
                // serial
                switch (baseSettings_.getSerialEOBValue()) {
                    case "LF":
                        gcodeVirtualMachine_.setSerialEOB("\n");
                        break;
                    case "LF CR CR":
                        gcodeVirtualMachine_.setSerialEOB("\n\r\r");
                        break;
                    case "CR LF":
                        gcodeVirtualMachine_.setSerialEOB("\r\n");
                        break;
                    default:
                        break;
                }
                gcodeVirtualMachine_.setStartCode(baseSettings_.getSerialStartCodeValue());
                gcodeVirtualMachine_.setEndCode(baseSettings_.getSerialEndCodeValue());
            }

            // back program
            if (change_.get(GcodeVirtualMachine.CHG.BACK_PROGRAM)) {
                // back program
                int intval;
                Path backgroundFile;

                for (intval = 0; intval < virtualMachineSettings_.getBackGroundFileValue().length; intval++) {
                    if (!virtualMachineSettings_.getBackGroundFileValue(intval).trim().isEmpty()) {
                        backgroundFile = Paths.get(virtualMachineSettings_.getBackGroundFileValue(intval));
                        if (Files.exists(backgroundFile)) {
                            if (Files.isRegularFile(backgroundFile)) {
                                if (Files.isReadable(backgroundFile)) {
                                    try {
                                        byte[] decrypted = Files.readAllBytes(backgroundFile);
                                        String lines = Crypto.decrypt(DesignEnums.GCODE.toString() + DesignEnums.BYTECODE.toString(), decrypted, DesignEnums.ALGORITHM.toString());
                                        if (lines != null) {
                                            String[] line = lines.split("\n");
                                            if ((DesignEnums.GCODE.toString() + DesignEnums.BYTECODE.toString()).equals(line[0])) {
                                                if (GcodeParser.VERSION.equals(line[1])) {
                                                    gcodeVirtualMachine_.addBytecodesBack(Arrays.asList(line), 2, false);
                                                } else {
                                                    writeLog(DesignEnums.PARSER_NOT_COMPATIBLE.toString() + "[" + backgroundFile.toString() + "]", true);
                                                    return -1;
                                                }
                                            } else {
                                                lines = null;
                                            }
                                        }
                                        if (lines == null) {
                                            if (!gcodeVirtualMachine_.addBytecodesBack(Files.newInputStream(backgroundFile), 0, true)) {
                                                return -1;
                                            }
                                        }
                                    } catch (IOException ex) {
                                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                                        return -1;
                                    }
                                } else {
                                    fileCanNotRead(backgroundFile);
                                    return -1;
                                }
                            } else {
                                fileNotFile(backgroundFile);
                                return -1;
                            }
                        } else {
                            fileNotFound(backgroundFile);
                            return -1;
                        }
                    }
                }
            }

            // external sub-program directory
            if (change_.get(GcodeVirtualMachine.CHG.EXTERNAL_SUBPROGRAM_DIR)) {
                if (!virtualMachineSettings_.getExternalSubProgramDirectoryValue().trim().isEmpty()) {
                    Path externalSubProgramDirectory = Paths.get(virtualMachineSettings_.getExternalSubProgramDirectoryValue());
                    if (Files.exists(externalSubProgramDirectory)) {
                        if (Files.isDirectory(externalSubProgramDirectory)) {
                            if (Files.isReadable(externalSubProgramDirectory)) {
                                gcodeVirtualMachine_.setExternalSubProgramDirectory(externalSubProgramDirectory.toAbsolutePath());
                            } else {
                                fileCanNotRead(externalSubProgramDirectory);
                                return -1;
                            }
                        } else {
                            fileNotDirectory(externalSubProgramDirectory);
                            return -1;
                        }
                    } else {
                        fileNotFound(externalSubProgramDirectory);
                        return -1;
                    }
                }
            }

            // code change
            if (change_.get(GcodeVirtualMachine.CHG.CODECHANGE)) {
                BufferedReader bufferedReader;
                String line;

                // program call
                gcodeVirtualMachine_.clearProgramCallConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeProgramCallValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    if (address.startsWith("O")) {
                                        if (pUInt.matcher(address.substring(1)).find()) {
                                            address = "O" + Integer.toString(Integer.parseInt(address.substring(1)));
                                        }
                                    }
                                    if (value.startsWith("O")) {
                                        if (pUInt.matcher(value.substring(1)).find()) {
                                            value = "O" + Integer.toString(Integer.parseInt(value.substring(1)));
                                        }
                                    }
                                    gcodeVirtualMachine_.putProgramCallConvert(address, value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_PROGRAM_CALL.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // g code convert
                gcodeVirtualMachine_.clearGCodeConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeGValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putGCodeConvert(Double.parseDouble(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_G.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // m code convert
                gcodeVirtualMachine_.clearMCodeConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeMValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putMCodeConvert(Double.parseDouble(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_M.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // t code convert
                gcodeVirtualMachine_.clearTCodeConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeTValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putTCodeConvert(Integer.parseInt(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_T.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // d code convert
                gcodeVirtualMachine_.clearDCodeConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeDValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putDCodeConvert(Integer.parseInt(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_D.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // h code convert
                gcodeVirtualMachine_.clearHCodeConvert();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getCodeChangeHValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putHCodeConvert(Integer.parseInt(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_CODE_CHANGE_H.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }
            }

            // macro call
            if (change_.get(GcodeVirtualMachine.CHG.MACROCALL)) {
                BufferedReader bufferedReader;
                String line;

                // g code
                gcodeVirtualMachine_.clearGMacroCall();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getMacroCallGValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    if (value.startsWith("O")) {
                                        if (pUInt.matcher(value.substring(1)).find()) {
                                            value = "O" + Integer.toString(Integer.parseInt(value.substring(1)));
                                        }
                                    }
                                    gcodeVirtualMachine_.putGMacroCall(Double.parseDouble(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_MACRO_CALL_G + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // m code
                gcodeVirtualMachine_.clearMMacroCall();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getMacroCallMValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    if (value.startsWith("O")) {
                                        if (pUInt.matcher(value.substring(1)).find()) {
                                            value = "O" + Integer.toString(Integer.parseInt(value.substring(1)));
                                        }
                                    }
                                    gcodeVirtualMachine_.putMMacroCall(Double.parseDouble(address), value);
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_MACRO_CALL_M + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }
            }

            // gcode groups, coordinate, tool offset, variables
            if (change_.get(GcodeVirtualMachine.CHG.GGROUP) || change_.get(GcodeVirtualMachine.CHG.COORDINATE) || change_.get(GcodeVirtualMachine.CHG.TOOLOFFSET) || change_.get(GcodeVirtualMachine.CHG.VARIABLES)) {
                BufferedReader bufferedReader;
                String line;
                int intval;

                // init global
                gcodeVirtualMachine_.setGlobal(null);
                gcodeVirtualMachine_.setName(null);

                // g group
                for (intval = 0; intval < virtualMachineSettings_.getGcodeGroupValue().length; intval++) {
                    for (String group : virtualMachineSettings_.getGcodeGroupValue(intval).split("\\|")) {
                        group = group.trim();
                        if (!group.isEmpty()) {
                            if (group.startsWith("@")) {
                                group = group.substring(1);
                                gcodeVirtualMachine_.setVariable(4000 + intval, Double.parseDouble(group), null, null);
                            }
                            gcodeVirtualMachine_.putGGroup(Double.parseDouble(group), intval);
                        }
                    }
                }

                // coordinate flg
                if (virtualMachineSettings_.getOriginMachineCheck(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_X);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_Y);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_Z);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_A);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_B);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_C);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_U);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_V);
                }
                if (virtualMachineSettings_.getOriginMachineCheck(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ORIGIN_MACHINE_W);
                }

                if (virtualMachineSettings_.getCoordinateExtCheck(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_X);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_Y);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_Z);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_A);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_B);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_C);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_U);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_V);
                }
                if (virtualMachineSettings_.getCoordinateExtCheck(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_EXT_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_EXT_W);
                }

                if (virtualMachineSettings_.getCoordinateG92Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_X);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_Y);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_Z);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_A);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_B);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_C);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_U);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_V);
                }
                if (virtualMachineSettings_.getCoordinateG92Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G92_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G92_W);
                }

                if (virtualMachineSettings_.getCoordinateToolCheck()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_TOOL, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_TOOL);
                }

                if (virtualMachineSettings_.getCoordinateMirrorCheck()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_MIRROR, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_MIRROR);
                }

                if (virtualMachineSettings_.getCoordinateG54Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_X);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_Y);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_Z);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_A);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_B);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_C);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_U);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_V);
                }
                if (virtualMachineSettings_.getCoordinateG54Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G54_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G54_W);
                }

                if (virtualMachineSettings_.getCoordinateG55Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_X);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_Y);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_Z);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_A);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_B);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_C);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_U);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_V);
                }
                if (virtualMachineSettings_.getCoordinateG55Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G55_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G55_W);
                }

                if (virtualMachineSettings_.getCoordinateG56Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_X);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_Y);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_Z);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_A);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_B);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_C);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_U);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_V);
                }
                if (virtualMachineSettings_.getCoordinateG56Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G56_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G56_W);
                }

                if (virtualMachineSettings_.getCoordinateG57Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_X);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_Y);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_Z);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_A);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_B);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_C);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_U);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_V);
                }
                if (virtualMachineSettings_.getCoordinateG57Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G57_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G57_W);
                }

                if (virtualMachineSettings_.getCoordinateG58Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_X);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_Y);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_Z);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_A);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_B);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_C);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_U);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_V);
                }
                if (virtualMachineSettings_.getCoordinateG58Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G58_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G58_W);
                }

                if (virtualMachineSettings_.getCoordinateG59Check(0)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_X, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_X);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(1)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_Y, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_Y);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(2)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_Z, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_Z);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(3)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_A, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_A);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(4)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_B, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_B);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(5)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_C, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_C);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(6)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_U, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_U);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(7)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_V, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_V);
                }
                if (virtualMachineSettings_.getCoordinateG59Check(8)) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.COORDINATE_G59_W, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.COORDINATE_G59_W);
                }

                // coordinate value
                // 座標
                gcodeVirtualMachine_.setVariable(5001, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(0)), null, null);
                gcodeVirtualMachine_.setVariable(5002, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(1)), null, null);
                gcodeVirtualMachine_.setVariable(5003, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(2)), null, null);
                gcodeVirtualMachine_.setVariable(5004, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(3)), null, null);
                gcodeVirtualMachine_.setVariable(5005, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(4)), null, null);
                gcodeVirtualMachine_.setVariable(5006, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(5)), null, null);
                gcodeVirtualMachine_.setVariable(5007, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(6)), null, null);
                gcodeVirtualMachine_.setVariable(5008, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(7)), null, null);
                gcodeVirtualMachine_.setVariable(5009, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(8)), null, null);
                // 機械座標
                gcodeVirtualMachine_.setVariable(5021, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(0)), null, null);
                gcodeVirtualMachine_.setVariable(5022, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(1)), null, null);
                gcodeVirtualMachine_.setVariable(5023, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(2)), null, null);
                gcodeVirtualMachine_.setVariable(5024, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(3)), null, null);
                gcodeVirtualMachine_.setVariable(5025, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(4)), null, null);
                gcodeVirtualMachine_.setVariable(5026, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(5)), null, null);
                gcodeVirtualMachine_.setVariable(5027, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(6)), null, null);
                gcodeVirtualMachine_.setVariable(5028, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(7)), null, null);
                gcodeVirtualMachine_.setVariable(5029, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(8)), null, null);
                // 工具補正(磨耗)
                gcodeVirtualMachine_.setVariable(5081, 0, null, null);
                gcodeVirtualMachine_.setVariable(5083, 0, null, null);
                gcodeVirtualMachine_.setVariable(5082, 0, null, null);
                gcodeVirtualMachine_.setVariable(5084, 0, null, null);
                gcodeVirtualMachine_.setVariable(5085, 0, null, null);
                gcodeVirtualMachine_.setVariable(5086, 0, null, null);
                gcodeVirtualMachine_.setVariable(5087, 0, null, null);
                gcodeVirtualMachine_.setVariable(5088, 0, null, null);
                gcodeVirtualMachine_.setVariable(5089, 0, null, null);
                // 機械原点(RS274NGC)
                gcodeVirtualMachine_.setVariable(5161, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(0)), null, null);
                gcodeVirtualMachine_.setVariable(5162, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(1)), null, null);
                gcodeVirtualMachine_.setVariable(5163, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(2)), null, null);
                gcodeVirtualMachine_.setVariable(5164, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(3)), null, null);
                gcodeVirtualMachine_.setVariable(5165, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(4)), null, null);
                gcodeVirtualMachine_.setVariable(5166, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(5)), null, null);
                gcodeVirtualMachine_.setVariable(5167, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(6)), null, null);
                gcodeVirtualMachine_.setVariable(5168, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(7)), null, null);
                gcodeVirtualMachine_.setVariable(5169, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(8)), null, null);
                // 第2リファレンス点(RS274NGC)
                gcodeVirtualMachine_.setVariable(5181, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(0)), null, null);
                gcodeVirtualMachine_.setVariable(5182, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(1)), null, null);
                gcodeVirtualMachine_.setVariable(5183, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(2)), null, null);
                gcodeVirtualMachine_.setVariable(5184, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(3)), null, null);
                gcodeVirtualMachine_.setVariable(5185, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(4)), null, null);
                gcodeVirtualMachine_.setVariable(5186, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(5)), null, null);
                gcodeVirtualMachine_.setVariable(5187, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(6)), null, null);
                gcodeVirtualMachine_.setVariable(5188, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(7)), null, null);
                gcodeVirtualMachine_.setVariable(5189, Double.parseDouble(virtualMachineSettings_.getOriginMachineValue(8)), null, null);
                // 外部ワーク座標
                gcodeVirtualMachine_.setVariable(5201, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(0)), null, null);
                gcodeVirtualMachine_.setVariable(5202, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(1)), null, null);
                gcodeVirtualMachine_.setVariable(5203, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(2)), null, null);
                gcodeVirtualMachine_.setVariable(5204, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(3)), null, null);
                gcodeVirtualMachine_.setVariable(5205, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(4)), null, null);
                gcodeVirtualMachine_.setVariable(5206, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(5)), null, null);
                gcodeVirtualMachine_.setVariable(5207, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(6)), null, null);
                gcodeVirtualMachine_.setVariable(5208, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(7)), null, null);
                gcodeVirtualMachine_.setVariable(5209, Double.parseDouble(virtualMachineSettings_.getCoordinateExtValue(8)), null, null);
                // G92ワーク座標(RS274NGC)
                gcodeVirtualMachine_.setVariable(5211, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5212, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5213, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5214, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5215, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5216, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5217, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5218, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5219, Double.parseDouble(virtualMachineSettings_.getCoordinateG92Value(8)), null, null);
                // 工具オフセット
                gcodeVirtualMachine_.setVariable(5121, 0, null, null);
                gcodeVirtualMachine_.setVariable(5123, 0, null, null);
                gcodeVirtualMachine_.setVariable(5122, 0, null, null);
                gcodeVirtualMachine_.setVariable(5124, 0, null, null);
                gcodeVirtualMachine_.setVariable(5125, 0, null, null);
                gcodeVirtualMachine_.setVariable(5126, 0, null, null);
                gcodeVirtualMachine_.setVariable(5127, 0, null, null);
                gcodeVirtualMachine_.setVariable(5128, 0, null, null);
                gcodeVirtualMachine_.setVariable(5129, 0, null, null);
                // ミラー座標(独自)
                gcodeVirtualMachine_.setVariable(5131, 0, null, null);
                gcodeVirtualMachine_.setVariable(5132, 0, null, null);
                gcodeVirtualMachine_.setVariable(5133, 0, null, null);
                gcodeVirtualMachine_.setVariable(5134, 0, null, null);
                gcodeVirtualMachine_.setVariable(5135, 0, null, null);
                gcodeVirtualMachine_.setVariable(5136, 0, null, null);
                gcodeVirtualMachine_.setVariable(5137, 0, null, null);
                gcodeVirtualMachine_.setVariable(5138, 0, null, null);
                gcodeVirtualMachine_.setVariable(5139, 0, null, null);
                // G54ワーク座標
                gcodeVirtualMachine_.setVariable(5221, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5222, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5223, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5224, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5225, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5226, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5227, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5228, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5229, Double.parseDouble(virtualMachineSettings_.getCoordinateG54Value(8)), null, null);
                // G55ワーク座標
                gcodeVirtualMachine_.setVariable(5241, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5242, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5243, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5244, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5245, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5246, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5247, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5248, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5249, Double.parseDouble(virtualMachineSettings_.getCoordinateG55Value(8)), null, null);
                // G56ワーク座標
                gcodeVirtualMachine_.setVariable(5261, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5262, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5263, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5264, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5265, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5266, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5267, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5268, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5269, Double.parseDouble(virtualMachineSettings_.getCoordinateG56Value(8)), null, null);
                // G57ワーク座標
                gcodeVirtualMachine_.setVariable(5281, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5282, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5283, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5284, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5285, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5286, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5287, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5288, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5289, Double.parseDouble(virtualMachineSettings_.getCoordinateG57Value(8)), null, null);
                // G58ワーク座標
                gcodeVirtualMachine_.setVariable(5301, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5302, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5303, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5304, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5305, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5306, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5307, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5308, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5309, Double.parseDouble(virtualMachineSettings_.getCoordinateG58Value(8)), null, null);
                // G59ワーク座標
                gcodeVirtualMachine_.setVariable(5321, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(0)), null, null);
                gcodeVirtualMachine_.setVariable(5322, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(1)), null, null);
                gcodeVirtualMachine_.setVariable(5323, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(2)), null, null);
                gcodeVirtualMachine_.setVariable(5324, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(3)), null, null);
                gcodeVirtualMachine_.setVariable(5325, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(4)), null, null);
                gcodeVirtualMachine_.setVariable(5326, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(5)), null, null);
                gcodeVirtualMachine_.setVariable(5327, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(6)), null, null);
                gcodeVirtualMachine_.setVariable(5328, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(7)), null, null);
                gcodeVirtualMachine_.setVariable(5329, Double.parseDouble(virtualMachineSettings_.getCoordinateG59Value(8)), null, null);

                // G初期値
                gcodeVirtualMachine_.setVariable(4113, 0, null, null);
                gcodeVirtualMachine_.setVariable(4130, 0, null, null);

                // tool offset
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getToolOffsetValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            if (!address.isEmpty()) {
                                int offset = Integer.parseInt(address);
                                if (offset > 0) {
                                    String[] tool = word[1].split("\\|");
                                    String value;
                                    switch (tool.length) {
                                        case 1:
                                            // Tool compensation memory A
                                            value = tool[0].trim();
                                            if (!value.isEmpty()) {
                                                if (offset < 200) {
                                                    try {
                                                        gcodeVirtualMachine_.setVariable(2000 + offset, Double.parseDouble(value), null, null);
                                                        gcodeVirtualMachine_.setVariable(2400 + offset, Double.parseDouble(value), null, null);
                                                    } catch (NumberFormatException ex) {
                                                        writeLog(DesignEnums.ERROR_TOOL_OFFSET.toString() + ": " + line, true);
                                                        return -1;
                                                    }
                                                }
                                                try {
                                                    gcodeVirtualMachine_.setVariable(10000 + offset, Double.parseDouble(value), null, null);
                                                    gcodeVirtualMachine_.setVariable(12000 + offset, Double.parseDouble(value), null, null);
                                                } catch (NumberFormatException ex) {
                                                    writeLog(DesignEnums.ERROR_TOOL_OFFSET.toString() + ": " + line, true);
                                                    return -1;
                                                }
                                            }
                                            break;
                                        case 2:
                                        case 3:
                                        case 4:
                                            // Tool compensation memory B,C
                                            for (int i = 0; i < tool.length; i++) {
                                                value = tool[i].trim();
                                                if (!value.isEmpty()) {
                                                    if (offset < 200) {
                                                        try {
                                                            gcodeVirtualMachine_.setVariable(2000 + (200 * i) + offset, Double.parseDouble(value), null, null);
                                                        } catch (NumberFormatException ex) {
                                                            writeLog(DesignEnums.ERROR_TOOL_OFFSET.toString() + ": " + line, true);
                                                            return -1;
                                                        }
                                                    }
                                                    try {
                                                        gcodeVirtualMachine_.setVariable(10000 + (1000 * i) + offset, Double.parseDouble(value), null, null);
                                                    } catch (NumberFormatException ex) {
                                                        writeLog(DesignEnums.ERROR_TOOL_OFFSET.toString() + ": " + line, true);
                                                        return -1;
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // variables
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getVariablesValue()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String[] variable = word[1].split("\\|");
                            String value, name;
                            switch (variable.length) {
                                case 1:
                                    value = variable[0].trim();
                                    if (!address.isEmpty() && !value.isEmpty()) {
                                        try {
                                            gcodeVirtualMachine_.setVariable(Integer.parseInt(address), Double.parseDouble(value), null, null);
                                        } catch (NumberFormatException ex) {
                                            writeLog(DesignEnums.ERROR_VARIABLES.toString() + ": " + line, true);
                                            return -1;
                                        }
                                    }
                                    break;
                                case 2:
                                    value = variable[0].trim();
                                    name = variable[1].trim();
                                    if (!address.isEmpty() && !value.isEmpty()) {
                                        if (name.isEmpty()) {
                                            name = null;
                                        }
                                        try {
                                            gcodeVirtualMachine_.setVariable(Integer.parseInt(address), Double.parseDouble(value), null, name);
                                        } catch (NumberFormatException ex) {
                                            writeLog(DesignEnums.ERROR_VARIABLES.toString() + ": " + line, true);
                                            return -1;
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }
            }

            // tool change
            if (change_.get(GcodeVirtualMachine.CHG.TOOLCHANGE)) {
                String strval;
                if (virtualMachineSettings_.getToolChangeProgramCheck()) {
                    strval = virtualMachineSettings_.getToolChangeProgramValue().trim();
                    if (strval.startsWith("O")) {
                        if (pUInt.matcher(strval).find()) {
                            gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.TOOL_CHANGE, "O" + Integer.toString(Integer.parseInt(strval.substring(1))));
                        } else {
                            gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.TOOL_CHANGE, strval);
                        }
                    } else {
                        gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.TOOL_CHANGE, strval);
                    }
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.TOOL_CHANGE);
                }
                if (virtualMachineSettings_.getToolChangeXYCheck()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.TOOL_CHANGE_XY, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.TOOL_CHANGE_XY);
                }
                if (virtualMachineSettings_.getToolChangeMCodeCheck()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.TOOL_CHANGE_M_CODE, virtualMachineSettings_.getToolChangeMCodeValue());
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.TOOL_CHANGE_M_CODE);
                }
            }

            // skip function
            if (change_.get(GcodeVirtualMachine.CHG.SKIPFUNCTION)) {
                String strval;
                if (virtualMachineSettings_.getSkipFunctionProgramCheck()) {
                    strval = virtualMachineSettings_.getSkipFunctionProgramValue().trim();
                    if (strval.startsWith("O")) {
                        if (pUInt.matcher(strval).find()) {
                            gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.SKIP_FUNCTION, "O" + Integer.toString(Integer.parseInt(strval.substring(1))));
                        } else {
                            gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.SKIP_FUNCTION, strval);
                        }
                    } else {
                        gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.SKIP_FUNCTION, strval);
                    }
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.SKIP_FUNCTION);
                }
            }

            // ladder
            if (change_.get(GcodeVirtualMachine.CHG.LADDER)) {
                String address1, address2;
                int i;

                gcodeVirtualMachine_.clearLadderAddress();
                for (i = 0; i < 38; i += 2) {
                    address1 = virtualMachineSettings_.getLadderValue(i).trim();
                    address2 = virtualMachineSettings_.getLadderValue(i + 1).trim();
                    if (!address1.isEmpty() && !address2.isEmpty()) {
                        if (!pReal.matcher(address1).find() && !pReal.matcher(address2).find()) {
                            gcodeVirtualMachine_.putLadderAddress(i, address1);
                            gcodeVirtualMachine_.putLadderAddress(i + 1, address2);
                        } else {
                            switch (i) {
                                case 0:
                                case 1:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_SEQUENCE_CODE.toString(), true);
                                    break;
                                case 2:
                                case 3:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_PREPARATORY_CODE.toString(), true);
                                    break;
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                case 17:
                                case 18:
                                case 19:
                                case 20:
                                case 21:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_DIMENSION_CODE.toString(), true);
                                    break;
                                case 22:
                                case 23:
                                case 24:
                                case 25:
                                case 26:
                                case 27:
                                case 28:
                                case 29:
                                case 30:
                                case 31:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_AUXILIARY_CODE.toString(), true);
                                    break;
                                case 32:
                                case 33:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_FEEDRATE_CODE.toString(), true);
                                    break;
                                case 34:
                                case 35:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_SPINDLE_CODE.toString(), true);
                                    break;
                                case 36:
                                case 37:
                                    writeLog(DesignEnums.ERROR_LADDER.toString() + ": " + DesignEnums.LADDER_TOOL_CODE.toString(), true);
                                    break;
                            }
                            return -1;
                        }
                    }
                }
            }

            // option
            if (change_.get(GcodeVirtualMachine.CHG.OPTION)) {
                BufferedReader bufferedReader;
                String line;

                // Optimization
                if (virtualMachineSettings_.getOptionOptimization()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.OPTIMIZATION, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.OPTIMIZATION);
                }

                // ex comment
                if (virtualMachineSettings_.getOptionExComment()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.EXCOMMENT, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.EXCOMMENT);
                }

                // disable #3003 and #3004
                if (virtualMachineSettings_.getOptionDisable30033004()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.DISABLE_3003, "");
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.DISABLE_3004, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.DISABLE_3003);
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.DISABLE_3004);
                }

                // replace #3006 to M0
                if (virtualMachineSettings_.getOptionReplace3006M0()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.REPLACE_3006_M0, "M0");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.REPLACE_3006_M0);
                }

                // only s
                if (virtualMachineSettings_.getOptionOnlyS()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.ONLY_S, "M3");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.ONLY_S);
                }

                // Next Generation Controller
                if (virtualMachineSettings_.getOptionRS274NGC()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.RS274NGC, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.RS274NGC);
                }

                // debug json
                if (virtualMachineSettings_.getOptionDebugJson()) {
                    gcodeVirtualMachine_.putOption(GcodeInterpreter.OPT.DEBUG_JSON, "");
                } else {
                    gcodeVirtualMachine_.removeOption(GcodeInterpreter.OPT.DEBUG_JSON);
                }

                // max feed rate
                gcodeVirtualMachine_.clearMaxFeedRate();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getOptionMaxFeedRate()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putMaxFeedRate(Integer.parseInt(address), Integer.parseInt(value));
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_OPTION_MAX_FEED_RATE.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                // max revolution
                gcodeVirtualMachine_.clearMaxRevolution();
                bufferedReader = new BufferedReader(new StringReader(virtualMachineSettings_.getOptionMaxRevolution()));
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] word = line.split("=");
                        if (word.length == 2) {
                            String address = word[0].trim();
                            String value = word[1].trim();
                            if (!address.isEmpty() && !value.isEmpty()) {
                                try {
                                    gcodeVirtualMachine_.putMaxRevolution(Integer.parseInt(address), Integer.parseInt(value));
                                } catch (NumberFormatException ex) {
                                    writeLog(DesignEnums.ERROR_OPTION_MAX_REVOLUTION.toString() + ": " + line, true);
                                    return -1;
                                }
                            }
                        }
                    }
                    bufferedReader.close();
                } catch (IOException ex) {
                    Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    return -1;
                }

                gcodeVirtualMachine_.clearBytecodesStart();
                if (!virtualMachineSettings_.getOptionStartProgram().isEmpty()) {
                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(virtualMachineSettings_.getOptionStartProgram().getBytes("UTF-8"))) {
                        if (!gcodeVirtualMachine_.addBytecodesStart(byteArrayInputStream, false)) {
                            return -1;
                        }
                    } catch (IOException | NegativeArraySizeException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                        return -1;
                    }
                }

                gcodeVirtualMachine_.clearBytecodesBlock();
                if (!virtualMachineSettings_.getOptionBlockProgram().isEmpty()) {
                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(virtualMachineSettings_.getOptionBlockProgram().getBytes("UTF-8"))) {
                        if (!gcodeVirtualMachine_.addBytecodesBlock(byteArrayInputStream, false)) {
                            return -1;
                        }
                    } catch (IOException | NegativeArraySizeException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                        return -1;
                    }
                }

                gcodeVirtualMachine_.clearBytecodesEnd();
                if (!virtualMachineSettings_.getOptionEndProgram().isEmpty()) {
                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(virtualMachineSettings_.getOptionEndProgram().getBytes("UTF-8"))) {
                        if (!gcodeVirtualMachine_.addBytecodesEnd(byteArrayInputStream, false)) {
                            return -1;
                        }
                    } catch (IOException | NegativeArraySizeException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                        return -1;
                    }
                }
            }

            // set ladders
            gcodeVirtualMachine_.setLadders(laddersController_.getLadders());

            // set web
            gcodeVirtualMachine_.setWebViewer(webController_.getWebViewer());
            gcodeVirtualMachine_.setWebEditor(webEditor_);

            // use serial
            if (useSerial) {
                gcodeVirtualMachine_.setSerial(serial_);
                if (virtualMachineSettings_.isLogCheck()) {
                    if (logPath_ != null) {
                        try {
                            SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMddHHmmss");
                            if (!Files.exists(logPath_)) {
                                Files.createDirectories(logPath_);
                            }
                            serial_.setLogFile(logPath_.resolve(DesignEnums.GCODE.toString() + "_" + sdfFile.format(new Date()) + ".txt"));
                        } catch (IOException ex) {
                            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                        }
                    }
                } else {
                    serial_.setLogFile(null);
                }
            } else {
                gcodeVirtualMachine_.setSerial(null);
                serial_.setLogFile(null);
            }

            // debug
            if (virtualMachineSettings_.isDebugCheck()) {
                if (debugPath_ != null) {
                    try {
                        String filename;
                        if (!Files.exists(debugPath_)) {
                            Files.createDirectories(debugPath_);
                        }
                        filename = webEditor_.getFileName();
                        if (filename == null) {
                            filename = "no_name";
                        } else {
                            filename = JavaLibrary.removeFileExtension(filename);
                        }
                        gcodeVirtualMachine_.setDebugFileName(debugPath_.resolve(filename));
                    } catch (IOException ex) {
                        Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
                    }
                }
            } else {
                gcodeVirtualMachine_.setDebugFileName(null);
            }

            // start virtual machine
            if (Platform.isFxApplicationThread()) {
                runVirtualMachine();
            } else {
                Platform.runLater(() -> {
                    runVirtualMachine();
                });
            }

            // flg clear
            for (GcodeVirtualMachine.CHG enums : GcodeVirtualMachine.CHG.values()) {
                putChange(enums, false);
            }
        }
        return 0;
    }

    private void runVirtualMachine() {
        if (gcodeVirtualMachine_.getState() == Worker.State.READY) {
            gcodeVirtualMachine_.start();
        } else {
            gcodeVirtualMachine_.restart();
        }
    }

    private void initProperties() {
        int i;

        for (GcodeVirtualMachine.CHG enums : GcodeVirtualMachine.CHG.values()) {
            putChange(enums, true);
        }

        // language
        stageSettings_.setLanguage(LANG.EN, false);

        // stage
        stageSettings_.setStageMaximized(false, false);
        stageSettings_.setStageWidth(640.0, false);
        stageSettings_.setStageHeight(480.0, false);
        stageSettings_.setRootWidth(640.0, false);
        stageSettings_.setRootHeight(480.0, false);
        stageSettings_.setViewControlPanel(true, false);
        stageSettings_.setWebviewConsoleDividerPositions(0.618, false);
        stageSettings_.setViewConsole(true, false);

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

        // program
        virtualMachineSettings_.setProgramNumberValue("", false);
        Boolean[] optionalSkipChecks = new Boolean[10];
        for (i = 0; i < optionalSkipChecks.length; i++) {
            optionalSkipChecks[i] = false;
        }
        virtualMachineSettings_.setOptionalSkipCheck(optionalSkipChecks, false);

        // base
        virtualMachineSettings_.setVirtualMachineNameValue("", false);
        virtualMachineSettings_.setLogCheck(false, false);
        virtualMachineSettings_.setDebugCheck(false, false);
        baseSettings_.setSerialPortValue("", false);
        baseSettings_.setSerialBaudrateValue("4800", false);
        baseSettings_.setSerialDataBitsValue("7", false);
        baseSettings_.setSerialStopBitsValue("2", false);
        baseSettings_.setSerialParityValue("EVEN", false);
        baseSettings_.setSerialEOBValue("LF", false);
        baseSettings_.setSerialBufferLimitValue("1", false);
        baseSettings_.setSerialDelayValue("1", false);
        baseSettings_.setSerialStartCodeValue("EOB", false);
        baseSettings_.setSerialEndCodeValue("%", false);
        baseSettings_.setSerialCharacterCheck(false, false);
        baseSettings_.setSerialObserveCTSCheck(false, false);
        baseSettings_.setSerialObserveDSRCheck(false, false);
        baseSettings_.setSerialObserveDC2DC4Check(false, false);
        String[] backGroundFiles = new String[3];
        for (i = 0; i < backGroundFiles.length; i++) {
            backGroundFiles[i] = "";
        }
        virtualMachineSettings_.setBackGroundFileValue(backGroundFiles, false);
        virtualMachineSettings_.setExternalSubProgramDirectoryValue("", false);

        // gcode group
        String[] gcodeGroupValues = new String[31];
        for (i = 0; i < gcodeGroupValues.length; i++) {
            gcodeGroupValues[i] = "";
        }
        virtualMachineSettings_.setGcodeGroupValue(gcodeGroupValues);

        // code change
        virtualMachineSettings_.setCodeChangeProgramCallValue("");
        virtualMachineSettings_.setCodeChangeGValue("");
        virtualMachineSettings_.setCodeChangeMValue("");
        virtualMachineSettings_.setCodeChangeTValue("");
        virtualMachineSettings_.setCodeChangeDValue("");
        virtualMachineSettings_.setCodeChangeHValue("");

        // macro call
        virtualMachineSettings_.setMacroCallGValue("");
        virtualMachineSettings_.setMacroCallMValue("");

        // coordinate
        Boolean[] originMachineCheck = new Boolean[9];
        String[] originMachineValue = new String[9];
        Boolean[] coordinateExtCheck = new Boolean[9];
        String[] coordinateExtValue = new String[9];
        Boolean[] coordinateG92Check = new Boolean[9];
        String[] coordinateG92Value = new String[9];
        Boolean[] coordinateG54Check = new Boolean[9];
        String[] coordinateG54Value = new String[9];
        Boolean[] coordinateG55Check = new Boolean[9];
        String[] coordinateG55Value = new String[9];
        Boolean[] coordinateG56Check = new Boolean[9];
        String[] coordinateG56Value = new String[9];
        Boolean[] coordinateG57Check = new Boolean[9];
        String[] coordinateG57Value = new String[9];
        Boolean[] coordinateG58Check = new Boolean[9];
        String[] coordinateG58Value = new String[9];
        Boolean[] coordinateG59Check = new Boolean[9];
        String[] coordinateG59Value = new String[9];
        for (i = 0; i < originMachineCheck.length; i++) {
            originMachineCheck[i] = false;
        }
        for (i = 0; i < originMachineValue.length; i++) {
            originMachineValue[i] = "0.0";
        }
        for (i = 0; i < coordinateExtCheck.length; i++) {
            coordinateExtCheck[i] = false;
        }
        for (i = 0; i < coordinateExtValue.length; i++) {
            coordinateExtValue[i] = "0.0";
        }
        for (i = 0; i < coordinateG92Check.length; i++) {
            coordinateG92Check[i] = false;
        }
        for (i = 0; i < coordinateG92Value.length; i++) {
            coordinateG92Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG54Check.length; i++) {
            coordinateG54Check[i] = false;
        }
        for (i = 0; i < coordinateG54Value.length; i++) {
            coordinateG54Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG55Check.length; i++) {
            coordinateG55Check[i] = false;
        }
        for (i = 0; i < coordinateG55Value.length; i++) {
            coordinateG55Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG56Check.length; i++) {
            coordinateG56Check[i] = false;
        }
        for (i = 0; i < coordinateG56Value.length; i++) {
            coordinateG56Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG57Check.length; i++) {
            coordinateG57Check[i] = false;
        }
        for (i = 0; i < coordinateG57Value.length; i++) {
            coordinateG57Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG58Check.length; i++) {
            coordinateG58Check[i] = false;
        }
        for (i = 0; i < coordinateG58Value.length; i++) {
            coordinateG58Value[i] = "0.0";
        }
        for (i = 0; i < coordinateG59Check.length; i++) {
            coordinateG59Check[i] = false;
        }
        for (i = 0; i < coordinateG59Value.length; i++) {
            coordinateG59Value[i] = "0.0";
        }
        virtualMachineSettings_.setOriginMachineCheck(originMachineCheck);
        virtualMachineSettings_.setOriginMachineValue(originMachineValue);
        virtualMachineSettings_.setCoordinateExtCheck(coordinateExtCheck);
        virtualMachineSettings_.setCoordinateExtValue(coordinateExtValue);
        virtualMachineSettings_.setCoordinateG92Check(coordinateG92Check);
        virtualMachineSettings_.setCoordinateG92Value(coordinateG92Value);
        virtualMachineSettings_.setCoordinateToolCheck(false);
        virtualMachineSettings_.setCoordinateMirrorCheck(false);
        virtualMachineSettings_.setCoordinateG54Check(coordinateG54Check);
        virtualMachineSettings_.setCoordinateG54Value(coordinateG54Value);
        virtualMachineSettings_.setCoordinateG55Check(coordinateG55Check);
        virtualMachineSettings_.setCoordinateG55Value(coordinateG55Value);
        virtualMachineSettings_.setCoordinateG56Check(coordinateG56Check);
        virtualMachineSettings_.setCoordinateG56Value(coordinateG56Value);
        virtualMachineSettings_.setCoordinateG57Check(coordinateG57Check);
        virtualMachineSettings_.setCoordinateG57Value(coordinateG57Value);
        virtualMachineSettings_.setCoordinateG58Check(coordinateG58Check);
        virtualMachineSettings_.setCoordinateG58Value(coordinateG58Value);
        virtualMachineSettings_.setCoordinateG59Check(coordinateG59Check);
        virtualMachineSettings_.setCoordinateG59Value(coordinateG59Value);

        // tool offset
        virtualMachineSettings_.setToolOffsetValue("");

        // variables
        virtualMachineSettings_.setVariablesValue("");

        // tool change
        virtualMachineSettings_.setToolChangeProgramCheck(false);
        virtualMachineSettings_.setToolChangeXYCheck(false);
        virtualMachineSettings_.setToolChangeProgramValue("");
        virtualMachineSettings_.setToolChangeMCodeCheck(false);
        virtualMachineSettings_.setToolChangeMCodeValue("");

        // skip function
        virtualMachineSettings_.setSkipFunctionProgramCheck(false);
        virtualMachineSettings_.setSkipFunctionProgramValue("");

        // ladder 
        String[] ladderValue = new String[38];
        for (i = 0; i < ladderValue.length; i++) {
            ladderValue[i] = "";
        }
        virtualMachineSettings_.setLadderValue(ladderValue);

        // option
        virtualMachineSettings_.setOptionOptimization(false);
        virtualMachineSettings_.setOptionExComment(false);
        virtualMachineSettings_.setOptionDisable30033004(false);
        virtualMachineSettings_.setOptionReplace3006M0(false);
        virtualMachineSettings_.setOptionOnlyS(false);
        virtualMachineSettings_.setOptionRS274NGC(false);
        virtualMachineSettings_.setOptionDebugJson(false);
        virtualMachineSettings_.setOptionMaxFeedRate("");
        virtualMachineSettings_.setOptionMaxRevolution("");
        virtualMachineSettings_.setOptionStartProgram("");
        virtualMachineSettings_.setOptionBlockProgram("");
        virtualMachineSettings_.setOptionEndProgram("");
    }

    private boolean loadProperties(Path propertyFile) {
        if (Files.exists(propertyFile) && Files.isRegularFile(propertyFile) && Files.isReadable(propertyFile)) {
            Properties properties = new Properties();
            try {
                properties.loadFromXML(Files.newInputStream(propertyFile));
                // language
                stageSettings_.setLanguage(LANG.valueOf(properties.getProperty("LANG", LANG.EN.toString())), true);

                // stage
                stageSettings_.setStageMaximized((Boolean.valueOf(properties.getProperty("STAGE_MAXIMIZED", "false"))), true);
                stageSettings_.setStageWidth(Double.parseDouble(properties.getProperty("STAGE_WIDTH", "640.0")), true);
                stageSettings_.setStageHeight(Double.parseDouble(properties.getProperty("STAGE_HEIGHT", "480.0")), true);
                stageSettings_.setRootWidth(Double.parseDouble(properties.getProperty("PANE_ROOT_WIDTH", "640.0")), true);
                stageSettings_.setRootHeight(Double.parseDouble(properties.getProperty("PANE_ROOT_HEIGHT", "480.0")), true);
                stageSettings_.setViewControlPanel(Boolean.parseBoolean(properties.getProperty("CONTROL_PANEL_VIEW", "true")), true);
                stageSettings_.setWebviewConsoleDividerPositions(Double.parseDouble(properties.getProperty("SPLIT_DIVIDER_WEBVIEW_CONSOLE", "0.618")), true);
                stageSettings_.setViewConsole(Boolean.parseBoolean(properties.getProperty("CONSOLE_VIEW", "true")), true);
                webEditor_.setWorkFilePath(properties.getProperty("WORK_FILE_PATH", ""));

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

                // program
                virtualMachineSettings_.setProgramNumberValue(properties.getProperty("PROGRAM_NUMBER", ""), true);
                virtualMachineSettings_.setOptionalSkipCheck(0, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_0", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(2, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_2", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(3, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_3", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(4, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_4", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(5, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_5", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(6, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_6", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(7, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_7", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(8, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_8", "false")), true);
                virtualMachineSettings_.setOptionalSkipCheck(9, Boolean.valueOf(properties.getProperty("OPTIONAL_SKIP_9", "false")), true);

                // settings
                virtualMachineSettings_.setVirtualMachineNameValue(properties.getProperty("VIRTUAL_MACHINE_NAME", ""), true);
                virtualMachineSettings_.setLogCheck(Boolean.parseBoolean(properties.getProperty("LOG", "false")), true);
                virtualMachineSettings_.setDebugCheck(Boolean.parseBoolean(properties.getProperty("DEBUG", "false")), true);
                baseSettings_.setSerialPortValue(properties.getProperty("SERIAL_PORT", ""), true);
                baseSettings_.setSerialBaudrateValue(properties.getProperty("SERIAL_BAUDRATE", "4800"), true);
                baseSettings_.setSerialDataBitsValue(properties.getProperty("SERIAL_DATA_BITS", "7"), true);
                baseSettings_.setSerialStopBitsValue(properties.getProperty("SERIAL_STOP_BITS", "2"), true);
                baseSettings_.setSerialParityValue(properties.getProperty("SERIAL_PARITY", "EVEN"), true);
                baseSettings_.setSerialEOBValue(properties.getProperty("SERIAL_EOB", "LF"), true);
                baseSettings_.setSerialBufferLimitValue(properties.getProperty("SERIAL_BUFFER_LIMIT", "1"), true);
                baseSettings_.setSerialDelayValue(properties.getProperty("SERIAL_DELAY", "1"), true);
                baseSettings_.setSerialStartCodeValue(properties.getProperty("SERIAL_START_CODE", "EOB"), true);
                baseSettings_.setSerialEndCodeValue(properties.getProperty("SERIAL_END_CODE", "%"), true);
                baseSettings_.setSerialCharacterCheck(Boolean.parseBoolean(properties.getProperty("SERIAL_CHARACTER", "false")), true);
                baseSettings_.setSerialObserveCTSCheck(Boolean.parseBoolean(properties.getProperty("SERIAL_OBSERVE_CTS", "false")), true);
                baseSettings_.setSerialObserveDSRCheck(Boolean.parseBoolean(properties.getProperty("SERIAL_OBSERVE_DSR", "false")), true);
                baseSettings_.setSerialObserveDC2DC4Check(Boolean.parseBoolean(properties.getProperty("SERIAL_OBSERVE_DC2DC4", "false")), true);
                virtualMachineSettings_.setBackGroundFileValue(0, properties.getProperty("BACKGROUND_FILE_1", ""), true);
                virtualMachineSettings_.setBackGroundFileValue(1, properties.getProperty("BACKGROUND_FILE_2", ""), true);
                virtualMachineSettings_.setBackGroundFileValue(2, properties.getProperty("BACKGROUND_FILE_3", ""), true);
                virtualMachineSettings_.setExternalSubProgramDirectoryValue(properties.getProperty("EXTERNAL_SUBPROGRAM_DIRECTORY", ""), true);

                // gcode group
                virtualMachineSettings_.setGcodeGroupValue(0, properties.getProperty("GCODE_GROUP_0", ""));
                virtualMachineSettings_.setGcodeGroupValue(1, properties.getProperty("GCODE_GROUP_1", ""));
                virtualMachineSettings_.setGcodeGroupValue(2, properties.getProperty("GCODE_GROUP_2", ""));
                virtualMachineSettings_.setGcodeGroupValue(3, properties.getProperty("GCODE_GROUP_3", ""));
                virtualMachineSettings_.setGcodeGroupValue(4, properties.getProperty("GCODE_GROUP_4", ""));
                virtualMachineSettings_.setGcodeGroupValue(5, properties.getProperty("GCODE_GROUP_5", ""));
                virtualMachineSettings_.setGcodeGroupValue(6, properties.getProperty("GCODE_GROUP_6", ""));
                virtualMachineSettings_.setGcodeGroupValue(7, properties.getProperty("GCODE_GROUP_7", ""));
                virtualMachineSettings_.setGcodeGroupValue(8, properties.getProperty("GCODE_GROUP_8", ""));
                virtualMachineSettings_.setGcodeGroupValue(9, properties.getProperty("GCODE_GROUP_9", ""));
                virtualMachineSettings_.setGcodeGroupValue(10, properties.getProperty("GCODE_GROUP_10", ""));
                virtualMachineSettings_.setGcodeGroupValue(11, properties.getProperty("GCODE_GROUP_11", ""));
                virtualMachineSettings_.setGcodeGroupValue(12, properties.getProperty("GCODE_GROUP_12", ""));
                virtualMachineSettings_.setGcodeGroupValue(13, properties.getProperty("GCODE_GROUP_13", ""));
                virtualMachineSettings_.setGcodeGroupValue(14, properties.getProperty("GCODE_GROUP_14", ""));
                virtualMachineSettings_.setGcodeGroupValue(15, properties.getProperty("GCODE_GROUP_15", ""));
                virtualMachineSettings_.setGcodeGroupValue(16, properties.getProperty("GCODE_GROUP_16", ""));
                virtualMachineSettings_.setGcodeGroupValue(17, properties.getProperty("GCODE_GROUP_17", ""));
                virtualMachineSettings_.setGcodeGroupValue(18, properties.getProperty("GCODE_GROUP_18", ""));
                virtualMachineSettings_.setGcodeGroupValue(19, properties.getProperty("GCODE_GROUP_19", ""));
                virtualMachineSettings_.setGcodeGroupValue(20, properties.getProperty("GCODE_GROUP_20", ""));
                virtualMachineSettings_.setGcodeGroupValue(21, properties.getProperty("GCODE_GROUP_21", ""));
                virtualMachineSettings_.setGcodeGroupValue(22, properties.getProperty("GCODE_GROUP_22", ""));
                virtualMachineSettings_.setGcodeGroupValue(23, properties.getProperty("GCODE_GROUP_23", ""));
                virtualMachineSettings_.setGcodeGroupValue(24, properties.getProperty("GCODE_GROUP_24", ""));
                virtualMachineSettings_.setGcodeGroupValue(25, properties.getProperty("GCODE_GROUP_25", ""));
                virtualMachineSettings_.setGcodeGroupValue(26, properties.getProperty("GCODE_GROUP_26", ""));
                virtualMachineSettings_.setGcodeGroupValue(27, properties.getProperty("GCODE_GROUP_27", ""));
                virtualMachineSettings_.setGcodeGroupValue(28, properties.getProperty("GCODE_GROUP_28", ""));
                virtualMachineSettings_.setGcodeGroupValue(29, properties.getProperty("GCODE_GROUP_29", ""));
                virtualMachineSettings_.setGcodeGroupValue(30, properties.getProperty("GCODE_GROUP_30", ""));

                // code change
                virtualMachineSettings_.setCodeChangeProgramCallValue(properties.getProperty("CODE_CHANGE_PROGRAM_CALL", ""));
                virtualMachineSettings_.setCodeChangeGValue(properties.getProperty("CODE_CHANGE_G", ""));
                virtualMachineSettings_.setCodeChangeMValue(properties.getProperty("CODE_CHANGE_M", ""));
                virtualMachineSettings_.setCodeChangeTValue(properties.getProperty("CODE_CHANGE_T", ""));
                virtualMachineSettings_.setCodeChangeDValue(properties.getProperty("CODE_CHANGE_D", ""));
                virtualMachineSettings_.setCodeChangeHValue(properties.getProperty("CODE_CHANGE_H", ""));

                // macro call
                virtualMachineSettings_.setMacroCallGValue(properties.getProperty("MACRO_CALL_G", ""));
                virtualMachineSettings_.setMacroCallMValue(properties.getProperty("MACRO_CALL_M", ""));

                // coordinate
                virtualMachineSettings_.setOriginMachineCheck(0, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_X", "false")));
                virtualMachineSettings_.setOriginMachineValue(0, properties.getProperty("ORIGIN_MACHINE_X", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(1, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_Y", "false")));
                virtualMachineSettings_.setOriginMachineValue(1, properties.getProperty("ORIGIN_MACHINE_Y", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(2, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_Z", "false")));
                virtualMachineSettings_.setOriginMachineValue(2, properties.getProperty("ORIGIN_MACHINE_Z", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(3, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_A", "false")));
                virtualMachineSettings_.setOriginMachineValue(3, properties.getProperty("ORIGIN_MACHINE_A", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(4, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_B", "false")));
                virtualMachineSettings_.setOriginMachineValue(4, properties.getProperty("ORIGIN_MACHINE_B", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(5, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_C", "false")));
                virtualMachineSettings_.setOriginMachineValue(5, properties.getProperty("ORIGIN_MACHINE_C", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(6, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_U", "false")));
                virtualMachineSettings_.setOriginMachineValue(6, properties.getProperty("ORIGIN_MACHINE_U", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(7, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_V", "false")));
                virtualMachineSettings_.setOriginMachineValue(7, properties.getProperty("ORIGIN_MACHINE_V", "0.0"));
                virtualMachineSettings_.setOriginMachineCheck(8, Boolean.parseBoolean(properties.getProperty("ORIGIN_MACHINE_CHECK_W", "false")));
                virtualMachineSettings_.setOriginMachineValue(8, properties.getProperty("ORIGIN_MACHINE_W", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateExtValue(0, properties.getProperty("COORDINATE_EXTERNAL_X", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateExtValue(1, properties.getProperty("COORDINATE_EXTERNAL_Y", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateExtValue(2, properties.getProperty("COORDINATE_EXTERNAL_Z", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateExtValue(3, properties.getProperty("COORDINATE_EXTERNAL_A", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateExtValue(4, properties.getProperty("COORDINATE_EXTERNAL_B", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateExtValue(5, properties.getProperty("COORDINATE_EXTERNAL_C", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateExtValue(6, properties.getProperty("COORDINATE_EXTERNAL_U", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateExtValue(7, properties.getProperty("COORDINATE_EXTERNAL_V", "0.0"));
                virtualMachineSettings_.setCoordinateExtCheck(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_EXTERNAL_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateExtValue(8, properties.getProperty("COORDINATE_EXTERNAL_W", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG92Value(0, properties.getProperty("COORDINATE_G92_X", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG92Value(1, properties.getProperty("COORDINATE_G92_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG92Value(2, properties.getProperty("COORDINATE_G92_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG92Value(3, properties.getProperty("COORDINATE_G92_A", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG92Value(4, properties.getProperty("COORDINATE_G92_B", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG92Value(5, properties.getProperty("COORDINATE_G92_C", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG92Value(6, properties.getProperty("COORDINATE_G92_U", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG92Value(7, properties.getProperty("COORDINATE_G92_V", "0.0"));
                virtualMachineSettings_.setCoordinateG92Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G92_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG92Value(8, properties.getProperty("COORDINATE_G92_W", "0.0"));
                virtualMachineSettings_.setCoordinateToolCheck(Boolean.parseBoolean(properties.getProperty("COORDINATE_TOOL_CHECK", "false")));
                virtualMachineSettings_.setCoordinateMirrorCheck(Boolean.parseBoolean(properties.getProperty("COORDINATE_MIRROR_CHECK", "false")));
                virtualMachineSettings_.setCoordinateG54Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG54Value(0, properties.getProperty("COORDINATE_G54_X", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG54Value(1, properties.getProperty("COORDINATE_G54_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG54Value(2, properties.getProperty("COORDINATE_G54_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG54Value(3, properties.getProperty("COORDINATE_G54_A", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG54Value(4, properties.getProperty("COORDINATE_G54_B", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG54Value(5, properties.getProperty("COORDINATE_G54_C", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG54Value(6, properties.getProperty("COORDINATE_G54_U", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG54Value(7, properties.getProperty("COORDINATE_G54_V", "0.0"));
                virtualMachineSettings_.setCoordinateG54Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G54_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG54Value(8, properties.getProperty("COORDINATE_G54_W", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG55Value(0, properties.getProperty("COORDINATE_G55_X", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG55Value(1, properties.getProperty("COORDINATE_G55_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG55Value(2, properties.getProperty("COORDINATE_G55_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG55Value(3, properties.getProperty("COORDINATE_G55_A", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG55Value(4, properties.getProperty("COORDINATE_G55_B", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG55Value(5, properties.getProperty("COORDINATE_G55_C", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG55Value(6, properties.getProperty("COORDINATE_G55_U", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG55Value(7, properties.getProperty("COORDINATE_G55_V", "0.0"));
                virtualMachineSettings_.setCoordinateG55Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G55_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG55Value(8, properties.getProperty("COORDINATE_G55_W", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG56Value(0, properties.getProperty("COORDINATE_G56_X", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG56Value(1, properties.getProperty("COORDINATE_G56_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG56Value(2, properties.getProperty("COORDINATE_G56_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG56Value(3, properties.getProperty("COORDINATE_G56_A", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG56Value(4, properties.getProperty("COORDINATE_G56_B", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG56Value(5, properties.getProperty("COORDINATE_G56_C", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG56Value(6, properties.getProperty("COORDINATE_G56_U", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG56Value(7, properties.getProperty("COORDINATE_G56_V", "0.0"));
                virtualMachineSettings_.setCoordinateG56Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G56_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG56Value(8, properties.getProperty("COORDINATE_G56_W", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG57Value(0, properties.getProperty("COORDINATE_G57_X", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG57Value(1, properties.getProperty("COORDINATE_G57_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG57Value(2, properties.getProperty("COORDINATE_G57_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG57Value(3, properties.getProperty("COORDINATE_G57_A", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG57Value(4, properties.getProperty("COORDINATE_G57_B", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG57Value(5, properties.getProperty("COORDINATE_G57_C", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG57Value(6, properties.getProperty("COORDINATE_G57_U", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG57Value(7, properties.getProperty("COORDINATE_G57_V", "0.0"));
                virtualMachineSettings_.setCoordinateG57Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G57_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG57Value(8, properties.getProperty("COORDINATE_G57_W", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG58Value(0, properties.getProperty("COORDINATE_G58_X", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG58Value(1, properties.getProperty("COORDINATE_G58_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG58Value(2, properties.getProperty("COORDINATE_G58_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG58Value(3, properties.getProperty("COORDINATE_G58_A", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG58Value(4, properties.getProperty("COORDINATE_G58_B", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG58Value(5, properties.getProperty("COORDINATE_G58_C", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG58Value(6, properties.getProperty("COORDINATE_G58_U", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG58Value(7, properties.getProperty("COORDINATE_G58_V", "0.0"));
                virtualMachineSettings_.setCoordinateG58Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G58_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG58Value(8, properties.getProperty("COORDINATE_G58_W", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(0, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_X", "false")));
                virtualMachineSettings_.setCoordinateG59Value(0, properties.getProperty("COORDINATE_G59_X", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(1, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_Y", "false")));
                virtualMachineSettings_.setCoordinateG59Value(1, properties.getProperty("COORDINATE_G59_Y", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(2, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_Z", "false")));
                virtualMachineSettings_.setCoordinateG59Value(2, properties.getProperty("COORDINATE_G59_Z", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(3, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_A", "false")));
                virtualMachineSettings_.setCoordinateG59Value(3, properties.getProperty("COORDINATE_G59_A", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(4, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_B", "false")));
                virtualMachineSettings_.setCoordinateG59Value(4, properties.getProperty("COORDINATE_G59_B", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(5, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_C", "false")));
                virtualMachineSettings_.setCoordinateG59Value(5, properties.getProperty("COORDINATE_G59_C", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(6, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_U", "false")));
                virtualMachineSettings_.setCoordinateG59Value(6, properties.getProperty("COORDINATE_G59_U", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(7, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_V", "false")));
                virtualMachineSettings_.setCoordinateG59Value(7, properties.getProperty("COORDINATE_G59_V", "0.0"));
                virtualMachineSettings_.setCoordinateG59Check(8, Boolean.parseBoolean(properties.getProperty("COORDINATE_G59_CHECK_W", "false")));
                virtualMachineSettings_.setCoordinateG59Value(8, properties.getProperty("COORDINATE_G59_W", "0.0"));

                // tool offset
                virtualMachineSettings_.setToolOffsetValue(properties.getProperty("TOOL_OFFSET", ""));

                // variables
                virtualMachineSettings_.setVariablesValue(properties.getProperty("VARIABLES", ""));

                // tool change
                virtualMachineSettings_.setToolChangeProgramCheck(Boolean.parseBoolean(properties.getProperty("TOOL_CHANGE_PROGRAM_CHECK", "false")));
                virtualMachineSettings_.setToolChangeXYCheck(Boolean.parseBoolean(properties.getProperty("TOOL_CHANGE_XY_CHECK", "false")));
                virtualMachineSettings_.setToolChangeProgramValue(properties.getProperty("TOOL_CHANGE_PROGRAM", ""));
                virtualMachineSettings_.setToolChangeMCodeCheck(Boolean.parseBoolean(properties.getProperty("TOOL_CHANGE_MCODE_CHECK", "false")));
                virtualMachineSettings_.setToolChangeMCodeValue(properties.getProperty("TOOL_CHANGE_MCODE", ""));

                // skip function
                virtualMachineSettings_.setSkipFunctionProgramCheck(Boolean.parseBoolean(properties.getProperty("SKIP_FUNCTION_PROGRAM_CHECK", "false")));
                virtualMachineSettings_.setSkipFunctionProgramValue(properties.getProperty("SKIP_FUNCTION_PROGRAM", ""));

                // ladder
                virtualMachineSettings_.setLadderValue(0, properties.getProperty("LADDER_N1", ""));
                virtualMachineSettings_.setLadderValue(1, properties.getProperty("LADDER_N2", ""));
                virtualMachineSettings_.setLadderValue(2, properties.getProperty("LADDER_G1", ""));
                virtualMachineSettings_.setLadderValue(3, properties.getProperty("LADDER_G2", ""));
                virtualMachineSettings_.setLadderValue(4, properties.getProperty("LADDER_X1", ""));
                virtualMachineSettings_.setLadderValue(5, properties.getProperty("LADDER_X2", ""));
                virtualMachineSettings_.setLadderValue(6, properties.getProperty("LADDER_Y1", ""));
                virtualMachineSettings_.setLadderValue(7, properties.getProperty("LADDER_Y2", ""));
                virtualMachineSettings_.setLadderValue(8, properties.getProperty("LADDER_Z1", ""));
                virtualMachineSettings_.setLadderValue(9, properties.getProperty("LADDER_Z2", ""));
                virtualMachineSettings_.setLadderValue(10, properties.getProperty("LADDER_A1", ""));
                virtualMachineSettings_.setLadderValue(11, properties.getProperty("LADDER_A2", ""));
                virtualMachineSettings_.setLadderValue(12, properties.getProperty("LADDER_B1", ""));
                virtualMachineSettings_.setLadderValue(13, properties.getProperty("LADDER_B2", ""));
                virtualMachineSettings_.setLadderValue(14, properties.getProperty("LADDER_C1", ""));
                virtualMachineSettings_.setLadderValue(15, properties.getProperty("LADDER_C2", ""));
                virtualMachineSettings_.setLadderValue(16, properties.getProperty("LADDER_U1", ""));
                virtualMachineSettings_.setLadderValue(17, properties.getProperty("LADDER_U2", ""));
                virtualMachineSettings_.setLadderValue(18, properties.getProperty("LADDER_V1", ""));
                virtualMachineSettings_.setLadderValue(19, properties.getProperty("LADDER_V2", ""));
                virtualMachineSettings_.setLadderValue(20, properties.getProperty("LADDER_W1", ""));
                virtualMachineSettings_.setLadderValue(21, properties.getProperty("LADDER_W2", ""));
                virtualMachineSettings_.setLadderValue(22, properties.getProperty("LADDER_M1", ""));
                virtualMachineSettings_.setLadderValue(23, properties.getProperty("LADDER_M2", ""));
                virtualMachineSettings_.setLadderValue(24, properties.getProperty("LADDER_R1", ""));
                virtualMachineSettings_.setLadderValue(25, properties.getProperty("LADDER_R2", ""));
                virtualMachineSettings_.setLadderValue(26, properties.getProperty("LADDER_I1", ""));
                virtualMachineSettings_.setLadderValue(27, properties.getProperty("LADDER_I2", ""));
                virtualMachineSettings_.setLadderValue(28, properties.getProperty("LADDER_J1", ""));
                virtualMachineSettings_.setLadderValue(29, properties.getProperty("LADDER_J2", ""));
                virtualMachineSettings_.setLadderValue(30, properties.getProperty("LADDER_K1", ""));
                virtualMachineSettings_.setLadderValue(31, properties.getProperty("LADDER_K2", ""));
                virtualMachineSettings_.setLadderValue(32, properties.getProperty("LADDER_F1", ""));
                virtualMachineSettings_.setLadderValue(33, properties.getProperty("LADDER_F2", ""));
                virtualMachineSettings_.setLadderValue(34, properties.getProperty("LADDER_S1", ""));
                virtualMachineSettings_.setLadderValue(35, properties.getProperty("LADDER_S2", ""));
                virtualMachineSettings_.setLadderValue(36, properties.getProperty("LADDER_T1", ""));
                virtualMachineSettings_.setLadderValue(37, properties.getProperty("LADDER_T2", ""));

                // option
                virtualMachineSettings_.setOptionOptimization(Boolean.parseBoolean(properties.getProperty("OPTION_OPTIMIZATION", "false")));
                virtualMachineSettings_.setOptionExComment(Boolean.parseBoolean(properties.getProperty("OPTION_EX_COMMENT", "false")));
                virtualMachineSettings_.setOptionDisable30033004(Boolean.parseBoolean(properties.getProperty("OPTION_DISABLE_3003_3004", "false")));
                virtualMachineSettings_.setOptionReplace3006M0(Boolean.parseBoolean(properties.getProperty("OPTION_REPLACE_3006_M0", "false")));
                virtualMachineSettings_.setOptionOnlyS(Boolean.parseBoolean(properties.getProperty("OPTION_ONLY_S", "false")));
                virtualMachineSettings_.setOptionRS274NGC(Boolean.parseBoolean(properties.getProperty("OPTION_RS274NGC", "false")));
                virtualMachineSettings_.setOptionDebugJson(Boolean.parseBoolean(properties.getProperty("OPTION_DEBUG_JSON", "false")));
                virtualMachineSettings_.setOptionMaxFeedRate(properties.getProperty("OPTION_MAX_FEED_RATE", ""));
                virtualMachineSettings_.setOptionMaxRevolution(properties.getProperty("OPTION_MAX_REVOLUTION", ""));
                virtualMachineSettings_.setOptionStartProgram(properties.getProperty("OPTION_START_PROGRAM", ""));
                virtualMachineSettings_.setOptionBlockProgram(properties.getProperty("OPTION_BLOCK_PROGRAM", ""));
                virtualMachineSettings_.setOptionEndProgram(properties.getProperty("OPTION_END_PROGRAM", ""));
                return true;
            } catch (FileNotFoundException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            }
        } else {
            // language
            stageSettings_.setLanguage(stageSettings_.getLanguage(), true);

            // stage
            stageSettings_.setStageMaximized(stageSettings_.isStageMaximized(), true);
            stageSettings_.setStageWidth(stageSettings_.getStageWidth(), true);
            stageSettings_.setStageHeight(stageSettings_.getStageHeight(), true);
            stageSettings_.setRootWidth(stageSettings_.getRootWidth(), true);
            stageSettings_.setRootHeight(stageSettings_.getRootHeight(), true);
            stageSettings_.setViewControlPanel(stageSettings_.isViewControlPanel(), true);
            stageSettings_.setWebviewConsoleDividerPositions(stageSettings_.getWebviewConsoleDividerPositions(), true);
            stageSettings_.setViewConsole(stageSettings_.isViewConsole(), true);

            // program
            virtualMachineSettings_.setProgramNumberValue(virtualMachineSettings_.getProgramNumberValue(), true);
            virtualMachineSettings_.setOptionalSkipCheck(virtualMachineSettings_.getOptionalSkipCheck(), true);

            // settings
            virtualMachineSettings_.setVirtualMachineNameValue(virtualMachineSettings_.getVirtualMachineNameValue(), true);
            virtualMachineSettings_.setLogCheck(virtualMachineSettings_.isLogCheck(), true);
            virtualMachineSettings_.setDebugCheck(virtualMachineSettings_.isDebugCheck(), true);
            baseSettings_.setSerialPortValue(baseSettings_.getSerialPortValue(), true);
            baseSettings_.setSerialBaudrateValue(baseSettings_.getSerialBaudrateValue(), true);
            baseSettings_.setSerialDataBitsValue(baseSettings_.getSerialDataBitsValue(), true);
            baseSettings_.setSerialStopBitsValue(baseSettings_.getSerialStopBitsValue(), true);
            baseSettings_.setSerialParityValue(baseSettings_.getSerialParityValue(), true);
            baseSettings_.setSerialEOBValue(baseSettings_.getSerialEOBValue(), true);
            baseSettings_.setSerialBufferLimitValue(baseSettings_.getSerialBufferLimitValue(), true);
            baseSettings_.setSerialDelayValue(baseSettings_.getSerialDelayValue(), true);
            baseSettings_.setSerialStartCodeValue(baseSettings_.getSerialStartCodeValue(), true);
            baseSettings_.setSerialEndCodeValue(baseSettings_.getSerialEndCodeValue(), true);
            baseSettings_.setSerialCharacterCheck(baseSettings_.isSerialCharacterCheck(), true);
            baseSettings_.setSerialObserveCTSCheck(baseSettings_.isSerialObserveCTSCheck(), true);
            baseSettings_.setSerialObserveDSRCheck(baseSettings_.isSerialObserveDSRCheck(), true);
            baseSettings_.setSerialObserveDC2DC4Check(baseSettings_.isSerialObserveDC2DC4Check(), true);
            virtualMachineSettings_.setBackGroundFileValue(virtualMachineSettings_.getBackGroundFileValue(), true);
            virtualMachineSettings_.setExternalSubProgramDirectoryValue(virtualMachineSettings_.getExternalSubProgramDirectoryValue(), true);
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
                // language
                properties.setProperty("LANG", stageSettings_.getLanguage().toString());

                // stage
                properties.setProperty("STAGE_MAXIMIZED", Boolean.toString(stageSettings_.isStageMaximized()));
                properties.setProperty("STAGE_WIDTH", Double.toString(stageSettings_.getStageWidth()));
                properties.setProperty("STAGE_HEIGHT", Double.toString(stageSettings_.getStageHeight()));
                properties.setProperty("PANE_ROOT_WIDTH", Double.toString(stageSettings_.getRootWidth()));
                properties.setProperty("PANE_ROOT_HEIGHT", Double.toString(stageSettings_.getRootHeight()));
                properties.setProperty("CONTROL_PANEL_VIEW", Boolean.toString(stageSettings_.isViewControlPanel()));
                properties.setProperty("SPLIT_DIVIDER_WEBVIEW_CONSOLE", Double.toString(stageSettings_.getWebviewConsoleDividerPositions()));
                properties.setProperty("CONSOLE_VIEW", Boolean.toString(stageSettings_.isViewConsole()));
                properties.setProperty("WORK_FILE_PATH", webEditor_.getWorkFilePath());

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

                // program
                properties.setProperty("PROGRAM_NUMBER", virtualMachineSettings_.getProgramNumberValue());
                properties.setProperty("OPTIONAL_SKIP_0", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(0)));
                properties.setProperty("OPTIONAL_SKIP_2", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(2)));
                properties.setProperty("OPTIONAL_SKIP_3", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(3)));
                properties.setProperty("OPTIONAL_SKIP_4", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(4)));
                properties.setProperty("OPTIONAL_SKIP_5", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(5)));
                properties.setProperty("OPTIONAL_SKIP_6", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(6)));
                properties.setProperty("OPTIONAL_SKIP_7", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(7)));
                properties.setProperty("OPTIONAL_SKIP_8", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(8)));
                properties.setProperty("OPTIONAL_SKIP_9", Boolean.toString(virtualMachineSettings_.getOptionalSkipCheck(9)));

                // settings
                properties.setProperty("VIRTUAL_MACHINE_NAME", virtualMachineSettings_.getVirtualMachineNameValue());
                properties.setProperty("LOG", Boolean.toString(virtualMachineSettings_.isLogCheck()));
                properties.setProperty("DEBUG", Boolean.toString(virtualMachineSettings_.isDebugCheck()));
                properties.setProperty("SERIAL_PORT", baseSettings_.getSerialPortValue());
                properties.setProperty("SERIAL_BAUDRATE", baseSettings_.getSerialBaudrateValue());
                properties.setProperty("SERIAL_DATA_BITS", baseSettings_.getSerialDataBitsValue());
                properties.setProperty("SERIAL_STOP_BITS", baseSettings_.getSerialStopBitsValue());
                properties.setProperty("SERIAL_PARITY", baseSettings_.getSerialParityValue());
                properties.setProperty("SERIAL_EOB", baseSettings_.getSerialEOBValue());
                properties.setProperty("SERIAL_BUFFER_LIMIT", baseSettings_.getSerialBufferLimitValue());
                properties.setProperty("SERIAL_DELAY", baseSettings_.getSerialDelayValue());
                properties.setProperty("SERIAL_START_CODE", baseSettings_.getSerialStartCodeValue());
                properties.setProperty("SERIAL_END_CODE", baseSettings_.getSerialEndCodeValue());
                properties.setProperty("SERIAL_CHARACTER", Boolean.toString(baseSettings_.isSerialCharacterCheck()));
                properties.setProperty("SERIAL_OBSERVE_CTS", Boolean.toString(baseSettings_.isSerialObserveCTSCheck()));
                properties.setProperty("SERIAL_OBSERVE_DSR", Boolean.toString(baseSettings_.isSerialObserveDSRCheck()));
                properties.setProperty("SERIAL_OBSERVE_DC2DC4", Boolean.toString(baseSettings_.isSerialObserveDC2DC4Check()));
                properties.setProperty("BACKGROUND_FILE_1", virtualMachineSettings_.getBackGroundFileValue(0));
                properties.setProperty("BACKGROUND_FILE_2", virtualMachineSettings_.getBackGroundFileValue(1));
                properties.setProperty("BACKGROUND_FILE_3", virtualMachineSettings_.getBackGroundFileValue(2));
                properties.setProperty("EXTERNAL_SUBPROGRAM_DIRECTORY", virtualMachineSettings_.getExternalSubProgramDirectoryValue());

                // gcode group
                properties.setProperty("GCODE_GROUP_0", virtualMachineSettings_.getGcodeGroupValue(0));
                properties.setProperty("GCODE_GROUP_1", virtualMachineSettings_.getGcodeGroupValue(1));
                properties.setProperty("GCODE_GROUP_2", virtualMachineSettings_.getGcodeGroupValue(2));
                properties.setProperty("GCODE_GROUP_3", virtualMachineSettings_.getGcodeGroupValue(3));
                properties.setProperty("GCODE_GROUP_4", virtualMachineSettings_.getGcodeGroupValue(4));
                properties.setProperty("GCODE_GROUP_5", virtualMachineSettings_.getGcodeGroupValue(5));
                properties.setProperty("GCODE_GROUP_6", virtualMachineSettings_.getGcodeGroupValue(6));
                properties.setProperty("GCODE_GROUP_7", virtualMachineSettings_.getGcodeGroupValue(7));
                properties.setProperty("GCODE_GROUP_8", virtualMachineSettings_.getGcodeGroupValue(8));
                properties.setProperty("GCODE_GROUP_9", virtualMachineSettings_.getGcodeGroupValue(9));
                properties.setProperty("GCODE_GROUP_10", virtualMachineSettings_.getGcodeGroupValue(10));
                properties.setProperty("GCODE_GROUP_11", virtualMachineSettings_.getGcodeGroupValue(11));
                properties.setProperty("GCODE_GROUP_12", virtualMachineSettings_.getGcodeGroupValue(12));
                properties.setProperty("GCODE_GROUP_13", virtualMachineSettings_.getGcodeGroupValue(13));
                properties.setProperty("GCODE_GROUP_14", virtualMachineSettings_.getGcodeGroupValue(14));
                properties.setProperty("GCODE_GROUP_15", virtualMachineSettings_.getGcodeGroupValue(15));
                properties.setProperty("GCODE_GROUP_16", virtualMachineSettings_.getGcodeGroupValue(16));
                properties.setProperty("GCODE_GROUP_17", virtualMachineSettings_.getGcodeGroupValue(17));
                properties.setProperty("GCODE_GROUP_18", virtualMachineSettings_.getGcodeGroupValue(18));
                properties.setProperty("GCODE_GROUP_19", virtualMachineSettings_.getGcodeGroupValue(19));
                properties.setProperty("GCODE_GROUP_20", virtualMachineSettings_.getGcodeGroupValue(20));
                properties.setProperty("GCODE_GROUP_21", virtualMachineSettings_.getGcodeGroupValue(21));
                properties.setProperty("GCODE_GROUP_22", virtualMachineSettings_.getGcodeGroupValue(22));
                properties.setProperty("GCODE_GROUP_23", virtualMachineSettings_.getGcodeGroupValue(23));
                properties.setProperty("GCODE_GROUP_24", virtualMachineSettings_.getGcodeGroupValue(24));
                properties.setProperty("GCODE_GROUP_25", virtualMachineSettings_.getGcodeGroupValue(25));
                properties.setProperty("GCODE_GROUP_26", virtualMachineSettings_.getGcodeGroupValue(26));
                properties.setProperty("GCODE_GROUP_27", virtualMachineSettings_.getGcodeGroupValue(27));
                properties.setProperty("GCODE_GROUP_28", virtualMachineSettings_.getGcodeGroupValue(28));
                properties.setProperty("GCODE_GROUP_29", virtualMachineSettings_.getGcodeGroupValue(29));
                properties.setProperty("GCODE_GROUP_30", virtualMachineSettings_.getGcodeGroupValue(30));

                // code change
                properties.setProperty("CODE_CHANGE_PROGRAM_CALL", virtualMachineSettings_.getCodeChangeProgramCallValue());
                properties.setProperty("CODE_CHANGE_G", virtualMachineSettings_.getCodeChangeGValue());
                properties.setProperty("CODE_CHANGE_M", virtualMachineSettings_.getCodeChangeMValue());
                properties.setProperty("CODE_CHANGE_T", virtualMachineSettings_.getCodeChangeTValue());
                properties.setProperty("CODE_CHANGE_D", virtualMachineSettings_.getCodeChangeDValue());
                properties.setProperty("CODE_CHANGE_H", virtualMachineSettings_.getCodeChangeHValue());

                // macro call
                properties.setProperty("MACRO_CALL_G", virtualMachineSettings_.getMacroCallGValue());
                properties.setProperty("MACRO_CALL_M", virtualMachineSettings_.getMacroCallMValue());

                // coordinate
                properties.setProperty("ORIGIN_MACHINE_CHECK_X", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(0)));
                properties.setProperty("ORIGIN_MACHINE_X", virtualMachineSettings_.getOriginMachineValue(0));
                properties.setProperty("ORIGIN_MACHINE_CHECK_Y", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(1)));
                properties.setProperty("ORIGIN_MACHINE_Y", virtualMachineSettings_.getOriginMachineValue(1));
                properties.setProperty("ORIGIN_MACHINE_CHECK_Z", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(2)));
                properties.setProperty("ORIGIN_MACHINE_Z", virtualMachineSettings_.getOriginMachineValue(2));
                properties.setProperty("ORIGIN_MACHINE_CHECK_A", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(3)));
                properties.setProperty("ORIGIN_MACHINE_A", virtualMachineSettings_.getOriginMachineValue(3));
                properties.setProperty("ORIGIN_MACHINE_CHECK_B", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(4)));
                properties.setProperty("ORIGIN_MACHINE_B", virtualMachineSettings_.getOriginMachineValue(4));
                properties.setProperty("ORIGIN_MACHINE_CHECK_C", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(5)));
                properties.setProperty("ORIGIN_MACHINE_C", virtualMachineSettings_.getOriginMachineValue(5));
                properties.setProperty("ORIGIN_MACHINE_CHECK_U", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(6)));
                properties.setProperty("ORIGIN_MACHINE_U", virtualMachineSettings_.getOriginMachineValue(6));
                properties.setProperty("ORIGIN_MACHINE_CHECK_V", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(7)));
                properties.setProperty("ORIGIN_MACHINE_V", virtualMachineSettings_.getOriginMachineValue(7));
                properties.setProperty("ORIGIN_MACHINE_CHECK_W", Boolean.toString(virtualMachineSettings_.getOriginMachineCheck(8)));
                properties.setProperty("ORIGIN_MACHINE_W", virtualMachineSettings_.getOriginMachineValue(8));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(0)));
                properties.setProperty("COORDINATE_EXTERNAL_X", virtualMachineSettings_.getCoordinateExtValue(0));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(1)));
                properties.setProperty("COORDINATE_EXTERNAL_Y", virtualMachineSettings_.getCoordinateExtValue(1));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(2)));
                properties.setProperty("COORDINATE_EXTERNAL_Z", virtualMachineSettings_.getCoordinateExtValue(2));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(3)));
                properties.setProperty("COORDINATE_EXTERNAL_A", virtualMachineSettings_.getCoordinateExtValue(3));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(4)));
                properties.setProperty("COORDINATE_EXTERNAL_B", virtualMachineSettings_.getCoordinateExtValue(4));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(5)));
                properties.setProperty("COORDINATE_EXTERNAL_C", virtualMachineSettings_.getCoordinateExtValue(5));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(6)));
                properties.setProperty("COORDINATE_EXTERNAL_U", virtualMachineSettings_.getCoordinateExtValue(6));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(7)));
                properties.setProperty("COORDINATE_EXTERNAL_V", virtualMachineSettings_.getCoordinateExtValue(7));
                properties.setProperty("COORDINATE_EXTERNAL_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateExtCheck(8)));
                properties.setProperty("COORDINATE_EXTERNAL_W", virtualMachineSettings_.getCoordinateExtValue(8));
                properties.setProperty("COORDINATE_G92_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(0)));
                properties.setProperty("COORDINATE_G92_X", virtualMachineSettings_.getCoordinateG92Value(0));
                properties.setProperty("COORDINATE_G92_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(1)));
                properties.setProperty("COORDINATE_G92_Y", virtualMachineSettings_.getCoordinateG92Value(1));
                properties.setProperty("COORDINATE_G92_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(2)));
                properties.setProperty("COORDINATE_G92_Z", virtualMachineSettings_.getCoordinateG92Value(2));
                properties.setProperty("COORDINATE_G92_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(3)));
                properties.setProperty("COORDINATE_G92_A", virtualMachineSettings_.getCoordinateG92Value(3));
                properties.setProperty("COORDINATE_G92_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(4)));
                properties.setProperty("COORDINATE_G92_B", virtualMachineSettings_.getCoordinateG92Value(4));
                properties.setProperty("COORDINATE_G92_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(5)));
                properties.setProperty("COORDINATE_G92_C", virtualMachineSettings_.getCoordinateG92Value(5));
                properties.setProperty("COORDINATE_G92_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(6)));
                properties.setProperty("COORDINATE_G92_U", virtualMachineSettings_.getCoordinateG92Value(6));
                properties.setProperty("COORDINATE_G92_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(7)));
                properties.setProperty("COORDINATE_G92_V", virtualMachineSettings_.getCoordinateG92Value(7));
                properties.setProperty("COORDINATE_G92_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG92Check(8)));
                properties.setProperty("COORDINATE_G92_W", virtualMachineSettings_.getCoordinateG92Value(8));
                properties.setProperty("COORDINATE_TOOL_CHECK", Boolean.toString(virtualMachineSettings_.getCoordinateToolCheck()));
                properties.setProperty("COORDINATE_MIRROR_CHECK", Boolean.toString(virtualMachineSettings_.getCoordinateMirrorCheck()));
                properties.setProperty("COORDINATE_G54_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(0)));
                properties.setProperty("COORDINATE_G54_X", virtualMachineSettings_.getCoordinateG54Value(0));
                properties.setProperty("COORDINATE_G54_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(1)));
                properties.setProperty("COORDINATE_G54_Y", virtualMachineSettings_.getCoordinateG54Value(1));
                properties.setProperty("COORDINATE_G54_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(2)));
                properties.setProperty("COORDINATE_G54_Z", virtualMachineSettings_.getCoordinateG54Value(2));
                properties.setProperty("COORDINATE_G54_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(3)));
                properties.setProperty("COORDINATE_G54_A", virtualMachineSettings_.getCoordinateG54Value(3));
                properties.setProperty("COORDINATE_G54_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(4)));
                properties.setProperty("COORDINATE_G54_B", virtualMachineSettings_.getCoordinateG54Value(4));
                properties.setProperty("COORDINATE_G54_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(5)));
                properties.setProperty("COORDINATE_G54_C", virtualMachineSettings_.getCoordinateG54Value(5));
                properties.setProperty("COORDINATE_G54_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(6)));
                properties.setProperty("COORDINATE_G54_U", virtualMachineSettings_.getCoordinateG54Value(6));
                properties.setProperty("COORDINATE_G54_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(7)));
                properties.setProperty("COORDINATE_G54_V", virtualMachineSettings_.getCoordinateG54Value(7));
                properties.setProperty("COORDINATE_G54_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG54Check(8)));
                properties.setProperty("COORDINATE_G54_W", virtualMachineSettings_.getCoordinateG54Value(8));
                properties.setProperty("COORDINATE_G55_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(0)));
                properties.setProperty("COORDINATE_G55_X", virtualMachineSettings_.getCoordinateG55Value(0));
                properties.setProperty("COORDINATE_G55_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(1)));
                properties.setProperty("COORDINATE_G55_Y", virtualMachineSettings_.getCoordinateG55Value(1));
                properties.setProperty("COORDINATE_G55_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(2)));
                properties.setProperty("COORDINATE_G55_Z", virtualMachineSettings_.getCoordinateG55Value(2));
                properties.setProperty("COORDINATE_G55_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(3)));
                properties.setProperty("COORDINATE_G55_A", virtualMachineSettings_.getCoordinateG55Value(3));
                properties.setProperty("COORDINATE_G55_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(4)));
                properties.setProperty("COORDINATE_G55_B", virtualMachineSettings_.getCoordinateG55Value(4));
                properties.setProperty("COORDINATE_G55_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(5)));
                properties.setProperty("COORDINATE_G55_C", virtualMachineSettings_.getCoordinateG55Value(5));
                properties.setProperty("COORDINATE_G55_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(6)));
                properties.setProperty("COORDINATE_G55_U", virtualMachineSettings_.getCoordinateG55Value(6));
                properties.setProperty("COORDINATE_G55_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(7)));
                properties.setProperty("COORDINATE_G55_V", virtualMachineSettings_.getCoordinateG55Value(7));
                properties.setProperty("COORDINATE_G55_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG55Check(8)));
                properties.setProperty("COORDINATE_G55_W", virtualMachineSettings_.getCoordinateG55Value(8));
                properties.setProperty("COORDINATE_G56_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(0)));
                properties.setProperty("COORDINATE_G56_X", virtualMachineSettings_.getCoordinateG56Value(0));
                properties.setProperty("COORDINATE_G56_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(1)));
                properties.setProperty("COORDINATE_G56_Y", virtualMachineSettings_.getCoordinateG56Value(1));
                properties.setProperty("COORDINATE_G56_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(2)));
                properties.setProperty("COORDINATE_G56_Z", virtualMachineSettings_.getCoordinateG56Value(2));
                properties.setProperty("COORDINATE_G56_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(3)));
                properties.setProperty("COORDINATE_G56_A", virtualMachineSettings_.getCoordinateG56Value(3));
                properties.setProperty("COORDINATE_G56_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(4)));
                properties.setProperty("COORDINATE_G56_B", virtualMachineSettings_.getCoordinateG56Value(4));
                properties.setProperty("COORDINATE_G56_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(5)));
                properties.setProperty("COORDINATE_G56_C", virtualMachineSettings_.getCoordinateG56Value(5));
                properties.setProperty("COORDINATE_G56_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(6)));
                properties.setProperty("COORDINATE_G56_U", virtualMachineSettings_.getCoordinateG56Value(6));
                properties.setProperty("COORDINATE_G56_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(7)));
                properties.setProperty("COORDINATE_G56_V", virtualMachineSettings_.getCoordinateG56Value(7));
                properties.setProperty("COORDINATE_G56_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG56Check(8)));
                properties.setProperty("COORDINATE_G56_W", virtualMachineSettings_.getCoordinateG56Value(8));
                properties.setProperty("COORDINATE_G57_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(0)));
                properties.setProperty("COORDINATE_G57_X", virtualMachineSettings_.getCoordinateG57Value(0));
                properties.setProperty("COORDINATE_G57_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(1)));
                properties.setProperty("COORDINATE_G57_Y", virtualMachineSettings_.getCoordinateG57Value(1));
                properties.setProperty("COORDINATE_G57_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(2)));
                properties.setProperty("COORDINATE_G57_Z", virtualMachineSettings_.getCoordinateG57Value(2));
                properties.setProperty("COORDINATE_G57_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(3)));
                properties.setProperty("COORDINATE_G57_A", virtualMachineSettings_.getCoordinateG57Value(3));
                properties.setProperty("COORDINATE_G57_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(4)));
                properties.setProperty("COORDINATE_G57_B", virtualMachineSettings_.getCoordinateG57Value(4));
                properties.setProperty("COORDINATE_G57_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(5)));
                properties.setProperty("COORDINATE_G57_C", virtualMachineSettings_.getCoordinateG57Value(5));
                properties.setProperty("COORDINATE_G57_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(6)));
                properties.setProperty("COORDINATE_G57_U", virtualMachineSettings_.getCoordinateG57Value(6));
                properties.setProperty("COORDINATE_G57_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(7)));
                properties.setProperty("COORDINATE_G57_V", virtualMachineSettings_.getCoordinateG57Value(7));
                properties.setProperty("COORDINATE_G57_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG57Check(8)));
                properties.setProperty("COORDINATE_G57_W", virtualMachineSettings_.getCoordinateG57Value(8));
                properties.setProperty("COORDINATE_G58_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(0)));
                properties.setProperty("COORDINATE_G58_X", virtualMachineSettings_.getCoordinateG58Value(0));
                properties.setProperty("COORDINATE_G58_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(1)));
                properties.setProperty("COORDINATE_G58_Y", virtualMachineSettings_.getCoordinateG58Value(1));
                properties.setProperty("COORDINATE_G58_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(2)));
                properties.setProperty("COORDINATE_G58_Z", virtualMachineSettings_.getCoordinateG58Value(2));
                properties.setProperty("COORDINATE_G58_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(3)));
                properties.setProperty("COORDINATE_G58_A", virtualMachineSettings_.getCoordinateG58Value(3));
                properties.setProperty("COORDINATE_G58_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(4)));
                properties.setProperty("COORDINATE_G58_B", virtualMachineSettings_.getCoordinateG58Value(4));
                properties.setProperty("COORDINATE_G58_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(5)));
                properties.setProperty("COORDINATE_G58_C", virtualMachineSettings_.getCoordinateG58Value(5));
                properties.setProperty("COORDINATE_G58_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(6)));
                properties.setProperty("COORDINATE_G58_U", virtualMachineSettings_.getCoordinateG58Value(6));
                properties.setProperty("COORDINATE_G58_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(7)));
                properties.setProperty("COORDINATE_G58_V", virtualMachineSettings_.getCoordinateG58Value(7));
                properties.setProperty("COORDINATE_G58_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG58Check(8)));
                properties.setProperty("COORDINATE_G58_W", virtualMachineSettings_.getCoordinateG58Value(8));
                properties.setProperty("COORDINATE_G59_CHECK_X", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(0)));
                properties.setProperty("COORDINATE_G59_X", virtualMachineSettings_.getCoordinateG59Value(0));
                properties.setProperty("COORDINATE_G59_CHECK_Y", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(1)));
                properties.setProperty("COORDINATE_G59_Y", virtualMachineSettings_.getCoordinateG59Value(1));
                properties.setProperty("COORDINATE_G59_CHECK_Z", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(2)));
                properties.setProperty("COORDINATE_G59_Z", virtualMachineSettings_.getCoordinateG59Value(2));
                properties.setProperty("COORDINATE_G59_CHECK_A", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(3)));
                properties.setProperty("COORDINATE_G59_A", virtualMachineSettings_.getCoordinateG59Value(3));
                properties.setProperty("COORDINATE_G59_CHECK_B", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(4)));
                properties.setProperty("COORDINATE_G59_B", virtualMachineSettings_.getCoordinateG59Value(4));
                properties.setProperty("COORDINATE_G59_CHECK_C", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(5)));
                properties.setProperty("COORDINATE_G59_C", virtualMachineSettings_.getCoordinateG59Value(5));
                properties.setProperty("COORDINATE_G59_CHECK_U", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(6)));
                properties.setProperty("COORDINATE_G59_U", virtualMachineSettings_.getCoordinateG59Value(6));
                properties.setProperty("COORDINATE_G59_CHECK_V", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(7)));
                properties.setProperty("COORDINATE_G59_V", virtualMachineSettings_.getCoordinateG59Value(7));
                properties.setProperty("COORDINATE_G59_CHECK_W", Boolean.toString(virtualMachineSettings_.getCoordinateG59Check(8)));
                properties.setProperty("COORDINATE_G59_W", virtualMachineSettings_.getCoordinateG59Value(8));

                // tool offset
                properties.setProperty("TOOL_OFFSET", virtualMachineSettings_.getToolOffsetValue());

                // variables
                properties.setProperty("VARIABLES", virtualMachineSettings_.getVariablesValue());

                // tool change
                properties.setProperty("TOOL_CHANGE_PROGRAM_CHECK", Boolean.toString(virtualMachineSettings_.getToolChangeProgramCheck()));
                properties.setProperty("TOOL_CHANGE_XY_CHECK", Boolean.toString(virtualMachineSettings_.getToolChangeXYCheck()));
                properties.setProperty("TOOL_CHANGE_PROGRAM", virtualMachineSettings_.getToolChangeProgramValue());
                properties.setProperty("TOOL_CHANGE_MCODE_CHECK", Boolean.toString(virtualMachineSettings_.getToolChangeMCodeCheck()));
                properties.setProperty("TOOL_CHANGE_MCODE", virtualMachineSettings_.getToolChangeMCodeValue());

                // skip function
                properties.setProperty("SKIP_FUNCTION_PROGRAM_CHECK", Boolean.toString(virtualMachineSettings_.getSkipFunctionProgramCheck()));
                properties.setProperty("SKIP_FUNCTION_PROGRAM", virtualMachineSettings_.getSkipFunctionProgramValue());

                // ladder
                properties.setProperty("LADDER_N1", virtualMachineSettings_.getLadderValue(0));
                properties.setProperty("LADDER_N2", virtualMachineSettings_.getLadderValue(1));
                properties.setProperty("LADDER_G1", virtualMachineSettings_.getLadderValue(2));
                properties.setProperty("LADDER_G2", virtualMachineSettings_.getLadderValue(3));
                properties.setProperty("LADDER_X1", virtualMachineSettings_.getLadderValue(4));
                properties.setProperty("LADDER_X2", virtualMachineSettings_.getLadderValue(5));
                properties.setProperty("LADDER_Y1", virtualMachineSettings_.getLadderValue(6));
                properties.setProperty("LADDER_Y2", virtualMachineSettings_.getLadderValue(7));
                properties.setProperty("LADDER_Z1", virtualMachineSettings_.getLadderValue(8));
                properties.setProperty("LADDER_Z2", virtualMachineSettings_.getLadderValue(9));
                properties.setProperty("LADDER_A1", virtualMachineSettings_.getLadderValue(10));
                properties.setProperty("LADDER_A2", virtualMachineSettings_.getLadderValue(11));
                properties.setProperty("LADDER_B1", virtualMachineSettings_.getLadderValue(12));
                properties.setProperty("LADDER_B2", virtualMachineSettings_.getLadderValue(13));
                properties.setProperty("LADDER_C1", virtualMachineSettings_.getLadderValue(14));
                properties.setProperty("LADDER_C2", virtualMachineSettings_.getLadderValue(15));
                properties.setProperty("LADDER_U1", virtualMachineSettings_.getLadderValue(16));
                properties.setProperty("LADDER_U2", virtualMachineSettings_.getLadderValue(17));
                properties.setProperty("LADDER_V1", virtualMachineSettings_.getLadderValue(18));
                properties.setProperty("LADDER_V2", virtualMachineSettings_.getLadderValue(19));
                properties.setProperty("LADDER_W1", virtualMachineSettings_.getLadderValue(20));
                properties.setProperty("LADDER_W2", virtualMachineSettings_.getLadderValue(21));
                properties.setProperty("LADDER_M1", virtualMachineSettings_.getLadderValue(22));
                properties.setProperty("LADDER_M2", virtualMachineSettings_.getLadderValue(23));
                properties.setProperty("LADDER_R1", virtualMachineSettings_.getLadderValue(24));
                properties.setProperty("LADDER_R2", virtualMachineSettings_.getLadderValue(25));
                properties.setProperty("LADDER_I1", virtualMachineSettings_.getLadderValue(26));
                properties.setProperty("LADDER_I2", virtualMachineSettings_.getLadderValue(27));
                properties.setProperty("LADDER_J1", virtualMachineSettings_.getLadderValue(28));
                properties.setProperty("LADDER_J2", virtualMachineSettings_.getLadderValue(29));
                properties.setProperty("LADDER_K1", virtualMachineSettings_.getLadderValue(30));
                properties.setProperty("LADDER_K2", virtualMachineSettings_.getLadderValue(31));
                properties.setProperty("LADDER_F1", virtualMachineSettings_.getLadderValue(32));
                properties.setProperty("LADDER_F2", virtualMachineSettings_.getLadderValue(33));
                properties.setProperty("LADDER_S1", virtualMachineSettings_.getLadderValue(34));
                properties.setProperty("LADDER_S2", virtualMachineSettings_.getLadderValue(35));
                properties.setProperty("LADDER_T1", virtualMachineSettings_.getLadderValue(36));
                properties.setProperty("LADDER_T2", virtualMachineSettings_.getLadderValue(37));

                // option
                properties.setProperty("OPTION_OPTIMIZATION", Boolean.toString(virtualMachineSettings_.getOptionOptimization()));
                properties.setProperty("OPTION_EX_COMMENT", Boolean.toString(virtualMachineSettings_.getOptionExComment()));
                properties.setProperty("OPTION_DISABLE_3003_3004", Boolean.toString(virtualMachineSettings_.getOptionDisable30033004()));
                properties.setProperty("OPTION_REPLACE_3006_M0", Boolean.toString(virtualMachineSettings_.getOptionReplace3006M0()));
                properties.setProperty("OPTION_ONLY_S", Boolean.toString(virtualMachineSettings_.getOptionOnlyS()));
                properties.setProperty("OPTION_RS274NGC", Boolean.toString(virtualMachineSettings_.getOptionRS274NGC()));
                properties.setProperty("OPTION_DEBUG_JSON", Boolean.toString(virtualMachineSettings_.getOptionDebugJson()));
                properties.setProperty("OPTION_MAX_FEED_RATE", virtualMachineSettings_.getOptionMaxFeedRate());
                properties.setProperty("OPTION_MAX_REVOLUTION", virtualMachineSettings_.getOptionMaxRevolution());
                properties.setProperty("OPTION_START_PROGRAM", virtualMachineSettings_.getOptionStartProgram());
                properties.setProperty("OPTION_BLOCK_PROGRAM", virtualMachineSettings_.getOptionBlockProgram());
                properties.setProperty("OPTION_END_PROGRAM", virtualMachineSettings_.getOptionEndProgram());

                properties.storeToXML(Files.newOutputStream(propertyFile), DesignEnums.GCODE_FX.toString());
                return true;
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
        }
        return false;
    }

    private void fileNotFound(Path file) {
        if (file != null) {
            writeLog(DesignEnums.FILE_NOT_FOUND.toString() + ": " + file.toString(), true);
        } else {
            writeLog(DesignEnums.FILE_NOT_FOUND.toString(), true);
        }
    }

    private void fileCanNotRead(Path file) {
        if (file != null) {
            writeLog(DesignEnums.FILE_CAN_NOT_READ.toString() + ": " + file.toString(), true);
        } else {
            writeLog(DesignEnums.FILE_CAN_NOT_READ.toString(), true);
        }
    }

    private void fileCanNotWrite(Path file) {
        if (file != null) {
            writeLog(DesignEnums.FILE_CAN_NOT_WRITE.toString() + ": " + file.toString(), true);
        } else {
            writeLog(DesignEnums.FILE_CAN_NOT_WRITE.toString(), true);
        }
    }

    private void fileNotFile(Path file) {
        if (file != null) {
            writeLog(DesignEnums.FILE_NOT_FILE.toString() + ": " + file.toString(), true);
        } else {
            writeLog(DesignEnums.FILE_NOT_FILE.toString(), true);
        }
    }

    private void fileNotDirectory(Path file) {
        if (file != null) {
            writeLog(DesignEnums.FILE_NOT_DIRECTORY.toString() + ": " + file.toString(), true);
        } else {
            writeLog(DesignEnums.FILE_NOT_DIRECTORY.toString(), true);
        }
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
                    fileNotFound(filePath);
                }
            }
        } else if (response == ButtonType.YES) {
            webEditor_.fileSave();
            file = webEditor_.fileOpen(filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound(filePath);
                }
            }
        } else if (response == ButtonType.NO) {
            file = webEditor_.fileOpen(filePath);
            if (file == null) {
                if (filePath != null) {
                    fileNotFound(filePath);
                }
            }
        }
    }

    private void showLicenses() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        TextArea textAera = new TextArea();
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("LICENSES"), "UTF-8"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
        }
        textAera.setText(stringBuilder.toString());
        if (!icons_.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
        }
        alert.setResizable(true);
        alert.setTitle(DesignEnums.HELP_SHOW_LICENSES.toString());
        alert.getDialogPane().setHeaderText(null);
        textAera.setEditable(false);
        textAera.setWrapText(true);
        alert.getDialogPane().setContent(textAera);
        alert.show();
    }

    private void showVersion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        StringBuilder version = new StringBuilder();

        version.append(DesignEnums.VERSION.toString()).append(" ").append(GcodeFX.class.getPackage().getImplementationVersion());

        if (!icons_.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
        }
        alert.setResizable(true);
        alert.setTitle(DesignEnums.HELP_SHOW_VERSION.toString());
        alert.setHeaderText(DesignEnums.GCODE_FX.toString());
        alert.setContentText(version.toString());
        alert.show();
    }

    private void putChange(GcodeVirtualMachine.CHG chg, boolean bln) {
        change_.put(chg, bln);
        gcodeVirtualMachine_.putChange(chg, bln);
    }

    private boolean loadVirtualMachine(Path file) {
        if (file != null) {
            if (Files.exists(file)) {
                if (Files.isRegularFile(file)) {
                    if (Files.isReadable(file)) {
                        openFile_ = file;
                        initProperties();
                        loadProperties(file);
                        virtualMachineSettings_.setVirtualMachineNameValue(JavaLibrary.removeFileExtension(file), true);
                        initSerial();
                        runInitDesign();
                        runViewRecentFile(true);
                        if (virtualMachineSettingsController_ != null) {
                            virtualMachineSettingsController_.runValueUpdate();
                            virtualMachineSettingsController_.runViewToolChange();
                            virtualMachineSettingsController_.runViewSkipFunction();
                        }
                        return true;
                    } else {
                        fileCanNotRead(file);
                    }
                } else {
                    fileNotFile(file);
                }
            } else {
                fileNotFound(file);
            }
        }
        return false;
    }

    private boolean saveVirtualMachine(Path file) {
        if (file != null) {
            try {
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                if (Files.isWritable(file.getParent())) {
                    openFile_ = file;
                    virtualMachineSettings_.setVirtualMachineNameValue(JavaLibrary.removeFileExtension(file), true);
                    saveProperties(file);
                    return true;
                } else {
                    fileCanNotWrite(file);
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            }
        }
        return false;
    }

    synchronized private void serialOpen() {
        if (!serial_.isOwned()) {
            if (serial_.open(baseSettings_.getSerialPortValue(), baseSettings_.getSerialBaudrateValue(), baseSettings_.getSerialDataBitsValue(), baseSettings_.getSerialStopBitsValue(), baseSettings_.getSerialParityValue(), baseSettings_.getSerialBufferLimitValue(), baseSettings_.getSerialDelayValue(), baseSettings_.isSerialCharacterCheck(), baseSettings_.isSerialObserveCTSCheck(), baseSettings_.isSerialObserveDSRCheck(), baseSettings_.isSerialObserveDC2DC4Check())) {
                if (Platform.isFxApplicationThread()) {
                    runSerial();
                } else {
                    Platform.runLater(() -> {
                        runSerial();
                    });
                }
                runViewSerial(false);
            } else {
                runViewSerial(true);
            }
        } else {
            runViewSerial(true);
        }
    }

    private void runSerial() {
        if (serial_.getState() == Worker.State.READY) {
            serial_.start();
        } else {
            serial_.restart();
        }
    }

    private void serialClose() {
        if (serial_.isOwned()) {
            serial_.cancel();
            serial_.close();
            baseSettings_.setSerial(false);
            runViewSerial(true);
        }
    }

    /**
     *
     * @param stage
     * @param icons
     * @param args
     */
    public void startUp(Stage stage, List<Image> icons, Application.Parameters args) {
        stage_ = stage;
        icons_ = icons;

        // current path
        currentPath_ = Paths.get("properties.xml");
        if (Files.exists(currentPath_)) {
            currentPath_ = currentPath_.toAbsolutePath().getParent();
        } else {
            currentPath_ = Paths.get(System.getProperty("java.class.path").split(";")[0]).toAbsolutePath().getParent();
        }

        // web path
        webPath_ = currentPath_.resolve("web");
        if (!Files.exists(webPath_)) {
            try {
                Files.createDirectories(webPath_);
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
            }
        }

        // log path
        logPath_ = currentPath_.resolve("log");

        // debug path
        debugPath_ = currentPath_.resolve("debug");

        // load properties
        initProperties();
        loadProperties(currentPath_.resolve("properties.xml"));

        // serial
        initSerial();

        // design
        addEventDesign();
        runInitDesign();
        runViewRecentFile(true);

        // ladder
        try {
            stageLadder_ = new Stage(StageStyle.DECORATED);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignLadders.fxml"));
            Parent root = (Parent) loader.load();
            laddersController_ = (DesignLaddersController) loader.getController();
            stageLadder_.setScene(new Scene(root));
            if (!icons_.isEmpty()) {
                stageLadder_.getIcons().addAll(icons_);
            }
            laddersController_.startUp(stageLadder_, icons_, currentPath_);
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
        }

        // soem
        soem_ = new Soem();

        // web editor
        Path startFile = null;
        if (args.getUnnamed().size() == 1) {
            startFile = Paths.get(args.getUnnamed().get(0));
            if (!Files.exists(startFile) || !Files.isRegularFile(startFile) || !Files.isReadable(startFile)) {
                startFile = null;
            }
        }
        webEditor_.setGcodeVirtualMachine(gcodeVirtualMachine_);
        webEditor_.setGcodeInterpreter(gcodeInterpreter_);
        webEditor_.setStageSettings(stageSettings_);
        webEditor_.setBaseSettings(baseSettings_);
        webEditor_.setVirtualMachineSettings(virtualMachineSettings_);
        webEditor_.setLadders(laddersController_.getLadders());
        webEditor_.setSoem(soem_);
        webEditor_.startUp(stage_, icons_, currentPath_, webPath_, startFile);

        // web
        try {
            stageWeb_ = new Stage(StageStyle.DECORATED);
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/DesignWeb.fxml"));
            Parent root = (Parent) loader.load();
            webController_ = (DesignWebController) loader.getController();
            stageWeb_.setScene(new Scene(root));
            if (!icons_.isEmpty()) {
                stageWeb_.getIcons().addAll(icons_);
            }
            webController_.setWebEditor(webEditor_);
            webController_.setGcodeVirtualMachine(gcodeVirtualMachine_);
            webController_.setGcodeInterpreter(gcodeInterpreter_);
            webController_.setStageSettings(stageSettings_);
            webController_.setBaseSettings(baseSettings_);
            webController_.setVirtualMachineSettings(virtualMachineSettings_);
            webController_.setLadders(laddersController_.getLadders());
            webController_.setSoem(soem_);
            webController_.startUp(stageWeb_, icons_, webPath_);
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.CONTROLLER.toString(), ex);
        }
    }

    /**
     *
     * @return
     */
    public boolean cleanUp() {
        try {
            if (!stageSettings_.isExit()) {
                // save check
                ButtonType response = fileSavedCheck();
                if (response == ButtonType.YES) {
                    webEditor_.fileSave();
                } else if ((response == ButtonType.CANCEL) || (response == ButtonType.CLOSE)) {
                    return false;
                }

                // ladder
                if (!laddersController_.cleanUp()) {
                    return false;
                }
                stageLadder_.close();

                // web
                webController_.cleanUp();
                stageWeb_.close();

                // virtual machine
                gcodeVirtualMachine_.removeGcodeVirtualMachineListener(this);
                gcodeVirtualMachine_.cancel();

                // web editor
                webEditor_.removeWebEditorListener(this);
                webEditor_.cleanUp();

                // serial
                serial_.removeSerialListener(this);
                serial_.cancel();
                serial_.close();

                // save properties
                saveProperties(currentPath_.resolve("properties.xml"));

                // console
                Console.close();

                // exit
                stageSettings_.setExit(true);
            }
        } catch (NoClassDefFoundError ex) {
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Console.setTextArea(txtConsole);

        stageSettings_ = new StageSettings();
        baseSettings_ = new BaseSettings();
        virtualMachineSettings_ = new VirtualMachineSettings();

        change_ = new HashMap<>();
        webEditor_ = new WebEditor(webView);
        webEditor_.addWebEditorListener(this);
        gcodeVirtualMachine_ = new GcodeVirtualMachine();
        gcodeVirtualMachine_.addGcodeVirtualMachineListener(this);
        gcodeInterpreter_ = gcodeVirtualMachine_.getGcodeInterpreter();
        serial_ = new Serial(webEditor_);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

        stageSettings_.setExit(false);
        baseSettings_.setSerial(false);
        virtualMachineSettings_.setRunning(false);
        virtualMachineSettings_.setSettings(false);
    }

    /**
     *
     */
    @Override
    public void startGcodeVirtualMachine() {
        virtualMachineSettings_.setRunning(true);
        runViewRunning(false);
    }

    /**
     *
     */
    @Override
    public void abnormalEndGcodeVirtualMachine() {
        writeLog(DesignEnums.ABNORMAL_END.toString(), true);
        virtualMachineSettings_.setRunning(false);
        runViewRunning(true);
        serial_.clearFlow();
        serial_.clear();
    }

    /**
     *
     */
    @Override
    public void errorEndGcodeVirtualMachine() {
        writeLog(DesignEnums.ERROR_END.toString(), true);
        virtualMachineSettings_.setRunning(false);
        runViewRunning(true);
        serial_.clearFlow();
        serial_.clear();
    }

    /**
     *
     */
    @Override
    public void normalEndGcodeVirtualMachine() {
        virtualMachineSettings_.setRunning(false);
        runViewRunning(true);
        serial_.clearFlow();
        serial_.clear();
    }

    @Override
    public void notifyChangedWebEditor(String getValue, boolean isClean) {
        webEditor_.update(getValue, isClean, false);
        putChange(GcodeVirtualMachine.CHG.PROGRAM, true);
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
        if (compile(false) < 0) {
            virtualMachineSettings_.setRunning(false);
            runViewRunning(true);
        }
    }

    @Override
    public void notifyFileNew() {
        putChange(GcodeVirtualMachine.CHG.PROGRAM, true);
    }

    @Override
    public void notifyFileOpen(String filename) {
        addRecentFile(filename);
        putChange(GcodeVirtualMachine.CHG.PROGRAM, true);
    }

    @Override
    public void notifyFileSave(String filename) {
        addRecentFile(filename);
        putChange(GcodeVirtualMachine.CHG.PROGRAM, true);
    }

    /**
     *
     */
    @Override
    public void startSerial() {
        if (compile(true) < 0) {
            virtualMachineSettings_.setRunning(false);
            runViewRunning(true);
            serial_.clearFlow();
        }
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(DesignEnums.CONTROLLER.toString(), msg, err);
    }
}
