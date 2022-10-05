package us.pax.basil.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/***
 * ============================================================================
 * = COPYRIGHT auth-service
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/8/10 	         ly            	    
 * ============================================================================
 */
@Component
@ConfigurationProperties(prefix = "security.cors")
@Data
public class CorsPathProperties {
    private List<CorsProperties> configs;

    @Data
    static public class CorsProperties {
        private String path;

        private List<String> origins;

        private List<String> headers;

        private List<String> methods;

        private Boolean allowCredentials = false;

        private Long maxAge = 3600L;
    }
}
