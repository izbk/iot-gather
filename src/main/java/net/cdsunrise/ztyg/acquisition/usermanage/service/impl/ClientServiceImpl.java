package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import cn.hutool.core.util.IdUtil;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Client;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.ClientMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IClientService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
public class ClientServiceImpl implements IClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ITokenService tokenService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Client create(String clientId, String clientType) {
        Client client = new Client();
        client.setClientId(clientId);
        client.setClientSecret(IdUtil.simpleUUID());
        client.setKeySecret(IdUtil.simpleUUID());
        client.setCreateTime(Calendar.getInstance().getTime());
        client.setClientType(clientType);
        if (this.clientMapper.insert(client) == 0) {throw new BusinessException(ExceptionEnum.OPERATE_FAIL);}
        return client;
    }

    @Override
    public Client get(String token) {
        // TODO Auto-generated method stub
        Token ttoken = this.tokenService.findByToken(token);
        if (token == null){
            return null;
        }
        return this.clientMapper.findById(ttoken.getClientId());
    }

	@Override
	public Client getByClientId(String clientId) {
		return clientMapper.findByClientId(clientId);
	}

}
