package com.fh.shop.api.member.param;

import com.fh.shop.api.member.po.Member;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberParam extends Member implements Serializable {

    private String code;

    private String confirmPassword;


}
