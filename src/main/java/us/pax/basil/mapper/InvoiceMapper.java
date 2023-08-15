package us.pax.basil.mapper;

import org.apache.ibatis.annotations.Mapper;
import us.pax.basil.entity.invoice.Invoice;

import java.util.List;
@Mapper
public interface InvoiceMapper {
    void insertMasterInvoiceList(List<Invoice> invoiceList);

    void updateMasterInvoice(List<Invoice> invoiceList);

    List<Invoice> getInvoiceByMoOID(Integer moOID);
}
