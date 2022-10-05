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

public class RmaSchedulingConstant {
    public final static String  SQL_QUERY= "SELECT " +
                                               "xm.XM_OID AS id, " +
                                               "mo.MO_OID AS RMA_NUMBER, " +
                                               "xm.RECEIVED_DATE, " +
                                               "sav_priority.VALUE PRIORITY, " +
                                               "part.PART_NUMBER_LONG PART_NUMBER, " +
                                               "msn.SERIAL_NUMBER, " +
                                               "xm.BOX_NUMBER, " +
                                               "xm.SCHEDULED_DATE, " +
                                               "xm.QUARANTINE_DATE, " +
                                               "xt.XT_OID, " +
                                               "emp.NAME ASSIGNEE, " +
                                               "sav_status.SAV_OID STATUS_OID, " +
                                               "sav_status.VALUE STATUS, " +
                                               "sav_location.VALUE SAV_LOCATION, " +
                                               "xm.VERSION, " +
                                               "xm.PRIORITY AS PRIORITY_ID, " +
                                               "mc.CLIENT_GROUP AS CLIENT_GROUP_ID, " +
                                               "sav.VALUE AS CLIENT_GROUP, " +
                                               "xm.LOCATION, " +
                                               "sav_department.VALUE DEPARTMENT " +
                                           "FROM " +
                                               "MASTER_ORDER mo " +
                                               "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                               "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                               "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_status ON sav_status.SAV_OID = xm.STATUS " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_department ON sav_department.SAV_OID = xm.DEPARTMENT " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_location ON sav_location.SAV_OID = xm.LOCATION " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_priority ON sav_priority.SAV_OID = xm.PRIORITY " +
                                               "LEFT JOIN XREF_TECHNICIAN xt ON xt.XT_OID = xm.TECHNICIAN " +
                                               "LEFT JOIN EMPLOYEE_MASTER emp ON emp.EMP_OID = xt.EMP_OID " +
                                               "LEFT JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP ";

    public final static String  SQL_QUERY_TECH= "SELECT " +
                                                    "short_number, " +
                                                    "repaired_devices, " +
                                                    "active_assignments " +
                                                "FROM " +
                                                    "(SELECT " +
                                                         "trh.PART_NUMBER_SHORT AS short_number, " +
                                                         "trh.XT_OID, " +
                                                         "trh.NUMBER_REPAIRED_DEVICES AS repaired_devices " +
                                                      "FROM " +
                                                         "TECH_REPAIR_HIST trh " +
                                                      "WHERE " +
                                                         "trh.XT_OID = " + QueryUtilsConstant.REPLACE_STRING1 + " AND trh.PART_NUMBER_SHORT = " +
                                                                 "(SELECT mp.PART_NUMBER_SHORT " +
                                                                     "FROM " +
                                                                         "XREF_MATERIALS xm " +
                                                                         "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                                         "INNER JOIN MASTER_PART mp ON mp.PART_OID = msn.PART_OID " +
                                                                     "WHERE " +
                                                                         "xm.XM_OID = " + QueryUtilsConstant.REPLACE_STRING2 + " LIMIT 1)) A " +
                                                                         "LEFT JOIN (SELECT " +
                                                                                        "COUNT(*) AS active_assignments, " +
                                                                                        "TECHNICIAN " +
                                                                                    "FROM " +
                                                                                        "XREF_MATERIALS " +
                                                                                    "WHERE " +
                                                                                        "STATUS = 110 GROUP BY TECHNICIAN) B ON A.XT_OID = B.TECHNICIAN";

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("assignee", new ColumnMapping("xt.XT_OID" , false)); 
            put("rmaNumber", new ColumnMapping("mo.MO_OID" , true)); 
            put("dateReceived", new ColumnMapping("RECEIVED_DATE" , true)); 
            put("receivedDate", new ColumnMapping("RECEIVED_DATE" , true)); 
            put("scheduledDate", new ColumnMapping("SCHEDULED_DATE" , true)); 
            put("priority", new ColumnMapping("PRIORITY" , true)); 
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG" , true)); 
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
            put("status", new ColumnMapping("sav_status.SAV_OID" , false)); 
            put("id", new ColumnMapping("mo.MO_OID" , true)); 
            put("clientGroupId", new ColumnMapping("sav.VALUE" , false)); 
            put("savLocation", new ColumnMapping("sav_location.VALUE" , false)); 
        }};

    //
    // JSON keys used when return data
    //
    public final static String ID = "id";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String QUARANTINE_DATE = "quarantineDate";
    public final static String RECEIVED_DATE = "receivedDate";
    public final static String PRIORITY = "priority";
    public final static String CUSTOMER_NAME = "customerName";
    public final static String PRIORITY_ID = "priorityId";
    public final static String PART_NUMBER = "partNumber";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String BOX_NUMBER = "boxNumber";
    public final static String SCHEDULEDDATE = "scheduledDate";
    public final static String XT_OID = "xtOid";
    public final static String ASSIGNEE = "assignee";
    public final static String STATUS_OID = "statusOid";
    public final static String XM_OID = "xm_oid";
    public final static String SCHEDULED_DATE = "scheduled_date";
    public final static String STATUS = "status";
    public final static String TECHNICIAN = "technician";
    public final static String LOCATION = "location";
    public final static String SAV_LOCATION = "savLocation";
    public final static String SHORT_NUMBER = "short_number";
    public final static String REPAIRED_DEVICES = "repaired_devices";
    public final static String ACTIVE_ASSIGNMENTS = "active_assignments";
    public final static String DEPARTMENT = "department";
    public final static String CONTACT_NEEDED = "contact";
}
