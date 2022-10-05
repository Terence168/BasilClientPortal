package us.pax.basil.dto.input;
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
 * 2020-04-26 23:10	     yyyty
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "Menu Create DTO")
public class MenuCreateDTO {
    @ApiModelProperty(value = "Parent Menu ID. Top menu if 0", required = true)
    @NotNull(message = "Parent Menu cannot be empty")
    private Integer pid;

    @Length(max = 100,message = "Menu name cannot exceed 100 characters")
    @NotBlank(message ="Menu name cannot be empty" )
    @ApiModelProperty(value = "Menu Name", required = true)
    private String name;

    @ApiModelProperty(value = "Menu Sorted Order")
    private Integer sort;

    @NotNull(message = "Menu Level Cannot be Empty")
    @Range(min=0, max=2, message ="Incorrect Menu Level")
    @ApiModelProperty(value = "Menu Level  0:Content 1:Menu 2:Function", required = true)
    private Integer level;
}

 