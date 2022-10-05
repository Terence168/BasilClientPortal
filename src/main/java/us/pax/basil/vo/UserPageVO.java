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
 * 2020-04-29 14:37	     yyyty
 * ============================================================================
 */


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserPageVO {
    @ApiModelProperty(value = "User ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "Login Name")
    private String userName;

    @ApiModelProperty(value = "Nickname")
    private String nickName;


    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "Phone")
    private String phone;

    @ApiModelProperty(value = "Status: 0-inactive, 1-disabled, 2-active, -1: deleted")
    private Integer status;

}

 