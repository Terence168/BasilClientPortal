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
									 String partNumber,
									 String shipDate,
									 Integer customerId);

	QueryResultArrayDTO quarantineQuery(Integer currentPage,
									    Integer sizePerPage, 
									    String sortColumns, 
									    Long rmaNumber, 
									    String serialNumber, 
									    String partNumber,
										Integer customerId);

	QueryResultArrayDTO statusTier1(String partNumber, Long rmaNumber, String serialNumber, Integer customerId);
	QueryResultArrayDTO statusTier2(String partNumber, Long rmaNumber, String serialNumber, Integer customerId);
	QueryResultArrayDTO statusTier3(Long rmaNumber, String partNumber, String serialNumber, Integer customerId);
	QueryResultArrayDTO statusTier4(Integer id);
}
