package us.pax.basil.utils;
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
 * 2021/05/10               rb
 * ============================================================================
 */

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import lombok.extern.log4j.Log4j2;
import us.pax.basil.constant.QueryUtilsConstant;
import us.pax.basil.entity.LabelValuePair;


@Log4j2
public class QueryUtils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(QueryUtilsConstant.DATE_FORMAT);

    @SuppressWarnings("unchecked")
    public static QueryAttributes executeSql(EntityManager entityManager, 
                                             String sql, 
                                             Map<String, ColumnMapping> columnMapping, 
                                             Map<String, String[]> reqParamMap) throws Exception {
        QueryAttributes queryAttributes = setQueryConstraint(sql, columnMapping, reqParamMap);

        queryAttributes.setResult(entityManager.createNativeQuery(queryAttributes.getSql()).getResultList());
        
        setStartEndIndex(queryAttributes, queryAttributes.getResult().size());
        
        return queryAttributes;
    }

    public static void setStartEndIndex(QueryAttributes attributes, int total) {
        int pageInt = Integer.parseInt(attributes.getPage());
        int perPageInt = Integer.parseInt(attributes.getPerPage());

        // Check if page requested is beyond length of data available
        int maxPages = total / perPageInt;

        if ((total % perPageInt)> 0)
                ++maxPages;

        if (pageInt > maxPages)
            pageInt = maxPages;

        // Calculate starting/ending page index to be used to
        // extract data from the customer list
        int startIndex = perPageInt * pageInt - perPageInt;
        attributes.setStartIndex(Math.max(startIndex, 0));

        int endIndex = startIndex + perPageInt;
        attributes.setEndIndex(Math.min(endIndex, total));
    }

    // setQueryConstraint()
    public static QueryAttributes setQueryConstraint(String sql, Map<String, ColumnMapping> columnMapping, Map<String, String[]> reqParamMap) throws Exception {
        //
        // Process per_page, page and sort settings
        //
        String per_page=QueryUtilsConstant.PER_PAGE_DEFAULT;
        String page=QueryUtilsConstant.PAGE_DEFAULT;
        String value="";
        StringBuilder orderString= new StringBuilder();
        StringBuilder whereString= new StringBuilder();
        StringBuilder dateRangeString= new StringBuilder();

        QueryAttributes queryAttributes = new QueryAttributes();

        if (reqParamMap != null) {
            //
            // In order to delete the contents of the parameter map a copy needs to be made.
            // This is done in the event that there are other parameters that need to be
            // processed. We don't want the standard parameters(page, per_page, sort, field_code)
            // to be in the map.
            //
            Map<String, String[]> paramMap = new HashMap<>(reqParamMap);

            List<String> removeKey = new LinkedList<>();
            for (Entry<String, String[]> e: paramMap.entrySet()) {
                String key = e.getKey();
                String[] values = e.getValue();
                if (values != null && values.length >=1) {
                    value = values[0];
                }
    
                switch(key) {
                case QueryUtilsConstant.PAGE_KEY:
                    if (value != null && !value.isEmpty()) {
                        if (StringUtils.isNumeric(value))
                            page = value;
                        removeKey.add(QueryUtilsConstant.PAGE_KEY);
                    }
                    break;
    
                case QueryUtilsConstant.PER_PAGE_KEY:
                    if (value != null && !value.isEmpty()) {
                        if (StringUtils.isNumeric(value))
                            per_page = value;
                        removeKey.add(QueryUtilsConstant.PER_PAGE_KEY);
                    }
                    break;
    
                case QueryUtilsConstant.COMPLETED_DATE_KEY:
                case QueryUtilsConstant.DATE_RECEIVED_KEY:
                case QueryUtilsConstant.SCHEDULED_DATE_KEY:
                case QueryUtilsConstant.SHIPPING_DATE_KEY:
                case QueryUtilsConstant.REGISTER_TIME_KEY:
                case QueryUtilsConstant.LAST_LOGIN_KEY:
                    // Date range format: yyyy/MM/dd ~ yyyy/MM/dd
                    // Example: 2021/05/05 ~ 2021/05/06
                    if (value != null && !value.isEmpty()) {
                        String [] args = value.split(QueryUtilsConstant.DATE_RANGE_DELIMITER);
    
                        if (dateRangeString.length() > 0)
                            dateRangeString.append("AND ");
    
                        ColumnMapping colMap = columnMapping.get(key);
                        dateRangeString.append(colMap.getColumnName())
                                       .append(" BETWEEN ")
                                       .append("'")
                                       .append(args[0])
                                       .append("'")
                                       .append(" AND ")
                                       .append("'")
                                       .append(args[1])
                                       .append("'");
    
                        removeKey.add(key);
                    }
                    break;
    
                case QueryUtilsConstant.SORT_KEY:
                    if (value != null && !value.isEmpty()) {
                        orderString.append(createOrderByString(columnMapping, value));
                        removeKey.add(QueryUtilsConstant.SORT_KEY);
                    }
                    break;
                }
            }

            // Remove keys so they won't be processed again
            for (String s: removeKey) {
                paramMap.remove(s);
            }

            // Rest of the map contents are assumed to be field comparison.
            int entryCount = 0;
            int mapSize = paramMap.size();
    
            for(Entry<String, String[]> e: paramMap.entrySet()) {
                whereString.append(createWhereString(columnMapping, e.getKey(), e.getValue()[0]));

                // Add "AND" if there are other fields following
                if (++entryCount < mapSize)
                    whereString.append(QueryUtilsConstant.WHERE_DELIMETER);
            }

            // Add "AND" if there was a preceding data range specified
            if (dateRangeString.length() != 0) {
                if (whereString.length() == 0)
                    whereString = new StringBuilder(dateRangeString.toString());
                else
                    whereString.append(" AND ").append(dateRangeString);
            }
        }

        queryAttributes.setPage(page);
        queryAttributes.setPerPage(per_page);
        queryAttributes.setWhereString(whereString.toString());
        queryAttributes.setOrderString(orderString.toString());

        if (sql != null && !sql.isEmpty())
            queryAttributes.setSql(updateSql(sql, whereString.toString(), orderString.toString()));

        // 0,0 is the start/end index which is not known at this time
        return queryAttributes;
    }

    private static String updateSql(String sql, String whereString, String orderString) {
        log.info("Update SQL Statement: sql= {}", sql);
        log.info("Update SQL Statement: whereString= {}", whereString);
        log.info("Update SQL Statement: orderString= {}", orderString);
        // Insert WHERE clause in sql
        //   - Add "AND" if WHERE clause already exists
        //   - WHERE needs to be before GROUP BY
        //   - WHERE needs to be after ORDER BY
        if (!whereString.isEmpty()) {
            if (StringUtils.containsIgnoreCase(sql, "where")) {
                sql = StringUtils.replaceIgnoreCase(sql, "WHERE", "WHERE " + whereString + " AND");
            } else {
                if (StringUtils.containsIgnoreCase(sql, "group by")) {
                    sql = StringUtils.replaceIgnoreCase(sql, "group by", "WHERE " + whereString + " GROUP BY");
                } else if (StringUtils.containsIgnoreCase(sql, "order by")) {
                    sql = StringUtils.replaceIgnoreCase(sql, "order by", "WHERE " + whereString + " ORDER BY");
                } else {
                    sql = sql + " WHERE " + whereString;
                }
            }
        }

        // Insert ORDER BY clause in sql
        //   - Add "," if ORDER BY clause already exists
        if (!orderString.isEmpty()) {
            if (StringUtils.containsIgnoreCase(sql, "order by")) {
                sql = StringUtils.replaceIgnoreCase(sql, "order by", "ORDER BY " + orderString + ",");
            } else {
                sql = sql + " ORDER BY " + orderString;
            }
        }
        
        log.info("*** sql:  -{}-", sql);
        return sql;
    }

    private static String createWhereString(Map<String, ColumnMapping> columnMapping, String key, String fieldValue) {
        if (columnMapping == null)
            return "";

        StringBuilder rtnString= new StringBuilder();
        String fieldOperation="";

        // fieldCodes can be the following: <fieldName1>[.operation]=<value>
        String [] args1 = key.split("\\.");
        ColumnMapping columnMap = columnMapping.get(args1[0]);

        if (columnMap == null) {
            log.error("*** {} does not have a mapping in the column map", args1[0]);
            return "";
        }

        String fieldName = columnMap.getColumnName();
        if (fieldName == null || fieldName.equals("")) {
            log.error("*** {} does not have a mapping in the column map", args1[0]);
            return "";
        }
            
        // 
        // <field>=<value>
        //
        if (args1.length == 1) {
            fieldOperation = "eq";
        } else if (args1.length == 2) {
            // 
            // <field>[.operation]=<value>
            //
            fieldOperation = args1[1];
        }
        
        String [] fields = fieldName.split("\\|");
        
        int cnt=0;
        for (String field: fields) {
            //
            // OR string needs to be surrounded by ( and )
            //
            if (cnt == 1)
                rtnString = new StringBuilder("(" + rtnString + " OR ");
            else if (cnt > 1)
                rtnString.append(" OR ");

            switch(fieldOperation.toLowerCase()) {
            case "eq":  // is equal(like)
                //
                // Assignee is a number and LIKE shouldn't be used.
                //
                if (columnMap.isUseLike())
                    rtnString.append(field)
                             .append(QueryUtilsConstant.WHERE_LIKE)
                             .append("'%")
                             .append(fieldValue)
                             .append("%'");
                else
                    rtnString.append(field)
                             .append(QueryUtilsConstant.WHERE_EQUAL)
                             .append("'")
                             .append(fieldValue)
                             .append("'");
                
                break;
            case "ge":  // greater than or equal
                rtnString.append(field).append(" >= ").append(fieldValue);
                break;
            case "gt":  // greater than
                rtnString.append(field).append(" > ").append(fieldValue);
                break;
            case "le":  // less than or equal
                rtnString.append(field).append(" <= ").append(fieldValue);
                break;
            case "lt":  // less than
                rtnString.append(field).append(" < ").append(fieldValue);
                break; 
            default:
                log.error("*** Unrecognized field operator: {}", fieldOperation);
                break;    
            }
            ++cnt;
        }

        //  Add ending ')' to OR string
        if (cnt > 1)
            rtnString.append(")");
        return rtnString.toString();
    }
    
    //
    // createOrderByString()
    //
    private static String createOrderByString(Map<String, ColumnMapping> columnMapping, String value) throws Exception {
        if (columnMapping == null)
            return "";

        StringBuilder rtnString= new StringBuilder();
        String [] argsStrings = value.split(",");
        int argsLength = argsStrings.length;

        for (int i=0; i < argsStrings.length; ++i) {
            String s =  argsStrings[i];
            String [] fieldArgs = s.split("\\.");
            ColumnMapping columnMap = columnMapping.get(fieldArgs[0]);

            if (columnMap == null) {
                log.error("*** {} does not have a mapping in the column map", fieldArgs[0]);
                throw new Exception(fieldArgs[0] + " does not have a column mapping");
            }

            String fieldName = columnMap.getColumnName();
            
            if (fieldName == null || fieldName.equals("")) {
                log.error("*** {} does not have a mapping in the column map", fieldArgs[0]);
                throw new Exception(fieldArgs[0] + " does not have a column mapping");
            }

            if (fieldArgs.length == 2) {
                switch(fieldArgs[1].toLowerCase()) {
                case "desc":
                    rtnString.append(fieldName).append(" desc");
                    break;

                case "asc":
                default:
                    rtnString.append(fieldName).append(" asc");
                    break;
                }
            } else {
                // default to Asc if nothing is specified
                rtnString.append(fieldName).append(" asc");
            }
            
            if (i < argsLength-1)
                rtnString.append(", ");
        }
        return rtnString.toString();
    }
    
    public static String objectToString(Object object) {
        if (object == null)
            return null;
        
        return (String)object;
    }
    
    public static String integerToString(Integer integer) {
        if (integer == null)
            return null;
        
        return String.valueOf(integer);
    }

    public static String bigDecimalToString(BigDecimal bigD) {
        if (bigD == null)
            return null;
        
        return bigD.toString();
    }

    public static String dateToString(Date date) {
        String strDate=null;

        if (date != null) {
            strDate = DATE_FORMAT.format(date);  
        }

        return strDate;
    }
    
    
    public static Integer getLabelValueValue(LabelValuePair lvp) {
        if (lvp == null)
            return null;
        else
            return lvp.getValue();
    }

    public static void setLabelValue(Map<String, Object> parent, Map<String, Object> child, String childKey, Object obj1, Object obj2) {

        String labelValue = QueryUtils.objectToString(obj1);
        String valueValue = QueryUtils.integerToString((Integer)obj2);

        if (labelValue == null && valueValue == null) {
            parent.put(childKey, null);
        } else {
            child.put("label",labelValue);
            child.put("value",valueValue);
            parent.put(childKey, child);
        }
    }
    
    //
    // getCellValue() - Return Excel cell value
    //
    public static String getCellValue(Cell cell) {
        String cellValue=null;
        if (cell != null) {
            DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
             cellValue = formatter.formatCellValue(cell);
        }
        return cellValue;
    }

    // getMsnOid()
    public static String getMsnOid(EntityManager entityManager, String serialNumber) throws Exception { 
        QueryAttributes queryAttributes = QueryUtils.executeSql(entityManager, 
                                                               "SELECT MSN_OID FROM BASIL_ODS_PRD.MASTER_SERIAL_NUMBER WHERE SERIAL_NUMBER = '" + serialNumber +"'",
                                                               null,
                                                               null);

        if (queryAttributes.getResult().size() == 0) { 
            return null; 
        }

        List<Object []> result = queryAttributes.getResult();

        Object [] o = result.toArray();
        
        return QueryUtils.integerToString((Integer)o[0]);
    }
    
    //
    // containsSpecialCharacter()
    //
    public static boolean containsSpecialCharacter(String str) {
        String specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}";
        for (int i=0; i < str.length() ; ++i)
        {
            char ch = str.charAt(i);
            if(specialCharactersString.contains(Character.toString(ch))) {
                return true;
            }    
        }
        return false;
    }
}
