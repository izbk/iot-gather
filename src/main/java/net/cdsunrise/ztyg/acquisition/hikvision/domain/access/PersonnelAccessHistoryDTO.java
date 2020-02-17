package net.cdsunrise.ztyg.acquisition.hikvision.domain.access;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sh
 * @date 2019-12-13 14:36
 */
@Data
@TableName("t_personnel_access_history")
public class PersonnelAccessHistoryDTO {
    /** 主键id */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 姓名 */
    private String name;
    /** 工号 */
    private String jobNo;
    /** 卡号 */
    private Integer carNo;
    /** 人员id */
    private String personId;
    /** 所属组织 */
    private String orgPathName;
    /** 门禁点 */
    private String srcName;
    /** 出入 */
    private String access;
    /** 事件类型码 */
    private Integer eventType;
    /** 事件类型名称 */
    private String eventTypeName;
    /** 抓拍图片url */
    private String extEventPictureUrl;
    /** 大头贴url */
    private String photoUrl;
    /** 事件产生时间 */
    private LocalDateTime happenTime;
    /** 创建时间 */
    private LocalDateTime createTime;
}
