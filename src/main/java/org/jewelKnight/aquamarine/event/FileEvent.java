package com.impactcn.aquamarine.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author impactCn
 * @date 2023/10/6 2:37
 */
public class FileEvent extends ApplicationEvent {

    public FileEvent(Object source) {
        super(source);
    }

}
