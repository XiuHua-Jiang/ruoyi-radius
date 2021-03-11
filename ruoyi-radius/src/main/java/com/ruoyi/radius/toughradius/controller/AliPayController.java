/**  
* <p>Title: AliPayController.java</p>  
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.szcenterstar.com</p>  
* @author pwl  
* @date 2019年10月28日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.ruoyi.radius.toughradius.service.AliPayService;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.utils.ResultMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 支付宝支付Controller
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
@RequestMapping("/wifiweb/alipay")
public class AliPayController {
	
	@Autowired
	private AliPayService service;
	@Autowired
	protected Memarylogger logger;

	/**
	 * H5网页支付
	 * @param request
	 * @return
	 */
	@RequestMapping("/h5")
	@ResponseBody
	public String h5pay(HttpServletRequest request) {
		logger.info("进入方法：H5支付",Memarylogger.ALIPAY);
		try {
			return service.h5pay(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询订单状态
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryOrder")
	@ResponseBody
	public JSONObject queryOrder(HttpServletRequest request) {
		logger.info("进入方法：查询订单支付结果queryOrder",Memarylogger.ALIPAY);
		try {
			return service.queryOrder(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("查询订单状态失败, UnsupportedEncodingException :: ",e, Memarylogger.ALIPAY);
		} catch (AlipayApiException e) {
			e.printStackTrace();
			logger.error("查询订单状态失败, AlipayApiException :: ",e, Memarylogger.ALIPAY);
		}
		return ResultMsgUtils.error("操作失败");
	}

	/**
	 * 同步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/payback")
	public String payback(HttpServletRequest request,HttpServletResponse response) {
		logger.info("进入方法：同步回调payback",Memarylogger.ALIPAY);
		try {
			boolean result = service.payback(request,response);
			if(result){//支付成功 
				return "redirect:http://www.baidu.com";
			}
		} catch (UnsupportedEncodingException | AlipayApiException e) {
			e.printStackTrace();
			logger.error("同步回调失败, Exception :: ",e, Memarylogger.ALIPAY);
			return "redirect:/payment";
		} 
		//支付失败 跳转到验证页面
		return "redirect:/payment";
	}

	/**
	 * 异步回调
	 * @param request
	 * @param response
	 */
	@RequestMapping("/notify")
	public void notify(HttpServletRequest request,HttpServletResponse response) {
		logger.info("进入方法：异步回调notify",Memarylogger.ALIPAY);
		try {
			boolean result = service.notify(request,response);
			String msg = "";
			if(result){//验证成功
				msg = "success";
			}else{//验证失败
				msg = "fail";
			}
			response.setContentType("text/html");
            response.getWriter().write(msg);
            response.flushBuffer();
		} catch (UnsupportedEncodingException | AlipayApiException e) {
			e.printStackTrace();
			logger.error("异步回调失败, Exception :: ",e, Memarylogger.ALIPAY);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("异步回调失败, IOException :: ",e, Memarylogger.ALIPAY);
		}
	}
}
