package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 制冷模式
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ModeEnum {
    WIND("00","送风"),
    HEAT("01","制热"),
    COOL("02","制冷"),
    AUTO("03","自动"),
    DRY("07","除湿");

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, ModeEnum> MODE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(ModeEnum.values())
            .collect(Collectors.toMap(ModeEnum::getCode, e -> e)));

    public static Map<String, ModeEnum> getModeEnumMap() {
        return MODE_ENUM_MAP;
    }
}
