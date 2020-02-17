package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import com.github.pagehelper.PageHelper;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Role;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.RoleMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role create(Role role) {
        if (this.existByNameOrCode(role.getId(), role.getName(), role.getCode())) {
            throw new BusinessException(ExceptionEnum.DB_EXISTS_ERROR);
        }
        this.roleMapper.insert(role);
        return role;
    }

    private boolean existByNameOrCode(Long id, String name, String code) {
        boolean flag = false;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        map.put("code", code);
        if (this.roleMapper.countByOrConditionSelective(map) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public Role update(Role role) {
        if (this.existByNameOrCode(role.getId(), role.getName(), role.getCode())) {
            throw new BusinessException(ExceptionEnum.DB_EXISTS_ERROR);
        }

        int count = this.roleMapper.updateByPrimaryKey(role);
        if (count == 0) {
            throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return role;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteById(Long id) {
    	/**
    	 * 级联删除关联表
    	 */
        this.roleMapper.deleteByRoleIdFromRR(id);
        this.roleMapper.deleteByRoleIdFromUR(id);
        int affect = this.roleMapper.deleteByPrimaryKey(id);
        if (affect == 0) {
            throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
        }
        return affect;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteByIds(Long[] ids) {
    	/**
    	 * 级联删除关联表
    	 */
        this.roleMapper.deleteByRoleIdsFromRR(ids);
        this.roleMapper.deleteByRoleIdsFromUR(ids);
        int affect = this.roleMapper.deleteByPrimaryKeys(ids);
        if (affect != ids.length) {
            throw new BusinessException(ExceptionEnum.DB_DELETE_FAIL);
        }
        return affect;
    }

    @Override
    public Role findById(Long id) {
        return this.roleMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assignResourcesToRole(Long roleId, Long[] resourceIds) {
        this.roleMapper.deleteByRoleIdFromRR(roleId);
        this.roleMapper.insertToRR(roleId, resourceIds);
    }

    @Override
    public List<Role> findAssignedRolesByUserId(Long userId) {
        List<Role> list = this.roleMapper.findRolesByUserIdFromUR(userId);
        return list;
    }

    @Override
    public List<Role> findPageByParams(Map<String, Object> params, int page, int size) {
        PageHelper.startPage(page, size);
        return this.roleMapper.findByParams(params);
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        List<Role> allRoles = this.roleMapper.findByParams(new HashMap<>());
        List<Role> checkedRoles = this.roleMapper.findRolesByUserIdFromUR(userId);
        List<Long> checkedIds = new ArrayList<>();
        for (Role r : checkedRoles) {
            checkedIds.add(r.getId());
        }
        for (Role r : allRoles) {
            if (checkedIds.contains(r.getId())) {
                r.setChecked(true);
            }
        }
        return allRoles;
    }

    @Override
    public List<Long> findResourceIdsByRoleId(Long roleId) {
        return roleMapper.selectByRoleIdFromRR(roleId);
    }

}
