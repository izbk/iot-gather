package net.cdsunrise.ztyg.acquisition.ils.vo;

import lombok.Data;

import java.util.List;

/**
 * 控制指令返回实体
 * @author Binke Zhang
 * @date 2019/11/29 11:53
 */
@Data
public class ControlResponseVo {
    private List<IlsDeviceVo> deviceVoList;
}
