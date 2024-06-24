package com.impactcn.aquamarine.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/10/29 20:22
 */
@Component
public class EditorTabController implements Initializable {

    public HBox editorTitleContainer;

    public Label title;
    public Button editorTitleIcon;
    public Button editorTitleClose;

    private Integer lastFocusIndex = null;

    private ObservableList<Node> nodes;

    @Autowired
    private EditorController editorController;


    @Value("${file.path}")
    private String path;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editorTitleClose.setVisible(false);
        nodes = editorController.getTabList();
    }

    /**
     * 添加 tab
     * @param tab
     */
    public void addTab(HBox tab) {
        nodes.add(tab);
    }

    /**
     * 设置 tab 的点击事件
     * @param tab
     */
    public void setClickTab(HBox tab, Map<HBox, List<Node>> tabMapping) {
        // tab 设置点击事件
        // 当点击的时候才会生效
        tab.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                HBox lastTab = (HBox) nodes.get(lastFocusIndex);
                // 点击自己
                if (lastTab.equals(tab)) {
                    return;
                } else {
                    setPreviousAndMouseEvent(lastTab);
                }
                lastFocusIndex = nodes.indexOf(tab);
                setFocusAndClearMouseEvent(tab);
//                replaceContent(tab);
                List<Node> nodes = tabMapping.get(tab);
                editorController.addInputAndOutput(nodes);

            }
        });


    }

    /**
     * 设置 tab 的关闭事件
     * @param tab
     * @param fileMapping
     */
    public void setClickClose(HBox tab, Map<String, HBox> fileMapping, Map<HBox, List<Node>> tabMapping) {
        // 点击 close 触发的事件
        Button close = (Button) tab.getChildren().get(2);
        close.setOnAction(event -> {
            // 删除的位置
            int index = nodes.indexOf(tab);
            nodes.remove(tab);
            // 最后一个
            if (nodes.isEmpty()) {
                lastFocusIndex = null;
                editorController.setHide(false);
            } else {
                // 删除前面
                if (lastFocusIndex > index) {
                    // 个数减少了
                    lastFocusIndex = lastFocusIndex - 1;
                }
                else if (lastFocusIndex == index) {
                    // 避免越界
                    if (lastFocusIndex != 0) {
                        lastFocusIndex = lastFocusIndex - 1;
                    }
                }

                HBox currTab = (HBox) nodes.get(lastFocusIndex);
                setFocusAndClearMouseEvent(currTab);
//                replaceContent(currTab);
                List<Node> nodes = tabMapping.get(currTab);
                editorController.addInputAndOutput(nodes);
            }
            // 删除的 tab
            Label label = (Label) tab.getChildren().get(1);
            String key = path + "/" + label.getText();
            fileMapping.remove(key);

        });
    }

    public void setTitle(String name) {
        title.setText(name);
    }

    /**
     * 判断是否首次添加的 tab ，才选择添加事件
     * 对于首次，由于每次都是添加操作，直接传当前 list 的大小进去，则表示是最后一个需要高亮
     * 非首次添加，可以在 fileTree 直接选择已有的点击
     */
    public void setTabStatus(HBox tab, boolean isFirst) {
        if (isFirst) {
            if (lastFocusIndex != null) {
                // 上一个高亮恢复成原来
                HBox lastTab = (HBox) nodes.get(lastFocusIndex);
                setPreviousAndMouseEvent(lastTab);
            }
            setFocusAndClearMouseEvent(tab);
            lastFocusIndex = nodes.size();
        } else {
            int index = nodes.indexOf(tab);

            if (index == lastFocusIndex) {
                // 当前已经是高亮的
                return;
            }
            HBox currTab = (HBox) nodes.get(index);
            setFocusAndClearMouseEvent(currTab);
            HBox lastTab = (HBox) nodes.get(lastFocusIndex);
            setPreviousAndMouseEvent(lastTab);
            lastFocusIndex = index;
        }
    }


    public HBox getCurrFocus() {
        return (HBox) nodes.get(lastFocusIndex);
    }

    public String getCurrTabPath() {
        Label label = (Label) getCurrFocus().getChildren().get(1);
        return path + "/" + label.getText();
    }

    public String getCurrTabName() {

        if (lastFocusIndex == null) {
            return null;
        }

        Label label = (Label) getCurrFocus().getChildren().get(1);
        return label.getText();
    }


    /**
     * 设置上一个的颜色和事件
     * @param tab
     */
    private void setPreviousAndMouseEvent(HBox tab) {
        previous(tab);
        setOnMouseEvent(tab);
    }

    private void setFocusAndClearMouseEvent(HBox tab) {
        focus(tab);
        clearOnMouseEvent(tab);
    }


    /**
     * 设置 tab 事件
     * @param tab
     */
    private void setOnMouseEvent(HBox tab) {
        tab.setOnMouseEntered(event -> touch(tab));
        tab.setOnMouseExited(event -> previous(tab));
    }

    /**
     * 清除 tab 事件
     * @param tab
     */
    private void clearOnMouseEvent(HBox tab) {
        tab.setOnMouseEntered(null);
        tab.setOnMouseExited(null);
    }

    /**
     * 最初颜色
     * @param tab
     */
    private void previous(HBox tab) {
        tab.setStyle("-fx-background-color: #3c3f41");
        tab.getChildren().get(0).setStyle("-fx-background-color: #3c3f41");
        tab.getChildren().get(2).setStyle("-fx-background-color: #3c3f41");
        tab.getChildren().get(2).setVisible(false);
    }

    /**
     * 触碰颜色
     * @param tab
     */
    private void touch(HBox tab) {
        tab.setStyle("-fx-background-color: #27292a");
        tab.getChildren().get(0).setStyle("-fx-background-color: #27292a");
        tab.getChildren().get(2).setStyle("-fx-background-color: #27292a");
        tab.getChildren().get(2).setVisible(true);
    }

    /**
     * 聚焦颜色
     * @param tab
     */
    private void focus(HBox tab) {
        tab.setStyle("-fx-background-color: #4e5254");
        tab.getChildren().get(0).setStyle("-fx-background-color: #4e5254");
        tab.getChildren().get(2).setStyle("-fx-background-color: #4e5254");
        tab.getChildren().get(2).setVisible(true);
    }

}
