package com.ruoyi.radius.toughradius.domain;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 统计信息实体类
 */
public class RadiusMain {

    private static final long serialVersionUID = 1L;
    /**
     * 订单总数
     */
    private String orderCount;
    /**
     * 订单总金额
     */
    private String orderSum;
    /**
     * 设备总数
     */
    private String brasCount;
    /**
     * 临时访客总数
     */
    private String tempUserCount;
    /**
     * 注册用户总数
     */
    private String registerUserCount;

    /**
     * 在线设备数
     */
    private String onLineBrasCount;
    /**
     * 在线临时用户数
     */
    private String onLineTempUserCount;
    /**
     * 在线注册用户数
     */
    private String onLineRegisterUserCount;

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = formatData(orderCount);
    }

    public String getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(String orderSum) {
        this.orderSum = formatMoney(orderSum);
    }

    public String getBrasCount() {
        return brasCount;
    }

    public void setBrasCount(String brasCount) {
        this.brasCount = formatData(brasCount);
    }

    public String getTempUserCount() {
        return tempUserCount;
    }

    public void setTempUserCount(String tempUserCount) {
        this.tempUserCount = formatData(tempUserCount);
    }

    public String getRegisterUserCount() {
        return registerUserCount;
    }

    public void setRegisterUserCount(String registerUserCount) {
        this.registerUserCount = formatData(registerUserCount);
    }

    public String getOnLineBrasCount() {
        return onLineBrasCount;
    }

    public void setOnLineBrasCount(String onLineBrasCount) {
        this.onLineBrasCount = formatData(onLineBrasCount);
    }

    public String getOnLineTempUserCount() {
        return onLineTempUserCount;
    }

    public void setOnLineTempUserCount(String onLineTempUserCount) {
        this.onLineTempUserCount = formatData(onLineTempUserCount);
    }

    public String getOnLineRegisterUserCount() {
        return onLineRegisterUserCount;
    }

    public void setOnLineRegisterUserCount(String onLineRegisterUserCount) {
        this.onLineRegisterUserCount = formatData(onLineRegisterUserCount);
    }

    /**
     * 格式化金额  千分位和两个小数
     * @param str
     * @return
     */
    private String formatMoney(String str){
        String money = BigDecimal.valueOf(Long.valueOf(str)).divide(new BigDecimal(100)).toString();
        if(money.indexOf(".") > -1){
            money = formatData(money.substring(0,money.indexOf("."))) + money.substring(money.indexOf("."),money.length());
        }
        return money;
    }

    /**
     * 格式化数字 千分位
     * @param str
     * @return
     */
    private String formatData(String str){
        DecimalFormat df = new DecimalFormat("#,###");
        try {
            return df.format(Long.parseLong(str));
        }catch (Exception e){
            return "0";
        }

    }

    @Override
    public String toString() {
        return "RadiusMain{" +
                "orderCount='" + orderCount + '\'' +
                ", orderSum='" + orderSum + '\'' +
                ", brasCount='" + brasCount + '\'' +
                ", tempUserCount='" + tempUserCount + '\'' +
                ", registerUserCount='" + registerUserCount + '\'' +
                ", onLineBrasCount='" + onLineBrasCount + '\'' +
                ", onLineTempUserCount='" + onLineTempUserCount + '\'' +
                ", onLineRegisterUserCount='" + onLineRegisterUserCount + '\'' +
                '}';
    }
}
