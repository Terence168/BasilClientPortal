package us.pax.basil.entity.customer;

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
 * 2021/07/06               rb
 * ============================================================================
 */

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(value="Customer Object")
public class Customer{

    private Long id;
    private String customerName;
    private String contactName;

    private String address1;
    private String address2;
    private String city;

    private String state;
    private String zip;
    private String country;

    private String contactPhone;
    private String storeNumber;
    private Integer clientGroup;
    private Integer payAfter;
    private Integer version;

    public boolean isInvalidCustomer() {
        return StringUtils.isAnyEmpty(
                this.getCustomerName(),
                this.getAddress1(),
                this.getCity(),
                this.getState(),
                this.getZip(),
                this.getContactName()
        );
    }
}

