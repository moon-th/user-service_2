package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    /**
     * 권한에 관련된 configure
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();  // actuator 요청 설정
        http.authorizeRequests().antMatchers("/**") //모든 요청에 따라
                .hasIpAddress("127.0.0.1") // 설정된 IP 만 접근 을 허용한다.
                .and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();

    }

    /**
     * 인증 처리 Filter
     * @return
     * @throws Exception
     */
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(),userService,env);
        //filter.setAuthenticationManager(authenticationManager());
        return filter;
    }


    /**
     * 인증에 관련된 configure
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //사용자가 전달했던 정보를 가지고 로그인 처리를 해준다.
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
