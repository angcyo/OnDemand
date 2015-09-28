package com.angcyo.ondemand.model;

/**
 * 配送公司信息
 * Created by angcyo on 15-09-26-026.
 */
public class TableCompany extends BaseTable {
    String caption;//		varchar	50	50	0			单位名称
    String address;//		varchar	50	50	0			单位地址
    String contacts;//		varchar	50	50	0			单位联系人
    String phone;//		varchar	50	50	0			单位电话

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
