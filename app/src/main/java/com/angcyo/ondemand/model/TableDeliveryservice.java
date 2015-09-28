package com.angcyo.ondemand.model;

/**
 * 商户订单配送情况
 * Created by angcyo on 15-09-26-026.
 */
public class TableDeliveryservice extends BaseTable {
    String seller_order_identifier;//			varchar	20	20	0			商户订单号
    int sid_ec;//			int	4	10	0			电商平台ID
    int sid_seller;//		int	4	10	0		((0))	所属商户ID
    int sid_customer;//			int	4	10	0		((0))	消费者ID
    int sid_member;//			int	4	10	0		((0))	配送员ID
    String dt_locked;//		datetime	8	23	3	√		锁单时间（配送员终端接受送任务）
    String dt_start;//	datetime	8	23	3	√		配送开始时间（配送员取到派送物件）
    String dt_end;//	datetime	8	23	3	√		配送完成时间（配送员派送到达目的地完成文件交接）
    int status;//			tinyint	1	3	0		((0))	订单状态（0待命 1锁单 2派送中 3派送丢失 4客户拒收 9客户已收）
    String comment;//			varchar	100	100	0	√		备注

    public String getSeller_order_identifier() {
        return seller_order_identifier;
    }

    public void setSeller_order_identifier(String seller_order_identifier) {
        this.seller_order_identifier = seller_order_identifier;
    }

    public int getSid_ec() {
        return sid_ec;
    }

    public void setSid_ec(int sid_ec) {
        this.sid_ec = sid_ec;
    }

    public int getSid_seller() {
        return sid_seller;
    }

    public void setSid_seller(int sid_seller) {
        this.sid_seller = sid_seller;
    }

    public int getSid_customer() {
        return sid_customer;
    }

    public void setSid_customer(int sid_customer) {
        this.sid_customer = sid_customer;
    }

    public int getSid_member() {
        return sid_member;
    }

    public void setSid_member(int sid_member) {
        this.sid_member = sid_member;

    }

    public String getDt_locked() {
        return dt_locked;
    }

    public void setDt_locked(String dt_locked) {
        this.dt_locked = dt_locked;
    }

    public String getDt_start() {
        return dt_start;
    }

    public void setDt_start(String dt_start) {
        this.dt_start = dt_start;
    }

    public String getDt_end() {
        return dt_end;
    }

    public void setDt_end(String dt_end) {
        this.dt_end = dt_end;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
