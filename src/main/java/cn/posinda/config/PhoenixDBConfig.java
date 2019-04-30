package cn.posinda.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ClassUtils;

import java.io.IOException;

@Configuration
@MapperScan(basePackages = "cn.posinda.phoenix.repo",sqlSessionFactoryRef = "phoenixSqlSessionFactory")
@SuppressWarnings("all")
public class PhoenixDBConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name="phoenixDataSource")
    public DruidDataSource phoenixDataSource(){
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(env.getProperty("phoenix.url"));
        datasource.setDriverClassName(env.getProperty("phoenix.driver-class-name"));
        datasource.setInitialSize(Integer.valueOf(env.getProperty("phoenix.initialSize")));
        datasource.setMinIdle(Integer.valueOf(env.getProperty("phoenix.minIdle")));
        datasource.setMaxActive(Integer.valueOf(env.getProperty("phoenix.max-active")));
        datasource.setMaxWait(Integer.valueOf(env.getProperty("phoenix.maxWait")));
        datasource.setTimeBetweenEvictionRunsMillis(Integer.valueOf(env.getProperty("phoenix.timeBetweenEvictionRunsMillis")));
        datasource.setMinEvictableIdleTimeMillis(Integer.valueOf(env.getProperty("phoenix.minEvictableIdleTimeMillis")));
        datasource.setTestWhileIdle(Boolean.valueOf(env.getProperty("phoenix.testWhileIdle")));
        datasource.setTestOnBorrow(Boolean.valueOf(env.getProperty("phoenix.testOnBorrow")));
        datasource.setTestOnReturn(Boolean.valueOf(env.getProperty("phoenix.testOnReturn")));
        datasource.setPoolPreparedStatements(Boolean.valueOf(env.getProperty("phoenix.poolPreparedStatements")));
        datasource.setMaxPoolPreparedStatementPerConnectionSize(Integer.valueOf(env.getProperty("phoenix.maxOpenPreparedStatements")));
        datasource.setDefaultAutoCommit(Boolean.valueOf(env.getProperty("phoenix.default-auto-commit")));
        return datasource;
    }


    @Primary
    @Bean(name="phoenixSqlSessionFactory")
    public SqlSessionFactory phoenixSqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(phoenixDataSource());
        sqlSessionFactory.setConfigLocation(new ClassPathResource("phoenix/mybatis-config.xml"));
        sqlSessionFactory.setFailFast(true);
        sqlSessionFactory.setMapperLocations(getResource("phoenix/mapper", "*.xml"));
        return sqlSessionFactory.getObject();
    }

    @Primary
    @Bean(name="phoenixTransactionManager")
    public DataSourceTransactionManager phoenixTransactionManager() {
        return new DataSourceTransactionManager(phoenixDataSource());
    }

    public Resource[] getResource(String basePackage, String pattern) throws IOException {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage)) + "/" + pattern;
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
        return resources;
    }
}
