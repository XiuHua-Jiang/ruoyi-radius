/**  
*  
*   
* @author pwl  
* @date 2020年11月26日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.thread;

import java.util.Observable;
import java.util.Observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.ruoyi.radius.toughradius.service.Memarylogger;


/**
 * 监听订单更新线程，如果挂起自动重启
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
@Order(100)
public class OrderUpdateThreadListener implements Observer,ApplicationRunner{
	@Autowired
	private Memarylogger logger;
	@Autowired
	private OrderUpdateThread orderUpdateThread;
	 /* (non-Javadoc)
	 * 
	 * @param o
	 * @param arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		logger.info("订单更新线程监听   重启开始", Memarylogger.SYSTEM);
		orderUpdateThread.addObserver(this);
		new Thread(orderUpdateThread).start();
		logger.info("订单更新线程监听  重启完成", Memarylogger.SYSTEM);
	}

	 /* (non-Javadoc)
	 * 
	 * @param args
	 * @throws Exception
	 * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("订单更新线程监听  初始化 开始", Memarylogger.SYSTEM);
		orderUpdateThread.addObserver(this);
		new Thread(orderUpdateThread).start();
		logger.info("订单更新线程监听 初始化 完成", Memarylogger.SYSTEM);
	}

}
