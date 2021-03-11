package com.ruoyi.radius.toughradius.utils.wx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 微信支付配置信息工具类
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
@PropertySource(value = "classpath:conf/wechatConfig.properties")
@ConfigurationProperties(prefix = "wechat")
@Data
public class WeChatPropertyConfig {
	public String appid;
	public String appSecret;
	public String merchantId;
	public String merchantName;
	public String merchantKey;
	public String notifyUrl;
	public String unifiedorder;
	public String orderquery;
	public String accessTocken;
	public String redirectUrl;
	public String amount;
	public String title;
	
	/** 支付成功后的服务器回调url */
	public static final String NOTIFYURL = "https://api.weixin.qq.com/sns/jscode2session";
	
	/** 签名方式 */
	public static final String SIGNTYPE = "MD5";
	
	/** 交易类型 */
	public static final String TRADETYPE = "JSAPI";
	
	/** 微信下单接口url */
	public static final String PAYURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getUnifiedorder() {
		return unifiedorder;
	}

	public void setUnifiedorder(String unifiedorder) {
		this.unifiedorder = unifiedorder;
	}

	public String getOrderquery() {
		return orderquery;
	}

	public void setOrderquery(String orderquery) {
		this.orderquery = orderquery;
	}

	public String getAccessTocken() {
		return accessTocken;
	}

	public void setAccessTocken(String accessTocken) {
		this.accessTocken = accessTocken;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
