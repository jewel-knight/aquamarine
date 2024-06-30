package org.jewel.knight.aquamarine.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.CodeArea;
import org.jewel.knight.aquamarine.controller.event.FlexibleWindowsEvent;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
public class EditorController implements Initializable {

    public VBox editorInnerContainer;

    public VBox editorContainer;

    public Button editorMode;

    public Button previewMode;

    public HBox editorTitle;
    public HBox inputAndOutputContainer;
    public HBox editorTabContainer;


    /**
     * 获取 tab 列表
     * @return
     */
    public ObservableList<Node> getTabList() {
        return editorTabContainer.getChildren();
    }

    /**
     * 获取当前的 input
     * @return
     */
    public CodeArea getInput() {
        VBox input = getInputVBox();
        return (CodeArea) input.lookup("#input");
    }

    /**
     * 获取当前的 findInput
     * @return
     */
    public TextField getFindInput() {
        VBox input = getInputVBox();
        return (TextField) input.lookup("#findInput");
    }

    /**
     * 获取当前的 replaceInput
     * @return
     */
    public TextField getReplaceInput() {
        VBox input = getInputVBox();
        return (TextField) input.lookup("#replaceInput");
    }

    /**
     * 获取当前的 output
     * @return
     */
    public CodeArea getOutput() {
        VBox input = (VBox) inputAndOutputContainer.getChildren().get(1);
        return (CodeArea) input.lookup("#output");
    }



    public void setHide(boolean flag) {
        inputAndOutputContainer.setVisible(flag);
        editorTitle.setVisible(flag);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editorInnerContainer.setPrefHeight(FlexibleWindowsEvent.MIN_HEIGHT);
        editorContainer.setPrefHeight(FlexibleWindowsEvent.MIN_HEIGHT);
        editorContainer.setPrefWidth(FlexibleWindowsEvent.MIN_WIDTH);

        setHide(false);

        initEditorMode();
        initPreviewMode();
    }


    /**
     * 添加 input 和 output 节点的时候，先清除之前的节点
     */
    public void addInputAndOutput(List<Node> nodes) {
        inputAndOutputContainer.getChildren().clear();
        inputAndOutputContainer.getChildren().addAll(nodes);
    }

    public void addInputAndOutput(Node node) {
        inputAndOutputContainer.getChildren().clear();
        inputAndOutputContainer.setAlignment(Pos.CENTER);
        inputAndOutputContainer.getChildren().add(node);
    }


    private VBox getInputVBox() {
        return (VBox) inputAndOutputContainer.getChildren().get(0);
    }



    private void initEditorMode() {
        editorMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                outputContainer.setVisible(false);
//                HBox.setHgrow(outputContainer, Priority.NEVER);
//                output.setPrefWidth(0);
            }
        });
    }

    private void initPreviewMode() {
        previewMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                outputContainer.setVisible(true);
//                HBox.setHgrow(outputContainer, Priority.ALWAYS);
//                output.setPrefWidth(200);
//                inputController.input.setPrefWidth(200);
            }
        });
    }




}