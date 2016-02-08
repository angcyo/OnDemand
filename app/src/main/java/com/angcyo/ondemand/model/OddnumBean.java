package com.angcyo.ondemand.model;

/**
 * Created by angcyo on 15-09-27-027.
 */
public class OddnumBean {
    public String caption;//订单平台
    public int platformId;//平台id
    public String oddnum;//订单号
    public int memberId;//配送员id
    public int sid;//订单sid

    public int sid_seller = 0;//商户id
    public int sid_customer = 0;//消费者id
    public String customerPhone;//消费者手机号码

    public int status = 2;//订单的状态 //订单状态（0待命 1锁单 2派送中 3派送丢失 4已撤销 9客户已收）


    @Override
    public boolean equals(Object o) {
        if (this.platformId == ((OddnumBean) o).platformId && this.oddnum.equalsIgnoreCase(((OddnumBean) o).oddnum)) {
            return true;
        }
        return false;
    }
}
