package net.cdsunrise.ztyg.acquisition.usermanage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;

import java.util.Map;

public interface IUserService {

	User create(User user);
	
	Integer update(User user);
	
	Integer updateStatusById(Long id);
	
	Integer deleteById(Long id);
	
	Integer deleteByIds(Long[] ids);
	
	User findById(Long id);
	
	Integer modifyPassword(Long id, String oldPassword, String newPassword);
	
	Integer setPassword(Long id, String password);
	
	Integer assignRolesToUser(Long userId, Long[] roleIds);

	PageInfo<User> findPageByParams(Map<String, Object> params, int page, int size);
	
	boolean existsValidUserByUserIds(Long[] userIds);
	
	User findByToken(String token);
}
