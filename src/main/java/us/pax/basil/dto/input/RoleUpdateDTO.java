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
 * 2020-05-07 17:18	     yyyty
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("Object of modifying role information")
public class RoleUpdateDTO {
    @NotBlank(message = "Role name cannot be empty")
    @Length(max = 100,message = "Role name cannot exceed 100 characters")
    @ApiModelProperty(value = "Role Name")
    private String name;

    @NotBlank(message = "Role code cannot be empty")
    @Length(max = 100,message = "Role code cannot exceed 100 characters")
    @ApiModelProperty(value = "Role code. eg. admin")
    private String code;

    @Length(max = 100,message = "Note cannot exceed 100 characters")
    @ApiModelProperty(value = "Note")
    private String note;
}

 