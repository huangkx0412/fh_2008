package com.fh.shop.api;


import com.alibaba.fastjson.JSON;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.Constants;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseController {

    public MemberVo buildMemberVo(HttpServletRequest request){

        try {
            String memberVoJson = URLDecoder.decode(request.getHeader(Constants.CURR_MEMBER), "utf-8");//获取参数时解密
            MemberVo memberVo = JSON.parseObject(memberVoJson, MemberVo.class);

            return memberVo;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
