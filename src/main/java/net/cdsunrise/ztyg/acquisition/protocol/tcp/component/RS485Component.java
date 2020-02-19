package net.cdsunrise.ztyg.acquisition.protocol.tcp.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.client.handler.SyncClientHandler;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.utils.CodecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Binke Zhang
 * @date 2019/11/20 15:26
 */
@Slf4j
@Component
public class RS485Component {
    @Resource
    private Bootstrap rs485BootStrap;
    @Resource
    private Bootstrap vrvBootStrap;

    @Value("${gather.vrv.ip}")
    private String vrvIp;
    @Value("${gather.vrv.port}")
    private String vrvPort;

    @Value("${gather.ils.ip}")
    private String ilsIp;
    @Value("${gather.ils.port}")
    private String ilsPort;

    @Autowired
    private SyncClientHandler syncClientHandler;

    /**
     *  照明系统
     * @param msg
     * @return
     */
    public RS485Msg sendForIls(RS485Msg msg){
        return send2(msg,ilsIp,Integer.valueOf(ilsPort));
    }

    public void connect(Bootstrap bootstrap, String host, Integer port) {
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.channel().closeFuture().addListener((r) -> {
                System.out.println("执行shutdown");
            });
    }

    @SuppressWarnings("Duplicates")
    private RS485Msg send2(RS485Msg msg, String ip, Integer port){
        ChannelHandlerContext ctx = syncClientHandler.getCtx();
        if(ctx == null){
            connect(rs485BootStrap,ip,port);
            ctx = syncClientHandler.getCtx();
            if(ctx == null){
                throw new BusinessException(ExceptionEnum.EXECUTE_ERROR);
            }
        }
        ByteBuf heapBuf = CodecUtils.getByteBuf(msg);
        // 发送数据
        ChannelFuture channelPromise = syncClientHandler.sendMessage(heapBuf);
        log.info(">>>>RS485Client请求设备数据:{}",msg.toString());
        try {
            channelPromise.await(2000L,TimeUnit.MILLISECONDS);
        } catch (Exception e) {}
        return syncClientHandler.getData();
    }

    public void initSocketConnect(){
        connect(rs485BootStrap,ilsIp,Integer.valueOf(ilsPort));
        connect(vrvBootStrap,vrvIp,Integer.valueOf(vrvPort));
    }
}
