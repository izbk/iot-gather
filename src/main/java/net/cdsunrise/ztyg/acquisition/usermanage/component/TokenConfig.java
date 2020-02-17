package net.cdsunrise.ztyg.acquisition.usermanage.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: UaaProperties 
 * @Description: 用户权限模块的配置属性 
 * @author Binke Zhang
 * @date 2017年4月25日 下午5:28:44 
 *
 */
@Component
@ConfigurationProperties(prefix="sys")
public class TokenConfig {
	
	/**token过期时间，默认为30分钟*/
	private long tokenExpire = 60 * 30 * 1000L;
	
	/**层级关系的位数，比如：资源中一级为0001，二级为00010001*/
	private int levelCodeDigit = 4;

	public long getTokenExpire() {
		return tokenExpire;
	}

	public void setTokenExpire(long tokenExpire) {
		this.tokenExpire = tokenExpire;
	}

	public int getLevelCodeDigit() {
		return levelCodeDigit;
	}

	public void setLevelCodeDigit(int levelCodeDigit) {
		this.levelCodeDigit = levelCodeDigit;
	}

}
