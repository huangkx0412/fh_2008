package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.api.cate.po.Cate;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cateService")
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper cateMapper;



    @Override
    public ServerResponse findCateList() {

        //先从缓存找是否有数据
        String cateListInfo = RedisUtil.get("cateList");
        if (StringUtils.isNotEmpty(cateListInfo)){
            //有就将数据转换成前台要的数据格式 直接返回
            List<Cate> cates = JSON.parseArray(cateListInfo, Cate.class);
            return ServerResponse.success(cates);
        }

        // redis数据为空 去数据库查
        List<Cate> cateList = cateMapper.findAllCateList();
        //redis 要存的是string 类型 转换
        String cateListJSON = JSON.toJSONString(cateList);
        //往 redis set 存一份 下次就不需要走数据库查
        RedisUtil.set("cateList",cateListJSON);
        return ServerResponse.success(cateList);
    }



}
