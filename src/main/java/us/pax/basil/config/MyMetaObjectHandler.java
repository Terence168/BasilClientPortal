package us.pax.basil.config;
/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016-2020 PAX Technology, Inc. All rights reserved.
 * Description:
 *
 * Revision History:
 * Date	                 Author	                Action
 * 2020-01-07 10:11	     yyyty
 * ============================================================================
 */


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.utils.AuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author: yinyy
 * @Date: 2020-01-07 10:13
 * @MethodName: mybatis-plus Auto Fill Strategy
 * @Description:
 * @Params:
 * @Return:
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreate", LocalDateTime.now(), metaObject);

        CustomUserDetails user = AuthUtil.getUser();
        if ((null != user) && (StringUtils.isNotBlank(user.getUsername()))) {
            this.setFieldValByName("creator", user.getUsername(), metaObject);
        } else {
            this.setFieldValByName("creator", "Unknown", metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtModified", LocalDateTime.now(), metaObject);

        if (null != AuthUtil.getUser() && StringUtils.isNotBlank(AuthUtil.getUser().getUsername())) {
            this.setFieldValByName("modifier", AuthUtil.getUser().getUsername(), metaObject);
        }
    }

}

 