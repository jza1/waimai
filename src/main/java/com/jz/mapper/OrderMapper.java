package com.jz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}