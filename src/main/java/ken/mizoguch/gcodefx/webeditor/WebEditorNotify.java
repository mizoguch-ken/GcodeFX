/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

import java.util.EventListener;
import javax.swing.event.EventListenerList;
import ken.mizoguch.webviewer.plugin.gcodefx.WebEditorPluginListener;

/**
 *
 * @author mizoguch-ken
 */
public class WebEditorNotify {

    private final EventListenerList eventListenerList_;
    private final EventListenerList eventPluginListenerList_;

    /**
     *
     */
    public WebEditorNotify() {
        eventListenerList_ = new EventListenerList();
        eventPluginListenerList_ = new EventListenerList();
    }

    /**
     *
     * @param l
     */
    public void addWebEditorListener(EventListener l) {
        boolean isListener = false;

        if (l instanceof WebEditorListener) {
            for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
                if (listener == l) {
                    isListener = true;
                    break;
                }
            }
            if (!isListener) {
                eventListenerList_.add(WebEditorListener.class, (WebEditorListener) l);
            }
        }

        if (l instanceof WebEditorPluginListener) {
            for (WebEditorPluginListener listener : eventPluginListenerList_.getListeners(WebEditorPluginListener.class)) {
                if (listener == l) {
                    isListener = true;
                    break;
                }
            }
            if (!isListener) {
                eventPluginListenerList_.add(WebEditorPluginListener.class, (WebEditorPluginListener) l);
            }
        }
    }

    /**
     *
     * @param l
     */
    public void removeWebEditorListener(EventListener l) {
        if (l instanceof WebEditorListener) {
            eventListenerList_.remove(WebEditorListener.class, (WebEditorListener) l);
        }

        if (l instanceof WebEditorPluginListener) {
            eventPluginListenerList_.remove(WebEditorPluginListener.class, (WebEditorPluginListener) l);
        }
    }

    /**
     *
     * @param getValue
     * @param isClean
     */
    public void notifyChange(String getValue, boolean isClean) {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.notifyChangedWebEditor(getValue, isClean);
        }
    }

    /**
     *
     */
    public void notifyFileNew() {
        for (WebEditorPluginListener listener : eventPluginListenerList_.getListeners(WebEditorPluginListener.class)) {
            listener.notifyFileNew();
        }
    }

    /**
     *
     */
    public void execFileNew() {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.execFileNewWebEditor();
        }
    }

    /**
     *
     * @param filename
     */
    public void notifyFileOpen(String filename) {
        for (WebEditorPluginListener listener : eventPluginListenerList_.getListeners(WebEditorPluginListener.class)) {
            listener.notifyFileOpen(filename);
        }
    }

    /**
     *
     */
    public void execFileOpen() {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.execFileOpenWebEditor();
        }
    }

    /**
     *
     * @param filename
     */
    public void notifyFileSave(String filename) {
        for (WebEditorPluginListener listener : eventPluginListenerList_.getListeners(WebEditorPluginListener.class)) {
            listener.notifyFileSave(filename);
        }
    }

    /**
     *
     */
    public void execFileSave() {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.execFileSaveWebEditor();
        }
    }

    /**
     *
     */
    public void execFileSaveAs() {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.execFileSaveAsWebEditor();
        }
    }

    /**
     *
     */
    public void execNextGenerationController() {
        for (WebEditorListener listener : eventListenerList_.getListeners(WebEditorListener.class)) {
            listener.execNextGenerationController();
        }
    }
}
