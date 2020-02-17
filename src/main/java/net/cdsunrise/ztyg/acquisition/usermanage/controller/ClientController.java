package net.cdsunrise.ztyg.acquisition.usermanage.controller;

import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Client;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IClientService;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.ClientVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Binke Zhang
 * @ClassName: ClientController
 * @Description: 客户端控制器
 * @date 2017年4月25日 下午5:36:49
 */
@RestController
@RequestMapping(value = "client")
public class ClientController {

    @Autowired
    IClientService clientService;

    /**
     * @param vo clientId：客户端ID， clientType：客户端类型
     * @return
     * @Title create
     * @Description 创建一个客户端
     */
    @PostMapping(value = "create")
    public Result<Client> create(@RequestBody ClientVo vo) {
        return ResultUtil.success(this.clientService.create(vo.getClientId(), vo.getClientType()));
    }

}
