package net.cdsunrise.ztyg.acquisition.protocol.tcp.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author: LiuWei
 * @date: Create in 23:21 2018/12/14
 */
@Data
public class RS485PackageData {

    @JSONField(serialize = false)
    protected Channel channel;
}
