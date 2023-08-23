package us.pax.basil.mapper;

import org.apache.ibatis.annotations.Mapper;
import us.pax.basil.entity.invoice.Invoice;

import java.util.List;
@Mapper
public interface InvoiceMapper {
    void insertSecInvoiceList(List<Integer> pxmOidList, Integer companyId);
    void reinsertSecInvoiceList(List<Integer> pxmOidList, Integer companyId);
    void deleteInvoiceList(List<Integer> pxmOidList);
    List<Invoice> getInvoiceByMoOID(Integer moOID);
    Double getTotalInvoice(Integer moOID);
}
