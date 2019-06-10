package org.qhn.stateless.threeGitHub.realm;

import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.qhn.stateless.threeGitHub.service.ShiroStatelessAccountProvider;
import org.qhn.stateless.threeGitHub.token.HmacToken;

/**
 * 基于HMAC（ 散列消息认证码）的控制域
 *
 * @author wangjie (https://github.com/wj596)
 * @date 2016年6月31日
 */
public class HmacRealm extends AuthorizingRealm {

    private ShiroStatelessAccountProvider accountProvider;

    public Class<?> getAuthenticationTokenClass() {
        return HmacToken.class;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException {
        // 只认证HmacToken
        if (!(token instanceof HmacToken)) {
            return null;
        }
        HmacToken hmacToken = (HmacToken) token;
        String appId = hmacToken.getAppId();
        String digest = hmacToken.getDigest();
        return new SimpleAuthenticationInfo("hmac:{" + appId + "}", digest, this.getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String payload = (String) principals.getPrimaryPrincipal();
        if (payload.startsWith("hmac:") && payload.charAt(5) == '{'
            && payload.charAt(payload.length() - 1) == '}') {
            String appId = payload.substring(6, payload.length() - 1);
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<String> roles = this.accountProvider.loadRoles(appId);
            Set<String> permissions = this.accountProvider.loadPermissions(appId);
            if (null != roles && !roles.isEmpty()) {
                info.setRoles(roles);
            }
            if (null != permissions && !permissions.isEmpty()) {
                info.setStringPermissions(permissions);
            }
            return info;
        }
        return null;
    }

    public void setAccountProvider(ShiroStatelessAccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }
}
