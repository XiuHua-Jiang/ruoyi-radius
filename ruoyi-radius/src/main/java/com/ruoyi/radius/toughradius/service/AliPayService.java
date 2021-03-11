/**  
* <p>Title: AliPayService.java</p>  
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.szcenterstar.com</p>  
* @author pwl  
* @date 2019年10月28日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.toughradius.common.CoderUtil;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.utils.ResultMsgUtils;
import com.ruoyi.radius.toughradius.utils.alipay.AliPayPropertyConfig;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

/**
 * 支付宝支付业务处理类
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class AliPayService {
	Log log = LogFactory.getLog(AliPayService.class);
	@Autowired
	private AliPayPropertyConfig config;
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
	 * @throws UnsupportedEncodingException
	 */
	public String h5pay(HttpServletRequest request) throws UnsupportedEncodingException{
		
		String customer = new String(request.getParameter("customer").getBytes("ISO-8859-1"),"UTF-8");
		
		String mealId = new String(request.getParameter("mealId").getBytes("ISO-8859-1"),"UTF-8");

		int price = (mealService.selectMealById(Integer.parseInt(mealId)).getPrice());
		
		// 商户订单号，商户网站订单系统中唯一订单号，必填
	    String out_trade_no = String.valueOf(CoderUtil.randomLong15Id());//new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		// 订单名称，必填
	    String subject = "WIFI上网充值,手机号码为" + customer;//new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
	    // 付款金额，必填   config.amount;
	    String total_amount = BigDecimal.valueOf(Long.valueOf(price)).divide(new BigDecimal(100)).toString();;
	    // 商品描述，可空
	    String body = "WIFI上网充值,手机号码为" + customer;//new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
	    // 超时时间 可空
	    String timeout_express="2m";
	    // 销售产品码 必填
	    String product_code="QUICK_WAP_WAY";
	    /**********************/
	    // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
	    //调用RSA签名方式
	    AlipayClient client = new DefaultAlipayClient(config.url, config.appid, config.privateKey, config.format, config.charset, config.publicKey,config.signType);
	    AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
	    
	    // 封装请求支付信息
	    AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
	    model.setOutTradeNo(out_trade_no);
	    model.setSubject(subject);
	    model.setTotalAmount(total_amount);
	    model.setBody(body);
	    model.setTimeoutExpress(timeout_express);
	    model.setProductCode(product_code);
	    alipay_request.setBizModel(model);
	    // 设置异步通知地址
	    alipay_request.setNotifyUrl(config.notifyUrl);
	    // 设置同步地址
	    alipay_request.setReturnUrl(config.returnUrl);   
	    
	    // form表单生产
	    String form = "";
		try {
			// 调用SDK生成表单
			form = client.pageExecute(alipay_request).getBody();
			//response.setContentType("text/html;charset=" + config.charset); 
		    //response.getWriter().write(form);//直接将完整的表单html输出到页面 
		    //response.getWriter().flush(); 
		    //response.getWriter().close();
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// 把订单写入数据库
		Order order = new Order();
		order.setOrderNo(out_trade_no);
		order.setCustomer(customer);
		order.setPayType(1);
		//order.setOpenId(out_trade_no);
		order.setMealId(Integer.parseInt(mealId));
		order.setMoney(price);
		order.setStatus(0);
		if(null != request.getSession().getAttribute("temp_user_name")){
			order.setTempUserName(request.getSession().getAttribute("temp_user_name").toString());
		}
		order.setCreateTime(DateTimeUtil.nowTimestamp());
		orderService.insertOrder(order);
		return form;
	}

	/**
	 * 查询订单状态（待完善参数设置）
	 * 注意：商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws AlipayApiException
	 */
	public JSONObject queryOrder(HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
		
		if(request.getParameter("WIDout_trade_no")!=null||request.getParameter("WIDtrade_no")!=null){
			 //商户订单号，商户网站订单系统中唯一订单号，必填
			 String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			 //支付宝交易号
			 String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
			 /**********************/
			 // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
			 AlipayClient client = new DefaultAlipayClient(config.url, config.appid, config.privateKey, config.format, config.charset, config.publicKey,config.signType);
			 AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
			 
			 AlipayTradeQueryModel model=new AlipayTradeQueryModel();
		     model.setOutTradeNo(out_trade_no);
		     model.setTradeNo(trade_no);
		     alipay_request.setBizModel(model);
		     
		     AlipayTradeQueryResponse alipay_response =client.execute(alipay_request);
		     System.out.println(alipay_response.getBody());
		     return ResultMsgUtils.success(alipay_response.getBody());
		 }
		return ResultMsgUtils.error("操作失败");
	}

	/**
	 * 异步回调处理
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws AlipayApiException
	 */
	public boolean notify(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, AlipayApiException{
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号

		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		//计算得出通知验证结果
		boolean verify_result = AlipaySignature.rsaCheckV1(params, config.publicKey, config.charset, config.signType);
		
		/*if(verify_result){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码

			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			
			if(trade_status.equals("TRADE_FINISHED")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					//如果有做过处理，不执行商户的业务程序
					
				//注意：
				//如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				//如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
			} else if (trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					//如果有做过处理，不执行商户的业务程序
					
				//注意：
				//如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
			}
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			//out.clear();
			//out.println("success");	//请不要修改或删除
			return true;
			//////////////////////////////////////////////////////////////////////////////////////////
		}else{//验证失败
			//out.println("fail");
			return false;
		}*/
		/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      开始************************/
		Order order = new Order();
		if(verify_result){// 支付成功
			// 更新订单
			order.setOrderNo(out_trade_no);
			order.setOpenId(trade_no);
			order.setStatus(1);
			order.setUpdateTime(DateTimeUtil.nowTimestamp());
			orderService.updateOrder(order);
			
			Order o = orderService.selectByOrderNo(out_trade_no);
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
			order.setOrderNo(out_trade_no);
			order.setOpenId(trade_no);
			order.setStatus(2);
			order.setUpdateTime(DateTimeUtil.nowTimestamp());
			orderService.updateOrder(order);
		}
		/********写自己的业务逻辑，更新订单状态和新建（更新）上网账号      结束************************/
		
		return verify_result;
	}

	/**
	 * 同步回调
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws AlipayApiException
	 */
	public boolean payback(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, AlipayApiException{
		//获取支付宝GET过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号

		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		//计算得出通知验证结果
		boolean verify_result = AlipaySignature.rsaCheckV1(params, config.publicKey, config.charset, config.signType);
		
		
		/*if(verify_result){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码
			//该页面可做页面美工编辑
			//out.clear();
			//out.println("验证成功<br />");
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			return true;
			//////////////////////////////////////////////////////////////////////////////////////////
		}else{
			//该页面可做页面美工编辑
			//out.clear();
			//out.println("验证失败");
			return false;
		}*/
		
		return verify_result;
	}
}
