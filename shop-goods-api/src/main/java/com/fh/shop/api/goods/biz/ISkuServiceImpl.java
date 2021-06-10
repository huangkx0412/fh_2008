package com.fh.shop.api.goods.biz;

import com.fh.shop.api.goods.mapper.ISkuMapper;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.common.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hkx
 * @create 2021-04-12 15:46
 */
@Service("skuService")
public class ISkuServiceImpl implements ISkuService {

    @Autowired
    private ISkuMapper skuMapper;


    @Override
    public List<Sku> findRecommendNewProductList() {
        List<Sku> skuList = skuMapper.findRecommendNewProductList();
        return skuList;
    }

    @Override
    public ServerResponse findSku(Long id) {
        Sku sku = skuMapper.selectById(id);
        return ServerResponse.success(sku);
    }
}
