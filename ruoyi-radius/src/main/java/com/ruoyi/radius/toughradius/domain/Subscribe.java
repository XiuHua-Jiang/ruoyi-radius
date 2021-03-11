package com.ruoyi.radius.toughradius.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户管理对象 tr_subscribe
 *
 * @author panweilei
 * @date 2021-01-25
 */
public class Subscribe extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 节点 */
    @Excel(name = "节点")
    private Long nodeId;

    /** 账号 */
    @Excel(name = "账号")
    private String subscriber;

    /** 姓名 */
    @Excel(name = "姓名")
    private String realname;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 认证域 */
    @Excel(name = "认证域")
    private String domain;

    /** 地址池 */
    @Excel(name = "地址池")
    private String addrPool;

    /** 自定义策略 */
    @Excel(name = "自定义策略")
    private String policy;

    /** 是否在线 */
    @Excel(name = "是否在线")
    private Integer isOnline;

    /** 最大在线 */
    @Excel(name = "最大在线")
    private Integer activeNum;

    /** 绑定MAC */
    @Excel(name = "绑定MAC")
    private Integer bindMac;

    /** 绑定VLAN */
    @Excel(name = "绑定VLAN")
    private Integer bindVlan;

    /** 固定IP地址 */
    @Excel(name = "固定IP地址")
    private String ipAddr;

    /** MAc地址 */
    @Excel(name = "MAc地址")
    private String macAddr;

    /** 内层VLAN */
    @Excel(name = "内层VLAN")
    private Integer inVlan;

    /** 外层VLAN */
    @Excel(name = "外层VLAN")
    private Integer outVlan;

    /** 上行速率(Mbps) */
    @Excel(name = "上行速率(Mbps)")
    private Long upRate;

    /** 下行速率(Mbps) */
    @Excel(name = "下行速率(Mbps)")
    private Long downRate;

    /** 突发上行速率(Mbps) */
    @Excel(name = "突发上行速率(Mbps)")
    private Long upPeakRate;

    /** 突发下行速率(Mbps) */
    @Excel(name = "突发下行速率(Mbps)")
    private Long downPeakRate;

    /** 上行速率策略 */
    @Excel(name = "上行速率策略")
    private String upRateCode;

    /** 下行速率策略 */
    @Excel(name = "下行速率策略")
    private String downRateCode;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 计费开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计费开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    /** 计费到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计费到期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setNodeId(Long nodeId)
    {
        this.nodeId = nodeId;
    }

    public Long getNodeId()
    {
        return nodeId;
    }
    public void setSubscriber(String subscriber)
    {
        this.subscriber = subscriber;
    }

    public String getSubscriber()
    {
        return subscriber;
    }
    public void setRealname(String realname)
    {
        this.realname = realname;
    }

    public String getRealname()
    {
        return realname;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public String getDomain()
    {
        return domain;
    }
    public void setAddrPool(String addrPool)
    {
        this.addrPool = addrPool;
    }

    public String getAddrPool()
    {
        return addrPool;
    }
    public void setPolicy(String policy)
    {
        this.policy = policy;
    }

    public String getPolicy()
    {
        return policy;
    }
    public void setIsOnline(Integer isOnline)
    {
        this.isOnline = isOnline;
    }

    public Integer getIsOnline()
    {
        return isOnline;
    }
    public void setActiveNum(Integer activeNum)
    {
        this.activeNum = activeNum;
    }

    public Integer getActiveNum()
    {
        return activeNum;
    }
    public void setBindMac(Integer bindMac)
    {
        this.bindMac = bindMac;
    }

    public Integer getBindMac()
    {
        return bindMac;
    }
    public void setBindVlan(Integer bindVlan)
    {
        this.bindVlan = bindVlan;
    }

    public Integer getBindVlan()
    {
        return bindVlan;
    }
    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
    }

    public String getIpAddr()
    {
        return ipAddr;
    }
    public void setMacAddr(String macAddr)
    {
        this.macAddr = macAddr;
    }

    public String getMacAddr()
    {
        return macAddr;
    }
    public void setInVlan(Integer inVlan)
    {
        this.inVlan = inVlan;
    }

    public Integer getInVlan()
    {
        return inVlan;
    }
    public void setOutVlan(Integer outVlan)
    {
        this.outVlan = outVlan;
    }

    public Integer getOutVlan()
    {
        return outVlan;
    }
    public void setUpRate(Long upRate)
    {
        this.upRate = upRate;
    }

    public Long getUpRate()
    {
        return upRate;
    }
    public void setDownRate(Long downRate)
    {
        this.downRate = downRate;
    }

    public Long getDownRate()
    {
        return downRate;
    }
    public void setUpPeakRate(Long upPeakRate)
    {
        this.upPeakRate = upPeakRate;
    }

    public Long getUpPeakRate()
    {
        return upPeakRate;
    }
    public void setDownPeakRate(Long downPeakRate)
    {
        this.downPeakRate = downPeakRate;
    }

    public Long getDownPeakRate()
    {
        return downPeakRate;
    }
    public void setUpRateCode(String upRateCode)
    {
        this.upRateCode = upRateCode;
    }

    public String getUpRateCode()
    {
        return upRateCode;
    }
    public void setDownRateCode(String downRateCode)
    {
        this.downRateCode = downRateCode;
    }

    public String getDownRateCode()
    {
        return downRateCode;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }
    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("nodeId", getNodeId())
                .append("subscriber", getSubscriber())
                .append("realname", getRealname())
                .append("password", getPassword())
                .append("domain", getDomain())
                .append("addrPool", getAddrPool())
                .append("policy", getPolicy())
                .append("isOnline", getIsOnline())
                .append("activeNum", getActiveNum())
                .append("bindMac", getBindMac())
                .append("bindVlan", getBindVlan())
                .append("ipAddr", getIpAddr())
                .append("macAddr", getMacAddr())
                .append("inVlan", getInVlan())
                .append("outVlan", getOutVlan())
                .append("upRate", getUpRate())
                .append("downRate", getDownRate())
                .append("upPeakRate", getUpPeakRate())
                .append("downPeakRate", getDownPeakRate())
                .append("upRateCode", getUpRateCode())
                .append("downRateCode", getDownRateCode())
                .append("status", getStatus())
                .append("remark", getRemark())
                .append("beginTime", getBeginTime())
                .append("expireTime", getExpireTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
