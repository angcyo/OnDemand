package com.angcyo.ondemand.event;

/**
 * Created by angcyo on 15-09-26-026.
 */
public class EventLoadData {
    public boolean isAutoRefresh = false;
    public boolean isARefresh = false;

    public EventLoadData() {
    }

    public EventLoadData(boolean isARefresh, boolean isAutoRefresh) {
        this.isARefresh = isARefresh;
        this.isAutoRefresh = isAutoRefresh;
    }
}
