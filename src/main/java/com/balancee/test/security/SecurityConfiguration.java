package com.balancee.test.security;


import com.balancee.test.service.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    public SecurityConfiguration(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        http.
                csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/tasks**")))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/tasks/**")))
                .userDetailsService(jpaUserDetailsService)
                .headers(headers->headers.frameOptions().sameOrigin())
                .httpBasic(Customizer.withDefaults());

              return   http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain signUpFilterChain(HttpSecurity http)throws Exception{
        return http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/signup"))
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/signup")).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(csrf->csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/signup")))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    // use bcrypt to hash password
    @Bean
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }
}
