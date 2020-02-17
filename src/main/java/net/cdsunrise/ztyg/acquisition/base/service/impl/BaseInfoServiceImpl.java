package net.cdsunrise.ztyg.acquisition.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.base.service.BaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-19 09:28
 */
@Service
public class BaseInfoServiceImpl implements BaseInfoService {

    private final BaseInfoMapper baseInfoMapper;

    @Autowired
    public BaseInfoServiceImpl(BaseInfoMapper baseInfoMapper) {
        this.baseInfoMapper = baseInfoMapper;
    }

    @Override
    public List<BaseInfo> getAll() {
        return baseInfoMapper.selectList(null);
    }

    @Override
    public BaseInfo getByDeviceCode(String deviceCode) {
        QueryWrapper<BaseInfo> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseInfo::getDeviceCode, deviceCode);
        return baseInfoMapper.selectOne(queryWrapper);
    }
}
