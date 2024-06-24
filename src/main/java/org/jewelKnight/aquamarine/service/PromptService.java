package com.impactcn.aquamarine.service;

/**
 * @author impactCn
 * @date 2024/1/3 23:11
 */
public interface PromptService {

    /**
     * 信息提取
     * @param text
     * @return
     */
    String preface(String text);

    String rag(String text);

    void initData();
}
