package net.cdsunrise.ztyg.acquisition.protocol.rs485.session;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.Channel;
import lombok.Data;
import net.cdsunrise.ztyg.acquisition.vrv.domain.dto.DeviceDTO;

import java.io.Serializable;


/**
 * 通用session
 * 设备与channel的关联信息
 *
 * @author
 * @date: Create in 15:30 2018/09/20
 */
@Data
public abstract class BaseSession implements Serializable {

    /**
     * channel long id
     */
    protected String id;
    /***
     * 设备标识
     */
    protected String deviceIdentifier;

    /**
     * channel
     */
    @JSONField(serialize = false)
    protected Channel channel;
    /**
     * 创建时间
     */
    protected Long createTime;

    /**
     * 认证标志
     */
    protected boolean authenticated = false;

    public static String buildId(Channel channel) {
        return channel.id().asLongText();
    }
}
