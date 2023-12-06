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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import us.pax.basil.constant.PrivilegeConstant;
import us.pax.basil.constant.SupportAttributeConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.customer.Customer;
import us.pax.basil.entity.privilege.PasswordChange;
import us.pax.basil.entity.privilege.Role;
import us.pax.basil.entity.privilege.RoleType;
import us.pax.basil.service.PrivilegeService;
import us.pax.basil.service.SupportAttributeService;
import us.pax.basil.service.UserService;

@RestController
@RequestMapping("/privilege")
@Api(tags = "Decryption API Interface")
@AllArgsConstructor
public class PrivilegeController {

    private PrivilegeService privilegeService;
    private UserService userService;
    private SupportAttributeService supportAttributeService;
    private EntityManager entityManager;

    // AddCustomer() - Add a new customer record to the database.
    @PreAuthorize("hasAnyAuthority('privilege.role-type.add')")
    @PostMapping("/role-type/add")
    public SqlResultDTO addRoleType(@RequestBody RoleType roleType) {
        return privilegeService.AddRoleType(roleType);
    }

    // UpdateCustomer() - Update customer information.  Required fields will be manually
    //                    checked so that a SQLResultDTO can be returned.
    @PreAuthorize("hasAnyAuthority('privilege.role-type.update')")
    @PostMapping("/role-type/update")
    public SqlResultDTO updateRoleType(@RequestBody RoleType roleType) {
        return privilegeService.UpdateRoleType(roleType);
    }

    // QueryCustomer() - Retrieve customer information based on the parameters passed in.
    //                   The parameters can be dynamic, therefore, need to use HttpServletRequest.
    @PreAuthorize("hasAnyAuthority('privilege.role-type.view')")
    @GetMapping("/role-type/query")
    public QueryResultArrayDTO queryRoleType(HttpServletRequest request) {
        return privilegeService.queryRoleType(entityManager, PrivilegeConstant.queryColumnMapping, request);
    }

    @PreAuthorize("hasAnyAuthority('privilege.role.add')")
    @PostMapping("/role/add")
    public SqlResultDTO addRole(@RequestBody Role role) {
        return privilegeService.AddRole(role);
    }

    // UpdateCustomer() - Update customer information.  Required fields will be manually
    //                    checked so that a SQLResultDTO can be returned.
    @PreAuthorize("hasAnyAuthority('privilege.role.update')")
    @PostMapping("/role/update")
    public SqlResultDTO updateRole(@RequestBody Role role) {
        return privilegeService.UpdateRole(role);
    }

    // QueryCustomer() - Retrieve customer information based on the parameters passed in.
    //                   The parameters can be dynamic, therefore, need to use HttpServletRequest.
    @PreAuthorize("hasAnyAuthority('privilege.role')")
    @GetMapping("/role/query")
    public QueryResultArrayDTO queryRole(HttpServletRequest request) {
        return privilegeService.QueryRole(request);
    }

    // View() - Retrieve customer information based on the parameters passed in.
    //                   The parameters can be dynamic, therefore, need to use HttpServletRequest.
    @PreAuthorize("hasAnyAuthority('privilege.role')")
    @GetMapping("/role/view/query")
    public QueryResultArrayDTO roleViewQuery(HttpServletRequest request, @RequestParam Integer id) {
    	// request.getSession().invalidate();
        return privilegeService.ViewQueryRole(request, id);
    }

    /**
     * Query the full role list under all role types.
     * Update user need this privilege.
     * @return the {@link QueryResultArrayDTO} which stores the desired list of roles and role types
     */
    @PreAuthorize("hasAnyAuthority('privilege.role-type', 'privilege.user.update')")
    @GetMapping("/user/all-roles")
    public QueryResultArrayDTO queryAllRoles() {
        return privilegeService.queryAllRoles();
    }

    @PreAuthorize("hasAnyAuthority('privilege')")
    @GetMapping("/all-privileges/query")
    public QueryResultArrayDTO queryAllPrivileges(HttpServletRequest request) {
        return privilegeService.queryAllPrivileges(request);
    }

    @PreAuthorize("hasAnyAuthority('privilege.role')")
    @GetMapping("/tree")
    public QueryResultArrayDTO queryUserPrivileges(HttpServletRequest request) {
    	return privilegeService.queryUserPrivileges(request);
    }

    /**
     * Query the user list which is stored in the database table "EMPLOYEE_MASTER"
     * @param currentPage Indicating the current page for pagination query. Default current page is the 1st page
     * @param sizePerPage Indicating the number of items for each page. Default size is 10
     * @param sort The required sort string. Example: field1,field2.desc,field3.asc
     * @param userName The query filter with username specified
     * @param email The query filter with email specified
     * @param registerTime The query filter with registered date-time specified
     * @param lastLogin The query filter with last login date-time specified
     * @return the {@link QueryResultArrayDTO} which stores the desired list of users
     */
    @PreAuthorize("hasAnyAuthority('privilege.user')")
    @GetMapping("/user/query")
    public QueryResultArrayDTO userQuery(@RequestParam(value = "page", required = false) Integer currentPage,
                                         @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                         @RequestParam(value = "sort", required = false) String sort,
                                         @RequestParam(value = "userName", required = false) String userName,
                                         @RequestParam(value = "email", required = false) String email,
                                         @RequestParam(value = "registerTime", required = false) String registerTime,
                                         @RequestParam(value = "lastLogin", required = false) String lastLogin,
                                         @RequestParam(value = "status", required = false) Integer status) {
        if (null == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }


        return userService.queryPrivilegeUsers(currentPage,
                                                sizePerPage,
                                                sort,
                                                userName,
                                                email,
                                                registerTime,
                                                lastLogin,
                                                status);
    }

    //
    // userViewQuery() - Retrieve customer information based on the parameters passed in.
    //
    @PreAuthorize("hasAnyAuthority('privilege.user')")
    @GetMapping("/user/view/query")
    public QueryResultArrayDTO userViewQuery(@RequestParam Integer id) {
        return privilegeService.viewQueryUser(id);
    }

    //
    // userAdd() - Retrieve customer information based on the parameters passed in.
    //
    @PreAuthorize("hasAnyAuthority('privilege.user.add')")
    @PostMapping("/user/add")
    public SqlResultDTO userAdd(HttpServletRequest request, @RequestBody User user) {
        return userService.addUser(request, user);
    }

    //
    // userUpdate() - Retrieve customer information based on the parameters passed in.
    //
    @PreAuthorize("hasAnyAuthority('privilege.user.update')")
    @PostMapping("/user/update")
    public SqlResultDTO userUpdate(@RequestBody User user) {
        return privilegeService.updateUser(user);
    }

    // passwordChange() - Change user password
    //  id:  Is equal to EMPLOYEE_MASTER.EMP_OID
    //  newPassword:  This is the encoded password that the front-end generates from the user entered password
    @PostMapping("/user/password-change")
    public SqlResultDTO passwordChange(@RequestBody PasswordChange passwordChange) {
        return privilegeService.changePassword(passwordChange);
    }


    @PreAuthorize("hasAnyAuthority('customer.info.update')")
    @PutMapping("/company")
    public QueryResultDTO updateCompany(@RequestBody Customer customer){
        return privilegeService.updateCustomer(customer);
    }

    @GetMapping("/company")
    public QueryResultDTO getCompany(){
        return privilegeService.getCustomer();
    }
}
