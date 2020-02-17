package net.cdsunrise.ztyg.acquisition.ils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author binke zhang
 * @date 2019-11-22 09:35
 */
@AllArgsConstructor
public enum CommandTypeEnum {
    LAMPLIGHT(4, "灯光控制类指令"),
    CURTAIN(5, "窗帘（幕布、电动窗）控制类指令"),
    MUSIC(6, "背景音乐控制类指令"),
    VRV(7, "中央空调控制类指令"),
    FLOOR_HEATING(8, "地暖控制类指令"),
    AIR_CONDITIONING(9, "新风控制类指令"),
    SCENE(10, "情景控制类指令"),
    INFRARED(11, "红外遥控设备或 485/232 协议设备控制类指令"),
    TIME(12, "系统时间发送指令"),
    ENVIRONMENT(13, "环境参数传输指令"),
    ;

    @Getter
    private Integer code;
    @Getter
    private String message;

    private static final Map<Integer, CommandTypeEnum> COMMAND_TYPE_ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(CommandTypeEnum.values())
            .collect(Collectors.toMap(CommandTypeEnum::getCode, e  -> e)));

    public static Map<Integer, CommandTypeEnum> getCommandTypeEnumMap() {
        return COMMAND_TYPE_ENUM_MAP;
    }
}
