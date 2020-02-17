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
public enum SystemTypeEnum {
    SBJK(1, "设备监控系统"),
    ZNZM(2, "智能照明系统"),
    SPJK(3, "视频监控系统"),
    NYGL(4, "能源管理系统"),
    DQHZ(5, "电气火灾系统"),
    XFBJ(6, "消防报警系统"),
    YJSS(7, "应急疏散系统"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, SystemTypeEnum> SYSTEM_TYPE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(SystemTypeEnum.values())
            .collect(Collectors.toMap(SystemTypeEnum::getCode, e  -> e)));

    public static Map<Integer, SystemTypeEnum> getSystemTypeEnumMap() {
        return SYSTEM_TYPE_ENUM_MAP;
    }
}
