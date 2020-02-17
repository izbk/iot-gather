package net.cdsunrise.ztyg.acquisition.usermanage.controller;

import com.github.pagehelper.PageInfo;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;
import net.cdsunrise.ztyg.acquisition.usermanage.enums.ResourceTypeEnum;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @ClassName: ResourceController
 * @Description: 资源控制器
 * @date 2017年4月25日 下午5:37:39
 */
@RestController
@RequestMapping(value = "resource")
public class ResourceController {

    @Autowired
    private IResourceService resourceService;

    /**
     * @param resource
     * @return
     * @Title create
     * @Description 新增资源信息
     */
    @PostMapping(value = "create")
    public Result<Resource> create(@RequestBody Resource resource) {
        if (StringUtils.isEmpty(resource.getName()) || StringUtils.isEmpty(resource.getCode())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        if (ResourceTypeEnum.getEnumMap().get(resource.getType()) == null) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(this.resourceService.create(resource));
    }

    /**
     * @param resource
     * @return
     * @Title update
     * @Description 更新资源信息
     */
    @PostMapping(value = "update")
    public Result<Resource> update(@RequestBody Resource resource) {
        if (resource.getId() == null && resource.getVersion() == null || StringUtils.isEmpty(resource.getName()) || StringUtils.isEmpty(resource.getCode())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        if (ResourceTypeEnum.getEnumMap().get(resource.getType()) == null) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(this.resourceService.update(resource));
    }

    /**
     * @param id
     * @return
     * @Title delete
     * @Description 删除单一资源信息
     */
    @PostMapping(value = "delete")
    public Result<Integer> delete(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(resourceService.deleteById(id));
    }

    /**
     * @param id
     * @return
     * @Title cascadingDelete
     * @Description 级联删除单一资源信息
     */
    @PostMapping(value = "cascadeDelete")
    public Result<Resource> cascadeDelete(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(resourceService.cascadeDeleteById(id));
    }

    /**
     * @param ids
     * @return
     * @Title batchDelete
     * @Description 批量删除资源信息
     */
    @PostMapping(value = "batchDelete")
    public Result<Integer> batchDelete(@RequestBody Long[] ids) {
        return ResultUtil.success(resourceService.deleteByIds(ids));
    }

    /**
     * @param id
     * @return
     * @Title get
     * @Description 获取单一资源信息
     */
    @GetMapping(value = "get")
    public Result<Resource> get(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(this.resourceService.findById(id));
    }

    /**
     * @param parentId 资源ID，如果为空，则获取所有的资源信息
     * @param type     资源类型
     * @return
     * @Title findResourceTree
     * @Description 获取资源树
     */
    @GetMapping(value = "findResourceTree")
    public Result<List<Resource>> findResourceTree(@RequestParam(name = "parentId", required = false) Long parentId, @RequestParam(name = "type", required = false) Integer type) {
        return ResultUtil.success(this.resourceService.findResourceTree(parentId, type));
    }

    /**
     * @param id，资源ID，如果ID为空，则获取第一级的资源列表
     * @return
     * @Title findChildren
     * @Description 根据资源ID获取子节点资源
     */
    @GetMapping(value = "findChildren")
    public Result<List<Resource>> findChildren(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(this.resourceService.findChildrenById(id));
    }

    /**
     * @param roleId 角色ID
     * @return
     * @Title findAssignedResourcesByRoleId
     * @Description 根据角色ID获取分配给该角色的资源列表
     */
    @GetMapping(value = "findAssignedResourcesByRoleId")
    public Result<List<Resource>> findAssignedResourcesByRoleId(@RequestParam(name = "roleId") Long roleId) {
        return ResultUtil.success(resourceService.findAssignedResourcesByRoleId(roleId));
    }

    /**
     * @param roleId
     * @return
     * @Title findResourceTreeByRoleId
     * @Description 根据角色ID获取完整资源树，如果已分配则checked属性为true
     */
    @GetMapping(value = "findResourceTreeByRoleId")
    public Result<List<Resource>> findResourceTreeByRoleId(@RequestParam(name = "roleId") Long roleId) {
        return ResultUtil.success(this.resourceService.findResourceTreeByRoleId(roleId));
    }

    /**
     * @param params
     * @param page
     * @param size
     * @return
     * @Title findPageByParams
     * @Description 分页查询资源列表
     */
    @PostMapping(value = "findPageByParams")
    public Result<PageInfo<Resource>> findPageByParams(@RequestBody Map<String, Object> params, int page, int size) {
        List<Resource> list = this.resourceService.findPageByParams(params, page, size);
        return ResultUtil.success(new PageInfo<>(list));
    }
}
