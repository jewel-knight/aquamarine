package com.impactcn.aquamarine.controller;

import com.impactcn.aquamarine.controller.event.InputTextChangeEvent;
import com.impactcn.aquamarine.loader.CssLoader;
import com.impactcn.aquamarine.loader.OutputLoader;
import com.impactcn.aquamarine.service.FileService;
import com.impactcn.aquamarine.service.WebEngineService;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/12/9 19:47
 */
@Component
public class OutputController implements Initializable {

    public WebView output;

    public VBox outputContainer;

    @Autowired
    private WebEngineService webEngineService;

    @Autowired
    private EditorTabController editorTabController;

    @Autowired
    private InputController inputController;

    @Autowired
    private FileService fileService;

    @Autowired
    private BottomBarController bottomBarController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        webEngineService.setWebEngine(output.getEngine());
//        output.prefHeightProperty().bind(outputContainer.heightProperty());
        output.getEngine().setUserStyleSheetLocation(CssLoader.getVal("github-markdown-dark.css"));
        output.getEngine().loadContent(OutputLoader.getVal("output.html"));

        loadTextChangeListener();
        forbidRightClick();
    }

    private void forbidRightClick() {
        output.setContextMenuEnabled(false);
    }

    private void loadTextChangeListener() {
        InputTextChangeEvent.builder()
                .read(inputController.getInput())
                .countWord(bottomBarController.wordsCount)
                .show(output)
                .save(fileService, editorTabController)
                .build();
    }
}
