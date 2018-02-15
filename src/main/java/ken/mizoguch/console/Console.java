/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.console;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 * @author mizoguch-ken
 */
public class Console {

    private static TextArea textArea_ = null;
    private static PrintStream printStream_ = null;

    /**
     *
     * @param textArea
     */
    public static void setTextArea(TextArea textArea) {
        textArea_ = textArea;
    }

    /**
     *
     * @param file
     * @return
     */
    public static boolean setPrintStream(Path file) {
        if (file == null) {
            if (printStream_ != null) {
                printStream_.close();
                printStream_ = null;
            }
        } else if (Files.exists(file.getParent())) {
            try {
                printStream_ = new PrintStream(Files.newOutputStream(file), true, "UTF-8");
                return true;
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     *
     */
    public static void close() {
        setTextArea(null);
        setPrintStream(null);
    }

    /**
     *
     * @param name
     * @param throwable
     */
    public static void writeStackTrace(final String name, final Throwable throwable) {
        if ((textArea_ == null) && (printStream_ == null)) {
            Logger.getLogger(name).log(Level.SEVERE, null, throwable);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            for (StackTraceElement ste : throwable.getStackTrace()) {
                if (ste.getClassName().equals(name)) {
                    stringWriter.append(simpleDateFormat.format(new Date()) + " " + name + " " + ste.getMethodName() + "\n");
                    break;
                }
            }
            throwable.printStackTrace(printWriter);

            if (textArea_ != null) {
                if (Platform.isFxApplicationThread()) {
                    runWriteStackTrace(stringWriter.getBuffer().toString());
                } else {
                    Platform.runLater(() -> {
                        runWriteStackTrace(stringWriter.getBuffer().toString());
                    });
                }
            }

            if (printStream_ != null) {
                printStream_.println(stringWriter.getBuffer().toString());
            }
        }
    }

    /**
     *
     * @param name
     * @param msg
     * @param err
     */
    public static void write(final String name, String msg, final boolean err) {
        if (textArea_ != null) {
            if (Platform.isFxApplicationThread()) {
                runWrite(name, msg, err);
            } else {
                Platform.runLater(() -> {
                    runWrite(name, msg, err);
                });
            }
        }

        if (printStream_ != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
            if (err) {
                printStream_.println(simpleDateFormat.format(new Date()) + " :: " + name + " Error :: " + msg);
            } else {
                printStream_.println(simpleDateFormat.format(new Date()) + " :: " + name + " :: " + msg);
            }
        }
    }

    private static void runWriteStackTrace(String stackTrace) {
        if (textArea_ != null) {
            textArea_.appendText(stackTrace);

            // Limit row count
            String sprt = "\n";
            String s[] = textArea_.getText().split(sprt);
            if (s.length > 100) {
                textArea_.deleteText(0, s[0].length() + sprt.length());
                textArea_.appendText("");
            }
        }
    }

    private static void runWrite(String name, String msg, boolean err) {
        if (textArea_ != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
            if (err) {
                textArea_.appendText(simpleDateFormat.format(new Date()) + " :: " + name + " Error :: " + msg + "\n");
            } else {
                textArea_.appendText(simpleDateFormat.format(new Date()) + " :: " + name + " :: " + msg + "\n");
            }

            // Limit row count
            String sprt = "\n";
            String s[] = textArea_.getText().split(sprt);
            if (s.length > 100) {
                textArea_.deleteText(0, s[0].length() + sprt.length());
                textArea_.appendText("");
            }
        }
    }
}
