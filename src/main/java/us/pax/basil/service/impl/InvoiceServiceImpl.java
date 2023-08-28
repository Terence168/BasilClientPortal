package us.pax.basil.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.invoice.Invoice;
import us.pax.basil.mapper.InvoiceMapper;
import us.pax.basil.service.InvoiceService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceMapper invoiceMapper;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public QueryResultDTO insertInvoiceList(List<Integer> pxmOidList, Integer companyId) {
        try {
            invoiceMapper.insertSecInvoiceList(pxmOidList, companyId);
            return new QueryResultDTO(null, 0, "");
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultDTO updateInvoiceList(List<Integer> pxmOidList, Integer companyId) {
        try {
            if(!pxmOidList.isEmpty()) {
                invoiceMapper.deleteInvoiceList(pxmOidList);
                invoiceMapper.reinsertSecInvoiceList(pxmOidList, companyId);
            }
            return new QueryResultDTO(null, 0, "");
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultArrayDTO getInvoiceList(Integer moOID) {
        try {
            List<Invoice> invoices = invoiceMapper.getInvoiceByMoOID(moOID);
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            for(Invoice invoice:invoices){
                Map<String, Object> objectMap = objectMapper.convertValue(invoice, Map.class);
                result.add(objectMap);
            }
            return new QueryResultArrayDTO(result, result.size(),0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0,-1, e.getMessage());
        }
    }

    @Override
    public Double getTotalInvoice(Integer moOID) {
        try {
            return invoiceMapper.getTotalInvoice(moOID);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public QueryResultDTO deleteInvoiceList(List<Integer> pxmOidList) {
        try {
            if(!pxmOidList.isEmpty()) {
                invoiceMapper.deleteInvoiceList(pxmOidList);
            }
            return new QueryResultDTO(null, 0, "");
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }


}
