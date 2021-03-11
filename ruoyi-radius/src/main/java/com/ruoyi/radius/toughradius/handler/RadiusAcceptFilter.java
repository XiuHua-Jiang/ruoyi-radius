package com.ruoyi.radius.toughradius.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.radius.tinyradius.packet.AccessAccept;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.common.ValidateUtil;
import com.ruoyi.radius.toughradius.service.ConfigService;
import com.ruoyi.radius.toughradius.domain.Bras;
import com.ruoyi.radius.toughradius.domain.Subscribe;

@Component
public class RadiusAcceptFilter implements RadiusConstant{

    @Autowired
    private ConfigService configService;

    /**
     * Radius 认证成功后下发属性处理
     * @param accept
     * @param nas
     * @param user
     * @return
     */
    public AccessAccept doFilter(AccessAccept accept, Bras nas, Subscribe user){
        accept = filterDefault(accept,user, nas);
        switch (nas.getVendorId()){
            case VENDOR_TOUGHSOCKS:
                return filterToughSocks(accept, user);
            case VENDOR_MIKROTIK:
                return filterMikrotik(accept, user);
            case VENDOR_IKUAI:
                return filterIkuai(accept, user);
            case VENDOR_HUAWEI:
                return filterHuawei(accept, user);
            case VENDOR_H3C:
                return filterH3c(accept, user);
            case VENDOR_ZTE:
                return filterZTE(accept, user);
            case VENDOR_CISCO:
                return filterCisco(accept, user);
            case VENDOR_RADBACK:
                return filterRadback(accept, user);
            default:
                return accept;
        }
    }

    /**
     * 默认属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterDefault(AccessAccept accept, Subscribe user, Bras nas){
        int sessionTimeout  = DateTimeUtil.compareSecond(user.getExpireTime(),new Date());
        long preSessionTimeout = accept.getPreSessionTimeout();
        if(preSessionTimeout>Integer.MAX_VALUE){
            preSessionTimeout = Integer.MAX_VALUE;
        }
        if(preSessionTimeout!=0){
            sessionTimeout = (int)preSessionTimeout;
        }
        int interimTimes = accept.getPreInterim();
        Integer dbInterimTimes = configService.getInterimTimes();
        if(dbInterimTimes!=null){
            interimTimes = dbInterimTimes;
        }

        if(ValidateUtil.isNotEmpty(user.getAddrPool())){
            accept.addAttribute("Framed-Pool",user.getAddrPool());
        }

        String ipaddr = user.getIpAddr();
        if(ValidateUtil.isNotEmpty(ipaddr) && ValidateUtil.isIP(ipaddr)){
            accept.addAttribute("Framed-IP-Address", ipaddr);
        }

        accept.addAttribute("Session-Timeout", String.valueOf(sessionTimeout));
        accept.addAttribute("Acct-Interim-Interval", String.valueOf(interimTimes));

        return accept;
    }


    /**
     * Mikrotik 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterMikrotik(AccessAccept accept, Subscribe user){
        long up = user.getUpRate() * 1024;
        long down = user.getDownRate() * 1024;
        /**
         * 客户的速率限制。字符串表示，格式为 rx-rate[/tx-rate] [rx-burst-rate[/tx-burst-rate] 
         * [rx-burst-threshold[/tx-burst-threshold] 
         * [rx-burst-time[/tx-burst-time] [priority] 
         * [rx-rate-min[/tx-rate-min]]]] 
         * 其中，[]中内容表示可选。
         * "rx" 表示客户的上传速率，
         * "tx"客户的下载速率。
         * burst-rate突发速率，
         * burst-threshold突发门限，
         * burst-time突发时长。
        */
        if(user.getUpPeakRate() !=null && user.getDownPeakRate() != null){
        	accept.addAttribute("Mikrotik-Rate-Limit", String.format("%sk/%sk %sk/%sk", up,down,user.getUpPeakRate()*1024,user.getDownPeakRate()*1024));
        }else{
        	accept.addAttribute("Mikrotik-Rate-Limit", String.format("%sk/%sk", up,down));
        }
        // 添加登录人数限制
        accept.addAttribute("Acct-Link-Count", String.valueOf(user.getActiveNum()));
        return accept;
    }

    /**
     * Toughsocks 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterToughSocks(AccessAccept accept, Subscribe user){
        long up = user.getUpRate() * 1024;
        long down = user.getDownRate() * 1024;
        accept.addAttribute("ToughProxy-Up-Limit", String.valueOf(up));
        accept.addAttribute("ToughProxy-Down-Limit", String.valueOf(down));
        accept.addAttribute("ToughProxy-Max-Session", String.valueOf(user.getActiveNum()));
        return accept;
    }


    private AccessAccept filterIkuai(AccessAccept accept, Subscribe user){
        long up = user.getUpRate() * 1024 * 8;
        long down = user.getDownRate() * 1024 * 8;
        accept.addAttribute("RP-Upstream-Speed-Limit", String.valueOf(up));
        accept.addAttribute("RP-Downstream-Speed-Limit", String.valueOf(down));
        return accept;
    }

    /**
     * Huawei 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterHuawei(AccessAccept accept, Subscribe user){
        long up = user.getUpRate() * 1024 * 1024;
        long down = user.getDownRate()* 1024 * 1024;
        long peakUp = up * 4;
        long peakDown = down * 4;
        try{
            peakUp = user.getUpPeakRate()* 1024 * 1024;
            peakDown = user.getDownPeakRate()* 1024 * 1024;
        }catch(Exception e){
            e.printStackTrace();
        }
        accept.addAttribute("Huawei-Input-Average-Rate", String.valueOf(up));
        accept.addAttribute("Huawei-Input-Peak-Rate", String.valueOf(peakUp));
        accept.addAttribute("Huawei-Output-Average-Rate", String.valueOf(down));
        accept.addAttribute("Huawei-Output-Peak-Rate", String.valueOf(peakDown));

        String domain = user.getDomain();
        if(ValidateUtil.isNotEmpty(domain)){
            accept.addAttribute("Huawei-Domain-Name", domain);
        }

        return accept;
    }

    /**
     * H3c 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterH3c(AccessAccept accept, Subscribe user){
        long up = user.getUpRate()* 1024 * 1024;
        long down = user.getDownRate()* 1024 * 1024;
        long peakUp = up * 4;
        long peakDown = down * 4;
        try{
            peakUp = user.getUpPeakRate()* 1024 * 1024;
            peakDown = user.getDownPeakRate()* 1024 * 1024;
        }catch(Exception e){
            e.printStackTrace();
        }
        accept.addAttribute("H3C-Input-Average-Rate", String.valueOf(up));
        accept.addAttribute("H3C-Input-Peak-Rate", String.valueOf(peakUp));
        accept.addAttribute("H3C-Output-Average-Rate", String.valueOf(down));
        accept.addAttribute("H3C-Output-Peak-Rate", String.valueOf(peakDown));
        return accept;
    }

    /**
     * ZTE 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterZTE(AccessAccept accept, Subscribe user){
        long up = user.getUpRate()* 1024 * 1024;
        long down = user.getDownRate()* 1024 * 1024;
        accept.addAttribute("ZTE-Rate-Ctrl-Scr-Up", String.valueOf(up));
        accept.addAttribute("ZTE-Rate-Ctrl-Scr-Down", String.valueOf(down));
        return accept;
    }

    /**
     * Cisco 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterCisco(AccessAccept accept, Subscribe user){
        accept.addAttribute("Cisco-AVPair", String.format("sub-qos-policy-in=%s",user.getUpRateCode()));
        accept.addAttribute("Cisco-AVPair", String.format("sub-qos-policy-out=%s",user.getDownRateCode()));
        if(ValidateUtil.isNotEmpty(user.getAddrPool())){
            accept.addAttribute("Cisco-AVPair", String.format("addr-pool=%s",user.getAddrPool()));
        }
        return accept;
    }

    /**
     * radback 属性下发
     * @param accept
     * @param user
     * @return
     */
    private AccessAccept filterRadback(AccessAccept accept, Subscribe user){
        String policy = user.getPolicy();
        if(ValidateUtil.isNotEmpty(policy))
            accept.addAttribute("Sub-Profile-Name", policy);

        String domain = user.getDomain();
        if(ValidateUtil.isNotEmpty(domain)){
            accept.addAttribute("Context-Name", domain);
        }

        return accept;
    }

}
