package net.cdsunrise.ztyg.acquisition.usermanage.controller;

import com.github.pagehelper.PageInfo;
import net.cdsunrise.common.utility.vo.Result;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.utils.ResultUtil;
import net.cdsunrise.ztyg.acquisition.usermanage.domain.User;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IAuthService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IRoleService;
import net.cdsunrise.ztyg.acquisition.usermanage.service.IUserService;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.AssignRoleVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.ModifyPasswordVo;
import net.cdsunrise.ztyg.acquisition.usermanage.vo.ResetPasswordVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Binke Zhang
 * @ClassName: UserController
 * @Description: 用户管理控制器
 * @date 2017年4月25日 下午5:44:20
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    IRoleService roleService;

    /**
     * @param token
     * @param user
     * @return
     * @Title create
     * @Description 创建用户
     */
    @PostMapping(value = "create")
    public Result<User> create(@RequestHeader(name = "token") String token, @RequestBody User user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword()) || user.getStatus() == null || StringUtils.isEmpty(user.getTrueName())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        return ResultUtil.success(userService.create(user));
    }

    /**
     * @param user
     * @return
     * @Title update
     * @Description 更新用户信息
     */
    @PostMapping(value = "update")
    public Result<Integer> update(@RequestBody User user) {
        if (user.getId() == null || user.getVersion() == null || StringUtils.isEmpty(user.getTrueName())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(userService.update(user));
    }

    /**
     * @param id
     * @return
     * @Title updateStatus
     * @Description 更新用户状态
     */
    @PostMapping(value = "disable")
    public Result<Integer> disableUser(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(userService.updateStatusById(id));
    }

    /**
     * @param id
     * @return
     * @Title delete
     * @Description 删除用户
     */
    @PostMapping(value = "delete")
    public Result<Integer> delete(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(userService.deleteById(id));
    }

    /**
     * @param id
     * @return
     * @Title get
     * @Description 获取单一用户信息
     */
    @GetMapping(value = "get")
    public Result<User> get(@RequestParam(name = "id") Long id) {
        return ResultUtil.success(this.userService.findById(id));
    }

    /**
     * @param token
     * @param vo
     * @return
     * @Title modifyPassword
     * @Description 密码修改
     */
    @PostMapping(value = "modifyPassword")
    public Result<Integer> modifyPassword(@RequestHeader(name = "token") String token, @RequestBody ModifyPasswordVo vo) {
        if (vo.getId() == null || StringUtils.isEmpty(vo.getOldPassword()) || StringUtils.isEmpty(vo.getNewPassword())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(userService.modifyPassword(vo.getId(), vo.getOldPassword(), vo.getNewPassword()));
    }

    @PostMapping(value = "setPassword")
    public Result<Integer> setPassword(@RequestHeader(name = "token") String token, @RequestBody ResetPasswordVo vo) {
        if (vo.getId() == null || StringUtils.isEmpty(vo.getPassword())) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(userService.setPassword(vo.getId(),vo.getPassword()));
    }

    /**
     * @param vo
     * @return
     * @Title assignRolesToUser
     * @Description 向用户分配角色
     */
    @PostMapping(value = "assignRolesToUser")
    public Result<Integer> assignRolesToUser(@RequestBody AssignRoleVo vo) {
        if (vo.getUserId() == null || vo.getRoleIds() == null) {
            return ResultUtil.fail(ExceptionEnum.PARAM_ERROR);
        }
        return ResultUtil.success(userService.assignRolesToUser(vo.getUserId(),vo.getRoleIds()));
    }

    /**
     * @param params
     * @param page
     * @param size
     * @return
     * @Title findPageByParams
     * @Description 分页查询用户列表
     */
    @PostMapping(value = "findPageByParams")
    public Result<PageInfo<User>> findPageByParams(@RequestParam(name = "page") Integer page, @RequestParam(name = "size") Integer size, @RequestBody Map<String, Object> params) {
        return ResultUtil.success(this.userService.findPageByParams(params, page, size));
    }

}
