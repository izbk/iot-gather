package net.cdsunrise.ztyg.acquisition.vrv.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * @author sh
 * @date 2019-12-26 09:19
 */
@Data
@TableName("t_vrv_real_data")
public class RealDataDTO {
    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id ;
    /** 设备编码 */
    private String deviceCode;
    /** 运行模式 */
    private String runningMode;
    /** 运转风量 */
    private String airVolume;
    /** 设定温度 */
    private Integer setTemperature;
    /** 室内温度 */
    private Integer indoorTemperature;
    /** 采集时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
