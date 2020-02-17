package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import net.cdsunrise.common.utility.utils.JsonUtils;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.usermanage.component.TokenConfig;
import net.cdsunrise.ztyg.acquisition.usermanage.constants.CacheKey;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.*;
import net.cdsunrise.ztyg.acquisition.usermanage.enums.UserStatusEnum;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.ClientMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.TokenMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.UserMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IAuthService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IResourceService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IRoleService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IUserService;
import net.cdsunrise.ztyg.acquisition.usermanage.utils.DESHelper;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.TokenVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserInfoVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService {

	@Autowired
	private ClientMapper clientMapper;

	@Autowired
	private TokenMapper tokenMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private IResourceService resourceService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private TokenConfig tokenConfig;

    @Autowired
	IUserService userService;

	private final CommonCache<String,Token> tokenCache = CommonCache.getInstance();
	private final CommonCache<String,User> userCache = CommonCache.getInstance();

	@Override
	public Token generateToken(String clientId, String clientSecret, String requestTime) {
		Client client = this.clientMapper.findByClientId(clientId);
		if (client == null) {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
		}
		try {
			clientSecret = new String(DESHelper.decrypt(Base64.getDecoder().decode(clientSecret), client.getKeySecret()));
			requestTime = new String(DESHelper.decrypt(Base64.getDecoder().decode(requestTime), client.getKeySecret()));
		} catch (Exception e) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","解密client参数出错");
		}

		if (this.validateRequestTime(requestTime) == false) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","requestTime参数验证出错");
		}

		if (!client.getClientSecret().equals(clientSecret)) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","clientSecret参数验证出错");
		}
		Token token = new Token();
		token.setClientId(client.getId());
		token.setToken(IdUtil.simpleUUID());
		Date curTime = Calendar.getInstance().getTime();
		token.setLastAccessTime(curTime);
		token.setCreateTime(curTime);
		if (this.tokenMapper.insert(token) == 0){
			throw new BusinessException(ExceptionEnum.DB_INSERT_FAIL);
		}
		return token;
	}

	private boolean validateRequestTime(String requestTime) {
		boolean flag = false;
		long requestTimeMillis = Long.valueOf(requestTime);
		if (Calendar.getInstance().getTimeInMillis() - requestTimeMillis < 60000) {
			flag = true;
		}
		return flag;
	}

	@Override
	public UserInfoVo getUserInfo(String token) {
		Token t = this.tokenMapper.findByToken(token);
		if (t == null || t.getUserInfo() == null) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","通过token获取用户信息出错");
		}
		UserInfoVo vo = JsonUtils.toObject(t.getUserInfo(), new TypeReference<UserInfoVo>() {});
		return vo;
	}

	@Override
	public String findKeyByToken(String token) {
		return this.tokenMapper.findKeyByToken(token);
	}

	@Override
	@SuppressWarnings("Duplicates")
	public TokenVo login(String username, String password) {
		// 用户名密码验证
		User user = this.userMapper.findByUsername(username);
		if (user == null || !user.getPassword().equals(DigestUtils.md5Hex(username+password))) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","用户名或密码错误");
		}
		if (UserStatusEnum.DISABLED.getCode().equals(user.getStatus())) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","此用户已禁用");
		}
		Long userId = user.getId();
		// 获取用户所拥有的角色
		List<Role> roles = roleService.findAssignedRolesByUserId(userId);
		// 获取用户所拥有的资源
		List<Resource> resourceList = this.resourceService.findAssignedResourceListByUserId(userId);
		UserInfoVo vo = new UserInfoVo();
		vo.setUserVo(new UserVo(user));
		vo.setRoles(roles);
		vo.setBtnResources(this.resourceService.buildBtnResourceList(resourceList));
		vo.setInterResource(this.resourceService.buildInterResourceList(resourceList));
		vo.setMenuResources(this.resourceService.buildMenuResourceTree(resourceList));
		vo.setDataResource(this.resourceService.buildDataResourceList(resourceList));
		// 删除token
		tokenMapper.deleteByUserId(userId);
		// 生成token验证
		Token token = new Token();
		token.setToken(IdUtil.simpleUUID());
		Date curTime = Calendar.getInstance().getTime();
		token.setLastAccessTime(curTime);
		token.setCreateTime(curTime);
		token.setUserId(userId);
		token.setUserInfo(JsonUtils.toJsonString(vo));
		if (this.tokenMapper.insert(token) == 0){
			throw new BusinessException(ExceptionEnum.DB_INSERT_FAIL);
		}
		userCache.put(CacheKey.USER_KEY+token.getToken(),user);
		tokenCache.put(CacheKey.TOKEN_KEY+userId,token);
		return new TokenVo(token);
	}

	@Override
	@SuppressWarnings("Duplicates")
	public UserInfoVo login(String token, String username, String password) {
		// token验证
		Token tokenInfo = tokenMapper.findByToken(token);
		if (tokenInfo == null) {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
		}
		// 用户名密码验证
		User user = this.userMapper.findByUsername(username);
		if (user == null || !user.getPassword().equals(DigestUtils.md5Hex(username+password))) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","用户名或密码错误");
		}
		if (UserStatusEnum.DISABLED.getCode().equals(user.getStatus())) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","此用户已禁用");
		}
		Long userId = user.getId();
		//删除过期token
		tokenMapper.deleteExpireTokenByUserId(userId, token);
		// 获取用户所拥有的角色
		List<Role> roles = roleService.findAssignedRolesByUserId(userId);
		// 获取用户所拥有的资源
		List<Resource> resourceList = this.resourceService.findAssignedResourceListByUserId(userId);
		// 获取员工及机构信息
		UserInfoVo vo = new UserInfoVo();
		vo.setUserVo(new UserVo(user));
		vo.setRoles(roles);
		vo.setBtnResources(this.resourceService.buildBtnResourceList(resourceList));
		vo.setInterResource(this.resourceService.buildInterResourceList(resourceList));
		vo.setMenuResources(this.resourceService.buildMenuResourceTree(resourceList));
		vo.setDataResource(this.resourceService.buildDataResourceList(resourceList));
		// token绑定用户信息
		this.tokenMapper.updateUserByToken(token, user.getId(), JsonUtils.toJsonString(vo));
		// 增加缓存
		Token storeToken = tokenMapper.findByToken(token);
		userCache.put(CacheKey.USER_KEY+token,user);
		tokenCache.put(CacheKey.TOKEN_KEY+storeToken.getUserId(),storeToken);
		return vo;
	}

	@Override
	public Boolean logout(String token) {
		// 清除缓存
		User user = userCache.remove(CacheKey.USER_KEY+token);
		tokenCache.remove(CacheKey.TOKEN_KEY+user.getId());
		return tokenMapper.deleteByToken(token) > 0;
	}

	@Override
	public Boolean checkAuth(String token, String method, String path) {
		Token cacheToken = null;
		User user = userCache.get(CacheKey.USER_KEY+token);
		if(user !=null) {
			cacheToken = tokenCache.get(CacheKey.TOKEN_KEY+user.getId());
		}
		Token tokenInfo = null;
		UserInfoVo vo = null;
		if (cacheToken == null) {
			tokenInfo = this.tokenMapper.findByToken(token);
			if (tokenInfo == null) {
				throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","token无效");
			}
			if (tokenInfo.getUserId() == null || tokenInfo.getUserInfo() == null) {
				throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","token无效");
			}
			if (this.isOutTime(tokenInfo)) {
				throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","token已过期");
			}
			vo = JsonUtils.toObject(tokenInfo.getUserInfo(), new TypeReference<UserInfoVo>() {});
		} else {
			vo = JsonUtils.toObject(cacheToken.getUserInfo(), new TypeReference<UserInfoVo>() {});
			tokenInfo = cacheToken;
		}
		// 获取用户所拥有的角色
		List<Role> roles = vo.getRoles();
		// 判断拥有的角色和资源
		if (!hasAdminRole(roles) && !this.hasResourceAuth(vo, method, path)) {
			throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"","权限不足");
		}
		// 更新最后一次访问时间
		return setCacheAndUpdateDB(tokenInfo, Calendar.getInstance().getTime()) != null;
	}

	private boolean isOutTime(Token token) {
		boolean isOut = calcTimeout(token.getLastAccessTime()) <= 0;
		if(isOut) {
			this.logout(token.getToken());
		}
		return isOut;
	}

	/**
	 * 是否拥有管理员角色
	 *
	 * @Title isAdmin   
	 * @Description   
	 * @param roles
	 * @return
	 *
	 */
	private boolean hasAdminRole(List<Role> roles) {
		return roles.stream().allMatch(role -> role.getCode().equals("super_admin"));
	}
	/**
	 * 是否拥有指定资源
	 *
	 * @Title hasResourceAuth   
	 * @Description   
	 * @param userInfoVo
	 * @param method
	 * @param path
	 * @return
	 *
	 */
	@SuppressWarnings("Duplicates")
	private boolean hasResourceAuth(UserInfoVo userInfoVo, String method, String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		List<Resource> buttonList = userInfoVo.getBtnResources();
		List<Resource> interfaceList = userInfoVo.getInterResource();
		List<Resource> menuList = userInfoVo.getMenuResources();
		boolean b1 = false;
		if(CollectionUtil.isNotEmpty(buttonList)){
			b1 = buttonList.stream().anyMatch(resource -> resource.getRequestMethod().equalsIgnoreCase(method) && path.startsWith(resource.getUrl()));
		}
		boolean b2 = false;
		if(CollectionUtil.isNotEmpty(interfaceList)){
			b2 = interfaceList.stream().anyMatch(resource -> resource.getRequestMethod().equalsIgnoreCase(method) && path.startsWith(resource.getUrl()));
		}
		boolean b3 = false;
		if(CollectionUtil.isNotEmpty(menuList)){
			b3 = menuList.stream().anyMatch(resource -> resource.getRequestMethod().equalsIgnoreCase(method) && path.startsWith(resource.getUrl()));
		}
		return b1 || b2 || b3;
	}

	/**
	 * 更新缓存及DB
	 *
	 * @Title setCacheAndUpdateDB
	 * @Description
	 * @param token
	 * @param lastAccessTime
	 * @return
	 *
	 */
	private Token setCacheAndUpdateDB(Token token, Date lastAccessTime) {
		token.setLastAccessTime(lastAccessTime);
		this.tokenMapper.updateLastAccessTimeByToken(token.getToken(), lastAccessTime);
		return token;
	}

	/**
	 * 通过最后一次访问时间计算过期时间
	 *
	 * @Title calcTimeout
	 * @Description
	 * @param lastAccessTime
	 * @return
	 *
	 */
	private long calcTimeout(Date lastAccessTime) {
		long timeout = tokenConfig.getTokenExpire()
				- (Calendar.getInstance().getTimeInMillis() - lastAccessTime.getTime());
		return timeout;
	}
	
}
