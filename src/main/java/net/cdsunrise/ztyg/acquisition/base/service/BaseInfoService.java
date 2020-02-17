package net.cdsunrise.ztyg.acquisition.base.service;

import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-19 09:23
 */
public interface BaseInfoService {
    /**
     * 获取所有基础信息
     * @return 所有基础信息
     */
    List<BaseInfo> getAll();

    /**
     * 根据设备编码获取基础信息
     * @param deviceCode 设备编码
     * @return 基础信息
     */
    BaseInfo getByDeviceCode(String deviceCode);
}
