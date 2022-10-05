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

public class RmaInvoiceConstant {
    public final static String  SQL_QUERY= "SELECT " +
                                               "mo.MO_OID AS id, " +
                                               "mo.MO_OID AS RMA_NUMBER, " +
                                               "MAX(xs.SHIP_DATE) SHIPPING_DATE, " +
                                               "COUNT(DISTINCT xm.XM_OID) NUMBER_OF_UNITS, " +
                                               "SUM(fm.MEASURE) TOTAL_INVOICE, " +
                                               "mo.INVOICE_NUMBER INVOICE_NUMBER, " +
                                               "mo.INVOICE_NUMBER2 INVOICE_NUMBER2, " +
                                               "mc.CUSTOMER_ORGANIZATION, " +
                                               "mc.CLIENT_GROUP AS CLIENT_GROUP_ID, " +
                                               "sav.VALUE AS CLIENT_GROUP, " +
                                               "mc.MC_OID AS MC_OID, " +
                                               "IF(mo.INVOICE_PAID = 'yes' AND mo.INVOICE_PAID2 = 'yes', 'Paid', 'Unpaid') AS PAYMENT_STATUS " +
                                           "FROM " +
                                               "MASTER_ORDER mo " +
                                               "INNER JOIN XREF_MATERIALS xm USING (MO_OID) " +
                                               "LEFT JOIN XREF_SHIP xs ON xs.BOX_OID = xm.BOX_OID " +
                                               "LEFT JOIN FACT_MEASURE fm ON fm.XM_OID = xm.XM_OID AND fm.MEASURE_OID IN (6 , 7) " +
                                               "LEFT JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP " +
                                            "GROUP BY mo.MO_OID HAVING mo.MO_OID IN (SELECT " +
                                                         "DISTINCT mo.MO_OID " +
                                                      "FROM " +
                                                         "MASTER_ORDER mo " +
                                                         "INNER JOIN XREF_MATERIALS xm USING (MO_OID) " +
                                                         "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                         "LEFT JOIN MASTER_SERIAL_NUMBER msn2 ON msn2.MSN_OID = xm.MSN_OID2 " +
                                                         "LEFT JOIN MASTER_SERIAL_NUMBER msn3 ON msn3.MSN_OID = xm.MSN_OID3 " +
                                                         "LEFT JOIN MASTER_SERIAL_NUMBER msn4 ON msn4.MSN_OID = xm.MSN_OID4 )";

    public final static String  SQL_INVOICE_VIEW= "SELECT " +
                                                      "mo.ZOHO_LINK, " +
                                                      "mo.INVOICE_LINK, " +
                                                      "mo.INVOICE_LINK2, " +
                                                      "mo.INVOICE_NUMBER, " +
                                                      "mo.INVOICE_NUMBER2, " +
                                                      "mo.INVOICE_PAID, " +
                                                      "mo.INVOICE_PAID2, " +
                                                      "xm.XM_OID, " +
                                                      "msn.SERIAL_NUMBER, " +
                                                      "part.PART_NUMBER_LONG, " +
                                                      "xm.REPAIR_SEVERITY REPAIR_SEVERITY_OID, " +
                                                      "sav_repair_severity.VALUE REPAIR_SEVERITY, " +
                                                      "xm.WARRANTY_STATUS WARRANTY_STATUS_OID, " +
                                                      "sav_warranty_status.VALUE WARRANTY_STATUS, " +
                                                      "xm.WARRANTY_VOIDED, " +
                                                      "msn.WARRANTY_VOIDED_DATE, " +
                                                      "fm.MEASURE INVOICE_AMOUNT, " +
                                                      "fm1.MEASURE INVOICE_AMOUNT2, " +
                                                      "xm.CUSTOMER_CONTACT_NEEDED, " +
                                                      "sav_contact_needed.VALUE CONTACT_NEEDED_STATUS, " +
                                                      "mo.VERSION VERSION_MO, " +
                                                      "xm.VERSION VERSION_XM, " +
                                                      "mc.CLIENT_GROUP AS CLIENT_GROUP_ID, " +
                                                      "sav.VALUE AS CLIENT_GROUP, " +
                                                      "mc.CUSTOMER_ORGANIZATION AS CUSTOMER_NAME, " +
                                                      "mc.MC_OID AS MC_OID " +
                                                  "FROM " +
                                                      "MASTER_ORDER mo " +
                                                      "INNER JOIN XREF_MATERIALS xm ON xm.MO_OID = mo.MO_OID " +
                                                      "INNER JOIN MASTER_SERIAL_NUMBER msn ON msn.MSN_OID = xm.MSN_OID " +
                                                      "INNER JOIN MASTER_PART part ON part.PART_OID = msn.PART_OID " +
                                                      "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_repair_severity ON sav_repair_severity.SAV_OID = xm.REPAIR_SEVERITY " +
                                                      "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_warranty_status ON sav_warranty_status.SAV_OID = xm.WARRANTY_STATUS " +
                                                      "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_contact_needed ON sav_contact_needed.SAV_OID = xm.CUSTOMER_CONTACT_NEEDED " +
                                                      "LEFT JOIN (SELECT " + 
                                                                     "PERIOD_CODE, " +
                                                                     "XM_OID, " +
                                                                     "MEASURE_OID, " +
                                                                     "MEASURE " +
                                                                  "FROM ( SELECT " +
                                                                             "PERIOD_CODE, " +
                                                                             "XM_OID, " +
                                                                             "MEASURE_OID, " +
                                                                             "MEASURE, " +
                                                                             "RANK() OVER (PARTITION BY XM_OID, MEASURE_OID ORDER BY PERIOD_CODE DESC) PERIOD_RANK " +
                                                                          "FROM " +
                                                                             "FACT_MEASURE " +
                                                                          "WHERE " +
                                                                             "MEASURE_OID = 6) RANK_A WHERE PERIOD_RANK = 1) fm ON fm.XM_OID = xm.XM_OID " +
                                                                          "LEFT JOIN (SELECT " +
                                                                                         "PERIOD_CODE, " +
                                                                                         "XM_OID, " +
                                                                                         "MEASURE_OID, " +
                                                                                         "MEASURE " +
                                                                                      "FROM (SELECT " +
                                                                                                "PERIOD_CODE, " +
                                                                                                "XM_OID, " +
                                                                                                "MEASURE_OID, " +
                                                                                                "MEASURE, " +
                                                                                                "RANK() OVER (PARTITION BY XM_OID, MEASURE_OID ORDER BY PERIOD_CODE DESC) PERIOD_RANK " +
                                                                                             "FROM " +
                                                                                                "FACT_MEASURE " +
                                                                                             "WHERE " +
                                                                                                "MEASURE_OID = 7) RANK_B " +
                                                                                      "WHERE " +
                                                                                          "PERIOD_RANK = 1) fm1 ON fm1.XM_OID = xm.XM_OID " +
                                                                          "LEFT JOIN MASTER_CUSTOMER mc ON mc.MC_OID = mo.MC_OID " +
                                                                          "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = mc.CLIENT_GROUP " +
                                                                  "WHERE mo.MO_OID = " + QueryUtilsConstant.REPLACE_STRING1;

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
        private static final long serialVersionUID = 1L; 
        {
            put("rmaNumber", new ColumnMapping("mo.MO_OID", true)); 
            put("shippingDate", new ColumnMapping("SHIPPING_DATE", true)); 
            put("invoiceNumber", new ColumnMapping("INVOICE_NUMBER|INVOICE_NUMBER2", true)); 
            put("serialNumber", new ColumnMapping("msn.SERIAL_NUMBER|msn2.SERIAL_NUMBER|msn3.SERIAL_NUMBER|msn4.SERIAL_NUMBER", true)); 
            put("unitNumber", new ColumnMapping("NUMBER_OF_UNITS", true)); 
            put("totalInvoice", new ColumnMapping("TOTAL_INVOICE", true)); 
            put("clientGroup", new ColumnMapping("CLIENT_GROUP_ID", false)); 
            put("customerId", new ColumnMapping("MC_OID", false)); 
            put("paymentStatus", new ColumnMapping("PAYMENT_STATUS", false)); 
        }};

    public final static String ID = "id";
    public final static String MO_OID = "mo_oid";
    public final static String ZOHO_LINK = "zoho_link";
    public final static String INVOICE_LINK = "invoice_link";
    public final static String INVOICE_LINK2 = "invoice_link2";
    public final static String INVOICE_NUMBER = "invoice_number";
    public final static String INVOICE_NUMBER2 = "invoice_number2";
    public final static String INVOICE_PAID = "invoice_paid";
    public final static String INVOICE_PAID2 = "invoice_paid2";
    public final static String XM_OID = "xm_oid";
    public final static String INVOICE_AMOUNT = "invoice_amount";
    public final static String INVOICE_AMOUNT2 = "invoice_amount2";
    public final static String REPAIR_SEVERITY = "repair_severity";
    public final static String REPAIRSEVERITY = "repairSeverity";
    public final static String WARRANTY_STATUS = "warranty_status";
    public final static String WARRANTYSTATUS = "warrantyStatus";
    public final static String WARRANTY_VOIDED = "warranty_voided";
    public final static String CONTACT_NEEDED = "contact_needed";
    public final static String CUSTOMER_CONTACT = "customerContact";
    public final static String CUSTOMER_NAME = "customerName";

    public final static String RMA_NUMBER = "rmaNumber";
    public final static String SHIPPING_DATE = "shippingDate";
    public final static String UNIT_NUMBER = "unitNumber";
    public final static String TOTAL_INVOICE = "totalInvoice";

    public final static String ZOHOLINK= "zohoLink";
    public final static String INVOICELINK = "invoiceLink";
    public final static String INVOICELINK2 = "invoiceLink2";
    public final static String INVOICENUMBER = "invoiceNumber";
    public final static String INVOICENUMBER2 = "invoiceNumber2";
    public final static String INVOICEPAID = "invoicePaid";
    public final static String INVOICEPAID2 = "invoicePaid2";
    public final static String UNITS = "units";

    public final static String PART_NUMBER_LONG = "partNumberLong";
    public final static String SERIAL_NUMBER = "serialNumber";
    public final static String INVOICE_AMT = "invoiceAmt";
    public final static String INVOICE_AMT2 = "invoiceAmt2";
    public final static String WARRANTYVOIDED= "warrantyVoided";
    public final static String WARRANTY_VOIDED_DATE= "warrantyVoidedDate";
    public final static String PAYMENT_STATUS= "paymentStatus";

}
