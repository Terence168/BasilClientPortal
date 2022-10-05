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

public class RmaShippingConstant {
    public final static String  SQL_QUERY= "SELECT " +
                                               "mo.MO_OID AS id, " +
                                               "mo.MO_OID AS RMA_NUMBER, " +
                                               "mo.VERSION AS VERSION, " +
                                               "xm.RECEIVED_DATE, " +
                                               "xm.SCHEDULED_DATE, " +
                                               "part.PART_NUMBER_LONG PART_NUMBER, " +
                                               "msn.SERIAL_NUMBER, " +
                                               "xs.SHIP_DATE, " +
                                               "sav_status.SAV_OID STATUS_OID, " +
                                               "sav_status.VALUE STATUS, " +
                                               "mc.CUSTOMER_ORGANIZATION " +
                                           "FROM " +
                                               "MASTER_ORDER mo " +
                                               "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID AND (xm.STATUS = 112 OR xm.STATUS = 113) " +
                                               "INNER JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                               "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                               "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                               "INNER JOIN SUPPORT_ATTRIBUTE_VALUES sav_status ON sav_status.SAV_OID = xm.STATUS " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                               "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 " +
                                               "LEFT JOIN XREF_SHIP xs ON xs.BOX_OID = xm.BOX_OID";
    
    public final static String  SQL_SHIPPING_VIEW= "SELECT " +
                                                       "mo.MO_OID AS RMA_NUMBER, " +
                                                       "mo.ADDRESS, " +
                                                       "mo.CITY, " +
                                                       "mo.STATE, " +
                                                       "mo.ZIP_CODE, " +
                                                       "mo.CUSTOMER_CONTACT, " +
                                                       "xs.BOX_OID, " +
                                                       "xs.BOX_DIMENSIONS, " +
                                                       "xs.WEIGHT, " +
                                                       "xs.SHIP_DATE, " +
                                                       "xs.TRACKING_NUMBER, " +
                                                       "xm.XM_OID, " +
                                                       "msn.SERIAL_NUMBER, " +
                                                       "mo.VERSION VERSION_MO, " +
                                                       "xm.VERSION VERSION_XM, " +
                                                       "xs.VERSION VERSION_XS " +
                                                   "FROM MASTER_ORDER mo " +
                                                       "INNER JOIN XREF_MATERIALS xm USING (MO_OID) " +
                                                       "INNER JOIN MASTER_SERIAL_NUMBER msn USING (MSN_OID) " +
                                                       "INNER JOIN XREF_SHIP xs USING (BOX_OID) " +
                                                   "WHERE mo.MO_OID = " + QueryUtilsConstant.REPLACE_STRING1;

    public final static String SQL_RETRIEVE_XM_OID = "SELECT " +
                                                         "XM_OID " +
                                                     "FROM " +
                                                         "XREF_MATERIALS " +
                                                     "WHERE MO_OID=" + QueryUtilsConstant.REPLACE_STRING1 + " AND MSN_OID="; 

    public final static String SQL_RETRIEVE_BOX_OID = "SELECT " +
                                                          "BOX_OID " +
                                                      "FROM " +
                                                          "XREF_SHIP " +
                                                      "WHERE " +
                                                          "TRACKING_NUMBER = '" + QueryUtilsConstant.REPLACE_STRING1 + "'";


    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("address", new ColumnMapping("address", true)); 
            put("customerContact", new ColumnMapping("CUSTOMER_CONTACT", true)); 
            put("customerName", new ColumnMapping("mc.MC_OID", true)); 
            put("customerId", new ColumnMapping("mc.MC_OID", false)); 
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
            put("dateReceived", new ColumnMapping("RECEIVED_DATE", true)); 
            put("receivedDate", new ColumnMapping("RECEIVED_DATE", true)); 
            put("scheduledDate", new ColumnMapping("SCHEDULED_DATE", true)); 
            put("shippingDate", new ColumnMapping("xs.SHIP_DATE", true)); 
            put("partNumber", new ColumnMapping("part.PART_NUMBER_LONG", true)); 
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
            put("id", new ColumnMapping("mo.MO_OID", true)); 
        }};

    //
    // JSON keys used when return data
    //
    public final static String ID = "id";
    public final static String RMA_NUMBER = "rmaNumber";
    public final static String DATE_RECEIVED = "receivedDate";
    public final static String PRIORITY = "priority";
    public final static String PART_NUMBER = "partNumber";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String SCHEDULED_DATE = "scheduledDate";
    public final static String SHIPPING_DATE = "shippingDate";
    public final static String MO_OID = "mo_oid";
    public final static String ADDRESS = "address";
    public final static String CITY = "city";
    public final static String STATE = "state";
    public final static String ZIP = "zip";
    public final static String CUSTOMER_CONTACT = "customerContact";
    public final static String CUSTOMER_NAME = "customerName";
    public final static String BOX_DIMENSIONS = "box_dimensions";
    public final static String WEIGHT = "weight";
    public final static String TRACKING_NUMBER = "tracking_number";
    public final static String SHIP_DATE = "ship_date";
    public final static String SHIPPER_NAME = "shipper_name";
    public final static String SHIPPING_NOTES = "shipping_notes";
    public final static String SHIPPING_COST = "shipping_cost";
    public final static String SHIPPING_METHOD = "shipping_method";
    public final static String STATUS_ID = "statusId";
    public final static String STATUS = "status";
    public final static String BOX_OID = "box_oid";
    public final static String XM_OID = "xm_oid";
    public final static String ADDRESS1 = "address1";
    public final static String ADDRESS2 = "address2";
    public final static String CONTACT_NAME = "contactName";
    public final static String NUMBER_OF_BOXES = "numberOfBoxes";
    public final static String BOXES = "boxes";

}
