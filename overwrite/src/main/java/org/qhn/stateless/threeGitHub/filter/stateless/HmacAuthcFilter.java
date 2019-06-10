/*
 * Copyright 2017-2018 the original author(https://github.com/wj596)
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package org.qhn.stateless.threeGitHub.filter.stateless;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.qhn.stateless.threeGitHub.config.MessageConfig;
import org.qhn.stateless.threeGitHub.util.Commons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于HMAC（ 散列消息认证码）的无状态认证过滤器--认证
 *
 * @author wangjie (https://github.com/wj596)
 * @date 2016年6月31日
 */
public class HmacAuthcFilter extends StatelessFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HmacAuthcFilter.class);

    /**
     * 是否放行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
        Object mappedValue) {
        Subject subject = getSubject(request, response);
        if (null != subject && subject.isAuthenticated()) {
            return true;
        }
        return false;
    }

    /**
     * 拒绝处理
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        //如果是Hmac鉴权的请求
        if (isHmacSubmission(request)) {
            //创建令牌
            AuthenticationToken token = createHmacToken(request, response);
            try {
                Subject subject = getSubject(request, response);
                subject.login(token);
                return true;
            } catch (AuthenticationException e) {
                LOGGER.error(request.getRemoteHost() + " HMAC认证  " + e.getMessage());
                Commons.restFailed(WebUtils.toHttp(response)
                    , MessageConfig.REST_CODE_AUTH_UNAUTHORIZED, e.getMessage());
            }
        }
        return false;
    }

}
