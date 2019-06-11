package org.qhn.coexist.shirojwtqhn.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDO;
import org.qhn.coexist.shirojwtqhn.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:37
 * @Description:
 */
@RestController
public class UserController {

    private UserService userServiceImpl;

    public UserController(UserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping(value = "/guest/enter")
    public String login() {
        return "欢迎进入，您的身份是游客";
    }

    @GetMapping(value = "/guest/getMessage")
    public String submitLogin() {
        return "您拥有获得游客接口的信息的权限！";
    }

    @GetMapping(value = "/user/getUserDetail")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    @RequiresPermissions("getUserDetail")
    public String getUserMessage() {
        QhnUserDO qhnUserDO = userServiceImpl.getUserDetailById(1L);
        return "您拥有普通用户的权限，可以获得获取用户信息的权限！当前用户为：".concat(qhnUserDO.toString());
    }

    @GetMapping(value = "/admin/setRoleFunction")
    @RequiresRoles("admin")
    @RequiresPermissions("setRoleFunction")
    public String getAdminMessage() {
        return "您拥有管理员权限，可以获得设置用户角色和权限接口的权限！";
    }

}
