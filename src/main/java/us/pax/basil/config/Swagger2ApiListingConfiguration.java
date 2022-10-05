package us.pax.basil.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Sets;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * 2020/10/20 	         ly            	    
 * ============================================================================
 */
@Component
public class Swagger2ApiListingConfiguration implements ApiListingScannerPlugin {
    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        Operation usernameLoginOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("User Login")
                .notes("User Name / Password Login")
                .consumes(Sets.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) // Request Parameter Format
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE)) // Returning Parameter Format
                .tags(Sets.newHashSet("Login Interface"))
                .uniqueId("userLogin")
                .parameters(Arrays.asList(
                        new ParameterBuilder()
                                .description("User Name")
                                .type(new TypeResolver().resolve(String.class))
                                .name("username")
                                .defaultValue("")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .build(),
                        new ParameterBuilder()
                                .description("Password")
                                .type(new TypeResolver().resolve(String.class))
                                .name("password")
                                .defaultValue("")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .build()
                ))
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("Request Successful")
                                .responseModel(new ModelRef("com.paxcq.cloud.common.dto.Result")).build()))
                .build();

        Operation usernameLogoutOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("User Logout")
                .notes("User Logout")
                .consumes(Sets.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) // Request Parameter Format
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE)) // Returning Parameter Format
                .tags(Sets.newHashSet("Login Interface"))
                .uniqueId("userLogout")
                .responseMessages(Collections.singleton(
                        new ResponseMessageBuilder().code(200).message("Request Successful")
                                .responseModel(new ModelRef("com.paxcq.cloud.common.dto.Result")).build()))
                .build();

        ApiDescription loginApiDescription = new ApiDescription("login", "/login", "Login Interface",
                Arrays.asList(usernameLoginOperation), false);

        ApiDescription logoutApiDescription = new ApiDescription("login", "/logout", "Logout Interface",
                Arrays.asList(usernameLogoutOperation), false);

        return Arrays.asList(loginApiDescription, logoutApiDescription);
    }

    /**
     * Whether to user this plugin
     *
     * @param documentationType swagger documentation type
     * @return true - enable
     */
    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }
}
