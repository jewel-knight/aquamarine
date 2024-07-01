package org.jewel.knight.aquamarine.controller.event;

import javafx.scene.Cursor;
import javafx.scene.layout.Region;

/**
 * @author impactCn
 * @date 2023/11/27 23:00
 */
public class FlexibleNodeEvent {

    private final Region node;

    private static boolean within;

    private double xOffset = 0;

    public FlexibleNodeEvent(Region node) {
        this.node = node;
    }

    public void resize(String position) {
        setOnMouseMove(position);
        setOnMousePressed();
        setOnMouseDragged();
    }

    private void setOnMouseMove(String position) {

        node.setOnMouseMoved(event -> {
            within = true;
            switch (position) {
                case Position.RIGHT -> node.setCursor(rightPosition(event.getX(), node.getPrefWidth()));
            }
        });


    }

    private void setOnMousePressed() {
        node.setOnMousePressed(event -> {
            xOffset = event.getX();
        });
    }

    private void setOnMouseDragged() {

        node.setOnMouseDragged(event -> {
            if (within) {
                double delta = event.getX() - xOffset;
                double newWidth = node.getPrefWidth() + delta;
                if (newWidth > Position.MIN_WIDTH) {
                    node.setPrefWidth(newWidth);
                }
                xOffset = event.getX();

            }
        });
    }

    private Cursor rightPosition(double x, double width) {
        if (x < width && x >= width - Position.RESIZE_WIDTH) {
            return Cursor.E_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }
}
