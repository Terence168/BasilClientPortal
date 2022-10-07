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
 * 2020/04/24               yinyy
 * ============================================================================
 */


import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.service.UserService;
import com.paxcq.cloud.common.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Basil API Interface")
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    //@PreAuthorize("hasAuthority('admin.user.create')")
    @ApiOperation(value = "Create User", notes = "Permission Code: admin.user.create")
    @PostMapping("/add")
    public SqlResultDTO addUser(HttpServletRequest request, @RequestBody User user) {
        return userService.addUser(request, user);
    }

    @ApiOperation(value = "Activate User", notes = "Permission Code: admin.user.create")
    @PostMapping("/activate")
    public SqlResultDTO activateUser(HttpServletRequest request,
    		                          @RequestParam(value="password", required=true) String password,
    		                          @RequestParam(value="token", required=true) String token) {
        return userService.activateUser(request, password, token);
    }

    @PreAuthorize("hasAuthority('admin.user.delete')")
    @ApiOperation(value = "Delete user by ID", notes = "Permission Code: admin.user.delete")
    @ApiImplicitParam(name = "id", dataType = "int", value = "User ID")
    @DeleteMapping("/{id}")
    public Result<String> deleteUserById(@PathVariable("id") Integer id) {
        userService.removeById(id);
        return Result.ok();
    }

    
    @GetMapping("/detail")
    public QueryResultArrayDTO getUserDetail(Authentication authentication) {
        return userService.getUserDetail(authentication);
    }
}
