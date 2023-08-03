package us.pax.basil.entity.ticket;

import java.util.List;

public class TicketEditObject {
    private Integer mo_oid;
    private List<Integer> deleteSerials;//mc_oid
    private List<SNInfo> updateSerials;
    private List<SNInfo> addSerials;
}
