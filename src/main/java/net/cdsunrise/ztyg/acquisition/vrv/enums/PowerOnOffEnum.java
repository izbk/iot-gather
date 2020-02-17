package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 开关机
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PowerOnOffEnum {
    /** 关 */
    OFF("60","关机"),
    /** 开 */
    ON("61","开机"),
    /** 不控制 */
    UNDO("FF","不控制");

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, PowerOnOffEnum> POWER_ON_OFF_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(PowerOnOffEnum.values())
            .collect(Collectors.toMap(PowerOnOffEnum::getCode, e -> e)));
}
