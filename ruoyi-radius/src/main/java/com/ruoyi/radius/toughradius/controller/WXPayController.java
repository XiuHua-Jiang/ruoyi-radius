package com.ruoyi.radius.toughradius.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.service.IMealService;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.service.OnlineCache;
import com.ruoyi.radius.toughradius.service.IOrderService;
import com.ruoyi.radius.toughradius.service.SubscribeCache;
import com.ruoyi.radius.toughradius.service.ISubscribeService;
import com.ruoyi.radius.toughradius.service.WXPayService;
import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.utils.ResultMsgUtils;
import com.ruoyi.radius.toughradius.utils.wx.QRCodeUtil;
import com.ruoyi.radius.toughradius.utils.wx.WXPayUtil;
import com.ruoyi.radius.toughradius.utils.wx.WeChatUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;

/**
 * 微信支付
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
@RequestMapping("/wifiweb/wxpay")
public class WXPayController {
	
	@Autowired
	protected Memarylogger logger;
	@Autowired
	private WXPayService service;
	@Autowired
	private WeChatUtils weChatUtils;
	@Autowired
	private IMealService mealService;
	@Autowired
	private IOrderService orderService;
	@Autowired
    protected ISubscribeService subscribeService;
	@Autowired
    protected SubscribeCache subscribeCache;
	@Autowired
	protected OnlineCache onlineCache;

	/**
	 * H5支付
	 * @param request
	 * @return
	 */
	@PostMapping("/h5")
	@ResponseBody
	public JSONObject wechatPay(HttpServletRequest request) {
		logger.info("进入方法：H5支付",Memarylogger.WXPAY);
		//订单来源ip
		String bill_create_ip = request.getRemoteAddr();
		String mweb_url = service.wxH5Pay(request,bill_create_ip);
		
		return ResultMsgUtils.success(mweb_url);
	}

	/**
	 * 支付后实时查询订单支付结果
	 * @param request
	 * @return
	 */
	@PostMapping("/queryOrderStatus")
	@ResponseBody
	public JSONObject queryOrderStatus(HttpServletRequest request) {
		logger.info("进入方法：查询订单支付结果queryOrderStatus",Memarylogger.WXPAY);
		boolean signatureValid = service.queryOrderStatus(request);
		if(signatureValid){
			return ResultMsgUtils.success(signatureValid);
		} 
		return ResultMsgUtils.error("操作失败");
	}

	/**
	 * 支付异步回调，供微信异步调用
	 * @param request
	 * @param response
	 */
	@RequestMapping("/payback")
	public void weChatPayResult(HttpServletRequest request,HttpServletResponse response) {
		logger.info("进入异步回调方法payback",Memarylogger.WXPAY);
		try {
			//将微信的返回参数封装成map
			Map<String, Object> reqData = weChatUtils.getWeChatReplay(request);
			//logger.info("微信回调传回的参数是：" + JSONObject.toJSONString(reqData),Memarylogger.WXPAY);
			String returnCode = String.valueOf(reqData.get("return_code"));
	        String resultCode = String.valueOf(reqData.get("result_code"));
	        if (WeChatUtils.SUCCESS.equals(returnCode) && WeChatUtils.SUCCESS.equals(resultCode)) {
	        	
	            boolean signatureValid = service.orderQuery(reqData);
	            //logger.info("signatureValid：" + signatureValid,Memarylogger.WXPAY);
	            
	            /********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      开始************************/
	    		Order order = new Order();
	    		String orderNo = String.valueOf(reqData.get("out_trade_no"));
	    		String tradeNo = String.valueOf(reqData.get("transaction_id"));// 微信支付订单号
	    		if(signatureValid){// 支付成功
	    			// 更新订单
	    			order.setOrderNo(orderNo);
	    			order.setOpenId(tradeNo);
	    			order.setStatus(1);
	    			order.setUpdateTime(DateTimeUtil.nowTimestamp());
	    			orderService.updateOrder(order);
	    			
	    			Order o = orderService.selectByOrderNo(orderNo);
	    			// 把临时账号踢下线
	    			if(StringUtils.isNotEmpty(o.getTempUserName())){
	    				onlineCache.unlockOnlineByUser(o.getTempUserName());
	    			}
	    			// 创建或更新账号
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
	    				// 动态修改用户权限
	    				onlineCache.DynamicAuthorizationByUserName(o.getCustomer());
	    			}
	    			
	    		}else{// 支付失败
	    			// 更新订单
	    			order.setOrderNo(orderNo);
	    			//order.setOpenId(tradeNo);
	    			order.setStatus(2);
	    			order.setUpdateTime(DateTimeUtil.nowTimestamp());
	    			orderService.updateOrder(order);
	    		}
	    		/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      结束************************/
	    		
	            if (signatureValid) {
	            	
	            	//支付成功后 从缓存中取出订单号和验证码 存入数据库 并把缓存中的记录清除 防止二次操作
	            	/*String code = String.valueOf(CacheUtils.get(Constant.ORDER_CACHE,String.valueOf(reqData.get("out_trade_no"))));
	            	LOGGER.debug("code：" + code);
	            	if(!StringUtils.isBlank(code)){//这里的数字10表示可用10次 后面看情况再调整
	                	//存入数据库
	                	checkSerivce.createCheckCode(new WifiCheck(code,10,ResourceUtil.effective * 60 * 60 * 1000));
	                	//清除缓存数据
	                	CacheUtils.remove(Constant.ORDER_CACHE,String.valueOf(reqData.get("out_trade_no")));
	                	LOGGER.debug("存入数据库，清除缓存数据完成");
	                }*/
	            	Map<String, String> responseMap = new HashMap<>(2);
	                responseMap.put("return_code", "SUCCESS");
	                responseMap.put("return_msg", "OK");
	                String responseXml = WXPayUtil.mapToXml(responseMap);
	                response.setContentType("text/xml");
	                response.getWriter().write(responseXml);
	                response.flushBuffer();
	            }
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("退出异步回调方法payback",Memarylogger.WXPAY);
	}

	/**
	 * native扫码支付
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) throws Exception{

    	//订单来源ip
		String bill_create_ip = request.getRemoteAddr();
		//获取微信支付二维码URL
		String code_url = service.nativePay(request,bill_create_ip);
		
		byte[] qrcode = null;
		try {
			//生成二维码byte
			qrcode = QRCodeUtil.getQRCodeImage(code_url);
		} catch (IOException | WriterException e) {
			logger.error("Could not generate QR Code, Exception :: ",e, Memarylogger.WXPAY);
		}
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qrcode, headers, HttpStatus.OK);
    }

}
