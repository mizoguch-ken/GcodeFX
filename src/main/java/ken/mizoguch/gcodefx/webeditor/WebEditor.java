/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import ken.mizoguch.console.Console;
import ken.mizoguch.gcodefx.DesignController;
import ken.mizoguch.gcodefx.JavaLibrary;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeBytecode;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeInterpreter;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVariable;
import ken.mizoguch.gcodefx.gcodevirtualmachine.GcodeVirtualMachine;
import ken.mizoguch.webviewer.plugin.gcodefx.BaseSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeFXWebViewerPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeInterpreterPluginListener;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodePlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.GcodeVirtualMachinePluginListener;
import ken.mizoguch.webviewer.plugin.gcodefx.LaddersPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.StageSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.VirtualMachineSettingsPlugin;
import ken.mizoguch.webviewer.plugin.gcodefx.WebEditorPlugin;
import ken.mizoguch.ladders.Ladders;
import ken.mizoguch.webviewer.plugin.gcodefx.SoemPlugin;
import ken.mizoguch.soem.Soem;
import ken.mizoguch.webviewer.WebViewer;
import ken.mizoguch.webviewer.plugin.WebViewerPlugin;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 *
 * @author mizoguch-ken
 */
public class WebEditor implements GcodeFXWebViewerPlugin, WebEditorPlugin, GcodePlugin {

    private WebEditorClipboardData webEditorClipboardData_;
    private WebEditorNotify webEditorNotify_;

    private JSObject window_;
    private JSObject editor_;

    private Stage stage_;
    private List<Image> icons_;
    private WebEngine webEngine_;
    private Worker.State workerState_;
    private Path webPath_, filePath_, startFile_;
    private String getValue_;

    private final List<WebViewerPlugin> plugins_;
    private final String newLineCharacter_;

    private boolean navigateFileStart_, reset_, isClean_, readOnly_;
    private int gotoLineLineNumber_, gotoLineColumn_;
    private boolean suppressGotoLine_, gotoLineCenter_, gotoLineAnimate_;

    private GcodeVirtualMachine gcodeVirtualMachine_;
    private GcodeInterpreter gcodeInterpreter_;
    private DesignController.StageSettings stageSettings_;
    private DesignController.BaseSettings baseSettings_;
    private DesignController.VirtualMachineSettings virtualMachineSettings_;
    private Ladders ladders_;
    private Soem soem_;

    private final String undefined_ = "undefined";

    /**
     *
     * @return
     */
    public String getNewLineCharacter() {
        return newLineCharacter_;
    }

    /**
     *
     * @param getValue
     * @param isClean
     * @param force
     */
    public void update(String getValue, boolean isClean, boolean force) {
        if (getValue != null) {
            getValue_ = getValue;
        }

        if (navigateFileStart_) {
            gotoLine(1, 0, false, false);
            navigateFileStart_ = false;
        }

        if ((isClean_ != isClean) || force || reset_) {
            isClean_ = isClean;

            if (reset_) {
                undoManager_reset();
                reset_ = false;
                isClean_ = true;
            }

            StringBuilder title = new StringBuilder();
            if (filePath_ != null) {
                if (Files.isRegularFile(filePath_)) {
                    title.append(filePath_.getFileName().toString());
                }
            }

            if (!isClean_) {
                title.append(" *");
            }

            stage_.setTitle(title.toString());
        }
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return getValue_;
    }

    /**
     *
     * @param value
     */
    public void setValue(String value) {
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("setValue"))) {
                    if (value == null) {
                        webEngine_.executeScript("setValue(null);");
                    } else {
                        webEngine_.executeScript("setValue('" + value + "');");
                    }
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     * @param text
     */
    public void insert(String text) {
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("insert"))) {
                    webEngine_.executeScript("insert('" + text + "');");
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     */
    public void undoManager_reset() {
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("undoManager_reset"))) {
                    webEngine_.executeScript("undoManager_reset();");
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     */
    public void undoManager_markClean() {
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("undoManager_markClean"))) {
                    webEngine_.executeScript("undoManager_markClean();");
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean undoManager_isClean() {
        return isClean_;
    }

    /**
     *
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        readOnly_ = readOnly;
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("setReadOnly"))) {
                    webEngine_.executeScript("setReadOnly(" + readOnly_ + ");");
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     * @param lineNumber
     * @param column
     * @param center
     * @param animate
     */
    public void gotoLine(int lineNumber, int column, boolean center, boolean animate) {
        gotoLineLineNumber_ = lineNumber;
        gotoLineColumn_ = column;
        gotoLineCenter_ = center;
        gotoLineAnimate_ = animate;
        if (workerState_ == Worker.State.SUCCEEDED) {
            if (!suppressGotoLine_) {
                suppressGotoLine_ = true;
                try {
                    if (!undefined_.equals(window_.getMember("gotoLine"))) {
                        webEngine_.executeScript("gotoLine(" + gotoLineLineNumber_ + "," + gotoLineColumn_ + "," + gotoLineCenter_ + "," + gotoLineAnimate_ + ");");
                    }
                } catch (JSException ex) {
                    Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
                }
                suppressGotoLine_ = false;
            }
        }
    }

    /**
     *
     * @param lang
     */
    public void setLanguage(DesignController.LANG lang) {
        if (workerState_ == Worker.State.SUCCEEDED) {
            try {
                if (!undefined_.equals(window_.getMember("setLanguage"))) {
                    webEngine_.executeScript("setLanguage('" + lang.toString() + "');");
                }
            } catch (JSException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public Path fileOpen(Path file) {
        if (file == null) {
            FileChooser fileChooser = new FileChooser();
            if (filePath_ != null) {
                if (filePath_.getParent() != null) {
                    if (Files.exists(filePath_.getParent())) {
                        fileChooser.setInitialDirectory(filePath_.getParent().toFile());
                    }
                }
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All types", "*"));
            File fcfile = fileChooser.showOpenDialog(stage_);
            if (fcfile == null) {
                return null;
            }
            file = fcfile.toPath();
        }

        if (Files.exists(file)) {
            filePath_ = file;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file), "UTF-8"))) {
                StringBuilder stringBuilder = new StringBuilder();
                String lines, line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    if (bufferedReader.ready()) {
                        stringBuilder.append(newLineCharacter_);
                    }
                }
                lines = stringBuilder.toString();
                lines = lines.replace("\\", "\\\\");
                lines = lines.replace(Character.toString((char) 0x00), "");
                lines = lines.replace(Character.toString((char) 0x02), "");
                lines = lines.replace(Character.toString((char) 0x03), "");
                lines = lines.replace(Character.toString((char) 0x11), "");
                lines = lines.replace(Character.toString((char) 0x12), "");
                lines = lines.replace(Character.toString((char) 0x13), "");
                lines = lines.replace(Character.toString((char) 0x14), "");
                lines = lines.replace(Character.toString((char) 0x1b), "");
                lines = lines.replace("\r", "\\r");
                lines = lines.replace("\n", "\\n");
                lines = lines.replace("\t", "\\t");
                lines = lines.replace("'", "\\'");
                navigateFileStart_ = true;
                reset_ = true;
                setValue(lines);
                webEditorNotify_.notifyFileOpen(file.toString());
                return file;
            } catch (FileNotFoundException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            } catch (JSException | IOException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Path fileSave() {
        if (filePath_ == null) {
            return fileSaveAs();
        } else if (Files.exists(filePath_)) {
            if (Files.isRegularFile(filePath_)) {
                return fileSave(filePath_);
            } else {
                return fileSaveAs();
            }
        } else {
            return fileSaveAs();
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public Path fileSave(Path file) {
        if (file != null) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(file), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(new StringReader(getValue_))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outputStreamWriter.write(line);
                    if (bufferedReader.ready()) {
                        outputStreamWriter.write(newLineCharacter_);
                    }
                }
                filePath_ = file;
                undoManager_markClean();
                update(null, true, true);
                webEditorNotify_.notifyFileSave(file.toString());
                return file;
            } catch (JSException | IOException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Path fileSaveAs() {
        FileChooser fileChooser = new FileChooser();

        if (filePath_ != null) {
            if (filePath_.getParent() != null) {
                if (Files.exists(filePath_.getParent())) {
                    fileChooser.setInitialDirectory(filePath_.getParent().toFile());
                }
            }
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All types", "*"));
        File fcfile = fileChooser.showSaveDialog(stage_);
        if (fcfile != null) {
            filePath_ = fcfile.toPath();
            return fileSave(filePath_);
        }
        return null;
    }

    private PDPage pdfNewPage(PDDocument doc, PDRectangle rectangle) {
        PDPage page = new PDPage(rectangle);
        doc.addPage(page);
        return page;
    }

    private PDPageContentStream pdfPageBeginText(PDDocument doc, PDPage page, PDFont font, float fontSize) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setLeading(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000f * fontSize);
        return contentStream;
    }

    private void pdfPageEndText(PDPageContentStream contentStream) throws IOException {
        contentStream.endText();
        contentStream.close();
    }

    /**
     *
     * @param file
     * @return
     */
    public Path exportPdf(Path file) {
        if (file != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new StringReader(getValue_));
                    PDDocument doc = new PDDocument();
                    TrueTypeCollection ttc = new TrueTypeCollection(getClass().getClassLoader().getResourceAsStream("font/MyricaM.TTC"))) {
                PDRectangle rectangle = PDRectangle.A4;
                PDPage page;
                PDPageContentStream contentStream;
                PDFont font = PDType0Font.load(doc, ttc.getFontByName("MyricaMM"), true);
                float margin = 20f * 25.4f / 72f;
                float fontSize = 12f;
                float textWidth, textHeight;
                float fontWidth = font.getFontDescriptor().getFontBoundingBox().getWidth() / 1000f * fontSize;
                float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000f * fontSize;
                float pageShowMinWidth = margin;
                float pageShowMaxWidth = rectangle.getWidth() - margin;
                float pageShowMinHeight = margin;
                float pageShowMaxHeight = rectangle.getHeight() - margin;
                String line, c;
                int index;

                page = pdfNewPage(doc, rectangle);
                textHeight = pageShowMinHeight;
                if (getFileName() != null) {
                    // page header title
                    contentStream = pdfPageBeginText(doc, page, font, fontSize);
                    contentStream.newLineAtOffset(((pageShowMaxWidth - (font.getStringWidth(getFileName()) / 1000 * fontSize)) / 2f) + pageShowMinWidth + fontWidth, pageShowMaxHeight - fontHeight);
                    contentStream.showText(getFileName());
                    pdfPageEndText(contentStream);
                    textHeight += fontHeight;
                }
                contentStream = pdfPageBeginText(doc, page, font, fontSize);
                contentStream.newLineAtOffset(pageShowMinWidth + fontWidth, pageShowMaxHeight - fontHeight - textHeight);
                while ((line = bufferedReader.readLine()) != null) {
                    textWidth = pageShowMinWidth + fontWidth;
                    textHeight += fontHeight;

                    if (textHeight > (pageShowMaxHeight - fontHeight)) {
                        pdfPageEndText(contentStream);
                        page = pdfNewPage(doc, rectangle);
                        contentStream = pdfPageBeginText(doc, page, font, fontSize);
                        contentStream.newLineAtOffset(pageShowMinWidth + fontWidth, pageShowMaxHeight - fontHeight);
                        textHeight = pageShowMinHeight + fontHeight;
                    }

                    for (index = 0; index < line.length(); index++) {
                        c = line.substring(index, index + 1);
                        textWidth += font.getStringWidth(c) / 1000 * fontSize;
                        if (textWidth > (pageShowMaxWidth - fontWidth)) {
                            textHeight += fontHeight;
                            if (textHeight > (pageShowMaxHeight - fontHeight)) {
                                pdfPageEndText(contentStream);
                                page = pdfNewPage(doc, rectangle);
                                contentStream = pdfPageBeginText(doc, page, font, fontSize);
                                contentStream.newLineAtOffset(pageShowMinWidth + fontWidth, pageShowMaxHeight - fontHeight);
                                textHeight = pageShowMinHeight + fontHeight;
                            }
                            contentStream.newLine();
                            textWidth = pageShowMinWidth + fontWidth;
                        }
                        contentStream.showText(c);
                    }

                    if (bufferedReader.ready()) {
                        contentStream.newLine();
                    }
                }
                pdfPageEndText(contentStream);

                // page footer count
                for (index = 0; index < doc.getNumberOfPages(); index++) {
                    line = (index + 1) + " / " + doc.getNumberOfPages();
                    contentStream = pdfPageBeginText(doc, doc.getPage(index), font, fontSize);
                    contentStream.newLineAtOffset(((pageShowMaxWidth - (font.getStringWidth(line) / 1000 * fontSize)) / 2f) + pageShowMinWidth + fontWidth, pageShowMinHeight);
                    contentStream.showText(line);
                    pdfPageEndText(contentStream);
                }

                doc.save(file.toFile());
                return file;
            } catch (JSException | IOException ex) {
                Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public Path exportPdf() {
        FileChooser fileChooser = new FileChooser();

        if (filePath_ != null) {
            if (filePath_.getParent() != null) {
                if (Files.exists(filePath_.getParent())) {
                    fileChooser.setInitialDirectory(filePath_.getParent().toFile());
                }
            }
            if (!Files.isDirectory(filePath_)) {
                fileChooser.setInitialFileName(JavaLibrary.removeFileExtension(filePath_.getFileName()));
            }
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File fcfile = fileChooser.showSaveDialog(stage_);
        if (fcfile != null) {
            return exportPdf(fcfile.toPath());
        }
        return null;
    }

    /**
     *
     * @param workFilePath
     */
    public void setWorkFilePath(String workFilePath) {
        if (workFilePath != null) {
            if (workFilePath.isEmpty()) {
                filePath_ = null;
            } else {
                filePath_ = Paths.get(workFilePath);
                if (!Files.isDirectory(filePath_)) {
                    filePath_ = filePath_.getParent();
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public String getWorkFilePath() {
        if (filePath_ == null) {
            return "";
        } else {
            return filePath_.toString();
        }
    }

    /**
     *
     * @param virtualMachine
     */
    public void setGcodeVirtualMachine(GcodeVirtualMachine virtualMachine) {
        gcodeVirtualMachine_ = virtualMachine;
    }

    /**
     *
     * @param interpreter
     */
    public void setGcodeInterpreter(GcodeInterpreter interpreter) {
        gcodeInterpreter_ = interpreter;
    }

    /**
     *
     * @param stageSettings
     */
    public void setStageSettings(DesignController.StageSettings stageSettings) {
        stageSettings_ = stageSettings;
    }

    /**
     *
     * @param baseSettings
     */
    public void setBaseSettings(DesignController.BaseSettings baseSettings) {
        baseSettings_ = baseSettings;
    }

    /**
     *
     * @param virtualMachineSettings
     */
    public void setVirtualMachineSettings(DesignController.VirtualMachineSettings virtualMachineSettings) {
        virtualMachineSettings_ = virtualMachineSettings;
    }

    /**
     *
     * @param ladders
     */
    public void setLadders(Ladders ladders) {
        ladders_ = ladders;
    }

    /**
     *
     * @param soem
     */
    public void setSoem(Soem soem) {
        soem_ = soem;
    }

    /**
     *
     * @param stage
     * @param icons
     * @param currentPath
     * @param webPath
     * @param startFile
     */
    public void startUp(Stage stage, List<Image> icons, Path currentPath, Path webPath, Path startFile) {
        stage_ = stage;
        icons_ = icons;
        webPath_ = webPath;
        startFile_ = startFile;

        String html = null;
        Path fileEditor = currentPath.resolve("webeditor");
        if (Files.exists(fileEditor)) {
            Path fileIndex = fileEditor.resolve("index.html");
            if (Files.exists(fileIndex)) {
                html = fileIndex.toUri().toString();
            } else {
                write(WebEditorEnums.WEB_EDITOR.toString(), "[" + fileIndex.toAbsolutePath().toString() + "] file is not found", true);
            }
        } else {
            write(WebEditorEnums.WEB_EDITOR.toString(), "[" + fileEditor.toAbsolutePath().toString() + "] folder is not found", true);
        }

        try {
            Path pluginPath = webPath_.resolve("plugins");
            if (Files.exists(pluginPath)) {
                Files.list(pluginPath).forEach((plugin) -> {
                    if (Files.isRegularFile(plugin)) {
                        try {
                            JarFile jarFile = new JarFile(plugin.toFile());
                            Manifest manifest = jarFile.getManifest();
                            if (manifest != null) {
                                String mainClass = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
                                if (mainClass != null) {
                                    URLClassLoader loader = URLClassLoader.newInstance(new URL[]{plugin.toUri().toURL()}, getClass().getClassLoader());
                                    Class<WebViewerPlugin> clazz = (Class<WebViewerPlugin>) loader.loadClass(mainClass);
                                    WebViewerPlugin webViewerPlugin = clazz.getDeclaredConstructor().newInstance();
                                    webViewerPlugin.initialize(this);
                                    plugins_.add(webViewerPlugin);
                                }
                            }
                        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
                        } catch (IOException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                            writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
                        }
                    }
                });
            }
        } catch (IOException ex) {
            writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
        }

        webEngine_.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            workerState_ = newValue;

            for (int index = 0, size = plugins_.size(); index < size; index++) {
                plugins_.get(index).state(workerState_);
            }
            if (workerState_ == Worker.State.SUCCEEDED) {
                try {
                    window_ = (JSObject) webEngine_.executeScript("window");
                    stage_.setTitle(webEngine_.getTitle());
                    window_.setMember("clipboardData", webEditorClipboardData_);
                    window_.setMember("gcodefx", webEditorNotify_);

                    for (int index = 0, size = plugins_.size(); index < size; index++) {
                        if (plugins_.get(index).functionName() != null) {
                            if (undefined_.equals(window_.getMember(plugins_.get(index).functionName()))) {
                                window_.setMember(plugins_.get(index).functionName(), plugins_.get(index));
                            }
                        }
                    }
                    navigateFileStart_ = false;
                    reset_ = false;
                    isClean_ = true;
                    setReadOnly(readOnly_);
                    if (startFile_ != null) {
                        if (!virtualMachineSettings_.isRunning() && !readOnly_) {
                            fileOpen(startFile_);
                        }
                        startFile_ = null;
                    }
                    if (!undefined_.equals(window_.getMember("getEditor"))) {
                        editor_ = (JSObject) webEngine_.executeScript("getEditor();");
                    }
                    if (!undefined_.equals(window_.getMember("setup"))) {
                        webEngine_.executeScript("setup();");
                    }
                    setLanguage(stageSettings_.getLanguage());
                } catch (JSException | ClassCastException | SecurityException ex) {
                    Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
                }
            } else if ((workerState_ == Worker.State.FAILED) && (webEngine_.getLoadWorker().getException() != null)) {
                write(WebEditorEnums.WEB_EDITOR.toString(), webEngine_.getLoadWorker().getException().toString(), true);
            }
        });

        if (html == null) {
            webEngine_.loadContent("<html><head></head><body><h1>Failed to load</h1></body></html>");
        } else {
            webEngine_.load(html);
        }
    }

    /**
     *
     */
    public void cleanUp() {
        for (int index = 0, size = plugins_.size(); index < size; index++) {
            plugins_.get(index).close();
        }
        plugins_.clear();
    }

    /**
     *
     * @param webView
     */
    public WebEditor(WebView webView) {
        webEditorClipboardData_ = new WebEditorClipboardData();
        webEditorNotify_ = new WebEditorNotify();

        getValue_ = "";
        newLineCharacter_ = "\r\n";

        webEngine_ = webView.getEngine();
        webEngine_.setJavaScriptEnabled(true);

        webEngine_.setCreatePopupHandler((PopupFeatures param) -> {
            Stage stage = new Stage(StageStyle.DECORATED);
            WebView view = new WebView();
            WebViewer viewer = new WebViewer(view);

            stage.setScene(new Scene(view));
            if (!icons_.isEmpty()) {
                stage.getIcons().addAll(icons_);
            }
            stage.setResizable(param.isResizable());
            stage.setOnCloseRequest((WindowEvent event) -> {
                viewer.cleanUp();
            });

            viewer.setWebEditor(this);
            viewer.setGcodeVirtualMachine(gcodeVirtualMachine_);
            viewer.setGcodeInterpreter(gcodeInterpreter_);
            viewer.setStageSettings(stageSettings_);
            viewer.setBaseSettings(baseSettings_);
            viewer.setVirtualMachineSettings(virtualMachineSettings_);
            viewer.setLadders(ladders_);
            viewer.setSoem(soem_);
            viewer.startUp(stage, icons_, webPath_);

            stage.show();
            return view.getEngine();
        });

        webEngine_.setOnAlert((WebEvent<String> event) -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setResizable(true);
            alert.setTitle("Alert");
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContentText(event.getData());
            alert.showAndWait();
        });

        webEngine_.setConfirmHandler((String param) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setResizable(true);
            alert.setTitle("Confirm");
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContentText(param);
            Optional<ButtonType> result = alert.showAndWait();
            return (result.get() == ButtonType.OK);
        });

        webEngine_.setPromptHandler((PromptData param) -> {
            TextInputDialog alert = new TextInputDialog(param.getDefaultValue());
            if (!icons_.isEmpty()) {
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons_);
            }
            alert.setResizable(true);
            alert.setTitle("Prompt");
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().setContentText(param.getMessage());
            Optional<String> result = alert.showAndWait();
            if (result.isPresent()) {
                return result.get();
            } else {
                return null;
            }
        });

        webEngine_.setOnError((WebErrorEvent event) -> {
            write(WebEditorEnums.WEB_EDITOR.toString(), event.getMessage(), true);
        });

        workerState_ = Worker.State.READY;
        plugins_ = new ArrayList<>();
    }

    @Override
    public void addWebEditorListener(EventListener listener) {
        webEditorNotify_.addWebEditorListener(listener);
    }

    @Override
    public void removeWebEditorListener(EventListener listener) {
        webEditorNotify_.removeWebEditorListener(listener);
    }

    @Override
    public JSObject getEditor() {
        return editor_;
    }

    @Override
    public void fileNew() {
        reset_ = true;
        filePath_ = null;
        setValue(null);
        webEditorNotify_.notifyFileNew();
    }

    @Override
    public Boolean fileOpen(String path) {
        return (fileOpen(Paths.get(path)) != null);
    }

    @Override
    public Boolean fileSave(String path) {
        return (fileSave(Paths.get(path)) != null);
    }

    @Override
    public String getFileName() {
        if (filePath_ == null) {
            return null;
        }
        if (Files.isDirectory(filePath_)) {
            return null;
        }
        return filePath_.getFileName().toString();
    }

    @Override
    public void initialize(WebViewerPlugin webViewer) {
    }

    @Override
    public String functionName() {
        return null;
    }

    @Override
    public void state(Worker.State state) {
    }

    @Override
    public void close() {
    }

    @Override
    public StageSettingsPlugin stageSettings() {
        return stageSettings_;
    }

    @Override
    public BaseSettingsPlugin baseSettings() {
        return baseSettings_;
    }

    @Override
    public VirtualMachineSettingsPlugin virtualMachineSettings() {
        return virtualMachineSettings_;
    }

    @Override
    public WebEditorPlugin webEditor() {
        return this;
    }

    @Override
    public GcodePlugin gcode() {
        return this;
    }

    @Override
    public Boolean script(String codes) {
        List<GcodeBytecode> bytecodesScript = new ArrayList<>();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(codes.getBytes("UTF-8"))) {
            if (GcodeVirtualMachine.addBytecodes(bytecodesScript, null, GcodeVirtualMachine.parse(byteArrayInputStream), 0, true)) {
                GcodeInterpreter lastGcodeInterpreter = gcodeInterpreter_.getLastGcodeInterpreter();
                lastGcodeInterpreter.cpyBytecodesScript(bytecodesScript);
                return true;
            }
        } catch (UnsupportedEncodingException ex) {
            Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
        } catch (IOException ex) {
            Console.writeStackTrace(WebEditorEnums.WEB_EDITOR.toString(), ex);
        }
        return false;
    }

    @Override
    public Double getVariable(int address) {
        GcodeVariable gv = gcodeInterpreter_.getLastGcodeInterpreter().getVariable(address);
        if (gv.isNil()) {
            return null;
        } else {
            return gv.getDouble();
        }
    }

    @Override
    public void setVariable(int address, Double value) {
        if (value == null) {
            gcodeInterpreter_.getLastGcodeInterpreter().setVariable(address, new GcodeVariable(address));
        } else {
            gcodeInterpreter_.getLastGcodeInterpreter().setVariable(address, gcodeInterpreter_.getLastGcodeInterpreter().getVariable(address).setDouble(value));
        }
    }

    @Override
    public void addGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener) {
        gcodeVirtualMachine_.addGcodeVirtualMachineListener(listener);
    }

    @Override
    public void removeGcodeVirtualMachineListener(GcodeVirtualMachinePluginListener listener) {
        gcodeVirtualMachine_.removeGcodeVirtualMachineListener(listener);
    }

    @Override
    public void addGcodeInterpreterListener(GcodeInterpreterPluginListener listener) {
        gcodeInterpreter_.addGcodeInterpreterListener(listener);
    }

    @Override
    public void removeGcodeInterpreterListener(GcodeInterpreterPluginListener listener) {
        gcodeInterpreter_.removeGcodeInterpreterListener(listener);
    }

    @Override
    public LaddersPlugin ladders() {
        return ladders_;
    }

    @Override
    public SoemPlugin soem() {
        return soem_;
    }

    @Override
    public Stage stage() {
        return stage_;
    }

    @Override
    public List<Image> icons() {
        return icons_;
    }

    @Override
    public WebEngine webEngine() {
        return webEngine_;
    }

    @Override
    public Path webPath() {
        return webPath_;
    }

    @Override
    public void writeStackTrace(String name, Throwable throwable) {
        Console.writeStackTrace(name, throwable);
    }

    @Override
    public void write(String name, String msg, boolean err) {
        Console.write(name, msg, err);
    }
}
