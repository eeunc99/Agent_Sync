package com.app.daol.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** 원본 DB 조회 서비스. 실제 SELECT 문은 application.properties(original.query.*)에서 주입. */
@Service
@Slf4j
public class OriginalDatabaseService {

    @Autowired
    @Qualifier("originalJdbcTemplate")
    private JdbcTemplate originalJdbcTemplate;

    @Value("${original.query.group.select}")
    private String selectGroupQuery;

    @Value("${original.query.user.select}")
    private String selectUserQuery;

    public List<Map<String, Object>> selectGroupQueryList() {
        if (selectGroupQuery == null || selectGroupQuery.isEmpty()) {
            return null;
        }
        return originalJdbcTemplate.queryForList(selectGroupQuery);
    }

    public List<Map<String, Object>> selectUserQueryList() {
        if (selectUserQuery == null || selectUserQuery.isEmpty()) {
            return null;
        }
        return originalJdbcTemplate.queryForList(selectUserQuery);
    }
}
