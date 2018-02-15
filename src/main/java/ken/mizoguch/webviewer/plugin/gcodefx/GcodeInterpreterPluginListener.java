/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.util.EventListener;
import java.util.Map;

/**
 *
 * @author mizoguch-ken
 */
public interface GcodeInterpreterPluginListener extends EventListener {

    public void variablesGcodeInterpreter(Map<Integer, Map<String, String>> variables);

    public void exCommentGcodeInterpreter(String excomment);
}
