package org.jewel.knight.aquamarine.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author impactCn
 * @date 2023/10/5 2:52
 */
public class SceneEvent extends ApplicationEvent {

    private Double sceneXOffset;

    private Double getSceneYOffset;

    public SceneEvent(Object source, Double sceneXOffset, Double getSceneYOffset) {
        super(source);
        this.sceneXOffset = sceneXOffset;
        this.getSceneYOffset = getSceneYOffset;
    }

    public Double getSceneXOffset() {
        return sceneXOffset;
    }

    public Double getGetSceneYOffset() {
        return getSceneYOffset;
    }
}
