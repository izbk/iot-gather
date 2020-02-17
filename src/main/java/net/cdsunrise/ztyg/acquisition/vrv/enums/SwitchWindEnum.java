package net.cdsunrise.ztyg.acquisition.vrv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sh
 * @date 2019-11-22 09:35
 */
@AllArgsConstructor
public enum SwitchWindEnum {
    OFF("FF60", "关机"),
    ON("FF61", "开机"),
    LL("10FF", "风速ll"),
    L("20FF", "风速l"),
    M("30FF", "风速m"),
    H("40FF", "风速h"),
    HH("50FF", "风速hh"),
    AUTO("A0FF", "自动"),
    ;

    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, SwitchWindEnum> SWITCH_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(SwitchWindEnum.values())
            .collect(Collectors.toMap(SwitchWindEnum::getCode, e  -> e)));

    public static Map<String, SwitchWindEnum> getSwitchEnumMap() {
        return SWITCH_ENUM_MAP;
    }
}
