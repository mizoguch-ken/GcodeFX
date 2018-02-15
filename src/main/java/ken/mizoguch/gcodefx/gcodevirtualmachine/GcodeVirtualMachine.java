/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.gcodevirtualmachine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.swing.event.EventListenerList;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.DesignEnums;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeVirtualMachinePlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeVirtualMachinePluginListener;
import ken.mizoguch.gcodefx.webeditor.WebEditor;
import ken.mizoguch.gcodefx.serial.Serial;
import ken.mizoguch.gcodeparser.GcodeParser;
import ken.mizoguch.gcodeparser.GcodeParserConstants;
import ken.mizoguch.gcodeparser.ParseException;
import ken.mizoguch.gcodeparser.TokenMgrError;
import ken.mizoguch.ladders.Ladders;
import ken.mizoguch.webviewer.WebViewer;

/**
 *
 * @author mizoguch-ken
 */
public class GcodeVirtualMachine extends Service<Void> implements GcodeVirtualMachinePlugin {

    public static enum CHG {
        PROGRAM, PROGRAMNUMBER, BLOCKSKIP,
        SETTINGS, BACK_PROGRAM, EXTERNAL_SUBPROGRAM_DIR,
        GGROUP,
        CODECHANGE,
        MACROCALL,
        COORDINATE,
        TOOLOFFSET,
        VARIABLES,
        TOOLCHANGE,
        SKIPFUNCTION,
        LADDER,
        OPTION;
    }

    private final EventListenerList eventListenerList_;

    private final GcodeInterpreter interpreter_ = new GcodeInterpreter();
    private final List<GcodeBytecode> bytecodesFore_ = new ArrayList<>();
    private final List<GcodeBytecode> bytecodesBack_ = new ArrayList<>();
    private final List<GcodeBytecode> bytecodesStart_ = new ArrayList<>();
    private final List<GcodeBytecode> bytecodesBlock_ = new ArrayList<>();
    private final List<GcodeBytecode> bytecodesEnd_ = new ArrayList<>();
    private final Map<String, Integer> programNumbersFore_ = new HashMap<>();
    private final Map<String, Integer> programNumbersBack_ = new HashMap<>();
    private final Map<Double, Integer> ggroup_ = new HashMap<>();
    private final Map<String, String> programcallconvert_ = new HashMap<>();
    private final Map<Double, String> gcodeconvert_ = new HashMap<>();
    private final Map<Double, String> mcodeconvert_ = new HashMap<>();
    private final Map<Integer, String> tcodeconvert_ = new HashMap<>();
    private final Map<Integer, String> dcodeconvert_ = new HashMap<>();
    private final Map<Integer, String> hcodeconvert_ = new HashMap<>();
    private final Map<Double, String> gmacrocall_ = new HashMap<>();
    private final Map<Double, String> mmacrocall_ = new HashMap<>();
    private final Map<Integer, String> ladderAddress_ = new HashMap<>();
    private final Map<Integer, Integer> maxfeedrate_ = new HashMap<>();
    private final Map<Integer, Integer> maxrevolution_ = new HashMap<>();
    private final Map<Integer, Boolean> blockskip_ = new HashMap<>();
    private final Map<GcodeInterpreter.OPT, String> option_;
    private Path externalsubprogramdir_;
    private String programnumber_;

    private final Map<CHG, Boolean> change_;
    private Ladders ladders_;
    private WebViewer webViewer_;
    private WebEditor webEditor_;
    private Serial serial_;
    private String serialEob_, startCode_, endCode_, program_;
    private Path debugFilePath_;

    private final Pattern pUInt = Pattern.compile("^[0-9]+$");

    /**
     *
     * @param input
     * @return
     */
    public static List<String> parse(InputStream input) {
        try {
            GcodeParser gcodeParser = new GcodeParser(input);
            gcodeParser.Start();
            if (gcodeParser.isErrors()) {
                gcodeParser.getParseErrors().stream().forEach((ex) -> {
                    Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), ex.getMessage(), true);
                });
            } else {
                return gcodeParser.getBytecodes();
            }
        } catch (TokenMgrError | ParseException | RuntimeException ex) {
            Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), ex.getMessage(), true);
        }
        return null;
    }

    /**
     *
     * @param bytecodes
     * @param programNumbers
     * @param blocks
     * @param beginIndex
     * @param isDebug
     * @return
     */
    public static boolean addBytecodes(List<GcodeBytecode> bytecodes, Map<String, Integer> programNumbers, List<String> blocks, int beginIndex, boolean isDebug) {
        if (blocks == null) {
            return false;
        }

        String block;
        String[] word;
        int token;
        String strval;
        boolean isEOB = true;
        int i;
        Pattern pUInt = Pattern.compile("^[0-9]+$");

        for (i = beginIndex; i < blocks.size(); i++) {
            block = blocks.get(i).trim();
            if (block.isEmpty()) {
                return false;
            }
            word = block.split("\t");
            token = Integer.parseInt(word[0]);
            if (programNumbers != null) {
                switch (token) {
                    case GcodeParserConstants.COMMENT:
                        break;
                    case GcodeParserConstants.O:
                        if (word.length == 2) {
                            strval = "O" + Integer.toString(Integer.parseInt(word[1]));
                            if (programNumbers.containsKey(strval)) {
                                Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.DUPLICATE_PROGRAM_NUMBER.toString() + "[" + word[1] + "]", true);
                                return false;
                            } else {
                                programNumbers.put(strval, bytecodes.size());
                            }
                        } else {
                            Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.NO_CORRESPOND_PROGRAM_NUMBER.toString(), true);
                            return false;
                        }
                        break;
                    case GcodeParserConstants.FILENAME:
                        if (isEOB) {
                            if (word.length == 2) {
                                strval = word[1].trim();
                                if (strval.startsWith("O")) {
                                    if (pUInt.matcher(strval.substring(1)).find()) {
                                        strval = "O" + Integer.toString(Integer.parseInt(strval.substring(1)));
                                    }
                                }
                                if (programNumbers.containsKey(strval)) {
                                    Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.DUPLICATE_PROGRAM_NUMBER.toString() + "[" + word[1] + "]", true);
                                    return false;
                                } else {
                                    programNumbers.put(strval, bytecodes.size());
                                }
                            } else {
                                Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.NO_CORRESPOND_PROGRAM_NUMBER.toString(), true);
                                return false;
                            }
                        }
                        isEOB = false;
                        break;
                    case GcodeParserConstants.EOB:
                        isEOB = true;
                        break;
                    default:
                        isEOB = false;
                        break;
                }
            }
            switch (word.length) {
                case 1:
                    bytecodes.add(new GcodeBytecode(token, isDebug));
                    break;
                case 2:
                    bytecodes.add(new GcodeBytecode(token, word[1], isDebug));
                    break;
                default:
                    Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.BYTE_CODE_LENGTH_INVALID.toString(), true);
                    return false;
            }
        }
        return true;
    }

    /**
     *
     * @param bytecodes
     */
    public static void clearBytecodes(List<GcodeBytecode> bytecodes) {
        bytecodes.clear();
    }

    /**
     *
     * @param programNumbers
     */
    public static void clearProgramNumbers(Map<String, Integer> programNumbers) {
        programNumbers.clear();
    }

    /**
     *
     */
    public GcodeVirtualMachine() {
        eventListenerList_ = new EventListenerList();
        option_ = new HashMap<>();
        externalsubprogramdir_ = null;
        change_ = new HashMap<>();
        webEditor_ = null;
        serial_ = null;
        debugFilePath_ = null;

        interpreter_.setEventListenerList(null);
    }

    /**
     *
     * @return
     */
    public GcodeInterpreter getGcodeInterpreter() {
        return interpreter_;
    }

    /**
     *
     * @param chg
     * @param bln
     */
    public void putChange(CHG chg, boolean bln) {
        change_.put(chg, bln);
    }

    /**
     *
     * @param program
     */
    public void setProgram(String program) {
        program_ = program;
    }

    /**
     *
     * @param programnumber
     */
    public void setProgramNumber(String programnumber) {
        programnumber_ = programnumber;
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
     * @param webViewer
     */
    public void setWebViewer(WebViewer webViewer) {
        webViewer_ = webViewer;
    }

    /**
     *
     * @param editor
     */
    public void setWebEditor(WebEditor editor) {
        webEditor_ = editor;
    }

    /**
     *
     * @param serial
     */
    public void setSerial(Serial serial) {
        serial_ = serial;
    }

    /**
     *
     * @param serialEob
     */
    public void setSerialEOB(String serialEob) {
        serialEob_ = serialEob;
    }

    /**
     *
     * @param option
     * @param string
     */
    public void putOption(GcodeInterpreter.OPT option, String string) {
        option_.put(option, string);
    }

    /**
     *
     * @param option
     */
    public void removeOption(GcodeInterpreter.OPT option) {
        option_.remove(option);
    }

    /**
     *
     * @param externalsubprogramdir
     */
    public void setExternalSubProgramDirectory(Path externalsubprogramdir) {
        externalsubprogramdir_ = externalsubprogramdir;
    }

    /**
     *
     * @param global
     */
    public void setGlobal(Map<Integer, GcodeVariable> global) {
        interpreter_.setGlobal(global);
    }

    /**
     *
     * @param name
     */
    public void setName(Map<String, Integer> name) {
        interpreter_.setName(name);
    }

    /**
     *
     * @param address
     * @param value
     * @param string
     * @param name
     */
    public void setVariable(int address, double value, String string, String name) {
        interpreter_.setVariable(address, value, string, name);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putGGroup(double key, int value) {
        ggroup_.put(key, value);
    }

    /**
     *
     */
    public void clearGGroup() {
        ggroup_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putProgramCallConvert(String key, String value) {
        programcallconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearProgramCallConvert() {
        programcallconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putGCodeConvert(double key, String value) {
        gcodeconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearGCodeConvert() {
        gcodeconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putMCodeConvert(double key, String value) {
        mcodeconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearMCodeConvert() {
        mcodeconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putTCodeConvert(int key, String value) {
        tcodeconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearTCodeConvert() {
        tcodeconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putDCodeConvert(int key, String value) {
        dcodeconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearDCodeConvert() {
        dcodeconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putHCodeConvert(int key, String value) {
        hcodeconvert_.put(key, value);
    }

    /**
     *
     */
    public void clearHCodeConvert() {
        hcodeconvert_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putGMacroCall(double key, String value) {
        gmacrocall_.put(key, value);
    }

    /**
     *
     */
    public void clearGMacroCall() {
        gmacrocall_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putMMacroCall(double key, String value) {
        mmacrocall_.put(key, value);
    }

    /**
     *
     */
    public void clearMMacroCall() {
        mmacrocall_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putLadderAddress(int key, String value) {
        ladderAddress_.put(key, value);
    }

    /**
     *
     */
    public void clearLadderAddress() {
        ladderAddress_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putMaxFeedRate(int key, int value) {
        maxfeedrate_.put(key, value);
    }

    /**
     *
     */
    public void clearMaxFeedRate() {
        maxfeedrate_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putMaxRevolution(int key, int value) {
        maxrevolution_.put(key, value);
    }

    /**
     *
     */
    public void clearMaxRevolution() {
        maxrevolution_.clear();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void putBlockSkip(int key, boolean value) {
        blockskip_.put(key, value);
    }

    /**
     *
     * @param debugFilePath
     */
    public void setDebugFileName(Path debugFilePath) {
        debugFilePath_ = debugFilePath;
    }

    /**
     *
     * @param input
     * @param beginIndex
     * @param isDebug
     * @return
     */
    public boolean addBytecodesFore(InputStream input, int beginIndex, boolean isDebug) {
        if (addBytecodes(bytecodesFore_, programNumbersFore_, parse(input), beginIndex, isDebug)) {
            for (String key : programNumbersFore_.keySet()) {
                if (programNumbersBack_.containsKey(key)) {
                    Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.DUPLICATE_PROGRAM_NUMBER.toString() + "[" + key + "]", true);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void clearBytecodesFore() {
        clearBytecodes(bytecodesFore_);
    }

    /**
     *
     */
    public void clearProgramNumbersFore() {
        clearProgramNumbers(programNumbersFore_);
    }

    /**
     *
     * @param input
     * @param beginIndex
     * @param isDebug
     * @return
     */
    public boolean addBytecodesBack(InputStream input, int beginIndex, boolean isDebug) {
        if (addBytecodes(bytecodesBack_, programNumbersBack_, parse(input), beginIndex, isDebug)) {
            for (String key : programNumbersBack_.keySet()) {
                if (programNumbersFore_.containsKey(key)) {
                    Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), GcodeVirtualMachineEnums.DUPLICATE_PROGRAM_NUMBER.toString() + "[" + key + "]", true);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param blocks
     * @param beginIndex
     * @param isDebug
     * @return
     */
    public boolean addBytecodesBack(List<String> blocks, int beginIndex, boolean isDebug) {
        return addBytecodes(bytecodesBack_, programNumbersBack_, blocks, beginIndex, isDebug);
    }

    /**
     *
     */
    public void clearBytecodesBack() {
        clearBytecodes(bytecodesBack_);
    }

    /**
     *
     */
    public void clearProgramNumbersBack() {
        clearProgramNumbers(programNumbersBack_);
    }

    /**
     *
     * @param input
     * @param isDebug
     * @return
     */
    public boolean addBytecodesStart(InputStream input, boolean isDebug) {
        return addBytecodes(bytecodesStart_, null, parse(input), 0, isDebug);
    }

    /**
     *
     */
    public void clearBytecodesStart() {
        clearBytecodes(bytecodesStart_);
    }

    /**
     *
     * @param input
     * @param isDebug
     * @return
     */
    public boolean addBytecodesBlock(InputStream input, boolean isDebug) {
        return addBytecodes(bytecodesBlock_, null, parse(input), 0, isDebug);
    }

    /**
     *
     */
    public void clearBytecodesBlock() {
        clearBytecodes(bytecodesBlock_);
    }

    /**
     *
     * @param input
     * @param isDebug
     * @return
     */
    public boolean addBytecodesEnd(InputStream input, boolean isDebug) {
        return addBytecodes(bytecodesEnd_, null, parse(input), 0, isDebug);
    }

    /**
     *
     */
    public void clearBytecodesEnd() {
        clearBytecodes(bytecodesEnd_);
    }

    /**
     *
     * @param value
     */
    public void setStartCode(String value) {
        startCode_ = value;
    }

    /**
     *
     * @param value
     */
    public void setEndCode(String value) {
        endCode_ = value;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                List<String> startCodeChar = new ArrayList<>();
                List<String> endCodeChar = new ArrayList<>();
                List<Double> checkGGroup = new ArrayList<>();
                int ret = GcodeInterpreter.RET_END;
                boolean isWaitLoop;

                do {
                    // sleep
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ex) {
                    }

                    isWaitLoop = false;
                    for (CHG enums : CHG.values()) {
                        if (change_.get(enums)) {
                            isWaitLoop = true;
                        }
                    }
                } while (isWaitLoop);

                try {
                    BufferedWriter ngcBw = null;
                    BufferedWriter debugBw = null;
                    BufferedWriter drawBw = null;

                    if (debugFilePath_ != null) {
                        ngcBw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(debugFilePath_.toString() + ".ngc")), "UTF-8"));
                        debugBw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(debugFilePath_.toString() + ".dbg")), "UTF-8"));
                        drawBw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(debugFilePath_.toString() + ".dxf")), "UTF-8"));
                        setDxfHeader(drawBw);
                    }

                    // notify
                    for (GcodeVirtualMachinePluginListener listener : eventListenerList_.getListeners(GcodeVirtualMachinePluginListener.class)) {
                        listener.startGcodeVirtualMachine();
                    }

                    // interpreter flg reset
                    option_.remove(GcodeInterpreter.OPT.DISABLE_OUTPUT);

                    // interpreter settings
                    interpreter_.setBytecodesFore(bytecodesFore_);
                    interpreter_.setBytecodesBack(bytecodesBack_);
                    interpreter_.setBytecodesExternal(null);
//                    interpreter_.setBytecodesOption(bytecodesBlock_);
                    interpreter_.setBytecodesScript(null);
                    interpreter_.setProgramNumbersFore(programNumbersFore_);
                    interpreter_.setProgramNumbersBack(programNumbersBack_);
                    interpreter_.setProgramNumbersExternal(null);
//                    interpreter_.setGlobal(null);
                    interpreter_.setLocal(null);
                    interpreter_.setGGroup(ggroup_);
                    interpreter_.setProgramCallConvert(programcallconvert_);
                    interpreter_.setGCodeConvert(gcodeconvert_);
                    interpreter_.setMCodeConvert(mcodeconvert_);
                    interpreter_.setTCodeConvert(tcodeconvert_);
                    interpreter_.setDCodeConvert(dcodeconvert_);
                    interpreter_.setHCodeConvert(hcodeconvert_);
                    interpreter_.setGMacroCall(gmacrocall_);
                    interpreter_.setMMacroCall(mmacrocall_);
                    interpreter_.setLadderAddress(ladderAddress_);
                    interpreter_.setMaxFeedRate(maxfeedrate_);
                    interpreter_.setMaxRevolution(maxrevolution_);
                    interpreter_.setBlockSkip(blockskip_);
                    interpreter_.setArgModal(null);
                    interpreter_.setOptimize(null);
                    interpreter_.setDraw(null);
                    interpreter_.setWords(null);
                    interpreter_.setFlg(null);
//                    interpreter_.setState(GcodeInterpreter.STATE.NONE);
//                    interpreter_.setProgramNumber(programnumber_);
                    interpreter_.setForeLine(-1);
                    interpreter_.setLocalLevel(0);
                    interpreter_.setCallJump(-1);
                    interpreter_.setOptions(option_);
                    interpreter_.setExternalSubProgramDirectory(externalsubprogramdir_);
                    interpreter_.setLadders(ladders_);
                    interpreter_.setLaddersIo(null);
                    interpreter_.setLaddersIoImmediate(null);
                    interpreter_.setWebViewer(webViewer_);
                    interpreter_.setWebEditor(webEditor_);
                    interpreter_.setSerial(serial_);
                    interpreter_.setSerialEOB(serialEob_);
                    interpreter_.setScript(null);
                    interpreter_.setNgcBufferedWriter(ngcBw);
                    interpreter_.setDebugBufferedWriter(debugBw);
                    interpreter_.setDrawBufferedWriter(drawBw);

                    // start code
                    switch (startCode_) {
                        case "none":
                            break;
                        case "EOB":
                            startCodeChar.add("");
                            break;
                        case "%":
                            startCodeChar.add("%");
                            break;
                        case "DC2":
                            startCodeChar.add(Character.toString((char) 0x12));
                            break;
                        case "% | none":
                            if (programnumber_ == null) {
                                startCodeChar.add("%");
                            }
                            break;
                        case "% | EOB":
                            if (programnumber_ == null) {
                                startCodeChar.add("%");
                            } else {
                                startCodeChar.add("");
                            }
                            break;
                        default:
                            break;
                    }

                    if (!startCodeChar.isEmpty()) {
                        startCodeChar.stream().forEach((code) -> {
                            if (((code.length() + serialEob_.length()) % 2) == 1) {
                                code += " ";
                            }

                            interpreter_.serialOutput(code + serialEob_);
                        });
                    }

                    if (programnumber_ != null) {
                        // showing global variable update
                        interpreter_.notifyGlobalVariables();

                        // program number
                        if (!option_.containsKey(GcodeInterpreter.OPT.RS274NGC)) {
                            if (programnumber_.startsWith("O")) {
                                if (pUInt.matcher(programnumber_.substring(1)).find()) {
                                    interpreter_.serialOutput("O" + Integer.toString(Integer.parseInt(programnumber_.substring(1))), -1, GcodeInterpreter.STATE.NONE);
                                    interpreter_.fileOutput("O" + Integer.toString(Integer.parseInt(programnumber_.substring(1))), GcodeInterpreter.STATE.NONE, false);
                                } else {
                                    interpreter_.serialOutput("<" + programnumber_ + ">", -1, GcodeInterpreter.STATE.NONE);
                                    interpreter_.fileOutput("<" + programnumber_ + ">", GcodeInterpreter.STATE.NONE, false);
                                }
                            } else {
                                interpreter_.serialOutput("<" + programnumber_ + ">", -1, GcodeInterpreter.STATE.NONE);
                                interpreter_.fileOutput("<" + programnumber_ + ">", GcodeInterpreter.STATE.NONE, false);
                            }
                        }

                        // g group check
                        checkGGroup.add(0.0);
                        checkGGroup.add(1.0);
                        checkGGroup.add(2.0);
                        checkGGroup.add(3.0);
                        checkGGroup.add(10.0);
                        checkGGroup.add(11.0);
                        checkGGroup.add(17.0);
                        checkGGroup.add(18.0);
                        checkGGroup.add(19.0);
                        checkGGroup.add(28.0);
                        checkGGroup.add(31.0);
                        checkGGroup.add(40.0);
                        checkGGroup.add(41.0);
                        checkGGroup.add(42.0);
                        checkGGroup.add(43.0);
                        checkGGroup.add(44.0);
                        checkGGroup.add(49.0);
                        checkGGroup.add(50.1);
                        checkGGroup.add(51.1);
                        checkGGroup.add(53.0);
                        checkGGroup.add(54.0);
                        checkGGroup.add(54.1);
                        checkGGroup.add(55.0);
                        checkGGroup.add(56.0);
                        checkGGroup.add(57.0);
                        checkGGroup.add(58.0);
                        checkGGroup.add(59.0);
                        checkGGroup.add(65.0);
                        checkGGroup.add(66.0);
                        checkGGroup.add(67.0);
                        checkGGroup.add(73.0);
                        checkGGroup.add(74.0);
                        checkGGroup.add(76.0);
                        checkGGroup.add(80.0);
                        checkGGroup.add(81.0);
                        checkGGroup.add(82.0);
                        checkGGroup.add(83.0);
                        checkGGroup.add(84.0);
                        checkGGroup.add(85.0);
                        checkGGroup.add(86.0);
                        checkGGroup.add(87.0);
                        checkGGroup.add(88.0);
                        checkGGroup.add(89.0);
                        checkGGroup.add(90.0);
                        checkGGroup.add(91.0);
                        checkGGroup.add(98.0);
                        checkGGroup.add(99.0);
                        for (Double ggroup : checkGGroup) {
                            if (!ggroup_.containsKey(ggroup)) {
                                writeLog(GcodeVirtualMachineEnums.NO_G_GROUP.toString() + "[G" + ggroup + "]", true);
                                ret = GcodeInterpreter.RET_ERROR;
                            }
                        }

                        // start program
                        if (bytecodesStart_.size() > 0) {
                            switch (ret) {
                                case GcodeInterpreter.RET_NONE:
                                    // abnormal end
                                    break;
                                case GcodeInterpreter.RET_ERROR:
                                    // error end
                                    break;
                                case GcodeInterpreter.RET_END:
                                    // normal end
                                    interpreter_.setBytecodesOption(bytecodesStart_);
                                    interpreter_.setState(GcodeInterpreter.STATE.OPTION);
                                    interpreter_.setProgramNumber(null);
                                    ret = interpreter_.exec();
                                    break;
                                default:
                                    break;
                            }
                        }

                        // main program
                        if (bytecodesFore_.size() > 0) {
                            switch (ret) {
                                case GcodeInterpreter.RET_NONE:
                                    // abnormal end
                                    break;
                                case GcodeInterpreter.RET_ERROR:
                                    // error end
                                    break;
                                case GcodeInterpreter.RET_END:
                                    // normal end
                                    interpreter_.setBytecodesOption(bytecodesBlock_);
                                    interpreter_.setState(GcodeInterpreter.STATE.FORE_BACK);
                                    interpreter_.setProgramNumber(programnumber_);
                                    ret = interpreter_.exec();
                                    break;
                                default:
                                    break;
                            }
                        }

                        // end program
                        if (bytecodesEnd_.size() > 0) {
                            switch (ret) {
                                case GcodeInterpreter.RET_NONE:
                                    // abnormal end
                                    break;
                                case GcodeInterpreter.RET_ERROR:
                                    // error end
                                    break;
                                case GcodeInterpreter.RET_END:
                                    // normal end
                                    interpreter_.setBytecodesOption(bytecodesEnd_);
                                    interpreter_.setState(GcodeInterpreter.STATE.OPTION);
                                    interpreter_.setProgramNumber(null);
                                    ret = interpreter_.exec();
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        // normal send
                        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(program_))) {
                            String block;
                            int line = 1;
                            while ((block = bufferedReader.readLine()) != null) {
                                block = block.replace(Character.toString((char) 0x02), "");
                                block = block.replace(Character.toString((char) 0x03), "");
                                block = block.replace(Character.toString((char) 0x11), "");
                                block = block.replace(Character.toString((char) 0x12), "");
                                block = block.replace(Character.toString((char) 0x13), "");
                                block = block.replace(Character.toString((char) 0x14), "");
                                block = block.replace(Character.toString((char) 0x1b), "");
                                block = block.trim();
                                if (!"%".equals(block)) {
                                    block = block.replace("%", "");
                                    interpreter_.serialOutput(block, line, GcodeInterpreter.STATE.NONE);
                                    interpreter_.fileOutput(block, GcodeInterpreter.STATE.NONE, false);
                                }
                                line++;
                            }
                        }
                    }

                    // end code
                    switch (endCode_) {
                        case "none":
                            break;
                        case "EOB":
                            endCodeChar.add("");
                            break;
                        case "%":
                            endCodeChar.add("%");
                            break;
                        case "DC4":
                            endCodeChar.add(Character.toString((char) 0x14));
                            break;
                        case "% | none":
                            if (programnumber_ == null) {
                                endCodeChar.add("%");
                            }
                            break;
                        case "% | EOB":
                            if (programnumber_ == null) {
                                endCodeChar.add("%");
                            } else {
                                endCodeChar.add("");
                            }
                            break;
                        default:
                            break;
                    }

                    if (!endCodeChar.isEmpty()) {
                        endCodeChar.stream().forEach((code) -> {
                            interpreter_.serialOutput(code);
                        });
                    }

                    if (ngcBw != null) {
                        ngcBw.close();
                    }

                    if (debugBw != null) {
                        debugBw.close();
                    }

                    if (drawBw != null) {
                        setDxfFooter(drawBw);
                        drawBw.close();
                    }
                } catch (IOException | EmptyStackException | UnsupportedOperationException | ClassCastException | NullPointerException | IllegalArgumentException ex) {
                    Console.writeStackTrace(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), ex);
                }

                if (serial_ != null) {
                    do {
                        // sleep
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException ex) {
                        }

                        isWaitLoop = false;
                        if (serial_.isOwned()) {
                            if (serial_.size() > endCodeChar.size()) {
                                isWaitLoop = true;
                            }

                            if (isWaitLoop) {
                                if (serial_.isObserveDSR() && serial_.isObserveCTS()) {
                                    if (!serial_.isDataSetReady() || !serial_.isClearToSend()) {
                                        isWaitLoop = false;
                                    }
                                } else if (serial_.isObserveDSR()) {
                                    if (!serial_.isDataSetReady()) {
                                        isWaitLoop = false;
                                    }
                                } else if (serial_.isObserveCTS()) {
                                    if (!serial_.isClearToSend()) {
                                        isWaitLoop = false;
                                    }
                                }
                            } else if (serial_.isObserveDSR() && serial_.isObserveCTS()) {
                                if (serial_.isDataSetReady() && serial_.isClearToSend()) {
                                    isWaitLoop = true;
                                }
                            } else if (serial_.isObserveDSR()) {
                                if (serial_.isDataSetReady()) {
                                    isWaitLoop = true;
                                }
                            } else if (serial_.isObserveCTS()) {
                                if (serial_.isClearToSend()) {
                                    isWaitLoop = true;
                                }
                            }
                        }
                    } while (isWaitLoop);

                    serial_.clearFlow();
                    serial_.clear();
                }

                // check program
                switch (ret) {
                    case GcodeInterpreter.RET_NONE:
                        // abnormal end
                        for (GcodeVirtualMachinePluginListener listener : eventListenerList_.getListeners(GcodeVirtualMachinePluginListener.class)) {
                            listener.abnormalEndGcodeVirtualMachine();
                        }
                        break;
                    case GcodeInterpreter.RET_ERROR:
                        // error end
                        for (GcodeVirtualMachinePluginListener listener : eventListenerList_.getListeners(GcodeVirtualMachinePluginListener.class)) {
                            listener.errorEndGcodeVirtualMachine();
                        }
                        break;
                    case GcodeInterpreter.RET_END:
                        // normal end
                        for (GcodeVirtualMachinePluginListener listener : eventListenerList_.getListeners(GcodeVirtualMachinePluginListener.class)) {
                            listener.normalEndGcodeVirtualMachine();
                        }
                        break;
                    default:
                        break;
                }
                return null;
            }
        };
    }

    private void setDxfHeader(BufferedWriter drawBw) {
        try {
            // dxf header
            // section header
            drawBw.append("  0").append("\r\n");
            drawBw.append("SECTION").append("\r\n");
            // section header header
            drawBw.append("  2").append("\r\n");
            drawBw.append("HEADER").append("\r\n");
            // dimention text height
            drawBw.append("  9").append("\r\n");
            drawBw.append("$DIMTXT").append("\r\n");
            drawBw.append(" 40").append("\r\n");
            drawBw.append("5.0").append("\r\n");
            // section header footer
            drawBw.append("  0").append("\r\n");
            drawBw.append("ENDSEC").append("\r\n");

            // section header
            drawBw.append("  0").append("\r\n");
            drawBw.append("SECTION").append("\r\n");
            // section tables header
            drawBw.append("  2").append("\r\n");
            drawBw.append("TABLES").append("\r\n");
            // table header
            drawBw.append("  0").append("\r\n");
            drawBw.append("TABLE").append("\r\n");
            // table ltype header
            drawBw.append("  2").append("\r\n");
            drawBw.append("LTYPE").append("\r\n");
            // Continuous
            drawBw.append("  0").append("\r\n");
            drawBw.append("LTYPE").append("\r\n");
            drawBw.append("  2").append("\r\n");
            drawBw.append("Continuous").append("\r\n");
            drawBw.append(" 70").append("\r\n");
            drawBw.append("     0").append("\r\n");
            drawBw.append("  3").append("\r\n");
            drawBw.append("Solid line").append("\r\n");
            drawBw.append(" 72").append("\r\n");
            drawBw.append("    65").append("\r\n");
            drawBw.append(" 73").append("\r\n");
            drawBw.append("     0").append("\r\n");
            drawBw.append(" 40").append("\r\n");
            drawBw.append("0.0").append("\r\n");
            // HIDDEN2
            drawBw.append("  0").append("\r\n");
            drawBw.append("LTYPE").append("\r\n");
            drawBw.append("  2").append("\r\n");
            drawBw.append("HIDDEN2").append("\r\n");
            drawBw.append(" 70").append("\r\n");
            drawBw.append("     0").append("\r\n");
            drawBw.append("  3").append("\r\n");
            drawBw.append("Hidden (.5x) _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _").append("\r\n");
            drawBw.append(" 72").append("\r\n");
            drawBw.append("    65").append("\r\n");
            drawBw.append(" 73").append("\r\n");
            drawBw.append("     2").append("\r\n");
            drawBw.append(" 40").append("\r\n");
            drawBw.append("4.7625").append("\r\n");
            drawBw.append(" 49").append("\r\n");
            drawBw.append("3.175").append("\r\n");
            drawBw.append(" 49").append("\r\n");
            drawBw.append("-1.5875").append("\r\n");
            // table ltype footer
            drawBw.append("  0").append("\r\n");
            drawBw.append("ENDTAB").append("\r\n");
            // section tables footer
            drawBw.append("  0").append("\r\n");
            drawBw.append("ENDSEC").append("\r\n");

            // section header
            drawBw.append("  0").append("\r\n");
            drawBw.append("SECTION").append("\r\n");
            // section entities header
            drawBw.append("  2").append("\r\n");
            drawBw.append("ENTITIES").append("\r\n");
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), ex);
        }
    }

    private void setDxfFooter(BufferedWriter drawBw) {
        try {
            // section footer
            drawBw.append("  0").append("\r\n");
            drawBw.append("ENDSEC").append("\r\n");
            // eof
            drawBw.append("  0").append("\r\n");
            drawBw.append("EOF").append("\r\n");
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), ex);
        }
    }

    @Override
    public void addGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener) {
        if (listener instanceof GcodeVirtualMachinePluginListener) {
            boolean isListener = false;
            for (GcodeVirtualMachinePluginListener gvml : eventListenerList_.getListeners(GcodeVirtualMachinePluginListener.class)) {
                if (gvml == listener) {
                    isListener = true;
                    break;
                }
            }
            if (!isListener) {
                eventListenerList_.add(GcodeVirtualMachinePluginListener.class, (GcodeVirtualMachinePluginListener) listener);
            }
        }
    }

    @Override
    public void removeGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener) {
        if (listener instanceof GcodeVirtualMachinePluginListener) {
            eventListenerList_.remove(GcodeVirtualMachinePluginListener.class, (GcodeVirtualMachinePluginListener) listener);
        }
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(DesignEnums.GCODE_VIRTUAL_MACHINE.toString(), msg, err);
    }
}
