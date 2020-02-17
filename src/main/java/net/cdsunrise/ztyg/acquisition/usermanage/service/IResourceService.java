package net.cdsunrise.ztyg.acquisition.usermanage.service;

import net.cdsunrise.ztyg.acquisition.usermanage.domain.Resource;

import java.util.List;
import java.util.Map;

public interface IResourceService {

	Resource create(Resource resource);
	
	Resource update(Resource resource);
	
	int deleteById(Long id);
	
	Resource cascadeDeleteById(Long id);
	
	int deleteByIds(Long[] ids);
	
	Resource findById(Long id);
	
	List<Resource> findResourceTree(Long parentId, Integer type);
	
	List<Resource> findChildrenById(Long id);
	
	List<Resource> findAssignedResourcesByRoleId(Long roleId);
	
	List<Resource> findResourceTreeByRoleId(Long roleId);
	
	List<Resource> findAssignedResourceListByUserId(Long userId);
	
	List<Resource> findAssignedResourceTreeByUserId(Long userId);
	
	List<Resource> buildMenuResourceTree(List<Resource> resourceList);
	
	List<Resource> buildBtnResourceList(List<Resource> resourceList);
	
	List<Resource> buildInterResourceList(List<Resource> resourceList);

	List<Resource> buildDataResourceList(List<Resource> resourceList);
	
	List<Resource> findPageByParams(Map<String, Object> params, int page, int size);
	
	List<Resource> findByIds(Long[] ids);
}
