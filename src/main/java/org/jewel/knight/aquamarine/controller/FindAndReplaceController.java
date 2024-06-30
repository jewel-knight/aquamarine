package org.jewel.knight.aquamarine.controller;

import org.jewel.knight.aquamarine.markdown.Search;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.SelectionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;

/**
 * @author impactCn
 * @date 2023/12/12 23:58
 */
@Component
public class FindAndReplaceController implements Initializable {


    private final static Map<CodeArea, List<SelectionImpl<Collection<String>, String, Collection<String>>>> SELECTIONS = new HashMap<>();

    private final static Map<CodeArea, int[]> RESULTS = new HashMap<>();

    @FXML
    private VBox findAndReplaceContainer;

    @FXML
    private Button findClose;


    @FXML
    private TextField findInput;
    @FXML
    private Text resultText;
    @FXML
    private TextField replaceInput;

    @FXML
    private Button replace;

    @FXML
    private Button replaceAll;

    @Autowired
    private EditorController editorController;

    @Autowired
    private InputController inputController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setFindInputAction();
        replace();
        replaceAll();
        hideFindAndReplace();
//        findAndReplaceContainer.setVisible(false);
    }

    /**
     * 关闭查找和替换
     */
    private void hideFindAndReplace() {
        findClose.setOnAction(event -> {
            inputController.getFindAndReplaceContainer().setVisible(false);
        });
    }



    /**
     * 设置查询事件
     */
    private void setFindInputAction() {
        // 禁止右键
        findInput.setContextMenu(null);
        // 搜索
        findInput.textProperty().addListener((observableValue, s, t1) -> {
            CodeArea input = editorController.getInput();
            String text = input.getText();
            clearSelections(input);
            if (!StringUtils.hasLength(t1)) {
                resultText.setText(0 + " results");
                RESULTS.remove(input);
                return;
            }
            int[] results = Search.findAll(t1, text).toArray();
            resultText.setText(results.length + " results");
            for (int result : results) {
                addExtraSelection(result, result + t1.length(), input);
            }

            RESULTS.put(input, results);

        });
    }

    /**
     * 添加额外的选中，进行高亮
     * @param start
     * @param end
     * @param input
     */
    private void addExtraSelection(int start, int end, CodeArea input) {
        SelectionImpl<Collection<String>, String, Collection<String>> anotherSelection = new SelectionImpl<>(String.valueOf(start), input, selectionPath -> {
            selectionPath.setStrokeWidth(0);
            selectionPath.setFill(Color.valueOf("#32593d"));
        });
        if (!input.addSelection(anotherSelection)) {
            throw new IllegalStateException("selection was not added to area");
        }
        anotherSelection.selectRange(start, end);
        if (SELECTIONS.containsKey(input)) {
            SELECTIONS.get(input).add(anotherSelection);
        } else {
            List<SelectionImpl<Collection<String>, String, Collection<String>>> selections = new ArrayList<>();
            selections.add(anotherSelection);
            SELECTIONS.put(input, selections);
        }
    }

    /**
     * 根据 input ，清除选中的高亮
     * @param input
     */
    private void clearSelections(CodeArea input) {
        List<SelectionImpl<Collection<String>, String, Collection<String>>> selections = SELECTIONS.get(input);

        if (selections != null) {
            for (SelectionImpl<Collection<String>, String, Collection<String>> selection : selections) {
                selection.selectRange(0, 0);
                input.removeSelection(selection);
            }
            selections.clear();
        }
    }



    private void replace() {
        replace.setOnAction(event -> {
            CodeArea input = editorController.getInput();
            if (RESULTS.containsKey(input)) {
                clearSelections(input);
                int[] ints = RESULTS.get(input);
                // 最后一个开始替换
                int start = ints[ints.length - 1];
                String findText = editorController.getFindInput().getText();
                String replaceText = editorController.getReplaceInput().getText();
                int end = start + findText.length();
                input.replaceText(start, end, replaceText);
                // int[] 删除最后一个空间
                if (ints.length > 1) {
                    int[] newInts = new int[ints.length - 1];
                    System.arraycopy(ints, 0, newInts, 0, ints.length - 1);
                    for (int i = 0; i < ints.length - 1; i++) {
                        newInts[i] = ints[i];
                        addExtraSelection(ints[i], ints[i] + findText.length(), input);
                    }
                    RESULTS.put(input, newInts);
                    resultText.setText(ints.length - 1 + " results");
                } else {
                    RESULTS.remove(input);
                    resultText.setText(0 + " results");

                }

            }
        });
    }

    private void replaceAll() {
        replaceAll.setOnAction(event -> {
            CodeArea input = editorController.getInput();
            if (RESULTS.containsKey(input)) {
                int[] results = RESULTS.get(input);
                String findText = editorController.getFindInput().getText();
                String replaceText = editorController.getReplaceInput().getText();
                for (int i = results.length - 1; i >= 0; i--) {
                    int start = results[i];
                    editorController.getInput().replaceText(start, start + findText.length(), replaceText);
                }
                resultText.setText(0 + " results");
                clearSelections(input);
                RESULTS.remove(input);
            }

        });
    }
}
