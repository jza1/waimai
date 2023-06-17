package com.jz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.common.R;
import com.jz.entity.Employee;
import com.jz.servise.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController//@RestController注解相当于@ResponseBody 和 @Controller合在一起的作用
//@RestController注解其实就是将 return 中的内容以 JSON字符串的形式返回客户端
@RequestMapping("/employee") //与前端请求对应,@RequestMapping注解是一个用来处理请求地址映射的注解，可用于映射一个请求或一个方法，可以用在类或方法上。
//用于方法上，表示在类的父路径下追加方法上注解中的地址将会访问到该方法
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /*
    员工登录
    @param request 获得Session（保存数据到Session）
    @param employee
    @return
     */
    @PostMapping("/login") //与前端请求对应
    //request 前端传输数据？
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、将页面提交的密码password进行md5加密处理,employee前端传输回来的信息
        String password = employee.getPassword();

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();


        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//唯一的用户名
//        System.out.println(emp);
        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果，0禁用，1可用
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    /*因为前端传过来的是JS形式的所以使用@RequestBody*/
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //添加了自动填充（MyMetaObjecthandler)，就不需要以下代码
        //获取当前系统时间（当前记录创建时间）
        //employee.setCreateTime(LocalDateTime.now());
        //获取当前系统时间（当前记录更新时间）
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id，getAttribute返回的是Object类型
        //request: 代表了这次 http 请求，包含请求的头部信息、请求参数等。
        //getsession(): 从 request 对象中获取当前会话所在的 httpsession 对象。httpsession 表示了服务器跟踪客户端会话状态的的一种方式，其中存储了一些关于客户端和服务器之间交互的信息。
        //getattribute(): 这是 httpsession 接口提供的获取属性值的方法，需要传入一个属性名作为参数。当调用该方法时，它返回指定属性名所对应的属性值。
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
//        System.out.println("新增员工"+" "+employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件 like相似比较,模糊查询 eq相等比较
        //StringUtils.isNotEmpty(name)表示name是否为空，不为空添加，为空跳过
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);
//        System.out.println("111"+pageInfo);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();//获得当前线程的id
        log.info("线程id为：{}",id);

//添加了自动填充（MyMetaObjecthandler类)，就不需要以下代码
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")//有{}的原因，id是通过url地址的方式传输过来的，@PathVariable表示路径变量
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id); //返回Employee对象
        if(employee != null){
            return R.success(employee); //把Employee对象转成了json
        }
        return R.error("没有查询到对应员工信息");
    }
}
