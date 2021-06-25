package com.example.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class TokenUtil {
    @Resource
    private UserRepository userRepository;
    //设置过期时间
    private static final long EXPIRE_DATE=60*60*100000;
    //token秘钥
    private static final String TOKEN_SECRET = "ZCfasfhuaUUHufguGuwu2020BQWE";
    public String token (String username, String password){

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("username",username)
                    .withClaim("password",password)
                    .sign(algorithm);
//            .withExpiresAt(date)
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }

    public Map<String, String> verify(String token) throws BisException {
        /**
         * @desc   验证token，通过返回账户密码
         * @params [token]需要校验的串
         **/
        Map<String, Claim> map;
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            map = jwt.getClaims();
            Map<String, String> resultMap = new HashMap<>(map.size());
            map.forEach((k, v) -> resultMap.put(k, v.asString()));
            return resultMap;
        }catch (Exception e){
            throw new BisException("102", "token has expired");
        }
    }

    public UserEntity verifyPassword(String account, String password) {
        UserEntity userEntity = userRepository.findOneByAccountEmail(account);
        if (userEntity == null || !userEntity.getPassword().equals(password)) {
            throw new BisException("102", "token has expired");
        }
        return userEntity;
    }
}
