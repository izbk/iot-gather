package net.cdsunrise.ztyg.acquisition.base.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.base.domain.BaseInfo;
import net.cdsunrise.ztyg.acquisition.base.service.BaseInfoService;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sh
 * @date 2019-12-19 09:30
 */
@RestController
@RequestMapping("/base_info")
public class BaseInfoController {
    private final BaseInfoService baseInfoService;

    @Autowired
    public BaseInfoController(BaseInfoService baseInfoService) {
        this.baseInfoService = baseInfoService;
    }

    @GetMapping("/all")
    public Result<List<BaseInfo>> getAll() {
        return ResultUtil.success(baseInfoService.getAll());
    }

    @GetMapping("/get/{deviceCode}")
    public Result<BaseInfo> getByDeviceCode(@PathVariable("deviceCode") String deviceCode) {
        return ResultUtil.success(baseInfoService.getByDeviceCode(deviceCode));
    }
}
