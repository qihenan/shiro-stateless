package org.qhn.stateless.one;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: qihenan
 * @Date: 2019/5/24 15:56
 * @Description:
 */
public class LoginController {

    private static final String CLIENT_TYPE = "";

//    @RequestMapping("/login")
//    public void login(@RequestParam("code") String code, HttpServletRequest request) {

        /*Shiro框架的几个核心的管理对象。
        第一：ShiroFilterFactory：Shiro过滤器工厂类，具体的实现类是：ShiroFilterFactoryBean，此实现类是依赖于SecurityManager安全管理器的。
        第二：SecurityManager：Shiro的安全管理器，主要是身份认证的管理，缓存管理，Cookie管理，所以在时机开发中主要是和SecurityManager进行打交道的，ShiroFilterFacotory只要配置好Filter就可以了。
        第三：AccessControlFilter：访问控制过滤器，对请求进行拦截处理，在这里我们可以进行一些基本的判断以及数据的基本处理，然后生成一个AuthenticationToken，然后委托给Realm进行身份的验证和权限的验证。
        第四：Ream：用于身份信息权限的验证*/

//        Map<String, Object> data = new HashMap<>();
//        if (SecurityUtils.getSubject().isAuthenticated()) {
//            //这里代码着已经登陆成功，所以自然不用再次认证，直接从rquest中取出就行了，
//            data.put(StatelessSessionManager.HEADER_TOKEN_NAME, getServerToken());
//            data.put(BIND, ShiroKit.getUser().getTel() != null);
//            response(data);
//        }
//        LOG.info("授权码为:" + code);
//        AuthorizationService authorizationService = authorizationFactory
//            .getAuthorizationService(CLIENT_TYPE);
//        UserDetail authorization = authorizationService.authorization(code);
//
//        Oauth2UserDetail userDetail = (Oauth2UserDetail) authorization;
//
//        loginService.login(userDetail);
//        ShiroKit.getSession().setAttribute(ShiroKit.USER_DETAIL_KEY, userDetail);
//        ShiroKit.getSession().setAttribute(ShiroKit.USER_KEY, user);
//        data.put(BIND, user.getTel() != null);
//        //这里的代码，必须放到login之执行，因为login后，才会创建session，才会得到最新的token咯
//        data.put(StatelessSessionManager.HEADER_TOKEN_NAME, getServerToken());
//        response(data);
//    }

}
