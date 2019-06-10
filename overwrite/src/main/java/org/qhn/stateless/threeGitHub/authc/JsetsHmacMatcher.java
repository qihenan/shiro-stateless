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
package org.qhn.stateless.threeGitHub.authc;

import com.google.common.base.Strings;
import java.util.Date;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.qhn.stateless.threeGitHub.cache.CacheDelegator;
import org.qhn.stateless.threeGitHub.config.MessageConfig;
import org.qhn.stateless.threeGitHub.config.ShiroProperties;
import org.qhn.stateless.threeGitHub.model.StatelessLogined;
import org.qhn.stateless.threeGitHub.service.ShiroCryptoService;
import org.qhn.stateless.threeGitHub.service.ShiroStatelessAccountProvider;
import org.qhn.stateless.threeGitHub.token.HmacToken;
import org.qhn.stateless.threeGitHub.util.Commons;

/**
 * 无状态分布式鉴权体系
 * HMAC签名匹配器
 *
 * @author wangjie (https://github.com/wj596/jsets-shiro-spring-boot-starter?_blank)
 * https://www.jianshu.com/p/0a5d3d07a151
 * @date 2016年6月31日
 */
public class JsetsHmacMatcher implements CredentialsMatcher {

    //JWT(json web token)是可在网络上传输的用于声明某种主张的令牌（token），以JSON 对象为载体的轻量级开放标准（RFC 7519）。

    //一个JWT令牌的定义包含头信息、荷载信息、签名信息三个部分：
    //Header//头信息
    //{
    //    "alg": "HS256",//签名或摘要算法
    //    "typ": "JWT"//token类型
    //}
    //Playload//荷载信息
    //{
    //    "iss": "token-server",//签发者
    //    "exp ": "Mon Nov 13 15:28:41 CST 2017",//过期时间
    //    "sub ": "wangjie",//用户名
    //    "aud": "web-server-1"//接收方,
    //    "nbf": "Mon Nov 13 15:40:12 CST 2017",//这个时间之前token不可用
    //    "jat": "Mon Nov 13 15:20:41 CST 2017",//签发时间
    //    "jti": "0023",//令牌id标识
    //    "claim": {“auth”:”ROLE_ADMIN”}//访问主张
    //}
    //Signature//签名信息
    //签名或摘要算法（
    //    base64urlencode（Header），
    //    Base64urlencode（Playload），
    //    secret-key
    //）
    //

    //按照JWT规范，对这个令牌定义进行如下操作：
    //base64urlencode（Header）
    //+"."+
    //base64urlencode（Playload）
    //+"."+
    //signature（
    //    base64urlencode（Header）
    //    +"."+
    //    base64urlencode（Playload）
    //    ,secret-key
    //）

    //形成一个完整的JWT:
    //eyJhbGciOiJIUzUxMiIsInppcCI6IkRFRiJ9.eNqqVspMLFGyMjQ1NDA1tTA0NNRRKi5NUrJSKk_MS8_KTFXSUUqtKEAoMDKsBQAAAP__.dGLe7BVECKzQ_utZJqk4hbcBZthNhohuEjjue98vmpQSGn_9cCYHq7lPIfwKubW8M553F8Uhk933EJwgI5vbLQ

    //需要注意的是：
    //1：荷载信息(Playload)中的属性可以根据情况进行设置，不要求必须全部填写。
    //2：由token的生成方式发现，Header和Playload仅仅是base64编码，通过base64解码之后可见，基本相当于是明文传输，所以应避免敏感信息放入Playload。

    //2、令牌特点
    //紧凑性：体积较小、意味着传输速度快，可以作为POST参数或放置在HTTP头。
    //自包含性：有效的负载包含用户鉴权所需所有信息，避免多次查询数据库。
    //安全性：支持对称和非对称方式(HMAC、RSA)进行消息摘要签名。
    //标准化：开放标准，多语言支持，跨平台。

    //适用场景
    //1：无状态、分布式鉴权，比如rest api系统、微服务系统。
    //2：方便解决跨域授权的问题，比如SSO单点登陆。
    //3：JWT只是消息协议，不牵涉到会话管理和存储机制，所以单体WEB应用还是推荐session-cookie机制。

    //4、安全策略
    //1：重放攻击(Replay Attacks)：应保证token只能使用一次，可以将有效期设置极短(这个时间不好控制)；如果token只使用一次，可以将token的ID放入缓存（redis、memcached）进行阅后即焚(这个可操作性强)；如果一个token需要连续穿梭多个系统进行鉴权，在最后一次使用后将token的ID放入销毁缓存（redis、memcached）。
    //2：跨站请求伪造（CSRF  Cross-site request forgery)：由于不依赖Cookie，所以一般情况下不需要考虑CSRF。
    //3：跨站脚本攻击(XSS Cross Site Scripting):相比较CSRF JWT更容易收到XSS的威胁，可以考虑使用过滤器进行处理，JAVA环境下的XSS HTMLFilter和PHP环境下的TWIG。
    //4：防止伪造令牌：如果使用公私钥密码体系，请注意公钥也应该保密，只对可信系统开放。


    private ShiroProperties properties;
    private MessageConfig messages;
    private ShiroCryptoService cryptoService;
    private ShiroStatelessAccountProvider accountProvider;
    private CacheDelegator cacheDelegator;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        HmacToken hmacToken = (HmacToken) token;
        String appId = hmacToken.getAppId();
        String digest = (String) info.getCredentials();
        String serverDigest = null;
        if (this.properties.isHmacBurnEnabled()
            && this.cacheDelegator.cutBurnedToken(digest)) {
            throw new AuthenticationException(MessageConfig.MSG_BURNED_TOKEN);
        }
        if (Commons.hasLen(this.properties.getHmacSecretKey())) {
            serverDigest = this.cryptoService.hmacDigest(hmacToken.getBaseString());
        } else {
            String appKey = accountProvider.loadAppKey(appId);
            if (Strings.isNullOrEmpty(appKey)) {
                throw new AuthenticationException(MessageConfig.MSG_NO_SECRET_KEY);
            }
            serverDigest = this.cryptoService.hmacDigest(hmacToken.getBaseString(), appKey);
        }

        if (Strings.isNullOrEmpty(serverDigest)) {
            throw new AuthenticationException(this.messages.getMsgHmacError());
        }
        if (!serverDigest.equals(digest)) {
            throw new AuthenticationException(this.messages.getMsgHmacError());
        }
        Long currentTimeMillis = System.currentTimeMillis();
        Long tokenTimestamp = Long.valueOf(hmacToken.getTimestamp());
        // 数字签名超时失效
        if ((currentTimeMillis - tokenTimestamp) > this.properties.getHmacPeriod()) {
            throw new AuthenticationException(this.messages.getMsgHmacTimeout());
        }
        // 检查账号
        boolean checkAccount = this.accountProvider.checkAccount(appId);
        if (!checkAccount) {
            throw new AuthenticationException(this.messages.getMsgAccountException());
        }
        StatelessLogined statelessAccount = new StatelessLogined();
        statelessAccount.setTokenId(hmacToken.getDigest());
        statelessAccount.setAppId(hmacToken.getAppId());
        statelessAccount.setHost(hmacToken.getHost());
        statelessAccount.setIssuedAt(new Date(tokenTimestamp));
        StatelessLocals.setAccount(statelessAccount);
        return true;
    }

    public void setProperties(ShiroProperties properties) {
        this.properties = properties;
    }

    public void setCryptoService(ShiroCryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public void setAccountProvider(ShiroStatelessAccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

    public void setMessages(MessageConfig messages) {
        this.messages = messages;
    }

    public void setCacheDelegator(CacheDelegator cacheDelegator) {
        this.cacheDelegator = cacheDelegator;
    }
}
