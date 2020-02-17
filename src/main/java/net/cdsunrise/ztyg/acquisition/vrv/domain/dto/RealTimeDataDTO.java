package net.cdsunrise.ztyg.acquisition.vrv.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 设备实时数据信息
 * @author Binke Zhang
 * @date 2019-8-27 14:26:07
 */
@Data
@TableName("real_time_data")
public class RealTimeDataDTO {
        /** 主键ID */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id ;
        /**
         * 设备ID
         */
        private Long deviceId ;
        /**
         * 运行状态
         */
        private Integer runningState;
        /**
         * 通信状态
         */
        private Integer communicateState;
        /**
         * 运行模式
         */
        private String runningMode;
        /**
         * 运转风量
         */
        private String airVolume;
        /**
         * 设定温度
         */
        private Double setTemperature;
        /**
         * 室内温度
         */
        private Double indoorTemperature;
        /**
         * 采集时间
         */
        private Date createTime;
}
