package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import lombok.Data;

/**
 * 门禁报文响应
 * @author sh
 * @date 2019-12-11 10:56
 */
@Data
public class AccessMesResp {
    /** 默认事件类通知名称为”OnEventNotify” */
    private String method;
    private Param params;
}
