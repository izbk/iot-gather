package net.cdsunrise.ztyg.acquisition.ils.component;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.mapper.BaseInfoMapper;
import net.cdsunrise.ztyg.acquisition.common.enums.DeviceTypeEnum;
import net.cdsunrise.ztyg.acquisition.common.enums.SystemTypeEnum;
import net.cdsunrise.ztyg.acquisition.ils.domain.*;
import net.cdsunrise.ztyg.acquisition.ils.mapper.*;
import net.cdsunrise.ztyg.acquisition.ils.service.DataProcessService;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.protocol.tcp.component.RS485Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时监控服务状态
 * @author Binke Zhang
 * @date 2019/9/19 20:16
 */
@Slf4j
@Component
public class GatherEnvDataTask {

    private final CommonCache<String, List<BaseInfo>> sensorCache = CommonCache.getInstance();
    private static final String ILS_DEVICE = "ILS_DEVICE";

    @Autowired
    private BaseInfoMapper baseInfoMapper;
    @Autowired
    private IlsTemperatureMapper temperatureMapper;
    @Autowired
    private IlsHumidityMapper humidityMapper;
    @Autowired
    private IlsPm25Mapper pm25Mapper;
    @Autowired
    private IlsThCurveMapper thCurveMapper;
    @Autowired
    private IlsPmCurveMapper pmCurveMapper;


    @Autowired
    private RS485Component component;
    @Autowired
    private DataProcessService dataProcessService;
    @ConditionalOnProperty(value = "schedule.enabled", havingValue = "true")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void getDeviceState(){
        // 获取照明设备最新状态
        component.sendForIls(dataProcessService.getGlobalSearchCommand());
    }

    @ConditionalOnProperty(value = "schedule.enabled", havingValue = "true")
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduled(){
        List<BaseInfo> list = getIlsDevices();
        Date date = Calendar.getInstance().getTime();
        for (BaseInfo b: list) {
            /**
             * 温湿度曲线数据
             */
            IlsThCurve thCurve = new IlsThCurve();
            thCurve.setCreateTime(date);
            thCurve.setDeviceCode(b.getDeviceCode());
            List<IlsTemperature> temperatures = temperatureMapper.selectList(new QueryWrapper<IlsTemperature>().lambda()
                    .eq(IlsTemperature::getDeviceCode,b.getDeviceCode())
                    .gt(IlsTemperature::getCreateTime, DateUtil.offsetMinute(date,5))
            );
            if(CollectionUtil.isNotEmpty(temperatures)){
                thCurve.setTemperature(temperatures.get(0).getTemperature());
            }
            List<IlsHumidity> humidities = humidityMapper.selectList(new QueryWrapper<IlsHumidity>().lambda()
                    .eq(IlsHumidity::getDeviceCode,b.getDeviceCode())
                    .gt(IlsHumidity::getCreateTime, DateUtil.offsetMinute(date,5))
            );
            if(CollectionUtil.isNotEmpty(humidities)){
                thCurve.setHumidity(humidities.get(0).getHumidity());
            }
            thCurveMapper.insert(thCurve);

            /**
             * pm2.5曲线数据
             */
            List<IlsPm25> pm25s = pm25Mapper.selectList(new QueryWrapper<IlsPm25>().lambda()
                    .eq(IlsPm25::getDeviceCode,b.getDeviceCode())
                    .gt(IlsPm25::getCreateTime, DateUtil.offsetMinute(date,2))
            );
            if(CollectionUtil.isNotEmpty(pm25s)){
                IlsPmCurve pmCurve = new IlsPmCurve();
                pmCurve.setDeviceCode(b.getDeviceCode());
                pmCurve.setCreateTime(date);
                thCurve.setHumidity(pm25s.get(0).getPm25());
                pmCurveMapper.insert(pmCurve);
            }
        }
    }
    /**
     * 获取照明设备信息
     * @return
     */
    private List<BaseInfo> getIlsDevices(){
        List<BaseInfo> list = sensorCache.get(ILS_DEVICE);
        if(list == null || list.size() == 0){
            list = baseInfoMapper.selectList(new QueryWrapper<BaseInfo>().lambda()
                    .eq(BaseInfo::getSystemType, SystemTypeEnum.ZNZM.getCode())
                    .in(BaseInfo::getDeviceType, Arrays.asList(DeviceTypeEnum.ZM.getCode())
            ));
            sensorCache.put(ILS_DEVICE,list);
        }
        return list;
    }

}