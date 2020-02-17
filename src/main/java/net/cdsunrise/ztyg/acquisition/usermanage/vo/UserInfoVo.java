package net.cdsunrise.ztyg.acquisition.usermanage.vo;

import lombok.Data;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Role;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: UserInfoVo 
 * @Description: 用户信息
 * @author Binke Zhang
 * @date 2017年4月25日 下午6:05:30 
 *
 */
@Data
public class UserInfoVo implements Serializable{
	private static final long serialVersionUID = 1529598080961608531L;
	/**用户信息*/
	private UserVo userVo;
	/**用户角色信息*/
	private List<Role> roles;
	/**用户拥有的菜单资源列表*/
	private List<Resource> menuResources;
	/**用户拥有的按钮资源列表*/
	private List<Resource> btnResources;
	/**用户拥有的接口资源列表*/
	private List<Resource> interResource;
	/**用户拥有的数据资源列表*/
	private List<Resource> dataResource;
}
