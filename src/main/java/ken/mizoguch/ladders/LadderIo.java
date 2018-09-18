/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import ken.mizoguch.console.Console;
import netscape.javascript.JSException;

/**
 *
 * @author mizoguch-ken
 */
public class LadderIo {

    private WebEngine webEngine_;
    private Worker.State state_;

    private String address_, comment_;
    private double value_;

    private double cumulativeValue_;
    private boolean isDefaultLd_, isLd_, isLdRising_, isLdFalling_, isOldLd_;

    private Object scriptObject_;
    private final StringBuilder script_;
    private final Object lockScript_;
    private boolean isCycled_;

    private final String undefined_ = "undefined";

    /**
     *
     * @param address
     */
    public LadderIo(String address) {
        webEngine_ = null;
        state_ = null;
        address_ = address;
        comment_ = "";
        value_ = 0.0;
        cumulativeValue_ = 0.0;
        isDefaultLd_ = false;
        isLd_ = false;
        isLdRising_ = false;
        isLdFalling_ = false;
        isOldLd_ = false;
        script_ = new StringBuilder();
        lockScript_ = new Object();
        isCycled_ = false;
    }

    /**
     *
     * @param webEngine
     * @param state
     */
    public void setWebEngineState(WebEngine webEngine, Worker.State state) {
        webEngine_ = webEngine;
        state_ = state;
    }

    /**
     *
     * @return
     */
    public String getAddress() {
        return address_;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        address_ = address;
    }

    /**
     *
     * @return
     */
    public String getComment() {
        return comment_;
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment) {
        comment_ = comment;
    }

    /**
     *
     */
    public void chehckEdge() {
        isLdRising_ = (isLd_ && !isOldLd_);
        isLdFalling_ = (!isLd_ && isOldLd_);
    }

    /**
     *
     * @return
     */
    public double getValue() {
        return value_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValue(double value) {
        isDefaultLd_ = false;
        value_ = value;
        isLd_ = (value != 0.0);
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValueNot(double value) {
        isDefaultLd_ = true;
        value_ = value;
        isLd_ = (value != 0.0);
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @return
     */
    public boolean resetValue() {
        isLd_ = isDefaultLd_;
        value_ = 0.0;
        cumulativeValue_ = 0.0;
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValueRising(double value) {
        isDefaultLd_ = false;
        isLd_ = (value != 0.0) && (value_ == 0.0);
        value_ = value;
        chehckEdge();
        isOldLd_ = isLd_;
        return isLdRising_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValueRisingNot(double value) {
        isDefaultLd_ = true;
        isLd_ = !((value == 0.0) && (value_ != 0.0));
        value_ = value;
        chehckEdge();
        isOldLd_ = isLd_;
        return isLdRising_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValueFalling(double value) {
        isDefaultLd_ = false;
        isLd_ = (value == 0.0) && (value_ != 0.0);
        value_ = value;
        chehckEdge();
        isOldLd_ = isLd_;
        return isLdFalling_;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean setValueFallingNot(double value) {
        isDefaultLd_ = true;
        isLd_ = !((value != 0.0) && (value_ == 0.0));
        value_ = value;
        chehckEdge();
        isOldLd_ = isLd_;
        return isLdFalling_;
    }

    /**
     *
     * @param value
     * @param time
     * @param cycleTime
     * @return
     */
    public boolean setValueTimer(double value, double time, long cycleTime) {
        isDefaultLd_ = false;
        if (value != 0.0) {
            cumulativeValue_ += (cycleTime / 1000000.0);
            if ((time <= cumulativeValue_)) {
                value_ = value;
                isLd_ = true;
            } else {
                isLd_ = false;
                value_ = 0.0;
            }
        } else {
            cumulativeValue_ = 0.0;
            value_ = 0.0;
            isLd_ = false;
        }
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param value
     * @param time
     * @param cycleTime
     * @return
     */
    public boolean setValueTimerNot(double value, double time, long cycleTime) {
        isDefaultLd_ = true;
        if (value != 0.0) {
            cumulativeValue_ += (cycleTime / 1000000.0);
            if (time > cumulativeValue_) {
                value_ = value;
                isLd_ = true;
            } else {
                value_ = 0.0;
                isLd_ = false;
            }
        } else {
            cumulativeValue_ = 0.0;
            value_ = 0.0;
            isLd_ = false;
        }
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param value
     * @param count
     * @return
     */
    public boolean setValueCounter(double value, double count) {
        isDefaultLd_ = false;
        if (value != 0.0) {
            cumulativeValue_++;
            if (count <= cumulativeValue_) {
                value_ = value;
                isLd_ = true;
            } else {
                value_ = 0.0;
                isLd_ = false;
            }
        } else {
            cumulativeValue_ = 0.0;
            value_ = 0.0;
            isLd_ = false;
        }
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param value
     * @param count
     * @return
     */
    public boolean setValueCounterNot(double value, double count) {
        isDefaultLd_ = true;
        if (value != 0.0) {
            cumulativeValue_++;
            if (count > cumulativeValue_) {
                value_ = value;
                isLd_ = true;
            } else {
                value_ = 0.0;
                isLd_ = false;
            }
        } else {
            cumulativeValue_ = 0.0;
            value_ = 0.0;
            isLd_ = false;
        }
        chehckEdge();
        isOldLd_ = isLd_;
        return isLd_;
    }

    /**
     *
     * @param script
     * @return
     */
    public boolean setScript(String script) {
        if (webEngine_ != null) {
            isDefaultLd_ = false;
            if (script != null) {
                synchronized (lockScript_) {
                    script_.append(script).append(";");
                }
                Platform.runLater(() -> {
                    String executeScript;

                    synchronized (lockScript_) {
                        executeScript = script_.toString();
                        script_.delete(0, script_.length());
                    }
                    if (executeScript.length() > 0) {
                        if (webEngine_ != null) {
                            if (state_ == Worker.State.SUCCEEDED) {
                                try {
                                    scriptObject_ = webEngine_.executeScript(executeScript);
                                    if (!undefined_.equals(scriptObject_)) {
                                        if (scriptObject_ instanceof Integer) {
                                            value_ = (Integer) scriptObject_;
                                        } else if (scriptObject_ instanceof Double) {
                                            value_ = (Double) scriptObject_;
                                        }
                                    }
                                } catch (JSException | ClassCastException ex) {
                                    Console.writeStackTrace(LadderEnums.LADDER.toString(), ex);
                                }
                            }
                            isLd_ = (value_ != 0.0);
                            chehckEdge();
                            isOldLd_ = isLd_;
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isLd() {
        return isLd_;
    }

    /**
     *
     * @return
     */
    public boolean isLdRising() {
        return isLdRising_;
    }

    /**
     *
     * @return
     */
    public boolean isLdFalling() {
        return isLdFalling_;
    }

    /**
     *
     * @return
     */
    public double getCumulativeValue() {
        return cumulativeValue_;
    }

    /**
     *
     * @return
     */
    public boolean isCycled() {
        return isCycled_;
    }

    /**
     *
     * @param isCycled
     */
    public void setCycled(boolean isCycled) {
        isCycled_ = isCycled;
    }
}
