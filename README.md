## 平台简介

<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;前段时间做了一个路由认证项目，网上查了好久，终于在gitee上找到了jamiesun大佬开源的<a href="https://gitee.com/jamiesun/ToughRADIUS.git" target="_blank">ToughRADIUS</a>，基本上符合项目的需求，但还是不能完全满足，以前一直用的若依大佬开源的<a href="https://gitee.com/y_project/RuoYi.git" target="_blank">若依后台管理框架</a>，最后把2个大佬的项目合并，再添加项目的新功能，形成了本项目最终框架。</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;此框架继承了2个框架的优点，以<a href="https://gitee.com/y_project/RuoYi.git" target="_blank">若依后台管理框架V4.6.0</a>做为基础框架，实现了<a href="https://gitee.com/jamiesun/ToughRADIUS.git" target="_blank">ToughRADIUS</a>大部分功能，支持<b>标准RADIUS协议（RFC 2865, RFC 2866）</b>，提供<b>完整的AAA实现</b>。可运用于<b>酒店WIFI认证</b>，<b>公司局域网权限认证</b>，<b>商场WIFI认证</b>，<b>其它公共场所</b>等等，当然，您也可以对她进行深度定制，以做出更强系统。所有前端后台代码封装过后十分精简易上手，出错概率低。系统会陆续更新一些实用功能。</p>

* 感谢 若依后台管理 框架。[点我前往](https://gitee.com/y_project/RuoYi.git)
* 感谢 ToughRADIUS 框架。[点我前往](https://gitee.com/jamiesun/ToughRADIUS.git)
* 阿里云折扣场：[点我前往](https://www.aliyun.com/minisite/goods?userCode=vvwubb2a)
* 腾讯云秒杀场：[点我前往](https://cloud.tencent.com/act/cps/redirect?redirect=1078&cps_key=86eaf6934c31445e209ce1dacfc62db0&from=console)

## 运行环境

JDK：>= 1.8

数据库: MySQL >= 5.7（推荐）

## 环境搭建

- 数据库
直接导入项目中sql目录下 ruoyiradius-init.sql 即可
- 其它部署相关内容可参考若依API [点我前往](http://doc.ruoyi.vip/ruoyi/)

## 支持的路由器厂商

- 爱立信
- ToughProxy
- 中兴
- 思科
- H3C
- 华为
- juniper
- Mikrotik


## 主要实现功能

- 套餐管理
- 设备管理
- 用户管理
- 订单管理
- 在线管理
- 微信、支付宝H5支付
- 实现用户上线请求认证、动态授权、限速、强制下线
- 实现上网时间到期后自动下线

## 若依后台管理框架内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存查询，删除、清空等操作。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 在线体验

- admin/admin123  

演示地址： [点我进入](http://39.108.211.5:8090/index) 


## 交流群

QQ群： 5475847 [点我进入](https://jq.qq.com/?_wv=1027&k=xJHpvKp1)