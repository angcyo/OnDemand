package com.angcyo.ondemand.event;

/**
 * Created by angcyo on 2016-01-28.
 */
public class EventTakeOddnum extends EventBase {
    public int position;
    public int state;

    public EventTakeOddnum() {
    }

    public EventTakeOddnum(int position) {
        this.position = position;
    }

    public EventTakeOddnum(int position, boolean isSucceed) {
        this.isSucceed = isSucceed;
        this.position = position;
    }

    public EventTakeOddnum(boolean isSucceed, int position, int state) {
        this.position = position;
        this.state = state;
        this.isSucceed = isSucceed;
    }
}
