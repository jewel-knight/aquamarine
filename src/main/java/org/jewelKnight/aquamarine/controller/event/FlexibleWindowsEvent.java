package com.impactcn.aquamarine.controller.event;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author impactCn
 * @date 2023/11/4 12:09
 */
public class FlexibleWindowsEvent {

    public static final double MIN_WIDTH = 1400d;

    public static final double MIN_HEIGHT = 800d;
 

    private static boolean within;

    private final Stage stage;

    private final Node node;

    private static final Screen PRIMARY_SCREEN = Screen.getPrimary();

    public FlexibleWindowsEvent(Stage stage, Node node) {
        this.stage = stage;
        this.node = node;

    }

    public void setOnMouseMoved(String position, double x, double y) {
        within = true;
        switch (position) {
            case Position.TOP -> node.setCursor(topPosition(x, y, stage.getWidth()));
            case Position.BOTTOM -> node.setCursor(bottomPosition(x, y, stage.getWidth(), stage.getHeight()));
            case Position.RIGHT -> node.setCursor(rightPosition(x, y, stage.getWidth(), stage.getHeight()));
            case Position.LEFT -> node.setCursor(leftPosition(x, y, stage.getHeight()));
            case Position.RIGHT_TOP -> node.setCursor(rightAndTopPosition(x, y, stage.getWidth()));
            case Position.RIGHT_BOTTOM -> node.setCursor(rightAndBottomPosition(x, y, stage.getWidth(), stage.getHeight()));
            case Position.LEFT_TOP -> node.setCursor(leftAndTopPosition(x, y));
            case Position.LEFT_BOTTOM -> node.setCursor(leftAndBottomPosition(x, y, stage.getHeight()));
        }
    }

    public void setOnMouseDragged(double x, double y) {
        if (within) {
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = stage.getX();
            double nextY = stage.getY();
            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();

            //鼠标焦点位于左侧边界，执行左右移动
            if (Cursor.NW_RESIZE.equals(node.getCursor())
                    || Cursor.W_RESIZE.equals(node.getCursor())
                    || Cursor.SW_RESIZE.equals(node.getCursor())) {
                double width = Math.max( stage.getWidth() - x, MIN_WIDTH);
                nextX = nextX +  stage.getWidth() - width;
                nextWidth = width;
            }

            //鼠标焦点位于右侧边界，执行左右移动
            if (Cursor.NE_RESIZE.equals(node.getCursor())
                    || Cursor.E_RESIZE.equals(node.getCursor())
                    || Cursor.SE_RESIZE.equals(node.getCursor())) {
                nextWidth = Math.max(x, MIN_WIDTH);
            }

            //鼠标焦点位于顶部边界，执行上下移动
            if (Cursor.SW_RESIZE.equals(node.getCursor())
                    || Cursor.SE_RESIZE.equals(node.getCursor())
                    || Cursor.S_RESIZE.equals(node.getCursor())) {
                nextHeight = Math.max(y, MIN_HEIGHT);
            }

            //鼠标焦点位于底部边界，执行上下移动
            if (Cursor.NW_RESIZE.equals(node.getCursor())
                    || Cursor.N_RESIZE.equals(node.getCursor())
                    || Cursor.NE_RESIZE.equals(node.getCursor())) {
                if (y == 0.0) {
                    nextHeight = PRIMARY_SCREEN.getBounds().getHeight() - 2;
                } else {
                    double height = Math.max(nextHeight - y, MIN_HEIGHT);
                    nextY = nextY + nextHeight - height;
                    nextHeight = height;
                }

            }

            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            stage.setX(nextX);
            stage.setY(nextY);
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);

        }
    }

    private Cursor leftAndTopPosition(double x, double y) {
        if (x < Position.RESIZE_WIDTH && x >= 0 && y >= 0 && y < Position.RESIZE_WIDTH) {
            return Cursor.NW_RESIZE;
        }

        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor leftAndBottomPosition(double x, double y, double height) {
        if (x < Position.RESIZE_WIDTH && x >= 0 && y >= height - Position.RESIZE_WIDTH && y < height) {
            return Cursor.SW_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor rightAndBottomPosition(double x, double y, double width, double height) {
        if (x < width && x >= width - Position.RESIZE_WIDTH && y >= height - Position.RESIZE_WIDTH && y < height) {
            return Cursor.SE_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor rightAndTopPosition(double x, double y, double width) {
        if (x < width && x >= width - Position.RESIZE_WIDTH && y >= 0 && y < Position.RESIZE_WIDTH) {
            return Cursor.NE_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor leftPosition(double x, double y, double height) {
        if (x < Position.RESIZE_WIDTH && x >= 0 && y >= Position.RESIZE_WIDTH && y < height - Position.RESIZE_WIDTH) {
            return Cursor.W_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor rightPosition(double x, double y, double width, double height) {
        if (x < width && x >= width - Position.RESIZE_WIDTH && y >= Position.RESIZE_WIDTH && y < height - Position.RESIZE_WIDTH) {
            return Cursor.E_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor topPosition(double x, double y, double width) {
        if (x < width - Position.RESIZE_WIDTH && x >= Position.RESIZE_WIDTH && y >= 0 && y < Position.RESIZE_WIDTH) {
            return Cursor.N_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }

    private Cursor bottomPosition(double x, double y, double width, double height) {
        if (x < width - Position.RESIZE_WIDTH && x >= Position.RESIZE_WIDTH && y >= height - Position.RESIZE_WIDTH && y < height) {
            return Cursor.S_RESIZE;
        }
        within = false;
        return Cursor.DEFAULT;
    }


    public boolean isWithin() {
        return within;
    }
}
