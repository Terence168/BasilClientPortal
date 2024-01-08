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

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.service.PasswordService;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/password")
@Api(tags = "Basil API Interface")
@AllArgsConstructor
public class PasswordController {
    private PasswordService passwordService;

    @PostMapping("/forgot")
    public CompletableFuture<SqlResultDTO> forgot(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        return passwordService.forgotPasswordAsync(request, userEmail);
    }

    // Save password
    @PostMapping("/save")
    public SqlResultDTO savePassword(/*@Valid PasswordDto passwordDto*/) {
        return passwordService.savePassword();
    }

    // Change user password
    @GetMapping("/reset")
    public SqlResultDTO resetPassword(final HttpServletRequest request, 
                                        @RequestParam(value="password", required = true) final String password,
                                        @RequestParam(value="token", required = true) final String token) {
        return passwordService.resetPassword(request,password, token);
    }

    @GetMapping("/token-valid")
    public SqlResultDTO resetPassword(final HttpServletRequest request, 
                                       @RequestParam(value="token", required = true) final String token) {
        return passwordService.tokenValid(token);
    }
}
