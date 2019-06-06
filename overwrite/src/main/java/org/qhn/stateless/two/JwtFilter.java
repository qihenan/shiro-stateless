//package org.qhn.stateless.two;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.filter.AccessControlFilter;
//import org.apache.shiro.web.util.WebUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @Auther: qihenan
// * @Date: 2019/5/24 17:49
// * @Description:
// */
//public class JwtFilter  extends AccessControlFilter {
//
//    private static final Logger log = LoggerFactory.getLogger(AccessControlFilter.class);
//
//    public static final String DEFAULT_JWT_PARAM = "jwt";
//
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//        if (null != getSubject(request, response)
//            && getSubject(request, response).isAuthenticated()) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        if(isJwtSubmission(request)){
//            AuthenticationToken token = createToken(request, response);
//            try {
//                Subject subject = getSubject(request, response);
//                subject.login(token);
//                return true;
//            } catch (AuthenticationException e) {
//                log.error(e.getMessage(),e);
//                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
//            }
//        }
//        return false;
//    }
//
//    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
//        String jwt = request.getParameter(DEFAULT_JWT_PARAM);
//        String host = request.getRemoteHost();
//        log.info("authenticate jwt token:"+jwt);
//        System.out.println("jwt:"+jwt);
//        return new JwtToken(jwt, host);
//    }
//
//    protected boolean isJwtSubmission(ServletRequest request) {
//        String jwt = request.getParameter(DEFAULT_JWT_PARAM);
//        return (request instanceof HttpServletRequest)
//            && StringUtils.isNotBlank(jwt);
//    }
//
//}
//
