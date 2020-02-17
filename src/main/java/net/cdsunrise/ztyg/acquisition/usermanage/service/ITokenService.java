package net.cdsunrise.ztyg.acquisition.usermanage.service;

import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;

public interface ITokenService {
	
	Integer cleanOutToken();

	Long findUserIdByToken(String token);
	
	//根据token查询clientId
	Token findByToken(String token);
}
