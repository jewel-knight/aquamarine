package com.impactcn.aquamarine;

import com.impactcn.aquamarine.listener.FileListener;
import com.impactcn.aquamarine.monitor.FileMonitor;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class AquamarineApplication {

    @Value("${file.path}")
    private String path;

    @Autowired
    private FileListener fileListener;

    public static void main(String[] args) {
        Application.launch(UiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            FileMonitor fileMonitor = new FileMonitor(1000);
            fileMonitor.monitor(path, fileListener);
            try {
                fileMonitor.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

}