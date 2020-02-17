package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sh
 * @date 2019-11-21 11:17
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum  AdapterStateEnum {

    /**
     * 未准备
     */
    READY("0000", "未准备"),
    /**
     * 准备好
     */
    NO_READY("0001", "准备好"),
    ;

    private static final Map<String, AdapterStateEnum> STATE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(AdapterStateEnum.values())
            .collect(Collectors.toMap(AdapterStateEnum::getCode, e -> e)));

    @Getter
    private String code;
    @Getter
    private String desc;

    public static Map<String, AdapterStateEnum> getStateEnumMap() {
        return STATE_ENUM_MAP;
    }
}
