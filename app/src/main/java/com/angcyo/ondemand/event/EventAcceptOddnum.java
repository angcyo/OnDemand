package com.angcyo.ondemand.event;

/**
 * Created by angcyo on 2016-01-28.
 */
public class EventAcceptOddnum extends EventBase {
    public int position;
    public int state;

    public EventAcceptOddnum() {
    }

    public EventAcceptOddnum(int position) {
        this.position = position;
    }

    public EventAcceptOddnum(int position, boolean isSucceed) {
        this.isSucceed = isSucceed;
        this.position = position;
    }

    public EventAcceptOddnum(boolean isSucceed, int position, int state) {
        this.position = position;
        this.state = state;
        this.isSucceed = isSucceed;
    }
}
