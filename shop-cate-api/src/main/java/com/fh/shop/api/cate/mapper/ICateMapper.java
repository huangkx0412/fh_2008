package com.fh.shop.api.cate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.cate.po.Cate;

import java.util.List;

public interface ICateMapper extends BaseMapper<Cate> {


    List<Cate> findAllCateList();


}
