package com.ruoyi.radius.toughradius.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.radius.toughradius.common.PageResult;
import com.ruoyi.radius.toughradius.common.RestResult;
import com.ruoyi.radius.toughradius.common.ValidateUtil;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.domain.RadiusOnline;
import com.ruoyi.radius.toughradius.service.OnlineCache;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线管理Controller
 *
 * @author panweilei
 * @date 2021-01-26
 */
@Controller
@RequestMapping("/radius/online")
public class OnlineController extends BaseController {

    private String prefix = "radius/online";
    @Autowired
    private OnlineCache onlineCache;


    /**
     * 页面加载
     *
     * @return
     */
    @RequiresPermissions("radius:online:view")
    @GetMapping()
    public String order() {
        return prefix + "/online";
    }

    /**
     * 查询在线管理列表
     */
    @RequiresPermissions("radius:online:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(RadiusOnline online) {
        startPage();
        List<RadiusOnline> list = onlineCache.queryOnlinePage(online.getRealname(),online.getUsername());
        return getDataTable(list);
    }

    /**
     * 强制下线
     */
    @RequiresPermissions("radius:online:remove")
    @Log(title = "在线管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {

        if (ValidateUtil.isNotEmpty(ids)) {
            onlineCache.unlockOnlines(ids);
           return success();
        } else {
            return error();
        }
    }

    /***************** 以下方法为 原ToughRADIUS中的方法 暂未使用**********************/
    //在线查询
    @GetMapping({"/api/v6/online/query","/admin/online/query"})
    public PageResult<RadiusOnline> queryOnlineHandler(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "40") int count,
                                                       String nodeId, Integer invlan, Integer outVlan, String nasAddr, String nasId, String beginTime, String endTime, String keyword, String sort){
        return onlineCache.queryOnlinePage(start,count,nodeId,invlan,outVlan,nasAddr,nasId,beginTime,endTime,keyword,sort);
    }


    @GetMapping({"/api/v6/online/unlock", "/admin/online/unlock"})
    public RestResult unlockOnlineHandler(@RequestParam(name = "ids")String ids,
                                          @RequestParam(name = "sessionId")String sessionId){
        if(ValidateUtil.isNotEmpty(ids)){
            onlineCache.unlockOnlines(ids);
            return new RestResult(0,"批量下线执行中，请等待");
        }else if(ValidateUtil.isNotEmpty(sessionId)){
            boolean r= onlineCache.unlockOnline(sessionId);
            return new RestResult(r?0:1,"下线执行完成");
        }else{
            return new RestResult(1,"无效参数");
        }
    }
    //清理在线
    @GetMapping({"/api/v6/online/clear","/admin/online/clear"})
    public RestResult clearOnlineHandler( String nodeId,Integer invlan, Integer outVlan,  String nasAddr, String nasId, String beginTime, String endTime,  String keyword){
        onlineCache.clearOnlineByFilter(nodeId,invlan, outVlan,nasAddr,nasId,beginTime,endTime,keyword);
        return new RestResult(0,"success");
    }


    @GetMapping("/api/v6/online/fc")
    public RestResult forceClearOnlineHandler(String username){
        onlineCache.unlockOnlineByUser(username);
        return new RestResult(0,"success");
    }


    //一个下线
    @GetMapping({"/api/v6/online/delete","/admin/online/delete"})
    public RestResult DeleteOnlineHandler(String ids){
        for(String oid : ids.split("/admin,")){
            onlineCache.removeOnline(oid);
        }
        return new RestResult(0,"success");
    }

    @GetMapping({"/api/v6/online/query/byids","/admin/online/query/byids"})
    public List<RadiusOnline> queryOnlineByIds(String ids){
        return onlineCache.queryOnlineByIds(ids);
    }


}

