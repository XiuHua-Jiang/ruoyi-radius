/**  
*  
*   
* @author pwl  
* @date 2020年11月26日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.thread;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import com.ruoyi.radius.toughradius.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.form.OrderForm;
import com.ruoyi.radius.toughradius.utils.alipay.AliPayPropertyConfig;
import com.ruoyi.radius.toughradius.utils.wx.HttpClient;
import com.ruoyi.radius.toughradius.utils.wx.WeChatPropertyConfig;
import com.ruoyi.radius.toughradius.utils.wx.WeChatUtils;
import com.ruoyi.radius.toughradius.utils.wx.XMLUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;


/**
 * 订单更新线程
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
public class OrderUpdateThread extends Observable implements Runnable{
	
	@Autowired
	private Memarylogger logger;
	@Autowired
	private AliPayPropertyConfig aliConfig;
	@Autowired
	private IMealService mealService;
	@Autowired
	private IOrderService orderService;
	@Autowired
    protected ISubscribeService subscribeService;
    @Autowired
    protected SubscribeCache subscribeCache;
    @Autowired
	private WeChatPropertyConfig wxConfig;
    @Autowired
	private HttpClient httpRequest;
	@Autowired
	private XMLUtils XMLUtils;
	@Autowired
	private WeChatUtils weChatUtils;
	/**
	 *  此方法一经调用，立马可以通知观察者，在本例中是监听线程
	 */
    public void doBusiness() {
        if (true) {
            super.setChanged();
        }
        // 通知监听器    出错了
        notifyObservers();
    }
	 /* (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("订单更新线程开启  ", Memarylogger.SYSTEM);
		while (true) {
			// 查询一天内的待支付订单
			OrderForm form = new OrderForm();
			form.setStatus(0);
			form.setStartDate(new Timestamp(System.currentTimeMillis() - 86400000));
			form.setEndDate(new Timestamp(System.currentTimeMillis() - 300000)); // 5分钟前
			List<Order> list = orderService.query(form);
			int count = 0;
			for (Order order : list) {
				count++;
				if(count > 30){// 订单过多时，休息一下
					try {
						Thread.sleep(5000);
						count = 0;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						doBusiness();
					}
				}
				switch (order.getPayType()) {
				case 1:
					new AliPayThread(order.getOrderNo(),order.getOpenId()).start();
					break;
				case 2:
					new WxPayThread(order.getOrderNo(),order.getOpenId()).start();
					break;
				default:
					break;
				}
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				doBusiness();
			}
		}
	}
	/**
	 * 支付宝订单查询线程
	 * 
	 * @author pwl
	 * @date 2020年11月26日
	 */
	class AliPayThread extends Thread{
		private String outTradeNo;
		private String tradeNo;
		
		public AliPayThread(String outTradeNo,String tradeNo){
			this.outTradeNo = outTradeNo;
			this.tradeNo = tradeNo;
		}
		@Override
		public void run() {
			try {
				queryAlipayOrder(this.outTradeNo,this.tradeNo);
			} catch (UnsupportedEncodingException | AlipayApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**
		 * 支付宝支付 订单查询
		 * 
		 * @param outTradeNo  商户订单号，商户网站订单系统中唯一订单号，必填 
		 * @param tradeNo     支付宝交易号
		 * @return
		 * @throws UnsupportedEncodingException
		 * @throws AlipayApiException
		 */
		public void queryAlipayOrder(String outTradeNo,String tradeNo) throws UnsupportedEncodingException, AlipayApiException {
			
			// SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
			AlipayClient client = new DefaultAlipayClient(aliConfig.url, aliConfig.appid, aliConfig.privateKey, aliConfig.format, aliConfig.charset, aliConfig.publicKey,aliConfig.signType);
			AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
			
			AlipayTradeQueryModel model=new AlipayTradeQueryModel();
			model.setOutTradeNo(outTradeNo);
			//model.setTradeNo(tradeNo);
			alipay_request.setBizModel(model);
			
			AlipayTradeQueryResponse alipay_response =client.execute(alipay_request);
			
			tradeNo = alipay_response.getTradeNo();// 更新支付宝交易号 
			
			/**
			 * trade_status 交易状态：
			 * WAIT_BUYER_PAY（交易创建，等待买家付款）、
			 * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
			 * TRADE_SUCCESS（交易支付成功）、
			 * TRADE_FINISHED（交易结束，不可退款
			 * 
			 * 详细参数可参考官网API：https://opendocs.alipay.com/apis/api_1/alipay.trade.query
			 */
			String tradeStatus = StringUtils.isEmpty(alipay_response.getTradeStatus()) ? "" : alipay_response.getTradeStatus().toUpperCase();
			int status = 0;
			if(tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")){
				status = 1;
			}else if(tradeStatus.equals("TRADE_CLOSED") || StringUtils.isEmpty(tradeStatus)){
				status = 2;
			}
			/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      开始************************/
			
			if(status == 1){// 支付成功
				// 更新订单
				Order order = new Order();
				order.setOrderNo(outTradeNo);
				order.setOpenId(tradeNo);
				order.setStatus(1);
				order.setUpdateTime(DateTimeUtil.nowTimestamp());
				orderService.updateOrder(order);
				// 创建或更新账号
				Order o = orderService.selectByOrderNo(outTradeNo);
				Meal meal = mealService.selectMealById(o.getMealId());
				long seconds = meal.getUseHour() * 3600;// 小时转秒
				Subscribe subscribe = subscribeService.findSubscribe(o.getCustomer());
				if(subscribe == null){
					// 创建缓存 数据
					subscribe = subscribeCache.createTempSubscribe(o.getCustomer(), o.getCustomer(), "", seconds,meal.getUseCount());
					// 写入数据库
					subscribeService.insertSubscribe(subscribe);
					
				}else{// 更新
					Timestamp expireTime = new Timestamp(subscribe.getExpireTime().getTime());
					if(expireTime.after(new Date())){
						// 现在还在使用期间，在原来的时间上累加
						subscribe.setExpireTime(new Timestamp(expireTime.getTime() + (seconds * 1000)));
					}else{
						//已经到期，从当前时间算过期时间
						subscribe.setExpireTime(new Timestamp(System.currentTimeMillis() + (seconds * 1000)));
					}
					subscribe.setMacAddr("");
					subscribe.setStatus("enabled");
					subscribe.setUpdateTime(DateTimeUtil.nowTimestamp());
					// 更新数据库
					subscribeService.updateSubscribe(subscribe);
					// 更新缓存 数据
					subscribeCache.updateSubscribeCache();
				}
				
			}else if(status == 2){// 支付失败
				// 更新订单
				Order order = new Order();
				order.setOrderNo(outTradeNo);
				order.setOpenId(tradeNo);
				order.setStatus(2);
				order.setUpdateTime(DateTimeUtil.nowTimestamp());
				orderService.updateOrder(order);
			}
			/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      结束************************/
		}
	}
	/**
	 * 微信订单查询线程
	 * 
	 * @author pwl
	 * @date 2020年11月26日
	 */
	class WxPayThread extends Thread{
		private String outTradeNo;
		private String tradeNo;
		
		public WxPayThread(String outTradeNo,String tradeNo){
			this.outTradeNo = outTradeNo;
			this.tradeNo = tradeNo;
		}
		@Override
		public void run() {
			queryWxpayOrder(this.outTradeNo,this.tradeNo);
		}
		/**
		 * 微信支付 订单查询
		 * 根据订单状态更新到数据库
		 * @param outTradeNo 商户系统内部订单号   二选一
		 * @param tradeNo 随机字符串
		 */
		public void queryWxpayOrder(String outTradeNo,String tradeNo) {
			TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
			treeMap.put("appid", wxConfig.getAppid());
			treeMap.put("mch_id", wxConfig.getMerchantId());
			//treeMap.put("transaction_id", "");
			//treeMap.put("out_trade_no", request.getSession().getAttribute("out_trade_no"));
			//treeMap.put("nonce_str", request.getSession().getAttribute("nonce_str"));
			treeMap.put("out_trade_no", outTradeNo);
			//treeMap.put("nonce_str", tradeNo);
			
			int signatureValid = 0; // 返回结果 0 不做处理  1支付成功  2支付失败
			
			String xmlBody = weChatUtils.packageOrderQueryXml(treeMap);
			//查询订单
			CloseableHttpResponse response = httpRequest.doPost(wxConfig.getOrderquery(), xmlBody);
			int status = response.getStatusLine().getStatusCode();
			try {
				if(status == HttpClient.SUCCESS) {
					//开始解析这个返回结果，取到需要的东西
					String httpResult = httpRequest.getRequestResult(response);
					// 过滤
					//httpResult = httpResult.replaceAll("<!\\[CDATA\\[|]]>", "");
					Map<String, Object> map = XMLUtils.doXMLParse(httpResult);
					//return_code：通信标识。可选：SUCCESS、FAIL。
					//result_code：业务处理标识。可选：SUCCESS、FAIL。
					//trade_state：交易标识。可选：SUCCESS、REFUND、NOTPAY、CLOSED、REVOKED、USERPAYING、PAYERROR 等。注意业务处理成功，不代表交易成功。
					String return_code = map == null || map.get("return_code") == null ? "" : String.valueOf(map.get("return_code"));
					String result_code = map == null || map.get("result_code") == null ? "" : String.valueOf(map.get("result_code"));
					//String trade_state = map == null || map.get("trade_state") == null ? "" : String.valueOf(map.get("trade_state"));
					tradeNo = map == null || map.get("transaction_id") == null ? null : String.valueOf(map.get("transaction_id"));// 微信支付订单号
					if(WeChatUtils.SUCCESS.equals(return_code) && WeChatUtils.SUCCESS.equals(result_code)) {
						/**
						 * trade_state   返回值说明
						 * SUCCESS—支付成功
							REFUND—转入退款
							NOTPAY—未支付
							CLOSED—已关闭
							REVOKED—已撤销（付款码支付）
							USERPAYING--用户支付中（付款码支付）
							PAYERROR--支付失败(其他原因，如银行返回失败)
							
							详细参数可参考官网：https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=9_2&index=2
						 */
						/*if(WeChatUtils.SUCCESS.equals(trade_state)){
							signatureValid = 1;
						}else if(trade_state.toUpperCase().equals("CLOSED") || trade_state.toUpperCase().equals("REVOKED") || trade_state.toUpperCase().equals("PAYERROR")){
							signatureValid = 2;
						}*/
						signatureValid = 1;
					// 状态和订单号为空时，判定为交易失败
					}else if(StringUtils.isEmpty(return_code) || StringUtils.isEmpty(result_code) || StringUtils.isEmpty(tradeNo)){
						signatureValid = 2;
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
				logger.error("微信订单查询失败:" + outTradeNo,e,Memarylogger.SYSTEM);
			}
			
			
			/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      开始************************/
			Order order = new Order();
			if(signatureValid == 1){// 支付成功
				// 更新订单
				order.setOrderNo(outTradeNo);
				order.setOpenId(tradeNo);
				order.setStatus(1);
				order.setUpdateTime(DateTimeUtil.nowTimestamp());
				orderService.updateOrder(order);
				// 创建或更新账号
				Order o = orderService.selectByOrderNo(outTradeNo);
				Meal meal = mealService.selectMealById(o.getMealId());
				long seconds = meal.getUseHour() * 3600;// 小时转秒
				Subscribe subscribe = subscribeService.findSubscribe(o.getCustomer());
				if(subscribe == null){
					// 创建缓存 数据
					subscribe = subscribeCache.createTempSubscribe(o.getCustomer(), o.getCustomer(), "", seconds,meal.getUseCount());
					// 写入数据库
					subscribeService.insertSubscribe(subscribe);
					
				}else{// 更新
					Timestamp expireTime = new Timestamp(subscribe.getExpireTime().getTime());
					if(expireTime.after(new Date())){
						// 现在还在使用期间，在原来的时间上累加
						subscribe.setExpireTime(new Timestamp(expireTime.getTime() + (seconds * 1000)));
					}else{
						//已经到期，从当前时间算过期时间
						subscribe.setExpireTime(new Timestamp(System.currentTimeMillis() + (seconds * 1000)));
					}
					subscribe.setMacAddr("");
					subscribe.setStatus("enabled");
					subscribe.setUpdateTime(DateTimeUtil.nowTimestamp());
					// 更新数据库
					subscribeService.updateSubscribe(subscribe);
					// 更新缓存 数据
					subscribeCache.updateSubscribeCache();
				}
				
			}else if(signatureValid == 2){// 支付失败
				// 更新订单
				order.setOrderNo(outTradeNo);
				if(!StringUtils.isEmpty(tradeNo)) order.setOpenId(tradeNo);
				order.setStatus(2);
				order.setUpdateTime(DateTimeUtil.nowTimestamp());
				orderService.updateOrder(order);
			}
			/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      结束************************/
			//return false;
		}
	}
}
