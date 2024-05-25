package com.boot.smc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig  {

    @Bean
    UserDetailsService getUserDetailsService() {
		
		return new UserDetailServiceImpl();
	}
	
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
    	
    	return new BCryptPasswordEncoder();
    }
    
    
//    @Bean
//    DaoAuthenticationProvider daoAuthenticationProvider() {
//    	
//    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//    	
//    	authenticationProvider.setUserDetailsService(getUserDetailsService());
//    	authenticationProvider.setPasswordEncoder(passwordEncoder());
//    	
//    	
//    	return authenticationProvider;
//    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    	
    	return authenticationConfiguration.getAuthenticationManager();
    }
    
    
   
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
     http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/admin/**").hasRole("ADMIN")
    			.requestMatchers("/user/**").hasRole("USER").requestMatchers("/**").permitAll()).formLogin(login -> login.loginPage("/signin").loginProcessingUrl("/signin").
    					defaultSuccessUrl("/user/index")).csrf(csrf -> csrf.disable());
    	
     return http.build();
    }
    
    

}
