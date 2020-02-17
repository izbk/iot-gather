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
public enum SensorTypeEnum {
    CO2(1,"CO2"),
    WD(2,"温度"),
    WSD(3,"温湿度");

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, SensorTypeEnum> MODE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(SensorTypeEnum.values())
            .collect(Collectors.toMap(SensorTypeEnum::getCode, e -> e)));

    public static Map<Integer, SensorTypeEnum> getModeEnumMap() {
        return MODE_ENUM_MAP;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
