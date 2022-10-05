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
 * 2021/06/01               rb
 * ============================================================================
 */

import java.util.HashMap;
import java.util.Map;
import us.pax.basil.utils.ColumnMapping;

public class RmaSettingConstant {
    public final static String BASIC_SQL_PART = "select A. MENU_NAME, B.* from BASIL_ODS_PRD.SUPPORT_ATTRIBUTE A, BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES B WHERE A.SA_OID = B.SA_OID AND A.SA_OID = ";
    public final static String ORDER_BY_SORT_ORDER_ASC = " ORDER BY SORT_ORDER asc";
    public final static String ORDER_BY_SORT_ORDER_DESC = " ORDER BY SORT_ORDER desc";

    //
    // SQL column names
    //
    public final static String SQL_COLUMN_1 = "VALUE";
    public final static String SQL_COLUMN_2 = "MENU_NAME";

    //
    // Stored procedure arguments
    //
    public final static String PROCEDURE_ID = "id";
    public final static String PROCEDURE_VALUE = "value";
    public final static String PROCEDURE_DEPARTMENT = "department";
    public final static String PROCEDURE_SORT_ORDER = "sort_order";


    //
    // Common JSON key values used to return to the front-end
    //
    public final static String FRONTEND_ID = "id";

    
    /*************************************************************************/
    /*                          Reported Issue                               */
    /*************************************************************************/
    public final static int ISSUE_SA_OID = 7;
    public final static String ISSUE_SQL = BASIC_SQL_PART + ISSUE_SA_OID + ORDER_BY_SORT_ORDER_DESC;

    //
    // Reported Issue JSON key values used to return to the front-end
    //
    public final static String FRONTEND_ISSUE_REPORTED_ISSUE = "reportedIssue";


    public final static Map<String, ColumnMapping> issueQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                  private static final long serialVersionUID = 1L; 
                                                  {
                                                  put(RmaSettingConstant.FRONTEND_ISSUE_REPORTED_ISSUE, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                  }};

    public final static Map<String, ColumnMapping> issueSearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                  private static final long serialVersionUID = 1L; 
                                                  {
                                                  put(RmaSettingConstant.FRONTEND_ISSUE_REPORTED_ISSUE, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                  }};

    /*************************************************************************/
    /*                              Status                                   */
    /*************************************************************************/
    public final static int STATUS_SA_OID = 2;
    
    public final static String STATUS_SQL = "SELECT " +
                                                "SAV_OID AS value, " +
                                                "VALUE AS label, " +
                                                "VERSION " +
                                            "FROM " +
                                                "BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES " +
                                            "WHERE " +
                                                "SA_OID = 2 AND DEPARTMENT = " + QueryUtilsConstant.REPLACE_STRING1 + " " +
                                            "ORDER BY SORT_ORDER";

    //
    // Status JSON key values used to return to the front-end
    //
    public final static String FRONTEND_STATUS_STATUS = "status";
    
    public final static Map<String, ColumnMapping> statusQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_STATUS_STATUS, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    public final static Map<String, ColumnMapping> statusSearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_STATUS_STATUS, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};
    
    /*************************************************************************/
    /*                             Location                                  */
    /*************************************************************************/
    public final static Integer LOCATION_SA_OID = 3;
    public final static Integer INVENTORY_DEPARTMENTS_SA_OID = 100;
    public final static Integer MATERIAL_DEPARTMENTS_SA_OID = 104;
    public final static String INVENTORY_DEPT_FILTER = " AND DEPARTMENT = " + QueryUtilsConstant.REPLACE_STRING1;
    public final static String LOCATION_DEPTS_SQL = BASIC_SQL_PART + LOCATION_SA_OID + INVENTORY_DEPT_FILTER + ORDER_BY_SORT_ORDER_DESC;
    public final static String LOCATION_SQL = BASIC_SQL_PART + LOCATION_SA_OID + ORDER_BY_SORT_ORDER_DESC;

    //
    // Location JSON key values used to return to the front-end
    //
    public final static String FRONTEND_LOCATION_LOCATION = "location";

    public final static Map<String, ColumnMapping> locationQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_LOCATION_LOCATION, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    public final static Map<String, ColumnMapping> locationSearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_LOCATION_LOCATION, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    /*************************************************************************/
    /*                             Priority                                  */
    /*************************************************************************/
    public final static int PRIORITY_SA_OID = 4;
    public final static String PRIORITY_SQL = BASIC_SQL_PART + PRIORITY_SA_OID + ORDER_BY_SORT_ORDER_DESC;

    //
    // Priority JSON key values used to return to the front-end
    //
    public final static String FRONTEND_PRIORITY_PRIORITY = "priority";

    public final static Map<String, ColumnMapping> priorityQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_PRIORITY_PRIORITY, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    public final static Map<String, ColumnMapping> prioritySearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_PRIORITY_PRIORITY, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};
                                                   
                                                   
    /*************************************************************************/
    /*                       DEPARTMENT DROP_DOWN SQL                        */
    /*************************************************************************/
    public final static String DEPARTMENT_DROPDOWN_SQL = "SELECT " + 
                                                                  "* " +
    		                                                  "FROM " +
                                                                  "SUPPORT_DEPARTMENT " +
    		                                                  "WHERE DEPARTMENT = 100 OR DEPARTMENT =104";
                                                   

    /*************************************************************************/
    /*                       DEFAULT DROP-DOWN INDICES                       */
    /*************************************************************************/
    public final static Integer DEFAULT_VALUE_INDEX = 1;
    public final static Integer DEFAULT_LABEL_INDEX = 3;
    public final static Integer DEFAULT_VERSION_INDEX = 5;
    public final static Integer DEFAULT_DEPARTMENT_INDEX = 6;
    public final static Integer STATUS_VALUE_INDEX = 0;
    public final static Integer STATUS_LABEL_INDEX = 1;
    public final static Integer STATUS_DROP_DOWN_VERSION_INDEX = 2;
                                                            

    /*************************************************************************/
    /*                             Assignee                                  */
    /*************************************************************************/
    public final static String ASSIGNEE_SQL = "SELECT " +
                                                  "XT_OID, " +
                                                  "NAME " +
                                              "FROM " +
                                                  "EMPLOYEE_MASTER emp " +
                                                  "INNER JOIN XREF_TECHNICIAN xt USING (EMP_OID) " +
                                              "WHERE " +
                                                  "EMP_STATUS = 548 ORDER BY NAME";


    public final static Map<String, ColumnMapping> assigneeColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_PRIORITY_PRIORITY, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    public final static int ASSIGNEE_VALUE_INDEX = 0;
    public final static int ASSIGNEE_LABEL_INDEX = 1;

    /*************************************************************************/
    /*                         SERIAL NUMBER                                 */
    /*************************************************************************/
    public final static String SERIAL_NUMBER_SQL = "SELECT xm.XM_OID, msn.SERIAL_NUMBER FROM XREF_MATERIALS xm INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID WHERE xm.MO_OID = " + QueryUtilsConstant.REPLACE_STRING1;
    public final static Map<String, ColumnMapping> serialNumberQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                   private static final long serialVersionUID = 1L; 
                                                   {
                                                   put(RmaSettingConstant.FRONTEND_PRIORITY_PRIORITY, new ColumnMapping(RmaSettingConstant.SQL_COLUMN_1, true)); 
                                                   }};

    public final static int SERIAL_NUMBER_VALUE_INDEX = 0;
    public final static int SERIAL_NUMBER_LABEL_INDEX = 1;

                                                            
                                                            
    /*************************************************************************/
    /*                             FAULT CODES                               */
    /*************************************************************************/
    public final static String  SQL_FAULT_CODE_QUERY= "SELECT SFV_OID, VALUE as FAULT_CODE FROM SUPPORT_FAULT_CODE_VALUES WHERE SF_OID=1";

    public final static String  SQL_FAILURE_MODE_QUERY= "SELECT " +
                                                            "SFV_OID, VALUE AS FAILURE_MODE " +
                                                        "FROM " +
                                                            "SUPPORT_FAULT_CODE_VALUES " +
                                                        "WHERE " +
                                                            "SF_OID = 2 AND PARENT_DROP_DOWN IN (SELECT " +
                                                                                                    "SFV_OID " +
                                                                                                "FROM " +
                                                                                                    "SUPPORT_FAULT_CODE_VALUES " +
                                                                                                "WHERE " +
                                                                                                    "SF_OID = 1 AND VALUE = (SELECT " +
                                                                                                                                 "VALUE " +
                                                                                                                              "FROM " +
                                                                                                                                 "SUPPORT_FAULT_CODE_VALUES " +
                                                                                                                              "WHERE " +
                                                                                                                                 "SFV_OID = " + QueryUtilsConstant.REPLACE_STRING1 + "))";

    public final static String  SQL_TIER1_CAUSE_QUERY= "SELECT " +
                                                           "SFV_OID, VALUE AS 1st_TIER_CAUSE " +
                                                       "FROM " +
                                                           "SUPPORT_FAULT_CODE_VALUES " +
                                                       "WHERE " +
                                                           "SF_OID = 3 AND PARENT_DROP_DOWN IN (SELECT " +
                                                                                                   "SFV_OID " +
                                                                                               "FROM " +
                                                                                                   "SUPPORT_FAULT_CODE_VALUES " +
                                                                                               "WHERE " +
                                                                                                   "SF_OID = 2 AND VALUE = (SELECT " +
                                                                                                                                 "VALUE " +
                                                                                                                              "FROM " +
                                                                                                                                 "SUPPORT_FAULT_CODE_VALUES " +
                                                                                                                              "WHERE " +
                                                                                                                                 "SFV_OID = " + QueryUtilsConstant.REPLACE_STRING1 + "))";

    public final static String  SQL_TIER2_CAUSE_QUERY= "SELECT " +
                                                           "SFV_OID, VALUE AS 2nd_TIER_CAUSE " +
                                                       "FROM " +
                                                           "SUPPORT_FAULT_CODE_VALUES " +
                                                       "WHERE " +
                                                           "SF_OID = 4 AND PARENT_DROP_DOWN IN (SELECT " +
                                                                                                   "SFV_OID " +
                                                                                               "FROM " +
                                                                                                   "SUPPORT_FAULT_CODE_VALUES " +
                                                                                               "WHERE " +
                                                                                                   "SF_OID = 3 AND VALUE = (SELECT " +
                                                                                                                                 "VALUE " +
                                                                                                                              "FROM " +
                                                                                                                                 "SUPPORT_FAULT_CODE_VALUES " +
                                                                                                                              "WHERE " +
                                                                                                                                 "SFV_OID = " + QueryUtilsConstant.REPLACE_STRING1 + "))";

    public final static String  SQL_SOLUTION_QUERY= "SELECT " +
                                                        "SFV_OID, VALUE AS SOLUTION " +
                                                    "FROM " +
                                                        "SUPPORT_FAULT_CODE_VALUES " +
                                                    "WHERE " +
                                                        "SF_OID = 5 AND PARENT_DROP_DOWN IN (SELECT " +
                                                                                                "SFV_OID " +
                                                                                            "FROM " +
                                                                                                "SUPPORT_FAULT_CODE_VALUES " +
                                                                                            "WHERE " +
                                                                                                "SF_OID = 4 AND VALUE = (SELECT " +
                                                                                                                              "VALUE " +
                                                                                                                           "FROM " +
                                                                                                                              "SUPPORT_FAULT_CODE_VALUES " +
                                                                                                                           "WHERE " +
                                                                                                                              "SFV_OID = " + QueryUtilsConstant.REPLACE_STRING1 + "))";
    public final static Map<String, ColumnMapping> faultCodeQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("faultCode", new ColumnMapping("VALUE", true)); 
                                                            }};

    public final static Map<String, ColumnMapping> faultCodeSearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("faultCode", new ColumnMapping("VALUE", true)); 
                                                            }};

    /*************************************************************************/
    /*                             PART NUMBER                               */
    /*************************************************************************/
    // public final static String PART_NUMBER_SHORT_SQL = "SELECT DISTINCT PART_NUMBER_SHORT FROM BASIL_ODS_PRD.MASTER_PART ORDER BY PART_NUMBER_SHORT LIMIT 0, 10";
    public final static String PART_NUMBER_SHORT_SQL = "SELECT DISTINCT PART_NUMBER_SHORT FROM BASIL_ODS_PRD.MASTER_PART ORDER BY PART_NUMBER_SHORT";
    public final static String PART_NUMBER_LONG_SQL = "SELECT DISTINCT PART_NUMBER_LONG, MATERIAL_NUMBER FROM BASIL_ODS_PRD.MASTER_PART WHERE PART_NUMBER_SHORT = '" + QueryUtilsConstant.REPLACE_STRING1 + "' ORDER BY PART_NUMBER_LONG";
    public final static String PART_NUMBER_LONG_DROP_DOWN_SQL = "SELECT PART_OID ,CONCAT(PART_NUMBER_LONG, \" / V\",VERSION_NUMBER) PART_NUMBER FROM BASIL_ODS_PRD.MASTER_PART " +
                                                                "WHERE PART_NUMBER_LONG LIKE '%" + QueryUtilsConstant.REPLACE_STRING1 + "%' ORDER BY PART_NUMBER";

    public final static String VERSION_NUMBER_SQL = "SELECT DISTINCT VERSION_NUMBER, PART_OID FROM BASIL_ODS_PRD.MASTER_PART WHERE PART_NUMBER_LONG = '" + QueryUtilsConstant.REPLACE_STRING1 + "' ORDER BY VERSION_NUMBER";

    public final static Map<String, ColumnMapping> partNumberQueryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("partNumber", new ColumnMapping("PART_NUMBER_SHORT", true)); 
                                                            }};
    public final static Map<String, ColumnMapping> partNumberSearchColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("partNumber", new ColumnMapping("PART_NUMBER_SHORT", true)); 
                                                            }};
                                                            
                                                            
    public final static String ID = "id";
    public final static String UID = "uid";
    public final static String PUID = "puid";
    public final static String PID = "pid";
    public final static String LABEL = "label";
    public final static String HEADER = "header";
    public final static String DEPARTMENT = "department";
    public final static String MATERIAL_NUMBER = "materialNumber";
    public final static String SHORT = "short";
    public final static String PART = "part";
    public final static String IDX = "idx";
    public final static String CHILDREN = "children";
    public final static String PART_TITLE = "partTitle";
    public final static String VERSION= "version";
    public final static String VERSION_TITLE = "versionTitle";
    public final static String FAULT_CODE = "faultCode";
    public final static String FAILURE_MODE= "failureMode";
    public final static String FAILURE_MODE_TITLE = "failureModeTitle";
    public final static String VALUE = "value";
    public final static String FIRST_TIER_CAUSE = "firstTierCause";
    public final static String FIRST_TIER_CAUSE_TITLE = "firstTierCauseTitle";
    public final static String SECOND_TIER_CAUSE = "secondTierCause";
    public final static String SECOND_TIER_CAUSE_TITLE = "secondTierCauseTitle";
    public final static String SOLUTION= "solution";
    public final static String SOLUTION_TITLE = "solutionTitle";

    public final static String NAME = "name";
    public final static String RETURN = "rtn";
    public final static String SUPPORT_FAULT_OID = "sfOid";
    public final static String SUPPORT_FAULT_VALUE_OID = "sfvOid";
    public final static String SUPPORT_FAULT_VALUE_OID_NEW = "sfvOidNew";
    public final static String DESCRIPTION = "description";

    public final static Integer FIRST_TIER_CAUSE_OID = 3;
    public final static Integer SECOND_TIER_CAUSE_OID = 4;
    public final static Integer SOLUTION_OID = 5;

}
