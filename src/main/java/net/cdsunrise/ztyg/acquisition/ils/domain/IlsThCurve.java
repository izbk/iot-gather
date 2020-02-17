package net.cdsunrise.ztyg.acquisition.ils.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 环境参数状态
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ils_th_curve")
public class IlsThCurve {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceCode;
    private Integer temperature = 0;
    private Integer humidity = 0;
    private Date createTime;
}
