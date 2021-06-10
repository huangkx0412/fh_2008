package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemVo implements Serializable {

    private String skuName;

    private Long skuId;

    private String skuImage;

    private String price;

    private Long count;

    private String subPrice;


}
