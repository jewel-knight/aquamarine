package org.jewel.knight.aquamarine.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.jewel.knight.aquamarine.service.PromptService;
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

    private String inputText = "";

    private final static String LOADING_TEXT = "...loading";

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
                inputText = input.getText();
                output.appendText("you: \n" + inputText + "\n\n");
                input.clear();

                output.appendText( "assistant: \n" + LOADING_TEXT);
            }
        });
        // 监听输入框的变化
        input.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                output.deleteText( output.getLength() - LOADING_TEXT.length(), output.getLength());
                String rag = promptService.rag(inputText);
                rag = rag + "\n\n";
                createTextAnimation(rag).play();
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

    private Timeline createTextAnimation(String text) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(i * 0.05), // 逐字显示的速度
                    event -> {
                        output.appendText(String.valueOf(text.charAt(index)));
//                        input.replaceText(text.substring(0, index) + originText);
//                        input.moveTo(input.getLength());
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        return timeline;
    }
}
