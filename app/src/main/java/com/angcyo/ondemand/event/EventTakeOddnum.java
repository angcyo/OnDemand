package com.angcyo.ondemand.event;

/**
 * Created by angcyo on 2016-01-28.
 */
public class EventTakeOddnum extends EventBase {
    public int position;

    public EventTakeOddnum(int position, boolean isSucceed) {
        this.isSucceed = isSucceed;
        this.position = position;
    }
}
