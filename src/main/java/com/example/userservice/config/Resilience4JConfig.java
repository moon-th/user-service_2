package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {




    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration(){

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)  // 기준시간 동안 실패건수 기준
                .waitDurationInOpenState(Duration.ofMillis(1000)) // 기준시간
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 카운트로 할건지 시간으로 할건지
                .slidingWindowSize(2) // 두번의 카운트가 마지막에 저장
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                .build();

        // 이게 몬말이냐 ...
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(timeLimiterConfig)
                        .circuitBreakerConfig(circuitBreakerConfig)
                        .build());
    }

}
