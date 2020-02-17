package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import lombok.Data;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-11 10:57
 */
@Data
public class Param {
    /** 事件分类, 门禁事件默认为”event_acs”，长度36 */
    private String ability;
    private List<Event> events;
    /** 采用ISO8601时间格式，长度32 */
    private String sendTime;
}
