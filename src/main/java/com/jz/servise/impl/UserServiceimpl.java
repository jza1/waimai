package com.jz.servise.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.User;
import com.jz.mapper.UserMapper;
import com.jz.servise.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User> implements UserService {
}
