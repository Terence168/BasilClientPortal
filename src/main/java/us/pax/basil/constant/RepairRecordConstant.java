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

public class RepairRecordConstant {
    public final static String SQL_QUERY = "SELECT " +
                                               "xm.XM_OID AS id, " +
                                               "mo.MO_OID AS RMA_NUMBER, " +
                                               "xm.RECEIVED_DATE, " +
                                               "xm.QUARANTINE_DATE, " +
                                               "part.PART_NUMBER_LONG PART_NUMBER, " +
                                               "msn.SERIAL_NUMBER, " +
                                               "xm.REPAIR_DATE COMPLETED_DATE " +
                                           "FROM " +
                                               "MASTER_ORDER mo " +
                                               "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                               "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                               "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 " +
                                           "WHERE xm.STATUS NOT IN (107 , 116)";
    

    public final static String SQL_VIEW_QUERY = "SELECT " +
    		"xm.XM_OID AS id, " +
    		"mo.MO_OID AS RMA_NUMBER, " +
    		"xm.RECEIVED_DATE, " +
    		"xm.QUARANTINE_DATE, " +
    		"part.PART_NUMBER_LONG PART_NUMBER, " +
    		"msn.SERIAL_NUMBER, " +
    		"part.VERSION_NUMBER, " +
    		"part2.PART_NUMBER_LONG PART_NUMBER2, " +
    		"msn2.SERIAL_NUMBER SERIAL_NUMBER2, " +
    		"part2.VERSION_NUMBER VERSION_NUMBER2, " +
    		"xm.REPAIR_DATE COMPLETED_DATE, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.CUSTOMER_REPORTED_ISSUE) CUSTOMER_REPORTED_ISSUE, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.ESD_KIT) ESD_KIT, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.PHYSICAL_DAMAGE_PRESENT) PHYSICAL_DAMAGE_PRESENT, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.CUSTOMER_ISSUE_REPRODUCED) CUSTOMER_ISSUE_REPRODUCED, " +
    		"xm.CUSTOMER_REPORTED_ISSUE_EXT, " +
    		"xm.TAMPER_LOG_INTERPRETATION, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.ERROR_MESSAGE) ERROR_MESSAGE, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.BATTERY_VOLTAGE) BATTERY_VOLTAGE, " +
    		"msn.WARRANTY_VOIDED_DATE, " +
    		"xm.REPAIR_DATE, " +
    		"(SELECT VALUE FROM BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES WHERE SAV_OID = xm.DEPARTMENT) DEPARTMENT, " +
    		"(SELECT TRACKING_NUMBER FROM BASIL_ODS_PRD.XREF_SHIP xs WHERE xs.BOX_OID = xm.BOX_OID) TRACKING_NUMBER, " +
    		"(SELECT CUSTOMER_ORGANIZATION FROM BASIL_ODS_PRD.MASTER_CUSTOMER mc WHERE mc.MC_OID = mo.MC_OID) CUSTOMER_NAME, " +
    		"emp.NAME ASSIGHNEE, " +
    		"xm.TECH_NOTES, " +
    		"IF((msn.WARRANTY_END_DATE > mo.ORDER_DATE OR msn.WARRANTY_END_DATE IS NULL) AND msn.WARRANTY_VOIDED_DATE IS NULL, 'Under Warranty', 'Out of Warranty') AS WARRANTY_STATUS, " +
    		"msn.WARRANTY_END_DATE WARRANTY_EXPIRATION, " +
    		"(SELECT SHIP_DATE FROM BASIL_ODS_PRD.XREF_SHIP xs WHERE xs.BOX_OID = xm.BOX_OID) SHIP_DATE " +

		"FROM " +
    		"MASTER_ORDER mo " +
    		"INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
    		"INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
    		"INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
    		"LEFT JOIN XREF_TECHNICIAN xt ON xt.XT_OID = xm.TECHNICIAN " +
    		"LEFT JOIN EMPLOYEE_MASTER emp ON emp.EMP_OID = xt.EMP_OID " +
    		"LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
    		"LEFT JOIN MASTER_PART part2 ON part2.PART_OID = msn2.PART_OID " +
        "WHERE xm.XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;

    public final static String SQL_MATERIALS_USED_QUERY = "SELECT " +
    		                                                       "xrmu.MM_OID, " +
    		                                                       "mm.MATERIAL_NUMBER, " +
    		                                                       "mm.MATERIAL_DESCRIPTION, " +
    		                                                       "xrmu.QUANTITY_FLAG " +
		                                                       "FROM " +
    		                                                       "XREF_REPAIR_MATERIALS_USED xrmu " +
    		                                                       "INNER JOIN MASTER_MATERIAL mm USING (MM_OID) " +
                                                               "WHERE XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;
    
    public final static String SQL_QAQC_HISTORY_QUERY = "SELECT " +
    		   "xq.QA_QC_TECH, " +
    		   "xq.QA_QC_DATE, " +
    		   "sav_function_test.VALUE FUNCTION_TEST_PASSED, " +
    		   "sav_tamper_sticker.VALUE TAMPER_STICKER_APPLIED, " +
    		   "sav_ca.VALUE CA_APPLIED, " +
    		   "sav_service_sticker.VALUE SERVICE_STICKER_APPLIED, " +
    		   "sav_shipping.VALUE PREPARED_FOR_SHIPPING " +
    		"FROM " +
    		   "BASIL_ODS_PRD.XREF_QA xq " +
    		   "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_function_test ON sav_function_test.SAV_OID = xq.FUNCTION_TEST_PASSED " +
    		   "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_tamper_sticker ON sav_tamper_sticker.SAV_OID = xq.TAMPER_STICKER_APPLIED " +
    		   "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_ca ON sav_ca.SAV_OID = xq.CA_APPLIED " +
    		   "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_service_sticker ON sav_service_sticker.SAV_OID = xq.SERVICE_STICKER_APPLIED " +
    		   "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_shipping ON sav_shipping.SAV_OID = xq.PREPARED_FOR_SHIPPING " +
            "WHERE xq.XM_OID = " + QueryUtilsConstant.REPLACE_STRING1;
    


    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
                                   private static final long serialVersionUID = 1L; 
                                   {
                                     put("dateReceived", new ColumnMapping("xm.RECEIVED_DATE", true)); 
                                     put("completedDate", new ColumnMapping("xm.REPAIR_DATE", true)); 
                                     put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
                                     put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER", true)); 
                                     put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
                                     put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
                                     put("customerId", new ColumnMapping("mo.MC_OID", true)); 
                                     put("assignee", new ColumnMapping("xm.TECHNICIAN", false)); 
                                   }};

    public final static String ID = "id";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String DATE_RECEIVED = "dateReceived";
    public final static String QUARANTINE_DATE = "quarantineDate";

    public final static String PART_NUMBER = "partNumber";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String VERSION_NUMBER = "versionNumber";

    public final static String PART_NUMBER2 = "partNumber2";
    public final static String SERIAL_NUMBER2 = "serialNumber2";
    public final static String VERSION_NUMBER2 = "versionNumber2";

    public final static String COMPLETED_DATE = "completedDate";
    public final static String ESD_KIT = "esdKit";
    public final static String REPORTED_ISSUE = "reportedIssue";
    public final static String REPORTED_ISSUE_EXT = "reportedIssueExt";
    public final static String ASIGHNEE = "asighnee";
    public final static String TECH_NOTES = "techNotes";
    public final static String IMAGES = "images";
    public final static String MATERIAL = "material";
    public final static String FAULT_CODES = "faultCodes";
    public final static String FAULT_CODE = "faultCode";
    public final static String FAILURE_MODE = "failureMode";
    public final static String FIRST_TIER = "firstTier";
    public final static String SECOND_TIER = "secondTier";
    public final static String SOLUTION = "solution";
    public final static String DAMAGE_PRESENT ="damagePresent";
    public final static String ISSUE_REPRODUCED ="issueReproduced";

    public final static String TAMPER_LOG = "tamperLog";
    public final static String ERROR_MESSAGE = "errorMessage";
    public final static String BATTERY_VOLTAGE = "batteryVoltage";
    public final static String WARRANTY_VOIDED_DATE = "warrantyVoidedDate";
    public final static String REPAIR_DATE = "repairDate";
    public final static String DEPARTMENT = "department";
    public final static String TRACKING_NUMBER = "trackingNumber";
    public final static String CUSTOMER_NAME = "customerName";
    public final static String WARRANTY_STATUS = "warrantyStatus";
    public final static String WARRANTY_EXPIRATION = "warrantyExpiration";
    public final static String SHIP_DATE = "shipDate";

    public final static String MATERIAL_NUMBER ="materialNumber";
    public final static String MATERIAL_NAME ="materialName";
    public final static String QUANTITY ="quantity";

    public final static String QAQC_TECH = "qaqcTech";
    public final static String QAQC_DATE = "qaqcDate";
    public final static String FUNC_TEST_PASSED = "funcTestPassed";
    public final static String TAMPER_STICKER_APPLIED = "tamperStickerApplied";
    public final static String CA_APPLIED = "caApplied";
    public final static String SERVICE_STICKER_APPLIED = "serviceStickerApplied";
    public final static String PREPARED_FOR_SHIPPING = "preparedForShipping";
    public final static String QAQC_HISTORY = "qaqcHistory";
}
