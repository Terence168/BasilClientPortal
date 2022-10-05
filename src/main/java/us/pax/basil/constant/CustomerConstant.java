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
public class CustomerConstant {
    public final static String SQL_QUERY = "SELECT " +
                                               "MC_OID, " +
                                               "CUSTOMER_ORGANIZATION, " +
                                               "CUST_ADDRESS, " +
                                               "CUST_ADDRESS2, " +
                                               "CUST_CONTACT_NAME, " +
                                               "CITY, " +
                                               "STATE, " +
                                               "ZIP_CODE, " +
                                               "COUNTRY, " +
                                               "STORE_NUMBER, " +
                                               "CLIENT_GROUP, " +
                                               "PAY_AFTER, " +
                                               "sav_client_group.value CLIENT_GROUP_VALUE, " +
                                               "sav_pay_after.value PAY_AFTER_VALUE, " +
                                               "mc.VERSION " +
                                           "FROM " +
                                               "MASTER_CUSTOMER mc " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_client_group ON sav_client_group.SAV_OID = mc.CLIENT_GROUP " +
                                               "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_pay_after ON sav_pay_after.SAV_OID = mc.PAY_AFTER";
    
    
    public final static String SQL_DROP_DOWN_QUERY = "SELECT " +
                                                         "MC_OID, " +
                                                         "CUSTOMER_ORGANIZATION, " +
                                                         "CLIENT_GROUP AS CLIENT_GROUP_ID, " +
                                                         "sav.VALUE AS CLIENT_GROUP " +
                                                     "FROM " +
                                                         "MASTER_CUSTOMER co " +
                                                         "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav ON sav.SAV_OID = co.CLIENT_GROUP " +
                                                     "WHERE " +
                                                         "CUSTOMER_ORGANIZATION LIKE '%" + QueryUtilsConstant.REPLACE_STRING1 + "%'";
    
    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("customerId", new ColumnMapping("MC_OID", false)); 
                                                                put("customerName", new ColumnMapping("CUSTOMER_ORGANIZATION", true)); 
                                                                put("contactName", new ColumnMapping("CUST_CONTACT_NAME", true)); 
                                                                put("address1", new ColumnMapping("CUST_ADDRESS", true)); 
                                                                put("clientGroupValue", new ColumnMapping("CLIENT_GROUP_VALUE", true)); 
                                                                put("payAfterValue", new ColumnMapping("PAY_AFTER_VALUE", true)); 
                                                            }};


   //
   // JSON Key values use for return values
   //
   public final static String ID_KEY = "id";
   public final static String CUSTOMER_NAME_KEY = "customerName";
   public final static String ADDRESS1_KEY = "address1";
   public final static String ADDRESS2_KEY = "address2";
   public final static String CONTACT_NAME_KEY = "contactName";
   public final static String CITY_KEY = "city";
   public final static String STATE_KEY = "state";
   public final static String ZIP_KEY = "zip";
   public final static String SORT_KEY = "sort";
   public final static String COUNTRY = "country";
   public final static String STORE_NUMBER = "storeNumber";
   public final static String CLIENT_GROUP = "clientGroup";
   public final static String CLIENT_GROUP_VALUE = "clientGroupValue";
   public final static String PAY_AFTER = "payAfter";
   public final static String PAY_AFTER_VALUE = "payAfterValue";

    //
    // Stored procedure arguments
    //
    public final static String PROCEDURE_ID = "id";
    public final static String PROCEDURE_ORGANIZATION = "organization";
    public final static String PROCEDURE_CONTACT_NAME = "contact_name";
    public final static String PROCEDURE_ADDRESS1 = "address1";
    public final static String PROCEDURE_ADDRESS2 = "address2";
    public final static String PROCEDURE_CITY = "city";
    public final static String PROCEDURE_STATE = "state";
    public final static String PROCEDURE_ZIP_CODE = "zip_code";
    public final static String PROCEDURE_STORE_NUMBER = "store_number";
}
