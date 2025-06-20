package us.pax.basil.constant;

/**
 * Warranty-related constants
 */
public class WarrantyConstant {
    
    // Warranty status values
    public static final String WARRANTY_STATUS_IN_WARRANTY = "In Warranty";
    public static final String WARRANTY_STATUS_OUT_OF_WARRANTY = "Out of Warranty";
    public static final String WARRANTY_STATUS_VOIDED = "Voided";
    public static final String WARRANTY_STATUS_NA = "N/A";
    
    // Table aliases
    public static final String MSN_TABLE = "BASIL_ODS_PRD.MASTER_SERIAL_NUMBER";
    public static final String MP_TABLE = "BASIL_ODS_PRD.MASTER_PART";
    public static final String XM_TABLE = "BASIL_ODS_PRD.XREF_MATERIALS";
    public static final String PXM_TABLE = "BASIL_SEC_PRD.PREP_XREF_MATERIALS";
    public static final String MO_TABLE = "BASIL_ODS_PRD.MASTER_ORDER";
    public static final String PMO_TABLE = "BASIL_SEC_PRD.PREP_MASTER_ORDER";
    public static final String MC_TABLE = "BASIL_ODS_PRD.MASTER_CUSTOMER";
    
    // Column names
    public static final String SERIAL_NUMBER = "SERIAL_NUMBER";
    public static final String WARRANTY_START_DATE = "WARRANTY_START_DATE";
    public static final String WARRANTY_END_DATE = "WARRANTY_END_DATE";
    public static final String WARRANTY_VOIDED_DATE = "WARRANTY_VOIDED_DATE";
    public static final String SALES_ORDER = "SALES_ORDER";
    public static final String CUSTOMER_PO_NUMBER = "CUSTOMER_PO_NUMBER";
    
    // Default values
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_BATCH_SIZE = 1000;
} 