package com.jz.servise.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.common.CustomException;
import com.jz.dto.SetmealDto;
import com.jz.entity.Setmeal;
import com.jz.entity.SetmealDish;
import com.jz.mapper.SetmealMapper;
import com.jz.servise.SetmealDishService;
import com.jz.servise.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceimpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时保存套餐与菜品的关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作Setmeal表，执行install
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐与菜品关联信息，操作Setmeal_dish表，执行install,saveBatch()批量保存
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    /**
     *  删除套餐，同时删除套餐与菜品的关系
     */
    public void removeWithDish(List<Long> ids) {
        //查询满足id为（1，2，3）的数量有多少
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
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
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

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

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal=this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId,setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新dish表基本信息
        this.updateById(setmealDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();

        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }
}
