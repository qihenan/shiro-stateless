package org.qhn.coexist.shirojwtqhn.exception;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AccountException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 10:40
 * @Description:
 */
public class ServiceException {

    @ExceptionHandler(AccountException.class)
    public String handleShiroException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ShiroException.class)
    public String handle401() {
        return "您没有权限访问！";
    }

}
