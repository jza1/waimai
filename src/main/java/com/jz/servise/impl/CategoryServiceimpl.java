package com.jz.servise.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.Category;
import com.jz.entity.Dish;
import com.jz.entity.Setmeal;
import com.jz.mapper.CategoryMapper;
import com.jz.servise.CategoryService;
import com.jz.servise.DishService;
import com.jz.servise.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceimpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired //注入菜品的Service
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /*
    根据id删除分类，删除前需要进行判断
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果已经关联，抛出异常
        if(count1>0){
            //已经关联菜品，抛出异常
            throw new ClassCastException("当前分类信息下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count();
        if(count1>0){
            //已经关联套餐，抛出异常
            throw new ClassCastException("当前分类信息下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);

    }
}