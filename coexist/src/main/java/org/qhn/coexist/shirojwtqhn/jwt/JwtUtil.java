package org.qhn.coexist.shirojwtqhn.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecResolver;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.apache.shiro.util.Assert;

/**
 * @Auther: qihenan
 * @Date: 2019/6/10 11:02
 * @Description:
 */
public class JwtUtil {

    //  jwt的claim里一般包含以下几种数据:
    // *         1. iss -- token的发行者
    // *         2. sub -- 该JWT所面向的用户
    // *         3. aud -- 接收该JWT的一方
    // *         4. exp -- token的失效时间
    // *         5. nbf -- 在此时间段之前,不会被处理
    // *         6. iat -- jwt发布时间
    // *         7. jti -- jwt唯一标识,防止重复使用

    private static final Long EXPIRE_TIME = 120000L;

    private static final String SECRET = "qihenan";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static CompressionCodecResolver CODECRESOLVER = new DefaultCompressionCodecResolver();

    public static String createToken(String username, String password) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create().withIssuer("qhn")
            .withClaim("username", username)
            .withExpiresAt(date)
            .sign(algorithm);//到期时间 .withExpiresAt(date) //创建一个新的JWT，并使用给定的算法进行标记 .sign(algorithm);
        return token;
    }

    /**
     * @description: 验证token签名是否合法，合法返回true ,非法返回false
     **/
    public static boolean verifyToken(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("username", username)
                .withIssuer("qhn")
                .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            System.out.println("Invalid signature/claims");
            return false;
        }
    }

    /**
     * 获取登录名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @description:
     **/
    public static DecodedJWT decodeToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String payload = jwt.getPayload();
            return jwt;
        } catch (JWTDecodeException exception) {
            //Invalid token
            System.out.println("Invalid signature/claims");
            return null;
        }
    }

    /**
     * 解析JWT的Payload
     */
    public static String parseJwtPayload(String jwt) {
        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
        String base64UrlEncodedHeader = null;
        String base64UrlEncodedPayload = null;
        String base64UrlEncodedDigest = null;
        int delimiterCount = 0;
        StringBuilder sb = new StringBuilder(128);
        for (char c : jwt.toCharArray()) {
            if (c == '.') {
                CharSequence tokenSeq = io.jsonwebtoken.lang.Strings.clean(sb);
                String token = tokenSeq != null ? tokenSeq.toString() : null;

                if (delimiterCount == 0) {
                    base64UrlEncodedHeader = token;
                } else if (delimiterCount == 1) {
                    base64UrlEncodedPayload = token;
                }

                delimiterCount++;
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (delimiterCount != 2) {
            String msg =
                "JWT strings must contain exactly 2 period characters. Found: " + delimiterCount;
            throw new MalformedJwtException(msg);
        }
        if (sb.length() > 0) {
            base64UrlEncodedDigest = sb.toString();
        }
        if (base64UrlEncodedPayload == null) {
            throw new MalformedJwtException("JWT string '" + jwt + "' is missing a body/payload.");
        }
        // =============== Header =================
        Header header = null;
        CompressionCodec compressionCodec = null;
        if (base64UrlEncodedHeader != null) {
            String origValue = TextCodec.BASE64URL.decodeToString(base64UrlEncodedHeader);
            Map<String, Object> m = readValue(origValue);
            if (base64UrlEncodedDigest != null) {
                header = new DefaultJwsHeader(m);
            } else {
                header = new DefaultHeader(m);
            }
            compressionCodec = CODECRESOLVER.resolveCompressionCodec(header);
        }
        // =============== Body =================
        String payload;
        if (compressionCodec != null) {
            byte[] decompressed = compressionCodec
                .decompress(TextCodec.BASE64URL.decode(base64UrlEncodedPayload));
            payload = new String(decompressed, io.jsonwebtoken.lang.Strings.UTF_8);
        } else {
            payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
        }
        return payload;
    }

    public static Map<String, Object> readValue(String val) {
        try {
            return MAPPER.readValue(val, Map.class);
        } catch (IOException e) {
            throw new MalformedJwtException("Unable to read JSON value: " + val, e);
        }
    }

    public static void main(String[] args) {
        // 生成token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjEyMzQ1NiIsImlzcyI6ImF1dGgwIiwiZXhwIjoxNTYwMTU2NDg4LCJ1c2VybmFtZSI6ImFkbWluIn0.gMDPMr1uzhCht_Sxx7x0MutUzmFcPZMVGj1b365ic-k";
        // 打印token
        System.out.println("token: " + token);
        // 解密token
        DecodedJWT jwt = decodeToken(token);
        System.out.println("issuer: " + jwt.getIssuer());
        System.out.println("username: " + jwt.getClaim("username").asString());
        System.out.println("expire time：      " + jwt.getExpiresAt());
        System.out.println("algorithm：      " + jwt.getAlgorithm());
    }

}
