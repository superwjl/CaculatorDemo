package com.tik.caculatordemo.bean;


public class LoanBean {

    private int stage;//期数
    private int yuegong;//月供
    private int yuegongbenjin;//本金每月
    private int yuegonglixi;//利息每月
    private int totalRepaid;//已还总额
    private int benjinRepaid;//已还本金总额
    private int benjinLeft;//剩余本金
    private int lixiRepaid;//已还利息总额

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getYuegong() {
        return yuegong;
    }

    public void setYuegong(int yuegong) {
        this.yuegong = yuegong;
    }

    public int getYuegongbenjin() {
        return yuegongbenjin;
    }

    public void setYuegongbenjin(int yuegongbenjin) {
        this.yuegongbenjin = yuegongbenjin;
    }

    public int getYuegonglixi() {
        return yuegonglixi;
    }

    public void setYuegonglixi(int yuegonglixi) {
        this.yuegonglixi = yuegonglixi;
    }

    public int getTotalRepaid() {
        return totalRepaid;
    }

    public void setTotalRepaid(int totalRepaid) {
        this.totalRepaid = totalRepaid;
    }

    public int getBenjinRepaid() {
        return benjinRepaid;
    }

    public void setBenjinRepaid(int benjinRepaid) {
        this.benjinRepaid = benjinRepaid;
    }

    public int getLixiRepaid() {
        return lixiRepaid;
    }

    public void setLixiRepaid(int lixiRepaid) {
        this.lixiRepaid = lixiRepaid;
    }

    public int getBenjinLeft() {
        return benjinLeft;
    }

    public void setBenjinLeft(int benjinLeft) {
        this.benjinLeft = benjinLeft;
    }
}
