package us.pax.basil.config;
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
 * 2018/12/20               yyyty
 * ============================================================================
 */


import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ClassUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * com.pax.cms.config
 *
 * @author yyyty
 * @time :  2018/12/20
 * @description: swagger2 configuration
 */
@EnableSwagger2
@Configuration
//Specify the environment where to turn on swagger
@Profile({"dev", "test"})
public class Swagger2Configuration {

    @Bean
    public Docket createRestfulApi() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(basePackage("us.pax.basil.controller"))
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("PAXUS Decryption Service Interface - RESTfUL APIs")
                .version("1.0")
                .description("Decryption Service")
                .termsOfServiceUrl("http://www.pax.cn/")
                .license("PAX Technology, Inc.")
                .licenseUrl("http://www.pax.cn/")
                .build();
    }

    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return (input) -> {
            return declaringClass(input).transform(handlerPackage(basePackage)).or(true);
        };
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return (input) -> {
            for (String strPackage : basePackage.split(";")) {
                if(ClassUtils.getPackageName(input).startsWith(strPackage))
                    return true;
            }
            return false;
        };
    }

    @SuppressWarnings("deprecation")
	private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }
}
