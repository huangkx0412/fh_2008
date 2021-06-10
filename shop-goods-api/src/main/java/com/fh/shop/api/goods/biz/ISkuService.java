package com.fh.shop.api.goods.biz;

import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.common.ServerResponse;

import java.util.List;

/**
 * @author hkx
 * @create 2021-04-12 15:46
 */
public interface ISkuService {
    List<Sku> findRecommendNewProductList();

    ServerResponse findSku(Long id);
}
