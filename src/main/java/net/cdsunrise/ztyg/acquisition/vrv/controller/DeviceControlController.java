package net.cdsunrise.ztyg.acquisition.vrv.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.vrv.domain.dto.RealDataDTO;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.req.WriteRequest;
import net.cdsunrise.ztyg.acquisition.vrv.domain.vo.resp.IndoorStateResp;
import net.cdsunrise.ztyg.acquisition.vrv.enums.AdapterStateEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.ModeEnum;
import net.cdsunrise.ztyg.acquisition.vrv.enums.SwitchWindEnum;
import net.cdsunrise.ztyg.acquisition.vrv.service.IDeviceControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/11/19 15:20
 */
@RestController
@RequestMapping("/dc")
public class DeviceControlController {
    private final IDeviceControlService deviceControlService;
    @Autowired
    public DeviceControlController(IDeviceControlService deviceControlService) {
        this.deviceControlService = deviceControlService;
    }

    @PostMapping("/setAll")
    public Result<Boolean> setAll(@RequestBody @Validated WriteRequest.SetAllReq request){
        boolean success = deviceControlService.setFacilityAll(request);
        return ResultUtil.build(success, success ? ExceptionEnum.SUCCESS : ExceptionEnum.OPERATE_FAIL);
    }

    @GetMapping("/adapter_state")
    public Result<AdapterStateEnum> getAdapterState() {
        return ResultUtil.success(deviceControlService.getAdapterState());
    }

    @GetMapping("/indoor/conn_state")
    public Result<boolean[]> getIndoorMachineConnState() {
        return ResultUtil.success(deviceControlService.getIndoorMachineConnState());
    }

    @GetMapping("/indoor/state")
    public Result<IndoorStateResp> getIndoorMachineState(String deviceCode) {
        Asserts.assertStringNotNull(deviceCode, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        return ResultUtil.success(deviceControlService.getIndoorMachineState(deviceCode));
    }

    @GetMapping("/set/power_wind_speed")
    public Result<Boolean> setPowerWindSpeed(String deviceCode, String flag) {
        Asserts.assertStringNotNull(deviceCode, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        SwitchWindEnum switchWindEnum;
        try {
            switchWindEnum = SwitchWindEnum.valueOf(flag);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionEnum.PARAM_NOT_EXIST);
        }
        boolean success = deviceControlService.setPowerAndWindSpeed(deviceCode, switchWindEnum);
        return ResultUtil.build(success, success ? ExceptionEnum.SUCCESS : ExceptionEnum.OPERATE_FAIL);
    }

    @GetMapping("/set/mode")
    public Result<Boolean> setMode(String deviceCode, String flag) {
        Asserts.assertStringNotNull(deviceCode, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        ModeEnum modeEnum;
        try {
            modeEnum = ModeEnum.valueOf(flag);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionEnum.PARAM_NOT_EXIST);
        }
        boolean success = deviceControlService.setMode(deviceCode, modeEnum);
        return ResultUtil.build(success, success ? ExceptionEnum.SUCCESS : ExceptionEnum.OPERATE_FAIL);
    }

    @GetMapping("/set/temperature")
    public Result<Boolean> setTemperature(String deviceCode, Integer temperature) {
        Asserts.assertStringNotNull(deviceCode, () -> new BusinessException(ExceptionEnum.DEVICE_CODE_NOT_EXIST));
        boolean success = deviceControlService.setTemperature(deviceCode, temperature);
        return ResultUtil.build(success, success ? ExceptionEnum.SUCCESS : ExceptionEnum.OPERATE_FAIL);
    }

    @GetMapping("/indoor/all")
    public Result<Map<Short, List<IndoorStateResp>>> getAllMachineState() {
        return ResultUtil.success(deviceControlService.getAllIndoorMachineState());
    }

    @GetMapping("/indoor/{floor}")
    public Result<List<IndoorStateResp>> getMachineStateByFloor(@PathVariable("floor") Short floor) {
        return ResultUtil.success(deviceControlService.getIndoorMachineStateByFloor(floor));
    }

    @GetMapping("/set/indoor/state/all")
    public Result<Boolean> switchAllMachine(String flag) {
        SwitchWindEnum switchWindEnum;
        try {
            switchWindEnum = SwitchWindEnum.valueOf(flag);
            Asserts.assertTrue(switchWindEnum == SwitchWindEnum.ON || switchWindEnum == SwitchWindEnum.OFF,
                    () -> new BusinessException(ExceptionEnum.PARAM_NOT_EXIST));
            deviceControlService.switchAllMachine(switchWindEnum);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionEnum.PARAM_NOT_EXIST);
        }
        return ResultUtil.success();
    }

    @GetMapping("/set/indoor/state/floor")
    public Result<Boolean> switchMachineByFloor(String flag, Short floor) {
        SwitchWindEnum switchWindEnum;
        try {
            switchWindEnum = SwitchWindEnum.valueOf(flag);
            Asserts.assertTrue(switchWindEnum == SwitchWindEnum.ON || switchWindEnum == SwitchWindEnum.OFF,
                    () -> new BusinessException(ExceptionEnum.PARAM_NOT_EXIST));
            deviceControlService.switchMachineByFloor(floor, switchWindEnum);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionEnum.PARAM_NOT_EXIST);
        }
        return ResultUtil.success();
    }

    @GetMapping("/acquisition/{deviceCode}")
    public Result<List<RealDataDTO>> getRealDataByDeviceCode(@PathVariable("deviceCode") String deviceCode) {
        return ResultUtil.success(deviceControlService.getRealDataByDeviceCode(deviceCode));
    }
}
