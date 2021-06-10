package com.fh.shop.api.goods.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Sku {

    private Long id;

    private Long spuId;

    private String skuName;

    private BigDecimal price;

    private Integer stock;

    private String specInfo;

    private Long colorId;

    private String colorImage;

    private Integer status;//是否上架

    private Integer recommend;//推荐

    private Integer newProduct;//新品

    private Long salesVolume;//销量



}
