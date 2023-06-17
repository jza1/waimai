package com.jz.servise.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.entity.AddressBook;
import com.jz.mapper.AddressBookMapper;
import com.jz.servise.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceimpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
