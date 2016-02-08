package com.angcyo.ondemand.model;

/**
 * 商圈表
 * Created by angcyo on 16-01-27-027.
 */
public class TableTradingArea{
    /*od_tradingarea	商圈信息	1	pid			int	4	10	0		((0))	商圈ID(上级)
    2	sid	√	√	int	4	10	0			商圈ID(本级）
            3	dscrp			varchar	50	50	0			商圈描述（名称）
            4	seqc			int	4	10	0		((0))	序列
    5	clsid			tinyint	1	3	0			商圈类型
*/

    int pid;//上级id
    int sid;//本级id
    String  dscrp;//商圈描述
    int seqc;//序列
    int clsid;//类型

    public int getPid() {
        return pid;
    }

    public TableTradingArea setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public int getSid() {
        return sid;
    }

    public TableTradingArea setSid(int sid) {
        this.sid = sid;
        return this;
    }

    public String getDscrp() {
        return dscrp;
    }

    public TableTradingArea setDscrp(String dscrp) {
        this.dscrp = dscrp;
        return this;
    }

    public int getSeqc() {
        return seqc;
    }

    public TableTradingArea setSeqc(int seqc) {
        this.seqc = seqc;
        return this;
    }

    public int getClsid() {
        return clsid;
    }

    public TableTradingArea setClsid(int clsid) {
        this.clsid = clsid;
        return this;
    }
}
