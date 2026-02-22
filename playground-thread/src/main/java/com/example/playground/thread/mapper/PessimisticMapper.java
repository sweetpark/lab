package com.example.playground.thread.mapper;


import com.example.playground.thread.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PessimisticMapper {
    Product selectForUpdate(@Param("id") Long id);
    int updateStock(@Param("id") Long id);
}
