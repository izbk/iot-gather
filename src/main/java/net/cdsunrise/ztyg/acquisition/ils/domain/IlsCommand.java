package net.cdsunrise.ztyg.acquisition.ils.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 智能楼宇指令
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
@TableName("t_ils_command")
public class IlsCommand {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 指令
     **/
    private Integer command;
    /**
     * 地址
     */
    private String address;
    /**
     * 指令代码
     */
    private String code;
    /**
     * 设备代码
     */
    private String deviceSn;
    /**
     * 数据位
     */
    private String d6;
    /**
     * 数据位
     */
    private String d7;
    /**
     * 指令类型
     */
    private Integer type;
    /**
     * 指令说明
     */
    private String remark;

}
