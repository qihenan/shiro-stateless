package org.qhn.coexist.shirojwtqhn.conf;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.qhn.coexist.shirojwtqhn.jwt.JwtToken;
import org.qhn.coexist.shirojwtqhn.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:49
 * @Description:
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    private String result = null;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
        Object mappedValue) throws UnauthorizedException {
        if (((HttpServletRequest) request).getHeader("Token") != null) {
            //如果存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
            try {
                executeLogin(request, response);
                return true;
            } catch (Exception e) {
                //token 错误
                logger.error("token error", e);
                HttpServletResponse servletResponse = ((HttpServletResponse) response);
                String responseBody = e.getMessage();
                if (null != result) {
                    responseBody = responseBody.concat(result);
                }
                servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                servletResponse.setContentType("application/json;charset=UTF-8");
                servletResponse.setCharacterEncoding("UTF-8");
                PrintWriter writer = null;
                try {
                    writer = response.getWriter();
                } catch (IOException ioException) {
                    logger.error("JWTFilter.isAccessAllowed.token error", ioException);
                }
                writer.write(responseBody);
                writer.flush();
                return false;
            }
        }
        //如果请求头不存在 Token，则可能是执行登陆操作或者是游客状态访问，无需检查 token，直接返回 true
        return true;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Token");
        String username = JwtUtil.getUsername(token);
        if (null == username || !username.equals("admin")) {
            result = "用户不存在";
            return false;
        }
        DecodedJWT decodedJWT = JwtUtil.decodeToken(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        Date now = new Date();
        if (now.compareTo(expiresAt) > 0) {
            result = "token过期，请重新登录";
            return false;
        }
        JwtToken jwtToken = new JwtToken(token);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse
            .setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse
            .setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
            httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


}
