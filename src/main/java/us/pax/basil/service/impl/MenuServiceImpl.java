package us.pax.basil.service.impl;
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

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import us.pax.basil.entity.Menu;
import us.pax.basil.mapper.MenuMapper;
import us.pax.basil.service.MenuService;
import us.pax.basil.vo.MenuTreeVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private MenuMapper menuMapper;

    @Override
    public List<MenuTreeVO> getMenuTree() {

        List<MenuTreeVO> menuList = menuMapper.listAllMenus();
        List<MenuTreeVO> sortedMenus = Lists.newArrayList();

        // Get all top parent node data
        for (MenuTreeVO menu : menuList) {
            if (0 == menu.getId()) {
                sortedMenus.add(menu);
            }
        }
        // Generate tree structure
        getMenuTrees(menuList, sortedMenus);

        return sortedMenus;
    }

    /***
     * @author yinyy
     * @description Generate the tree structure of the menu
     * @date 12:41 2019-08-04
     * @param
     * @return
     **/
    private void getMenuTrees(List<MenuTreeVO> allMenus, List<MenuTreeVO> parentMenus) {
        // Add leaf node
        if (CollectionUtils.isNotEmpty(parentMenus)) {
            for (MenuTreeVO menuTree : parentMenus) {
                // Remove data which is already fetched to reduce traverse times
                if (CollectionUtils.isNotEmpty(allMenus)) {
                    for (MenuTreeVO child : allMenus) {
                        // Add it it is child. otherwise put it into the left set
                        if (child.getPid().equals(menuTree.getId())) {
                            menuTree.addChild(child);
                        }
                    }
                    // recursion generation
                    getMenuTrees(allMenus, menuTree.getChildren());
                }
            }
        }
    }

}
