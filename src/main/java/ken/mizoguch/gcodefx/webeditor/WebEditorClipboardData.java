/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 *
 * @author mizoguch-ken
 */
public class WebEditorClipboardData {

    private final ClipboardContent clipboardContent_;

    /**
     *
     */
    public WebEditorClipboardData() {
        clipboardContent_ = new ClipboardContent();
    }

    /**
     *
     * @param format
     * @return
     */
    public String getData(String format) {
        String ret = null;
        switch (format) {
            case "Text":
            case "text/plain":
                if (Clipboard.getSystemClipboard().hasString()) {
                    clipboardContent_.putString(Clipboard.getSystemClipboard().getString());
                }
                ret = clipboardContent_.getString();
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     *
     * @param format
     * @param data
     * @return
     */
    public boolean setData(String format, String data) {
        boolean ret = false;
        switch (format) {
            case "Text":
            case "text/plain":
                clipboardContent_.putString(data);
                ret = Clipboard.getSystemClipboard().setContent(clipboardContent_);
                break;
            default:
                break;
        }
        return ret;
    }
}
