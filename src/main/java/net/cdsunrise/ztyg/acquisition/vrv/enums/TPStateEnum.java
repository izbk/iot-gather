package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 温度传感器状态
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TPStateEnum {
    OK("0000","正常"),
    ERROR("0001","异常"),
    ;

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, TPStateEnum> TP_STATE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(TPStateEnum.values())
            .collect(Collectors.toMap(TPStateEnum::getCode, e -> e)));

    public static Map<String, TPStateEnum> getTpStateEnumMap() {
        return TP_STATE_ENUM_MAP;
    }
}
