package net.cdsunrise.ztyg.acquisition.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author binke zhang
 * @date 2019-11-22 09:35
 */
@AllArgsConstructor
public enum DeviceTypeEnum {
    ZM(1, "照明"),
    CO2(2, "CO2传感器"),
    QJ(3, "球型摄像机"),
    KL(4, "客流统计摄像机"),
    MD(5, "人流密度摄像机"),
    KT(6, "中央空调面板"),
    SNWD(7, "室内温湿度传感器"),
    WLBQ(8, "室内半球"),
    SWWD(9, "室外温湿度传感器"),
    WD(10, "室内温度传感器"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, DeviceTypeEnum> DEVICE_TYPE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(DeviceTypeEnum.values())
            .collect(Collectors.toMap(DeviceTypeEnum::getCode, e  -> e)));

    public static Map<Integer, DeviceTypeEnum> getDeviceTypeEnumMap() {
        return DEVICE_TYPE_ENUM_MAP;
    }
}
