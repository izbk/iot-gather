package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsDeviceState;

import java.util.Date;

/**
 * 智能楼宇指令
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
public class IlsDeviceStateVo {
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
     * 设备状态
     */
    private Integer state;
    /**
     * 更新时间
     */
    private Date updateTime;

    public IlsDeviceStateVo(IlsDeviceState deviceState) {
        this.floor = deviceState.getFloor();
        this.room = deviceState.getRoom();
        this.deviceName = deviceState.getDeviceName();
        this.state = deviceState.getState();
        this.updateTime = deviceState.getUpdateTime();
    }
}
