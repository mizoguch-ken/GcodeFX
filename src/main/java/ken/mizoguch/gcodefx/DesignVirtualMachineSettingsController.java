/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 *
 * @author mizoguch-ken
 */
public class DesignVirtualMachineSettingsController implements Initializable {

    // tab
    @FXML
    private TabPane mainTabPane;
    // tab base
    @FXML
    private Tab tabBase;
    @FXML
    private Label lblVirtualMachine;
    @FXML
    private TextField txtVirtualMachineName;
    @FXML
    private Button btnVirtualMachineLoad;
    @FXML
    private Button btnVirtualMachineSave;
    @FXML
    private CheckBox chkLog;
    @FXML
    private CheckBox chkDebug;
    @FXML
    private Label lblSerialPort;
    @FXML
    private TextField txtSerialPort;
    @FXML
    private Button btnSerialPortList;
    @FXML
    private Button btnSerialOpen;
    @FXML
    private Button btnSerialClose;
    @FXML
    private Label lblSerialBaudrate;
    @FXML
    private TextField txtSerialBaudrate;
    @FXML
    private Label lblSerialDataBits;
    @FXML
    private ComboBox<String> cmbSerialDataBits;
    @FXML
    private Label lblSerialStopBits;
    @FXML
    private ComboBox<String> cmbSerialStopBits;
    @FXML
    private Label lblSerialParity;
    @FXML
    private ComboBox<String> cmbSerialParity;
    @FXML
    private Label lblSerialEOB;
    @FXML
    private ComboBox<String> cmbSerialEOB;
    @FXML
    private Label lblSerialBufferLimit;
    @FXML
    private TextField txtSerialBufferLimit;
    @FXML
    private Label lblSerialDelay;
    @FXML
    private TextField txtSerialDelay;
    @FXML
    private Label lblSerialStartCode;
    @FXML
    private ComboBox<String> cmbSerialStartCode;
    @FXML
    private Label lblSerialEndCode;
    @FXML
    private ComboBox<String> cmbSerialEndCode;
    @FXML
    private CheckBox chkSerialCharacter;
    @FXML
    private CheckBox chkSerialObserveCTS;
    @FXML
    private CheckBox chkSerialObserveDSR;
    @FXML
    private CheckBox chkSerialObserveDC2DC4;
    @FXML
    private Label lblBackGroundFile;
    @FXML
    private TextField txtBackGroundFile1;
    @FXML
    private Button btnBackGroundFile1;
    @FXML
    private TextField txtBackGroundFile2;
    @FXML
    private Button btnBackGroundFile2;
    @FXML
    private TextField txtBackGroundFile3;
    @FXML
    private Button btnBackGroundFile3;
    @FXML
    private Label lblExternalSubProgramDirectory;
    @FXML
    private TextField txtExternalSubProgramDirectory;
    @FXML
    private Button btnExternalSubProgramDirectory;
    // tab gcode group
    @FXML
    private Tab tabGcodeGroup;
    @FXML
    private Label lblGcodeGroup0;
    @FXML
    private TextField txtGcodeGroup0;
    @FXML
    private Label lblGcodeGroup1;
    @FXML
    private TextField txtGcodeGroup1;
    @FXML
    private Label lblGcodeGroup2;
    @FXML
    private TextField txtGcodeGroup2;
    @FXML
    private Label lblGcodeGroup3;
    @FXML
    private TextField txtGcodeGroup3;
    @FXML
    private Label lblGcodeGroup4;
    @FXML
    private TextField txtGcodeGroup4;
    @FXML
    private Label lblGcodeGroup5;
    @FXML
    private TextField txtGcodeGroup5;
    @FXML
    private Label lblGcodeGroup6;
    @FXML
    private TextField txtGcodeGroup6;
    @FXML
    private Label lblGcodeGroup7;
    @FXML
    private TextField txtGcodeGroup7;
    @FXML
    private Label lblGcodeGroup8;
    @FXML
    private TextField txtGcodeGroup8;
    @FXML
    private Label lblGcodeGroup9;
    @FXML
    private TextField txtGcodeGroup9;
    @FXML
    private Label lblGcodeGroup10;
    @FXML
    private TextField txtGcodeGroup10;
    @FXML
    private Label lblGcodeGroup11;
    @FXML
    private TextField txtGcodeGroup11;
    @FXML
    private Label lblGcodeGroup12;
    @FXML
    private TextField txtGcodeGroup12;
    @FXML
    private Label lblGcodeGroup13;
    @FXML
    private TextField txtGcodeGroup13;
    @FXML
    private Label lblGcodeGroup14;
    @FXML
    private TextField txtGcodeGroup14;
    @FXML
    private Label lblGcodeGroup15;
    @FXML
    private TextField txtGcodeGroup15;
    @FXML
    private Label lblGcodeGroup16;
    @FXML
    private TextField txtGcodeGroup16;
    @FXML
    private Label lblGcodeGroup17;
    @FXML
    private TextField txtGcodeGroup17;
    @FXML
    private Label lblGcodeGroup18;
    @FXML
    private TextField txtGcodeGroup18;
    @FXML
    private Label lblGcodeGroup19;
    @FXML
    private TextField txtGcodeGroup19;
    @FXML
    private Label lblGcodeGroup20;
    @FXML
    private TextField txtGcodeGroup20;
    @FXML
    private Label lblGcodeGroup21;
    @FXML
    private TextField txtGcodeGroup21;
    @FXML
    private Label lblGcodeGroup22;
    @FXML
    private TextField txtGcodeGroup22;
    @FXML
    private Label lblGcodeGroup23;
    @FXML
    private TextField txtGcodeGroup23;
    @FXML
    private Label lblGcodeGroup24;
    @FXML
    private TextField txtGcodeGroup24;
    @FXML
    private Label lblGcodeGroup25;
    @FXML
    private TextField txtGcodeGroup25;
    @FXML
    private Label lblGcodeGroup26;
    @FXML
    private TextField txtGcodeGroup26;
    @FXML
    private Label lblGcodeGroup27;
    @FXML
    private TextField txtGcodeGroup27;
    @FXML
    private Label lblGcodeGroup28;
    @FXML
    private TextField txtGcodeGroup28;
    @FXML
    private Label lblGcodeGroup29;
    @FXML
    private TextField txtGcodeGroup29;
    @FXML
    private Label lblGcodeGroup30;
    @FXML
    private TextField txtGcodeGroup30;
    // tab code change
    @FXML
    private Tab tabCodeChange;
    @FXML
    private Label lblCodeChangeProgramCall;
    @FXML
    private TextArea txtCodeChangeProgramCall;
    @FXML
    private Label lblCodeChangeG;
    @FXML
    private TextArea txtCodeChangeG;
    @FXML
    private Label lblCodeChangeM;
    @FXML
    private TextArea txtCodeChangeM;
    @FXML
    private Label lblCodeChangeT;
    @FXML
    private TextArea txtCodeChangeT;
    @FXML
    private Label lblCodeChangeD;
    @FXML
    private TextArea txtCodeChangeD;
    @FXML
    private Label lblCodeChangeH;
    @FXML
    private TextArea txtCodeChangeH;
    // tab macro call
    @FXML
    private Tab tabMacroCall;
    @FXML
    private Label lblMacroCallG;
    @FXML
    private TextArea txtMacroCallG;
    @FXML
    private Label lblMacroCallM;
    @FXML
    private TextArea txtMacroCallM;
    // tab coordinate
    @FXML
    private Tab tabCoordinate;
    @FXML
    private Label lblOriginMachine;
    @FXML
    private CheckBox chkOriginMachineX;
    @FXML
    private TextField txtOriginMachineX;
    @FXML
    private CheckBox chkOriginMachineY;
    @FXML
    private TextField txtOriginMachineY;
    @FXML
    private CheckBox chkOriginMachineZ;
    @FXML
    private TextField txtOriginMachineZ;
    @FXML
    private CheckBox chkOriginMachineA;
    @FXML
    private TextField txtOriginMachineA;
    @FXML
    private CheckBox chkOriginMachineB;
    @FXML
    private TextField txtOriginMachineB;
    @FXML
    private CheckBox chkOriginMachineC;
    @FXML
    private TextField txtOriginMachineC;
    @FXML
    private CheckBox chkOriginMachineU;
    @FXML
    private TextField txtOriginMachineU;
    @FXML
    private CheckBox chkOriginMachineV;
    @FXML
    private TextField txtOriginMachineV;
    @FXML
    private CheckBox chkOriginMachineW;
    @FXML
    private TextField txtOriginMachineW;
    @FXML
    private Label lblCoordinateExt;
    @FXML
    private CheckBox chkCoordinateExtX;
    @FXML
    private TextField txtCoordinateExtX;
    @FXML
    private CheckBox chkCoordinateExtY;
    @FXML
    private TextField txtCoordinateExtY;
    @FXML
    private CheckBox chkCoordinateExtZ;
    @FXML
    private TextField txtCoordinateExtZ;
    @FXML
    private CheckBox chkCoordinateExtA;
    @FXML
    private TextField txtCoordinateExtA;
    @FXML
    private CheckBox chkCoordinateExtB;
    @FXML
    private TextField txtCoordinateExtB;
    @FXML
    private CheckBox chkCoordinateExtC;
    @FXML
    private TextField txtCoordinateExtC;
    @FXML
    private CheckBox chkCoordinateExtU;
    @FXML
    private TextField txtCoordinateExtU;
    @FXML
    private CheckBox chkCoordinateExtV;
    @FXML
    private TextField txtCoordinateExtV;
    @FXML
    private CheckBox chkCoordinateExtW;
    @FXML
    private TextField txtCoordinateExtW;
    @FXML
    private Label lblCoordinateG92;
    @FXML
    private CheckBox chkCoordinateG92X;
    @FXML
    private TextField txtCoordinateG92X;
    @FXML
    private CheckBox chkCoordinateG92Y;
    @FXML
    private TextField txtCoordinateG92Y;
    @FXML
    private CheckBox chkCoordinateG92Z;
    @FXML
    private TextField txtCoordinateG92Z;
    @FXML
    private CheckBox chkCoordinateG92A;
    @FXML
    private TextField txtCoordinateG92A;
    @FXML
    private CheckBox chkCoordinateG92B;
    @FXML
    private TextField txtCoordinateG92B;
    @FXML
    private CheckBox chkCoordinateG92C;
    @FXML
    private TextField txtCoordinateG92C;
    @FXML
    private CheckBox chkCoordinateG92U;
    @FXML
    private TextField txtCoordinateG92U;
    @FXML
    private CheckBox chkCoordinateG92V;
    @FXML
    private TextField txtCoordinateG92V;
    @FXML
    private CheckBox chkCoordinateG92W;
    @FXML
    private TextField txtCoordinateG92W;
    @FXML
    private Label lblCoordinateExtension;
    @FXML
    private CheckBox chkCoordinateTool;
    @FXML
    private CheckBox chkCoordinateMirror;
    @FXML
    private Label lblCoordinateG54;
    @FXML
    private CheckBox chkCoordinateG54X;
    @FXML
    private TextField txtCoordinateG54X;
    @FXML
    private CheckBox chkCoordinateG54Y;
    @FXML
    private TextField txtCoordinateG54Y;
    @FXML
    private CheckBox chkCoordinateG54Z;
    @FXML
    private TextField txtCoordinateG54Z;
    @FXML
    private CheckBox chkCoordinateG54A;
    @FXML
    private TextField txtCoordinateG54A;
    @FXML
    private CheckBox chkCoordinateG54B;
    @FXML
    private TextField txtCoordinateG54B;
    @FXML
    private CheckBox chkCoordinateG54C;
    @FXML
    private TextField txtCoordinateG54C;
    @FXML
    private CheckBox chkCoordinateG54U;
    @FXML
    private TextField txtCoordinateG54U;
    @FXML
    private CheckBox chkCoordinateG54V;
    @FXML
    private TextField txtCoordinateG54V;
    @FXML
    private CheckBox chkCoordinateG54W;
    @FXML
    private TextField txtCoordinateG54W;
    @FXML
    private Label lblCoordinateG55;
    @FXML
    private CheckBox chkCoordinateG55X;
    @FXML
    private TextField txtCoordinateG55X;
    @FXML
    private CheckBox chkCoordinateG55Y;
    @FXML
    private TextField txtCoordinateG55Y;
    @FXML
    private CheckBox chkCoordinateG55Z;
    @FXML
    private TextField txtCoordinateG55Z;
    @FXML
    private CheckBox chkCoordinateG55A;
    @FXML
    private TextField txtCoordinateG55A;
    @FXML
    private CheckBox chkCoordinateG55B;
    @FXML
    private TextField txtCoordinateG55B;
    @FXML
    private CheckBox chkCoordinateG55C;
    @FXML
    private TextField txtCoordinateG55C;
    @FXML
    private CheckBox chkCoordinateG55U;
    @FXML
    private TextField txtCoordinateG55U;
    @FXML
    private CheckBox chkCoordinateG55V;
    @FXML
    private TextField txtCoordinateG55V;
    @FXML
    private CheckBox chkCoordinateG55W;
    @FXML
    private TextField txtCoordinateG55W;
    @FXML
    private Label lblCoordinateG56;
    @FXML
    private CheckBox chkCoordinateG56X;
    @FXML
    private TextField txtCoordinateG56X;
    @FXML
    private CheckBox chkCoordinateG56Y;
    @FXML
    private TextField txtCoordinateG56Y;
    @FXML
    private CheckBox chkCoordinateG56Z;
    @FXML
    private TextField txtCoordinateG56Z;
    @FXML
    private CheckBox chkCoordinateG56A;
    @FXML
    private TextField txtCoordinateG56A;
    @FXML
    private CheckBox chkCoordinateG56B;
    @FXML
    private TextField txtCoordinateG56B;
    @FXML
    private CheckBox chkCoordinateG56C;
    @FXML
    private TextField txtCoordinateG56C;
    @FXML
    private CheckBox chkCoordinateG56U;
    @FXML
    private TextField txtCoordinateG56U;
    @FXML
    private CheckBox chkCoordinateG56V;
    @FXML
    private TextField txtCoordinateG56V;
    @FXML
    private CheckBox chkCoordinateG56W;
    @FXML
    private TextField txtCoordinateG56W;
    @FXML
    private Label lblCoordinateG57;
    @FXML
    private CheckBox chkCoordinateG57X;
    @FXML
    private TextField txtCoordinateG57X;
    @FXML
    private CheckBox chkCoordinateG57Y;
    @FXML
    private TextField txtCoordinateG57Y;
    @FXML
    private CheckBox chkCoordinateG57Z;
    @FXML
    private TextField txtCoordinateG57Z;
    @FXML
    private CheckBox chkCoordinateG57A;
    @FXML
    private TextField txtCoordinateG57A;
    @FXML
    private CheckBox chkCoordinateG57B;
    @FXML
    private TextField txtCoordinateG57B;
    @FXML
    private CheckBox chkCoordinateG57C;
    @FXML
    private TextField txtCoordinateG57C;
    @FXML
    private CheckBox chkCoordinateG57U;
    @FXML
    private TextField txtCoordinateG57U;
    @FXML
    private CheckBox chkCoordinateG57V;
    @FXML
    private TextField txtCoordinateG57V;
    @FXML
    private CheckBox chkCoordinateG57W;
    @FXML
    private TextField txtCoordinateG57W;
    @FXML
    private Label lblCoordinateG58;
    @FXML
    private CheckBox chkCoordinateG58X;
    @FXML
    private TextField txtCoordinateG58X;
    @FXML
    private CheckBox chkCoordinateG58Y;
    @FXML
    private TextField txtCoordinateG58Y;
    @FXML
    private CheckBox chkCoordinateG58Z;
    @FXML
    private TextField txtCoordinateG58Z;
    @FXML
    private CheckBox chkCoordinateG58A;
    @FXML
    private TextField txtCoordinateG58A;
    @FXML
    private CheckBox chkCoordinateG58B;
    @FXML
    private TextField txtCoordinateG58B;
    @FXML
    private CheckBox chkCoordinateG58C;
    @FXML
    private TextField txtCoordinateG58C;
    @FXML
    private CheckBox chkCoordinateG58U;
    @FXML
    private TextField txtCoordinateG58U;
    @FXML
    private CheckBox chkCoordinateG58V;
    @FXML
    private TextField txtCoordinateG58V;
    @FXML
    private CheckBox chkCoordinateG58W;
    @FXML
    private TextField txtCoordinateG58W;
    @FXML
    private Label lblCoordinateG59;
    @FXML
    private CheckBox chkCoordinateG59X;
    @FXML
    private TextField txtCoordinateG59X;
    @FXML
    private CheckBox chkCoordinateG59Y;
    @FXML
    private TextField txtCoordinateG59Y;
    @FXML
    private CheckBox chkCoordinateG59Z;
    @FXML
    private TextField txtCoordinateG59Z;
    @FXML
    private CheckBox chkCoordinateG59A;
    @FXML
    private TextField txtCoordinateG59A;
    @FXML
    private CheckBox chkCoordinateG59B;
    @FXML
    private TextField txtCoordinateG59B;
    @FXML
    private CheckBox chkCoordinateG59C;
    @FXML
    private TextField txtCoordinateG59C;
    @FXML
    private CheckBox chkCoordinateG59U;
    @FXML
    private TextField txtCoordinateG59U;
    @FXML
    private CheckBox chkCoordinateG59V;
    @FXML
    private TextField txtCoordinateG59V;
    @FXML
    private CheckBox chkCoordinateG59W;
    @FXML
    private TextField txtCoordinateG59W;
    // tab tool offset
    @FXML
    private Tab tabToolOffset;
    @FXML
    private Label lblToolOffset;
    @FXML
    private TextArea txtToolOffset;
    // tab variables
    @FXML
    private Tab tabVariables;
    @FXML
    private Label lblVariables;
    @FXML
    private TextArea txtVariables;
    // tab tool change
    @FXML
    private Tab tabToolChange;
    @FXML
    private CheckBox chkToolChangeProgram;
    @FXML
    private CheckBox chkToolChangeXY;
    @FXML
    private Label lblToolChangeProgram;
    @FXML
    private TextField txtToolChangeProgram;
    @FXML
    private CheckBox chkToolChangeMCode;
    @FXML
    private Label lblToolChangeMCode;
    @FXML
    private TextField txtToolChangeMCode;
    // tab skip function
    @FXML
    private Tab tabSkipFunction;
    @FXML
    private CheckBox chkSkipFunctionProgram;
    @FXML
    private Label lblSkipFunctionProgram;
    @FXML
    private TextField txtSkipFunctionProgram;
    // tab ladder
    @FXML
    private Tab tabLadder;
    @FXML
    private Label lblLadderSequenceCode;
    @FXML
    private Label lblLadderN;
    @FXML
    private TextField txtLadderN1;
    @FXML
    private TextField txtLadderN2;
    @FXML
    private Label lblLadderPreparatoryCode;
    @FXML
    private Label lblLadderG;
    @FXML
    private TextField txtLadderG1;
    @FXML
    private TextField txtLadderG2;
    @FXML
    private Label lblLadderDimensionCode;
    @FXML
    private Label lblLadderX;
    @FXML
    private TextField txtLadderX1;
    @FXML
    private TextField txtLadderX2;
    @FXML
    private Label lblLadderY;
    @FXML
    private TextField txtLadderY1;
    @FXML
    private TextField txtLadderY2;
    @FXML
    private Label lblLadderZ;
    @FXML
    private TextField txtLadderZ1;
    @FXML
    private TextField txtLadderZ2;
    @FXML
    private Label lblLadderA;
    @FXML
    private TextField txtLadderA1;
    @FXML
    private TextField txtLadderA2;
    @FXML
    private Label lblLadderB;
    @FXML
    private TextField txtLadderB1;
    @FXML
    private TextField txtLadderB2;
    @FXML
    private Label lblLadderC;
    @FXML
    private TextField txtLadderC1;
    @FXML
    private TextField txtLadderC2;
    @FXML
    private Label lblLadderU;
    @FXML
    private TextField txtLadderU1;
    @FXML
    private TextField txtLadderU2;
    @FXML
    private Label lblLadderV;
    @FXML
    private TextField txtLadderV1;
    @FXML
    private TextField txtLadderV2;
    @FXML
    private Label lblLadderW;
    @FXML
    private TextField txtLadderW1;
    @FXML
    private TextField txtLadderW2;
    @FXML
    private Label lblLadderAuxiliaryCode;
    @FXML
    private Label lblLadderM;
    @FXML
    private TextField txtLadderM1;
    @FXML
    private TextField txtLadderM2;
    @FXML
    private Label lblLadderR;
    @FXML
    private TextField txtLadderR1;
    @FXML
    private TextField txtLadderR2;
    @FXML
    private Label lblLadderI;
    @FXML
    private TextField txtLadderI1;
    @FXML
    private TextField txtLadderI2;
    @FXML
    private Label lblLadderJ;
    @FXML
    private TextField txtLadderJ1;
    @FXML
    private TextField txtLadderJ2;
    @FXML
    private Label lblLadderK;
    @FXML
    private TextField txtLadderK1;
    @FXML
    private TextField txtLadderK2;
    @FXML
    private Label lblLadderFeedrateCode;
    @FXML
    private Label lblLadderF;
    @FXML
    private TextField txtLadderF1;
    @FXML
    private TextField txtLadderF2;
    @FXML
    private Label lblLadderSpindleCode;
    @FXML
    private Label lblLadderS;
    @FXML
    private TextField txtLadderS1;
    @FXML
    private TextField txtLadderS2;
    @FXML
    private Label lblLadderToolCode;
    @FXML
    private Label lblLadderT;
    @FXML
    private TextField txtLadderT1;
    @FXML
    private TextField txtLadderT2;
    // tab option
    @FXML
    private Tab tabOption;
    @FXML
    private CheckBox chkOptionOptimization;
    @FXML
    private CheckBox chkOptionExComment;
    @FXML
    private CheckBox chkOptionDisable30033004;
    @FXML
    private CheckBox chkOptionReplace3006M0;
    @FXML
    private CheckBox chkOptionOnlyS;
    @FXML
    private CheckBox chkOptionRS274NGC;
    @FXML
    private CheckBox chkOptionDebugJson;
    @FXML
    private Label lblOptionMaxFeedRate;
    @FXML
    private TextArea txtOptionMaxFeedRate;
    @FXML
    private Label lblOptionMaxRevolution;
    @FXML
    private TextArea txtOptionMaxRevolution;
    @FXML
    private Label lblOptionStartProgram;
    @FXML
    private TextArea txtOptionStartProgram;
    @FXML
    private Label lblOptionBlockProgram;
    @FXML
    private TextArea txtOptionBlockProgram;
    @FXML
    private Label lblOptionEndProgram;
    @FXML
    private TextArea txtOptionEndProgram;

    private DesignController.VirtualMachineSettings virtualMachineSettings_;
    private DesignController.BaseSettings baseSettings_;

    private void addEventDesign() {
        // base tab
        btnVirtualMachineLoad.setOnAction((ActionEvent event) -> {
            virtualMachineSettings_.actionLoadVirtualMachine(null);
        });
        btnVirtualMachineSave.setOnAction((ActionEvent event) -> {
            if (virtualMachineSettings_.actionSaveVirtualMachine(null)) {
                txtVirtualMachineName.setText(virtualMachineSettings_.getVirtualMachineNameValue());
            }
        });
        chkLog.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setLogCheck(newValue, false);
        });
        chkDebug.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setDebugCheck(newValue, false);
        });
        txtSerialPort.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialPortValue(newValue, true);
        });
        btnSerialPortList.setOnAction((ActionEvent event) -> {
            if (baseSettings_.actionSerialPortList()) {
                txtSerialPort.setText(baseSettings_.getSerialPortValue());
            }
        });
        btnSerialOpen.setOnAction((ActionEvent event) -> {
            if (!baseSettings_.getSerialPortValue().isEmpty()) {
                baseSettings_.actionSerialOpen();
            }
        });
        btnSerialClose.setOnAction((ActionEvent event) -> {
            baseSettings_.actionSerialClose();
        });
        txtSerialBaudrate.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialBaudrateValue(newValue, false);
        });
        cmbSerialDataBits.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialDataBitsValue(newValue, false);
        });
        cmbSerialStopBits.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialStopBitsValue(newValue, false);
        });
        cmbSerialParity.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialParityValue(newValue, false);
        });
        cmbSerialEOB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialEOBValue(newValue, false);
        });
        cmbSerialStartCode.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialStartCodeValue(newValue, false);
        });
        txtSerialBufferLimit.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialBufferLimitValue(newValue, false);
        });
        txtSerialDelay.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialDelayValue(newValue, false);
        });
        cmbSerialEndCode.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            baseSettings_.setSerialEndCodeValue(newValue, false);
        });
        chkSerialCharacter.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            baseSettings_.setSerialCharacterCheck(newValue, false);
        });
        chkSerialObserveCTS.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            baseSettings_.setSerialObserveCTSCheck(newValue, false);
        });
        chkSerialObserveDSR.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            baseSettings_.setSerialObserveDSRCheck(newValue, false);
        });
        chkSerialObserveDC2DC4.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            baseSettings_.setSerialObserveDC2DC4Check(newValue, false);
        });
        txtBackGroundFile1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setBackGroundFileValue(0, newValue, false);
        });
        btnBackGroundFile1.setOnAction((ActionEvent event) -> {
            if (virtualMachineSettings_.actionBackGroundFileValue(0)) {
                txtBackGroundFile1.setText(virtualMachineSettings_.getBackGroundFileValue(0));
            }
        });
        txtBackGroundFile2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setBackGroundFileValue(1, newValue, false);
        });
        btnBackGroundFile2.setOnAction((ActionEvent event) -> {
            if (virtualMachineSettings_.actionBackGroundFileValue(1)) {
                txtBackGroundFile2.setText(virtualMachineSettings_.getBackGroundFileValue(1));
            }
        });
        txtBackGroundFile3.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setBackGroundFileValue(2, newValue, false);
        });
        btnBackGroundFile3.setOnAction((ActionEvent event) -> {
            if (virtualMachineSettings_.actionBackGroundFileValue(2)) {
                txtBackGroundFile3.setText(virtualMachineSettings_.getBackGroundFileValue(2));
            }
        });
        txtExternalSubProgramDirectory.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setExternalSubProgramDirectoryValue(newValue, false);
        });
        btnExternalSubProgramDirectory.setOnAction((ActionEvent event) -> {
            if (virtualMachineSettings_.actionExternalSubProgramDirectoryValue()) {
                txtExternalSubProgramDirectory.setText(virtualMachineSettings_.getExternalSubProgramDirectoryValue());
            }
        });

        // g code group tab
        txtGcodeGroup0.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(0, newValue);
        });
        txtGcodeGroup1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(1, newValue);
        });
        txtGcodeGroup2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(2, newValue);
        });
        txtGcodeGroup3.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(3, newValue);
        });
        txtGcodeGroup4.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(4, newValue);
        });
        txtGcodeGroup5.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(5, newValue);
        });
        txtGcodeGroup6.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(6, newValue);
        });
        txtGcodeGroup7.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(7, newValue);
        });
        txtGcodeGroup8.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(8, newValue);
        });
        txtGcodeGroup9.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(9, newValue);
        });
        txtGcodeGroup10.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(10, newValue);
        });
        txtGcodeGroup11.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(11, newValue);
        });
        txtGcodeGroup12.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(12, newValue);
        });
        txtGcodeGroup13.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(13, newValue);
        });
        txtGcodeGroup14.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(14, newValue);
        });
        txtGcodeGroup15.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(15, newValue);
        });
        txtGcodeGroup16.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(16, newValue);
        });
        txtGcodeGroup17.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(17, newValue);
        });
        txtGcodeGroup18.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(18, newValue);
        });
        txtGcodeGroup19.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(19, newValue);
        });
        txtGcodeGroup20.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(20, newValue);
        });
        txtGcodeGroup21.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(21, newValue);
        });
        txtGcodeGroup22.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(22, newValue);
        });
        txtGcodeGroup23.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(23, newValue);
        });
        txtGcodeGroup24.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(24, newValue);
        });
        txtGcodeGroup25.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(25, newValue);
        });
        txtGcodeGroup26.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(26, newValue);
        });
        txtGcodeGroup27.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(27, newValue);
        });
        txtGcodeGroup28.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(28, newValue);
        });
        txtGcodeGroup29.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(29, newValue);
        });
        txtGcodeGroup30.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setGcodeGroupValue(30, newValue);
        });

        // code change tab
        txtCodeChangeProgramCall.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeProgramCallValue(newValue);
        });
        txtCodeChangeG.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeGValue(newValue);
        });
        txtCodeChangeM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeMValue(newValue);
        });
        txtCodeChangeT.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeTValue(newValue);
        });
        txtCodeChangeD.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeDValue(newValue);
        });
        txtCodeChangeH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCodeChangeHValue(newValue);
        });

        // macro call tab
        txtMacroCallG.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setMacroCallGValue(newValue);
        });
        txtMacroCallM.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setMacroCallMValue(newValue);
        });

        // coordinate tab
        chkOriginMachineX.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(0, newValue);
        });
        txtOriginMachineX.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(0, newValue);
        });
        chkOriginMachineY.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(1, newValue);
        });
        txtOriginMachineY.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(1, newValue);
        });
        chkOriginMachineZ.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(2, newValue);
        });
        txtOriginMachineZ.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(2, newValue);
        });
        chkOriginMachineA.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(3, newValue);
        });
        txtOriginMachineA.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(3, newValue);
        });
        chkOriginMachineB.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(4, newValue);
        });
        txtOriginMachineB.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(4, newValue);
        });
        chkOriginMachineC.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(5, newValue);
        });
        txtOriginMachineC.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(5, newValue);
        });
        chkOriginMachineU.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(6, newValue);
        });
        txtOriginMachineU.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(6, newValue);
        });
        chkOriginMachineV.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(7, newValue);
        });
        txtOriginMachineV.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(7, newValue);
        });
        chkOriginMachineW.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOriginMachineCheck(8, newValue);
        });
        txtOriginMachineW.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOriginMachineValue(8, newValue);
        });

        chkCoordinateExtX.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(0, newValue);
        });
        txtCoordinateExtX.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(0, newValue);
        });
        chkCoordinateExtY.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(1, newValue);
        });
        txtCoordinateExtY.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(1, newValue);
        });
        chkCoordinateExtZ.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(2, newValue);
        });
        txtCoordinateExtZ.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(2, newValue);
        });
        chkCoordinateExtA.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(3, newValue);
        });
        txtCoordinateExtA.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(3, newValue);
        });
        chkCoordinateExtB.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(4, newValue);
        });
        txtCoordinateExtB.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(4, newValue);
        });
        chkCoordinateExtC.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(5, newValue);
        });
        txtCoordinateExtC.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(5, newValue);
        });
        chkCoordinateExtU.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(6, newValue);
        });
        txtCoordinateExtU.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(6, newValue);
        });
        chkCoordinateExtV.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(7, newValue);
        });
        txtCoordinateExtV.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(7, newValue);
        });
        chkCoordinateExtW.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateExtCheck(8, newValue);
        });
        txtCoordinateExtW.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateExtValue(8, newValue);
        });

        chkCoordinateG92X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(0, newValue);
        });
        txtCoordinateG92X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(0, newValue);
        });
        chkCoordinateG92Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(1, newValue);
        });
        txtCoordinateG92Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(1, newValue);
        });
        chkCoordinateG92Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(2, newValue);
        });
        txtCoordinateG92Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(2, newValue);
        });
        chkCoordinateG92A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(3, newValue);
        });
        txtCoordinateG92A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(3, newValue);
        });
        chkCoordinateG92B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(4, newValue);
        });
        txtCoordinateG92B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(4, newValue);
        });
        chkCoordinateG92C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(5, newValue);
        });
        txtCoordinateG92C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(5, newValue);
        });
        chkCoordinateG92U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(6, newValue);
        });
        txtCoordinateG92U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(6, newValue);
        });
        chkCoordinateG92V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(7, newValue);
        });
        txtCoordinateG92V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(7, newValue);
        });
        chkCoordinateG92W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG92Check(8, newValue);
        });
        txtCoordinateG92W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG92Value(8, newValue);
        });

        chkCoordinateTool.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateToolCheck(newValue);
        });

        chkCoordinateMirror.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateMirrorCheck(newValue);
        });

        chkCoordinateG54X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(0, newValue);
        });
        txtCoordinateG54X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(0, newValue);
        });
        chkCoordinateG54Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(1, newValue);
        });
        txtCoordinateG54Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(1, newValue);
        });
        chkCoordinateG54Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(2, newValue);
        });
        txtCoordinateG54Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(2, newValue);
        });
        chkCoordinateG54A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(3, newValue);
        });
        txtCoordinateG54A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(3, newValue);
        });
        chkCoordinateG54B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(4, newValue);
        });
        txtCoordinateG54B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(4, newValue);
        });
        chkCoordinateG54C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(5, newValue);
        });
        txtCoordinateG54C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(5, newValue);
        });
        chkCoordinateG54U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(6, newValue);
        });
        txtCoordinateG54U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(6, newValue);
        });
        chkCoordinateG54V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(7, newValue);
        });
        txtCoordinateG54V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(7, newValue);
        });
        chkCoordinateG54W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG54Check(8, newValue);
        });
        txtCoordinateG54W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG54Value(8, newValue);
        });

        chkCoordinateG55X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(0, newValue);
        });
        txtCoordinateG55X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(0, newValue);
        });
        chkCoordinateG55Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(1, newValue);
        });
        txtCoordinateG55Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(1, newValue);
        });
        chkCoordinateG55Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(2, newValue);
        });
        txtCoordinateG55Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(2, newValue);
        });
        chkCoordinateG55A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(3, newValue);
        });
        txtCoordinateG55A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(3, newValue);
        });
        chkCoordinateG55B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(4, newValue);
        });
        txtCoordinateG55B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(4, newValue);
        });
        chkCoordinateG55C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(5, newValue);
        });
        txtCoordinateG55C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(5, newValue);
        });
        chkCoordinateG55U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(6, newValue);
        });
        txtCoordinateG55U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(6, newValue);
        });
        chkCoordinateG55V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(7, newValue);
        });
        txtCoordinateG55V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(7, newValue);
        });
        chkCoordinateG55W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG55Check(8, newValue);
        });
        txtCoordinateG55W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG55Value(8, newValue);
        });

        chkCoordinateG56X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(0, newValue);
        });
        txtCoordinateG56X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(0, newValue);
        });
        chkCoordinateG56Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(1, newValue);
        });
        txtCoordinateG56Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(1, newValue);
        });
        chkCoordinateG56Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(2, newValue);
        });
        txtCoordinateG56Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(2, newValue);
        });
        chkCoordinateG56A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(3, newValue);
        });
        txtCoordinateG56A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(3, newValue);
        });
        chkCoordinateG56B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(4, newValue);
        });
        txtCoordinateG56B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(4, newValue);
        });
        chkCoordinateG56C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(5, newValue);
        });
        txtCoordinateG56C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(5, newValue);
        });
        chkCoordinateG56U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(6, newValue);
        });
        txtCoordinateG56U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(6, newValue);
        });
        chkCoordinateG56V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(7, newValue);
        });
        txtCoordinateG56V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(7, newValue);
        });
        chkCoordinateG56W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG56Check(8, newValue);
        });
        txtCoordinateG56W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG56Value(8, newValue);
        });

        chkCoordinateG57X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(0, newValue);
        });
        txtCoordinateG57X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(0, newValue);
        });
        chkCoordinateG57Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(1, newValue);
        });
        txtCoordinateG57Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(1, newValue);
        });
        chkCoordinateG57Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(2, newValue);
        });
        txtCoordinateG57Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(2, newValue);
        });
        chkCoordinateG57A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(3, newValue);
        });
        txtCoordinateG57A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(3, newValue);
        });
        chkCoordinateG57B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(4, newValue);
        });
        txtCoordinateG57B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(4, newValue);
        });
        chkCoordinateG57C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(5, newValue);
        });
        txtCoordinateG57C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(5, newValue);
        });
        chkCoordinateG57U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(6, newValue);
        });
        txtCoordinateG57U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(6, newValue);
        });
        chkCoordinateG57V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(7, newValue);
        });
        txtCoordinateG57V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(7, newValue);
        });
        chkCoordinateG57W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG57Check(8, newValue);
        });
        txtCoordinateG57W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG57Value(8, newValue);
        });

        chkCoordinateG58X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(0, newValue);
        });
        txtCoordinateG58X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(0, newValue);
        });
        chkCoordinateG58Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(1, newValue);
        });
        txtCoordinateG58Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(1, newValue);
        });
        chkCoordinateG58Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(2, newValue);
        });
        txtCoordinateG58Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(2, newValue);
        });
        chkCoordinateG58A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(3, newValue);
        });
        txtCoordinateG58A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(3, newValue);
        });
        chkCoordinateG58B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(4, newValue);
        });
        txtCoordinateG58B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(4, newValue);
        });
        chkCoordinateG58C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(5, newValue);
        });
        txtCoordinateG58C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(5, newValue);
        });
        chkCoordinateG58U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(6, newValue);
        });
        txtCoordinateG58U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(6, newValue);
        });
        chkCoordinateG58V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(7, newValue);
        });
        txtCoordinateG58V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(7, newValue);
        });
        chkCoordinateG58W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG58Check(8, newValue);
        });
        txtCoordinateG58W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG58Value(8, newValue);
        });

        chkCoordinateG59X.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(0, newValue);
        });
        txtCoordinateG59X.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(0, newValue);
        });
        chkCoordinateG59Y.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(1, newValue);
        });
        txtCoordinateG59Y.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(1, newValue);
        });
        chkCoordinateG59Z.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(2, newValue);
        });
        txtCoordinateG59Z.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(2, newValue);
        });
        chkCoordinateG59A.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(3, newValue);
        });
        txtCoordinateG59A.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(3, newValue);
        });
        chkCoordinateG59B.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(4, newValue);
        });
        txtCoordinateG59B.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(4, newValue);
        });
        chkCoordinateG59C.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(5, newValue);
        });
        txtCoordinateG59C.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(5, newValue);
        });
        chkCoordinateG59U.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(6, newValue);
        });
        txtCoordinateG59U.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(6, newValue);
        });
        chkCoordinateG59V.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(7, newValue);
        });
        txtCoordinateG59V.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(7, newValue);
        });
        chkCoordinateG59W.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setCoordinateG59Check(8, newValue);
        });
        txtCoordinateG59W.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setCoordinateG59Value(8, newValue);
        });

        // tool offset tab
        txtToolOffset.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setToolOffsetValue(newValue);
        });

        // variables tab
        txtVariables.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setVariablesValue(newValue);
        });

        // tool change tab
        chkToolChangeProgram.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            runViewToolChange();
            virtualMachineSettings_.setToolChangeProgramCheck(newValue);
        });
        chkToolChangeXY.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setToolChangeXYCheck(newValue);
        });
        txtToolChangeProgram.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setToolChangeProgramValue(newValue);
        });
        chkToolChangeMCode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            runViewToolChange();
            virtualMachineSettings_.setToolChangeMCodeCheck(newValue);
        });
        txtToolChangeMCode.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setToolChangeMCodeValue(newValue);
        });

        // skip function tab
        chkSkipFunctionProgram.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            runViewSkipFunction();
            virtualMachineSettings_.setSkipFunctionProgramCheck(newValue);
        });
        txtSkipFunctionProgram.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setSkipFunctionProgramValue(newValue);
        });

        // ladder tab
        txtLadderN1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(0, newValue);
        });
        txtLadderN2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(1, newValue);
        });
        txtLadderG1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(2, newValue);
        });
        txtLadderG2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(3, newValue);
        });
        txtLadderX1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(4, newValue);
        });
        txtLadderX2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(5, newValue);
        });
        txtLadderY1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(6, newValue);
        });
        txtLadderY2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(7, newValue);
        });
        txtLadderZ1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(8, newValue);
        });
        txtLadderZ2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(9, newValue);
        });
        txtLadderA1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(10, newValue);
        });
        txtLadderA2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(11, newValue);
        });
        txtLadderB1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(12, newValue);
        });
        txtLadderB2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(13, newValue);
        });
        txtLadderC1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(14, newValue);
        });
        txtLadderC2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(15, newValue);
        });
        txtLadderU1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(16, newValue);
        });
        txtLadderU2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(17, newValue);
        });
        txtLadderV1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(18, newValue);
        });
        txtLadderV2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(19, newValue);
        });
        txtLadderW1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(20, newValue);
        });
        txtLadderW2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(21, newValue);
        });
        txtLadderM1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(22, newValue);
        });
        txtLadderM2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(23, newValue);
        });
        txtLadderR1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(24, newValue);
        });
        txtLadderR2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(25, newValue);
        });
        txtLadderI1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(26, newValue);
        });
        txtLadderI2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(27, newValue);
        });
        txtLadderJ1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(28, newValue);
        });
        txtLadderJ2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(29, newValue);
        });
        txtLadderK1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(30, newValue);
        });
        txtLadderK2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(31, newValue);
        });
        txtLadderF1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(32, newValue);
        });
        txtLadderF2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(33, newValue);
        });
        txtLadderS1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(34, newValue);
        });
        txtLadderS2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(35, newValue);
        });
        txtLadderT1.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(36, newValue);
        });
        txtLadderT2.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setLadderValue(37, newValue);
        });

        // opton tab
        chkOptionOptimization.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionOptimization(newValue);
        });
        chkOptionExComment.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionExComment(newValue);
        });
        chkOptionDisable30033004.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionDisable30033004(newValue);
        });
        chkOptionReplace3006M0.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionReplace3006M0(newValue);
        });
        chkOptionOnlyS.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionOnlyS(newValue);
        });
        chkOptionRS274NGC.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionRS274NGC(newValue);
        });
        chkOptionDebugJson.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            virtualMachineSettings_.setOptionDebugJson(newValue);
        });

        txtOptionMaxFeedRate.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOptionMaxFeedRate(newValue);
        });
        txtOptionMaxRevolution.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOptionMaxRevolution(newValue);
        });
        txtOptionStartProgram.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOptionStartProgram(newValue);
        });
        txtOptionBlockProgram.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOptionBlockProgram(newValue);
        });
        txtOptionEndProgram.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            virtualMachineSettings_.setOptionEndProgram(newValue);
        });
    }

    public void runInitDesign() {
        if (Platform.isFxApplicationThread()) {
            initDesign();
        } else {
            Platform.runLater(() -> {
                initDesign();
            });
        }
    }

    private void initDesign() {
        // base tab
        tabBase.setText(DesignEnums.BASE.toString());
        lblVirtualMachine.setText(DesignEnums.VIRTUAL_MACHINE.toString());
        txtVirtualMachineName.setTooltip(new Tooltip(DesignEnums.VIRTUAL_MACHINE_NAME_TIP.toString()));
        txtVirtualMachineName.setEditable(false);
        btnVirtualMachineLoad.setTooltip(new Tooltip(DesignEnums.VIRTUAL_MACHINE_LOAD_TIP.toString()));
        btnVirtualMachineLoad.setText(DesignEnums.VIRTUAL_MACHINE_LOAD.toString());
        btnVirtualMachineSave.setTooltip(new Tooltip(DesignEnums.VIRTUAL_MACHINE_SAVE_TIP.toString()));
        btnVirtualMachineSave.setText(DesignEnums.VIRTUAL_MACHINE_SAVE.toString());
        chkLog.setTooltip(new Tooltip(DesignEnums.LOG_TIP.toString()));
        chkLog.setText(DesignEnums.LOG.toString());
        chkDebug.setTooltip(new Tooltip(DesignEnums.DEBUG_TIP.toString()));
        chkDebug.setText(DesignEnums.DEBUG.toString());
        lblSerialPort.setText(DesignEnums.SERIAL_PORT.toString());
        btnSerialPortList.setTooltip(new Tooltip(DesignEnums.SERIAL_PORT_LIST_TIP.toString()));
        btnSerialPortList.setText(DesignEnums.SERIAL_PORT_LIST.toString());
        txtSerialPort.setTooltip(new Tooltip(DesignEnums.SERIAL_PORT_TIP.toString()));
        btnSerialOpen.setTooltip(new Tooltip(DesignEnums.SERIAL_CONNECT_TIP.toString()));
        btnSerialOpen.setText(DesignEnums.SERIAL_CONNECT.toString());
        btnSerialClose.setTooltip(new Tooltip(DesignEnums.SERIAL_DISCONNECT_TIP.toString()));
        btnSerialClose.setText(DesignEnums.SERIAL_DISCONNECT.toString());
        lblSerialBaudrate.setText(DesignEnums.SERIAL_BAUDRATE.toString());
        txtSerialBaudrate.setTooltip(new Tooltip(DesignEnums.SERIAL_BAUDRATE_TIP.toString()));
        lblSerialDataBits.setText(DesignEnums.SERIAL_DATA_BITS.toString());
        cmbSerialDataBits.getItems().addAll(
                "5", "6", "7", "8"
        );
        cmbSerialDataBits.setTooltip(new Tooltip(DesignEnums.SERIAL_DATA_BITS_TIP.toString()));
        lblSerialStopBits.setText(DesignEnums.SERIAL_STOP_BITS.toString());
        cmbSerialStopBits.getItems().addAll(
                "1", "2", "1.5"
        );
        cmbSerialStopBits.setTooltip(new Tooltip(DesignEnums.SERIAL_STOP_BITS_TIP.toString()));
        lblSerialParity.setText(DesignEnums.SERIAL_PARITY.toString());
        cmbSerialParity.getItems().addAll(
                "NONE", "ODD", "EVEN", "MARK", "SPACE"
        );
        cmbSerialParity.setTooltip(new Tooltip(DesignEnums.SERIAL_PARITY_TIP.toString()));
        lblSerialEOB.setText(DesignEnums.SERIAL_EOB.toString());
        cmbSerialEOB.getItems().addAll(
                "LF", "LF CR CR", "CR LF"
        );
        cmbSerialEOB.setTooltip(new Tooltip(DesignEnums.SERIAL_EOB_TIP.toString()));
        lblSerialBufferLimit.setText(DesignEnums.SERIAL_BUFFER_LIMIT.toString());
        txtSerialBufferLimit.setTooltip(new Tooltip(DesignEnums.SERIAL_BUFFER_LIMIT_TIP.toString()));
        lblSerialDelay.setText(DesignEnums.SERIAL_DELAY.toString());
        txtSerialDelay.setTooltip(new Tooltip(DesignEnums.SERIAL_DELAY_TIP.toString()));
        lblSerialStartCode.setText(DesignEnums.SERIAL_START_CODE.toString());
        cmbSerialStartCode.getItems().addAll(
                "none", "EOB", "%", "DC2", "% | none", "% | EOB"
        );
        cmbSerialStartCode.setTooltip(new Tooltip(DesignEnums.SERIAL_START_CODE_TIP.toString()));
        lblSerialEndCode.setText(DesignEnums.SERIAL_END_CODE.toString());
        cmbSerialEndCode.getItems().addAll(
                "none", "EOB", "%", "DC4", "% | none", "% | EOB"
        );
        cmbSerialEndCode.setTooltip(new Tooltip(DesignEnums.SERIAL_END_CODE_TIP.toString()));
        chkSerialCharacter.setTooltip(new Tooltip(DesignEnums.SERIAL_CHARACTER_TIP.toString()));
        chkSerialCharacter.setText(DesignEnums.SERIAL_CHARACTER.toString());
        chkSerialObserveCTS.setTooltip(new Tooltip(DesignEnums.SERIAL_OBSERVE_CTS_TIP.toString()));
        chkSerialObserveCTS.setText(DesignEnums.SERIAL_OBSERVE_CTS.toString());
        chkSerialObserveDSR.setTooltip(new Tooltip(DesignEnums.SERIAL_OBSERVE_DSR_TIP.toString()));
        chkSerialObserveDSR.setText(DesignEnums.SERIAL_OBSERVE_DSR.toString());
        chkSerialObserveDC2DC4.setTooltip(new Tooltip(DesignEnums.SERIAL_OBSERVE_DC2DC4_TIP.toString()));
        chkSerialObserveDC2DC4.setText(DesignEnums.SERIAL_OBSERVE_DC2DC4.toString());
        lblBackGroundFile.setText(DesignEnums.BACK_GROUND_FILE.toString());
        txtBackGroundFile1.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_TIP.toString()));
        btnBackGroundFile1.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_BROWSE_TIP.toString()));
        btnBackGroundFile1.setText(DesignEnums.BACK_GROUND_FILE_BROWSE.toString());
        txtBackGroundFile2.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_TIP.toString()));
        btnBackGroundFile2.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_BROWSE_TIP.toString()));
        btnBackGroundFile2.setText(DesignEnums.BACK_GROUND_FILE_BROWSE.toString());
        txtBackGroundFile3.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_TIP.toString()));
        btnBackGroundFile3.setTooltip(new Tooltip(DesignEnums.BACK_GROUND_FILE_BROWSE_TIP.toString()));
        btnBackGroundFile3.setText(DesignEnums.BACK_GROUND_FILE_BROWSE.toString());
        lblExternalSubProgramDirectory.setText(DesignEnums.EXTERNAL_SUBPROGRAM_DIRECTORY.toString());
        txtExternalSubProgramDirectory.setTooltip(new Tooltip(DesignEnums.EXTERNAL_SUBPROGRAM_DIRECTORY_TIP.toString()));
        btnExternalSubProgramDirectory.setTooltip(new Tooltip(DesignEnums.EXTERNAL_SUBPROGRAM_DIRECTORY_BROWSE_TIP.toString()));
        btnExternalSubProgramDirectory.setText(DesignEnums.EXTERNAL_SUBPROGRAM_DIRECTORY_BROWSE.toString());

        // g code group tab
        tabGcodeGroup.setText(DesignEnums.GCODE_GROUP.toString());
        lblGcodeGroup0.setText(DesignEnums.GCODE_GROUP0.toString());
        txtGcodeGroup0.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup1.setText(DesignEnums.GCODE_GROUP1.toString());
        txtGcodeGroup1.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup2.setText(DesignEnums.GCODE_GROUP2.toString());
        txtGcodeGroup2.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup3.setText(DesignEnums.GCODE_GROUP3.toString());
        txtGcodeGroup3.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup4.setText(DesignEnums.GCODE_GROUP4.toString());
        txtGcodeGroup4.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup5.setText(DesignEnums.GCODE_GROUP5.toString());
        txtGcodeGroup5.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup6.setText(DesignEnums.GCODE_GROUP6.toString());
        txtGcodeGroup6.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup7.setText(DesignEnums.GCODE_GROUP7.toString());
        txtGcodeGroup7.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup8.setText(DesignEnums.GCODE_GROUP8.toString());
        txtGcodeGroup8.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup9.setText(DesignEnums.GCODE_GROUP9.toString());
        txtGcodeGroup9.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup10.setText(DesignEnums.GCODE_GROUP10.toString());
        txtGcodeGroup10.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup11.setText(DesignEnums.GCODE_GROUP11.toString());
        txtGcodeGroup11.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup12.setText(DesignEnums.GCODE_GROUP12.toString());
        txtGcodeGroup12.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup13.setText(DesignEnums.GCODE_GROUP13.toString());
        txtGcodeGroup13.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup14.setText(DesignEnums.GCODE_GROUP14.toString());
        txtGcodeGroup14.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup15.setText(DesignEnums.GCODE_GROUP15.toString());
        txtGcodeGroup15.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup16.setText(DesignEnums.GCODE_GROUP16.toString());
        txtGcodeGroup16.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup17.setText(DesignEnums.GCODE_GROUP17.toString());
        txtGcodeGroup17.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup18.setText(DesignEnums.GCODE_GROUP18.toString());
        txtGcodeGroup18.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup19.setText(DesignEnums.GCODE_GROUP19.toString());
        txtGcodeGroup19.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup20.setText(DesignEnums.GCODE_GROUP20.toString());
        txtGcodeGroup20.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup21.setText(DesignEnums.GCODE_GROUP21.toString());
        txtGcodeGroup21.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup22.setText(DesignEnums.GCODE_GROUP22.toString());
        txtGcodeGroup22.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup23.setText(DesignEnums.GCODE_GROUP23.toString());
        txtGcodeGroup23.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup24.setText(DesignEnums.GCODE_GROUP24.toString());
        txtGcodeGroup24.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup25.setText(DesignEnums.GCODE_GROUP25.toString());
        txtGcodeGroup25.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup26.setText(DesignEnums.GCODE_GROUP26.toString());
        txtGcodeGroup26.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup27.setText(DesignEnums.GCODE_GROUP27.toString());
        txtGcodeGroup27.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup28.setText(DesignEnums.GCODE_GROUP28.toString());
        txtGcodeGroup28.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup29.setText(DesignEnums.GCODE_GROUP29.toString());
        txtGcodeGroup29.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));
        lblGcodeGroup30.setText(DesignEnums.GCODE_GROUP30.toString());
        txtGcodeGroup30.setTooltip(new Tooltip(DesignEnums.GCODE_GROUP_TIP.toString()));

        // code change tab
        tabCodeChange.setText(DesignEnums.CODE_CHANGE.toString());
        lblCodeChangeProgramCall.setText(DesignEnums.CODE_CHANGE_PROGRAM_CALL.toString());
        txtCodeChangeProgramCall.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_PROGRAM_CALL_TIP.toString()));
        lblCodeChangeG.setText(DesignEnums.CODE_CHANGE_G.toString());
        txtCodeChangeG.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_TIP.toString()));
        lblCodeChangeM.setText(DesignEnums.CODE_CHANGE_M.toString());
        txtCodeChangeM.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_TIP.toString()));
        lblCodeChangeT.setText(DesignEnums.CODE_CHANGE_T.toString());
        txtCodeChangeT.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_TIP.toString()));
        lblCodeChangeD.setText(DesignEnums.CODE_CHANGE_D.toString());
        txtCodeChangeD.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_TIP.toString()));
        lblCodeChangeH.setText(DesignEnums.CODE_CHANGE_H.toString());
        txtCodeChangeH.setTooltip(new Tooltip(DesignEnums.CODE_CHANGE_TIP.toString()));

        // macro call tab
        tabMacroCall.setText(DesignEnums.MACRO_CALL.toString());
        lblMacroCallG.setText(DesignEnums.MACRO_CALL_G.toString());
        txtMacroCallG.setTooltip(new Tooltip(DesignEnums.MACRO_CALL_TIP.toString()));
        lblMacroCallM.setText(DesignEnums.MACRO_CALL_M.toString());
        txtMacroCallM.setTooltip(new Tooltip(DesignEnums.MACRO_CALL_TIP.toString()));

        // coordinate tab
        tabCoordinate.setText(DesignEnums.COORDINATE.toString());
        lblOriginMachine.setText(DesignEnums.ORIGIN.toString());
        chkOriginMachineX.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineX.setText(DesignEnums.COORDINATE_X.toString());
        txtOriginMachineX.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkOriginMachineY.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineY.setText(DesignEnums.COORDINATE_Y.toString());
        txtOriginMachineY.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkOriginMachineZ.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineZ.setText(DesignEnums.COORDINATE_Z.toString());
        txtOriginMachineZ.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkOriginMachineA.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineA.setText(DesignEnums.COORDINATE_A.toString());
        txtOriginMachineA.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkOriginMachineB.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineB.setText(DesignEnums.COORDINATE_B.toString());
        txtOriginMachineB.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkOriginMachineC.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineC.setText(DesignEnums.COORDINATE_C.toString());
        txtOriginMachineC.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkOriginMachineU.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineU.setText(DesignEnums.COORDINATE_U.toString());
        txtOriginMachineU.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkOriginMachineV.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineV.setText(DesignEnums.COORDINATE_V.toString());
        txtOriginMachineV.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkOriginMachineW.setTooltip(new Tooltip(DesignEnums.ORIGIN_TIP.toString()));
        chkOriginMachineW.setText(DesignEnums.COORDINATE_W.toString());
        txtOriginMachineW.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateExt.setText(DesignEnums.EXTERNAL.toString());
        chkCoordinateExtX.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtX.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateExtX.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateExtY.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtY.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateExtY.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateExtZ.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtZ.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateExtZ.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateExtA.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtA.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateExtA.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateExtB.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtB.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateExtB.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateExtC.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtC.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateExtC.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateExtU.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtU.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateExtU.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateExtV.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtV.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateExtV.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateExtW.setTooltip(new Tooltip(DesignEnums.EXTERNAL_TIP.toString()));
        chkCoordinateExtW.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateExtW.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG92.setText(DesignEnums.G92.toString());
        chkCoordinateG92X.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG92X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG92Y.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG92Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG92Z.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG92Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG92A.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG92A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG92B.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG92B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG92C.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG92C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG92U.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG92U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG92V.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG92V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG92W.setTooltip(new Tooltip(DesignEnums.G92_TIP.toString()));
        chkCoordinateG92W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG92W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateExtension.setText(DesignEnums.EXTENSION.toString());
        chkCoordinateTool.setTooltip(new Tooltip(DesignEnums.TOOL_TIP.toString()));
        chkCoordinateTool.setText(DesignEnums.TOOL.toString());
        chkCoordinateMirror.setTooltip(new Tooltip(DesignEnums.MIRROR_TIP.toString()));
        chkCoordinateMirror.setText(DesignEnums.MIRROR.toString());
        lblCoordinateG54.setText(DesignEnums.G54.toString());
        chkCoordinateG54X.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG54X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG54Y.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG54Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG54Z.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG54Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG54A.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG54A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG54B.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG54B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG54C.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG54C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG54U.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG54U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG54V.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG54V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG54W.setTooltip(new Tooltip(DesignEnums.G54_TIP.toString()));
        chkCoordinateG54W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG54W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG55.setText(DesignEnums.G55.toString());
        chkCoordinateG55X.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG55X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG55Y.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG55Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG55Z.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG55Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG55A.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG55A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG55B.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG55B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG55C.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG55C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG55U.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG55U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG55V.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG55V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG55W.setTooltip(new Tooltip(DesignEnums.G55_TIP.toString()));
        chkCoordinateG55W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG55W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG56.setText(DesignEnums.G56.toString());
        chkCoordinateG56X.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG56X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG56Y.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG56Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG56Z.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG56Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG56A.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG56A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG56B.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG56B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG56C.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG56C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG56U.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG56U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG56V.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG56V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG56W.setTooltip(new Tooltip(DesignEnums.G56_TIP.toString()));
        chkCoordinateG56W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG56W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG57.setText(DesignEnums.G57.toString());
        chkCoordinateG57X.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG57X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG57Y.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG57Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG57Z.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG57Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG57A.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG57A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG57B.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG57B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG57C.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG57C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG57U.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG57U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG57V.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG57V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG57W.setTooltip(new Tooltip(DesignEnums.G57_TIP.toString()));
        chkCoordinateG57W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG57W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG58.setText(DesignEnums.G58.toString());
        chkCoordinateG58X.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG58X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG58Y.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG58Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG58Z.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG58Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG58A.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG58A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG58B.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG58B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG58C.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG58C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG58U.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG58U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG58V.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG58V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG58W.setTooltip(new Tooltip(DesignEnums.G58_TIP.toString()));
        chkCoordinateG58W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG58W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));
        lblCoordinateG59.setText(DesignEnums.G59.toString());
        chkCoordinateG59X.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59X.setText(DesignEnums.COORDINATE_X.toString());
        txtCoordinateG59X.setTooltip(new Tooltip(DesignEnums.COORDINATE_X_TIP.toString()));
        chkCoordinateG59Y.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59Y.setText(DesignEnums.COORDINATE_Y.toString());
        txtCoordinateG59Y.setTooltip(new Tooltip(DesignEnums.COORDINATE_Y_TIP.toString()));
        chkCoordinateG59Z.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59Z.setText(DesignEnums.COORDINATE_Z.toString());
        txtCoordinateG59Z.setTooltip(new Tooltip(DesignEnums.COORDINATE_Z_TIP.toString()));
        chkCoordinateG59A.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59A.setText(DesignEnums.COORDINATE_A.toString());
        txtCoordinateG59A.setTooltip(new Tooltip(DesignEnums.COORDINATE_A_TIP.toString()));
        chkCoordinateG59B.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59B.setText(DesignEnums.COORDINATE_B.toString());
        txtCoordinateG59B.setTooltip(new Tooltip(DesignEnums.COORDINATE_B_TIP.toString()));
        chkCoordinateG59C.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59C.setText(DesignEnums.COORDINATE_C.toString());
        txtCoordinateG59C.setTooltip(new Tooltip(DesignEnums.COORDINATE_C_TIP.toString()));
        chkCoordinateG59U.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59U.setText(DesignEnums.COORDINATE_U.toString());
        txtCoordinateG59U.setTooltip(new Tooltip(DesignEnums.COORDINATE_U_TIP.toString()));
        chkCoordinateG59V.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59V.setText(DesignEnums.COORDINATE_V.toString());
        txtCoordinateG59V.setTooltip(new Tooltip(DesignEnums.COORDINATE_V_TIP.toString()));
        chkCoordinateG59W.setTooltip(new Tooltip(DesignEnums.G59_TIP.toString()));
        chkCoordinateG59W.setText(DesignEnums.COORDINATE_W.toString());
        txtCoordinateG59W.setTooltip(new Tooltip(DesignEnums.COORDINATE_W_TIP.toString()));

        // tool offset tab
        tabToolOffset.setText(DesignEnums.TOOL_OFFSET.toString());
        lblToolOffset.setText(DesignEnums.TOOL_OFFSET.toString());
        txtToolOffset.setTooltip(new Tooltip(DesignEnums.TOOL_OFFSET_TIP.toString()));

        // variables tab
        tabVariables.setText(DesignEnums.VARIABLES.toString());
        lblVariables.setText(DesignEnums.VARIABLES.toString());
        txtVariables.setTooltip(new Tooltip(DesignEnums.VARIABLES_TIP.toString()));

        // tool change tab
        tabToolChange.setText(DesignEnums.TOOL_CHANGE.toString());
        chkToolChangeProgram.setTooltip(new Tooltip(DesignEnums.TOOL_CHANGE_PROGRAM_CHECK_TIP.toString()));
        chkToolChangeProgram.setText(DesignEnums.TOOL_CHANGE_PROGRAM_CHECK.toString());
        chkToolChangeXY.setTooltip(new Tooltip(DesignEnums.TOOL_CHANGE_XY_CHECK_TIP.toString()));
        chkToolChangeXY.setText(DesignEnums.TOOL_CHANGE_XY_CHECK.toString());
        lblToolChangeProgram.setText(DesignEnums.TOOL_CHANGE_PROGRAM.toString());
        txtToolChangeProgram.setTooltip(new Tooltip(DesignEnums.TOOL_CHANGE_PROGRAM_TIP.toString()));
        chkToolChangeMCode.setTooltip(new Tooltip(DesignEnums.TOOL_CHANGE_MCODE_CHECK_TIP.toString()));
        chkToolChangeMCode.setText(DesignEnums.TOOL_CHANGE_MCODE_CHECK.toString());
        lblToolChangeMCode.setText(DesignEnums.TOOL_CHANGE_MCODE.toString());
        txtToolChangeMCode.setTooltip(new Tooltip(DesignEnums.TOOL_CHANGE_MCODE_TIP.toString()));

        // skip function tab
        tabSkipFunction.setText(DesignEnums.SKIP_FUNCTION.toString());
        chkSkipFunctionProgram.setTooltip(new Tooltip(DesignEnums.SKIP_FUNCTION_PROGRAM_CHECK_TIP.toString()));
        chkSkipFunctionProgram.setText(DesignEnums.SKIP_FUNCTION_PROGRAM_CHECK.toString());
        lblSkipFunctionProgram.setText(DesignEnums.SKIP_FUNCTION_PROGRAM.toString());
        txtSkipFunctionProgram.setTooltip(new Tooltip(DesignEnums.SKIP_FUNCTION_PROGRAM_TIP.toString()));

        // ladder tab
        tabLadder.setText(DesignEnums.LADDER.toString());
        lblLadderSequenceCode.setText(DesignEnums.LADDER_SEQUENCE_CODE.toString());
        lblLadderN.setText(DesignEnums.LADDER_N.toString());
        txtLadderN1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderN2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderPreparatoryCode.setText(DesignEnums.LADDER_PREPARATORY_CODE.toString());
        lblLadderG.setText(DesignEnums.LADDER_G.toString());
        txtLadderG1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderG2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderDimensionCode.setText(DesignEnums.LADDER_DIMENSION_CODE.toString());
        lblLadderX.setText(DesignEnums.LADDER_X.toString());
        txtLadderX1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderX2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderY.setText(DesignEnums.LADDER_Y.toString());
        txtLadderY1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderY2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderZ.setText(DesignEnums.LADDER_Z.toString());
        txtLadderZ1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderZ2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderA.setText(DesignEnums.LADDER_A.toString());
        txtLadderA1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderA2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderB.setText(DesignEnums.LADDER_B.toString());
        txtLadderB1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderB2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderC.setText(DesignEnums.LADDER_C.toString());
        txtLadderC1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderC2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderU.setText(DesignEnums.LADDER_U.toString());
        txtLadderU1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderU2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderV.setText(DesignEnums.LADDER_V.toString());
        txtLadderV1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderV2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderW.setText(DesignEnums.LADDER_W.toString());
        txtLadderW1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderW2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderAuxiliaryCode.setText(DesignEnums.LADDER_AUXILIARY_CODE.toString());
        lblLadderM.setText(DesignEnums.LADDER_M.toString());
        txtLadderM1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderM2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderR.setText(DesignEnums.LADDER_R.toString());
        txtLadderR1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderR2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderI.setText(DesignEnums.LADDER_I.toString());
        txtLadderI1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderI2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderJ.setText(DesignEnums.LADDER_J.toString());
        txtLadderJ1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderJ2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderK.setText(DesignEnums.LADDER_K.toString());
        txtLadderK1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderK2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderFeedrateCode.setText(DesignEnums.LADDER_FEEDRATE_CODE.toString());
        lblLadderF.setText(DesignEnums.LADDER_F.toString());
        txtLadderF1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderF2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderSpindleCode.setText(DesignEnums.LADDER_SPINDLE_CODE.toString());
        lblLadderS.setText(DesignEnums.LADDER_S.toString());
        txtLadderS1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderS2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));
        lblLadderToolCode.setText(DesignEnums.LADDER_TOOL_CODE.toString());
        lblLadderT.setText(DesignEnums.LADDER_T.toString());
        txtLadderT1.setTooltip(new Tooltip(DesignEnums.LADDER1_TIP.toString()));
        txtLadderT2.setTooltip(new Tooltip(DesignEnums.LADDER2_TIP.toString()));

        // option tab
        tabOption.setText(DesignEnums.OPTION.toString());
        chkOptionOptimization.setTooltip(new Tooltip(DesignEnums.OPTION_OPTIMIZATION_TIP.toString()));
        chkOptionOptimization.setText(DesignEnums.OPTION_OPTIMIZATION.toString());
        chkOptionExComment.setTooltip(new Tooltip(DesignEnums.OPTION_EXCOMMENT_TIP.toString()));
        chkOptionExComment.setText(DesignEnums.OPTION_EXCOMMENT.toString());
        chkOptionDisable30033004.setTooltip(new Tooltip(DesignEnums.OPTION_DISABLE_30033004_TIP.toString()));
        chkOptionDisable30033004.setText(DesignEnums.OPTION_DISABLE_30033004.toString());
        chkOptionReplace3006M0.setTooltip(new Tooltip(DesignEnums.OPTION_REPLACE_3006M0_TIP.toString()));
        chkOptionReplace3006M0.setText(DesignEnums.OPTION_REPLACE_3006M0.toString());
        chkOptionOnlyS.setTooltip(new Tooltip(DesignEnums.OPTION_ONLY_S_TIP.toString()));
        chkOptionOnlyS.setText(DesignEnums.OPTION_ONLY_S.toString());
        chkOptionRS274NGC.setTooltip(new Tooltip(DesignEnums.OPTION_RS274NGC_TIP.toString()));
        chkOptionRS274NGC.setText(DesignEnums.OPTION_RS274NGC.toString());
        chkOptionDebugJson.setTooltip(new Tooltip(DesignEnums.OPTION_DEBUG_JSON_TIP.toString()));
        chkOptionDebugJson.setText(DesignEnums.OPTION_DEBUG_JSON.toString());
        lblOptionMaxFeedRate.setText(DesignEnums.OPTION_MAX_FEED_RATE.toString());
        txtOptionMaxFeedRate.setTooltip(new Tooltip(DesignEnums.OPTION_MAX_FEED_RATE_TIP.toString()));
        lblOptionMaxRevolution.setText(DesignEnums.OPTION_MAX_REVOLUTION.toString());
        txtOptionMaxRevolution.setTooltip(new Tooltip(DesignEnums.OPTION_MAX_REVOLUTION_TIP.toString()));
        lblOptionStartProgram.setText(DesignEnums.OPTION_START_PROGRAM.toString());
        txtOptionStartProgram.setTooltip(new Tooltip(DesignEnums.OPTION_START_PROGRAM_TIP.toString()));
        lblOptionBlockProgram.setText(DesignEnums.OPTION_BLOCK_PROGRAM.toString());
        txtOptionBlockProgram.setTooltip(new Tooltip(DesignEnums.OPTION_BLOCK_PROGRAM_TIP.toString()));
        lblOptionEndProgram.setText(DesignEnums.OPTION_END_PROGRAM.toString());
        txtOptionEndProgram.setTooltip(new Tooltip(DesignEnums.OPTION_END_PROGRAM_TIP.toString()));
    }

    public void runValueUpdate() {
        if (Platform.isFxApplicationThread()) {
            valueUpdate();
        } else {
            Platform.runLater(() -> {
                valueUpdate();
            });
        }
    }

    private void valueUpdate() {
        // base tab
        txtVirtualMachineName.setText(virtualMachineSettings_.getVirtualMachineNameValue());
        chkLog.setSelected(virtualMachineSettings_.isLogCheck());
        chkDebug.setSelected(virtualMachineSettings_.isDebugCheck());
        txtSerialPort.setText(baseSettings_.getSerialPortValue());
        txtSerialBaudrate.setText(baseSettings_.getSerialBaudrateValue());
        cmbSerialDataBits.setValue(baseSettings_.getSerialDataBitsValue());
        cmbSerialStopBits.setValue(baseSettings_.getSerialStopBitsValue());
        cmbSerialParity.setValue(baseSettings_.getSerialParityValue());
        cmbSerialEOB.setValue(baseSettings_.getSerialEOBValue());
        txtSerialBufferLimit.setText(baseSettings_.getSerialBufferLimitValue());
        txtSerialDelay.setText(baseSettings_.getSerialDelayValue());
        cmbSerialStartCode.setValue(baseSettings_.getSerialStartCodeValue());
        cmbSerialEndCode.setValue(baseSettings_.getSerialEndCodeValue());
        chkSerialCharacter.setSelected(baseSettings_.isSerialCharacterCheck());
        chkSerialObserveCTS.setSelected(baseSettings_.isSerialObserveCTSCheck());
        chkSerialObserveDSR.setSelected(baseSettings_.isSerialObserveDSRCheck());
        chkSerialObserveDC2DC4.setSelected(baseSettings_.isSerialObserveDC2DC4Check());
        txtBackGroundFile1.setText(virtualMachineSettings_.getBackGroundFileValue(0));
        txtBackGroundFile2.setText(virtualMachineSettings_.getBackGroundFileValue(1));
        txtBackGroundFile3.setText(virtualMachineSettings_.getBackGroundFileValue(2));
        txtExternalSubProgramDirectory.setText(virtualMachineSettings_.getExternalSubProgramDirectoryValue());

        // g code group tab
        txtGcodeGroup0.setText(virtualMachineSettings_.getGcodeGroupValue(0));
        txtGcodeGroup1.setText(virtualMachineSettings_.getGcodeGroupValue(1));
        txtGcodeGroup2.setText(virtualMachineSettings_.getGcodeGroupValue(2));
        txtGcodeGroup3.setText(virtualMachineSettings_.getGcodeGroupValue(3));
        txtGcodeGroup4.setText(virtualMachineSettings_.getGcodeGroupValue(4));
        txtGcodeGroup5.setText(virtualMachineSettings_.getGcodeGroupValue(5));
        txtGcodeGroup6.setText(virtualMachineSettings_.getGcodeGroupValue(6));
        txtGcodeGroup7.setText(virtualMachineSettings_.getGcodeGroupValue(7));
        txtGcodeGroup8.setText(virtualMachineSettings_.getGcodeGroupValue(8));
        txtGcodeGroup9.setText(virtualMachineSettings_.getGcodeGroupValue(9));
        txtGcodeGroup10.setText(virtualMachineSettings_.getGcodeGroupValue(10));
        txtGcodeGroup11.setText(virtualMachineSettings_.getGcodeGroupValue(11));
        txtGcodeGroup12.setText(virtualMachineSettings_.getGcodeGroupValue(12));
        txtGcodeGroup13.setText(virtualMachineSettings_.getGcodeGroupValue(13));
        txtGcodeGroup14.setText(virtualMachineSettings_.getGcodeGroupValue(14));
        txtGcodeGroup15.setText(virtualMachineSettings_.getGcodeGroupValue(15));
        txtGcodeGroup16.setText(virtualMachineSettings_.getGcodeGroupValue(16));
        txtGcodeGroup17.setText(virtualMachineSettings_.getGcodeGroupValue(17));
        txtGcodeGroup18.setText(virtualMachineSettings_.getGcodeGroupValue(18));
        txtGcodeGroup19.setText(virtualMachineSettings_.getGcodeGroupValue(19));
        txtGcodeGroup20.setText(virtualMachineSettings_.getGcodeGroupValue(20));
        txtGcodeGroup21.setText(virtualMachineSettings_.getGcodeGroupValue(21));
        txtGcodeGroup22.setText(virtualMachineSettings_.getGcodeGroupValue(22));
        txtGcodeGroup23.setText(virtualMachineSettings_.getGcodeGroupValue(23));
        txtGcodeGroup24.setText(virtualMachineSettings_.getGcodeGroupValue(24));
        txtGcodeGroup25.setText(virtualMachineSettings_.getGcodeGroupValue(25));
        txtGcodeGroup26.setText(virtualMachineSettings_.getGcodeGroupValue(26));
        txtGcodeGroup27.setText(virtualMachineSettings_.getGcodeGroupValue(27));
        txtGcodeGroup28.setText(virtualMachineSettings_.getGcodeGroupValue(28));
        txtGcodeGroup29.setText(virtualMachineSettings_.getGcodeGroupValue(29));
        txtGcodeGroup30.setText(virtualMachineSettings_.getGcodeGroupValue(30));

        // code change tab
        txtCodeChangeProgramCall.setText(virtualMachineSettings_.getCodeChangeProgramCallValue());
        txtCodeChangeG.setText(virtualMachineSettings_.getCodeChangeGValue());
        txtCodeChangeM.setText(virtualMachineSettings_.getCodeChangeMValue());
        txtCodeChangeT.setText(virtualMachineSettings_.getCodeChangeTValue());
        txtCodeChangeD.setText(virtualMachineSettings_.getCodeChangeDValue());
        txtCodeChangeH.setText(virtualMachineSettings_.getCodeChangeHValue());

        // macro call tab
        txtMacroCallG.setText(virtualMachineSettings_.getMacroCallGValue());
        txtMacroCallM.setText(virtualMachineSettings_.getMacroCallMValue());

        // coordinate tab
        chkOriginMachineX.setSelected(virtualMachineSettings_.getOriginMachineCheck(0));
        txtOriginMachineX.setText(virtualMachineSettings_.getOriginMachineValue(0));
        chkOriginMachineY.setSelected(virtualMachineSettings_.getOriginMachineCheck(1));
        txtOriginMachineY.setText(virtualMachineSettings_.getOriginMachineValue(1));
        chkOriginMachineZ.setSelected(virtualMachineSettings_.getOriginMachineCheck(2));
        txtOriginMachineZ.setText(virtualMachineSettings_.getOriginMachineValue(2));
        chkOriginMachineA.setSelected(virtualMachineSettings_.getOriginMachineCheck(3));
        txtOriginMachineA.setText(virtualMachineSettings_.getOriginMachineValue(3));
        chkOriginMachineB.setSelected(virtualMachineSettings_.getOriginMachineCheck(4));
        txtOriginMachineB.setText(virtualMachineSettings_.getOriginMachineValue(4));
        chkOriginMachineC.setSelected(virtualMachineSettings_.getOriginMachineCheck(5));
        txtOriginMachineC.setText(virtualMachineSettings_.getOriginMachineValue(5));
        chkOriginMachineU.setSelected(virtualMachineSettings_.getOriginMachineCheck(6));
        txtOriginMachineU.setText(virtualMachineSettings_.getOriginMachineValue(6));
        chkOriginMachineV.setSelected(virtualMachineSettings_.getOriginMachineCheck(7));
        txtOriginMachineV.setText(virtualMachineSettings_.getOriginMachineValue(7));
        chkOriginMachineW.setSelected(virtualMachineSettings_.getOriginMachineCheck(8));
        txtOriginMachineW.setText(virtualMachineSettings_.getOriginMachineValue(8));
        chkCoordinateExtX.setSelected(virtualMachineSettings_.getCoordinateExtCheck(0));
        txtCoordinateExtX.setText(virtualMachineSettings_.getCoordinateExtValue(0));
        chkCoordinateExtY.setSelected(virtualMachineSettings_.getCoordinateExtCheck(1));
        txtCoordinateExtY.setText(virtualMachineSettings_.getCoordinateExtValue(1));
        chkCoordinateExtZ.setSelected(virtualMachineSettings_.getCoordinateExtCheck(2));
        txtCoordinateExtZ.setText(virtualMachineSettings_.getCoordinateExtValue(2));
        chkCoordinateExtA.setSelected(virtualMachineSettings_.getCoordinateExtCheck(3));
        txtCoordinateExtA.setText(virtualMachineSettings_.getCoordinateExtValue(3));
        chkCoordinateExtB.setSelected(virtualMachineSettings_.getCoordinateExtCheck(4));
        txtCoordinateExtB.setText(virtualMachineSettings_.getCoordinateExtValue(4));
        chkCoordinateExtC.setSelected(virtualMachineSettings_.getCoordinateExtCheck(5));
        txtCoordinateExtC.setText(virtualMachineSettings_.getCoordinateExtValue(5));
        chkCoordinateExtU.setSelected(virtualMachineSettings_.getCoordinateExtCheck(6));
        txtCoordinateExtU.setText(virtualMachineSettings_.getCoordinateExtValue(6));
        chkCoordinateExtV.setSelected(virtualMachineSettings_.getCoordinateExtCheck(7));
        txtCoordinateExtV.setText(virtualMachineSettings_.getCoordinateExtValue(7));
        chkCoordinateExtW.setSelected(virtualMachineSettings_.getCoordinateExtCheck(8));
        txtCoordinateExtW.setText(virtualMachineSettings_.getCoordinateExtValue(8));
        chkCoordinateG92X.setSelected(virtualMachineSettings_.getCoordinateG92Check(0));
        txtCoordinateG92X.setText(virtualMachineSettings_.getCoordinateG92Value(0));
        chkCoordinateG92Y.setSelected(virtualMachineSettings_.getCoordinateG92Check(1));
        txtCoordinateG92Y.setText(virtualMachineSettings_.getCoordinateG92Value(1));
        chkCoordinateG92Z.setSelected(virtualMachineSettings_.getCoordinateG92Check(2));
        txtCoordinateG92Z.setText(virtualMachineSettings_.getCoordinateG92Value(2));
        chkCoordinateG92A.setSelected(virtualMachineSettings_.getCoordinateG92Check(3));
        txtCoordinateG92A.setText(virtualMachineSettings_.getCoordinateG92Value(3));
        chkCoordinateG92B.setSelected(virtualMachineSettings_.getCoordinateG92Check(4));
        txtCoordinateG92B.setText(virtualMachineSettings_.getCoordinateG92Value(4));
        chkCoordinateG92C.setSelected(virtualMachineSettings_.getCoordinateG92Check(5));
        txtCoordinateG92C.setText(virtualMachineSettings_.getCoordinateG92Value(5));
        chkCoordinateG92U.setSelected(virtualMachineSettings_.getCoordinateG92Check(6));
        txtCoordinateG92U.setText(virtualMachineSettings_.getCoordinateG92Value(6));
        chkCoordinateG92V.setSelected(virtualMachineSettings_.getCoordinateG92Check(7));
        txtCoordinateG92V.setText(virtualMachineSettings_.getCoordinateG92Value(7));
        chkCoordinateG92W.setSelected(virtualMachineSettings_.getCoordinateG92Check(8));
        txtCoordinateG92W.setText(virtualMachineSettings_.getCoordinateG92Value(8));
        chkCoordinateTool.setSelected(virtualMachineSettings_.getCoordinateToolCheck());
        chkCoordinateMirror.setSelected(virtualMachineSettings_.getCoordinateMirrorCheck());
        chkCoordinateG54X.setSelected(virtualMachineSettings_.getCoordinateG54Check(0));
        txtCoordinateG54X.setText(virtualMachineSettings_.getCoordinateG54Value(0));
        chkCoordinateG54Y.setSelected(virtualMachineSettings_.getCoordinateG54Check(1));
        txtCoordinateG54Y.setText(virtualMachineSettings_.getCoordinateG54Value(1));
        chkCoordinateG54Z.setSelected(virtualMachineSettings_.getCoordinateG54Check(2));
        txtCoordinateG54Z.setText(virtualMachineSettings_.getCoordinateG54Value(2));
        chkCoordinateG54A.setSelected(virtualMachineSettings_.getCoordinateG54Check(3));
        txtCoordinateG54A.setText(virtualMachineSettings_.getCoordinateG54Value(3));
        chkCoordinateG54B.setSelected(virtualMachineSettings_.getCoordinateG54Check(4));
        txtCoordinateG54B.setText(virtualMachineSettings_.getCoordinateG54Value(4));
        chkCoordinateG54C.setSelected(virtualMachineSettings_.getCoordinateG54Check(5));
        txtCoordinateG54C.setText(virtualMachineSettings_.getCoordinateG54Value(5));
        chkCoordinateG54U.setSelected(virtualMachineSettings_.getCoordinateG54Check(6));
        txtCoordinateG54U.setText(virtualMachineSettings_.getCoordinateG54Value(6));
        chkCoordinateG54V.setSelected(virtualMachineSettings_.getCoordinateG54Check(7));
        txtCoordinateG54V.setText(virtualMachineSettings_.getCoordinateG54Value(7));
        chkCoordinateG54W.setSelected(virtualMachineSettings_.getCoordinateG54Check(8));
        txtCoordinateG54W.setText(virtualMachineSettings_.getCoordinateG54Value(8));
        chkCoordinateG55X.setSelected(virtualMachineSettings_.getCoordinateG55Check(0));
        txtCoordinateG55X.setText(virtualMachineSettings_.getCoordinateG55Value(0));
        chkCoordinateG55Y.setSelected(virtualMachineSettings_.getCoordinateG55Check(1));
        txtCoordinateG55Y.setText(virtualMachineSettings_.getCoordinateG55Value(1));
        chkCoordinateG55Z.setSelected(virtualMachineSettings_.getCoordinateG55Check(2));
        txtCoordinateG55Z.setText(virtualMachineSettings_.getCoordinateG55Value(2));
        chkCoordinateG55A.setSelected(virtualMachineSettings_.getCoordinateG55Check(3));
        txtCoordinateG55A.setText(virtualMachineSettings_.getCoordinateG55Value(3));
        chkCoordinateG55B.setSelected(virtualMachineSettings_.getCoordinateG55Check(4));
        txtCoordinateG55B.setText(virtualMachineSettings_.getCoordinateG55Value(4));
        chkCoordinateG55C.setSelected(virtualMachineSettings_.getCoordinateG55Check(5));
        txtCoordinateG55C.setText(virtualMachineSettings_.getCoordinateG55Value(5));
        chkCoordinateG55U.setSelected(virtualMachineSettings_.getCoordinateG55Check(6));
        txtCoordinateG55U.setText(virtualMachineSettings_.getCoordinateG55Value(6));
        chkCoordinateG55V.setSelected(virtualMachineSettings_.getCoordinateG55Check(7));
        txtCoordinateG55V.setText(virtualMachineSettings_.getCoordinateG55Value(7));
        chkCoordinateG55W.setSelected(virtualMachineSettings_.getCoordinateG55Check(8));
        txtCoordinateG55W.setText(virtualMachineSettings_.getCoordinateG55Value(8));
        chkCoordinateG56X.setSelected(virtualMachineSettings_.getCoordinateG56Check(0));
        txtCoordinateG56X.setText(virtualMachineSettings_.getCoordinateG56Value(0));
        chkCoordinateG56Y.setSelected(virtualMachineSettings_.getCoordinateG56Check(1));
        txtCoordinateG56Y.setText(virtualMachineSettings_.getCoordinateG56Value(1));
        chkCoordinateG56Z.setSelected(virtualMachineSettings_.getCoordinateG56Check(2));
        txtCoordinateG56Z.setText(virtualMachineSettings_.getCoordinateG56Value(2));
        chkCoordinateG56A.setSelected(virtualMachineSettings_.getCoordinateG56Check(3));
        txtCoordinateG56A.setText(virtualMachineSettings_.getCoordinateG56Value(3));
        chkCoordinateG56B.setSelected(virtualMachineSettings_.getCoordinateG56Check(4));
        txtCoordinateG56B.setText(virtualMachineSettings_.getCoordinateG56Value(4));
        chkCoordinateG56C.setSelected(virtualMachineSettings_.getCoordinateG56Check(5));
        txtCoordinateG56C.setText(virtualMachineSettings_.getCoordinateG56Value(5));
        chkCoordinateG56U.setSelected(virtualMachineSettings_.getCoordinateG56Check(6));
        txtCoordinateG56U.setText(virtualMachineSettings_.getCoordinateG56Value(6));
        chkCoordinateG56V.setSelected(virtualMachineSettings_.getCoordinateG56Check(7));
        txtCoordinateG56V.setText(virtualMachineSettings_.getCoordinateG56Value(7));
        chkCoordinateG56W.setSelected(virtualMachineSettings_.getCoordinateG56Check(8));
        txtCoordinateG56W.setText(virtualMachineSettings_.getCoordinateG56Value(8));
        chkCoordinateG57X.setSelected(virtualMachineSettings_.getCoordinateG57Check(0));
        txtCoordinateG57X.setText(virtualMachineSettings_.getCoordinateG57Value(0));
        chkCoordinateG57Y.setSelected(virtualMachineSettings_.getCoordinateG57Check(1));
        txtCoordinateG57Y.setText(virtualMachineSettings_.getCoordinateG57Value(1));
        chkCoordinateG57Z.setSelected(virtualMachineSettings_.getCoordinateG57Check(2));
        txtCoordinateG57Z.setText(virtualMachineSettings_.getCoordinateG57Value(2));
        chkCoordinateG57A.setSelected(virtualMachineSettings_.getCoordinateG57Check(3));
        txtCoordinateG57A.setText(virtualMachineSettings_.getCoordinateG57Value(3));
        chkCoordinateG57B.setSelected(virtualMachineSettings_.getCoordinateG57Check(4));
        txtCoordinateG57B.setText(virtualMachineSettings_.getCoordinateG57Value(4));
        chkCoordinateG57C.setSelected(virtualMachineSettings_.getCoordinateG57Check(5));
        txtCoordinateG57C.setText(virtualMachineSettings_.getCoordinateG57Value(5));
        chkCoordinateG57U.setSelected(virtualMachineSettings_.getCoordinateG57Check(6));
        txtCoordinateG57U.setText(virtualMachineSettings_.getCoordinateG57Value(6));
        chkCoordinateG57V.setSelected(virtualMachineSettings_.getCoordinateG57Check(7));
        txtCoordinateG57V.setText(virtualMachineSettings_.getCoordinateG57Value(7));
        chkCoordinateG57W.setSelected(virtualMachineSettings_.getCoordinateG57Check(8));
        txtCoordinateG57W.setText(virtualMachineSettings_.getCoordinateG57Value(8));
        chkCoordinateG58X.setSelected(virtualMachineSettings_.getCoordinateG58Check(0));
        txtCoordinateG58X.setText(virtualMachineSettings_.getCoordinateG58Value(0));
        chkCoordinateG58Y.setSelected(virtualMachineSettings_.getCoordinateG58Check(1));
        txtCoordinateG58Y.setText(virtualMachineSettings_.getCoordinateG58Value(1));
        chkCoordinateG58Z.setSelected(virtualMachineSettings_.getCoordinateG58Check(2));
        txtCoordinateG58Z.setText(virtualMachineSettings_.getCoordinateG58Value(2));
        chkCoordinateG58A.setSelected(virtualMachineSettings_.getCoordinateG58Check(3));
        txtCoordinateG58A.setText(virtualMachineSettings_.getCoordinateG58Value(3));
        chkCoordinateG58B.setSelected(virtualMachineSettings_.getCoordinateG58Check(4));
        txtCoordinateG58B.setText(virtualMachineSettings_.getCoordinateG58Value(4));
        chkCoordinateG58C.setSelected(virtualMachineSettings_.getCoordinateG58Check(5));
        txtCoordinateG58C.setText(virtualMachineSettings_.getCoordinateG58Value(5));
        chkCoordinateG58U.setSelected(virtualMachineSettings_.getCoordinateG58Check(6));
        txtCoordinateG58U.setText(virtualMachineSettings_.getCoordinateG58Value(6));
        chkCoordinateG58V.setSelected(virtualMachineSettings_.getCoordinateG58Check(7));
        txtCoordinateG58V.setText(virtualMachineSettings_.getCoordinateG58Value(7));
        chkCoordinateG58W.setSelected(virtualMachineSettings_.getCoordinateG58Check(8));
        txtCoordinateG58W.setText(virtualMachineSettings_.getCoordinateG58Value(8));
        chkCoordinateG59X.setSelected(virtualMachineSettings_.getCoordinateG59Check(0));
        txtCoordinateG59X.setText(virtualMachineSettings_.getCoordinateG59Value(0));
        chkCoordinateG59Y.setSelected(virtualMachineSettings_.getCoordinateG59Check(1));
        txtCoordinateG59Y.setText(virtualMachineSettings_.getCoordinateG59Value(1));
        chkCoordinateG59Z.setSelected(virtualMachineSettings_.getCoordinateG59Check(2));
        txtCoordinateG59Z.setText(virtualMachineSettings_.getCoordinateG59Value(2));
        chkCoordinateG59A.setSelected(virtualMachineSettings_.getCoordinateG59Check(3));
        txtCoordinateG59A.setText(virtualMachineSettings_.getCoordinateG59Value(3));
        chkCoordinateG59B.setSelected(virtualMachineSettings_.getCoordinateG59Check(4));
        txtCoordinateG59B.setText(virtualMachineSettings_.getCoordinateG59Value(4));
        chkCoordinateG59C.setSelected(virtualMachineSettings_.getCoordinateG59Check(5));
        txtCoordinateG59C.setText(virtualMachineSettings_.getCoordinateG59Value(5));
        chkCoordinateG59U.setSelected(virtualMachineSettings_.getCoordinateG59Check(6));
        txtCoordinateG59U.setText(virtualMachineSettings_.getCoordinateG59Value(6));
        chkCoordinateG59V.setSelected(virtualMachineSettings_.getCoordinateG59Check(7));
        txtCoordinateG59V.setText(virtualMachineSettings_.getCoordinateG59Value(7));
        chkCoordinateG59W.setSelected(virtualMachineSettings_.getCoordinateG59Check(8));
        txtCoordinateG59W.setText(virtualMachineSettings_.getCoordinateG59Value(8));

        // tool offset tab
        txtToolOffset.setText(virtualMachineSettings_.getToolOffsetValue());

        // variables tab
        txtVariables.setText(virtualMachineSettings_.getVariablesValue());

        // tool change tab
        chkToolChangeProgram.setSelected(virtualMachineSettings_.getToolChangeProgramCheck());
        chkToolChangeXY.setSelected(virtualMachineSettings_.getToolChangeXYCheck());
        txtToolChangeProgram.setText(virtualMachineSettings_.getToolChangeProgramValue());
        chkToolChangeMCode.setSelected(virtualMachineSettings_.getToolChangeMCodeCheck());
        txtToolChangeMCode.setText(virtualMachineSettings_.getToolChangeMCodeValue());

        // skip function tab
        chkSkipFunctionProgram.setSelected(virtualMachineSettings_.getSkipFunctionProgramCheck());
        txtSkipFunctionProgram.setText(virtualMachineSettings_.getSkipFunctionProgramValue());

        // ladder tab
        txtLadderN1.setText(virtualMachineSettings_.getLadderValue(0));
        txtLadderN2.setText(virtualMachineSettings_.getLadderValue(1));
        txtLadderG1.setText(virtualMachineSettings_.getLadderValue(2));
        txtLadderG2.setText(virtualMachineSettings_.getLadderValue(3));
        txtLadderX1.setText(virtualMachineSettings_.getLadderValue(4));
        txtLadderX2.setText(virtualMachineSettings_.getLadderValue(5));
        txtLadderY1.setText(virtualMachineSettings_.getLadderValue(6));
        txtLadderY2.setText(virtualMachineSettings_.getLadderValue(7));
        txtLadderZ1.setText(virtualMachineSettings_.getLadderValue(8));
        txtLadderZ2.setText(virtualMachineSettings_.getLadderValue(9));
        txtLadderA1.setText(virtualMachineSettings_.getLadderValue(10));
        txtLadderA2.setText(virtualMachineSettings_.getLadderValue(11));
        txtLadderB1.setText(virtualMachineSettings_.getLadderValue(12));
        txtLadderB2.setText(virtualMachineSettings_.getLadderValue(13));
        txtLadderC1.setText(virtualMachineSettings_.getLadderValue(14));
        txtLadderC2.setText(virtualMachineSettings_.getLadderValue(15));
        txtLadderU1.setText(virtualMachineSettings_.getLadderValue(16));
        txtLadderU2.setText(virtualMachineSettings_.getLadderValue(17));
        txtLadderV1.setText(virtualMachineSettings_.getLadderValue(18));
        txtLadderV2.setText(virtualMachineSettings_.getLadderValue(19));
        txtLadderW1.setText(virtualMachineSettings_.getLadderValue(20));
        txtLadderW2.setText(virtualMachineSettings_.getLadderValue(21));
        txtLadderM1.setText(virtualMachineSettings_.getLadderValue(22));
        txtLadderM2.setText(virtualMachineSettings_.getLadderValue(23));
        txtLadderR1.setText(virtualMachineSettings_.getLadderValue(24));
        txtLadderR2.setText(virtualMachineSettings_.getLadderValue(25));
        txtLadderI1.setText(virtualMachineSettings_.getLadderValue(26));
        txtLadderI2.setText(virtualMachineSettings_.getLadderValue(27));
        txtLadderJ1.setText(virtualMachineSettings_.getLadderValue(28));
        txtLadderJ2.setText(virtualMachineSettings_.getLadderValue(29));
        txtLadderK1.setText(virtualMachineSettings_.getLadderValue(30));
        txtLadderK2.setText(virtualMachineSettings_.getLadderValue(31));
        txtLadderF1.setText(virtualMachineSettings_.getLadderValue(32));
        txtLadderF2.setText(virtualMachineSettings_.getLadderValue(33));
        txtLadderS1.setText(virtualMachineSettings_.getLadderValue(34));
        txtLadderS2.setText(virtualMachineSettings_.getLadderValue(35));
        txtLadderT1.setText(virtualMachineSettings_.getLadderValue(36));
        txtLadderT2.setText(virtualMachineSettings_.getLadderValue(37));

        // option tab
        chkOptionOptimization.setSelected(virtualMachineSettings_.getOptionOptimization());
        chkOptionExComment.setSelected(virtualMachineSettings_.getOptionExComment());
        chkOptionDisable30033004.setSelected(virtualMachineSettings_.getOptionDisable30033004());
        chkOptionReplace3006M0.setSelected(virtualMachineSettings_.getOptionReplace3006M0());
        chkOptionOnlyS.setSelected(virtualMachineSettings_.getOptionOnlyS());
        chkOptionRS274NGC.setSelected(virtualMachineSettings_.getOptionRS274NGC());
        chkOptionDebugJson.setSelected(virtualMachineSettings_.getOptionDebugJson());
        txtOptionMaxFeedRate.setText(virtualMachineSettings_.getOptionMaxFeedRate());
        txtOptionMaxRevolution.setText(virtualMachineSettings_.getOptionMaxRevolution());
        txtOptionStartProgram.setText(virtualMachineSettings_.getOptionStartProgram());
        txtOptionBlockProgram.setText(virtualMachineSettings_.getOptionBlockProgram());
        txtOptionEndProgram.setText(virtualMachineSettings_.getOptionEndProgram());
    }

    public void runViewRunning() {
        if (Platform.isFxApplicationThread()) {
            viewRunning(virtualMachineSettings_.isViewRunning());
        } else {
            Platform.runLater(() -> {
                viewRunning(virtualMachineSettings_.isViewRunning());
            });
        }
    }

    private void viewRunning(boolean bln) {
        chkLog.setDisable(!bln);
        chkDebug.setDisable(!bln);
        txtBackGroundFile1.setDisable(!bln);
        btnBackGroundFile1.setDisable(!bln);
        txtBackGroundFile2.setDisable(!bln);
        btnBackGroundFile2.setDisable(!bln);
        txtBackGroundFile3.setDisable(!bln);
        btnBackGroundFile3.setDisable(!bln);
        txtExternalSubProgramDirectory.setDisable(!bln);
        btnExternalSubProgramDirectory.setDisable(!bln);
    }

    public void runViewSerial() {
        if (Platform.isFxApplicationThread()) {
            viewSerial(baseSettings_.isViewSerial());
        } else {
            Platform.runLater(() -> {
                viewSerial(baseSettings_.isViewSerial());
            });
        }
    }

    private void viewSerial(boolean bln) {
        btnVirtualMachineLoad.setDisable(!bln);
        btnSerialPortList.setDisable(!bln);
        txtSerialPort.setDisable(!bln);
        btnSerialOpen.setDisable(!bln);
        btnSerialClose.setDisable(bln);
        txtSerialBaudrate.setDisable(!bln);
        cmbSerialDataBits.setDisable(!bln);
        cmbSerialStopBits.setDisable(!bln);
        cmbSerialParity.setDisable(!bln);
        cmbSerialEOB.setDisable(!bln);
        txtSerialBufferLimit.setDisable(!bln);
        txtSerialDelay.setDisable(!bln);
        cmbSerialStartCode.setDisable(!bln);
        cmbSerialEndCode.setDisable(!bln);
        chkSerialCharacter.setDisable(!bln);
        chkSerialObserveCTS.setDisable(!bln);
        chkSerialObserveDSR.setDisable(!bln);
        chkSerialObserveDC2DC4.setDisable(!bln);
    }

    public void runViewToolChange() {
        if (Platform.isFxApplicationThread()) {
            viewToolChange();
        } else {
            Platform.runLater(() -> {
                viewToolChange();
            });
        }
    }

    private void viewToolChange() {
        if (chkToolChangeProgram.isSelected()) {
            chkToolChangeXY.setDisable(false);
            txtToolChangeProgram.setDisable(false);
        } else {
            chkToolChangeXY.setDisable(true);
            txtToolChangeProgram.setDisable(true);
        }

        if (chkToolChangeMCode.isSelected()) {
            txtToolChangeMCode.setDisable(false);
        } else {
            txtToolChangeMCode.setDisable(true);
        }
    }

    public void runViewSkipFunction() {
        if (Platform.isFxApplicationThread()) {
            viewSkipFunction();
        } else {
            Platform.runLater(() -> {
                viewSkipFunction();
            });
        }
    }

    private void viewSkipFunction() {
        if (chkSkipFunctionProgram.isSelected()) {
            txtSkipFunctionProgram.setDisable(false);
        } else {
            txtSkipFunctionProgram.setDisable(true);
        }
    }

    /**
     *
     * @param baseSettings
     * @param virtualMachineSettings
     */
    public void startUp(DesignController.BaseSettings baseSettings, DesignController.VirtualMachineSettings virtualMachineSettings) {
        baseSettings_ = baseSettings;
        virtualMachineSettings_ = virtualMachineSettings;

        // design
        addEventDesign();
        runInitDesign();
        runValueUpdate();
        runViewSerial();
        runViewToolChange();
        runViewSkipFunction();
    }

    /**
     *
     * @return
     */
    public boolean cleanUp() {
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
