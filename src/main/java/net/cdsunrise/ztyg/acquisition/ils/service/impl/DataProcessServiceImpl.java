package net.cdsunrise.ztyg.acquisition.ils.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.domain.DataDictionary;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.base.mapper.DataDictionaryMapper;
import net.cdsunrise.ztyg.acquisition.common.enums.DeviceTypeEnum;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.enums.SystemTypeEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.ils.constants.CacheKey;
import net.cdsunrise.ztyg.acquisition.ils.domain.*;
import net.cdsunrise.ztyg.acquisition.ils.enums.CommandTypeEnum;
import net.cdsunrise.ztyg.acquisition.ils.enums.EnvironmentTypeEnum;
import net.cdsunrise.ztyg.acquisition.ils.enums.StateEnum;
import net.cdsunrise.ztyg.acquisition.ils.mapper.*;
import net.cdsunrise.ztyg.acquisition.ils.service.DataProcessService;
import net.cdsunrise.ztyg.acquisition.ils.vo.EnvironmentResponseVo;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsRealtimeData;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.constant.ScaleSystem;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.msg.RS485Msg;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Binke Zhang
 * @date 2019/11/29 14:30
 */
@Service
public class DataProcessServiceImpl implements DataProcessService {

    @Autowired
    private BaseInfoMapper baseInfoMapper;
    @Autowired
    private IlsCommandMapper ilsCommandMapper;
    @Autowired
    private IlsDeviceStateMapper ilsDeviceStateMapper;
    @Autowired
    private IlsTemperatureMapper temperatureMapper;
    @Autowired
    private IlsHumidityMapper humidityMapper;
    @Autowired
    private IlsPm25Mapper pm25Mapper;
    @Autowired
    private DataDictionaryMapper dataDictionaryMapper;

    private final CommonCache<String, EnvironmentResponseVo> environmentResponseVoCache = CommonCache.getInstance();
    private final CommonCache<String, List<BaseInfo>> deviceCache = CommonCache.getInstance();
    private final CommonCache<String, List<IlsDeviceState>> deviceStateCache = CommonCache.getInstance();
    private final CommonCache<String, Map<String,String>> dataDictionaryCache = CommonCache.getInstance();

    /**
     * 楼层、房间数据字典
     */
    private static final String ILS_FLOOR = "ILS_FLOOR";
    private static final String ILS_ROOM = "ILS_ROOM";
    private static final String ILS_CODE_KEY = "ILS_CODE_KEY";
    private static final String ILS_NAME_KEY = "ILS_NAME_KEY";
    private static final String NO_DELETED = "0";
    private static final String DEFAULT_VALUE = "00";
    private static final Integer SEARCH_GLOBAL = 1001;
    private static final Integer SYSTEM_LIGHT_ON = 4001;
    private static final Integer SYSTEM_LIGHT_OFF = 4002;
    private static final Integer FLOOR_LIGHT_ON = 4003;
    private static final Integer FLOOR_LIGHT_OFF = 4004;
    @Override
    public RS485Msg processCommand(String deviceCode, Integer command) {
        BaseInfo baseInfo =  baseInfoMapper.selectOne(new QueryWrapper<BaseInfo>().lambda()
                .eq(BaseInfo::getDeviceCode,deviceCode)
        );
        IlsCommand ilsCommand =  ilsCommandMapper.selectOne(new QueryWrapper<IlsCommand>().lambda()
                .eq(IlsCommand::getCommand,command)
        );
        if (baseInfo == null){
            throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备信息失败");
        }
        if (ilsCommand == null){
            throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取照明指令失败");
        }
        Integer type = ilsCommand.getType();
        RS485Msg msg = null;
        // 获取设备的楼层及房间信息
        Short floor = baseInfo.getFloor();
        String room = baseInfo.getRoom();
        // 获取楼层及房间的数据字典
        Map<String,String> floorNameMap = getDictCache(ILS_FLOOR,ILS_NAME_KEY);
        Map<String,String> roomNameMap = getDictCache(ILS_ROOM,ILS_NAME_KEY);
        // 将floor、room转换为对应的代码
        String floorCode;
        String roomCode;
        if(isSystemCommand(command)){
            floorCode = DEFAULT_VALUE;
            roomCode = DEFAULT_VALUE;
        }else if(isFloorLightCommand(command)){
            floorCode = floorNameMap.get(floor.toString());
            roomCode =DEFAULT_VALUE;
        }else{
            floorCode = floorNameMap.get(floor.toString());
            roomCode = roomNameMap.get(room);
        }
        // 构建请求参数
        switch(CommandTypeEnum.getCommandTypeEnumMap().get(type)){
            case LAMPLIGHT:
                msg = new RS485Msg(ilsCommand.getAddress(), ilsCommand.getCode(), floorCode, roomCode,
                        ilsCommand.getDeviceSn(), ilsCommand.getD6(), ilsCommand.getD7(), null);
                break;
            case SCENE:
                msg = new RS485Msg(ilsCommand.getAddress(),floorCode,roomCode,ilsCommand.getCode(),
                        ilsCommand.getDeviceSn(),ilsCommand.getD6(),ilsCommand.getD7(),null);
                if(checkSpecialScene(command)){
                    msg.setFloorCode(DEFAULT_VALUE);
                    msg.setRoomCode(DEFAULT_VALUE);
                }
                break;
            default:
        }
        return  calcAndSetVerifyCode(msg);
    }

    /**
     * 获取全局搜索指令
     * @return
     */
    @Override
    public RS485Msg getGlobalSearchCommand(){
        RS485Msg rs485Msg = new RS485Msg("F3","D8",DEFAULT_VALUE,DEFAULT_VALUE,DEFAULT_VALUE,DEFAULT_VALUE,DEFAULT_VALUE,DEFAULT_VALUE);
        return calcAndSetVerifyCode(rs485Msg);
    }

    @Override
    public void processEnvironmentMsg(RS485Msg rs485Msg) {
        /**
         * 获取设备信息
         */
        // 获取楼层及房间的数据字典
        Map<String,String> floorCodeMap = getDictCache(ILS_FLOOR,ILS_CODE_KEY);
        Map<String,String> roomCodeMap = getDictCache(ILS_ROOM,ILS_CODE_KEY);
        Short floor  = 0;
        String room = "";
        // 楼层
        String floorCode = rs485Msg.getFloorCode();
        if(StringUtils.isNotEmpty(floorCode)){
            floor = Short.valueOf(floorCodeMap.get(floorCode));
        }
        // 房间
        String roomCode = rs485Msg.getRoomCode();
        if(StringUtils.isNotEmpty(roomCode)){
            // 过滤卫生间的数据
            if("05".equals(roomCode)){
                return;
            }
            room = roomCodeMap.get(roomCode);
            if(StringUtils.isEmpty(room)){
                return;
            }
        }

        // 获取照明控制面板
        final String filterFloor = floor.toString();
        final String filterRoom = room;
        List<BaseInfo> baseInfos = deviceCache.get(CacheKey.ILS_DEVICE_INFO).stream().filter(d->d.getFloor().equals(filterFloor)&&d.getRoom().equals(filterRoom)).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(baseInfos)){
            throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"获取设备信息出错");
        }
        BaseInfo baseInfo = baseInfos.get(0);

        /**
         * 封装并缓存environmentResponseVo
         */
        String deviceCode = baseInfo.getDeviceCode();
        EnvironmentResponseVo vo;
        EnvironmentResponseVo cacheEnvironmentResponseVo = environmentResponseVoCache.get(deviceCode);
        if(cacheEnvironmentResponseVo == null){
            vo = new EnvironmentResponseVo();
        }else{
            vo = cacheEnvironmentResponseVo;
        }
        vo.setFloor(floor);
        vo.setRoom(room);
        vo.setDeviceCode(deviceCode);
        vo.setUptime(baseInfo.getUptime());
        String data = rs485Msg.getDeviceSn();
        String typeCode = rs485Msg.getD6();
        Date createTime = Calendar.getInstance().getTime();
        if(StringUtils.isNotEmpty(data)&&StringUtils.isNotEmpty(typeCode)){
            Integer type = Integer.parseInt(typeCode, ScaleSystem.HEX);
            // 数据位
            EnvironmentTypeEnum typeEnum = EnvironmentTypeEnum.getEnvironmentTypeEnumMap().get(type);
            if(typeEnum == null){
                return;
            }
            switch(typeEnum){
                case TEMPERATURE:
                    vo.setTemperature(Integer.parseInt(data,ScaleSystem.HEX));
                    /**
                     * 记录原始数据
                     */
                    IlsTemperature temperature = new IlsTemperature(vo);
                    temperature.setDeviceCode(deviceCode);
                    temperature.setCreateTime(createTime);
                    temperatureMapper.insert(temperature);
                    break;
                case HUMIDITY:
                    vo.setHumidity(Integer.parseInt(data,ScaleSystem.HEX));
                    IlsHumidity humidity = new IlsHumidity(vo);
                    humidity.setDeviceCode(deviceCode);
                    humidity.setCreateTime(createTime);
                    humidityMapper.insert(humidity);
                    break;
                case PM25:
                    vo.setPm25(Integer.parseInt(data,ScaleSystem.HEX)*3);
                    IlsPm25 pm25 = new IlsPm25(vo);
                    pm25.setDeviceCode(deviceCode);
                    pm25.setCreateTime(createTime);
                    pm25Mapper.insert(pm25);
                    break;
                default:
            }
        }
        vo.setUpdateTime(createTime);
        environmentResponseVoCache.put(deviceCode,vo);
        return;
    }

    /**
     * 更新设备状态
     * @param respMsg
     */
    @Override
    public void updateDeviceState(RS485Msg respMsg){
        List<IlsDeviceState> list = deviceStateCache.get(CacheKey.ILS_DEVICE_STATE);
        list = reloadDeviceStateCache(list);
        String floorCode = respMsg.getFloorCode();
        String roomCode = respMsg.getRoomCode();
        String deviceCode = respMsg.getDeviceSn();
        String d7 = respMsg.getD7();
        list.forEach(d-> {
            if(d.getFloorCode().equals(floorCode)
                    && d.getRoomCode().equals(roomCode)
                    && d.getDeviceCode().equals(deviceCode)){
                d.setState(parseOnOff(d7));
                ilsDeviceStateMapper.updateById(d);
            }
        });
    }

    /**
     * 查询设备状态
     * @param reqMsg
     */
    @Override
    public List<IlsDeviceState> queryDeviceState(RS485Msg reqMsg){
        List<IlsDeviceState> list = deviceStateCache.get(CacheKey.ILS_DEVICE_STATE);
        list = reloadDeviceStateCache(list);
        String floorCode = reqMsg.getFloorCode();
        if(DEFAULT_VALUE.endsWith(floorCode)){
            // 楼层为00返回所有设备
            return list;
        }
        String roomCode = reqMsg.getRoomCode();
        if(DEFAULT_VALUE.equals(roomCode)){
            // 房间为00 返回整楼层设备
            return list.stream().filter(d-> d.getFloorCode().equals(floorCode))
                    .collect(Collectors.toList());
        }
        String deviceCode = reqMsg.getDeviceSn();
        if(DEFAULT_VALUE.equals(deviceCode)){
            // 设备为00 返回整房间设备
            return list.stream().filter(d-> d.getFloorCode().equals(floorCode)
                    && d.getRoomCode().equals(roomCode))
                    .collect(Collectors.toList());
        }
        // 返回单个设备
        return list.stream().filter(d-> d.getFloorCode().equals(floorCode)
                && d.getRoomCode().equals(roomCode)
                && d.getDeviceCode().equals(deviceCode))
                .collect(Collectors.toList());
    }

    private List<IlsDeviceState> reloadDeviceStateCache(List<IlsDeviceState> list) {
        if (CollectionUtil.isEmpty(list)) {
            loadDeviceState();
            list = deviceStateCache.get(CacheKey.ILS_DEVICE_STATE);
        }
        return list;
    }

    /**
     * 查询设备状态[外部接口]
     * @param floor
     * @param room
     */
    @Override
    public List<IlsDeviceState> queryDeviceState(String floor, String room){
        List<IlsDeviceState> list = deviceStateCache.get(CacheKey.ILS_DEVICE_STATE);
        list = reloadDeviceStateCache(list);
        List<IlsDeviceState> resultList = list;
        if(StringUtils.isNotEmpty(floor)){
            resultList = list.stream().filter(d-> d.getFloor().equals(floor))
                    .collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(room)){
            resultList = resultList.stream().filter(d-> d.getRoom().equals(room))
                    .collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     * 查询设备状态[外部接口]
     * @param deviceCode
     */
    @Override
    public List<IlsDeviceState> queryDeviceState(String deviceCode){
        List<IlsDeviceState> list = deviceStateCache.get(CacheKey.ILS_DEVICE_STATE);
        list = reloadDeviceStateCache(list);
        return list.stream().filter(d-> d.getDeviceSn().equals(deviceCode))
                .collect(Collectors.toList());
    }

    /**
     * 查询设备状态
     * @param deviceCode
     */
    @Override
    public IlsRealtimeData queryEnvironmentState(String deviceCode){
        EnvironmentResponseVo vo = environmentResponseVoCache.get(deviceCode);
        IlsRealtimeData realtimeData = new IlsRealtimeData();
        if(vo == null){
            vo = new EnvironmentResponseVo();
            BaseInfo baseInfo = baseInfoMapper.selectOne(new QueryWrapper<BaseInfo>().lambda().eq(BaseInfo::getDeviceCode,deviceCode));
            vo.setFloor(baseInfo.getFloor());
            vo.setRoom(baseInfo.getRoom());
            vo.setUptime(baseInfo.getUptime());
            IlsTemperature temperature = temperatureMapper.selectOne(new QueryWrapper<IlsTemperature>().lambda()
                    .eq(IlsTemperature::getDeviceCode,deviceCode)
                    .orderByDesc(IlsTemperature::getId)
                    .last(" limit 1"));
            vo.setUpdateTime(temperature.getCreateTime());
            vo.setTemperature(temperature.getTemperature());
            IlsHumidity humidity = humidityMapper.selectOne(new QueryWrapper<IlsHumidity>().lambda()
                    .eq(IlsHumidity::getDeviceCode,deviceCode)
                    .orderByDesc(IlsHumidity::getId)
                    .last(" limit 1"));
            vo.setHumidity(humidity.getHumidity());
            IlsPm25 pm25 = pm25Mapper.selectOne(new QueryWrapper<IlsPm25>().lambda()
                    .eq(IlsPm25::getDeviceCode,deviceCode)
                    .orderByDesc(IlsPm25::getId)
                    .last(" limit 1"));
            vo.setPm25(pm25.getPm25());
            environmentResponseVoCache.put(deviceCode,vo);
        }
        loadIlsRealtimeData(realtimeData,vo);
        realtimeData.setCommunicateState(StateEnum.NORMAL.getCode());
        realtimeData.setRunningState(StateEnum.NORMAL.getCode());
        realtimeData.setUpdateTime(vo.getUptime());
        realtimeData.setRunningTime(DateUtil.between(vo.getUptime(),vo.getUpdateTime(), DateUnit.HOUR));
        return realtimeData;
    }

    private void loadIlsRealtimeData(IlsRealtimeData data,EnvironmentResponseVo vo) {
        data.setDeviceCode(vo.getDeviceCode());
        data.setFloor(vo.getFloor());
        data.setRoom(vo.getRoom());
        data.setTemperature(vo.getTemperature());
        data.setHumidity(vo.getHumidity());
        data.setPm25(vo.getPm25());
        data.setUpdateTime(vo.getUpdateTime());
    }

    @Override
    public void loadDeviceState(){
        deviceStateCache.put(CacheKey.ILS_DEVICE_STATE,ilsDeviceStateMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public void loadDataDictionary(){
        // 加载楼层数据字典数据
        getDictCache(ILS_FLOOR,ILS_NAME_KEY);
        getDictCache(ILS_FLOOR,ILS_CODE_KEY);
        getDictCache(ILS_ROOM,ILS_NAME_KEY);
        getDictCache(ILS_ROOM,ILS_CODE_KEY);
    }

    @Override
    public void loadILSCache(){
        // 预热照明设备缓存
        loadDeviceState();
        // 加载照明数据字典
        loadDataDictionary();
    }

    /**
     * 加载指定的数据字典
     * @param parentCode
     * @param keyType
     * @return
     */
    private Map<String,String> getDictCache(String parentCode,String keyType){
        Map<String,String> map = dataDictionaryCache.get(parentCode+keyType);
        if(map == null || map.size() == 0){
            // 加载房间数据字典数据
            List<DataDictionary> list = dataDictionaryMapper.selectList(
                    new QueryWrapper<DataDictionary>().lambda()
                            .eq(DataDictionary::getParentCode,parentCode)
                            .eq(DataDictionary::getIsDeleted,NO_DELETED));
            map = new HashMap<>(list.size());

            for (DataDictionary d : list) {
                if(ILS_CODE_KEY.equals(keyType)){
                    map.put(d.getCode(),d.getName());
                }else{
                    map.put(d.getName(),d.getCode());
                }
            }
        }
        dataDictionaryCache.put(parentCode+keyType,map);
        return map;
    }

    /**
     * 获取照明设备列表
     * @return
     */
    private List<BaseInfo> getDeviceCache(){
        List<BaseInfo> deviceInfo = deviceCache.get(CacheKey.ILS_DEVICE_INFO);
        if(deviceInfo == null || deviceInfo.size() == 0){
            deviceInfo = baseInfoMapper.selectList(new QueryWrapper<BaseInfo>().lambda()
                    .eq(BaseInfo::getSystemType, SystemTypeEnum.ZNZM.getCode())
                    .eq(BaseInfo::getDeviceType, DeviceTypeEnum.ZM.getCode())
            );
            deviceCache.put(CacheKey.ILS_DEVICE_INFO,deviceInfo);
        }
        return deviceInfo;
    }

    /**
     * 计算并设置校验码
     * @param msg
     * @return
     */
    private RS485Msg calcAndSetVerifyCode(RS485Msg msg) {
        int[] ints = CodecUtils.convertMsgToArray(msg);
        int verify = CodecUtils.getCHVerify(ints);
        msg.setVerifyCode(Integer.toHexString(verify).toUpperCase());
        return msg;
    }

    /**
     * 特殊场景无需楼层和房间编码
     * @param command
     * @return
     */
    private boolean checkSpecialScene(Integer command){
        return Arrays.asList(10001,10002,10003).contains(command);
    }

    /**
     * 解析开关位
     * @param d7
     * @return
     */
    private Integer parseOnOff(String d7){
        String binStr = hexStrToBinStr(d7);
        return Integer.valueOf(binStr.substring(binStr.length()-1));
    }

    /**
     * 是否灯光控制系统级指令
     * @param command
     * @return
     */
    private boolean isSystemCommand(Integer command){
        return SEARCH_GLOBAL.equals(command)
                || SYSTEM_LIGHT_ON.equals(command)
                || SYSTEM_LIGHT_OFF.equals(command)
                ;
    }

    /**
     * 是否灯光控制楼层级指令
     * @param command
     * @return
     */
    private boolean isFloorLightCommand(Integer command){return FLOOR_LIGHT_ON.equals(command) || FLOOR_LIGHT_OFF.equals(command); }

    /**
     * 16进制转二进制字符串，多位以，隔开
     */
    public static String hexStrToBinStr(String hex) {
        byte[] b = HexUtil.decodeHex(hex);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public static void main(String[] args){
        String d7 = "1A";
        String bS = hexStrToBinStr(d7);
        System.out.println(bS);
        System.out.println(bS.substring(bS.length()-1));
    }
}
