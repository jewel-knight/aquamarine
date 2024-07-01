package org.jewel.knight.aquamarine.service.impl;

import org.jewel.knight.aquamarine.service.WebEngineService;
import javafx.scene.web.WebEngine;
import org.springframework.stereotype.Service;

/**
 * @author impactCn
 * @date 2023/11/25 14:14
 */
@Service
public class WebEngineServiceImpl implements WebEngineService {

    private WebEngine webEngine;


    @Override
    public void setWebEngine(WebEngine engine) {
        webEngine = engine;
    }

    @Override
    public WebEngine getWebEngine() {
        return webEngine;
    }

    @Override
    public String getContent() {
        assert webEngine != null;
        return (String) webEngine.executeScript("document.documentElement.outerHTML");
    }

    @Override
    public void scroll(Double scrollHeight) {
        setVerticalScrollPosition(webEngine, scrollHeight);
    }

    private void setVerticalScrollPosition(WebEngine webEngine, double position) {
        // 执行JavaScript代码以设置垂直滚动位置
        String script = "window.scrollTo(0, " + position + ");";

//        String s = """
//                    window.scrollTo({
//                      top: 1500,
//                      left: 0, // 设置 left 为 0，表示不改变水平方向的滚动位置
//                      behavior: 'smooth' // 可选，以平滑的动画效果进行滚动
//                    });""";
        webEngine.executeScript(script);
    }
}
