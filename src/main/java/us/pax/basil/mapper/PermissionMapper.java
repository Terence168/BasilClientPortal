package us.pax.basil.mapper;
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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import us.pax.basil.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    List<String> getPrivilegeListByEmpOid(int empOid);

    /**
     * @Author: yinyy
     * @Date: 2020-05-09 10:49
     * @MethodName: listPermissionsByUserId
     * @Description: query the permission of the user
     * @param: userId
     * @Return: java.util.List<java.lang.String>
     **/
    List<String> listPermissionsByUserId(@Param("userId") Integer userId);


    /**
     * @Author: yinyy
     * @Date: 2020-05-09 10:52
     * @MethodName: removeMenuPermission
     * @Description: unbind the menu with the permission
     * @param: permissionId
     * @Return: int
     **/
    int removeMenuPermission(@Param("permissionId") int permissionId);

    /**
     * @author: ly
     * @description: remove the permission associated with the menu
     * @date: 11:53 2020/8/12
     *
     * @param: menuId
     * @return: int
     */
    int removePermissionByMenuId(@Param("menuId") int menuId);

    /**
     * @author: ly
     * @description: remove the association between the menu and the permission
     * @date: 11:53 2020/8/12
     *
     * @param: menuId
     * @return: int
     */
    int removeMenuPermissionByMenuId(@Param("menuId") int menuId);
}
