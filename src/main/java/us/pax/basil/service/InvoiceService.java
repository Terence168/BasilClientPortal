package us.pax.basil.service;

import us.pax.basil.entity.invoice.Invoice;

import java.util.List;

public interface InvoiceService {
    void insertInvoiceList(List<Invoice> invoiceList);
    void updateInvoiceList(List<Invoice> invoiceList);
}
