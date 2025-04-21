package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Merchant;
import com.takeout.mapper.MerchantMapper;
import com.takeout.service.MerchantService;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {
    private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);
    @Override
    public Merchant getMerchantById(Long shopId) throws NotFoundException {
        try {
            Merchant merchant = getById(shopId);
            if (merchant == null) {
                throw new NotFoundException("商家不存在，shopId: " + shopId);
            }
            return merchant;
        } catch (Exception e) {
            logger.error("查询商家信息失败，shopId: {}", shopId, e); // ✅ 添加日志记录
            throw e; // ✅ 抛出异常，由控制器捕获
        }
    }
    @Override
    public Page<Merchant> pageMerchants(Page<Merchant> page, String name) {
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name)
                    .or()
                    .like("description", name);
        }
        return baseMapper.selectPage(page, queryWrapper);
    }


}