package com.impactcn.aquamarine.controller.event;

import com.impactcn.aquamarine.markdown.MarkdownMatch;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;


/**
 * @author impactCn
 * @date 2023/11/15 22:32
 */
public class InputKeyPressEvent {

    private final static String BOLD = "**";

    private final static String HEAD_1 = "# ";

    private final static String HEAD_2 = "## ";

    private final static String HEAD_3 = "### ";

    private final static String HEAD_4 = "#### ";

    private final static String HEAD_5 = "##### ";

    private final static String HEAD_6 = "###### ";

    //// todo 段落
    private final static String PARAGRAPH = "paragraph";

    private final static String ITALIC = "*";

    private final static String QUOTES = "> ";

    private final static String CODE = "```";

    private final static String AUTO = "auto";



    private final CodeArea input;

    public InputKeyPressEvent(CodeArea input) {

        this.input = input;
    }

    public void setOnKeyPress() {
        input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String markdownKeyWord = match(keyEvent);

                // 有序&无序列表自动添加下一行的关键字
                if (AUTO.equals(markdownKeyWord)) {
                    String lastParagraph = input.getText(input.getCurrentParagraph() - 1);
                    String listKey = MarkdownMatch.matchList(lastParagraph);

                    if (listKey != null) {
                        input.insertText(input.getCurrentParagraph(), 0, listKey);
                    }
                    return;
                }
                // 选中的时候，按下快捷键自动生成 markdown 关键字
                String selectedText = input.getSelectedText();
                if (markdownKeyWord != null) {
                    if ("".equals(input.getSelectedText())) {
                        input.insertText(input.getCurrentParagraph(), input.getCaretColumn(), concat(markdownKeyWord, selectedText));
                    } else {
                        IndexRange indexRange = input.getSelection();
                        input.replaceText(indexRange, concat(markdownKeyWord, selectedText));
                    }

                }

            }
        });
    }

    /**
     * 匹配快捷键
     * @param keyEvent
     * @return
     */
    private String match(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            return AUTO;
        }
        if (keyEvent.isControlDown()) {
            switch (keyEvent.getCode()) {
                // ctrl + 1
                case NUMPAD1 -> {
                    return HEAD_1;
                }
                // ctrl + 2
                case NUMPAD2 -> {
                    return HEAD_2;
                }
                // ctrl + 3
                case NUMPAD3 -> {
                    return HEAD_3;
                }
                // ctrl + 4
                case NUMPAD4 -> {
                    return HEAD_4;
                }
                // ctrl + 5
                case NUMPAD5 -> {
                    return HEAD_5;
                }
                // ctrl + 6
                case NUMPAD6 -> {
                    return HEAD_6;
                }
                // ctrl + I
                case I -> {
                    return ITALIC;
                }

                case Q -> {
                    return QUOTES;
                }

                case B -> {
                    return BOLD;
                }
                case BACK_QUOTE -> {
                    return CODE;
                }

            }
        }
        return null;

    }

    private String concat(String markdownKeyWord, String selectText) {

        switch (markdownKeyWord) {
            case BOLD -> {
                return BOLD + selectText + BOLD;
            }
            case CODE -> {
                return CODE + "\n" + selectText + "\n" + CODE;
            }
            case ITALIC -> {
                return ITALIC + selectText + ITALIC;
            }
            case QUOTES -> {
                StringBuilder stringBuilder = new StringBuilder();
                String[] paragraphs = selectText.split("\n");
                for (int i = 0; i < selectText.split("\n").length; i++) {
                    stringBuilder.append(QUOTES);
                    stringBuilder.append(paragraphs[i]);
                    if (paragraphs.length - 1 != i) {
                        stringBuilder.append("\n");
                    }
                }
                return stringBuilder.toString();

            }
            default -> {
                return markdownKeyWord + selectText;
            }
        }

    }


}
