package net.cdsunrise.ztyg.acquisition.usermanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Binke Zhang
 * @date 2019/12/3 9:45
 */
@Repository
public interface ResourceMapper extends BaseMapper<Resource> {
    /**
     * @Title findChildrenById
     * @Description 根据资源ID查询子节点
     * @param id
     * @return
     */
    List<Resource> findChildrenById(@Param("id")Long id);
    /**
     * @Title findResourceByRoleIdFromRR
     * @Description 获取已分配的角色资源
     * @param roleId
     * @return
     */
    List<Resource> findResourceByRoleIdFromRR(@Param("roleId") Long roleId);
    /**
     * @Title deleteByResourceIdsFromRR
     * @Description 根据资源ID数组删除Role_Resouce中间表的数据
     * @param resourceIds
     * @return
     */
    int deleteByResourceIdsFromRR(Long[] resourceIds);
    /**
     * @Title findResourcesByUserId
     * @Description 根据用户ID查询用户拥有的资源列表
     * @param userId
     * @return
     */
    List<Resource> findResourcesByUserId(@Param("userId")Long userId);

    List<Resource> findByParams(Map<String, Object> params);

    List<Resource> selectByPrimaryKeys(Long[] resourceIds);
}
