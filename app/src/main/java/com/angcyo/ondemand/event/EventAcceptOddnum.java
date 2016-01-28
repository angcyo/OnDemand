package com.angcyo.ondemand.event;

/**
 * Created by angcyo on 2016-01-28.
 */
public class EventAcceptOddnum extends EventBase {
    public int position;

    public EventAcceptOddnum(int position, boolean isSucceed) {
        this.isSucceed = isSucceed;
        this.position = position;
    }
}
