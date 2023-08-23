package us.pax.basil.service;

import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.invoice.Invoice;

import java.util.List;

public interface InvoiceService {
    QueryResultDTO insertInvoiceList(List<Integer> pxmOidList, Integer companyId);
    QueryResultDTO updateInvoiceList(List<Integer> pxmOidList, Integer companyId);
    QueryResultArrayDTO getInvoiceList(Integer moOID);
    QueryResultDTO deleteInvoiceList(List<Integer> pxmOidList);
}
