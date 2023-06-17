package com.jz.servise.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.DishFlavor;
import com.jz.mapper.DishFlavorMapper;
import com.jz.servise.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceimpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
