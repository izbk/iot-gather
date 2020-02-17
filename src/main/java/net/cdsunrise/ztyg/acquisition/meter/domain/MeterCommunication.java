package net.cdsunrise.ztyg.acquisition.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author sh
 * @date 2019-11-27 10:15
 */
@Data
@TableName("t_meter_communication")
public class MeterCommunication {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** 从站地址 */
    private Integer slaveId;
    /** 读电压起始地址 */
    private String voltageRegisterId;
    /** 读功率因子起始地址 */
    private String powerFactorRegisterId;
    /** 读用电量起始地址 */
    private String electricityRegisterId;
}
