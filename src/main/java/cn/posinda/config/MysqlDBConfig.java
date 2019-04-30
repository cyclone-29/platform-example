package cn.posinda.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages = {"cn.posinda.mysql.repo","cn.posinda.phoenix.job.importConcerned.repo"}, sqlSessionFactoryRef = "mysqlSqlSessionFactory")
@SuppressWarnings("all")
public class MysqlDBConfig {

    @Autowired
    private Environment env;

    @Bean(name = "mysqlDataSource")
    public DruidDataSource mysqlDataSource() throws SQLException {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(env.getProperty("mysql.url"));
        datasource.setDriverClassName(env.getProperty("mysql.driver-class-name"));
        datasource.setInitialSize(Integer.valueOf(env.getProperty("mysql.initialSize")));
        datasource.setMinIdle(Integer.valueOf(env.getProperty("mysql.minIdle")));
        datasource.setMaxActive(Integer.valueOf(env.getProperty("mysql.max-active")));
        datasource.setMaxWait(Integer.valueOf(env.getProperty("mysql.maxWait")));
        datasource.setTimeBetweenEvictionRunsMillis(Integer.valueOf(env.getProperty("mysql.timeBetweenEvictionRunsMillis")));
        datasource.setMinEvictableIdleTimeMillis(Integer.valueOf(env.getProperty("mysql.minEvictableIdleTimeMillis")));
        datasource.setTestWhileIdle(Boolean.valueOf(env.getProperty("mysql.testWhileIdle")));
        datasource.setTestOnBorrow(Boolean.valueOf(env.getProperty("mysql.testOnBorrow")));
        datasource.setTestOnReturn(Boolean.valueOf(env.getProperty("mysql.testOnReturn")));
        datasource.setPoolPreparedStatements(Boolean.valueOf(env.getProperty("mysql.poolPreparedStatements")));
        datasource.setMaxPoolPreparedStatementPerConnectionSize(Integer.valueOf(env.getProperty("mysql.maxOpenPreparedStatements")));
        datasource.setUsername(env.getProperty("mysql.username"));
        datasource.setPassword(env.getProperty("mysql.password"));
        datasource.setFilters(env.getProperty("mysql.filters"));
        datasource.setConnectionProperties(env.getProperty("mysql.connectionProperties"));
        return datasource;
    }


    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(mysqlDataSource());
        sqlSessionFactory.setConfigLocation(new ClassPathResource("mysql/mybatis-config.xml"));
        sqlSessionFactory.setFailFast(true);
        sqlSessionFactory.setMapperLocations(getResource("mysql/mapper", "*.xml"));
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "mysqlTransactionManager")
    public DataSourceTransactionManager mysqlTransactionManager() throws SQLException {
        return new DataSourceTransactionManager(mysqlDataSource());
    }

    public Resource[] getResource(String basePackage, String pattern) throws IOException {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage)) + "/" + pattern;
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
        return resources;
    }
}
