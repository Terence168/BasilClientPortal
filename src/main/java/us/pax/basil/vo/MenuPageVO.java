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
 * 2020-04-30 16:54	     yyyty                 Menu Page DTO
 * ============================================================================
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MenuPageVO {

    @ApiModelProperty(value = "Menu ID")
    private Integer id;

    @ApiModelProperty(value = "Parent Menu ID")
    private Integer pid;

    @ApiModelProperty(value = "Menu Name")
    private String name;

    @ApiModelProperty(value = "Menu Sort Order")
    private Integer sort;

    @ApiModelProperty(value = "Menu Level  0:Content 1:Menu 2:Function")
    private Integer level;

}

 