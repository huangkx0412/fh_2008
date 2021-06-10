package com.fh.shop.api.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.goods.po.Sku;

import java.util.List;

/**
 * @author hkx
 * @create 2021-04-12 15:45
 */
public interface ISkuMapper extends BaseMapper<Sku> {

    List<Sku> findRecommendNewProductList();

//    int updateStock(CartItemVo cartItemVo);
//
//    void updateSkuBlackStock(@Param("skuId") Long skuId, @Param("skuCount") Long skuCount);
//
//    void updateSalesVolume(@Param("skuId") Long skuId, @Param("salesVolume") Long salesVolume);
}
