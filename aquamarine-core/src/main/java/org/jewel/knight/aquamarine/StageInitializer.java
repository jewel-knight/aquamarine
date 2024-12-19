package org.jewel.knight.aquamarine;

import org.jewel.knight.aquamarine.loader.CssLoader;
import org.jewel.knight.aquamarine.loader.FxmlLoader;
import org.jewel.knight.aquamarine.loader.ImgLoader;
import org.jewel.knight.aquamarine.controller.EditorController;
import org.jewel.knight.aquamarine.controller.LeftBarController;
import org.jewel.knight.aquamarine.controller.StageManager;
import org.jewel.knight.aquamarine.controller.TitleBarController;
import org.jewel.knight.aquamarine.controller.event.FlexibleWindowsEvent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jewel.knight.aquamarine.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author impactCn
 * @date 2023/10/9 23:25
 */
@Component
public class StageInitializer implements ApplicationListener<UiApplication.StageReadyEvent> {

    private final ApplicationContext applicationContext;

    private static Label infoLb;

    private Parent main;

    public StageInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Value("${file.path}")
    private String path;

    @Autowired
    private StageManager stageManager;

    @Autowired
    private EditorController editorController;

    @Autowired
    private LeftBarController leftBarController;

    @Autowired
    private TitleBarController titleBarController;

    @Autowired
    private PromptService promptService;

    @Override
    public void onApplicationEvent(UiApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        initSource();
        startUpPage(stage);

        new Thread(() -> {
            initSystem();
            Platform.runLater(() -> {
                Scene scene = new Scene(main, FlexibleWindowsEvent.MIN_WIDTH, FlexibleWindowsEvent.MIN_HEIGHT);
                scene.getStylesheets().addAll(CssLoader.getAll());
                stage.setScene(scene);

                stage.show();
            });

        }).start();

        stageManager.setMainStage(stage);
        stageEvent(stage);

//        initOther();

    }

    /**
     * 设置中间部分的高宽
     * @param stage
     */
    private void stageEvent(Stage stage) {
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                editorController.editorContainer.setPrefHeight(t1.doubleValue());
                leftBarController.leftBarContainer.setPrefHeight(t1.doubleValue());
            }
        });
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                editorController.editorContainer.setPrefWidth(t1.doubleValue());
                titleBarController.titleBarContainer.setPrefWidth(t1.doubleValue());
            }
        });
    }

    private void initSource() {
        createDir();
        promptService.initData();
    }

    private void createDir() {
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private void startUpPage(Stage stage) {
        Image image = new Image(ImgLoader.getVal("startup.png"));
        ImageView view = new ImageView(image);
        infoLb = new Label();
        infoLb.setTextFill(Color.WHITE);

        AnchorPane.setRightAnchor(infoLb, 10.0);
        AnchorPane.setBottomAnchor(infoLb, 10.0);
        AnchorPane page = new AnchorPane();
        page.getChildren().addAll(view, infoLb);

        stage.setScene(new Scene(page));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private void initSystem() {
        try {
            showInfo("Initial main page...");
            initMainPage();
            showInfo("Initial other page...");
            initOther();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String info) {
        Platform.runLater(() -> infoLb.setText(info));
    }



    private void initMainPage() {
        Parent bottomBar = FxmlLoader.getParent("bottomBar.fxml", applicationContext);

        Parent editor = FxmlLoader.getParent("editor.fxml", applicationContext);

        Parent titleBar = FxmlLoader.getParent("titleBar.fxml", applicationContext);

        Parent leftBar = FxmlLoader.getParent("leftBar.fxml", applicationContext);


        VBox main = new VBox();

        HBox mid = new HBox();
        mid.getChildren().addAll(leftBar, editor);

        HBox bottom = new HBox();
        bottom.getChildren().addAll(bottomBar);

        main.getChildren().addAll(titleBar, mid, bottom);

        this.main = main;
    }

    private void initOther() {
        Platform.runLater(() -> {
            //// todo 这些应该放到加载封面的时候加载
            FxmlLoader.getParent("popUpContextMenu.fxml", applicationContext);
            FxmlLoader.getParent("setting.fxml", applicationContext);
            FxmlLoader.getParent("knowledgeBase.fxml", applicationContext);

            // 初始化 tab
            FxmlLoader.getParent("editorTab.fxml", applicationContext);
            FxmlLoader.getParent("input.fxml", applicationContext);
            FxmlLoader.getParent("output.fxml", applicationContext);
        });

    }


}
