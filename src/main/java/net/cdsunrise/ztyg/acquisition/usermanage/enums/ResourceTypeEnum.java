package net.cdsunrise.ztyg.acquisition.usermanage.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum ResourceTypeEnum {
    MENU(1,"菜单"),
    BUTTON(2,"按钮"),
    INTERFACE(3,"接口"),
    DATA(4,"数据");

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, ResourceTypeEnum> ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(ResourceTypeEnum.values())
            .collect(Collectors.toMap(ResourceTypeEnum::getCode, e -> e)));

    public static Map<Integer, ResourceTypeEnum> getEnumMap() {
        return ENUM_MAP;
    }
}
