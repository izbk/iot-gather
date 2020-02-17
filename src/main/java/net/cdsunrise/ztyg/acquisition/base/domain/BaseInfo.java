package net.cdsunrise.ztyg.acquisition.base.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author sh
 * @date 2019-11-29 10:20
 */
@Data
@TableName("t_base_info")
public class BaseInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 设备编码 */
    private String deviceCode;
    /** 设备名称 */
    private String deviceName;
    /** 设备型号 */
    private String deviceModel;
    /** 工作电压 */
    private String operatingVoltage;
    /** 工作电流 */
    private String operatingCurrent;
    /** 功耗 */
    private String powerDissipation;
    /** 信号输出 */
    private String signalOutput;
    /** 外观尺寸 */
    private String appearanceSize;
    /** 生产商 */
    private String manufacturer;
    /** 生产商联系人 */
    private String maContactPersonnel;
    /** 生产商联系方式 */
    private String maContactPhone;
    /** 供货商联系人 */
    private String suContactPersonnel;
    /** 供货商联系方式 */
    private String suContactPhone;
    /** 供货商 */
    private String supplier;
    /** 技术参数 */
    private String technicalParam;
    /** 设备材质 */
    private String deviceMaterial;
    /** 安装位置 */
    private String installLocation;
    /** 图片地址 */
    private String pictureUrl;
    /** 安装方式 */
    private String installMethod;
    /** 资产编号 */
    private String assetNum;
    /** 楼层：-1，1，2，3 */
    private Short floor;
    /** 设备类型 */
    private Short deviceType;
    /** 系统类型 */
    private Short systemType;
    /** 房间 */
    private String room;
    /** 投运时间 */
    private Date uptime;
}
