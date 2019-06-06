//package org.qhn.stateless.two;
//
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.subject.SubjectContext;
//import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
//
///**
// * @Auther: qihenan
// * @Date: 2019/5/24 17:40
// * @Description:
// */
//public class AgileSubjectFactory extends DefaultWebSubjectFactory {
//
//    public Subject createSubject(SubjectContext context) {
//        AuthenticationToken token = context.getAuthenticationToken();
//        if((token instanceof HmacToken)){
//            // 当token为HmacToken时， 不创建 session
//            context.setSessionCreationEnabled(false);
//        }
//        return super.createSubject(context);
//    }
//}
