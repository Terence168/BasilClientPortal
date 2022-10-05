package us.pax.basil.utils;
/***
 * ============================================================================
 * = COPYRIGHT file
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                     Author                    Action
 * 2020/10/20              ly                    
 * ============================================================================
 */

import com.paxcq.cloud.common.dto.Result;
import com.paxcq.cloud.common.util.JsonUtils;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ResponseUtil {

    public static void out(HttpServletResponse response, Result<?> result) {
        ServletOutputStream out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            out = response.getOutputStream();
            out.write(JsonUtils.object2String(result).getBytes());
        } catch (IOException ex) {
            log.error("Http Servlet Response IO error", ex);
        } finally{
            if(out!=null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    log.error("Close Servlet Output Stream failed!");
                    e.printStackTrace();
                }
            }
        }
    }
}
