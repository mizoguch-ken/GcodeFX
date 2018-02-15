/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.webeditor.WebEditor;

/**
 *
 * @author mizoguch-ken
 */
public class Serial extends Service<Void> implements SerialPortEventListener {

    private SerialListener serialListener_;

    private final WebEditor webEditor_;
    private BlockingQueue<String> out_;
    private BlockingQueue<Integer> line_;
    private BlockingQueue<Character> cout_;
    private SerialPort port_;
    private boolean owned_, onecharacter_, observects_, observedsr_, softflow_, printflow_, fileflow_, bufferflow_;
    private BlockingQueue<Integer> printdata_;
    private int bufferlimit_;
    private long delay_;
    private StringBuilder printtext_;

    private BufferedReader bufferedReader_;
    private BufferedWriter bufferedWriter_;
    private BufferedWriter bufferedWriterLog_;

    /**
     *
     * @param editor
     */
    public Serial(WebEditor editor) {
        serialListener_ = null;
        webEditor_ = editor;
        port_ = null;
        owned_ = false;
        onecharacter_ = false;
        observects_ = false;
        observedsr_ = false;
        softflow_ = false;
        printflow_ = false;
        fileflow_ = false;
        bufferflow_ = false;
        bufferlimit_ = 0;
        delay_ = 0;
        printtext_ = null;
        bufferedReader_ = null;
        bufferedWriter_ = null;
        bufferedWriterLog_ = null;
    }

    /**
     *
     * @param listener
     */
    public void addSerialListener(SerialListener listener) {
        serialListener_ = listener;
    }

    /**
     *
     * @param listener
     */
    public void removeSerialListener(SerialListener listener) {
        serialListener_ = null;
    }

    /**
     *
     * @return
     */
    public boolean isOwned() {
        return owned_;
    }

    /**
     *
     * @return
     */
    public boolean isClearToSend() {
        return port_.isCTS();
    }

    /**
     *
     * @return
     */
    public boolean isDataSetReady() {
        return port_.isDSR();
    }

    /**
     *
     * @return
     */
    public boolean isBufferFlow() {
        return bufferflow_;
    }

    /**
     *
     * @return
     */
    public List<String> getPort() {
        String portName;
        boolean portOwned;

        List<String> retPortName = new ArrayList<>();
        Enumeration enm = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier cpi;

        while (enm.hasMoreElements()) {
            // get port of list
            cpi = (CommPortIdentifier) enm.nextElement();

            // get port name
            portName = cpi.getName();

            // true = used / false = unused
            portOwned = cpi.isCurrentlyOwned();

            // add port
            if (!portOwned) {
                retPortName.add(portName);
            }
        }
        return retPortName;
    }

    /**
     *
     * @param name
     * @param baud
     * @param databits
     * @param stopbits
     * @param parity
     * @param bufferlimit
     * @param delay
     * @param character
     * @param observects
     * @param observedsr
     * @return
     */
    public boolean open(String name, String baud, String databits, String stopbits, String parity, String bufferlimit, String delay, boolean character, boolean observects, boolean observedsr) {
        if ((name != null) && (baud != null) && (databits != null) && (stopbits != null) && (parity != null) && (bufferlimit != null) && (delay != null)) {
            if (!owned_ && !name.isEmpty() && !baud.isEmpty() && !databits.isEmpty() && !stopbits.isEmpty() && !parity.isEmpty() && !bufferlimit.isEmpty() && !delay.isEmpty()) {
                try {
                    CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(name.trim());
                    CommPort com = id.open(SerialEnums.SERIAL.toString(), -1);
                    port_ = (SerialPort) com;

                    int[] param = new int[4];

                    param[0] = Integer.parseInt(baud.trim());
                    switch (databits.trim()) {
                        case "5":
                            param[1] = SerialPort.DATABITS_5;
                            break;
                        case "6":
                            param[1] = SerialPort.DATABITS_6;
                            break;
                        case "7":
                            param[1] = SerialPort.DATABITS_7;
                            break;
                        case "8":
                            param[1] = SerialPort.DATABITS_8;
                            break;
                        default:
                            param[1] = 0;
                            break;
                    }
                    switch (stopbits.trim()) {
                        case "1":
                            param[2] = SerialPort.STOPBITS_1;
                            break;
                        case "2":
                            param[2] = SerialPort.STOPBITS_2;
                            break;
                        case "1.5":
                            param[2] = SerialPort.STOPBITS_1_5;
                            break;
                        default:
                            param[2] = 0;
                    }
                    switch (parity.trim()) {
                        case "NONE":
                            param[3] = SerialPort.PARITY_NONE;
                            break;
                        case "ODD":
                            param[3] = SerialPort.PARITY_ODD;
                            break;
                        case "EVEN":
                            param[3] = SerialPort.PARITY_EVEN;
                            break;
                        case "MARK":
                            param[3] = SerialPort.PARITY_MARK;
                            break;
                        case "SPACE":
                            param[3] = SerialPort.PARITY_SPACE;
                            break;
                        default:
                            param[3] = 0;
                            break;
                    }

                    port_.setSerialPortParams(
                            param[0],
                            param[1],
                            param[2],
                            param[3]
                    );
                    port_.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                    printtext_ = new StringBuilder();
                    bufferedReader_ = new BufferedReader(new InputStreamReader(port_.getInputStream(), "UTF-8"));
                    bufferedWriter_ = new BufferedWriter(new OutputStreamWriter(port_.getOutputStream(), "UTF-8"));
                    // input buffer clear
                    while (bufferedReader_.ready()) {
                        bufferedReader_.read();
                    }

                    port_.addEventListener(this);
                    port_.notifyOnDataAvailable(false);
                    port_.notifyOnOutputEmpty(false);
                    port_.notifyOnBreakInterrupt(true);
                    port_.notifyOnCarrierDetect(false);
                    port_.notifyOnCTS(true);
                    port_.notifyOnDSR(true);
                    port_.notifyOnFramingError(true);
                    port_.notifyOnOverrunError(true);
                    port_.notifyOnParityError(true);
                    port_.notifyOnRingIndicator(false);

                    port_.setDTR(true);
                    port_.setRTS(true);

                    out_ = new LinkedBlockingQueue<>();
                    line_ = new LinkedBlockingQueue<>();
                    cout_ = new LinkedBlockingQueue<>();
                    printdata_ = new LinkedBlockingQueue<>();

                    bufferlimit_ = Integer.parseInt(bufferlimit.trim());
                    delay_ = Long.parseLong(delay.trim());
                    onecharacter_ = character;
                    observects_ = observects;
                    observedsr_ = observedsr;
                    owned_ = true;
                    return true;
                } catch (NoSuchPortException ex) {
                    writeLog(SerialEnums.SERIAL_PORT_NOT_FOUND.toString(), true);
                } catch (PortInUseException ex) {
                    writeLog(SerialEnums.SERIAL_PORT_USED.toString(), true);
                } catch (UnsupportedCommOperationException ex) {
                    writeLog(SerialEnums.SERIAL_NOT_SUPPORT.toString(), true);
                } catch (IOException | TooManyListenersException ex) {
                    Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public void close() {
        if (port_ != null) {
            port_.setRTS(false);
            port_.setDTR(false);

            port_.notifyOnDataAvailable(false);
            port_.notifyOnOutputEmpty(false);
            port_.notifyOnBreakInterrupt(false);
            port_.notifyOnCarrierDetect(false);
            port_.notifyOnCTS(false);
            port_.notifyOnDSR(false);
            port_.notifyOnFramingError(false);
            port_.notifyOnOverrunError(false);
            port_.notifyOnParityError(false);
            port_.notifyOnRingIndicator(false);
            port_.removeEventListener();
            port_.close();
            port_ = null;
        }

        if (printtext_ != null) {
            printtext_.delete(0, printtext_.length());
            printtext_ = null;
        }

        if (bufferedReader_ != null) {
            try {
                bufferedReader_.close();
                bufferedReader_ = null;
            } catch (IOException ex) {
                Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
            }
        }

        if (bufferedWriter_ != null) {
            try {
                bufferedWriter_.close();
                bufferedWriter_ = null;
            } catch (IOException ex) {
                Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
            }
        }

        clearFlow();
        clear();

        out_ = null;
        line_ = null;
        cout_ = null;
        printdata_ = null;
        onecharacter_ = false;
        observects_ = false;
        observedsr_ = false;
        owned_ = false;
    }

    /**
     *
     * @param file
     */
    public void setLogFile(Path file) {
        try {
            if (file == null) {
                if (bufferedWriterLog_ != null) {
                    bufferedWriterLog_.close();
                }
                bufferedWriterLog_ = null;
            } else {
                bufferedWriterLog_ = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file), "UTF-8"));
            }
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
        } catch (IOException ex) {
            Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
        }
    }

    /**
     *
     * @param state
     */
    public void setSoftFlow(boolean state) {
        softflow_ = state;
    }

    /**
     *
     * @param state
     */
    public void setPrintFlow(boolean state) {
        printflow_ = state;
    }

    /**
     *
     * @param state
     */
    public void setFileFlow(boolean state) {
        fileflow_ = state;
    }

    /**
     *
     * @param state
     */
    public void setBufferFlow(boolean state) {
        bufferflow_ = state;
    }

    /**
     *
     * @return
     */
    public int sizePrintData() {
        return printdata_.size();
    }

    /**
     *
     * @return
     */
    public Integer pollPrintData() {
        return printdata_.poll();
    }

    /**
     *
     * @return
     */
    public int size() {
        if (isEmpty()) {
            return -1;
        }

        if (cout_.isEmpty()) {
            return out_.size();
        } else {
            return (out_.size() + 1);
        }
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        if ((out_ == null) || (cout_ == null)) {
            return true;
        }
        return (out_.isEmpty() && cout_.isEmpty());
    }

    /**
     *
     * @return
     */
    public boolean isObserveCTS() {
        return observects_;
    }

    /**
     *
     * @return
     */
    public boolean isObserveDSR() {
        return observedsr_;
    }

    /**
     *
     * @param e
     * @param l
     */
    public void offer(String e, int l) {
        out_.offer(e);
        line_.offer(l);

        if ((bufferlimit_ > 0) && (out_.size() >= bufferlimit_)) {
            bufferflow_ = true;
        }
    }

    /**
     *
     */
    public void clear() {
        if (out_ != null) {
            out_.clear();
        }

        if (line_ != null) {
            line_.clear();
        }

        if (cout_ != null) {
            cout_.clear();
        }

        if (bufferedWriter_ != null) {
            try {
                bufferedWriter_.flush();
            } catch (IOException ex) {
                Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
            }
        }

        if (bufferedWriterLog_ != null) {
            try {
                bufferedWriterLog_.flush();
                bufferedWriterLog_.close();
                bufferedWriterLog_ = null;
            } catch (IOException ex) {
                Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
            }
        }
    }

    /**
     *
     */
    public void clearFlow() {
        softflow_ = false;
        printflow_ = false;
        fileflow_ = false;
        bufferflow_ = false;
    }

    /**
     *
     * @param spe
     */
    @Override
    public void serialEvent(SerialPortEvent spe) {
        switch (spe.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                dataAvailable(spe);
                break;
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                outputBufferEmpty(spe);
                break;
            case SerialPortEvent.BI:
                breakInterrupt(spe);
                break;
            case SerialPortEvent.CD:
                carrierDetect(spe);
                break;
            case SerialPortEvent.CTS:
                clearToSend(spe);
                break;
            case SerialPortEvent.DSR:
                dataSetReady(spe);
                break;
            case SerialPortEvent.FE:
                framingError(spe);
                break;
            case SerialPortEvent.OE:
                overrunError(spe);
                break;
            case SerialPortEvent.PE:
                parityError(spe);
                break;
            case SerialPortEvent.RI:
                ringIndicator(spe);
                break;
            default:
                break;
        }
    }

    /**
     * dataAvailable
     *
     * @param spe
     */
    protected void dataAvailable(SerialPortEvent spe) {
        writeLog("dataAvailable", false);
    }

    /**
     * outputBufferEmpty
     *
     * @param spe
     */
    protected void outputBufferEmpty(SerialPortEvent spe) {
        writeLog("outputBufferEmpty", false);
    }

    /**
     * breakInterrupt
     *
     * @param spe
     */
    protected void breakInterrupt(SerialPortEvent spe) {
        writeLog("breakInterrupt", false);
    }

    /**
     * carrierDetect
     *
     * @param spe
     */
    protected void carrierDetect(SerialPortEvent spe) {
        writeLog("carrierDetect: " + port_.isCD(), false);
    }

    /**
     * clearToSend
     *
     * @param spe
     */
    protected void clearToSend(SerialPortEvent spe) {
//        writeLog("clearToSend: " + port_.isCTS(), false);
        if (observects_ && !port_.isCTS()) {
            printWrite();

            clearFlow();
            clear();
        }
    }

    /**
     * dataSetReady
     *
     * @param spe
     */
    protected void dataSetReady(SerialPortEvent spe) {
//        writeLog("dataSetReady: " + port_.isDSR(), false);
        if (observedsr_ && !port_.isDSR()) {
            printWrite();

            clearFlow();
            clear();
        }
    }

    /**
     * framingError
     *
     * @param spe
     */
    protected void framingError(SerialPortEvent spe) {
        writeLog("framingError", true);
    }

    /**
     * overrunError
     *
     * @param spe
     */
    protected void overrunError(SerialPortEvent spe) {
        writeLog("overrunError", true);
    }

    /**
     * parityError
     *
     * @param spe
     */
    protected void parityError(SerialPortEvent spe) {
        writeLog("parityError", true);
    }

    /**
     * ringIndicator
     *
     * @param spe
     */
    protected void ringIndicator(SerialPortEvent spe) {
        writeLog("ringIndicator: " + port_.isRI(), false);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                while (owned_) {
                    try {
                        // recv
                        while (bufferedReader_.ready()) {
                            int read = bufferedReader_.read();
                            switch (read) {
                                case 0x11:
                                    softflow_ = true;
                                    break;
                                case 0x12:
                                    printflow_ = true;
                                    printdata_.clear();
                                    fileflow_ = false;
                                    break;
                                case 0x13:
                                    softflow_ = false;
                                    break;
                                case 0x14:
                                    printWrite();
                                    break;
                                default:
                                    if (printflow_) {
                                        printdata_.offer(read);
                                        if (read == '%') {
                                            if (fileflow_) {
                                                printWrite();
                                            } else {
                                                fileflow_ = true;
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    } catch (IOException ex) {
                    }

                    if (softflow_ && port_.isCTS()) {
                        if (!cout_.isEmpty()) {
                            try {
                                if (bufferedWriterLog_ != null) {
                                    bufferedWriterLog_.append(cout_.peek());
                                }
                                bufferedWriter_.write(cout_.poll());
                                bufferedWriter_.flush();
                            } catch (IOException ex) {
                            }
                        }

                        if (cout_.isEmpty()) {
                            if (out_.isEmpty()) {
                                // start
                                serialListener_.startSerial();
                            } else {
                                if (onecharacter_) {
                                    for (char c : out_.poll().toCharArray()) {
                                        cout_.offer(c);
                                    }
                                } else {
                                    try {
                                        if (bufferedWriterLog_ != null) {
                                            bufferedWriterLog_.append(out_.peek());
                                        }
                                        bufferedWriter_.write(out_.poll());
                                        bufferedWriter_.flush();
                                    } catch (IOException ex) {
                                        Console.writeStackTrace(SerialEnums.SERIAL.toString(), ex);
                                    }
                                }

                                if ((line_ != null) && (!line_.isEmpty())) {
                                    Platform.runLater(() -> {
                                        if ((line_ != null) && (!line_.isEmpty())) {
                                            int ln = line_.poll();
                                            if ((webEditor_ != null) && (ln > -1)) {
                                                webEditor_.gotoLine(ln, 0, true, false);
                                            }
                                        }
                                    });
                                }

                                if ((bufferflow_) && (out_.size() < bufferlimit_)) {
                                    bufferflow_ = false;
                                }
                            }
                        }
                    }

                    // sleep
                    try {
                        TimeUnit.MILLISECONDS.sleep(delay_);
                    } catch (InterruptedException ex) {
                    }
                }
                return null;
            }
        };
    }

    private void printWrite() {
        boolean timeout = false;
        long time = System.currentTimeMillis() + 3000;

        while (!timeout) {
            try {
                if (bufferedReader_.ready()) {
                    printdata_.offer(bufferedReader_.read());
                    time = System.currentTimeMillis() + 3000;
                }
            } catch (IOException ex) {
            }

            if (time < System.currentTimeMillis()) {
                timeout = true;
            }
        }

        if (printflow_) {
            if (!printdata_.isEmpty()) {
                if (fileflow_) {
                    char c;
                    printtext_.delete(0, printtext_.length());
                    while (!printdata_.isEmpty()) {
                        c = (char) printdata_.poll().byteValue();
                        switch (c) {
                            case 0x00:
                            case 0x02:
                            case 0x03:
                            case 0x11:
                            case 0x12:
                            case 0x13:
                            case 0x14:
                            case 0x1b:
                                break;
                            case '\r':
                                printtext_.append("\\r");
                                break;
                            case '\n':
                                printtext_.append("\\n");
                                break;
                            case '\t':
                                printtext_.append("\\t");
                                break;
                            case '\'':
                                printtext_.append("\\'");
                                break;
                            default:
                                printtext_.append(c);
                                break;
                        }
                    }
                    printdata_.clear();
                    Platform.runLater(() -> {
                        if ((webEditor_ != null) && (printtext_ != null)) {
                            webEditor_.insert(printtext_.toString());
                        }
                    });
                    fileflow_ = false;
                } else {
                    StringBuilder text = new StringBuilder();
                    while (!printdata_.isEmpty()) {
                        text.append(Character.toString((char) printdata_.poll().byteValue()));
                    }
                    writeLog(text.toString(), false);
                    printdata_.clear();
                }
            }
            printflow_ = false;
        }
    }

    private void writeLog(final String msg, final boolean err) {
        Console.write(SerialEnums.SERIAL.toString(), msg, err);
    }
}
