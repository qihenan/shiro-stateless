//package org.qhn.stateless.one;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import org.apache.shiro.spring.LifecycleBeanPostProcessor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.apache.shiro.mgt.SecurityManager;
//
//
///**
// * @Auther: qihenan
// * @Date: 2019/5/24 15:59
// * @Description:
// */
//@Configuration
//public class ShiroConf {
//
//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
//        return new LifecycleBeanPostProcessor();
//    }
//
//    @Bean
//    public SecurityManager securityManager(){
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setSessionManager(new StatelessSessionManager());
//        securityManager.setRealm(new ShiroUserRealm());
//        return securityManager;
//    }
//
//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
//        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
//        bean.setSecurityManager(securityManager);
//
//        Map<String,String> map = new LinkedHashMap<>();
//        map.put("/public/**","anon");
//        map.put("/login/**","anon");
//        map.put("/**","user");
//        bean.setFilterChainDefinitionMap(map);
//
//        return bean;
//    }
//
//}
