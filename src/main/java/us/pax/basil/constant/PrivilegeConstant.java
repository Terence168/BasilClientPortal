package us.pax.basil.constant;
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
 * 2021/07/06               rb
 * ============================================================================
 */

import java.util.HashMap;
import java.util.Map;
import us.pax.basil.utils.ColumnMapping;

public class PrivilegeConstant {
    public final static String SQL_QUERY_ROLE_TYPE = "SELECT RT_OID as id, NAME as roleType FROM BASIL_SEC_PRD.ROLE_TYPES";

    public final static String BURDEN_RATE="burdenRate";
    public final static String DIVISION_STRING="division";
    public final static String EMPLOYEE_STATUS="employeeStatus";
    public final static String TITLE_STRING="title";
    public final static String ID="id";
    public final static String NAME="name";
    public final static String NAME_UPPERCASE="NAME";
    public final static String LAST_LOGIN="lastLogin";
    public final static String REGISTER_TIME="registerTime";
    public final static String ROLES="roles";
    public final static String ROLE_ID="ROLE_ID";
    public final static String ROLE_NAME="roleName";
    public final static String ROLE_TYPE="roleType";
    public final static String ROLE_TYPE_ID="roleTypeID";
    public final static String ROLE_TYPE_NAME="roleTypeName";
    public final static String RT_ID="RT_ID";
    public final static String PRIVILEGE_ID="PRIVILEGE_ID";
    public final static String PERMISSIONS="permissions";
    public final static String TITLE_STRING_UPPERCASE="TITLE";
    public final static String USERNAME="userName";
    public final static String USERS="users";
    public final static String EMAIL="email";

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("userName", new ColumnMapping("NAME", true)); 
                                                                put("roleType", new ColumnMapping("NAME", true)); 
                                                                put("registerTime", new ColumnMapping("CREATE_DATE", true)); 
                                                                put("email", new ColumnMapping("EMAIL", true)); 
                                                                put("lastLogin", new ColumnMapping("LAST_LOGIN_DATE", true)); 
                                                            }};
}
