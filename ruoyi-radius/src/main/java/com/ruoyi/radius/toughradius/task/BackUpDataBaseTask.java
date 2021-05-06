package com.ruoyi.radius.toughradius.task;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.manager.AsyncManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

/**
 * 自动备份数据库定时任务
 *
 * @author panweilei
 * @date 2021-05-06
 */
@Component("backUpDataBaseTask")
public class BackUpDataBaseTask {
    private static final Log log = LogFactory.getLog(BackUpDataBaseTask.class);

    @Value("${spring.datasource.druid.master.url}")
    private String url;
    @Value("${spring.datasource.druid.master.username}")
    private String userName;
    @Value("${spring.datasource.druid.master.password}")
    private String password;

    @Value("${ruoyi.backupPath}")
    private String sqlFilePath;

    @Value("${ruoyi.clearDays}")
    private int clearDays;

    /**
     * 数据库导出 方法
     * 在系统监控--->定时任务 中配置 backUpDataBaseTask.exportSql()
     */
    public void exportSql() throws IOException, InterruptedException {

        // 预处理数据库连接字符串
        if(url.indexOf("//") > 0) url = url.substring(url.indexOf("//") + 2,url.indexOf("?"));
        if(url.indexOf("?") > 0) url = url.substring(url.indexOf("?"));
        // 指定导出的 sql 存放的文件夹
        File saveFile = new File(sqlFilePath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        String dataBaseName = getDataBaseName();
        String fileName = dataBaseName + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS)+ ".sql";

        StringBuilder cmdStr = new StringBuilder();

        // 拼接备份命令 mysqldump -h localhost -P3306 -uroot -p123456 ruoyiradius > E:/abcd.sql
        cmdStr.append("mysqldump")
                .append(" -h ").append(getHost())
                .append(" -P").append(getPort()) // 大写
                .append(" -u").append(userName)
                .append(" -p").append(password) // 小写
                .append(" ").append(dataBaseName)
                .append(" ").append("--hex-blob")//使用十六进制符号转储二进制字符序列，防止乱码
                .append(" > ").append(sqlFilePath).append(fileName);
        // Windows下：
        if(isWindows()){
            Process exec = Runtime.getRuntime().exec(new String[]{ "cmd", "/c", cmdStr.toString()});
            if (exec.waitFor() == 0) {
                log.info("数据库备份成功，保存路径：" + sqlFilePath);
            } else {
                System.out.println("process.waitFor()=" + exec.waitFor());
            }
        }else if(isLinux()){// Linux下：
            Process exec = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c", cmdStr.toString()});
            if (exec.waitFor() == 0) {
                log.info("数据库备份成功，保存路径：" + sqlFilePath);
            } else {
                System.out.println("process.waitFor()=" + exec.waitFor());
            }
        }else {
            throw new RuntimeException("暂时只支持Linux和windows系统！你的系统是：" + System.getProperty("os.name"));
        }
        // 清理数据库备份文件
        AsyncManager.me().execute(cleanOutDateBackupFile(clearDays,sqlFilePath));
    }
    /**
     * 清理过期的数据库备份文件
     * @param days  如果传入30，表示清理30天前的文件
     * @param folder 存放备份文件的文件夹路径
     * @return
     */
    public static TimerTask cleanOutDateBackupFile(final Integer days, final String folder) {
        return new TimerTask() {
            @Override
            public void run() {
                File file = new File(folder);
                File[] files =  file.listFiles();
                Date now = new Date();
                Date daysAgo = DateUtils.addDays(now,days > 0 ? - days : days);
                for(File f : files){
                    Date date = new Date(f.lastModified());
                    if(date.before(daysAgo)){
                        if(f.exists()){
                            f.delete();
                        }
                    }
                }
            }
        };
    }
    /**
     * 判断操作系统类型 是否微软系统
     * @return
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").indexOf("Windows") != -1;
    }
    /**
     * 判断操作系统类型 是否Linux系统
     * @return
     */
    private static boolean isLinux() {
        return System.getProperty("os.name").indexOf("Linux") != -1;
    }
    /**
     * 获取数据库名
     */
    public String getDataBaseName() {
        return url.substring(url.indexOf("/") + 1);
    }

    /**
     * 获取主机地址
     */
    private String getHost() {
        return url.substring(0,url.indexOf(":"));
    }
    /**
     * 获取主机端口
     */
    private String getPort() {
        return url.substring(url.indexOf(":") + 1,url.indexOf("/"));
    }
}
