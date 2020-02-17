package net.cdsunrise.ztyg.acquisition.common.utils;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;

/**
 * 返回ResultUtil
 * @author Binke Zhang
 */
public class ResultUtil<T> {
    private ResultUtil() {throw new IllegalStateException("Utility class");}
    public static<T> Result<T> success() {
        return success(null);
    }

    public static<T> Result<T> success(T data) {
        return build(ExceptionEnum.SUCCESS.getCode(), ExceptionEnum.SUCCESS.getMessage(), data, true);
    }

    public static<T> Result<T> success(String code, String msg, T data) {
        return build(code, msg, data, true);
    }

    public static<T> Result<T> fail() {
        return fail(ExceptionEnum.FAILED.getCode(), ExceptionEnum.FAILED.getMessage(), null);
    }

    public static<T> Result<T> fail(String msg) {
        return build(ExceptionEnum.OPERATE_FAIL.getCode(), msg, null, false);
    }

    public static<T> Result<T> fail(String code, String msg) {
        return build(code, msg, null, false);
    }

    public static<T> Result<T> fail(String code, String msg, T data) {
        return build(code, msg, data, false);
    }

    public static <T> Result<T> build(boolean success, ExceptionEnum exceptionEnum) {
        return build(exceptionEnum.getCode(), exceptionEnum.getChineseMessage(), null, success);
    }

    public static<T> Result<T> build(String code, String msg, T data, boolean success) {
        Result<T> result = new Result<>();
        result.setMessage(msg);
        result.setCode(code);
        result.setData(data);
        result.setSuccess(success);
        return result;
    }

    public static <T> Result<T> fail(ExceptionEnum exceptionEnum) {
        return build(exceptionEnum.getCode(), exceptionEnum.getChineseMessage(), null, false);
    }
    public static <T> Result<T> fail(ExceptionEnum exceptionEnum,T data) {
        return build(exceptionEnum.getCode(), exceptionEnum.getChineseMessage(), data, false);
    }

}
