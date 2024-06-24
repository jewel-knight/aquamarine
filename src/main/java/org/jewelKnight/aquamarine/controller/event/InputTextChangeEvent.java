package com.impactcn.aquamarine.controller.event;

import com.impactcn.aquamarine.check.LangGrammarCheck;
import com.impactcn.aquamarine.config.ThreadPoolConfig;
import com.impactcn.aquamarine.controller.EditorTabController;
import com.impactcn.aquamarine.loader.OutputLoader;
import com.impactcn.aquamarine.markdown.MarkdownParser;
import com.impactcn.aquamarine.service.FileService;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


/**
 * @author impactCn
 * @date 2023/11/25 11:27
 */
public class InputTextChangeEvent {

    private CodeArea input;

    private WebView output;

    private FileService fileService;

    private EditorTabController editorTabController;

    private Text countWord;

    private int scrollY = 0;

    /**
     * 是否加载过
     */
    private volatile boolean isLoad = false;

    private final MarkdownParser markdownParser =  new MarkdownParser();


    public static InputTextChangeEvent builder() {
        return new InputTextChangeEvent();
    }

    /**
     * 读取内容
     * @param input
     * @return
     */
    public InputTextChangeEvent read(CodeArea input) {
        this.input = input;
        return this;
    }

    public InputTextChangeEvent countWord(Text countWord) {
        this.countWord = countWord;
        return this;
    }

    /**
     * 渲染
     * @param output
     * @return
     */
    public InputTextChangeEvent show(WebView output) {
        this.output = output;
        return this;
    }

    /**
     * 保存进文件
     * @return
     */
    public InputTextChangeEvent save(FileService fileService, EditorTabController editorTabController) {
        this.fileService = fileService;
        this.editorTabController = editorTabController;
        return this;
    }


    public void build() {
        if (input != null) {
            input.textProperty().addListener((observableValue, s, t1) -> {
                // 保存
                if (fileService != null) {
                    String currTabPath = editorTabController.getCurrTabPath();
                    //// todo 异步处理，提交任务给调度做处理
                    fileService.save(currTabPath, t1);
                }

                ThreadPoolConfig.getNewInstance().submit(() -> {
                    Platform.runLater(() -> {
                        // 输出
                        if (output != null) {
                            output.getEngine().executeScript("document.body.innerHTML = ''");
                            String htmlStr = markdownParser.parse(t1);
                            String replaceAll = htmlStr
                                    .replaceAll("\\\\$", "\\\\$")
                                    .replaceAll("\\{", "\\\\{")
                                    .replaceAll("}", "\\\\}")
                                    .replaceAll("`", "\\\\`");
                            output.getEngine().executeScript("document.body.innerHTML = `" + replaceAll + "`");
                            String content = (String) output.getEngine().executeScript("document.documentElement.innerText");
                            countWord.setText(content.length() + " Words");

                        }

                        // 重置高亮
                        input.clearStyle(0, input.getLength());
                        // 高亮
                        if (markdownParser.getRanges() != null) {
                            List<MarkdownParser.StyleRange> ranges = markdownParser.getRanges();
                            for (MarkdownParser.StyleRange range : ranges) {
                                input.setStyle(range.getBegin(), range.getEnd(), List.of(range.getStyleClass()));
                            }
                        }
                    });
                });

//                Platform.runLater(() -> {
//                    // 输出
//                    if (output != null) {
//                        output.getEngine().executeScript("document.body.innerHTML = ''");
//                        String htmlStr = markdownParser.parse(t1);
//                        String replaceAll = htmlStr
//                                .replaceAll("\\\\$", "\\\\$")
//                                .replaceAll("\\{", "\\\\{")
//                                .replaceAll("}", "\\\\}")
//                                .replaceAll("`", "\\\\`");
//                        output.getEngine().executeScript("document.body.innerHTML = `" + replaceAll + "`");
//                        String content = (String) output.getEngine().executeScript("document.documentElement.innerText");
//                        countWord.setText(content.length() + " Words");
//
//                    }
//                    // 重置高亮
//                    input.clearStyle(0, input.getLength());
//                    // 高亮
//                    if (markdownParser.getRanges() != null) {
//                        List<MarkdownParser.StyleRange> ranges = markdownParser.getRanges();
//                        for (MarkdownParser.StyleRange range : ranges) {
//                            input.setStyle(range.getBegin(), range.getEnd(), List.of(range.getStyleClass()));
//                        }
//                    }
//                });

            });


        }

    }

}
