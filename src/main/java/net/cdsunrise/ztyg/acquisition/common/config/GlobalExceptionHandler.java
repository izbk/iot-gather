package net.cdsunrise.ztyg.acquisition.common.config;

import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 * @author binke zhang
 * @date 2019-8-27 15:46:50
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求内容格式或数据类型错误
     *
     * @return 处理结果
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    public Result httpMessageNotReadableException(Exception e) {
        log.error("请求内容不正确：{}", e.getMessage());
        return ResultUtil.fail("请求内容格式或数据类型错误");
    }

    /**
     * 权限检查不通过
     *
     * @return 处理结果
     */
    @ExceptionHandler({BusinessException.class})
    public Result businessException(BusinessException e) {
        log.error("", e);
        return ResultUtil.fail(e.getCode(),e.getChineseMessage());
    }

    /**
     * 参数检查不通过
     *
     * @return 处理结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Result result = ResultUtil.fail(ExceptionEnum.PARAM_ERROR,bindingResult);
        log.error("参数检查不通过：{}", result.getMessage());
        return result;
    }

    /**
     * 请求方式不支持
     *
     * @return 处理结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方式不支持：{}", e.getMethod());
        return ResultUtil.fail("请求方式不正确");
    }

    /**
     * 未知错误
     *
     * @return 处理结果
     */
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        log.error("未知错误", e);
        return ResultUtil.fail(ExceptionEnum.FAILED);
    }
}
