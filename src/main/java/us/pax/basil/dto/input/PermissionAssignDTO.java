package us.pax.basil.dto.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/***
 * ============================================================================
 * = COPYRIGHT auth-web
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/9/3 	         ly
 * ============================================================================
 */
@Data
@ApiModel(value = "Permission Assign DTO")
public class PermissionAssignDTO {

    @ApiModelProperty(value = "Role ID")
    @NotNull(message = "Role ID cannot be empty")
    private Integer roleId;

    @ApiModelProperty(value = "Permission ID List")
    private List<Integer> permissionIds;
}
