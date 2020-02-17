package net.cdsunrise.ztyg.acquisition.sensor.controller;

import cn.hutool.core.date.DateUtil;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.sensor.domain.SensorRealtimeData;
import net.cdsunrise.ztyg.acquisition.sensor.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/11/19 15:20
 */
@RestController
@RequestMapping("/sensor")
public class SensorController {
    private final ProcessService processService;
    @Autowired
    public SensorController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/read")
    public Result<SensorRealtimeData> read(@RequestParam String deviceCode){
        return ResultUtil.success(processService.read(deviceCode));
    }

    @GetMapping("/history")
    public Result<Map<String,Object>> history(@RequestParam String deviceCode){
        Date date = new Date();
        return ResultUtil.success(processService.query(deviceCode, DateUtil.beginOfDay(date),date));
    }
}
