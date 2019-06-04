package org.qhn.stateless.one;

import java.io.Serializable;
import java.util.UUID;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: qihenan
 * @Date: 2019/5/24 15:32
 * @Description: 无状态的sessionManager
 */
public class StatelessSessionManager extends DefaultWebSessionManager {

    /**
     * 这个是服务端要返回给客户端，
     */
    public final static String TOKEN_NAME = "TOKEN";
    /**
     * 这个是客户端请求给服务端带的header
     */
    public final static String HEADER_TOKEN_NAME = "token";
    public final static Logger LOG = LoggerFactory.getLogger(StatelessSessionManager.class);


    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable sessionId = key.getSessionId();
        if(sessionId == null){
            HttpServletRequest request = WebUtils.getHttpRequest(key);
            HttpServletResponse response = WebUtils.getHttpResponse(key);
            sessionId = this.getSessionId(request,response);
        }
        //*******************************************************//
        //把获取到的token设置到request
        //因为app调用登陆接口的时候，是没有token的，登陆成功后，产生了token,我们把它放到request中，
        //返回结果给客户端的时候，把它从request中取出来，并且传递给客户端，
        //客户端每次带着这个token过来，就相当于是浏览器的cookie的作用，也就能维护会话了
        HttpServletRequest request = WebUtils.getHttpRequest(key);
        request.setAttribute(TOKEN_NAME,sessionId.toString());
        //*******************************************************//

        return sessionId;
    }

    @Override
    protected Serializable getSessionId(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(HEADER_TOKEN_NAME);
        if(token == null){
            token = UUID.randomUUID().toString();
        }

        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
            ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());

        return token;
    }

}
