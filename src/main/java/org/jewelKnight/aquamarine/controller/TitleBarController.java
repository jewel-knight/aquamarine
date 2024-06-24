package com.impactcn.aquamarine.controller;

import com.impactcn.aquamarine.controller.event.FlexibleWindowsEvent;
import com.impactcn.aquamarine.controller.event.Position;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/9/21 23:11
 */
@Component
public class TitleBarController implements Initializable {

    public Button icon;

    public HBox left;

    public HBox titleBarContainer;

    public Button close;

    public Button minimize;

    public Button restoreDown;

    @Autowired
    private StageManager stageManager;

    private double xOffset = 0;
    private double yOffset = 0;

    private volatile boolean isFullScreen = false;

    private double currHeight = 0;

    private double currWidth = 0;

    private double currX = 0;

    private double currY = 0;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTileBarContainerEvent();
        initIconEvent();
        initMinimizeEvent();
        initRestoreDownEvent();
        initCloseEvent();
    }

    private void initTileBarContainerEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), titleBarContainer);
        titleBarContainer.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.TOP, event.getSceneX(), event.getSceneY());
        });
        titleBarContainer.setOnMouseDragged(event ->  {

            // 如果是可拉伸的范围，则可进行窗口自由拉伸变大变小
            if (flexibleWindowsEvent.isWithin()) {
                flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY());
            } else {
                // 否则，自由移动窗口
                stageManager.getMainStage().setX(event.getScreenX() - xOffset);
                stageManager.getMainStage().setY(event.getScreenY() - yOffset);
            }

        });

        titleBarContainer.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBarContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                    if (isFullScreen) {
                        stageManager.getMainStage().setHeight(currHeight);
                        stageManager.getMainStage().setWidth(currWidth);
                        stageManager.getMainStage().setX(currX);
                        stageManager.getMainStage().setY(currY);
                        isFullScreen = false;
                    } else {
                        currHeight = stageManager.getMainStage().getHeight();
                        currWidth = stageManager.getMainStage().getWidth();
                        currX = stageManager.getMainStage().getX();
                        currY = stageManager.getMainStage().getY();
                        toggleFullScreen(stageManager.getMainStage());
                        isFullScreen = true;

                    }
                }
            }
        });

    }

    private void toggleFullScreen(Stage stage) {
        Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).forEach(screen -> {
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            stage.setWidth(screen.getVisualBounds().getWidth());
            stage.setHeight(screen.getVisualBounds().getHeight());
        });

    }

    private void initIconEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), icon);
        icon.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.TOP, event.getSceneX(), event.getSceneY());

            if (flexibleWindowsEvent.isWithin()) {
                return;
            }
            flexibleWindowsEvent.setOnMouseMoved(Position.LEFT, event.getSceneX(), event.getSceneY());

            if (flexibleWindowsEvent.isWithin()) {
                return;
            }
            flexibleWindowsEvent.setOnMouseMoved(Position.LEFT_TOP, event.getSceneX(), event.getSceneY());
        });

        icon.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY());
            }
        });
    }

    private void initMinimizeEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), minimize);
        minimize.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.TOP, event.getSceneX(), event.getSceneY());
        });
        minimize.setOnAction(event -> stageManager.getMainStage().setIconified(true));

        minimize.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY());
            }
        });
    }

    private void initRestoreDownEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), restoreDown);

        restoreDown.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.TOP, event.getSceneX(), event.getSceneY());
        });
        //// todo 自由缩放
        restoreDown.setOnAction(event -> stageManager.getMainStage().setIconified(false));

        restoreDown.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY());
            }
        });
    }

    private void initCloseEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), close);
        close.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.RIGHT, event.getSceneX(), event.getSceneY());

            if (flexibleWindowsEvent.isWithin()) {
                return;
            }

            flexibleWindowsEvent.setOnMouseMoved(Position.TOP, event.getSceneX(), event.getSceneY());

            if (flexibleWindowsEvent.isWithin()) {
                return;
            }
            flexibleWindowsEvent.setOnMouseMoved(Position.RIGHT_TOP, event.getSceneX(), event.getSceneY());

        });
        close.setOnAction(event -> stageManager.getMainStage().close());

        close.setOnMouseDragged(event -> flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY()));
    }


}
