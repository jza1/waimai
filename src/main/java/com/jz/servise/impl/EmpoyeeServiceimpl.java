package com.jz.servise.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.Employee;
import com.jz.mapper.EmployeeMapper;
import com.jz.servise.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmpoyeeServiceimpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
