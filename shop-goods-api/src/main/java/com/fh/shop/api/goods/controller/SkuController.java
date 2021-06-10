package com.fh.shop.api.goods.controller;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.fh.shop.api.goods.biz.ISkuService;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.api.goods.vo.SkuVo;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hkx
 * @create 2021-04-12 15:47
 */
@RestController
@RequestMapping("/api")
@Api(tags = "商品信息模块")
@Slf4j
public class SkuController {

    @Resource(name = "skuService")
    private ISkuService skuService;

    @ApiOperation(value = "查询展示商品")
    @GetMapping("/skus/recommend/newProduct")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth",value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse list(){

        String skuVoListInfo = RedisUtil.get("skuVoList");
        log.info("这是什么？{}",skuVoListInfo);
        if (StringUtils.isNotEmpty(skuVoListInfo)){
            List<Sku> skuListJson = JSON.parseArray(skuVoListInfo, Sku.class);
            return ServerResponse.success(skuListJson);
        }

        List<Sku> skuList = skuService.findRecommendNewProductList();
        //构建只需要得数据，多余数据不传送
        List<SkuVo> skuVoList = skuList.stream().map(x -> {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(x.getId());
            skuVo.setSkuName(x.getSkuName());
            skuVo.setPrice(x.getPrice());
            skuVo.setColorImage(x.getColorImage());
            return skuVo;
        }).collect(Collectors.toList());

        String skuVoListJSON = JSON.toJSONString(skuVoList);

        RedisUtil.setEx("skuVoList",skuVoListJSON,30);

        return ServerResponse.success(skuVoList);
    }

    @GetMapping("/skus/findSku")
    public ServerResponse findSku(@RequestParam("id") Long id){

        return skuService.findSku(id);
    }




}
