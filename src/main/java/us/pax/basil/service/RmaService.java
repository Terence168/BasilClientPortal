package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;

import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.rma.Quarantine;
import us.pax.basil.entity.rma.Shipped;
import us.pax.basil.entity.rma.StatusExcelExport;

import java.util.ArrayList;

public interface RmaService extends IService<Integer> {
	QueryResultArrayDTO statusQuery(Integer currentPage, 
									Integer sizePerPage, 
									String sortColumns,
									String rmaNumber,
									String serialNumber, 
									String partNumber);

	QueryResultArrayDTO shippedQuery(Integer currentPage,
									 Integer sizePerPage, 
									 String sortColumns,
									 String rmaNumber,
									 String serialNumber, 
									 String partNumber,
									 String shipDate,
									 String customerId);

	ArrayList<Shipped> shippedExcelExportQuery(String sortColumns,
											   String rmaNumber,
											   String serialNumber,
											   String partNumber,
											   String shipDate,
											   String customerId);

	QueryResultArrayDTO quarantineQuery(Integer currentPage,
									    Integer sizePerPage, 
									    String sortColumns,
										String rmaNumber,
									    String serialNumber, 
									    String partNumber,
										String customerId,
										Integer contact);

	ArrayList<Quarantine> quarantineExcelExportQuery(String sortColumns,
													 String rmaNumber,
													 String serialNumber,
													 String model,
													 String customerId,
													 Integer contact);

	QueryResultArrayDTO statusTier1(String partNumber, String rmaNumber, String serialNumber, String customerId);
	QueryResultArrayDTO statusTier2(String partNumber, String rmaNumber, String serialNumber, String customerId);
	QueryResultArrayDTO statusTier3(String rmaNumber, String partNumber, String serialNumber, String customerId);
	QueryResultArrayDTO statusTier4(Integer id);
	ArrayList<StatusExcelExport> statusExcelExportQuery(String partNumber,
														String rmaNumber,
														String serialNumber,
														String customerId);
}
