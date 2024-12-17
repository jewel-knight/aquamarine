package org.jewel.knight.aquamarine.controller;

import org.jewel.knight.aquamarine.check.LangGrammarCheck;
import org.jewel.knight.aquamarine.controller.event.InputKeyPressEvent;
import org.jewel.knight.aquamarine.loader.CssLoader;
import org.jewel.knight.aquamarine.loader.ImgLoader;
import org.jewel.knight.aquamarine.markdown.MarkdownMatch;
import org.jewel.knight.aquamarine.service.FileService;
import org.jewel.knight.aquamarine.service.PromptService;
import org.jewel.knight.aquamarine.service.WebEngineService;
import org.jewel.knight.aquamarine.utils.ConvertUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.value.Val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.regex.Matcher;

/**
 * @author impactCn
 * @date 2023/12/7 23:00
 */
@Component
public class InputController implements Initializable {

    private static final ObservableList<Integer> olistValue = FXCollections.observableArrayList();

    private static final ListProperty<Integer> listValue = new SimpleListProperty<Integer>(olistValue);

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static final LangGrammarCheck LANG_GRAMMAR_CHECK = new LangGrammarCheck();

    @FXML
    private HBox findAndReplaceOuterContainer;

    @FXML
    private CodeArea input;

    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem pasteMenuItem;

    @FXML
    private MenuItem generatePrefaceItem;

    @FXML
    private MenuItem toPdfMenuItem;

    @FXML
    private MenuItem ragItem;

    @FXML
    private VBox inputContainer;

    @FXML
    private VirtualizedScrollPane<CodeArea> scrollPane;

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return scrollPane;
    }

    @Value("${file.path}")
    private String path;

    @Autowired
    private EditorTabController editorTabController;

    @Autowired
    private FileService fileService;


    @Autowired
    private ContextMenuController contextMenuController;

    @Autowired
    private WebEngineService webEngineService;

    @Autowired
    private PromptService promptService;

    @Autowired
    private KnowledgeBaseController knowledgeBaseController;

    /**
     * 是否第一次加载
     */
    private boolean isFirst = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        input.prefWidthProperty().bind(inputContainer.widthProperty());

        InputKeyPressEvent inputKeyPressEvent = new InputKeyPressEvent(input);
        inputKeyPressEvent.setOnKeyPress();

        input.setWrapText(true);

//        calculateWidth();

        initLineNumber();

//        EventStream<StyleSpans<Collection<String>>> spellEvent = input.multiPlainChanges()
//                .successionEnds(java.time.Duration.ofMillis(500))
//                .retainLatestUntilLater(EXECUTOR)
//                .supplyTask(this::checkAsync)
//                .awaitLatest(input.multiPlainChanges())
//                .filterMap(t -> {
//                    if (t.isSuccess()) {
//                        return Optional.of(t.get());
//                    } else {
//                        t.getFailure().printStackTrace();
//                        return Optional.empty();
//                    }
//                });
//                .subscribe(this::checkFinish);

//        input.multiPlainChanges()
//                .successionEnds(java.time.Duration.ofMillis(250))
//                .retainLatestUntilLater(EXECUTOR)
//                .supplyTask(this::computeHighlightingAsync)
//                .awaitLatest(input.multiPlainChanges())
//                .filterMap(t -> {
//                    if (t.isSuccess()) {
//                        return Optional.of(t.get());
//                    } else {
//                        t.getFailure().printStackTrace();
//                        return Optional.empty();
//                    }
//                })
//                .subscribe(this::applyHighlighting);

//        EventStreams.merge(spellEvent, eventStream).subscribe(this::applyHighlighting);

        initInputContextMenu();
        scrollSimultaneously();
    }

    /**
     * 生成生成都是不同对象的 input，只能在 init 的时候使用，不能在其他地方使用
     * @return
     */
    public CodeArea getInput() {
        return input;
    }

    public HBox getFindAndReplaceOuterContainer() {
        return findAndReplaceOuterContainer;
    }


    /**
     * 计算空白处的宽度
     * 等于 input 的宽度 - 实际占用宽度（输入&按钮的地方）
     */
    private void calculateWidth() {
        int thumbWidth = 12;
        HBox blankContainer = (HBox) findAndReplaceOuterContainer.lookup("#blankContainer");
        HBox replaceInnerContainer = (HBox) findAndReplaceOuterContainer.lookup("#replaceInnerContainer");
        HBox replaceInnerButtonContainer = (HBox) findAndReplaceOuterContainer.lookup("#replaceInnerButtonContainer");
        input.widthProperty().addListener((observableValue, number, t1) -> {
            if (isFirst) {
                blankContainer.setPrefWidth(200);
                isFirst = false;
            } else {
                blankContainer.setPrefWidth(t1.doubleValue() + thumbWidth - replaceInnerContainer.getWidth() - replaceInnerButtonContainer.getWidth());
            }
        });
    }



    /**
     * 输入和输出同时滚动
     */
    private void scrollSimultaneously() {
        scrollPane.estimatedScrollYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number aDouble, Number t1) {
                int index = input.firstVisibleParToAllParIndex();
                webEngineService.scroll(index * 32d);
            }
        });

    }

//    private ScrollBar findScrollBar() {
//        for (Node node : scrollPane.getChildrenUnmodifiable()) {
//            if (node instanceof ScrollBar scrollBar) {
//                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
//                    return scrollBar;
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 初始化右键菜单
     */
    private void initInputContextMenu() {

        copyContextMenu();
        cutContextMenu();
        pasteContextMenu();
        generatePrefaceContextMenu();
        toPdfContextMenu();
        ragContextMenu();

    }

    /**
     * rag 右键菜单
     */
    private void ragContextMenu() {
        HBox ragItemGraphic = (HBox) ragItem.getGraphic();
        contextMenuController.setMenuHover(ragItemGraphic);
        contextMenuController.setMenu(ragItemGraphic, "Rag", "Ctrl+G", null);
        ragItem.setOnAction(event -> {
//            input.setVisible(false);
//            input.setEditable(false);
//            input.setDisable(true);
            //// todo 由于 PopUp 无法正确获取聚焦事件，导致输入法出现问题，后续在修改
            knowledgeBaseController.showPopup();
            promptService.initData();
        });
    }

    /**
     * 生成前言右键菜单
     */
    private void generatePrefaceContextMenu() {
        HBox generatePrefaceItemGraphic = (HBox) generatePrefaceItem.getGraphic();
        contextMenuController.setMenuHover(generatePrefaceItemGraphic);
        contextMenuController.setMenu(generatePrefaceItemGraphic, "Generate Preface", "", null);


        generatePrefaceItem.setOnAction(event -> {

            Platform.runLater(() -> {
                String preface = promptService.preface(input.getText());
                String quotesText = "> " + preface + "\n";
                Timeline timeline = createTextAnimation(quotesText, input.getText());
                timeline.play();
            });

        });
    }

    /**
     * 转换为 pdf 右键菜单
     */
    private void toPdfContextMenu() {
        HBox toPdfMenuItemGraphic = (HBox) toPdfMenuItem.getGraphic();
        contextMenuController.setMenuHover(toPdfMenuItemGraphic);
        contextMenuController.setMenu(toPdfMenuItemGraphic, "Convert Pdf", "", null);
        toPdfMenuItem.setOnAction(event -> {
            String currTabPath = editorTabController.getCurrTabPath();
            String newName = currTabPath.split("\\.")[0] + ".pdf";
            ConvertUtil.toPdf(webEngineService.getContent(),
                    CssLoader.getVal("github-markdown-dark.css"), newName);
        });
    }

    /**
     * 粘贴右键菜单
     */
    private void pasteContextMenu() {
        HBox pasteMenuItemGraphic = (HBox) pasteMenuItem.getGraphic();
        contextMenuController.setMenuHover(pasteMenuItemGraphic);
        contextMenuController.setMenu(pasteMenuItemGraphic, "Paste", "Ctrl+V", ImgLoader.getVal("paste.png"));
        pasteMenuItem.setOnAction(event -> input.paste());
    }

    /**
     * 剪切右键菜单
     */
    private void cutContextMenu() {
        HBox cutMenuItemGraphic = (HBox) cutMenuItem.getGraphic();
        contextMenuController.setMenuHover(cutMenuItemGraphic);
        contextMenuController.setMenu(cutMenuItemGraphic, "Cut", "Ctrl+X", null);
        cutMenuItem.setOnAction(event -> input.cut());

    }

    /**
     * 复制右键菜单
     */
    private void copyContextMenu() {
        HBox copyMenuItemGraphic = (HBox) copyMenuItem.getGraphic();

        contextMenuController.setMenuHover(copyMenuItemGraphic);
        contextMenuController.setMenu(copyMenuItemGraphic, "Copy", "Ctrl+C", null);

        copyMenuItem.setOnAction(event -> input.copy());
    }

    /**
     * 只有初始化的时候才能调用，因为每次都会重新设置 input 对象
     * @param fileName
     */
    public void replaceContent(String fileName) {

        String content = fileService.getContent(path, fileName);
        input.replaceText(0, 0, content);
    }


    private void initLineNumber() {
        olistValue.add(input.getCurrentParagraph() + 1);
        IntFunction<Node> arrowFactory = new MultiBreakPointFactory(listValue);
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox(arrowFactory.apply(line));
            hbox.setAlignment(Pos.TOP_CENTER);
            hbox.setPadding(new Insets(5, 0 , 0, 0));
            hbox.setPrefWidth(80);
            return hbox;
        };

        input.setParagraphGraphicFactory(graphicFactory);
    }

    private Timeline createTextAnimation(String text, String originText) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(i * 0.05), // 逐字显示的速度
                    event -> {
                        input.insertText(0, index, String.valueOf(text.charAt(index)));
//                        input.replaceText(text.substring(0, index) + originText);
                        input.moveTo(input.getLength());
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        return timeline;
    }






    private Task<StyleSpans<Collection<String>>> checkAsync() {
        String text = input.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return spellHighlighting(text);
            }
        };
        EXECUTOR.execute(task);
        return task;
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = input.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        EXECUTOR.execute(task);
        return task;
    }

    private void checkFinish(StyleSpans<Collection<String>> highlighting) {
        input.setStyleSpans(0, highlighting);
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        input.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> spellHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;
        for (LangGrammarCheck.Range range : LANG_GRAMMAR_CHECK.check(text)) {
            spansBuilder.add(Collections.singleton("global"), range.getStartPos() - lastKwEnd);
            spansBuilder.add(Collections.singleton("underline"), range.getEndPos() - range.getStartPos());
            lastKwEnd = range.getEndPos();
        }
        return spansBuilder.create();

    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        // 关键字高亮
        Matcher matcher = MarkdownMatch.PATTERN.matcher(text);
        int lastKwEnd = 0;
        while(matcher.find()) {
            String styleClass = getString(matcher);
            spansBuilder.add(Collections.singleton("global"), matcher.start() - lastKwEnd);

            if ("headkey".equals(styleClass)) {
                spansBuilder.add(Collections.singleton(styleClass), matcher.end(styleClass.toUpperCase()) - matcher.start());
                spansBuilder.add(Collections.singleton("headval"), matcher.end("HEADVAL") - matcher.start("HEADVAL"));
                lastKwEnd = matcher.end("HEADVAL");
            }
            else if ("bold".equals(styleClass)) {
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            else {
                spansBuilder.add(Collections.singleton(styleClass), matcher.end(styleClass.toUpperCase()) - matcher.start());
                lastKwEnd = matcher.end(styleClass.toUpperCase());
            }

        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static String getString(Matcher matcher) {
        String styleClass =
                matcher.group("CODE") != null ? "code" :
                        matcher.group("HEADKEY") != null ? "headkey" :
                                matcher.group("BR") != null ? "br" :
                                        matcher.group("QUOTE") != null ? "quote" :
                                                matcher.group("LINK") != null ? "link" :
                                                        matcher.group("ORDER") != null ? "order" :
                                                                matcher.group("DISORDER") != null ? "disorder" :
                                                                        matcher.group("BOLD") != null ? "bold" : null;
        /* never happens */
        assert styleClass != null;
        return styleClass;
    }



    class MultiBreakPointFactory implements IntFunction<Node> {

        private final ListProperty<Integer> shownLines;


        public MultiBreakPointFactory(ListProperty<Integer> shownLine) {
            input.currentParagraphProperty().addListener((observableValue, integer, t1) -> {
                olistValue.clear();
                olistValue.add(t1+1);
            });
            this.shownLines = shownLine;
        }
        @Override
        public Node apply(int lineIndex) {
            Label before =  new Label();
            before.setId("line-number");
            before.setText(String.valueOf(lineIndex + 1));
            ObservableValue<Boolean> visible = Val.map(shownLines, sl -> {
                boolean contains = sl.contains(lineIndex + 1);

                if (contains) {
                    before.setStyle("-fx-text-fill: #cacfd2");
                } else {
                    before.setStyle("-fx-text-fill: #606366");
                }
                return true;
            });
            before.visibleProperty().bind(
                    Val.flatMap(before.sceneProperty(), scene -> {
                        if (scene != null) {
                            return visible;
                        }
                        return Val.constant(false);
                    }));

            return before;
        }
    }


}
