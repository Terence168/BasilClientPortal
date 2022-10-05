package us.pax.basil.entity.privilege;
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
 * 2022/01/27               rb
 * ============================================================================
 */


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(value="User Add/Update Object", description="")
public class UserAddUpdate {
    private String id;
    private String userName;
    private String email;
    private String creator;
    private String create_date;
    private String last_login_date;
    private Integer division;
    private Integer title;
    private Integer employeeStatus;
    private float burdenRate;
    private Integer [] roles;
}
