package net.cdsunrise.ztyg.acquisition.ils.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 智能楼宇指令
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
@TableName("t_ils_device_state")
public class IlsDeviceState {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 设备编码
     */
    private String deviceSn;
    /**
     * 楼层
     **/
    private String floor;
    /**
     * 房间
     */
    private String room;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 楼层代码
     */
    private String floorCode;
    /**
     * 房间代码
     */
    private String roomCode;
    /**
     * 设备代码
     */
    private String deviceCode;
    /**
     * 设备状态
     */
    private Integer state;
    /**
     * 更新时间
     */
    private Date updateTime;

}
