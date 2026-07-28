package com.app.daol.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/** 대상(Target) DB 커넥션/세션 설정. 접속정보는 spring.target.datasource.* 프로퍼티에서 주입. */
@Configuration
@EnableTransactionManagement
public class SqlSessionConfigTarget {

    @Bean(name = "targetDataSource")
    @ConfigurationProperties(prefix = "spring.target.datasource")
    public DataSource targetDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "targetSqlSessionFactory")
    public SqlSessionFactory targetSqlSessionFactory(@Qualifier("targetDataSource") DataSource targetDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(targetDataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sqlmap/target/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "targetSqlSessionTemplate")
    public SqlSessionTemplate targetSqlSessionTemplate(@Autowired @Qualifier("targetSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "targetNameJdbcTemplate")
    public NamedParameterJdbcTemplate targetNameJdbcTemplate(@Qualifier("targetDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "targetParamJdbcTemplate")
    public JdbcTemplate targetParamJdbcTemplate(@Qualifier("targetDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
