package com.ruoyi.radius.toughradius.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.utils.wx.HttpClient;
import com.ruoyi.radius.toughradius.utils.wx.WeChatPropertyConfig;
import com.ruoyi.radius.toughradius.utils.wx.WeChatUtils;
import com.ruoyi.radius.toughradius.utils.wx.XMLUtils;

import com.alibaba.fastjson.JSONObject;


/**
 * 微信支付业务处理类
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class WXPayService {
	
	@Autowired
	protected Memarylogger logger;

	@Autowired
	private WeChatPropertyConfig config;
	@Autowired
	private HttpClient httpRequest;
	@Autowired
	private XMLUtils XMLUtils;
	@Autowired
	private WeChatUtils weChatUtils;
	@Autowired
	private IOrderService orderService;

	/**
	 * 订单查询
	 * @param reqData
	 * @return
	 */
	public boolean orderQuery(Map<String, Object> reqData) {
		String xmlBody = weChatUtils.packageOrderQueryXml(reqData);
		
		//logger.info("订单查询  ---->xmlBody:" + xmlBody,Memarylogger.WXPAY);
		//查询订单
		CloseableHttpResponse response = httpRequest.doPost(config.getOrderquery(), xmlBody);
		int status = response.getStatusLine().getStatusCode();
		//logger.info("订单查询  ---->status:" + status,Memarylogger.WXPAY);
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
	            String return_code = String.valueOf(map.get("return_code"));
	            String result_code = String.valueOf(map.get("result_code"));
	            //String trade_state = String.valueOf(map.get("trade_state"));
	            //logger.info("订单查询  ---->map:" + JSONObject.toJSONString(map),Memarylogger.WXPAY);
				if(WeChatUtils.SUCCESS.equals(return_code) && WeChatUtils.SUCCESS.equals(result_code)) {//  && WeChatUtils.SUCCESS.equals(trade_state)
	            		//此处添加支付成功后，支付金额和实际订单金额是否等价，防止钓鱼
	                    /*if (map.get("openid") != null && map.get("trade_type") !=null) {
	                        String total_fee = String.valueOf(map.get("total_fee"));
	                        String order_total_fee = String.valueOf(map.get("total_fee"));
	                        if (Integer.parseInt(order_total_fee) >= Integer.parseInt(total_fee)) {
	                            return true;
	                        }
	                    }*/
	                    return true;
	            }
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),Memarylogger.WXPAY);
		}
		
		return false;
	}

	/**
	 * 查询订单状态
	 * @param request
	 * @return
	 */
	public boolean queryOrderStatus(HttpServletRequest request) {
		
		if(null != request.getSession().getAttribute("out_trade_no")){
			
			String out_trade_no = request.getSession().getAttribute("out_trade_no").toString();
			// 从数据库中查询订单信息
			Order order = orderService.selectByOrderNo(out_trade_no);
			
			if(order.getStatus() == 1) return true;
			
		}
		return false;
	}

	/**
	 * 微信H5支付
	 * @param request
	 * @param bill_create_ip
	 * @return
	 */
	public String wxH5Pay(HttpServletRequest request,String bill_create_ip) {
		logger.info("进入微信H5支付方法",Memarylogger.WXPAY);
		String mweb_url = null;
		try {
			
			// 将订单参数封装之后给微信
			String prePayXml = weChatUtils.packageParam(request,bill_create_ip);
			
			CloseableHttpResponse response = httpRequest.doPost(config.getUnifiedorder(), prePayXml);
			int status = response.getStatusLine().getStatusCode();
			if(status == HttpClient.SUCCESS) {
				//开始解析这个返回结果，取到需要的东西
				String httpResult = httpRequest.getRequestResult(response);
				 // 过滤
				//httpResult = httpResult.replaceAll("<!\\[CDATA\\[|]]>", "");
	            Map<String, Object> map = XMLUtils.doXMLParse(httpResult);
	            Map<String, Object> ordermap = XMLUtils.doXMLParse(prePayXml);
	            
	            String return_code = String.valueOf(map.get("return_code"));
	            if(WeChatUtils.SUCCESS.equals(return_code)) {
	            	//解析mweb_url
	            	mweb_url = String.valueOf(map.get("mweb_url"));
	            	//拼接来源地址绑定
	            	mweb_url += "&redirect_url=" + config.getRedirectUrl() + "?out_trade_no=" + String.valueOf(ordermap.get("out_trade_no"));
	            	
	            	Order order = new Order();
	            	order.setOpenId(mweb_url);
	            	order.setOrderNo(String.valueOf(ordermap.get("out_trade_no")));
	            	
	            	orderService.updateOrder(order);
	            }else {
	            	logger.error(String.valueOf(map.get("return_msg")),Memarylogger.WXPAY);
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),Memarylogger.WXPAY);
		}
		return mweb_url;
	}

	/**
	 * 扫码支付下单
	 * @param request
	 * @param bill_create_ip
	 * @return
	 */
	public String nativePay(HttpServletRequest request,String bill_create_ip) {
		logger.info("进入nativePay方法",Memarylogger.WXPAY);
		String code_url = null;
		try {

			// 将订单参数封装之后给微信
			String prePayXml = weChatUtils.packageNativeParam(request,bill_create_ip);
			
			CloseableHttpResponse response = httpRequest.doPost(config.getUnifiedorder(), prePayXml);
			int status = response.getStatusLine().getStatusCode();
			if(status == HttpClient.SUCCESS) {
				//开始解析这个返回结果，取到需要的东西
				String httpResult = httpRequest.getRequestResult(response);
				// 过滤
				//httpResult = httpResult.replaceAll("<!\\[CDATA\\[|]]>", "");
				Map<String, Object> ordermap = XMLUtils.doXMLParse(prePayXml);
				logger.info("传给微信的订单信息：" + JSONObject.toJSONString(ordermap),Memarylogger.WXPAY);
				Map<String, Object> map = XMLUtils.doXMLParse(httpResult);
				logger.info("微信返回的订单信息：" + JSONObject.toJSONString(map),Memarylogger.WXPAY);
				String return_code = String.valueOf(map.get("return_code"));
				if(WeChatUtils.SUCCESS.equals(return_code)) {
					//解析code_url
					code_url = String.valueOf(map.get("code_url"));
					//微信响应成功后 把订单号及验证码存入缓存中 供微信支付回调使用
					
					// ???????????????????????????s
					
					//CacheUtils.put(Constant.ORDER_CACHE,String.valueOf(ordermap.get("out_trade_no")), String.valueOf(ordermap.get("body")));
					logger.info(code_url,Memarylogger.WXPAY);
				}else {
					logger.error(String.valueOf(map.get("return_msg")),Memarylogger.WXPAY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),Memarylogger.WXPAY);
		}
		logger.info("退出nativePay方法",Memarylogger.WXPAY);
		return code_url;
	}
	
}
