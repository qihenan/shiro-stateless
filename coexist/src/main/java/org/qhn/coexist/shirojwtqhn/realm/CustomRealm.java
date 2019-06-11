package org.qhn.coexist.shirojwtqhn.realm;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnFunctionDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnFunctionDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnRoleDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnRoleDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnRoleFunctionDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnRoleFunctionDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDOCriteria.Criteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserRoleDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserRoleDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnFunctionDOMapper;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnRoleDOMapper;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnRoleFunctionDOMapper;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnUserDOMapper;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnUserRoleDOMapper;
import org.qhn.coexist.shirojwtqhn.jwt.JwtToken;
import org.qhn.coexist.shirojwtqhn.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:30
 * @Description:
 */
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private QhnUserDOMapper qhnUserDOMapper;

    @Autowired
    private QhnRoleDOMapper qhnRoleDOMapper;

    @Autowired
    private QhnFunctionDOMapper qhnFunctionDOMapper;

    @Autowired
    private QhnUserRoleDOMapper userRoleDOMapper;

    @Autowired
    private QhnRoleFunctionDOMapper qhnRoleFunctionDOMapper;

    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     * doGetAuthenticationInfo 方法则是需要身份认证时（比如前面的 Subject.login() 方法）才会进入
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        //token.getUsername()  //获得用户名 String
        //token.getPrincipal() //获得用户名 Object
        //token.getPassword()  //获得密码 char[]
        //token.getCredentials() //获得密码 Object
        return new SimpleAuthenticationInfo(authenticationToken.getPrincipal(),
            authenticationToken.getCredentials(), this.getName());
    }

    /**
     * 获取授权信息
     *
     * @return doGetAuthorizationInfo 方法只有在需要权限认证时才会进去，
     * 比如前面配置类中配置了 filterChainDefinitionMap.put("/admin/**", "roles[admin]"); 的管理员角色，
     * 这时进入 /admin 时就会进入 doGetAuthorizationInfo 方法来检查权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String token = (String) principalCollection.getPrimaryPrincipal();
        String username = JwtUtil.getUsername(token);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        QhnUserDOCriteria qhnUserDOCriteria = new QhnUserDOCriteria();
        Criteria criteria = qhnUserDOCriteria.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<QhnUserDO> qhnUserDOS = qhnUserDOMapper.selectByExample(qhnUserDOCriteria);
        if (CollectionUtils.isNotEmpty(qhnUserDOS)) {
            QhnUserRoleDOCriteria qhnUserRoleDOCriteria = new QhnUserRoleDOCriteria();
            QhnUserRoleDOCriteria.Criteria qhnUserRoleDOCriteriaCriteria = qhnUserRoleDOCriteria
                .createCriteria();
            qhnUserRoleDOCriteriaCriteria
                .andUserIdIn(qhnUserDOS.stream().map(QhnUserDO::getId).collect(
                    Collectors.toList()));
            List<QhnUserRoleDO> qhnUserRoleDOS = userRoleDOMapper
                .selectByExample(qhnUserRoleDOCriteria);
            if (CollectionUtils.isNotEmpty(qhnUserRoleDOS)) {
                QhnRoleDOCriteria qhnRoleDOCriteria = new QhnRoleDOCriteria();
                QhnRoleDOCriteria.Criteria qhnRoleDOCriteriaCriteria = qhnRoleDOCriteria
                    .createCriteria();
                qhnRoleDOCriteriaCriteria
                    .andIdIn(qhnUserRoleDOS.stream().map(QhnUserRoleDO::getRoleId).collect(
                        Collectors.toList()));
                List<QhnRoleDO> qhnRoleDOS = qhnRoleDOMapper.selectByExample(qhnRoleDOCriteria);
                if (CollectionUtils.isNotEmpty(qhnRoleDOS)) {
                    info.setRoles(
                        qhnRoleDOS.stream().map(QhnRoleDO::getDescp).collect(Collectors.toSet()));
                    QhnRoleFunctionDOCriteria qhnRoleFunctionDOCriteria = new QhnRoleFunctionDOCriteria();
                    QhnRoleFunctionDOCriteria.Criteria qhnRoleFunctionDOCriteriaCriteria = qhnRoleFunctionDOCriteria
                        .createCriteria();
                    qhnRoleFunctionDOCriteriaCriteria
                        .andRoleIdIn(qhnRoleDOS.stream().map(QhnRoleDO::getId).collect(
                            Collectors.toList()));
                    List<QhnRoleFunctionDO> qhnRoleFunctionDOS = qhnRoleFunctionDOMapper
                        .selectByExample(qhnRoleFunctionDOCriteria);
                    if (CollectionUtils.isNotEmpty(qhnRoleFunctionDOS)) {
                        QhnFunctionDOCriteria qhnFunctionDOCriteria = new QhnFunctionDOCriteria();
                        QhnFunctionDOCriteria.Criteria qhnFunctionDOCriteriaCriteria = qhnFunctionDOCriteria
                            .createCriteria();
                        qhnFunctionDOCriteriaCriteria.andIdIn(
                            qhnRoleFunctionDOS.stream().map(QhnRoleFunctionDO::getFunctionId)
                                .collect(
                                    Collectors.toList()));
                        List<QhnFunctionDO> qhnFunctionDOS = qhnFunctionDOMapper
                            .selectByExample(qhnFunctionDOCriteria);
                        if (CollectionUtils.isNotEmpty(qhnFunctionDOS)) {
                            info.setStringPermissions(
                                qhnFunctionDOS.stream().map(QhnFunctionDO::getDescp).collect(
                                    Collectors.toSet()));
                        }
                    }
                }
            }
        }
        return info;
    }

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

}
