package org.qhn.coexist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoexistApplication {

    //1:jwt安全吗
    //2:防盗:增加一个“Token吊销”过程来应对Token被盗的情形，类似于当发现银行卡或电话卡丢失，用户主动挂失的过程
    //https://stormpath.com/blog/manage-authentication-lifecycle-mobile
    //关于“Token吊销”的实现，文章建议个方式如下：
    //在DB中记录用户对应的Token
    //实现一个Api Endpoint，负责将指定用户的Token从DB中删除
    //3:

    public static void main(String[] args) {
        SpringApplication.run(CoexistApplication.class, args);
    }

}
