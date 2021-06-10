package com.fh.shop.api.cate.controller;

import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hkx
 * @create 2021-04-11 20:48
 */
@RestController
@RequestMapping("/api/")
public class CateController {

    @Resource(name = "cateService")
    private ICateService cateService;

    //查询
    @GetMapping("cates")
    public ServerResponse queryCateAllData(){
        return cateService.findCateList();
    }





}
