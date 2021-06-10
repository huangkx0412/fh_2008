package com.fh.shop.api.cart.biz;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.fh.shop.api.cart.vo.CartItemVo;
import com.fh.shop.api.cart.vo.CartVo;
import com.fh.shop.api.goods.IGoodsFeignService;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.BigDecimalUtil;
import com.fh.shop.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private IGoodsFeignService goodsFeignService;

    //添加购物车
    @Override
    public ServerResponse addCartItem(Long memberId, Long skuId, Long count) {

        //通过接口访问判断
        Integer countLimit = Constants.SKU_COUNT_LIMIT;
        if (count >countLimit){
            return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);
        }

        //当前商品是否存在
        ServerResponse<Sku> skuData = goodsFeignService.findSku(skuId);
        Sku sku = skuData.getData();
        if (sku == null){
            return  ServerResponse.error(ResponseEnum.CART_SKU_IS_NOT_EXIST);
        }
        //商品是否已上架
        if(sku.getStatus().equals(Constants.STATUS_DOWN)){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_DOWN);
        }
        //商品的库存量小于购买数量
        Integer stock = sku.getStock();
        if (stock.intValue() <count){
            return ServerResponse.error(ResponseEnum.CART_SKU_STOCK_ERROR);
        }



        //会员是否有购物车
        String buildCartKey = KeyUtil.buildCartKey(memberId);
        //判断当前会员有没有购物车
        boolean exist = RedisUtil.exist(buildCartKey);
        if (!exist){
            //判断通过接口直接访问
            if (count <= 0){
                return ServerResponse.success(ResponseEnum.CART_IS_ERROR);
            }
            //创建一个购物车，直接把商品加入到购物车
            CartVo cartVo = new CartVo();
            CartItemVo cartItemVo = new CartItemVo();//代表一个商品
            cartItemVo.setCount(count);
            String price = sku.getPrice().toString();
            cartItemVo.setPrice(price);
            cartItemVo.setSkuId(sku.getId());
            cartItemVo.setSkuImage(sku.getColorImage());
            cartItemVo.setSkuName(sku.getSkuName());
            //BigDecimal类不能用普通算法去计算 使用他自己的方法
            BigDecimal subPrice = BigDecimalUtil.mul(price, count+"");
            cartItemVo.setSubPrice(subPrice.toString());
            cartVo.getCartItemVoList().add(cartItemVo);
            cartVo.setTotalCount(count);
            cartVo.setTotalPrice(cartItemVo.getPrice());
            //redis存数据
            //RedisUtil.set(buildCartKey, JSON.toJSONString(cartVo));
            RedisUtil.hset(buildCartKey, Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));
            //购物车所有商品数量
            RedisUtil.hset(buildCartKey, Constants.CART_JSON_FIELD_COUNT, cartVo.getTotalCount()+"");

        }else {

            String cartJson = RedisUtil.hget(buildCartKey, Constants.CART_JSON_FIELD);
            //如果有购物车
            CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
            List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
            Optional<CartItemVo> item = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            //购物车有这款商品
            if (item.isPresent()){
                //购物车有这款商品，找到这款商品更新商品的数量，小计
                CartItemVo cartItemVo = item.get();

                long itemCount= cartItemVo.getCount()+count;
                //【通过网页访问】 购买限制 同一款限购十个
                if (itemCount > countLimit){
                    return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);
                }
                //如果商品<=0
                if (itemCount <= 0){
                    //从购物车删除当前商品
                    cartItemVoList.removeIf(x -> x.getSkuId().longValue() == cartItemVo.getSkuId().longValue());
                   //如果购物车移除了所有的商品
                    if (cartItemVoList.size() == Constants.CART_SKU_IS_NULL){
                        //把整个购物车删掉
                        RedisUtil.delete(buildCartKey);
                        return ServerResponse.success();
                    }
                   //更新购物车 CartVo
                    updateCartMethod(buildCartKey,cartVo);
                    return ServerResponse.success();
                }
                cartItemVo.setCount(itemCount);
                BigDecimal subPrice = new BigDecimal(cartItemVo.getSubPrice());
                String subPriceStr = subPrice.add(BigDecimalUtil.mul(cartItemVo.getPrice(), count + "")).toString();
                cartItemVo.setSubPrice(subPriceStr);
                //更新购物车 相同一款的商品
                updateCartMethod(buildCartKey, cartVo);

            }else {
                //判断通过接口直接访问
                if (count <= 0){
                    return ServerResponse.success(ResponseEnum.CART_IS_ERROR);
                }
                //购物车没有这款商品，直接将商品加入购物车
                CartItemVo cartItemVo = new CartItemVo();
                cartItemVo.setCount(count);
                String price = sku.getPrice().toString();
                cartItemVo.setPrice(price);
                cartItemVo.setSkuId(sku.getId());
                cartItemVo.setSkuImage(sku.getColorImage());
                cartItemVo.setSkuName(sku.getSkuName());
                BigDecimal subPrice = BigDecimalUtil.mul(price, count + "");
                cartItemVo.setSubPrice(subPrice.toString());
                cartVo.getCartItemVoList().add(cartItemVo);

                updateCartMethod(buildCartKey, cartVo);

            }

        }


        return ServerResponse.success();
    }

    //更新购物车 提取的的方法
    public void updateCartMethod(String buildCartKey, CartVo cartVo) {

        List<CartItemVo> cartItemVos = cartVo.getCartItemVoList();
        long totalCount = 0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVos) {
            totalCount += itemVo.getCount();
            totalPrice = totalPrice.add(new BigDecimal(itemVo.getSubPrice()));
        }
        cartVo.setTotalCount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());

        //更新购物车 【redis中的购物车 】
        RedisUtil.hset(buildCartKey, Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));
        //购物车所有商品数量
        RedisUtil.hset(buildCartKey, Constants.CART_JSON_FIELD_COUNT, cartVo.getTotalCount()+"");
    }

    //查询购物车数据
    @Transactional(readOnly = true)
    @Override
    public ServerResponse findCartList(Long memberId) {
        String cartJsonList = RedisUtil.hget(KeyUtil.buildCartKey(memberId),Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJsonList, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    //购物车所有商品数量
    @Override
    public ServerResponse findCartListCount(Long memberId) {
        //取到hash里count属性对应的value
        String cartJsonCount = RedisUtil.hget(KeyUtil.buildCartKey(memberId),Constants.CART_JSON_FIELD_COUNT);
        return ServerResponse.success(cartJsonCount);
    }

    //删除购物车商品
    @Override
    public ServerResponse deleteCartGoods(Long memberId, Long skuId) {
        //获取会员对应的购物车
        String memberCart = RedisUtil.hget(KeyUtil.buildCartKey(memberId), Constants.CART_JSON_FIELD);
        //将json
        CartVo cartVo = JSON.parseObject(memberCart, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Optional<CartItemVo> itemVo = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if (!itemVo.isPresent()){
            return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
        }
        //移除该集合和这个skuid对应的商品
        cartItemVoList.removeIf(x -> x.getSkuId().longValue() == skuId.longValue());
        if (cartItemVoList.size() == Constants.CART_SKU_IS_NULL){
            RedisUtil.delete(KeyUtil.buildCartKey(memberId));
            return ServerResponse.success();
        }
        //更新购物车
        updateCartMethod(KeyUtil.buildCartKey(memberId),cartVo);
        return ServerResponse.success();
    }

    //批量删除购物车商品
    @Override
    public ServerResponse deleteBatch(Long memberId, String ids) {
        if (StringUtils.isEmpty(ids)){
            return ServerResponse.error(ResponseEnum.CART_BATCH_DELETE_SELECT_NULL);
        }
        String cartJson = RedisUtil.hget(KeyUtil.buildCartKey(memberId), Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Arrays.stream(ids.split(",")).forEach(x ->cartItemVoList.removeIf(y -> y.getSkuId().longValue() == Long.parseLong(x)));

        if (cartItemVoList.size() == Constants.CART_SKU_IS_NULL){
            RedisUtil.delete(KeyUtil.buildCartKey(memberId));
            return ServerResponse.success();
        }
        updateCartMethod(KeyUtil.buildCartKey(memberId),cartVo);
        return ServerResponse.success();
    }


}
