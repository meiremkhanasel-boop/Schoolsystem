package com.assel.schoolsystem;

import com.assel.school.filter.AuthFilter;
import com.assel.school.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthFilter authFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          AuthFilter authFilter,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.authFilter = authFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/register",
                    "/css/**", "/js/**", "/images/**",
                    "/error", "/hello-servlet",
                    "/api/auth/**",
                    "/api/main/fix-admin",
                    "/h2-console/**",
                    "/api/email/**"
                ).permitAll()

                .requestMatchers(
                    "/students/add", "/students/edit/**", "/students/delete/**",
                    "/teachers/add", "/teachers/edit/**", "/teachers/delete/**",
                    "/subjects/add", "/subjects/edit/**", "/subjects/delete/**",
                    "/ratings/add", "/ratings/delete/**",
                    "/admin/**",
                    "/api/students/add", "/api/students/update/**", "/api/students/delete/**",
                    "/api/teachers/add", "/api/teachers/update/**", "/api/teachers/delete/**"
                ).hasRole("ADMIN")

                .requestMatchers("/dashboard", "/api/stats/**").hasRole("ADMIN")

                .requestMatchers(
                    "/", "/students", "/teachers", "/subjects", "/ratings", "/profile", "/homework", "/homework/**",
                    "/profile/change-password",
                    "/api/students", "/api/students/**",
                    "/api/teachers", "/api/teachers/**"
                ).hasAnyRole("ADMIN", "USER", "TEACHER", "STUDENT")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionFixation(fix -> fix.migrateSession())
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
