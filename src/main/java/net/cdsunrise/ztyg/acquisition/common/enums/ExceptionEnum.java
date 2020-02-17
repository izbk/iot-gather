package net.cdsunrise.ztyg.acquisition.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 异常枚举
 * @author binke zhang
 * @date 2019-08-03 15:30
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExceptionEnum {

    /** 处理成功 */
    SUCCESS("04000000", "success","操作成功"),
    /**系统错误 */
    FAILED("04001001","failed","系统错误"),
    PARAM_ERROR("04001003", "param error", "参数错误"),
    EXECUTE_ERROR("04001004", "execute command error", "执行指令错误，请稍后重试"),
    NO_PERMISSION("04001006", "no permission", "无权访问"),
    OPERATE_FAIL("04001008", "operate fail", "操作失败"),
    PARAM_NOT_EXIST("004009", "param not exist", "参数不存在"),
    DEVICE_CODE_NOT_EXIST("004010", "device not exist", "设备编码不存在"),
    DATE_FORMAT_ERROR("004011","date format error, expect: yyyy-MM-dd","日期格式错误，期望格式：yyyy-MM-dd"),
    DATE_TIME_FORMAT_ERROR("004012","date time format error, expect: yyyy-MM-dd HH:mm:ss","时间格式错误，期望格式：yyyy-MM-dd HH:mm:ss"),
    DATE_NIL("004013","date is null","日期不能为空"),
    CONVERT_STRING2DATE("004014","convert string to date error","日期字符串转换为Date错误"),
    DB_INSERT_FAIL("005001","database insert fail","数据库插入失败"),
    DB_UPDATE_FAIL("005002","database update fail","数据库更新失败"),
    DB_DELETE_FAIL("005003","database delete fail","数据库删除失败"),
    DB_EXISTS_ERROR("005004","name or code is exists","名称或编码已存在"),
    DATA_HAS_CHILD_ERROR("005005","data has child","数据存在子节点"),
    DATA_NOT_EXISTS_ERROR("005006","data is not exists","数据不存在"),
    ;

    @Getter
    private String code;
    @Getter
    private String message;
    @Getter
    private String chineseMessage;

    private static final Map<String,ExceptionEnum> CODE_MAP =
            Collections.unmodifiableMap(Arrays.stream(ExceptionEnum.values()).collect(Collectors.toMap(ExceptionEnum::getCode, Function.identity())));
    public static Map<String, ExceptionEnum> getCodeMap() {
        return CODE_MAP;
    }
}
