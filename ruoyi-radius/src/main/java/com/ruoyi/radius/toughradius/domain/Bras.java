package com.ruoyi.radius.toughradius.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备管理对象 tr_bras
 * 
 * @author panweilei
 * @date 2021-01-25
 */
public class Bras extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 标识 */
    @Excel(name = "标识")
    private String identifier;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ipaddr;

    /** 设备厂商 */
    @Excel(name = "设备厂商")
    private String vendorId;

    /** portal协议 */
    @Excel(name = "portal协议")
    private String portalVendor;

    /** 共享密钥 */
    @Excel(name = "共享密钥")
    private String secret;

    /** COA端口 */
    @Excel(name = "COA端口")
    private Integer coaPort;

    /** AC端口 */
    @Excel(name = "AC端口")
    private Integer acPort;

    /** 认证并发 */
    @Excel(name = "认证并发")
    private Integer authLimit;

    /** 记账并发 */
    @Excel(name = "记账并发")
    private Integer acctLimit;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setIdentifier(String identifier) 
    {
        this.identifier = identifier;
    }

    public String getIdentifier() 
    {
        return identifier;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setIpaddr(String ipaddr) 
    {
        this.ipaddr = ipaddr;
    }

    public String getIpaddr() 
    {
        return ipaddr;
    }
    public void setVendorId(String vendorId) 
    {
        this.vendorId = vendorId;
    }

    public String getVendorId() 
    {
        return vendorId;
    }
    public void setPortalVendor(String portalVendor) 
    {
        this.portalVendor = portalVendor;
    }

    public String getPortalVendor() 
    {
        return portalVendor;
    }
    public void setSecret(String secret) 
    {
        this.secret = secret;
    }

    public String getSecret() 
    {
        return secret;
    }
    public void setCoaPort(Integer coaPort)
    {
        this.coaPort = coaPort;
    }

    public Integer getCoaPort()
    {
        return coaPort;
    }
    public void setAcPort(Integer acPort)
    {
        this.acPort = acPort;
    }

    public Integer getAcPort()
    {
        return acPort;
    }
    public void setAuthLimit(Integer authLimit)
    {
        this.authLimit = authLimit;
    }

    public Integer getAuthLimit()
    {
        return authLimit;
    }
    public void setAcctLimit(Integer acctLimit)
    {
        this.acctLimit = acctLimit;
    }

    public Integer getAcctLimit()
    {
        return acctLimit;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("identifier", getIdentifier())
            .append("name", getName())
            .append("ipaddr", getIpaddr())
            .append("vendorId", getVendorId())
            .append("portalVendor", getPortalVendor())
            .append("secret", getSecret())
            .append("coaPort", getCoaPort())
            .append("acPort", getAcPort())
            .append("authLimit", getAuthLimit())
            .append("acctLimit", getAcctLimit())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .toString();
    }
}
