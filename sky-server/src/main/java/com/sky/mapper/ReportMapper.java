package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    List<Map<String, Object>> turnoverStatistics(LocalDate begin, LocalDate end);
}
