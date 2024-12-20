package org.jewel.knight.aquamarine.vo;

import java.util.List;

/**
 * @author sinsy
 * @date 2024-12-20
 */
public class FileVO {

    private String md5;

    private List<String> ids;

    public FileVO(String md5, List<String> ids) {
        this.md5 = md5;
        this.ids = ids;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
