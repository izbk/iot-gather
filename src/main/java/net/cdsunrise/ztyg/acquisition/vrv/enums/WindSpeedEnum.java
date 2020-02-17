package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 风速
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WindSpeedEnum {
    /** 超低 */
    LL("10","超低"),
    /** 低 */
    L("20","低"),
    /** 中 */
    M("30","中"),
    /** 高 */
    H("40","高"),
    /** 超高 */
    HH("50","超高"),
    /** 不操作 */
    UNDO("FF","不操作");

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, WindSpeedEnum> WIND_SPEED_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(WindSpeedEnum.values())
            .collect(Collectors.toMap(WindSpeedEnum::getCode, e -> e)));

    public static Map<String, WindSpeedEnum> getWindSpeedEnumMap() {
        return WIND_SPEED_ENUM_MAP;
    }
}
