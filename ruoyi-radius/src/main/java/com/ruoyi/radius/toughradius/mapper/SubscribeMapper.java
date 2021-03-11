package com.ruoyi.radius.toughradius.mapper;

import com.ruoyi.radius.toughradius.domain.Subscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import com.ruoyi.radius.toughradius.form.SubscribeQuery;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public interface SubscribeMapper {
	
	Subscribe findSubscribe(@Param(value = "username") String username);

	List<Subscribe> findLastUpdateUser(@Param(value = "lastUpdate") String lastUpdate);

	List<Subscribe> findByMacAddr(@Param(value = "macAddr") String macAddr,@Param(value = "lastTime") Date lastTime);

	 Integer updateMacAddr(@Param(value = "username") String username, @Param(value = "macAddr") String macAddr);

	 Integer updateInValn(@Param(value = "username") String username, @Param(value = "inValn") Integer inValn);

	 Integer updateOutValn(@Param(value = "username") String username, @Param(value = "outValn") Integer outValn);

    void release(String ids);

	/**
	 * 查询用户管理
	 *
	 * @param id 用户管理ID
	 * @return 用户管理
	 */
	public Subscribe selectSubscribeById(Long id);

	/**
	 * 查询用户管理列表
	 *
	 * @param subscribe 用户管理
	 * @return 用户管理集合
	 */
	public List<Subscribe> selectSubscribeList(Subscribe subscribe);

	/**
	 * 新增用户管理
	 *
	 * @param subscribe 用户管理
	 * @return 结果
	 */
	public int insertSubscribe(Subscribe subscribe);

	/**
	 * 修改用户管理
	 *
	 * @param subscribe 用户管理
	 * @return 结果
	 */
	public int updateSubscribe(Subscribe subscribe);

	/**
	 * 删除用户管理
	 *
	 * @param id 用户管理ID
	 * @return 结果
	 */
	public int deleteSubscribeById(Long id);

	/**
	 * 批量删除用户管理
	 *
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteSubscribeByIds(String[] ids);
}
