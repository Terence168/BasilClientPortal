package us.pax.basil.dto.output;

import lombok.*;


@Getter
@Setter
public class SubmitTicketDTO extends SqlResultDTO {
    private Integer mo_OID;

    public SubmitTicketDTO(Integer mo_OID, int resultCode, String errorMessage) {
        super(resultCode, errorMessage);
        this.mo_OID = mo_OID;
    }

}
