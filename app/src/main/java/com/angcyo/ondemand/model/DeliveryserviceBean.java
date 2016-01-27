package com.angcyo.ondemand.model;

/**
 * Created by angcyo on 16-01-27-027.
 */
public class DeliveryserviceBean {
    //select * from dbo.get_tradingarea_order_seller(50) where  status=0 and dt_create>='2015-11-16 12:00'
    //返回的值
    int sid;//订单sid
    int seller_order_identifier;//订单号
    int status;//订单状态
    String comment;//描述
    String dt_create;//创建时间
    String caption;//商家

    public int getSid() {
        return sid;
    }

    public DeliveryserviceBean setSid(int sid) {
        this.sid = sid;
        return this;
    }

    public int getSeller_order_identifier() {
        return seller_order_identifier;
    }

    public DeliveryserviceBean setSeller_order_identifier(int seller_order_identifier) {
        this.seller_order_identifier = seller_order_identifier;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public DeliveryserviceBean setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public DeliveryserviceBean setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getDt_create() {
        return dt_create;
    }

    public DeliveryserviceBean setDt_create(String dt_create) {
        this.dt_create = dt_create;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public DeliveryserviceBean setCaption(String caption) {
        this.caption = caption;
        return this;
    }
}
