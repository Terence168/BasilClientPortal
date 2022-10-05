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
import us.pax.basil.entity.Menu;
import us.pax.basil.vo.MenuTreeVO;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * @Author: yinyy
     * @Date: 2020-04-30 22:21
     * @MethodName: listAllMenus
     * @Description: Query all menus
     * @param:
     * @Return: java.util.List<com.paxcq.cloud.admin.auth.vo.MenuTreeVO>
     **/
    List<MenuTreeVO> listAllMenus();

}
