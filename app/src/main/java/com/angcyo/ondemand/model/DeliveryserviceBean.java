package com.angcyo.ondemand.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by angcyo on 16-01-27-027.
 */
public class DeliveryserviceBean implements Parcelable {
    public static final Creator<DeliveryserviceBean> CREATOR = new Creator<DeliveryserviceBean>() {
        public DeliveryserviceBean createFromParcel(Parcel source) {
            return new DeliveryserviceBean(source);
        }

        public DeliveryserviceBean[] newArray(int size) {
            return new DeliveryserviceBean[size];
        }
    };
    public boolean isAccept = false;//是否已接单
    public boolean isTake = false;//是否已取货
    //select * from dbo.get_tradingarea_order_seller(50) where  status=0 and dt_create>='2015-11-16 12:00'
    //返回的值
    int sid;//订单sid
    String seller_order_identifier;//订单号
    int status;//订单状态
    String comment;//描述
    String dt_create;//创建时间
    String caption;//商家
    String address;//目标地址
    String ec_caption;//平台信息
    String phone;//消费者手机号码
    String name;//消费者姓名

    public DeliveryserviceBean() {
    }

    protected DeliveryserviceBean(Parcel in) {
        this.sid = in.readInt();
        this.seller_order_identifier = in.readString();
        this.status = in.readInt();
        this.comment = in.readString();
        this.dt_create = in.readString();
        this.caption = in.readString();
        this.address = in.readString();
        this.ec_caption = in.readString();
        this.isAccept = in.readByte() != 0;
        this.isTake = in.readByte() != 0;
        this.phone = in.readString();
        this.name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEc_caption() {
        return ec_caption;
    }

    public void setEc_caption(String ec_caption) {
        this.ec_caption = ec_caption;
    }

    public int getSid() {
        return sid;
    }

    public DeliveryserviceBean setSid(int sid) {
        this.sid = sid;
        return this;
    }

    public String getSeller_order_identifier() {
        return seller_order_identifier;
    }

    public DeliveryserviceBean setSeller_order_identifier(String seller_order_identifier) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        DeliveryserviceBean bean = (DeliveryserviceBean) o;
        if (this.getSeller_order_identifier().equalsIgnoreCase(bean.getSeller_order_identifier()) &&
                this.getStatus() == bean.getStatus() && this.getDt_create().equalsIgnoreCase(bean.getDt_create())) {
            return true;
        }
        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sid);
        dest.writeString(this.seller_order_identifier);
        dest.writeInt(this.status);
        dest.writeString(this.comment);
        dest.writeString(this.dt_create);
        dest.writeString(this.caption);
        dest.writeString(this.address);
        dest.writeString(this.ec_caption);
        dest.writeByte(isAccept ? (byte) 1 : (byte) 0);
        dest.writeByte(isTake ? (byte) 1 : (byte) 0);
        dest.writeString(this.phone);
        dest.writeString(this.name);
    }
}
