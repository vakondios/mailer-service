package com.avakio.mailer.configurations;

import com.avakio.mailer.caches.AuditCache;
import com.avakio.mailer.events.AppEventPublisher;
import com.avakio.mailer.filters.RequestFilter;
import com.avakio.mailer.properties.AppSecurityProperties;
import com.avakio.mailer.security.AuthenticationSecurity;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Basic Authentication for all rest calls except from the White list
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AppSecurityProperties appSecurityProperties;
    private final AuthenticationSecurity authenticationSecurity;
    private final AuditCache auditCache;
    private final AppEventPublisher appEventPublisher;

    @Autowired
    public WebSecurityConfiguration(AppSecurityProperties appSecurityProperties,
                                    AuthenticationSecurity authenticationSecurity,
                                    AuditCache auditCache,
                                    AppEventPublisher appEventPublisher
                             ){
        this.appSecurityProperties = appSecurityProperties;
        this.authenticationSecurity = authenticationSecurity;
        this.auditCache = auditCache;
        this.appEventPublisher = appEventPublisher;
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        String[] authWhitelist = authWhitelist();

        httpSecurity.cors()
                .and().csrf().ignoringAntMatchers(authWhitelist).disable()
                .exceptionHandling().authenticationEntryPoint(authenticationSecurity)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(authWhitelist).permitAll()
                .anyRequest().authenticated()
                .and().httpBasic();

        httpSecurity.addFilterBefore(new RequestFilter(auditCache, appEventPublisher), BasicAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authentication)
            throws Exception
    {
        authentication.inMemoryAuthentication()
                .withUser(appSecurityProperties.getUsername())
                .password(bCryptPasswordEncoder().encode(appSecurityProperties.getPassword()))
                .authorities("ROLE_USER");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(ImmutableList.of("*"));
        corsConfiguration.setAllowedMethods(ImmutableList.of("*"));
        corsConfiguration.setAllowedHeaders(ImmutableList.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    private String[] authWhitelist() {
        List<String> list = appSecurityProperties.getUriAuthWhitelist();
        list.add("/v3/api-docs");
        list.add("/v3/api-docs.yaml");
        list.add("/v3/api-docs/**");
        list.add("/swagger-resources");
        list.add("/swagger-resources/**");
        list.add("/swagger-ui.html");
        list.add("/swagger-ui/**");
        list.add("/favicon.ico");
        list.add("/webjars/**");

        return list.toArray(new String[list.size()]);
    }
}
