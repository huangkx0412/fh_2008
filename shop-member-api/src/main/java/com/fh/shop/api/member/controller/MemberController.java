package com.fh.shop.api.member.controller;

import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.biz.IMemberService;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Api(tags = "会员接口模块")
public class MemberController extends BaseController {

    @Resource
    private IMemberService memberService;

    @Resource
    private HttpServletRequest request;



    //登录
    @PostMapping("/members/login")
    @ApiOperation(value = "登录")
    public ServerResponse login(String memberName,String password){
        return memberService.login(memberName,password);
    }


    //注销
    @ApiOperation("注销")
    @GetMapping("/members/loginOut")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", dataType = "java.lang.String" ,paramType = "header",required = true)
    })
    public ServerResponse logOut(){
       // MemberVo memberVo = (MemberVo) request.getAttribute(Constants.CURR_MEMBER);
        MemberVo memberVo = buildMemberVo(request);
        RedisUtil.delete(KeyUtil.buildMemberKey(memberVo.getId()));

        return ServerResponse.success();
    }


    //前后端分离使用jwt 登录后如何获取用户信息
    @GetMapping("/members/memberLoginInfo")
    @ApiOperation("jwt获取登录用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "头信息", name = "x-auth", dataType = "java.lang.String" ,paramType = "header",required = true)
    })
    public ServerResponse getMemberLoginInfo() {
       // MemberVo memberVo = (MemberVo) request.getAttribute(Constants.CURR_MEMBER);
        MemberVo memberVo = buildMemberVo(request);
        return ServerResponse.success(memberVo);
    }




}
