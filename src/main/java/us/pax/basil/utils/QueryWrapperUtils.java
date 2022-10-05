package us.pax.basil.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j2;

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
 * 2021/05/17               rb
 * ============================================================================
 */

@Log4j2
public class QueryWrapperUtils {
    //
    // setOrderBy() - This sets up the SQL "order by" settings in the wrapper based on 
    //                a comma delimited string. For example:
    //
    //                "field1,field2.desc,field3"
    //
    public static <T> void setOrderBy(QueryWrapper<T> wrapper, String sortString) {
        String [] argsStrings = sortString.split(",");
        
        for (String s: argsStrings) {
            String [] fieldArgs = s.split("\\.");

            if (fieldArgs.length == 2) {
                switch(((String)fieldArgs[1]).toLowerCase()) {
                case "asc":
                    wrapper.orderByAsc(fieldArgs[0]);
                    break;
                case "desc":
                    wrapper.orderByDesc(fieldArgs[0]);
                    break;
                default:
                    wrapper.orderByAsc(fieldArgs[0]);
                    break;
                }
            } else {
                //
                // default to Asc if nothing is specified
                //
                wrapper.orderByAsc(fieldArgs[0]);
            }
        }
    }

    //
    // setDateRange() - Configure wrapper for date range search
    //
    public static <T> void setDateRange(QueryWrapper<T>wrapper, String key, String date1, String date2) {
        wrapper.between(key, date1, date2);
    }

    //
    // setWhere() - Parse fieldCodeString and add the SQL "where" criteria to the wrapper.
    //
    public static <T> void setWhere(QueryWrapper<T> wrapper, String key, String fieldValue) {
        String fieldName="";
        String fieldOperation="";

        //
        // fieldCodes can be the following: <fieldName1>[.operation]=<value>
        //

        String [] args1 = key.split("\\.");
            
        // 
        // <field>=<value>
        //
        if (args1.length == 1) {
            fieldName = args1[0];
            fieldOperation = "eq";
        } else if (args1.length == 2) {
            // 
            // <field>[.operation]=<value>
            //
            fieldName = args1[0];
            fieldOperation = args1[1];
        }
            
        switch(fieldOperation.toLowerCase()) {
        case "eq":  // is equal(like)
            wrapper.like(fieldName, fieldValue);
            break;
        case "ge":  // greater than or equal
            wrapper.ge(fieldName, fieldValue);
            break;
        case "gt":  // greater than
            wrapper.gt(fieldName, fieldValue);
            break;
        case "le":  // less than or equal
            wrapper.le(fieldName, fieldValue);
            break;
        case "lt":  // less than
            wrapper.lt(fieldName, fieldValue);
            break; 
        default:
            log.error("*** Unrecognized field operator: {}", fieldOperation);
            break;    
        }
    }
}
