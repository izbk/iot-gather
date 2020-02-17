package net.cdsunrise.ztyg.acquisition.vrv.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 设备基本信息
 * @author Binke Zhang
 * @date 2019-8-27 14:26:07
 */
@Data
@TableName("device")
public class DeviceDTO {
        /** 主键ID */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id ;
        /**
         * 设备编号
         */
        private String deviceCode;
        /**
         * 设备名称
         */
        private String name;
}
