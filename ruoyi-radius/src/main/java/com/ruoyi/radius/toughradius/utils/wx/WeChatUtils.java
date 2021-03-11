package com.ruoyi.radius.toughradius.utils.wx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.ruoyi.radius.toughradius.service.IMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.radius.toughradius.common.CoderUtil;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.service.IOrderService;
import com.ruoyi.radius.toughradius.domain.Order;


/**
 * 微信支付工具类
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
public class WeChatUtils {
	
	//支付状态
	public static final String SUCCESS= "SUCCESS";
	public static final String FAIL= "FAIL";
	
	@Autowired
	private WeChatPropertyConfig weChatPropertyConfig;
	@Autowired
	private XMLUtils XMLUtils;
	@Autowired
	private IMealService mealService;
	@Autowired
	private IOrderService orderService;


	/**
	 * 封装订单查询XML字符串
	 * @param map
	 * @return
	 */
	public String packageOrderQueryXml(Map<String, Object> map) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("appid", String.valueOf(map.get("appid")));
		treeMap.put("mch_id", String.valueOf(map.get("mch_id")));
		if(null != map.get("transaction_id")) treeMap.put("transaction_id", String.valueOf(map.get("transaction_id")));
		//treeMap.put("nonce_str", UUIDUtils.createUUID());
		if(null != map.get("nonce_str")) treeMap.put("nonce_str", String.valueOf(map.get("nonce_str")));
		if(null != map.get("out_trade_no")) treeMap.put("out_trade_no", String.valueOf(map.get("out_trade_no")));
		
		String sign = "";
		try {
			sign = WXPayUtil.generateSignature(treeMap, weChatPropertyConfig.getMerchantKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		treeMap.put("sign", sign);
		String xmlStr = "";
		try {
			xmlStr = WXPayUtil.mapToXml(treeMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlStr;
	}
	
	/**
	 * 封装需要提交给微信的参数（H5支付）<br>
	 * 此处参数再实际使用中最好传入一个订单类，根据订单参数封装提交给微信的数据
	 * @param bill_create_ip
	 * @return
	 */
	public String packageParam(HttpServletRequest request,String bill_create_ip) {
		String prePayXml = null;
		
		try {
			//订单id
			String out_trade_no  = String.valueOf(CoderUtil.randomLong15Id());
			//随机数
			String nonce_str = UUIDUtils.createUUID();
			String customer = new String(request.getParameter("customer").getBytes("ISO-8859-1"),"UTF-8");
			String mealId = new String(request.getParameter("mealId").getBytes("ISO-8859-1"),"UTF-8");
			int price = (mealService.selectMealById(Integer.parseInt(mealId)).getPrice());
			
			//获取配置信息，appId，商户id，商户名称，商户key，回调地址
			String appid = weChatPropertyConfig.getAppid();
			String merchantId = weChatPropertyConfig.getMerchantId();
			String merchantKey = weChatPropertyConfig.getMerchantKey();
			//String merchantName = weChatPropertyConfig.getMerchantName();
			String notifyUrl = weChatPropertyConfig.getNotifyUrl();
			//Double amount = Double.parseDouble(weChatPropertyConfig.getAmount());
			
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put("appid", appid);
			treeMap.put("mch_id", merchantId);// 设置商户号
			treeMap.put("nonce_str", nonce_str);//随机数
			//treeMap.put("body", URLEncoder.encode("测试支付", "UTF-8"));//商品描述  
			treeMap.put("body", "WIFI上网充值,手机号码为" + customer);//商品描述  
			treeMap.put("out_trade_no", out_trade_no);//商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一,详细说明
			treeMap.put("total_fee", String.valueOf(price));// 商品总金额,以分为单位，古放大100倍向下取整、
			//treeMap.put("total_fee", String.valueOf(1));
			treeMap.put("spbill_create_ip", bill_create_ip);
			treeMap.put("notify_url", notifyUrl);//通知回调地址
			treeMap.put("trade_type", "MWEB");//H5支付
			String sceneinfo = "{\"h5_info\":{\"type\":\"Wap\",\"wap_url\":\"http://test4.csservice.cn\",\"wap_name\":\"dangfeijiaona\"}}";
			treeMap.put("scene_info",sceneinfo);
			//treeMap.put("openid", null);
			//treeMap.put("attach", URLEncoder.encode("测试微信支付", "UTF-8"));
			treeMap.put("attach", "WIFI");
			String sign = WXPayUtil.generateSignature(treeMap, merchantKey);
			treeMap.put("sign", sign);
			
			
			// 把订单写入数据库
			Order order = new Order();
			order.setOrderNo(out_trade_no);
			order.setCustomer(customer);
			order.setPayType(2);
			//order.setOpenId(nonce_str);
			order.setMealId(Integer.parseInt(mealId));
			order.setMoney(price);
			order.setStatus(0);
			if(null != request.getSession().getAttribute("temp_user_name")){
				order.setTempUserName(request.getSession().getAttribute("temp_user_name").toString());
			}
			order.setCreateTime(DateTimeUtil.nowTimestamp());
			orderService.insertOrder(order);
			
			return WXPayUtil.mapToXml(treeMap);
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return prePayXml;
	}

	/**
	 * 封装需要提交给微信的参数(扫码支付)
	 * @param request
	 * @param bill_create_ip
	 * @return
	 */
	public String packageNativeParam(HttpServletRequest request,String bill_create_ip) {
		String prePayXml = null;
		
		try {
			//订单id
			String out_trade_no  = UUIDUtils.createUUID();
			//获取配置信息，appId，商户id，商户名称，商户key，回调地址
			String appid = weChatPropertyConfig.getAppid();
			String merchantId = weChatPropertyConfig.getMerchantId();
			String merchantKey = weChatPropertyConfig.getMerchantKey();
			//String merchantName = weChatPropertyConfig.getMerchantName();
			String notifyUrl = weChatPropertyConfig.getNotifyUrl();
			Double amount = Double.parseDouble(weChatPropertyConfig.getAmount());
			
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put("appid", appid);
			treeMap.put("mch_id", merchantId);// 设置商户号
			treeMap.put("nonce_str", UUIDUtils.createUUID());//随机数
			//treeMap.put("body", URLEncoder.encode("测试支付", "UTF-8"));//商品描述  
			//String body = CheckCodeUtil.getCheckCode(Constant.CHECKCODE_CACHE);
			treeMap.put("body", "测试支付");//商品描述  
			treeMap.put("out_trade_no", out_trade_no);//商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一,详细说明
			treeMap.put("total_fee", String.valueOf((int)Math.floor(amount*100)));// 商品总金额,以分为单位，古放大100倍向下取整、
			//treeMap.put("total_fee", String.valueOf(1));
			treeMap.put("spbill_create_ip", bill_create_ip);
			treeMap.put("notify_url", notifyUrl);//通知回调地址
			treeMap.put("trade_type", "NATIVE");//H5支付
			//String sceneinfo = "{\"h5_info\":{\"type\":\"Wap\",\"wap_url\":\"http://test4.csservice.cn\",\"wap_name\":\"dangfeijiaona\"}}";
			//treeMap.put("scene_info",sceneinfo);
			//treeMap.put("openid", null);
			//treeMap.put("attach", URLEncoder.encode("测试微信支付", "UTF-8"));
			//treeMap.put("attach", "WIFI");
			request.getSession().setAttribute("code", treeMap.get("body"));
			String sign = WXPayUtil.generateSignature(treeMap, merchantKey);
			treeMap.put("sign", sign);
			return WXPayUtil.mapToXml(treeMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prePayXml;
	}

	/**
	 * 获取微信回调数据
	 * @param request
	 * @return
	 */
	public Map<String, Object> getWeChatReplay(HttpServletRequest request){
		Map<String, Object> map = null;
		try {
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			inStream.close();
			String result = new String(outSteam.toByteArray(), "utf-8");
			map = XMLUtils.doXMLParse(result);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
