package net.cdsunrise.ztyg.acquisition.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sh
 * @date 2019-11-22 10:03
 */
@AllArgsConstructor
public enum ExceptionEnum {
    PARAM_NOT_EXIST("001001", "param not exist", "参数不存在"),
    ;
    @Getter
    private String code;
    @Getter
    private String message;
    @Getter
    private String chineseMessage;
}
