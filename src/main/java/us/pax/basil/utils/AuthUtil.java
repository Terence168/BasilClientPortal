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

import us.pax.basil.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static CustomUserDetails getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return (CustomUserDetails) auth.getPrincipal();
        }
    }
    public static int setUserCompanyId(Integer companyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return -1;
        } else {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            userDetails.setCompanyId(companyId);
            return 0;
        }

    }
}
