package com.ruoyi.radius.toughradius.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.radius.toughradius.common.ValidateUtil;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.toughradius.mapper.BrasMapper;
import com.ruoyi.radius.toughradius.domain.Bras;
import com.ruoyi.radius.toughradius.service.IBrasService;
import com.ruoyi.common.core.text.Convert;

/**
 * 设备管理Service业务层处理
 * 
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class BrasServiceImpl implements IBrasService 
{
    @Autowired
    private BrasMapper brasMapper;
    @Autowired
    private Memarylogger logger;
    /**
     * 查询设备管理
     * 
     * @param id 设备管理ID
     * @return 设备管理
     */
    @Override
    public Bras selectBrasById(Long id)
    {
        return brasMapper.selectBrasById(id);
    }

    /**
     * 查询设备管理列表
     * 
     * @param bras 设备管理
     * @return 设备管理
     */
    @Override
    public List<Bras> selectBrasList(Bras bras)
    {
        return brasMapper.selectBrasList(bras);
    }

    /**
     * 新增设备管理
     * 
     * @param bras 设备管理
     * @return 结果
     */
    @Override
    public int insertBras(Bras bras)
    {
        bras.setCreateTime(DateUtils.getNowDate());
        return brasMapper.insertBras(bras);
    }

    /**
     * 修改设备管理
     * 
     * @param bras 设备管理
     * @return 结果
     */
    @Override
    public int updateBras(Bras bras)
    {
        return brasMapper.updateBras(bras);
    }

    /**
     * 删除设备管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBrasByIds(String ids)
    {
        return brasMapper.deleteBrasByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除设备管理信息
     * 
     * @param id 设备管理ID
     * @return 结果
     */
    @Override
    public int deleteBrasById(Long id)
    {
        return brasMapper.deleteBrasById(id);
    }

    /**
     * 查找 BRAS 信息
     *
     * @param ipaddr     数据包来源IP
     * @param srcip      设备中配置的IP，可能是内网IP
     * @param identifier 设备唯一标识
     * @return
     * @throws ServiceException
     */
    @Override
    public Bras findBras(String ipaddr, String srcip, String identifier) throws ServiceException {
        Bras tcBras = null;
        if(ValidateUtil.isNotEmpty(ipaddr)&&!"0.0.0.0".equals(ipaddr)){
            tcBras = brasMapper.findByIPAddr(ipaddr);
        }

        if(tcBras == null && ValidateUtil.isNotEmpty(srcip)&&!"0.0.0.0".equals(srcip)){
            tcBras = brasMapper.findByIPAddr(srcip);
        }

        if (tcBras == null && ValidateUtil.isNotEmpty(identifier)) {
            tcBras = brasMapper.findByidentifier(identifier);
        }

        if (tcBras == null) {
            String message = String.format("Bras设备 id=%s, ip=%s 不存在", identifier, ipaddr);
            logger.error(message, Memarylogger.RADIUSD);
            throw new ServiceException(message);
        }

        if (tcBras.getStatus() != null && "disabled".equals(tcBras.getStatus())) {
            String message = String.format("Bras设备 id=%s, ip=%s 已停用", identifier, ipaddr);
            logger.error(message, Memarylogger.RADIUSD);
            throw new ServiceException(message);
        }
        // 设备IP 取实时接收到的设备IP
        if(ValidateUtil.isNotEmpty(ipaddr) && !"0.0.0.0".equals(ipaddr)){
            tcBras.setIpaddr(ipaddr);
        }
        return tcBras;
    }

    /**
     * 校验唯一标识
     *
     * @param bras
     * @return
     */
    @Override
    public String checkIdentifierUnique(Bras bras) {
        Long id = StringUtils.isNull(bras.getId()) ? -1L : bras.getId();
        Bras rbras = brasMapper.findByidentifier(bras.getIdentifier());
        if(StringUtils.isNotNull(rbras) && rbras.getId().longValue() != id.longValue())
            return "1";
        else
            return "0";
    }
}
