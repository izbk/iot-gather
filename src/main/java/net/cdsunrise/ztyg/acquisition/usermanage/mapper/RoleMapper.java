package net.cdsunrise.ztyg.acquisition.usermanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:45
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * @Title deleteByPrimaryKey
     * @Description 根据主键删除角色
     * @param id 角色ID
     * @return
     */
    int deleteByPrimaryKey(Long id);
    /**
     * @Title deleteByPrimaryKeys
     * @Description 批量删除角色
     * @param ids 角色ID数组
     * @return
     */
    int deleteByPrimaryKeys(Long[] ids);
    /**
     * @Title insert
     * @Description 向数据库插入角色数据
     * @param role
     * @return
     */
    int insert(Role role);

    int insertSelective(Role role);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role role);

    int updateByPrimaryKey(Role role);

    int countByOrConditionSelective(Map<String, Object> params);

    int insertToRR(@Param("roleId")Long roleId, @Param("array")Long[] resourceIds);

    int deleteByRoleIdFromRR(@Param("roleId")Long roleId);

    int deleteByRoleIdsFromRR(Long[] roleIds);

    int deleteByRoleIdFromUR(@Param("roleId")Long roleId);

    int deleteByRoleIdsFromUR(Long[] roleIds);

    List<Role> findRolesByUserIdFromUR(@Param("userId")Long userId);

    List<Role> findByParams(Map<String, Object> params);

    List<Long> selectByRoleIdFromRR(@Param("roleId")Long roleId);

    List<Long> selectByResourceIdsFromRR(Long[] resourceIds);

    List<Long> selectByRoleIdsFromUR(Long[] roleIds);
}
