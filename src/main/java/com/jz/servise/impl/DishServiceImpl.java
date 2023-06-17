package com.jz.servise.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.common.CustomException;
import com.jz.dto.DishDto;
import com.jz.entity.Dish;
import com.jz.entity.DishFlavor;
import com.jz.entity.SetmealDish;
import com.jz.mapper.DishMapper;
import com.jz.servise.DishFlavorService;
import com.jz.servise.DishService;
import com.jz.servise.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        System.out.println(dish.getId()+" "+id);

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     *  删除套餐，同时删除套餐与菜品的关系
     */
    @Override
    @Transactional
    public void removeWithSetmeal(List<Long> ids) {
        //查询满足id为（1，2，3）的数量有多少
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //有多个id，QueryWrapper中in方法需要的格式是List
        // queryWrapper.in（“属性”，条件，条件 ）——符合多个条件的值
        // queryWrapper.lt（）——小于
        //queryWrapper.le（）——小于等于
        //queryWrapper.gt（）——大于
        //queryWrapper.ge（）——大于等于
        //queryWrapper.eq（）——等于
        //queryWrapper.ne（）——不等于
        //queryWrapper.betweeen（“age”,10,20）——age在值10到20之间
        //queryWrapper.notBetweeen（“age”,10,20）——age不在值10到20之间
        //queryWrapper.like（“属性”,“值”）——模糊查询匹配值‘%值%’
        //queryWrapper.notLike（“属性”,“值”）——模糊查询不匹配值‘%值%’
        //queryWrapper.likeLeft（“属性”,“值”）——模糊查询匹配最后一位值‘%值’
        //queryWrapper.likeRight（“属性”,“值”）——模糊查询匹配第一位值‘值%’
        //queryWrapper.isNull（）——值为空或null
        //queryWrapper.isNotNull（）——值不为空或null
        //queryWrapper.in（“属性”，条件，条件 ）——符合多个条件的值
        //queryWrapper.notIn(“属性”，条件，条件 )——不符合多个条件的值
        //queryWrapper.or（）——或者
        //queryWrapper.and（）——和
        //queryWrapper.orderByAsc(“属性”)——根据属性升序排序
        //queryWrapper.orderByDesc(“属性”)——根据属性降序排序
        //queryWrapper.inSql(“sql语句”)——符合sql语句的值
        //queryWrapper.notSql(“sql语句”)——不符合SQL语句的值
        //queryWrapper.esists（“SQL语句”）——查询符合SQL语句的值
        //queryWrapper.notEsists（“SQL语句”）——查询不符合SQL语句的值
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
