package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private  UserService userService;
    private  Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try { //getInputStream 사용하여 받는 이유는 POST 방식으로 넘겨준 값은 requestParameter 형식으로 받을 수 없기 때문에 사용한다.
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

                   //토큰으로 변경된 값을 인증처리 하기 위해 getAuthenticationManager 요청 한다.
            return getAuthenticationManager()
                    //사용자가 입력한 이메일 과 PWD 를 스프링 시큐리티에서 사용할 수 있는 토큰으로 변경하여 준다. new ArrayList<>() =  어떤 권한을 가질 것인지
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    creds.getEmail(),
                                    creds.getPassword(),
                                    new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = (((User)authResult.getPrincipal()).getUsername());
        UserDto userDetails = userService.getUserDetailByEmail(userName);

       String token = Jwts.builder()
               .setSubject(userDetails.getUserId()) // 어떤 값으로 토큰을 생성 할 지
               .setExpiration(
                       new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time")))) // 언제까지 유효 할 지
               //암호화,키 조합
               .signWith(SignatureAlgorithm.HS512,env.getProperty("token.secret"))
               .compact();


       response.addHeader("token",token);
       response.addHeader("userId",userDetails.getUserId());

    }
}
