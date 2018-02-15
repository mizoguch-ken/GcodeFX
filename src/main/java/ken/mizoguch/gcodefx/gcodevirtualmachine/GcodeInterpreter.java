/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.gcodevirtualmachine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javax.swing.event.EventListenerList;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.Crypto;
import ken.mizoguch.gcodefx.DesignController;
import ken.mizoguch.gcodefx.DesignEnums;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeInterpreterPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeInterpreterPluginListener;
import ken.mizoguch.gcodefx.webeditor.WebEditor;
import ken.mizoguch.gcodefx.serial.Serial;
import ken.mizoguch.gcodeparser.GcodeParser;
import ken.mizoguch.gcodeparser.GcodeParserConstants;
import ken.mizoguch.ladders.Ladders;
import ken.mizoguch.webviewer.WebViewer;
import netscape.javascript.JSObject;

/**
 *
 * @author mizoguch-ken
 */
public class GcodeInterpreter implements GcodeInterpreterPlugin {

    private EventListenerList eventListenerList_;

    public enum STATE {
        NONE, FORE, BACK, FORE_BACK, OPTION, EXTERNAL, SCRIPT
    }

    public static enum OPT {
        TOOL_CHANGE, TOOL_CHANGE_XY, TOOL_CHANGE_M_CODE,
        SKIP_FUNCTION,
        OPTIMIZATION, EXCOMMENT, AT_VARIABLE, DISABLE_3003, DISABLE_3004, REPLACE_3006_M0, ONLY_S, RS274NGC,
        ORIGIN_MACHINE_X, ORIGIN_MACHINE_Y, ORIGIN_MACHINE_Z, ORIGIN_MACHINE_A, ORIGIN_MACHINE_B, ORIGIN_MACHINE_C, ORIGIN_MACHINE_U, ORIGIN_MACHINE_V, ORIGIN_MACHINE_W,
        COORDINATE_EXT_X, COORDINATE_EXT_Y, COORDINATE_EXT_Z, COORDINATE_EXT_A, COORDINATE_EXT_B, COORDINATE_EXT_C, COORDINATE_EXT_U, COORDINATE_EXT_V, COORDINATE_EXT_W,
        COORDINATE_G92_X, COORDINATE_G92_Y, COORDINATE_G92_Z, COORDINATE_G92_A, COORDINATE_G92_B, COORDINATE_G92_C, COORDINATE_G92_U, COORDINATE_G92_V, COORDINATE_G92_W,
        COORDINATE_TOOL, COORDINATE_MIRROR,
        COORDINATE_G54_X, COORDINATE_G54_Y, COORDINATE_G54_Z, COORDINATE_G54_A, COORDINATE_G54_B, COORDINATE_G54_C, COORDINATE_G54_U, COORDINATE_G54_V, COORDINATE_G54_W,
        COORDINATE_G55_X, COORDINATE_G55_Y, COORDINATE_G55_Z, COORDINATE_G55_A, COORDINATE_G55_B, COORDINATE_G55_C, COORDINATE_G55_U, COORDINATE_G55_V, COORDINATE_G55_W,
        COORDINATE_G56_X, COORDINATE_G56_Y, COORDINATE_G56_Z, COORDINATE_G56_A, COORDINATE_G56_B, COORDINATE_G56_C, COORDINATE_G56_U, COORDINATE_G56_V, COORDINATE_G56_W,
        COORDINATE_G57_X, COORDINATE_G57_Y, COORDINATE_G57_Z, COORDINATE_G57_A, COORDINATE_G57_B, COORDINATE_G57_C, COORDINATE_G57_U, COORDINATE_G57_V, COORDINATE_G57_W,
        COORDINATE_G58_X, COORDINATE_G58_Y, COORDINATE_G58_Z, COORDINATE_G58_A, COORDINATE_G58_B, COORDINATE_G58_C, COORDINATE_G58_U, COORDINATE_G58_V, COORDINATE_G58_W,
        COORDINATE_G59_X, COORDINATE_G59_Y, COORDINATE_G59_Z, COORDINATE_G59_A, COORDINATE_G59_B, COORDINATE_G59_C, COORDINATE_G59_U, COORDINATE_G59_V, COORDINATE_G59_W,
        DEBUG_JSON,
        PRINT,
        DISABLE_OUTPUT
    }

    public static final int RET_NONE = 0, RET_ERROR = -1, RET_END = -2;

    private enum FLG {
        EXIT, ALARM, STOP, GOTO, MIRROR, COORDINATE, AXIS_MOVE, SKIP, TOOL_CHANGE, SKIP_FUNCTION, CALL, EXTCALL, MCALL, MODAL, RETURN, CYCLE, RECALL, MODE, PARAMETER, DRAW
    }

    private final int ARG_PROGRAM = -1, ARG_LOOP = -2, ARG_CALL_JUMP = -3, ARG_A = 1, ARG_B = 2, ARG_C = 3, ARG_D = 7, ARG_E = 8, ARG_F = 9, ARG_G = 10, ARG_H = 11, ARG_I = 4, ARG_J = 5, ARG_K = 6, ARG_L = 12, ARG_M = 13, ARG_N = 14, ARG_O = 15, ARG_P = 16, ARG_Q = 17, ARG_R = 18, ARG_S = 19, ARG_T = 20, ARG_U = 21, ARG_V = 22, ARG_W = 23, ARG_X = 24, ARG_Y = 25, ARG_Z = 26;

    private GcodeInterpreter gcodeInterpreter_;
    private List<GcodeBytecode> bytecodesFore_;
    private List<GcodeBytecode> bytecodesBack_;
    private List<GcodeBytecode> bytecodesExternal_;
    private List<GcodeBytecode> bytecodesOption_;
    private List<GcodeBytecode> bytecodesScript_;
    private Map<String, Integer> programNumbersFore_;
    private Map<String, Integer> programNumbersBack_;
    private Map<String, Integer> programNumbersExternal_;
    private Map<Integer, GcodeVariable> global_;
    private Map<Integer, GcodeVariable> local_;
    private Map<String, Integer> name_;
    private Map<Double, Integer> ggroup_;
    private Map<String, String> programcallconvert_;
    private Map<Double, String> gcodeconvert_;
    private Map<Double, String> mcodeconvert_;
    private Map<Integer, String> tcodeconvert_;
    private Map<Integer, String> dcodeconvert_;
    private Map<Integer, String> hcodeconvert_;
    private Map<Double, String> gmacrocall_;
    private Map<Double, String> mmacrocall_;
    private Map<Integer, String> ladderAddress_;
    private Map<Integer, Integer> maxfeedrate_;
    private Map<Integer, Integer> maxrevolution_;
    private Map<Integer, Boolean> blockskip_;
    private Map<Integer, GcodeVariable> argmodal_;
    private Map<Integer, GcodeVariable> optimize_;
    private Map<Integer, GcodeVariable> draw_;
    private List<String> words_;
    private Map<Integer, Boolean> flg_;
    private String programnumber_;
    private STATE state_;
    private int locallevel_, line_, foreline_, calljump_;
    private Map<OPT, String> option_;
    private Path externalsubprogramdir_;

    private Ladders ladders_;
    private Map<Integer, Double> laddersIo_;
    private Map<Integer, Double> laddersIoImmediate_;
    private WebViewer webViewer_;
    private WebEditor webEditor_;
    private Serial serial_;
    private String serialEob_;
    private StringBuilder script_;
    private String stringpath_;
    private BufferedWriter ngcBufferedWriter_, debugBufferedWriter_, drawBufferedWriter_;

    private final Map<Integer, Map<String, String>> variables_ = new HashMap<>();
    private static final DecimalFormat DECIMAL_FORMAT_VAL = new DecimalFormat("0.0###");
    private static final DecimalFormat DECIMAL_FORMAT_CODE = new DecimalFormat("0.#");
    private static final DecimalFormat DECIMAL_FORMAT_PRINT = new DecimalFormat();
    private static final Pattern PATTERN_EXCOMMENT = Pattern.compile("([^\\$]|\\$(?!\\d)+)|\\$(\\d+)");
    private static final Pattern PATTERN_NUMBER = Pattern.compile("^\\-?[0-9]*\\.?[0-9]+$");
    private static final Pattern PATTERN_UINT = Pattern.compile("^[0-9]+$");

    /**
     *
     */
    public GcodeInterpreter() {
        eventListenerList_ = null;
        gcodeInterpreter_ = null;
        bytecodesFore_ = null;
        bytecodesBack_ = null;
        bytecodesOption_ = null;
        bytecodesExternal_ = null;
        bytecodesScript_ = null;
        programNumbersFore_ = null;
        programNumbersBack_ = null;
        global_ = null;
        local_ = null;
        name_ = null;
        ggroup_ = null;
        programcallconvert_ = null;
        gcodeconvert_ = null;
        mcodeconvert_ = null;
        tcodeconvert_ = null;
        dcodeconvert_ = null;
        hcodeconvert_ = null;
        gmacrocall_ = null;
        mmacrocall_ = null;
        ladderAddress_ = null;
        maxfeedrate_ = null;
        maxrevolution_ = null;
        blockskip_ = null;
        argmodal_ = null;
        optimize_ = null;
        draw_ = null;
        words_ = null;
        flg_ = null;
        state_ = STATE.NONE;
        programnumber_ = null;
        locallevel_ = -1;
        line_ = -1;
        foreline_ = -1;
        calljump_ = -1;
        option_ = null;
        externalsubprogramdir_ = null;

        ladders_ = null;
        webViewer_ = null;
        webEditor_ = null;
        serial_ = null;
        serialEob_ = null;
        script_ = null;
        stringpath_ = null;
        ngcBufferedWriter_ = null;
        debugBufferedWriter_ = null;
        drawBufferedWriter_ = null;
    }

    /**
     *
     * @param interpreter
     */
    public void inheritanceGcodeInterpreter(GcodeInterpreter interpreter) {
        setEventListenerList(interpreter.getEventListenerList());
        setBytecodesFore(interpreter.getBytecodesFore());
        setBytecodesBack(interpreter.getBytecodesBack());
        setBytecodesExternal(interpreter.getBytecodesExternal());
        setProgramNumbersFore(interpreter.getProgramNumbersFore());
        setProgramNumbersBack(interpreter.getProgramNumbersBack());
        setProgramNumbersExternal(interpreter.getProgramNumbersExternal());
        setGlobal(interpreter.getGlobal());
        setName(interpreter.getName());
        setGGroup(interpreter.getGGroup());
        setProgramCallConvert(interpreter.getProgramCallConvert());
        setGCodeConvert(interpreter.getGCodeConvert());
        setMCodeConvert(interpreter.getMCodeConvert());
        setTCodeConvert(interpreter.getTCodeConvert());
        setDCodeConvert(interpreter.getDCodeConvert());
        setHCodeConvert(interpreter.getHCodeConvert());
        setGMacroCall(interpreter.getGMacroCall());
        setMMacroCall(interpreter.getMMacroCall());
        setLadderAddress(interpreter.getLadderAddress());
        setMaxFeedRate(interpreter.getMaxFeedRate());
        setMaxRevolution(interpreter.getMaxRevolution());
        setBlockSkip(interpreter.getBlockSkip());
        setOptimize(interpreter.getOptimize());
        setDraw(interpreter.getDraw());
        setWords(interpreter.getWords());
        setFlg(interpreter.getFlg());
        setForeLine(interpreter.getForeLine());
        setOptions(interpreter.getOptions());
        setExternalSubProgramDirectory(interpreter.getExternalSubProgramDirectory());
        setLadders(interpreter.getLadders());
        setLaddersIo(interpreter.getLaddersIo());
        setLaddersIoImmediate(interpreter.getLaddersIoImmediatet());
        setWebViewer(interpreter.getWebViewer());
        setWebEditor(interpreter.getWebEditor());
        setSerial(interpreter.getSerial());
        setSerialEOB(interpreter.getSerialEOB());
        setNgcBufferedWriter(interpreter.getNgcBufferedWriter());
        setDebugBufferedWriter(interpreter.getDebugBufferedWriter());
        setDrawBufferedWriter(interpreter.getDrawBufferedWriter());
    }

    /**
     *
     * @return
     */
    public GcodeInterpreter getLastGcodeInterpreter() {
        if (gcodeInterpreter_ == null) {
            return this;
        } else {
            return gcodeInterpreter_.getLastGcodeInterpreter();
        }
    }

    /**
     *
     * @return
     */
    public EventListenerList getEventListenerList() {
        return eventListenerList_;
    }

    /**
     *
     * @param list
     */
    public void setEventListenerList(EventListenerList list) {
        if (list == null) {
            eventListenerList_ = new EventListenerList();
        } else {
            eventListenerList_ = list;
        }
    }

    /**
     *
     * @return
     */
    public List<GcodeBytecode> getBytecodesFore() {
        return bytecodesFore_;
    }

    /**
     *
     * @param bytecodes
     */
    public void setBytecodesFore(List<GcodeBytecode> bytecodes) {
        if (bytecodes == null) {
            bytecodesFore_ = new ArrayList<>();
        } else {
            bytecodesFore_ = bytecodes;
        }
    }

    /**
     *
     * @return
     */
    public List<GcodeBytecode> getBytecodesBack() {
        return bytecodesBack_;
    }

    /**
     *
     * @param bytecodes
     */
    public void setBytecodesBack(List<GcodeBytecode> bytecodes) {
        if (bytecodes == null) {
            bytecodesBack_ = new ArrayList<>();
        } else {
            bytecodesBack_ = bytecodes;
        }
    }

    /**
     *
     * @return
     */
    public List<GcodeBytecode> getBytecodesExternal() {
        return bytecodesExternal_;
    }

    /**
     *
     * @param bytecodes
     */
    public void setBytecodesExternal(List<GcodeBytecode> bytecodes) {
        if (bytecodes == null) {
            bytecodesExternal_ = new ArrayList<>();
        } else {
            bytecodesExternal_ = bytecodes;
        }
    }

    /**
     *
     * @return
     */
    public List<GcodeBytecode> getBytecodesOption() {
        return bytecodesOption_;
    }

    /**
     *
     * @param bytecodes
     */
    public void setBytecodesOption(List<GcodeBytecode> bytecodes) {
        if (bytecodes == null) {
            bytecodesOption_ = new ArrayList<>();
        } else {
            bytecodesOption_ = bytecodes;
        }
    }

    /**
     *
     * @return
     */
    public List<GcodeBytecode> getBytecodesScript() {
        return bytecodesScript_;
    }

    /**
     *
     * @param bytecodes
     */
    public void setBytecodesScript(List<GcodeBytecode> bytecodes) {
        if (bytecodes == null) {
            bytecodesScript_ = new ArrayList<>();
        } else {
            bytecodesScript_ = bytecodes;
        }
    }

    /**
     *
     * @param bytecodes
     */
    public void cpyBytecodesScript(List<GcodeBytecode> bytecodes) {
        bytecodesScript_ = new ArrayList<>();
        bytecodesScript_.addAll(bytecodes);
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getProgramNumbersFore() {
        return programNumbersFore_;
    }

    /**
     *
     * @param programnumbers
     */
    public void setProgramNumbersFore(Map<String, Integer> programnumbers) {
        if (programnumbers == null) {
            programNumbersFore_ = new HashMap<>();
        } else {
            programNumbersFore_ = programnumbers;
        }
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getProgramNumbersBack() {
        return programNumbersBack_;
    }

    /**
     *
     * @param programnumbers
     */
    public void setProgramNumbersBack(Map<String, Integer> programnumbers) {
        if (programnumbers == null) {
            programNumbersBack_ = new HashMap<>();
        } else {
            programNumbersBack_ = programnumbers;
        }
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getProgramNumbersExternal() {
        return programNumbersExternal_;
    }

    /**
     *
     * @param programnumbers
     */
    public void setProgramNumbersExternal(Map<String, Integer> programnumbers) {
        if (programnumbers == null) {
            programNumbersExternal_ = new HashMap<>();
        } else {
            programNumbersExternal_ = programnumbers;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, GcodeVariable> getGlobal() {
        return global_;
    }

    /**
     *
     * @param global
     */
    public void setGlobal(Map<Integer, GcodeVariable> global) {
        if (global == null) {
            global_ = new HashMap<>();
        } else {
            global_ = global;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, GcodeVariable> getLocal() {
        return local_;
    }

    /**
     *
     * @param local
     */
    public void setLocal(Map<Integer, GcodeVariable> local) {
        if (local == null) {
            local_ = new HashMap<>();
        } else {
            local_ = local;
        }
    }

    /**
     *
     * @param local
     */
    public void cpyLocal(Map<Integer, GcodeVariable> local) {
        local_ = new HashMap<>();
        local_.putAll(local);
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> getName() {
        return name_;
    }

    /**
     *
     * @param name
     */
    public void setName(Map<String, Integer> name) {
        if (name == null) {
            name_ = new HashMap<>();
        } else {
            name_ = name;
        }
    }

    /**
     *
     * @return
     */
    public Map<Double, Integer> getGGroup() {
        return ggroup_;
    }

    /**
     *
     * @param ggroup
     */
    public void setGGroup(Map<Double, Integer> ggroup) {
        ggroup_ = ggroup;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getProgramCallConvert() {
        return programcallconvert_;
    }

    /**
     *
     * @param programcallconvert
     */
    public void setProgramCallConvert(Map<String, String> programcallconvert) {
        programcallconvert_ = programcallconvert;
    }

    /**
     *
     * @return
     */
    public Map<Double, String> getGCodeConvert() {
        return gcodeconvert_;
    }

    /**
     *
     * @param gcodeconvert
     */
    public void setGCodeConvert(Map<Double, String> gcodeconvert) {
        gcodeconvert_ = gcodeconvert;
    }

    /**
     *
     * @return
     */
    public Map<Double, String> getMCodeConvert() {
        return mcodeconvert_;
    }

    /**
     *
     * @param mcodeconvert
     */
    public void setMCodeConvert(Map<Double, String> mcodeconvert) {
        mcodeconvert_ = mcodeconvert;
    }

    /**
     *
     * @return
     */
    public Map<Integer, String> getTCodeConvert() {
        return tcodeconvert_;
    }

    /**
     *
     * @param tcodeconvert
     */
    public void setTCodeConvert(Map<Integer, String> tcodeconvert) {
        tcodeconvert_ = tcodeconvert;
    }

    /**
     *
     * @return
     */
    public Map<Integer, String> getDCodeConvert() {
        return dcodeconvert_;
    }

    /**
     *
     * @param dcodeconvert
     */
    public void setDCodeConvert(Map<Integer, String> dcodeconvert) {
        dcodeconvert_ = dcodeconvert;
    }

    /**
     *
     * @return
     */
    public Map<Integer, String> getHCodeConvert() {
        return hcodeconvert_;
    }

    /**
     *
     * @param hcodeconvert
     */
    public void setHCodeConvert(Map<Integer, String> hcodeconvert) {
        hcodeconvert_ = hcodeconvert;
    }

    /**
     *
     * @return
     */
    public Map<Double, String> getGMacroCall() {
        return gmacrocall_;
    }

    /**
     *
     * @param gmacrocall
     */
    public void setGMacroCall(Map<Double, String> gmacrocall) {
        gmacrocall_ = gmacrocall;
    }

    /**
     *
     * @return
     */
    public Map<Double, String> getMMacroCall() {
        return mmacrocall_;
    }

    /**
     *
     * @param mmacrocall
     */
    public void setMMacroCall(Map<Double, String> mmacrocall) {
        mmacrocall_ = mmacrocall;
    }

    /**
     *
     * @return
     */
    public Map<Integer, String> getLadderAddress() {
        return ladderAddress_;
    }

    /**
     *
     * @param ladderAddress
     */
    public void setLadderAddress(Map<Integer, String> ladderAddress) {
        ladderAddress_ = ladderAddress;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Integer> getMaxFeedRate() {
        return maxfeedrate_;
    }

    /**
     *
     * @param maxfeedrate
     */
    public void setMaxFeedRate(Map<Integer, Integer> maxfeedrate) {
        maxfeedrate_ = maxfeedrate;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Integer> getMaxRevolution() {
        return maxrevolution_;
    }

    /**
     *
     * @param maxrevolution
     */
    public void setMaxRevolution(Map<Integer, Integer> maxrevolution) {
        maxrevolution_ = maxrevolution;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Boolean> getBlockSkip() {
        return blockskip_;
    }

    /**
     *
     * @param blockskip
     */
    public void setBlockSkip(Map<Integer, Boolean> blockskip) {
        blockskip_ = blockskip;
    }

    /**
     *
     * @return
     */
    public Map<Integer, GcodeVariable> getArgModal() {
        return argmodal_;
    }

    /**
     *
     * @param arg
     */
    public void setArgModal(Map<Integer, GcodeVariable> arg) {
        if (arg == null) {
            argmodal_ = new HashMap<>();
        } else {
            argmodal_ = arg;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, GcodeVariable> getOptimize() {
        return optimize_;
    }

    /**
     *
     * @param optimize
     */
    public void setOptimize(Map<Integer, GcodeVariable> optimize) {
        if (optimize == null) {
            optimize_ = new HashMap<>();
        } else {
            optimize_ = optimize;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, GcodeVariable> getDraw() {
        return draw_;
    }

    /**
     *
     * @param draw
     */
    public void setDraw(Map<Integer, GcodeVariable> draw) {
        if (draw == null) {
            draw_ = new HashMap<>();
            setGcodeVariable(draw_, 5021, getGcodeVariable(global_, 5021));
            setGcodeVariable(draw_, 5022, getGcodeVariable(global_, 5022));
            setGcodeVariable(draw_, 5023, getGcodeVariable(global_, 5023));
        } else {
            draw_ = draw;
        }
    }

    /**
     *
     * @return
     */
    public List<String> getWords() {
        return words_;
    }

    /**
     *
     * @param words
     */
    public void setWords(List<String> words) {
        if (words == null) {
            words_ = new ArrayList<>();
        } else {
            words_ = words;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, Boolean> getFlg() {
        return flg_;
    }

    /**
     *
     * @param flg
     */
    public void setFlg(Map<Integer, Boolean> flg) {
        if (flg == null) {
            flg_ = new HashMap<>();
        } else {
            flg_ = flg;
        }
    }

    /**
     *
     * @return
     */
    public STATE getState() {
        return state_;
    }

    /**
     *
     * @param state
     */
    public void setState(STATE state) {
        state_ = state;
    }

    /**
     *
     * @return
     */
    public String getProgramNumber() {
        return programnumber_;
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
     * @return
     */
    public int getForeLine() {
        return foreline_;
    }

    /**
     *
     * @param line
     */
    public void setForeLine(int line) {
        foreline_ = line;
    }

    /**
     *
     * @return
     */
    public int getLocalLevel() {
        return locallevel_;
    }

    /**
     *
     * @param locallevel
     */
    public void setLocalLevel(int locallevel) {
        locallevel_ = locallevel;
    }

    /**
     *
     * @return
     */
    public int getCallJump() {
        return calljump_;
    }

    /**
     *
     * @param calljump
     */
    public void setCallJump(int calljump) {
        calljump_ = calljump;
    }

    /**
     *
     * @return
     */
    public Map<OPT, String> getOptions() {
        return option_;
    }

    /**
     *
     * @param opt
     */
    public void setOptions(Map<OPT, String> opt) {
        if (opt == null) {
            option_ = new HashMap<>();
        } else {
            option_ = opt;
        }
    }

    /**
     *
     * @return
     */
    public Path getExternalSubProgramDirectory() {
        return externalsubprogramdir_;
    }

    /**
     *
     * @param externalsubprogramdir
     */
    public void setExternalSubProgramDirectory(Path externalsubprogramdir) {
        if (externalsubprogramdir == null) {
            externalsubprogramdir_ = Paths.get(".").toAbsolutePath();
        } else {
            externalsubprogramdir_ = externalsubprogramdir;
        }
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
     * @param ladders
     */
    public void setLadders(Ladders ladders) {
        ladders_ = ladders;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Double> getLaddersIo() {
        return laddersIo_;
    }

    /**
     *
     * @param laddersIo
     */
    public void setLaddersIo(Map<Integer, Double> laddersIo) {
        if (laddersIo == null) {
            laddersIo_ = new HashMap<>();
        } else {
            laddersIo_ = laddersIo;
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, Double> getLaddersIoImmediatet() {
        return laddersIoImmediate_;
    }

    /**
     *
     * @param laddersIo
     */
    public void setLaddersIoImmediate(Map<Integer, Double> laddersIo) {
        if (laddersIo == null) {
            laddersIoImmediate_ = new HashMap<>();
        } else {
            laddersIoImmediate_ = laddersIo;
        }
    }

    /**
     *
     * @return
     */
    public WebViewer getWebViewer() {
        return webViewer_;
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
     * @return
     */
    public WebEditor getWebEditor() {
        return webEditor_;
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
     * @return
     */
    public Serial getSerial() {
        return serial_;
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
     * @return
     */
    public String getSerialEOB() {
        return serialEob_;
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
     * @return
     */
    public StringBuilder getScript() {
        return script_;
    }

    /**
     *
     * @param script
     */
    public void setScript(StringBuilder script) {
        if (script == null) {
            script_ = new StringBuilder();
        } else {
            script_ = script;
        }
    }

    /**
     *
     * @return
     */
    public BufferedWriter getNgcBufferedWriter() {
        return ngcBufferedWriter_;
    }

    /**
     *
     * @param bufferedWriter
     */
    public void setNgcBufferedWriter(BufferedWriter bufferedWriter) {
        ngcBufferedWriter_ = bufferedWriter;
    }

    /**
     *
     * @return
     */
    public BufferedWriter getDebugBufferedWriter() {
        return debugBufferedWriter_;
    }

    /**
     *
     * @param bufferedWriter
     */
    public void setDebugBufferedWriter(BufferedWriter bufferedWriter) {
        debugBufferedWriter_ = bufferedWriter;
    }

    /**
     *
     * @return
     */
    public BufferedWriter getDrawBufferedWriter() {
        return drawBufferedWriter_;
    }

    /**
     *
     * @param bufferedWriter
     */
    public void setDrawBufferedWriter(BufferedWriter bufferedWriter) {
        drawBufferedWriter_ = bufferedWriter;
    }

    private int gotoJump(List<GcodeBytecode> bytecodes, Map<String, Integer> programNumbers, int pc, int sequenceNumber) {
        GcodeBytecode gb;
        boolean skip = false;
        int _pc;

        for (_pc = pc + 1; _pc != pc; _pc++) {
            if (_pc >= bytecodes.size()) {
                if (programNumbers == null) {
                    _pc = 0;
                } else {
                    _pc = programNumbers.get(programnumber_) + 1;
                }
            }

            gb = bytecodes.get(_pc);
            switch (gb.getOpCode()) {
                case GcodeParserConstants.COMMENT:
                case GcodeParserConstants.LINECOMMENT:
                    break;
                case GcodeParserConstants.N:
                    if (!skip) {
                        if (gb.getIntValue() == sequenceNumber) {
                            return getBlockTopPc(bytecodes, _pc);
                        }
                    }
                    break;
                case GcodeParserConstants.O:
                case GcodeParserConstants.FILENAME:
                    if (!skip) {
                        if (programNumbers == null) {
                            _pc = -1;
                        } else {
                            _pc = programNumbers.get(programnumber_);
                        }
                    }
                    break;
                case GcodeParserConstants.EOB:
                    skip = false;
                    break;
                case GcodeParserConstants.BLOCK_SKIP:
                    if (blockskip_.containsKey(gb.getIntValue())) {
                        if (blockskip_.get(gb.getIntValue())) {
                            skip = true;
                        }
                    }
                    break;
                default:
                    skip = true;
                    break;
            }
        }
        return RET_ERROR;
    }

    private int ifJump(List<GcodeBytecode> bytecodes, int pc) {
        return getNextBlockTopPc(bytecodes, pc + 1);
    }

    private int whileJump(List<GcodeBytecode> bytecodes, int pc, int endNumber) {
        GcodeBytecode gb;
        boolean skip = false;
        int _pc;

        for (_pc = pc + 1; _pc < bytecodes.size(); _pc++) {
            gb = bytecodes.get(_pc);
            switch (gb.getOpCode()) {
                case GcodeParserConstants.COMMENT:
                case GcodeParserConstants.LINECOMMENT:
                case GcodeParserConstants.N:
                    break;
                case GcodeParserConstants.END:
                    if (!skip) {
                        if (gb.getIntValue() == endNumber) {
                            return getNextBlockTopPc(bytecodes, _pc);
                        }
                    }
                    break;
                case GcodeParserConstants.O:
                case GcodeParserConstants.FILENAME:
                    if (!skip) {
                        return RET_ERROR;
                    }
                    break;
                case GcodeParserConstants.EOB:
                    skip = false;
                    break;
                case GcodeParserConstants.BLOCK_SKIP:
                    if (blockskip_.containsKey(gb.getIntValue())) {
                        if (blockskip_.get(gb.getIntValue())) {
                            skip = true;
                        }
                    }
                    break;
                default:
                    skip = true;
                    break;
            }
        }
        return RET_ERROR;
    }

    private int endJump(List<GcodeBytecode> bytecodes, int pc, int doNumber) {
        GcodeBytecode gb;
        boolean def;
        int _pc;

        for (_pc = pc - 1; _pc > -1; _pc--) {
            pc = getBlockTopPc(bytecodes, _pc);
            _pc = pc;
            def = false;
            for (; pc < bytecodes.size(); pc++) {
                gb = bytecodes.get(pc);
                switch (gb.getOpCode()) {
                    case GcodeParserConstants.COMMENT:
                    case GcodeParserConstants.LINECOMMENT:
                    case GcodeParserConstants.N:
                        break;
                    case GcodeParserConstants.WHILE:
                    case GcodeParserConstants.DO:
                        if (gb.getIntValue() == doNumber) {
                            return _pc;
                        }
                        break;
                    case GcodeParserConstants.O:
                    case GcodeParserConstants.FILENAME:
                        if (!def) {
                            return RET_ERROR;
                        }
                        break;
                    case GcodeParserConstants.EOB:
                        pc = bytecodes.size();
                        break;
                    case GcodeParserConstants.BLOCK_SKIP:
                        if (blockskip_.containsKey(gb.getIntValue())) {
                            if (blockskip_.get(gb.getIntValue())) {
                                pc = bytecodes.size();
                            }
                        }
                        break;
                    default:
                        def = true;
                        break;
                }
            }
        }

        return RET_ERROR;
    }

    private int getBlockTopPc(List<GcodeBytecode> bytecodes, int pc) {
        GcodeBytecode gb;
        int _pc;

        for (_pc = pc - 1; _pc > -1; _pc--) {
            gb = bytecodes.get(_pc);
            switch (gb.getOpCode()) {
                case GcodeParserConstants.EOB:
                    return (_pc + 1);
            }
        }
        return 0;
    }

    private int getNextBlockTopPc(List<GcodeBytecode> bytecodes, int pc) {
        GcodeBytecode gb;
        int _pc;

        for (_pc = pc; _pc < bytecodes.size(); _pc++) {
            gb = bytecodes.get(_pc);
            switch (gb.getOpCode()) {
                case GcodeParserConstants.EOB:
                    return (_pc + 1);
            }
        }
        return RET_ERROR;
    }

    /**
     *
     * @param variables
     */
    public void notifyVariables(Map<Integer, Map<String, String>> variables) {
        for (GcodeInterpreterPluginListener listener : eventListenerList_.getListeners(GcodeInterpreterPluginListener.class)) {
            listener.variablesGcodeInterpreter(variables);
        }
    }

    /**
     *
     * @param message
     */
    public void notifyExComment(String message) {
        for (GcodeInterpreterPluginListener listener : eventListenerList_.getListeners(GcodeInterpreterPluginListener.class)) {
            listener.exCommentGcodeInterpreter(message);
        }
    }

    /**
     *
     */
    public void notifyGlobalVariables() {
        GcodeVariable gv;
        int i;

        for (i = 100; i <= 199; i++) {
            Map<String, String> var = new HashMap<>();

            gv = getGcodeVariable(global_, i);
            var.put("nest", "-1");
            if (!gv.isNil()) {
                var.put("value", DECIMAL_FORMAT_VAL.format(gv.getDouble()));
            }
            var.put("name", gv.getName());
            variables_.put(i, var);
        }

        for (i = 500; i <= 999; i++) {
            Map<String, String> var = new HashMap<>();

            gv = getGcodeVariable(global_, i);
            var.put("nest", "-1");
            if (!gv.isNil()) {
                var.put("value", DECIMAL_FORMAT_VAL.format(gv.getDouble()));
            }
            var.put("name", gv.getName());
            variables_.put(i, var);
        }
    }

    /**
     *
     */
    public void notifyLocalVariables() {
        GcodeVariable gv;
        int i;

        for (i = 1; i <= 33; i++) {
            Map<String, String> var = new HashMap<>();

            gv = getGcodeVariable(local_, i);
            var.put("nest", DECIMAL_FORMAT_CODE.format(locallevel_));
            if (!gv.isNil()) {
                var.put("value", DECIMAL_FORMAT_VAL.format(gv.getDouble()));
            }
            var.put("name", gv.getName());
            variables_.put(i, var);
        }
    }

    /**
     *
     * @param address
     * @return
     */
    public GcodeVariable getVariable(int address) {
        if (address < 100) {
            return getGcodeVariable(local_, address);
        } else {
            return getGcodeVariable(global_, address);
        }
    }

    /**
     *
     * @param address
     * @param gv
     */
    public void setVariable(int address, GcodeVariable gv) {
        Map<String, String> var = new HashMap<>();

        if (address < 100) {
            setGcodeVariable(local_, address, gv);
            var.put("nest", DECIMAL_FORMAT_CODE.format(locallevel_));
        } else {
            setGcodeVariable(global_, address, gv);
            var.put("nest", "-1");
        }
        if (!gv.isNil()) {
            var.put("value", DECIMAL_FORMAT_VAL.format(gv.getDouble()));
        }
        var.put("name", gv.getName());

        variables_.put(address, var);
    }

    /**
     *
     * @param address
     * @param value
     * @param string
     * @param name
     */
    public void setVariable(int address, Double value, String string, String name) {
        Map<String, String> var = new HashMap<>();

        if (address < 100) {
            setGcodeVariable(local_, address, value, string, name);
            var.put("nest", DECIMAL_FORMAT_CODE.format(locallevel_));
        } else {
            setGcodeVariable(global_, address, value, string, name);
            var.put("nest", "-1");
        }
        if (value != null) {
            var.put("value", DECIMAL_FORMAT_VAL.format(value));
        }
        var.put("name", name);

        variables_.put(address, var);
    }

    /**
     *
     * @param arg
     * @param address
     * @return
     */
    public GcodeVariable getGcodeVariable(Map<Integer, GcodeVariable> arg, int address) {
        if (!arg.containsKey(address)) {
            arg.put(address, new GcodeVariable(address));
        }
        return arg.get(address);
    }

    /**
     *
     * @param arg
     * @param address
     * @param gv
     */
    public void setGcodeVariable(Map<Integer, GcodeVariable> arg, int address, GcodeVariable gv) {
        if (arg.containsKey(address)) {
            arg.get(address).copy(gv);
        } else {
            arg.put(address, new GcodeVariable(gv));
        }
    }

    /**
     *
     * @param arg
     * @param address
     * @param value
     * @param string
     * @param name
     */
    public void setGcodeVariable(Map<Integer, GcodeVariable> arg, int address, Double value, String string, String name) {
        if (name != null) {
            name_.put(name, address);
        }
        if (arg.containsKey(address)) {
            arg.get(address).setDouble(value);
        } else {
            arg.put(address, new GcodeVariable(value, address, string, name));
        }
    }

    /**
     *
     * @return
     */
    public int exec() {
        List<GcodeBytecode> bytecodes;
        Map<String, Integer> programNumbers;
        ConcurrentLinkedDeque<GcodeVariable> stack = new ConcurrentLinkedDeque<>();
        Map<Integer, GcodeVariable> arglocal = new HashMap<>();
        Map<Integer, GcodeVariable> argcycle = new HashMap<>();
        GcodeVariable[] gv = new GcodeVariable[2];
        GcodeBytecode gb;
        List<String> words;
        Map<FLG, Boolean> flg = new HashMap<>();
        Map.Entry<Integer, Double> entryIo;
        Double ladderIo;
        boolean isExit = false, checkLadder, checkSerial;
        StringBuilder out = new StringBuilder();
        StringBuilder subout = new StringBuilder();
        StringBuilder strPrint = new StringBuilder();
        String[] sv;
        String strval;
        double dblval, dv;
        int ret, lp, calljump, retval = RET_NONE, intval, pc, gotopc = 0, iv, ic = 0, jc = 0, kc = 0;
        Object objval;

        switch (state_) {
            case FORE_BACK:
                // fore or back program
                if (programnumber_ != null) {
                    if (programNumbersFore_.containsKey(programnumber_)) {
                        pc = programNumbersFore_.get(programnumber_);
                        bytecodes = bytecodesFore_;
                        programNumbers = programNumbersFore_;
                        state_ = STATE.FORE;
                    } else if (programNumbersBack_.containsKey(programnumber_)) {
                        pc = programNumbersBack_.get(programnumber_);
                        bytecodes = bytecodesBack_;
                        programNumbers = programNumbersBack_;
                        state_ = STATE.BACK;
                    } else {
                        writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString() + "[" + programnumber_ + "]", true);
                        return RET_ERROR;
                    }
                } else {
                    writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString(), true);
                    return RET_ERROR;
                }
                // call jump
                if (calljump_ > 0) {
                    pc = gotoJump(bytecodes, programNumbers, pc + 1, calljump_);
                    if (pc == RET_ERROR) {
                        writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + calljump_ + "]", true);
                        return RET_ERROR;
                    }
                }
                break;
            case EXTERNAL:
                // external program
                if (programnumber_ != null) {
                    if (programNumbersExternal_.containsKey(programnumber_)) {
                        pc = programNumbersExternal_.get(programnumber_);
                        bytecodes = bytecodesExternal_;
                        programNumbers = programNumbersExternal_;
                    } else {
                        writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString() + "[" + programnumber_ + "]", true);
                        return RET_ERROR;
                    }
                } else {
                    writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString(), true);
                    return RET_ERROR;
                }
                // call jump
                if (calljump_ > 0) {
                    pc = gotoJump(bytecodes, programNumbers, pc + 1, calljump_);
                    if (pc == RET_ERROR) {
                        writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + calljump_ + "]", true);
                        return RET_ERROR;
                    }
                }
                break;
            case SCRIPT:
                // script program
                if (bytecodesScript_.size() > 0) {
                    pc = 0;
                    bytecodes = bytecodesScript_;
                    programNumbers = null;
                } else {
                    writeLog(GcodeInterpreterEnums.NO_BYTECODE.toString(), true);
                    return RET_ERROR;
                }
                break;
            case OPTION:
                // option program
                if (bytecodesOption_.size() > 0) {
                    pc = 0;
                    bytecodes = bytecodesOption_;
                    programNumbers = null;
                } else {
                    writeLog(GcodeInterpreterEnums.NO_BYTECODE.toString(), true);
                    return RET_ERROR;
                }
                break;
            default:
                return RET_ERROR;
        }

        // flag clear
        words = new ArrayList<>();
        for (FLG enums : FLG.values()) {
            flg.put(enums, false);
        }

        // showing local variable update
        notifyLocalVariables();

        while (!isExit) {
            // ladder & serial
            if (serial_ == null) {
                // ladder check
                checkLadder = !laddersIoImmediate_.isEmpty();

                if (checkLadder) {
                    laddersIoImmediate_.entrySet().forEach((entry) -> {
                        ladders_.setValueDirect(ladderAddress_.get(entry.getKey()), entry.getValue());
                    });

                    while (checkLadder) {
                        if (!ladders_.isCycling()) {
                            writeLog(GcodeInterpreterEnums.LADDER_NOT_RUNNING.toString(), true);
                            return RET_ERROR;
                        }

                        for (Iterator<Map.Entry<Integer, Double>> iterator = laddersIoImmediate_.entrySet().iterator(); iterator.hasNext();) {
                            entryIo = iterator.next();
                            ladderIo = ladders_.getValue(ladderAddress_.get(entryIo.getKey() + 1));
                            if (ladderIo != null) {
                                if (ladderIo.equals(entryIo.getValue())) {
                                    iterator.remove();
                                }
                            }
                        }
                        checkLadder = !laddersIoImmediate_.isEmpty();
                    }
                }
            } else {
                // ladder check & serial buffer check 
                checkLadder = !laddersIoImmediate_.isEmpty();
                checkSerial = serial_.isObserveDSR() || serial_.isObserveCTS() || serial_.isBufferFlow();

                if (checkLadder) {
                    laddersIoImmediate_.entrySet().forEach((entry) -> {
                        ladders_.setValueDirect(ladderAddress_.get(entry.getKey()), entry.getValue());
                    });
                }

                while (checkLadder || checkSerial) {
                    if (checkLadder) {
                        if (!ladders_.isCycling()) {
                            writeLog(GcodeInterpreterEnums.LADDER_NOT_RUNNING.toString(), true);
                            return RET_ERROR;
                        }

                        for (Iterator<Map.Entry<Integer, Double>> iterator = laddersIoImmediate_.entrySet().iterator(); iterator.hasNext();) {
                            entryIo = iterator.next();
                            ladderIo = ladders_.getValue(ladderAddress_.get(entryIo.getKey() + 1));
                            if (ladderIo != null) {
                                if (ladderIo.equals(entryIo.getValue())) {
                                    iterator.remove();
                                }
                            }
                        }
                        checkLadder = !laddersIoImmediate_.isEmpty();
                    }

                    if (checkSerial) {
                        if (!serial_.isOwned()) {
                            writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                            return RET_ERROR;
                        } else if (serial_.isObserveDSR() && serial_.isObserveCTS()) {
                            if (!serial_.isDataSetReady() || !serial_.isClearToSend()) {
                                writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                return RET_ERROR;
                            }
                        } else if (serial_.isObserveDSR()) {
                            if (!serial_.isDataSetReady()) {
                                writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                return RET_ERROR;
                            }
                        } else if (serial_.isObserveCTS()) {
                            if (!serial_.isClearToSend()) {
                                writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                return RET_ERROR;
                            }
                        }
                        checkSerial = serial_.isBufferFlow();
                    } else {
                        checkSerial = false;
                    }

                    if (checkSerial) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }

            if (pc >= bytecodes.size()) {
                switch (state_) {
                    case OPTION:
                        // option program
                        switch (retval) {
                            case RET_NONE:
                            case RET_END:
                                return RET_END;
                        }
                        return RET_ERROR;
                    case SCRIPT:
                        // script program
                        return retval;
                    default:
                        writeLog(GcodeInterpreterEnums.ABNORMAL_PROGRAM_COUNTER.toString(), true);
                        return RET_ERROR;
                }
            }

            gb = bytecodes.get(pc);
            switch (gb.getOpCode()) {
                case GcodeParserConstants.COMMENT:
                case GcodeParserConstants.LINECOMMENT:
                    if (!flg.get(FLG.SKIP)) {
                        if (flg.get(FLG.ALARM) || flg.get(FLG.STOP)) {
                            strval = gb.getStrValue();
                            if (strval == null) {
                                strval = "";
                            }
                            words.add("(" + strval + ")");
                        } else if (option_.containsKey(OPT.EXCOMMENT)) {
                            strval = gb.getStrValue();
                            if (strval != null) {
                                if (strval.toLowerCase(Locale.getDefault()).startsWith("msg,")) {
                                    strPrint.delete(0, strPrint.length());
                                    Matcher matcher = PATTERN_EXCOMMENT.matcher(strval.substring(4).trim());
                                    while (matcher.find()) {
                                        if (matcher.group(1) != null) {
                                            strPrint.append(matcher.group(1));
                                        } else if (matcher.group(2) != null) {
                                            gv[0] = getVariable(Integer.parseInt(matcher.group(2)));
                                            if (gv[0].isNil()) {
                                                strPrint.append("#0");
                                            } else {
                                                strPrint.append(DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                            }
                                        }
                                    }
                                    writeLog(strPrint.toString(), false);
                                } else if (strval.toLowerCase(Locale.getDefault()).startsWith("print,")) {
                                    strPrint.delete(0, strPrint.length());
                                    Matcher matcher = PATTERN_EXCOMMENT.matcher(strval.substring(6).trim());
                                    while (matcher.find()) {
                                        if (matcher.group(1) != null) {
                                            strPrint.append(matcher.group(1));
                                        } else if (matcher.group(2) != null) {
                                            gv[0] = getVariable(Integer.parseInt(matcher.group(2)));
                                            if (gv[0].isNil()) {
                                                strPrint.append("#0");
                                            } else {
                                                strPrint.append(DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                            }
                                        }
                                    }
                                    words.add(strPrint.toString());
                                } else if (strval.toLowerCase(Locale.getDefault()).startsWith("notify,")) {
                                    strPrint.delete(0, strPrint.length());
                                    Matcher matcher = PATTERN_EXCOMMENT.matcher(strval.substring(7).trim());
                                    while (matcher.find()) {
                                        if (matcher.group(1) != null) {
                                            strPrint.append(matcher.group(1));
                                        } else if (matcher.group(2) != null) {
                                            gv[0] = getVariable(Integer.parseInt(matcher.group(2)));
                                            if (gv[0].isNil()) {
                                                strPrint.append("#0");
                                            } else {
                                                strPrint.append(DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                            }
                                        }
                                    }
                                    notifyExComment(strPrint.toString());
                                } else if (strval.toLowerCase(Locale.getDefault()).startsWith("js,")) {
                                    if (webViewer_.getWorkerState() == Worker.State.SUCCEEDED) {
                                        script_.append(strval.substring(3).trim());
                                        if (script_.toString().endsWith("\\")) {
                                            script_.deleteCharAt(script_.length() - 1);
                                        } else {
                                            objval = platformRun(() -> {
                                                return webViewer_.getWebEngine().executeScript(script_.toString());
                                            });
                                            if (objval != null) {
                                                if (objval.getClass() == JSObject.class) {
                                                } else if (objval.getClass() == Integer.class) {
                                                } else if (objval.getClass() == Double.class) {
                                                } else if (objval.getClass() == String.class) {
                                                }
                                            }
                                            script_.delete(0, script_.length());
                                        }
                                    }
                                } else if (strval.toLowerCase(Locale.getDefault()).startsWith("path,")) {
                                    if (flg.get(FLG.EXTCALL)) {
                                        stringpath_ = strval.substring(5).trim();
                                    }
                                }
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.EOB:
                    if (flg.get(FLG.COORDINATE)) {
                        flg.put(FLG.COORDINATE, false);

                        // cycle
                        if (flg.get(FLG.CYCLE)) {
                            if (getGcodeVariable(argcycle, ARG_LOOP).isNil()) {
                                intval = 1;
                            } else {
                                intval = getGcodeVariable(argcycle, ARG_LOOP).getInt();
                                setGcodeVariable(argcycle, ARG_LOOP, null, null, null);
                            }
                            if (getGcodeVariable(global_, 4000 + ggroup_.get(17.0)).getDouble() == 17.0) {
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                    if (!getGcodeVariable(argcycle, ARG_X).isNil()) {
                                        setVariable(5021, getGcodeVariable(argcycle, ARG_X));
                                        setGcodeVariable(argcycle, ARG_X, null, null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                            laddersIo_.put(DesignController.LADDER_X1, getGcodeVariable(global_, 5021).getDouble());
                                        }
                                    }
                                    if (!getGcodeVariable(argcycle, ARG_Y).isNil()) {
                                        setVariable(5022, getGcodeVariable(argcycle, ARG_Y));
                                        setGcodeVariable(argcycle, ARG_Y, null, null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                            laddersIo_.put(DesignController.LADDER_Y1, getGcodeVariable(global_, 5022).getDouble());
                                        }
                                    }
                                    if (!getGcodeVariable(argcycle, ARG_R).isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 6983 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 5203 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                            dblval += getGcodeVariable(global_, 5133).getDouble() - getGcodeVariable(argcycle, ARG_R).getDouble();
                                        } else {
                                            dblval += getGcodeVariable(argcycle, ARG_R).getDouble();
                                        }
                                        setGcodeVariable(argcycle, ARG_V, dblval, null, null);
                                        setGcodeVariable(argcycle, ARG_R, null, null, null);
                                    }
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                    if (!getGcodeVariable(argcycle, ARG_X).isNil()) {
                                        setVariable(5021, getGcodeVariable(global_, 5021).getDouble() + (getGcodeVariable(argcycle, ARG_X).getDouble() * intval), null, null);
                                        setGcodeVariable(argcycle, ARG_X, null, null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                            laddersIo_.put(DesignController.LADDER_X1, getGcodeVariable(global_, 5021).getDouble());
                                        }
                                    }
                                    if (!getGcodeVariable(argcycle, ARG_Y).isNil()) {
                                        setVariable(5022, getGcodeVariable(global_, 5022).getDouble() + (getGcodeVariable(argcycle, ARG_Y).getDouble() * intval), null, null);
                                        setGcodeVariable(argcycle, ARG_Y, null, null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                            laddersIo_.put(DesignController.LADDER_Y1, getGcodeVariable(global_, 5022).getDouble());
                                        }
                                    }
                                    if (!getGcodeVariable(argcycle, ARG_R).isNil()) {
                                        dblval = getGcodeVariable(argcycle, ARG_R).getDouble();
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                            dblval = -dblval;
                                        }
                                        setGcodeVariable(argcycle, ARG_V, getGcodeVariable(argcycle, ARG_W).getDouble() + dblval, null, null);
                                        setGcodeVariable(argcycle, ARG_R, null, null, null);
                                    }
                                }
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(98.0)).getDouble() == 98.0) {
                                    setVariable(5023, getGcodeVariable(argcycle, ARG_W));
                                    if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                        laddersIo_.put(DesignController.LADDER_Z1, getGcodeVariable(global_, 5023).getDouble());
                                    }
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(99.0)).getDouble() == 99.0) {
                                    setVariable(5023, getGcodeVariable(argcycle, ARG_V));
                                    if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                        laddersIo_.put(DesignController.LADDER_Z1, getGcodeVariable(global_, 5023).getDouble());
                                    }
                                }
                            }
                        }

                        // X
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5161).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 6981 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5211).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5161).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 5201 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5211).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                            setVariable(5001, getGcodeVariable(global_, 5131).getDouble() - (getGcodeVariable(global_, 5021).getDouble() - getGcodeVariable(global_, 5121).getDouble() - dblval), null, null);
                        } else {
                            setVariable(5001, getGcodeVariable(global_, 5021).getDouble() - getGcodeVariable(global_, 5121).getDouble() - dblval, null, null);
                        }
                        setVariable(5041, getGcodeVariable(global_, 5021).getDouble() - dblval, null, null);
                        setVariable(5061, getGcodeVariable(global_, 5021).getDouble() - dblval, null, null);

                        // Y
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5162).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 6982 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5212).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5162).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 5202 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5212).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                            setVariable(5002, getGcodeVariable(global_, 5132).getDouble() - getGcodeVariable(global_, 5022).getDouble() - getGcodeVariable(global_, 5123).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5002, getGcodeVariable(global_, 5022).getDouble() - getGcodeVariable(global_, 5123).getDouble() - dblval, null, null);
                        }
                        setVariable(5042, getGcodeVariable(global_, 5022).getDouble() - dblval, null, null);
                        setVariable(5062, getGcodeVariable(global_, 5022).getDouble() - dblval, null, null);

                        // Z
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5163).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 6983 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5163).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 5203 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                            setVariable(5003, getGcodeVariable(global_, 5133).getDouble() - getGcodeVariable(global_, 5023).getDouble() - getGcodeVariable(global_, 5122).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5003, getGcodeVariable(global_, 5023).getDouble() - getGcodeVariable(global_, 5122).getDouble() - dblval, null, null);
                        }
                        setVariable(5043, getGcodeVariable(global_, 5023).getDouble() - dblval, null, null);
                        setVariable(5063, getGcodeVariable(global_, 5023).getDouble() - dblval, null, null);

                        // A
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5164).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 6984 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5214).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5164).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 5204 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5214).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                            setVariable(5004, getGcodeVariable(global_, 5134).getDouble() - getGcodeVariable(global_, 5024).getDouble() - getGcodeVariable(global_, 5124).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5004, getGcodeVariable(global_, 5024).getDouble() - getGcodeVariable(global_, 5124).getDouble() - dblval, null, null);
                        }
                        setVariable(5044, getGcodeVariable(global_, 5024).getDouble() - dblval, null, null);
                        setVariable(5064, getGcodeVariable(global_, 5024).getDouble() - dblval, null, null);

                        // B
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5165).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 6985 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5215).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5165).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 5205 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5215).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                            setVariable(5005, getGcodeVariable(global_, 5135).getDouble() - getGcodeVariable(global_, 5025).getDouble() - getGcodeVariable(global_, 5125).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5005, getGcodeVariable(global_, 5025).getDouble() - getGcodeVariable(global_, 5125).getDouble() - dblval, null, null);
                        }
                        setVariable(5045, getGcodeVariable(global_, 5025).getDouble() - dblval, null, null);
                        setVariable(5065, getGcodeVariable(global_, 5025).getDouble() - dblval, null, null);

                        // C
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5166).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 6986 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5216).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5166).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 5206 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5216).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                            setVariable(5006, getGcodeVariable(global_, 5136).getDouble() - getGcodeVariable(global_, 5026).getDouble() - getGcodeVariable(global_, 5126).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5006, getGcodeVariable(global_, 5026).getDouble() - getGcodeVariable(global_, 5126).getDouble() - dblval, null, null);
                        }
                        setVariable(5046, getGcodeVariable(global_, 5026).getDouble() - dblval, null, null);
                        setVariable(5066, getGcodeVariable(global_, 5026).getDouble() - dblval, null, null);

                        // U
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5167).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 6987 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5217).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5167).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 5207 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5217).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                            setVariable(5007, getGcodeVariable(global_, 5137).getDouble() - getGcodeVariable(global_, 5027).getDouble() - getGcodeVariable(global_, 5127).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5007, getGcodeVariable(global_, 5027).getDouble() - getGcodeVariable(global_, 5127).getDouble() - dblval, null, null);
                        }
                        setVariable(5047, getGcodeVariable(global_, 5027).getDouble() - dblval, null, null);
                        setVariable(5067, getGcodeVariable(global_, 5027).getDouble() - dblval, null, null);

                        // V
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5168).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 6988 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5218).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5168).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 5208 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5218).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                            setVariable(5008, getGcodeVariable(global_, 5138).getDouble() - getGcodeVariable(global_, 5028).getDouble() - getGcodeVariable(global_, 5128).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5008, getGcodeVariable(global_, 5028).getDouble() - getGcodeVariable(global_, 5128).getDouble() - dblval, null, null);
                        }
                        setVariable(5048, getGcodeVariable(global_, 5028).getDouble() - dblval, null, null);
                        setVariable(5068, getGcodeVariable(global_, 5028).getDouble() - dblval, null, null);

                        // W
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                            dblval = getGcodeVariable(global_, 5169).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 6989 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5219).getDouble();
                        } else {
                            dblval = getGcodeVariable(global_, 5169).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 5209 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5219).getDouble();
                        }
                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                            setVariable(5009, getGcodeVariable(global_, 5139).getDouble() - getGcodeVariable(global_, 5029).getDouble() - getGcodeVariable(global_, 5129).getDouble() - dblval, null, null);
                        } else {
                            setVariable(5009, getGcodeVariable(global_, 5029).getDouble() - getGcodeVariable(global_, 5129).getDouble() - dblval, null, null);
                        }
                        setVariable(5049, getGcodeVariable(global_, 5029).getDouble() - dblval, null, null);
                        setVariable(5069, getGcodeVariable(global_, 5029).getDouble() - dblval, null, null);
                    }

                    // Original Program
                    out.delete(0, out.length());
                    sv = gb.getStrValue().split("\\$\\$");
                    switch (sv.length) {
                        case 1:
                            // empty block
                            // out = sv[0];
                            break;
                        case 2:
                            line_ = Integer.parseInt(sv[0]) + 1;
                            out.append(sv[1]);
                            if (state_ == STATE.FORE) {
                                foreline_ = line_;
                            }
                            break;
                    }

                    // debug
                    if (gb.isDebug()) {
                        debugPrintHeader();
                        debugPrintStatus(pc, out.toString());
                        debugPrintJoin();
                        debugPrintChange();
                        debugPrintJoin();
                        debugPrintPositions();
                        debugPrintJoin();
                        debugPrintOffsets();
                        debugPrintJoin();
                        debugPrintVariables();
                    }

                    // ngc
                    if (state_ == STATE.FORE) {
                        subout.delete(0, subout.length());
                        subout.append("(");
                        subout.append(out.toString().replace("(", "<").replace(")", ">"));
                        subout.append(")");
                        if ((subout.length() % 2) == 1) {
                            subout.append(" ");
                        }

                        try {
                            if (ngcBufferedWriter_ != null) {
                                ngcBufferedWriter_.append(subout).append("\r\n");
                            }
                        } catch (IOException ex) {
                            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
                        }
                    }

                    // optimaization
                    if (option_.containsKey(OPT.OPTIMIZATION)) {
                        words = optimaization(words, flg);
                    }

                    // GCode
                    if (!words.isEmpty()) {
                        output(words, flg, gb.isDebug());
                    }
                    words.clear();

                    // notify variables
                    notifyVariables(variables_);
                    variables_.clear();

                    // debug
                    if (gb.isDebug()) {
                        debugPrintFooter();
                    }

                    // dxf
                    if (flg.get(FLG.DRAW)) {
                        flg.put(FLG.DRAW, false);
                        dxfPrint();
                    }

                    // flg clear
                    setVariable(4000, null, null, null);
                    setVariable(4113, null, null, null);
                    setVariable(4313, null, null, null);
                    flg.put(FLG.MIRROR, false);
                    flg.put(FLG.SKIP, false);
                    flg.put(FLG.TOOL_CHANGE, false);
                    flg.put(FLG.SKIP_FUNCTION, false);
                    flg.put(FLG.MODE, false);
                    if (flg.get(FLG.RECALL)) {
                        if (option_.containsKey(OPT.DISABLE_OUTPUT)) {
                            // Double flag prevention
                            flg.put(FLG.RECALL, false);
                        } else {
                            // disable program output
                            option_.put(OPT.DISABLE_OUTPUT, "");
                        }
                    }

                    // macro alarm
                    if (flg.get(FLG.ALARM)) {
                        flg.put(FLG.ALARM, false);
                        if (!option_.containsKey(OPT.DISABLE_OUTPUT)) {
                            writeLog(GcodeInterpreterEnums.MACRO_ALARM.toString() + ": " + GcodeFanucTwoByteCharacterCode.encode(out.toString()), false);
                            return RET_ERROR;
                        }
                    }

                    // macro stop
                    if (flg.get(FLG.STOP)) {
                        flg.put(FLG.STOP, false);
                        if (!option_.containsKey(OPT.DISABLE_OUTPUT)) {
                            writeLog(GcodeInterpreterEnums.MACRO_STOP.toString() + ": " + GcodeFanucTwoByteCharacterCode.encode(out.toString()), false);
                        }
                    }

                    // ladder & serial
                    if (serial_ == null) {
                        // ladder check
                        checkLadder = !laddersIo_.isEmpty();

                        if (checkLadder) {
                            laddersIo_.entrySet().forEach((entry) -> {
                                ladders_.setValueDirect(ladderAddress_.get(entry.getKey()), entry.getValue());
                            });

                            while (checkLadder) {
                                if (!ladders_.isCycling()) {
                                    writeLog(GcodeInterpreterEnums.LADDER_NOT_RUNNING.toString(), true);
                                    return RET_ERROR;
                                }

                                for (Iterator<Map.Entry<Integer, Double>> iterator = laddersIo_.entrySet().iterator(); iterator.hasNext();) {
                                    entryIo = iterator.next();
                                    ladderIo = ladders_.getValue(ladderAddress_.get(entryIo.getKey() + 1));
                                    if (ladderIo != null) {
                                        if (ladderIo.equals(entryIo.getValue())) {
                                            iterator.remove();
                                        }
                                    }
                                }
                                checkLadder = !laddersIo_.isEmpty();
                            }
                        }
                    } else {
                        // ladder check & serial buffer check 
                        checkLadder = !laddersIo_.isEmpty();
                        checkSerial = serial_.isObserveDSR() || serial_.isObserveCTS() || serial_.isBufferFlow();

                        if (checkLadder) {
                            laddersIo_.entrySet().forEach((entry) -> {
                                ladders_.setValueDirect(ladderAddress_.get(entry.getKey()), entry.getValue());
                            });
                        }

                        while (checkLadder || checkSerial) {
                            if (checkLadder) {
                                if (!ladders_.isCycling()) {
                                    writeLog(GcodeInterpreterEnums.LADDER_NOT_RUNNING.toString(), true);
                                    return RET_ERROR;
                                }

                                for (Iterator<Map.Entry<Integer, Double>> iterator = laddersIo_.entrySet().iterator(); iterator.hasNext();) {
                                    entryIo = iterator.next();
                                    ladderIo = ladders_.getValue(ladderAddress_.get(entryIo.getKey() + 1));
                                    if (ladderIo != null) {
                                        if (ladderIo.equals(entryIo.getValue())) {
                                            iterator.remove();
                                        }
                                    }
                                }
                                checkLadder = !laddersIo_.isEmpty();
                            }

                            if (checkSerial) {
                                if (!serial_.isOwned()) {
                                    writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                    return RET_ERROR;
                                } else if (serial_.isObserveDSR() && serial_.isObserveCTS()) {
                                    if (!serial_.isDataSetReady() || !serial_.isClearToSend()) {
                                        writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                        return RET_ERROR;
                                    }
                                } else if (serial_.isObserveDSR()) {
                                    if (!serial_.isDataSetReady()) {
                                        writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                        return RET_ERROR;
                                    }
                                } else if (serial_.isObserveCTS()) {
                                    if (!serial_.isClearToSend()) {
                                        writeLog(GcodeInterpreterEnums.DISCONNECTED_SERIAL_CONNECTION.toString(), true);
                                        return RET_ERROR;
                                    }
                                }
                                checkSerial = serial_.isBufferFlow();
                            }

                            if (checkSerial) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(100);
                                } catch (InterruptedException ex) {
                                }
                            }
                        }
                    }

                    // script program exec
                    if ((state_ != STATE.SCRIPT) && (bytecodesScript_.size() > 0)) {
                        gcodeInterpreter_ = new GcodeInterpreter();
                        gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                        gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                        gcodeInterpreter_.setBytecodesScript(bytecodesScript_);
                        gcodeInterpreter_.setLocal(local_);
                        gcodeInterpreter_.setArgModal(argmodal_);
                        gcodeInterpreter_.setState(STATE.SCRIPT);
                        gcodeInterpreter_.setProgramNumber(null);
                        gcodeInterpreter_.setLocalLevel(locallevel_);
                        gcodeInterpreter_.setCallJump(-1);
                        gcodeInterpreter_.setScript(script_);

                        ret = gcodeInterpreter_.exec();
                        setWords(gcodeInterpreter_.getWords());
                        setFlg(gcodeInterpreter_.getFlg());
                        setScript(gcodeInterpreter_.getScript());
                        gcodeInterpreter_ = null;

                        switch (ret) {
                            case RET_ERROR:
                                return RET_ERROR;
                            case RET_END:
                                return RET_END;
                        }
                        bytecodesScript_.clear();
                    }

                    // option program exec
                    if ((state_ != STATE.OPTION) && (bytecodesOption_.size() > 0)) {
                        gcodeInterpreter_ = new GcodeInterpreter();
                        gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                        gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                        gcodeInterpreter_.setBytecodesScript(null);
                        gcodeInterpreter_.setLocal(local_);
                        gcodeInterpreter_.setArgModal(null);
                        gcodeInterpreter_.setState(STATE.OPTION);
                        gcodeInterpreter_.setProgramNumber(null);
                        gcodeInterpreter_.setLocalLevel(locallevel_);
                        gcodeInterpreter_.setCallJump(-1);
                        gcodeInterpreter_.setScript(script_);

                        ret = gcodeInterpreter_.exec();
                        setWords(gcodeInterpreter_.getWords());
                        setFlg(gcodeInterpreter_.getFlg());
                        setScript(gcodeInterpreter_.getScript());
                        gcodeInterpreter_ = null;

                        switch (ret) {
                            case RET_ERROR:
                                return RET_ERROR;
                        }
                    }

                    // axis move
                    if (flg.get(FLG.AXIS_MOVE)) {
                        flg.put(FLG.AXIS_MOVE, false);
                        if (getGcodeVariable(global_, 4000 + ggroup_.get(66.0)).getDouble() == 66.0) {
                            setVariable(4000 + ggroup_.get(67.0), 67.0, null, null);
                            if (argmodal_.get(ARG_PROGRAM) == null) {
                                writeLog(GcodeInterpreterEnums.PROGRAM_NUMBER_NO_DECLARATION.toString(), true);
                                return RET_ERROR;
                            }

                            ret = RET_NONE;
                            lp = 0;
                            if (argmodal_.containsKey(ARG_LOOP)) {
                                lp = argmodal_.get(ARG_LOOP).getInt() - 1;
                            }
                            for (; lp >= 0; lp--) {
                                gcodeInterpreter_ = new GcodeInterpreter();
                                gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                                gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                                gcodeInterpreter_.setBytecodesScript(null);
                                gcodeInterpreter_.cpyLocal(argmodal_);
                                gcodeInterpreter_.setArgModal(null);
                                gcodeInterpreter_.setState(STATE.FORE_BACK);
                                gcodeInterpreter_.setProgramNumber(argmodal_.get(ARG_PROGRAM).getString());
                                gcodeInterpreter_.setLocalLevel(locallevel_ + 1);
                                gcodeInterpreter_.setCallJump(-1);
                                gcodeInterpreter_.setScript(null);

                                ret = gcodeInterpreter_.exec();
                                setWords(gcodeInterpreter_.getWords());
                                setFlg(gcodeInterpreter_.getFlg());
                                gcodeInterpreter_ = null;

                                switch (ret) {
                                    case RET_ERROR:
                                        return RET_ERROR;
                                    case RET_END:
                                        return RET_END;
                                }
                            }
                            if (ret != RET_NONE) {
                                pc = gotoJump(bytecodes, programNumbers, pc, ret);
                                if (pc == RET_ERROR) {
                                    writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + ret + "]", true);
                                    return RET_ERROR;
                                }
                                pc--;
                            }
                            setVariable(4000 + ggroup_.get(66.0), 66.0, null, null);
                            if (programnumber_ != null) {
                                if (programnumber_.startsWith("O")) {
                                    if (PATTERN_UINT.matcher(programnumber_.substring(1)).find()) {
                                        setVariable(4115, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                        setVariable(4315, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                    } else {
                                        setVariable(4115, null, programnumber_, null);
                                        setVariable(4315, null, programnumber_, null);
                                    }
                                } else {
                                    setVariable(4115, null, programnumber_, null);
                                    setVariable(4315, null, programnumber_, null);
                                }
                            }
                        }
                    }

                    // modal
                    if (flg.get(FLG.MODAL)) {
                        flg.put(FLG.MODAL, false);
                        ic = jc = kc = 0;
                        argmodal_.putAll(arglocal);
                    }

                    // flgs
                    if (flg.get(FLG.EXIT)) {
                        retval = RET_END;
                    } else if (flg.get(FLG.RETURN)) {
                        flg.put(FLG.RETURN, false);
                        flg.put(FLG.EXIT, true);
                    } else if (flg.get(FLG.CALL)) {
                        flg.put(FLG.CALL, false);
                        if (arglocal.get(ARG_PROGRAM) == null) {
                            writeLog(GcodeInterpreterEnums.PROGRAM_NUMBER_NO_DECLARATION.toString(), true);
                            return RET_ERROR;
                        }
                        ret = RET_NONE;
                        lp = 0;
                        calljump = -1;
                        if (arglocal.containsKey(ARG_LOOP)) {
                            lp = arglocal.get(ARG_LOOP).getInt() - 1;
                        }
                        if (arglocal.containsKey(ARG_CALL_JUMP)) {
                            calljump = arglocal.get(ARG_CALL_JUMP).getInt();
                        }
                        for (; lp >= 0; lp--) {
                            gcodeInterpreter_ = new GcodeInterpreter();
                            gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                            gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                            gcodeInterpreter_.setBytecodesScript(null);
                            gcodeInterpreter_.setLocal(local_);
                            gcodeInterpreter_.setArgModal(argmodal_);
                            gcodeInterpreter_.setState(STATE.FORE_BACK);
                            gcodeInterpreter_.setProgramNumber(arglocal.get(ARG_PROGRAM).getString());
                            gcodeInterpreter_.setLocalLevel(locallevel_);
                            gcodeInterpreter_.setCallJump(calljump);
                            gcodeInterpreter_.setScript(script_);

                            ret = gcodeInterpreter_.exec();
                            setWords(gcodeInterpreter_.getWords());
                            setFlg(gcodeInterpreter_.getFlg());
                            setScript(gcodeInterpreter_.getScript());
                            gcodeInterpreter_ = null;

                            switch (ret) {
                                case RET_ERROR:
                                    return RET_ERROR;
                                case RET_END:
                                    return RET_END;
                            }
                        }
                        if (ret != RET_NONE) {
                            pc = gotoJump(bytecodes, programNumbers, pc, ret);
                            if (pc == RET_ERROR) {
                                writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + ret + "]", true);
                                return RET_ERROR;
                            }
                            pc--;
                        }
                        arglocal.clear();

                        if (programnumber_ != null) {
                            if (programnumber_.startsWith("O")) {
                                if (PATTERN_UINT.matcher(programnumber_.substring(1)).find()) {
                                    setVariable(4115, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                    setVariable(4315, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                } else {
                                    setVariable(4115, null, programnumber_, null);
                                    setVariable(4315, null, programnumber_, null);
                                }
                            } else {
                                setVariable(4115, null, programnumber_, null);
                                setVariable(4315, null, programnumber_, null);
                            }
                        }
                    } else if (flg.get(FLG.EXTCALL)) {
                        flg.put(FLG.EXTCALL, false);
                        if (bytecodesExternal_.size() > 0) {
                            writeLog(GcodeInterpreterEnums.EXTERNAL_SUBPROGRAM_MULTIPLE_CALL.toString(), true);
                            return RET_ERROR;
                        }
                        if (arglocal.get(ARG_PROGRAM) == null) {
                            writeLog(GcodeInterpreterEnums.PROGRAM_NUMBER_NO_DECLARATION.toString(), true);
                            return RET_ERROR;
                        }

                        try {
                            Path externalSubProgramFile;
                            if (stringpath_ == null) {
                                externalSubProgramFile = externalsubprogramdir_.resolve(arglocal.get(ARG_PROGRAM).getString());
                                if (!Files.exists(externalSubProgramFile)) {
                                    externalSubProgramFile = externalsubprogramdir_.resolve(arglocal.get(ARG_PROGRAM).getString() + ".dat");
                                }
                            } else {
                                externalSubProgramFile = externalsubprogramdir_.resolve(stringpath_);
                            }
                            if (Files.exists(externalSubProgramFile)) {
                                if (Files.isReadable(externalSubProgramFile)) {
                                    byte[] decrypted = Files.readAllBytes(externalSubProgramFile);
                                    String lines = Crypto.decrypt(DesignEnums.GCODE.toString() + DesignEnums.BYTECODE.toString(), decrypted, DesignEnums.ALGORITHM.toString());
                                    if (lines != null) {
                                        String[] line = lines.split("\n");
                                        if ((DesignEnums.GCODE.toString() + DesignEnums.BYTECODE.toString()).equals(line[0])) {
                                            if (GcodeParser.VERSION.equals(line[1])) {
                                                GcodeVirtualMachine.addBytecodes(bytecodesExternal_, programNumbersExternal_, Arrays.asList(line), 2, false);
                                            } else {
                                                writeLog(DesignEnums.PARSER_NOT_COMPATIBLE.toString() + "[" + externalSubProgramFile.toString() + "]", true);
                                                return RET_ERROR;
                                            }
                                        } else {
                                            lines = null;
                                        }
                                    }
                                    if (lines == null) {
                                        if (!GcodeVirtualMachine.addBytecodes(bytecodesExternal_, programNumbersExternal_, GcodeVirtualMachine.parse(Files.newInputStream(externalSubProgramFile)), 0, true)) {
                                            return RET_ERROR;
                                        }
                                    }
                                } else {
                                    writeLog(DesignEnums.FILE_CAN_NOT_READ.toString() + "[" + externalSubProgramFile.toString() + "]", true);
                                    return RET_ERROR;
                                }
                            } else {
                                writeLog(DesignEnums.FILE_NOT_FOUND.toString() + "[" + externalSubProgramFile.toString() + "]", true);
                                return RET_ERROR;
                            }
                        } catch (IOException ex) {
                            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
                            return RET_ERROR;
                        }

                        ret = RET_NONE;
                        lp = 0;
                        calljump = -1;
                        if (arglocal.containsKey(ARG_LOOP)) {
                            lp = arglocal.get(ARG_LOOP).getInt() - 1;
                        }
                        if (arglocal.containsKey(ARG_CALL_JUMP)) {
                            calljump = arglocal.get(ARG_CALL_JUMP).getInt();
                        }
                        for (; lp >= 0; lp--) {
                            gcodeInterpreter_ = new GcodeInterpreter();
                            gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                            gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                            gcodeInterpreter_.setBytecodesScript(null);
                            gcodeInterpreter_.setLocal(local_);
                            gcodeInterpreter_.setArgModal(argmodal_);
                            gcodeInterpreter_.setState(STATE.EXTERNAL);
                            gcodeInterpreter_.setProgramNumber(arglocal.get(ARG_PROGRAM).getString());
                            gcodeInterpreter_.setLocalLevel(locallevel_);
                            gcodeInterpreter_.setCallJump(calljump);
                            gcodeInterpreter_.setScript(script_);

                            ret = gcodeInterpreter_.exec();
                            setWords(gcodeInterpreter_.getWords());
                            setFlg(gcodeInterpreter_.getFlg());
                            setScript(gcodeInterpreter_.getScript());
                            gcodeInterpreter_ = null;

                            switch (ret) {
                                case RET_ERROR:
                                    return RET_ERROR;
                                case RET_END:
                                    return RET_END;
                            }
                        }
                        if (ret != RET_NONE) {
                            pc = gotoJump(bytecodes, programNumbers, pc, ret);
                            if (pc == RET_ERROR) {
                                writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + ret + "]", true);
                                return RET_ERROR;
                            }
                            pc--;
                        }
                        arglocal.clear();
                        GcodeVirtualMachine.clearBytecodes(bytecodesExternal_);
                        GcodeVirtualMachine.clearProgramNumbers(programNumbersExternal_);
                        stringpath_ = null;

                        if (programnumber_ != null) {
                            if (programnumber_.startsWith("O")) {
                                if (PATTERN_UINT.matcher(programnumber_.substring(1)).find()) {
                                    setVariable(4115, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                    setVariable(4315, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                } else {
                                    setVariable(4115, null, programnumber_, null);
                                    setVariable(4315, null, programnumber_, null);
                                }
                            } else {
                                setVariable(4115, null, programnumber_, null);
                                setVariable(4315, null, programnumber_, null);
                            }
                        }
                    } else if (flg.get(FLG.MCALL)) {
                        flg.put(FLG.MCALL, false);
                        if (arglocal.get(ARG_PROGRAM) == null) {
                            writeLog(GcodeInterpreterEnums.PROGRAM_NUMBER_NO_DECLARATION.toString(), true);
                            return RET_ERROR;
                        }
                        ic = jc = kc = 0;
                        ret = RET_NONE;
                        lp = 0;
                        if (arglocal.containsKey(ARG_LOOP)) {
                            lp = arglocal.get(ARG_LOOP).getInt() - 1;
                        }
                        for (; lp >= 0; lp--) {
                            gcodeInterpreter_ = new GcodeInterpreter();
                            gcodeInterpreter_.inheritanceGcodeInterpreter(this);
                            gcodeInterpreter_.setBytecodesOption(bytecodesOption_);
                            gcodeInterpreter_.setBytecodesScript(null);
                            gcodeInterpreter_.cpyLocal(arglocal);
                            gcodeInterpreter_.setArgModal(argmodal_);
                            gcodeInterpreter_.setState(STATE.FORE_BACK);
                            gcodeInterpreter_.setProgramNumber(arglocal.get(ARG_PROGRAM).getString());
                            gcodeInterpreter_.setLocalLevel(locallevel_ + 1);
                            gcodeInterpreter_.setCallJump(-1);
                            gcodeInterpreter_.setScript(null);

                            ret = gcodeInterpreter_.exec();
                            setWords(gcodeInterpreter_.getWords());
                            setFlg(gcodeInterpreter_.getFlg());
                            gcodeInterpreter_ = null;

                            switch (ret) {
                                case RET_ERROR:
                                    return RET_ERROR;
                                case RET_END:
                                    return RET_END;
                            }
                        }
                        if (ret != RET_NONE) {
                            pc = gotoJump(bytecodes, programNumbers, pc, ret);
                            if (pc == RET_ERROR) {
                                writeLog(GcodeInterpreterEnums.NO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + ret + "]", true);
                                return RET_ERROR;
                            }
                            pc--;
                        }
                        arglocal.clear();

                        if (programnumber_ != null) {
                            if (programnumber_.startsWith("O")) {
                                if (PATTERN_UINT.matcher(programnumber_.substring(1)).find()) {
                                    setVariable(4115, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                    setVariable(4315, Double.parseDouble(programnumber_.substring(1)), programnumber_, null);
                                } else {
                                    setVariable(4115, null, programnumber_, null);
                                    setVariable(4315, null, programnumber_, null);
                                }
                            } else {
                                setVariable(4115, null, programnumber_, null);
                                setVariable(4315, null, programnumber_, null);
                            }
                        }
                    }

                    // enable program output
                    if (flg.get(FLG.RECALL)) {
                        flg.put(FLG.RECALL, false);
                        option_.remove(OPT.DISABLE_OUTPUT);
                    }

                    // Set program counter of GOTO destination
                    if (flg.get(FLG.GOTO)) {
                        flg.put(FLG.GOTO, false);
                        pc = gotopc;
                    }

                    isExit = flg.get(FLG.EXIT);
                    break;
                case GcodeParserConstants.GOTO:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gotopc = gotoJump(bytecodes, programNumbers, pc, gv[0].getInt());
                        if (gotopc == RET_ERROR) {
                            writeLog(GcodeInterpreterEnums.NO_GOTO_SEQUENCE_NUMBER.toString() + "[" + programnumber_ + " : " + gv[0].getInt() + "]", true);
                            return RET_ERROR;
                        } else {
                            gotopc--;
                            flg.put(FLG.GOTO, true);
                            flg.put(FLG.SKIP, true);
                        }
                    }
                    break;
                case GcodeParserConstants.IF:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (gv[0].getInt() == GcodeVariable.FALSE) {
                            gotopc = ifJump(bytecodes, pc);
                            if (gotopc == RET_ERROR) {
                                writeLog(GcodeInterpreterEnums.DESTINATION_UNKNOWN_IF.toString() + "[" + programnumber_ + "]", true);
                                return RET_ERROR;
                            } else {
                                gotopc--;
                                flg.put(FLG.GOTO, true);
                                flg.put(FLG.SKIP, true);
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.WHILE:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (gv[0].getInt() == GcodeVariable.FALSE) {
                            gotopc = whileJump(bytecodes, pc, gb.getIntValue());
                            if (gotopc == RET_ERROR) {
                                writeLog(GcodeInterpreterEnums.NO_END_NUMBER_WHILE.toString() + "[" + programnumber_ + " : " + gb.getIntValue() + "]", true);
                                return RET_ERROR;
                            } else {
                                gotopc--;
                                flg.put(FLG.GOTO, true);
                                flg.put(FLG.SKIP, true);
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.DO:
                    break;
                case GcodeParserConstants.END:
                    if (!flg.get(FLG.SKIP)) {
                        gotopc = endJump(bytecodes, pc, gb.getIntValue());
                        if (gotopc == RET_ERROR) {
                            writeLog(GcodeInterpreterEnums.NO_DO_NUMBER_WHILE.toString() + "[" + programnumber_ + " : " + gb.getIntValue() + "]", true);
                            return RET_ERROR;
                        } else {
                            gotopc--;
                            flg.put(FLG.GOTO, true);
                            flg.put(FLG.SKIP, true);
                        }
                    }
                    break;
                case GcodeParserConstants.PLUS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].add(gv[0]));
                    }
                    break;
                case GcodeParserConstants.MINUS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].sub(gv[0]));
                    }
                    break;
                case GcodeParserConstants.ASTERISK:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].mul(gv[0]));
                    }
                    break;
                case GcodeParserConstants.SLASH:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].div(gv[0]));
                    }
                    break;
                case GcodeParserConstants.SIGN_PLUS:
                    if (!flg.get(FLG.SKIP)) {
                    }
                    break;
                case GcodeParserConstants.SIGN_MINUS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].neg());
                    }
                    break;
                case GcodeParserConstants.BLOCK_SKIP:
                    if (!flg.get(FLG.SKIP)) {
                        if (blockskip_.containsKey(gb.getIntValue())) {
                            if (blockskip_.get(gb.getIntValue())) {
                                flg.put(FLG.SKIP, true);
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.AND:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].and(gv[0]));
                    }
                    break;
                case GcodeParserConstants.OR:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].or(gv[0]));
                    }
                    break;
                case GcodeParserConstants.XOR:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].xor(gv[0]));
                    }
                    break;
                case GcodeParserConstants.MOD:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].mod(gv[0]));
                    }
                    break;
                case GcodeParserConstants.EQ:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].eq(gv[0]));
                    }
                    break;
                case GcodeParserConstants.NE:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].ne(gv[0]));
                    }
                    break;
                case GcodeParserConstants.LT:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].lt(gv[0]));
                    }
                    break;
                case GcodeParserConstants.LE:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].le(gv[0]));
                    }
                    break;
                case GcodeParserConstants.GT:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].gt(gv[0]));
                    }
                    break;
                case GcodeParserConstants.GE:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].ge(gv[0]));
                    }
                    break;
                case GcodeParserConstants.SIN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].sin());
                    }
                    break;
                case GcodeParserConstants.COS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].cos());
                    }
                    break;
                case GcodeParserConstants.TAN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].tan());
                    }
                    break;
                case GcodeParserConstants.ASIN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].asin());
                    }
                    break;
                case GcodeParserConstants.ACOS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].acos());
                    }
                    break;
                case GcodeParserConstants.ATAN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].atan());
                    }
                    break;
                case GcodeParserConstants.SQRT:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].sqrt());
                    }
                    break;
                case GcodeParserConstants.ABS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].abs());
                    }
                    break;
                case GcodeParserConstants.BIN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].bin());
                    }
                    break;
                case GcodeParserConstants.BCD:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].bcd());
                    }
                    break;
                case GcodeParserConstants.ROUND:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].round());
                    }
                    break;
                case GcodeParserConstants.FIX:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].fix());
                    }
                    break;
                case GcodeParserConstants.FUP:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].fup());
                    }
                    break;
                case GcodeParserConstants.LN:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].ln());
                    }
                    break;
                case GcodeParserConstants.EXP:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].exp());
                    }
                    break;
                case GcodeParserConstants.POW:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].pow(gv[0]));
                    }
                    break;
                case GcodeParserConstants.ADP:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(gv[0].adp());
                    }
                    break;
                case GcodeParserConstants.ATAN2:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        stack.push(gv[1].atan2(gv[0]));
                    }
                    break;
                case GcodeParserConstants.POPEN:
                    if (!flg.get(FLG.SKIP)) {
                        option_.put(OPT.PRINT, "");
                    }
                    break;
                case GcodeParserConstants.PCLOS:
                    if (!flg.get(FLG.SKIP)) {
                        option_.remove(OPT.PRINT);
                    }
                    break;
                case GcodeParserConstants.BPRNT:
                    if (!flg.get(FLG.SKIP)) {
                        if (option_.containsKey(OPT.PRINT)) {
                            strPrint.delete(0, strPrint.length());
                            Matcher matcher = Pattern.compile("([A-Z\\d\\*/\\+\\-\\?@&_]+)|#(\\d+)\\[(\\d)\\]").matcher(gb.getStrValue().trim());
                            while (matcher.find()) {
                                if (matcher.group(1) != null) {
                                    strPrint.append(matcher.group(1).replace("*", " "));
                                } else if (matcher.group(2) != null) {
                                    strPrint.append(Integer.toHexString((int) Math.round(getVariable(Integer.parseInt(matcher.group(2))).getDouble() * Math.pow(10, Integer.parseInt(matcher.group(3))))));
                                } else {
                                    return RET_ERROR;
                                }
                            }
                            writeLog(strPrint.toString(), false);
                        } else {
                            writeLog(GcodeInterpreterEnums.NOT_OPEN_PRINT.toString(), true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.DPRNT:
                    if (!flg.get(FLG.SKIP)) {
                        if (option_.containsKey(OPT.PRINT)) {
                            strPrint.delete(0, strPrint.length());
                            Matcher matcher = Pattern.compile("([A-Z\\d\\*/\\+\\-\\?@&_]+)|#(\\d+)\\[(\\d)?(\\d)\\]").matcher(gb.getStrValue().trim());
                            while (matcher.find()) {
                                if (matcher.group(1) != null) {
                                    strPrint.append(matcher.group(1).replace("*", " "));
                                } else if (matcher.group(2) != null) {
                                    DECIMAL_FORMAT_PRINT.setGroupingUsed(false);
                                    DECIMAL_FORMAT_PRINT.setMinimumFractionDigits(0);
                                    DECIMAL_FORMAT_PRINT.setMinimumIntegerDigits(0);
                                    if (matcher.group(3) == null) {
                                        DECIMAL_FORMAT_PRINT.setMaximumIntegerDigits(0);
                                    } else {
                                        DECIMAL_FORMAT_PRINT.setMaximumIntegerDigits(Integer.parseInt(matcher.group(3)));
                                    }
                                    if (matcher.group(4) == null) {
                                        DECIMAL_FORMAT_PRINT.setMaximumFractionDigits(0);
                                    } else {
                                        DECIMAL_FORMAT_PRINT.setMaximumFractionDigits(Integer.parseInt(matcher.group(4)));
                                    }
                                    strPrint.append(DECIMAL_FORMAT_PRINT.format(getVariable(Integer.parseInt(matcher.group(2))).getDouble()));
                                } else {
                                    return RET_ERROR;
                                }
                            }
                            writeLog(strPrint.toString(), false);
                        } else {
                            writeLog(GcodeInterpreterEnums.NOT_OPEN_PRINT.toString(), true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.A:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_A, gv[0].setNumber(ARG_A));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("A#0");
                                } else {
                                    words.add("A" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xfff7), null, null);
                                    setVariable(5134, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0008), null, null);
                                    setVariable(5134, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[A]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("A" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("A" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5024, getGcodeVariable(global_, 5164).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_A1)) {
                                            laddersIo_.put(DesignController.LADDER_A1, getGcodeVariable(global_, 5024).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5124).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 6984 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5124).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 5204 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                            setVariable(5214, getGcodeVariable(global_, 5024).getDouble() - dblval - (getGcodeVariable(global_, 5134).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5214, getGcodeVariable(global_, 5024).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5164).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_A, dblval, null, null);
                                                } else {
                                                    setVariable(5024, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_A1)) {
                                                        laddersIo_.put(DesignController.LADDER_A1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_A)) {
                                                    dblval -= getGcodeVariable(global_, 5164).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5164).getDouble() + getGcodeVariable(global_, 5124).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 6984 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5214).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5164).getDouble() + getGcodeVariable(global_, 5124).getDouble() + getGcodeVariable(global_, 5204).getDouble() + getGcodeVariable(global_, 5204 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5214).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                                    dblval += getGcodeVariable(global_, 5134).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_A, dblval, null, null);
                                                } else {
                                                    setVariable(5024, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_A1)) {
                                                        laddersIo_.put(DesignController.LADDER_A1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_A)) {
                                                    dblval -= getGcodeVariable(global_, 5164).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_A)) {
                                                    dblval -= getGcodeVariable(global_, 5204).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_A)) {
                                                    dblval -= getGcodeVariable(global_, 5214).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5124).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                                        dblval -= getGcodeVariable(global_, 5134).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_A)) {
                                                                dblval -= getGcodeVariable(global_, 5224).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_A)) {
                                                                dblval -= getGcodeVariable(global_, 5244).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_A)) {
                                                                dblval -= getGcodeVariable(global_, 5264).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_A)) {
                                                                dblval -= getGcodeVariable(global_, 5284).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_A)) {
                                                                dblval -= getGcodeVariable(global_, 5304).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_A)) {
                                                                dblval -= getGcodeVariable(global_, 5324).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("A" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_A, dblval, null, null);
                                                } else {
                                                    setVariable(5024, getGcodeVariable(global_, 5024).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_A1)) {
                                                        laddersIo_.put(DesignController.LADDER_A1, getGcodeVariable(global_, 5024).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0008) == 0x0008) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("A" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.B:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_B, gv[0].setNumber(ARG_B));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("B#0");
                                } else {
                                    words.add("B" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xffef), null, null);
                                    setVariable(5135, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0010), null, null);
                                    setVariable(5135, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[B]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("B" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("B" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5025, getGcodeVariable(global_, 5165).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_B1)) {
                                            laddersIo_.put(DesignController.LADDER_B1, getGcodeVariable(global_, 5025).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5125).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 6985 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5125).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 5205 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                            setVariable(5215, getGcodeVariable(global_, 5025).getDouble() - dblval - (getGcodeVariable(global_, 5135).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5215, getGcodeVariable(global_, 5025).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5165).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_B, dblval, null, null);
                                                } else {
                                                    setVariable(5025, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_B1)) {
                                                        laddersIo_.put(DesignController.LADDER_B1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_B)) {
                                                    dblval -= getGcodeVariable(global_, 5165).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5165).getDouble() + getGcodeVariable(global_, 5125).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 6985 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5215).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5165).getDouble() + getGcodeVariable(global_, 5125).getDouble() + getGcodeVariable(global_, 5205).getDouble() + getGcodeVariable(global_, 5205 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5215).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                                    dblval += getGcodeVariable(global_, 5135).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_B, dblval, null, null);
                                                } else {
                                                    setVariable(5025, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_B1)) {
                                                        laddersIo_.put(DesignController.LADDER_B1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_B)) {
                                                    dblval -= getGcodeVariable(global_, 5165).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_B)) {
                                                    dblval -= getGcodeVariable(global_, 5205).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_B)) {
                                                    dblval -= getGcodeVariable(global_, 5215).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5125).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                                        dblval -= getGcodeVariable(global_, 5135).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_B)) {
                                                                dblval -= getGcodeVariable(global_, 5225).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_B)) {
                                                                dblval -= getGcodeVariable(global_, 5245).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_B)) {
                                                                dblval -= getGcodeVariable(global_, 5265).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_B)) {
                                                                dblval -= getGcodeVariable(global_, 5285).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_B)) {
                                                                dblval -= getGcodeVariable(global_, 5305).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_B)) {
                                                                dblval -= getGcodeVariable(global_, 5325).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("B" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_B, dblval, null, null);
                                                } else {
                                                    setVariable(5025, getGcodeVariable(global_, 5025).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_B1)) {
                                                        laddersIo_.put(DesignController.LADDER_B1, getGcodeVariable(global_, 5025).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0010) == 0x0010) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("B" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.C:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_C, gv[0].setNumber(ARG_C));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("C#0");
                                } else {
                                    words.add("C" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xffdf), null, null);
                                    setVariable(5136, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0020), null, null);
                                    setVariable(5136, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[C]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("C" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("C" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5026, getGcodeVariable(global_, 5166).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_C1)) {
                                            laddersIo_.put(DesignController.LADDER_C1, getGcodeVariable(global_, 5026).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5126).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 6986 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5126).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 5206 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                            setVariable(5216, getGcodeVariable(global_, 5026).getDouble() - dblval - (getGcodeVariable(global_, 5136).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5216, getGcodeVariable(global_, 5026).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5166).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_C, dblval, null, null);
                                                } else {
                                                    setVariable(5026, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_C1)) {
                                                        laddersIo_.put(DesignController.LADDER_C1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_C)) {
                                                    dblval -= getGcodeVariable(global_, 5166).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5166).getDouble() + getGcodeVariable(global_, 5126).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 6986 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5216).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5166).getDouble() + getGcodeVariable(global_, 5126).getDouble() + getGcodeVariable(global_, 5206).getDouble() + getGcodeVariable(global_, 5206 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5216).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                                    dblval += getGcodeVariable(global_, 5136).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_C, dblval, null, null);
                                                } else {
                                                    setVariable(5026, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_C1)) {
                                                        laddersIo_.put(DesignController.LADDER_C1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_C)) {
                                                    dblval -= getGcodeVariable(global_, 5166).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_C)) {
                                                    dblval -= getGcodeVariable(global_, 5206).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_C)) {
                                                    dblval -= getGcodeVariable(global_, 5216).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5126).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                                        dblval -= getGcodeVariable(global_, 5136).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_C)) {
                                                                dblval -= getGcodeVariable(global_, 5226).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_C)) {
                                                                dblval -= getGcodeVariable(global_, 5246).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_C)) {
                                                                dblval -= getGcodeVariable(global_, 5266).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_C)) {
                                                                dblval -= getGcodeVariable(global_, 5286).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_C)) {
                                                                dblval -= getGcodeVariable(global_, 5306).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_C)) {
                                                                dblval -= getGcodeVariable(global_, 5326).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("C" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_C, dblval, null, null);
                                                } else {
                                                    setVariable(5026, getGcodeVariable(global_, 5026).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_C1)) {
                                                        laddersIo_.put(DesignController.LADDER_C1, getGcodeVariable(global_, 5026).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0020) == 0x0020) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("C" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.D:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_D, gv[0].setNumber(ARG_D));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("D#0");
                                } else {
                                    words.add("D" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            iv = gv[0].getInt();
                            if (dcodeconvert_.containsKey(iv)) {
                                for (String code : dcodeconvert_.get(iv).split("\\|")) {
                                    code = code.trim();
                                    if (!code.isEmpty()) {
                                        if (PATTERN_NUMBER.matcher(code).find()) {
                                            intval = Integer.parseInt(code);
                                            if (intval >= 0) {
                                                words.add("D" + DECIMAL_FORMAT_CODE.format(intval));
                                            }
                                        } else {
                                            words.add(code);
                                        }
                                    }
                                }
                            } else {
                                words.add("D" + DECIMAL_FORMAT_CODE.format(iv));
                            }
                            setVariable(4107, gv[0].setNumber(4107));
                            setVariable(4307, gv[0].setNumber(4307));
                            if (getGcodeVariable(global_, 4000 + ggroup_.get(41.0)).getDouble() == 41.0) {
                                if (gv[0].getDouble() == 0) {
                                    setVariable(5081, 0.0, null, null);
                                    setVariable(5083, 0.0, null, null);
                                    setVariable(5121, 0.0, null, null);
                                    setVariable(5123, 0.0, null, null);
                                } else {
                                    setVariable(5081, getGcodeVariable(global_, 12000 + gv[0].getInt()));
                                    setVariable(5083, getGcodeVariable(global_, 12000 + gv[0].getInt()));
                                    setVariable(5121, getGcodeVariable(global_, 13000 + gv[0].getInt()));
                                    setVariable(5123, getGcodeVariable(global_, 13000 + gv[0].getInt()));
                                    getGcodeVariable(global_, 5081).neg();
                                    getGcodeVariable(global_, 5083).neg();
                                    getGcodeVariable(global_, 5121).neg();
                                    getGcodeVariable(global_, 5123).neg();
                                }
                            } else if (getGcodeVariable(global_, 4000 + ggroup_.get(42.0)).getDouble() == 42.0) {
                                if (gv[0].getDouble() == 0) {
                                    setVariable(5081, 0.0, null, null);
                                    setVariable(5083, 0.0, null, null);
                                    setVariable(5121, 0.0, null, null);
                                    setVariable(5123, 0.0, null, null);
                                } else {
                                    setVariable(5081, getGcodeVariable(global_, 12000 + gv[0].getInt()));
                                    setVariable(5083, getGcodeVariable(global_, 12000 + gv[0].getInt()));
                                    setVariable(5121, getGcodeVariable(global_, 13000 + gv[0].getInt()));
                                    setVariable(5123, getGcodeVariable(global_, 13000 + gv[0].getInt()));
                                }
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.E:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_E, gv[0].setNumber(ARG_E));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("E#0");
                                } else {
                                    words.add("E" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[E]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.F:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_F, gv[0].setNumber(ARG_F));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("F#0");
                                } else {
                                    words.add("F" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            iv = gv[0].getInt();
                            intval = getGcodeVariable(global_, 5400).getInt();
                            if (maxfeedrate_.containsKey(intval)) {
                                if (maxfeedrate_.get(intval) < iv) {
                                    iv = maxfeedrate_.get(intval);
                                }
                            }
                            words.add("F" + iv);
                            setVariable(4109, gv[0].setNumber(4109));
                            setVariable(4309, gv[0].setNumber(4309));
                            if (ladderAddress_.containsKey(DesignController.LADDER_F1)) {
                                laddersIoImmediate_.put(DesignController.LADDER_F1, gv[0].getDouble());
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.G:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (!gv[0].isNil()) {
                            if (gmacrocall_.containsKey(gv[0].getDouble())) {
                                // macro call
                                flg.put(FLG.MCALL, true);
                                setVariable(4000, gv[0].setNumber(4000));
                                strval = gmacrocall_.get(gv[0].getDouble());
                                if (strval.startsWith("O")) {
                                    if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                        intval = Integer.parseInt(strval.substring(1));
                                        strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable((double) intval, ARG_PROGRAM, strval, null));
                                    } else {
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                    }
                                } else {
                                    arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                }
                                if (programcallconvert_.containsKey(strval)) {
                                    flg.put(FLG.RECALL, true);
                                    strval = programcallconvert_.get(strval);
                                    words.add("G65");
                                    if (strval.startsWith("O")) {
                                        if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                            words.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                                        } else {
                                            words.add("<" + strval + ">");
                                        }
                                    } else {
                                        words.add("<" + strval + ">");
                                    }
                                }
                            } else {
                                dv = gv[0].getDouble();
                                if (gcodeconvert_.containsKey(dv)) {
                                    for (String code : gcodeconvert_.get(dv).split("\\|")) {
                                        code = code.trim();
                                        if (!code.isEmpty()) {
                                            if (PATTERN_NUMBER.matcher(code).find()) {
                                                dblval = Double.parseDouble(code);
                                                if (dblval >= 0) {
                                                    words.add("G" + DECIMAL_FORMAT_CODE.format(dblval));
                                                }
                                            } else {
                                                words.add(code);
                                            }
                                        }
                                    }
                                } else {
                                    dblval = gv[0].getDouble();
                                    switch ((int) (dblval * 10)) {
                                        case 20:
                                        case 30:
                                            if (option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(17.0)).getDouble() == 17.0) {
                                                    // XY
                                                    intval = getGcodeVariable(global_, 3007).getInt() & 0x0003;
                                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(18.0)).getDouble() == 18.0) {
                                                    // ZX
                                                    intval = getGcodeVariable(global_, 3007).getInt() & 0x0005;
                                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(19.0)).getDouble() == 19.0) {
                                                    // YZ
                                                    intval = getGcodeVariable(global_, 3007).getInt() & 0x0006;
                                                } else {
                                                    writeLog(GcodeInterpreterEnums.ABNORMAL_PLANE_SELECTION.toString(), true);
                                                    return RET_ERROR;
                                                }
                                                if (Integer.bitCount(intval) % 2 == 0) {
                                                    words.add("G" + DECIMAL_FORMAT_CODE.format(dblval));
                                                } else {
                                                    // G2 -> G3, G3 -> G2
                                                    words.add("G" + DECIMAL_FORMAT_CODE.format(5.0 - dblval));
                                                }
                                            } else {
                                                words.add("G" + DECIMAL_FORMAT_CODE.format(dblval));
                                            }
                                            break;
                                        default:
                                            words.add("G" + DECIMAL_FORMAT_CODE.format(dblval));
                                            break;
                                    }
                                }
                                dblval = gv[0].getDouble();
                                if (ggroup_.containsKey(dblval)) {
                                    intval = 4000 + ggroup_.get(dblval);
                                    setVariable(intval, gv[0].setNumber(intval));
                                    intval = 4200 + ggroup_.get(dblval);
                                    setVariable(intval, gv[0].setNumber(intval));
                                } else {
                                    writeLog(GcodeInterpreterEnums.NO_G_GROUP.toString() + "[G" + dblval + "]", true);
                                    return RET_ERROR;
                                }
                                switch ((int) (dblval * 10)) {
                                    case 100: // G10
                                        flg.put(FLG.MODE, true);
                                        break;
                                    case 110: // G11
                                        flg.put(FLG.PARAMETER, false);
                                        break;
                                    case 280: // G28
                                        flg.put(FLG.COORDINATE, true);
                                        break;
                                    case 310: // G31
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.SKIP_FUNCTION, true);
                                        break;
                                    case 650: // G65
                                        flg.put(FLG.MCALL, true);
                                        break;
                                    case 400: // G40
                                        setVariable(5081, 0.0, null, null);
                                        setVariable(5083, 0.0, null, null);
                                        setVariable(5121, 0.0, null, null);
                                        setVariable(5123, 0.0, null, null);
                                        break;
                                    case 490: // G49
                                        setVariable(5082, 0.0, null, null);
                                        setVariable(5122, 0.0, null, null);
                                        break;
                                    case 730: // G73
                                    case 740: // G74
                                    case 760: // G76
                                    case 810: // G81
                                    case 820: // G82
                                    case 830: // G83
                                    case 840: // G84
                                    case 850: // G85
                                    case 860: // G86
                                    case 870: // G87
                                    case 880: // G88
                                    case 890: // G89
                                        flg.put(FLG.CYCLE, true);
                                        setGcodeVariable(argcycle, ARG_V, getGcodeVariable(global_, 5023));
                                        setGcodeVariable(argcycle, ARG_W, getGcodeVariable(global_, 5023));
                                        break;
                                    case 800: // G80
                                        flg.put(FLG.CYCLE, false);
                                        argcycle.clear();
                                        break;
                                    case 660: // G66
                                        flg.put(FLG.MODAL, true);
                                        break;
                                    case 540: // G54
                                    case 541: // G54.1
                                    case 550: // G55
                                    case 560: // G56
                                    case 570: // G57
                                    case 580: // G58
                                    case 590: // G59
                                        flg.put(FLG.COORDINATE, true);
                                        setVariable(4130, 0.0, null, null);
                                        setVariable(4330, 0.0, null, null);
                                        break;
                                    case 501: // G50.1
                                    case 511: // G51.1
                                        flg.put(FLG.MIRROR, true);
                                        break;
                                }
                                if (ladderAddress_.containsKey(DesignController.LADDER_G1)) {
                                    laddersIoImmediate_.put(DesignController.LADDER_G1, dblval);
                                }
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.H:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.CALL) || flg.get(FLG.EXTCALL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_CALL_JUMP, gv[0]);
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("H#0");
                                } else {
                                    words.add("H" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_H, gv[0].setNumber(ARG_H));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("H#0");
                                } else {
                                    words.add("H" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            iv = gv[0].getInt();
                            if (hcodeconvert_.containsKey(iv)) {
                                for (String code : hcodeconvert_.get(iv).split("\\|")) {
                                    code = code.trim();
                                    if (!code.isEmpty()) {
                                        if (PATTERN_NUMBER.matcher(code).find()) {
                                            intval = Integer.parseInt(code);
                                            if (intval >= 0) {
                                                words.add("H" + DECIMAL_FORMAT_CODE.format(intval));
                                            }
                                        } else {
                                            words.add(code);
                                        }
                                    }
                                }
                            } else {
                                words.add("H" + DECIMAL_FORMAT_CODE.format(iv));
                            }
                            setVariable(4111, gv[0].setNumber(4111));
                            setVariable(4311, gv[0].setNumber(4311));
                            if (getGcodeVariable(global_, 4000 + ggroup_.get(43.0)).getDouble() == 43.0) {
                                if (gv[0].getDouble() == 0) {
                                    setVariable(5082, 0.0, null, null);
                                    setVariable(5122, 0.0, null, null);
                                } else {
                                    setVariable(5082, getGcodeVariable(global_, 10000 + gv[0].getInt()));
                                    setVariable(5122, getGcodeVariable(global_, 11000 + gv[0].getInt()));
                                    getGcodeVariable(global_, 5082).neg();
                                    getGcodeVariable(global_, 5122).neg();
                                }
                            } else if (getGcodeVariable(global_, 4000 + ggroup_.get(44.0)).getDouble() == 44.0) {
                                if (gv[0].getDouble() == 0) {
                                    setVariable(5082, 0.0, null, null);
                                    setVariable(5122, 0.0, null, null);
                                } else {
                                    setVariable(5082, getGcodeVariable(global_, 10000 + gv[0].getInt()));
                                    setVariable(5122, getGcodeVariable(global_, 11000 + gv[0].getInt()));
                                }
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.I:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if ((ic < jc) || (ic < kc)) {
                                if (jc < kc) {
                                    ic = kc;
                                } else {
                                    ic = jc;
                                }
                            }
                            intval = ic++ * 3 + ARG_I;
                            if (!gv[0].isNil()) {
                                arglocal.put(intval, gv[0].setNumber(intval));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("I#0");
                                } else {
                                    words.add("I" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if ((getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0) || (getGcodeVariable(global_, 4000 + ggroup_.get(3.0)).getDouble() == 3.0)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.DRAW, true);
                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                    words.add("I" + DECIMAL_FORMAT_VAL.format(-gv[0].getDouble()));
                                } else {
                                    words.add("I" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                                setGcodeVariable(draw_, ARG_I, gv[0]);
                                if (ladderAddress_.containsKey(DesignController.LADDER_I1)) {
                                    laddersIo_.put(DesignController.LADDER_I1, gv[0].getDouble());
                                }
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[I]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.J:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if ((jc < (ic - 1)) || (jc < kc)) {
                                if ((ic - 1) < kc) {
                                    jc = kc;
                                } else {
                                    jc = ic - 1;
                                }
                            }
                            intval = jc++ * 3 + ARG_J;
                            if (!gv[0].isNil()) {
                                arglocal.put(intval, gv[0].setNumber(intval));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("J#0");
                                } else {
                                    words.add("J" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if ((getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0) || (getGcodeVariable(global_, 4000 + ggroup_.get(3.0)).getDouble() == 3.0)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.DRAW, true);
                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                    words.add("J" + DECIMAL_FORMAT_VAL.format(-gv[0].getDouble()));
                                } else {
                                    words.add("J" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                                setGcodeVariable(draw_, ARG_J, gv[0]);
                                if (ladderAddress_.containsKey(DesignController.LADDER_J1)) {
                                    laddersIo_.put(DesignController.LADDER_J1, gv[0].getDouble());
                                }
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[J]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.K:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if ((kc < (ic - 1)) || (kc < (jc - 1))) {
                                if (ic < jc) {
                                    kc = jc - 1;
                                } else {
                                    kc = ic - 1;
                                }
                            }
                            intval = kc++ * 3 + ARG_K;
                            if (!gv[0].isNil()) {
                                arglocal.put(intval, gv[0].setNumber(intval));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("K#0");
                                } else {
                                    words.add("K" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if ((getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0) || (getGcodeVariable(global_, 4000 + ggroup_.get(3.0)).getDouble() == 3.0)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.DRAW, true);
                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                    words.add("K" + DECIMAL_FORMAT_VAL.format(-gv[0].getDouble()));
                                } else {
                                    words.add("K" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                                setGcodeVariable(draw_, ARG_K, gv[0]);
                                if (ladderAddress_.containsKey(DesignController.LADDER_K1)) {
                                    laddersIo_.put(DesignController.LADDER_K1, gv[0].getDouble());
                                }
                            }
                        } else if (flg.get(FLG.CYCLE)) {
                            if (!gv[0].isNil()) {
                                setGcodeVariable(argcycle, ARG_LOOP, gv[0]);
                                words.add("K" + DECIMAL_FORMAT_CODE.format(gv[0].getDouble()));
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[K]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.L:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if ((flg.get(FLG.CALL)) || (flg.get(FLG.EXTCALL)) || (flg.get(FLG.MCALL)) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_LOOP, gv[0]);
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("L#0");
                                } else {
                                    words.add("L" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MODE)) {
                            if ((gv[0].getDouble() == 50.0) || (gv[0].getDouble() == 52.0)) {
                                flg.put(FLG.PARAMETER, true);
                            }
                        } else if (flg.get(FLG.CYCLE)) {
                            if (!gv[0].isNil()) {
                                setGcodeVariable(argcycle, ARG_LOOP, gv[0]);
                                words.add("L" + DECIMAL_FORMAT_CODE.format(gv[0].getDouble()));
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[L]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.M:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_M, gv[0].setNumber(ARG_M));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("M#0");
                                } else {
                                    words.add("M" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            if (mmacrocall_.containsKey(gv[0].getDouble())) {
                                flg.put(FLG.CALL, true);
                                strval = mmacrocall_.get(gv[0].getDouble());
                                if (strval.startsWith("O")) {
                                    if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                        intval = Integer.parseInt(strval.substring(1));
                                        strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable((double) intval, ARG_PROGRAM, strval, null));
                                    } else {
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                    }
                                } else {
                                    arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                }
                                if (programcallconvert_.containsKey(strval)) {
                                    flg.put(FLG.RECALL, true);
                                    strval = programcallconvert_.get(strval);
                                    words.add("M98");
                                    if (strval.startsWith("O")) {
                                        if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                            words.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                                        } else {
                                            words.add("<" + strval + ">");
                                        }
                                    } else {
                                        words.add("<" + strval + ">");
                                    }
                                }
                            } else {
                                if (option_.containsKey(OPT.TOOL_CHANGE_M_CODE)) {
                                    if (gv[0].getInt() == Integer.parseInt(option_.get(OPT.TOOL_CHANGE_M_CODE))) {
                                        gv[1] = getGcodeVariable(global_, 5000);
                                        if (!gv[1].isNil()) {
                                            flg.put(FLG.TOOL_CHANGE, true);
                                            iv = gv[1].getInt();
                                            if (tcodeconvert_.containsKey(iv)) {
                                                for (String code : tcodeconvert_.get(iv).split("\\|")) {
                                                    code = code.trim();
                                                    if (!code.isEmpty()) {
                                                        if (PATTERN_NUMBER.matcher(code).find()) {
                                                            intval = Integer.parseInt(code);
                                                            if (intval >= 0) {
                                                                words.add("T" + DECIMAL_FORMAT_CODE.format(intval));
                                                            }
                                                        } else {
                                                            words.add(code);
                                                        }
                                                    }
                                                }
                                            } else {
                                                words.add("T" + DECIMAL_FORMAT_CODE.format(iv));
                                            }
                                            setVariable(5400, gv[1].setNumber(5400));
                                            setVariable(5000, null, null, null);
                                        }
                                    }
                                }
                                dv = gv[0].getDouble();
                                if (mcodeconvert_.containsKey(dv)) {
                                    for (String code : mcodeconvert_.get(dv).split("\\|")) {
                                        code = code.trim();
                                        if (!code.isEmpty()) {
                                            if (PATTERN_NUMBER.matcher(code).find()) {
                                                dblval = Double.parseDouble(code);
                                                if (dblval >= 0) {
                                                    words.add("M" + DECIMAL_FORMAT_CODE.format(dblval));
                                                }
                                            } else {
                                                words.add(code);
                                            }
                                        }
                                    }
                                } else {
                                    words.add("M" + DECIMAL_FORMAT_CODE.format(gv[0].getDouble()));
                                }
                                switch ((int) (gv[0].getDouble() * 10)) {
                                    case 20:  // M02
                                    case 300: // M30
                                        flg.put(FLG.EXIT, true);
                                        break;
                                    case 980: // M98
                                        flg.put(FLG.CALL, true);
                                        break;
                                    case 990: // M99
                                        flg.put(FLG.RETURN, true);
                                        break;
                                    case 1980: // M198
                                        flg.put(FLG.EXTCALL, true);
                                        break;
                                }
                            }
                            setVariable(4113, gv[0].setNumber(4113));
                            setVariable(4313, gv[0].setNumber(4313));
                            if (ladderAddress_.containsKey(DesignController.LADDER_M1)) {
                                laddersIoImmediate_.put(DesignController.LADDER_M1, gv[0].getDouble());
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.N:
                    if (!flg.get(FLG.SKIP)) {
                        if (flg.get(FLG.PARAMETER)) {
                            // parameter number
                        } else {
                            setVariable(4114, gb.getDblValue(), null, null);
                            setVariable(4314, gb.getDblValue(), null, null);
                            if (ladderAddress_.containsKey(DesignController.LADDER_N1)) {
                                laddersIoImmediate_.put(DesignController.LADDER_N1, gb.getDblValue());
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.O:
                    if (!flg.get(FLG.SKIP)) {
                        if (programnumber_ != null) {
                            if (programnumber_.startsWith("O")) {
                                if (PATTERN_UINT.matcher(programnumber_.substring(1)).find()) {
                                    intval = Integer.parseInt(programnumber_.substring(1));
                                    strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                    if (intval == gb.getIntValue()) {
                                        setVariable(4115, gb.getDblValue(), strval, null);
                                        setVariable(4315, gb.getDblValue(), strval, null);
                                    } else {
                                        writeLog(GcodeInterpreterEnums.INVALID.toString() + "[O:" + strval + "]", true);
                                        return RET_ERROR;
                                    }
                                } else {
                                    strval = gb.getStrValue();
                                    if (programnumber_.equals(strval)) {
                                        setVariable(4115, null, strval, null);
                                        setVariable(4315, null, strval, null);
                                    } else {
                                        writeLog(GcodeInterpreterEnums.INVALID.toString() + "[O:" + strval + "]", true);
                                        return RET_ERROR;
                                    }
                                }
                            } else {
                                strval = gb.getStrValue();
                                if (programnumber_.equals(strval)) {
                                    setVariable(4115, null, strval, null);
                                    setVariable(4315, null, strval, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.INVALID.toString() + "[O:" + strval + "]", true);
                                    return RET_ERROR;
                                }
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString(), true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.P:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.PARAMETER)) {
                            // parameter axis
                        } else if (getGcodeVariable(global_, 4000).getDouble() == 4.0) {
                            if (!gv[0].isNil()) {
                                words.add("P" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                            }
                        } else if ((getGcodeVariable(global_, 4000).getDouble() == 54.0) || (getGcodeVariable(global_, 4000).getDouble() == 54.1)) {
                            setVariable(4000 + ggroup_.get(54.1), 54.1, null, null);
                            setVariable(4130, gv[0].setNumber(4130));
                            setVariable(4330, gv[0].setNumber(4330));
                        } else if (flg.get(FLG.CALL) || flg.get(FLG.EXTCALL) || flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                intval = gv[0].getInt();
                                strval = "O" + Integer.toString(intval);
                                arglocal.put(ARG_PROGRAM, gv[0].setString(strval));
                                if (programcallconvert_.containsKey(strval)) {
                                    flg.put(FLG.RECALL, true);
                                    strval = programcallconvert_.get(strval);
                                    if (flg.get(FLG.CALL)) {
                                        words.add("M98");
                                    } else if (flg.get(FLG.EXTCALL)) {
                                        words.add("M198");
                                    } else if (flg.get(FLG.MCALL)) {
                                        words.add("G65");
                                    } else if (flg.get(FLG.MODAL)) {
                                        words.add("G66");
                                    }
                                    if (strval.startsWith("O")) {
                                        if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                            words.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                                        } else {
                                            words.add("<" + strval + ">");
                                        }
                                    } else {
                                        words.add("<" + strval + ">");
                                    }
                                }
                            } else {
                                writeLog(GcodeInterpreterEnums.INVALID.toString() + "[P]", true);
                                return RET_ERROR;
                            }
                        } else if (flg.get(FLG.PARAMETER)) {
                            // parameter value
                        } else if (flg.get(FLG.RETURN)) {
                            retval = gv[0].getInt();
                        } else if (flg.get(FLG.CYCLE)) {
                            if (!gv[0].isNil()) {
                                setGcodeVariable(argcycle, ARG_P, gv[0]);
                                words.add("P" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[P]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.Q:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.PARAMETER)) {
                            // parameter bit
                        } else if (flg.get(FLG.CALL) || flg.get(FLG.EXTCALL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_CALL_JUMP, gv[0]);
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("Q#0");
                                } else {
                                    words.add("Q" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_Q, gv[0].setNumber(ARG_Q));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("Q#0");
                                } else {
                                    words.add("Q" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.CYCLE)) {
                            if (!gv[0].isNil()) {
                                argcycle.put(ARG_Q, gv[0].setNumber(ARG_Q));
                                words.add("Q" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[Q]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.R:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.PARAMETER)) {
                            // parameter value
                        } else if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_R, gv[0].setNumber(ARG_R));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("R#0");
                                } else {
                                    words.add("R" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if ((flg.get(FLG.CYCLE)) || (getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0) || (getGcodeVariable(global_, 4000 + ggroup_.get(3.0)).getDouble() == 3.0)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                setGcodeVariable(argcycle, ARG_R, gv[0]);
                                words.add("R" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                setGcodeVariable(draw_, ARG_R, gv[0]);
                                if (ladderAddress_.containsKey(DesignController.LADDER_R1)) {
                                    laddersIo_.put(DesignController.LADDER_R1, gv[0].getDouble());
                                }
                            }
                        } else {
                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[R]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.S:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_S, gv[0].setNumber(ARG_S));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("S#0");
                                } else {
                                    words.add("S" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            iv = gv[0].getInt();
                            intval = getGcodeVariable(global_, 5400).getInt();
                            if (maxrevolution_.containsKey(intval)) {
                                if (maxrevolution_.get(intval) < iv) {
                                    iv = maxrevolution_.get(intval);
                                }
                            }
                            words.add("S" + iv);
                            setVariable(4119, gv[0].setNumber(4119));
                            setVariable(4319, gv[0].setNumber(4319));
                            if (ladderAddress_.containsKey(DesignController.LADDER_S1)) {
                                laddersIoImmediate_.put(DesignController.LADDER_S1, gv[0].getDouble());
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.T:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_T, gv[0].setNumber(ARG_T));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("T#0");
                                } else {
                                    words.add("T" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (!gv[0].isNil()) {
                            if (option_.containsKey(OPT.TOOL_CHANGE_M_CODE)) {
                                setVariable(5000, gv[0].setNumber(5000));
                            } else {
                                flg.put(FLG.TOOL_CHANGE, true);
                                iv = gv[0].getInt();
                                if (tcodeconvert_.containsKey(iv)) {
                                    for (String code : tcodeconvert_.get(iv).split("\\|")) {
                                        code = code.trim();
                                        if (!code.isEmpty()) {
                                            if (PATTERN_NUMBER.matcher(code).find()) {
                                                intval = Integer.parseInt(code);
                                                if (intval >= 0) {
                                                    words.add("T" + DECIMAL_FORMAT_CODE.format(intval));
                                                }
                                            } else {
                                                words.add(code);
                                            }
                                        }
                                    }
                                } else {
                                    words.add("T" + DECIMAL_FORMAT_CODE.format(iv));
                                }
                                setVariable(5400, gv[0].setNumber(5400));
                            }
                            setVariable(4107, null, null, null);
                            setVariable(4109, null, null, null);
                            setVariable(4111, null, null, null);
                            setVariable(4119, null, null, null);
                            setVariable(4120, gv[0].setNumber(4120));
                            setVariable(4307, null, null, null);
                            setVariable(4309, null, null, null);
                            setVariable(4311, null, null, null);
                            setVariable(4319, null, null, null);
                            setVariable(4320, gv[0].setNumber(4320));
                            if (ladderAddress_.containsKey(DesignController.LADDER_T1)) {
                                laddersIoImmediate_.put(DesignController.LADDER_T1, gv[0].getDouble());
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.U:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_U, gv[0].setNumber(ARG_U));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("U#0");
                                } else {
                                    words.add("U" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xffbf), null, null);
                                    setVariable(5137, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0040), null, null);
                                    setVariable(5137, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[U]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("U" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("U" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5027, getGcodeVariable(global_, 5167).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_U1)) {
                                            laddersIo_.put(DesignController.LADDER_U1, getGcodeVariable(global_, 5027).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5127).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 6987 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5127).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 5207 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                            setVariable(5217, getGcodeVariable(global_, 5027).getDouble() - dblval - (getGcodeVariable(global_, 5137).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5217, getGcodeVariable(global_, 5027).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5167).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_U, dblval, null, null);
                                                } else {
                                                    setVariable(5027, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_U1)) {
                                                        laddersIo_.put(DesignController.LADDER_U1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_U)) {
                                                    dblval -= getGcodeVariable(global_, 5167).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5167).getDouble() + getGcodeVariable(global_, 5127).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 6987 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5217).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5167).getDouble() + getGcodeVariable(global_, 5127).getDouble() + getGcodeVariable(global_, 5207).getDouble() + getGcodeVariable(global_, 5207 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5217).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                                    dblval += getGcodeVariable(global_, 5137).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_U, dblval, null, null);
                                                } else {
                                                    setVariable(5027, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_U1)) {
                                                        laddersIo_.put(DesignController.LADDER_U1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_U)) {
                                                    dblval -= getGcodeVariable(global_, 5167).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_U)) {
                                                    dblval -= getGcodeVariable(global_, 5207).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_U)) {
                                                    dblval -= getGcodeVariable(global_, 5217).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5127).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                                        dblval -= getGcodeVariable(global_, 5137).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_U)) {
                                                                dblval -= getGcodeVariable(global_, 5227).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_U)) {
                                                                dblval -= getGcodeVariable(global_, 5247).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_U)) {
                                                                dblval -= getGcodeVariable(global_, 5267).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_U)) {
                                                                dblval -= getGcodeVariable(global_, 5287).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_U)) {
                                                                dblval -= getGcodeVariable(global_, 5307).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_U)) {
                                                                dblval -= getGcodeVariable(global_, 5327).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("U" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_U, dblval, null, null);
                                                } else {
                                                    setVariable(5027, getGcodeVariable(global_, 5027).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_U1)) {
                                                        laddersIo_.put(DesignController.LADDER_U1, getGcodeVariable(global_, 5027).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0040) == 0x0040) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("U" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.V:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_V, gv[0].setNumber(ARG_V));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("V#0");
                                } else {
                                    words.add("V" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xff7f), null, null);
                                    setVariable(5138, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0080), null, null);
                                    setVariable(5138, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[V]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("V" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("V" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5028, getGcodeVariable(global_, 5168).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_V1)) {
                                            laddersIo_.put(DesignController.LADDER_V1, getGcodeVariable(global_, 5028).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5128).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 6988 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5128).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 5208 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                            setVariable(5218, getGcodeVariable(global_, 5028).getDouble() - dblval - (getGcodeVariable(global_, 5138).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5218, getGcodeVariable(global_, 5028).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5168).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_V, dblval, null, null);
                                                } else {
                                                    setVariable(5028, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_V1)) {
                                                        laddersIo_.put(DesignController.LADDER_V1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_V)) {
                                                    dblval -= getGcodeVariable(global_, 5168).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5168).getDouble() + getGcodeVariable(global_, 5128).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 6988 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5218).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5168).getDouble() + getGcodeVariable(global_, 5128).getDouble() + getGcodeVariable(global_, 5208).getDouble() + getGcodeVariable(global_, 5208 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5218).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                                    dblval += getGcodeVariable(global_, 5138).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_V, dblval, null, null);
                                                } else {
                                                    setVariable(5028, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_V1)) {
                                                        laddersIo_.put(DesignController.LADDER_V1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_V)) {
                                                    dblval -= getGcodeVariable(global_, 5168).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_V)) {
                                                    dblval -= getGcodeVariable(global_, 5208).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_V)) {
                                                    dblval -= getGcodeVariable(global_, 5218).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5128).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                                        dblval -= getGcodeVariable(global_, 5138).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_V)) {
                                                                dblval -= getGcodeVariable(global_, 5228).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_V)) {
                                                                dblval -= getGcodeVariable(global_, 5248).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_V)) {
                                                                dblval -= getGcodeVariable(global_, 5268).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_V)) {
                                                                dblval -= getGcodeVariable(global_, 5288).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_V)) {
                                                                dblval -= getGcodeVariable(global_, 5308).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_V)) {
                                                                dblval -= getGcodeVariable(global_, 5328).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("V" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_V, dblval, null, null);
                                                } else {
                                                    setVariable(5028, getGcodeVariable(global_, 5028).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_V1)) {
                                                        laddersIo_.put(DesignController.LADDER_V1, getGcodeVariable(global_, 5028).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0080) == 0x0080) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("V" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.W:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_W, gv[0].setNumber(ARG_W));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("W#0");
                                } else {
                                    words.add("W" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xfeff), null, null);
                                    setVariable(5139, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0100), null, null);
                                    setVariable(5139, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[W]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("W" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("W" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5029, getGcodeVariable(global_, 5169).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_W1)) {
                                            laddersIo_.put(DesignController.LADDER_W1, getGcodeVariable(global_, 5029).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5129).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 6989 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5129).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 5209 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                            setVariable(5219, getGcodeVariable(global_, 5029).getDouble() - dblval - (getGcodeVariable(global_, 5139).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5219, getGcodeVariable(global_, 5029).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5169).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_W, dblval, null, null);
                                                } else {
                                                    setVariable(5029, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_W1)) {
                                                        laddersIo_.put(DesignController.LADDER_W1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_W)) {
                                                    dblval -= getGcodeVariable(global_, 5169).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5169).getDouble() + getGcodeVariable(global_, 5129).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 6989 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5219).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5169).getDouble() + getGcodeVariable(global_, 5129).getDouble() + getGcodeVariable(global_, 5209).getDouble() + getGcodeVariable(global_, 5209 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5219).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                                    dblval += getGcodeVariable(global_, 5139).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_W, dblval, null, null);
                                                } else {
                                                    setVariable(5029, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_W1)) {
                                                        laddersIo_.put(DesignController.LADDER_W1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_W)) {
                                                    dblval -= getGcodeVariable(global_, 5169).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_W)) {
                                                    dblval -= getGcodeVariable(global_, 5209).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_W)) {
                                                    dblval -= getGcodeVariable(global_, 5219).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5129).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                                        dblval -= getGcodeVariable(global_, 5139).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_W)) {
                                                                dblval -= getGcodeVariable(global_, 5229).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_W)) {
                                                                dblval -= getGcodeVariable(global_, 5249).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_W)) {
                                                                dblval -= getGcodeVariable(global_, 5269).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_W)) {
                                                                dblval -= getGcodeVariable(global_, 5289).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_W)) {
                                                                dblval -= getGcodeVariable(global_, 5309).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_W)) {
                                                                dblval -= getGcodeVariable(global_, 5329).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("W" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_W, dblval, null, null);
                                                } else {
                                                    setVariable(5029, getGcodeVariable(global_, 5029).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_W1)) {
                                                        laddersIo_.put(DesignController.LADDER_W1, getGcodeVariable(global_, 5029).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0100) == 0x0100) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("W" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.X:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_X, gv[0].setNumber(ARG_X));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("X#0");
                                } else {
                                    words.add("X" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xfffe), null, null);
                                    setVariable(5131, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0001), null, null);
                                    setVariable(5131, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[X]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("X" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 40:
                                    // Dwell
                                    if (!gv[0].isNil()) {
                                        words.add("X" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                    }
                                    break;
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("X" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5021, getGcodeVariable(global_, 5161).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                            laddersIo_.put(DesignController.LADDER_X1, getGcodeVariable(global_, 5021).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5121).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 6981 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5121).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 5201 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                            setVariable(5211, getGcodeVariable(global_, 5021).getDouble() - dblval - (getGcodeVariable(global_, 5131).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5211, getGcodeVariable(global_, 5021).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5161).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_X, dblval, null, null);
                                                } else {
                                                    setVariable(5021, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                                        laddersIo_.put(DesignController.LADDER_X1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_X)) {
                                                    dblval -= getGcodeVariable(global_, 5161).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5161).getDouble() + getGcodeVariable(global_, 5121).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 6981 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5211).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5161).getDouble() + getGcodeVariable(global_, 5121).getDouble() + getGcodeVariable(global_, 5201).getDouble() + getGcodeVariable(global_, 5201 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5211).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                                    dblval += getGcodeVariable(global_, 5131).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_X, dblval, null, null);
                                                } else {
                                                    setVariable(5021, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                                        laddersIo_.put(DesignController.LADDER_X1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_X)) {
                                                    dblval -= getGcodeVariable(global_, 5161).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_X)) {
                                                    dblval -= getGcodeVariable(global_, 5201).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_X)) {
                                                    dblval -= getGcodeVariable(global_, 5211).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5121).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                                        dblval -= getGcodeVariable(global_, 5131).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_X)) {
                                                                dblval -= getGcodeVariable(global_, 5221).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_X)) {
                                                                dblval -= getGcodeVariable(global_, 5241).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_X)) {
                                                                dblval -= getGcodeVariable(global_, 5261).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_X)) {
                                                                dblval -= getGcodeVariable(global_, 5281).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_X)) {
                                                                dblval -= getGcodeVariable(global_, 5301).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_X)) {
                                                                dblval -= getGcodeVariable(global_, 5321).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("X" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_X, dblval, null, null);
                                                } else {
                                                    setVariable(5021, getGcodeVariable(global_, 5021).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_X1)) {
                                                        laddersIo_.put(DesignController.LADDER_X1, getGcodeVariable(global_, 5021).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("X" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.Y:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_Y, gv[0].setNumber(ARG_Y));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("Y#0");
                                } else {
                                    words.add("Y" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xfffd), null, null);
                                    setVariable(5132, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0002), null, null);
                                    setVariable(5132, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[Y]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("Y" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("Y" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5022, getGcodeVariable(global_, 5162).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                            laddersIo_.put(DesignController.LADDER_Y1, getGcodeVariable(global_, 5022).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 6982 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 5202 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                            setVariable(5212, getGcodeVariable(global_, 5022).getDouble() - dblval - (getGcodeVariable(global_, 5132).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5212, getGcodeVariable(global_, 5022).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5162).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Y, dblval, null, null);
                                                } else {
                                                    setVariable(5022, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                                        laddersIo_.put(DesignController.LADDER_Y1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_Y)) {
                                                    dblval -= getGcodeVariable(global_, 5162).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5162).getDouble() + getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 6982 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5212).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5162).getDouble() + getGcodeVariable(global_, 5123).getDouble() + getGcodeVariable(global_, 5202).getDouble() + getGcodeVariable(global_, 5202 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5212).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                                    dblval += getGcodeVariable(global_, 5132).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Y, dblval, null, null);
                                                } else {
                                                    setVariable(5022, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                                        laddersIo_.put(DesignController.LADDER_Y1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_Y)) {
                                                    dblval -= getGcodeVariable(global_, 5162).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_Y)) {
                                                    dblval -= getGcodeVariable(global_, 5202).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_Y)) {
                                                    dblval -= getGcodeVariable(global_, 5212).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5122).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                                        dblval -= getGcodeVariable(global_, 5132).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5222).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5242).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5262).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5282).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5302).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_Y)) {
                                                                dblval -= getGcodeVariable(global_, 5322).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("Y" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Y, dblval, null, null);
                                                } else {
                                                    setVariable(5022, getGcodeVariable(global_, 5022).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Y1)) {
                                                        laddersIo_.put(DesignController.LADDER_Y1, getGcodeVariable(global_, 5022).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("Y" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.Z:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        if (flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            // macro call
                            if (!gv[0].isNil()) {
                                arglocal.put(ARG_Z, gv[0].setNumber(ARG_Z));
                            }
                            if (flg.get(FLG.RECALL)) {
                                if (gv[0].isNil()) {
                                    words.add("Z#0");
                                } else {
                                    words.add("Z" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                }
                            }
                        } else if (flg.get(FLG.MIRROR)) {
                            if (!gv[0].isNil()) {
                                flg.put(FLG.COORDINATE, true);
                                dblval = gv[0].getDouble();
                                if (getGcodeVariable(global_, 4000 + ggroup_.get(50.1)).getDouble() == 50.1) {
                                    // Programmable mirror image cancellation
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() & 0xfffb), null, null);
                                    setVariable(5133, 0.0, null, null);
                                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(51.1)).getDouble() == 51.1) {
                                    // Programmable mirror image
                                    setVariable(3007, (double) (getGcodeVariable(global_, 3007).getInt() | 0x0004), null, null);
                                    setVariable(5133, dblval * 2, null, null);
                                } else {
                                    writeLog(GcodeInterpreterEnums.MIRROR_CODE_ERROR.toString() + "[Z]", true);
                                    return RET_ERROR;
                                }
                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                    words.add("Z" + DECIMAL_FORMAT_VAL.format(dblval));
                                }
                            }
                        } else {
                            switch ((int) (getGcodeVariable(global_, 4000).getDouble() * 10)) {
                                case 280:
                                    // Automatic return to reference point
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        flg.put(FLG.DRAW, true);
                                        words.add("Z" + DECIMAL_FORMAT_VAL.format(gv[0].getDouble()));
                                        setVariable(5023, getGcodeVariable(global_, 5163).getDouble(), null, null);
                                        if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                            laddersIo_.put(DesignController.LADDER_Z1, getGcodeVariable(global_, 5023).getDouble());
                                        }
                                    }
                                    break;
                                case 920:
                                    // Setting of work coordinate system
                                    if (!gv[0].isNil()) {
                                        flg.put(FLG.COORDINATE, true);
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                            dblval = getGcodeVariable(global_, 5122).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 6983 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble();
                                        } else {
                                            dblval = getGcodeVariable(global_, 5122).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 5203 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble();
                                        }
                                        if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                            setVariable(5213, getGcodeVariable(global_, 5023).getDouble() - dblval - (getGcodeVariable(global_, 5133).getDouble() - gv[0].getDouble()), null, null);
                                            writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G92]", true);
                                            return RET_ERROR;
                                        } else {
                                            setVariable(5213, getGcodeVariable(global_, 5023).getDouble() - dblval - gv[0].getDouble(), null, null);
                                        }
                                    }
                                    break;
                                default:
                                    if (!gv[0].isNil()) {
                                        if (getGcodeVariable(global_, 4000 + ggroup_.get(90.0)).getDouble() == 90.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() == 53.0) {
                                                dblval = getGcodeVariable(global_, 5163).getDouble();
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                                    writeLog(GcodeInterpreterEnums.DISABLE_MIRROR_AXIS.toString() + "[G53]", true);
                                                    return RET_ERROR;
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Z, dblval, null, null);
                                                } else {
                                                    setVariable(5023, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                                        laddersIo_.put(DesignController.LADDER_Z1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_Z)) {
                                                    dblval -= getGcodeVariable(global_, 5163).getDouble();
                                                }
                                            } else {
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                    dblval = getGcodeVariable(global_, 5163).getDouble() + getGcodeVariable(global_, 5122).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 6983 + getGcodeVariable(global_, 4130).getInt() * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                                                } else {
                                                    dblval = getGcodeVariable(global_, 5163).getDouble() + getGcodeVariable(global_, 5122).getDouble() + getGcodeVariable(global_, 5203).getDouble() + getGcodeVariable(global_, 5203 + (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt() - 53) * 20).getDouble() + getGcodeVariable(global_, 5213).getDouble();
                                                }
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                                    dblval += getGcodeVariable(global_, 5133).getDouble() - gv[0].getDouble();
                                                } else {
                                                    dblval += gv[0].getDouble();
                                                }
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Z, dblval, null, null);
                                                } else {
                                                    setVariable(5023, dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                                        laddersIo_.put(DesignController.LADDER_Z1, dblval);
                                                    }
                                                }
                                                if (!option_.containsKey(OPT.ORIGIN_MACHINE_Z)) {
                                                    dblval -= getGcodeVariable(global_, 5163).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_EXT_Z)) {
                                                    dblval -= getGcodeVariable(global_, 5203).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_G92_Z)) {
                                                    dblval -= getGcodeVariable(global_, 5213).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_TOOL)) {
                                                    dblval -= getGcodeVariable(global_, 5123).getDouble();
                                                }
                                                if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                                        dblval -= getGcodeVariable(global_, 5133).getDouble() - (gv[0].getDouble() * 2);
                                                    }
                                                }
                                                if (getGcodeVariable(global_, 4000 + ggroup_.get(54.1)).getDouble() == 54.1) {
                                                } else {
                                                    switch (getGcodeVariable(global_, 4000 + ggroup_.get(54.0)).getInt()) {
                                                        case 54:
                                                            if (!option_.containsKey(OPT.COORDINATE_G54_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5223).getDouble();
                                                            }
                                                            break;
                                                        case 55:
                                                            if (!option_.containsKey(OPT.COORDINATE_G55_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5243).getDouble();
                                                            }
                                                            break;
                                                        case 56:
                                                            if (!option_.containsKey(OPT.COORDINATE_G56_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5263).getDouble();
                                                            }
                                                            break;
                                                        case 57:
                                                            if (!option_.containsKey(OPT.COORDINATE_G57_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5283).getDouble();
                                                            }
                                                            break;
                                                        case 58:
                                                            if (!option_.containsKey(OPT.COORDINATE_G58_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5303).getDouble();
                                                            }
                                                            break;
                                                        case 59:
                                                            if (!option_.containsKey(OPT.COORDINATE_G59_Z)) {
                                                                dblval -= getGcodeVariable(global_, 5323).getDouble();
                                                            }
                                                            break;
                                                    }
                                                }
                                            }
                                            words.add("Z" + DECIMAL_FORMAT_VAL.format(dblval));
                                        } else if (getGcodeVariable(global_, 4000 + ggroup_.get(91.0)).getDouble() == 91.0) {
                                            flg.put(FLG.COORDINATE, true);
                                            flg.put(FLG.AXIS_MOVE, true);
                                            flg.put(FLG.DRAW, true);
                                            dblval = gv[0].getDouble();
                                            if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                                dblval = -dblval;
                                            }
                                            if (getGcodeVariable(global_, 4000 + ggroup_.get(53.0)).getDouble() != 53.0) {
                                                if (flg.get(FLG.CYCLE)) {
                                                    setGcodeVariable(argcycle, ARG_Z, dblval, null, null);
                                                } else {
                                                    setVariable(5023, getGcodeVariable(global_, 5023).getDouble() + dblval, null, null);
                                                    if (ladderAddress_.containsKey(DesignController.LADDER_Z1)) {
                                                        laddersIo_.put(DesignController.LADDER_Z1, getGcodeVariable(global_, 5023).getDouble());
                                                    }
                                                }
                                            }
                                            if (!option_.containsKey(OPT.COORDINATE_MIRROR)) {
                                                if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                                                    dblval = -dblval;
                                                }
                                            }
                                            words.add("Z" + DECIMAL_FORMAT_VAL.format(dblval));
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.EQUALS:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        gv[1] = stack.pop();
                        intval = gv[1].getNumber();
                        if (intval <= 0) {
                            writeLog(GcodeInterpreterEnums.VARIABLE_ASSIGNMENT_BAN.toString() + "[" + intval + "]", true);
                            return RET_ERROR;
                        } else if (intval == 3000) {
                            flg.put(FLG.ALARM, true);
                            words.add("#3000=" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                        } else if (intval == 3003) {
                            if (!option_.containsKey(OPT.DISABLE_3003)) {
                                words.add("#3003=" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                            }
                        } else if (intval == 3004) {
                            if (!option_.containsKey(OPT.DISABLE_3004)) {
                                words.add("#3004=" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                            }
                        } else if (intval == 3006) {
                            flg.put(FLG.STOP, true);
                            if (option_.containsKey(OPT.REPLACE_3006_M0)) {
                                words.add(option_.get(OPT.REPLACE_3006_M0));
                            } else {
                                words.add("#3006=" + DECIMAL_FORMAT_CODE.format(gv[0].getInt()));
                            }
                        } else if ((5021 <= intval) && (intval <= 5023)) {
                            // For forced machine coordinates change
                            flg.put(FLG.COORDINATE, true);
                        } else if ((5201 <= intval) && (intval <= 7999)) {
                            // Work coordinate system
                            flg.put(FLG.COORDINATE, true);
                        }
                        setVariable(intval, gv[0].setNumber(intval));
                    }
                    break;
                case GcodeParserConstants.NUMBER:
                case GcodeParserConstants.VARIABLE_NUMBER:
                    if (!flg.get(FLG.SKIP)) {
                        stack.push(new GcodeVariable(gb.getDblValue(), 0, Double.toString(gb.getDblValue()), null));
                    }
                    break;
                case GcodeParserConstants.FLOAT:
                case GcodeParserConstants.VARIABLE_FLOAT:
                    if (!flg.get(FLG.SKIP)) {
                        stack.push(new GcodeVariable(gb.getDblValue(), 0, Double.toString(gb.getDblValue()), null));
                    }
                    break;
                case GcodeParserConstants.SHARP:
                case GcodeParserConstants.VARIABLE_SHARP:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(new GcodeVariable(getVariable(gv[0].getInt())));
                    }
                    break;
                case GcodeParserConstants.AT:
                case GcodeParserConstants.VARIABLE_AT:
                    // 予定 拡張機能用
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        stack.push(new GcodeVariable(getVariable(gv[0].getInt())));
                    }
                    break;
                case GcodeParserConstants.SETVN:
                    if (!flg.get(FLG.SKIP)) {
                        intval = gb.getIntValue() + stack.size();
                        while (!stack.isEmpty()) {
                            gv[0] = stack.pop();
                            intval--;
                            if (name_.containsKey(gv[0].getName())) {
                                if (intval < name_.get(gv[0].getName())) {
                                    name_.put(gv[0].getName(), intval);
                                }
                            } else {
                                name_.put(gv[0].getName(), intval);
                            }
                        }
                    }
                    break;
                case GcodeParserConstants.SETVN_VARIABLE:
                    if (!flg.get(FLG.SKIP)) {
                        stack.push(new GcodeVariable(gb.getStrValue()));
                    }
                    break;
                case GcodeParserConstants.VARIABLE_STRING:
                    if (!flg.get(FLG.SKIP)) {
                        strval = gb.getStrValue();
                        if (name_.containsKey(strval)) {
                            stack.push(new GcodeVariable((double) name_.get(strval), 0, null, null));
                        } else {
                            writeLog(GcodeInterpreterEnums.NO_VARIABLE.toString() + "[" + strval + "]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.VARIABLE_STRING_ARRAY:
                    if (!flg.get(FLG.SKIP)) {
                        gv[0] = stack.pop();
                        strval = gb.getStrValue() + "[" + gv[0].getInt() + "]";
                        if (name_.containsKey(strval)) {
                            stack.push(new GcodeVariable((double) name_.get(strval), 0, null, null));
                        } else {
                            writeLog(GcodeInterpreterEnums.NO_VARIABLE.toString() + "[" + strval + "]", true);
                            return RET_ERROR;
                        }
                    }
                    break;
                case GcodeParserConstants.FILENAME:
                    if (!flg.get(FLG.SKIP)) {
                        strval = gb.getStrValue().trim();
                        if (flg.get(FLG.CALL) || flg.get(FLG.EXTCALL) || flg.get(FLG.MCALL) || flg.get(FLG.MODAL)) {
                            if (!strval.isEmpty()) {
                                if (strval.startsWith("O")) {
                                    if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                        intval = Integer.parseInt(strval.substring(1));
                                        strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable((double) intval, ARG_PROGRAM, strval, null));
                                    } else {
                                        arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                    }
                                } else {
                                    arglocal.put(ARG_PROGRAM, new GcodeVariable(null, ARG_PROGRAM, strval, null));
                                }
                                if (programcallconvert_.containsKey(strval)) {
                                    flg.put(FLG.RECALL, true);
                                    strval = programcallconvert_.get(strval);
                                    if (flg.get(FLG.CALL)) {
                                        words.add("M98");
                                    } else if (flg.get(FLG.EXTCALL)) {
                                        words.add("M198");
                                    } else if (flg.get(FLG.MCALL)) {
                                        words.add("G65");
                                    } else if (flg.get(FLG.MODAL)) {
                                        words.add("G66");
                                    }
                                    if (strval.startsWith("O")) {
                                        if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                            words.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                                        } else {
                                            words.add("<" + strval + ">");
                                        }
                                    } else {
                                        words.add("<" + strval + ">");
                                    }
                                }
                            } else {
                                writeLog(GcodeInterpreterEnums.INVALID.toString() + "[<FILENAME>]", true);
                                return RET_ERROR;
                            }
                        } else if (flg.get(FLG.CYCLE)) {
                            if (!strval.isEmpty()) {
                                if (strval.startsWith("O")) {
                                    if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                        intval = Integer.parseInt(strval.substring(1));
                                        strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                        setGcodeVariable(argcycle, ARG_P, (double) intval, strval, null);
                                        words.add("P" + DECIMAL_FORMAT_CODE.format(intval));
                                    } else {
                                        setGcodeVariable(argcycle, ARG_P, null, strval, null);
                                        words.add("<" + strval + ">");
                                    }
                                } else {
                                    setGcodeVariable(argcycle, ARG_P, null, strval, null);
                                    words.add("<" + strval + ">");
                                }
                            }
                        } else {
                            if (!strval.isEmpty()) {
                                if (programnumber_ != null) {
                                    if (strval.startsWith("O")) {
                                        if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                                            intval = Integer.parseInt(strval.substring(1));
                                            strval = "O" + DECIMAL_FORMAT_CODE.format(intval);
                                            if (programnumber_.equals(strval)) {
                                                setVariable(4115, (double) intval, strval, null);
                                                setVariable(4315, (double) intval, strval, null);
                                            } else {
                                                writeLog(GcodeInterpreterEnums.INVALID.toString() + "[<FILENAME>]", true);
                                                return RET_ERROR;
                                            }
                                        } else {
                                            if (programnumber_.equals(strval)) {
                                                setVariable(4115, null, strval, null);
                                                setVariable(4315, null, strval, null);
                                            } else {
                                                writeLog(GcodeInterpreterEnums.INVALID.toString() + "[<FILENAME>]", true);
                                                return RET_ERROR;
                                            }
                                        }
                                    } else {
                                        if (programnumber_.equals(strval)) {
                                            setVariable(4115, null, strval, null);
                                            setVariable(4315, null, strval, null);
                                        } else {
                                            writeLog(GcodeInterpreterEnums.INVALID.toString() + "[<FILENAME>]", true);
                                            return RET_ERROR;
                                        }
                                    }
                                } else {
                                    writeLog(GcodeInterpreterEnums.NO_PROGRAM_NUMBER.toString(), true);
                                    return RET_ERROR;
                                }
                            }
                        }
                    }
                    break;
            }
            pc++;
        }
        return retval;
    }

    /**
     *
     * @param block
     */
    public void serialOutput(String block) {
        if (!option_.containsKey(OPT.DISABLE_OUTPUT) && !block.isEmpty()) {
            if ((serial_ != null) && (serial_.isOwned())) {
                serial_.offer(block, foreline_);
            } else if ((webEditor_ != null) && (foreline_ > -1)) {
                Platform.runLater(() -> {
                    if ((webEditor_ != null) && (foreline_ > -1)) {
                        webEditor_.gotoLine(foreline_, 0, true, false);
                    }
                });
            }
        }
    }

    /**
     *
     * @param block
     * @param line
     * @param state
     */
    public void serialOutput(String block, int line, STATE state) {
        if (!option_.containsKey(OPT.DISABLE_OUTPUT) && ((!block.isEmpty()) || (state == STATE.NONE))) {

            if (((block.length() + serialEob_.length()) % 2) == 1) {
                block += " ";
            }

            if ((serial_ != null) && (serial_.isOwned())) {
                serial_.offer(block + serialEob_, line);
            } else if ((webEditor_ != null) && (line > -1)) {
                Platform.runLater(() -> {
                    if ((webEditor_ != null) && (line > -1)) {
                        webEditor_.gotoLine(line, 0, true, false);
                    }
                });
            }
        }
    }

    /**
     *
     * @param words
     * @param line
     * @param state
     */
    public void serialOutput(List<String> words, int line, STATE state) {
        if (!option_.containsKey(OPT.DISABLE_OUTPUT) && ((!words.isEmpty()) || (state == STATE.NONE))) {
            StringBuilder block = new StringBuilder();
            double dblval;
            int intval;

            if (option_.containsKey(OPT.RS274NGC)) {
                intval = words.indexOf("G4");
                if (intval > -1) {
                    if (words.size() == (intval + 1)) {
                        words.add("P0");
                    } else if (words.get(intval + 1).startsWith("X")) {
                        dblval = Double.parseDouble(words.get(intval + 1).substring(1));
                        words.set(intval + 1, "P" + DECIMAL_FORMAT_VAL.format(dblval));
                    } else if (words.get(intval + 1).startsWith("P")) {
                        dblval = Double.parseDouble(words.get(intval + 1).substring(1));
                        words.set(intval + 1, "P" + DECIMAL_FORMAT_VAL.format(dblval / 1000.0));
                    }
                }
            }
            words.stream().forEach((word) -> {
                block.append(word);
            });

            if (((block.length() + serialEob_.length()) % 2) == 1) {
                block.append(" ");
            }

            if ((serial_ != null) && (serial_.isOwned())) {
                serial_.offer(block + serialEob_, line);
            } else if ((webEditor_ != null) && (line > -1)) {
                Platform.runLater(() -> {
                    if ((webEditor_ != null) && (line > -1)) {
                        webEditor_.gotoLine(line, 0, true, false);
                    }
                });
            }
        }
    }

    /**
     *
     * @param block
     * @param state
     * @param debug
     */
    public void fileOutput(String block, STATE state, boolean debug) {
        if (!option_.containsKey(OPT.DISABLE_OUTPUT) && ((!block.isEmpty()) || (state == STATE.NONE))) {

            if (((block.length() + serialEob_.length()) % 2) == 1) {
                block += " ";
            }

            try {
                if (ngcBufferedWriter_ != null) {
                    ngcBufferedWriter_.append(block).append("\r\n");
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
            }

            try {
                if (debug) {
                    if (debugBufferedWriter_ != null) {
                        if (option_.containsKey(OPT.DEBUG_JSON)) {
                            debugBufferedWriter_.append("{\"out\":\"").append(block).append("\"}").append("\r\n");
                        } else {
                            debugBufferedWriter_.append(block).append("\r\n");
                        }
                    }
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
            }
        }
    }

    /**
     *
     * @param words
     * @param state
     * @param debug
     */
    public void fileOutput(List<String> words, STATE state, boolean debug) {
        if (!option_.containsKey(OPT.DISABLE_OUTPUT) && ((!words.isEmpty()) || (state == STATE.NONE))) {
            StringBuilder block = new StringBuilder();
            double dblval;
            int intval;

            if (option_.containsKey(OPT.RS274NGC)) {
                intval = words.indexOf("G4");
                if (intval > -1) {
                    if (words.size() == (intval + 1)) {
                        words.add("P0");
                    } else if (words.get(intval + 1).startsWith("X")) {
                        dblval = Double.parseDouble(words.get(intval + 1).substring(1));
                        words.set(intval + 1, "P" + DECIMAL_FORMAT_VAL.format(dblval));
                    } else if (words.get(intval + 1).startsWith("P")) {
                        dblval = Double.parseDouble(words.get(intval + 1).substring(1));
                        words.set(intval + 1, "P" + DECIMAL_FORMAT_VAL.format(dblval / 1000.0));
                    }
                }
            }
            words.stream().forEach((word) -> {
                block.append(word);
            });

            if (((block.length() + serialEob_.length()) % 2) == 1) {
                block.append(" ");
            }

            try {
                if (ngcBufferedWriter_ != null) {
                    ngcBufferedWriter_.append(block).append("\r\n");
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
            }

            try {
                if (debug) {
                    if (debugBufferedWriter_ != null) {
                        debugPrintJoin();
                        if (option_.containsKey(OPT.DEBUG_JSON)) {
                            debugBufferedWriter_.append("\"out\":\"").append(block).append("\"");
                        } else {
                            debugBufferedWriter_.append(block);
                        }
                    }
                }
            } catch (IOException ex) {
                Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
            }
        }
    }

    private void output(List<String> words, Map<FLG, Boolean> flg, boolean debug) {
        List<String> out = new ArrayList<>();
        List<String> subout = new ArrayList<>();
        String strval;
        boolean boolval;
        double dblval;

        if ((flg.get(FLG.TOOL_CHANGE)) && (option_.containsKey(OPT.TOOL_CHANGE))) {
            subout.add("G65");
            strval = option_.get(OPT.TOOL_CHANGE);
            if (strval.startsWith("O")) {
                if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                    subout.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                } else {
                    subout.add("<" + strval + ">");
                }
            } else {
                subout.add("<" + strval + ">");
            }
            words.stream().forEach((word) -> {
                if (word.startsWith("T")) {
                    subout.add(word);
                } else if ((word.startsWith("X") || word.startsWith("Y")) && (option_.containsKey(OPT.TOOL_CHANGE_XY))) {
                    subout.add(word);
                } else {
                    out.add(word);
                }
            });
        } else if ((flg.get(FLG.SKIP_FUNCTION)) && (option_.containsKey(OPT.SKIP_FUNCTION))) {
            subout.add("G65");
            strval = option_.get(OPT.SKIP_FUNCTION);
            if (strval.startsWith("O")) {
                if (PATTERN_UINT.matcher(strval.substring(1)).find()) {
                    subout.add("P" + DECIMAL_FORMAT_CODE.format(Integer.parseInt(strval.substring(1))));
                } else {
                    subout.add("<" + strval + ">");
                }
            } else {
                subout.add("<" + strval + ">");
            }
            for (String word : words) {
                if ((word.startsWith("X")) || (word.startsWith("Y")) || (word.startsWith("Z")) || word.startsWith("F")) {
                    subout.add(word);
                } else {
                    boolval = false;
                    if (gcodeconvert_.containsKey(31.0)) {
                        for (String code : gcodeconvert_.get(31.0).split("\\|")) {
                            code = code.trim();
                            if (!code.isEmpty()) {
                                if (PATTERN_NUMBER.matcher(code).find()) {
                                    dblval = Double.parseDouble(code);
                                    if (dblval >= 0) {
                                        if (word.equals("G" + DECIMAL_FORMAT_CODE.format(dblval))) {
                                            boolval = true;
                                        }
                                    }
                                } else if (word.equals(code)) {
                                    boolval = true;
                                }
                            }
                        }
                    } else if (word.equals("G31")) {
                        boolval = true;
                    }

                    if (!boolval) {
                        out.add(word);
                    }
                }
            }
        } else if ((option_.containsKey(OPT.ONLY_S) && (words.size() == 1) && (words.get(0).startsWith("S")))) {
            out.add(option_.get(OPT.ONLY_S));
            out.add(words.get(0));
        } else {
            out.addAll(words);
        }

        serialOutput(out, foreline_, state_);
        serialOutput(subout, foreline_, state_);

        fileOutput(out, state_, debug);
        fileOutput(subout, state_, debug);
    }

    private void debugPrintHeader() {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("{");
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintJoin() {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append(",");
                } else {
                    debugBufferedWriter_.append("\r\n");
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintFooter() {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("}");
                } else {
                    debugBufferedWriter_.append("\r\n");
                }
                debugBufferedWriter_.append("\r\n");
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintStatus(int pc, String block) {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("\"status\":{");
                    debugBufferedWriter_.append("\"pc\":").append(DECIMAL_FORMAT_CODE.format(pc)).append(",");
                    debugBufferedWriter_.append("\"line\":").append(DECIMAL_FORMAT_CODE.format(line_)).append(",");
                    debugBufferedWriter_.append("\"program_number\":\"").append(programnumber_).append("\",");
                    debugBufferedWriter_.append("\"nest\":").append(DECIMAL_FORMAT_CODE.format(locallevel_)).append(",");
                    debugBufferedWriter_.append("\"block\":\"").append(block.replace("\\", "\\\\").replace("\"", "\\\"").replace("/", "\\/")).append("\"");
                    debugBufferedWriter_.append("}");
                } else {
                    debugBufferedWriter_.append(padRight("pc", 6, ' ')).append(padRight(DECIMAL_FORMAT_CODE.format(pc), 10, ' '));
                    debugBufferedWriter_.append(" : ");
                    debugBufferedWriter_.append(padRight("line", 6, ' ')).append(padRight(DECIMAL_FORMAT_CODE.format(line_), 10, ' '));
                    debugBufferedWriter_.append(" : ");
                    debugBufferedWriter_.append(padRight("prog", 6, ' ')).append(padRight(programnumber_, 10, ' '));
                    debugBufferedWriter_.append(" : ");
                    debugBufferedWriter_.append(padRight("nest", 6, ' ')).append(padRight(DECIMAL_FORMAT_CODE.format(locallevel_), 10, ' '));
                    debugBufferedWriter_.append(" : ");
                    debugBufferedWriter_.append(block);
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintChange() {
        int i;

        try {
            if (debugBufferedWriter_ != null) {
                if (!variables_.isEmpty()) {
                    if (option_.containsKey(OPT.DEBUG_JSON)) {
                        debugBufferedWriter_.append("\"change\":{");
                        List<Integer> sortedKeys = new ArrayList<>(variables_.keySet());
                        Collections.sort(sortedKeys);
                        for (i = 0; i < sortedKeys.size(); i++) {
                            String val = variables_.get(sortedKeys.get(i)).get("value");
                            if (val == null) {
                                val = "null";
                            }
                            debugBufferedWriter_.append("\"#").append(DECIMAL_FORMAT_CODE.format(sortedKeys.get(i))).append("\":").append(val);
                            if (i < (sortedKeys.size() - 1)) {
                                debugBufferedWriter_.append(",");
                            }
                        }
                        debugBufferedWriter_.append("}");
                    } else {
                        List<Integer> sortedKeys = new ArrayList<>(variables_.keySet());
                        Collections.sort(sortedKeys);
                        for (i = 0; i < sortedKeys.size(); i++) {
                            String val = variables_.get(sortedKeys.get(i)).get("value");
                            if (val == null) {
                                val = "#0";
                            }
                            debugBufferedWriter_.append(padRight("$" + DECIMAL_FORMAT_CODE.format(sortedKeys.get(i)) + " = " + val, 35, ' '));
                            if ((i + 1) < sortedKeys.size()) {
                                if ((i + 1) % 5 == 0) {
                                    debugBufferedWriter_.append("\r\n");
                                } else {
                                    debugBufferedWriter_.append(" : ");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintPositions() {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("\"positions\":{");
                    debugBufferedWriter_.append("\"machine\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5021).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5021).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5022).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5022).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5023).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5023).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5024).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5024).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5025).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5025).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5026).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5026).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5027).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5027).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5028).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5028).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5029).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5029).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"monitor\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5001).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5001).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5002).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5002).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5003).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5003).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5004).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5004).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5005).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5005).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5006).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5006).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5007).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5007).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5008).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5008).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5009).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5009).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append("}");
                } else {
                    debugBufferedWriter_.append(padRight("X_MCN", 6, ' ')).append((getGcodeVariable(global_, 5021).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5021).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_MCN", 6, ' ')).append((getGcodeVariable(global_, 5022).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5022).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_MCN", 6, ' ')).append((getGcodeVariable(global_, 5023).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5023).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_MCN", 6, ' ')).append((getGcodeVariable(global_, 5024).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5024).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_MCN", 6, ' ')).append((getGcodeVariable(global_, 5025).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5025).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_MCN", 6, ' ')).append((getGcodeVariable(global_, 5026).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5026).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_MCN", 6, ' ')).append((getGcodeVariable(global_, 5027).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5027).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_MCN", 6, ' ')).append((getGcodeVariable(global_, 5028).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5028).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_MCN", 6, ' ')).append((getGcodeVariable(global_, 5029).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5029).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_MON", 6, ' ')).append((getGcodeVariable(global_, 5001).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5001).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_MON", 6, ' ')).append((getGcodeVariable(global_, 5002).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5002).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_MON", 6, ' ')).append((getGcodeVariable(global_, 5003).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5003).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_MON", 6, ' ')).append((getGcodeVariable(global_, 5004).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5004).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_MON", 6, ' ')).append((getGcodeVariable(global_, 5005).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5005).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_MON", 6, ' ')).append((getGcodeVariable(global_, 5006).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5006).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_MON", 6, ' ')).append((getGcodeVariable(global_, 5007).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5007).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_MON", 6, ' ')).append((getGcodeVariable(global_, 5008).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5008).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_MON", 6, ' ')).append((getGcodeVariable(global_, 5009).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5009).getDouble()), 10, ' ')));
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintOffsets() {
        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("\"offsets\":{");
                    debugBufferedWriter_.append("\"external\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5201).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5201).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5202).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5202).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5203).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5203).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5204).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5204).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5205).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5205).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5206).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5206).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5207).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5207).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5208).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5208).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5209).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5209).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g92\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5211).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5211).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5212).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5212).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5213).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5213).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5214).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5214).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5215).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5215).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5216).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5216).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5217).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5217).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5218).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5218).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5219).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5219).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"tool\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5121).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5121).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5123).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5123).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5122).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5122).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5124).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5124).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5125).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5125).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5126).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5126).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5127).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5127).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5128).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5128).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5129).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5129).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g54\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5221).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5221).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5222).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5222).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5223).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5223).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5224).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5224).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5225).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5225).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5226).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5226).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5227).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5227).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5228).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5228).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5229).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5229).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g55\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5241).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5241).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5242).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5242).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5243).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5243).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5244).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5244).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5245).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5245).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5246).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5246).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5247).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5247).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5248).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5248).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5249).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5249).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g56\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5261).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5261).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5262).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5262).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5263).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5263).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5264).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5264).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5265).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5265).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5266).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5266).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5267).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5267).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5268).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5268).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5269).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5269).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g57\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5281).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5281).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5282).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5282).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5283).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5283).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5284).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5284).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5285).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5285).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5286).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5286).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5287).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5287).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5288).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5288).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5289).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5289).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g58\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5301).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5301).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5302).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5302).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5303).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5303).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5304).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5304).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5305).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5305).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5306).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5306).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5307).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5307).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5308).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5308).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5309).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5309).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"g59\":{");
                    debugBufferedWriter_.append("\"x\":").append(getGcodeVariable(global_, 5321).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5321).getDouble())).append(",");
                    debugBufferedWriter_.append("\"y\":").append(getGcodeVariable(global_, 5322).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5322).getDouble())).append(",");
                    debugBufferedWriter_.append("\"z\":").append(getGcodeVariable(global_, 5323).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5323).getDouble())).append(",");
                    debugBufferedWriter_.append("\"a\":").append(getGcodeVariable(global_, 5324).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5324).getDouble())).append(",");
                    debugBufferedWriter_.append("\"b\":").append(getGcodeVariable(global_, 5325).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5325).getDouble())).append(",");
                    debugBufferedWriter_.append("\"c\":").append(getGcodeVariable(global_, 5326).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5326).getDouble())).append(",");
                    debugBufferedWriter_.append("\"u\":").append(getGcodeVariable(global_, 5327).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5327).getDouble())).append(",");
                    debugBufferedWriter_.append("\"v\":").append(getGcodeVariable(global_, 5328).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5328).getDouble())).append(",");
                    debugBufferedWriter_.append("\"w\":").append(getGcodeVariable(global_, 5329).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5329).getDouble()));
                    debugBufferedWriter_.append("}");
                    debugBufferedWriter_.append("}");
                } else {
                    debugBufferedWriter_.append(padRight("X_EXT", 6, ' ')).append((getGcodeVariable(global_, 5201).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5201).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_EXT", 6, ' ')).append((getGcodeVariable(global_, 5202).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5202).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_EXT", 6, ' ')).append((getGcodeVariable(global_, 5203).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5203).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_EXT", 6, ' ')).append((getGcodeVariable(global_, 5204).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5204).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_EXT", 6, ' ')).append((getGcodeVariable(global_, 5205).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5205).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_EXT", 6, ' ')).append((getGcodeVariable(global_, 5206).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5206).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_EXT", 6, ' ')).append((getGcodeVariable(global_, 5207).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5207).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_EXT", 6, ' ')).append((getGcodeVariable(global_, 5208).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5208).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_EXT", 6, ' ')).append((getGcodeVariable(global_, 5209).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5209).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G92", 6, ' ')).append((getGcodeVariable(global_, 5211).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5211).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G92", 6, ' ')).append((getGcodeVariable(global_, 5212).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5212).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G92", 6, ' ')).append((getGcodeVariable(global_, 5213).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5213).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G92", 6, ' ')).append((getGcodeVariable(global_, 5214).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5214).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G92", 6, ' ')).append((getGcodeVariable(global_, 5215).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5215).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G92", 6, ' ')).append((getGcodeVariable(global_, 5216).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5216).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G92", 6, ' ')).append((getGcodeVariable(global_, 5217).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5217).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G92", 6, ' ')).append((getGcodeVariable(global_, 5218).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5218).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G92", 6, ' ')).append((getGcodeVariable(global_, 5219).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5219).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_TOL", 6, ' ')).append((getGcodeVariable(global_, 5121).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5121).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_TOL", 6, ' ')).append((getGcodeVariable(global_, 5123).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5123).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_TOL", 6, ' ')).append((getGcodeVariable(global_, 5122).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5122).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_TOL", 6, ' ')).append((getGcodeVariable(global_, 5124).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5124).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_TOL", 6, ' ')).append((getGcodeVariable(global_, 5125).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5125).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_TOL", 6, ' ')).append((getGcodeVariable(global_, 5126).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5126).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_TOL", 6, ' ')).append((getGcodeVariable(global_, 5127).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5127).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_TOL", 6, ' ')).append((getGcodeVariable(global_, 5128).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5128).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_TOL", 6, ' ')).append((getGcodeVariable(global_, 5129).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5129).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G54", 6, ' ')).append((getGcodeVariable(global_, 5221).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5221).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G54", 6, ' ')).append((getGcodeVariable(global_, 5222).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5222).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G54", 6, ' ')).append((getGcodeVariable(global_, 5223).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5223).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G54", 6, ' ')).append((getGcodeVariable(global_, 5224).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5224).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G54", 6, ' ')).append((getGcodeVariable(global_, 5225).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5225).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G54", 6, ' ')).append((getGcodeVariable(global_, 5226).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5226).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G54", 6, ' ')).append((getGcodeVariable(global_, 5227).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5227).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G54", 6, ' ')).append((getGcodeVariable(global_, 5228).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5228).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G54", 6, ' ')).append((getGcodeVariable(global_, 5229).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5229).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G55", 6, ' ')).append((getGcodeVariable(global_, 5241).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5241).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G55", 6, ' ')).append((getGcodeVariable(global_, 5242).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5242).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G55", 6, ' ')).append((getGcodeVariable(global_, 5243).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5243).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G55", 6, ' ')).append((getGcodeVariable(global_, 5244).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5244).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G55", 6, ' ')).append((getGcodeVariable(global_, 5245).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5245).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G55", 6, ' ')).append((getGcodeVariable(global_, 5246).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5246).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G55", 6, ' ')).append((getGcodeVariable(global_, 5247).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5247).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G55", 6, ' ')).append((getGcodeVariable(global_, 5248).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5248).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G55", 6, ' ')).append((getGcodeVariable(global_, 5249).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5249).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G56", 6, ' ')).append((getGcodeVariable(global_, 5261).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5261).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G56", 6, ' ')).append((getGcodeVariable(global_, 5262).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5262).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G56", 6, ' ')).append((getGcodeVariable(global_, 5263).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5263).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G56", 6, ' ')).append((getGcodeVariable(global_, 5264).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5264).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G56", 6, ' ')).append((getGcodeVariable(global_, 5265).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5265).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G56", 6, ' ')).append((getGcodeVariable(global_, 5266).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5266).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G56", 6, ' ')).append((getGcodeVariable(global_, 5267).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5267).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G56", 6, ' ')).append((getGcodeVariable(global_, 5268).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5268).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G56", 6, ' ')).append((getGcodeVariable(global_, 5269).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5269).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G57", 6, ' ')).append((getGcodeVariable(global_, 5281).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5281).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G57", 6, ' ')).append((getGcodeVariable(global_, 5282).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5282).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G57", 6, ' ')).append((getGcodeVariable(global_, 5283).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5283).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G57", 6, ' ')).append((getGcodeVariable(global_, 5284).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5284).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G57", 6, ' ')).append((getGcodeVariable(global_, 5285).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5285).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G57", 6, ' ')).append((getGcodeVariable(global_, 5286).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5286).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G57", 6, ' ')).append((getGcodeVariable(global_, 5287).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5287).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G57", 6, ' ')).append((getGcodeVariable(global_, 5288).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5288).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G57", 6, ' ')).append((getGcodeVariable(global_, 5289).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5289).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G58", 6, ' ')).append((getGcodeVariable(global_, 5301).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5301).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G58", 6, ' ')).append((getGcodeVariable(global_, 5302).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5302).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G58", 6, ' ')).append((getGcodeVariable(global_, 5303).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5303).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G58", 6, ' ')).append((getGcodeVariable(global_, 5304).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5304).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G58", 6, ' ')).append((getGcodeVariable(global_, 5305).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5305).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G58", 6, ' ')).append((getGcodeVariable(global_, 5306).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5306).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G58", 6, ' ')).append((getGcodeVariable(global_, 5307).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5307).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G58", 6, ' ')).append((getGcodeVariable(global_, 5308).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5308).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G58", 6, ' ')).append((getGcodeVariable(global_, 5309).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5309).getDouble()), 10, ' ')));
                    debugBufferedWriter_.append("\r\n");
                    debugBufferedWriter_.append(padRight("X_G59", 6, ' ')).append((getGcodeVariable(global_, 5321).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5321).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Y_G59", 6, ' ')).append((getGcodeVariable(global_, 5322).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5322).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("Z_G59", 6, ' ')).append((getGcodeVariable(global_, 5323).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5323).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("A_G59", 6, ' ')).append((getGcodeVariable(global_, 5324).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5324).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("B_G59", 6, ' ')).append((getGcodeVariable(global_, 5325).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5325).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("C_G59", 6, ' ')).append((getGcodeVariable(global_, 5326).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5326).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("U_G59", 6, ' ')).append((getGcodeVariable(global_, 5327).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5327).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("V_G59", 6, ' ')).append((getGcodeVariable(global_, 5328).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5328).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("W_G59", 6, ' ')).append((getGcodeVariable(global_, 5329).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5329).getDouble()), 10, ' ')));
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private void debugPrintVariables() {
        int i;

        try {
            if (debugBufferedWriter_ != null) {
                if (option_.containsKey(OPT.DEBUG_JSON)) {
                    debugBufferedWriter_.append("\"variables\":{");
                    for (i = 1; i <= 33; i++) {
                        debugBufferedWriter_.append("\"#").append(DECIMAL_FORMAT_CODE.format(i)).append("\":").append(local_.get(i).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(local_.get(i).getDouble()));
                        if (i < 33) {
                            debugBufferedWriter_.append(",");
                        }
                    }
                    debugBufferedWriter_.append(",");
                    for (i = 100; i <= 199; i++) {
                        debugBufferedWriter_.append("\"#").append(DECIMAL_FORMAT_CODE.format(i)).append("\":").append(global_.get(i).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()));
                        if (i < 199) {
                            debugBufferedWriter_.append(",");
                        }
                    }
                    debugBufferedWriter_.append(",");
                    for (i = 500; i <= 999; i++) {
                        debugBufferedWriter_.append("\"#").append(DECIMAL_FORMAT_CODE.format(i)).append("\":").append(global_.get(i).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()));
                        if (i < 999) {
                            debugBufferedWriter_.append(",");
                        }
                    }
                    debugBufferedWriter_.append(",");
                    for (i = 4000; i <= 4030; i++) {
                        debugBufferedWriter_.append("\"#").append(DECIMAL_FORMAT_CODE.format(i)).append("\":").append(getGcodeVariable(global_, i).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()));
                        if (i < 4030) {
                            debugBufferedWriter_.append(",");
                        }
                    }
                    debugBufferedWriter_.append(",");
                    debugBufferedWriter_.append("\"#4107\":").append(getGcodeVariable(global_, 4107).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4107).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4108\":").append(getGcodeVariable(global_, 4108).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4108).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4109\":").append(getGcodeVariable(global_, 4109).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4109).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4111\":").append(getGcodeVariable(global_, 4111).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4111).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4113\":").append(getGcodeVariable(global_, 4113).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4113).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4114\":").append(getGcodeVariable(global_, 4114).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4114).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4115\":").append(getGcodeVariable(global_, 4115).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4115).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4119\":").append(getGcodeVariable(global_, 4119).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4119).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4120\":").append(getGcodeVariable(global_, 4120).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4120).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#4130\":").append(getGcodeVariable(global_, 4130).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(4130).getDouble())).append(",");
                    debugBufferedWriter_.append("\"#5400\":").append(getGcodeVariable(global_, 5400).isNil() ? "null" : DECIMAL_FORMAT_VAL.format(global_.get(5400).getDouble()));
                    debugBufferedWriter_.append("}");
                } else {
                    debugBufferedWriter_.append(padRight("1(A)", 6, ' ')).append((local_.get(1).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(1).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("2(B)", 6, ' ')).append((local_.get(2).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(2).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("3(C)", 6, ' ')).append((local_.get(3).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(3).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4(I)", 6, ' ')).append((local_.get(4).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(4).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("5(J)", 6, ' ')).append((local_.get(5).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(5).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("6(K)", 6, ' ')).append((local_.get(6).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(6).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("7(D)", 6, ' ')).append((local_.get(7).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(7).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("8(E)", 6, ' ')).append((local_.get(8).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(8).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("9(F)", 6, ' ')).append((local_.get(9).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(9).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("10", 6, ' ')).append((local_.get(10).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(10).getDouble()), 10, ' '))).append("\r\n");
                    debugBufferedWriter_.append(padRight("11(H)", 6, ' ')).append((local_.get(11).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(11).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("12", 6, ' ')).append((local_.get(12).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(12).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("13(M)", 6, ' ')).append((local_.get(13).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(13).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("14", 6, ' ')).append((local_.get(14).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(14).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("15", 6, ' ')).append((local_.get(15).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(15).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("16", 6, ' ')).append((local_.get(16).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(16).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("17(Q)", 6, ' ')).append((local_.get(17).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(17).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("18(R)", 6, ' ')).append((local_.get(18).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(18).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("19(S)", 6, ' ')).append((local_.get(19).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(19).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("20(T)", 6, ' ')).append((local_.get(20).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(20).getDouble()), 10, ' '))).append("\r\n");
                    debugBufferedWriter_.append(padRight("21(U)", 6, ' ')).append((local_.get(21).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(21).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("22(V)", 6, ' ')).append((local_.get(22).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(22).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("23(W)", 6, ' ')).append((local_.get(23).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(23).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("24(X)", 6, ' ')).append((local_.get(24).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(24).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("25(Y)", 6, ' ')).append((local_.get(25).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(25).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("26(Z)", 6, ' ')).append((local_.get(26).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(26).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("27", 6, ' ')).append((local_.get(27).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(27).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("28", 6, ' ')).append((local_.get(28).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(28).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("29", 6, ' ')).append((local_.get(29).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(29).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("30", 6, ' ')).append((local_.get(30).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(30).getDouble()), 10, ' '))).append("\r\n");
                    debugBufferedWriter_.append(padRight("31", 6, ' ')).append((local_.get(31).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(31).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("32", 6, ' ')).append((local_.get(32).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(32).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("33", 6, ' ')).append((local_.get(33).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(local_.get(33).getDouble()), 10, ' '))).append("\r\n");

                    for (i = 100; i <= 199; i++) {
                        debugBufferedWriter_.append(padRight(DECIMAL_FORMAT_CODE.format(i), 6, ' ')).append((global_.get(i).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()), 10, ' ')));
                        if ((i + 1) % 10 == 0) {
                            debugBufferedWriter_.append("\r\n");
                        } else {
                            debugBufferedWriter_.append(" : ");
                        }
                    }
                    for (i = 500; i <= 999; i++) {
                        debugBufferedWriter_.append(padRight(DECIMAL_FORMAT_CODE.format(i), 6, ' ')).append((global_.get(i).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()), 10, ' ')));
                        if ((i + 1) % 10 == 0) {
                            debugBufferedWriter_.append("\r\n");
                        } else {
                            debugBufferedWriter_.append(" : ");
                        }
                    }
                    for (i = 4000; i <= 4030; i++) {
                        debugBufferedWriter_.append(padRight(DECIMAL_FORMAT_CODE.format(i), 6, ' ')).append((getGcodeVariable(global_, i).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(i).getDouble()), 10, ' ')));
                        if ((i + 1) % 10 == 0) {
                            debugBufferedWriter_.append("\r\n");
                        } else {
                            debugBufferedWriter_.append(" : ");
                        }
                    }
                    debugBufferedWriter_.append(padRight("4107", 6, ' ')).append((getGcodeVariable(global_, 4107).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4107).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4108", 6, ' ')).append((getGcodeVariable(global_, 4108).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4108).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4109", 6, ' ')).append((getGcodeVariable(global_, 4109).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4109).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4111", 6, ' ')).append((getGcodeVariable(global_, 4111).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4111).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4113", 6, ' ')).append((getGcodeVariable(global_, 4113).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4113).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4114", 6, ' ')).append((getGcodeVariable(global_, 4114).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4114).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4115", 6, ' ')).append((getGcodeVariable(global_, 4115).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4115).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4119", 6, ' ')).append((getGcodeVariable(global_, 4119).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4119).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("4120", 6, ' ')).append((getGcodeVariable(global_, 4120).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4120).getDouble()), 10, ' '))).append("\r\n");
                    debugBufferedWriter_.append(padRight("4130", 6, ' ')).append((getGcodeVariable(global_, 4130).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(4130).getDouble()), 10, ' '))).append(" : ");
                    debugBufferedWriter_.append(padRight("5400", 6, ' ')).append((getGcodeVariable(global_, 5400).isNil() ? padRight("#0", 10, ' ') : padRight(DECIMAL_FORMAT_VAL.format(global_.get(5400).getDouble()), 10, ' ')));
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private List<String> optimaization(List<String> words, Map<FLG, Boolean> flg) {
        double gCode;
        int intval, gGroupCode;

        if (!words.isEmpty()) {
            // delete rapid only G4
            gCode = 0.0;
            gGroupCode = 4000 + ggroup_.get(gCode);
            if (getGcodeVariable(global_, gGroupCode).getDouble() == gCode) {
                intval = words.indexOf("G4");
                if (intval > -1) {
                    if (words.size() == (intval + 1)) {
                        words.remove(intval);
                    } else if (!words.get(intval + 1).startsWith("X") && !words.get(intval + 1).startsWith("P")) {
                        words.remove(intval);
                    }
                }
            }
        }
        return words;
    }

    private void dxfPrint() {
        try {
            if (drawBufferedWriter_ != null) {
                double startX, startY, startZ, endX, endY, endZ, circleX, circleY, circleZ, circleR, circleI, circleJ, circleK, aa, bb, cc, dd, ee, ll, p1, p2;

                startX = getGcodeVariable(draw_, 5021).getDouble();
                startY = getGcodeVariable(draw_, 5022).getDouble();
                startZ = getGcodeVariable(draw_, 5023).getDouble();
                endX = getGcodeVariable(global_, 5021).getDouble();
                endY = getGcodeVariable(global_, 5022).getDouble();
                endZ = getGcodeVariable(global_, 5023).getDouble();

                setGcodeVariable(draw_, 5021, endX, null, null);
                setGcodeVariable(draw_, 5022, endY, null, null);
                setGcodeVariable(draw_, 5023, endZ, null, null);

                if (getGcodeVariable(draw_, ARG_R).isNil()) {
                    circleR = 0.0;
                } else {
                    circleR = getGcodeVariable(draw_, ARG_R).getDouble();
                    setGcodeVariable(draw_, ARG_R, null, null, null);
                }
                if (getGcodeVariable(draw_, ARG_I).isNil()) {
                    circleI = 0.0;
                } else {
                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0001) == 0x0001) {
                        circleI = -getGcodeVariable(draw_, ARG_I).getDouble();
                    } else {
                        circleI = getGcodeVariable(draw_, ARG_I).getDouble();
                    }
                    setGcodeVariable(draw_, ARG_I, null, null, null);
                }
                if (getGcodeVariable(draw_, ARG_J).isNil()) {
                    circleJ = 0.0;
                } else {
                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0002) == 0x0002) {
                        circleJ = -getGcodeVariable(draw_, ARG_J).getDouble();
                    } else {
                        circleJ = getGcodeVariable(draw_, ARG_J).getDouble();
                    }
                    setGcodeVariable(draw_, ARG_J, null, null, null);
                }
                if (getGcodeVariable(draw_, ARG_K).isNil()) {
                    circleK = 0.0;
                } else {
                    if ((getGcodeVariable(global_, 3007).getInt() & 0x0004) == 0x0004) {
                        circleK = -getGcodeVariable(draw_, ARG_K).getDouble();
                    } else {
                        circleK = getGcodeVariable(draw_, ARG_K).getDouble();
                    }
                    setGcodeVariable(draw_, ARG_K, null, null, null);
                }

                if ((getGcodeVariable(global_, 4000 + ggroup_.get(28.0)).getDouble() == 28.0) || (getGcodeVariable(global_, 4000 + ggroup_.get(0.0)).getDouble() == 0.0)) {
                    // G0
                    if ((startX != endX) || (startY != endY) || (startZ != endZ)) {
                        drawBufferedWriter_.append("  0").append("\r\n");
                        drawBufferedWriter_.append("LINE").append("\r\n");
                        drawBufferedWriter_.append("  8").append("\r\n");
                        drawBufferedWriter_.append("Rapid_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                        drawBufferedWriter_.append(" 62").append("\r\n");
                        drawBufferedWriter_.append("     1").append("\r\n");
                        drawBufferedWriter_.append("  6").append("\r\n");
                        drawBufferedWriter_.append("HIDDEN2").append("\r\n");
                        drawBufferedWriter_.append(" 10").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startX)).append("\r\n");
                        drawBufferedWriter_.append(" 20").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startY)).append("\r\n");
                        drawBufferedWriter_.append(" 30").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startZ)).append("\r\n");
                        drawBufferedWriter_.append(" 11").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endX)).append("\r\n");
                        drawBufferedWriter_.append(" 21").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endY)).append("\r\n");
                        drawBufferedWriter_.append(" 31").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endZ)).append("\r\n");
                    }
                } else if (getGcodeVariable(global_, 4000 + ggroup_.get(1.0)).getDouble() == 1.0) {
                    // G1
                    if ((startX != endX) || (startY != endY) || (startZ != endZ)) {
                        drawBufferedWriter_.append("  0").append("\r\n");
                        drawBufferedWriter_.append("LINE").append("\r\n");
                        drawBufferedWriter_.append("  8").append("\r\n");
                        drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                        drawBufferedWriter_.append(" 62").append("\r\n");
                        drawBufferedWriter_.append("     3").append("\r\n");
                        drawBufferedWriter_.append("  6").append("\r\n");
                        drawBufferedWriter_.append("Continuous").append("\r\n");
                        drawBufferedWriter_.append(" 10").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startX)).append("\r\n");
                        drawBufferedWriter_.append(" 20").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startY)).append("\r\n");
                        drawBufferedWriter_.append(" 30").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(startZ)).append("\r\n");
                        drawBufferedWriter_.append(" 11").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endX)).append("\r\n");
                        drawBufferedWriter_.append(" 21").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endY)).append("\r\n");
                        drawBufferedWriter_.append(" 31").append("\r\n");
                        drawBufferedWriter_.append(Double.toString(endZ)).append("\r\n");
                    }
                } else if ((getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0)
                        || (getGcodeVariable(global_, 4000 + ggroup_.get(3.0)).getDouble() == 3.0)) {
                    // G2 G3
                    if (getGcodeVariable(global_, 4000 + ggroup_.get(2.0)).getDouble() == 2.0) {
                        // G2
                        ee = 1.0;
                    } else {
                        // G3
                        ee = -1.0;
                    }
                    if (getGcodeVariable(global_, 4000 + ggroup_.get(17.0)).getDouble() == 17.0) {
                        // XY
                        if (option_.containsKey(OPT.COORDINATE_MIRROR)) {
                            if (Integer.bitCount(getGcodeVariable(global_, 3007).getInt() & 0x0003) % 2 != 0) {
                                ee = -ee;
                            }
                        }
                        if (circleR == 0.0) {
                            circleX = startX + circleI;
                            circleY = startY + circleJ;
                            circleZ = startZ + circleK;
                            circleR = Math.sqrt(Math.pow(circleI, 2) + Math.pow(circleJ, 2));
                        } else {
                            if (circleR > 0) {
                                dd = 1.0;
                            } else {
                                circleR = -circleR;
                                dd = -1.0;
                            }
                            ll = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                            cc = Math.atan2(endY - startY, endX - startX);
                            bb = Math.pow(ll, 2) / (2.0 * ll * circleR);
                            if (bb < -1.0) {
                                bb = -1.0;
                            } else if (bb > 1.0) {
                                bb = 1.0;
                            }
                            aa = Math.acos(bb);
                            circleX = startX + circleR * Math.cos(cc - dd * ee * aa);
                            circleY = startY + circleR * Math.sin(cc - dd * ee * aa);
                            circleZ = startZ;
                        }
                        if (ee > 0) {
                            // G2
                            p1 = Math.atan2(endY - circleY, endX - circleX);
                            p2 = Math.atan2(startY - circleY, startX - circleX);
                        } else {
                            // G3
                            p1 = Math.atan2(startY - circleY, startX - circleX);
                            p2 = Math.atan2(endY - circleY, endX - circleX);
                        }
                        if (p1 == p2) {
                            // circle
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("CIRCLE").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                        } else {
                            // arc
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("ARC").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append(" 50").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p1))).append("\r\n");
                            drawBufferedWriter_.append(" 51").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p2))).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                        }
                    } else if (getGcodeVariable(global_, 4000 + ggroup_.get(18.0)).getDouble() == 18.0) {
                        // ZX
                        if (option_.containsKey(OPT.COORDINATE_MIRROR)) {
                            if (Integer.bitCount(getGcodeVariable(global_, 3007).getInt() & 0x0005) % 2 != 0) {
                                ee = -ee;
                            }
                        }
                        if (circleR == 0.0) {
                            circleX = startX + circleI;
                            circleZ = startZ + circleK;
                            circleY = startY + circleJ;
                            circleR = Math.sqrt(Math.pow(circleI, 2) + Math.pow(circleK, 2));
                        } else {
                            if (circleR > 0) {
                                dd = 1.0;
                            } else {
                                circleR = -circleR;
                                dd = -1.0;
                            }
                            ll = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endZ - startZ, 2));
                            cc = Math.atan2(endZ - startZ, endX - startX);
                            bb = Math.pow(ll, 2) / (2.0 * ll * circleR);
                            if (bb < -1.0) {
                                bb = -1.0;
                            } else if (bb > 1.0) {
                                bb = 1.0;
                            }
                            aa = Math.acos(bb);
                            circleX = startX + circleR * Math.cos(cc + dd * ee * aa);
                            circleZ = startZ + circleR * Math.sin(cc + dd * ee * aa);
                            circleY = startY;
                        }
                        if (ee > 0) {
                            p1 = Math.atan2(endZ - circleZ, endX - circleX) + Math.PI;
                            p2 = Math.atan2(startZ - circleZ, startX - circleX);
                        } else {
                            p1 = Math.atan2(startZ - circleZ, startX - circleX) + Math.PI;
                            p2 = Math.atan2(endZ - circleZ, endX - circleX);
                        }
                        if (p1 == p2) {
                            // circle
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("CIRCLE").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(-circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                        } else {
                            // arc
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("ARC").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(-circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append(" 50").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p1))).append("\r\n");
                            drawBufferedWriter_.append(" 51").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p2))).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                        }
                    } else if (getGcodeVariable(global_, 4000 + ggroup_.get(19.0)).getDouble() == 19.0) {
                        // YZ
                        if (option_.containsKey(OPT.COORDINATE_MIRROR)) {
                            if (Integer.bitCount(getGcodeVariable(global_, 3007).getInt() & 0x0006) % 2 != 0) {
                                ee = -ee;
                            }
                        }
                        if (circleR == 0.0) {
                            circleY = startY + circleJ;
                            circleZ = startZ + circleK;
                            circleX = startX + circleI;
                            circleR = Math.sqrt(Math.pow(circleJ, 2) + Math.pow(circleK, 2));
                        } else {
                            if (circleR > 0) {
                                dd = 1.0;
                            } else {
                                circleR = -circleR;
                                dd = -1.0;
                            }
                            ll = Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2));
                            cc = Math.atan2(endZ - startZ, endY - startY);
                            bb = Math.pow(ll, 2) / (2.0 * ll * circleR);
                            if (bb < -1.0) {
                                bb = -1.0;
                            } else if (bb > 1.0) {
                                bb = 1.0;
                            }
                            aa = Math.acos(bb);
                            circleY = startY + circleR * Math.cos(cc - dd * ee * aa);
                            circleZ = startZ + circleR * Math.sin(cc - dd * ee * aa);
                            circleX = startX;
                        }
                        if (ee > 0) {
                            p1 = Math.atan2(endZ - circleZ, endY - circleY);
                            p2 = Math.atan2(startZ - circleZ, startY - circleY);
                        } else {
                            p1 = Math.atan2(startZ - circleZ, startY - circleY);
                            p2 = Math.atan2(endZ - circleZ, endY - circleY);
                        }
                        if (p1 == p2) {
                            // circle
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("CIRCLE").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                        } else {
                            // arc
                            drawBufferedWriter_.append("  0").append("\r\n");
                            drawBufferedWriter_.append("ARC").append("\r\n");
                            drawBufferedWriter_.append("  8").append("\r\n");
                            drawBufferedWriter_.append("Feed_T").append(Integer.toString(getGcodeVariable(global_, 5400).getInt())).append("\r\n");
                            drawBufferedWriter_.append(" 62").append("\r\n");
                            drawBufferedWriter_.append("     3").append("\r\n");
                            drawBufferedWriter_.append("  6").append("\r\n");
                            drawBufferedWriter_.append("Continuous").append("\r\n");
                            drawBufferedWriter_.append(" 10").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleY)).append("\r\n");
                            drawBufferedWriter_.append(" 20").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleZ)).append("\r\n");
                            drawBufferedWriter_.append(" 30").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleX)).append("\r\n");
                            drawBufferedWriter_.append(" 40").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(circleR)).append("\r\n");
                            drawBufferedWriter_.append(" 50").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p1))).append("\r\n");
                            drawBufferedWriter_.append(" 51").append("\r\n");
                            drawBufferedWriter_.append(Double.toString(Math.toDegrees(p2))).append("\r\n");
                            drawBufferedWriter_.append("210").append("\r\n");
                            drawBufferedWriter_.append("1").append("\r\n");
                            drawBufferedWriter_.append("220").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                            drawBufferedWriter_.append("230").append("\r\n");
                            drawBufferedWriter_.append("0").append("\r\n");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
    }

    private String padRight(String str, int len, char pad) {
        StringBuilder stringBuilder = new StringBuilder(str);
        int i;

        for (i = str.length(); i < len; i++) {
            stringBuilder.append(pad);
        }
        return stringBuilder.toString();
    }

    private Object platformRun(Callable<Object> callable) {
        try {
            if (Platform.isFxApplicationThread()) {
                return callable.call();
            }
            RunnableFuture<Object> future = new FutureTask<>(callable);
            Platform.runLater(future);
            return future.get();
        } catch (InterruptedException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        } catch (ExecutionException ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        } catch (Exception ex) {
            Console.writeStackTrace(DesignEnums.GCODE_INTERPRETER.toString(), ex);
        }
        return null;
    }

    @Override
    public void addGcodeInterpreterListener(GcodeInterpreterPluginListener listener) {
        boolean isListener = false;
        for (GcodeInterpreterPluginListener gil : eventListenerList_.getListeners(GcodeInterpreterPluginListener.class)) {
            if (gil == listener) {
                isListener = true;
                break;
            }
        }
        if (!isListener) {
            eventListenerList_.add(GcodeInterpreterPluginListener.class, listener);
        }
    }

    @Override
    public void removeGcodeInterpreterListener(GcodeInterpreterPluginListener listener) {
        eventListenerList_.remove(GcodeInterpreterPluginListener.class, listener);
    }

    private void writeLog(final String msg, final boolean err) {
        StringBuilder message = new StringBuilder(msg);
        if (err) {
            message.append(" [line: ").append(foreline_).append(" program_number: ").append(programnumber_).append(" - ").append(line_).append("]");
        }
        Console.write(DesignEnums.GCODE_INTERPRETER.toString(), message.toString(), err);
    }
}
