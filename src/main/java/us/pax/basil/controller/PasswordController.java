package us.pax.basil.controller;



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
 * 2021/05/10               rb
 * ============================================================================
 */

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.service.PasswordService;

@RestController
@RequestMapping("/basil/password")
@Api(tags = "Basil API Interface")
@AllArgsConstructor
public class PasswordController {
    private PasswordService passwordService;


    // Forgot password
    @PostMapping("/forgot")
    public SqlResultDTO forgot(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        return passwordService.forgotPassword(request, userEmail);
    }

    // Save password
    @PostMapping("/save")
    public SqlResultDTO savePassword(/*@Valid PasswordDto passwordDto*/) {

    	/*
        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
        */
    	return passwordService.savePassword();
    }

    // Change user password
    @GetMapping("/reset")
    public SqlResultDTO resetPassword(final HttpServletRequest request, 
    									@RequestParam(value="password", required = true) final String password,
    									@RequestParam(value="token", required = true) final String token) {
        return passwordService.resetPassword(request,password, token);
    }
}
