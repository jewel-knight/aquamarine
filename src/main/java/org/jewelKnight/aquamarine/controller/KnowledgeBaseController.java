package com.impactcn.aquamarine.controller;

import com.impactcn.aquamarine.service.PromptService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author impactCn
 * @date 2024/2/21 20:15
 */
@Component
public class KnowledgeBaseController implements Initializable {

    @FXML
    private VBox knowledgeBaseContainer;
    @FXML
    private TextArea output;

    @FXML
    private TextField input;


    private static final Popup FLOATING_POPUP = new Popup();

    @Autowired
    private StageManager stageManager;

    @Autowired
    private PromptService promptService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        output.setEditable(false);
        output.setWrapText(true);
        input.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String text = input.getText();
                        output.appendText("you: \n" + text + "\n\n");
                        input.clear();
                        String rag = promptService.rag(text);
                        output.appendText("assistant: \n" + rag + "\n\n");
                    }
                });

            }
        });


        FLOATING_POPUP.setAutoHide(true);
        FLOATING_POPUP.getContent().add(knowledgeBaseContainer);
    }

    public void showPopup() {

        // 通过 X 坐标，来确认当前使用的 screen
        Screen currScreen = null;
        for (Screen screen : Screen.getScreens()) {
            if (screen.getBounds().getMinX() <= stageManager.getMainStage().getX()
                    && screen.getBounds().getMaxX() >= stageManager.getMainStage().getX()) {
                // 当前使用的 screen
                currScreen = screen;
                break;
            }
        }
        double currX = (currScreen.getBounds().getMinX() + currScreen.getBounds().getMaxX()) / 2 -
                (knowledgeBaseContainer.getPrefWidth() / 2);

        double currY = (currScreen.getBounds().getMinY() + currScreen.getBounds().getMaxY()) / 2 -
                (knowledgeBaseContainer.getPrefWidth() / 2);

        FLOATING_POPUP.show(stageManager.getMainStage(), currX, currY);
    }
}
