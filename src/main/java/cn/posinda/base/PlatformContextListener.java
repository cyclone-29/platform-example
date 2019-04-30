package cn.posinda.base;

import cn.posinda.utils.HbaseUtil;
import cn.posinda.utils.package$;
import com.alibaba.druid.pool.DruidDataSource;
import com.mashape.unirest.http.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
@Slf4j
public class PlatformContextListener implements ServletContextListener {


    @Resource
    @Qualifier("phoenixDataSource")
    private DruidDataSource phoenixDataSource;

    @Resource
    private HbaseUtil hbaseUtil;

    @Resource
    @Qualifier("mysqlDataSource")
    private DruidDataSource mysqlDataSource;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("context initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("clean up resources......");
        try {
            log.info("close hdfs fileSystem..");
            package$.MODULE$.fileSystem().close();
            log.info("close hbase connection..");
            hbaseUtil.admin().close();
            log.info("close phoenix datasource");
            phoenixDataSource.close();
            log.info("close mysql datasource");
            mysqlDataSource.close();
            log.info("complete cleaning up resources,context destroyed..");
            Unirest.shutdown();
            log.info("unirest shutdown....");
        } catch (IOException e) {
            log.error("failed to clean up resources...");
            e.printStackTrace();
        }
    }
}
