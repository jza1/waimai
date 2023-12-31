package com.jz.filter;

import com.alibaba.fastjson.JSON;
import com.jz.common.BaseContext;
import com.jz.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户是否已经完成登录
//过滤器，拦截请求
 */
@WebFilter(filterName="loginCheckFilter",urlPatterns="/*")//  "/*"表示拦截所有的，urlPatterns指向拦截的路径请求
@Slf4j
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符,请求路径是否属于指定的路径
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest; //强转，转型
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        //1、获取本次请求的URI（网址）
        String requestURI = request.getRequestURI();// /backend/index.html

        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径，拦截请求，看不见数据
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };


        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response); //放行
            return;
        }

        //4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            //获取修改者更新者的id并保存到ThreadLocal（BaseContext类中）中
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();//获得当前线程的id
            log.info("线程id为：{}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、移动端判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            //获取修改者更新者的id并保存到ThreadLocal（BaseContext类中）中
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id = Thread.currentThread().getId();//获得当前线程的id
            log.info("线程id为：{}",id);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        //遍历出urls(定义的地址），match匹配
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
