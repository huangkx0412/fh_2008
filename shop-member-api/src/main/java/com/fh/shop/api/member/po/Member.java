package com.fh.shop.api.member.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_member")
public class Member implements Serializable {

    private Long id;

    private String memberName;

    private String password;

    private String nickName;

    private String phone;

    private String mail;

    private String status;

    private Long score;


}
