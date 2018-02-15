/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer.plugin;

import java.nio.file.Path;
import java.util.List;
import javafx.concurrent.Worker;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

/**
 *
 * @author mizoguch-ken
 */
public interface WebViewerPlugin {

    // webviewer -> plugin
    public void initialize(WebViewerPlugin webViewer);

    public String functionName();

    public void state(Worker.State state);

    public void close();

    // plugin -> webviewer
    public Stage stage();

    public List<Image> icons();

    public WebEngine webEngine();

    public Path webPath();

    // console
    public void writeStackTrace(String name, Throwable throwable);

    public void write(String name, String msg, boolean err);
}
