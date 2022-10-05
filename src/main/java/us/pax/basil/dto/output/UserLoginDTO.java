package us.pax.basil.dto.output;
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
 * 2020-05-06 16:45	     yyyty
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("User Login DTO")
public class UserLoginDTO {
    @ApiModelProperty(value = "User ID")
    private Integer id;

    @ApiModelProperty(value = "Login Name")
    private String userName;

    @ApiModelProperty(value = "Login Password")
    private String password;

    @ApiModelProperty(value = "User Status")
    private Integer status;

    @ApiModelProperty(value = "User Role List")
    private List<String> roles;

}

 