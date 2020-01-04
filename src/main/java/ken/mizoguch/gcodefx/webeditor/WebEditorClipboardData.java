/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author mizoguch-ken
 */
public class WebEditorClipboardData {

    private final Clipboard clipboard_;
    private Transferable clipboardContent_;

    /**
     *
     */
    public WebEditorClipboardData() {
        clipboard_ = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboardContent_ = null;
    }

    /**
     *
     * @param format
     * @return
     * @throws java.awt.datatransfer.UnsupportedFlavorException
     * @throws java.io.IOException
     */
    public String getData(String format) throws UnsupportedFlavorException, IOException {
        String ret = null;

        switch (format) {
            case "Text":
            case "text/plain":
                if (clipboard_.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    clipboardContent_ = new StringSelection((String) clipboard_.getData(DataFlavor.stringFlavor));
                }
                ret = (String) clipboardContent_.getTransferData(DataFlavor.stringFlavor);
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
                clipboardContent_ = new StringSelection(data);
                clipboard_.setContents(clipboardContent_, null);
                break;
            default:
                break;
        }
        return ret;
    }
}
