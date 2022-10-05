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
 * 2020-01-06 11:07	     yyyty
 * ============================================================================
 */

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@MapperScan("us.pax.basil.mapper")
@Configuration
public class MyBatisPlusConfig {
    /**
     * Paging Plugin
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        // Set the action if the requested page number is larger than the maximum page number:
        //         True  - Return to the first page
        //         False - Continue request  (Default)
        paginationInterceptor.setOverflow(true);

        // Set the maximum number of items per page. (Default value is 500. Set to -1 for no limitation)
        paginationInterceptor.setLimit(1000);
        // Enable optimization for JOIN of count - Only for parts of LEFT JOIN
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));

        return paginationInterceptor;
    }

    /**
     * SQL Performance Analyze Plugin. Output the SQL statements and the elapsed time
     * It should not be used in the production environment to avoid performance issue
     * @return The SqlExplainInterceptor
     */
    @Bean
    @Profile({"dev", "test", "uat"})
    public SqlExplainInterceptor sqlExplainInterceptor() {
        return new SqlExplainInterceptor();
    }

    /**
     * Optimistic Lock Plugin
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }
}

 