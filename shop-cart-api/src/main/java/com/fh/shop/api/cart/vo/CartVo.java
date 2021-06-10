package com.fh.shop.api.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartVo implements Serializable {

    private Long totalCount;

    private String totalPrice;

    private List<CartItemVo> cartItemVoList = new ArrayList<>();

}
