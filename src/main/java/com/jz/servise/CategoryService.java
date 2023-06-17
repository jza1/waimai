package com.jz.servise;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
