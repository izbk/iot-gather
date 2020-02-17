package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;

/**
 * 设备状态查询
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
public class QueryDeviceStateVo {
    /**
     * 楼层
     **/
    private String floor;
    /**
     * 房间
     */
    private String room;
    /**
     * 设备代码
     */
    private String deviceCode;

}
