package org.jewel.knight.aquamarine.service;

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

    /**
     * 写作润色
     * @param text
     * @return
     */
    String suggest(String text);

    void initData();

    /**
     * 重新加载数据
     * @param path
     */
    void reloadData(String path);
}
