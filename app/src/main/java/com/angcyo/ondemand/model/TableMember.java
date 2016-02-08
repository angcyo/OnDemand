package com.angcyo.ondemand.model;

import com.angcyo.ondemand.util.Util;

/**
 * 配送员信息（移动终端）
 * Created by angcyo on 15-09-26-026.
 */
public class TableMember extends BaseTable {
    String name_login;//		varchar	50	50	0			登陆名,,已经变成身份证信息了
    String name_real;//		varchar	8	8	0	√		实名
    String phone;//		varchar	13	13	0			电话
    int id_company;//		int	4	10	0			所属单位（SID）
    String online;//	datetime	8	23	3	√		末次在线时间
    String psw;//	nchar	32	16	0	√		二进制密码（压缩MD5）
    int id_tradingarea;//所负责商圈

    public TableMember() {
        this.name_login = "";
        this.name_real = "";
        this.phone = "";
        this.id_company = -1;
        this.online = Util.getDateAndTime();
        this.psw = "";
        this.id_tradingarea = -1;
    }

    public String getName_login() {
        return name_login;
    }

    public void setName_login(String name_login) {
        this.name_login = name_login;
    }

    public String getName_real() {
        return name_real;
    }

    public void setName_real(String name_real) {
        this.name_real = name_real;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId_company() {
        return id_company;
    }

    public void setId_company(int id_company) {
        this.id_company = id_company;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public int getId_tradingarea() {
        return id_tradingarea;
    }

    public TableMember setId_tradingarea(int id_tradingarea) {
        this.id_tradingarea = id_tradingarea;
        return this;
    }
}
