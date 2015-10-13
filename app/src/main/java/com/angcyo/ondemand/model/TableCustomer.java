package com.angcyo.ondemand.model;

/**
 * Created by angcyo on 15-10-13-013.
 */
public class TableCustomer extends  BaseTable{
//    customer_index	消费者基本信息	1	sid	√	√	int	4	10	0			消费者ID
//    3	name			varchar	20	20	0	√		实名
//    4	address			varchar	100	100	0	√		地址（送货）
//            5	phone			varchar	16	16	0			电话

    String nickname;
    String name;
    int pid;
    String address;
    String phone;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
