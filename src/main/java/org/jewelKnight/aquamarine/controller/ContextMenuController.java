package com.impactcn.aquamarine.controller;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

/**
 * @author impactCn
 * @date 2023/9/23 19:41
 */
@Component
public class ContextMenuController {


    public static final String H_BOX_LEFT_ID = "hBoxLeft";

    public static final String H_BOX_RIGHT_ID = "hBoxRight";

    public static final String MENU_TEXT_LEFT = "menuTextLeft";

    public static final String MENU_TEXT_RIGHT = "menuTextRight";

    public static final String MENU_ICON = "menuIcon";

    private final static String BEFORE_COLOR = "#3c3f41";

    private final static String AFTER_COLOR = "#4b6eaf";

    public HBox contextMenuContainer;
    public Label menuTextLeft;
    public Label menuTextRight;

    public void setMenuHover(HBox hBox) {
        hBox.setOnMouseEntered(mouseEvent -> {
            String style = hBox.getStyle();
            String replace = style.replace(BEFORE_COLOR, AFTER_COLOR);
            hBox.setStyle(replace);
        });

        hBox.setOnMouseExited(mouseEvent -> {
            String style = hBox.getStyle();
            String replace = style.replace(AFTER_COLOR, BEFORE_COLOR);
            hBox.setStyle(replace);
        });
    }

    public void setMenu(HBox hBox, String functionName, String shortcut, String imageUrl) {


        for (Node child : hBox.getChildren()) {

            if (child.getId().equals(H_BOX_LEFT_ID)) {
                HBox hBoxLeft = (HBox) child;
                for (Node hBoxLeftChild : hBoxLeft.getChildren()) {

                    if (hBoxLeftChild.getId().equals(MENU_ICON) && imageUrl != null) {
                        ImageView imageView = (ImageView) hBoxLeftChild;
                        Image image = new Image(imageUrl);
                        imageView.setImage(image);
                    }

                    if (hBoxLeftChild.getId().equals(MENU_TEXT_LEFT)) {
                        Label label = (Label) hBoxLeftChild;

                        label.setText(functionName);
                    }
                }
            }

            if (child.getId().equals(H_BOX_RIGHT_ID)) {
                HBox hBoxRight = (HBox) child;
                for (Node hBoxRightChild : hBoxRight.getChildren()) {
                    if (hBoxRightChild.getId().equals(MENU_TEXT_RIGHT)) {
                        Label label = (Label) hBoxRightChild;
                        label.setText(shortcut);
                    }

                }
            }

        }
    }


}
