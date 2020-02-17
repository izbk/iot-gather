package net.cdsunrise.ztyg.acquisition.protocol.modbus.config;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import net.cdsunrise.ztyg.acquisition.common.enums.ProtocolEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author Binke Zhang
 * @date 2019/11/12 10:50
 */
@Configuration
public class ModbusConfig {
    @Bean
    public ModbusFactory modbusFactory(){
        return new ModbusFactory();
    }

    @Bean
    public HashMap<String, ModbusMaster> masterMap() {
        return new HashMap<>();
    }

    @Autowired
    @Qualifier("masterMap")
    private HashMap<String,ModbusMaster> masterMap;

    @Autowired
    private ModbusFactory modbusFactory;

    /**
     * @Title initMaster
     * @Description: 通过ip获取对应的modbus连接器
     * @params: [ip]
     * @return: com.serotonin.modbus4j.ModbusMaster
     * @author: caiwei
     * @date: 2019/5/1 13:58
     */
    public ModbusMaster initMaster(String ip, Integer port) {
        String key = getKey(ip, port, ProtocolEnum.TCP);
        ModbusMaster modbusMaster = masterMap.get(key);
        if(modbusMaster == null) {
            setMaster(ip, port,ProtocolEnum.TCP);
            modbusMaster = masterMap.get(key);
        }
        return modbusMaster;
    }

    /**
     * @Title setMaster
     * @Description: 设置ip对应的modbus连接器
     * @params: [ip, port]
     * @return: void
     * @author: caiwei
     * @date: 2019/5/1 13:59
     */
    private void setMaster(String ip, Integer port,ProtocolEnum protocol) {
        ModbusMaster master = null;
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);
        switch(protocol){
            case RTU:
                break;
            case ASCII:
                break;
            case UDP:
                break;
            case TCP:
                master = modbusFactory.createTcpMaster(params,true);
                break;
            default:
        }
        try {
            //设置超时时间
            master.setTimeout(3*1000);
            //设置重连次数
            master.setRetries(3);
            //初始化
            master.init();
        } catch (ModbusInitException e) {
            e.printStackTrace();
        }
        masterMap.put(getKey(ip, port, protocol), master);
    }

    private String getKey(String ip, Integer port, ProtocolEnum protocol){
        return ip+":"+port+":"+protocol;
    }
}
