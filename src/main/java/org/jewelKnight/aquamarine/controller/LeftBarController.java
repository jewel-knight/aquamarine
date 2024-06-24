package com.impactcn.aquamarine.controller;

import com.impactcn.aquamarine.controller.event.FlexibleNodeEvent;
import com.impactcn.aquamarine.controller.event.FlexibleWindowsEvent;
import com.impactcn.aquamarine.controller.event.Position;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/9/30 1:40
 */
@Component
public class LeftBarController implements Initializable {

    public VBox leftBarContainer;
    public HBox topPart;
    public HBox buttonPart;
    public Button foldBtn;
    public Button settingBtn;

    /**
     * 是否是是折叠状态
     */
    private volatile boolean isFold = false;

    private double defaultWidth = 130d;
    private final double zeroWidth = 0;

    @Autowired
    private FileManageController fileManageController;

    @Autowired
    private SettingController settingController;

    @Autowired
    private StageManager stageManager;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        settingAction();
        foldAction();
        setFlexibleWindowsEvent();
    }

    /**
     * 设置可拖动左边窗口事件
     */
    private void setFlexibleWindowsEvent() {
        FlexibleWindowsEvent flexibleWindowsEvent = new FlexibleWindowsEvent(stageManager.getMainStage(), leftBarContainer);

        leftBarContainer.setOnMouseMoved(event -> {
            flexibleWindowsEvent.setOnMouseMoved(Position.LEFT, event.getSceneX(), event.getSceneY());
        });

        leftBarContainer.setOnMouseDragged(event -> {
            if (flexibleWindowsEvent.isWithin()) {
                flexibleWindowsEvent.setOnMouseDragged(event.getSceneX(), event.getSceneY());
            }
        });

    }

    /**
     * 设置setting按钮事件展示界面
     */
    private void settingAction() {
        settingBtn.setOnAction(event -> settingController.show());
    }

    private void foldAction() {
        foldBtn.setOnAction(event -> {

            if (isFold) {
                // 不是折叠
                foldStatus(isFold , defaultWidth);
                isFold = false;

            } else {
                // 是折叠
                defaultWidth = fileManageController.fileTree.getPrefWidth();
                foldStatus(isFold, zeroWidth);
                isFold = true;

            }
        });
    }


    private void foldStatus(boolean visible, double width) {

        if (visible) {
            fileManageController.fileContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
            fileManageController.fileSettingContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        } else {
            fileManageController.fileContainer.setPrefWidth(zeroWidth);
            fileManageController.fileSettingContainer.setPrefWidth(zeroWidth);
        }

        fileManageController.fileTree.setVisible(visible);
        fileManageController.fileSettingContainer.setVisible(visible);
        fileManageController.fileTree.setPrefWidth(width);

    }


}
