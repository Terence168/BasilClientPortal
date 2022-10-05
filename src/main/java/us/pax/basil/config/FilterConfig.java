package us.pax.basil.config;

import us.pax.basil.filter.StreamWrapperFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

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
 * Date	                 Author	                Action
 * 2020/10/21 	         ly            	    
 * ============================================================================
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<Filter> someFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setFilter(streamWrapperFilter());
        registration.addUrlPatterns("/api/*");
        registration.setName("streamWrapperFilter");
        return registration;
    }

    @Bean(name = "streamWrapperFilter")
    public Filter streamWrapperFilter() {
        return new StreamWrapperFilter();
    }
}
