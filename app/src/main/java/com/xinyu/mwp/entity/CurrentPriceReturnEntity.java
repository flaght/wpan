package com.xinyu.mwp.entity;


/**
 * Created by Administrator on 2017/2/23.
 */
public class CurrentPriceReturnEntity extends BaseEntity {

    /**
     * goodType : AG
     * exchangeName : TJPME
     * platformName : JH
     * currntPrice : 100.01
     * change : 12.1
     * openingTodayPrice : 1.12
     * closedYesterdayPrice : 1.33
     * highPrice : 1.11
     * lowPrice : 1.01
     * priceTime : 1483137783
     * upDown : 12.1
     *  "pchg": 0.06,
     */

    private String goodType;
    private String exchangeName;
    private String platformName;
    private double currentPrice;
    private double change;
    private double openingTodayPrice;
    private double closedYesterdayPrice;
    private double highPrice;
    private double lowPrice;
    private long priceTime;
    private double upDown;
    private double pchg;
    private String symbol;
    private long sysTime;//当前服务器时间	若当前服务器时间和报价时间相差60s，表示已经休盘或者不能进行交易
    private String showSymbol;

    public String getShowSymbol() {
        return showSymbol;
    }

    public void setShowSymbol(String showSymbol) {
        this.showSymbol = showSymbol;
    }

    public double getPchg() {
        return pchg;
    }

    public void setPchg(double pchg) {
        this.pchg = pchg;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getSysTime() {
        return sysTime;
    }

    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getOpeningTodayPrice() {
        return openingTodayPrice;
    }

    public void setOpeningTodayPrice(double openingTodayPrice) {
        this.openingTodayPrice = openingTodayPrice;
    }

    public double getClosedYesterdayPrice() {
        return closedYesterdayPrice;
    }

    public void setClosedYesterdayPrice(double closedYesterdayPrice) {
        this.closedYesterdayPrice = closedYesterdayPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public long getPriceTime() {
        return priceTime;
    }

    public void setPriceTime(long priceTime) {
        this.priceTime = priceTime;
    }

    public double getUpDown() {
        return upDown;
    }

    public void setUpDown(double upDown) {
        this.upDown = upDown;
    }

}
