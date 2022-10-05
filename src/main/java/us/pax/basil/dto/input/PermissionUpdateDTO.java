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
 * 2020-05-09 11:08	     yyyty
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("Object of Modifying Permission")
public class PermissionUpdateDTO {
    @Length(max = 100,message = "Permission Name cannot exceed 100 characters")
    @NotBlank(message ="Permission name cannot be empty" )
    @ApiModelProperty(value = "Permission Name")
    private String name;

    @Length(max = 64,message = "Permission Code cannot exceed 64 characterss")
    @NotBlank(message ="Permission Code Cannot be Empty" )
    @ApiModelProperty(value = "Permission Code")
    private String code;

    @NotNull(message = "Menu ID cannot be empty")
    @ApiModelProperty(value = "Menu ID")
    private Integer menuId;
}

 