package us.pax.basil.vo;
/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016-2020 PAX Technology, Inc. All rights reserved.
 * Description:
 *
 * Revision History:
 * Date	                 Author	                Action
 * 2020-04-30 18:31	     yyyty
 * ============================================================================
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTreeVO {

    @ApiModelProperty(value = "Menu ID")
    private Integer id;

    @ApiModelProperty(value = "Parent Menu ID")
    private Integer pid;

    @ApiModelProperty(value = "Menu Name")
    private String name;
    @ApiModelProperty(value = "Submenu")
    private List<MenuTreeVO> children;


    public void addChild(MenuTreeVO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public List<MenuTreeVO> getChildren() {
        return children == null ? new ArrayList<>() : children;
    }
}

 