/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

import java.util.EventListener;

/**
 *
 * @author mizoguch-ken
 */
public interface WebEditorListener extends EventListener {

    /**
     *
     * @param getValue
     * @param isClean
     */
    public void notifyChangedWebEditor(String getValue, boolean isClean);

    /**
     *
     */
    public void execFileNewWebEditor();

    /**
     *
     */
    public void execFileOpenWebEditor();

    /**
     *
     */
    public void execFileSaveWebEditor();

    /**
     *
     */
    public void execFileSaveAsWebEditor();

    /**
     *
     */
    public void execNextGenerationController();
}
