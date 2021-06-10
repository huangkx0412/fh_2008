package com.fh.shop.api.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface CartService {

    ServerResponse addCartItem(Long memberId, Long skuId, Long count);

    ServerResponse findCartList(Long memberId);

    ServerResponse findCartListCount(Long memberId);


    ServerResponse deleteCartGoods(Long memberId, Long skuId);

    ServerResponse deleteBatch(Long memberId, String ids);
}
