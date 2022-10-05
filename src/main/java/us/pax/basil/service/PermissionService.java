package us.pax.basil.service;
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

import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.input.PermissionCreateDTO;
import us.pax.basil.dto.input.PermissionUpdateDTO;
import us.pax.basil.entity.Permission;
import java.util.List;

public interface PermissionService extends IService<Permission> {

    /**
     * @Author: yinyy
     * @Date: 2020-05-06 14:35
     * @MethodName: listPermissionsByUserId
     * @Description: Query user permissions by user id
     * @param: userId
     * @Return: java.util.List<java.lang.String>
     **/
    List<String> listPermissionsByUserId(int userId);


    /**
     * @Author: yinyy
     * @Date: 2020-05-08 17:48
     * @MethodName: createNewPermission
     * @Description: Add Permission
     * @param: permisssionCreateDTO
     * @Return: void
     **/
    void createNewPermission(PermissionCreateDTO permissionCreateDTO);


    /**
     * @Author: yinyy
     * @Date: 2020-05-09 10:44
     * @MethodName: deletePermissionById
     * @Description: Delete Permission
     * @param: permissionId
     * @Return: void
     **/
    void deletePermissionById(Integer permissionId);

    /**
     * @Author: yinyy
     * @Date: 2020-05-09 11:10
     * @MethodName: updatePermission
     * @Description: Modify Permission
     * @param: id
     * @param: permissionUpdateDTO
     * @param: sysUserInfoBO
     * @Return: void
     **/
    void updatePermission(Integer id, PermissionUpdateDTO permissionUpdateDTO);

    void deletePermissionByMenuId(Integer menuId);
}
