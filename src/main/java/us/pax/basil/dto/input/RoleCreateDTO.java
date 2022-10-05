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
 * 2020-04-26 13:36	     yyyty
 * ============================================================================
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@ApiModel("Role Create DTO")
@Data
public class RoleCreateDTO {
    @NotBlank(message = "Role Name Cannot be Empty")
    @Length(max = 100,message = "Role name cannot exceed 100 characters")
    @ApiModelProperty(value = "Role Name")
    private String name;

    @NotBlank(message = "Role Code Cannot be Empty")
    @Length(max = 100,message = "Role code cannot exceed 100 characters")
    @ApiModelProperty(value = "Role code. Eg: admin")
    private String code;

    @Length(max = 100,message = "Note cannot exceed 100 characters")
    @ApiModelProperty(value = "Node")
    private String note;

}

 