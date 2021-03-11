/**  
*  
*   
* @author pwl  
* @date 2020年11月18日  
* @version 1.0  
*/
package com.ruoyi.radius.toughradius.form;

import java.sql.Timestamp;

import com.ruoyi.radius.toughradius.domain.Order;


/**
 * 订单查询条件
 *
 * @author panweilei
 * @date 2021-01-25
 */
public class OrderForm extends Order {
	
	 private String keyword;
	    
	    private Timestamp startDate;
	    
	    private Timestamp endDate;

	    public String getKeyword() {
	        return keyword;
	    }

	    public void setKeyword(String keyword) {
	        this.keyword = keyword;
	    }

		public Timestamp getStartDate() {
			return startDate;
		}

		public void setStartDate(Timestamp startDate) {
			this.startDate = startDate;
		}

		public Timestamp getEndDate() {
			return endDate;
		}

		public void setEndDate(Timestamp endDate) {
			this.endDate = endDate;
		}

		
}
