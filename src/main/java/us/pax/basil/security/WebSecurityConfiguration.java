package us.pax.basil.security;
/***
 * ============================================================================
 * = COPYRIGHT Basil
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                     Author                    Action
 *
 * ============================================================================
 */

import us.pax.basil.property.CorsPathProperties;
import us.pax.basil.property.PermitAllUrlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    @Autowired
    private CorsPathProperties corsPathProperties;

    @Autowired
    private PermitAllUrlProperties permitAllUrlProperties;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailHandler failHandler;

    @Qualifier("userDetailsService")
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.eraseCredentials(false);
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui.html/**"
                , "/webjars/**"
                , "/swagger-resources/**"
                , "/v2/api-docs/**"
                , "/swagger-resources/configuration/ui/**"
                , "/swagger-resources/configuration/security/**"
                , "/images/**"
                , "/static/**"
        );
    }

    /**
     * // use bcrypt to encode the password
     * @return
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * This configuration is necessary.
     * Unless springboot will automatically configure a AuthenticationManager which will override the user in the RAM
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // use the default manager
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (permitAllUrlProperties != null && !CollectionUtils.isEmpty(permitAllUrlProperties.getUrls())) {
            // configure the resources which do not need authentication
            http.authorizeRequests().antMatchers(permitAllUrlProperties.getUrls().toArray(new String[]{})).permitAll();
        }

        // customize login interface
        http.formLogin()
                .loginProcessingUrl("/api/v1/login")
                .successHandler(successHandler)
                .failureHandler(failHandler)
            .and()
                .logout()
                .logoutUrl("/api/v1/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(new CustomLogoutSuccessHandler())
            .and()
                .exceptionHandling()
                // access not authorized resources after login (jump to error page by default)
                .accessDeniedHandler(new LoginedDeniedHandler())
                // access resources without logged in (jump to login page by default)
                .authenticationEntryPoint(new NotLoginDeniedHandler())
            .and()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .csrf().disable()
                .sessionManagement()
            .and()
                .httpBasic();


        // CORS configuration
        http.cors().configurationSource(corsConfigurationSource());

    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if(null != corsPathProperties && !CollectionUtils.isEmpty(corsPathProperties.getConfigs())){
            for(CorsPathProperties.CorsProperties cp: corsPathProperties.getConfigs()){
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowCredentials(cp.getAllowCredentials());
                corsConfiguration.setAllowedOrigins(cp.getOrigins());
                corsConfiguration.setAllowedHeaders(cp.getHeaders());
                corsConfiguration.setAllowedMethods(cp.getMethods());
                ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration(cp.getPath(), corsConfiguration);
            }
        }

        return source;
    }
}
