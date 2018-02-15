/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

/**
 *
 * @author mizoguch-ken
 */
public interface LaddersPlugin {

    public Boolean isRegistered(Object object);

    public void register(WebEngine webEngine, Worker.State state);

    public Boolean registerIn(Object... objects);

    public Boolean registerOut(Object... objects);

    public void unregister(Object object);

    public Double getValue(String address);

    public void setValue(String address, double value);
}
