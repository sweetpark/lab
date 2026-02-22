package com.example.playground.thread.mapper;

import com.example.playground.thread.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OptimisticMapper {
    Product selectById(@Param("id") Long id);
    int updateWithVersion(@Param("id") Long id, @Param("version") Integer version);
}
