/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.webviewer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.DesignController;
import ken.mizoguch.gcodefx.webeditor.WebEditor;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeInterpreter;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVirtualMachine;
import ken.mizoguch.ladders.Ladders;
import ken.mizoguch.soem.Soem;

/**
 * FXML Controller class
 *
 * @author mizoguch-ken
 */
public class DesignWebController implements Initializable {

    @FXML
    private WebView webView;

    private WebViewer webViewer_;
    private final String template_
            = "<!DOCTYPE html>" + "\n"
            + "<html>" + "\n"
            + "<head>" + "\n"
            + "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" + "\n"
            + "\t<meta charset=\"utf-8\">" + "\n"
            + "\t<title>GcodeFX</title>" + "\n"
            + "\t<meta name=\"description\" content=\"\">" + "\n"
            + "\t<meta name=\"author\" content=\"\">" + "\n"
            + "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + "\n"
            + "\t<link rel=\"stylesheet\" href=\"\">" + "\n"
            + "\t<!--[if lt IE 9]>" + "\n"
            + "\t\t<script src=\"//cdn.jsdelivr.net/html5shiv/3.7.2/html5shiv.min.js\"></script>" + "\n"
            + "\t\t<script src=\"//cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js\"></script>" + "\n"
            + "\t<![endif]-->" + "\n"
            + "\t<link rel=\"shortcut icon\" href=\"\">" + "\n"
            + "</head>" + "\n"
            + "<body>" + "\n"
            + "</body>" + "\n"
            + "</html>";

    /**
     *
     * @return
     */
    public WebViewer getWebViewer() {
        return webViewer_;
    }

    /**
     *
     * @param stageSettings
     */
    public void setStageSettings(DesignController.StageSettings stageSettings) {
        webViewer_.setStageSettings(stageSettings);
    }

    /**
     *
     * @param baseSettings
     */
    public void setBaseSettings(DesignController.BaseSettings baseSettings) {
        webViewer_.setBaseSettings(baseSettings);
    }

    /**
     *
     * @param virtualMachineSettings
     */
    public void setVirtualMachineSettings(DesignController.VirtualMachineSettings virtualMachineSettings) {
        webViewer_.setVirtualMachineSettings(virtualMachineSettings);
    }

    /**
     *
     * @param editor
     */
    public void setWebEditor(WebEditor editor) {
        webViewer_.setWebEditor(editor);
    }

    /**
     *
     * @param virtualMachine
     */
    public void setGcodeVirtualMachine(GcodeVirtualMachine virtualMachine) {
        webViewer_.setGcodeVirtualMachine(virtualMachine);
    }

    /**
     *
     * @param interpreter
     */
    public void setGcodeInterpreter(GcodeInterpreter interpreter) {
        webViewer_.setGcodeInterpreter(interpreter);
    }

    /**
     *
     * @param ladders
     */
    public void setLadders(Ladders ladders) {
        webViewer_.setLadders(ladders);
    }

    /**
     *
     * @param soem
     */
    public void setSoem(Soem soem) {
        webViewer_.setSoem(soem);
    }

    /**
     *
     * @param stage
     * @param icons
     * @param webPath
     */
    public void startUp(Stage stage, List<Image> icons, Path webPath) {
        Path indexPath;

        indexPath = Paths.get(webPath.toUri().resolve("index.html"));
        if (!Files.exists(indexPath)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(indexPath), "UTF-8")) {
                osw.write(template_);
            } catch (IOException ex) {
                Console.writeStackTrace(WebEnums.WEB.toString(), ex);
            }
        }
        webViewer_.startUp(stage, icons, webPath);
        webViewer_.load(indexPath.toUri().toString());
    }

    /**
     *
     */
    public void cleanUp() {
        webViewer_.cleanUp();
        webViewer_ = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webViewer_ = new WebViewer(webView);
    }
}
