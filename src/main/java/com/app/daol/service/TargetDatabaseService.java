package com.app.daol.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 대상 DB 조회/등록/수정 서비스. 실제 쿼리는 application.properties(target.query.*)에서 주입. */
@Service
@Slf4j
public class TargetDatabaseService {

    @Autowired
    @Qualifier("targetNameJdbcTemplate")
    private NamedParameterJdbcTemplate targetNameJdbcTemplate;

    @Autowired
    @Qualifier("targetParamJdbcTemplate")
    private JdbcTemplate targetParamJdbcTemplate;

    @Value("${target.query.group.select}")
    private String targetGroupSelectQuery;

    @Value("${target.query.group.insert}")
    private String targetGroupInsertQuery;

    @Value("${target.query.group.update}")
    private String targetGroupUpdateQuery;

    @Value("${target.query.group.use.update}")
    private String targetGroupUseUpdateQuery;

    @Value("${target.query.user.insert}")
    private String targetUserInsertQuery;

    @Value("${target.query.user.update}")
    private String targetUserUpdateQuery;

    @Value("${target.query.update}")
    private String targetUpdateQuery;

    // 실 테이블/컬럼명은 비공개. 배포 환경에서 별도 관리.
    @Value("${target.query.user.id.select}")
    private String userIdQuery;

    @Value("${target.query.group.id.select}")
    private String groupIdQuery;

    public List<Map<String, Object>> groupList() {
        if (targetGroupSelectQuery == null || targetGroupSelectQuery.isEmpty()) {
            return null;
        }
        return targetParamJdbcTemplate.queryForList(targetGroupSelectQuery);
    }

    public int groupInsert(Map<String, Object> groupInsertMap) {
        if (targetGroupInsertQuery == null || targetGroupInsertQuery.isEmpty()) {
            return 0;
        }
        return targetNameJdbcTemplate.update(targetGroupInsertQuery, toParamSource(groupInsertMap));
    }

    public int groupUpdate(Map<String, Object> groupUpdateMap) {
        if (targetGroupUpdateQuery == null || targetGroupUpdateQuery.isEmpty()) {
            return 0;
        }
        return targetNameJdbcTemplate.update(targetGroupUpdateQuery, toParamSource(groupUpdateMap));
    }

    public int groupUseUpdate(Map<String, Object> groupUseUpdateMap) {
        if (targetGroupUseUpdateQuery == null || targetGroupUseUpdateQuery.isEmpty()) {
            return 0;
        }
        return targetNameJdbcTemplate.update(targetGroupUseUpdateQuery, toParamSource(groupUseUpdateMap));
    }

    public Set<String> userIdList() {
        List<Map<String, Object>> idList = targetParamJdbcTemplate.queryForList(userIdQuery);
        Set<String> userIdSet = new HashSet<>();
        for (Map<String, Object> row : idList) {
            Object id = row.get("id");
            if (id != null) {
                userIdSet.add(id.toString());
            }
        }
        return userIdSet;
    }

    public int userInsert(Map<String, Object> insertMap) {
        return targetNameJdbcTemplate.update(targetUserInsertQuery, toParamSource(insertMap));
    }

    public int userUpdate(Map<String, Object> userUpdateMap) {
        if (targetUserUpdateQuery == null || targetUserUpdateQuery.isEmpty()) {
            return 0;
        }
        return targetNameJdbcTemplate.update(targetUserUpdateQuery, toParamSource(userUpdateMap));
    }

    public int update(Map<String, Object> updateMap) {
        if (targetUpdateQuery == null || targetUpdateQuery.isEmpty()) {
            return 0;
        }
        return targetNameJdbcTemplate.update(targetUpdateQuery, toParamSource(updateMap));
    }

    private MapSqlParameterSource toParamSource(Map<String, Object> map) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            paramSource.addValue(entry.getKey(), entry.getValue());
        }
        return paramSource;
    }
}
