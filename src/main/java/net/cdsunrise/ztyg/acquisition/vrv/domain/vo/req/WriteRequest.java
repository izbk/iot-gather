package net.cdsunrise.ztyg.acquisition.vrv.domain.vo.req;

import lombok.Data;
import net.cdsunrise.ztyg.acquisition.vrv.enums.ModeEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.PowerOnOffEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.WindSpeedEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 设备新增参数
 * @author Binke Zhang
 * @date 2019/8/3 11:41
 */
public class WriteRequest {
        /**
         * 设置设备所有参数
         */
        @Data
        public static class SetAllReq{
                @NotEmpty(message = "设备编号不能为空")
                private String deviceCode;
                @NotNull(message = "风速不能为空")
                private WindSpeedEnum windSpeedEnum;
                @NotNull(message = "开关机不能为空")
                private PowerOnOffEnum powerOnOffEnum;
                @NotNull(message = "制冷模式不能为空")
                private ModeEnum modeEnum;
                @NotNull(message = "摄氏度不能为空")
                private Integer temperature;
        }
}
