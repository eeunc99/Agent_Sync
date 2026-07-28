package com.app.daol.mapper.original;

import com.app.daol.dto.ColumnDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Original {
    List<ColumnDto> selectUser();
}
