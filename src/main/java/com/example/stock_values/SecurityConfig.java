package com.example.stock_values;

import com.example.stock_values.Models.UserEntity;
import com.example.stock_values.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    UserRepository userRepository;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
    //Adding valid user credentials.
       List<UserEntity> usersList= userRepository.findAll();
        List<UserDetails> userDetails = new ArrayList<>();
       for (UserEntity user:usersList) {
            UserDetails normal = User.withUsername(user.getEmail())
                    .password(passwordEncoder().encode(user.getPassword()))
                    .roles("User")
                    .build();
            userDetails.add(normal);
        }

        return new InMemoryUserDetailsManager(userDetails);
    }

    // subscribe api will require authentication and all other will be open.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/subscribe").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin();

        return httpSecurity.build();
    }

}

