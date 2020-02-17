package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author sh
 * @date 2019-12-11 11:08
 */
@Data
public class EventData {
    /** 人员身份证信息 */
    @JsonProperty("ExtEventIdentityCardInfo")
    private ExtEventIdentityCardInfo extEventIdentityCardInfo;
    /** 人员通道号 */
    @JsonProperty("ExtAccessChannel")
    private Integer extAccessChannel;
    /** 报警输入/防区通道 */
    @JsonProperty("ExtEventAlarmInID")
    private Integer extEventAlarmInID;
    /** 报警输出通道 */
    @JsonProperty("ExtEventAlarmOutID")
    private Integer extEventAlarmOutID;
    /** 卡号 */
    @JsonProperty("ExtEventCardNo")
    private Integer extEventCardNo;
    /** 事件输入通道 */
    @JsonProperty("ExtEventCaseID")
    private Integer extEventCaseID;
    /** 事件类型代码 */
    @JsonProperty("ExtEventCode")
    private Integer extEventCode;
    /** 通道事件信息 */
    @JsonProperty("ExtEventCustomerNumInfo")
    private ExtEventCustomerNumInfo extEventCustomerNumInfo;
    /** 门编号 */
    @JsonProperty("ExtEventDoorID")
    private Integer extEventDoorID;
    /** 身份证图片URL, 长度200 */
    @JsonProperty("ExtEventIDCardPictureURL")
    private String extEventIDCardPictureURL;
    /**
     * 进出方向
     * 进出类型
     * 1：进
     * 0：出
     * -1:未知
     * 要求：进门读卡器拨码设置为1，出门读卡器拨码设置为2
     * */
    @JsonProperty("ExtEventInOut")
    private Integer extEventInOut;
    /** 就地控制器id */
    @JsonProperty("ExtEventLocalControllerID")
    private Integer extEventLocalControllerID;
    /** 主设备拨码 */
    @JsonProperty("ExtEventMainDevID")
    private Integer extEventMainDevID;
    /** 图片的url，长度32 */
    @JsonProperty("ExtEventPictureURL")
    private String extEventPictureURL;
    /** 读卡器id */
    @JsonProperty("ExtEventReaderID")
    private Integer extEventReaderID;
    /**
     * 读卡器类别
     * 0-无效
     * 1-IC读卡器
     * 2-身份证读卡器
     * 3-二维码读卡器
     * 4-指纹头
     * */
    @JsonProperty("ExtEventReaderKind")
    private Integer extEventReaderKind;
    /** 报告上传通道 */
    @JsonProperty("ExtEventReportChannel")
    private Integer extEventReportChannel;
    /** 群组编号 */
    @JsonProperty("ExtEventRoleID")
    private Integer extEventRoleID;
    /** 分控制器硬件ID */
    @JsonProperty("ExtEventSubDevID")
    private Integer extEventSubDevID;
    /** 刷卡次数 */
    @JsonProperty("ExtEventSwipNum")
    private Integer extEventSwipNum;
    /**
     * 事件类型：如普通门禁事件为0,身份证信息事件为1，客流量统计为2
     * */
    @JsonProperty("ExtEventType")
    private Integer extEventType;
    /** 多重认证序号 */
    @JsonProperty("ExtEventVerifyID")
    private Integer extEventVerifyID;
    /**
     * 白名单单号
     * 1-8，为0无效
     * */
    @JsonProperty("ExtEventWhiteListNo")
    private Integer extEventWhiteListNo;
    /** 事件上报驱动的时间 */
    @JsonProperty("ExtReceiveTime")
    private String extReceiveTime;
    /** 事件流水号 */
    @JsonProperty("Seq")
    private Integer seq;
    /** 图片服务器唯一编码 */
    private String svrIndexCode;
}
