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
 * 2020-04-25 23:15	     yyyty
 * ============================================================================
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("Object of transporting the new created user")
@Data
public class UserCreateDTO {
    private String name;
    private String companyId;
    private String email;
}

 