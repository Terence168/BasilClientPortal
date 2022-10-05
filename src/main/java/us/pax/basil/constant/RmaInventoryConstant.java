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

public class RmaInventoryConstant {
    public final static String SQL_SERIAL_NUMBER_QUERY = "SELECT " +
                                                             "MSN_OID, " +
                                                             "SERIAL_NUMBER, " +
                                                             "PART_NUMBER_LONG, " +
                                                             "VERSION_NUMBER " + 
                                                         "FROM " + 
                                                             "MASTER_SERIAL_NUMBER msn, " +
                                                             "MASTER_PART mp " + 
                                                         "WHERE " +
                                                             "msn.PART_OID = mp.PART_OID";

    public final static String SQL_INVENTORY_VIEW = "SELECT " +
                                                        "mo.MO_OID AS RMA_NUMBER, " +
                                                        "mo.SHIPPING_ADDRESS, " +
                                                        "mo.ZOHO_EMAIL, " +
                                                        "mo.VERSION MO_VERSION, " +
                                                        "mc.MC_OID, " +
                                                        "mc.CUSTOMER_ORGANIZATION, " +
                                                        "xm.XM_OID, " +
                                                        "msn.MSN_OID, " +
                                                        "msn.SERIAL_NUMBER, " +
                                                        "part.PART_NUMBER_LONG PART_NUMBER, " +
                                                        "part.VERSION_NUMBER, " +
                                                        "msn2.MSN_OID as MSN_OID2, " +
                                                        "msn2.SERIAL_NUMBER AS SERIAL_NUMBER2, " +
                                                        "part2.PART_NUMBER_LONG AS PART_NUMBER2, " +
                                                        "part2.VERSION_NUMBER AS VERSION_NUMBER2, " +
                                                        "xm.CUSTOMER_TERMINAL_ID, " +
                                                        "xm.RECEIVED_DATE, " +
                                                        "xm.POS_TRACKING_NUMBER, " +
                                                        "xm.BOX_NUMBER, " +
                                                        "xm.CUSTOMER_RMA, " +
                                                        "xm.TAMPER_LOG_INTERPRETATION, " +
                                                        "xm.CUSTOMER_REPORTED_ISSUE_EXT, " +
                                                        "xm.VERSION XM_VERSION, " +
                                                        "sav_adapter.SAV_OID POWER_ADAPTER_OID, " +
                                                        "sav_adapter.VALUE POWER_ADAPTER, " +
                                                        "sav_packaging.SAV_OID OEM_PACKAGING_OID, " +
                                                        "sav_packaging.VALUE OEM_PACKAGING, " +
                                                        "sav_status.SAV_OID STATUS_OID, " +
                                                        "sav_status.VALUE STATUS, " +
                                                        "sav_priority.SAV_OID PRIORITY_OID, " +
                                                        "sav_priority.VALUE PRIORITY, " +
                                                        "sav_location.SAV_OID LOCATION_OID, " +
                                                        "sav_location.VALUE LOCATION, " +
                                                        "mc.CLIENT_GROUP AS CLIENT_GROUP_ID, " +
                                                        "sav.VALUE AS CLIENT_GROUP, " +
                                                        "xm.ACCESSORIES_INCLUDED AS ACCESSORY, " +
                                                        "xm.DEPARTMENT " +
                                                    "FROM " +
                                                        "MASTER_ORDER mo " +
                                                        "LEFT JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                                        "LEFT JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_priority ON sav_priority.SAV_OID = xm.PRIORITY " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_status ON sav_status.SAV_OID = xm.STATUS " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_location ON sav_location.SAV_OID = xm.LOCATION " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_packaging ON sav_packaging.SAV_OID = xm.OEM_PACKAGING " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_adapter ON sav_adapter.SAV_OID = xm.POWER_ADAPTER " +
                                                        "LEFT JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                        "LEFT JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                                        "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                                        "LEFT JOIN MASTER_PART part2 ON part2.PART_OID = msn2.PART_OID " +
                                                        "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP ";

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
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
            put("statusId", new ColumnMapping("STATUS", false)); 
            put("locationId", new ColumnMapping("LOCATION", false)); 
            put("id", new ColumnMapping("mo.MO_OID", false)); 
        }};

    public final static Map<String, ColumnMapping> serialNumberQueryColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
            put("receivedDate", new ColumnMapping("RECEIVED_DATE", true)); 
            put("dateReceived", new ColumnMapping("RECEIVED_DATE", true)); 
            put("priority", new ColumnMapping("PRIORITY", true)); 
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER", true)); 
            put("status", new ColumnMapping("STATUS", true)); 
            put("location", new ColumnMapping("LOCATION", true)); 
            put("statusId", new ColumnMapping("STATUS", false)); 
            put("locationId", new ColumnMapping("LOCATION", false)); 
            put("id", new ColumnMapping("mo.MO_OID", false)); 
        }};

    // Dumped from the variable "serialNumberQueryColumnMapping" above and change "useLike" of "msn.SERIAL_NUMBER" to false
    // This is a TEMPORARY solution to fix ticket BAS-204 to avoid modifying "serialNumberQueryColumnMapping"
    //     since it was referred to in multiple calls
    // Jay - 20220425
    public final static Map<String, ColumnMapping> serialNumberQueryColumnMappingFullMatchSn = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L;
        {
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true));
            put("receivedDate", new ColumnMapping("RECEIVED_DATE", true));
            put("dateReceived", new ColumnMapping("RECEIVED_DATE", true));
            put("priority", new ColumnMapping("PRIORITY", true));
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true));
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER", false));
            put("status", new ColumnMapping("STATUS", true));
            put("location", new ColumnMapping("LOCATION", true));
            put("statusId", new ColumnMapping("STATUS", false));
            put("locationId", new ColumnMapping("LOCATION", false));
            put("id", new ColumnMapping("mo.MO_OID", false));
        }};

    //
    // map keys
    //
    public final static String ACCESSORY = "accessory";
    public final static String BOX_NUMBER = "boxNumber";
    public final static String BOX_TOTAL = "boxTotal";
    public final static String CUSTOMER_REPORTED_ISSUE = "customerReportedIssue";
    public final static String CUSTOMER_RMA = "customerRMA";
    public final static String CUSTOMER_ORGANIZATION = "customerOrganization";
    public final static String CUSTOMER_TERMINALID = "customerTerminalId";
    public final static String CUSTOMER_TERMINAL_ID = "customerTerminalID";
    public final static String DATE_RECEIVED = "dateReceived";
    public final static String DEPARTMENT = "department";
    public final static Integer INVENTORY_DEPT = 107;
    public final static String LOCATION = "location";
    public final static String LOCATION_OID = "locationOid";
    public final static String MCOID = "mcOid";
    public final static String MC_OID = "mc_oid";
    public final static String MO_OID = "mo_oid";
    public final static String MSNOID = "msnOid";
    public final static String MSNOID2 = "msnOid2";
    public final static String MSN_OID = "msn_oid";
    public final static String MSN_OID1 = "msn_oid1";
    public final static String MSN_OID2 = "msn_oid2";
    public final static String MSN_OID3 = "msn_oid3";
    public final static String MSN_OID4 = "msn_oid4";
    public final static String OEM_PACKAGING = "oemPackaging";
    public final static String OEM_PACKAGING_OID = "OemPackagingOid";
    public final static String ORIGINAL_PACKAGING = "originalPackage";
    public final static String ALTERNATIVE_RMA = "alternativeRMA";
    public final static String TAMPER_LOG = "tamperLog";
    public final static String REPORTED_ISSUE_EXT = "reportedIssueExt";
    public final static String CREATED_BY_USER_ID = "user_id"; // added by Jay for BAS-200 - 05/03/2022

    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String SERIAL_NUMBER2 = "serialNumber2";
    public final static String PART_NUMBER = "partNumber";
    public final static String PART_NUMBER2 = "partNumber2";
    public final static String VERSION_NUMBER = "versionNumber";
    public final static String VERSION_NUMBER2 = "versionNumber2";

    public final static String POS_TRACKING_NUMBER = "posTrackingNumber";
    public final static String POWER_ADAPTER_OID = "powerAdapterOid";
    public final static String POWER_ADAPTER = "powerAdapted";
    public final static String PRIORITY = "priority";
    public final static String PRIORITY_OID = "priorityOid";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String SHIPPING_ADDRESS = "shippingAddress";
    public final static String STATUS = "status";
    public final static String STATUS_OID = "statusOid";
    public final static String TAMPER_LOG_INTERPRETATION = "tamperLogInterpretation";
    public final static String XMOID = "xmOid";
    public final static String XM_OID = "xm_oid";
    public final static String VERSION_OLD_MO = "version_old_mo";
    public final static String VERSION_OLD_XM = "version_old_xm";
    public final static String ZOHO_EMAIL = "zohoEmail";

    //
    // JSON keys used when return data
    //
    public final static String ID_KEY = "id";
    public final static String RMA_NUMBER_KEY = "rmaNumber";
    public final static String RECEIVED_DATE_KEY = "receivedDate";
    public final static String PRIORITY_KEY = "priority";
    public final static String PART_NUMBER_KEY = "partNumber";
    public final static String PART_NUMBER_LONG = "partNumberLong";
    public final static String SERIAL_NUMBER_KEY = "serialNumber";
    public final static String STATUS_KEY = "status";
    public final static String LOCATION_KEY = "location";

    public final static Integer EXCEL_REQUIRED_COLUMNS = 9;
}
