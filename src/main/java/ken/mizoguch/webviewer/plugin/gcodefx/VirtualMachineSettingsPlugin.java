/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.nio.file.Path;

/**
 *
 * @author mizoguch-ken
 */
public interface VirtualMachineSettingsPlugin {

    public boolean isRunning();

    public boolean isSettings();

    public boolean isViewRunning();

    public void setViewRunning(boolean bln);

    public String getVirtualMachineNameValue();

    public boolean actionLoadVirtualMachine(Path file);

    public boolean actionSaveVirtualMachine(Path file);

    public Boolean isLogCheck();

    public void setLogCheck(Boolean check);

    public Boolean isDebugCheck();

    public void setDebugCheck(Boolean check);

    public String getProgramNumberValue();

    public void setProgramNumberValue(String value);

    public Boolean[] getOptionalSkipCheck();

    public Boolean getOptionalSkipCheck(int index);

    public void setOptionalSkipCheck(Boolean[] checks);

    public void setOptionalSkipCheck(int index, Boolean check);

    public boolean actionBackGroundFileValue(int index);

    public String[] getBackGroundFileValue();

    public String getBackGroundFileValue(int index);

    public void setBackGroundFileValue(String[] values);

    public void setBackGroundFileValue(int index, String value);

    public boolean actionExternalSubProgramDirectoryValue();

    public String getExternalSubProgramDirectoryValue();

    public void setExternalSubProgramDirectoryValue(String value);

    public String[] getGcodeGroupValue();

    public String getGcodeGroupValue(int index);

    public void setGcodeGroupValue(String[] values);

    public void setGcodeGroupValue(int index, String value);

    public String getCodeChangeProgramCallValue();

    public void setCodeChangeProgramCallValue(String value);

    public String getCodeChangeGValue();

    public void setCodeChangeGValue(String value);

    public String getCodeChangeMValue();

    public void setCodeChangeMValue(String value);

    public String getCodeChangeTValue();

    public void setCodeChangeTValue(String value);

    public String getCodeChangeDValue();

    public void setCodeChangeDValue(String value);

    public String getCodeChangeHValue();

    public void setCodeChangeHValue(String value);

    public String getMacroCallGValue();

    public void setMacroCallGValue(String value);

    public String getMacroCallMValue();

    public void setMacroCallMValue(String value);

    public Boolean[] getOriginMachineCheck();

    public Boolean getOriginMachineCheck(int index);

    public void setOriginMachineCheck(Boolean[] checks);

    public void setOriginMachineCheck(int index, Boolean check);

    public String[] getOriginMachineValue();

    public String getOriginMachineValue(int index);

    public void setOriginMachineValue(String[] values);

    public void setOriginMachineValue(int index, String value);

    public Boolean[] getCoordinateExtCheck();

    public Boolean getCoordinateExtCheck(int index);

    public void setCoordinateExtCheck(Boolean[] checks);

    public void setCoordinateExtCheck(int index, Boolean check);

    public String[] getCoordinateExtValue();

    public String getCoordinateExtValue(int index);

    public void setCoordinateExtValue(String[] values);

    public void setCoordinateExtValue(int index, String value);

    public Boolean[] getCoordinateG92Check();

    public Boolean getCoordinateG92Check(int index);

    public void setCoordinateG92Check(Boolean[] checks);

    public void setCoordinateG92Check(int index, Boolean check);

    public String[] getCoordinateG92Value();

    public String getCoordinateG92Value(int index);

    public void setCoordinateG92Value(String[] values);

    public void setCoordinateG92Value(int index, String value);

    public Boolean getCoordinateToolCheck();

    public void setCoordinateToolCheck(Boolean check);

    public Boolean getCoordinateMirrorCheck();

    public void setCoordinateMirrorCheck(Boolean check);

    public Boolean[] getCoordinateG54Check();

    public Boolean getCoordinateG54Check(int index);

    public void setCoordinateG54Check(Boolean[] checks);

    public void setCoordinateG54Check(int index, Boolean check);

    public String[] getCoordinateG54Value();

    public String getCoordinateG54Value(int index);

    public void setCoordinateG54Value(String[] values);

    public void setCoordinateG54Value(int index, String value);

    public Boolean[] getCoordinateG55Check();

    public Boolean getCoordinateG55Check(int index);

    public void setCoordinateG55Check(Boolean[] checks);

    public void setCoordinateG55Check(int index, Boolean check);

    public String[] getCoordinateG55Value();

    public String getCoordinateG55Value(int index);

    public void setCoordinateG55Value(String[] values);

    public void setCoordinateG55Value(int index, String value);

    public Boolean[] getCoordinateG56Check();

    public Boolean getCoordinateG56Check(int index);

    public void setCoordinateG56Check(Boolean[] checks);

    public void setCoordinateG56Check(int index, Boolean check);

    public String[] getCoordinateG56Value();

    public String getCoordinateG56Value(int index);

    public void setCoordinateG56Value(String[] values);

    public void setCoordinateG56Value(int index, String value);

    public Boolean[] getCoordinateG57Check();

    public Boolean getCoordinateG57Check(int index);

    public void setCoordinateG57Check(Boolean[] checks);

    public void setCoordinateG57Check(int index, Boolean check);

    public String[] getCoordinateG57Value();

    public String getCoordinateG57Value(int index);

    public void setCoordinateG57Value(String[] values);

    public void setCoordinateG57Value(int index, String value);

    public Boolean[] getCoordinateG58Check();

    public Boolean getCoordinateG58Check(int index);

    public void setCoordinateG58Check(Boolean[] checks);

    public void setCoordinateG58Check(int index, Boolean check);

    public String[] getCoordinateG58Value();

    public String getCoordinateG58Value(int index);

    public void setCoordinateG58Value(String[] values);

    public void setCoordinateG58Value(int index, String value);

    public Boolean[] getCoordinateG59Check();

    public Boolean getCoordinateG59Check(int index);

    public void setCoordinateG59Check(Boolean[] checks);

    public void setCoordinateG59Check(int index, Boolean check);

    public String[] getCoordinateG59Value();

    public String getCoordinateG59Value(int index);

    public void setCoordinateG59Value(String[] values);

    public void setCoordinateG59Value(int index, String value);

    public String getToolOffsetValue();

    public void setToolOffsetValue(String value);

    public String getVariablesValue();

    public void setVariablesValue(String value);

    public Boolean getToolChangeProgramCheck();

    public void setToolChangeProgramCheck(Boolean check);

    public Boolean getToolChangeXYCheck();

    public void setToolChangeXYCheck(Boolean check);

    public String getToolChangeProgramValue();

    public void setToolChangeProgramValue(String value);

    public Boolean getToolChangeMCodeCheck();

    public void setToolChangeMCodeCheck(Boolean check);

    public String getToolChangeMCodeValue();

    public void setToolChangeMCodeValue(String value);

    public Boolean getSkipFunctionProgramCheck();

    public void setSkipFunctionProgramCheck(Boolean check);

    public String getSkipFunctionProgramValue();

    public void setSkipFunctionProgramValue(String value);

    public String[] getLadderValue();

    public String getLadderValue(int index);

    public void setLadderValue(String[] values);

    public void setLadderValue(int index, String value);

    public Boolean getOptionOptimization();

    public void setOptionOptimization(Boolean check);

    public Boolean getOptionExComment();

    public void setOptionExComment(Boolean check);

    public Boolean getOptionDisable30033004();

    public void setOptionDisable30033004(Boolean check);

    public Boolean getOptionReplace3006M0();

    public void setOptionReplace3006M0(Boolean check);

    public Boolean getOptionOnlyS();

    public void setOptionOnlyS(Boolean check);

    public Boolean getOptionRS274NGC();

    public void setOptionRS274NGC(Boolean check);

    public Boolean getOptionDebugJson();

    public void setOptionDebugJson(Boolean check);

    public String getOptionMaxFeedRate();

    public void setOptionMaxFeedRate(String value);

    public String getOptionMaxRevolution();

    public void setOptionMaxRevolution(String value);

    public String getOptionStartProgram();

    public void setOptionStartProgram(String value);

    public String getOptionBlockProgram();

    public void setOptionBlockProgram(String value);

    public String getOptionEndProgram();

    public void setOptionEndProgram(String value);
}
