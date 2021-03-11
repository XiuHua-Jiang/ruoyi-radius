/**  
*  
*   
* @author pwl  
* @date 2020年11月20日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.common.RestResult;
import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.service.IMealService;
import com.ruoyi.radius.toughradius.service.ISubscribeService;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 用户充值
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
public class UserPayController {
	
	@Autowired
	private IMealService mealService;

	@Autowired
	protected Memarylogger logger;

	@Autowired
	protected ISubscribeService subscribeService;
	@Autowired
	private IOrderService orderService;
	/**
	 * 用户支付页面
	 *
	 * @return
	 */
	@RequestMapping("/payment")
	public String paymentHtml(HttpServletRequest request) {
		try {
			String userName = null;
			if(null != request.getParameter("temp_user_name")){
				userName = new String(request.getParameter("temp_user_name").getBytes("ISO-8859-1"),"UTF-8");
				request.getSession().setAttribute("temp_user_name", userName);
			}else if(null != request.getParameter("login_user_name")){
				userName = new String(request.getParameter("login_user_name").getBytes("ISO-8859-1"),"UTF-8");
			}
			request.getSession().setAttribute("login_user_name", userName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "payment.html";
	}
	/**
	 * 微信支付回调
	 *
	 * @return
	 */
	@RequestMapping("/wifiweb/wxpayRedirectUrl")
	public String wxpayRedirectUrlHtml(HttpServletRequest request) {
		try {
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			logger.info("wxpayRedirectUrl 参数：out_trade_no=" + out_trade_no,Memarylogger.WXPAY);
			request.getSession().setAttribute("out_trade_no", out_trade_no);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "redirect:/wifiweb/wxpayRedirectUrl";
		}
		return "wxpay_redirect_url.html";
	}
	/**
	 * 微信重新支付
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/wifiweb/wxpay/fullback")
	public String fullbackHtml(HttpServletRequest request) {
		if(null != request.getSession().getAttribute("out_trade_no")){
			String orderNo = request.getSession().getAttribute("out_trade_no").toString();
			Order order = orderService.selectByOrderNo(orderNo);
			logger.info("fullback 参数：mweb_url=" + order.getOpenId(),Memarylogger.WXPAY);
			if(null != order && order.getOpenId().indexOf("http") > -1) return "redirect:" + order.getOpenId();
		}
		return "/wifiweb/wxpayRedirectUrl";
	}
	/**
	 * 获取套餐列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/wifiweb/getMealList")
    @ResponseBody
    public RestResult getMealList(HttpServletRequest request){
		
        RestResult result = new RestResult(0,"ok");
        List<Meal> list = mealService.selectMealList(null);
        result.setData(list);
		if(null != request.getSession().getAttribute("login_user_name")){
			String userName = request.getSession().getAttribute("login_user_name").toString();
			Subscribe subscribe = subscribeService.findSubscribe(userName);
			result.setMsg(DateTimeUtil.toDateTimeString(subscribe.getExpireTime().getTime()));
		}
        return  result;
    }
}
