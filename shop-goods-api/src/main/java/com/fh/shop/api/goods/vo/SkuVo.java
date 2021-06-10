package com.fh.shop.api.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author hkx
 * @create 2021-04-12 18:41
 */
@Data
public class SkuVo {

    private Long id;

    private String skuName;

    private BigDecimal price;

    private String colorImage;

}
