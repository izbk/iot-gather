package net.cdsunrise.ztyg.acquisition.protocol.modbus.component;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.*;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.protocol.modbus.config.ModbusConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Binke Zhang
 * @date 2019/11/12 11:32
 */
@Slf4j
@Component
public class ModbusTcpComponent {

    @Autowired
    private ModbusConfig modbusConfig;

    /**
     * 获取主站master
     * @return
     */
    public ModbusMaster getMaster(String ip, Integer port){
        return modbusConfig.initMaster(ip,port);
    }

    /**
     * 功能码01  读线圈状态 00001-09999 位操作
     * 读取[01 Coil Status 0x]类型 开关数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param start 开始位置
     * @param len 长度
     * @return
     */
    public boolean[] readCoilStatus(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, len);
            ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
            return response.getBooleanData();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码02 读离散输入状态 10001-19999 位操作
     * 读取[02 Input Status 1x]类型 开关数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param start 开始位置
     * @param len 长度
     * @return
     */
    public boolean[] readDiscreteInputStatus(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
            ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);
            return response.getBooleanData();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null; 
    }

    /**
     * 功能码03 读保持寄存器 40001-49999 字操作
     * 批量读取[03 Holding Register类型 2x]模拟量数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param start 开始位置
     * @param len 长度
     * @return
     */
    public ReadHoldingRegistersResponse readHoldingRegisters(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码04 读输入寄存器 30001-39999 字操作
     * 读取[04 Input Registers 3x]类型 模拟量数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param start 开始位置
     * @param len 长度
     * @return
     */
    public ReadInputRegistersResponse readInputRegisters(ModbusMaster master, int slaveId, int start, int len) {
        try {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) master.send(request);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码03 读保持寄存器 40001-49999 字操作
     * 批量读取[03 Holding Register类型 2x]模拟量数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param offset 偏移量
     * @param dataType 数据类型
     * @return
     */
    public Number readHoldingRegister(ModbusMaster master, int slaveId, int offset, int dataType) {
        try {
            BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
            return master.getValue(loc);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码04 读输入寄存器 30001-39999 字操作
     * 读取[04 Input Registers 3x]类型 模拟量数据
     * @param master 主站master
     * @param slaveId 从站ID
     * @param offset 偏移量
     * @param dataType 数据类型
     * @return
     */
    public Number readInputRegister(ModbusMaster master, int slaveId, int offset, int dataType) {
        try {
            BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
            return master.getValue(loc);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码03 读保持寄存器 40001-49999 字操作
     * 批量读取[03 Holding Register类型 2x]模拟量数据
     * ids,offsets,types必须根据索引一一对应
     * @param master 主站master
     * @param slaveId 从站ID
     * @param ids 数据索引数组
     * @param offsets 偏移量数组
     * @param types 数据类型数组
     * @return
     */
    public BatchResults<Number> batchReadHoldingRegister(ModbusMaster master,int slaveId,int[] ids,int[] offsets,int types[]) {
        try {
            if (checkParam(ids, offsets, types)){
                return null;
            }
            BatchRead<Number> batch = new BatchRead<>();
            for (int i = 0; i <ids.length ; i++) {
                batch.addLocator(ids[i], BaseLocator.holdingRegister(slaveId, offsets[i], types[i]));
            }
            batch.setContiguousRequests(false);
            return master.send(batch);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 功能码04 读输入寄存器 30001-39999 字操作
     * 批量读取[04 Input Registers 3x]类型 模拟量数据
     * ids,offsets,types必须根据索引一一对应
     * @param master 主站master
     * @param slaveId 从站ID
     * @param ids 数据索引数组
     * @param offsets 偏移量数组
     * @param types 数据类型数组
     * @return
     */
    public BatchResults<Number> batchReadInputRegister(ModbusMaster master,int slaveId,int[] ids,int[] offsets,int[] types) {
        try {
            if (checkParam(ids, offsets, types)){
                return null;
            }
            BatchRead<Number> batch = new BatchRead<>();
            for (int i = 0; i <ids.length ; i++) {
                batch.addLocator(ids[i], BaseLocator.inputRegister(slaveId, offsets[i], types[i]));
            }
            batch.setContiguousRequests(false);
            return master.send(batch);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private boolean checkParam(int[] ids, int[] offsets, int[] types) {
        if (ids == null || offsets == null || types == null) {
            return true;
        }
        if (ids.length == 0 || offsets.length == 0 || types.length == 0) {
            return true;
        }
        if (ids.length != offsets.length || ids.length != types.length) {
            return true;
        }
        return false;
    }

    /**
     * 功能码05 写单个线圈 00001-09999 位操作
     *写 [01 Coil Status(0x)]
     * @param master 主站master
     * @param slaveId 从站ID
     * @param offset 偏移量
     * @param value 数据值
     * @return
     */
    public boolean writeCoil(ModbusMaster master,int slaveId, int offset, boolean value) {
        try {
            // 创建请求
            WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, value);
            // 发送请求并获取响应对象
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);
            if (response.isException()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 功能码15 写多个线圈 00001-09999 位操作
     * 写多个[01 Coil Status(0x)]
     * @param master 主站master
     * @param slaveId 从站ID
     * @param startOffset 起始偏移量
     * @param booleans 值数组
     * @return
     */
    public boolean writeCoils(ModbusMaster master,int slaveId, int startOffset, boolean[] booleans) {
        try {
            // 创建请求
            WriteCoilsRequest request = new WriteCoilsRequest(slaveId, startOffset, booleans);
            // 发送请求并获取响应对象
            WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);
            if (response.isException()) {
                log.error(response.getExceptionMessage());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 功能码06 写单个保持寄存器 40001-49999 字操作
     * 写[03 Holding Register(4x)]
     * @param master 主站master
     * @param slaveId 从站ID
     * @param offset 偏移量
     * @param value 数据值
     * @return
     */
    public boolean writeRegister(ModbusMaster master,int slaveId, int offset, short value) {
        try {
            // 创建请求对象
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
            if (response.isException()) {
                log.error(response.getExceptionMessage());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 功能码16 写多个保持寄存器 40001-49999 字操作
     * 写多个[03 Holding Register(4x)] function ID=16
     * @param master 主站master
     * @param slaveId 从站ID
     * @param startOffset 起始偏移量
     * @param shorts 值数组
     * @return
     */
    public boolean writeRegisters(ModbusMaster master,int slaveId, int startOffset, short[] shorts) {
        try {
            // 创建请求对象
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, startOffset, shorts);
            // 发送请求并获取响应对象
            ModbusResponse response = master.send(request);
            if (response.isException()) {
                log.error(response.getExceptionMessage());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 写入保持寄存器模拟量
     * @param master 主站master
     * @param slaveId 从站ID
     * @param offset 偏移量
     * @param value 数字值
     * @param dataType 数据类型
     */
    public void writeHoldingRegister(ModbusMaster master,int slaveId, int offset, Number value, int dataType) {
        try {
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
            master.setValue(locator, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取异常状态
     * @param master
     * @param slaveId
     */
    public byte readExceptionStatus(ModbusMaster master, int slaveId){
        try {
            ReadExceptionStatusRequest request = new ReadExceptionStatusRequest(slaveId);
            ReadExceptionStatusResponse response = (ReadExceptionStatusResponse) master.send(request);
            if (response.isException()) {
                return 0;
            }else{
                return response.getExceptionStatus();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    /**
     * 上报SlaveId
     * @param master
     * @param slaveId
     */
    public String reportSlaveId(ModbusMaster master, int slaveId){
        try {
            ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
            ReportSlaveIdResponse response = (ReportSlaveIdResponse) master.send(request);
            if (!response.isException()) {
                return Arrays.toString(response.getData());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }
}