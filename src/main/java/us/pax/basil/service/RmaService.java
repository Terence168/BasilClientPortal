package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;

import us.pax.basil.dto.output.QueryResultArrayDTO;

public interface RmaService extends IService<Integer> {
	QueryResultArrayDTO statusQuery(Integer currentPage, 
									Integer sizePerPage, 
									String sortColumns, 
									Long rmaNumber, 
									String serialNumber, 
									String partNumber);

	QueryResultArrayDTO shippedQuery(Integer currentPage,
									 Integer sizePerPage, 
									 String sortColumns, 
									 Long rmaNumber, 
									 String serialNumber, 
									 String partNumber);

	QueryResultArrayDTO quarantineQuery(Integer currentPage,
									    Integer sizePerPage, 
									    String sortColumns, 
									    Long rmaNumber, 
									    String serialNumber, 
									    String partNumber);

	QueryResultArrayDTO statusTier1();
}
