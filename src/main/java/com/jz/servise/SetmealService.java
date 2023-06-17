package com.jz.servise;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.dto.SetmealDto;
import com.jz.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时保存套餐与菜品的关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     *  删除套餐，同时删除套餐与菜品的关系
     */
    public void removeWithDish(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
