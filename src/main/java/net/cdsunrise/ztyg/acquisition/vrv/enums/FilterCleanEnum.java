package net.cdsunrise.ztyg.acquisition.vrv.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 过滤网清洗标志
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FilterCleanEnum {
    /** 标志 */
    FLAG("42","过滤网清洗标志");

    /**
     * 16进制字符
     */
    @Getter
    private String code;
    @Getter
    private String message;

    private static final Map<String, FilterCleanEnum> FILTER_CLEAN_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(FilterCleanEnum.values())
            .collect(Collectors.toMap(FilterCleanEnum::getCode, e -> e)));

    public static Map<String, FilterCleanEnum> getFilterCleanEnumMap() {
        return FILTER_CLEAN_ENUM_MAP;
    }
}
