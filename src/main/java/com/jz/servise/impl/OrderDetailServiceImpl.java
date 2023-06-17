package com.jz.servise.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.OrderDetail;
import com.jz.mapper.OrderDetailMapper;
import com.jz.servise.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}