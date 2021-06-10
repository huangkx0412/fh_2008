package com.fh.shop.api.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CrossFilter extends ZuulFilter {

    /**
     * pre 所有客户端请求在访问 真正的微服务 前 执行
     * route
     * post 在访问 微服务 之后 执行
     * error
     *
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 过滤器执行顺序 数值越小， 优先级越高
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * true 当前过滤器 是否生效（启用）
     * false: 失效
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }


    /**
     *
     *  所有的!! 业务逻辑处理
     *  永远都要返回null
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        log.info("============cross");
        //统一处理跨域
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,"POST,GET,PUT,DELETE");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"x-auth,x-token,content-type");

        //需要进行验证得
        //浏览器会发送两次请求 对没有携带请求头的options请求不进行放行
        //获取请求的方式 post get put delete options
        HttpServletRequest request = currentContext.getRequest();
        String method = request.getMethod();
        if (method.equalsIgnoreCase("OPTIONS")){//忽略大小写
            currentContext.setSendZuulResponse(false);
            return null;
        }


        return null;
    }
}
