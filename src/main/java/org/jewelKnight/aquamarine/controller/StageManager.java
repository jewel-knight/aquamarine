package com.impactcn.aquamarine.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

/**
 * @author impactCn
 * @date 2023/10/14 23:02
 */
@Component
public class StageManager {
    private Stage mainStage;

    public Stage getMainStage() {
        return mainStage;
    }
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public Pair<Double, Double> getCenterPoint() {
        double centerX = mainStage.getX() + mainStage.getWidth() / 2;
        double centerY = mainStage.getY() + mainStage.getHeight() / 2;
        return new Pair<>(centerX, centerY);
    }


}
