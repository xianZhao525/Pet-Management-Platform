package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Spring Boot 3.x 获取 AuthenticationManager 的正确方式
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        // 配置认证提供者
        @Bean
        public DaoAuthenticationProvider authenticationProvider(
                        PasswordEncoder passwordEncoder,
                        UserDetailsService userDetailsService) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // 静态资源放行
                                                .requestMatchers("/static/**", "/resources/**", "/public/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**")
                                                .permitAll()
                                                .requestMatchers("/favicon.ico").permitAll()
                                                // ✅ 添加根路径放行
                                                .requestMatchers("/", "/index").permitAll() // <-- 添加这一行
                                                // 公共页面放行
                                                .requestMatchers("/pet/**", "/foster/**", "/donation/**",
                                                                "/adoption/**")
                                                .permitAll()
                                                // 用户认证相关页面放行
                                                .requestMatchers("/user/login", "/user/register", "/user/logout")
                                                .permitAll()
                                                // 测试接口放行
                                                .requestMatchers("/test").permitAll()
                                                // 管理员路径 - 需要ROLE_ADMIN角色
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                // 其他请求需要认证
                                                .anyRequest().authenticated())
                                .logout(logout -> logout
                                                .logoutUrl("/user/logout")
                                                .logoutSuccessUrl("/user/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }
}