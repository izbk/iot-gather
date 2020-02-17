package net.cdsunrise.ztyg.acquisition.usermanage.service;


import net.cdsunrise.ztyg.acquisition.usermanage.domain.Client;

public interface IClientService {
	
	Client create(String clientId, String clientType);
	
	Client get(String token);
	
	Client getByClientId(String clientId);
	
}
