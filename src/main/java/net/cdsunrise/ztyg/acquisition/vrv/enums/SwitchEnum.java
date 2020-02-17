package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 开关
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SwitchEnum {
    /** 关 */
    OFF("F0","关"),
    /** 开 */
    ON("F1","开");

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, SwitchEnum> SWITCH_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(SwitchEnum.values())
            .collect(Collectors.toMap(SwitchEnum::getCode, e -> e)));

    public static Map<String, SwitchEnum> getSwitchEnumMap() {
        return SWITCH_ENUM_MAP;
    }
}
