/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

/**
 *
 * @author mizoguch-ken
 */
public interface BaseSettingsPlugin {

    public boolean isSerial();

    public boolean isViewSerial();

    public void setViewSerial(boolean bln);

    public String getSerialPortValue();

    public void setSerialPortValue(String value);

    public boolean actionSerialPortList();

    public void actionSerialOpen();

    public void actionSerialClose();

    public String getSerialBaudrateValue();

    public void setSerialBaudrateValue(String value);

    public String getSerialDataBitsValue();

    public void setSerialDataBitsValue(String value);

    public String getSerialStopBitsValue();

    public void setSerialStopBitsValue(String value);

    public String getSerialParityValue();

    public void setSerialParityValue(String value);

    public String getSerialEOBValue();

    public void setSerialEOBValue(String value);

    public String getSerialBufferLimitValue();

    public void setSerialBufferLimitValue(String value);

    public String getSerialDelayValue();

    public void setSerialDelayValue(String value);

    public String getSerialStartCodeValue();

    public void setSerialStartCodeValue(String value);

    public String getSerialEndCodeValue();

    public void setSerialEndCodeValue(String value);

    public Boolean isSerialCharacterCheck();

    public void setSerialCharacterCheck(Boolean check);

    public Boolean isSerialObserveCTSCheck();

    public void setSerialObserveCTSCheck(Boolean check);

    public Boolean isSerialObserveDSRCheck();

    public void setSerialObserveDSRCheck(Boolean check);
}
