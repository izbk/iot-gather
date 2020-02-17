package net.cdsunrise.ztyg.acquisition.ils.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.cdsunrise.ztyg.acquisition.ils.vo.EnvironmentResponseVo;

import java.util.Date;

/**
 * 环境参数状态
 * @author Binke Zhang
 * @date 2019-11-27 10:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ils_pm25")
public class IlsPm25 {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceCode;
    private Integer pm25;
    private Date createTime;

    public IlsPm25(EnvironmentResponseVo vo) {
        this.deviceCode = vo.getDeviceCode();
        this.pm25 = vo.getPm25();
        this.createTime = new Date();
    }

}
