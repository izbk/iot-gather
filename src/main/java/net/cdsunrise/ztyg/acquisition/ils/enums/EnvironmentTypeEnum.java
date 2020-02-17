package net.cdsunrise.ztyg.acquisition.ils.enums;

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
public enum EnvironmentTypeEnum {
    TEMPERATURE(20, "温度"),
    HUMIDITY(22, "湿度"),
    PM25(23, "pm2.5"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, EnvironmentTypeEnum> ENVIRONMENT_TYPE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(EnvironmentTypeEnum.values())
            .collect(Collectors.toMap(EnvironmentTypeEnum::getCode, e  -> e)));

    public static Map<Integer, EnvironmentTypeEnum> getEnvironmentTypeEnumMap() {
        return ENVIRONMENT_TYPE_ENUM_MAP;
    }
}
