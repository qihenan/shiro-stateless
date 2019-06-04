package org.qhn.stateless;

import org.qhn.stateless.threeGitHub.config.EnableJsetsShiro;
import org.qhn.stateless.threeGitHub.jdbc.config.EnableJsetsJdbc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJsetsJdbc
@EnableJsetsShiro
public class OverwriteApplication {

    //浏览器中打开http://localhost:8080/shiro-demo 进入系统
    //使用用户名"admin"，密码"123"登陆，在"用户管理"界面中可以看到4个测试账号密码都是123，您可以用这些账号进行测试。
    public static void main(String[] args) {
        SpringApplication.run(OverwriteApplication.class, args);
    }

}
