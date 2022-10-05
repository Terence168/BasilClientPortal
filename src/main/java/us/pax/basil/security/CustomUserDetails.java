package us.pax.basil.security;

import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

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

@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends User {
	private static final long serialVersionUID = 1L;

    private String  emailAddress;
    private Integer empOid;
    private List<String> roles;

	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUserDetails setEmailAddress(String emailAddress) {
    	this.emailAddress = emailAddress;
    	return this;
    }
    
    public String getEmailAddress() {
    	return emailAddress;
    }

    public CustomUserDetails setEmpOid(Integer empOid) {
        this.empOid = empOid;
        return this;
    }
    public Integer getEmpOid() {
        return empOid;
    }

    public List<String> getRoles() {
        return roles;
    }

    public CustomUserDetails setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }
}
