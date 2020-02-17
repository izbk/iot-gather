package net.cdsunrise.ztyg.acquisition.protocol.rs485.session;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备关联channel对象
 *
 * @author: LiuWei
 * @date: Create in 9:19 2018/08/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RS485Session extends BaseSession {

    private volatile boolean register;

    public static RS485Session buildSession(Channel channel) {
        RS485Session rs485Session = new RS485Session();
        rs485Session.setId(buildId(channel));
        rs485Session.setChannel(channel);
        rs485Session.setRegister(true);
        return rs485Session;
    }

    public static RS485Session buildSession(String deviceIdentifier, Channel channel) {
        RS485Session rs485Session = buildSession(channel);
        rs485Session.setRegister(true);
        rs485Session.setDeviceIdentifier(deviceIdentifier);
        return rs485Session;
    }
}
