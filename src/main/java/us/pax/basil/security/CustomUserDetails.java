package us.pax.basil.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/***
 * ============================================================================
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * @Date                 @AUTHOR            	@ACTION
 * 2019-06-09 13:14        Yinyy
 * ============================================================================
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends User {
	private static final long serialVersionUID = 1L;

    private Integer userId;
    private String emailAddress;
    private Integer companyId;

	private Integer standardUser;

	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

	public CustomUserDetails setUserId(Integer userId) {
		this.userId = userId;
		return this;
    }
	public CustomUserDetails setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
    }
	public CustomUserDetails setCompanyId(Integer id) {
		this.companyId = id;
		return this;
    }
	public CustomUserDetails setStandardUser(Integer standardUser) {
		this.standardUser = standardUser;
		return this;
	}
}
