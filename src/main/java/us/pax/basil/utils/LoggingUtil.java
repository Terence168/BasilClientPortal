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
 * 2022/01/27               rb
 * ============================================================================
 */


import java.util.HashMap;
import us.pax.basil.mapper.LogProgramMapper;
import us.pax.basil.security.CustomUserDetails;

public class LoggingUtil {
    public static final String PASS="PASS";
    public static final String FAIL="FAIL";
    public static final String INFO="INFO";

    private static final String PROGRAM_NAME="programName";
    private static final String USER_ID="userId";
    private static final String PROG_OID="progOid";
    private static final String STATUS="status";
    private static final String INFO_DESCRIPTION="infoDesc";
    
    //
    // addLogProgram()
    //
    public static Integer addLogProgram(LogProgramMapper logProgramMapper, String programName) throws Exception {
    
        CustomUserDetails userDetails = AuthUtil.getUser();
        
        if (userDetails == null) {
            throw new Exception("Unable to get login user name.  Login session possibly expired.  Re-login.");
        }
    
        HashMap<String, Object> hashMap = new HashMap<>();
        
        hashMap.put(PROGRAM_NAME, programName);
        hashMap.put(USER_ID, userDetails.getUsername());

        logProgramMapper.addLogProgram(hashMap);
        
        return (Integer)hashMap.get(PROG_OID);
    }

    //
    // updateLogProgram()
    //
    public static void updateLogProgram(LogProgramMapper logProgramMapper, Integer progOid, String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        
        hashMap.put(PROG_OID, progOid);
        hashMap.put(STATUS, status);

        logProgramMapper.updateLogProgram(hashMap);
    }

    //
    // addLogProgramInfo()
    //
    public static void addLogProgramInfo(LogProgramMapper logProgramMapper, Integer progOid, String info) {
        HashMap<String, Object> hashMap = new HashMap<>();
        
        hashMap.put(PROG_OID, progOid);
        hashMap.put(INFO_DESCRIPTION, info);

        logProgramMapper.addLogProgramInfo(hashMap);
    }
}
