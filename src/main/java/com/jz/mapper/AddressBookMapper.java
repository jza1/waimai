package com.jz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jz.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
