package net.cdsunrise.ztyg.acquisition.sensor.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 传感器类型
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StateEnum {
    NORMAL(1,"正常"),
    FAULT(0,"故障");

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, StateEnum> MODE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(StateEnum.values())
            .collect(Collectors.toMap(StateEnum::getCode, e -> e)));

    public static Map<Integer, StateEnum> getModeEnumMap() {
        return MODE_ENUM_MAP;
    }
}
