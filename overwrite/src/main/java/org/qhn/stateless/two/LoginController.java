//package org.qhn.stateless.two;
//
//import com.google.common.collect.Maps;
//import io.jsonwebtoken.CompressionCodecs;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import java.util.Date;
//import java.util.Map;
//import java.util.UUID;
//import javax.xml.bind.DatatypeConverter;
//import org.apache.logging.log4j.util.Strings;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @Auther: qihenan
// * @Date: 2019/5/24 17:41
// * @Description:
// */
//@RestController
//@RequestMapping("/auth")
//public class LoginController {
//
//    private final String SECRET_KEY = "*(-=4eklfasdfarerf41585fdasf";
//
//    @RequestMapping(value="/apply-token",method= RequestMethod.POST)
//    public Map<String,Object> applyToken(@RequestParam(name="clientKey") String clientKey) {
//        // 签发一个Json Web Token
//        // 令牌ID=uuid，用户=clientKey，签发者=clientKey
//        // token有效期=1分钟，用户角色=null,用户权限=create,read,update,delete
//        String jwt = issueJwt(UUID.randomUUID().toString(), clientKey,
//            "token-server",60000l, null, "create,read,update,delete");
//        Map<String,Object> respond = Maps.newHashMap();
//        respond.put("jwt", jwt);
//        return respond;
//    }
//
//    /**
//     * @param id 令牌ID
//     * @param subject 用户ID
//     * @param issuer 签发人
//     * @param period 有效时间(毫秒)
//     * @param roles 访问主张-角色
//     * @param permissions 访问主张-权限
//     * @param algorithm 加密算法
//     * @return json web token
//     */
//    private String issueJwt(String id,String subject,String issuer,Long period,String roles
//        ,String permissions, SignatureAlgorithm algorithm) {
//        long currentTimeMillis = System.currentTimeMillis();// 当前时间戳
//        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);// 秘钥
//        JwtBuilder jwt  =  Jwts.builder();
//        if(Strings.isNotBlank(id)) jwt.setId(id);
//        jwt.setSubject(subject);// 用户名主题
//        if(Strings.isNotBlank(issuer)) jwt.setIssuer(issuer);//签发者
//        if(Strings.isNotBlank(issuer)) jwt.setIssuer(issuer);//签发者
//        jwt.setIssuedAt(new Date(currentTimeMillis));//签发时间
//        if(null != period){
//            Date expiration = new Date(currentTimeMillis+period);
//            jwt.setExpiration(expiration);//有效时间
//        }
//        if(Strings.isNotBlank(roles)) jwt.claim("roles", roles);//角色
//        if(Strings.isNotBlank(permissions)) jwt.claim("perms", permissions);//权限
//        jwt.compressWith(CompressionCodecs.DEFLATE);//压缩，可选GZIP
//        jwt.signWith(algorithm, secretKeyBytes);//加密设置
//        return jwt.compact();
//    }
//
//}
