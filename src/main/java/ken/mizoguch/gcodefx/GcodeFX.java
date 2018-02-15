/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author mizoguch-ken
 */
public class GcodeFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        JavaLibrary.setClassName(getClass().getSimpleName());

        List<Image> icons = new ArrayList<>();
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_016.png")));
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_032.png")));
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_048.png")));
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_128.png")));
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_256.png")));
        icons.add(new Image(getClass().getClassLoader().getResourceAsStream("icons/icon_512.png")));

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Design.fxml"));
        Parent root = (Parent) loader.load();
        DesignController controller = (DesignController) loader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        if (!icons.isEmpty()) {
            stage.getIcons().addAll(icons);
        }
        stage.show();
        controller.startUp(stage, icons, getParameters());
    }

    @Override
    public void stop() {
        Platform.exit();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
