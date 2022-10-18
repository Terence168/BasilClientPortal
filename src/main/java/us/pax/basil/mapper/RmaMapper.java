package us.pax.basil.mapper;



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

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import us.pax.basil.entity.rma.PartNumberTier1;
import us.pax.basil.entity.rma.Quarantine;
import us.pax.basil.entity.rma.RmaNumberTier2;
import us.pax.basil.entity.rma.Shipped;

public interface RmaMapper extends BaseMapper<Integer> {
	List<Shipped> getStatus(Integer offset,
                         	Integer count,
                         	String sortColumns,
                         	Long rmaNumber,
                         	String serialNumber,
                         	String partNumber);

	Integer getQuarantineTotal(Integer id, Long rmaNumber, String serialNumber, String partNumber);
	Integer getShippingTotal(Integer id, Long rmaNumber, String serialNumber, String partNumber);

	List<Shipped> getShipping(Integer offset,
                         	 Integer count,
                         	 String sortColumns,
                         	 Integer id,
                         	 Long rmaNumber,
                         	 String serialNumber,
                         	 String partNumber);

	List<Quarantine> getQuarantine(Integer offset,
                         	    Integer count,
                         	    String sortColumns,
                         	    Integer id,
                         	    Long rmaNumber,
                         	    String serialNumber,
                         	    String partNumber);
	
	List<PartNumberTier1> getPartNumberTier1(Integer id);
	List<RmaNumberTier2> getRmaNumberTier2(Integer id, String partNumber);
}
