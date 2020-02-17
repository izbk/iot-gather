package net.cdsunrise.ztyg.acquisition.usermanage.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.cdsunrise.ztyg.acquisition.sensor.enums.StateEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Binke Zhang
 * @date 2019/12/25 9:33
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum  UserStatusEnum {
    NORMAL(1,"正常"),
    DISABLED(0,"禁用"),
    DELETED(-1,"删除");

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, UserStatusEnum> ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(UserStatusEnum.values())
            .collect(Collectors.toMap(UserStatusEnum::getCode, e -> e)));

    public static Map<Integer, UserStatusEnum> getEnumMap() {
        return ENUM_MAP;
    }
}
