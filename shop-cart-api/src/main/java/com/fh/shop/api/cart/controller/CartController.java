package com.fh.shop.api.cart.controller;

import com.fh.shop.api.BaseController;
import com.fh.shop.api.cart.biz.CartService;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/carts")
@Api(tags = "商品购物车接口")
public class CartController extends BaseController {

    @Autowired
    private HttpServletRequest request;

    @Resource
    private CartService cartService;



    @PostMapping("/addCartItem")
    @ApiOperation(value = "新增购物车商品接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "商品规格id",required = true,dataType = "java.lang.Long"),
            @ApiImplicitParam(name = "count", value = "商品个数",required = true,dataType = "java.lang.Long"),
            @ApiImplicitParam(name = "x-auth", value = "头信息",required = true,dataType = "java.lang.String",paramType= "header")
    })
    public ServerResponse addCart(Long skuId, Long count){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.addCartItem(memberId,skuId,count);
    }

    //查询redis购物车
    @GetMapping("/findCartList")
    @ApiOperation(value = "查询购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse findCartList(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCartList(memberId);
    }



    @GetMapping("/findCartListCount")
    @ApiOperation(value = "查询购物车数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头信息",dataType = "java.lang.String",required = true,paramType = "header")
    })
    public ServerResponse findCartListCount(){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.findCartListCount(memberId);
    }


    @DeleteMapping("/deleteCartItem")
    @ApiOperation(value = "删除购物车商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "商品id",dataType = "java.lang.Long",required = true),
            @ApiImplicitParam(name = "x-auth", value = "头信息",required = true,dataType = "java.lang.String",paramType = "header")
    })
    public ServerResponse deleteCartGoods(Long skuId){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteCartGoods(memberId, skuId);
    }


    @DeleteMapping("/deleteBatch")
    @ApiOperation(value = "批量删除购物车商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "商品id字符串",dataType = "java.lang.String",required = true),
            @ApiImplicitParam(name = "x-auth", value = "头信息",required = true,dataType = "java.lang.String",paramType = "header")
    })
    public ServerResponse deleteBatch(String ids){
        MemberVo memberVo = buildMemberVo(request);
        Long memberId = memberVo.getId();
        return cartService.deleteBatch(memberId, ids);
    }



}
