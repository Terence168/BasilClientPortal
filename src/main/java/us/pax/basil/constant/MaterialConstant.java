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

public class MaterialConstant {
    public final static String SQL_BASE_QUERY = "SELECT DISTINCT " +
                                                    "xm.XM_OID AS id, " +
                                                    "mm.MATERIAL_NUMBER, " +
                                                    "mm.MATERIAL_DESCRIPTION, " +
                                                    "FLOOR(fm.MEASURE) AS QOH, " +
                                                    "sav_unit.VALUE AS UNIT, " +
                                                    "sav_location.VALUE AS LOCATION, " +
                                                    "xm.MAX, " +
                                                    "xm.MIN, " +
                                                    "xm.THRESHOLD, " +
                                                    "IF(FLOOR(fm.MEASURE) <= xm.THRESHOLD OR xm.THRESHOLD IS NULL, 0, 1) AS MATERIAL_SORT " +
                                                "FROM " +
                                                    "MASTER_MATERIAL mm " +
                                                    "INNER JOIN XREF_MATERIALS xm ON xm.MM_OID = mm.MM_OID " +
                                                    "LEFT JOIN FACT_MEASURE fm ON fm.XM_OID = xm.XM_OID " +
                                                    "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_location ON sav_location.SAV_OID = xm.LOCATION " +
                                                    "LEFT JOIN SUPPORT_ATTRIBUTE_VALUES sav_unit ON sav_unit.SAV_OID = mm.UNIT " +
                                                "WHERE " +
                                                    "fm.MEASURE_OID = 1 ";
                                                
                                                    
    public final static String SQL_MATERIAL_SORT = "ORDER BY MATERIAL_SORT";

    public final static String SQL_PART_NUMBER_SEARCH = " AND mm.MM_OID IN (SELECT DISTINCT " +
                                                               "mm.MM_OID " +
                                                            "FROM " +
                                                               "MASTER_MATERIAL mm " +
                                                               "INNER JOIN XREF_MATERIAL_PART xmp USING (MM_OID) " +
                                                               "INNER JOIN MASTER_PART mp USING (PART_OID) " +
                                                            "WHERE mp.PART_NUMBER_LONG LIKE '%" + QueryUtilsConstant.REPLACE_STRING1 + "%')";
    

    public final static String SQL_VIEW_QUERY = "SELECT " +
                                                    "xm.XM_OID AS id, " +
                                                    "mm.MM_OID AS mm_id, " +
                                                    "mm.MATERIAL_NUMBER, " +
                                                    "mm.MATERIAL_DESCRIPTION MATERIAL_NAME, " +
                                                    "mm.MATERIAL_DESC2, " +
                                                    "FLOOR(MEASURE) AS QOH, " +
                                                    "xm.MAX MAX_COUNT, " +
                                                    "xm.MIN MIN_COUNT, " +
                                                    "xm.THRESHOLD, " +
                                                    "xm.STATUS, " +
                                                    "xm.LOCATION, " +
                                                    "mm.DISCONTINUED, " +
                                                    "mm.UNIT, " +
                                                    "mm.REVISION, " +
                                                    "mm.VERSION " +
                                                "FROM " +
                                                    "MASTER_MATERIAL mm " +
                                                    "LEFT JOIN XREF_MATERIALS xm ON xm.MM_OID = mm.MM_OID " +
                                                    "LEFT JOIN FACT_MEASURE fm ON fm.XM_OID = xm.XM_OID " +
                                                "WHERE " +
                                                    "fm.MEASURE_OID = 1 AND xm.XM_OID = " + QueryUtilsConstant.REPLACE_STRING1; 
            
    
    public final static String SQL_FAULT_QUERY = "SELECT FAULT FROM BASIL_ODS_PRD.XREF_MATERIAL_FAULT WHERE mm_oid=" + QueryUtilsConstant.REPLACE_STRING1;

    public final static String SQL_PART_QUERY = "SELECT DISTINCT " +
                                                        "CAST(PART_OID as CHAR), " +
                                                        "CONCAT(PART_NUMBER_LONG, \" / V\",VERSION_NUMBER) PART_NUMBER, " +
                                                        "REFERENCE_POSITION " +
                                                    "FROM " +
                                                        "XREF_MATERIAL_PART " +
                                                        "INNER JOIN MASTER_PART using(PART_OID) " +
                                                    "WHERE " +
                                                        "mm_oid=" + QueryUtilsConstant.REPLACE_STRING1;
   
    public final static String SQL_FAULT_CODE_QUERY = "SELECT SAV_OID as VALUE, VALUE as LABEL FROM SUPPORT_ATTRIBUTE_VALUES WHERE SA_OID=48";

    public final static String SQL_ADD_QUERY = "SELECT " +
                                                       "mm.MM_OID AS mm_id, " +
                                                       "mm.MATERIAL_DESCRIPTION, " +
                                                       "mm.MATERIAL_NUMBER, " +
                                                       "mm.DISCONTINUED, " +
                                                       "mm.UNIT, " +
                                                       "mm.REVISION, " +
                                                       "mm.MATERIAL_DESC2, " +
                                                       "mm.VERSION " +
                                                   "FROM " +
                                                       "MASTER_MATERIAL mm " +
                                                       "LEFT JOIN XREF_MATERIALS xm on xm.MM_OID = mm.MM_OID " +
                                                   "WHERE " +
                                                       "mm.MATERIAL_NUMBER = " + QueryUtilsConstant.REPLACE_STRING1 +
                                                       " AND xm.XM_OID IS NULL";

    public final static Map<String, ColumnMapping> queryColumnMapping = new HashMap<String, ColumnMapping>() {
                                                            private static final long serialVersionUID = 1L; 
                                                            {
                                                                put("location", new ColumnMapping("sav_location.VALUE", true)); 
                                                                put("materialName", new ColumnMapping("MATERIAL_DESCRIPTION", true)); 
                                                                put("materialNumber", new ColumnMapping("mm.MATERIAL_NUMBER", true)); 
                                                                put("min_count", new ColumnMapping("MIN_COUNT", true)); 
                                                                put("max_count", new ColumnMapping("MAX_COUNT", true)); 
                                                                put("qoh", new ColumnMapping("QOH", true)); 
                                                                put("faultCode", new ColumnMapping("??", true)); 
                                                                put("partNumber", new ColumnMapping("PART_NUMBER_LONG", true)); 
                                                            }};
                                                            
    public final static String DISCONTINUED = "discontinued";
    public final static String FAULT = "fault";
    public final static String ID = "id";
    public final static String LOCATION = "location";
    public final static String MATERIAL_NUMBER = "materialNumber";
    public final static String MATERIAL_NAME = "materialName";
    public final static String MATERIAL_DESC2 = "materialDesc2";
    public final static String MATERIAL_DESCRIPTION = "description";
    public final static String MAX = "max";
    public final static String MAX_COUNT = "max_count";
    public final static String MIN = "min";
    public final static String MIN_COUNT = "min_count";
    public final static String MM_OID = "mm_oid";
    public final static String XM_OID = "xm_oid";
    public final static String PART_OID = "part_oid";
    public final static String PART_NUMBER = "partNumber";
    public final static String QUANTITY_ON_HAND = "qoh";
    public final static String REVISION = "revision";
    public final static String REFERENCE_POSITION = "referencePosition";
    public final static String STATUS = "status";
    public final static String THRESHOLD = "th";
    public final static String THRESHOLD_NAME = "threshold";
    public final static String MATERIAL_SORT = "materialSort";
    public final static String UNIT = "unit";
    public final static String UNITS = "units";
    public final static String PART = "part";
}
