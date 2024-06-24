package com.impactcn.aquamarine.service;

import javafx.scene.web.WebEngine;

/**
 * @author impactCn
 * @date 2023/11/25 14:11
 */
public interface WebEngineService {


    void setWebEngine(WebEngine engine);

    /**
     * 获取 webEngine
     * @return
     */
    WebEngine getWebEngine();



    /**
     * 获取 webEngine 加载的内容，带 HTML 标签的内容
     * @return
     */
    String getContent();

    void scroll(Double scrollHeight);


}
