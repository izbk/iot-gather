package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.cdsunrise.common.utility.utils.JsonUtils;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.protocol.rs485.component.CommonCache;
import net.cdsunrise.ztyg.acquisition.usermanage.component.TokenConfig;
import net.cdsunrise.ztyg.acquisition.usermanage.constants.CacheKey;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Token;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;
import net.cdsunrise.ztyg.acquisition.usermanage.enums.UserStatusEnum;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.TokenMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.UserMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IResourceService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IUserService;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.UserInfoVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenMapper tokenMapper;
    
    @Autowired
    private IResourceService resourceService;
    
	private final CommonCache<String,Token> tokenCache = CommonCache.getInstance();

    @Override
    public User create(User user) {
        if (this.existByUsername(user.getUsername())) {
        	throw new BusinessException(ExceptionEnum.DB_EXISTS_ERROR);
        }
        user.setPassword(DigestUtils.md5Hex(user.getUsername()+user.getPassword()));
        int affect = this.userMapper.insert(user);
		if(affect == 0){
			throw new BusinessException(ExceptionEnum.DB_INSERT_FAIL);
		}
        return user;
    }

    private boolean existByUsername(String username) {
        boolean flag = false;
        if (this.userMapper.countByUsername(username) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public Integer update(User user) {
        //设置不能被更新的字段为null
        user.setStatus(null);
        user.setUsername(null);
        user.setPassword(null);
        int affect = this.userMapper.updateByPrimaryKeySelective(user);
        if (affect == 0) {
            throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Override
    public Integer updateStatusById(Long id) {
        User user = this.userMapper.selectByPrimaryKey(id);
        user.setStatus(UserStatusEnum.DISABLED.getCode());
        int affect = this.userMapper.updateByPrimaryKey(user);
        if (affect == 0) {
        	throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Override
    public Integer deleteById(Long id) {
    	/**
    	 * 用户可能还没有分配角色或登录过，这里不做判断
    	 */
        this.tokenMapper.deleteByUserId(id);
        this.userMapper.deleteByUserIdFromUR(id);
        User user = this.userMapper.selectByPrimaryKey(id);
        user.setStatus(UserStatusEnum.DELETED.getCode());
        int affect = this.userMapper.updateByPrimaryKeySelective(user);
        if (affect == 0) {
        	throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Transactional
    @Override
    public Integer deleteByIds(Long[] ids) {
    	int affect = 0;
        for (Long id : ids) {
        	affect += deleteById(id);
        }
        return affect;
    }

    @Override
    public User findById(Long id) {
        return this.userMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer modifyPassword(Long id, String oldPassword, String newPassword) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if (user == null) {
        	throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
        }
        if (!user.getPassword().equals(DigestUtils.md5Hex(user.getUsername()+oldPassword))) {
        	throw new BusinessException(ExceptionEnum.OPERATE_FAIL.getCode(),"旧密码错误");
        }
        user.setPassword(DigestUtils.md5Hex(user.getUsername()+newPassword));
        int affect = this.userMapper.updateByPrimaryKeySelective(user);
        if (affect == 0) {
            throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Transactional
    @Override
    public Integer assignRolesToUser(Long userId, Long[] roleIds) {
        this.userMapper.deleteByUserIdFromUR(userId);
        int affect = 0;
        if (roleIds != null && roleIds.length > 0) {
            affect = this.userMapper.insertToUR(userId, roleIds);
            if(affect == 0){
            	throw new BusinessException(ExceptionEnum.DB_INSERT_FAIL);
            }
            updateCache(userId);
        }
        return affect;
    }
    
    /**
     * 更新缓存
     *
     * @Title updateCache   
     * @Description   
     * @param userId
     *
     */
	private void updateCache(Long userId) {
		Token token = tokenCache.get(CacheKey.TOKEN_KEY+userId);
		UserInfoVo vo = null;
		if(token == null) {
			vo = new UserInfoVo();
			token = new Token();
		}else {
			vo = JsonUtils.toObject(token.getUserInfo(), new TypeReference<UserInfoVo>() {});
		}
		// 获取最新资源信息
		List<Resource> resourceList = resourceService.findAssignedResourceListByUserId(userId);
		vo.setBtnResources(this.resourceService.buildBtnResourceList(resourceList));
		vo.setInterResource(this.resourceService.buildInterResourceList(resourceList));
		vo.setMenuResources(this.resourceService.buildMenuResourceTree(resourceList));
		vo.setDataResource(this.resourceService.buildDataResourceList(resourceList));
		token.setLastAccessTime(Calendar.getInstance().getTime());
		token.setUserInfo(JsonUtils.toJsonString(vo));
		this.tokenMapper.updateUserByToken(token.getToken(), userId, JsonUtils.toJsonString(vo));
        tokenCache.put(CacheKey.TOKEN_KEY+userId,token);
	}

    @Override
    public PageInfo<User> findPageByParams(Map<String, Object> params, int page, int size) {
        PageHelper.startPage(page, size);
        List<User> list = this.userMapper.findByParams(params);
        return new PageInfo<>(list);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public Integer setPassword(Long id, String password) {
        User user = this.userMapper.selectByPrimaryKey(id);
        if (user == null) {
        	throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
        }
        user.setPassword(DigestUtils.md5Hex(user.getUsername()+password));
        int affect = this.userMapper.updateByPrimaryKeySelective(user);
        if(affect == 0){
        	throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Override
    public boolean existsValidUserByUserIds(Long[] userIds) {
        int count = this.userMapper.countByUserIds(userIds);
        if (count < userIds.length)
            return false;
        return true;
    }

    @Override
    public User findByToken(String token) {
        Token storeToken = this.tokenMapper.findByToken(token);
        if (storeToken != null) {
            User user = this.userMapper.selectByPrimaryKey(storeToken.getUserId());
            return user;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(DigestUtils.md5Hex("zhangsan123456"));
    }

}
