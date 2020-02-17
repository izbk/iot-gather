package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import net.cdsunrise.ztyg.acquisition.usermanage.component.TokenConfig;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.TokenMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class TokenServiceImpl implements ITokenService {

	@Autowired
	private TokenMapper tokenMapper;
	
	@Autowired
	private TokenConfig tokenConfig;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer cleanOutToken() {
		long expireIn = this.tokenConfig.getTokenExpire();
		Date date = new Date(Calendar.getInstance().getTimeInMillis() - expireIn);
		int affect = this.tokenMapper.deleteOuttimeTokens(date);
		return affect;
	}

	@Override
	public Long findUserIdByToken(String token) {
		return tokenMapper.findUserIdByToken(token) ;
	}

	@Override
	public Token findByToken(String token) {
		return this.tokenMapper.findByToken(token);
	}

}
