/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx;

import javafx.application.Platform;

/**
 *
 * @author mizoguch-ken
 */
public class ShutdownHook extends Thread {

    final DesignController controller_;

    /**
     *
     * @param controller
     */
    public ShutdownHook(DesignController controller) {
        controller_ = controller;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            if (controller_ != null) {
                controller_.cleanUp();
            }
        });
    }
}
