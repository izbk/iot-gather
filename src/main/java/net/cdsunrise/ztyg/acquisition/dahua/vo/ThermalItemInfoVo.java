package net.cdsunrise.ztyg.acquisition.dahua.vo;

import lombok.Data;

/**
 * 测温项信息
 * @author Binke Zhang
 * @date 2020/2/18 16:57
 */
@Data
public class ThermalItemInfoVo {
    /**
     * 测温类型 0:未知 1:点 2:线 3:区域
      */
    private String meterType;
    /**
     *  温度单位 0:未知 1:摄氏度 2:华氏度
     */
    private String temperUnit;
    /**
     * 点的温度或者平均温度
     */
    private String temperAver;
    /**
     * 最高温度
     */
    private String temperMax;
    /**
     * 最低温度
     */
    private String temperMin;
    /**
     * 中间温度值
     */
    private String temperMid;
    /**
     * 标准方差值
     */
    private String temperStd;
}
