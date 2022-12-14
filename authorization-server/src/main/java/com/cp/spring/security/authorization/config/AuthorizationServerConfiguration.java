package com.cp.spring.security.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyStore;
import java.time.Duration;
import java.util.UUID;

/**
 * Copyright (C) 2022 YUNTU Inc.All Rights Reserved.
 * FileName:<??????>
 * Description: <?????????>
 * History:
 * ?????????  ??????      ??????              ??????????????????????????????
 * 1.0    CP.Chen  2022/5/25 16:30   Create
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfiguration {

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        RequestMatcher requestMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.requestMatcher(requestMatcher)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .csrf(csrf -> csrf.ignoringRequestMatchers(requestMatcher))
                .formLogin()
                .and()
                .apply(authorizationServerConfigurer);
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

//        RegisteredClient registeredClient = createRegisteredClient("1000");
//        registeredClientRepository.save(registeredClient);

        return registeredClientRepository;
    }

    private RegisteredClient createRegisteredClient(final String id) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
//               ?????????ID?????????
                .clientId("felord")
//               ????????????????????????????????????????????????
                .id(id)
//                client_secret_basic    ????????????????????????   ??????????????????
                .clientSecret(PasswordEncoderFactories.createDelegatingPasswordEncoder()
                        .encode("secret"))
//                ?????? ????????????
                .clientName("felord")
//                ????????????
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                ????????????
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                ????????????????????????????????????????????? ??????????????????IP????????????  ???????????? localhost
                .redirectUri("http://127.0.0.1:8082/login/oauth2/code/felord-client-oidc")
                .redirectUri("http://127.0.0.1:8082/authorized")
                .redirectUri("http://127.0.0.1:8082/login/oauth2/code/felord")
                .redirectUri("http://127.0.0.1:8082/foo/bar")
                .redirectUri("https://baidu.com")
//                OIDC??????
                .scope(OidcScopes.OPENID)
//                ??????Scope
                .scope("message.read")
                .scope("userinfo")
                .scope("message.write")
//                JWT???????????? ??????TTL  ????????????refreshToken??????
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofDays(7)).build())
//                ???????????????????????????????????????????????????????????? ????????????????????????
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true).build())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate) {
        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER", "ADMIN")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(jdbcTemplate.getDataSource());
//        users.createUser(user);
//        users.createUser(admin);
        return users;
    }

    /**
     * ????????????
     *
     * @param jdbcTemplate               the jdbc template
     * @param registeredClientRepository the registered client repository
     * @return the o auth 2 authorization service
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate,
                registeredClientRepository);
    }

    /**
     * Authorization consent service o auth 2 authorization consent service.
     *
     * @param jdbcTemplate               the jdbc template
     * @param registeredClientRepository the registered client repository
     * @return the o auth 2 authorization consent service
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate,
                registeredClientRepository);
    }

    /**
     * ??????JWK??????
     *
     * @return the jwk source
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        //TODO ?????????????????????
        String path = "jose.jks";
        String alias = "jose";
        String pass = "123456";

        ClassPathResource resource = new ClassPathResource(path);
        KeyStore jks = KeyStore.getInstance("jks");
        char[] pin = pass.toCharArray();
        jks.load(resource.getInputStream(), pin);
        RSAKey rsaKey = RSAKey.load(jks, alias, pin);

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings(@Value("${server.port}") Integer port) {
        return ProviderSettings.builder().issuer("http://localhost:" + port).build();
    }

}
