package com.jz.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.common.R;
import com.jz.entity.Orders;
import com.jz.servise.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,number);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件 like相似比较,模糊查询 eq相等比较
        //StringUtils.isNotEmpty(name)表示name是否为空，不为空添加，为空跳过
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行查询
        orderService.page(pageInfo,queryWrapper);
//        System.out.println("111"+pageInfo);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Orders orders){
        log.info(orders.toString());

        long id = Thread.currentThread().getId();//获得当前线程的id
        log.info("线程id为：{}",id);
        orderService.updateById(orders);

        return R.success("订单信息修改成功");
    }

}