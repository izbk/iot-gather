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
@TableName("t_ils_temperature")
public class IlsTemperature {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceCode;
    private Integer temperature;
    private Date createTime;

    public IlsTemperature(EnvironmentResponseVo vo) {
        this.deviceCode = vo.getDeviceCode();
        this.temperature = vo.getTemperature();
        this.createTime = new Date();
    }

}
