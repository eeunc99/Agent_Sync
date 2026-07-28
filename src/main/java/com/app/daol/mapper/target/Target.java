package com.app.daol.mapper.target;

import com.app.daol.dto.ColumnDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Target {
    List<ColumnDto> selectUser();
    void insertUser(ColumnDto columnDto);
    void updateUser(ColumnDto columnDto);
}
