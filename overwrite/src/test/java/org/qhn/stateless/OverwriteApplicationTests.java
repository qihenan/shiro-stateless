package org.qhn.stateless;

import java.util.Map;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OverwriteApplicationTests {

    @Test
    public void contextLoads() {


    }

//    @Test
//    public String applyToken(){
//        Long current = System.currentTimeMillis() ;
//        String url = "http://localhost:8080/tokenServer/auth/apply-token";
//        MultiValueMap<String, Object> dataMap = new LinkedMultiValueMap<String, Object>();
//        String clientKey = "administrator";// 客户端标识（用户名）
//        String mix = String.valueOf(new Random().nextFloat());// 随机数，进行混淆
//        String timeStamp = current.toString();// 时间戳
//        dataMap.add("clientKey", clientKey);
//        dataMap.add("mix", mix);
//        dataMap.add("timeStamp", timeStamp);
//        String baseString = clientKey+mix+timeStamp;
//        String digest =  hmacDigest(baseString);// 生成HMAC摘要
//        dataMap.add("digest", digest);
//        Map result = rt.postForObject(url, dataMap, Map.class);
//        return (String)result.get("jwt");
//    }

}
