package org.qhn.coexist.shirojwtqhn.enums;

import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.filter.authz.PortFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.authz.SslFilter;
import org.apache.shiro.web.filter.session.NoSessionCreationFilter;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:12
 * @Description:
 */
public enum DefaultFilter {

    /**
     * anon, authc, authcBasic, user 是第一组认证过滤器，
     * perms, port, rest, roles, ssl 是第二组授权过滤器，要通过授权过滤器，
     * 就先要完成登陆认证操作（即先要完成认证才能前去寻找授权) 才能走第二组授权器（例如访问需要 roles 权限的 url，
     * 如果还没有登陆的话，会直接跳转到 shiroFilterFactoryBean.setLoginUrl(); 设置的 url ）
     */

    //无参，开放权限，可以理解为匿名用户或游客
    anon(AnonymousFilter.class),
    //无参，需要认证
    authc(FormAuthenticationFilter.class),
    //无参，表示 httpBasic 认证
    authcBasic(BasicHttpAuthenticationFilter.class),
    //无参，注销，执行后会直接跳转到shiroFilterFactoryBean.setLoginUrl(); 设置的 url
    logout(LogoutFilter.class),
    noSessionCreation(NoSessionCreationFilter.class),
    //参数可写多个，表示需要某个或某些权限才能通过，多个参数时写 perms["user, admin"]，当有多个参数时必须每个参数都通过才算通过
    perms(PermissionsAuthorizationFilter.class),
    //当请求的URL端口不是8081时，跳转到schemal://serverName:8081?queryString 其中 schmal 是协议 http 或 https 等等，
    //serverName 是你访问的 Host，8081 是 Port 端口，queryString 是你访问的 URL 里的 ? 后面的参数
    port(PortFilter.class),
    //根据请求的方法，相当于 perms[user:method]，其中 method 为 post，get，delete 等
    rest(HttpMethodPermissionFilter.class),
    //参数可写多个，表示是某个或某些角色才能通过，多个参数时写 roles["admin，user"]，当有多个参数时必须每个参数都通过才算通过
    roles(RolesAuthorizationFilter.class),
    //无参，表示安全的URL请求，协议为 https
    ssl(SslFilter.class),
    //无参，表示必须存在用户，当登入操作时不做检查
    user(UserFilter.class);

    private Class clazz;

    public Class getClazz() {
        return clazz;
    }

    DefaultFilter(Class clazz) {

    }
}
