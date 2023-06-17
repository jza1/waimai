package com.jz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.common.R;
import com.jz.dto.SetmealDto;
import com.jz.entity.Category;
import com.jz.entity.Setmeal;
import com.jz.servise.CategoryService;
import com.jz.servise.SetmealDishService;
import com.jz.servise.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
/**
 * 套餐管理
 */
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息{}",setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> page1=new Page<>(page,pageSize);
        Page<SetmealDto> page2 =new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        //添加过滤条件
        //like模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件，根据更新时间进行排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(page1,page2,"records");

        List<Setmeal> records=page1.getRecords();
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();//获取分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        page2.setRecords(list);
        return R.success(page2);
    }

    @DeleteMapping
    //@RequestParam 主要用于将请求参数区域的数据映射到控制层方法的参数上
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }
    @PostMapping("/status/{status}")
    public R<String> statusUpdate(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        UpdateWrapper<Setmeal> updateWrapper=new UpdateWrapper<>();
        //根据ids查询需要更新的信息
        updateWrapper.in("id",ids);
        Setmeal setmeal=new Setmeal();
        //修改对应信息状态
        if(status==0){
            setmeal.setStatus(0);
        }else {
            setmeal.setStatus(1);
        }

        setmealService.update(setmeal,updateWrapper);
        return R.success("修改套餐状态成功");
    }

    @GetMapping("/{id}")
    //@Pathvariable映射URL绑定的占位符，将URL中的占位符参数绑定到控制器的方法进行入参时，URL中{xxx}占位符可以通过@Pathvariable(“XXX”)进行绑定。一般是在get请求中使用。
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto=setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 更新修改套餐
     * @param
     * @return
     */
    @PutMapping
    // 前端请求提交的是json数据，所有要转换，添加@RequestBody
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
