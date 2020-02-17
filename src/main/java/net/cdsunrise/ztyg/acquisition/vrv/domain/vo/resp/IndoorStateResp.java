package net.cdsunrise.ztyg.acquisition.vrv.domain.vo.resp;

import lombok.Data;
import net.cdsunrise.ztyg.acquisition.vrv.enums.*;

/**
 * @author sh
 * @date 2019-11-21 15:15
 */
@Data
public class IndoorStateResp {
    /** 设备编号 */
    private String deviceCode;
    /** 位置描述 */
    private String locationDesc;
    /** 风量 */
    private WindSpeedEnum windSpeed;
    /** 开关 */
    private SwitchEnum switchStr;
    /** 清洗描述 */
    private FilterCleanEnum cleanDesc;
    /** 制冷模式 */
    private ModeEnum mode;
    /** 设置温度 */
    private Integer setTemperature;
    /** 室内温度 */
    private Integer indoorTemperature;
    /** 温度传感器状态 */
    private TPStateEnum tpState;
}
