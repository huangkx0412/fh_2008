package com.fh.shop.api.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends ZuulFilter {

    @Value("${fh.shop.checkUrls}")
    private List<String> checkUrls;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }



    @SneakyThrows
    @Override
    public Object run() throws ZuulException {

        log.info("url:{}", checkUrls);

        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();


        //获取当前访问的url
        StringBuffer requestURL = request.getRequestURL();
       boolean isCheck = false;
        for (String checkUrl : checkUrls) {
            if (requestURL.indexOf(checkUrl) >0){
                isCheck = true;
                break;
            }
        }

        if (!isCheck){
            //默认相当于放行
            return null;
        }


        //验证签名 x-auth 前台传过来从request对象获取 [x-auth: eyJpZCI6MSwibWVtYmVyTmFtZSI6ImhreCIsIm5pY2tOYW1lIjoia2sifQ==.MThra3JlamQqKl4lLTkwNGhqaGZmZDE4OA==]
        //判断是否有头信息
        String header = request.getHeader("x-auth");
        if (StringUtils.isEmpty(header)){

            return  buildResponse(ResponseEnum.TOKEN_IS_MISS);
            //throw new ShopException(ResponseEnum.TOKEN_IS_MISS);
        }

        //判断头信息是否完整
        String[] headerArr = header.split("\\.");
        if (headerArr.length !=2){
            return  buildResponse(ResponseEnum.TOKEN_HEADER_IS_NOT_FULL);
            //throw new ShopException(ResponseEnum.TOKEN_HEADER_IS_NOT_FULL);
        }
        //验签 【核心】
        String memberVoJsonBase64 = headerArr[0];
        String signBase64 = headerArr[1];

        //进行Base64解锁 把字节数组变成string
        String  memberVoJson = new String(Base64.getDecoder().decode(memberVoJsonBase64),"utf-8");
        String sign = new String(Base64.getDecoder().decode(signBase64),"utf-8");
        //生成新的签名
        String newSign = Md5Util.sign(memberVoJson, Constants.SECRET);
        if (!newSign.equals(sign)){
            return  buildResponse(ResponseEnum.TOKEN_IS_FALL);
            //throw new ShopException(ResponseEnum.TOKEN_IS_FALL);
        }

        //将json转为Java 对象
        MemberVo memberVo = JSON.parseObject(memberVoJson, MemberVo.class);
        Long memberId = memberVo.getId();
        //判断是否已过期
        if (!RedisUtil.exist(KeyUtil.buildMemberKey(memberId))){
            return buildResponse(ResponseEnum.TOKEN_LOGIN_IS_TIMEOUT);
           // throw new ShopException(ResponseEnum.TOKEN_LOGIN_IS_TIMEOUT);
        }

        //续命
        RedisUtil.expire(KeyUtil.buildMemberKey(memberId),Constants.TOKEN_EXPIRE_TIME);
        //获取会员信息 将memberVo存入request
       // request.setAttribute(Constants.CURR_MEMBER, memberVo);
        currentContext.addZuulRequestHeader(Constants.CURR_MEMBER, memberVoJson);
        URLEncoder.encode(memberVoJson, "utf-8");
        return null;
    }


    private Object buildResponse(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        //在拦截器里不仅拦截 还能向前台响应信息
        response.setContentType("application/json;charset=utf-8");//解决返回的数据乱码问题
        currentContext.setSendZuulResponse(false);//拦截，不会在进行路由转发
        //提示json信息
        ServerResponse error = ServerResponse.error(responseEnum);
        String errorRes = JSON.toJSONString(error);
        currentContext.setResponseBody(errorRes);
        return null;
    }
}
