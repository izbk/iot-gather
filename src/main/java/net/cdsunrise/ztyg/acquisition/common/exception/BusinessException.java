package net.cdsunrise.ztyg.acquisition.common.exception;

import lombok.Data;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;

/**
 * @author YQ
 */
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1622337383462701502L;
    private final String code;
    private final String message;
    private final String chineseMessage;

    public BusinessException(String code, String message) {
        this.code = code;
        this.message = message;
        this.chineseMessage = "";
    }

    public BusinessException(String code, String message,String chineseMessage) {
        this.code = code;
        this.message = message;
        this.chineseMessage = chineseMessage;
    }

    public BusinessException(ExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
        this.chineseMessage = exceptionEnum.getChineseMessage();
    }

}
