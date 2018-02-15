/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.util.EventListener;
import netscape.javascript.JSObject;

/**
 *
 * @author mizoguch-ken
 */
public interface WebEditorPlugin {

    public void addWebEditorListener(EventListener listener);

    public void removeWebEditorListener(EventListener listener);

    public JSObject getEditor();

    public void fileNew();

    public Boolean fileOpen(String path);

    public Boolean fileSave(String path);

    public String getFileName();
}
