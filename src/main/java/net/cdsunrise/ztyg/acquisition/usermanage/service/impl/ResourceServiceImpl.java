package net.cdsunrise.ztyg.acquisition.usermanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;
import net.cdsunrise.ztyg.acquisition.usermanage.enums.ResourceTypeEnum;
import net.cdsunrise.ztyg.acquisition.usermanage.mapper.ResourceMapper;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements IResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Resource create(Resource resource) {
        //是否已经存在code
        if (resourceMapper.selectCount(new QueryWrapper<Resource>().lambda().eq(Resource::getCode,resource.getCode())) > 0) {
            throw new BusinessException(ExceptionEnum.DB_EXISTS_ERROR);
        }

        resource.setParentIds(this.getSelfParentIds(resource.getParentId()));
        if (resourceMapper.insert(resource) == 0) {
            throw new BusinessException(ExceptionEnum.DB_INSERT_FAIL);
        }
        return resource;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Resource update(Resource resource) {

        String parentPids = this.getSelfParentIds(resource.getParentId());
        resource.setParentIds(parentPids);
        // 设置不能修改的参数
        resource.setCreateTime(null);

        List<Resource> updateResource = new ArrayList<>();
        updateResource.add(resource);

        parentPids = parentPids == null ? "|" + resource.getId() + "|" : parentPids + resource.getId() + "|";

        List<Resource> list = findResourceTree(resource.getId(), null);
        for (Resource child : list) {
            restParentPids(parentPids, child, updateResource);
        }
        for (Resource r : updateResource) {
            if (resourceMapper.updateById(r) == 0) {
                throw new BusinessException(ExceptionEnum.DB_UPDATE_FAIL);
            }
        }
        return resource;
    }

    public void restParentPids(String pids, Resource resource, List<Resource> updateResource) {
        resource.setParentIds(pids);
        resource.setCreateTime(null);
        updateResource.add(resource);
        if (resource.getChildren() != null) {
            for (Resource child : resource.getChildren()) {
                restParentPids(pids + resource.getId() + "|", child, updateResource);
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteById(Long id) {
        //判读是否有子节点
        if (resourceMapper.selectCount(new QueryWrapper<Resource>().lambda().eq(Resource::getParentId,id)) > 0) {
            throw new BusinessException(ExceptionEnum.DATA_HAS_CHILD_ERROR);
        }
        Long[] ids = new Long[]{id};
        resourceMapper.deleteByResourceIdsFromRR(ids);
        if (resourceMapper.deleteById(id) == 0) {
            throw new BusinessException(ExceptionEnum.DB_DELETE_FAIL);
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Resource cascadeDeleteById(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
        }
        //找到所有的子节点
        List<Resource> list = resourceMapper.selectList(new QueryWrapper<Resource>().lambda().like(Resource::getParentIds,"|" + id + "|"));
        list.add(resource);

        Long[] ids = new Long[list.size()];

        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getId();
        }

        this.resourceMapper.deleteByResourceIdsFromRR(ids);
        for (Long key : ids) {
            this.resourceMapper.deleteById(key);
        }
        return resource;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteByIds(Long[] ids) {
        int count = resourceMapper.deleteBatchIds(Arrays.asList(ids));
        if (count != ids.length) {
            throw new BusinessException(ExceptionEnum.DB_DELETE_FAIL);
        }
        return count;
    }

    @Override
    public Resource findById(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    public List<Resource> findResourceTree(Long parentId, Integer type) {
        List<Resource> list = this.resourceMapper.selectList(new QueryWrapper<Resource>().lambda()
                                              .like(parentId != null,Resource::getParentIds,"|" + parentId + "|")
                                              .eq(type != null,Resource::getType,type)
                                              .orderByAsc(Resource::getIndexNum)
        );
        List<Resource> resourceTree = this.buildTree(parentId, list);
        return resourceTree;
    }

    public List<Resource> buildTree(Long parentId, List<Resource> allList) {
        List<Resource> list = this.findChildrenByParentId(parentId, allList);
        for (Resource r : list) {
            this.buildOneNode(r, allList);
        }
        return list;
    }

    private void buildOneNode(Resource node, List<Resource> allList) {
        List<Resource> children = this.findChildrenByParentId(node.getId(), allList);
        if (children.size() > 0) {
            for (Resource gr : children) {
                this.buildOneNode(gr, allList);
            }
            node.setChildren(children);
        } else {
            node.setChildren(null);
        }
    }

    private List<Resource> findChildrenByParentId(Long id, List<Resource> allList) {
        List<Resource> list = new ArrayList<>();
        for (Resource r : allList) {
            if (id == null && r.getParentId() == null) {
                list.add(r);
            } else if (id != null && id.equals(r.getParentId())) {
                list.add(r);
            }
        }
        return list;
    }


    private String getSelfParentIds(Long parentId) {
        if (parentId == null) {
            //没有设置父节点直接返回
            return null;
        }
        Resource resource = this.resourceMapper.selectById(parentId);
        if (resource == null) {
            throw new BusinessException(ExceptionEnum.DATA_NOT_EXISTS_ERROR);
        }

        StringBuffer sb = new StringBuffer();
        if (resource.getParentIds() != null) {
            sb.append(resource.getParentIds());
        } else {
            sb.append("|");
        }
        sb.append(parentId);
        sb.append("|");
        return sb.toString();
    }

    @Override
    public List<Resource> findChildrenById(Long id) {
        return resourceMapper.selectList(new QueryWrapper<Resource>().lambda()
                .isNull(id ==null,Resource::getParentId)
                .eq(id != null,Resource::getParentId,id)
                .orderByAsc(Resource::getIndexNum)
        );
    }

    @Override
    public List<Resource> findAssignedResourcesByRoleId(Long roleId) {
        List<Resource> list = this.resourceMapper.findResourceByRoleIdFromRR(roleId);
        return list;
    }

    @Override
    public List<Resource> findResourceTreeByRoleId(Long roleId) {
        List<Resource> allList = this.resourceMapper.selectList(new QueryWrapper<>());
        List<Resource> assignedResources = this.resourceMapper.findResourceByRoleIdFromRR(roleId);
        this.setChecked(allList, assignedResources);
        List<Resource> treeList = this.buildTree(null, allList);
        return treeList;
    }

    private void setChecked(List<Resource> allList, List<Resource> assignedResources) {
        List<Long> selectedIds = new ArrayList<Long>();
        for (Resource r : assignedResources) {
            selectedIds.add(r.getId());
        }
        for (Resource r : allList) {
            if (selectedIds.contains(r.getId())) {
                r.setChecked(true);
            }
        }
    }

    @Override
    public List<Resource> findAssignedResourceListByUserId(Long userId) {
        List<Resource> list = this.resourceMapper.findResourcesByUserId(userId);
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Resource> findAssignedResourceTreeByUserId(Long userId) {
        List<Resource> list = this.resourceMapper.findResourcesByUserId(userId);
        list = list.stream().distinct().collect(Collectors.toList());
        list = this.buildTree(null, list);
        return list;
    }

    @Override
    public List<Resource> buildMenuResourceTree(List<Resource> resourceList) {
        return this.buildTree(null, this.filterResourceByType(ResourceTypeEnum.MENU.getCode(), resourceList));
    }

    @Override
    public List<Resource> buildBtnResourceList(List<Resource> resourceList) {
        return this.filterResourceByType(ResourceTypeEnum.BUTTON.getCode(), resourceList);
    }

    @Override
    public List<Resource> buildInterResourceList(List<Resource> resourceList) {
        return this.filterResourceByType(ResourceTypeEnum.INTERFACE.getCode(), resourceList);
    }

    @Override
    public List<Resource> buildDataResourceList(List<Resource> resourceList) {
        return this.filterResourceByType(ResourceTypeEnum.DATA.getCode(), resourceList);
    }


    private List<Resource> filterResourceByType(int type, List<Resource> resourceList) {
        List<Resource> list = new ArrayList<>();
        for (Resource r : resourceList) {
            if (r != null && r.getType() == type) {
                list.add(r);
            }
        }
        return list;
    }

    @Override
    public List<Resource> findPageByParams(Map<String, Object> params, int page, int size) {
        PageHelper.startPage(page, size);
        List<Resource> list = this.resourceMapper.findByParams(params);
        return list;
    }

    @Override
    public List<Resource> findByIds(Long[] ids) {
        return resourceMapper.selectByPrimaryKeys(ids);
    }


}
