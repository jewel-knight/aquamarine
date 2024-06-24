package com.impactcn.aquamarine.listener;

import com.impactcn.aquamarine.event.FileEvent;
import com.impactcn.aquamarine.monitor.FileMonitor;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
