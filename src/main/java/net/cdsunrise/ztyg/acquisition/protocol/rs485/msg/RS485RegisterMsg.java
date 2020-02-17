package net.cdsunrise.ztyg.acquisition.protocol.rs485.msg;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.vo.RS485PackageData;

/**
 * @author: LiuWei
 * @date: Create in 23:12 2018/12/14
 */
@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class RS485RegisterMsg extends RS485PackageData {

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
    private String deviceSn;

    /**
     * 获取设备标识符
     * @return
     */
    public String getDeviceIdentifier(){
        return StrUtil.concat(false,floorCode,"_",roomCode,"_", deviceSn);
    }

}
