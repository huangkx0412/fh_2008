package com.fh.shop.api.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.api.member.mapper.IMemberMapper;
import com.fh.shop.api.member.po.Member;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Base64;

@Service
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {

    @Resource
    private IMemberMapper memberMapper;


    @Override
    public ServerResponse login(String memberName, String password) {
        //验证非空
        if (StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_INFO_IS_NULL);
        }
        //判断用户名是否正确
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("memberName",memberName);
        Member member = memberMapper.selectOne(memberQueryWrapper);
        Long id = member.getId();

        if (member ==null){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_MEMBERNAME_IS_EXIST);
        }
        //验证密码是否正确
        if (!member.getPassword().equals(Md5Util.md5(password))){
            return ServerResponse.error(ResponseEnum.MEMBER_LOGIN_MEMBER_PWD_IS_ERROR);
        }
        //会员激活状态
        if (!member.getStatus().equals(Constants.ACTIVE_SUCCESS_CODE)){

            return ServerResponse.error(ResponseEnum.MEMBER_ATCIVR_MAIL_IS_NOT_ATCIVE);
        }


        //==============生成签名
        //响应用户信息【响应给前台的用户信息不能包含 *密码】和签名给前台
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());
        memberVo.setNickName(member.getNickName());
        String memberVoJson = JSON.toJSONString(memberVo);
        //生成签名
        String sign = Md5Util.sign(memberVoJson , Constants.SECRET);

        //==============生成签名
        //将用户信息进行base64编码
        String memberVoJsonBase64 = Base64.getEncoder().encodeToString(memberVoJson.getBytes());
        String signBase64 = Base64.getEncoder().encodeToString(sign.getBytes());
        //将信息存入redis中
        RedisUtil.setEx(KeyUtil.buildMemberKey(id),"",Constants.TOKEN_EXPIRE_TIME);
        //将 用户信息和签名通过 . 分割成一个字符串响应给前台 组成结构 x.y
        return ServerResponse.success(memberVoJsonBase64+"."+signBase64);
    }
}
