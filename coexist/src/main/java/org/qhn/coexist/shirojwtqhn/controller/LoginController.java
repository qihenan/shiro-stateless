package org.qhn.coexist.shirojwtqhn.controller;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDO;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDOCriteria;
import org.qhn.coexist.shirojwtqhn.domain.entity.QhnUserDOCriteria.Criteria;
import org.qhn.coexist.shirojwtqhn.domain.mapper.QhnUserDOMapper;
import org.qhn.coexist.shirojwtqhn.jwt.JwtToken;
import org.qhn.coexist.shirojwtqhn.jwt.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:41
 * @Description:
 */

@RestController
public class LoginController {

    private QhnUserDOMapper qhnUserDOMapper;

    public LoginController(
        QhnUserDOMapper qhnUserDOMapper) {
        this.qhnUserDOMapper = qhnUserDOMapper;
    }

    @GetMapping(value = "/notLogin")
    public String notLogin() {
        return "未登陆！";
    }

    @GetMapping(value = "/notRole")
    public String notRole() {
        return "无权限！";
    }

    @GetMapping(value = "/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "已注销！";
    }

    @PostMapping(value = "/login")
    public String login(String username, String password) {
        QhnUserDOCriteria qhnUserDOCriteria = new QhnUserDOCriteria();
        Criteria criteria = qhnUserDOCriteria.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<QhnUserDO> qhnUserDOS = qhnUserDOMapper.selectByExample(qhnUserDOCriteria);
        if(CollectionUtils.isEmpty(qhnUserDOS)){
            return "用户不存在";
        }
        if (!password.equals(qhnUserDOS.get(0).getPassword())) {
            return "密码错误";
        }
        Subject subject = SecurityUtils.getSubject();
        String token = JwtUtil.createToken(username, password);
        JwtToken jwtToken = new JwtToken(token);
        subject.login(jwtToken);
        return token;
    }

}
