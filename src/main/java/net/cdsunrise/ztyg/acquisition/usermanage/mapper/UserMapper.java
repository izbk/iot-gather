package net.cdsunrise.ztyg.acquisition.usermanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:45
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    int deleteByPrimaryKey(Long id);

    int deleteByPrimaryKeys(Long[] ids);

    int insert(User user);

    int insertSelective(User user);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User user);

    int updateByPrimaryKey(User user);

    int countByUsername(String username);

    int deleteByUserIdFromUR(@Param("userId")Long userId);

    int deleteByUserIdsFromUR(Long[] userIds);

    int insertToUR(@Param("userId")Long userId, @Param("array")Long[] roleIds);

    User findByUsername(@Param("username")String username);

    List<User> findByParams(Map<String, Object> params);

    int countByUserIds(@Param("array")Long[] userIds);
}
