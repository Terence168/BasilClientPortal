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
 * 2020/10/20              ly
 * ============================================================================
 */

import us.pax.basil.dto.input.DemoRequestDTO;
import us.pax.basil.dto.output.ApiResultDTO;
import us.pax.basil.enums.ApiCodeEnum;

import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Api(tags = "Decryption API Interface")
public class ApiController {

    @PostMapping("/demo")
    public ApiResultDTO DataService(@RequestBody @Validated DemoRequestDTO demoRequestDTO){
        return new ApiResultDTO(ApiCodeEnum.SUCCESS);
    }

    @GetMapping("/demo")
    public ApiResultDTO GetDataService(HttpServletRequest request) {
        return new ApiResultDTO(ApiCodeEnum.SUCCESS);
    }

}
