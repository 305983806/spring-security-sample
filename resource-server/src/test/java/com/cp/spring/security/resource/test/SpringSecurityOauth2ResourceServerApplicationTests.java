package com.cp.spring.security.resource.test;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyStore;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<类名>
 * Description: <类说明>
 * History:
 * 版本号  作者      日期              简要操作以及相关介绍
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@SpringBootTest
public class SpringSecurityOauth2ResourceServerApplicationTests {

    private final JWKSource<SecurityContext> jwkSource = jwkSource();

    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * 通过以下命令行，生成公私钥
     *
     * 以下命令行使用keytool生成2048长度的RSA密钥证书， jose.jks 文件，alias(密钥别名) 值为 jose，密码为 123456，有效期为 356 天，它是一个二进制文件，包含一对RSA公私钥。
     * keytool -genkey -alias jose  -keyalg RSA -storetype PKCS12 -keysize 2048 -validity 365 -keystore /Users/peidongchen/Workspaces/cp/spring-security-sample/resource-server/src/main/resources/jose.jks -storepass 123456  -dname "CN=(felord), OU=(felord), O=(felord), L=(zz), ST=(hn), C=(cn)"
     *
     * 再通过以下命令提取出公钥文件 pub.cer
     * keytool -export -alias jose -keystore /Users/peidongchen/Workspaces/cp/spring-security-sample/resource-server/src/main/resources/jose.jks  -file /Users/peidongchen/Workspaces/cp/spring-security-sample/resource-server/src/main/resources/pub.cer
     */

    /**
     * 资源服务器不应该生成JWT 但是为了测试 假设这是个认证服务器
     */
    @Test
    public void imitateAuthServer() {

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .jwkSetUrl("https://127.0.0.1:9000/oauth2/jwks")
                .type("JWT")
                .build();

        Instant issuedAt = Clock.system(ZoneId.of("Asia/Shanghai")).instant();

        long exp = 604800L;
        Instant expiresAt = issuedAt.plusSeconds(exp);
        Instant notBefore = issuedAt.minusSeconds(60);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .subject("cp.Chen")
//                .audience(Collections.singletonList("https://resourceserver.felord.cn"))
                .expiresAt(expiresAt)
                .issuedAt(issuedAt)
                .notBefore(notBefore)
                .id(UUID.randomUUID().toString())
                .claim("scope", Arrays.asList("message.read", "message.write"))
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters
                .from(jwsHeader, jwtClaimsSet);
        Jwt jwt = jwtEncoder.encode(parameters);

        String token = jwt.getTokenValue();
        System.out.println("json web token —> "+token);
    }

    private JWKSource<SecurityContext> jwkSource() {
        try {
            ClassPathResource resource = new ClassPathResource("jose.jks");
            KeyStore jks = KeyStore.getInstance("jks");
            String pass = "123456";
            char[] pem = pass.toCharArray();
            jks.load(resource.getInputStream(), pem);

            /**
             * 生成 JWK，JWK：JWT的密钥，也就是我们常说的 secret。
             * 也可以从公钥文件 pub.cer 中加载 JWK，请参考 {@link com.cp.spring.security.resource.config.JwtDecoderConfiguration#jwtDecoder(DelegatingOAuth2TokenValidator)}
             */
            RSAKey rsaKey = RSAKey.load(jks, "jose", pem);
            JWKSet jwkSet = new JWKSet(rsaKey);
            return new ImmutableJWKSet<>(jwkSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
