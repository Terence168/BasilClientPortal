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

public class QaCaConstant {
    public final static String SQL_QUERY = "SELECT " + 
                                               "xm.XM_OID AS id, " +
                                               "mo.MO_OID AS RMA_NUMBER, " +
                                               "xm.RECEIVED_DATE, " +
                                               "sav_priority.VALUE PRIORITY, " +
                                               "sav_priority.SAV_OID PRIORITY_SAV_OID, " +
                                               "part.PART_NUMBER_LONG PART_NUMBER, " +
                                               "msn.SERIAL_NUMBER, " +
                                               "xm.SCHEDULED_DATE, " +
                                               "xm.REPAIR_DATE COMPLETED_DATE, " +
                                               "sav_status.VALUE STATUS, " +
                                               "xq.QA_NOTES, " +
                                               "xq.QA_OID, " +
                                               "xm.VERSION AS XM_VERSION " +
                                           "FROM " +
                                               "MASTER_ORDER mo " +
                                               "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                               "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                               "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                               "INNER JOIN SUPPORT_ATTRIBUTE_VALUES sav_status ON sav_status.SAV_OID = xm.STATUS " +
                                               "LEFT JOIN XREF_QA xq on xq.XM_OID = xm.XM_OID " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_priority ON sav_priority.SAV_OID = xm.PRIORITY " +
                                           "WHERE xm.STATUS = 111";

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
                              private static final long serialVersionUID = 1L; 
                              {
                                  put("dateReceived", new ColumnMapping("xm.RECEIVED_DATE", true)); 
                                  put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
                                  put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER", true)); 
                                  put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
                                  put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
                                  put("priority", new ColumnMapping("sav_priority.SAV_OID", true)); 
                                  put("priorityId", new ColumnMapping("sav_priority.SAV_OID", false)); 
                              }};
                                                            
    public final static String TEST_PASSED = "testPassed";
    public final static String CA = "ca";
    public final static String TAMPER_STICKER = "tamperSticker";
    public final static String SERVICE_STICKER = "serviceSticker";
    public final static String SHIPPING = "shipping";
    public final static String STATUS = "status";
    public final static String TECH = "tech";
    public final static String XM_OID = "xm_oid";
    public final static String QA_OID_OUT = "qa_oid";
    public final static String NOTES = "notes";
    public final static String ID = "id";
    public final static String LOCATION = "location";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String DATE_RECEIVED = "dateReceived";
    public final static String PRIORITY = "priority";
    public final static String PART_NUMBER = "partNumber";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String SCHEDULED_DATE = "scheduledDate";
    public final static String COMPLETED_DATE = "completedDate";
    public final static String SHIPMENT_STATUS = "shipmentStatus";
    public final static String TECH_NOTES = "techNotes";
    public final static String QA_OID = "qaOid";
    public final static String QA_FAULTS = "qaFaults";
    public final static String VERSION_QA = "version_qa";
    public final static String VERSION_XM = "version_xm";
    public final static String VERSION_OLD_QA = "version_old_qa";
    public final static String VERSION_OLD_XM = "version_old_xm";

    public final static Integer OUT_FOR_REPAIR = 110;
    public final static Integer READY_TO_SHIP_LOCATION = 292;
    
    // radio button "No" values
    public final static Integer FUNCTION_TEST_PASSED_NO = 471;
    public final static Integer CA_APPLIED_NO = 473;
    public final static Integer TAMPER_STICKER_APPLIED_NO = 475;
    public final static Integer SERVICE_STICKER_APPLIED_NO = 477;
    public final static Integer PREPARED_FOR_SHIPPING_NO = 479;
}
