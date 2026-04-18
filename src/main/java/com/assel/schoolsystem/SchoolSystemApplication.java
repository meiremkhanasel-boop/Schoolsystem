package com.assel.schoolsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = {"com.assel.schoolsystem", "com.assel.school"})
@EnableAsync
@EntityScan(basePackages = "com.assel.school.model")
@EnableJpaRepositories(basePackages = "com.assel.school.repository")
@ServletComponentScan(basePackages = {"com.assel.schoolsystem", "com.assel.school"})
public class SchoolSystemApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SchoolSystemApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SchoolSystemApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> encodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public CommandLineRunner checkEnvironment(Environment env) {
        return args -> {
            System.out.println("==========================================================");
            System.out.println("✅ Active Profiles: " + Arrays.toString(env.getActiveProfiles()));
            System.out.println("✅ Datasource URL: " + env.getProperty("spring.datasource.url"));
            System.out.println("==========================================================");
        };
    }
}