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

public class ScheduledWorkConstant {
    public final static String  SQL_SCHEDULE_QUERY= "SELECT " +
                                                        "xm.XM_OID AS id, " +
                                                        "mo.MO_OID AS RMA_NUMBER, " +
                                                        "xm.RECEIVED_DATE, " +
                                                        "sav_priority.VALUE PRIORITY, " +
                                                        "part.PART_NUMBER_LONG PART_NUMBER, " +
                                                        "msn.SERIAL_NUMBER, " +
                                                        "xm.SCHEDULED_DATE, " +
                                                        "sav_status.SAV_OID STATUS_OID, " +
                                                        "sav_status.VALUE STATUS, " +
                                                        "mc.CLIENT_GROUP AS CLIENT_GROUP_ID, " +

                                                        "mc.CUSTOMER_ORGANIZATION AS CUSTOMER_NAME, " +
                                                        "IF((msn.WARRANTY_END_DATE > mo.ORDER_DATE OR msn.WARRANTY_END_DATE IS NULL) " +
                                                           "AND msn.WARRANTY_VOIDED_DATE IS NULL, 'Under Warranty', 'Out of Warranty') AS WARRANTY_STATUS, " +

                                                        "sav.VALUE AS CLIENT_GROUP, " +
                                                        "xm.QUARANTINE_DATE, " +
                                                        "xm.ESD_KIT, " +
                                                        "xm.COSMETIC, " +
                                                        "xm.TECH_OPENED_DEVICE " +
                                                    "FROM " +
                                                        "MASTER_ORDER mo " +
                                                        "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                                        "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                        "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                                        "INNER JOIN SUPPORT_ATTRIBUTE_VALUES sav_status ON sav_status.SAV_OID = xm.STATUS " +
                                                        "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                                        "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                                        "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_priority ON sav_priority.SAV_OID = xm.PRIORITY " +
                                                        "LEFT JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP " +
                                                    "WHERE " +
                                                        "xm.TECHNICIAN = " + QueryUtilsConstant.REPLACE_STRING1 + " AND xm.STATUS = 110 AND xm.SCHEDULED_DATE IS NOT NULL";
    
    public final static String  SQL_SCHEDULE_VIEW_QUERY= "SELECT " +
                                                             "xm.STATUS, " +
                                                             "xm.LOCATION, " +
                                                             "xm.POWER_ADAPTER POWER_ADAPTER_OID, " +
                                                             "xm.PHOTO_UPLOADED PHOTO_UPLOADED_OID, " +
                                                             "xm.PHYSICAL_DAMAGE_PRESENT PHYSICAL_DAMAGE_PRESENT_OID, " +
                                                             "xm.RETRIEVE_TRANSACTION RETRIEVE_TRANSACTION_OID, " +
                                                             "xm.OEM_PACKAGING OEM_PACKAGING_OID, " +
                                                             "xm.CUSTOMER_ISSUE_REPRODUCED CUSTOMER_ISSUE_REPRODUCED_OID, " +
                                                             "xm.ESD_KIT ESD_KIT_OID, " +
                                                             "xm.TRANSACTION_AMOUNT, " +
                                                             "xm.REPAIR_SEVERITY, " +
                                                             "xm.TECH_NOTES, " +
                                                             "xm.TAMPER_LOG_INTERPRETATION, " +
                                                             "xm.CUSTOMER_CONTACT_NEEDED CUSTOMER_CONTACT_NEEDED_VALUE, " +
                                                             "sav.VALUE CUSTOMER_CONTACT_NEEDED_LABEL, " +
                                                             "xm.VERSION, " +
                                                             "xm.ACCESSORIES_INCLUDED, " +
                                                             "xm.CUSTOMER_REPORTED_ISSUE, " +
                                                             "xm.BATTERY_VOLTAGE, " +
                                                             "xm.SPECIAL_PROJECT_INDICATOR, " +
                                                             "xm.ERROR_MESSAGE, " +
                                                             "xm.WARRANTY_VOIDED, " +
                                                             "msn.WARRANTY_VOIDED_DATE, " +
                                                             "xm.COSMETIC, " +
                                                             "xm.TECH_OPENED_DEVICE " +
                                                         "FROM " +
                                                             "XREF_MATERIALS xm " +
                                                             "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                             "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = xm.CUSTOMER_CONTACT_NEEDED " +
                                                         "WHERE xm.XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;



    public final static String  SQL_SCHEDULE_UPDATE_QUERY= "SELECT " +
                                                               "mo.MO_OID AS rmaNumber, " +
                                                               "xm.RECEIVED_DATE AS receivedDate, " +
                                                               "sav_priority.VALUE AS priority, " +
                                                               "part.PART_NUMBER_LONG AS partNumber, " +
                                                               "msn.SERIAL_NUMBER AS serialNumber, " +
                                                               "part.VERSION_NUMBER AS versionNumber, " +
                                                               "part2.PART_NUMBER_LONG AS partNumber2, " +
                                                               "msn2.SERIAL_NUMBER AS serialNumber2, " +
                                                               "part2.VERSION_NUMBER AS versionNumber2, " +
                                                               "xm.SCHEDULED_DATE AS scheduledDate, " +
                                                               "xm.CUSTOMER_TERMINAL_ID AS customerTerminalID, " +
                                                               "mc.CUSTOMER_ORGANIZATION AS customerName, " +
                                                               "emp.NAME AS assignee, " +
                                                               "sav.VALUE AS clientGroup, " +
                                                               "xm.CUSTOMER_REPORTED_ISSUE_EXT, " +
                                                               "sav_pay_after.VALUE AS PAY_AFTER, " +
                                                               "xm.COSMETIC, " +
                                                               "xm.TECH_OPENED_DEVICE " +
                                                           "FROM " +
                                                               "XREF_MATERIALS xm " +
                                                               "INNER JOIN MASTER_ORDER mo ON xm.MO_OID = mo.MO_OID " +
                                                               "INNER JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_priority ON sav_priority.SAV_OID = xm.PRIORITY " +
                                                               "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                               "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                                               "INNER JOIN XREF_TECHNICIAN xt ON xt.XT_OID = xm.TECHNICIAN " +
                                                               "INNER JOIN EMPLOYEE_MASTER emp ON emp.EMP_OID = xt.EMP_OID " +
                                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP " +
                                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                                               "LEFT JOIN MASTER_PART part2 ON part2.PART_OID = msn2.PART_OID " +
                                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_pay_after ON sav_pay_after.SAV_OID = mc.PAY_AFTER " +
                                                           "WHERE xm.XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;

    
    public final static String  SQL_SCHEDULE_FAULT_QUERY= "SELECT " +
                                                              "XFC_OID, " +
                                                              "FAULT_CODE, " +
                                                              "(SELECT sfv.VALUE FROM SUPPORT_FAULT_CODE_VALUES sfv WHERE sfv.SFV_OID = xfc.FAULT_CODE) FAULT_CODE_LABEL, " +
                                                              "FAILURE_MODE, " +
                                                              "(SELECT sfv.VALUE FROM SUPPORT_FAULT_CODE_VALUES sfv WHERE sfv.SFV_OID = xfc.FAILURE_MODE) FAILURE_MODE_LABEL, " +
                                                              "FIRST_TIER_CAUSE, " +
                                                              "(SELECT sfv.VALUE FROM SUPPORT_FAULT_CODE_VALUES sfv WHERE sfv.SFV_OID = xfc.FIRST_TIER_CAUSE) FIRST_TIER_CAUSE_LABEL, " +
                                                              "SECOND_TIER_CAUSE, " +
                                                              "(SELECT sfv.VALUE FROM SUPPORT_FAULT_CODE_VALUES sfv WHERE sfv.SFV_OID = xfc.SECOND_TIER_CAUSE) SECOND_TIER_CAUSE_LABEL, " +
                                                              "SOLUTION, " +
                                                              "(SELECT sfv.VALUE FROM SUPPORT_FAULT_CODE_VALUES sfv WHERE sfv.SFV_OID = xfc.SOLUTION) SOLUTION_LABEL " +
                                                              "FROM " +
                                                              "XREF_FAULT_CODE xfc " +
                                                              "WHERE XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;
    
    public final static String SQL_MATERIAL_QUERY = "SELECT " +
                                                        "MM_OID, " +
                                                        "QUANTITY_FLAG " +
                                                    "FROM " +
                                                        "BASIL_ODS_PRD.XREF_REPAIR_MATERIALS_USED " +
                                                    "WHERE XM_OID=" + QueryUtilsConstant.REPLACE_STRING1;
    

    public final static Map<String, ColumnMapping> scheduleQueryColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
            put("receivedDate", new ColumnMapping("RECEIVED_DATE", true)); 
            put("dateReceived", new ColumnMapping("RECEIVED_DATE", true)); 
            put("priority", new ColumnMapping("PRIORITY", true)); 
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
            put("status", new ColumnMapping("STATUS", true)); 
            put("location", new ColumnMapping("LOCATION", true)); 
            put("id", new ColumnMapping("mo.MO_OID", true)); 
            put("clientGroup", new ColumnMapping("CLIENT_GROUP", true)); 
        }};

    public final static Map<String, ColumnMapping> scheduleSearchColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
            put("receivedDate", new ColumnMapping("RECEIVED_DATE", true)); 
            put("dateReceived", new ColumnMapping("RECEIVED_DATE", true)); 
            put("priority", new ColumnMapping("PRIORITY", false)); 
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
            put("serialNumber", new ColumnMapping("SERIAL_NUMBER", true)); 
            put("status", new ColumnMapping("STATUS", false)); 
            put("location", new ColumnMapping("LOCATION", true)); 
            put("id", new ColumnMapping("mo.MO_OID", true)); 
        }};

    //
    // JSON keys used when return data
    //
    public final static String ID = "id";
    public final static String ADAPTER = "adapter";
    public final static String ASSIGNEE = "assignee";
    public final static String CUST = "cust";
    public final static String CUSTOMER_CONTACT = "customerContact";
    public final static String CUSTOMER_NAME = "customerName";
    public final static String CUSTOMER_TERMINAL_ID = "customerTerminalID";
    public final static String DAMAGE = "damage";
    public final static String DATE_RECEIVED = "dateReceived";
    public final static String ESD_KIT = "esdKit";
    public final static String FAILURE_MODE = "failure_mode";
    public final static String FAILUREMODE= "failureMode";
    public final static String FAULT_CODE = "fault_code";
    public final static String FAULTCODE = "faultCode";
    public final static String FAULTCODES= "faultCodes";
    public final static String FILE_NAME= "file_name";
    public final static String FIRST_TIER_CAUSE= "first_tier_cause";
    public final static String FIRST_TIER= "firstTier";
    public final static String IMAGE = "image";
    public final static String IMAGES = "images";
    public final static String INVENTORY = "inventory";
    public final static String LOCATION = "location";
    public final static String MATERIAL = "material";
    public final static String MATERIAL_ID = "materialId";
    public final static String FM_OID = "fm_oid";
    public final static String MM_OID = "mm_oid";
    public final static String OEM_PACKAGING= "oemPackaging";
    public final static String OPTIONS = "options";
    public final static String PATH = "path";
    public final static String PART_NUMBER = "partNumber";
    public final static String PART_NUMBER1 = "partNumber1";
    public final static String PART_NUMBER2 = "partNumber2";
    public final static String POS_TRACKING = "posTracking";
    public final static String PRIORITY = "priority";
    public final static String QUANTITY = "quantity";
    public final static String QUARANTINE_DATE = "quarantineDate";
    public final static String RECEIVED_DATE = "receivedDate";
    public final static String REPAIR_SEVERITY = "repairSeverity";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String RETURN = "rtn";
    public final static String SCHEDULED_DATE = "scheduledDate";
    public final static String SCHEDULED_REPAIR = "scheduledRepair";
    public final static String SECOND_TIER_CAUSE= "second_tier_cause";
    public final static String SECOND_TIER = "secondTier";
    public final static String SELECTED = "selected";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String SERIAL_NUMBER1 = "serialNumber1";
    public final static String SERIAL_NUMBER2 = "serialNumber2";
    public final static String SOLUTION = "solution";
    public final static String STATUS = "status";
    public final static String STATUS_OID = "statusOid";
    public final static String TAMPER_LOG = "tamperLog";
    public final static String TECHNICIAN = "technician";
    public final static String TECH_NOTES = "techNotes";
    public final static String TERMINAL_ID = "terminalID";
    public final static String TRANSACTION = "transaction";
    public final static String TRANSACTION_AMT = "transactionAmt";
    public final static String VERSION_NUMBER1 = "versionNumber1";
    public final static String VERSION_NUMBER2 = "versionNumber2";
    public final static String WARRANTY = "warranty";
    public final static String WARRANTY_STATUS = "warrantyStatus";
    public final static String WARRANTY_VOIDED = "warrantyVoided";
    public final static String WARRANTY_VOIDED_DATE = "warrantyVoidedDate";
    public final static String XM_OID = "xm_oid";
    public final static String ZOHO_PHOTO = "zohoPhoto";

    public final static String ACCESSORIES_INCLUDED = "accessories";
    public final static String COSMETIC = "cosmetic";
    public final static String DEVICE_OPENED = "deviceOpened";
    public final static String CUSTOMER_REPORTED_ISSUE = "customerIssue";
    public final static String CUSTOMER_REPORTED_ISSUE_EXT = "customerIssueExt";
    public final static String BATTERY_VOLTAGE = "batteryVoltage";
    public final static String SPECIAL_PROJECT_INDICATOR = "specialProject";
    public final static String ERROR_MESSAGE = "errorMessage";
    public final static String PAY_AFTER = "payAfter";

    public final static String IMAGE_DIRECTORY = "images";
    

    public final static Integer QUARANTINE_STATUS = 108;
    public final static Integer AWAITING_QACA_STATUS = 111;

    public final static Integer CUSTOMER_REPORTED_ISSUE_SA_OID = 7;
    public final static Integer BATTERY_VOLTAGE_SA_OID = 8;
    public final static Integer SPECIAL_PROJECT_INDICATOR_SA_OID = 9;
    public final static Integer ERROR_MESSAGE_SA_OID = 14;
    public final static Integer SEVERITY_SA_OID = 17;
}
