/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.util.EventListener;

/**
 *
 * @author mizoguch-ken
 */
public interface WebEditorPluginListener extends EventListener {

    public void notifyFileNew();

    public void notifyFileOpen(String filename);

    public void notifyFileSave(String filename);
}
