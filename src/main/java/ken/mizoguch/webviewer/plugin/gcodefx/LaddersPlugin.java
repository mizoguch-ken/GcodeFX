/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.nio.file.Path;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

/**
 *
 * @author mizoguch-ken
 */
public interface LaddersPlugin {

    public int getHistoryGeneration();

    public void setHistoryGeneration(int historyGeneration);

    public long getIdealCycleTime();

    public void setIdealCycleTime(long idealCycleTime);

    public Boolean isRegistered(Object object);

    public void register(WebEngine webEngine, Worker.State state);

    public Boolean registerIn(Object... objects);

    public Boolean registerOut(Object... objects);

    public void unregister(Object object);

    public Double getValue(String address);

    public void setValue(String address, double value);

    public boolean connectLadder();

    public boolean runStartLadder();

    public void stopLadder();

    public boolean fileNew();

    public boolean fileOpen(Path filePath);

    public boolean fileSave();

    public String getFileName();
}
