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
	QueryResultArrayDTO statusTier2(String partNumber);
	QueryResultArrayDTO statusTier3(Long rmaNumber, String partNumber);
}
