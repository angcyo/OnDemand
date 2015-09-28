package com.angcyo.ondemand.model;

/**
 * 电子商务平台信息
 * Created by angcyo on 15-09-26-026.
 */
public class TablePlatform extends BaseTable{
    String caption = "";//平台名称

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
