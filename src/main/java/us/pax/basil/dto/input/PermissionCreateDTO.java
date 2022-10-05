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
 * 2020-05-08 17:41	     yyyty
 * ============================================================================
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("Object of creating new permission")
public class PermissionCreateDTO {

    @Length(max = 100,message = "Length of Permission Name cannot exceed 100 characters")
    @NotBlank(message ="Permission Name cannot be empty" )
    @ApiModelProperty(value = "Permission Name")
    private String name;

    @Length(max = 64,message = "Length of Permission Code cannot exceed 64 characters")
    @NotBlank(message ="Permission Code cannot be empty" )
    @ApiModelProperty(value = "Permission Code")
    private String code;

    @NotNull(message = "Menu ID cannot be empty")
    @ApiModelProperty(value = "Menu ID")
    private Integer menuId;
}

 