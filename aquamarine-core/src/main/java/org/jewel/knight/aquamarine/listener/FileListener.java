package org.jewel.knight.aquamarine.listener;

import org.jewel.knight.aquamarine.event.FileEvent;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author impactCn
 * @date 2023/10/6 2:18
 */
@Component
public class FileListener extends FileAlterationListenerAdaptor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        System.err.println("文件创建");
        applicationContext.publishEvent(new FileEvent(this));
    }

    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
        applicationContext.publishEvent(new FileEvent(this));
    }

    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        applicationContext.publishEvent(new FileEvent(this));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
