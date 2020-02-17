package net.cdsunrise.ztyg.acquisition.usermanage.service;

import net.cdsunrise.ztyg.acquisition.usermanage.domain.Role;

import java.util.List;
import java.util.Map;

public interface IRoleService {

    Role create(Role role);

    Role update(Role role);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);

    Role findById(Long id);

    void assignResourcesToRole(Long roleId, Long[] resourceIds);

    List<Role> findAssignedRolesByUserId(Long userId);

    List<Role> findRolesByUserId(Long userId);

    List<Role> findPageByParams(Map<String, Object> params, int page, int size);

    List<Long> findResourceIdsByRoleId(Long roleId);
}
