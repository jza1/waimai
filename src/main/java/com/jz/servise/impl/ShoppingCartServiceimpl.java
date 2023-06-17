package com.jz.servise.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.ShoppingCart;
import com.jz.mapper.ShoppingCartMapper;
import com.jz.servise.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceimpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
