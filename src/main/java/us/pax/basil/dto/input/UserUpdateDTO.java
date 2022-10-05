package us.pax.basil.dto.input;
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
 * 2020/06/18               moulh@paxsz.com
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author : moulh@paxsz.com
 * @time : 2020/6/18
 * @description : Object for transporting modified user
 */
@ApiModel("Object for transporting modified user")
@Data
public class UserUpdateDTO {
    @NotBlank(message = "User ID cannot be empty")
    @ApiModelProperty(value = "user id")
    private Integer userId;

    @NotBlank(message = "old user password cannot be empty")
    @ApiModelProperty(value = "old user password")
    private String oldPassword;

    @NotBlank(message = "new user password cannot be empty")
    @ApiModelProperty(value = "new user password")
    private String newPassword;
}
