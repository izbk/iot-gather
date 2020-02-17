package net.cdsunrise.ztyg.acquisition.vrv.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author sh
 * @date 2019-11-25 11:27
 */
@Data
@TableName("t_vrv_communication")
public class VrvCommunicationDTO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** 从站地址 */
    private String slaveId;
    /** 查询室内机状态地址 */
    private String queryIndoorStateAddr;
    /** 开关机及风速设定地址 */
    private String switchWindSetAddr;
    /** 模式设定地址 */
    private String modeSetAddr;
    /** 温度设定地址 */
    private String temperatureSetAddr;
    /** 多设定地址，包括开关、温度、风速等 */
    private String allSetAddr;
    /** 位置 */
    private String location;
    /** 位置描述 */
    private String locationDesc;
}
