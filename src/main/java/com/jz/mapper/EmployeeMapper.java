package com.jz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

// 操作数据库，接口
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
