package net.cdsunrise.ztyg.acquisition.ils.controller;

import cn.hutool.core.date.DateUtil;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsDeviceState;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsPmCurve;
import net.cdsunrise.ztyg.acquisition.ils.domain.IlsThCurve;
import net.cdsunrise.ztyg.acquisition.ils.service.ExecuteService;
import net.cdsunrise.ztyg.acquisition.ils.vo.EnvironmentResponseVo;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsDeviceStateVo;
import net.cdsunrise.ztyg.acquisition.ils.vo.IlsRealtimeData;
import net.cdsunrise.ztyg.acquisition.ils.vo.RequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Binke Zhang
 * @date 2019/11/19 15:20
 */
@RestController
@RequestMapping("/ils")
public class IntelligentLightController {
    private final ExecuteService executeService;
    @Autowired
    public IntelligentLightController(ExecuteService executeService) {
        this.executeService = executeService;
    }

    @PostMapping("/exec")
    public Result<List<IlsDeviceStateVo>> exec(@RequestBody RequestVo requestVo){
        List<IlsDeviceState> list = executeService.execute(requestVo);
        List<IlsDeviceStateVo> voList = new ArrayList<>(list.size());
        list.forEach(e-> voList.add(new IlsDeviceStateVo(e)));
        return ResultUtil.success(voList);
    }

    @GetMapping("/env")
    public Result<IlsRealtimeData> exec(@RequestParam("deviceCode") String deviceCode){
        return ResultUtil.success(executeService.query(deviceCode));
    }

    @GetMapping("/deviceState")
    public Result<Map<String,Map<String,List<IlsDeviceStateVo>>>> deviceState(@RequestParam(name="floor",required = false) String floor,@RequestParam(name="room",required = false) String room){
        List<IlsDeviceState> list = executeService.queryDeviceStates(floor, room);
        List<IlsDeviceStateVo> voList = new ArrayList<>(list.size());
        list.forEach(e-> voList.add(new IlsDeviceStateVo(e)));
        Map<String,List<IlsDeviceStateVo>> floorMap = voList.stream().collect(Collectors.groupingBy(e->e.getFloor()));
        Map<String,Map<String,List<IlsDeviceStateVo>>> finalMap = new HashMap<>(floorMap.size());
        floorMap.forEach((k,v)->{
            finalMap.put(k,v.stream().collect(Collectors.groupingBy(e -> e.getRoom())));
        });
        return ResultUtil.success(finalMap);
    }

    @GetMapping("/oneState")
    public Result<List<IlsDeviceStateVo>> oneState(@RequestParam("deviceCode") String deviceCode){
        List<IlsDeviceState> list = executeService.queryDeviceStates(deviceCode);
        List<IlsDeviceStateVo> voList = new ArrayList<>(list.size());
        list.forEach(e-> voList.add(new IlsDeviceStateVo(e)));
        return ResultUtil.success(voList);
    }

    @GetMapping("/queryTh")
    public Result<List<IlsThCurve>> queryTh(@RequestParam String deviceCode){
        Date date = new Date();
        return ResultUtil.success(executeService.queryThCurve(deviceCode, DateUtil.beginOfDay(date),date));
    }
    @GetMapping("/queryPm")
    public Result<List<IlsPmCurve>> queryPm(@RequestParam String deviceCode){
        Date date = new Date();
        return ResultUtil.success(executeService.queryPmCurve(deviceCode, DateUtil.beginOfDay(date),date));
    }

}
