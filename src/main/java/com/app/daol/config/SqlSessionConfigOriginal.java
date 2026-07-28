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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/** 원본(Original) DB 커넥션/세션 설정. 접속정보는 spring.original.datasource.* 프로퍼티에서 주입. */
@Configuration
@EnableTransactionManagement
public class SqlSessionConfigOriginal {

    @Bean(name = "originalDataSource")
    @ConfigurationProperties(prefix = "spring.original.datasource")
    public DataSource getoriginalDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "originalSqlSessionFactory")
    public SqlSessionFactory getoriginalSqlSessionFactory(@Qualifier("originalDataSource") DataSource originalDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(originalDataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "originalSqlSessionTemplate")
    public SqlSessionTemplate originalSqlSessionTemplate(@Autowired @Qualifier("originalSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "originalJdbcTemplate")
    public JdbcTemplate originalJdbcTemplate(@Qualifier("originalDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
