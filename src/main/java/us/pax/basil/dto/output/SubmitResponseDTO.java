package us.pax.basil.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitResponseDTO extends SqlResultDTO {
    private Integer rsp_OID;

    public SubmitResponseDTO(Integer rsp_OID, int resultCode, String errorMessage) {
        super(resultCode, errorMessage);
        this.rsp_OID = rsp_OID;
    }

}