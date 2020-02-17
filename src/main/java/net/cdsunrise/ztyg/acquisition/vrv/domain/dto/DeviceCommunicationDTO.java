package net.cdsunrise.ztyg.acquisition.vrv.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 设备通信属性信息
 * @author Binke Zhang
 * @date 2019-8-27 14:26:07
 */
@Data
@TableName("device_communication")
public class DeviceCommunicationDTO {
        /** 主键ID */
        @TableId(value = "deviceId", type = IdType.INPUT)
        private Long deviceId ;
        /**
         * 从站ID 16进制字符串
         */
        private String slaveId;
        /**
         * 寄存器地址
         */
        private String registerAddress;
}
