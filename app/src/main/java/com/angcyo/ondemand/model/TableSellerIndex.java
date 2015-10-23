package com.angcyo.ondemand.model;

/**
 * Created by angcyo on 15-10-14-014.
 */
public class TableSellerIndex extends BaseTable {
    /*
    * 商户基本信息索引
    * 1	sid	√	√	int	4	10	0			商户ID
	2	company			varchar	60	60	0			商户名称
	3	address			varchar	100	100	0			商户详细地址
	4	contacts			varchar	16	16	0			联系人
	5	phone			varchar	50	50	0			联系电话

    * */
    String company;
    String address;
    String contacts;
    String phone;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

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
}
