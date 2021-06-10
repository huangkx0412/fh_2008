package com.fh.shop.api.filter;


import lombok.Data;

//返回指定数据格式的类
@Data
public class MemberVo {

    private Long id;

    private String memberName;

    private String nickName;

}
