package us.pax.basil.dto.input;

import lombok.Data;

@Data
public class PasswordRecoveryCompleteDTO {
    private String encryptedUserId;
    private String encryptedToken;
    private String password;
}
