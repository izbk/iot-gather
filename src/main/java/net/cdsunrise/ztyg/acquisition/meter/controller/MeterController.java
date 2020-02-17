package net.cdsunrise.ztyg.acquisition.meter.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.meter.domain.MeterRealtimeData;
import net.cdsunrise.ztyg.acquisition.meter.service.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author sh
 * @date 2019-11-28 11:45
 */
@RestController
@RequestMapping("/meter")
public class MeterController {

    private static final Pattern DATE_PATTERN = Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])");
    private static final DateTimeFormatter DTF_D = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MeterService meterService;

    @Autowired
    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping("/pull/one")
    public Result<MeterRealtimeData> pullByDeviceCode(String deviceCode) {
        return ResultUtil.success(meterService.pullByDeviceCode(deviceCode));
    }

    /**
     * 每天23:59:00分执行
     */
//    @Scheduled(cron = "0 59 23 1 * ? *")
    public void pullElectricity() {
        meterService.pullElectricity();
    }

    /**
     * 每30S执行一次
     */
//    @Scheduled(cron = "30 * * * * ?")
    public void pullAll() {
        meterService.pullAll();
    }

    @GetMapping("/realtime/electricity")
    public Result<Integer> getRealtimeElectricity() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now, LocalTime.MAX);
        return ResultUtil.success(meterService.getRealtimeElectricity(start, end));
    }

    @GetMapping("/floor/electricity")
    public Result<Map<Short, Integer>> floorElectricity() {
        return ResultUtil.success(meterService.floorElectricity());
    }

    @GetMapping("/day/electricity")
    public Result<Map<String, Integer>> dayElectricity(String start, String end) {
        Asserts.assertTrue(DATE_PATTERN.matcher(start).matches(), () -> new BusinessException(ExceptionEnum.DATE_FORMAT_ERROR));
        Asserts.assertTrue(DATE_PATTERN.matcher(end).matches(), () -> new BusinessException(ExceptionEnum.DATE_FORMAT_ERROR));
        LocalDate s1 = LocalDate.parse(start, DTF_D);
        LocalDate s2 = LocalDate.parse(end, DTF_D);
        return ResultUtil.success(meterService.electricityOfDay(s1, s2));
    }
}
