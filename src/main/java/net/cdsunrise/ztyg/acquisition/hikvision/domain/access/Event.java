package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import lombok.Data;

/**
 * @author sh
 * @date 2019-12-11 11:00
 */
@Data
public class Event {
    /** 事件详情 */
    private EventData data;
    /** 事件的唯一id，长度32 */
    private String eventId;
    /** 事件类型码 */
    private Integer eventType;
    /** 事件类型名称，长度64 */
    private String eventTypeName;
    /** 事件产生时间, 采用ISO8601时间格式，长度32 */
    private String happenTime;
    /** 子类indexcode，长度32 */
    private String srcIndex;
    /** 设备名称，长度32 */
    private String srcName;
    /** 资源indexcode，长度32 */
    private String srcParentIndex;
    /** 资源类型，长度32 */
    private String srcType;
    /**
     * 事件状态
     * 0-瞬时
     * 1-开始
     * 2-停止
     * 3-事件脉冲
     * 4-事件联动结果更新
     * 5-异步图片上传
     * */
    private Integer status;
    /** 脉冲超时时间, 单位：秒，瞬时事件此字段填0 */
    private Integer timeout;
}
