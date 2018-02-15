/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin.gcodefx;

import java.nio.file.Path;

/**
 *
 * @author mizoguch-ken
 */
public interface StageSettingsPlugin {

    public boolean isStageMaximized();

    public void setStageMaximized(boolean bln);

    public double getStageWidth();

    public void setStageWidth(double value);

    public double getStageHeight();

    public void setStageHeight(double value);

    public double getRootWidth();

    public void setRootWidth(double value);

    public double getRootHeight();

    public void setRootHeight(double value);

    public boolean isViewControlPanel();

    public void setViewControlPanel(boolean state);

    public boolean isViewConsole();

    public void setViewConsole(boolean state);

    public String getConsole();

    public void setConsole(String value);

    public double getWebviewConsoleDividerPositions();

    public void setWebviewConsoleDividerPositions(double value);

    public void addEditor(Path file);
}
