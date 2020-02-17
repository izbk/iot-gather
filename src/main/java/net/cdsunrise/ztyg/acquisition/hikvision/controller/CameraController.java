package net.cdsunrise.ztyg.acquisition.hikvision.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.Camera;
import net.cdsunrise.ztyg.acquisition.hikvision.domain.camera.vo.CameraVo;
import net.cdsunrise.ztyg.acquisition.hikvision.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-19 14:00
 */
@RestController
@RequestMapping("/camera")
public class CameraController {
    private final CameraService cameraService;

    @Autowired
    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/all")
    public Result<List<Camera>> getAll() {
        return ResultUtil.success(cameraService.getAll());
    }

    @GetMapping("/get/{deviceCode}")
    public Result<CameraVo> getByDeviceCode(@PathVariable("deviceCode") String deviceCode) {
        return ResultUtil.success(cameraService.getCameraByDeviceCode(deviceCode));
    }
}
