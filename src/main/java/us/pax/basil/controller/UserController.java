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
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Basil API Interface")
@RestController
@RequestMapping("/user")
//@AllArgsConstructor
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
    public SqlResultDTO activateUser(HttpServletRequest request, @RequestBody  User user) {
        return userService.activateUser(request, user);
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

    @GetMapping("/query")
    public QueryResultArrayDTO queryList(@RequestParam(value = "page", required = false) Integer currentPage,
                                          @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                          @RequestParam(value = "sort", required = false) String sortColumns,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "company", required = false) Integer company,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "status", required = false) Integer status) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return userService.queryList(currentPage, sizePerPage, sortColumns, name, company, email, status);
    }

    @GetMapping("/view/query")
    public QueryResultArrayDTO viewQuery (HttpServletRequest request) {
        return userService.viewQuery(request);
    }
}
